<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>商户订单数据(模拟)</title>
</head>
<body onload="sharingPay()">
	<form id="pay_form" action="${pageContext.request.contextPath }/clearPay/quick/clear/pay.action" method="post">
	    <input type="hidden" name="v_version" value="${temp.v_version}"> <br>
	    <input type="hidden" name="v_mid" value="${temp.v_mid}"><br>
	    <input type="hidden" name="v_oid" value="${temp.v_oid}"><br>
	    <input type="hidden" name="v_txnAmt" value="${temp.v_txnAmt}"><br>
		<input type="hidden" name="v_time" value="${temp.v_time}"><br>
		<input type="hidden" name="v_cardNo" value="${temp.v_cardNo}"><br>
		<input type="hidden" name="v_realName" value="${temp.v_realName}"><br>		 	
		<input type="hidden" name="v_cert_no" value="${temp.v_cert_no}"><br>
		<input type="hidden" name="v_orderInfo" value="${temp.v_orderInfo}"><br>
		<input type="hidden" name="v_phone" value="${temp.v_phone}"><br>
	    <input type="hidden" name="v_notify_url" value="${temp.v_notify_url}"><br>
		<input type="hidden" name="v_url" value="${temp.v_url}"><br>
		<input type="hidden" name="v_cvn2" value="${temp.v_cvn2}"><br>
		<input type="hidden" name="v_expired" value="${temp.v_expired}"><br>
		<input type="hidden" name="v_attach" value="${temp.v_attach}"><br>
		<input type="hidden" name="v_userId" value="${temp.v_userId}"><br>
		<input type="hidden" name="v_userFee" value="${temp.v_userFee}"><br>
		<input type="hidden" name="v_settleCardNo" value="${temp.v_settleCardNo}"><br>
		<input type="hidden" name="v_settleName" value="${temp.v_settleName}"><br>
		<input type="hidden" name="v_settleUserFee" value="${temp.v_settleUserFee}"><br>
		<input type="hidden" name="v_settlePmsBankNo" value="${temp.v_settlePmsBankNo}"><br>
		<input type="hidden" name="v_settlePhone" value="${temp.v_settlePhone}"><br>			
		<input type="hidden" name="v_sign" value="${temp.v_sign}"><br>
	</form>
</body>
<script type="text/javascript">

  function sharingPay(){
	  
	  document.all.pay_form.submit();
  }
</script>
</html>
