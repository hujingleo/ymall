package com.youxin.ymall.filters;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;

import com.rsclouds.base.SimpleNetObject;
import com.rsclouds.util.StringTool;
import com.sslkg.utils.SpringUtils;
import com.youxin.ymall.domain.AppUserInfo;
import com.youxin.ymall.service.AppUserService;
import com.youxin.ymall.service.LoginLogService;
import com.youxin.ymall.sessions.AppUserInSession;
import com.youxin.ymall.utils.SessionUtil;

public class AppUserFilter implements Filter {

	private StringRedisTemplate redisTemplate;
	private AppUserService appuserService;
	private LoginLogService loginLogService;

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		// TODO Auto-generated method stub
		redisTemplate = (StringRedisTemplate) SpringUtils.getBean(
				"stringRedisTemplate", StringRedisTemplate.class);
		appuserService = SpringUtils.getBean(AppUserService.class);
		loginLogService = SpringUtils.getBean(LoginLogService.class);
	}

	public static Cookie getCookie(String name, HttpServletRequest request) {
		Cookie[] cookies = request.getCookies();
		if(cookies!=null){
			for (int i = 0; i < cookies.length; i++) {
				Cookie cookie = cookies[i];
				if (name.equals(cookies[i].getName())) {
					return cookie;
				}
			}
		}
		return null;
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		HttpServletRequest resq = (HttpServletRequest) request;
		HttpServletResponse resp = (HttpServletResponse) response;
		HttpSession session = resq.getSession();
		// 如果已经登录并且已经设置过session信息了，则按照流程跑
		if (session != null
				) {
			AppUserInfo userInfo= (AppUserInfo)session.getAttribute(SessionUtil.SESSION_APPUSERINFO);
			if(userInfo!=null){
				try
				{
					//判断用户的设备码是否一样,如果不一样就删除session，返回901
					String devicecode = resq.getParameter("devicecode");
					String encryptdevicecode = resq.getParameter("encryptdevicecode");
					//System.out.println("filter devicecode : "+ devicecode);
					//System.out.println("filter encryptdevicecode : "+ encryptdevicecode);
					//System.out.println("userip "+ resq.getParameter("userip"));
					
					
					SimpleNetObject sno = loginLogService.validate(userInfo.getUsername(),devicecode,encryptdevicecode);
					if(1 != sno.getResult())
					{
						//清空缓存
						session.removeAttribute(SessionUtil.SESSION_APPUSERINFO);
						resp.sendError(901,session.getId());
						return;
					}
					else
					{
						chain.doFilter(request, response);
					}
				} catch (Exception e) {
					e.printStackTrace();
					chain.doFilter(request, response);
				}
			}
			else{
				resp.sendError(901,session.getId());
			}
		} else {
			Cookie cookie = getCookie("JSESSIONID", resq);
			if (cookie != null) {
				String jsessionid = cookie.getValue();
				/**
				 * 判断redis是否启用，如果启用了则去redis里面判断会话是否存储了
				 */
				try {
//					String username = (String) redisTemplate.opsForHash().get(
//							"session", jsessionid);
//					if (StringTool.isNullOrEmpty(username)) {
//
//					} else {
//						AppUserInSession appuserInSession = new AppUserInSession(
//								appuserService.getById(username));
//						session.setAttribute(SessionUtil.SESSION_APPUSERINFO,
//								appuserInSession);
//						chain.doFilter(request, response);
//					}
				} catch (org.springframework.data.redis.RedisConnectionFailureException ex) {
					resp.sendError(901);
				}

			}

			// Cookie cookie=WebUtil.getCookie("sessionid", resq);
			// if(cookie!=null){
			// String sessionid=cookie.getValue();
			// String[] arrPairs=sessionid.split(",");
			// if(arrPairs.length==2){
			// User user =userService.getById(arrPairs[0]);
			// if(user!=null){
			// if(arrPairs[1].equalsIgnoreCase(user.getSessionid())){
			// session.setAttribute(SessionUtil.USERNAME, orderUser);
			// chain.doFilter(request, response);
			// return;
			// }
			// }
			// }
			// }
			resp.sendError(901);

		}

	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub

	}

}
