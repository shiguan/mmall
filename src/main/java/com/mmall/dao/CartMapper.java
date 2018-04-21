package com.mmall.dao;

import com.mmall.pojo.Cart;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CartMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Cart record);

    int insertSelective(Cart record);

    Cart selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Cart record);

    int updateByPrimaryKey(Cart record);

    Cart selectByUserIdAndProductId(@Param("userId") Integer userId,@Param("productId") Integer productId);

    List<Cart> selectByUserId(Integer userId);

    int selectProductCheckedAllByUserId(Integer userId);

    int deleteByProductIdsAndUserId(@Param("productIdList") List<String> productIdList,@Param("userId") Integer userId);

    int checkOrUnCheckAll(@Param("userId") Integer userId,@Param("checked") Integer checked);

    int getProductCountByUserId(Integer userId);

    List<Cart> selectCheckedByUserId(Integer userId);
}