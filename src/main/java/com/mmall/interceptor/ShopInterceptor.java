package com.mmall.interceptor;

import com.mmall.common.Constants;
import com.mmall.pojo.User;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by 15M-4528S on 2018/5/2.
 */
public class ShopInterceptor extends HandlerInterceptorAdapter {

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        User user = (User) request.getSession().getAttribute(Constants.CURRENT_USER);
        if(user == null)
            return false;
        if(user.getRole() != Constants.Role.ROLE_SHOP)
            return false;
        return true;
    }

}
