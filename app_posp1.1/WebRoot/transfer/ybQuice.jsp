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
	商户号:<input type="text" name="merid" class="merid" value="10032061473">
	批次号号:<input type="text" name="batchNo" class="batchNo" value="1505533534960">
	订单号:<input type="text" name="ordid" class="ordid" value="1505533534960">
	<input type="button" onclick="chaxun()" value="查询">
	<div class="div">
		
	</div>
</center>
<script type="text/javascript">
	function chaxun(){
		$.ajax({
			url:"${pageContext.request.contextPath}/YBController/ybQuert.action",
			type:"post",
			data:$("input[name]").serialize(),
			success:function(data){
				console.info(data);
				$(".div").html(data);
			}
		});
	}
</script>
</body>
</html>