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
	商户号:<input type="text" name="merchantId" class="merchantId" value="100120242118015"><br/><br/>
	订单号:<input type="text" name="merchOrderId" class="merchOrderId" value="<%=System.currentTimeMillis()%>"><br/><br/>
	金额:<input type="text" name="amount" class="amount" value="1000"><br/><br/>
	行业编码:<input type="text" name="industryNo" class="industryNo" value=""><br/><br/>
	银行卡号:<input type="text" name="bankNo" class="bankNo" value="6225571641115098"><br/><br/>
	姓名:<input type="text" name="name" class="name" value="李鑫"><br/><br/>
	手机号:<input type="text" name="mobileNo" class="mobileNo" value="13512946794"><br/><br/>
	短信凭证:<input type="text" name="smId" class="smId" value=""><br/><br/>
	证件号:<input type="text" name="cardNo" class="cardNo" value="120102198504095325"><br/><br/>
	<input type="button" value="提交" onclick="tijiao()">
</center>
<script type="text/javascript">
	function tijiao(){
		var merchantId=$(".merchantId").val();
		var merchOrderId=$(".merchOrderId").val();
		var amount=$(".amount").val();
		var industryNo=$(".industryNo").val();
		var bankNo=$(".bankNo").val();
		var name=$(".name").val();
		var cardNo=$(".cardNo").val();
		var mobileNo=$(".mobileNo").val();
		var smId=$(".smId").val();
		var data={"merchantId":merchantId,"merchOrderId":merchOrderId,"amount":amount,"industryNo":industryNo,"bankNo":bankNo,"name":name,"cardNo":cardNo,"mobileNo":mobileNo,"smId":smId};
		$.ajax({
			url:"${pageContext.request.contextPath}/clientH5Controller/merchantOrderParameter.action",
			data:data,
			type:"post",
			success:function(data){
				console.info(data);
				var data1={"merchantId":merchantId,"merchOrderId":merchOrderId,"amount":amount,"industryNo":industryNo,"bankNo":bankNo,"name":name,"cardNo":cardNo,"mobileNo":mobileNo,"smId":smId,"sign":data};
				$.ajax({
					url:"${pageContext.request.contextPath}/clientH5Controller/merchantOrderApi.action",
					data:data1,
					type:"post",
					dataType:"json",
					success:function(data){
						console.info(data);
						var str ="merchantId="+data.merchantId+"&smId="+data.smId+"&merchOrderId="+data.merchOrderId+"&tradeTime="+data.tradeTime+"&complated="+data.complated+"&remain="+data.remain+"&expTime="+data.expTime+"&cardNo="+data.cardNo+"&name="+data.name+"&mobileNo="+data.mobileNo+"&bankNo="+data.bankNo+"&amount="+data.amount+"&tradeTime="+data.tradeTime+"&industryNo="+industryNo;
						console.info(str);
						window.location.href="yilianApiPay.jsp?"+str;
					}
				});
			}
		});
	}
</script>
</body>
</html>