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
}
