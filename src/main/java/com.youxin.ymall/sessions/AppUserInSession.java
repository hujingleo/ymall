package com.youxin.ymall.sessions;

import java.io.Serializable;
import java.util.Date;

import com.rsclouds.util.DateUtil;
import com.youxin.ymall.entity.AppUser;

public class AppUserInSession implements Serializable{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1991462355379167034L;
	private String username;
	private String truename;
	private String packageName;
	private Date startTime;
	private Date endTime;
	private int accountState;

	private String userTemplate;
	
	public AppUserInSession(){
		
	}
	
	public AppUserInSession(AppUser appUser){
		this.username=appUser.getUsername();
		this.truename=appUser.getUsername();
		this.userTemplate=appUser.getTemplate_name();
		this.packageName=appUser.getPackagename();
		this.startTime=appUser.getCreated_datetime();
		this.endTime=appUser.getMemshipdate();
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getTruename() {
		return truename;
	}

	public void setTruename(String truename) {
		this.truename = truename;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public int getAccountState() {
		return accountState;
	}

	public void setAccountState(int accountState) {
		this.accountState = accountState;
	}

	public String getUserTemplate() {
		return userTemplate;
	}

	public void setUserTemplate(String userTemplate) {
		this.userTemplate = userTemplate;
	}
	
	public String getBalanceTime() {
		
		//有结束时间给出结束
		if(this.getEndTime()!=null){
			return DateUtil.getFormatDate(this.getEndTime(), "yyyy-MM-dd");
		}
		//没有结束时间看看是否有开始时间，如果有则是
		else{
			return this.getPackageName();
		}
	}
	
	
	
}
