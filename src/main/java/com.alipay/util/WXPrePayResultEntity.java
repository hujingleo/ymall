package com.alipay.util;

import java.io.Serializable;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * 
 * Description：预支付请求返回结果
 * @author lijianwei
 */
@XStreamAlias("xml")
public class WXPrePayResultEntity implements Serializable
{
	private static final long serialVersionUID = -7712257468736467291L;
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
	@XStreamAlias("trade_type")
	private String trade_type;//取值如下：JSAPI，NATIVE，APP，WAP,
	@XStreamAlias("prepay_id")
	private String prepay_id;//微信生成的预支付回话标识，用于后续接口调用中使用，该值有效期为2小时
	@XStreamAlias("code_url")
	private String code_url;//trade_type为NATIVE是有返回，可将该参数值生成二维码展示出来进行扫码支付
	
	public String getReturn_code() {
		return return_code;
	}
	public void setReturn_code(String return_code) {
		this.return_code = return_code;
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
	public String getTrade_type() {
		return trade_type;
	}
	public void setTrade_type(String trade_type) {
		this.trade_type = trade_type;
	}
	public String getPrepay_id() {
		return prepay_id;
	}
	public void setPrepay_id(String prepay_id) {
		this.prepay_id = prepay_id;
	}
	public String getCode_url() {
		return code_url;
	}
	public void setCode_url(String code_url) {
		this.code_url = code_url;
	}
}
