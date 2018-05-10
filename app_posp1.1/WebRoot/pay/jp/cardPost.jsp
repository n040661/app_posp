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
	<form action="<%=request.getAttribute("cardUrl") %>" method="post" id="from" style="display: none;">
		<input type="text" name="charset" value="<%=request.getAttribute("charset")%>"/>
		<input type="text" name="version" value="<%=request.getAttribute("version")%>"/>
		<input type="text" name="service" value="<%=request.getAttribute("service")%>"/>
		<input type="text" name="signType" value="<%=request.getAttribute("signType")%>"/>
		<input type="text" name="merchantId" value="<%=request.getAttribute("merchantId")%>"/>
		<input type="text" name="requestTime" value="<%=request.getAttribute("requestTime")%>"/>
		<input type="text" name="requestId" value="<%=request.getAttribute("requestId")%>"/>
		<input type="text" name="pageReturnUrl" value="<%=request.getAttribute("pageReturnUrl")%>"/>
		<input type="text" name="notifyUrl" value="<%=request.getAttribute("notifyUrl")%>"/>
		<input type="text" name="merchantName" value="<%=request.getAttribute("merchantName")%>"/>
		<%-- <input type="text" name="subMerchantId" value="<%=request.getAttribute("subMerchantId")%>"/> --%>
		<input type="text" name="memberId" value="<%=request.getAttribute("memberId")%>"/>
		<input type="text" name="orderTime" value="<%=request.getAttribute("orderTime")%>"/>
		<input type="text" name="orderId" value="<%=request.getAttribute("orderId")%>"/>
		<input type="text" name="totalAmount" value="<%=request.getAttribute("totalAmount")%>"/>
		<input type="text" name="currency" value="<%=request.getAttribute("currency")%>"/>
		<input type="text" name="bankAbbr" value="<%=request.getAttribute("bankAbbr")%>"/>
		<input type="text" name="cardType" value="<%=request.getAttribute("cardType")%>"/>
		<input type="text" name="payType" value="<%=request.getAttribute("payType")%>"/>
		<input type="text" name="validUnit" value="<%=request.getAttribute("validUnit")%>"/>
		<input type="text" name="validNum" value="<%=request.getAttribute("validNum")%>"/>
		<input type="text" name="goodsName" value="<%=request.getAttribute("goodsName")%>"/>
		<%-- <input type="text" name="goodsId" value="<%=request.getAttribute("goodsId")%>"/>
		<input type="text" name="goodsDesc" value="<%=request.getAttribute("goodsDesc")%>"/> --%>
		<input type="text" name="merchantSign" value="<%=request.getAttribute("merchantSign")%>"/>
		<input type="text" name="merchantCert" value="<%=request.getAttribute("merchantCert")%>"/>
	</form>
</center>
<script type="text/javascript">
	  $("#from").submit();  
</script>
</body>
</html>