<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
<script type="text/javascript" src="../../js/jquery-3.1.1.min.js"></script>
</head>
<body>
<center>
<form action="${pageContext.request.contextPath}/clientCollectionPayController/downloadPay.action" method="post">
	<table>
		<tr>
			<td>上游商户号:</td>
			<td><input type="text" class="merchant_no" name="merchant_no" value="100120000393"/></td>
		</tr>
		<tr>
			<td>时间:</td>
			<td><input type="text" class="trans_date" name="trans_date" value="20170523" placeholder="格式:20170523"/></td>
		</tr>
		<tr>
			<td colspan="1"><input type="submit" value="下载" /></td>
		</tr>
	</table>
</form>
	<!-- <script type="text/javascript">
		function xiazai(){
			var merchant_no =$(".merchant_no").val();
			var trans_date =$(".trans_date").val();
			$.ajax({
				url:"${pageContext.request.contextPath}/clientCollectionPayController/downloadPay.action",
				data:{"merchant_no":merchant_no,"trans_date":trans_date},
				dataType:"json",
				success:function(data){
					console.info(data);
					console.info(data.msg);
				}
				
			});
		}
	</script> -->
</center>

</body>
</html>