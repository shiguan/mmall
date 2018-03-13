package com.mmall.service;

import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;

/**
 * Created by 15M-4528S on 2018/3/11.
 */
public interface IUserService {

    ServerResponse<User> login(String username, String password);

    public ServerResponse<String> register(User user);

    ServerResponse<String> cheakValid(String str,String type);

    ServerResponse<String> forgetGetQuestion(String username);

    ServerResponse<String> CheckAnswer(String username,String question,String answer);

    ServerResponse<String> forgetResetPassword(String username,String newPassword,String forgetToken);

    ServerResponse<String> resetPassword(User user,String oldPassword,String newPassword);

    ServerResponse<User> updateUserInfo(User user);

    ServerResponse<User> getInfomation(User user);
}
