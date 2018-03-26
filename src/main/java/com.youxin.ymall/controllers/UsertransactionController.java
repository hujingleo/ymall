package com.youxin.ymall.controllers;

import com.rsclouds.base.SimpleListObject;
import com.youxin.ymall.domain.BillBO;
import com.youxin.ymall.service.UsertransactionService;
import com.youxin.ymall.utils.SessionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.List;


@Controller
@RequestMapping("/rest/v1/usertransaction")
public class UsertransactionController extends AppBaseController {

	
	@Autowired
	private UsertransactionService usertransactionService;
	@RequestMapping("/list")
	@ResponseBody
	public SimpleListObject<BillBO> getAppUserOrder(HttpSession session
			, Integer year, Integer month){
		String username= SessionUtil.getCurrentUserName(session);
		SimpleListObject<BillBO> slno=new SimpleListObject<BillBO>();
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
		List<BillBO> lstRows= this.usertransactionService.list(username,year, month);
		slno.setRows(lstRows);
		slno.setResult(1);
		return slno;
	}
}
