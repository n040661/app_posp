<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>更新</title>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-3.1.1.min.js"></script>
</head>
<body>
<center>
	商户号:<input type="text" name="merchantId" class="merchantId" value="10032061473"/><br /><br />
	费率编号：<input type="text" name="rateCode" class="rateCode" value="1003007"><br /><br />
	注册号:<input type="text" name="merchantCode" class="merchantCode" value="11361650"/><br /><br />
	变更类型:<input type="text" name="changeType" class="changeType" value="" placeholder="变更类型 1 交易费率变更 2 银行卡信息变更 3 交易费率新增 4 提现费率变更" title="变更类型 1 交易费率变更 2 银行卡信息变更 3 交易费率新增 4 提现费率变更"/><br /><br />
	-------------<br /><br />
	银行结算卡号:<input type="text" name="acctNo" class="acctNo" value=""/><br /><br />
	手机号:<input type="text" name="phone" class="phone" value=""/><br /><br />
	银行名称:<input type="text" name="bankName" class="bankName" value=""/><br /><br />
	支行名称:<input type="text" name="branchBankName" class="branchBankName" value=""/><br /><br />
	银行代码:<input type="text" name="bankCode" class="bankCode" value=""/><br /><br />
	银行编码:<input type="text" name="bankAbbr" class="bankAbbr" value=""/><br /><br />
	联行号:<input type="text" name="pmsbankNo" class="pmsbankNo" value=""/><br /><br />
	省:<input type="text" name="province" class="province" value=""/><br /><br />
	市:<input type="text" name="city" class="city" value=""/><br /><br />
	提现费率:<input type="text" name="withdrawDepositRate" class="withdrawDepositRate" value=""/><br /><br />
	单笔提现手续费<input type="text" name="withdrawDepositSingleFee" class="withdrawDepositSingleFee" value=""/><br /><br />
	---------------<br /><br />
	借记卡费率:<input type="text" name="debitRate" class="debitRate" value=""/><br /><br />
	借记卡封顶:<input type="text" name="debitCapAmount" class="debitCapAmount" value=""/><br /><br />
	信用卡费率:<input type="text" name="creditRate" class="creditRate" value=""/><br /><br />
	信用卡封顶:<input type="text" name="creditCapAmount" class="creditCapAmount" value=""/><br /><br />
	
	类型:<input type="text" name="type" class="type" value="cj014" /><br /><br />
	<input type="button" onclick="daifu()" value="更新"/><br /><br />
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