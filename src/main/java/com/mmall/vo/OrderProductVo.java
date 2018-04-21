package com.mmall.vo;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by 15M-4528S on 2018/4/21.
 */
public class OrderProductVo {

    private List<OrderItemVo> orderItemVoList;

    private String imageHost;

    private BigDecimal totalPrice;

    public List<OrderItemVo> getOrderItemVoList() {
        return orderItemVoList;
    }

    public void setOrderItemVoList(List<OrderItemVo> orderItemVoList) {
        this.orderItemVoList = orderItemVoList;
    }

    public String getImageHost() {
        return imageHost;
    }

    public void setImageHost(String imageHost) {
        this.imageHost = imageHost;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }
}
