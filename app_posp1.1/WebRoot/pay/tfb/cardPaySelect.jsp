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
	商户号:<input type="text" name="" class="spid" value="10012015423"><br><br>
	订单号:<input type="text" name="" class="spbillno" value="1499052916423"><br><br>
	<input type="button" onclick="chaxun()" value="查询">
	<div class="div">
		
	</div>
</center>
<script type="text/javascript">
	function chaxun(){
		var spid =$(".spid").val();
		var spbillno =$(".spbillno").val();
		var data={"spid":spid,"spbillno":spbillno};
		$.ajax({
			url:"${pageContext.request.contextPath}/TFBController/wxpayParameter.action",
			type:"post",
			data:data,
			success:function(data){
				console.info(data);
				var data1={"spid":spid,"spbillno":spbillno,"sign":data};
				$.ajax({
					url:"${pageContext.request.contextPath}/TFBController/cardSelect.action",
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