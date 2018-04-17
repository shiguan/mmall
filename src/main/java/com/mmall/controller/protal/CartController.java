package com.mmall.controller.protal;

import com.mmall.common.Constants;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.ICartService;
import com.mmall.vo.CartVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * Created by 15M-4528S on 2018/3/18.
 */
@Controller
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private ICartService iCartService;

    @RequestMapping("/list.do")
    @ResponseBody
    public ServerResponse<CartVo> list(HttpSession session){
        User user = (User) session.getAttribute(Constants.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCode(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.list(user.getId());
    }

    @RequestMapping("/add.do")
    @ResponseBody
    public ServerResponse<CartVo> add(HttpSession session, Integer productId, Integer count){
        User user = (User) session.getAttribute(Constants.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCode(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.add(productId,count,user.getId());
    }

    @RequestMapping("/update.do")
    @ResponseBody
    public ServerResponse<CartVo> update(HttpSession session, Integer productId, Integer count){
        User user = (User) session.getAttribute(Constants.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCode(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.add(productId,count,user.getId());
    }

    @RequestMapping("/delete_product.do")
    @ResponseBody
    public ServerResponse<CartVo> deleteProduct(HttpSession session, String productId){
        User user = (User) session.getAttribute(Constants.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCode(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.deleteProduct(productId,user.getId());
    }

    @RequestMapping("/select_all.do")
    @ResponseBody
    public ServerResponse<CartVo> selectAll(HttpSession session){
        User user = (User) session.getAttribute(Constants.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCode(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.selectAll(user.getId(),Constants.Cart.CHECK_IN);
    }

    @RequestMapping("/un_select_all.do")
    @ResponseBody
    public ServerResponse<CartVo> unSelectAll(HttpSession session){
        User user = (User) session.getAttribute(Constants.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCode(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.selectAll(user.getId(),Constants.Cart.CHECK_OUT);
    }

    @RequestMapping("/select.do")
    @ResponseBody
    public ServerResponse<CartVo> select(HttpSession session,Integer productId){
        User user = (User) session.getAttribute(Constants.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCode(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.select(user.getId(),productId,Constants.Cart.CHECK_IN);
    }

    @RequestMapping("/un_select.do")
    @ResponseBody
    public ServerResponse<CartVo> unSelect(HttpSession session,Integer productId){
        User user = (User) session.getAttribute(Constants.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCode(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.select(user.getId(),productId,Constants.Cart.CHECK_OUT);
    }

    @RequestMapping("/get_cart_product_count.do")
    @ResponseBody
    public ServerResponse<Integer> getCartProductCount(HttpSession session){
        User user = (User) session.getAttribute(Constants.CURRENT_USER);
        if(user == null){
            return ServerResponse.createBySuccessData(0);
        }
        return iCartService.getCartProductCount(user.getId());
    }
}
