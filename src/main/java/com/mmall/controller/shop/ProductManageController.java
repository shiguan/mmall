package com.mmall.controller.shop;

import com.github.pagehelper.PageInfo;
import com.mmall.common.Constants;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Product;
import com.mmall.pojo.User;
import com.mmall.service.IFileService;
import com.mmall.service.IProductService;
import com.mmall.service.IUserService;
import com.mmall.utils.PropertiesUtil;
import com.mmall.vo.ProductDetailVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by 15M-4528S on 2018/3/13.
 */
@Controller
@RequestMapping("/shop/product")
public class ProductManageController {

    @Autowired
    private IUserService iUserService;
    @Autowired
    private IProductService iProductService;
    @Autowired
    private IFileService iFileService;

    @RequestMapping("/save.do")
    @ResponseBody
    public ServerResponse<String> addProduct(HttpSession session,Product product){
        User user = (User) session.getAttribute(Constants.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorMsg("用户未登陆");
        }
        ServerResponse<String> response = iUserService.checkAdminRole(user);
        if(!response.isSuccess()){
            return ServerResponse.createByErrorMsg("用户无权限");
        }
        return iProductService.addProduct(product);
    }

    @RequestMapping("/set_sale_status.do")
    @ResponseBody
    public ServerResponse<String> updateStatus(HttpSession session,Integer priductId,Integer status){
        User user = (User) session.getAttribute(Constants.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorMsg("用户未登陆");
        }
        ServerResponse<String> response = iUserService.checkAdminRole(user);
        if(!response.isSuccess()){
            return ServerResponse.createByErrorMsg("用户无权限");
        }
        return iProductService.setProductStatus(priductId,status);
    }

    @RequestMapping("/detail.do")
    @ResponseBody
    public ServerResponse<ProductDetailVo> getDetail(HttpSession session, Integer priductId){
        User user = (User) session.getAttribute(Constants.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorMsg("用户未登陆");
        }
        ServerResponse<String> response = iUserService.checkAdminRole(user);
        if(!response.isSuccess()){
            return ServerResponse.createByErrorMsg("用户无权限");
        }
        return iProductService.getDetail(priductId);
    }

    @RequestMapping("/list.do")
    @ResponseBody
    public ServerResponse<PageInfo> getList(HttpSession session, @RequestParam(value = "pageNum", defaultValue="1") int pageNum,
                                            @RequestParam(value = "pageSize", defaultValue="10") int pageSize){
        User user = (User) session.getAttribute(Constants.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorMsg("用户未登陆");
        }
        ServerResponse<String> response = iUserService.checkAdminRole(user);
        if(!response.isSuccess()){
            return ServerResponse.createByErrorMsg("用户无权限");
        }
        return iProductService.getList(pageNum,pageSize);
    }

    @RequestMapping("/search.do")
    @ResponseBody
    public ServerResponse<PageInfo> productSearch(HttpSession session,String productName,Integer productId,
                                              @RequestParam(value = "pageNum", defaultValue="1") int pageNum,
                                            @RequestParam(value = "pageSize", defaultValue="10") int pageSize){
        User user = (User) session.getAttribute(Constants.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorMsg("用户未登陆");
        }
        ServerResponse<String> response = iUserService.checkAdminRole(user);
        if(!response.isSuccess()){
            return ServerResponse.createByErrorMsg("用户无权限");
        }
        return iProductService.productSearch(productName,productId,pageNum,pageSize);
    }

    @RequestMapping("/upload.do")
    @ResponseBody
    public ServerResponse upload(HttpSession session,MultipartFile multipartFile, HttpServletRequest request){
//        User user = (User) session.getAttribute(Constants.CURRENT_USER);
//        if(user == null){
//            return ServerResponse.createByErrorMsg("用户未登陆");
//        }
//        ServerResponse<String> response = iUserService.checkAdminRole(user);
//        if(!response.isSuccess()){
//            return ServerResponse.createByErrorMsg("用户无权限");
//        }
        String path = request.getSession().getServletContext().getRealPath("/upload");
//        ServletContext servletContext = request.getSession().getServletContext();
        path = this.getClass().getClassLoader().getResource("/").getPath();
        path = "E://upload";
        String targetFileName = iFileService.upload(multipartFile,path);
        String url = PropertiesUtil.getProperties("ftp.server.http.prefix") + targetFileName;
        Map<String,String> fileMap = new HashMap();
        fileMap.put("file_path",url);
        return ServerResponse.createBySuccessData(fileMap);
    }

    @RequestMapping("/richtext_img_upload.do")
    @ResponseBody
    public Map uploadMessage(HttpSession session, MultipartFile multipartFile, HttpServletRequest request, HttpServletResponse httpResponse){
        Map<String,Object> map = new HashMap();
//        User user = (User) session.getAttribute(Constants.CURRENT_USER);
//        if(user == null){
//            map.put("success",false);
//            map.put("msg","用户未登陆");
//            return map;
//        }
//        ServerResponse<String> response = iUserService.checkAdminRole(user);
//        if(!response.isSuccess()){
//            map.put("success",false);
//            map.put("msg","用户无权限");
//            return map;
//        }
        String path = request.getSession().getServletContext().getRealPath("upload");
        String targetFileName = iFileService.upload(multipartFile,path);
        if(StringUtils.isBlank(targetFileName)){
            map.put("success",false);
            map.put("msg","上传失败");
            return map;
        }
        String url = PropertiesUtil.getProperties("ftp.server.http.prefix") + targetFileName;
        //前端插件对返回值的要求
        map.put("success",true);
        map.put("msg","上传成功");
        map.put("url",url);
        httpResponse.addHeader("Access-Controller-Allow-Headers","X-File-Name");
        return map;
    }
}
