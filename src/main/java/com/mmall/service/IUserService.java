package com.mmall.service;

import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;

/**
 * Created by 15M-4528S on 2018/3/11.
 */
public interface IUserService {

    ServerResponse<User> login(String username, String password);
}
