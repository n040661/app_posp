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
商户号:<input type="text" name="merId" class="merId" value="10032061473"><br />
返回订单号:<input type="text" name="ksPayOrderId" class="ksPayOrderId" value="${payOrderId }"><br />
验证码： <input type="text" class="yzm" name="yzm" value=""/><br />
类型： <input type="text" class="type" name="type" value="cj006"/><br />
<input type="button" value="提交" onclick="tijiao()"><br />
<div id ="div"></div>
<br />
</center>
<script type="text/javascript">
	function tijiao(){
		$.ajax({
			url : "${pageContext.request.contextPath}/MBController/paySign.action",
			type : 'post',
			data : $("input[name]").serialize(),
			success : function(data) {
				$.ajax({
					url : "${pageContext.request.contextPath}/MBController/payClienty.action",
					type : 'post',
					data :$("input[name]").serialize()+"&sign="+data,
					success : function(data) {
						console.info(data);
						$("#div").text(data);
						
					}
				});
			}
		});
	}
			
	/* function tijiao(){
	   
	} */
</script>
</body>

</html>