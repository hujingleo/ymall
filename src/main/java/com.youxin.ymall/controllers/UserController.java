package com.youxin.ymall.controllers;

import com.github.miemiedev.mybatis.paginator.domain.PageBounds;
import com.github.miemiedev.mybatis.paginator.domain.PageList;
import com.rsclouds.base.SimpleListObject;
import com.rsclouds.base.SimpleNetObject;
import com.rsclouds.util.DateUtil;
import com.rsclouds.util.StringTool;
import com.sslkg.utils.SpringUtils;
import com.youxin.ecommerce.service.IProductService;
import com.youxin.pay.service.ChargeorderService;
import com.youxin.pay.utils.AlipayCore;
import com.youxin.ymall.configs.ReturnCode;
import com.youxin.ymall.dao.*;
import com.youxin.ymall.domain.AppUserInfo;
import com.youxin.ymall.domain.MsgCenterForUser;
import com.youxin.ymall.entity.*;
import com.youxin.ymall.service.AppUserService;
import com.youxin.ymall.service.AppUserServiceWrapper;
import com.youxin.ymall.service.MsggroupService;
import com.youxin.ymall.service.SettingService;
import com.youxin.ymall.utils.MobileLocationUtil;
import com.youxin.ymall.utils.PubFun;
import com.youxin.ymall.utils.SessionUtil;
import com.youxin.ymall.utils.StringTools;
import net.sf.json.JSONObject;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import push.android.AndroidXingeUtil;
import push.ios.IosXingeUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Controller
@RequestMapping("/rest")
public class UserController extends AppBaseController {
    private Logger logger = LoggerFactory.getLogger(UserController.class);


    @Autowired
    private AppUserServiceWrapper appuserServiceWrapper;
    @Autowired
    private RedissonClient redclient;

    @Autowired
    private ChargeorderService chargeorderService;

    @Autowired
    private AppUserMapper appUserMapper;
    @Autowired
    private SettingService settingService;
    @Autowired
    private MsggroupService msggroupService;

    @RequestMapping("/v1/getappuserinfo")
    @ResponseBody
    public SimpleNetObject getAppUserInfo(String userip, HttpSession session) {
        String username = SessionUtil.getCurrentUserName(session);
        AppUser appuser = this.appuserService.getById(username);
        SimpleNetObject sno = new SimpleNetObject();
        if (appuser == null) {
            sno.setMessage("未查询到该用户信息");
            sno.setResult(0);
            return sno;

        }
        AppUserInfo appUserInfo = new AppUserInfo(appuser);
        sno.setResult(1);
        sno.setData(appUserInfo);
        return sno;
    }



    @RequestMapping("/v1/getotherappuserinfo")
    @ResponseBody
    public SimpleNetObject getOtherAppUserInfo(String username, HttpSession session) {

        SimpleNetObject sno = new SimpleNetObject();

        if (StringTool.isNullOrEmpty(username)) {
            sno.setResult(2);
            sno.setMessage("请输入用户名");
            return sno;
        }
        if (null == appuserService.getById(username)) {
            sno.setResult(3);
            sno.setMessage("未找到该用户信息");
            return sno;
        }
        AppUser appuser = this.appuserService.getById(username);
        sno.setResult(1);
        if (StringTool.isNullOrEmpty(appuser.getName())) {
            sno.setData(username);
        } else
            sno.setData(appuser.getName());
        if (0 != appuser.getLocation_id()) {
            sno.setMessage("该用户有工厂信息");
        }
        return sno;
    }

    @Autowired
    private AppUserService appuserService;




