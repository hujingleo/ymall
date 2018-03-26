function getById(id){
	return document.getElementById(id);
}
function getParams(obj){
	var str=[];
	for(var prop in obj){
		str.push(prop+'='+obj[prop]);
	}
	return str.join('&');
}
function trim(stringToTrim){
	if(undefined == stringToTrim || null == stringToTrim)
	{
		return "";
	}
	return stringToTrim.replace(/^\s+|\s+$/g,"");
}
function isEmpty(value,allowEmptyString){
	return (value === null) || (value === undefined) || (!allowEmptyString ? value === '' : false) || (isArray(value) && value.length === 0);
}

function isArray(value){
	return Object.prototype.toString.call(value) === '[object Array]';
}
/**
 * 得到ajax对象
 */
function getajaxHttp() {
    var xmlHttp;
    try {
        // Firefox, Opera 8.0+, Safari
        xmlHttp = new XMLHttpRequest();
        } catch (e) {
            // Internet Explorer
            try {
                xmlHttp = new ActiveXObject("Msxml2.XMLHTTP");
            } catch (e) {
            try {
                xmlHttp = new ActiveXObject("Microsoft.XMLHTTP");
            } catch (e) {
                alert("您的浏览器不支持AJAX！");
                return false;
            }
        }
    }
    return xmlHttp;
}
/**
 * 发送ajax请求
 * url--url
 * methodtype(post/get)
 * con (true(异步)|false(同步))
 * parameter(参数)
 * functionName(回调方法名，不需要引号,这里只有成功的时候才调用)
 * (注意：这方法有二个参数，一个就是xmlhttp,一个就是要处理的对象)
 * obj需要到回调方法中处理的对象
 */
function ajaxrequest(url,methodtype,con,parameter,functionName,obj){
    var xmlhttp=getajaxHttp();
  
    xmlhttp.onreadystatechange=function(){
        if(xmlhttp.readyState==4){
            //HTTP响应已经完全接收才调用
            if (xmlhttp.status == 200) {
            	functionName(xmlhttp,obj);
            }
        }
    };
    xmlhttp.open(methodtype,url,con);
    xmlhttp.setRequestHeader("Content-Type",
    "application/x-www-form-urlencoded; charset=UTF-8");
    xmlhttp.send(parameter);
}

function hasClass(ele,cls) { 
return ele.className.match(new RegExp('(\\s|^)'+cls+'(\\s|$)')); 
} 

function addClass(ele,cls) { 
	if (!hasClass(ele,cls)) ele.className += " "+cls; 
} 

function removeClass(ele,cls) { 
	if (hasClass(ele,cls)) { 
	var reg = new RegExp('(\\s|^)'+cls+'(\\s|$)'); 
	ele.className=ele.className.replace(reg,' '); 
	} 
} 


function addEvent(element, type, handler) {
	//为每一个事件处理函数分派一个唯一的ID
	if (!handler.$$guid) handler.$$guid = addEvent.guid++;
	//为元素的事件类型创建一个哈希表
	if (!element.events) element.events = {};
	//为每一个"元素/事件"对创建一个事件处理程序的哈希表
	var handlers = element.events[type];
	if (!handlers) {
		handlers = element.events[type] = {};
		//存储存在的事件处理函数(如果有)
		if (element["on" + type]) {
			handlers[0] = element["on" + type];
		}
	}
	//将事件处理函数存入哈希表
	handlers[handler.$$guid] = handler;
	//指派一个全局的事件处理函数来做所有的工作
	element["on" + type] = handleEvent;
};
//用来创建唯一的ID的计数器
addEvent.guid = 1;
function removeEvent(element, type, handler) {
	//从哈希表中删除事件处理函数
	if (element.events && element.events[type]) {
	delete element.events[type][handler.$$guid];
	}
};
function handleEvent(event) {
	var returnValue = true;
	//抓获事件对象(IE使用全局事件对象)
	event = event || fixEvent(window.event);
	//取得事件处理函数的哈希表的引用
	var handlers = this.events[event.type];
	//执行每一个处理函数
	for (var i in handlers) {
	this.$$handleEvent = handlers[i];
	if (this.$$handleEvent(event) === false) {
	returnValue = false;
	}
	}
	return returnValue;
};
//为IE的事件对象添加一些“缺失的”函数
function fixEvent(event) {
	//添加标准的W3C方法
	event.preventDefault = fixEvent.preventDefault;
	event.stopPropagation = fixEvent.stopPropagation;
	return event;
};
fixEvent.preventDefault = function() {
	this.returnValue = false;
};
fixEvent.stopPropagation = function() {
	this.cancelBubble = true;
};

