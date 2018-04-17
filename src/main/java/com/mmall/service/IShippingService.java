package com.mmall.service;

import com.github.pagehelper.PageInfo;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Shipping;

import java.util.Map;

/**
 * Created by 15M-4528S on 2018/3/18.
 */
public interface IShippingService {

    ServerResponse<Map> add(Integer userId, Shipping shipping);

    ServerResponse delete(Integer userId,Integer shippingId);

    ServerResponse update(Integer userId,Shipping shipping);

    ServerResponse<Shipping> get(Integer userId,Integer shippingId);

    ServerResponse<PageInfo> list(int pageNum, int pageSize, Integer userId);
}
