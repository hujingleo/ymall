package com.youxin.ymall.app;

import com.youxin.ymall.service.SettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Service;




/**
 * 启动初始化检查
 * @author yukun
 *
 */
@Service
public class StartupInit implements ApplicationListener<ContextRefreshedEvent> {

	
	@Autowired
	private SettingService settingService;
	
	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		if (event.getApplicationContext().getParent() == null) {// root
																// application
																// context
																// 没有parent，他就是老大.
			
			int result=settingService.checksetting();
			if(result!=1){
				throw new RuntimeException("应用设置初始化有问题，请仔细检查日志");
			}
		}

	}

}
