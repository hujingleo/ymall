package com.youxin.ymall.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;


@Controller
@RequestMapping("/app")
public class AppController {

	
	@RequestMapping("/download")
	public String download(HttpServletRequest request){
		return "portal/mobile/download";
	}
}
