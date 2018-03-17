package com.mmall.utils;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

/**
 * Created by 15M-4528S on 2018/3/14.
 */
public class PropertiesUtil {

    private static Logger logger = LoggerFactory.getLogger(PropertiesUtil.class);

    private static Properties properties;

    static{
        String fileName = "/mmall.properties";
        properties = new Properties();
        try {
            InputStream in = PropertiesUtil.class.getResourceAsStream(fileName);
            properties.load(new InputStreamReader(in,"UTF-8"));
        } catch (IOException e) {
            logger.error("读取配置文件错误",e);
        }
    }

    public static String getProperties(String key){
        String value = properties.getProperty(key.trim());
        if(StringUtils.isBlank(value)){
            return null;
        }
        return value.trim();
    }

    public static String getProperties(String key,String defultValue){
        String value = properties.getProperty(key.trim());
        if(StringUtils.isBlank(value)){
            value = defultValue;
        }
        return value.trim();
    }

//    public static void main(String[] args){
//        String name = getProperties("ftp.server.ip");
//
//    }
}
