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
	商户号:<input type="text" name="" class="spid" value="10014118279">
	订单号:<input type="text" name="" class="sp_billno" value="P20180202130619231104707">
	<input type="button" onclick="chaxun()" value="查询">
	<div class="div">
		
	</div>
</center>
<script type="text/javascript">
	function chaxun(){
		var spid =$(".spid").val();
		var sp_billno =$(".sp_billno").val();
		var data={"spid":spid,"sp_billno":sp_billno,type:"0"};
		$.ajax({
			url:"${pageContext.request.contextPath}/TFBController/wxpayParameter.action",
			type:"post",
			data:data,
			success:function(data){
				console.info(data);
				var data1={"spid":spid,"sp_billno":sp_billno,type:"0","sign":data};
				$.ajax({
					url:"${pageContext.request.contextPath}/TFBController/select.action",
					type:"post",
					data:data1,
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