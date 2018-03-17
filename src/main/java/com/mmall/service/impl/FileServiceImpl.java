package com.mmall.service.impl;

import com.google.common.collect.Lists;
import com.mmall.service.IFileService;
import com.mmall.utils.FTPUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


/**
 * Created by 15M-4528S on 2018/3/14.
 */
@Service("iFileService")
public class FileServiceImpl implements IFileService {

    private static Logger logger = LoggerFactory.getLogger(FileServiceImpl.class);

    public String upload(MultipartFile file,String path){
        String fileName = file.getOriginalFilename();
        String fileType = fileName.substring(fileName.lastIndexOf(".")+1);
        String uploadFileName = UUID.randomUUID().toString() +"."+ fileType;
        logger.info("上传文件，文件名："+uploadFileName+"  上传路径为："+path);

        File fileDir = new File(path);
        if(!fileDir.exists()){
            //设置文件夹可写的权限
            fileDir.setWritable(true);
            fileDir.mkdirs();
        }
        File targetFile = new File(path,uploadFileName);
        try {
            file.transferTo(targetFile);
            //文件已经成功上传到tomcat的path中
            // 将文件上传到ftp
            List<File> list = new ArrayList<File>();
            list.add(targetFile);
            FTPUtil.uploadFile(list);
//             FTPUtil.uploadFile(Lists.newArrayList(targetFile));

            // 删除tomcat中的文件
            targetFile.delete();
        } catch (IOException e) {
            logger.error("上传文件异常",e);
            return null;
        }
        return targetFile.getName();
    }

    public static void main(String[] args){

    }
}
