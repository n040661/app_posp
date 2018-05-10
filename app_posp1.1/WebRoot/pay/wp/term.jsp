<%@ page language="java" contentType="text/html; charset=utf-8"
	import="xdt.quickpay.hengfeng.util.*" pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>资金代收协议签约</title>
</head>
<body>

	<form
		action="${pageContext.request.contextPath}/dk/term.action"
		method="post">
		<table>
			<tr>
				<td>商户号:</td>
				<td><input type="text" value="10012341630" id="merchantId"
					name="merchantId"></td>
			</tr>
			<tr>
				<td>订单号:</td>
				<td><input type="text" value="<%=HFUtil.HFrandomOrder()%>"
					id="orderId" name="orderId"></td>
			</tr>
			<tr>
				<td>工具类型:</td>
				<td><select id="payProducts" name="payProducts">
						<option selected="selected" value="XDK">现金账户</option>
						<option value="CDK">酬金账户</option>
						<option value="YDK">银行账户</option>
						<option value="B2BDK">对公银行账户
							</optiona>
						<option value="QYYEDK">企业余额资金</option>
						<option value="QYCJDK">企业酬金资金</option>
				</select></td>
			</tr>
			<tr>
				<th>子协议编号：</th>
				<td><input type="text" style="width: 350px;"
					name="subContractId" id="subContractId" value="Y17112036610000007860"></td>
			</tr>
			<tr>
				<td colspan="2"><input type="submit" value="确认" /></td>
			</tr>
		</table>
	</form>

</body>
</html>