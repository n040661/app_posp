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
		<form action="${pageContext.request.contextPath}/JPController/cardPay.action" method="post" id ="from">
		商户号:<input type="text" name="merchantId" class="merchantId" value="10032061473"><br/><br/>
		支付订单号:<input type="text" name="orderId" class="orderId" value="<%=System.currentTimeMillis()%>"><br/><br/>
		订单交易金额(分):<input type="text" name="amount" class="amount" value="1000"><br/><br/>
		异步回调地址:<input type="text" name="notifyUrl" class="notifyUrl" value="http://60.28.24.164:8104/app_posp/TFBController/notifyUrl.action"><br/><br/>
		同步回调地址:<input type="text" name="returnUrl" class="returnUrl" value="https://www.baidu.com"><br/><br/>
		商品名称 :<input type="text" name="productName" class="productName" value="大饼鸡蛋" ><br/><br/>
		银行编码:<input type="text" name="bankAbbr" class="bankAbbr" value="CMB"><br/><br/>
		银行类型:<input type="text" name="businessType" class="businessType" value="B2C" ><br/><br/>
		发起时间:<input type="text" name="startDate" class="startDate" value="" placeholder="yyyyMMddHHmmss"><br/><br/>		 
		<input type="button" onclick="shengcheng()" value="提交生成">
		</form>
</center>
</body>
<script type="text/javascript">
	
	function shengcheng(){
		
		$.ajax({
			url:"${pageContext.request.contextPath}/PayController/paySign.action",
			type:"post",
			data:$("input[name]").serialize(),
			success:function(data){
				$("#from").append('<input type="text" name="sign" class="sign" style="display: none" value="'+data+'"><br/>');
				console.info($(".sign").val());
				$("#from").submit();
				/*  $.ajax({
					url:"${pageContext.request.contextPath}/HJController/cardPay.action",
					type:"post",
					dataType:"json",
					data:$("input[name]").serialize()+"&sign="+data.sign,
					success:function(data){
						console.info(data);
						
					}
				});  */
			}
		});
	}
</script>
</html>