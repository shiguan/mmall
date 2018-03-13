package com.mmall.controller.protal;

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
@RequestMapping("/user")
public class UserController {

    @Autowired
    private IUserService iUserService;

    /**
     * 用户登陆
     * @param username
     * @param password
     * @param session
     * @return
     */
    @RequestMapping(value="/login.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> login(String username, String password, HttpSession session){
        ServerResponse<User> response = iUserService.login(username,password);
        if(response.isSuccess()){
           session.setAttribute(Constants.CURRENT_USER,response.getData());
        }
        return response;
    }

    @RequestMapping(value="/logout.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> logout(HttpSession session){
        session.removeAttribute(Constants.CURRENT_USER);
        return ServerResponse.createBySuccess();
    }

    @RequestMapping(value="/register.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> register(User user){
        return iUserService.register(user);
    }

    @RequestMapping(value="/check_vaild.do",method = RequestMethod.POST)
    @ResponseBody
    public  ServerResponse<String> checkValid(String str,String type){
        return iUserService.cheakValid(str,type);
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

    @RequestMapping(value="/forget_get_question.do",method = RequestMethod.POST)
    @ResponseBody
    public  ServerResponse<String> forgetGetQuestion(String username){
        return iUserService.forgetGetQuestion(username);
    }

    @RequestMapping(value="/forget_check_answer.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> forgetCheckAnswer(String username,String question,String answer){
        return iUserService.CheckAnswer(username,question,answer);
    }

    @RequestMapping(value="/forget_reset_password.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> forgetResetPassword(String username,String newPassword,String forgetToken){
        return iUserService.forgetResetPassword(username,newPassword,forgetToken);
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

    @RequestMapping(value="/update_infomation.do",method = RequestMethod.POST)
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
