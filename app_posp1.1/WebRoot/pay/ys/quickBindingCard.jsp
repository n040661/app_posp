<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>快捷绑卡</title>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-3.1.1.min.js"></script>
</head>
<body>
<center>
	商户号:<input type="text" name="merchantId" class="merchantId" value="10032061473"/><br /><br />
	订单号:<input type="text" name="orderId" class="orderId " value="<%=System.currentTimeMillis()%>"/><br /><br />
	身份证号:<input type="text" name="liceneceNo" class="liceneceNo " value="120105197510055420"/><br /><br />
	银行卡号:<input type="text" name="acctNo" class="acctNo" value="4512893456196113"/><br /><br />
	手机号:<input type="text" name="phone" class="phone" value="13323358548"/><br /><br />
	姓名:<input type="text" name="acctName" class="acctName" value="李娟"/><br /><br />
	cvv2:<input type="text" name="cvv2" class="cvv2" value="795"/><br /><br />
	年:<input type="text" name="year" class="year" value="20" /><br /><br />
	月:<input type="text" name="month" class="month" value="12" /><br /><br />
	唯一号:<input type="text" name="merchantCode" class="merchantCode" value="e6a62e84647b419b8401622357bd3a30" /><br /><br />
	类型:<input type="text" name="type" class="type" value="cj006" /><br /><br />
	<input type="button" onclick="daifu()" value="注册"/><br /><br />
	<div class="div"></div>
</center>
<script type="text/javascript">
	function daifu(){
		console.info($("input[name]").serialize());
		$.ajax({
			url:"${pageContext.request.contextPath}/YSController/paySign.action",
			type:"post",
			data:$("input[name]").serialize(),
			success:function(data){
				console.info(data);
				$.ajax({
					url:"${pageContext.request.contextPath}/YSController/quickPay.action",
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