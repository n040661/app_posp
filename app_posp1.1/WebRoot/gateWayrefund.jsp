<%@page import="xdt.util.UtilDate"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-3.1.1.min.js"></script>
</head>
<body>
<center>
	版本号：<input type="text" name="v_version" class="v_version" value="1.0.0.0"/><br /><br />
	商户号:<input type="text" name="v_mid" class="v_mid" value="10032061473"/><br /><br />
	商户代付单号:<input type="text" name="v_oid" class="v_oid" value="<%=System.currentTimeMillis()%>"/><br /><br />
	交易金额:<input type="text" name="v_txnAmt" class="v_txnAmt" value="5"/><br /><br />
	请求时间:<input type="text" name="v_time" class="v_time" value="<%=UtilDate.getOrderNum()%>"/><br /><br />
	异步地址:<input type="text" name="v_notify_url" class="v_notify_url" value="31231"/><br /><br />
	原订单号:<input type="text" name="v_orgBpSerialNum" class="v_orgBpSerialNum" value="1001805091137575436"/><br /><br />
	原订单时间:<input type="text" name="v_orgTransTime" class="v_orgTransTime" value="20180509113735" /><br /><br />
	<input type="button" onclick="daifu()" value="点击退款"/><br /><br />
	<div class="div"></div>
</center>
<script type="text/javascript">
	function daifu(){
		console.info($("input[name]").serialize());
		$.ajax({
			url:"${pageContext.request.contextPath }/gateWay/sign.action",
			type:"post",
			data:$("input[name]").serialize(),
			success:function(data){
				console.info(data);
				$.ajax({
					url:"${pageContext.request.contextPath}/gateWay/yftk.action",
					type:"post",
					dataType:"json",
					data:$("input[name]").serialize()+"&v_sign="+data,
					success:function(data){
						console.info(data);
						$(".div").html(data);
					}
				});
			}
		});
	}
</script>
</body>
</html>