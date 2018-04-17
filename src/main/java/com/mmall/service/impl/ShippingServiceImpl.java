package com.mmall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.dao.ShippingMapper;
import com.mmall.pojo.Shipping;
import com.mmall.service.IShippingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static javafx.scene.input.KeyCode.H;

/**
 * Created by 15M-4528S on 2018/3/18.
 */
@Service("iShippingService")
public class ShippingServiceImpl implements IShippingService {

    @Autowired
    private ShippingMapper shippingMapper;

    public ServerResponse<Map> add(Integer userId, Shipping shipping){
        shipping.setUserId(userId);
        int resultCount = shippingMapper.insert(shipping);
        if(resultCount == 0){
            return ServerResponse.createByErrorMsg("新建地址失败");
        }
        Map result = new HashMap();
        result.put("shippingId",shipping.getId());
        return ServerResponse.createBySuccessAll("新建地址成功",result);
    }

    public ServerResponse delete(Integer userId,Integer shippingId){
        //容易横向越权
        //int resultCount = shippingMapper.deleteByPrimaryKey(shippingId);
        int resultCount = shippingMapper.deleteByUserIdShippingId(userId,shippingId);
        if(resultCount == 0){
            return ServerResponse.createByErrorMsg("删除地址失败");
        }
        return ServerResponse.createBySuccessMsg("删除地地址成功");
    }

    public ServerResponse update(Integer userId,Shipping shipping){
        if(shipping == null){
            return ServerResponse.createByErrorCode(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        //容易横向越权
        //int resultCount = shippingMapper.updateByPrimaryKeySelective(shipping);
        shipping.setUserId(userId);
        int resultCount = shippingMapper.updateByUserIdShippingId(shipping);
        if(resultCount == 0){
            return ServerResponse.createByErrorMsg("修改地址失败");
        }
        return ServerResponse.createBySuccessMsg("修改地地址成功");
    }

    public ServerResponse<Shipping> get(Integer userId,Integer shippingId){
        Shipping shipping = shippingMapper.selectByUserIdShippingId(userId,shippingId);
        if(shipping == null){
            return ServerResponse.createByErrorMsg("获取地址失败");
        }
        return ServerResponse.createBySuccessData(shipping);
    }

    public ServerResponse<PageInfo> list(int pageNum,int pageSize,Integer userId){
        PageHelper.startPage(pageNum,pageSize);
        List<Shipping> shippingList = shippingMapper.selectByUserId(userId);
        PageInfo pageInfo = new PageInfo(shippingList);
        return ServerResponse.createBySuccessData(pageInfo);
    }
}
