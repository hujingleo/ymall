package com.youxin.ymall.controllers;

import com.alipay.util.AlipayNotify;
import com.alipay.util.WXPrePayEntity;
import com.alipay.util.WXPrePayResultEntity;
import com.rsclouds.base.SimpleNetObject;
import com.youxin.charge.domain.IChargeOrderBO;
import com.youxin.charge.domain.NotifyCharge;
import com.youxin.charge.domain.ThreeChargeForBuy;
import com.youxin.ecommerce.domain.IOrderBO;
import com.youxin.ecommerce.service.EcommerceThreeChargeService;
import com.youxin.ecommerce.service.OrderService;
import com.youxin.pay.utils.AlipayCore;
import com.youxin.pay.utils.WXPayNotifyUrlEntity;
import com.youxin.pay.utils.WeixinConfig;
import com.youxin.ymall.domain.ThreePayOrderInfo;
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
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.*;


/**
 * 统一三方支付回调
 * @author yukun
 *
 */
@Controller
public class PayController extends AppBaseController {
	private Logger logger=LoggerFactory.getLogger(PayController.class);
	public static String PAYWAY_ALIPAY="ALIPAY";
	public static String PAYWAY_WEIXIN="WEIXIN";
	@Value("${pay.alipay.burl}")
	private String alipayburl;
	@Value("${pay.alipay.furl}")
	private String alipayfurl;
	@Value("${pay.weixin.burl}")
	private String weixinburl;
	@Autowired
	private OrderService orderService;
	@Autowired
	private EcommerceThreeChargeService threechargeService;
	@RequestMapping("/rest/v1/pay/createtransforAlipay")
	@ResponseBody
	public SimpleNetObject createtransforAlipay(
			String devicetype,
			String ordercode,
			HttpSession session){
		
		String username= SessionUtil.getCurrentUserName(session);
		SimpleNetObject sno=new SimpleNetObject();
		IOrderBO order=orderService.getOrderByOrderCode(ordercode);
		IChargeOrderBO chargeorder=new ThreeChargeForBuy(username,
				order.getTotalpay()-order.getBalancepay()-order.getPoints(),
				order.getOrdername(),PAYWAY_ALIPAY,ordercode);
		chargeorder.setOname(order.getOrdername());
		chargeorder.setOdesc(order.getOrdername());
		chargeorder=threechargeService.createChargeOrder(chargeorder);
		ThreePayOrderInfo orderInfo=new ThreePayOrderInfo();
		orderInfo.setFurl(alipayfurl);
		orderInfo.setBurl(alipayburl);	
		orderInfo.setTradeno(chargeorder.getTradeno());						
		java.text.DecimalFormat df = new java.text.DecimalFormat("0.00");
		orderInfo.setSign(threechargeService.getSignData(chargeorder.getTradeno()
				, chargeorder.getOdesc()
				, chargeorder.getOdesc()
				, df.format(chargeorder.getChargemoney()/100.0d),alipayfurl,alipayburl));
		sno.setResult(1);
		sno.setData(orderInfo);
		return sno;
	}
	
