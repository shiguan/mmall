package com.mmall.service.impl;

import com.mmall.common.ServerResponse;
import com.mmall.dao.UserMapper;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * Created by 15M-4528S on 2018/3/11.
 */
@Service("iUserService")
public class UserServiceImpl implements IUserService {

    @Autowired
    private UserMapper userMapper;

    public ServerResponse<User> login(String username, String password) {
        int resultCount = userMapper.checkUsername(username);
        if(resultCount == 0)
            return ServerResponse.createByErrorMsg("用户不存在");

        User user = userMapper.selectByLogin(username,password);
        if(user == null)
            return ServerResponse.createByErrorMsg("密码错误");

        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.createBySuccessAll("登陆成功",user);
    }
}
