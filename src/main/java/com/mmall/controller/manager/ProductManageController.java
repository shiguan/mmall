package com.mmall.controller.manager;

import com.github.pagehelper.PageInfo;
import com.mmall.common.Constants;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.IProductService;
import com.mmall.service.IUserService;
import com.mmall.vo.ProductDetailVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.HttpSession;


/**
 * Created by 15M-4528S on 2018/3/13.
 */
@Controller
@RequestMapping("/manager/product")
public class ProductManageController {

    @Autowired
    private IUserService iUserService;
    @Autowired
    private IProductService iProductService;


    @RequestMapping("/set_sale_status.do")
    @ResponseBody
    public ServerResponse<String> updateStatus(HttpSession session,Integer priductId,Integer status){
        User user = (User) session.getAttribute(Constants.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorMsg("用户未登陆");
        }
        ServerResponse<String> response = iUserService.checkAdminRole(user);
        if(!response.isSuccess()){
            return ServerResponse.createByErrorMsg("用户无权限");
        }
        return iProductService.setProductStatus(priductId,status);
    }

    @RequestMapping("/detail.do")
    @ResponseBody
    public ServerResponse<ProductDetailVo> getDetail(HttpSession session, Integer priductId){
        User user = (User) session.getAttribute(Constants.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorMsg("用户未登陆");
        }
        ServerResponse<String> response = iUserService.checkAdminRole(user);
        if(!response.isSuccess()){
            return ServerResponse.createByErrorMsg("用户无权限");
        }
        return iProductService.getDetail(priductId);
    }

    @RequestMapping("/list.do")
    @ResponseBody
    public ServerResponse<PageInfo> getList(HttpSession session, @RequestParam(value = "pageNum", defaultValue="1") int pageNum,
                                            @RequestParam(value = "pageSize", defaultValue="10") int pageSize){
        User user = (User) session.getAttribute(Constants.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorMsg("用户未登陆");
        }
        ServerResponse<String> response = iUserService.checkAdminRole(user);
        if(!response.isSuccess()){
            return ServerResponse.createByErrorMsg("用户无权限");
        }
        return iProductService.getList(pageNum,pageSize);
    }

    @RequestMapping("/search.do")
    @ResponseBody
    public ServerResponse<PageInfo> productSearch(HttpSession session,String productName,Integer productId,
                                              @RequestParam(value = "pageNum", defaultValue="1") int pageNum,
                                            @RequestParam(value = "pageSize", defaultValue="10") int pageSize){
        User user = (User) session.getAttribute(Constants.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorMsg("用户未登陆");
        }
        ServerResponse<String> response = iUserService.checkAdminRole(user);
        if(!response.isSuccess()){
            return ServerResponse.createByErrorMsg("用户无权限");
        }
        return iProductService.productSearch(productName,productId,pageNum,pageSize);
    }

}
