package com.youxin.ymall.controllers;

import com.rsclouds.base.SimpleNetObject;
import com.rsclouds.util.StringTool;
import com.youxin.ymall.configs.Constant;
import com.youxin.ymall.configs.ReturnCode;
import com.youxin.ymall.entity.AppUser;
import com.youxin.ymall.exceptions.OperDBException;
import com.youxin.ymall.exceptions.SamException;
import com.youxin.ymall.service.AppUserService;
import com.youxin.ymall.service.AppUserServiceWrapper;
import com.youxin.ymall.utils.StringTools;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Date;

@Controller
@RequestMapping("/rest")
public class RegisterController extends AppBaseController {

    private Logger logger = LoggerFactory.getLogger(RegisterController.class);
    @Autowired
    private AppUserService appuserService;
    @Autowired
    private AppUserServiceWrapper appuserServiceWrapper;
    /**
     * 发送注册短信验证码
     *
     * @param mobile
     * @param session
     * @return
     */
    @RequestMapping("/sendregmsg")
    @ResponseBody
    public SimpleNetObject sendregmsg(String mobile, String devicetype, HttpSession session) {
        SimpleNetObject sno = new SimpleNetObject();
        AppUser appuser = this.appuserService.getById(mobile);
        if (appuser != null) {
            sno.setResult(90);
            sno.setMessage("该手机号已注册");
            return sno;
        }
        String ramCode = StringTool.getRamCode(4);
        String strContent = "您的短信注册验证码为:" + ramCode;
        if (StringTool.isNullOrEmpty(devicetype)) {
            devicetype = "移动终端";
        }
        try {
            Date sendcodedate = (Date) session.getAttribute("session_register_sendcode");
            if (null != sendcodedate) {
                long distancetime = new Date().getTime() - sendcodedate.getTime();
                if (1000 * 40 > distancetime) {
                    sno.setResult(98);
                    sno.setMessage("请等待一分钟才能重新发送验证码哦！");
                    return sno;
                }
            }
        } catch (Exception e) {
        }
        if (sno.getResult() == 1) {
            session.setAttribute(Constant.SESSION_REG_MSGCODE, ramCode);
            session.setAttribute(Constant.SESSION_REG_MOBILE, mobile);
            session.setAttribute("session_register_sendcode", new Date());

            return sno;
        } else
            return sno;

    }

    /**
     * portal发送短信注册验证码
     *
     * @param mobile
     * @param devicetype
     * @param session
     * @return
     */
    @RequestMapping("/sendregmsgforportal")
    @ResponseBody
    public SimpleNetObject sendregmsgforportal(String mobile, String devicetype, HttpSession session) {
        SimpleNetObject sno = new SimpleNetObject();
        if (StringTool.isNullOrEmpty(mobile)) {
            sno.setResult(90);
            sno.setMessage("请输入正确的手机号码");
            return sno;
        }
        AppUser appuser = appuserService.getById(mobile);
        if (appuser != null) {

            sno.setResult(90);
            sno.setMessage("该用户已注册");
            return sno;
        }
        if (!StringTool.isMobile(mobile)) {
            sno.setResult(90);
            sno.setMessage("请输入正确的手机号码");
            return sno;
        }
        String ramCode = StringTool.getRamCode(4);
        String strContent = "您的短信注册验证码为:" + ramCode;

        try {
            Date sendcodedate = (Date) session.getAttribute("session_register_sendcode");
            if (null != sendcodedate) {
                long distancetime = new Date().getTime() - sendcodedate.getTime();
                if (1000 * 40 > distancetime) {
                    sno.setResult(98);
                    sno.setMessage("请等待一分钟才能重新发送验证码哦！");
                    return sno;
                }
            }
        } catch (Exception e) {
        }
        if (sno.getResult() == 1) {
            session.setAttribute(Constant.SESSION_REG_MSGCODE, ramCode);
            session.setAttribute(Constant.SESSION_REG_MOBILE, mobile);
            session.setAttribute("session_register_sendcode", new Date());
            return sno;
        } else
            return sno;

    }

