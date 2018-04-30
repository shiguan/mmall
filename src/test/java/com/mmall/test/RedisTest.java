package com.mmall.test;

import com.github.pagehelper.PageInfo;
import com.mmall.common.ServerResponse;
import com.mmall.service.IProductService;
import com.mmall.service.IUserService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

/**
 * Created by 15M-4528S on 2018/4/30.
 */
@ContextConfiguration(locations = {"classpath*:applicationContext.xml"})
public class RedisTest extends AbstractJUnit4SpringContextTests {

    @Autowired
    private IProductService iProductService;

    @Test
    public void productTest(){

        ServerResponse response = iProductService.getList(1,10);
        PageInfo pageInfo = (PageInfo) response.getData();
        System.out.print(pageInfo.getList());
    }
}
