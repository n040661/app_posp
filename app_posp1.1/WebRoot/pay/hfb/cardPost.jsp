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
	<form action="https://c.heepay.com/quick/pc/index.do" method="post" id="from" style="display: none;">
		<input type="text" name="merchantId" value="<%=request.getAttribute("merchantId")%>"/>
		<input type="text" name="merchantOrderNo" value="<%=request.getAttribute("merchantOrderNo")%>"/>
		<input type="text" name="merchantUserId" value="<%=request.getAttribute("merchantUserId")%>"/>
		<input type="text" name="notifyUrl" value="<%=request.getAttribute("notifyUrl")%>"/>
		<input type="text" name="callBackUrl" value="<%=request.getAttribute("callBackUrl")%>"/>
		<input type="text" name="onlineType" value="<%=request.getAttribute("onlineType")%>"/>
		<input type="text" name="payAmount" value="<%=request.getAttribute("payAmount")%>"/>
		<input type="text" name="productCode" value="<%=request.getAttribute("productCode")%>"/>
		<input type="text" name="requestTime" value="<%=request.getAttribute("requestTime")%>"/>
		<input type="text" name="version" value="<%=request.getAttribute("version")%>"/>
		<input type="text" name="bankId" value="<%=request.getAttribute("bankId")%>"/>
		<input type="text" name="bankName" value="<%=request.getAttribute("bankName")%>"/>
		<input type="text" name="bankCardType" value="<%=request.getAttribute("bankCardType")%>"/>
		
		<input type="text" name="signString" value="<%=request.getAttribute("signString")%>"/>
	</form>
</center>
<script type="text/javascript">
	  $("#from").submit();  
</script>
</body>
</html>