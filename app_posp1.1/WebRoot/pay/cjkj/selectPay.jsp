<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>代付</title>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-3.1.1.min.js"></script>
</head>
<body>
<center>
	商户号:<input type="text" name="merchantId" class="merchantId" value="10032061473"/><br /><br />
	uuid:<input type="text" name="merchantUuid" class="merchantUuid" value="48a30b58-7624-4520-b9dc-f0dd7d21b304"/><br /><br />
	订单号:<input type="text" name="orderId" class="orderId" value="1520839435175"/><br /><br />
	类型:<input type="text" name="type" class="type" value="cj017" /><br /><br />
	<input type="button" onclick="daifu()" value="代付"/><br /><br />
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