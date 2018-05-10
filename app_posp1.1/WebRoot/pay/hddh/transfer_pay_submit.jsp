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
	<form id="pay_form"
		action="${pageContext.request.contextPath }/hddh/replacePay.action"
		method="post">
		<input type="hidden" name="merid" value="${temp.merid}"> <br>
		<input type="hidden" name="cooperator_repay_order_id"
			value="${temp.cooperator_repay_order_id}"><br> <input
			type="hidden" name="bank_card_id" value="${temp.bank_card_id}"><br>
		<input type="hidden" name="cooperator_user_id"
			value="${temp.cooperator_user_id}"><br> <input
			type="hidden" name="longitude" value="${temp.longitude}"> <br>
		<input type="hidden" name="latitude" value="${temp.latitude}"><br>
		<input type="hidden" name="rate" value="${temp.rate}"><br>
		<input type="hidden" name="union_callback_url"
			value="${temp.union_callback_url}"><br> <input
			type="hidden" name="cost" value="${temp.cost}"><br> <input
			type="hidden" name="province_name" value="${temp.province_name}"><br>
		<input type="hidden" name="city_name" value="${temp.city_name}"><br>
		<input type="hidden" name="device_id" value="${temp.device_id}"><br>
		<input type="hidden" name="repayItemList"
			value="${temp.repayItemList}"><br> <input type="hidden"
			name="v_sign" value="${temp.v_sign}"><br>
	</form>
</body>
<script type="text/javascript">

  function sharingPay(){
	  
	  document.all.pay_form.submit();
  }
</script>
</html>
