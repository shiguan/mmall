package com.mmall.common;

/**
 * Created by 15M-4528S on 2018/3/11.
 */
public class Constants {

    public static final String CURRENT_USER = "current_user";

    public static final String USERNAME = "username";
    public static final String EMAIL = "email";

    public static final String SALT="asdfghjkl";

    //进行分组
    public interface Role{
        int ROLE_CUSTOMER = 0;
        int ROLE_ADMIN = 1;
    }
}
