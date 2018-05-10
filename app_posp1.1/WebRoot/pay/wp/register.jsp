<%@ page language="java" contentType="text/html; charset=utf-8" import="xdt.quickpay.hengfeng.util.*"
	pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>资金代收协议签约</title>
</head>
<body>

	<form action="${pageContext.request.contextPath}/dk/signSimpleSubContract.action" method="post">
		<table>
			<tr>
				<td>商户号:</td>
				<td><input type="text" value="10012341630" id="merchantId" name="merchantId"></td>
			</tr>
			<tr>
				<td>订单号:</td>
				<td><input type="text" value="<%=HFUtil.HFrandomOrder()%>" id="orderId" name="orderId"></td>
			</tr>
			<tr>
				<td>银行编码:</td>
				<td><input type="text" value="SPDB" id="bankCode" name="bankCode">
				</td>
			</tr>
			<tr>
				<td>渠道来源:</td>
				<td><select id="signChnl" name="signChnl">
				     <option selected="selected" value="www">pc端</option>
				     <option value="app">移动端</option>
				</select></td>
			</tr>
			<tr>
				<td>工具类型:</td>
				<td><select id="payProducts" name="payProducts">
				     <option selected="selected" value="XDK">现金账户</option>
				     <option value="CDK">酬金账户</option>
				     <option value="YDK">银行账户</option>
				     <option value="B2BDK">对公银行账户</optiona>
				     <option value="QYYEDK">企业余额资金</option>
				     <option value="QYCJDK">企业酬金资金</option>
				</select></td>
			</tr>
			<tr>
				<td>账户类型:</td>
				<td><select id="accType" name="accType">
				   <option selected="selected" value="0">对公</option>
				   <option value="1">对私</option>
				</select></td>
			</tr>
			<tr>
				<td>账户编码:</td>
				<td><select id="accCode" name="accCode">
				   <option selected="selected" value="A01">借记卡</option>
				   <option value="A02">信用卡</option>
				</select></td>
			</tr>
			<tr>
				<td>银行卡号:</td>
				<td><input type="text" value="6222021001116245706" id="cardNo" name="cardNo" /></td>
			</tr>
						<tr>
				<td>姓名:</td>
				<td><input type="text" value="徐双双" id="name" name="name" /></td>
			</tr>
						<tr>
				<td>身份证号:</td>
				<td><input type="text" value="411521199204106024" id="idCardNo" name="idCardNo" /></td>
			</tr>
				<tr>
				<td>手机号:</td>
				<td><input type="text" value="18802195076" id="phoneNo" name="phoneNo" /></td>
			</tr>
			<tr>
				<td colspan="2"><input type="submit" value="确认"/></td>
			</tr>
		</table>
	</form>

</body>
</html>