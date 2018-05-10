<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
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
					<td colspan="2" style="text-align: center;color: red;font-size: 24px;">1.4子协议号查询接口</td>
				</tr>
				<tr>
					<th>银行卡号：</th>
					<td><input type="text" style="width:350px;" name="cardNo" value="${temp.cardNo}"/></td>
				</tr>
				<tr>
					<th>用户姓名：</th>
					<td><input type="text" style="width:350px;" name="name" value="${temp.name}"><br/></td>
				</tr>
				<tr>
					<th>身份证号：</th>
					<td><input type="text" style="width:350px;" name="idCardNo" value="${temp.idCardNo}"></td>
				</tr>
				<tr>
					<th>商户编号：</th>
					<td><input type="text" style="width:350px;" name="merchantId" value="${temp.merchantId}"></td>
				</tr>
		        <tr>
					<th>签名：</th>
					<td><input type="text" style="width:350px;" name="sign" value="${temp.sign}"></td>
				</tr>
				<tr>
					<th></th>
					<td><input type="button" value="提交查询" id="tj" style="background: #999"></td>
				</tr>
			</table>
		</form>
	</div>
</div>
</body>
<script type="text/javascript">
$("#tj").click(function(){
	demoOneForm.action='${pageContext.request.contextPath}/ysb/querySubContractId.action';
	demoOneForm.submit();
})
</script>
</html>