<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
<script type="text/javascript" src="../js/jquery-3.1.1.min.js"></script>
</head>
<body>
<center>
		商户号:<input type="text" class="mch_id" name="mch_id" value="${scan.mch_id}"/><br />
		商户订单号:<input type="text" class="out_trade_no" name="out_trade_no" value="${scan.out_trade_no}"/><br />
		签名:<input type="text" class="sign" name="sign" value="${scan.sign}"/><br />
		
		<input type="button" onclick="dianji()" value="提交"/><br>
		
</center>
<script type="text/javascript">
	function dianji(){
		var mch_id=$(".mch_id").val();
		var out_trade_no=$(".out_trade_no").val();
		var sign=$(".sign").val();
		$.ajax({
			url:"${pageContext.request.contextPath}/wechat/alipayScanParamSelect.action",
			type:"post",
			data:{"mch_id":mch_id,"out_trade_no":out_trade_no,"sign":sign},
			dataType:"json",
			success:function(data){
				data.trade_state
				console.info(data);
			}
		});
	}
</script>

</body>
</html>