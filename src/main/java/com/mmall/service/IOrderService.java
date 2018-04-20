package com.mmall.service;

import com.mmall.common.ServerResponse;

import java.util.Map;

/**
 * Created by 15M-4528S on 2018/4/18.
 */
public interface IOrderService {
    public ServerResponse pay(String path, Long orderNo, Integer userId);

    public ServerResponse callback(Map<String,String> map);

    public ServerResponse<Boolean> queryOrderPayStatus(Long orderNo,Integer userId);
}
