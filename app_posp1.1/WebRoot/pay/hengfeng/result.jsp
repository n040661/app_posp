<%@ page language="java" contentType="text/html; charset=UTF-8"
	import="xdt.quickpay.hengfeng.util.*" pageEncoding="UTF-8"%>
	<%@page import="xdt.quickpay.nbs.common.constant.Constant"%>
<%@page import="xdt.quickpay.nbs.common.util.RandomUtil"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
	String orderId=request.getParameter("orderId");
	String order="HF"+orderId;
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" style="overflow-x:hidden;">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1" />
<meta name="description" content="" />
<meta name="renderer" content="webkit" />
<meta name="viewport"
	content="initial-scale=1.0, maximum-scale=1.0, user-scalable=no" />
<meta name="viewport" content="width=device-width, initial-scale=1" />
<title>无标题文档</title>
<script src="${pageContext.request.contextPath}/js/jquery-3.1.1.min.js"></script>
<script src="${pageContext.request.contextPath}/js/jquery-2.0.0.js"></script>
<style type="text/css">
#page {
	width: 100%;
	height: 450px;
}
*{
    margin:0;
    padding:0;
}
#page1 {
	font-size: 16px;
	width: 100%;
	height: 50px;
	font-style: normal;
	line-height: 50px;
	margin-left: 20px;
}

#page2 {
	width: 100%;
	color: #f3f3f3;
}

.page3 {
	font-size: 16px;
	font-style: normal;
	line-height: 50px;
	text-align: right;
	float: right;
	border: hidden;
	margin-right: 30px;
}

#page4 {
	font-size: 16px;
	width: 100%;
	height: 82px;
	font-style: normal;
	line-height: 50px;
	margin-left: 20px;
	position: relative;
}

#page4 #page5 {
	font-family: Verdana, Geneva, sans-serif;
	height: 50px;
	width: 18%;
	font-size: 16px;
	text-align: center;
	position: absolute;
	margin-right: 140px;
	-moz-border-radius: 10px;
	-webkit-border-radius: 10px;
	border-radius: 10px;
	right: 0;
	bottom: 0;
}

#page4 #page6 {
	font-family: Verdana, Geneva, sans-serif;
	height: 50px;
	width: 40%;
	font-size: 16px;
	text-align: center;
	position: absolute;
	margin-right: 30px;
	-moz-border-radius: 10px;
	-webkit-border-radius: 10px;
	border-radius: 10px;
	right: 0;
	bottom: 0;
}

.boddle {
	font-size: 16px;
	width: 90%;
	height: 50px;
	margin-right: 25px;
	text-align: center;
	-moz-border-radius: 10px;
	-webkit-border-radius: 10px;
	font-style: normal;
	line-height: 50px;
	margin-left: 25px;
}
.code{
	width: 170px;
    height: 51px;
    border-radius: 5px;
    color: #fff;
    font-size: 16px;
    border: none;
}
.zhifu{
	width: 100%;
    height: 35px;
    border-radius: 5px;
    color: #fff;
    font-size: 16px;
    border: none;
}
</style>
<script type="text/javascript">
	function test1(obj) {
		// var username = document.getElementById('username');
		if (obj.value == obj.defaultValue) {
			obj.value = "";
		}
	}
	function test2(obj) {
		// var username = document.getElementById('username');
		if (obj.value == "") {
			obj.value = obj.defaultValue;
		}
	}
	function myReload() {  
	    document.getElementById("imgObj").src = document  
	            .getElementById("imgObj").src  
	            + "?nocache=" + new Date().getTime();  
	}
	
	function zhifu(){
		var start=0;
		 var txnAmt =$("#txnAmt").val();
			var orderId =$("#orderId").val();
			var phoneNo =$("#phoneNo").val();
			var txnTime =$("#txnTime").val();
			var token =$("#token").val();
			var tranTp =$("#tranTp").val(); 
			var backUrl =$("#backUrl").val(); 
			var smsCode =$("#smsCode").val();
			var data1={"merchantId":merchantId,"txnAmt":txnAmt,"orderId":orderId,"phoneNo":phoneNo,"txnTime":txnTime,"token":token,"tranTp":tranTp,"backUrl":backUrl,"smsCode":smsCode};
			
		 var  checkcode=$("#imgCode").val();
		 if(checkcode=='图片验证码'){
			 alert("请输入验证码");
		 }else{
			 $.ajax({
					url:"${pageContext.request.contextPath}/verifycode/vifyty.action",
					type:"post",
					data:{"checkcode":checkcode},
					success:function(data){
						console.info(data);
						$(".tishi").text(data);
						if(data=="验证成功"){
							$.ajax({
								url:"${pageContext.request.contextPath}/hfquick/hfsignconsume.action",
								data:"psot",
								data:data1,
								success:function(data){
									console.info(data);
									if(data!="签名错误"){
										data2={"merchantId":merchantId,"txnAmt":txnAmt,"orderId":orderId,"phoneNo":phoneNo,"txnTime":txnTime,"token":token,"tranTp":tranTp,"signmsg":data,"backUrl":backUrl,"smsCode":smsCode};
										$.ajax({
											type : "post",
											url : "${pageContext.request.contextPath}/hfquick/hfconsume.action",
											data:data2,
											success : function(data) {
												
											}
										}); 
									}
									
								}
							});
						}
					}
				});
			
		 }
		 
		 
	  
	 }
