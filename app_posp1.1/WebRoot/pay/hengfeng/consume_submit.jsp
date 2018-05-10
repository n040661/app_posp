<%@ page language="java" contentType="text/html; charset=UTF-8"
	import="xdt.quickpay.hengfeng.util.*" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Ryt全渠道PC/WAP跳转银联页面支付产品示例</title>
</head>
<body>

	<form
		action="${pageContext.request.contextPath }/hfquick/hfconsume.action"
		method="post">
		<table>
			<tr>
				<td>商户号：</td>
				<td><input name="merchantId" type="text"
					value="${temp.merchantId}"></td>
			</tr>
			<tr>
				<td>订单金额：</td>
				<td><input name="txnAmt" type="text" readonly="readonly"
					value="${temp.txnAmt}"></td>
			</tr>
			<tr>
				<td>订单号：</td>
				<td><input name="orderId" type="text" readonly="readonly"
					value="${temp.orderId}"></td>
			</tr>
			<tr>
				<td>交易时间：</td>
				<td><input name="txnTime" type="text" readonly="readonly"
					value="${temp.txnTime}"></td>
			</tr>
			<tr>
				<td>令牌：</td>
				<td><input name="token" id="token" type="text"
					value="${temp.token}"><font color="red"></font></td>
			</tr>
			<tr>
				<td>后台通知地址：</td>
				<td><input name="backUrl" id="backUrl" type="text"
					value="${temp.backUrl}"></td>
			</tr>
			<tr>
				<td>短信码：</td>
				<td><input name="smsCode" id="smsCode" type="text"
					value="${temp.smsCode}"></td>
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
				<td><input name="button" type="submit" value="提交" class="btn" /></td>
			</tr>
		</table>
	</form>
</body>
</html>