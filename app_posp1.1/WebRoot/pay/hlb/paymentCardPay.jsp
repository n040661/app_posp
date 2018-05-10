<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>绑卡支付短信</title>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-3.1.1.min.js"></script>
</head>
<body>
<center>
	商户号:<input type="text" name="merNo" class="merNo" value="10032061473"/><br /><br />
	商户单号:<input type="text" name="orderId" class="orderId" value="<%=System.currentTimeMillis()%>"/><br /><br />
	交易金额:<input type="text" name="orderAmount" class="orderAmount" value="100"/><br /><br />
	用户id:<input type="text" name="userId" class="userId" value="1511172615934"/><br /><br />
	绑卡id:<input type="text" name="bindId" class="bindId" value="c658aca90dff42da992d357b68877a75"/><br /><br />
	手机号:<input type="text" name="phone" class="phone" value="18322276803"/><br /><br />
	类型:<input type="text" name="type" class="type" value="cj006"/><br /><br />
	<input type="button" onclick="daifu()" value="点击代付"/><br /><br />
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
							window.location.href='paymentCard.jsp?orderId='+data.orderId+"&merNo="+data.merNo+"&orderAmount="+data.orderAmount;
						}
					}
				});
			}
		});
	}
</script>
</body>
</html>