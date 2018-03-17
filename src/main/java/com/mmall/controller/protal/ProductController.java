package com.mmall.controller.protal;

import com.github.pagehelper.PageInfo;
import com.mmall.common.Constants;
import com.mmall.common.ServerResponse;
import com.mmall.service.IProductService;
import com.mmall.vo.ProductDetailVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by 15M-4528S on 2018/3/17.
 */
@Controller
@RequestMapping("/product")
public class ProductController {

    @Autowired
    private IProductService iProductService;

    @RequestMapping("/detail.do")
    @ResponseBody
    public ServerResponse<ProductDetailVo> getDetail(Integer productId){
        ServerResponse<ProductDetailVo> response = iProductService.getDetail(productId);
        if(!response.isSuccess()){
            return response;
        }
        ProductDetailVo productDetailVo = response.getData();
        if(productDetailVo.getStatus() != Constants.ProductStatus.SALE.getCode()){
            return ServerResponse.createByErrorMsg("商品已下架");
        }
        return response;
    }

    @RequestMapping("/list.do")
    @ResponseBody
    public ServerResponse<PageInfo> list(@RequestParam(value="keyword",required=false) String keyword,
                                         @RequestParam(value="categoryId",required=false) Integer categoryId
            ,@RequestParam(value = "pageNum",defaultValue = "1") int pageNum,@RequestParam(value = "pageSize",defaultValue = "10")  int pageSize,
                                         @RequestParam(value = "orderBy",defaultValue = "")  String orderBy){
        return iProductService.getListByKeywordCategory(keyword,categoryId,pageNum,pageSize,orderBy);
    }
}
