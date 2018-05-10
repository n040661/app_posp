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
		商户号:<input type="text" name="merchantId" class="merchantId" value="10032061473"><br/><br/>
		支付订单号:<input type="text" name="orderId" class="orderId" value="<%=System.currentTimeMillis()%>"><br/><br/>
		订单交易金额(分):<input type="text" name="amount" class="amount" value="100"><br/><br/>
		<!-- 业务类型：<input type="text" name ="businessType" class="businessType" value="0"><br/><br/> -->
		姓名：<input type="text" name ="acctName" class="acctName" value="李娟"><br/><br/>
		证件号：<input type="text" name ="liceneceNo" class="liceneceNo" value="120105197510055420"><br/><br/>
		银行卡号：<input type="text" name ="acctNo" class="acctNo" value="6225571420109155"><br/><br/>
		银行名称：<input type="text" name ="branchBankName" class="branchBankName" value="广发银行"><br/><br/>
		手机号：<input type="text" name ="phone" class="phone" value="13323358548"><br/><br/>
		cvv2：<input type="text" name="cvv2" class="cvv2" value="623"><br/><br/>
		年：<input type="text" name="year" class="year" value="21"><br/><br/>
		月：<input type="text" name="month" class="month" value="12"><br/><br/>
		异步地址：<input type="text" name="backurl" class="backurl" value="http://60.28.24.164:8104/app_posp/QuickPayController/notifyUrl.action"><br/><br/>
		省份：<input type="text" name="province" class="province" value="天津市"><br/><br/>
		城市：<input type="text" name="city" class="city" value="天津市"><br/><br/>
		类型：<input type="text" name="type" class="type" value="cj001"><br/><br/>
		<input type="button" onclick="shengcheng()" value="提交生成">
		<div id="div"></div>
</center>
</body>
<script type="text/javascript">
	function shengcheng(){
		$.ajax({
			url:"${pageContext.request.contextPath}/QuickPayController/paySign.action",
			type:"post",
			dataType:"json",
			data:$("input[name]").serialize(),
			success:function(data){
				$.ajax({
					url:"${pageContext.request.contextPath}/QuickPayController/quickPay.action",
					type:"post",
					data:$("input[name]").serialize()+"&sign="+data.sign,
					success:function(data){
						console.info(data);
						$("#div").html('<font color="red">'+data+'</font>');
					}
				});
			}
		});
	}
</script>
</html>