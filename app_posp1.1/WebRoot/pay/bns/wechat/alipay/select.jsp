<%@page import="xdt.quickpay.nbs.common.constant.Constant"%>
<%@page import="xdt.quickpay.nbs.common.util.RandomUtil"%>
<%@ page language="java" contentType="text/html; charset=utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
</head>
<body>
	<center>
		<form action="${pageContext.request.contextPath}/wechat/alipayScanSelect.action" method="post">
			商户号:<input type="text" class="mch_id" name="mch_id" value="100123112343005"/><br />
			商户订单号:<input type="text" class="out_trade_no" name="out_trade_no" value=""/><br />
			<input type="submit"  value="生成"/><br>
			</form>
	</center>
</body>
</html>