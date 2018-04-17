package com.mmall.service;

import com.mmall.common.ServerResponse;
import com.mmall.vo.CartVo;

/**
 * Created by 15M-4528S on 2018/3/18.
 */
public interface ICartService {

    ServerResponse<CartVo> add(Integer productId, Integer count, Integer userId);

    ServerResponse<CartVo> deleteProduct(String productId,Integer userId);

    ServerResponse<CartVo> list(Integer userId);

    ServerResponse<CartVo> selectAll(Integer userId,Integer checked);

    ServerResponse<CartVo> select(Integer userId,Integer productId,Integer checked);

    ServerResponse<Integer> getCartProductCount(Integer userId);
}