	/**
	 * 创建支付宝订单流水
	 * @param amount
	 * @param session
	 * @return
	 */
	@RequestMapping("/pay/alipay/furl")
	public SimpleNetObject furl(BigDecimal amount,
			String odesc,
			HttpSession session){
		
		String username= SessionUtil.getCurrentUserName(session);
		SimpleNetObject sno=new SimpleNetObject();
		
		return sno;
	}
	

	
	/**
	 * 创建微信订单流水
	 * @param amount
	 * @param session
	 * @return
	 */
	@RequestMapping("/rest/v1/pay/createtransforWeixin")
	@ResponseBody
	public SimpleNetObject createtransforWeixin(
			String devicetype,
			String ordercode,
			HttpSession session,HttpServletRequest request){
		
		String username= SessionUtil.getCurrentUserName(session);
		SimpleNetObject sno=new SimpleNetObject();
		IOrderBO order=orderService.getOrderByOrderCode(ordercode);
		IChargeOrderBO chargeorder=new ThreeChargeForBuy(username,
				order.getTotalpay()-order.getBalancepay()-order.getPoints(),
				order.getOrdername(),PAYWAY_WEIXIN,ordercode);
		chargeorder.setOname(order.getOrdername());
		chargeorder.setOdesc(order.getOrdername());
		chargeorder=threechargeService.createChargeOrder(chargeorder);
		
		
		//生成微信支付的预支付信息
		String noncestr = StringTools.genRandomStr(20);
		WXPrePayEntity wxPrePayEntity = new WXPrePayEntity();
		wxPrePayEntity.setAppid(WeixinConfig.AppID);
		wxPrePayEntity.setMch_id(WeixinConfig.mch_id);
		wxPrePayEntity.setNonce_str(noncestr);
		wxPrePayEntity.setBody(chargeorder.getOdesc());
		wxPrePayEntity.setDetail(chargeorder.getOdesc());
		wxPrePayEntity.setAttach("");//附加数据
		wxPrePayEntity.setOut_trade_no(chargeorder.getTradeno());//商户系统内部的订单号,32个字符内、可包含字母, 
		wxPrePayEntity.setFee_type("CNY");//rmb
		wxPrePayEntity.setTotal_fee(chargeorder.getChargemoney()+"");//总金额.分，要注意小数点
		wxPrePayEntity.setSpbill_create_ip(StringTools.getRemoteHost(request));//用户的ip
		wxPrePayEntity.setTime_start(DateTools.timeToDateFormat(new Date().getTime(), "yyyyMMddHHmmss"));//交易起始时间
		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		c.add(Calendar.HOUR_OF_DAY, 2);
		wxPrePayEntity.setTime_expire(DateTools.timeToDateFormat(c.getTime().getTime(), "yyyyMMddHHmmss"));//交易结束时间2小时有效期
		wxPrePayEntity.setTrade_type("APP");
		wxPrePayEntity.setNotify_url(weixinburl);
		String sign = createPaySign(wxPrePayEntity);
		wxPrePayEntity.setSign(sign);
		//将bean转成xml发送到微信
		String payXml = XmlUtil.toXml(wxPrePayEntity);
		logger.debug(payXml);
		payXml = payXml.replaceAll("com.alipay.util.WXPrePayEntity", "xml");
		payXml = payXml.replaceAll("__", "_");
		logger.debug(payXml);
		
		//发送到微信
		String postResult = HttpsPostUtil.post("https://api.mch.weixin.qq.com/pay/unifiedorder", payXml, "utf-8");
		logger.debug("微信请求结果 ： " + postResult);
		if(StringTools.nil(postResult))
		{
			sno.setResult(94);
			sno.setMessage("请求预支付服务器的返回结果为空！");
			return sno;
		}
		WXPrePayResultEntity wxPrePayResultEntity = XmlUtil.toBean(postResult, WXPrePayResultEntity.class);
		if(null == wxPrePayResultEntity)
		{
			sno.setResult(94);
			sno.setMessage("解析请求预支付服务器的返回结果出现异常！");
			return sno;
		}
		//检查预支付结果
		if("FAIL".equals(wxPrePayResultEntity.getReturn_code()))
		{
			sno.setResult(93);
			sno.setMessage("预支付返回结果错误，原因："+wxPrePayResultEntity.getReturn_msg());
			return sno;
		}
		if("FAIL".equals(wxPrePayResultEntity.getResult_code()))
		{
			sno.setResult(92);
			sno.setMessage("预支付返回结果错误，原因："+"("+wxPrePayResultEntity.getErr_code()+")"+wxPrePayResultEntity.getErr_code_des());
			return sno;
		}
		
		//生成手机发起支付的sign
	/*	WXPrePayEntity appWxPrePayEntity = new WXPrePayEntity();
		wxPrePayEntity.setAppid(WeixinConfig.AppID);
		wxPrePayEntity.setMch_id(WeixinConfig.mch_id);
		wxPrePayEntity.setPrepayid(wxPrePayResultEntity.getPrepay_id());
		wxPrePayEntity.setSign(sign)*/
		
		String timestamp = (new Date().getTime()+"").substring(0,10);
		String stringSignTemp = "appid="+ WeixinConfig.AppID+"&noncestr="+noncestr+"&package=Sign=WXPay&partnerid="+ WeixinConfig.mch_id+"&prepayid=" +wxPrePayResultEntity.getPrepay_id()+
				"&timestamp="+timestamp+"&key="+ WeixinConfig.apiKey;
		logger.debug(stringSignTemp);
		String appsign = PubFun.MD5(stringSignTemp).toUpperCase();
		
		logger.debug(appsign);
		//预支付成功，给app返回预支付订单号
		Map<String, String> map = new HashMap<String, String>();
		map.put("prepayid", wxPrePayResultEntity.getPrepay_id());
		map.put("out_trade_no", wxPrePayEntity.getOut_trade_no());//用于支付完毕之后查询支付结果
		map.put("timestamp", timestamp);
		map.put("appid", WeixinConfig.AppID);
		map.put("partnerid", WeixinConfig.mch_id);
		map.put("package", "Sign=WXPay");
		map.put("noncestr", noncestr);
		map.put("sign", appsign);
		//sno.setData(wxPrePayEntity.getOut_trade_no());
		sno.setData(map);
		sno.setResult(1);
		sno.setMessage("获取微信预支付交易号成功");
		sno.setResult(1);
		
		return sno;
	}
	
