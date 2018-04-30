package com.mmall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mmall.common.Constants;
import com.mmall.common.ServerResponse;
import com.mmall.dao.CategoryMapper;
import com.mmall.dao.ProductMapper;
import com.mmall.pojo.Category;
import com.mmall.pojo.Product;
import com.mmall.service.ICategoryService;
import com.mmall.service.IProductService;
import com.mmall.utils.DateUtil;
import com.mmall.utils.PropertiesUtil;
import com.mmall.vo.ProductDetailVo;
import com.mmall.vo.ProductListVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import com.mmall.utils.PropertiesUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 15M-4528S on 2018/3/13.
 */
@Service("iProductService")
public class ProductServiceImpl implements IProductService {

    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private ICategoryService iCategoryService;

    public ServerResponse<String> addProduct(Product product){
       if(product == null){
           return ServerResponse.createByErrorMsg("参数错误");
       }
       if(StringUtils.isNotBlank(product.getSubImages())){
           String[] images = product.getSubImages().split(",");
           if(images.length > 0){
               product.setMainImage(images[0]);
           }
       }
       int resultCount;
       if(product.getId() != null){
           //更新
           resultCount = productMapper.updateByPrimaryKeySelective(product);
       }else{
           //新增
           resultCount = productMapper.insert(product);
       }
       if(resultCount == 0){
           return ServerResponse.createByErrorMsg("新增或更新产品失败");
       }
       return ServerResponse.createBySuccessMsg("新增或更新产品成功");
    }

    public ServerResponse<String> setProductStatus(Integer productId,Integer status){
        if(productId == null || status == null){
            return  ServerResponse.createByErrorMsg("参数错误");
        }
        int resultCount = productMapper.updateStatusByProductId(productId,status);
        if(resultCount == 0){
            return ServerResponse.createByErrorMsg("更新产品状态失败");
        }
        return ServerResponse.createBySuccessMsg("更新产品状态成功");
    }

    public ServerResponse<ProductDetailVo> getDetail(Integer productId){
        if(productId == null){
            return  ServerResponse.createByErrorMsg("参数错误");
        }
        Product product = productMapper.selectByPrimaryKey(productId);
        if(product == null){
            return  ServerResponse.createByErrorMsg("不存在此商品");
        }
        ProductDetailVo productDetailVo = assembleProductDetailVo(product);
        return ServerResponse.createBySuccessData(productDetailVo);
    }

    private ProductDetailVo assembleProductDetailVo(Product product){
        ProductDetailVo productDetailVo = new ProductDetailVo();
        productDetailVo.setId(product.getId());
        productDetailVo.setCategoryId(product.getCategoryId());
        productDetailVo.setDetail(product.getDetail());
        //productDetailVo.setCreateTime(product.getCreateTime());
        productDetailVo.setMainImage(product.getMainImage());
        productDetailVo.setStock(product.getStock());
        productDetailVo.setPrice(product.getPrice());
        productDetailVo.setName(product.getName());
        productDetailVo.setSubtitle(product.getSubtitle());
        productDetailVo.setSubImages(product.getSubImages());
        //productDetailVo.setUpdateTime(product.getUpdateTime());
        productDetailVo.setStatus(product.getStatus());

        productDetailVo.setImageHost(PropertiesUtil.getProperties("ftp.server.http.prefix","http://img.happymmall.com/"));

        Category category = categoryMapper.selectByPrimaryKey(product.getCategoryId());
        if(category == null){
            productDetailVo.setParentCategoryId(0);
        }else{
            productDetailVo.setParentCategoryId(category.getParentId());
        }
        productDetailVo.setCreateTime(DateUtil.dateToStr(product.getCreateTime()));
        productDetailVo.setUpdateTime(DateUtil.dateToStr(product.getUpdateTime()));
        return productDetailVo;
    }

    public ServerResponse<PageInfo> getList(int pageNum,int pageSize){
        PageHelper.startPage(pageNum,pageSize);
        List<Product> products = productMapper.selectAll();
        List<ProductListVo> results = new ArrayList<ProductListVo>();
        for(Product product:products){
            ProductListVo productListVo = assembleProductListVo(product);
            results.add(productListVo);
        }
        PageInfo pageInfo = new PageInfo(products);
        pageInfo.setList(results);
        return ServerResponse.createBySuccessData(pageInfo);
    }

    private ProductListVo assembleProductListVo(Product product){
        ProductListVo productListVo = new ProductListVo();
        productListVo.setId(product.getId());
        productListVo.setCategoryId(product.getCategoryId());
        productListVo.setName(product.getName());
        productListVo.setSubtitle(product.getSubtitle());
        productListVo.setPrice(product.getPrice());
        productListVo.setMainImage(product.getMainImage());
        productListVo.setStatus(product.getStatus());

        productListVo.setImageHost(PropertiesUtil.getProperties("ftp.server.http.prefix","http://image.lr.com/"));
        return productListVo;
    }

    public ServerResponse<PageInfo> productSearch(String productName,Integer productId,int pageNum,int pageSize){
        PageHelper.startPage(pageNum,pageSize);
        List<Product> products = productMapper.selectByProductNameAndId(productName,productId);
        List<ProductListVo> results = new ArrayList<ProductListVo>();
        for(Product product:products){
            ProductListVo productListVo = assembleProductListVo(product);
            results.add(productListVo);
        }
        PageInfo pageInfo = new PageInfo(products);
        pageInfo.setList(results);
        return ServerResponse.createBySuccessData(pageInfo);
    }

    public ServerResponse<PageInfo> getListByKeywordCategory(String keyword,Integer categoryId,
                                                             int pageNum,int pageSize,String orderBy){
        //?????
        if(StringUtils.isBlank(keyword) && categoryId == null){
            return  ServerResponse.createByErrorMsg("参数错误");
        }
        Category category = categoryMapper.selectByPrimaryKey(categoryId);
        List<ProductListVo> list;
        list = new ArrayList<ProductListVo>();
        PageHelper.startPage(pageNum,pageSize);
        if(category == null  && StringUtils.isBlank(keyword)){
            PageInfo pageInfo = new PageInfo(list);
            return ServerResponse.createBySuccessData(pageInfo);
        }
        List<Integer> categoryIds = iCategoryService.getDeepCategory(categoryId).getData();
        if(StringUtils.isNotBlank(keyword)){
            keyword = "&" + keyword + "%";
        }
        if(StringUtils.isBlank(orderBy)){
            String[] orderByArray = orderBy.split("_");
            if(orderByArray.length < 2){
                //todo
            }
            PageHelper.orderBy(orderByArray[0] + " " + orderByArray[1]);
        }
        List<Product> tempList = productMapper.selectByProductNameAndCategoryIds(StringUtils.isBlank(keyword)?null:keyword,
                categoryIds.size()==0?null:categoryIds);
        for(Product product:tempList){
            ProductListVo productListVo = assembleProductListVo(product);
            list.add(productListVo);
        }
        PageInfo pageInfo = new PageInfo(tempList);
        pageInfo.setList(list);
        return ServerResponse.createBySuccessData(pageInfo);
    }
}
