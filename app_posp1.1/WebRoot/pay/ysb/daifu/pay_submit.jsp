<%@ page language="java" contentType="text/html; charset=UTF-8" import="xdt.quickpay.hengfeng.util.*"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=uft-8">
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
					<td colspan="2" style="text-align: center;color: red;font-size: 24px;">1.1 实时代付接口</td>
				</tr>
				<tr>
					<th>商户编号:</th>
					<td><input type="text" style="width:350px;" name="merchantId" value="${temp.merchantId}"/></td>
				</tr>
				<tr>
					<th>用户姓名:</th>
					<td><input type="text" style="width:350px;" name="name" value="${temp.name}"></td>
				</tr>
				<tr>
					<th>银行卡号:</th>
					<td><input type="text" style="width:350px;" name="cardNo" value="${temp.cardNo}"></td>
				</tr>
				<tr>
					<th>订单编号:</th>
					<td><input type="text" style="width:350px;" name="orderId" value="${temp.orderId}"></td>
				</tr>
				<tr>
					<th>付款目的:</th>
					<td><input type="text" style="width:350px;" name="purpose" value="${temp.purpose}"></td>
				</tr>
				<tr>
					<th>付款金额:</th>
					<td><input type="text" style="width:350px;" name="amount" value="${temp.amount}"></td>
				</tr>
				<tr>
					<td>回调地址：</td>
					<td><input type="text" style="width:350px;" name="responseUrl" value="${temp.responseUrl}"><br/></td>
				</tr>
				<tr>
					<td>签名：</td>
					<td><input type="text" style="width:350px;" name="sign" value="${temp.sign}"><br/></td>
				</tr>
				<tr>
					<td></td>
					<td><input type="button" value="提交支付" id="pay" style="background: #999"> <br/></td>
				</tr>
			</table>
		</form>
	</div>
</div>
</body>
<script type="text/javascript">
$("#pay").click(function(){
	demoOneForm.action='${pageContext.request.contextPath}/ysb/pay.action';
	demoOneForm.submit();
})
</script>
</html>