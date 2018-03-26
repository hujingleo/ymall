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

//登录
function loginBtnClick() {
		var user = document.getElementById("user");
		var passw = document.getElementById("passw");
		var errorMsg = $("#errorMsg");
		if (user.value == "" || user.value == "请输入您的账号") {
			user.value = "请输入您的账号";
			errorMsg.html('请输入您的账号');
		} else if (passw.value == "" || passw.value == "请输入您的密码") {
			passw.type = "text";
			passw.value = "请输入您的密码";
			errorMsg.html('请输入您的密码');;
		} else {
			$.cookie('username',user.value);
			$.cookie('password',passw.value);
			$('#loginFrm').submit();
		}
	}

//一键下载App
function shortcutClick() {
		var con = confirm("您即将下载有啊一键上网APP,下载安装APP,一键上网超方便!");
		if(con==true){
			window.open('http://183.238.132.72:8888/your-wifi_install_packet.apk');
		}else{
			alert("取消下载");
		}
	}
	//赚金币

function shortcutBJClick() {
		var con = confirm("您即将下载有啊WIFI赚钱宝APP,通过有啊赚钱宝每天赚金币,WIFI就可以免费用!");
		if(con==true){
			window.open('http://192.168.3.2:8081/download/youa.apk');
		}else{
			alert("取消下载");
		}
	}
	/*
	 * end------登录界面
	 */
	/**
	 * start -----注册
	 */
	//文本输入框控制

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
	$.ajax();
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
		$.getJSON('./registeruser',{checkcode:check.value,phonenum:tel.value,password:setpass.value},function(data){
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
	$.getJSON('./sendmsgcode?phonenum='+tel,function(data){
			if(data.result==1){
				alert('短信发送发送成功');
			}
			else{
			
			}
				
		}
	);
}
	/**
	 * end -----注册
	 */
	/**
	 * start---登录成功
	 */
	//变更套餐







$(document).ready(function(){
	$('#btnGetCheck').on('click',getCheck);
	$('#user').val($.cookie('username'));
	$('#passw').val($.cookie('password'));
});