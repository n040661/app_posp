<%@ page language="java" contentType="text/html; charset=utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-3.1.1.min.js"></script>
</head>
<body>
<center>
商户号:<input type="text" name="merId" class="merId" value="10032061473"><br />
订单号:<input type="text" name="orderId" class="orderId" value="CJZF<%=System.currentTimeMillis()%>"><br />
金额： <input type="text" class="transAmount" name="transAmount" value="1000"/><br />
异步通知地址： <input type="text" class="backNotifyUrl" name="backNotifyUrl" value="http://60.28.24.164:8104/app_posp/TFBController/notifyUrl.action"/><br />
订单或者商品详细描述： <input type="text" class="orderDesc" name="orderDesc" value="大饼鸡蛋"/><br />
商户自定义域： <input type="text" class="dev" name="dev" value=""/><br />
<input type="button" value="提交" onclick="tijiao()"><br />
<br />
<img alt="图片" src="" id="img">
</center>
<script type="text/javascript">
	function tijiao(){
	    var merId = $(".merId").val();
		var orderId = $(".orderId").val();
		var transAmount = $(".transAmount").val();
		var backNotifyUrl =$(".backNotifyUrl").val();
		var orderDesc = $(".orderDesc").val();
		var dev = $(".dev").val();
		
		var data={"merId":merId,"orderId":orderId,"transAmount":transAmount,"backNotifyUrl":backNotifyUrl,"orderDesc":orderDesc,"dev":dev,"type":"cj001"};
		$.ajax({
					url : "${pageContext.request.contextPath}/MBController/paySign.action",
					type : 'post',
					data : data,
					success : function(date) {
						console.info(date);
						var data={"merId":merId,"orderId":orderId,"transAmount":transAmount,"backNotifyUrl":backNotifyUrl,"orderDesc":orderDesc,"dev":dev,"type":"cj001","sign":date};
						$.ajax({
									url : "${pageContext.request.contextPath}/MBController/payClienty.action",
									dataType:"json",
									type : 'post',
									data :data,
									success : function(data) {
										console.info(data);
										$("#img").attr({"src":data.codeImgUrl});
									}
								});
					}
				});
	}
</script>

</body>
</html>