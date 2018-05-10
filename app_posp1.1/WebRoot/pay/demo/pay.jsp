<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html >
<html>
<head>
<meta charset="UTF-8">
<title>转到支付页面</title>
</head>
<body>
<h3>自动提交支付平台</h3>
<p>pageurl:${temp.pageurl}</p>
<p>bgurl:${temp.bgurl}</p>
<p>transactionid:${temp.transactionid}</p>
<p>orderAmount:${temp.orderamount}</p>
<p>orderTime:${temp.ordertime}</p>
<p>productName:${temp.productname}</p>
<p>productnum:${temp.productnum}</p>
<p>productdesc:${temp.productdesc}</p>
<p>paytype:${temp.paytype}</p>
<p>bankid:${temp.bankid}</p>
<p>pid:${temp.pid}</p>
<p>bankno:${temp.bankno}</p>
<p>signmsg:${temp.signmsg}</p>
<form name="payForm" action="/app_posp/quick/pay.action" method="post">
	<input type="hidden" name="pageurl" value="${temp.pageurl}">
	<input type="hidden" name="bgurl" value="${temp.bgurl}">
	<input type="hidden" name="transactionid" value="${temp.transactionid}">
	<input type="hidden" name="orderamount" value="${temp.orderamount}">
	<input type="hidden" name="ordertime" value="${temp.ordertime}">
	<input type="hidden" name="productname" value="${temp.productname}">
	<input type="hidden" name="productnum" value="${temp.productnum}">
	<input type="hidden" name="productdesc" value="${temp.productdesc}">
	<input type="hidden" name="paytype" value="${temp.paytype}">
	<input type="hidden" name="bankid" value="${temp.bankid}">
	<input type="hidden" name="bankno" value="${temp.bankno}">
	<input type="hidden" name="pid" value="${temp.pid}">
	<input type="hidden" name="signmsg" value="${temp.signmsg}">
	<input type="submit" name="提交" value="提交支付" >
</form>
</body>
</html>