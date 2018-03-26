package com.youxin.ymall.controllers;

import com.rsclouds.base.SimpleNetObject;
import com.youxin.ymall.entity.AppUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
@RequestMapping("/rest")
public class RedisController {

	private Logger logger=LoggerFactory.getLogger(RedisController.class);
	@Autowired
	RedisTemplate<String, String> redisTemplate;  
	@Autowired
	RedisTemplate<String, AppUser> redisAppUserTemplate;
	@RequestMapping("/setcache")
	@ResponseBody
	public SimpleNetObject setCacheValue(String key,String value){
		SimpleNetObject sno=new SimpleNetObject();
		try
		{
			redisTemplate.opsForValue().set(key, value);
			sno.setResult(1);
			sno.setMessage("设置成功");
		}
		catch(Exception ex){
			logger.debug(ex.getMessage());
			sno.setResult(99);
			sno.setMessage("设置失败");
		}
		return sno;
	}
	@RequestMapping("/scobj")
	@ResponseBody
	public SimpleNetObject setCacheObject(String key){
		SimpleNetObject sno=new SimpleNetObject();
		try
		{
			AppUser appUser=new AppUser();
			appUser.setUsername("yukun");
			redisAppUserTemplate.opsForValue().set(key, appUser);
			sno.setResult(1);
			sno.setMessage("设置成功");
		}
		catch(Exception ex){
			logger.debug(ex.getMessage());
			sno.setResult(99);
			sno.setMessage("设置失败");
		}
		return sno;
	}
	@RequestMapping("/getcache")
	@ResponseBody
	public String getCacheValue(String key){
		SimpleNetObject sno=new SimpleNetObject();
		try
		{
			String value=redisTemplate.opsForValue().get(key);
			return value;
		}
		catch(Exception ex){
			logger.debug(ex.getMessage());
			return "error";
		}
		
	}
}
