package com.mmall.common;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by 15M-4528S on 2018/3/11.
 */
public class Constants {

    public static final String CURRENT_USER = "current_user";

    public static final String USERNAME = "username";
    public static final String EMAIL = "email";

    public static final String SALT="asdfghjkl";

    public interface Cart{
        int CHECK_IN = 1;  //购物车选中的状态
        int CHECK_OUT = 0;

        String LIMIT_NUM_FAIL = "LIMIT_NUM_FAIL";
        String LIMIT_NUM_SUCCESS = "LIMIT_NUM_SUCCESS";
    }

    public interface OrderBy{
//        Set<String> set = new HashSet<String>();
        String PRICE_DESC="price_desc";
        String PRICE_ASC="price_asc";
    }
    //进行分组
    public interface Role{
        int ROLE_CUSTOMER = 0;
        int ROLE_ADMIN = 1;
        int ROLE_SHOP = 2;
    }

    public enum ProductStatus{
        SALE(1,"在线");

        int code;
        String value;

        ProductStatus(int code, String value) {
            this.code = code;
            this.value = value;
        }

        public int getCode() {
            return code;
        }

        public String getValue() {
            return value;
        }
    }

    public enum TradeStatusEnum{
        CANCELED(0,"已取消"),
        NO_PAY(10,"未支付"),
        PAID(20,"已付款"),
        SHIPPED(30,"已发货"),
        ORDER_SUCCESS(40,"交易成功"),
        ORDER_CLOSE(50,"交易关闭");
        String value;
        int code;

        TradeStatusEnum(int code,String value) {
            this.value = value;
            this.code = code;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public static TradeStatusEnum codeOf(int code){
            for(TradeStatusEnum tradeStatusEnum:values()){
                if(tradeStatusEnum.getCode() == code){
                    return tradeStatusEnum;
                }
            }
            throw new RuntimeException("没有找到对应的枚举");
        }
    }

    public interface alipay{
        String trade_Status_WAIT_BUYER_PAY = "WAIT_BUYER_PAY";
        String trade_Status_TRADE_CLOSED = "TRADE_CLOSED";

        String response_TRADE_SUCCESS = "TRADE_SUCCESS";
        String respongse_TRADE_FINISHED = "TRADE_FINISHED";
    }

    public enum PayPlatformEnum{
        ALIPAY(1,"支付宝");

        String value;
        int code;

        PayPlatformEnum(int code,String value) {
            this.value = value;
            this.code = code;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }
    }

    public enum PaymentTypeEnum{
        ONLINE_PAY(1,"在线支付");

        String value;
        int code;

        PaymentTypeEnum(int code,String value) {
            this.value = value;
            this.code = code;
        }

        public String getValue() {
            return value;
        }

        public int getCode() {
            return code;
        }

        public static PaymentTypeEnum codeOf(int code){
            for(PaymentTypeEnum paymentTypeEnum:values()){
                if(paymentTypeEnum.getCode() == code){
                    return paymentTypeEnum;
                }
            }
            throw new RuntimeException("没有找到对应的枚举");
        }
    }
}
