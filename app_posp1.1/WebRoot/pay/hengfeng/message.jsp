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
		action="${pageContext.request.contextPath }/hfquick/hfsignmessage.action"
		method="post">
		<table>
			<tr>
				<td>商户号：</td>
				<td><input name="merchantId" type="text" value="10012014011"></td>
			</tr>
			<tr>
				<td>订单交易时间：</td>
				<td><input name="txnTime" type="text" readonly="readonly"
					value="<%=HFUtil.dateTime()%>"></td>
			</tr>
			<tr>
				<td>订单号：</td>
				<td><input name="orderId" type="text" readonly="readonly"
					value="<%=HFUtil.HFrandomOrder()%>"></td>
			</tr>
			<tr>
				<td>交易金额：</td>
				<td><input name="txnAmt" id="txnAmt" type="text" value="1"><font
					color="red">单位:分</font></td>
			</tr>
			<tr>
				<td>Token号：</td>
				<td><input name="token" id="token" type="text"
					value="6235240000269775694"></td>
			</tr>
			<tr>
				<td>手机号码：</td>
				<td><input name="phoneNo" id="phoneNo" type="text"
					value="18902195076"></td>
			</tr>
			<tr>
				<td>真实姓名：</td>
				<td><input name="name" id="name" type="text" value="尚延超"><font
					color="red"></font></td>
			</tr>
			<tr>
				<td>证件号码：</td>
				<td><input name="certNo" id="certNo" type="text"
					value="410324199203231912"></td>
			</tr>
			<tr>
				<td>入账卡号：</td>
				<td><input name="accNo" id="accNo" type="text"
					value="6212260302026649095"></td>
			</tr>
			<tr>
				<td>用户手续费：</td>
				<td><input name="userfee" id="userfee" type="text"
					value="50">单位:分</td>
			</tr>
			<tr>
				<td>交易类型:</td>
				<td><select name="tranTp">
						<option selected="selected" value="1">T1</option>
						<option value="0">T0</option>
				</select></td>
			</tr>
			<tr>
				<td><input name="button" type="submit" value="提交" class="btn" /></td>
			</tr>
		</table>
	</form>
</body>
</html>