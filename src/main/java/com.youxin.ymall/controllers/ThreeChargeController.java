package com.youxin.ymall.controllers;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradeAppPayModel;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradeAppPayRequest;
import com.alipay.api.response.AlipayTradeAppPayResponse;
import com.alipay.config.AlipayConfig;
import com.alipay.config.WeixinConfig;
import com.alipay.util.AlipayCore;
import com.alipay.util.WXPayNotifyUrlEntity;
import com.alipay.util.WXPrePayEntity;
import com.alipay.util.WXPrePayResultEntity;
import com.rsclouds.base.SimpleNetObject;
import com.rsclouds.util.StringTool;
import com.youxin.pay.entity.Chargeorder;
import com.youxin.pay.service.ChargeorderService;
import com.youxin.ymall.cacheservice.SettingCache;
import com.youxin.ymall.domain.ThreePayOrderInfo;
import com.youxin.ymall.entity.MallOrder;
import com.youxin.ymall.service.AppUserService;
import com.youxin.ymall.service.MallOrderService;
import com.youxin.ymall.service.UsertransactionService;
import com.youxin.ymall.utils.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
public class ThreeChargeController extends AppBaseController {
    private Logger logger = LoggerFactory.getLogger(ThreeChargeController.class);

    @Value("${alipay.burl}")
    private String alipayburl;
    @Value("${alipay.furl}")
    private String alipayfurl;
    @Value("${weixin.burl}")
    private String weixinburl;
    @Autowired
    private AppUserService appuserService;
    @Autowired
    private UsertransactionService transionService;
    @Autowired
    private ChargeorderService chargeOrderService;
    @Autowired
    private MallOrderService mallOrderService;
    @Autowired
    private SettingCache settingCacheService;


    //新支付包支付参数:
    private String CHARSET = "UTF-8";
    @Value("${alipay.alipublicKey}")
    private String alipay_alipublicKey;
    @Value("${alipay.privateKey}")
    private String alipay_privateKey;
    @Value("${alipay.appid}")
    private String alipay_appid;


    /**
     * 创建支付宝订单流水
     *
     * @param amount
     * @param session
     * @return
     */
    @RequestMapping("/rest/v1/newcreatetransforAlipay")
    @ResponseBody
    public SimpleNetObject newcreatetransforAlipay(BigDecimal amount, String odesc, String chargetype, String devicetype,
                                                   String ordercode, HttpSession session) {
        SimpleNetObject sno = new SimpleNetObject();
        String username = SessionUtil.getCurrentUserName(session);
        MallOrder mallOrder = mallOrderService.getById(ordercode);
        BigDecimal threepaymoney = mallOrder.getThreepay();
        if (chargetype.equalsIgnoreCase(ChargeorderService.CHARGE_BUY_TYPE_BUY)) {
            sno = chargeOrderService.createChargeBuyOrder(username, threepaymoney, odesc,
                    ChargeorderService.CHARGE_PAYTYPE_ALIPAY, devicetype, ordercode);
        }else  if (chargetype.equalsIgnoreCase(ChargeorderService.CHARGE_BUY_TYPE_CHARGE_BALANCE)){
            sno=chargeOrderService.createChargeBalanceOrder(username,mallOrder.getAmount(),odesc,
                    chargeOrderService.CHARGE_PAYTYPE_ALIPAY,devicetype,ordercode);
        }else{
            sno = chargeOrderService.createChargeOrder(username,threepaymoney,odesc,
                    ChargeorderService.CHARGE_PAYTYPE_ALIPAY,devicetype,mallOrder.getFactoryid());
        }
        if (sno.getResult()==1) {
            Chargeorder chargeorder = (Chargeorder) sno.getData();
//实例化客户端
            AlipayClient alipayClient = new DefaultAlipayClient("https://openapi.alipay.com/gateway.do",
                    alipay_appid, alipay_privateKey, "json", CHARSET, alipay_alipublicKey, "RSA2");
//实例化具体API对应的request类,类名称和接口名称对应,当前调用接口名称：alipay.trade.app.pay
            AlipayTradeAppPayRequest request = new AlipayTradeAppPayRequest();
//SDK已经封装掉了公共参数，这里只需要传入业务参数。以下方法为sdk的model入参方式(model和biz_content同时存在的情况下取biz_content)。
            AlipayTradeAppPayModel model = new AlipayTradeAppPayModel();
            model.setBody(chargetype);
            model.setSubject(odesc);
            model.setOutTradeNo(chargeorder.getTradeno());
            model.setTimeoutExpress("30m");
            model.setTotalAmount(amount.toString());
            model.setProductCode("QUICK_MSECURITY_PAY");
            request.setBizModel(model);
            request.setNotifyUrl(alipayburl);
            try {
                //这里和普通的接口调用不同，使用的是sdkExecute
                AlipayTradeAppPayResponse response = alipayClient.sdkExecute(request);
                System.out.println(response.getBody());//就是orderString 可以直接给客户端请求，无需再做处理。
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("orderinfo", response.getBody());
                map.put("tradeno", ordercode);
                sno.setData(map);
            } catch (AlipayApiException e) {
                e.printStackTrace();
            }
        }
        return sno;
    }

