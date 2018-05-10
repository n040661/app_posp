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

<title>生成二维码</title>

<script src="<%=basePath%>/js/jquery-2.0.0.js"></script>
</head>

<body>
	<fieldset>
		<legend>生成二维码</legend>
		<form action="">
		    <p>商户号:<input type="text" value="100120154112461" name="merchantId"></p>
		    <p>订单id:<input type="text" value="" name="orderId"></p>
		    <p>交易金额:<input type="text" placeholder="单位分" name="tranAmt" value="1" ></p>
		    <p>交易时间:<input type="text" readonly="readonly" value="20170111160449"  name="transTime"></p>
		    <p>类型:
		    	<select name="payType">
		    		<option selected="selected" value="0">支付宝</option>
		    		<option value="1">微信</option>
		    	</select>	
		    </p>
		    <p><input type="button" value="生成" id="create" onclick="createCode()"><input type="button" value="查询" id="create" onclick="query()"></p>
		</form>
		
	</fieldset>
	<fieldset>
		<legend>生成结果</legend>
		<div>
		<img alt="二维码" src="" id="qrcode" style="width: 100;height: 100px;">
		<br>
		提示	:<p >生成一次刷新一次</p>
		结果:<p id="result"></p>
		
		<br> 
		<!-- https://qr.alipay.com/bax01348reskltxmpcyf40a2 -->
	</div>
	</fieldset>
	<fieldset>
		<legend>查询结果</legend>
		结果:<p id="query_result"></p>
		
		<br> 
		<!-- https://qr.alipay.com/bax01348reskltxmpcyf40a2 -->
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
		function createCode(){
			console.info('创建二维码')
			var formBean={
					merchantId:$('[name="merchantId"]').val(),
					orderId:$('[name="orderId"]').val(),
					tranAmt:$('[name="tranAmt"]').val(),
					transTime:$('[name="transTime"]').val(),
					payType:$('[name="payType"]').val(),
					};
			sign('paySign',formBean,function(formBean){
				$.post('<%=path%>/pufa/produced/two/dimension.action',{requestData:JSON.stringify(formBean)},function(data){
					console.info(data);
					var url='http://s.jiathis.com/qrcode.php?url='+data.url;
					$('#qrcode').attr('src',url);
					$('#result').html(JSON.stringify(data));
					
				},'json');
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
