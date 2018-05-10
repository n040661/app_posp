<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
<!-- 引入JQuery -->
<script type="text/javascript" src="${ctx}/jquery-easyui-1.5.2/jquery-1.8.3.min.js"></script>
<link href="${ctx}/css/index.css"  type="text/css" rel="stylesheet"/>
<style type="text/css">
table th{
	float: right;
}
</style>
</head>
<body>
<div id="total">
	<div id="one">
		<form action="" name="demoOneForm" id="demoOneForm" method="post">
			<table>
				<tr>
					<td colspan="2" style="text-align: center;color: red;font-size: 24px;">1.4商户账户余额及保证金余额查询接口</td>
				</tr>
				<tr>
					<th>商户编号:</th>
					<td><input type="text" style="width:350px;" name="accountId" value="1120170803171302001"/><br/></td>
				</tr>
				<tr>
					<th>Key:</th>
					<td><input type="text" style="width:350px;" name="key" value="123456abc"></td>
				</tr>
				<tr>
					<td></td>
					<td><input type="button" value="提交查询" id="pay" style="background: #999"> <br/></td>
				</tr>
			</table>
		</form>
	</div>
</div>
</body>
<script type="text/javascript">
$("#pay").click(function(){
	demoOneForm.action='${ctx}/demoTest/queryBalance.do';
	demoOneForm.submit();
})
</script>
</html>