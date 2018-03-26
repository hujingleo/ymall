package com.youxin.ymall.controllers;

import com.rsclouds.base.SimpleListObject;
import com.rsclouds.base.SimpleNetObject;
import com.youxin.ymall.domain.AppUserOrder;
import com.youxin.ymall.entity.MallOrder;
import com.youxin.ymall.entity.Usertransaction;
import com.youxin.ymall.service.MallOrderService;
import com.youxin.ymall.service.UsertransactionService;
import com.youxin.ymall.utils.SessionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;


@RequestMapping("/rest/v1")
@Controller
public class MallOrderController extends AppBaseController {

	@Autowired
	private MallOrderService mallOrderService;
	
	@Autowired
	private UsertransactionService usertransactionService;
	/**
	 * 获得用户的消费记录
	 * @param session
	 * @param year
	 * @param month
	 * @return
	 */
	@RequestMapping("/getConsumeList")
	@ResponseBody
	public SimpleListObject<AppUserOrder> getAppUserOrder(HttpSession session
			, Integer year, Integer month){
		String username= SessionUtil.getCurrentUserName(session);
		SimpleListObject<AppUserOrder> slno=new SimpleListObject<AppUserOrder>();
		if(year==null||month==null){
			slno.setResult(99);
			slno.setMessage("请传递year和month参数");
			return slno;
		}
		if(month<=0||month>12||year<2014){
			slno.setResult(2);
			slno.setMessage("year或者month参数有误");
			return slno;
		}
		slno= this.mallOrderService.getAppUserOrder(username, year, month);
		
		slno.setResult(1);
		return slno;
	}
	
	/**
	 * 用户获得其充值记录
	 * @param session
	 * @param year
	 * @param month
	 * @return
	 */
	@RequestMapping("/getChargeList")
	@ResponseBody
	public SimpleListObject<Usertransaction> getChargeList(HttpSession session
			, Integer year, Integer month){
		String username= SessionUtil.getCurrentUserName(session);
		
		return this.usertransactionService.getChargeList(username, year, month);
		
	}
	@RequestMapping("/getorderstatusbyordercode")
	@ResponseBody
	public SimpleNetObject getOrderStatus(String ordercode){
		SimpleNetObject sno=new SimpleNetObject();
		MallOrder order= this.mallOrderService.getByOrderCode(ordercode);
		if(order==null){
			sno.setResult(2);
			sno.setMessage("未找到该订单号，查询失败");
			return sno;
		}
		else{
			sno.setResult(1);
			sno.setData(order.getOrderstatus());
			if(order.getCouponid()!= null && order.getCouponid() > 0 ) sno.setExtraData("sendMiaoshaVipCoupon");
			return sno;
		}
		
	}
}
