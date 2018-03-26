package com.youxin.ymall.controllers;

import com.youxin.ymall.exceptions.SessionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class WebBaseController {
	/**
	 * 当碰到sessionexception时，则为会话超时统一返回901状态码，app客户端需要重新请求
	 * @param ex
	 * @param request
	 * @param response
	 * @return
	 */
	@ExceptionHandler(value={SessionException.class})
	public ModelAndView exp(Exception ex,HttpServletRequest request,HttpServletResponse response) {
		
		
			ModelAndView mv=new ModelAndView("/login");
			boolean isMobile=com.youxin.ymall.utils.WebUtil.isMobileDevice(request);
			return mv;
		
	} 
}
