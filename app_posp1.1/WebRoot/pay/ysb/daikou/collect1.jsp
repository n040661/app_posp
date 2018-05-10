<%@ page language="java" contentType="text/html; charset=UTF-8" import="xdt.quickpay.hengfeng.util.*"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>Insert title here</title>
<!-- 引入JQuery -->
<script type="text/javascript" src="${pageContext.request.contextPath}/jquery-easyui-1.5.2/jquery-1.8.3.min.js"></script>
<link href="${pageContext.request.contextPath}/css/index.css"  type="text/css" rel="stylesheet"/>
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
					<td colspan="2" style="text-align: center;color: red;font-size: 24px;">1.2 委托代扣接口(子协议号)</td>
				</tr>
				<tr>
					<th>商户编号：</th>
					<td><input type="text" style="width:350px;" name="merchantId" value="10012012745"></td>
				</tr>
				<tr>
					<th>订单号：</th>
					<td><input type="text" style="width:350px;" name="orderId" value="<%=HFUtil.HFrandomOrder()%>"></td>
				</tr>
				<tr>
					<th>子协议编号：</th>
					<td><input type="text" style="width:350px;" name="subContractId" value="6390067"></td>
				</tr>
				<tr>
					<th>扣款目的：</th>
					<td><input type="text" style="width:350px;" name="purpose" value="测试"/></td>
				</tr>
				<tr>
					<th>金额：</th>
					<td><input type="text" style="width:350px;" name="amount" value="1"></td>
				</tr>
			    <tr>
					<th>回调地址：</th>
					<td><input type="text" style="width:350px;" name="responseUrl" value="http://60.28.24.164:8102/app_posp/ysb/bgPayResult.action"></td>
				</tr>
				<tr>
					<th>手机号：</th>
					<td><input type="text" style="width:350px;" name="phoneNo" value="18902195076"></td>
				</tr>
				<tr>
					<td></td>
					<td><input type="button" value="提交支付" id="pay" style="background: #999"></td>
				</tr>
			</table>
		</form>
	</div>
</div>
</body>
<script type="text/javascript">
$("#pay").click(function(){
	demoOneForm.action='${pageContext.request.contextPath}/ysb/hfsignmessage1.action';
	demoOneForm.submit();
})
</script>
</html>