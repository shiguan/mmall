package com.mmall.service.impl;

import com.alipay.api.AlipayResponse;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.alipay.demo.trade.config.Configs;
import com.alipay.demo.trade.model.ExtendParams;
import com.alipay.demo.trade.model.GoodsDetail;
import com.alipay.demo.trade.model.builder.AlipayTradePrecreateRequestBuilder;
import com.alipay.demo.trade.model.result.AlipayF2FPrecreateResult;
import com.alipay.demo.trade.service.AlipayTradeService;
import com.alipay.demo.trade.service.impl.AlipayTradeServiceImpl;
import com.alipay.demo.trade.utils.ZxingUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mmall.common.Constants;
import com.mmall.common.ServerResponse;
import com.mmall.dao.*;
import com.mmall.pojo.*;
import com.mmall.service.IOrderService;
import com.mmall.utils.BigDecimalUtil;
import com.mmall.utils.DateUtil;
import com.mmall.utils.FTPUtil;
import com.mmall.utils.PropertiesUtil;
import com.mmall.vo.OrderItemVo;
import com.mmall.vo.OrderProductVo;
import com.mmall.vo.OrderVo;
import com.mmall.vo.ShippingVo;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;


/**
 * Created by 15M-4528S on 2018/4/18.
 */
@Service("iOrderService")
public class OrderServiceImpl implements IOrderService {

    private static final Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderItemMapper orderItemMapper;
    @Autowired
    private PayInfoMapper payInfoMapper;
    @Autowired
    private CartMapper cartMapper;
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private ShippingMapper shippingMapper;

    public ServerResponse<String> sendGoods(Long orderNo){
        Order order = orderMapper.selectByOrderNo(orderNo);
        if(order == null){
            return ServerResponse.createByErrorMsg("订单不存在");
        }
        if(order.getStatus() < Constants.TradeStatusEnum.PAID.getCode()){
            return ServerResponse.createByErrorMsg("订单未付款");
        }
        Order updateOrder = new Order();
        updateOrder.setOrderNo(orderNo);
        updateOrder.setStatus(Constants.TradeStatusEnum.SHIPPED.getCode());
        updateOrder.setSendTime(new Date());
        int rowCount = orderMapper.updateByPrimaryKeySelective(updateOrder);
        if(rowCount == 0){
            return ServerResponse.createByErrorMsg("发货失败");
        }
        return ServerResponse.createBySuccessMsg("发货成功");
    }


    public ServerResponse<PageInfo> manageSearchByOrderNo(Long orderNo,int pageNum,int pageSize){
        PageHelper.startPage(pageNum,pageSize);
        List<Order> orderList = orderMapper.searchByOrderNo(orderNo);
        List<OrderVo> orderVoList = this.assembleOrderVoList(orderList,null);
        PageInfo result = new PageInfo(orderList);
        result.setList(orderVoList);
        return ServerResponse.createBySuccessData(result);
    }

    public ServerResponse<OrderVo> manageDetail(Long orderNo){
        Order order = orderMapper.selectByOrderNo(orderNo);
        if(order == null){
            return ServerResponse.createByErrorMsg("订单不存在");
        }
        List<OrderItem> orderItemList = orderItemMapper.selectByOrderNo(orderNo);
        OrderVo orderVo = this.assembleOrderVo(order,orderItemList);
        return ServerResponse.createBySuccessData(orderVo);
    }

    public ServerResponse<PageInfo> manageList(int pageNum,int pageSize){
        PageHelper.startPage(pageNum,pageSize);
        List<Order> orderList = orderMapper.selectAll();
        List<OrderVo> orderVoList = this.assembleOrderVoList(orderList,null);
        PageInfo result = new PageInfo(orderList);
        result.setList(orderVoList);
        return ServerResponse.createBySuccessData(result);
    }

