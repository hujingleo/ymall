package com.youxin.ymall.controllers;

import com.rsclouds.base.SimpleNetObject;
import com.rsclouds.util.StringTool;
import com.youxin.ymall.configs.ReturnCode;
import com.youxin.ymall.domain.AppUserInfo;
import com.youxin.ymall.domain.SimpleNetObjectWithSession;
import com.youxin.ymall.service.*;
import com.youxin.ymall.utils.SessionUtil;
import com.youxin.ymall.utils.StringTools;
import com.youxin.ymall.utils.ValidateCode;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.OutputStream;


@RequestMapping("/rest")
@Controller
public class LoginController {
	private Logger logger = LoggerFactory.getLogger(LoginController.class);
	@Autowired
	private AppUserService appUserService;
	@Value("${rediscache}")
	private boolean enableCache;
	@Value("${youxin.portal}")
	private String portalUrl;
	@Autowired
	private StringRedisTemplate redisTemplate;
	@Autowired
	private com.youxin.ymall.service.LoginLogService loginLogService;
	@Autowired
	private RedissonClient redclient;
	@Autowired
	private MembeshipUserService membershipUser;
	public static final String LOGINLOG_MSG_CODE = "loginlog_msg_code";
	public static final String LOGINLOG_PIC_CODE = "loginlog_pic_code";
	
//	@RequestMapping("/signin")
//	@ResponseBody
//	public SimpleNetObject signin(
//			String username,
//			String password,
//			String location,
//			HttpServletResponse response,
//			HttpSession session){
//		SimpleNetObject sno=membershipUser.validateUser(username, password);
//		if(sno.getResult()==1){
//			//用于持久化存储sessionid，用户下次再登录不需要再执行有关认证
//			SimpleNetObjectWithPointLevel sowp=new SimpleNetObjectWithPointLevel(sno);
//			sowp.setPointLevels(this.userPointLevelService.getByThreeId("YOUMI"));
//			session.setAttribute(SessionUtil.SESSION_APPUSERINFO
//					, (AppUserInfo)sno.getData());
//			return sowp;
//		}
//		else{
//			return sno;
//		}
//	}
	
	/**
	 * 
	 * Description：
	 * @param username
	 * @param password
	 * @param location
	 * @param devicecode
	 * @param encryptdevicecode
	 * @param msg_code
	 * @param response
	 * @param session
	 * @param pushtype 是否需要推送验证码，1和null是要，其他不需要
	 * @return
	 * @throws Exception       
	 *
	 */
	@RequestMapping("/signin")
	@ResponseBody
	public SimpleNetObject signin(String username,
			String password,
			String location,
			String devicecode,String encryptdevicecode,
			String msg_code,
			HttpServletResponse response,
			HttpSession session,Integer pushtype) throws Exception {
		SimpleNetObject sno=new SimpleNetObject();

		//验证登录终端
		sno = loginLogService.validate(username,devicecode,encryptdevicecode);

		//result 1 : 相同终端|第一次登录
		if(sno.getResult()!= ReturnCode.APP_LOGIN_VALIDATE_MOREPOINT&&sno.getResult()!=1) {
			return sno;
		}else if(sno.getResult()== ReturnCode.APP_LOGIN_VALIDATE_MOREPOINT) {
			if(StringTool.isNullOrEmpty(msg_code)) {
				if(null == pushtype || 1 == pushtype)
				{
				}
				return sno;
			}
			if(!loginLogService.validateCode(username,msg_code)) {
				sno.setResult(ReturnCode.APP_LOGIN_VALIDATE_MSGCODE_ERROR);
				sno.setMessage("推送的消息验证码错误。");
				return sno;
			}else {
				return validatePw(username,password,devicecode,encryptdevicecode,session);
			}
		}else {
			return validatePw(username,password,devicecode,encryptdevicecode,session);
		}
	}
	/**
	 * 检验账号密码
	 */
	private SimpleNetObject validatePw(String username,String password,String devicecode,String encryptdevicecode,HttpSession session) {

		SimpleNetObject sno=membershipUser.validateUser(username, password);
		if(sno.getResult()==1){
			//用于持久化存储sessionid，用户下次再登录不需要再执行有关认证
			SimpleNetObjectWithSession sowp=new SimpleNetObjectWithSession(sno);
			session.setAttribute(SessionUtil.SESSION_APPUSERINFO
					, (AppUserInfo)sno.getData());
			sowp.setJsessionid(session.getId());
			//添加登录记录
			loginLogService.addLog(username, devicecode);
			return sowp;
		}
		else{
			return sno;
		}
	}
	
	/**
	 * 重新推送消息验证码
	 * @param username
	 * @return
	 */
	@RequestMapping("/repush")
	@ResponseBody
	public SimpleNetObject repush(String username,HttpSession session) {
		SimpleNetObject sno=new SimpleNetObject();
		if(StringTool.isNullOrEmpty(username)){
			sno.setResult(ReturnCode.APP_LOGIN_VALIDATEERROR);
			sno.setMessage("缺少用户名参数");
			return sno;
		}
		sno.setResult(1);
		return sno;
	}
	
	/**
	 * 产生一个图片验证码，并存至session中
	 * @param width  默认：300
	 * @param height  默认：100
	 * @param codeCount 默认：4
	 * @param lineCount 干扰线数目  默认：3
	 * @param isNumber true
	 */
	@RequestMapping("/pcode")
	public void pcode(Integer width,Integer height,Integer codeCount,Integer lineCount,Boolean isNumber,HttpSession session,HttpServletResponse response) {
		if(width == null) { width = 300;}
		if(height == null) { height = 100;}
		if(codeCount == null) { codeCount = 4;}
		if(lineCount == null) { lineCount = 10;}
		if(isNumber == null) { isNumber = true;}
		try {
			OutputStream op = response.getOutputStream();
			ValidateCode vc = new ValidateCode(width, height, codeCount, lineCount, isNumber);
			vc.write(op);
			session.setAttribute(LOGINLOG_PIC_CODE , vc.getCode());
			logger.debug(vc.getCode());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
