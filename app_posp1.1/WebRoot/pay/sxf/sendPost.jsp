<%@page import="java.net.URLDecoder"%>
<%@page import="xdt.dto.sxf.SXFUtil"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-3.1.1.min.js"></script>
</head>
<body style="text-align: center">
<%
	String mercNo = request.getParameter("mercNo");
	
	String tranCd = request.getParameter("tranCd");
	String version = request.getParameter("version");
	String reqData = request.getParameter("reqData");
	reqData =reqData.replace(" ", "+");
	//reqData= URLDecoder.decode(reqData,"UTF-8");
	String ip =request.getParameter("ip");
	String encodeType =request.getParameter("encodeType");
	String type =request.getParameter("type");
	String sign =request.getParameter("sign");
	System.out.print("sign"+sign);
	sign =sign.replace(" ", "+");
	//sign= URLDecoder.decode(sign,"UTF-8");

%>
<h4>正在跳转，请稍后...</h4>
	<form action="<%=SXFUtil.url%>" method="POST">
		<div style="display: none"><!--   -->
			<input name="mercNo" value="<%=mercNo%>"/>
			<input name="tranCd" value="<%=tranCd%>"/>
			<input name="version" value="<%=version%>"/>
			<input name="reqData" value="<%=reqData%>"/>
			<input name="ip" value=""/>
			<input name="sign" value="<%=sign%>"/>
			<input name="encodeType" value="RSA#RSA"/>
			<input name="type" value="<%=type%>"/>
		</div>
	</form>
	<script type="text/javascript">
		 $(function(){
			$("form").submit();
		}); 
	</script> 
</body>
</html>