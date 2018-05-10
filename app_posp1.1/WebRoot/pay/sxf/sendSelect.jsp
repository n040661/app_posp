<%@page import="xdt.util.utils.PaymentUtils"%>
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
	String orderNo=request.getParameter("orderNo");
	String tranCd = request.getParameter("tranCd");
	String version = request.getParameter("version");
	System.out.println("orderNo:"+orderNo);
	//String reqData = new String(request.getParameter("reqData").getBytes("ISO-8859-1"),"UTF-8");
	//reqData =reqData.replace(" ", "+");
	String ip =request.getParameter("ip");
	net.sf.json.JSONObject json = new net.sf.json.JSONObject();
	json.put("orderNo", new String(orderNo.getBytes("ISO-8859-1"),"UTF-8"));
	String resData = PaymentUtils.encrypt(json.toString(), SXFUtil.publicKey);
	net.sf.json.JSONObject main = new net.sf.json.JSONObject();
	main.put("mercNo", mercNo);
	main.put("tranCd", tranCd);
	main.put("version", version);
	main.put("reqData", resData);
	main.put("ip", ip);
	System.out.println("main:"+main.toString());
	// 加签名，注意参数顺序
	String sign = PaymentUtils.sign(main.toString(), SXFUtil.mercPrivateKey);
	//String sign =request.getParameter("sign");
	//System.out.println("sign1："+sign);
	//sign =sign.replace(" ", "+");
	//System.out.println("sign2："+sign);
	main.put("sign", sign);
	main.put("encodeType", "RSA#RSA");
	String str =main.toString();
	System.out.println("str:"+str);
%>
<h4>正在跳转，请稍后...</h4>
	<form action="<%=SXFUtil.selectUrl%>" method="POST">
		<div style="display: none"><!--  -->
		<textarea rows="" cols="" name="_t"><%=str%></textarea>
		</div>
	</form>
	<script type="text/javascript">
		 $(function(){
			$("form").submit();
		}); 
	</script> 
</body>
</html>