    @RequestMapping("/v1/modifypassword")
    @ResponseBody
    public SimpleNetObject modifypassword(String oldpassword, String newpassword, HttpSession session) {
        SimpleNetObject sno = new SimpleNetObject();
        if (StringTool.isNullOrEmpty(oldpassword)) {
            sno.setResult(0);
            sno.setMessage("旧密码不正确");
            return sno;
        }
        if (StringTool.isNullOrEmpty(newpassword)) {
            sno.setResult(0);
            sno.setMessage("新密码不正确");
            return sno;
        }
        String username = SessionUtil.getCurrentUserName(session);
        AppUser appUser = this.appuserService.getById(username);
        String encodePwd = StringTool.Encrypt(oldpassword, null);
        /**8729.83
         * 检查当前库里密码是否有，如果没有需要先去sam系统验证
         */
        if (appUser != null) {
            if (!StringTool.isNullOrEmpty(appUser.getPassword())) {
                if (encodePwd.equalsIgnoreCase(appUser.getPassword())) {

                  //  return  wifiAuthService.resetPassword(username,newpassword);
                   int result = appuserService.modifyPassword(username,newpassword);
                    if (1!=result){
                        return new SimpleNetObject(99,"修改密码失败");
                    }
                    return new SimpleNetObject(1,"修改密码成功");
                } else {
                    sno.setResult(0);
                    sno.setMessage("原密码不正确");
                    return sno;
                }
            }  else {
                sno.setResult(0);
                sno.setMessage("原密码不正确");
                return sno;
            }
        } else {
            sno.setResult(0);
            sno.setMessage("用户不存在无法修改密码");
            return sno;
        }
    }

    /**
     * Description：获取用户消息中心未读数量
     *
     * @param session
     * @return
     */
    @RequestMapping("/v1/getusermsgunreadnum")
    @ResponseBody
    public SimpleNetObject getusermsgunreadnum(HttpSession session) {
        String username = SessionUtil.getCurrentUserName(session);
        return msggroupService.getusermsgunreadnum(username);
    }

    /**
     * Description：将一条或多条通知设置为已读
     *
     * @param groupids 消息的id，必须是逗号分隔
     * @param session
     * @return
     */
    @RequestMapping("/v1/updateusermsgreadbyids")
    @ResponseBody
    public SimpleNetObject updateusermsgreadbyids(String groupids, HttpSession session) {
        String username = SessionUtil.getCurrentUserName(session);
        return msggroupService.updateusermsgreadbyids(username, groupids);
    }

    /**
     * Description：将登录用户数据库全部通知设置为已读
     *
     * @param session
     * @return
     */
    @RequestMapping("/v1/updateallusermsgread")
    @ResponseBody
    public SimpleNetObject updateallusermsgread(HttpSession session) {
        String username = SessionUtil.getCurrentUserName(session);
        return msggroupService.updateallusermsgread(username);
    }

    @RequestMapping("/v1/getusermsg")
    @ResponseBody
    public SimpleListObject<MsgCenterForUser> getmsg(HttpSession session, PageBounds pagebounds) {
        String username = SessionUtil.getCurrentUserName(session);
        SimpleListObject<MsgCenterForUser> slno = new SimpleListObject<MsgCenterForUser>();
        slno.setResult(1);
        PageList<MsgCenterForUser> results = this.msggroupService.getUserMsg(username, null, pagebounds);
        slno.setRows(results);
        slno.setData(results.getPaginator().getTotalCount());
        return slno;
    }



    /**
     * 获取手机号归属地
     *
     * @param notBuyUser
     * @return
     */
    @RequestMapping("/getMobileLocation")
    @ResponseBody
    public SimpleNetObject getMobileLocation(String mobile) {
        return MobileLocationUtil.getMobileLocation(mobile);
    }


    // 测试三方回调订单处理
    @RequestMapping("/payOrderAndBuyPackage")
    @ResponseBody
    public SimpleNetObject payOrderAndBuyPackage(String ordercode) {
        SimpleNetObject sno = new SimpleNetObject();
        try {
            int result = appuserService.payOrderAndBuyPackage(ordercode);
            if (1 == result) {
                return new SimpleNetObject(1, "成功购买");
            }
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        return new SimpleNetObject(99, "购买失败");
    }
}