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
	商户号:<input type="text" class="merId" name="merId" value="10023049760">
	订单号:<input type="text" class="orderId" name="orderId" value="CJZF1502247764949">
	<input type="button" value="查询" onclick="chaxun()">
	<div id ="div"></div>
</center>
<script type="text/javascript">
	function chaxun(){
		var merId =$(".merId").val();
		var orderId =$(".orderId").val();
		var data={"merId":merId,"orderId":orderId,"type":"cj002"};
		$.ajax({
			url:"${pageContext.request.contextPath}/MBController/paySign.action",
			type:"post",
			data:data,
			success:function(data){
				console.info(data);
				var data1={"merId":merId,"orderId":orderId,"sign":data,"type":"cj002"};
				$.ajax({
					url:"${pageContext.request.contextPath}/MBController/payClienty.action",
					type:"post",
					data:data1,
					success:function(data){
						console.info(data);
						$("#div").text(data);
					}
				});
			}
			
		});
	}
</script>
</body>
</html>