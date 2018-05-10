<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>绑卡支付</title>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-3.1.1.min.js"></script>
</head>
<body>
<center>
	商户号:<input type="text" name="merNo" class="merNo" value="<%=request.getParameter("merNo")%>"/><br /><br />
	商户单号:<input type="text" name="orderId" class="orderId" value="<%=request.getParameter("orderId")%>"/><br /><br />
	交易金额:<input type="text" name="orderAmount" class="orderAmount" value="<%=request.getParameter("orderAmount")%>"/><br /><br />
	用户id:<input type="text" name="userId" class="userId" value="1511172615934"/><br /><br />
	绑卡ID:<input type="text" name="bindId" class="bindId" value="c658aca90dff42da992d357b68877a75"/><br /><br />
	商品名称:<input type="text" name="goodsName" class="goodsName" value="大饼鸡蛋"/><br /><br />
	商品描述:<input type="text" name="goodsDesc" class="goodsDesc" value="大饼鸡蛋"/><br /><br />
	终端标识:<input type="text" name="terminalId" class="terminalId" value="<%=System.currentTimeMillis()%>"/><br /><br />
	订单有效时间:<input type="text" name="period" class="period" value=""/><br /><br />
	订单有效时间单位:<input type="text" name="periodUnit" class="periodUnit" value=""/><br /><br />
	异步通知地址:<input type="text" name="notifyUrl" class="notifyUrl" value="https://www.baidu.com"/><br /><br />
	类型:<input type="text" name="type" class="type" value="cj007"/><br /><br />
	周期:<input type="text" name="dataType" class="dataType" value="0">
	验证码:<input type="text" name="validateCode" class="validateCode" value=""/>
	<input type="button" onclick="daifu()" value="点击支付"/><br /><br />
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
					}
				});
			}
		});
	}
</script>
</body>
</html>