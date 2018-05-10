<%@ page contentType="text/html;charset=UTF-8"
	import="xdt.quickpay.hengfeng.util.*" language="java"%>
<%@page import="xdt.dto.jsds.JsdsRequestDto"%>
<%@page import="java.util.*"%>
<%@page import="xdt.util.*"%>
<%
	String path = request.getContextPath();
	String service = request.getParameter("service");
	String merchantCode = request.getParameter("merchantCode");
	String orderNum = request.getParameter("orderNum");
	String returnUrl = request.getParameter("returnUrl");
	String bankCode = request.getParameter("bankCode");
	String notifyUrl = request.getParameter("notifyUrl");
	String commodityName = new String(request.getParameter("commodityName").getBytes("iso-8859-1"),"utf-8");	
	String transMoney = request.getParameter("transMoney");
	JsdsRequestDto reqData = new JsdsRequestDto();
	reqData.setService(service);
	reqData.setMerchantCode(merchantCode);
	reqData.setOrderNum(orderNum);
	reqData.setReturnUrl(returnUrl);
	reqData.setBankCode(bankCode);
	reqData.setNotifyUrl(notifyUrl);
	reqData.setCommodityName(commodityName);
	reqData.setTransMoney(transMoney);
	Map<String, String> result = new HashMap<String, String>();
	result.putAll(JsdsUtil.beanToMap(reqData));
	Set<String> keys = new HashSet<String>();
	// 剔除值为空的
	for (String key : result.keySet()) {
		if ("".equals(result.get(key)) || result.get(key) == null) {
			keys.add(key);
		}
	}
	for (String key : keys) {
		result.remove(key);
	}
	result.remove("sign");
	String sign=JsdsUtil.sign(result, "947d48d9a40f44318595237632123456");
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta charset="utf-8">
<title>测试页面</title>
</head>

<body>
	<form id="form" action="${pageContext.request.contextPath }/live/qrcode/interface.action" method="post">
		<table>
			<tr>
				<td>服务类型：</td>
				<td><input type="text" name="service" value="<%=service%>"></td>
			</tr>
			<tr>
				<td>商户号：</td>
				<td><input type="text" name="merchantCode"
					value="<%=merchantCode%>"></td>
			</tr>
			<tr>
				<td>订单号:
				</td>
				<td><input type="text" name="orderNum"
					value="<%=orderNum%>">(<font color="red">查询时候添加自己的订单号，下面都不要填</font>)</td>
			</tr>
			<tr>
				<td>前台通知页面地址：</td>
				<td><input type="text" name="returnUrl"
					value="<%=returnUrl%>"></td>
			</tr>
			<tr>
				<td>银行编码：</td>
				<td><input type="text" name="bankCode" value="<%=bankCode%>"></td>
			</tr>
			<tr>
				<td>异步通知url：</td>
				<td><input type="text" name="notifyUrl"
					value="<%=notifyUrl%>"></td>
			</tr>
			<tr>
				<td>商品名称：</td>
				<td><input type="text" name="commodityName" value="<%=commodityName%>"></td>
			</tr>
			<tr>
				<td>金额：</td>
				<td><input type="text" name="transMoney" value="<%=transMoney%>"></td>
			</tr>
			<tr>
				<td>签名：</td>
				<td><input type="text" name="sign" value="<%=sign%>"></td>
			</tr>
			<tr>
				<td colspan="2"><input type="submit"
					value="确定"></td>
			</tr>
		</table>
	</form>
</body>
</html>
