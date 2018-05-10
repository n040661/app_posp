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
		action="${pageContext.request.contextPath }/hfquick/hfsignconsume.action"
		method="post">
		<table>
			<tr>
				<td>商户号：</td>
				<td><input name="merchantId" type="text" value="${merchantId}"></td>
			</tr>
			<tr>
				<td>订单金额：</td>
				<td><input name="txnAmt" type="text" readonly="readonly"
					value="${txnAmt}"></td>
			</tr>
			<tr>
				<td>订单号：</td>
				<td><input name="orderId" type="text" readonly="readonly"
					value="${orderId}"></td>
			</tr>
			<tr>
				<td>交易时间：</td>
				<td><input name="txnTime" type="text" readonly="readonly"
					value="${txnTime}"></td>
			</tr>
			<tr>
				<td>令牌：</td>
				<td><input name="token" id="token" type="text" value="${token}"><font
					color="red"></font></td>
			</tr>
			<tr>
				<td>交易类型:</td>
				<td><select name="tranTp">
						<option selected="selected" value="1">T1</option>
						<option value="0">T0</option>
				</select></td>
			</tr>
			<tr>
				<td>后台通知地址：</td>
				<td><input name="backUrl" id="backUrl" type="text"
					value="http:60.28.24.164:8102/app_posp/hfquick/hfbgPayResult.action"></td>
			</tr>
			<tr>
				<td>短信码：</td>
				<td><input name="smsCode" id="smsCode" type="text" value=""></td>
			</tr>
			<tr>
				<td><input name="button" type="submit" value="提交" class="btn" /></td>
			</tr>
		</table>
	</form>
</body>
</html>