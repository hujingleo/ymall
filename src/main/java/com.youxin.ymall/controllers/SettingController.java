package com.youxin.ymall.controllers;

import com.rsclouds.base.SimpleNetObject;
import com.rsclouds.util.StringTool;
import com.youxin.ymall.entity.AppUser;
import com.youxin.ymall.entity.Setting;
import com.youxin.ymall.service.AppUserService;
import com.youxin.ymall.service.SettingService;
import com.youxin.ymall.utils.SessionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.HttpSession;
import java.util.HashMap;


@Controller
@RequestMapping("/rest/v1/setting")
public class SettingController extends AppBaseController {
	
	@Autowired
	private SettingService settingService;
	@Autowired
	private AppUserService appUserService;
	
	@RequestMapping("/getValue")
	@ResponseBody
	public SimpleNetObject getValue(String key ,HttpSession session , String helpbuyusername) {
		SimpleNetObject so = new SimpleNetObject();
		Setting s = settingService.getById(key);
		String username = SessionUtil.getCurrentUserName(session);
if (!StringTool.isNullOrEmpty(helpbuyusername)) {
	username=helpbuyusername;
}
		
		if(s == null) {
			so.setMessage("获取失败。");
			so.setResult(-1);
			return so;
		}
		
		so.setResult(1);
		so.setData(s);
		if (key.equals("GGK_PRICE")) {
			String str = s.getValue();
			double price = Double.parseDouble(str);
			double value = price/100;
			String valuestr = Double.toString(value);
			s.setValue(valuestr);
		}
		if (key.equals("ADVERTISING")) {
			s=settingService.getById(key);
			so.setData(s);
		}
		if (key.equals("huanxinkefu")) {
			HashMap<String, String> newsetting = new HashMap<String, String>();
			Setting s1=settingService.getById(key);
			if (s1.getValue().equals("true")) {
				newsetting.put("value", "true");
			}else{
				newsetting.put("value", "false");
			}
			s=settingService.getById("flopaward");
			if (s.getValue().equals("true")) {
				newsetting.put("flopaward", "true");
			}else{
				newsetting.put("flopaward", "false");
			}
			so.setData(newsetting);
		}
		return so;
}
}
