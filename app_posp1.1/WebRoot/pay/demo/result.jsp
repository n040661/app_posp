<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html >
<html>
<head>
<meta charset="UTF-8">
<title>支付结果</title>
</head>
<body>
<h1>前台查看支付结果</h1>
<p>支付方式:${param.paytype }</p>
<p>银行代码:${param.bankid }</p>
<p>合作伙伴用户编号:${param.pid }</p>
<p>商户交易号:${param.transactionid }</p>
<p>商户订单提交时间:${param.ordertime }</p>
<p>订单金额:${param.orderamount }</p>
<p>支付平台交易号:${param.dealid  }</p>
<p>支付平台交易时间:${param.dealtime }</p>
<p>订单实际支付金额:${param.payamount }</p>
<p>处理结果:${param.payresult }</p>
<p>错误代码:${param.errcode }</p>
<p>签名:${param.signmsg }</p>
</body>
</html>