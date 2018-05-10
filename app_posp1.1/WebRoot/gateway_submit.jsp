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
	<form id="form" action="${pageContext.request.contextPath }/gateWay/way.action" method="post" >
		<table>
		    <tr>
				<td>版本号：</td>
				<td><input type="text" name="v_version" value="${temp.v_version}"></td>
			</tr>
			<tr>
				<td>商户号：</td>
				<td><input type="text" name="v_mid" value="${temp.v_mid}"></td>
			</tr>
			<tr>
				<td>订单号：</td>
				<td><input type="text" name="v_oid"
					value="${temp.v_oid}"></td>
			</tr>
			<tr>
				<td>交易金额:
				</td>
				<td><input type="text" name="v_txnAmt"
					value="${temp.v_txnAmt}"></td>
			</tr>
						<tr>
				<td>异步通知url：</td>
				<td><input type="text" name="v_notify_url"
					value="${temp.v_notify_url}"></td>
			</tr>
			<tr>
				<td>前台通知页面地址：</td>
				<td><input type="text" name="v_url"
					value="${temp.v_url}"></td>
			</tr>
						<tr>
				<td>错误页面：</td>
				<td><input type="text" name="v_errorUrl" value="${temp.v_errorUrl}"></td>
			</tr>
						<tr>
				<td>银行编码：</td>
				<td><input type="text" name="v_bankAddr" value="${temp.v_bankAddr}"></td>
			</tr>
						<tr>
				<td>商品名称：</td>
				<td><input type="text" name="v_productName" value="${temp.v_productName}"></td>
			</tr>
						<tr>
				<td>商品数量：</td>
				<td><input type="text" name="v_productNum" value="${temp.v_productNum}"></td>
			</tr>
						<tr>
				<td>商品描述：</td>
				<td><input type="text" name="v_productDesc" value="${temp.v_productDesc}"></td>
			</tr>
						<tr>
				<td>支付类型：</td>
				<td><input type="text" name="v_cardType" value="${temp.v_cardType}"></td>
			</tr>
						<tr>
				<td>交易时间：</td>
				<td><input type="text" name="v_time" value="${temp.v_time}"></td>
			</tr>


			<tr>
				<td>订单有效时间：</td>
				<td><input type="text" name="v_expire_time" value="${temp.v_expire_time}"></td>
			</tr>
						<tr>
				<td>支付币种：</td>
				<td><input type="text" name="v_currency" value="${temp.v_currency}"></td>
			</tr>
						<tr>
				<td>渠道类型：</td>
				<td><input type="text" name="v_channel" value="${temp.v_channel}"></td>
			</tr>
			<tr>
				<td>附加数据：</td>
				<td><input type="text" name="v_attach" value="${temp.v_attach}"></td>
			</tr>
			<tr>
				<td>支付方式：</td>
				<td><input type="text" name="v_type" value="${temp.v_type}"></td>
			</tr>
			<tr>
				<td>签名：</td>
				<td><input type="text" name="v_sign" value="${temp.v_sign}"></td>
			</tr>
			<tr>
				<td colspan="2"><input type="submit"
					value="确定"></td>
			</tr>
		</table>
	</form>
</body>
</html>
