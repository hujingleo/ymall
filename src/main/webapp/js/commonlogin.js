/*
 * start------登录界面
 */
//文本输入框控制

function isEmpty(value,allowEmptyString){
	return (value === null) || (value === undefined) || (!allowEmptyString ? value === '' : false) || (isArray(value) && value.length === 0);
}
function isArray(value){
	return Object.prototype.toString.call(value) === '[object Array]';
}


function findpassword(){
	var returnResult=window.prompt('请输入要找回密码的手机');
	if(isEmpty(returnResult)){
	}
	else{
		if(isPhone(returnResult)){
			$.getJSON('/rest/findpassword',{phone:returnResult},function(data){
				if(data.result==1){
					alert(data.message);
				}
				else{
					alert(data.message);
				}
				
			}
		
			);
		}
		else{
			alert('号码输入有误，请重新输入');
		}
		
	}
}
function sendpwdmsg(){
	var returnResult=$('#findpasswordphone').val();
	if(isEmpty(returnResult)){
		alert('请输入要找回手机');
		return;
	}
	if(isPhone(returnResult)){
		$.getJSON('/rest/findpassword',{phone:returnResult},function(data){
			if(data.result==1){
				alert(data.message);
			}
			else{
				alert(data.message);
			}
			
		}
	
		);
	}
	else{
		alert('号码输入有误，请重新输入');
	}
}

//登录
function loginBtnClick() {
		var user = document.getElementById("username");
		var passw = document.getElementById("password");
		var errorMsg = $("#errormsg");
		if (user.value == "" || user.value == "请输入您的账号") {
			
			errorMsg.html('请输入您的账号');
		} else if (passw.value == "" || passw.value == "请输入您的密码") {
			
			errorMsg.html('请输入您的密码');
		} else {
			errorMsg.html('');
			if($("#remeberme:checked").val()=='on'){
				$.cookie('username',user.value);
				$.cookie('password',passw.value);
			
			}
			else{
				$.cookie('username','');
				$.cookie('password','');
			}
			$('#loginFrm').submit();
		}
	}
function downloadapp(){
	var con = confirm("您即将下载优信无限,通过优信无限每天赚金币,WIFI就可以免费用!");
	
	if(con==true){
		 var isAndroid=navigator.userAgent.match(/android/ig),
		isIos=navigator.userAgent.match(/iphone|ipod/ig),
		isIpad=navigator.userAgent.match(/ipad/ig),
		weixin=navigator.userAgent.match(/MicroMessenger/ig);
		if(isAndroid){
			window.open('http://192.168.3.100/download/newyoua.apk');			
		}
		else if(isIos||isIpad){
			window.open('itms-services://?action=download-manifest&url=https://ios.iyouxin.com:888/ios/manifest.plist');	
		}
		
	}else{
		alert("取消下载");
	}
	
}


function registerShow(index) {
	var tel = document.getElementById("tel");
	var check = document.getElementById("check");
	var setpass = document.getElementById("setpass");
	if (index == 0) {
		if (tel.value == "") {
			tel.type = "text";
//			tel.value = "输入手机号";
		}
	}
	if (index == 1) {
		if (check.value == "") {
			check.type = "text";
//			check.value = "输入验证码";
		}
	}
	if (index == 2) {
		if (setpass.value == "") {
			setpass.type = "text";
//			setpass.value = "设置您的密码";
		}
	}
}

function registerClick(index) {
		var tel = document.getElementById("tel");
		var check = document.getElementById("check");
		var setpass = document.getElementById("setpass");
		if (index == 0) {
			if (tel.value == "输入手机号") {
//				tel.value = "";
				tel.type = "number";
			}
		}
		if (index == 1) {
			if (check.value == "输入验证码") {
//				check.value = "";
				check.type = "number";
			}
		}
		if (index == 2) {
			if (setpass.value == "设置您的密码") {
//				setpass.value = "";
				setpass.type = "password";
			}
		}
		
		
	}
function isPhone(phone){
	return /^(13[0-9]|15[0-9]|17[0-9]|18[0-9])\d{8}$/.test(phone);
}
//注册按钮
function registerBtnClick() {
	var tel = document.getElementById("tel");
	var check = document.getElementById("check");
	var setpass = document.getElementById("setpass");
	var apmac=document.getElementById('apmac');
	var wlanuserip=document.getElementById('wlanuserip');
	var reg = new RegExp("^[0-9]*$");
	if (tel.value == "" || tel.value == "输入手机号") {
		alert("输入手机号");
		return false;
	} else if (/^(13[0-9]|15[0-9]|17[0-9]|18[0-9])\d{8}$/.test(tel.value)==false) {
		alert("输入正确电话号码");
		return false;
	} else if (check.value == "" || check.value == "输入验证码") {
		alert("输入验证码");
		return false;
	} else if (check.value.trim().length !=4) {
		alert("输入正确验证码");
		return false;
	} else if (setpass.value == "" || setpass.value == "设置您的密码") {
		alert("设置您的密码");
		return false;
	} else {
		$.getJSON('/rest/portaladduser',{vcode:check.value
			,username:tel.value
			,mobile:tel.value
			,password:setpass.value
			,regapmac:apmac.value
			,regip:wlanuserip.value
			,devicetype:'portal'},function(data){
			if(data.result==1){
				alert('注册成功');
				window.location='http://www.qq.com';
			}
			else{
				alert(data.message);
			}
		});
	}
}
g_counter=null,g_checkcode=false;
function changeVCodeStatus(){
		$('#btnSendMsg').html('重新发送(60)');
		g_counter=60;
		g_msghandle=setInterval(function(){
			g_counter--;
			$('#btnSendMsg').html('重新发送('+g_counter+')');
			
			if(g_counter<=0){
				clearInterval(g_msghandle);
				$('#btnSendMsg').html('重新发送');
				$('#btnSendMsg').bind('click',sendVCode);
			}
			
		},1000);
	}
