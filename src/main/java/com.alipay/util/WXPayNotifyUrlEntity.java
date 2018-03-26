package com.alipay.util;

import java.io.Serializable;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("xml")
public class WXPayNotifyUrlEntity implements Serializable
{
	private static final long serialVersionUID = 7652717400655347097L;
	@XStreamAlias("return_code")
	private String return_code;//SUCCESS/FAIL
	@XStreamAlias("return_msg")
	private String return_msg;//返回信息，如非空，为错误原因签名失败 参数格式校验错误
	
	@XStreamAlias("appid")
	private String appid;//微信分配的公众账号ID（企业号corpid即为此appId）
	@XStreamAlias("mch_id")
	private String mch_id;//微信支付分配的商户号
	@XStreamAlias("device_info")
	private String device_info;//终端设备号(门店号或收银设备ID)，注意：PC网页或公众号内支付请传"WEB"
	@XStreamAlias("nonce_str")
	private String nonce_str;//随机字符串，不长于32位。
	@XStreamAlias("sign")
	private String sign;//签名
	@XStreamAlias("result_code")
	private String result_code;//SUCCESS/FAIL业务结果
	@XStreamAlias("err_code")
	private String err_code;//
	@XStreamAlias("err_code_des")
	private String err_code_des;//错误返回的信息描述
	//以下字段在return_code 和result_code都为SUCCESS的时候有返回
	@XStreamAlias("is_subscribe")
	private String is_subscribe;//用户是否关注公众账号，Y-关注，N-未关注，仅在公众账号类型支付有效
	@XStreamAlias("trade_type")
	private String trade_type;//JSAPI、NATIVE、APP
	@XStreamAlias("bank_type")
	private String bank_type;//银行类型，采用字符串类型的银行标识，
	@XStreamAlias("total_fee")
	private String total_fee;//订单总金额，单位为分
	@XStreamAlias("fee_type")
	private String fee_type;//货币类型，符合ISO4217标准的三位字母代码，默认人民币：CNY，
	@XStreamAlias("cash_fee")
	private String cash_fee;//现金支付金额订单现金支付金额，
	@XStreamAlias("cash_fee_type")
	private String cash_fee_type;//货币类型，符合ISO4217标准的三位字母代码，默认人民币：CNY，
	@XStreamAlias("transaction_id")
	private String transaction_id;//微信支付订单号
	@XStreamAlias("out_trade_no")
	private String out_trade_no;//商户系统的订单号，与请求一致
	@XStreamAlias("attach")
	private String attach;//商家数据包，原样返回
	@XStreamAlias("time_end")
	private String time_end;//支付完成时间，格式为yyyyMMddHHmmss，如2009年12月25日9点10分10秒表示为20091225091010。
	@XStreamAlias("openid")
	private String openid;
	@XStreamAlias("coupon_count")
	private String coupon_count;
	@XStreamAlias("coupon_fee")
	private String coupon_fee;
	@XStreamAlias("coupon_fee_0")
	private String coupon_fee_0;
	@XStreamAlias("coupon_id_0")
	private String coupon_id_0;
	@XStreamAlias("body")
	private String body;
	@XStreamAlias("detail")
	private String detail;
	@XStreamAlias("spbill_create_ip")
	private String spbill_create_ip;
	@XStreamAlias("time_start")
	private String time_start;
	@XStreamAlias("time_expire")
	private String time_expire;
	@XStreamAlias("notify_url")
	private String notify_url;
	