    public ServerResponse<PageInfo> list(Integer userId,int pageNum,int pageSize){
        PageHelper.startPage(pageNum,pageSize);
        List<Order> orderList = orderMapper.selectByUserId(userId);
        List<OrderVo> orderVoList = this.assembleOrderVoList(orderList,userId);
        //todo ??
        PageInfo result = new PageInfo(orderList);
        result.setList(orderVoList);
        return ServerResponse.createBySuccessData(result);
    }

    private List<OrderVo> assembleOrderVoList(List<Order> orderList,Integer userId){
        List<OrderVo> orderVoList = new ArrayList<OrderVo>();
        for(Order order:orderList){
            List<OrderItem> orderItemList = null;
            if(userId == null){
                //todo 管理员查询的时候不需要userId
                orderItemList = orderItemMapper.selectByOrderNo(order.getOrderNo());
            }else {
                 orderItemList = orderItemMapper.selectByUserIdAndOrderNo(userId, order.getOrderNo());
            }
            orderVoList.add(this.assembleOrderVo(order,orderItemList));
        }
        return orderVoList;
    }

    public ServerResponse<OrderVo> detail(Integer userId,Long orderNo){
        Order order = orderMapper.selectByUserIdAndOrderNo(userId,orderNo);
        if(order == null){
            return ServerResponse.createByErrorMsg("订单不存在");
        }
        List<OrderItem> orderItemList = orderItemMapper.selectByUserIdAndOrderNo(userId,orderNo);
        OrderVo orderVo = this.assembleOrderVo(order,orderItemList);
        return ServerResponse.createBySuccessData(orderVo);
    }

    public ServerResponse getOrderCartProduct(Integer userId){
        OrderProductVo orderProductVo = new OrderProductVo();

        List<Cart> cartList = cartMapper.selectCheckedByUserId(userId);
        ServerResponse response = this. sumPrice(cartList);
        if(!response.isSuccess()){
            return response;
        }
        List<OrderItem> orderItemList = (List<OrderItem>) response.getData();
        //计算总价
        BigDecimal totalPrice = this.getTotalPrice(orderItemList);
        List<OrderItemVo> orderItemVoList = new ArrayList<>();
        for(OrderItem orderItem:orderItemList){
            orderItemVoList.add(this.assembleOrderItemVo(orderItem));
        }
        orderProductVo.setImageHost(PropertiesUtil.getProperties("ftp.server.http.prefix"));
        orderProductVo.setOrderItemVoList(orderItemVoList);
        orderProductVo.setTotalPrice(totalPrice);
        return ServerResponse.createBySuccessData(orderProductVo);
    }

    public ServerResponse<String> cancle(Integer userId,Long orderNo){
        Order order = orderMapper.selectByUserIdAndOrderNo(userId,orderNo);
        if(order == null){
            return ServerResponse.createByErrorMsg("订单不存在");
        }
        if(order.getStatus() >= Constants.TradeStatusEnum.PAID.getCode()){
            return ServerResponse.createByErrorMsg("订单已经付款，无法取消");
        }
        Order updateOrder = new Order();
        updateOrder.setOrderNo(orderNo);
        updateOrder.setStatus(Constants.TradeStatusEnum.CANCELED.getCode());
        int rowCount = orderMapper.updateByPrimaryKeySelective(updateOrder);
        if(rowCount > 0){
            return ServerResponse.createBySuccess();
        }
        return ServerResponse.createByError();
    }

    public ServerResponse create(Integer shippingId,Integer userId){
         List<Cart> cartList = cartMapper.selectCheckedByUserId(userId);
        ServerResponse response = sumPrice(cartList);
        if(!response.isSuccess()){
            return response;
        }
        List<OrderItem> orderItemList = (List<OrderItem>) response.getData();
        //计算总价
        BigDecimal totalPrice = this.getTotalPrice(orderItemList);

        Order order = this.assembleOrder(userId,shippingId,totalPrice);
        if(order == null){
            return ServerResponse.createByErrorMsg("生成订单错误");
        }
        for(OrderItem orderItem:orderItemList){
            orderItem.setOrderNo(order.getOrderNo());
        }

        //mybatis批量插入
        orderItemMapper.batchInsert(orderItemList);

        //减少产品库存
        this.reduceProductStock(orderItemList);

        //清空购物车
        cleanCart(cartList);

        OrderVo orderVo = assembleOrderVo(order,orderItemList);
        return ServerResponse.createBySuccessData(orderVo);
    }

