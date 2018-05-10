<HTML>
<HEAD>
<meta http-equiv="content-type" content="text/html; charset=UTF-8">
<TITLE>网关支付</TITLE>
</HEAD>
<BODY>
	<div style="display: none">
		<form action=https://ipay.cmbc.com.cn:9012/gwpay/payServlet
			id="orderform" method="POST">
			<tr>
				<td>sign:<input name=sign
					value=O39fjXHLvuqmCg2FlKaDN84Q3CP9b9QNEat7+EHj3BCcHYCVu7Ho/WAomSHGqF514qW9sB6685wj8tln+rHXrA==></input></td>
			</tr>
			<tr>
				<td>valid_order:<input name=valid_order value=></input></td>
			</tr>
			<tr>
				<td>no_order:<input name=no_order
					value=252017062718042234821548></input></td>
			</tr>
			<tr>
				<td>oid_partner:<input name=oid_partner value=GWP_JSDZSW></input></td>
			</tr>
			<tr>
				<td>pay_type:<input name=pay_type value=1></input></td>
			</tr>
			<tr>
				<td>url_return:<input name=url_return
					value=http://60.28.24.164:8105/app_posp/test/qrcode/JsbgPayResult.action></input></td>
			</tr>
			<tr>
				<td>notify_url:<input name=notify_url
					value=http://www.okpay365.com/payment/MsxmNotifyPayResultInterface/NotifyGatewayPayResultServlet></input></td>
			</tr>
			<tr>
				<td>name_goods:<input name=name_goods value=天津畅捷支付></input></td>
			</tr>
			<tr>
				<td>dt_order:<input name=dt_order value=20170627180422></input></td>
			</tr>
			<tr>
				<td>user_id:<input name=user_id value=928000000012394></input></td>
			</tr>
			<tr>
				<td>money_order:<input name=money_order value=10.00></input></td>
			</tr>
			<tr>
				<td>bank_code:<input name=bank_code value=01020000></input></td>
			</tr>
		</form>
	</div>
</BODY>
<script>
	window.onload = function() {
		document.forms["orderform"].submit();
	}
</script>
</HTML>
