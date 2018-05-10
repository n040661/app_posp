<%@ page language="java" contentType="text/html; charset=utf-8"
	import="xdt.quickpay.hengfeng.util.*" pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>资金代收协议签约</title>
</head>
<body>

	<form action="${pageContext.request.contextPath}/df/payroll.action"
		method="post">
		<table>
			<tr>
				<th>商户号:</th>
				<td><input type="text" value="10012341630" id="merchantId"
					name="merchantId"></td>
			</tr>
			<tr>
				<th>订单号:</th>
				<td><input type="text" value="<%=HFUtil.HFrandomOrder()%>"
					id="orderId" name="orderId"></td>
			</tr>
			<tr>
				<th>代付类型:</th>
				<td><select id="tranType" name="tranType">
						<option selected="selected" value="DF02">代发银行卡</option>
						<option value="DF01">代发沃账户</option>
						<option value="DF03">协议代发银行卡</option>
						<option value="DF04">代发酬金账户</option>
				</select></td>
			</tr>
			<tr>
				<th>业务类型：</th>
				<td><select id="bizCode" name="bizCode">
				    <option selected="selected" value="017"> 外部代发  </option>
				    <option  value="001">代理商放款 </option>
				    <option  value="002">代理商佣金发放 </option>
				    <option  value="003">理财产品赎回 </option>
				    <option  value="004">理财产品到期给付 </option>
				    <option  value="005">商户结算 </option>
				    <option  value="006">提现</option>	 
				    <option  value="007">转账 </option>
				    <option value="008" >退转打款</option>		 
				</select></td>
			</tr>
			<tr>
				<th>代付金额:</th>
				<td><input type="text" value="500" id="amount" name="amount"></td>
			</tr>
			<tr>
				<th>收款人账户:</th>
				<td><input type="text" value="6228450028016697770" id="payeeAcc" name="payeeAcc"></td>
			</tr>
			<tr>
				<th>收款账户类别:</th>
				<td><input type="text" value="4" id="woType" name="woType"></td>
			</tr>
			<tr>
				<th>收款银行编码：</th>
				<td><input type="text" style="width: 350px;"
					name="payeeBankCode" id="payeeBankCode"
					value="ABC"></td>
			</tr>
			<tr>
				<th>收款人姓名：</th>
				<td><input type="text" style="width: 350px;" name="payeeName" id="payeeName"
					value="李娟"></td>
			</tr>
			<tr>
				<th>收款银行分行：</th> 
				<td><input type="text" style="width: 350px;" name="payeeBankBranch" id="payeeBankBranch"
					value="中国农业银行天津广厦支行"></td>
			</tr>
			<tr>
				<th>收款银行分支行联行号：</th>
				<td><input type="text" style="width: 350px;" name="payeeUnionBankNo" id="payeeUnionBankNo"
					value="103110023002"></td>
			</tr>
			<tr>
				<th>收款银行分支行归属地：</th>
				<td><input type="text" style="width: 350px;" name="payeeAttribution"
					value="天津-天津" id="payeeAttribution"></td>
			</tr>
			<tr>
				<th>订单状态变更通知地址：</th>
				<td><input type="text" style="width: 350px;" name="callbackUrl" id="callbackUrl"
					value="http://60.28.24.164:8102/app_posp/dk/wzfbgPayResult.action"></td>
			</tr>
			<tr>
				<th>扩展字段：</th>
				<td><input type="text" style="width: 350px;" name="merExtend" id="merExtend"
					value="aaaaaa"></td>
			</tr>
			<tr>
				<th>收款人证件信息:</th>
				<td><select id="identityInfo" name="identityInfo">
						<option selected="selected" value="1">身份证类型</option>
						<option value="2">营业执照</option>
				</select></td>
			</tr>


			<tr>
				<td colspan="2"><input type="submit" value="确认" /></td>
			</tr>
		</table>
	</form>

</body>
</html>