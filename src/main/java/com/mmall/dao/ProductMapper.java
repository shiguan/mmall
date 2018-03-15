package com.mmall.dao;

import com.mmall.pojo.Product;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ProductMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Product record);

    int insertSelective(Product record);

    Product selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Product record);

    int updateByPrimaryKey(Product record);

    int updateStatusByProductId(@Param("productId") Integer productId,@Param("status")  Integer status);

    List<Product> selectAll();

    List<Product> selectByProductNameAndId(@Param("productName") String productName,@Param("productId") Integer productId);
}