package com.alipay.config;

/* *
 *类名：AlipayConfig
 *功能：基础配置类
 *详细：设置帐户有关信息及返回路径
 *版本：3.3
 *日期：2012-08-10
 *说明：
 *以下代码只是为了方便商户测试而提供的样例代码，商户可以根据自己网站的需要，按照技术文档编写,并非一定要使用该代码。
 *该代码仅供学习和研究支付宝接口使用，只是提供一个参考。
	
 *提示：如何获取安全校验码和合作身份者ID
 *1.用您的签约支付宝账号登录支付宝网站(www.alipay.com)
 *2.点击“商家服务”(https://b.alipay.com/order/myOrder.htm)
 *3.点击“查询合作者身份(PID)”、“查询安全校验码(Key)”

 *安全校验码查看时，输入支付密码后，页面呈灰色的现象，怎么办？
 *解决方法：
 *1、检查浏览器配置，不让浏览器做弹框屏蔽设置
 *2、更换浏览器或电脑，重新登录查询。
 */

public class AlipayConfig {
	
	//↓↓↓↓↓↓↓↓↓↓请在这里配置您的基本信息↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓
	// 合作身份者ID，以2088开头由16位纯数字组成的字符串
	public static String partner = "2088921849391336";
	
	public static String app_id = "2018022602276054";
	
	// 商户的私钥
	public static String private_key = "";
	
	// 支付宝的公钥，无需修改该值
	public static String ali_public_key  = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAhFYtN6wADtgKdGNsM2cij/6av8BQOB9TA8frlhnHJCQTNIBwNk4lwPDnhFJzOP9mKFPI+O2aqrscCmb/sEWlf0xQ5YexK3cauEBuAnFiOnexuMruOubajbDwwAuoz3sek8F2s5IBHt+AQLE75NKMTBMhxZl3Em9Nxr/QT90BT27Y4/xrQRgfoyoHEFB4qUgbvLy8iKVfbC1ZO2wMtIPxcGz3lI1W7jenhe+aOHQMwRZQxC7aWz2QQif6MQ0ICGu5/UJOg4lS6N90eXp0J/Ssf/xZawhwQSXYsLj+xZf2Xp1ah7WEMrxsdtxrB7mLY1WKP8yPZTAh5Iju2LTfvYmy5wIDAQAB";

	//↑↑↑↑↑↑↑↑↑↑请在这里配置您的基本信息↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑
	

	// 调试用，创建TXT日志文件夹路径
	public static String log_path = "D:\\";

	// 字符编码格式 目前支持 gbk 或 utf-8
	public static String input_charset = "utf-8";
	
	// 签名方式 不需修改
	public static String sign_type = "RSA2";

}
