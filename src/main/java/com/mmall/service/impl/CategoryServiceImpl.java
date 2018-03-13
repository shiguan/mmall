package com.mmall.service.impl;

import com.mmall.common.ServerResponse;
import com.mmall.dao.CategoryMapper;
import com.mmall.pojo.Category;
import com.mmall.pojo.User;
import com.mmall.service.ICategoryService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by 15M-4528S on 2018/3/13.
 */
@Service
public class CategoryServiceImpl implements ICategoryService {

    @Autowired
    private CategoryMapper categoryMapper;
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    public ServerResponse<List<Category>> getCategory(int categoryId){
        List<Category> list = categoryMapper.selectByParentId(categoryId);
        if(CollectionUtils.isEmpty(list)){
            //不用报出错误
            //return ServerResponse.createByErrorMsg("无子分类");
            logger.info("未找到当前分类的子分类，categoryId ID："+categoryId);
        }
        return ServerResponse.createBySuccessData(list);
    }

    public ServerResponse<String> addCategory(String categoryName,Integer parentId){
        if(parentId == null || StringUtils.isBlank(categoryName)){
            return ServerResponse.createByErrorMsg("参数错误");
        }
        Category category = new Category();
        category.setName(categoryName);
        category.setParentId(parentId);
        category.setStatus(true);
        int resultCount = categoryMapper.insert(category);
        if(resultCount == 0){
            return ServerResponse.createByErrorMsg("新增品类失败");
        }
        return ServerResponse.createBySuccessMsg("新增品类成功");
    }

    public ServerResponse<String> setCategoryName(String newName,Integer categoryId){
        if(categoryId == null || StringUtils.isBlank(newName)){
            return ServerResponse.createByErrorMsg("参数错误");
        }
        Category category = new Category();
        category.setName(newName);
        category.setId(categoryId);
        int resultCount = categoryMapper.updateByPrimaryKeySelective(category);
        if(resultCount == 0){
            return ServerResponse.createByErrorMsg("修改品类失败");
        }
        return ServerResponse.createBySuccessMsg("修改品类成功");
    }

    public ServerResponse<List<Integer>> getDeepCategory(Integer categoryId){
        if(categoryId == null){
            return ServerResponse.createBySuccess();
        }
        Set<Category> set = new HashSet<Category>();
        findChildCategory(set,categoryId);
        List<Integer> list = new ArrayList<Integer>();
        list.add(categoryId);
        for(Category category:set){
            list.add(category.getId());
        }
        return ServerResponse.createBySuccessData(list);
    }

    private Set<Category> findChildCategory(Set<Category> set,Integer categoryId){
//        Category category = categoryMapper.selectByPrimaryKey(categoryId);
//        if(category != null ){
//            set.add(category);
//        }
        List<Category> list = categoryMapper.selectByParentId(categoryId);
        for(int i = 0; i < list.size(); i++){
            Category temp = list.get(i);
            set.add(temp);
            findChildCategory(set,temp.getId());
        }
        return set;
    }
}
