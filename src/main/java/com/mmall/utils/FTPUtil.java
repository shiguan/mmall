package com.mmall.utils;


import org.apache.commons.net.ftp.FTPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

/**
 * Created by 15M-4528S on 2018/3/15.
 */
public class FTPUtil {

    private static String FTPIP = PropertiesUtil.getProperties("ftp.server.ip");
    private static String FTPUser = PropertiesUtil.getProperties("ftp.user");
    private static String FTPPass = PropertiesUtil.getProperties("ftp.pass");

    private String ip;
    private int port;
    private String user;
    private String pwd;
    private FTPClient ftpClient;

    private static final Logger logger = LoggerFactory.getLogger(FTPUtil.class);

    public FTPUtil(String ip, int port, String user, String pwd) {
        this.ip = ip;
        this.port = port;
        this.user = user;
        this.pwd = pwd;
    }

    public static boolean uploadFile(List<File> list) throws IOException {
        FTPUtil ftpUtil = new FTPUtil(FTPIP,21,FTPUser,FTPPass);
        logger.info("开始连接FTP服务器。。。。。。。。。。");
        boolean result = ftpUtil.uploadFile("img",list);
        logger.info("关闭FTP服务器。。。。。。。。。。。。。");
        return result;
    }

    private boolean uploadFile(String remotePath,List<File> list) throws IOException {
        boolean upload = true;
        FileInputStream fileInputStream = null;
        if(connectServer(this.ip,this.port,this.user,this.pwd)){
            try {
                ftpClient.changeWorkingDirectory(remotePath);
                ftpClient.setBufferSize(1024);
                ftpClient.setControlEncoding("UTF-8");
                ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
                //打开FTP服务器的被动模式
                ftpClient.enterLocalPassiveMode();
                for(File fileItem:list){
                    fileInputStream = new FileInputStream(fileItem);
                    ftpClient.storeFile(fileItem.getName(),fileInputStream);
                }
            } catch (IOException e) {
                upload = false;
                logger.error("上传文件异常",e);
            }finally {
                fileInputStream.close();
                ftpClient.disconnect();
            }
        }
        return upload;
    }

    //连接到FTP服务器
    private boolean connectServer(String ip,int port,String user,String pwd){

        boolean isSuccess = false;
        ftpClient = new FTPClient();
        try {
            ftpClient.connect(ip);
            isSuccess = ftpClient.login(user,pwd);
        } catch (IOException e) {
            logger.error("连接FTP服务器错误",e);
        }
        return isSuccess;
    }

     public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public FTPClient getFtpClient() {
        return ftpClient;
    }

    public void setFtpClient(FTPClient ftpClient) {
        this.ftpClient = ftpClient;
    }


}