    private OrderVo assembleOrderVo(Order order,List<OrderItem> orderItemList){
        OrderVo orderVo = new OrderVo();
        orderVo.setCloseTime(DateUtil.dateToStr(order.getCloseTime()));
        orderVo.setCreateTime(DateUtil.dateToStr(order.getCloseTime()));
        orderVo.setEndTime(DateUtil.dateToStr(order.getEndTime()));
        orderVo.setSendTime(DateUtil.dateToStr(order.getSendTime()));

        orderVo.setOrderNo(order.getOrderNo());
        orderVo.setPaymentType(order.getPaymentType());
        orderVo.setPaymentTypeDesc(Constants.PaymentTypeEnum.ONLINE_PAY.getValue());
        orderVo.setPayment(Constants.PaymentTypeEnum.codeOf(order.getPaymentType()).getValue());
        orderVo.setPostage(order.getPostage());
        orderVo.setStatus(order.getStatus());
        orderVo.setStatusDesc(Constants.TradeStatusEnum.codeOf(order.getStatus()).getValue());
        orderVo.setShippingId(order.getShippingId());

        Shipping shipping = shippingMapper.selectByPrimaryKey(order.getShippingId());
        if(shipping != null){
            orderVo.setReciveName(shipping.getReceiverName());
            orderVo.setShippingVo(assembleShippingVo(shipping));
        }
        orderVo.setImageHost(PropertiesUtil.getProperties("ftp.server.http.prefix"));

        //List<OrderItem> orderItemList
        List<OrderItemVo> orderItemVoList = new ArrayList<OrderItemVo>();
        for(OrderItem orderItem:orderItemList){
            orderItemVoList.add(assembleOrderItemVo(orderItem));
        }
        orderVo.setOrderItemVoList(orderItemVoList);
        return orderVo;
    }

    private OrderItemVo assembleOrderItemVo(OrderItem orderItem){
        OrderItemVo orderItemVo = new OrderItemVo();
        orderItemVo.setOrderNo(orderItem.getOrderNo());
        orderItemVo.setProductId(orderItem.getProductId());
        orderItemVo.setProductImage(orderItem.getProductImage());
        orderItemVo.setProductName(orderItem.getProductName());
        orderItemVo.setCurrentUnitPrice(orderItem.getCurrentUnitPrice());
        orderItemVo.setQuantity(orderItem.getQuantity());
        orderItemVo.setTotalPrice(orderItem.getTotalPrice());
        orderItemVo.setCreateTime(DateUtil.dateToStr(orderItem.getCreateTime()));
        return orderItemVo;
    }

    private ShippingVo assembleShippingVo(Shipping shipping){
        ShippingVo shippingVo = new ShippingVo();
        shippingVo.setReceiverName(shipping.getReceiverName());
        shippingVo.setReceiverAddress(shipping.getReceiverAddress());
        shippingVo.setReceiverCity(shipping.getReceiverCity());
        shippingVo.setReceiverDistrict(shipping.getReceiverDistrict());
        shippingVo.setReceiverMobile(shipping.getReceiverMobile());
        shippingVo.setReceiverPhone(shipping.getReceiverPhone());
        shippingVo.setReceiverProvince(shipping.getReceiverProvince());
        shippingVo.setReceiverZip(shipping.getReceiverZip());
        shippingVo.setCreateTime(shipping.getCreateTime());
        return shippingVo;
    }

