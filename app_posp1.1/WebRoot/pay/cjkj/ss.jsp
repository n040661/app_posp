<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>代付</title>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-3.1.1.min.js"></script>
</head>
<body>
<center>
	<input type="button" onclick="daifu()" value="查询"/><br /><br />
	<div class="div"></div>
</center>
<script type="text/javascript">
	function daifu(){
		console.info($("input[name]").serialize());
		$.ajax({
			url:"${pageContext.request.contextPath}/PayController/ss.action",
			type:"post",
			dataType:"json",
			success:function(data){
				console.info(data);
				var ss ='';
				for (var i = 0; i < data.length; i++) {
					console.info(data[i]);
					ss = data[i].merchantId+','+data[i].merchantUuid+','+data[i].quickPayD0WalletWithdrawBalance+','+data[i].quickPayT1WalletWithdrawBalance+','+data[i].quickPayWalletBalance+'/';
				}
					$(".div").html(ss);
				//$(".div").html(data);
			}
		});
	}
</script>
</body>
</html>