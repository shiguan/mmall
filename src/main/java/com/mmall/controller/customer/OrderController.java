package com.mmall.controller.customer;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.demo.trade.config.Configs;
import com.github.pagehelper.PageInfo;
import com.mmall.common.Constants;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.IOrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by 15M-4528S on 2018/4/18.
 */
@Controller
@RequestMapping("/customer/order")
public class OrderController {

    @Autowired
    private IOrderService iOrderService;
    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    @RequestMapping("/list.do")
    @ResponseBody
    public ServerResponse<PageInfo> list(HttpSession session, @RequestParam(value="pageNum",defaultValue = "1") int pageNum,
                                         @RequestParam(value="pageSize",defaultValue = "10")int pageSize){
        User user = (User) session.getAttribute(Constants.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCode(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iOrderService.list(user.getId(),pageNum,pageSize);
    }

    @RequestMapping("/detail.do")
    @ResponseBody
    public ServerResponse detail(HttpSession session,Long orderNo){
        User user = (User) session.getAttribute(Constants.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCode(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iOrderService.detail(user.getId(),orderNo);
    }

    //获取购物车中已经选中的商品详情
    @RequestMapping("/get_order_cart_product.do")
    @ResponseBody
    public ServerResponse getOrderCartProduct(HttpSession session){
        User user = (User) session.getAttribute(Constants.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCode(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iOrderService.getOrderCartProduct(user.getId());
    }

    @RequestMapping("/cancel.do")
    @ResponseBody
    public ServerResponse cancel(HttpSession session, Long orderNo){
        User user = (User) session.getAttribute(Constants.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCode(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iOrderService.cancle(user.getId(),orderNo);
    }

    @RequestMapping("/create.do")
    @ResponseBody
    public ServerResponse create(HttpSession session, Integer shippingId){
        User user = (User) session.getAttribute(Constants.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCode(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iOrderService.create(shippingId,user.getId());
    }

    @RequestMapping("/pay.do")
    @ResponseBody
    public ServerResponse pay(HttpSession session, long orderNum, HttpServletRequest httpServletRequest){
        User user = (User) session.getAttribute(Constants.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCode(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        String path = httpServletRequest.getSession().getServletContext().getRealPath("upload");
        return iOrderService.pay(path,orderNum,user.getId());
    }

    @RequestMapping("/alipay_callback.do")
    @ResponseBody
    public Object callback(HttpServletRequest request){
        Map<String,String> myParams = new HashMap<String,String>();
        Map params = request.getParameterMap();
        for(Iterator iter = params.keySet().iterator();iter.hasNext();){
            String key = (String) iter.next();
            String[] values = (String[])params.get(key);
            String str = "";
            for(int i = 0; i < values.length; i++){
               str = (i == values.length - 1) ? (str + values[i]):(str + ",");
            }
            myParams.put(key,str);
        }
        logger.info("支付宝回调，sign{},trade_status:{},参数:{}",params.get("sign"),params.get("trade_status"),myParams.toString());
        //验证回调的正确性

        myParams.remove("sign_type");
        try {
            boolean alipayRSACheckV2 = AlipaySignature.rsaCheckV2(myParams, Configs.getAlipayPublicKey(),"utf-8",Configs.getSignType());
            if(!alipayRSACheckV2){
                return ServerResponse.createByErrorMsg("支付宝验证回调不通过");
            }
        } catch (AlipayApiException e) {
            logger.error("支付宝验证回调不通过",e);
        }

        //验证数据正确性
        ServerResponse response = iOrderService.callback(myParams);
        if(response.isSuccess()){
            return Constants.alipay.response_TRADE_SUCCESS;
        }
        return Constants.alipay.respongse_TRADE_FINISHED;
    }

    @RequestMapping("/query_order_pay_status.do")
    @ResponseBody
    public ServerResponse<Boolean> queryOrderPayStatus(HttpSession session, Long orderNo){
        User user = (User) session.getAttribute("user");
        if(user == null){
            return ServerResponse.createByErrorCode(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iOrderService.queryOrderPayStatus(orderNo,user.getId());
    }
}
