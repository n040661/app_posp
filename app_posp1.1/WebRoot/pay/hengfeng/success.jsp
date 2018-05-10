<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html lang="en">
<head>
    <meta charset="utf-8">
	<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
	<meta name="description" content="">
	<meta name="renderer" content="webkit">
	<meta name="viewport" content="initial-scale=1.0, maximum-scale=1.0, user-scalable=no" />
	<meta name="viewport" content="width=device-width, initial-scale=1">
	<meta name="viewport" content="user-scalable=0" />	
	<link rel="stylesheet" href="${pageContext.request.contextPath }/css/success/index.css">
	<title>成功</title>
</head>
<body>
<div class="Withdrawals">
	<div class="payment">
		<img src="${pageContext.request.contextPath }/images/1.png" alt="" />
	</div>
	<div class="complete">
		<img src="${pageContext.request.contextPath }/images/2.png" alt="" />
	</div>
	<div class="Numbers">
		<div class="dahai">支付订单号：</div>
		<div class="mima"><input type="text" id="orderId" value="${temp.orderId}" height="" readonly="readonly"></div>
		<div class="clear"></div> 
	</div>
	<div class="confirm">
		<img src="${pageContext.request.contextPath }/images/3.png"/>
	</div>
	<div class="rw-li">请截图保存本订单号</div>
	<div class="rw-li">请关注如下公众号，查询更多支付咨询！</div>
	<div class="erwem">
		<img src="${pageContext.request.contextPath }/images/erweima_.jpg" alt="" />
	</div>
	<div class="rw-li">客服咨询电话：400-022-6763</div>
</div>
</body>
<script>
	var w = document.documentElement.clientWidth;
		if(w > 640) {
			w = 640
		}
		document.documentElement.style.fontSize = 20 / 320 * w + 'px';
		window.onresize = function() {
			document.documentElement.style.fontSize = 20 / 320 * w + 'px';
	};
</script>
</html>