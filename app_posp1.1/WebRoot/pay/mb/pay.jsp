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
订单号:<input type="text" name="orderId" class="orderId" value="CJZF<%=System.currentTimeMillis()%>"><br />
金额： <input type="text" class="transAmount" name="transAmount" value="100"/><br />
银行卡号： <input type="text" class="accNo" name="accNo" value="6228480020721272316"/><br />
开户名： <input type="text" class="accName" name="accName" value="高立明"/><br />
联行号： <input type="text" class="pmsbankno" name="pmsbankno" value="305110021002"/><br />
类型： <input type="text" class="type" name="type" value="cj003"/><br />
<input type="button" value="提交" onclick="tijiao()"><br />
<div id ="div"></div>
<br />
</center>
<script type="text/javascript">
	function tijiao(){
		 var merId = $(".merId").val();
			var orderId = $(".orderId").val();
			var transAmount = $(".transAmount").val();
			var accNo =$(".accNo").val();
			var accName = $(".accName").val();
			var pmsbankno = $(".pmsbankno").val();
			var type =$(".type").val();
			var data={"merId":merId,"orderId":orderId,"transAmount":transAmount,"accNo":accNo,"accName":accName,"pmsbankno":pmsbankno,"type":type};
			$.ajax({
						url : "${pageContext.request.contextPath}/MBController/paySign.action",
						type : 'post',
						data : data,
						success : function(data) {
							console.info(data);
							var datas={"merId":merId,"orderId":orderId,"transAmount":transAmount,"accNo":accNo,"accName":accName,"pmsbankno":pmsbankno,"type":type,"sign":data};
							$.ajax({
										url : "${pageContext.request.contextPath}/MBController/payClienty.action",
										type : 'post',
										data :datas,
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