<%@page import="xdt.util.UtilDate"%>
<%@ page contentType="text/html;charset=UTF-8"
	import="xdt.quickpay.hengfeng.util.*" language="java"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>测试页面</title>
</head>

<body>
	<form id="form" action="${pageContext.request.contextPath }/pay/gateway_submit.jsp" >
		<table>
			<tr>
				<td>服务类型：</td>
				<td><input type="text" name="service" value="cj007"></td>
			</tr>
			<tr>
				<td>商户号：</td>
				<td><input type="text" name="merchantCode"
					value="100510112345708"></td>
			</tr>
			<tr>
				<td>订单号:
				</td>
				<td><input type="text" name="orderNum"
					value="<%=System.currentTimeMillis()%>">(<font color="red">查询时候添加自己的订单号，下面都不要填</font>)</td>
			</tr>
			<tr>
				<td>前台通知页面地址：</td>
				<td><input type="text" name="returnUrl"
					value="http://60.28.24.164:8102/app_posp/test/qrcode/interface.action"></td>
			</tr>
			<tr>
				<td>银行编码：</td>
				<td><input type="text" name="bankCode" value="01030000"></td>
			</tr>
			<tr>
				<td>异步通知url：</td>
				<td><input type="text" name="notifyUrl"
					value="http://60.28.24.164:8102/app_posp/test/qrcode/interface.action"></td>
			</tr>
			<tr>
				<td>商品名称：</td>
				<td><input type="text" name="commodityName" value="担担面"></td>
			</tr>
			<tr>
				<td>金额：</td>
				<td><input type="text" name="transMoney" value="1000"></td>
			</tr>
			<tr>
				<td colspan="2"><input type="submit"
					value="确定"></td>
			</tr>
		</table>
	</form>
</body>
</html>
