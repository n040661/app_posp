<%@page import="java.net.URLEncoder"%>
<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <title>B2C支付余额查询提交</title>
  </head>
  <body onload="sub();">
    <form id="test1" name="test1" method="post" action="${pageContext.request.contextPath }/payeasy/merchantDownload.action"> 
			<table>
				<tr>
					<th>商户号:</th>
					<td><input type="text" id="merchantId" name="merchantId"
						value="${temp.merchantId}"></td>
				</tr>
				<tr>
					<th>签名:</th>
					<td><input type="text" id="v_mac" name="v_mac"
						value="${temp.v_mac}" size="50"></td>
				</tr>
				<tr>
					<td colspan="2"><input type="submit" value="查询"></td>
				</tr>
			</table>
    </form>
  </body>
</html>  
  
