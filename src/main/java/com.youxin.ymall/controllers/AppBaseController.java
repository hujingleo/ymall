package com.youxin.ymall.controllers;

import com.rsclouds.base.SimpleNetObject;
import com.youxin.ymall.exceptions.SessionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.TypeMismatchException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


/**
 * 所有app请求的基础控制器
 * @author yukun
 *
 */
public class AppBaseController {
	
	
	Logger logger=LoggerFactory.getLogger(AppBaseController.class);
	/**
	 * 当碰到sessionexception时，则为会话超时统一返回901状态码，app客户端需要重新请求
	 * @param ex
	 * @param request
	 * @param response
	 * @return
	 */
	@ExceptionHandler(value={SessionException.class})
	public String exp(Exception ex,HttpServletRequest request,HttpServletResponse response) {
		
		try {
			response.sendError(901);
		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	   return "error";
	} 
	@ExceptionHandler(value={TypeMismatchException.class})  
	public ModelAndView typedispatch(Exception ex,HttpServletRequest request,HttpServletResponse response) {
		
		TypeMismatchException tm=(TypeMismatchException)ex;
		ModelAndView mv=new ModelAndView();
		SimpleNetObject sno=new SimpleNetObject();
		sno.setResult(99);
		sno.setMessage(tm.getMessage());
		mv.addObject(sno);
		MappingJackson2JsonView jsonView=new MappingJackson2JsonView();
		jsonView.setExtractValueFromSingleKeyModel(true);
		mv.setView(jsonView);
		return mv;
		
	} 
	@ExceptionHandler(value={BindException.class})
	public ModelAndView beanbindexception(Exception ex,HttpServletRequest request,HttpServletResponse response) {
		
		BindException be=(BindException)ex;
		ModelAndView mv=new ModelAndView();
		String strMsg=be.getBindingResult().getAllErrors().get(0).getDefaultMessage();
		SimpleNetObject sno=new SimpleNetObject();
		sno.setResult(99);
		
		sno.setMessage(strMsg);
		mv.addObject(sno);
		MappingJackson2JsonView jsonView=new MappingJackson2JsonView();
		jsonView.setExtractValueFromSingleKeyModel(true);
		mv.setView(jsonView);
		return mv;
		
	} 
	
	@ExceptionHandler(value={Exception.class})  
	public ModelAndView notProcessException(Exception ex,HttpServletRequest request,HttpServletResponse response) {
		ModelAndView mav=new ModelAndView();
		StringBuffer sbMsg=new StringBuffer();
		sbMsg.append("请求");
		sbMsg.append(request.getRequestURI());
		sbMsg.append(ex.getMessage());
		MappingJackson2JsonView view=new MappingJackson2JsonView();
		mav.setView(view);
		mav.addObject("result",500);
		
		mav.addObject("message",ex.getMessage());
		
		logger.error(sbMsg.toString(),ex);
		return mav;
			

	   
	}  
	
}
