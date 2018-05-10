<%@page import="xdt.util.UtilDate"%>
<%@page import="xdt.util.DateUtil"%>
<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()
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
		<form id ="from" action="${pageContext.request.contextPath}/live/qrcode/interface.action" method="post">
			<p>
				服务类型：<input type="text" value="cj006" class="service" name="service">
			</p>
			<p>
				商户号:<input type="text" value="100510112345708" name="merchantCode">
			</p>
			<p>
				订单号:<input type="text" value="<%=System.currentTimeMillis()%>" name="orderNum">
			</p>
			<p>
				收款人账户号：<input type="text" value="6228450028016697770" name="bankCard">
			</p>
			<p>
				收款人账户名：<input type="text" value="李娟" name="accountName">
			</p>
			<p>
				收款人账户开户行名称：<input type="text" value="中国农业银行天津广厦支行" name="bankName">
			</p>
			<p>
				收款人账户开户行联行号：<input type="text"  name="bankLinked" value="103110023002">
			</p>
			<p>
				金额：<input type="text" value="500"
					name="transMoney">
			</p>
			<p>
				交易类型D0或T1： <select name="type">
					<option selected="selected" value="0">D0</option>
					<option value="1">T1</option>
				</select>
			</p>
			<p>
				<input type="button" value="生成" id="create" onclick="createCode()"><input
					type="button" value="查询" id="create" onclick="query()">
			</p>
		</form>

	</fieldset>
	<script type="text/javascript">
	
	function createCode(){
		
		$.ajax({
			url:"${pageContext.request.contextPath}/live/qrcode/paySign.action",
			type:"post",
			dataType:"json",
			data:$("input[name]").serialize(),
			success:function(data){
				$("#from").append('<input type="text" name="sign" class="sign" style="display: none" value="'+data.sign+'"><br/>');
				console.info($(".sign").val());
				$("#from").submit();
			}
		});
	}
</script>
</body>
</html>