    //新支付宝回调接口
    @RequestMapping("/alipay/burl")
    @ResponseBody
    public String paynotify(String sign, HttpServletRequest request) {
        //获取支付宝POST过来反馈信息
        Map<String, String> params = new HashMap<String, String>();
        Map requestParams = request.getParameterMap();
        for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext(); ) {
            String name = (String) iter.next();
            String[] values = (String[]) requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i]
                        : valueStr + values[i] + ",";
            }
            //乱码解决，这段代码在出现乱码时使用。
            //valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
            params.put(name, valueStr);
        }
        //切记alipaypublickey是支付宝的公钥，请去open.alipay.com对应应用下查看。
        //boolean AlipaySignature.rsaCheckV1(Map<String, String> params, String publicKey, String charset, String sign_type)


        try {

//			String content=AlipaySignature.getSignCheckContentV1(params);
//			logger.error("待签名内容："+content);

            //boolean flag = AlipaySignature.rsaCheckV1(params, alipay_alipublicKey, CHARSET,"RSA2");
            System.out.println("开始校验支付宝回调参数");
            //boolean flag = AlipaySignature.rsaCheckV1(params, alipay_alipublicKey, CHARSET,"RSA2");
            boolean flag = AlipaySignature.rsaCheckV1(params, alipay_alipublicKey, "UTF-8", AlipayConfig.sign_type);
            if (flag) {

                if ("TRADE_SUCCESS".equalsIgnoreCase(params.get("trade_status"))) {
                    String payaccountinfo = params.get("buyer_logon_id");
                    // String result=rechargeOrder.finishPay(params.get("out_trade_no"), params.toString(),payaccountinfo, null);
                    int result = chargeOrderService.updateChargeOrderStatus(params.get("out_trade_no"), params.get("trade_no"),
                            ChargeorderService.CHARGE_ORDERSTATUS_SUCCESS, "ALIPAY");
                    if (result == 1) {

                        return "success";
                    } else {
                        logger.error("处理充值失败，错误原因:" + result + "，参数" + params.toString());
                        return "fail";
                    }
                } else {
                    return "success";
                }
            } else {
                logger.error("支付宝回调参数校验错误，校验参数：" + params.toString());
                return "fail";
            }
        } catch (AlipayApiException e) {
            logger.error("支付宝回调参数校验异常，校验参数：" + params.toString(), e);
            e.printStackTrace();
            System.out.println("支付宝回调参数校验错误，校验参数：" + params.toString());
            return "fail";
        }
    }
    /**
     * 创建微信订单流水
     *
     * @param amount
     * @param session
     * @return
     */
    @RequestMapping("/rest/v1/createtransforWeixin")
    @ResponseBody
    public SimpleNetObject createtransforWeixin(BigDecimal amount, String odesc, String chargetype, String devicetype,
                                                String ordercode, HttpSession session, HttpServletRequest request) {

        String username = SessionUtil.getCurrentUserName(session);
        if (StringTool.isNullOrEmpty(chargetype)) {
            chargetype = ChargeorderService.CHARGE_BUY_TYPE_CHARGE;
        }
        SimpleNetObject sno = new SimpleNetObject();
        MallOrder mallOrder = mallOrderService.getById(ordercode);
        if (mallOrder == null) {
            sno.setResult(2);
            sno.setMessage("亲，您的网络不稳定，请确保您的网络畅通或连上WiFi，再重新购买试试！！");
            return sno;
        }
        BigDecimal threepaymoney = mallOrder.getThreepay();

        if (chargetype.equalsIgnoreCase(ChargeorderService.CHARGE_BUY_TYPE_BUY)) {
            sno = chargeOrderService.createChargeBuyOrder(username, threepaymoney, odesc,
                    ChargeorderService.CHARGE_PAYTYPE_WEIXIN, devicetype, ordercode);
        } else  if (chargetype.equalsIgnoreCase(ChargeorderService.CHARGE_BUY_TYPE_CHARGE_BALANCE)){
            sno=chargeOrderService.createChargeBalanceOrder(username,mallOrder.getAmount(),odesc,
                    chargeOrderService.CHARGE_PAYTYPE_ALIPAY,devicetype,ordercode);
        }else {
            sno = chargeOrderService.createChargeOrder(username, threepaymoney, odesc,
                    ChargeorderService.CHARGE_PAYTYPE_WEIXIN, devicetype, mallOrder.getFactoryid());
        }
        if (sno.getResult() == 1) {
            Chargeorder chargeOrder = (Chargeorder) sno.getData();
            ThreePayOrderInfo orderInfo = new ThreePayOrderInfo();
            orderInfo.setBurl(weixinburl);
            orderInfo.setTradeno(chargeOrder.getTradeno());

            // 生成微信支付的预支付信息
            String noncestr = StringTools.genRandomStr(20);
            WXPrePayEntity wxPrePayEntity = new WXPrePayEntity();
            wxPrePayEntity.setAppid(WeixinConfig.AppID);
            wxPrePayEntity.setMch_id(WeixinConfig.mch_id);
            wxPrePayEntity.setNonce_str(noncestr);
            wxPrePayEntity.setBody(chargeOrder.getDescription());
            wxPrePayEntity.setDetail(chargeOrder.getDescription());
            wxPrePayEntity.setAttach("");// 附加数据
            wxPrePayEntity.setOut_trade_no(chargeOrder.getTradeno());// 商户系统内部的订单号,32个字符内、可包含字母,
            wxPrePayEntity.setFee_type("CNY");// rmb
            wxPrePayEntity.setTotal_fee(threepaymoney.setScale(2, BigDecimal.ROUND_HALF_UP)
                    .multiply(new BigDecimal(100)).setScale(0).toPlainString());// 总金额.分，要注意小数点
            wxPrePayEntity.setSpbill_create_ip(StringTools.getRemoteHost(request));// 用户的ip
            wxPrePayEntity.setTime_start(DateTools.timeToDateFormat(new Date().getTime(), "yyyyMMddHHmmss"));// 交易起始时间
            Calendar c = Calendar.getInstance();
            c.setTime(new Date());
            c.add(Calendar.HOUR_OF_DAY, 2);
            wxPrePayEntity.setTime_expire(DateTools.timeToDateFormat(c.getTime().getTime(), "yyyyMMddHHmmss"));// 交易结束时间2小时有效期
            wxPrePayEntity.setTrade_type("APP");
            wxPrePayEntity.setNotify_url(weixinburl);
            String sign = createPaySign(wxPrePayEntity);
            wxPrePayEntity.setSign(sign);
            // 将bean转成xml发送到微信
            String payXml = XmlUtil.toXml(wxPrePayEntity);

            payXml = payXml.replaceAll("com.alipay.util.WXPrePayEntity", "xml");
            payXml = payXml.replaceAll("__", "_");

            // 发送到微信
            String postResult = HttpsPostUtil.post("https://api.mch.weixin.qq.com/pay/unifiedorder", payXml, "utf-8");

            if (StringTools.nil(postResult)) {
                logger.error(payXml);
                sno.setResult(94);
                sno.setMessage("请求预支付服务器的返回结果为空！");
                return sno;
            }
            WXPrePayResultEntity wxPrePayResultEntity = XmlUtil.toBean(postResult, WXPrePayResultEntity.class);
            if (null == wxPrePayResultEntity) {

                sno.setResult(94);
                sno.setMessage("解析请求预支付服务器的返回结果出现异常！");
                return sno;
            }
            // 检查预支付结果
            if ("FAIL".equals(wxPrePayResultEntity.getReturn_code())) {
                sno.setResult(93);
                sno.setMessage("预支付返回结果错误，原因：" + wxPrePayResultEntity.getReturn_msg());
                return sno;
            }
            if ("FAIL".equals(wxPrePayResultEntity.getResult_code())) {
                sno.setResult(92);
                sno.setMessage("预支付返回结果错误，原因：" + "(" + wxPrePayResultEntity.getErr_code() + ")"
                        + wxPrePayResultEntity.getErr_code_des());
                return sno;
            }

            String timestamp = (new Date().getTime() + "").substring(0, 10);
            String stringSignTemp = "appid=" + WeixinConfig.AppID + "&noncestr=" + noncestr
                    + "&package=Sign=WXPay&partnerid=" + WeixinConfig.mch_id + "&prepayid="
                    + wxPrePayResultEntity.getPrepay_id() + "&timestamp=" + timestamp + "&key=" + WeixinConfig.apiKey;
            logger.debug(stringSignTemp);
            String appsign = PubFun.MD5(stringSignTemp).toUpperCase();

            logger.debug(appsign);
            // 预支付成功，给app返回预支付订单号
            Map<String, String> map = new HashMap<String, String>();
            map.put("prepayid", wxPrePayResultEntity.getPrepay_id());
            map.put("out_trade_no", wxPrePayEntity.getOut_trade_no());// 用于支付完毕之后查询支付结果
            map.put("timestamp", timestamp);
            map.put("appid", WeixinConfig.AppID);
            map.put("partnerid", WeixinConfig.mch_id);
            map.put("package", "Sign=WXPay");
            map.put("noncestr", noncestr);
            map.put("sign", appsign);
            // sno.setData(wxPrePayEntity.getOut_trade_no());
            sno.setData(map);
            sno.setResult(1);
            sno.setMessage("获取微信预支付交易号成功");
        }
        return sno;
    }

    /**
     * 生成微信支付的签名
     *
     * @param wxPrePayEntity
     * @return
     */
    public static String createPaySign(WXPrePayEntity wxPrePayEntity) {
        try {
            // 将bean转成map
            Map<String, String> beanMap = AlipayCore.convertBean(wxPrePayEntity);
            // 去掉空值
            Map<String, String> resultMap = AlipayCore.paraFilter(beanMap);
            String string1 = AlipayCore.createLinkString(resultMap);
            String stringSignTemp = string1 + "&key=" + WeixinConfig.apiKey;
            String sign = PubFun.MD5(stringSignTemp).toUpperCase();
            return sign;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 微信后台回调
     *
     * @param session
     * @return
     */
    @RequestMapping("/weixin/burl")
    @ResponseBody
    public void weixinburl(HttpServletRequest request, HttpSession session, HttpServletResponse response) {
        try {
            logger.debug("微信后台回调:");
            Date date = new Date();
            SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd-hh-ss");
            String now = fmt.format(date);
            logger.debug(now + "微信回调开始");
            // 拿到request的xml，并转换成bean
            String requestXml = parseRequestXml(request);
            logger.debug("异步通知的返回xml ： " + requestXml);
            if (StringTools.nil(requestXml)) {
                payResultBackXml("FAIL", "参数格式校验错误", response);
                return;
            }
            WXPayNotifyUrlEntity wxPayNotifyUrlEntity = XmlUtil.toBean(requestXml, WXPayNotifyUrlEntity.class);
            if (null == wxPayNotifyUrlEntity) {
                payResultBackXml("FAIL", "参数格式校验错误", response);
                return;
            }
            // 判断充值结果
            if ("FAIL".equals(wxPayNotifyUrlEntity.getReturn_code())) {
                logger.debug("支付返回结果错误，原因：" + wxPayNotifyUrlEntity.getReturn_msg());
                payResultBackXml("FAIL", "参数格式校验错误", response);
                return;
            }
            if ("FAIL".equals(wxPayNotifyUrlEntity.getResult_code())) {
                logger.debug("支付返回结果错误，原因：(" + wxPayNotifyUrlEntity.getErr_code() + ")"
                        + wxPayNotifyUrlEntity.getErr_code_des());
                payResultBackXml("FAIL", "参数格式校验错误", response);
                return;
            }
            // 验证sign是否有效
            // 将请求的bean转成map
            // 将bean转成map
            Map<String, String> beanMap = AlipayCore.convertBean(wxPayNotifyUrlEntity);
            // 去掉空值
            Map<String, String> resultMap = AlipayCore.paraFilter(beanMap);
            String string1 = AlipayCore.createLinkString(resultMap);
            String wxTempSign = string1 + "&key=" + WeixinConfig.apiKey;
            wxTempSign = PubFun.MD5(wxTempSign).toUpperCase();

            if (!wxPayNotifyUrlEntity.getSign().equals(wxTempSign)) {
                logger.debug("签名不对！");
                logger.debug("微信发过来的： " + wxPayNotifyUrlEntity.getSign());
                logger.debug("后台根据参数生成的: " + wxTempSign);
                payResultBackXml("FAIL", "签名失败", response);
                return;
            }
            logger.debug(now + "微信回调签名验证成功");
            // 微信服务器提示支付成功，那就更新数据库各订单
            logger.debug(wxPayNotifyUrlEntity.getOut_trade_no() + "201710281437开始更新订单状态");
            int result = this.chargeOrderService.updateChargeOrderStatus(wxPayNotifyUrlEntity.getOut_trade_no(),
                    wxPayNotifyUrlEntity.getTransaction_id(), ChargeorderService.CHARGE_ORDERSTATUS_SUCCESS, "WEIXIN");
            if (result == 1) {
                payResultBackXml("SUCCESS", "OK", response);
                return;
            } else {
                payResultBackXml("FAIL", "我方平台处理错误", response);
                return;
            }
        } catch (Exception e) {
            logger.debug("出现错误了");
            e.printStackTrace();
        }
        payResultBackXml("FAIL", "参数格式校验错误", response);
        return;
    }

    /**
     * 根据request拿到请求内容的xml
     *
     * @param request
     * @return
     */
    public static String parseRequestXml(HttpServletRequest request) {
        try {
            String xml = "";
            InputStream in = request.getInputStream();
            int len = 0;
            byte[] buffer = new byte[1024];
            while ((len = in.read(buffer)) > 0) {
                xml += new String(buffer, 0, len);
            }
            System.out.println(xml);
            return xml;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 微信通知服务器支付结果之后，就要返回一个xml的信息，
     *
     * @param return_code SUCCESS
     * @param return_msg  OK
     * @return
     */
    public static void payResultBackXml(String return_code, String return_msg, HttpServletResponse response) {
        try {
            String xml = "<xml><return_code><![CDATA[" + return_code + "]]></return_code><return_msg><![CDATA["
                    + return_msg + "]]></return_msg></xml>";
            // String xml =
            // "<xml><return_code>"+return_code+"</return_code><return_msg>"+return_msg+"</return_msg></xml>";
            System.out.println(xml);
            OutputStream stream = response.getOutputStream();// 获取一个向Response对象写入数据的流,当tomcat服务器进行响应的时候，会将Response中的数据写给浏览器
            stream.write(xml.getBytes("UTF-8"));
            stream.flush();
            stream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RequestMapping("/rest/testupdateChargeOrderStatus")
    @ResponseBody
    public SimpleNetObject testupdateChargeOrderStatus(String ordercode) {

        SimpleNetObject sno = new SimpleNetObject();
        ordercode = "O170927072507206945";
        MallOrder mallorder = mallOrderService.getByOrderCode(ordercode);
        logger.debug("开始支付历史订单,订单为" + mallorder.toString());
        int result = appuserService.payOrder(mallorder);
        logger.debug("支付结果为" + result);
        logger.debug("get result is " + result);

        return sno;

    }
    @RequestMapping("/rest/testnewgetsignkey")
    @ResponseBody
    public SimpleNetObject testnewgetsignkey() {

        SimpleNetObject sno = new SimpleNetObject();
        String noncestr = StringTools.genRandomStr(20);
        // 将bean转成xml发送到微信
        WXPrePayEntity entity = new WXPrePayEntity();
        entity.setMch_id(WeixinConfig.mch_id);
        entity.setNonce_str(noncestr);
        String sign = createPaySign(entity);
        entity.setSign(sign);
        String payXml = XmlUtil.toXml(entity);
        payXml = payXml.replaceAll("com.alipay.util.WXPrePayEntity", "xml");
        payXml = payXml.replaceAll("__", "_");
        HashMap<String,String> map = new HashMap<String, String>();
        map.put("parameter",payXml);
        String postResult = HttpsPostUtil.post("https://apitest.mch.weixin.qq.com/sandboxnew/pay/getsignkey", payXml, "utf-8");
        if (!StringTool.isNullOrEmpty(postResult)){
            WXPrePayResultEntity wxPrePayResultEntity = XmlUtil.toBean(postResult, WXPrePayResultEntity.class);
            sno.setData(wxPrePayResultEntity);
            sno.setExtraData(map);
        }
        return sno;

    }
}
