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

<title>扫码退款</title>

<script src="<%=basePath%>/js/jquery-2.0.0.js"></script>
</head>

<body>
	<fieldset>
		<legend>扫码退款</legend>
		<form action="">
		    <p>商户号:<input type="text" readonly="readonly" value="100510112345708" name="merchantId"></p>
		    <p>订单id:<input type="text" value="<%=System.currentTimeMillis() %>" name="orderId"></p>
		    <p>原交易订单id:<input type="text"  name=origOrderId></p>
		    <p>退款金额:<input type="text" placeholder="单位分" name="tranAmt" value="1" ></p>
		    <p>退款理由:<input type="text"  name="refundReason" ></p>
		    <p>退款时间:<input type="text" readonly="readonly" value="<%=UtilDate.getOrderNum() %>"  name="transTime"></p>
		    <p>类型:
		    	<select name="payType">
		    		<option selected="selected" value="0">支付宝</option>
		    		<option value="1">微信</option>
		    	</select>	
		    </p>
		    <p><input type="button" value="退款" id="create" onclick="refund()"><input type="button" value="查询" onclick="query()"></p>
		</form>
		
	</fieldset>
	<fieldset>
		<legend>退款结果</legend>
		<div>
		<br>
		结果:<p id="result"></p>
	</div>
	</fieldset>
	<fieldset>
		<legend>查询结果</legend>
		结果:<p id="query_result"></p>
		<br> 
	</div>
	</fieldset>
	<script>
	
		//签名
		function sign(method,bean,callback){
			$.post('<%=path%>/pufa/'+method+'.action',{requestData:JSON.stringify(bean)},function(data){
				console.info(data);
				bean.sign=data;		
				callback(bean);
			},'text');
		}
		//创建二维码
		function refund(){
			console.info('创建二维码')
			var formBean={
					merchantId:$('[name="merchantId"]').val(),
					orderId:$('[name="orderId"]').val(),
					origOrderId:$('[name="origOrderId"]').val(),
					tranAmt:$('[name="tranAmt"]').val(),
					refundReason:$('[name="refundReason"]').val(),
					transTime:$('[name="transTime"]').val(),
					payType:$('[name="payType"]').val(),
					};
			sign('refundSign',formBean,function(formBean){
				$.post('<%=path%>/pufa/refund.action',{requestData:JSON.stringify(formBean)},function(data){
					$('#result').html(data);
				},'text');
			})
			
		}
		
		//查询
		function query(){
			console.info('创建二维码')
			var formBean={
					merchantId:$('[name="merchantId"]').val(),
					orderId:$('[name="orderId"]').val(),
					};
			sign('querySign',formBean,function(formBean){
				$.post('<%=path%>/pufa/query.action',{requestData:JSON.stringify(formBean)},function(data){
					console.info(data);
					$('#query_result').html(JSON.stringify(data));
				},'json');
			});
		}
	
	</script>
</body>
</html>
