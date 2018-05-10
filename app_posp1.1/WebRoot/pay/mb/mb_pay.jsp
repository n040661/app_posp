<%@ page language="java" contentType="text/html; charset=UTF-8"
    import="xdt.util.*" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>支付页面</title>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-3.1.1.min.js"></script>
</head>
<body>
<center>
<form action="${pageContext.request.contextPath}/MBController/payClienty.action" method="post">
商户号:<input type="text" name="merId" class="merId" value="10032061473"><br />
订单号:<input type="text" name="orderId" class="orderId" value="CJZF<%=System.currentTimeMillis()%>"><br />
金额： <input type="text" class="transAmount" name="transAmount" value="10"/><br />
交易时间： <input type="text" class="transDate" name="transDate" value="<%=UtilDate.getOrderNum()%>"/><br />
交易渠道： <input type="text" class="transChanlName" name="transChanlName" value="UNIONPAY"/><br />
开户银行：<input type="text" class="openBankName" name="openBankName" value="中国工商银行"/><br />
订单详情：<input type="text" class="orderDesc" name="orderDesc" value="交易"/><br />
自定义域：<input type="text" class="dev" name="dev" value="测试"/><br />
异步地址： <input type="text" class="backNotifyUrl" name="backNotifyUrl" value="http://60.28.24.164:8104/app_posp/MBController/payClientyaction"/><br />
前台通知地址I： <input type="text" class="pageNotifyUrl" name="pageNotifyUrl" value="http://60.28.24.164:8104/app_posp/MBController/payClientyaction"/><br />
类型： <input type="text" class="type" name="type" value="cj007"/><br />
<input type="submit" value="提交"><br />
</form>
<div id ="div"></div>
<br />
</center>
</body>

</html>