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
	 var orderDate=$(".orderDate").val();
		 $.ajax({
			 data:{"outMchId":outMchId,"orderDate":orderDate},
			 url:"${pageContext.request.contextPath}/registerController/merchantDownload.action",
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
		对账单日期<input type="text" class="orderDate" title="格式20170101八位字符串">
		<input type="button" class="button" value="查询" onclick="dianji()">
	</center>
</body>

</html>