package com.alipay.util;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("xml")
public class WXOrderQueryResultEntity {
	@XStreamAlias("return_code")
	private String return_code;
	@XStreamAlias("return_msg")
	private String return_msg;
	@XStreamAlias("appid")
	private String appid;
	@XStreamAlias("mch_id")
	private String mch_id;
	@XStreamAlias("device_info")
	private String device_info;
	@XStreamAlias("nonce_str")
	private String nonce_str;
	@XStreamAlias("sign")
	private String sign;
	@XStreamAlias("result_code")
	private String result_code;
	@XStreamAlias("openid")
	private String openid;
	@XStreamAlias("is_subscribe")
	private String is_subscribe;
	@XStreamAlias("trade_type")
	private String trade_type;
	@XStreamAlias("bank_type")
	private String bank_type;
	@XStreamAlias("total_fee")
	private String total_fee;
	@XStreamAlias("fee_type")
	private String fee_type;
	@XStreamAlias("transaction_id")
	private String transaction_id;
	@XStreamAlias("out_trade_no")
	private String out_trade_no;
	@XStreamAlias("attach")
	private String attach;
	@XStreamAlias("time_end")
	private String time_end;
	@XStreamAlias("trade_state")
	private String trade_state;
	@XStreamAlias("err_code")
	private String err_code;
	@XStreamAlias("err_code_des")
	private String err_code_des;
	@XStreamAlias("cash_fee")
	private String cash_fee;
	public WXOrderQueryResultEntity() {
	}
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
	public String getOpenid() {
		return openid;
	}
	public void setOpenid(String openid) {
		this.openid = openid;
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
	public String getTrade_state() {
		return trade_state;
	}
	public void setTrade_state(String trade_state) {
		this.trade_state = trade_state;
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
	public String getCash_fee() {
		return cash_fee;
	}
	public void setCash_fee(String cash_fee) {
		this.cash_fee = cash_fee;
	}
	
}
