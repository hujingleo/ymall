package com.youxin.ymall.controllers;


import com.rsclouds.base.SimpleNetObject;
import com.youxin.ymanage.service.MyModuleService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

/**
 * 我的功能模块APP接口 调用
 */
@Controller
@RequestMapping("/rest/my")
public class MyModuleController extends AppBaseController {
	@Resource
	private MyModuleService myModuleService;

	/**
	 * 通过参数调用APP接口 
	 * 参数名：oclass 
	 * param bianjie_shenghuo 便捷 活动 
	 * param shenhuo_service 生活 第三方服务
	 * param others 更多 设置
	 */
	@RequestMapping("/queryMyModuleByOclass")
	@ResponseBody
	public SimpleNetObject query_myModule_ByOclass(String oclass) {
		return myModuleService.query_myModule(oclass);
	}
	
	/**
	 * 清除功能块管理缓存
	 */
	@RequestMapping("/clearCacheQueryAllMyModuleApps")
	@ResponseBody
	public String clearCacheQueryAllTuiGuangApps()
	{
		return myModuleService.clearCacheQueryAllMyModuleApps();
	}
	
	
	/**
	 * APP调用查询所有的功能块管理列表
	 * @return
	 */
	@RequestMapping("/queryMyModuleApps")
	@ResponseBody
	public SimpleNetObject queryMyModuleApps() {
		return myModuleService.queryMymoduleApps();
	}
}
