<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>绑结算卡</title>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-3.1.1.min.js"></script>
</head>
<body>
<center>
	商户号:<input type="text" name="merNo" class="merNo" value="10012112721"/><br /><br />
	商户单号:<input type="text" name="orderId" class="orderId" value="<%=System.currentTimeMillis()%>"/><br /><br />
	用户id:<input type="text" name="userId" class="userId" value=""/><br /><br />
	姓名:<input type="text" name="payerName" class="payerName" value="尚延超"/><br /><br />
	身份证号:<input type="text" name="idCardNo" class="idCardNo" value="410324199203231912"/><br /><br />
	银行卡号:<input type="text" name="cardNo" class="cardNo" value="6212260302030264816"/><br /><br />
	联行号:<input type="text" name="bankUnionCode" class="bankUnionCode" value="102110001181"/><br /><br />
	手机号:<input type="text" name="phone" class="phone" value="18902195076"/><br /><br />
	周期:<input type="text" name="dataType" class="dataType" value="0">
	类型:<input type="text" name="type" class="type" value="cj008"/><br /><br />
	<input type="button" onclick="daifu()" value="点击绑结算卡"/><br /><br />
	<div class="div"></div>
</center>
<script type="text/javascript">
	function daifu(){
		console.info($("input[name]").serialize());
		$.ajax({
			url:"${pageContext.request.contextPath}//HLBController/paySign.action",
			type:"post",
			dataType:"json",
			data:$("input[name]").serialize(),
			success:function(data){
				console.info(data);
				$.ajax({
					url:"${pageContext.request.contextPath}/HLBController/cardPay.action",
					type:"post",
					dataType:"json",
					data:$("input[name]").serialize()+"&sign="+data.sign,
					success:function(data){
						console.info(data);
						if(data.respCode=='00'){
							window.location.href='pay.jsp?orderId='+$(".orderId").val()+"&merNo="+data.merNo+"&userId="+$(".userId").val();
						}
					}
				});
			}
		});
	}
	
</script>
</body>
</html>