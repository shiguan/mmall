package com.mmall.service.impl;

import com.mmall.common.Constants;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.dao.CartMapper;
import com.mmall.dao.ProductMapper;
import com.mmall.pojo.Cart;
import com.mmall.pojo.Product;
import com.mmall.pojo.User;
import com.mmall.service.ICartService;
import com.mmall.utils.BigDecimalUtil;
import com.mmall.utils.PropertiesUtil;
import com.mmall.vo.CartProductVo;
import com.mmall.vo.CartVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 15M-4528S on 2018/3/18.
 */
@Service("iCartService")
public class CartServiceImpl implements ICartService {

    @Autowired
    private CartMapper cartMapper;
    @Autowired
    private ProductMapper productMapper;


    public ServerResponse<CartVo> add(Integer productId,Integer count,Integer userId){
        if(productId == null || count == null){
            return ServerResponse.createByErrorCode(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }

        Cart cart = cartMapper.selectByUserIdAndProductId(userId,productId);
        int resultCount;
        if(cart == null){
            Cart cartItem = new Cart();
            cartItem.setChecked(Constants.Cart.CHECK_IN);
            cartItem.setProductId(productId);
            cartItem.setQuantity(count);
            cartItem.setUserId(userId);
            resultCount = cartMapper.insert(cartItem);
        }else{
            Cart temp = new Cart();
            temp.setId(cart.getId());
            temp.setQuantity(cart.getQuantity() + count);
            resultCount = cartMapper.updateByPrimaryKeySelective(temp);
        }
        if(resultCount == 0){
            return ServerResponse.createByErrorMsg("添加/修改购物车失败");
        }
        return this.list(userId);
    }

    public ServerResponse<CartVo> deleteProduct(String productId,Integer userId){
        if(StringUtils.isBlank(productId)){
            return ServerResponse.createByErrorCode(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }

        String[] productIds = productId.split(",");
        List<String> productIdList = new ArrayList<String>();
        for(String item:productIds){
            productIdList.add(item);
        }
        cartMapper.deleteByProductIdsAndUserId(productIdList,userId);
        return this.list(userId);
    }

    public ServerResponse<CartVo> list(Integer userId){
        CartVo cartVo = this.getCartVoLimit(userId);
        return ServerResponse.createBySuccessData(cartVo);
    }

    public ServerResponse<CartVo> selectAll(Integer userId,Integer checked){
        cartMapper.checkOrUnCheckAll(userId,checked);
        return this.list(userId);
    }

    public ServerResponse<CartVo> select(Integer userId,Integer productId,Integer checked){
        if(productId == null){
            return ServerResponse.createByErrorCode(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        Cart cart = cartMapper.selectByUserIdAndProductId(userId,productId);
        if(cart == null){
            return ServerResponse.createByErrorMsg("购物车中没有此商品");
        }
        Cart temp = new Cart();
        temp.setId(cart.getId());
        temp.setChecked(checked);
        cartMapper.updateByPrimaryKeySelective(temp);
        return this.list(userId);
    }

    public ServerResponse<Integer> getCartProductCount(Integer userId){
         Integer result = cartMapper.getProductCountByUserId(userId);
         return ServerResponse.createBySuccessData(result);
    }

    private CartVo getCartVoLimit(Integer userId){
        CartVo cartVo = new CartVo();
        List<Cart> cartList = cartMapper.selectByUserId(userId);
        BigDecimal cartTotalPrice = new BigDecimal("0.0");
        if(cartList == null){
            //todo
            return cartVo;
        }
        List<CartProductVo> cartProductVoList = new ArrayList<CartProductVo>();
        for (Cart item:cartList){
            Product product = productMapper.selectByPrimaryKey(item.getProductId());
            if(product == null){
                //todo
                continue;
            }
            CartProductVo cartProductVo = assembleCartProductVo(item,product);
            if(item.getChecked() == Constants.Cart.CHECK_IN){
                cartTotalPrice = BigDecimalUtil.add(cartTotalPrice.doubleValue(),cartProductVo.getProductTotalPrice().doubleValue());
            }
            cartProductVoList.add(cartProductVo);
        }
        cartVo.setCartProductVoList(cartProductVoList);
        cartVo.setCartTotalPrice(cartTotalPrice);
        cartVo.setImageHost(PropertiesUtil.getProperties("ftp.server.http.prefix"));
        cartVo.setAllChecked(this.isCheckedAll(userId));
        return cartVo;
    }

    private CartProductVo assembleCartProductVo(Cart cart,Product product){
        CartProductVo cartProductVo = new CartProductVo();
        cartProductVo.setId(cart.getId());
        cartProductVo.setProductId(product.getId());
        cartProductVo.setProductMainImage(product.getMainImage());
        cartProductVo.setProductName(product.getName());
        cartProductVo.setProductPrice(product.getPrice());
        cartProductVo.setProductStatus(product.getStatus());
        cartProductVo.setProductStock(product.getStock());
        cartProductVo.setProductSubtitle(product.getSubtitle());
        cartProductVo.setProductChecked(cart.getChecked());
        cartProductVo.setUserId(cart.getUserId());

        if(product.getStock() >= cart.getQuantity()){
            cartProductVo.setLimitQuantity(Constants.Cart.LIMIT_NUM_SUCCESS);
            cartProductVo.setQuantity(cart.getQuantity());
        }else{
            cartProductVo.setQuantity(product.getStock());
            cartProductVo.setLimitQuantity(Constants.Cart.LIMIT_NUM_FAIL);
            //更新购物车中的商品数量
            Cart cartTemp = new Cart();
            cartTemp.setQuantity(product.getStock());
            cartTemp.setId(cart.getId());
            cartMapper.updateByPrimaryKeySelective(cartTemp);
        }

        cartProductVo.setProductTotalPrice(BigDecimalUtil.mul(cart.getQuantity().doubleValue(),product.getPrice().doubleValue()));
        cartProductVo.setProductChecked(cart.getChecked());
        return cartProductVo;
    }

    private boolean isCheckedAll(Integer userId){
        if(userId == null){
            return false;
        }
        return cartMapper.selectProductCheckedAllByUserId(userId) == 0 ? true : false;
    }
}
