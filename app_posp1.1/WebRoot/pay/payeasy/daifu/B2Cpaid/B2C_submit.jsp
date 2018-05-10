<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<title>B2C批量代付</title>
<script src="<%=basePath%>/js/jquery-2.0.0.js"></script>
</head>
<body>
	<fieldset>
		<legend>代付</legend>
		<form action="">
			<p>
				商户号:<input type="text" id="merchantId" name="merchantId"
					value="${temp.merchantId}">
			</p>
			<p>
				代付数据:<input type="text" id="v_data" name="v_data"
					value="${temp.v_data}" size="135">
			</p>
			<p>
				签名:<input type="text" id="v_mac" name="v_mac" value="${temp.v_mac}"
					size="50">
			</p>
			<p>
				<input type="button" value="代付" id="create" onclick="createCode()">
			</p>
		</form>

	</fieldset>
	<fieldset>
		<legend>生成结果</legend>
		<div>
			<br /> 提示 :
			<p>生成一次刷新一次</p>
			结果:
			<p id="result"></p>
			<br />
		</div>
	</fieldset>
	<script>
		//创建二维码
		function createCode() {

			var merchantId = $("#merchantId").val();
			var v_data = $("#v_data").val();
			var v_mac = $("#v_mac").val();
			$
					.ajax({
						type : "post",
						dataType : "json",
						data : {
							"merchantId" : merchantId,
							"v_data" : v_data,
							"v_mac" : v_mac
						},
						url : "${pageContext.request.contextPath}/payeasy/select.action",
						success : function(data) {
							$('#result').html(JSON.stringify(data));
						}
					})

		}
	</script>
</body>
</html>

