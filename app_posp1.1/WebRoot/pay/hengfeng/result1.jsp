<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<meta name="viewport" content="width=device-width, initial-scale=1.0, minimum-scale=0.5, maximum-scale=2.0, user-scalable=yes" />
<title>无标题文档</title>
<style type="text/css">
#page {
	width: 500px;
	height: 450px;
}
#page1 {
	font-size: 16px;
	width: 500px;
	height: 50px;
	font-style: normal;
	line-height: 50px;
	margin-left: 20px;
}
#page2 {
	width: 500px;
	color: #f3f3f3;
}
.page3 {
	font-size: 16px;
	font-style: normal;
	line-height: 50px;
	text-align: right;
	float: right;
	border: hidden;
	margin-right: 50px;
}
#page4 {
	font-size: 16px;
	width: 500px;
	height: 100px;
	font-style: normal;
	line-height: 50px;
	margin-left: 20px;
	position:relative;
}
#page4 #page5 {
	background-color: #090;
	font-family: Verdana, Geneva, sans-serif;
	height: 50px;
	width: 90px;
	font-size: 16px;
	text-align: center;
	position:absolute;
	margin-right: 50px;
	-moz-border-radius: 10px;
    -webkit-border-radius: 10px;
    border-radius: 10px;
	right:0;
	bottom:0;
}
.boddle {
	background-color: #0093dd;
	font-size: 16px;
	width: 450px;
	height: 50px;
	margin-right: 25px;
	text-align: center;
	-moz-border-radius: 10px;
	-webkit-border-radius: 10px;
	font-style: normal;
	line-height: 50px;
	margin-left: 25px;
	
}
</style>
 <script type="text/javascript">
            function test1 (obj) {
                // var username = document.getElementById('username');
                if (obj.value == obj.defaultValue) {
                    obj.value = "";
                }
            }
            function test2 (obj) {
                // var username = document.getElementById('username');
                if (obj.value == "") {
                    obj.value = obj.defaultValue;
                }
            }
        </script>
</head>

<body>
<div ><img src="${pageContext.request.contextPath }/images/8.png" width="500" height="60" /></div>
<div><img src="${pageContext.request.contextPath }/images/7.png" width="500" height="40" /></div>
<div id="page">
<div id="page1">商户号 <input class="page3"  type="text" name="merchantId" id="merchantId" value="123456"/></div>
<hr id="page2"/>
<div id="page1">金额
  <input  class="page3" style="color:#F00" type="text" name="txnAmt" id="txnAmt" value="520.00"/>
</div>
<hr id="page2"/>
<div id="page1">订单号 <input class="page3" type="text" name="orderId" id="orderId" value="123456"/></div>
<hr id="page2"/>
<div id="page1">姓名<input class="page3" type="text" name="orderId" id="orderId" value="123456"/></div>
<hr id="page2"/>
<div id="page1">证件号 <input class="page3" type="text" name="orderId" id="orderId" value="123456"/></div>
<hr id="page2"/>
<div id="page1">卡号 <input class="page3" type="text" name="orderId" id="orderId" value="123456"/></div>
<hr id="page2"/>
<div id="page1">预留手机号 <input class="page3"  type="text" name="phoneNo" id="phoneNo" value="18902195076"/></div>
<hr id="page2"/>
<div id="page4">短信验证码 <input class="page3"  type="text" name="smsCode" id="smsCode" style="color: #CCC;" value="请输入6位短信验证码" onfocus="test1(this)" onblur="test2(this)"/>
   <div id="page5"><font color="#FFFFFF">免费获取</font></div>
</div>
<hr id="page2"/>
<div id="page4">图片校验码 <input class="page3"  type="text" name="imgCode" id="imgCode" style="color: #CCC;" value="请输入下方的校验码" onfocus="test1(this)" onblur="test2(this)"/></div>
<div class="boddle"><font color="#FFFFFF">支付</font></div>
</div>



</body>
</html>

