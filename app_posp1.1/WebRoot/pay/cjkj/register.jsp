<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-3.1.1.min.js"></script>
</head>
<body>
<center>
	商户号:<input type="text" name="merchantId" class="merchantId" value="10032061473"/><br /><br />
	身份证号:<input type="text" name="liceneceNo" class="liceneceNo " value="120105197510055420"/><br /><br />
	银行卡号:<input type="text" name="acctNo" class="acctNo" value="6228450028016697770"/><br /><br />
	手机号:<input type="text" name="phone" class="phone" value="13323358548"/><br /><br />
	姓名:<input type="text" name="acctName" class="acctName" value="李娟"/><br /><br />
	账户类型:<input type="text" name="businessType" class="businessType" value="2"/><br /><br />
	银行名称:<input type="text" name="bankName" class="bankName" value="中国农业银行" /><br /><br />
	支行名称:<input type="text" name="branchBankName" class="branchBankName" value="中国农业银行天津广厦支行" /><br /><br />
	银行编码:<input type="text" name="bankCode" class="bankCode" value="103" /><br /><br />
	银行代号:<input type="text" name="bankAbbr" class="bankAbbr" value="ABC" /><br /><br />
	联行号:<input type="text" name="pmsbankNo" class="pmsbankNo" value="103110023002" /><br /><br />
	省份:<input type="text" name="province" class="province" value="天津市" /><br /><br />
	城市:<input type="text" name="city" class="city" value="天津市" /><br /><br />
	借记卡费率:<input type="text" name="debitRate" class="debitRate" value="0.0035" /><br /><br />
	借记卡封顶值:<input type="text" name="debitCapAmount" class="debitCapAmount" value="99999900" /><br /><br />
	信用卡费率:<input type="text" name="creditRate" class="creditRate" value="0.0035" /><br /><br />
	信用卡封顶值:<input type="text" name="creditCapAmount" class="creditCapAmount" value="99999900" /><br /><br />
	提现费率:<input type="text" name="withdrawDepositRate" class="withdrawDepositRate" value="0.001" /><br /><br />
	单笔提现手续费:<input type="text" name="withdrawDepositSingleFee" class="withdrawDepositSingleFee" value="100" /><br /><br />
	费率编号：<input type="text" name="rateCode" class="rateCode" value="1001001"><br /><br />
	类型:<input type="text" name="type" class="type" value="cj006" /><br /><br />
	<input type="button" onclick="daifu()" value="注册"/><br /><br />
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