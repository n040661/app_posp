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
		    <p>商户号:<input type="text" readonly="readonly" value="100510112345708" name="merId"></p>
		    <p>订单id:<input type="text"  name="transactionId" ></p>
		    <p><input type="button" value="查询" id="create" onclick="query()">
		</form>
		
	</fieldset>
	<fieldset>
		<br>
		查询结果:<p id="result"></p>
	</div>
	</fieldset>
	<script>
		//查询
		function query(){
			var formBean={
					merId:$('[name="merId"]').val(),
					transactionId:$('[name="transactionId"]').val(),
					};
			$.post('<%=path%>/cj/quick/query.action',formBean,function(data){
				
				$('#result').html(data);
			},'text');
		}
	
	</script>
</body>
</html>
