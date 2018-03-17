package com.mmall.service;

import com.github.pagehelper.PageInfo;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Product;
import com.mmall.vo.ProductDetailVo;

/**
 * Created by 15M-4528S on 2018/3/13.
 */
public interface IProductService {

    ServerResponse<String> addProduct(Product product);

    ServerResponse<String> setProductStatus(Integer productId,Integer status);

    ServerResponse<ProductDetailVo> getDetail(Integer productId);

    ServerResponse<PageInfo> getList(int pageNum, int pageSize);

    ServerResponse<PageInfo> productSearch(String productName,Integer productId,int pageNum,int pageSize);

    ServerResponse<PageInfo> getListByKeywordCategory(String keyword,Integer categoryId,
                                                      int pageNum,int pageSize,String orderBy);
}
