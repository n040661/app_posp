<%@page import="xdt.util.UtilDate"%>
<%@ page contentType="text/html;charset=UTF-8"
	import="xdt.util.UtilDate" language="java"%>
	
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>测试页面</title>
</head>

<body>
	<form id="form" action="${pageContext.request.contextPath }/gateWay/GateWayScan.action" method="post" >
		<table>
			 <tr>
				<td>版本号：</td>
				<td><input type="text" name="v_version" value="1.0.0.0"></td>
			</tr>
		
			<tr>
				<td>商户号：</td>
				<td><input type="text" name="v_mid" value="10032061473"></td>
			</tr>
			<tr>
				<td>订单号：</td>
				<td><input type="text" name="v_oid"
					value="<%=System.currentTimeMillis()%>"></td>
			</tr>
			<tr>
				<td>交易金额:
				</td>
				<td><input type="text" name="v_txnAmt"
					value="100"></td>
			</tr>
						<tr>
				<td>异步通知url：</td>
				<td><input type="text" name="v_notify_url"
					value="http://60.28.24.164:8102/app_posp/test/qrcode/interface.action"></td>
			</tr>
			<tr>
				<td>前台通知页面地址：</td>
				<td><input type="text" name="v_url"
					value="http://60.28.24.164:8102/app_posp/test/qrcode/interface.action"></td>
			</tr>
						<tr>
				<td>错误页面：</td>
				<td><input type="text" name="v_errorUrl" value="http://60.28.24.164:8102/app_posp/test/qrcode/interface.action"></td>
			</tr>
						<tr>
				<td>银行编码：</td>
				<td><input type="text" name="v_bankAddr" value="CCB"></td>
			</tr>
						<tr>
				<td>商品名称：</td>
				<td><input type="text" name="v_productName" value="测试商品"></td>
			</tr>
						<tr>
				<td>商品数量：</td>
				<td><input type="text" name="v_productNum" value="1"></td>
			</tr>
						<tr>
				<td>商品描述：</td>
				<td><input type="text" name="v_productDesc" value="测试"></td>
			</tr>
						<tr>
				<td>支付类型：</td>
				<td> <select name="v_cardType">
					<option selected="selected" value="0">借记卡</option>
					<option value="1">贷记卡</option>
				</select></td>
			</tr>
						<tr>
				<td>交易时间：</td>
				<td><input type="text" name="v_time" value="<%=UtilDate.getOrderNum()%>"></td>
			</tr>


			<tr>
				<td>订单有效时间：</td>
				<td><input type="text" name="v_expire_time" value="<%=UtilDate.getOrderNum()%>"></td>
			</tr>
						<tr>
				<td>支付币种：</td>
				<td><input type="text" name="v_currency" value="1"></td>
			</tr>
						<tr>
				<td>渠道类型：</td>
				<td><select name="v_channel">
					<option selected="selected" value="0">D0</option>
					<option value="1">T1</option>
				</select></td>
			</tr>
						<tr>
				<td>附加数据：</td>
				<td><input type="text" name="v_attach" value="担担面"></td>
			</tr>
		    <tr>
				<td>支付方式：</td>
				<td><select name="v_type">
					<option selected="selected" value="1">标准</option>
					<option value="2">直连</option>
				</select></td>
			</tr>

			<tr>
				<td colspan="2"><input type="submit" value="确定"></td>
			</tr>
		</table>
	</form>
</body>
</html>