    private void cleanCart(List<Cart> cartList){
        for(Cart cart:cartList){
            cartMapper.deleteByPrimaryKey(cart.getId());
        }
    }

    private void reduceProductStock(List<OrderItem> orderItemList){
        for(OrderItem orderItem:orderItemList){
            Product product = productMapper.selectByPrimaryKey(orderItem.getProductId());
            product.setStock(product.getStock() - orderItem.getQuantity());
            productMapper.updateByPrimaryKeySelective(product);
        }
    }

    private Order assembleOrder(Integer userId,Integer shippingId,BigDecimal totalPrice){
        long orderNo = createOrderNo();
        Order order = new Order();
        order.setOrderNo(orderNo);
        order.setShippingId(shippingId);
        order.setPayment(totalPrice);
        order.setUserId(userId);
        order.setStatus(Constants.TradeStatusEnum.NO_PAY.getCode());
        order.setPaymentType(Constants.PaymentTypeEnum.ONLINE_PAY.getCode());
        order.setPostage(0);
        int rowCount = orderMapper.insert(order);
        if(rowCount > 0){
            return order;
        }
        return  null;
    }

    //生成订单号
    private long createOrderNo(){
        long currentTime = System.currentTimeMillis();
        //   return currentTime + currentTime % 10;   不能并发
        return currentTime + new Random().nextInt(100);
    }

    private BigDecimal getTotalPrice(List<OrderItem> orderItemList){
        BigDecimal totalPrice = new BigDecimal("0");
        for(OrderItem orderItem:orderItemList){
            totalPrice = BigDecimalUtil.add(totalPrice.doubleValue(),orderItem.getTotalPrice().doubleValue());
        }
        return totalPrice;
    }

    public ServerResponse<List<OrderItem>> sumPrice(List<Cart> cartList){
        List<OrderItem> orderItemList = new ArrayList<>();
        if(cartList == null){
            return ServerResponse.createByErrorMsg("购物车为空");
        }

         for(Cart cart:cartList){
             OrderItem orderItem = new OrderItem();
             Product product = productMapper.selectByPrimaryKey(cart.getProductId());
             if(Constants.ProductStatus.SALE.getCode() != product.getStatus()){
                 return ServerResponse.createByErrorMsg("产品"+product.getName()+"不是售卖状态");
             }
             if(cart.getQuantity() > product.getStock()){
                 return ServerResponse.createByErrorMsg("产品"+product.getName()+"库存不足");
             }
             orderItem.setUserId(cart.getUserId());
             orderItem.setProductId(product.getId());
             orderItem.setProductImage(product.getMainImage());
             orderItem.setProductName(product.getName());
             orderItem.setQuantity(cart.getQuantity());
             orderItem.setCurrentUnitPrice(product.getPrice());
             orderItem.setTotalPrice(BigDecimalUtil.mul(product.getPrice().doubleValue(),cart.getQuantity().doubleValue()));
             orderItemList.add(orderItem);
         }
         return ServerResponse.createBySuccessData(orderItemList);
    }

