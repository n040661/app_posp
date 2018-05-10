<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>Insert title here</title>
<!-- 引入JQuery -->
<script type="text/javascript" src="${pageContext.request.contextPath}/jquery-easyui-1.5.2/jquery-1.8.3.min.js"></script>
<link href="${pageContext.request.contextPath}/css/404/index.css"  type="text/css" rel="stylesheet"/>
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
					<td colspan="2" style="text-align: center;color: red;font-size: 24px;">1.3订单状态查询接口</td>
				</tr>
				<tr>
					<th>商户编号：</th>
					<td><input type="text" style="width:350px;" name="merchantId" value="10012012745"></td>
				</tr>
				<tr>
					<th>订单号：</th>
					<td><input type="text" style="width:350px;" name="orderId" value="20180108160455977"/></td>
				</tr>
				<tr>
					<th></th>
					<td><input type="button" value="提交查询" id="pay" style="background: #999"></td>
				</tr>
			</table>
		</form>
	</div>
</div>
</body>
<script type="text/javascript">
$("#pay").click(function(){
	demoOneForm.action='${pageContext.request.contextPath}/dk/queryOrderStatusdaiKou.action';
	demoOneForm.submit();
})
</script>
</html>