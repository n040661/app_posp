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
	商户号:<input type="text" name="account" class="account" value="10012015423"><br/><br/>
	订单号:<input type="text" name="order_id" class="order_id" value="<%=System.currentTimeMillis()%>"><br/><br/>
	金额:<input type="text" name="amount" class="amount" value="100"><br/><br/>
	备注:<input type="text" name="body" class="body" value="大饼鸡蛋"><br/><br/>
	交易方式:<input type="text" name="pay_method" class="pay_method" value="1" placeholder="01-微信，02-支付宝，03-QQ钱包，04-网关支付"><br/><br/>
	银行编码(网关填):<input type="text" name="bankCode" class="bankCode" value=""><br/><br/>
	异步回调地址:<input type="text" name="notifyurl" class="notifyurl" value="http://60.28.24.164:8104/app_posp/TFBController/returnUrl.action"><br/><br/>
	成功回调地址:<input type="text" name="return_url" class="return_url" value="http://60.28.24.164:8104/app_posp/TFBController/notifyUrl.action"><br/><br/>
	<input type="button" onclick="shengcheng()" value="提交生成">
</center>
<script type="text/javascript">
	function shengcheng(){
		var account=$(".account").val();
		var order_id=$(".order_id").val();
		var amount=$(".amount").val();
		var body=$(".body").val();
		var pay_method=$(".pay_method").val();
		var bankCode=$(".bankCode").val();
		var notifyurl=$(".notifyurl").val();
		var return_url=$(".return_url").val();
		var data={"account":account,"order_id":order_id,"amount":amount,"body":body,"pay_method":pay_method,"bankCode":bankCode,"notifyurl":notifyurl,"return_url":return_url};
		$.ajax({
			url:"${pageContext.request.contextPath}/KKXController/cardPayParameter.action",
			type:"post",
			data:data,
			success:function(data){
				console.info(data);
				var data1={"account":account,"order_id":order_id,"amount":amount,"body":body,"pay_method":pay_method,"bankCode":bankCode,"notifyurl":notifyurl,"return_url":return_url,"sign":data};
				$.ajax({
					url:"${pageContext.request.contextPath}/KKXController/pay.action",
					type:"post",
					data:data1,
					dataType:"json",
					success:function(data){
						console.info(data.sign);
					}
				});
			}
			
		});
	}
</script>
</body>
</html>