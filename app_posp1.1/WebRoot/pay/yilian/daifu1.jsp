<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>

</head>
<body>
订单号:<input type="text" value="${req_bean.BATCH_NO }"><br />
商户号:<input type="text" value="${req_bean.MERCHANT_ID }"><br />
签名:<input type="text" value="${req_bean.SIGN }"><br />
<input type="button" value="提交" onclick="tijiao()">

<hr/>
<div class="ss" style="color: red;">
	
</div>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-3.1.1.min.js"></script>
<script type="text/javascript">
	function tijiao(){
		$.ajax({
			url:"${pageContext.request.contextPath}/clientCollectionPayController/select.action",
			data:{"BATCH_NO":"${req_bean.BATCH_NO }","MERCHANT_ID":"${req_bean.MERCHANT_ID }","SIGN":"${req_bean.SIGN }"},
			dataType:"json",
			success:function(data){
				console.info(data);
				
			}
		});
	}
</script>
</body>
</html>