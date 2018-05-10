<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-3.1.1.min.js"></script>
<script type="text/javascript">
	
	 function dianji(){
	 var outMchId=$(".outMchId").val();
		 $.ajax({
			 data:{"outMchId":outMchId},
			 url:"${pageContext.request.contextPath}/registerController/select.action",
			 success:function(datas){
				 console.info(datas);
			 }
		 });
	 }
	
	 
</script>
</head>

<body>
	<center>
		下游商户号：<input type="text" class="outMchId">
		<input type="button" class="button" value="查询" onclick="dianji()">
	</center>
</body>

</html>