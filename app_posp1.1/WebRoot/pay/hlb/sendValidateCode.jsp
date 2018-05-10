<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>支付短信和确认支付</title>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-3.1.1.min.js"></script>
</head>
<body>
<center>
	商户号:<input type="text" name="merNo" class="merNo" value="<%=request.getParameter("merNo")%>"/><br /><br />
	商户单号:<input type="text" name="orderId" class="orderId" value="<%=request.getParameter("orderId")%>"/><br /><br />
	手机号:<input type="text" name="phone" class="phone" value="18902195076"/><br /><br />
	周期:<input type="text" name="dataType" class="dataType" value="0">
	验证码:<input type="text" name="validateCode" class="validateCode" value=""/>
	<input type="button" onclick="daifu()" value="点击获取验证码"/><br /><br />
	<input type="button" onclick="zhifu()" value="点击支付"/><br /><br />
	<div class="div"></div>
</center>
<script type="text/javascript">
	function daifu(){
		console.info($("input[name]").serialize());
		$.ajax({
			url:"${pageContext.request.contextPath}/HLBController/paySign.action",
			type:"post",
			dataType:"json",
			data:$("input[name]").serialize()+"&type=cj002",
			success:function(data){
				console.info(data);
				$.ajax({
					url:"${pageContext.request.contextPath}/HLBController/cardPay.action",
					type:"post",
					dataType:"json",
					data:$("input[name]").serialize()+"&sign="+data.sign+"&type=cj002",
					success:function(data){
						console.info(data);
						if(data.respCode=='00'){
							alert("获取成功！");
						}
					}
				});
			}
		});
	}
	function zhifu(){
		console.info($("input[name]").serialize());
		$.ajax({
			url:"${pageContext.request.contextPath}/HLBController/paySign.action",
			type:"post",
			dataType:"json",
			data:$("input[name]").serialize()+"&type=cj003",
			success:function(data){
				console.info(data);
				$.ajax({
					url:"${pageContext.request.contextPath}/HLBController/cardPay.action",
					type:"post",
					dataType:"json",
					data:$("input[name]").serialize()+"&sign="+data.sign+"&type=cj003",
					success:function(data){
						console.info(data);
						
					}
				});
			}
		});
	}
</script>
</body>
</html>