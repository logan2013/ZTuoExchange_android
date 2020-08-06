package top.biduo.exchange.ui.main.trade;

import top.biduo.exchange.base.Contract;
import top.biduo.exchange.entity.EntrustHistory;
import top.biduo.exchange.entity.Exchange;
import top.biduo.exchange.entity.Money;

import java.util.List;


public interface Coin2CoinContract {
    interface View extends Contract.BaseView<Presenter> {

        void errorMes(int e, String meg);

        /**
         * 盘口信息
         *
         * @param ask 买
         * @param bid 卖
         */
        void getSuccess(List<Exchange> ask, List<Exchange> bid);

        /**
         * 当前委托
         */
        void getDataLoaded(List<EntrustHistory> entrusts);

        /**
         * 历史委托
         */
        void getHistorySuccess(List<EntrustHistory> success);

        /**
         * 提交委托成功(买入或者卖出成功)
         */
        void getDataLoad(int code, String message);

        /**
         * 取消委托
         */
        void getCancelSuccess(String success);

        // 钱包
        void getWalletSuccess(Object coin, int type);

        void getAccuracy(int one, int two);

        void showDialog();

        void hideDialog();

        void setDefaultSymbol(String symbol);
    }

    interface Presenter {
        /**
         * 获取盘口的信息
         */
        void getExchange(String symbol);

        /**
         * 获取当前的委托
         */
        void getCurrentOrder(String token, int pageNo, int pageSize, String symbol, String type, String direction, String startTime, String endTime);

        /**
         * 获取历史委托
         */
        void getOrderHistory(String token, int pageNo, int pageSize, String symbol, String type, String direction, String startTime, String endTime);

        /**
         * 提交委托
         */
        void getAddOrder(String token, String symbol, String price, String amount, String direction, String type, String useDiscount,String touchPrice);

        /**
         * 取消委托
         */
        void getCancelEntrust(String token, String orderId);

        /**
         * 获取钱包
         */
        void getWallet(int type, String token, String coinName);

        /**
         * 获取精确度
         */
        void getSymbolInfo(String symbol);

        /**
         * 获取首次进入的默认交易对-目前仅处理币币交易
         */
        void getDefaultSymbol();


    }
}
