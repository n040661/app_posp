<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ page import="xdt.util.UtilDate"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>Insert title here</title>
</head>
<body>
	<form
		action="${pageContext.request.contextPath }/payeasy/signForWap.action"
		method="post">

		<center>
			<table>
				<tr>
					<td>机构号:</td>
					<td><input type="text" name="merchantId" value="10037021742"
						id="merchantId"></td>
				</tr>
				<tr>
					<td>订单编号:</td>
					<td><input type="text" name="v_oid"
						value="<%=UtilDate.randomOrder()%>" id="v_oid"></td>
				</tr>
				<tr>
					<td>姓名:</td>
					<td><input type="text" name="v_rcvname" value="13240"
						id="v_rcvname"></td>
				</tr>
				<tr>
					<td>地址:</td>
					<td><input type="text" name="v_rcvaddr" value="13240"
						id="v_rcvaddr"></td>
				</tr>
				<tr>
					<td>电话:</td>
					<td><input type="text" name="v_rcvtel" value="13240"
						id="v_rcvtel"></td>
				</tr>
				<tr>
					<td>邮政编码:</td>
					<td><input type="text" name="v_rcvpost" value="13240"
						id="v_rcvpost"></td>
				</tr>
				<tr>
					<td>订单总金额:</td>
					<td><input type="text" name="v_amount" value="1" id="v_amount"></td>
				</tr>
				<tr>
					<td>订单日期:</td>
					<td><input type="text" name="v_ymd"
						value="<%=UtilDate.getDate()%>" id="v_ymd"></td>
				</tr>
				<tr>
					<td>配货状态:</td>
					<td><input type="text" name="v_orderstatus" value="1"
						id="v_orderstatus"></td>
				</tr>
				<tr>
					<td>订货人姓名:</td>
					<td><input type="text" name="v_ordername" value="13240"
						id="v_ordername"></td>
				</tr>
				<tr>
					<td>支付币种:</td>
					<td><input type="text" name="v_moneytype" value="0"
						id="v_moneytype"></td>
				</tr>
				<tr>
					<td>支付类型:</td>
					<td><select name="v_type">
							<option selected="selected" value="2">直连银行</option>
							<option value="1">标准</option>
					</select></td>
				</tr>
				<tr>
					<td>银行类型:</td>
					<td><select name="v_pmode">
							<option selected="selected" value="9">工商银行</option>
							<option value="3">招商银行</option>
							<option value="4">建设银行</option>
							<option value="14">平安银行</option>
							<option value="28">民生银行</option>
							<option value="33">兴业银行</option>
							<option value="43">农业银行</option>
							<option value="44">广发银行</option>
							<option value="50">北京银行</option>
							<option value="59">中国邮政</option>
							<option value="60">华夏银行</option>
							<option value="67">交通银行</option>
							<option value="69">浦发银行</option>
							<option value="74">光大银行</option>
							<option value="75">北京农村商业银行</option>
							<option value="83">渤海银行</option>
							<option value="84">中信银行</option>
							<option value="85">中国银行</option>
							<option value="121">上海银行</option>
							<option value="126">银联银行</option>
					</select></td>
				</tr>
				<tr>
					<td>返回商户页面:</td>
					<td><input type="text" name="v_url"
						value="http://60.28.24.164:8102/app_posp/pay/payeasy/result.jsp"
						id="v_url"></td>
				</tr>
				<tr>
					<td>后台通知地址:</td>
					<td><input type="text" name="v_bgurl"
						value="http://60.28.24.164:8102/app_posp/payeasy/pagePayResult.action"
						id="v_bgurl"></td>
				</tr>
				<tr>
					<td colspan="2"><input type="submit" value="sharingPay"></td>
				</tr>
			</table>
		</center>
	</form>
</body>
</html>