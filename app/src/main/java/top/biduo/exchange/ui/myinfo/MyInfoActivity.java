package top.biduo.exchange.ui.myinfo;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.gyf.barlibrary.ImmersionBar;
import com.kyleduo.switchbutton.SwitchButton;

import top.biduo.exchange.R;
import top.biduo.exchange.ui.account_pwd.AccountPwdActivity;
import top.biduo.exchange.ui.account_pwd.EditAccountPwdActivity;
import top.biduo.exchange.ui.bind_account.BindAccountActivity;
import top.biduo.exchange.ui.bind_email.BindEmailActivity;
import top.biduo.exchange.ui.bind_email.EmailActivity;
import top.biduo.exchange.ui.bind_phone.BindPhoneActivity;
import top.biduo.exchange.ui.bind_phone.PhoneActivity;
import top.biduo.exchange.ui.credit.CreditInfoActivity;
import top.biduo.exchange.ui.credit.VideoActivity;
import top.biduo.exchange.ui.credit.VideoCreditActivity;
import top.biduo.exchange.ui.edit_login_pwd.EditLoginPwdActivity;
import top.biduo.exchange.ui.set_lock.SetLockActivity;
import top.biduo.exchange.app.GlobalConstant;
import top.biduo.exchange.app.MyApplication;
import top.biduo.exchange.base.BaseActivity;
import top.biduo.exchange.ui.dialog.HeaderSelectDialogFragment;
import top.biduo.exchange.entity.SafeSetting;
import top.biduo.exchange.utils.SharedPreferenceInstance;
import top.biduo.exchange.customview.CircleImageView;
import top.biduo.exchange.utils.WonderfulBitmapUtils;
import top.biduo.exchange.utils.WonderfulCodeUtils;
import top.biduo.exchange.utils.WonderfulFileUtils;
import top.biduo.exchange.utils.WonderfulPermissionUtils;
import top.biduo.exchange.utils.WonderfulStringUtils;
import top.biduo.exchange.utils.WonderfulToastUtils;
import top.biduo.exchange.utils.WonderfulUriUtils;

import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;
import com.yanzhenjie.permission.PermissionListener;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import top.biduo.exchange.app.Injection;
import top.biduo.exchange.utils.okhttp.WonderfulOkhttpUtils;

