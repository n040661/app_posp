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
		业务类型：<input type="text" name ="businessType" class="businessType" value="0"><br/><br/>
		姓名：<input type="text" name ="acctName" class="acctName" value="高立明"><br/><br/>
		证件号：<input type="text" name ="liceneceNo" class="liceneceNo" value="120224199303303413"><br/><br/>
		银行卡号：<input type="text" name ="acctNo" class="acctNo" value="6228480020721272316"><br/><br/>
		银行名称：<input type="text" name ="branchBankName" class="branchBankName" value="农业银行"><br/><br/>
		手机号：<input type="text" name ="phone" class="phone" value="18322276803"><br/><br/>
		证件类型：<input type="text" name="liceneceType" class="liceneceType" value="01"><br/><br/>
		用途：<input type="text" name="purpose" class="purpose" value="发工资"><br/><br/>
		省份：<input type="text" name="province" class="province" value="天津市"><br/><br/>
		城市：<input type="text" name="city" class="city" value="天津市"><br/><br/>
		
		<input type="button" onclick="shengcheng()" value="提交生成">
		<div id="div"></div>
</center>
</body>
<script type="text/javascript">
	function shengcheng(){
		$.ajax({
			url:"${pageContext.request.contextPath}/PayController/paySign.action",
			type:"post",
			data:$("input[name]").serialize(),
			success:function(data){
				$.ajax({
					url:"${pageContext.request.contextPath}/PayController/pay.action",
					type:"post",
					data:$("input[name]").serialize()+"&sign="+data,
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