    /**
     * 注册用户
     *
     * @param username
     * @param mobile
     * @param password
     * @param vcode
     * @param session
     * @return
     */
    @RequestMapping("/adduser")
    @ResponseBody
    public SimpleNetObject adduser(String username, String mobile,
                                   String password, String vcode, String regip, String regapmac
            , String yqr, HttpServletRequest request, HttpSession session) {
        SimpleNetObject sno = new SimpleNetObject();
        String sessionVcode = (String) session
                .getAttribute(Constant.SESSION_REG_MSGCODE);
        String sessionMobile = (String) session
                .getAttribute(Constant.SESSION_REG_MOBILE);

        if (StringTool.isNullOrEmpty(sessionVcode)) {
            sno.setResult(ReturnCode.REGISTER_VCODE_NOTEXISTS);
            sno.setMessage("请先发送短信验证码");
            return sno;
        }
        if (StringTool.isNullOrEmpty(sessionMobile)) {
            sno.setResult(ReturnCode.REGISTER_VCODE_NOTEXISTS);
            sno.setMessage("请输入手机号码");
            return sno;
        }
        if (!sessionVcode.equalsIgnoreCase(vcode)) {
            sno.setResult(ReturnCode.REGISTER_VCODE_NOTEXISTS);
            sno.setMessage("短信验证码输入不正确");
            return sno;
        }
        if (!StringTool.isNullOrEmpty(yqr) && null == appuserService.getById(yqr)) {
            return new SimpleNetObject(99, "邀请人不存在,请重新输入");
        }
        AppUser appuser = new AppUser();
        appuser.setPassword(password);
        appuser.setMobile(mobile);
        appuser.setViptype("1");
        appuser.setName(username);
        appuser.setUsername(appuser.getMobile());
        appuser.setYqr(yqr);
        appuser.setUsersource("REGISTER");
        appuser.setRegapmac(regapmac);
        appuser.setRegip(regip);
        String devicecode = request.getParameter("devicecode");
        try {
            sno = this.appuserService.createUser(appuser, devicecode);
            return sno;
        } catch (SamException se) {
            sno.setResult(98);
            sno.setMessage("WIFIAUTH开户失败，请联系客服检查预销户");
            logger.error(se.getMessage());
            return sno;
        } catch (Exception ex) {
            sno.setResult(99);
            sno.setMessage("用户注册失败");
            logger.error(ex.getMessage());
            return sno;
        }

    }


    /**
     * 注册用户
     *
     * @param username
     * @param mobile
     * @param password
     * @param vcode
     * @param session
     * @return
     */
    @RequestMapping("/portaladduser")
    @ResponseBody
    public SimpleNetObject portaladduser(String username, String mobile,
                                         String password, String vcode, String regip, String regapmac
            , String yqr, HttpSession session) {
        SimpleNetObject sno = new SimpleNetObject();
        String sessionVcode = (String) session
                .getAttribute(Constant.SESSION_REG_MSGCODE);
        String sessionMobile = (String) session
                .getAttribute(Constant.SESSION_REG_MOBILE);

        if (StringTool.isNullOrEmpty(sessionVcode)) {
            sno.setResult(ReturnCode.REGISTER_VCODE_NOTEXISTS);
            sno.setMessage("请先发送短信验证码");
            return sno;
        }
        if (StringTool.isNullOrEmpty(sessionMobile)) {
            sno.setResult(ReturnCode.REGISTER_VCODE_NOTEXISTS);
            sno.setMessage("请输入手机号码");
            return sno;
        }
        if (!sessionVcode.equalsIgnoreCase(vcode)) {
            sno.setResult(ReturnCode.REGISTER_VCODE_NOTEXISTS);
            sno.setMessage("短信验证码输入不正确");
            return sno;
        }
        AppUser appuser = new AppUser();

        appuser.setPassword(password);
        appuser.setMobile(mobile);
        appuser.setViptype("1");
        appuser.setName(appuser.getUsername());
        appuser.setUsername(appuser.getMobile());
        appuser.setYqr(yqr);
        appuser.setUsersource("PORTALREGISTER");
        appuser.setRegapmac(regapmac);
        appuser.setRegip(regip);
        try {
            sno = this.appuserService.createUser(appuser, "portaladduser_youxin888_2016-11-03");
            return sno;
        } catch (SamException se) {
            sno.setMessage("WIFIAUTH开户失败，请联系客服检查预销户");
            logger.error(se.getMessage());
            return sno;
        } catch (Exception ex) {
            sno.setMessage("用户注册失败");
            logger.error(ex.getMessage());
            return sno;
        }

    }