    public ServerResponse pay(String path,Long orderNo,Integer userId){
        Map<String,String> map = new HashMap<String,String>();
        Order order = orderMapper.selectByUserIdAndOrderNo(userId,orderNo);
        if(order == null){
            return ServerResponse.createByErrorMsg("用户没有该订单");
        }
        map.put("orderNo", String.valueOf(order.getOrderNo()));


        // (必填) 商户网站订单系统中唯一订单号，64个字符以内，只能包含字母、数字、下划线，
        // 需保证商户系统端不能重复，建议通过数据库sequence生成，
        String outTradeNo = String.valueOf(order.getOrderNo());

        // (必填) 订单标题，粗略描述用户的支付目的。如“xxx品牌xxx门店当面付扫码消费”
        String subject = new StringBuilder("网上商城消费,订单号：").append(order.getOrderNo().toString()).toString();

        // (必填) 订单总金额，单位为元，不能超过1亿元
        // 如果同时传入了【打折金额】,【不可打折金额】,【订单总金额】三者,则必须满足如下条件:【订单总金额】=【打折金额】+【不可打折金额】
        String totalAmount = order.getPayment().toString();

        // (可选) 订单不可打折金额，可以配合商家平台配置折扣活动，如果酒水不参与打折，则将对应金额填写至此字段
        // 如果该值未传入,但传入了【订单总金额】,【打折金额】,则该值默认为【订单总金额】-【打折金额】
        String undiscountableAmount = "0";

        // 卖家支付宝账号ID，用于支持一个签约账号下支持打款到不同的收款账号，(打款到sellerId对应的支付宝账号)
        // 如果该字段为空，则默认为与支付宝签约的商户的PID，也就是appid对应的PID
        String sellerId = "";

        // 订单描述，可以对交易或商品进行一个详细地描述，比如填写"购买商品2件共15.00元"
        String body = new StringBuilder("购买商品：").append(outTradeNo).append(totalAmount).append("元").toString();

        // 商户操作员编号，添加此参数可以为商户操作员做销售统计
        String operatorId = "test_operator_id";

        // (必填) 商户门店编号，通过门店号和商家后台可以配置精准到门店的折扣信息，详询支付宝技术支持
        String storeId = "test_store_id";

        // 业务扩展参数，目前可添加由支付宝分配的系统商编号(通过setSysServiceProviderId方法)，详情请咨询支付宝技术支持
        ExtendParams extendParams = new ExtendParams();
        extendParams.setSysServiceProviderId("2088100200300400500");

        // 支付超时，定义为120分钟
        String timeoutExpress = "120m";

        // 商品明细列表，需填写购买商品详细信息，
        List<GoodsDetail> goodsDetailList = new ArrayList<GoodsDetail>();
        List<OrderItem> orderItemList = orderItemMapper.selectByUserIdAndOrderNo(userId,orderNo);
        for(OrderItem orderItem:orderItemList){
            // 创建一个商品信息，参数含义分别为商品id（使用国标）、名称、单价（单位为分）、数量，如果需要添加商品类别，详见GoodsDetail
            GoodsDetail goods1 = GoodsDetail.newInstance(orderItem.getId().toString(), orderItem.getProductName(),
                    BigDecimalUtil.mul(orderItem.getCurrentUnitPrice().doubleValue(),new Double(100)).longValue(),
                    orderItem.getQuantity());
            // 创建好一个商品后添加至商品明细列表
            goodsDetailList.add(goods1);
        }

        // 创建扫码支付请求builder，设置请求参数
        AlipayTradePrecreateRequestBuilder builder = new AlipayTradePrecreateRequestBuilder()
                .setSubject(subject).setTotalAmount(totalAmount).setOutTradeNo(outTradeNo)
                .setUndiscountableAmount(undiscountableAmount).setSellerId(sellerId).setBody(body)
                .setOperatorId(operatorId).setStoreId(storeId).setExtendParams(extendParams)
                .setTimeoutExpress(timeoutExpress)
                .setNotifyUrl(PropertiesUtil.getProperties("alipay.callback.url"))//支付宝服务器主动通知商户服务器里指定的页面http路径,根据需要设置
                .setGoodsDetailList(goodsDetailList);

        /** 一定要在创建AlipayTradeService之前调用Configs.init()设置默认参数
         *  Configs会读取classpath下的zfbinfo.properties文件配置信息，如果找不到该文件则确认该文件是否在classpath目录
         */
        Configs.init("zfbinfo.properties");

        /** 使用Configs提供的默认参数
         *  AlipayTradeService可以使用单例或者为静态成员对象，不需要反复new
         */
        AlipayTradeService tradeService = new AlipayTradeServiceImpl.ClientBuilder().build();
        AlipayF2FPrecreateResult result = tradeService.tradePrecreate(builder);
        switch (result.getTradeStatus()) {
            case SUCCESS:
                logger.info("支付宝预下单成功: )");
                AlipayTradePrecreateResponse response = result.getResponse();
                dumpResponse(response);

                //生成二维码上传到ftp服务器
                File file = new File(path);
                if(!file.exists()){
                    file.setWritable(true);
                    file.mkdirs();
                }

                // 需要修改为运行机器上的路径
                String filePath = String.format(path+"/qr-%s.png", response.getOutTradeNo());
                String qrFilePath = String.format("qr-%s.png",response.getOutTradeNo());
                ZxingUtils.getQRCodeImge(response.getQrCode(), 256, filePath);
                File targetFile = new File(path,qrFilePath);
                List<File> files = new ArrayList<File>();
                files.add(targetFile);
                try {
                    FTPUtil.uploadFile(files);
                } catch (IOException e) {
                    logger.error("上传二维码到FTP服务器错误",e);
                }
                logger.info("filePath:" + filePath);
                String qrUrl = PropertiesUtil.getProperties("ftp.server.http.prefix")+targetFile.getName();
                map.put("qrUrl",qrUrl);
                return ServerResponse.createBySuccessData(map);

            case FAILED:
                logger.error("支付宝预下单失败!!!");
                return ServerResponse.createByErrorMsg("支付宝预下单失败!!!");

            case UNKNOWN:
                logger.error("系统异常，预下单状态未知!!!");
                return ServerResponse.createByErrorMsg("系统异常，预下单状态未知!!!");

            default:
                logger.error("不支持的交易状态，交易返回异常!!!");
                return ServerResponse.createByErrorMsg("不支持的交易状态，交易返回异常!!!");
        }
    }