function checkMobile(){
	var mobile=$('#tel').val();
	if(isEmpty(mobile)||/^(13[0-9]|15[0-9]|17[0-9]|18[0-9])\d{8}$/.test(mobile)==false){
		showError('mobile','请输入正确的手机号码');
		return false;
	}
	else{
		hideError('mobile');
	}
	return true;
}

//点击获取验证码
function getCheck() {
	var tel = document.getElementById("tel").value;
	if (isEmpty(tel)) {
		alert("输入手机号");
		return false;
	} else if(/^(13[0-9]|15[0-9]|17[0-9]|18[0-9])\d{8}$/.test(tel)==false) {
		alert("输入正确电话号码");
		return false;
	} 
	$('#btnGetCheck').unbind('click',getCheck);
	$('#btnGetCheck').css("font-size", "10px");
	$('#btnGetCheck').html('重新发送(60)');
		g_counter=60;
		g_msghandle=setInterval(function(){
			g_counter--;
			$('#btnGetCheck').html('重新发送('+g_counter+')');
			
			if(g_counter<=0){
				clearInterval(g_msghandle);
				$('#btnGetCheck').html('重新发送');
				$('#btnGetCheck').bind('click',getCheck);
			}
			
		},1000);
	$.getJSON('/rest/sendregmsgforportal?devicetype=pc&mobile='+tel,function(data){
			if(data.result==1){
				alert('短信发送发送成功');
			}
			else{
				
				clearInterval(g_msghandle);
				$('#btnGetCheck').bind('click',getCheck);
				$('#btnGetCheck').css("font-size", "10px");
				$('#btnGetCheck').html('重新发送');
				alert(data.message);
			}
				
		}
	);
}
/**
 * 点击忘记密码
 */
function getforgetpwdcodeclick(){
	var tel = document.getElementById("tel").value;
	if (isEmpty(tel)) {
		alert("输入手机号");
		return false;
	} else if(/^(13[0-9]|15[0-9]|17[0-9]|18[0-9])\d{8}$/.test(tel)==false) {
		alert("输入正确电话号码");
		return false;
	} 
	$('#btnGetCheck').unbind('click',getforgetpwdcodeclick);
	$('#btnGetCheck').css("font-size", "10px");
	$('#btnGetCheck').html('重新发送(300)');
		g_counter=300;
		g_msghandle=setInterval(function(){
			g_counter--;
			$('#btnGetCheck').html('重新发送('+g_counter+')');
			
			if(g_counter<=0){
				clearInterval(g_msghandle);
				$('#btnGetCheck').html('重新发送');
				$('#btnGetCheck').bind('click',getCheck);
			}
			
		},1000);
	$.getJSON('/rest/sendforgetpwdmsg?devicetype=pc&mobile='+tel,function(data){
			if(data.result==1){
				alert('短信发送发送成功');
			}
			else{
			
			}
				
		}
	);
	
}

/**
 * 点击重置密码
 */
function forgetpwdclick(){
	var tel = document.getElementById("tel");
	var check = document.getElementById("check");
	var setpass = document.getElementById("setpass");
	var reg = new RegExp("^[0-9]*$");
	if (tel.value == "" || tel.value == "输入手机号") {
		alert("输入手机号");
		return false;
	} else if (/^(13[0-9]|15[0-9]|17[0-9]|18[0-9])\d{8}$/.test(tel.value)==false) {
		alert("输入正确电话号码");
		return false;
	} else if (check.value == "" || check.value == "输入验证码") {
		alert("输入验证码");
		return false;
	} else if (check.value.trim().length !=4) {
		alert("输入正确验证码");
		return false;
	} else if (setpass.value == "" || setpass.value == "设置您的密码") {
		alert("设置您的密码");
		return false;
	} else {
		$.getJSON('/rest/resetpwdbyvcode',{vcode:check.value,mobile:tel.value,password:setpass.value},function(data){
			if(data.result==1){
				alert('重置密码成功');
				window.location='http://www.qq.com';
			}
			else{
				alert(data.message);
			}
		});
	}
}





//下线按钮
function offlineBtnClick() {
		var con = confirm("您是否退出有啊APP?");
		if (con == true) {
			window.location.href = "login.html";
			//	}else{
			//		alert("取消下载");
		}
	}
	/**
	 *end---登录成功
	 */

