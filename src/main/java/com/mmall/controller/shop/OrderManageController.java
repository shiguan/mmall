package com.mmall.controller.shop;

import com.github.pagehelper.PageInfo;
import com.mmall.common.Constants;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.IOrderService;
import com.mmall.service.IUserService;
import com.mmall.vo.OrderVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * Created by 15M-4528S on 2018/4/21.
 */
@Controller
@RequestMapping("/shop/order")
public class OrderManageController {

    @Autowired
    private IOrderService iOrderService;
    @Autowired
    private IUserService iUserService;

    @RequestMapping("/list.do")
    @ResponseBody
    public ServerResponse<PageInfo> getList(HttpSession session, @RequestParam(value="pageNum",defaultValue = "1") int pageNum,
                                            @RequestParam(value="pageSize",defaultValue = "10")int pageSize) {
        User user = (User) session.getAttribute(Constants.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCode(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        ServerResponse<String> response = iUserService.checkAdminRole(user);
        if(!response.isSuccess()){
            return ServerResponse.createByErrorMsg("用户无权限");
        }
        return iOrderService.manageList(pageNum,pageSize);
    }

    @RequestMapping("/detail.do")
    @ResponseBody
    public ServerResponse<OrderVo> detail(HttpSession session, Long orderNo) {
        User user = (User) session.getAttribute(Constants.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCode(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        ServerResponse<String> response = iUserService.checkAdminRole(user);
        if(!response.isSuccess()){
            return ServerResponse.createByErrorMsg("用户无权限");
        }
        return iOrderService.manageDetail(orderNo);
    }


    @RequestMapping("/search.do")
    @ResponseBody
    public ServerResponse<PageInfo> search(HttpSession session, Long orderNo,@RequestParam(value="pageNum",defaultValue = "1") int pageNum,
                                          @RequestParam(value="pageSize",defaultValue = "10")int pageSize) {
        User user = (User) session.getAttribute(Constants.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCode(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        ServerResponse<String> response = iUserService.checkAdminRole(user);
        if(!response.isSuccess()){
            return ServerResponse.createByErrorMsg("用户无权限");
        }
        return iOrderService.manageSearchByOrderNo(orderNo,pageNum,pageSize);
    }

    @RequestMapping("/send_goods.do")
    @ResponseBody
    public ServerResponse<String> sendGoods(HttpSession session, Long orderNo) {
        User user = (User) session.getAttribute(Constants.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCode(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        ServerResponse<String> response = iUserService.checkAdminRole(user);
        if(!response.isSuccess()){
            return ServerResponse.createByErrorMsg("用户无权限");
        }
        return iOrderService.sendGoods(orderNo);
    }
}