    // 简单打印应答
    private void dumpResponse(AlipayResponse response) {
        if (response != null) {
            logger.info(String.format("code:%s, msg:%s", response.getCode(), response.getMsg()));
            if (StringUtils.isNotEmpty(response.getSubCode())) {
                logger.info(String.format("subCode:%s, subMsg:%s", response.getSubCode(),
                        response.getSubMsg()));
            }
            logger.info("body:" + response.getBody());
        }
    }

    public ServerResponse callback(Map<String,String> map){
        Long orderNo = Long.parseLong(map.get("out_trade_no"));
        String tradeNo = map.get("trade_no");
        String tradeStatus = map.get("trade_status");
        Order order = orderMapper.selectByOrderNo(orderNo);
        if(order == null){
            return ServerResponse.createByErrorMsg("非商城订单，回调忽略");
        }
        if(order.getStatus() > Constants.TradeStatusEnum.PAID.getCode()){
            return ServerResponse.createBySuccessMsg("支付宝重复调用");
        }
        if(Constants.alipay.response_TRADE_SUCCESS.equals(tradeStatus)){
            order.setPaymentTime(DateUtil.strToDate(map.get("gmt_payment")));
            order.setStatus(Constants.TradeStatusEnum.PAID.getCode());
            orderMapper.updateByPrimaryKeySelective(order);
        }
        PayInfo payInfo = new PayInfo();
        payInfo.setUserId(order.getUserId());
        payInfo.setPayPlatform(Constants.PayPlatformEnum.ALIPAY.getCode());
        payInfo.setOrderNo(order.getOrderNo());
        payInfo.setPlatformNumber(tradeNo);
        payInfo.setPlatformStatus(tradeStatus);
        payInfoMapper.insert(payInfo);

        return ServerResponse.createBySuccess();
    }

    public ServerResponse<Boolean> queryOrderPayStatus(Long orderNo,Integer userId){
        Order order = orderMapper.selectByUserIdAndOrderNo(userId,orderNo);
        if(order == null){
            return ServerResponse.createBySuccessData(false);
        }
        if(order.getStatus() >= Constants.TradeStatusEnum.PAID.getCode()){
            return ServerResponse.createBySuccessData(true);
        }
        return ServerResponse.createBySuccessData(false);
    }
}
