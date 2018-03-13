package com.mmall.service.impl;

import com.mmall.common.Constants;
import com.mmall.common.ServerResponse;
import com.mmall.common.TokenCache;
import com.mmall.dao.UserMapper;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;

import com.mmall.utils.MD5Util;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;


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

        String Md5Password = MD5Util.MD5EncodeUtf8(password);
        User user = userMapper.selectByLogin(username,Md5Password);
        if(user == null)
            return ServerResponse.createByErrorMsg("密码错误");

        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.createBySuccessAll("登陆成功",user);
    }

    public ServerResponse<String> register(User user){
        ServerResponse response = cheakValid(user.getUsername(),Constants.USERNAME);
        if(!response.isSuccess()){
           return response;
        }

        response = cheakValid(user.getEmail(),Constants.EMAIL);
        if(!response.isSuccess()){
            return response;
        }

        user.setRole(Constants.Role.ROLE_CUSTOMER);
        user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));
        int resultCount = userMapper.insert(user);
        if(resultCount == 0){
            return ServerResponse.createByErrorMsg("注册失败");
        }
        return ServerResponse.createBySuccessMsg("注册成功");
    }

    public ServerResponse<String> cheakValid(String str,String type){
        if(StringUtils.isNotBlank(type)){
            if(Constants.USERNAME.equals(type)){
                int resultCount = userMapper.checkUsername(str);
                if(resultCount != 0) {
                    return ServerResponse.createByErrorMsg("用户已经存在");
                }
            }
            if(Constants.EMAIL.equals(type)){
                int resultCount = userMapper.checkEmail(str);
                if(resultCount > 0) {
                    return ServerResponse.createByErrorMsg("Email已经存在");
                }
            }
        }else{
            return ServerResponse.createByErrorMsg("参数错误");
        }
        return ServerResponse.createBySuccessMsg("校验成功");
    }

    public ServerResponse<String> forgetGetQuestion(String username){
        ServerResponse response = cheakValid(username,Constants.USERNAME);
        if(!response.isSuccess()){
            return ServerResponse.createByErrorMsg("用户不存在");
        }

        String question = userMapper.selectQuestionByUsername(username);
        if(StringUtils.isNotBlank(question)){
            return ServerResponse.createBySuccessData(question);
        }
        return ServerResponse.createByErrorMsg("找回密码问题为空");
    }

     public ServerResponse<String> CheckAnswer(String username,String question,String answer){
        int resultCount = userMapper.checkAnswer(username,question,answer);
        if(resultCount > 0){
            String forget_token = UUID.randomUUID().toString();
            TokenCache.setKey(TokenCache.TOKEN_PERFIX+username,forget_token);
            return ServerResponse.createBySuccessData(forget_token);
        }
        return ServerResponse.createByErrorMsg("问题答案错误");
     }

     public ServerResponse<String> forgetResetPassword(String username,String newPassword,String forgetToken){
         if(!StringUtils.isNotBlank(forgetToken)){
             return ServerResponse.createByErrorMsg("参数错误：token不能为空");
         }
         ServerResponse response = cheakValid(username,Constants.USERNAME);
         if(!response.isSuccess()){
             return ServerResponse.createByErrorMsg("用户不存在");
         }
         //todo 密码校验
         String token = TokenCache.getKey(TokenCache.TOKEN_PERFIX+username);
         if(StringUtils.isBlank(token)){
             return ServerResponse.createByErrorMsg("token无效或者过期");
         }
         if(StringUtils.equals(token,forgetToken)){
              String Md5Password = MD5Util.MD5EncodeUtf8(newPassword);
              int resultCount = userMapper.updatePasswordByUsername(username,Md5Password);
              if(resultCount > 0){
                  return ServerResponse.createBySuccessMsg("修改密码成功");
              }
         }else{
             return ServerResponse.createByErrorMsg("token错误，请重新获取");
         }
         return ServerResponse.createByErrorMsg("修改密码失败");
     }

     public ServerResponse<String> resetPassword(User user,String oldPassword,String newPassword){
         String Md5OldPassword = MD5Util.MD5EncodeUtf8(oldPassword);
         int resultCount = userMapper.checkPassword(user.getId(),Md5OldPassword);
         if(resultCount == 0){
             return ServerResponse.createByErrorMsg("用户旧密码错误");
         }
         //todo user.ppassword 校验
         String Md5NewPassword = MD5Util.MD5EncodeUtf8(newPassword);
         user.setPassword(Md5NewPassword);
         resultCount = userMapper.updateByPrimaryKeySelective(user);
         if(resultCount > 0){
             return ServerResponse.createBySuccessMsg("密码更新成功");
         }
         return ServerResponse.createByErrorMsg("密码更新失败");
     }

     public ServerResponse<User> updateUserInfo(User user){
         int resultCount = userMapper.selectEmailByUserId(user.getEmail(),user.getId());
         if(resultCount > 0){
             return ServerResponse.createByErrorMsg("更新Email失败");
         }
         User updateUser = new User();
         updateUser.setId(user.getId());
         updateUser.setAnswer(user.getAnswer());
         updateUser.setEmail(user.getEmail());
         updateUser.setQuestion(user.getQuestion());
         updateUser.setPhone(user.getPhone());
         resultCount = userMapper.updateByPrimaryKeySelective(updateUser);
         if(resultCount == 0){
             return ServerResponse.createByErrorMsg("更新用户信息失败");
         }
         return ServerResponse.createBySuccessData(user);
     }

     public ServerResponse<User> getInfomation(User user){
         user = userMapper.selectByPrimaryKey(user.getId());
         if(user == null){
             return ServerResponse.createByErrorMsg("未找到相关用户信息");
         }
         //user.setPassword(null);
         user.setPassword(StringUtils.EMPTY);
         return ServerResponse.createBySuccessData(user);
     }

}
