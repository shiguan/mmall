package com.mmall.controller.manager;

import com.mmall.common.Constants;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;


/**
 * Created by 15M-4528S on 2018/3/11.
 */
@Controller
@RequestMapping("/manager/user")
public class UserController {

    @Autowired
    private IUserService iUserService;


    @RequestMapping(value="/login.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> adminLogin(HttpSession session,String username,String password){
        ServerResponse<User> response = iUserService.login(username,password);
        if(response.isSuccess()){
            User user = response.getData();
            if(user.getRole() == Constants.Role.ROLE_ADMIN){
                session.setAttribute(Constants.CURRENT_USER,user);
                return ServerResponse.createBySuccessData(user);
            }else{
                return ServerResponse.createByErrorMsg("用户没有管理员权限");
            }
        }
        return response;
    }


    @RequestMapping(value="/logout.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> logout(HttpSession session){
        session.removeAttribute(Constants.CURRENT_USER);
        return ServerResponse.createBySuccess();
    }


    @RequestMapping(value="/get_user_info.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> getUserInfo(HttpSession session){
        User user = (User) session.getAttribute(Constants.CURRENT_USER);
        if(user == null){
            return ServerResponse.createBySuccessData(user);
        }
        return ServerResponse.createByErrorMsg("用户未登陆，无法获取信息");
    }


    @RequestMapping(value="/reset_password.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> resetPassword(HttpSession session,String oldPassword,String newPassword){
        User user = (User) session.getAttribute(Constants.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorMsg("用户未登陆");
        }
        return iUserService.resetPassword(user,oldPassword,newPassword);
    }

    @RequestMapping(value="/update_information.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> updateUserInfo(HttpSession session,User user){
        User currentUser = (User) session.getAttribute(Constants.CURRENT_USER);
        if(currentUser == null){
            return ServerResponse.createByErrorMsg("用户未登陆");
        }
        user.setId(currentUser.getId());
        user.setUsername(currentUser.getUsername());
        ServerResponse<User> response = iUserService.updateUserInfo(user);
        if(response.isSuccess()){
            session.setAttribute(Constants.CURRENT_USER,response.getData());
        }
        return response;
    }

    @RequestMapping(value="/get_infomation.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> getInfomation(HttpSession session){
        User user = (User) session.getAttribute(Constants.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorMsg("用户未登陆");
        }
        return iUserService.getInfomation(user);
    }
}