	public String getCoupon_count() {
		return coupon_count;
	}
	public void setCoupon_count(String coupon_count) {
		this.coupon_count = coupon_count;
	}
	public String getCoupon_fee_0() {
		return coupon_fee_0;
	}
	public void setCoupon_fee_0(String coupon_fee_0) {
		this.coupon_fee_0 = coupon_fee_0;
	}
	public String getCoupon_id_0() {
		return coupon_id_0;
	}
	public void setCoupon_id_0(String coupon_id_0) {
		this.coupon_id_0 = coupon_id_0;
	}
	public String getCoupon_fee() {
		return coupon_fee;
	}
	public void setCoupon_fee(String coupon_fee) {
		this.coupon_fee = coupon_fee;
	}
	
	
	public String getOpenid() {
		return openid;
	}
	public void setOpenid(String openid) {
		this.openid = openid;
	}
	public String getReturn_code() {
		return return_code;
	}
	public void setReturn_code(String return_code) {
		this.return_code = return_code;
	}
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
	public String getSpbill_create_ip() {
		return spbill_create_ip;
	}
	public void setSpbill_create_ip(String spbill_create_ip) {
		this.spbill_create_ip = spbill_create_ip;
	}
	public String getDetail() {
		return detail;
	}
	public void setDetail(String detail) {
		this.detail = detail;
	}
	public String getTime_start() {
		return time_start;
	}
	public void setTime_start(String time_start) {
		this.time_start = time_start;
	}
	public String getTime_expire() {
		return time_expire;
	}
	public void setTime_expire(String time_expire) {
		this.time_expire = time_expire;
	}
	public String getNotify_url() {
		return notify_url;
	}
	public void setNotify_url(String notify_url) {
		this.notify_url = notify_url;
	}
	public String getReturn_msg() {
		return return_msg;
	}
	public void setReturn_msg(String return_msg) {
		this.return_msg = return_msg;
	}
	public String getAppid() {
		return appid;
	}
	public void setAppid(String appid) {
		this.appid = appid;
	}
	public String getMch_id() {
		return mch_id;
	}
	public void setMch_id(String mch_id) {
		this.mch_id = mch_id;
	}
	public String getDevice_info() {
		return device_info;
	}
	public void setDevice_info(String device_info) {
		this.device_info = device_info;
	}
	public String getNonce_str() {
		return nonce_str;
	}
	public void setNonce_str(String nonce_str) {
		this.nonce_str = nonce_str;
	}
	public String getSign() {
		return sign;
	}
	public void setSign(String sign) {
		this.sign = sign;
	}
	public String getResult_code() {
		return result_code;
	}
	public void setResult_code(String result_code) {
		this.result_code = result_code;
	}
	public String getErr_code() {
		return err_code;
	}
	public void setErr_code(String err_code) {
		this.err_code = err_code;
	}
	public String getErr_code_des() {
		return err_code_des;
	}
	public void setErr_code_des(String err_code_des) {
		this.err_code_des = err_code_des;
	}
	public String getIs_subscribe() {
		return is_subscribe;
	}
	public void setIs_subscribe(String is_subscribe) {
		this.is_subscribe = is_subscribe;
	}
	public String getTrade_type() {
		return trade_type;
	}
	public void setTrade_type(String trade_type) {
		this.trade_type = trade_type;
	}
	public String getBank_type() {
		return bank_type;
	}
	public void setBank_type(String bank_type) {
		this.bank_type = bank_type;
	}
	public String getTotal_fee() {
		return total_fee;
	}
	public void setTotal_fee(String total_fee) {
		this.total_fee = total_fee;
	}
	public String getFee_type() {
		return fee_type;
	}
	public void setFee_type(String fee_type) {
		this.fee_type = fee_type;
	}
	public String getCash_fee() {
		return cash_fee;
	}
	public void setCash_fee(String cash_fee) {
		this.cash_fee = cash_fee;
	}
	public String getCash_fee_type() {
		return cash_fee_type;
	}
	public void setCash_fee_type(String cash_fee_type) {
		this.cash_fee_type = cash_fee_type;
	}
	public String getTransaction_id() {
		return transaction_id;
	}
	public void setTransaction_id(String transaction_id) {
		this.transaction_id = transaction_id;
	}
	public String getOut_trade_no() {
		return out_trade_no;
	}
	public void setOut_trade_no(String out_trade_no) {
		this.out_trade_no = out_trade_no;
	}
	public String getAttach() {
		return attach;
	}
	public void setAttach(String attach) {
		this.attach = attach;
	}
	public String getTime_end() {
		return time_end;
	}
	public void setTime_end(String time_end) {
		this.time_end = time_end;
	}
}
