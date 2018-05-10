<%@ page language="java" import="xdt.util.*" pageEncoding="UTF-8"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>

<!DOCTYPE >
<html>
<head>
<base href="<%=basePath%>">

<title>生成二维码</title>
</head>

<body>
		<form action="${pageContext.request.contextPath }/hddh/replacePaySign.action" method="post">
		    <p>商户号:<input type="text" value="100341512318531" name="merid"></p>
		    <p>订单id:<input type="text" value="<%=UtilDate.PayRandomOrder()%>" name="cooperator_repay_order_id"></p>
		    <p>卡号:<input type="text" name="bank_card_id" value="180416000764542846" ></p>
		    <p>合作商用户id:<input type="text" value="<%=UtilDate.getOrderNum()%>"  name="cooperator_user_id"></p>
		    <p>经度:<input type="text" value="117.22429281789958" name="longitude"></p>
		    <p>纬度:<input type="text" value="39.11904074338674" name="latitude"></p>
		    <p>费率:<input type="text" value="0.43" name="rate"></p>
		    <p>代付成本:<input type="text" value="50" name="cost"></p>
		    <p>省份:<input type="text" value="天津市" name="province_name"></p>
		    <p>城市:<input type="text" value="天津市" name="city_name"></p>
		    <p>设备号:<input type="text" value="123456" name="device_id"></p>
		    <p>回调地址:<input type="text" value="http://60.28.24.164:8102/app_posp/TFBController/returnUrl.action" name="union_callback_url"></p>
		    <p>代还计划明细列表:<input type="text" value="2018-04-16 17:33:26|2018-04-16 18:33:26|50215|50000|215|20180416163589" name="repayItemList"></p>
		    <p><input type="submit" value="生成" id="create"></p>
		</form>
		
</body>
</html>
