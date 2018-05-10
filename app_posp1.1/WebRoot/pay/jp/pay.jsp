<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-3.1.1.min.js"></script>
</head>
<body>
<center>
	商户号:<input type="text" name="merchantId" class="merchantId" value="10032061473"/><br /><br />
	商户代付单号:<input type="text" name="orderId" class="orderId" value="<%=System.currentTimeMillis()%>"/><br /><br />
	交易金额:<input type="text" name="amount" class="amount" value="100"/><br /><br />
	请求时间:<input type="text" name="startDate" class=""startDate"" value=""/><br /><br />
	银行卡号:<input type="text" name="acctNo" class="acctNo" value="6228450028016697770"/><br /><br />
	持卡人姓名:<input type="text" name="acctName" class="acctName" value="李娟"/><br /><br />
	卡类型:<input type="text" name="businessType" class="businessType" value="0"/><br /><br />
	联行号:<input type="text" name="pmsbankNo" class="pmsbankNo" value="103110023002"/><br /><br />
	银行名称:<input type="text" name="bankName" class="bankName" value="中国农业银行"/><br /><br />
	年份:<input type="text" name="year" class="yeatr" value=""/><br /><br />
	月份:<input type="text" name="month" class="month" value=""/><br /><br />
	CVV2:<input type="text" name="cvv2" class="cvv2" value=""/><br /><br />
	电话:<input type="text" name="phone" class="phone" value="13323358548"/><br /><br />
	异步地址:<input type="text" name="notifyUrl" class="notifyUrl" value="http://60.28.24.164:8104/app_posp/TFBController/notifyUrl.action"/><br /><br />
	<input type="button" onclick="daifu()" value="点击代付"/><br /><br />
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
					url:"${pageContext.request.contextPath}/JPController/pay.action",
					type:"post",
					dataType:"json",
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