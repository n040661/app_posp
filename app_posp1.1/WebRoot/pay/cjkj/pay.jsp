<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>代付</title>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-3.1.1.min.js"></script>
</head>
<body>
<center>
	商户号:<input type="text" name="merchantId" class="merchantId" value="10032061473"/><br /><br />
	uuid:<input type="text" name="merchantUuid" class="merchantUuid" value="48a30b58-7624-4520-b9dc-f0dd7d21b304"/><br /><br />
	订单号:<input type="text" name="orderId" class="orderId" value="<%=System.currentTimeMillis()%>"/><br /><br />
	金额（分）：<input type="text" name="amount" class="amount" value=""><br /><br />
	银行卡号:<input type="text" name="acctNo" class="acctNo" value="4512893456196113"/><br /><br />
	姓名:<input type="text" name="acctName" class="acctName" value="李娟"/><br /><br />
	账户类型:<input type="text" name="businessType" class="businessType" value="2"/><br /><br />
	银行名称：<input type="text" name="bankName" class="bankName" value="中国兴业银行"/><br /><br />
	支行名称:<input type="text" name="branchBankName" class="branchBankName" value="中国兴业银行"/><br /><br />
	银行编码:<input type="text" name="bankCode" class="bankCode" value="309" /><br /><br />
	银行代号:<input type="text" name="bankAbbr" class="bankAbbr" value="CIB" /><br /><br />
	联行号:<input type="text" name="pmsbankNo" class="pmsbankNo" value="103110023002" /><br /><br />
	省:<input type="text" name="province" class="province" value="天津市" /><br /><br />
	市：<input type="text" name="city" class="city" value="天津市" /><br /><br />
	账户类型(0:D0,1:T1)：<input type="text" name="summary" class="summary" value="0" /><br /><br />
	类型:<input type="text" name="type" class="type" value="cj015" /><br /><br />
	<input type="button" onclick="daifu()" value="代付"/><br /><br />
	<div class="div"></div>
</center>
<script type="text/javascript">
	function daifu(){
		console.info($("input[name]").serialize());
		$.ajax({
			url:"${pageContext.request.contextPath}/PayController/paySign.action",
			type:"post",
			data:$("input[name]").serialize(),
			success:function(data){
				console.info(data);
				$.ajax({
					url:"${pageContext.request.contextPath}/PayController/quickPay.action",
					type:"post",
					data:$("input[name]").serialize()+"&sign="+data,
					success:function(data){
						console.info(data);
						$(".div").html(data);
					}
				});
			}
		});
	}
</script>
</body>
</html>