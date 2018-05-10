<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    <%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
</head>
<body>
<form action="http://60.28.24.164:8107/app_posp/YBController/pay.action" method="post">
<input type="hidden" name="redirectUrl" value="http://101.200.38.184:8008/gateway/notify/async/upin/UPIN20180320100050">
<input type="hidden" name="orderId" value="66662018041910440154">
<input type="hidden" name="userNo" value="66662018041910440154">
<input type="hidden" name="cardType" value="DEBIT">
<input type="hidden" name="memo" value="">
<input type="hidden" name="v_sign" value="0772033AF194B420F62B949038EDCC6E">
<input type="hidden" name="paymentParamExt" value="">
<input type="hidden" name="riskParamExt" value="">
<input type="hidden" name="orderAmount" value="10.00">
<input type="hidden" name="industryParamExt" value="">
<input type="hidden" name="csUrl" value="">
<input type="hidden" name="requestDate" value="2018-04-19 15:43:03">
<input type="hidden" name="notifyUrl" value="http://111.230.194.185:8080/gateway/notify/sync/upin/UPIN20180112100104">
<input type="hidden" name="timeoutExpress" value="">
<input type="hidden" name="userType" value="USER_ID">
<input type="hidden" name="directPayType" value="YJZF">
<input type="hidden" name="goodsName" value="网银测试商品名称">
<input type="hidden" name="merchantNo" value="10036049569">
<input type="hidden" name="timestamp" value="1524121125000">
<input type="hidden" name="goodsDesc" value="网银测试商品名称">
<input type="submit" name="submit" value="生成">
</form>
</body></html>