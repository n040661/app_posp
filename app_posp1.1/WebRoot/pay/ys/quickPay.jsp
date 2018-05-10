<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-3.1.1.min.js"></script>
</head>
<body>
<center>
<form action="${pageContext.request.contextPath}/YSController/quickPay.action" method="post" id ="from">
商户号(C):<input type="text" name="merchantId" class="merchantId" value="10032061473"><br />
订单号(C):<input type="text" name="orderId" class="orderId" value="<%=System.currentTimeMillis()%>"><br />
金额(C)： <input type="text" class="amount" name="amount" value="11000"/><br />
银行卡号： <input type="text" class="accNo" name="acctNo" value="6253360097935869"/><br />
开户名： <input type="text" class="accName" name="acctName" value="高立明"/><br />
用户唯一标识(C)：<input type="text" class="merchantCode" name="merchantCode" value="015fa849f38e4e61bae9384f7f1b5c50"/><br />
手机号： <input type="text" class="phone" name="phone" value="18322276803"/><br />
联行号： <input type="text" class="pmsbankNo" name="pmsbankNo" value="103100000026"/><br />
身份证：<input type="text" class="liceneceNo" name="liceneceNo" value="120224199303303413"/><br />
异步地址(C)： <input type="text" class="notifyUrl" name="notifyUrl" value="http://60.28.24.164:8104/app_posp/YSController/notifyUrl"/><br />
同步步地址： <input type="text" class="returnUrl" name="returnUrl" value="http://60.28.24.164:8104/app_posp/YSController/returnUrl"/><br />
类型： <input type="text" class="type" name="type" value="cj003"/><br />
<input type="button" value="提交" onclick="tijiao()"><br />
</form>
<div id ="div"></div>
<br />
</center>
<script type="text/javascript">
	function tijiao(){
			$.ajax({
				url : "${pageContext.request.contextPath}/YSController/paySign.action",
				type : 'post',
				data : $("input[name]").serialize(),
				success : function(data) {
					
					$("#from").append('<input type="text" name="sign" class="sign" style="display: none" value="'+data+'"><br/>');
					console.info($(".sign").val());
					$("#from").submit();
					/* $.ajax({
						url : "${pageContext.request.contextPath}/YSController/quickPay.action",
						type : 'post',
						dataType:"json",
						data :$("input[name]").serialize()+"&sign="+data,
						success : function(data) {
							console.info(data);
							$("#div").text(data);
							window.location.href=data.html;
						}
					}); */
				}
			});
	}
	/* function tijiao(){
	   
	} */
</script>
</body>

</html>