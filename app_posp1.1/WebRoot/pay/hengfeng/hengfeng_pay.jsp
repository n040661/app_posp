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
				<td><input name="merchantId" type="text" value="10012017551"></td>
			</tr>
			<tr>
				<td>交易类型:</td>
				<td><select name="payType">
						<option selected="selected" value="20">T1</option>
						<option value="10">T0</option>
				</select></td>
			</tr>
			<tr>
				<td>订单金额：</td>
				<td><input name="txnAmt" type="text"
					value="10000"></td>
			</tr>
			<tr>
				<td>订单号：</td>
				<td><input name="orderId" type="text" readonly="readonly"
					value="<%=HFUtil.HFrandomOrder()%>"></td>
			</tr>
			<tr>
				<td>交易时间：</td>
				<td><input name="txnTime" type="text" readonly="readonly"
					value="<%=HFUtil.dateTime()%>"></td>
			</tr>
			<tr>
				<td>后台通知地址：</td>
				<td><input name="backUrl" id="backUrl" type="text"
					value="http://60.28.24.164:8102/app_posp/hfquick/hfbgPayResult.action"></td>
			</tr>
			<tr>
				<td>卡号：</td>
				<td><input type="text" name="accNo" value="6225571420109155"></td>
			</tr>
			<tr>
				<td>支付卡号预留手机号：</td>
				<td><input name="phoneNo" id="phoneNo" type="text"
					value="13323358548"></td>
			</tr>
			<tr>
				<td>用户手续费率：</td>
				<td><input name="userfee" id="userfee" type="text" value="0.5">单位:分</td>
			</tr>
			<tr>
				<td>结算银行：</td>
				<td><input name="bankName" id="bankName" type="text"
					value="农业银行"></td>
			</tr>
			<tr>
				<td>结算卡号：</td>
				<td><input name="toBankNo" id="toBankNo" type="text"
					value="6228450028016697770"></td>
			</tr>
			<tr>
				<td>真实姓名：</td>
				<td><input name="name" id="name" type="text" value="李娟"><font
					color="red"></font></td>
			</tr>
			<tr>
				<td>证件号码：</td>
				<td><input name="certNo" id="certNo" type="text"
					value="120105197510055420"></td>
			</tr>
			<tr>
				<td>结算预留手机号：</td>
				<td><input name="setPhoneNo" id="setPhoneNo" type="text"
					value="13323358548"></td>
			</tr>
			<tr>
				<td>前台通知地址：</td>
				<td><input type="text" name="frontUrl"
					value="http://60.28.24.164:8102/app_posp/hfquick/hfbgPayResult.action"></td>
			</tr>
			<tr>
				<td>代付手续费：</td>
				<td><input type="text" name="pounage"
					value="0.2"></td>
			</tr>
			<tr>
				<td><input name="button" type="submit" value="提交" class="btn" /></td>
			</tr>
		</table>
	</form>
</body>
</html>