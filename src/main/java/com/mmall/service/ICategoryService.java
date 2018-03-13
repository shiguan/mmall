package com.mmall.service;

import com.mmall.common.ServerResponse;
import com.mmall.pojo.Category;

import java.util.List;

/**
 * Created by 15M-4528S on 2018/3/13.
 */
public interface ICategoryService {

    ServerResponse<String> addCategory(String categoryName,Integer parentId);

    ServerResponse<String> setCategoryName(String newName,Integer categoryId);

    ServerResponse<List<Category>> getCategory(int categoryId);

    ServerResponse<List<Integer>> getDeepCategory(Integer categoryId);
}
