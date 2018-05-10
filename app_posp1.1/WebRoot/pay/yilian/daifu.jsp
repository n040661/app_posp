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
数据:<input type="text" value="${req_bean.MAP }" class="MAP"><br />
签名:<input type="text" value="${req_bean.SIGN }"><br />
<input type="button" value="提交" onclick="tijiao()">

<hr/>
<div class="ss" style="color: red;">
	
</div>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-3.1.1.min.js"></script>
<script type="text/javascript">
	function tijiao(){
		$.ajax({
			url:"${pageContext.request.contextPath}/clientCollectionPayController/paySigns.action",
			type: 'post',
			data:{"BATCH_NO":"${req_bean.BATCH_NO }","MERCHANT_ID":"${req_bean.MERCHANT_ID }","MAP":$(".MAP").val(),"SIGN":"${req_bean.SIGN }"},
			dataType:"json",
			success:function(data){
				console.info(data.BODYS);
				$.each(data.BODYS,function(index,datas){
					console.info(datas.REMARK);
					if(datas.PAY_STATE=='00A4'){
						$(".ss").html('订单代付中...');
					}else if(datas.PAY_STATE=='0000'){
						$(".ss").html('订单代付成功');
					}else{
						$(".ss").html('订单代付失败');
					}
				});
				
			}
		});
	}
</script>
</body>
</html>