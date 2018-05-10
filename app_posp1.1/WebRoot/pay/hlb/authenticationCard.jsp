<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>鉴权绑卡</title>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-3.1.1.min.js"></script>
</head>
<body>
<center>
	商户号:<input type="text" name="merNo" class="merNo" value="<%=request.getParameter("merNo")%>"/><br /><br />
	商户单号:<input type="text" name="orderId" class="orderId" value="<%=request.getParameter("orderId")%>"/><br /><br />
	用户id:<input type="text" name="userId" class="userId" value="<%=request.getParameter("userId")%>"/><br /><br />
	姓名:<input type="text" name="payerName" class="payerName" value="高立明"/><br /><br />
	证件号码:<input type="text" name="idCardNo" class="idCardNo" value="120224199303303413"/><br /><br />
	银行卡号:<input type="text" name="cardNo" class="cardNo" value="6228480020721272316"/><br /><br />
	信用卡有效期年份:<input type="text" name="year" class="year" value=""/><br /><br />
	信用卡有效期月份:<input type="text" name="month" class="month" value=""/><br /><br />
	卡后三位:<input type="text" name="cvv2" class="cvv2" value=""/><br /><br />
	手机号:<input type="text" name="phone" class="phone" value="18322276803"/><br /><br />
	短信验证码:<input type="text" name="validateCode" class="validateCode" value=""/><br /><br />
	类型:<input type="text" name="type" class="type" value="cj005"/><br /><br />
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
							window.location.href='sendValidateCode.jsp?orderId='+data.orderId+"&merNo="+data.merNo;
						}
					}
				});
			}
		});
	}
</script>
</body>
</html>