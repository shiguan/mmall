package com.mmall.service;

import com.github.pagehelper.PageInfo;
import com.mmall.common.ServerResponse;
import com.mmall.vo.OrderVo;

import java.util.Map;

/**
 * Created by 15M-4528S on 2018/4/18.
 */
public interface IOrderService {
    public ServerResponse pay(String path, Long orderNo, Integer userId);

    public ServerResponse callback(Map<String,String> map);

    public ServerResponse<Boolean> queryOrderPayStatus(Long orderNo,Integer userId);

    public ServerResponse create(Integer shippingId,Integer userId);

    public ServerResponse<String> cancle(Integer userId,Long orderNo);

    public ServerResponse getOrderCartProduct(Integer userId);

    public ServerResponse<OrderVo> detail(Integer userId, Long orderNo);

    public ServerResponse<PageInfo> list(Integer userId, int pageNum, int pageSize);
}