</script>
</head>

<body style="margin:0;padding:0;">
<div style="margin:0;padding:0;overflow-x:hidden;">
	<div style="width: 100%;height: 2.5rem;">
		<img src="${pageContext.request.contextPath }/images/8.png" width="100%" height="100%" />
	</div>
	<div>
		<img src="${pageContext.request.contextPath }/images/7.png"
			width="100%" height="40" />
	</div>
	<div id="page">
		<div id="page1">
			商户号 <input class="page3" type="text"  name="merchantId"
				id="merchantId" value="<%=request.getParameter("merid")%>" />
		</div>
		<hr id="page2" />
		<div id="page1">
			金额 <input class="page3" style="color: #F00" type="text"  name="txnAmt" id="txnAmt"/>
		</div>
		<hr id="page2" />
		<div id="page1">
			订单号 <input class="page3" type="text" name="orderId" id="orderId"
				value="<%=order%>" />
		</div>
		<hr id="page2" />
		<div id="page1">
			预留手机号 <input class="page3" type="text" name="phoneNo" id="phoneNo"/>
		</div>
		<hr id="page2" />
		<div id="page4">
			短信验证码 <input class="page3" type="text" name="smsCode" id="smsCode"
				style="color: #CCC;" value="请输入6位短信验证码" onfocus="test1(this)"
				onblur="test2(this)" />
			<div id="page5">
				 <button onclick="time()" class="code" style="cursor:pointer;background-color: green;height: 30px;margin-top: 30.5%;">免费获取</button>
			</div>
		</div>
		<hr id="page2" />
		  <div id="page4">
			图片校验码 <input class="page3" type="text" name="imgCode" id="imgCode"
				style="color: #CCC;" placeholder="图片验证码" value="" onfocus="test1(this)"
				onblur="test2(this)" />
			<div id="page6">
				<span class="input-group-addon"><img id="imgObj" onclick="myReload()" alt="点击刷新 " src="${pageContext.request.contextPath}/PictureCheckCode" border="0" /></span>
			</div>
		</div> 
		<input name="txnTime" type="hidden" id="txnTime" readonly="readonly"
					value="<%=request.getParameter("txnTime") %>"/>
		<input name="token" id="token" type="hidden"
					value="<%=request.getParameter("token") %>"/>
		<input name="tranTp" id="tranTp" type="hidden"
					value="<%=request.getParameter("tranTp") %>"/>
		<input name="backUrl" id="backUrl" type="hidden"
					value="http://60.28.24.164:8103/app_posp/hfquick/hengfengPayResult.action"/>
		<div class="boddle">
		<input type="button" class="zhifu" onclick="zhifu()" style="cursor:pointer; background-color: blue;" value="支付"/>
			<!-- <button  class="zhifu" onclick="zhifu()" style="cursor:pointer; background-color: blue;">支付</button> -->
		</div>
	</div>
</div>	
<script type="text/javascript">

	 function time() { 
		 var signmsg="";
		 var merchantId =$("#merchantId").val();
		 	var txnAmt =$("#txnAmt").val();
		 	var orderId =$("#orderId").val();
		 	var phoneNo =$("#phoneNo").val();
		 	var txnTime =$("#txnTime").val();
		 	var token =$("#token").val();
		 	var tranTp =$("#tranTp").val();
		 	var backUrl =$("#backUrl").val();
		 	var data1={"merchantId":merchantId,"txnAmt":txnAmt,"orderId":orderId,"phoneNo":phoneNo,"txnTime":txnTime,"token":token,"tranTp":tranTp,"backUrl":backUrl};
		 	var data2;
			console.info('创建二维码');
		    console.info(data1);
				 $.ajax({
						type : "post",
						url : "${pageContext.request.contextPath}/hfquick/hfsignmessage.action",
						data:data1,
						success : function(data) {
							console.info("111:"+data);
							signmsg=signmsg+data;
							console.info("11:"+signmsg);
							data2={"merchantId":merchantId,"txnAmt":txnAmt,"orderId":orderId,"phoneNo":phoneNo,"txnTime":txnTime,"token":token,"tranTp":tranTp,"signmsg":data,"backUrl":backUrl};
							console.info(data2);
							$.ajax({
								type : "post",
								url : "${pageContext.request.contextPath}/hfquick/hfmessage.action",
								data:data2,
								success : function(data) {
									console.info("222");
								}
							})
						}
					});

	 }
	 
</script>


</body>
</html>
