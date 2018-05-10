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
	商户号:<input type="text" name="merchantId" class="merchantId" value="10032061473"/><br /><br />
	商户代付单号:<input type="text" name="merchantBatchNo" class="merchantBatchNo" value="<%=System.currentTimeMillis()%>"/><br /><br />
	交易金额:<input type="text" name="amount" class="amount" value="100"/><br /><br />
	代付明细序号:<input type="text" name="merchantPayNo" class="merchantPayNo" value="CJZF<%=System.currentTimeMillis()%>"/><br /><br />
	收款人姓名:<input type="text" name="ownerName" class="ownerName" value="李娟"/><br /><br />
	收款人账号:<input type="text" name="bankcardNo" class="bankcardNo" value="6228450028016697770"/><br /><br />
	银行代码:<input type="text" name="bankId" class="bankId" value="103"/><br /><br />
	省:<input type="text" name="province" class="province" value="天津直辖市"/><br /><br />
	市:<input type="text" name="city" class="city" value="和平区" /><br /><br />
	对公对私:<input type="text" name="publicFlag" class="publicFlag" value="0"/><br /><br />
	开户行名称:<input type="text" name="bankName" class="bankName" value="中国农业银行天津广厦支行"/><br /><br />
	异步通知地址:<input type="text" name="notifyUrl" class="notifyUrl" value=""/><br /><br />
	当日/次日<input type="text" name="intoAccountDay" class="intoAccountDay" value="0"/><br /><br />
	<input type="button" onclick="daifu()" value="点击代付"/><br /><br />
	<div class="div"></div>
</center>
<script type="text/javascript">
	function daifu(){
		console.info($("input[name]").serialize());
		$.ajax({
			url:"${pageContext.request.contextPath}/HFBController/codeSign.action",
			type:"post",
			dataType:"json",
			data:$("input[name]").serialize(),
			success:function(data){
				console.info(data);
				$.ajax({
					url:"${pageContext.request.contextPath}/HFBController/pay.action",
					type:"post",
					dataType:"json",
					data:$("input[name]").serialize()+"&sign="+data.sign,
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