<%@ page language="java" contentType="text/html; charset=UTF-8"
	import="xdt.quickpay.hengfeng.util.*" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Ryt全渠道PC/WAP跳转银联页面支付产品示例</title>

</head>
<body>
	<form id="form"
		action="${pageContext.request.contextPath }/hfquick/hfsignForWap.action"
		method="post">
		<center>
			<table>
				<tr>
					<td>商户号：</td>
					<td><input type="text" name="merchantId" value="10012014011"></td>
				</tr>
				<tr>
					<td>订单交易时间：</td>
					<td><input type="text" name="txnTime"
						value="<%=HFUtil.dateTime()%>"></td>
				</tr>
				<tr>
					<td>订单号：</td>
					<td><input type="text" name="orderId"
						value="<%=HFUtil.HFrandomOrder()%>"></td>
				</tr>
				<tr>
					<td>卡号：</td>
					<td><input type="text" name="accNo"
						value="6212260302026649095"></td>
				</tr>
				<tr>
					<td>前台通知地址：</td>
					<td><input type="text" name="frontUrl"
						value="http:60.28.24.164:8101/app_posp/hfquick/hfbgPayResult.action"></td>
				</tr>
				<tr>
					<td>后台通知地址：</td>
					<td><input type="text" name="backUrl"
						value="http:60.28.24.164:8101/app_posp/hfquick/hfbgPayResult.action"></td>
				</tr>
				<tr>
					<td>交易类型:</td>
					<td><select name="tranTp">
							<option selected="selected" value="1">T1</option>
							<option value="0">T0</option>
					</select></td>
				</tr>
				<tr>
					<td colspan="2" align="center"><input type="submit"
						name="confirm" value="提交"></td>
				</tr>
			</table>
		</center>
	</form>
</body>
</html>