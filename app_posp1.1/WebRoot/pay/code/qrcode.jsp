<%@page import="xdt.util.UtilDate"%>
<%@page import="xdt.util.DateUtil"%>

<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>

<!DOCTYPE >
<html>
<head>
<base href="<%=basePath%>">

<title>扫码</title>

<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->
<script src="<%=basePath%>/js/jquery-2.0.0.js"></script>
</head>

<body>
	<fieldset>
		<legend>扫码</legend>
		<form action="" id="form">
		    <p>商户号:<input type="text" readonly="readonly" value="100510112345708" name="merchantId"></p>
		    <p>订单id:<input type="text" readonly="readonly" value="<%=System.currentTimeMillis() %>" name="orderId"></p>
		    <p>授权码:<input type="text" placeholder="支付宝条码" name="authCode" > </p>
		    <p>交易金额:<input type="text" placeholder="单位分" name="tranAmt" value="1"></p>
		    <p>交易时间:<input type="text" readonly="readonly" value="<%=UtilDate.getOrderNum() %>" name="transTime"></p>
		    <p>类型:
		    	<select name="payType">
		    		<option selected="selected" value="0">支付宝</option>
		    		<option value="1">微信</option>
		    	</select>	
		    </p>
		    <p><input type="button" value="收款" id="pay"></p>
		</form>
		
	</fieldset>
	<fieldset>
		<legend>收款结果</legend>
		<div>
		结果:<p id="result"></p>
		<br> 
	</div>	
	</fieldset>
<script >
	//签名
	function sign(method,bean,callback){
		$.post('<%=path%>/pufa/'+method+'.action',{requestData:JSON.stringify(bean)},function(data){
			console.info(data);
			bean.sign=data;		
			callback(bean);
		},'text');
	}
	$(function(){
		$('#pay').click(function(){
			console.info('收款')
			var formBean={
				merchantId:$('[name="merchantId"]').val(),
					orderId:$('[name="orderId"]').val(),
					authCode:$('[name="authCode"]').val(),
					tranAmt:$('[name="tranAmt"]').val(),
					transTime:$('[name="transTime"]').val(),
					payType:$('[name="payType"]').val(),
					};
			if(formBean.authCode===''){
				alert('授权码不能为空！')
				return;
			}
			sign('paySign',formBean,function(formBean){
				$.post('<%=path%>/pufa/pay.action',{requestData:JSON.stringify(formBean)},function(data){
					$('#result').html(data);
				},'text')
			})
		
			
		});
		
	})

</script>
</body>
</html>
