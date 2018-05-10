<%@ page language="java" contentType="text/html; charset=utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>代付查询</title>

</head>
<body>
订单号:<input type="text" value="" class="BATCH_NO"><br />
商户号:<input type="text" value="" class="MERCHANT_ID"><br />
<input type="button" value="提交" onclick="tijiao()">
</body>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-3.1.1.min.js"></script>
<script type="text/javascript">
	function tijiao(){
		console.info($(".BATCH_NO").val());
		console.info($(".MERCHANT_ID").val());
		$.ajax({
			url:"${pageContext.request.contextPath}/clientCollectionPayController/paySign.action",
			data:{"BATCH_NO":$(".BATCH_NO").val(),"MERCHANT_ID":$(".MERCHANT_ID").val()},
			dataType:"json",
			success:function(data){
				window.location.href="daifu1.jsp";
			}
		});
	}
</script>
</html>