public class MyInfoActivity extends BaseActivity implements MyInfoContract.View, HeaderSelectDialogFragment.OperateCallback {
    @BindView(R.id.ibBack)
    ImageButton ibBack;
    @BindView(R.id.ibRegist)
    TextView ibRegist;
    @BindView(R.id.llTitle)
    LinearLayout llTitle;
    @BindView(R.id.llAccountPwd)
    LinearLayout llAccountPwd;
    @BindView(R.id.ivHeader)
    CircleImageView ivHeader;
    @BindView(R.id.tvPhone)
    TextView tvPhone;
    @BindView(R.id.tvEmail)
    TextView tvEmail;
    @BindView(R.id.tvLoginPwd)
    TextView tvLoginPwd;
    @BindView(R.id.tvAcountPwd)
    TextView tvAcountPwd;
    @BindView(R.id.tvIdCard)
    TextView tvIdCard;
    @BindView(R.id.llPhone)
    LinearLayout llPhone;
    @BindView(R.id.llEmail)
    LinearLayout llEmail;
    @BindView(R.id.llLoginPwd)
    LinearLayout llLoginPwd;
    @BindView(R.id.llIdCard)
    LinearLayout llIdCard;
    @BindView(R.id.tvAccount)
    TextView tvAccount;
    @BindView(R.id.llAccount)
    LinearLayout llAccount;
    @BindView(R.id.llLockSet)
    LinearLayout llLockSet;
    @BindView(R.id.switchButton)
    SwitchButton switchButton;
    @BindView(R.id.view_back)
    View view_back;
    private CompoundButton.OnCheckedChangeListener listener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            SetLockActivity.actionStart(MyInfoActivity.this, isChecked ? 0 : 1);
        }
    };
    private File imageFile;
    private String filename = "header.jpg";
    private Uri imageUri;
    private String url;
    private MyInfoContract.Presenter presenter;
    private SafeSetting safeSetting;
    private HeaderSelectDialogFragment headerSelectDialogFragment;

    private PermissionListener permissionListener = new PermissionListener() {
        @Override
        public void onSucceed(int requestCode, @NonNull List<String> grantPermissions) {
            switch (requestCode) {
                case GlobalConstant.PERMISSION_CAMERA:
                    startCamera();
                    break;
                case GlobalConstant.PERMISSION_STORAGE:
                    chooseFromAlbum();
                    break;
                default:
            }
        }

        @Override
        public void onFailed(int requestCode, @NonNull List<String> deniedPermissions) {
            switch (requestCode) {
                case GlobalConstant.PERMISSION_CAMERA:
                    WonderfulToastUtils.showToast(getResources().getString(R.string.camera_permission));
                    break;
                case GlobalConstant.PERMISSION_STORAGE:
                    WonderfulToastUtils.showToast(getResources().getString(R.string.storage_permission));
                    break;
                default:
            }
        }
    };

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, MyInfoActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        loadData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        switchButton.setOnCheckedChangeListener(null);
        String password = SharedPreferenceInstance.getInstance().getLockPwd();
        switchButton.setChecked(!WonderfulStringUtils.isEmpty(password));
        switchButton.setOnCheckedChangeListener(listener);
    }


    @Override
    protected int getActivityLayoutId() {
        return R.layout.activity_my_info;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        new MyInfoPresenter(Injection.provideTasksRepository(getApplicationContext()), this);
        imageFile = WonderfulFileUtils.getCacheSaveFile(this, filename);
        ibBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayLoadingPopup();
                finish();
            }
        });
        view_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayLoadingPopup();
                finish();
            }
        });
        ivHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showHeaderSelectDialog();
            }
        });
        llPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phoneClick();
            }
        });
        llEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emailClick();
            }
        });
        llAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                accountClick();
            }
        });
        llLoginPwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginPwdClick();
            }
        });
        llAccountPwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                accountPwdClick();
            }
        });
        llIdCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkVerifiStatu();
            }
        });
        switchButton.setChecked(!WonderfulStringUtils.isEmpty(SharedPreferenceInstance.getInstance().getLockPwd()));
        switchButton.setOnCheckedChangeListener(listener);
    }

    private void accountClick() {
        if (safeSetting == null) {
            return;
        }
        if (safeSetting.getRealVerified() == 1 && safeSetting.getFundsVerified() == 1) {
            BindAccountActivity.actionStart(this);
        } else {
            WonderfulToastUtils.showToast(getResources().getString(R.string.password_realname));
        }
    }

    // 身份认证状态判断
    private void checkVerifiStatu() {
        if (safeSetting == null) {
            return;
        }
        if (safeSetting.getRealVerified() == 0) {
            if (safeSetting.getRealAuditing() == 1) {//审核中
                WonderfulToastUtils.showToast(getResources().getString(R.string.creditting));
            } else {
                if (safeSetting.getRealNameRejectReason() != null) {//失败
                    CreditInfoActivity.actionStart(this, CreditInfoActivity.AUDITING_FILED, safeSetting.getRealNameRejectReason());
                } else {//未认证
                    CreditInfoActivity.actionStart(this, CreditInfoActivity.UNAUDITING, safeSetting.getRealNameRejectReason());
                }
            }
        } else {
            //身份证已通过
            int kycStatu = safeSetting.getKycStatus();
            switch (kycStatu) {
                //0-未实名,5-待实名审核，2-实名审核失败、1-视频审核,6-待视频审核 ，3-视频审核失败,4-实名成功
                case 1:
                    //实名通过，进行视频认证
                    VideoCreditActivity.actionStart(this, "");
                    break;
                case 3:
                    VideoCreditActivity.actionStart(this, safeSetting.getRealNameRejectReason());
                    break;
                case 4:
                    WonderfulToastUtils.showToast(getString(R.string.verification));
                    break;
                case 6:
                    WonderfulToastUtils.showToast(getString(R.string.video_credit_auditing));
                    break;
                default:
            }
        }
    }

    private void accountPwdClick() {
        if (safeSetting == null) {
            return;
        }
        if (safeSetting.getFundsVerified() == 0) {
            AccountPwdActivity.actionStart(this);
        } else if (safeSetting.getFundsVerified() == 1) {
            EditAccountPwdActivity.actionStart(this);
        }
    }

    private void loginPwdClick() {
        if (safeSetting == null) {
            return;
        }
        if (safeSetting.getPhoneVerified() == 0) {
            WonderfulToastUtils.showToast(getResources().getString(R.string.binding_phone_first));
            return;
        }
        EditLoginPwdActivity.actionStart(this, safeSetting.getMobilePhone());
    }

    private void emailClick() {
        if (safeSetting == null) {
            return;
        }
        if (safeSetting.getEmailVerified() == 0) {
            BindEmailActivity.actionStart(this);
        } else {
            EmailActivity.actionStart(this, safeSetting.getEmail());
        }
    }

    private void phoneClick() {
        if (safeSetting == null) {
            return;
        }
        if (safeSetting.getPhoneVerified() == 0) {
            BindPhoneActivity.actionStart(this);
        } else {
            PhoneActivity.actionStart(this, safeSetting.getMobilePhone());
        }
    }

    private void showHeaderSelectDialog() {
        if (headerSelectDialogFragment == null) {
            headerSelectDialogFragment = HeaderSelectDialogFragment.getInstance(MyInfoActivity.this);
        }
        headerSelectDialogFragment.show(getSupportFragmentManager(), "header_select");
    }

    @Override
    protected void obtainData() {

    }

    @Override
    protected void fillWidget() {

    }

    @Override
    protected void loadData() {
        presenter.safeSetting(getToken());
    }

    @Override
    protected void initImmersionBar() {
        super.initImmersionBar();
        if (!isSetTitle) {
            ImmersionBar.setTitleBar(this, llTitle);
            isSetTitle = true;
        }
    }

    @Override
    public void setPresenter(MyInfoContract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void safeSettingSuccess(SafeSetting obj) {
        this.safeSetting = obj;
        fillViews();
    }

    private void fillViews() {
        if (ivHeader == null) {
            return;
        }
        Glide.with(getApplicationContext()).load(WonderfulStringUtils.isEmpty(safeSetting.getAvatar()) ? R.mipmap.icon_default_header : safeSetting.getAvatar()).into(ivHeader);
        tvPhone.setText(safeSetting.getPhoneVerified() == 0 ? R.string.unbound : R.string.bound);
        tvPhone.setEnabled(safeSetting.getPhoneVerified() == 0);
        tvEmail.setText(safeSetting.getEmailVerified() == 0 ? R.string.unbound : R.string.bound2);
        tvEmail.setEnabled(safeSetting.getEmailVerified() == 0);
        tvAcountPwd.setText(safeSetting.getFundsVerified() == 0 ? R.string.not_set : R.string.had_set);
        tvAcountPwd.setEnabled(safeSetting.getFundsVerified() == 0);
        tvAccount.setText(safeSetting.getAccountVerified() == 0 ? R.string.not_set : R.string.had_set);
        tvAccount.setEnabled(safeSetting.getAccountVerified() == 0);
        tvLoginPwd.setText(safeSetting.getLoginVerified() == 0 ? R.string.not_set : R.string.had_set);
        String verifiText = "";
        if (safeSetting.getRealVerified() == 0) {
            if (safeSetting.getRealAuditing() == 1) {
                verifiText = getString(R.string.id_card_creditting);
            } else {
                if (safeSetting.getRealNameRejectReason() != null) {
                    verifiText = getString(R.string.creditfail);
                } else {
                    verifiText = getString(R.string.unverified);
                }
            }
        } else {
            //身份证已通过
            int kycStatu = safeSetting.getKycStatus();
            switch (kycStatu) {
                //0-未实名,5-待实名审核，2-实名审核失败、1-视频审核,6-待视频审核 ，3-视频审核失败,4-实名成功
                case 0:
                    verifiText = getString(R.string.unverified);
                    break;
                case 1:
                    verifiText = getString(R.string.to_video_credit);
                    break;
                case 2:
                    verifiText = getString(R.string.creditfail);
                    break;
                case 3:
                    verifiText = getString(R.string.video_credit_fail);
                    break;
                case 4:
                    verifiText = getString(R.string.verification);
                    break;
                case 5:
                    verifiText = getString(R.string.id_card_creditting);
                    break;
                case 6:
                    verifiText = getString(R.string.video_creditting);
                    break;
                default:
            }
        }
        tvIdCard.setText(verifiText);
        tvIdCard.setEnabled(!verifiText.equals(getString(R.string.verification)));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case GlobalConstant.TAKE_PHOTO:
                takePhotoReturn(resultCode, data);
                break;
            case GlobalConstant.CHOOSE_ALBUM:
                choseAlbumReturn(resultCode, data);
                break;
            case SetLockActivity.RETURN_SET_LOCK:
                switchButton.setOnCheckedChangeListener(null);
                String password = SharedPreferenceInstance.getInstance().getLockPwd();
                switchButton.setChecked(!WonderfulStringUtils.isEmpty(password));
                switchButton.setOnCheckedChangeListener(listener);
                break;
            default:
        }
    }

    private void choseAlbumReturn(int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }
        imageUri = data.getData();
        if (Build.VERSION.SDK_INT >= 19) {
            imageFile = WonderfulUriUtils.getUriFromKitKat(this, imageUri);
        } else {
            imageFile = WonderfulUriUtils.getUriBeforeKitKat(this, imageUri);
        }
        if (imageFile == null) {
            WonderfulToastUtils.showToast(getResources().getString(R.string.library_file_exception));
            return;
        }
        Bitmap bm = WonderfulBitmapUtils.zoomBitmap(BitmapFactory.decodeFile(imageFile.getAbsolutePath()), ivHeader.getWidth(), ivHeader.getHeight());
        presenter.uploadBase64Pic(MyApplication.getApp().getCurrentUser().getToken(), "data:image/jpeg;base64," + WonderfulBitmapUtils.imgToBase64(bm));
        //ivHeader.setImageBitmap(bm);

    }

    private void takePhotoReturn(int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }
        //Glide.with(this).load(imageFile).override(ivHeader.getWidth(), ivHeader.getHeight()).skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE).into(ivHeader);
        Bitmap bitmap = WonderfulBitmapUtils.zoomBitmap(BitmapFactory.decodeFile(imageFile.getAbsolutePath()), ivHeader.getWidth(), ivHeader.getHeight());
        presenter.uploadBase64Pic(getToken(), "data:image/jpeg;base64," + WonderfulBitmapUtils.imgToBase64(bitmap));
    }

    private void startCamera() {
        if (imageFile == null) {
            WonderfulToastUtils.showToast(getResources().getString(R.string.unknown_error));
            return;
        }
        imageUri = WonderfulFileUtils.getUriForFile(this, imageFile);
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, GlobalConstant.TAKE_PHOTO);
    }

    private void chooseFromAlbum() {
        Intent intent = new Intent(Intent.ACTION_PICK);
//        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent, GlobalConstant.CHOOSE_ALBUM);
    }

    private void checkPermission(int requestCode, String[] permissions) {
        AndPermission.with(this).requestCode(requestCode).permission(permissions).callback(permissionListener).start();
    }

    @Override
    public void safeSettingFail(Integer code, String toastMessage) {
        WonderfulCodeUtils.checkedErrorCode(this, code, toastMessage);
    }

    @Override
    public void uploadBase64PicSuccess(String obj) {
        url = obj;

        presenter.avatar(getToken(), obj);
    }

    @Override
    public void uploadBase64PicFail(Integer code, String toastMessage) {
        WonderfulCodeUtils.checkedErrorCode(this, code, toastMessage);
    }

    @Override
    public void avatarSuccess(String obj) {
        MyApplication.getApp().getCurrentUser().setAvatar(url);
        MyApplication.getApp().saveCurrentUser();
        Glide.with(this).load(url).into(ivHeader);
    }

    @Override
    public void avatarFail(Integer code, String toastMessage) {
        WonderfulCodeUtils.checkedErrorCode(this, code, toastMessage);
    }

    @Override
    public void toTakePhoto() {
        if (!WonderfulPermissionUtils.isCanUseCamera(this)) {
            checkPermission(GlobalConstant.PERMISSION_CAMERA, Permission.CAMERA);
        } else {
            startCamera();
        }
    }

    @Override
    public void toChooseFromAlbum() {
        if (!WonderfulPermissionUtils.isCanUseStorage(this)) {
            checkPermission(GlobalConstant.PERMISSION_STORAGE, Permission.STORAGE);
        } else {
            chooseFromAlbum();
        }
    }


}
