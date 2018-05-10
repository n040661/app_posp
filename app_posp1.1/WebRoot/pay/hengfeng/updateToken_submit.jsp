<%@ page language="java" contentType="text/html; charset=UTF-8" import="xdt.quickpay.hengfeng.util.*" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Ryt全渠道PC/WAP跳转银联页面支付产品示例</title>

</head>
<body>
<form id="form"
		action="${pageContext.request.contextPath }/hfquick/hfpay.action"
		method="post">
		<center>
			<table>
				 <tr>
					<td>商户号：</td>
					<td><input type="text" name="merchantId"
						value="${temp.merchantId}"></td>
				</tr>
								<tr>
					<td>订单交易时间：</td>
					<td><input type="text" name="txnTime"
						value="${temp.txnTime}"></td>
				</tr>
				<tr>
					<td>订单号：</td>
					<td><input type="text" name="orderId"
						value="${temp.orderId}"></td>
				</tr>
				<tr>
					<td>卡号：</td>
					<td><input type="text" name="accNo" value="${temp.accNo}"></td>
				</tr>
								<tr>
					<td>前台通知地址：</td>
					<td><input type="text" name="frontUrl" value="${temp.frontUrl}"></td>
				</tr>
								<tr>
					<td>后台通知地址：</td>
					<td><input type="text" name="backUrl" value="${temp.backUrl}"></td>
				</tr>
							<tr>
					<td>签名：</td>
					<td><input type="text" name="signmsg"
						 value="${temp.signmsg}"></td>
				</tr>
				<tr>
					<td colspan="2" align="center"><input type="submit"
						name="confirm" value="支付"></td>
				</tr>
			</table>
		</center>
	</form>
</body>
</html>