<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
<script type="text/javascript" src="<%=basePath%>/js/jquery-3.1.1.min.js"></script>
</head>
<body>
<center>
		商户号:<input type="text" class="mch_id" name="mch_id" value="${scan.mch_id}"/><br />
		商户订单号:<input type="text" class="out_trade_no" name="out_trade_no" value="${scan.out_trade_no}"/><br />
		总金额:<input type="text" class="total_fee" name="total_fee" value="${scan.total_fee}"/><br />
		订单标题:<input type="text" class="subject" name="subject" value="${scan.subject}"/><br />
		商品描述:<input type="text" class="body" name="body" value="${scan.body}"/><br />
		随机字符串:<input type="text" class="nonce_str" name="nonce_str" value="${scan.nonce_str}"/><br />
		授权码:<input type="text" class="auth_code" name="auth_code" value="${scan.auth_code}"/><br />
		支付场景:<input type="text" class="scene" name="scene" value="${scan.scene}"/><br />
		签名:<input type="text" class="sign" name="sign" value="${scan.sign}"/><br />
		<input type="button" onclick="dianji()" value="提交"/><br>
		
</center>
<script type="text/javascript">
	function dianji(){
		var mch_id=$(".mch_id").val();
		var out_trade_no=$(".out_trade_no").val();
		var total_fee=$(".total_fee").val();
		var subject=$(".subject").val();
		var body=$(".body").val();
		var notify_url=$(".notify_url").val();
		var nonce_str=$(".nonce_str").val();
		var auth_code=$(".auth_code").val();
		var scene=$(".scene").val();
		var sign=$(".sign").val();
		$.ajax({
			url:"${pageContext.request.contextPath}/wechat/alipayScanParamCard.action",
			type:"post",
			data:{"mch_id":mch_id,"out_trade_no":out_trade_no,"total_fee":total_fee,"subject":subject,"body":body,"notify_url":notify_url,"nonce_str":nonce_str,"sign":sign,"auth_code":auth_code,"scene":scene},
			dataType:"json",
			success:function(data){
				console.info(data);
			}
		});
	}
</script>
</body>
</html>