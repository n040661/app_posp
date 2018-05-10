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
		商户号:<input type="text" name="merchantId" class="merchantId" value="10032061473"><br/><br/>
		支付订单号:<input type="text" name="orderNumber" class="orderNumber" value=""><br/><br/>
		
		<input type="button" onclick="shengcheng()" value="提交生成">
		<div id="div"></div>
</center>
</body>
<script type="text/javascript">
	function shengcheng(){
		$.ajax({
			url:"${pageContext.request.contextPath}/HMController/paySign.action",
			type:"post",
			data:$("input[name]").serialize(),
			success:function(data){
				$.ajax({
					url:"${pageContext.request.contextPath}/HMController/select.action",
					type:"post",
					data:$("input[name]").serialize()+"&sign="+data,
					success:function(data){
						console.info(data);
						$("#div").html('<font color="red">'+data+'</font>');
					}
				});
			}
		});
	}
</script>
</html>