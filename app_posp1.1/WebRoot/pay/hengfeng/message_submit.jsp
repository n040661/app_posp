<%@ page language="java" contentType="text/html; charset=UTF-8"
	import="xdt.quickpay.hengfeng.util.*" pageEncoding="UTF-8"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Ryt全渠道PC/WAP跳转银联页面支付产品示例</title>
<script src="<%=basePath%>/js/jquery-2.0.0.js"></script>
</head>
<body>
		<form id="form" action="${pageContext.request.contextPath }/hfquick/hfmessage.action" method="post">
			<table>
				<tr>
					<td>商户号：</td>
					<td><input name="merchantId" type="text"
						value="${temp.merchantId}"></td>
				</tr>
				<tr>
					<td>订单交易时间：</td>
					<td><input name="txnTime" type="text" readonly="readonly"
						value="${temp.txnTime}"></td>
				</tr>
				<tr>
					<td>订单号：</td>
					<td><input name="orderId" type="text" readonly="readonly"
						value="${temp.orderId}"></td>
				</tr>
				<tr>
					<td>交易金额：</td>
					<td><input name="txnAmt" id="txnAmt" type="text"
						value="${temp.txnAmt}"><font color="red">单位:分</font></td>
				</tr>
				<tr>
					<td>Token号：</td>
					<td><input name="token" id="token" type="text"
						value="${temp.token}"></td>
				</tr>
				<tr>
					<td>手机号码：</td>
					<td><input name="phoneNo" id="phoneNo" type="text"
						value="${temp.phoneNo}"></td>
				</tr>
				<tr>
					<td>真实姓名：</td>
					<td><input name="name" id="name" type="text"
						value="${temp.name}"><font color="red"></font></td>
				</tr>
				<tr>
					<td>证件号码：</td>
					<td><input name="certNo" id="certNo" type="text"
						value="${temp.certNo}"></td>
				</tr>
				<tr>
					<td>入账卡号：</td>
					<td><input name="accNo" id="accNo" type="text"
						value="${temp.accNo}"></td>
				</tr>
				<tr>
					<td>用户手续费：</td>
					<td><input name="userfee" id="userfee" type="text" value="${temp.userfee}"></td>
				</tr>
				<tr>
					<td>交易类型：</td>
					<td><input type="text" name="tranTp" value="${temp.tranTp}"></td>
				</tr>
				<tr>
					<td>签名：</td>
					<td><input type="text" name="signmsg" value="${temp.signmsg}"></td>
				</tr>
				<tr>
					<td><input type="submit" value="提交" id="create"/></td>
				</tr>
			</table>
		</form>
</body>
</html>