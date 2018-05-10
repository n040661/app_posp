<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>
<body>
<center>
<br>
<h2>支付结果</h2>
<div class="ss" style="color: red"></div>
<br>
<table>
	<tr>
		<td>商户号:</td>
		<td><input type="text" class="merchantId1" title="必填" value="100120242118015"></td>
	</tr>
	<tr>
		<td>订单号:</td>
		<td><input type="text" class="MerchOrderId" title="必填" value=""></td>
	</tr>
</table>
<input type="button" value="查询订单" onclick="dianji()">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-3.1.1.min.js"></script>
    <script type="text/javascript">
        function dianji() {
        	var merchantId1 =$(".merchantId1").val();
        	var MerchOrderId =$(".MerchOrderId").val();
           $.ajax({
        	   type:"post",
        	   data:{"merchantId1":merchantId1,"merchOrderId":MerchOrderId},
				url:"${pageContext.request.contextPath}/clientOrderQueryController/orderQueryTest.action",
				success:function(datas){
					$(".ss").html(datas)
				}
           })
        }
    </script>
</body>
</center>
</html>