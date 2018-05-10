<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
	
<%
           String array1=request.getParameter("array");
           String[] array=array1.split("\\||");
           String 
           

%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>公众号支付</title>
<link rel="stylesheet" type="text/css"
	href="${pageContext.request.contextPath}/css/404/cmstop-error.css"
	media="all">
</head>
<body class="body-bg">


	<div align="center">
		<a href="${pageContext.request.contextPath}/index.jsp"
			style="margin: 50px 0 0 0;" class="btn">返回网站首页</a>
		<h1 style="margin-top: 10px;">公众号支付请求参数</h1>

    <%=request.getParameter("array") %>
<%-- 		<form name="form" action="<%=request.getParameter("brcb_gateway_url") %>" method="post">
			<table>
				<tr>
					<td><input style="width: 300px;" id="service_type" type="text" 
						name="service_type" value="<%=request.getParameter("service_type") %>" /></td>
				</tr>

				<tr>
					<td><input style="width: 300px;" id="mch_id" name="mch_id" type="text"
						value="<%=request.getParameter("mch_id") %>" /></td>
				</tr>
				<tr>
					<td align="right">商品编号:</td>
					<td><input style="width: 300px;" id="out_trade_no" readonly="readonly"
						name="out_trade_no" value="<%=request.getParameter("out_trade_no") %>" /></td>
				</tr>
				<tr>
					<td align="right">商品名称:</td>
					<td><input style="width: 300px;" id="body" name="body" readonly="readonly"
						value="<%=request.getParameter("body") %>" /></td>
				</tr>
				<tr>
					<td align="right">价格:</td>
					<td><input style="width: 300px;" id="total_fee" readonly="readonly"
						name="total_fee" value="<%=request.getParameter("total_fee") %>" /></td>
				</tr>
				<tr>
					<td><input style="width: 300px;" id="spbill_create_ip" type="text"
						name="spbill_create_ip" value="<%=request.getParameter("spbill_create_ip") %>" /></td>
				</tr>
				<tr>
					<td><input style="width: 300px;" id="notify_url" type="text"
						name="notify_url" value="<%=request.getParameter("notify_url") %>" /></td>
				</tr>

				<tr>
					<td><input style="width: 300px;" id="nonce_str" type="text"
						name="nonce_str" value="<%=request.getParameter("nonce_str") %>" /></td>
				</tr>
				<tr>
					<td><input style="width: 300px;" id="sign" name="sign" type="text"
						value="<%=request.getParameter("sign") %>" /></td>
				</tr>
				<tr>
					<td colspan="2" align="right"><input type="submit" value="提交" />
					</td>
				</tr>
			</table>

		</form> --%>
	</div>
</body>