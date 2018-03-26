package com.youxin.ymall.controllers;

import com.rsclouds.base.SimpleNetObject;
import com.youxin.ymall.entity.Appinfo;
import com.youxin.ymall.service.AppinfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
@RequestMapping("/rest")
public class AppUpdateController {

	@Autowired
	private AppinfoService appInfoService;
	
	@RequestMapping("/getlastestappinfo")
	@ResponseBody
	public SimpleNetObject getlastestappinfo(String userip,String type,String ssid,String offline){
		SimpleNetObject sno=new SimpleNetObject();
		
		Appinfo appinfo=this.appInfoService.getLatestApp(type,userip,ssid,offline);
		sno.setData(appinfo);
		sno.setResult(1);
		return sno;
	}
}
