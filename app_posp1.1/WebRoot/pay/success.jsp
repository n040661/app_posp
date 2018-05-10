<%@page import="xdt.util.UtilDate"%>
<%@ page contentType="text/html;charset=UTF-8"
	import="xdt.quickpay.hengfeng.util.*" language="java"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<HTML>
<HEAD>
<meta http-equiv="content-type" content="text/html; charset=UTF-8">
<TITLE>网关支付</TITLE>
</HEAD>
<BODY>
	<div style="display: none">
		<form action="${url}"
			id="orderform" method="POST">
			<table>
			<tr>
				<td>sign:<input name=sign
					value="${sign}"></input></td>
			</tr>
			<tr>
				<td>valid_order:<input name=valid_order value="${valid_order}"></input></td>
			</tr>
			<tr>
				<td>no_order:<input name=no_order
					value="${no_order}"></input></td>
			</tr>
			<tr>
				<td>oid_partner:<input name=oid_partner value="${oid_partner}"></input></td>
			</tr>
			<tr>
				<td>pay_type:<input name=pay_type value="${pay_type}"></input></td>
			</tr>
			<tr>
				<td>url_return:<input name=url_return
					value="${url_return}"></input></td>
			</tr>
			<tr>
				<td>notify_url:<input name=notify_url
					value="${notify_url}"></input></td>
			</tr>
			<tr>
				<td>name_goods:<input name=name_goods value="${name_goods}"></input></td>
			</tr>
			<tr>
				<td>dt_order:<input name=dt_order value="${dt_order}"></input></td>
			</tr>
			<tr>
				<td>user_id:<input name=user_id value="${user_id}"></input></td>
			</tr>
			<tr>
				<td>money_order:<input name=money_order value="${money_order}"></input></td>
			</tr>
			<tr>
				<td>bank_code:<input name=bank_code value="${bank_code}"></input></td>
			</tr>
			</table>
		</form>
	</div>
</BODY>
<script>
	window.onload = function() {
		document.forms["orderform"].submit();
	}
</script>
</HTML>
