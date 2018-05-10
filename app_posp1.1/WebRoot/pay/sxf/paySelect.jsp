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
	商户号:<input type="text" class="mercNo" name="mercNo" value="10012015423">
	订单号:<input type="text" class="orderNo" name="orderNo" value="1499322467698">
	<input type="button" value="查询" onclick="chaxun()">
</center>
<script type="text/javascript">
	function chaxun(){
		var mercNo =$(".mercNo").val();
		var orderNo =$(".orderNo").val();
		var data={"mercNo":mercNo,"orderNo":orderNo};
		$.ajax({
			url:"${pageContext.request.contextPath}/SXFController/cardPayParameter.action",
			type:"post",
			data:data,
			success:function(data){
				console.info(data);
				var data1={"mercNo":mercNo,"orderNo":orderNo,"sign":data};
				$.ajax({
					url:"${pageContext.request.contextPath}/SXFController/paySelect.action",
					type:"post",
					data:data1,
					dataType:"json",
					success:function(data){
						console.info(data.orderNo);
						var str ="mercNo="+data.mercNo+"&tranCd="+data.tranCd+"&version="+data.version+"&reqData="+data.reqData+"&ip="+data.ip+"&sign="+data.sign+"&orderNo="+data.orderNo+"&encodeType="+data.encodeType;
						window.location.href="sendSelect.jsp?"+str;
					}
				});
			}
			
		});
	}
</script>
</body>
</html>