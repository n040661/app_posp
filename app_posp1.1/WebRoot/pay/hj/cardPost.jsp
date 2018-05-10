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
<center><!-- style="display: none;" -->
	<font>正在跳转请稍等...</font>
	<form action="<%=request.getAttribute("cardPayUrl")%>" method="post" id="from" style="display: none;">
		<input type="text" name="p1_MerchantNo" value="<%=request.getAttribute("p1_MerchantNo")%>"/>
		<input type="text" name="p2_OrderNo" value="<%=request.getAttribute("p2_OrderNo")%>"/>
		<input type="text" name="p3_Amount" value="<%=request.getAttribute("p3_Amount")%>"/>
		<input type="text" name="p4_Cur" value="<%=request.getAttribute("p4_Cur")%>"/>
		<input type="text" name="p5_ProductName" value="<%=request.getAttribute("p5_ProductName")%>"/>
		<input type="text" name="p6_Mp" value="<%=request.getAttribute("p6_Mp")%>"/>
		<input type="text" name="p7_ReturnUrl" value="<%=request.getAttribute("p7_ReturnUrl")%>"/>
		<input type="text" name="p8_NotifyUrl" value="<%=request.getAttribute("p8_NotifyUrl")%>"/>
		<%
			if(request.getAttribute("p9_FrpCode")!=null){
				%>
				<input type="text" name="p9_FrpCode" value="<%=request.getAttribute("p9_FrpCode")%>"/>
				<% 
			}
		%>
		<input type="text" name="pa_OrderPeriod" value="<%=request.getAttribute("pa_OrderPeriod")%>"/>
		<input type="text" name="hmac" value="<%=request.getAttribute("hmac")%>"/>
	</form>
</center>
<script type="text/javascript">
	  $("#from").submit();  
</script>
</body>
</html>