	/**
	 * 生成微信支付的签名
	 * @param wxPrePayEntity
	 * @return
	 */
	public static String  createPaySign(WXPrePayEntity wxPrePayEntity)
	{
		try 
		{
			//将bean转成map
			Map<String, String> beanMap = AlipayCore.convertBean(wxPrePayEntity);
			//去掉空值
			Map<String, String> resultMap = AlipayCore.paraFilter(beanMap);
			String string1 = AlipayCore.createLinkString(resultMap);
			String stringSignTemp = string1+"&key="+ WeixinConfig.apiKey;
			String sign = PubFun.MD5(stringSignTemp).toUpperCase();
			return sign;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
	/**
	 * Description：查询订单状态
	 * @param ordercode 订单号
	 * @return       
	 *
	 */
	@RequestMapping("/rest/v1/pay/queryOrderStatus")
	@ResponseBody
	public SimpleNetObject queryOrderStatus(String ordercode,HttpSession session){
		String username = SessionUtil.getCurrentUserName(session);
		SimpleNetObject sno=new SimpleNetObject();
		IOrderBO orderbo=orderService.getOrderByOrderCode(ordercode);
		if(orderbo!=null){
			sno.setData(orderbo.getStatus());
			return sno;
		}
		else{
			sno.setResult(2);
			sno.setMessage("未找到该订单信息");
			return sno;
		}
		
	}
	

	@RequestMapping("/pay/alipay/burl")
	@ResponseBody
	public String burl(HttpServletRequest request, HttpSession session)
	{
		logger.debug("支付宝后台回调:");
		Map map = request.getParameterMap();
		Map<String,String> params = new HashMap<String,String>();
		Map requestParams = request.getParameterMap();
		for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext();) {
			String name = (String) iter.next();
			String[] values = (String[]) requestParams.get(name);
			String valueStr = "";
			for (int i = 0; i < values.length; i++) {
				valueStr = (i == values.length - 1) ? valueStr + values[i]
						: valueStr + values[i] + ",";
			}
			//乱码解决，这段代码在出现乱码时使用。如果mysign和sign不相等也可以使用这段代码转化
			//valueStr = new String(valueStr.getBytes("ISO-8859-1"), "gbk");
			params.put(name, valueStr);
			logger.debug("支付宝回调参数-"+name+":"+valueStr);
		}
		//获取支付宝的通知返回参数，可参考技术文档中页面跳转同步通知参数列表(以下仅供参考)//
		//商户订单号
		String out_trade_no;
		try 
		{
			out_trade_no = new String(request.getParameter("out_trade_no").getBytes("ISO-8859-1"),"UTF-8");
			String trade_no = new String(request.getParameter("trade_no").getBytes("ISO-8859-1"),"UTF-8");
			String amount=new String(request.getParameter("total_fee").getBytes("ISO-8859-1"),"UTF-8");
			BigDecimal receiptMoney = new BigDecimal(amount);
			//交易状态
			String trade_status = new String(request.getParameter("trade_status").getBytes("ISO-8859-1"),"UTF-8");
			//获取支付宝的通知返回参数，可参考技术文档中页面跳转同步通知参数列表(以上仅供参考)//
			logger.debug("订单号为{},支付宝订单号为{},订单状态{}",new Object[]{out_trade_no,trade_no,trade_status});
			if(AlipayNotify.verify(params)){//验证成功
				logger.debug("支付宝回调验证通过");
				//////////////////////////////////////////////////////////////////////////////////////////
				//请在这里加上商户的业务逻辑程序代码

				//——请根据您的业务逻辑来编写程序（以下代码仅作参考）——
				
				if (trade_status.equals("TRADE_FINISHED")
						|| trade_status.equals("TRADE_SUCCESS")) {

					try {
						/**
						 * 我们系统金额是以分为单位，所以需要将产过来的金额*100转化为分单位
						 */
						NotifyCharge notifyCharge = new NotifyCharge(out_trade_no,trade_no
								, receiptMoney.multiply(
										new BigDecimal(100)).intValue());
						threechargeService.notifyChargeSuccess(notifyCharge);

					} catch (Exception e) {
						return "fail";
					}

					return "fail";
				} else
					return "fail";

				//////////////////////////////////////////////////////////////////////////////////////////
			}else{//验证失败
				logger.debug("支付宝回调验证失败");
				return "fail";
			}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "fail";
		}
		catch (NullPointerException e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage());
			return "fail";
		}
		//支付宝交易号
	}
	
	
	/**
	 * 微信后台回调
	 * @param session
	 * @return
	 */
	@RequestMapping("/pay/weixin/burl")
	@ResponseBody
	public void weixinburl(HttpServletRequest request,HttpSession session,HttpServletResponse response)
	{
		try 
		{
			logger.debug("微信后台回调:");
			
			//拿到request的xml，并转换成bean
			String requestXml = parseRequestXml(request);
			logger.debug("异步通知的返回xml ： "+ requestXml);
			if(StringTools.nil(requestXml))
			{
				logger.debug("微信返回xml为空");
				 payResultBackXml("FAIL","参数格式校验错误",response);
				 return;
			}
			WXPayNotifyUrlEntity wxPayNotifyUrlEntity = XmlUtil.toBean(requestXml, WXPayNotifyUrlEntity.class);
			if(null == wxPayNotifyUrlEntity)
			{
				 payResultBackXml("FAIL","参数格式校验错误",response);
				 return;
			}
			//判断充值结果
			if("FAIL".equals(wxPayNotifyUrlEntity.getReturn_code()))
			{
				logger.error("支付返回结果错误，原因："+wxPayNotifyUrlEntity.getReturn_msg());
				payResultBackXml("FAIL","参数格式校验错误",response);
				return;
			}
			if("FAIL".equals(wxPayNotifyUrlEntity.getResult_code()))
			{
				logger.debug("支付返回结果错误，原因：("+wxPayNotifyUrlEntity.getErr_code()+")"+wxPayNotifyUrlEntity.getErr_code_des());
				payResultBackXml("FAIL","参数格式校验错误",response);
				return;
			}
			//验证sign是否有效
			//将请求的bean转成map
			//将bean转成map
			Map<String, String> beanMap = AlipayCore.convertBean(wxPayNotifyUrlEntity);
			//去掉空值
			Map<String, String> resultMap = AlipayCore.paraFilter(beanMap);
			String string1 = AlipayCore.createLinkString(resultMap);
			String wxTempSign = string1+"&key="+ WeixinConfig.apiKey;
			wxTempSign = PubFun.MD5(wxTempSign).toUpperCase();
			
			if(!wxPayNotifyUrlEntity.getSign().equals(wxTempSign))
			{
				logger.error("签名不对！");
				logger.error("微信发过来的： "+wxPayNotifyUrlEntity.getSign());
				logger.error("后台根据参数生成的: "+wxTempSign);
				payResultBackXml("FAIL","签名失败",response);
				return;
			}
			NotifyCharge notifyCharge = new NotifyCharge(wxPayNotifyUrlEntity.getOut_trade_no()
					, wxPayNotifyUrlEntity.getTransaction_id(),Integer.parseInt(wxPayNotifyUrlEntity.getTotal_fee()));
			this.threechargeService.notifyChargeSuccess(notifyCharge);
			payResultBackXml("SUCCESS","OK",response);
			return;
		} catch (Exception e) {
			logger.debug("出现错误了");
			e.printStackTrace();
		}
		payResultBackXml("FAIL","参数格式校验错误",response);
		return;
	}
	
	
	
	
	/**
	 * 根据request拿到请求内容的xml
	 * @param request
	 * @return
	 */
	public static String parseRequestXml(HttpServletRequest request)
	{
		try 
		{
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
	 * @param return_code  SUCCESS
	 * @param return_msg   OK
	 * @return
	 */
	public static void payResultBackXml(String return_code, String return_msg, HttpServletResponse response)
	{
		try
		{
			String xml = "<xml><return_code><![CDATA["+return_code+"]]></return_code><return_msg><![CDATA["+return_msg+"]]></return_msg></xml>";
			//String xml = "<xml><return_code>"+return_code+"</return_code><return_msg>"+return_msg+"</return_msg></xml>";
			System.out.println(xml);
			OutputStream stream = response.getOutputStream();//获取一个向Response对象写入数据的流,当tomcat服务器进行响应的时候，会将Response中的数据写给浏览器
			stream.write(xml.getBytes("UTF-8"));
			stream.flush();
			stream.close();
		} catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
	
//	public static void main(String[] args) {
//		  // <xml><return_code><![CDATA[SUCCESS]]></return_code><return_msg><![CDATA[OK]]></return_msg><appid><![CDATA[wxe65a98c5b51cede6]]></appid><mch_id><![CDATA[1284244701]]></mch_id><nonce_str><![CDATA[NBnf1K0bAAEcFUT0]]></nonce_str><sign><![CDATA[D5B289ACBF49C53EDD93EEB1C747CEC7]]></sign><result_code><![CDATA[SUCCESS]]></result_code><prepay_id><![CDATA[wx20170816084941413c86ac360425535217]]></prepay_id><trade_type><![CDATA[APP]]></trade_type></xml>
//		   // String xml="<xml><return_code><![CDATA[SUCCESS]]></return_code><return_msg><![CDATA[OK]]></return_msg><appid><![CDATA[wxe65a98c5b51cede6]]></appid><mch_id><![CDATA[1284244701]]></mch_id><nonce_str><![CDATA[NBnf1K0bAAEcFUT0]]></nonce_str><sign><![CDATA[D5B289ACBF49C53EDD93EEB1C747CEC7]]></sign><result_code><![CDATA[SUCCESS]]></result_code><prepay_id><![CDATA[wx20170816084941413c86ac360425535217]]></prepay_id><trade_type><![CDATA[APP]]></trade_type></xml>";
//		// String xml="<xml><appid><![CDATA[wxe65a98c5b51cede6]]></appid><bank_type><![CDATA[PSBC_DEBIT]]></bank_type><cash_fee><![CDATA[474]]></cash_fee><coupon_count><![CDATA[1]]></coupon_count><coupon_fee>15</coupon_fee><coupon_fee_0><![CDATA[15]]></coupon_fee_0><coupon_id_0><![CDATA[2000000001220596210]]></coupon_id_0><fee_type><![CDATA[CNY]]></fee_type><is_subscribe><![CDATA[N]]></is_subscribe><mch_id><![CDATA[1284244701]]></mch_id><nonce_str><![CDATA[cefusmptfvb3lre14zk9]]></nonce_str><openid><![CDATA[oJAhRwqVDctklJn1ZaJtlZLgVwx4]]></openid><out_trade_no><![CDATA[170809082836783273]]></out_trade_no><result_code><![CDATA[SUCCESS]]></result_code><return_code><![CDATA[SUCCESS]]></return_code><sign><![CDATA[3A914D5F2B16E9599614EF37EA502328]]></sign><time_end><![CDATA[20170809082905]]></time_end><total_fee>489</total_fee><trade_type><![CDATA[APP]]></trade_type><transaction_id><![CDATA[4007242001201708095259758339]]></transaction_id></xml>";
//		//购买刮刮卡xml
//		 String xml="<xml><appid><![CDATA[wxe65a98c5b51cede6]]></appid><bank_type><![CDATA[CFT]]></bank_type><cash_fee><![CDATA[170]]></cash_fee><coupon_count><![CDATA[1]]></coupon_count><coupon_fee>30</coupon_fee><coupon_fee_0><![CDATA[30]]></coupon_fee_0><coupon_id_0><![CDATA[2000000001378950097]]></coupon_id_0><fee_type><![CDATA[CNY]]></fee_type><is_subscribe><![CDATA[N]]></is_subscribe><mch_id><![CDATA[1284244701]]></mch_id><nonce_str><![CDATA[ek6lcswr93hesctsb315]]></nonce_str><openid><![CDATA[oJAhRwoiYzh8048Yw3_TKfmaDt4o]]></openid><out_trade_no><![CDATA[C170816084940770140]]></out_trade_no><result_code><![CDATA[SUCCESS]]></result_code><return_code><![CDATA[SUCCESS]]></return_code><sign><![CDATA[4DCA8C541AF95627C4C2177E6CC54FB0]]></sign><time_end><![CDATA[20170816084959]]></time_end><total_fee>200</total_fee><trade_type><![CDATA[APP]]></trade_type><transaction_id><![CDATA[4009842001201708166479114931]]></transaction_id></xml>";
//		// <xml><appid><![CDATA[wxe65a98c5b51cede6]]></appid><bank_type><![CDATA[CFT]]></bank_type><cash_fee><![CDATA[170]]></cash_fee><coupon_count><![CDATA[1]]></coupon_count><coupon_fee>30</coupon_fee><coupon_fee_0><![CDATA[30]]></coupon_fee_0><coupon_id_0><![CDATA[2000000001378950097]]></coupon_id_0><fee_type><![CDATA[CNY]]></fee_type><is_subscribe><![CDATA[N]]></is_subscribe><mch_id><![CDATA[1284244701]]></mch_id><nonce_str><![CDATA[ek6lcswr93hesctsb315]]></nonce_str><openid><![CDATA[oJAhRwoiYzh8048Yw3_TKfmaDt4o]]></openid><out_trade_no><![CDATA[C170816084940770140]]></out_trade_no><result_code><![CDATA[SUCCESS]]></result_code><return_code><![CDATA[SUCCESS]]></return_code><sign><![CDATA[4DCA8C541AF95627C4C2177E6CC54FB0]]></sign><time_end><![CDATA[20170816084959]]></time_end><total_fee>200</total_fee><trade_type><![CDATA[APP]]></trade_type><transaction_id><![CDATA[4009842001201708166479114931]]></transaction_id> </xml>
//		 //购买时长xml
//		 //String xml="<xml><appid><![CDATA[wxe65a98c5b51cede6]]></appid><bank_type><![CDATA[PSBC_DEBIT]]></bank_type><cash_fee><![CDATA[474]]></cash_fee><coupon_count><![CDATA[1]]></coupon_count><coupon_fee>15</coupon_fee><coupon_fee_0><![CDATA[15]]></coupon_fee_0><coupon_id_0><![CDATA[2000000001220596210]]></coupon_id_0><fee_type><![CDATA[CNY]]></fee_type><is_subscribe><![CDATA[N]]></is_subscribe><mch_id><![CDATA[1284244701]]></mch_id><nonce_str><![CDATA[cefusmptfvb3lre14zk9]]></nonce_str><openid><![CDATA[oJAhRwqVDctklJn1ZaJtlZLgVwx4]]></openid><out_trade_no><![CDATA[170809082836783273]]></out_trade_no><result_code><![CDATA[SUCCESS]]></result_code><return_code><![CDATA[SUCCESS]]></return_code><sign><![CDATA[3A914D5F2B16E9599614EF37EA502328]]></sign><time_end><![CDATA[20170809082905]]></time_end><total_fee>489</total_fee><trade_type><![CDATA[APP]]></trade_type><transaction_id><![CDATA[4007242001201708095259758339]]></transaction_id></xml>";
////购买随心套餐
//		// <xml><appid><![CDATA[wxe65a98c5b51cede6]]></appid><bank_type><![CDATA[CFT]]></bank_type><cash_fee><![CDATA[591]]></cash_fee><fee_type><![CDATA[CNY]]></fee_type><is_subscribe><![CDATA[N]]></is_subscribe><mch_id><![CDATA[1284244701]]></mch_id><nonce_str><![CDATA[5nyku63bg5knii1d56jy]]></nonce_str><openid><![CDATA[oJAhRwp1hjqjuq9yHUSCoHr82bM8]]></openid><out_trade_no><![CDATA[170816174248107320]]></out_trade_no><result_code><![CDATA[SUCCESS]]></result_code><return_code><![CDATA[SUCCESS]]></return_code><sign><![CDATA[A80DA776F9C0F343FFB42B95C887D10D]]></sign><time_end><![CDATA[20170816174305]]></time_end><total_fee>591</total_fee><trade_type><![CDATA[APP]]></trade_type><transaction_id><![CDATA[4004602001201708166561816187]]></transaction_id></xml>
//
//		 String xml2 = "<xml><appid><![CDATA[wxe65a98c5b51cede6]]></appid><bank_type><![CDATA[CFT]]></bank_type><cash_fee><![CDATA[591]]></cash_fee><fee_type><![CDATA[CNY]]></fee_type><is_subscribe><![CDATA[N]]></is_subscribe><mch_id><![CDATA[1284244701]]></mch_id><nonce_str><![CDATA[5nyku63bg5knii1d56jy]]></nonce_str><openid><![CDATA[oJAhRwp1hjqjuq9yHUSCoHr82bM8]]></openid><out_trade_no><![CDATA[170816174248107320]]></out_trade_no><result_code><![CDATA[SUCCESS]]></result_code><return_code><![CDATA[SUCCESS]]></return_code><sign><![CDATA[A80DA776F9C0F343FFB42B95C887D10D]]></sign><time_end><![CDATA[20170816174305]]></time_end><total_fee>591</total_fee><trade_type><![CDATA[APP]]></trade_type><transaction_id><![CDATA[4004602001201708166561816187]]></transaction_id></xml>";
//		 WXPayNotifyUrlEntity wxPayNotifyUrlEntity =XmlUtil.toBean(xml2,WXPayNotifyUrlEntity.class);
//			Map<String, String> beanMap = AlipayCore.convertBean(wxPayNotifyUrlEntity);
//			//去掉空值
//			Map<String, String> resultMap = AlipayCore.paraFilter(beanMap);
//			String string1 = AlipayCore.createLinkString(resultMap);
//			String wxTempSign = string1+"&key="+WeixinConfig.apiKey;
//			wxTempSign = PubFun.MD5(wxTempSign).toUpperCase();
//			logger.debug("微信发过来的： "+wxPayNotifyUrlEntity.getSign());
//			logger.debug("后台根据参数生成的: "+wxTempSign);
//		 logger.debug(string1);
//	}
}
