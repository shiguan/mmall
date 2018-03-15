package com.mmall.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * Created by 15M-4528S on 2018/3/14.
 */
public interface IFileService {

    String upload(MultipartFile file, String path);
}
