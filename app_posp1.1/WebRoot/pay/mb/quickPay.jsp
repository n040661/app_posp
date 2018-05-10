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
商户号:<input type="text" name="merId" class="merId" value="10032061473"><br />
订单号:<input type="text" name="orderId" class="orderId" value="CJZF<%=System.currentTimeMillis()%>"><br />
金额： <input type="text" class="transAmount" name="transAmount" value="1000"/><br />
银行卡号： <input type="text" class="accNo" name="accNo" value="6222080302013903148"/><br />
开户名： <input type="text" class="accName" name="accName" value="李娟"/><br />
银行卡类型：<input type="text" class="accType" name="accType" value="01"/><br />
信用卡有效期：<input type="text" class="expireDate" name="expireDate" value=""/><br />
信用卡后三位：<input type="text" class="CVV" name="CVV" value=""/><br />
银行代码：<input type="text" class="bankCode" name="bankCode" value=""/><br />
银行编码：<input type="text" class="openBankName" name="openBankName" value=""/><br />
证件类型：<input type="text" class="cerType" name="cerType" value="01"/><br />
身份证：<input type="text" class="cerNumber" name="cerNumber" value="120105197510055420"/><br />
手机号： <input type="text" class="mobile" name="mobile" value="13323358548"/><br />
异步地址： <input type="text" class="backNotifyUrl" name="backNotifyUrl" value="http://60.28.24.164:8104/app_posp/MBController/payClientyaction"/><br />
类型： <input type="text" class="type" name="type" value="cj005"/><br />
<input type="button" value="提交" onclick="tijiao()"><br />
<div id ="div"></div>
<br />
</center>
<script type="text/javascript">
	function tijiao(){
			$.ajax({
				url : "${pageContext.request.contextPath}/MBController/paySign.action",
				type : 'post',
				data : $("input[name]").serialize(),
				success : function(data) {
					$.ajax({
						url : "${pageContext.request.contextPath}/MBController/payClienty.action",
						type : 'post',
						dataType:"json",
						data :$("input[name]").serialize()+"&sign="+data,
						success : function(data) {
							console.info(data);
							$("#div").text(data);
							window.location.href="verifcation.jsp?payOrderId="+data.payOrderId;
						}
					});
				}
			});
	}
	/* function tijiao(){
	   
	} */
</script>
</body>

</html>