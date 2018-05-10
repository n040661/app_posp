<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<head>
    <meta charset="utf-8">
	<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
	<meta name="description" content="">
	<meta name="renderer" content="webkit">
	<meta name="viewport" content="initial-scale=1.0, maximum-scale=1.0, user-scalable=no" />
	<meta name="viewport" content="width=device-width, initial-scale=1">
	<meta name="viewport" content="user-scalable=0" />	
	<link rel="stylesheet" href="${pageContext.request.contextPath }/css/index.css">
	<title>提现成功</title>
</head>
<body>
<div class="Withdrawals" style="margin-top: 3%;overflow-x:hidden;">
	<div class="logo">
		<img src="${pageContext.request.contextPath }/images/logo.png" alt="" />
	</div>
	<div class="jie_g">支付结果通知</div>
	<div class="Numbers">
		<div class="dahai">支付单号：</div>
	</div>
	<div class="Numbers">
		<div class="dahai">支付单号：</div>
		<div class="mima"><input type="text" id="orderid" value="<%=request.getParameter("orderid")%>" style="color:#000000;border:none;"></div>
		<div class="clear"></div> 
	</div>
	<div class="Numbers">
		<div class="dahai">提现单号：</div>
		<div class="mima"><input type="text" id="batch_no" value="<%=request.getParameter("batch_no")%>" style="color:#000000;border:none;"></div>
		<div class="clear"></div> 
	</div>
	<div class="Success">支付成功,未结算</div>
	<div class="rw-li"><font size="6"><b>请截图保存本订单号</b></font></div>
	<div class="rw-li"><font size="6"><b>请关注如下公众号，查询更多支付咨询！</b></font></div>
	<div class="erwem">
		<img src="${pageContext.request.contextPath }/images/erweima_.jpg" alt="" />
	</div>
	<div class="rw-li">客服咨询电话：400-022-6763</div>
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