    /**
     * 发送忘记密码验证码
     *
     * @param mobile
     * @param session
     * @return
     */
    @RequestMapping("/sendforgetpwdmsg")
    @ResponseBody
    public SimpleNetObject sendforgetpwdmsg(String mobile,
                                            String devicetype, HttpSession session) {
        SimpleNetObject sno = new SimpleNetObject();
        if (!StringTool.isMobile(mobile)) {
            sno.setResult(90);
            sno.setMessage("请输入正确的手机号码");
            return sno;
        }
        AppUser appuser = this.appuserService.getById(mobile);
        /**
         * 该帐号存在可以发送忘记密码短信，如果不存在直接提示用户
         */
        if (appuser != null) {
            String ramCode = StringTool.getRamCode(4);
            String strContent = "您的短信重置验证码为:" + ramCode;

            //判断上次发送验证码的时间是否在40秒之内
            try {
                Date sendcodedate = (Date) session.getAttribute("session_forgetpassword_sendcode");
                if (null != sendcodedate) {
                    long distancetime = new Date().getTime() - sendcodedate.getTime();
                    if (1000 * 40 > distancetime) {
                        sno.setResult(98);
                        sno.setMessage("请等待一分钟才能重新发送验证码哦！");
                        return sno;
                    }
                }
            } catch (Exception e) {
            }
            if (sno.getResult() == 1) {
                session.setAttribute(Constant.SESSION_FORGETPWD_VCODE, ramCode);
                session.setAttribute(Constant.SESSION_FORGETPWD_MOBILE, mobile);
                session.setAttribute("session_forgetpassword_sendcode", new Date());
                return sno;
            } else
                return sno;
        } else {
            sno.setResult(0);
            sno.setMessage("该手机号尚未注册");
            return sno;
        }
    }

    /**
     * 通过短信验证码重置密码
     *
     * @param mobile
     * @param session
     * @return
     */
    @RequestMapping("/resetpwdbyvcode")
    @ResponseBody
    public SimpleNetObject resetpwdbyvcode(String mobile, String vcode,
                                           String password, HttpSession session) {
        String vcodeInSession = (String) session
                .getAttribute(Constant.SESSION_FORGETPWD_VCODE);
        String mobileInSession = (String) session
                .getAttribute(Constant.SESSION_FORGETPWD_MOBILE);
        SimpleNetObject sno = new SimpleNetObject();
        if (StringTool.isNullOrEmpty(vcodeInSession)
                || !vcodeInSession.equalsIgnoreCase(vcode)) {
            sno.setResult(0);
            sno.setMessage("请输入正确的验证码");
            return sno;
        }
        if (StringTool.isNullOrEmpty(mobileInSession)
                || !mobileInSession.equalsIgnoreCase(mobile)) {
            sno.setResult(0);
            sno.setMessage("请输入正确的手机号码");
            return sno;
        }
        if (StringTool.isNullOrEmpty(password)) {
            sno.setResult(0);
            sno.setMessage("密码不能为空");
            return sno;
        }
        return appuserServiceWrapper.modifyUserPassword(mobile, password);


    }
}
