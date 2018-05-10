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
	商户号:<input type="text" name="merchantId" class="merchantId" value="<%=request.getParameter("merchantId")%>"><br/><br/>
	订单号:<input type="text" name="merchOrderId" class="merchOrderId" value="<%=request.getParameter("merchOrderId")%>"><br/><br/>
	金额:<input type="text" name="amount" class="amount" value="1000"><br/><br/>
	银行卡号:<input type="text" name="bankNo" class="bankNo" value="<%=request.getParameter("bankNo")%>"><br/><br/>
	姓名:<input type="text" name="name" class="name" value="<%=new String(request.getParameter("name").getBytes("ISO-8859-1"),"UTF-8")%>"><br/><br/>
	手机号:<input type="text" name="mobileNo" class="mobileNo" value="<%=request.getParameter("mobileNo")%>"><br/><br/>
	短信凭证:<input type="text" name="smId" class="smId" value="<%=request.getParameter("smId")%>"><br/><br/>
	证件号:<input type="text" name="cardNo" class="cardNo" value="<%=request.getParameter("cardNo")%>"><br/><br/>
	行业编码:<input type="text" name="industryNo" class="industryNo" value="<%=request.getParameter("industryNo")==null?"":request.getParameter("industryNo")%>"><br/><br/>
	提交时间：<input type="text" name="tradeTime" class="tradeTime" value="<%=request.getParameter("tradeTime")%>"><br/><br/>
	商户保留信息:<input type="text" name="extData" class="extData" value="大饼鸡蛋"><br/><br/>
	订单描述:<input type="text" name="orderDesc" class="orderDesc" value="大饼鸡蛋"><br/><br/>
	异步通知地址:<input type="text" name="notifyUrl" class="notifyUrl" value="http://60.28.24.164:8104/app_posp/TFBController/notifyUrl.action"><br/><br/>
	省份:<input type="text" name="city" class="city" value="天津市,天津市"><br/><br/>
	短信验证码:<input type="text" name="smCode" class="smCode" value=""><br/><br/>
	<input type="button" value="提交" onclick="tijiao()">
	<div class="div"></div>
</center>
<script type="text/javascript">
	function tijiao(){
		var merchantId=$(".merchantId").val();
		var merchOrderId=$(".merchOrderId").val();
		var amount=$(".amount").val();
		var industryNo=$(".industryNo").val();
		var bankNo=$(".bankNo").val();
		var city=$(".city").val();
		var name=$(".name").val();
		var mobileNo=$(".mobileNo").val();
		var smId=$(".smId").val();
		var extData=$(".extData").val();
		var orderDesc=$(".orderDesc").val();
		var notifyUrl=$(".notifyUrl").val();
		var cardNo=$(".cardNo").val();
		var smCode=$(".smCode").val();
		var tradeTime=$(".tradeTime").val();
		var data={"merchantId":merchantId,"merchOrderId":merchOrderId,"amount":amount,"industryNo":industryNo,"bankNo":bankNo,"name":name,"cardNo":cardNo,"mobileNo":mobileNo,"smId":smId,"city":city,"extData":extData,"orderDesc":orderDesc,"notifyUrl":notifyUrl,"smCode":smCode,"tradeTime":tradeTime};
		$.ajax({
			url:"${pageContext.request.contextPath}/clientH5Controller/merchantOrderParameter.action",
			data:data,
			type:"post",
			success:function(data){
				console.info(data);
				var data1={"merchantId":merchantId,"merchOrderId":merchOrderId,"amount":amount,"industryNo":industryNo,"bankNo":bankNo,"name":name,"cardNo":cardNo,"mobileNo":mobileNo,"smId":smId,"city":city,"extData":extData,"orderDesc":orderDesc,"notifyUrl":notifyUrl,"smCode":smCode,"tradeTime":tradeTime,"sign":data};
				$.ajax({
					url:"${pageContext.request.contextPath}/clientH5Controller/merchantOrderApiPay.action",
					data:data1,
					type:"post",
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