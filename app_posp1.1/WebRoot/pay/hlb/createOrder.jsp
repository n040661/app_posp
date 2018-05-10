<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>下单</title>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-3.1.1.min.js"></script>
</head>
<body>
<center>
	商户号:<input type="text" name="merNo" class="merNo" value="10012112721"/><br /><br />
	商户单号:<input type="text" name="orderId" class="orderId" value="CJZF<%=System.currentTimeMillis()%>"/><br /><br />
	交易金额:<input type="text" name="orderAmount" class="orderAmount" value="100"/><br /><br />
	用户id:<input type="text" name="userId" class="userId" value="<%=System.currentTimeMillis()%>"/><br /><br />
	姓名:<input type="text" name="payerName" class="payerName" value="尚延超"/><br /><br />
	证件号码:<input type="text" name="idCardNo" class="idCardNo" value="410324199203231912"/><br /><br />
	银行卡号:<input type="text" name="cardNo" class="cardNo" value="6222521212600252"/><br /><br />
	信用卡有效期年份:<input type="text" name="year" class="year" value="22"/><br /><br />
	信用卡有效期月份:<input type="text" name="month" class="month" value="11"/><br /><br />
	卡后三位:<input type="text" name="cvv2" class="cvv2" value="034"/><br /><br />
	手机号:<input type="text" name="phone" class="phone" value="18902195076"/><br /><br />
	商品名称:<input type="text" name="goodsName" class="goodsName" value="大饼鸡蛋"/><br /><br />
	商品描述:<input type="text" name="goodsDesc" class="goodsDesc" value="大饼鸡蛋"/><br /><br />
	终端标识:<input type="text" name="terminalId" class="terminalId" value="<%=System.currentTimeMillis()%>"/><br /><br />
	订单有效时间:<input type="text" name="period" class="period" value=""/><br /><br />
	订单有效时间单位:<input type="text" name="periodUnit" class="periodUnit" value=""/><br /><br />
	异步通知地址:<input type="text" name="notifyUrl" class="notifyUrl" value="https://www.baidu.com"/><br /><br />
	类型:<input type="text" name="type" class="type" value="cj001"/><br /><br />
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