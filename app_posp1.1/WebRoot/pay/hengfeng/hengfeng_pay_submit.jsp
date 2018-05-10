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
				<td><input name="merchantId" type="text" value="${temp.merchantId}"></td>
			</tr>
			<tr>
				<td>交易类型:</td>
					<td><input name="payType" type="text" readonly="readonly"
					value="${temp.payType}"></td>
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
				<td>后台通知地址：</td>
				<td><input name="backUrl" id="backUrl" type="text"
					value="${temp.backUrl}"></td>
			</tr>
			<tr>
				<td>卡号：</td>
				<td><input type="text" name="accNo" value="${temp.accNo}"></td>
			</tr>
			<tr>
				<td>支付卡号预留手机号：</td>
				<td><input name="phoneNo" id="phoneNo" type="text"
					value="${temp.phoneNo}"></td>
			</tr>
			<tr>
				<td>用户手续费率：</td>
				<td><input name="userfee" id="userfee" type="text" value="${temp.userfee}">单位:分</td>
			</tr>
			<tr>
				<td>结算银行：</td>
				<td><input name="bankName" id="bankName" type="text"
					value="${temp.bankName}"></td>
			</tr>
			<tr>
				<td>结算卡号：</td>
				<td><input name="toBankNo" id="toBankNo" type="text"
					value="${temp.toBankNo}"></td>
			</tr>
			<tr>
				<td>结算卡号预留手机号：</td>
				<td><input name="setPhoneNo" id="setPhoneNo" type="text"
					value="${temp.setPhoneNo}"></td>
			</tr>
			<tr>
				<td>真实姓名：</td>
				<td><input name="name" id="name" type="text" value="${temp.name}"><font
					color="red"></font></td>
			</tr>
			<tr>
				<td>证件号码：</td>
				<td><input name="certNo" id="certNo" type="text"
					value="${temp.certNo}"></td>
			</tr>
			<tr>
				<td>前台通知地址：</td>
				<td><input type="text" name="frontUrl"
					value="${temp.frontUrl}"></td>
			</tr>
			<tr>
				<td>代付手续费：</td>
				<td><input type="text" name="pounage"
					value="${temp.pounage}"></td>
			</tr>
			<tr>
				<td>签名：</td>
				<td><input type="text" name="signmsg"
					value="${temp.signmsg}"></td>
			</tr>
			<tr>
				<td><input name="button" type="submit" value="提交" class="btn" /></td>
			</tr>
		</table>
	</form>
</body>
</html>