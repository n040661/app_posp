<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
</head>
<body>
	<form id="pay_form"
		action="https://www.joinpay.com/gateway/gateway_init.action"
		method="post">
		<input type="hidden" name="p5_ProductName" id="p5_ProductName"
			value="%E6%B5%8B%E8%AF%95%E5%95%86%E5%93%81" /> <input type="hidden"
			name="respCode" id="respCode" value="0000" /> <input type="hidden"
			name="p8_NotifyUrl" id="p8_NotifyUrl"
			value="http%3A%2F%2F60.28.24.164%3A8102%2Fapp_posp%2FHJController%2FnotifyUrl.action" />
		<input type="hidden" name="hmac" id="hmac"
			value="fe4ccbcf991846fc946e0c561594d221" /> <input type="hidden"
			name="p1_MerchantNo" id="p1_MerchantNo" value="888100000004071" /> <input
			type="hidden" name="p7_ReturnUrl" id="p7_ReturnUrl"
			value="http%3A%2F%2F60.28.24.164%3A8102%2Fapp_posp%2FHJController%2FreturnUrl.action" />
		<input type="hidden" name="respMsg" id="respMsg" value="成功" /><input
			type="hidden" name="p4_Cur" id="p4_Cur" value="1" /><input
			type="hidden" name="p3_Amount" id="p3_Amount" value="1.0" /><input
			type="hidden" name="pa_OrderPeriod" id="pa_OrderPeriod"
			value="20171226144412" /><input type="hidden" name="p2_OrderNo"
			id="p2_OrderNo" value="1514270652606" /><input type="hidden"
			name="p6_Mp" id="p6_Mp" value="%E6%8B%85%E6%8B%85%E9%9D%A2" /><input
			type="hidden" name="p9_FrpCode" id="p9_FrpCode" value="CCB_NET_B2C" />
	</form>
</body>
<script type="text/javascript">
	document.all.pay_form.submit();
</script>
</html>