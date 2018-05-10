<%@ page language="java" import="java.util.*" pageEncoding="UTF-8" %>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
    <title>二维码测试首页</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/404/cmstop-error.css"
          media="all">
</head>
<body class="body-bg">
<div align="center">
    <h1 style="margin-top: 100px;">微信测试</h1>
    <br/>

    <h1 style="margin-top: 5px;"><a href="${pageContext.request.contextPath}/wechat/scan_param.action">微信-扫码测试</a></h1>
    <br/>

    <h1 style="margin-top: 5px;"><a href="${pageContext.request.contextPath}/wechat/micro_param.action">微信-刷卡(小额)支付测试</a></h1>
    <br/>

    <h1 style="margin-top: 5px;"><a href="${pageContext.request.contextPath}/pay/bns/wechat/webpay/webpay_entry.jsp">微信-公众号支付测试</a></h1>
    <br/>

    <h1 style="margin-top: 5px;"><a href="${pageContext.request.contextPath}/pay/bns/wechat/orderquery/orderquery_param.jsp">微信-订单查询</a></h1>
    <br/>
    <hr/>
    <h1 style="margin-top: 5px;"><a href="${pageContext.request.contextPath}/createAYardToPay.action">一码付测试</a></h1>
    <br/>
   <h1 style="margin-top: 5px;"><a href="${pageContext.request.contextPath}/wechat/alipayScan.action">支付宝</a></h1>
  <%--   <br/>
   <h1 style="margin-top: 5px;"><a href="${pageContext.request.contextPath}/pay/bns/wechat/alipay/alipayScanParam.jsp">支付宝刷卡</a></h1> --%>
   
   <br/>
   <h1 style="margin-top: 5px;"><a href="${pageContext.request.contextPath}/pay/bns/wechat/alipay/select.jsp">支付宝查询接口</a></h1>
   
</div>
</body>
</html>

