<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>
<body>
	<form action="http://60.28.24.164:8102/app_posp/clientH5Controller/merchantOrderTestH5.action" method="post">
		<input type="text" name="merchantId1" value="100120242118015"/>
		<input type="text" name="orderDesc" value="测试商品"/>
		<input type="text" name="amount" value="1000"/>
		<input type="text" name="extData" value="测试"/>
		<input type="text" name="miscData" value=""/>
		<input type="text" name="clientIp" value=""/>
		<input type="text" name="notifyUrl" value=""/>
		<input type="submit" value="提交">
	</form>
</body>
</html>