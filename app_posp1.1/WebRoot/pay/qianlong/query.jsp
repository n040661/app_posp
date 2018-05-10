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

<title>查询订单</title>

<script src="<%=basePath%>/js/jquery-2.0.0.js"></script>
</head>

<body>
	<fieldset>
		<legend>查询订单</legend>
		<form action="">
		    <p>商户号:<input type="text" value="100510112345708" name="merchartId"></p>
		    <p>收款订单号:<input type="text" name="orderNo"></p>
		    <p><input type="button" value="查询" id="create" onclick="query()"></p>
		</form>
		
	</fieldset>
	<fieldset>
		<br>
		结果:<p id="result"></p>
	</div>
	</fieldset>
	<script>
	
		//签名
		function sign(method,bean,callback){
			$.post('<%=path%>/ql/'+method+'.action',{requestData:JSON.stringify(bean)},function(data){
				console.info(data);
				bean.sign=data;		
				callback(bean);
			},'text');
		}
		
		//查询
		function query(){
			console.info('创建二维码')
			var formBean={
				    merchartId:$('[name="merchartId"]').val(),
					orderNo:$('[name="orderNo"]').val(),
					};
			sign('paySign',formBean,function(formBean){
				$.post('<%=path%>/ql/query.action',{requestData:JSON.stringify(formBean)},function(data){
					console.info(data);
					$('#result').html(JSON.stringify(data));
				},'json');
			});
		}
	
	</script>
</body>
</html>
