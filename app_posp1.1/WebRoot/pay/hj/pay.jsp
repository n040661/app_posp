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
	商户号:<input type="text" name="merchantNo" class="merchantNo" value="10032061473"/><br /><br />
	商户代付单号:<input type="text" name="batchNo" class="batchNo" value="<%=System.currentTimeMillis()%>"/><br /><br />
	交易金额:<input type="text" name="amount" class="amount" value="100"/><br /><br />
	代付明细序号:<input type="text" name="identity" class="identity" value="CJZF<%=System.currentTimeMillis()%>"/><br /><br />
	收款人姓名:<input type="text" name="accountName" class="accountName" value="李娟"/><br /><br />
	收款人账号:<input type="text" name="bankCard" class="bankCard" value="6228450028016697770"/><br /><br />
	收获账户地址:<input type="text" name="city" class="city" value="天津市" /><br /><br />
	是否审核:<input type="text" name="examine" class="examine" value="0"/><br /><br />
	对公对私:<input type="text" name="type" class="type" value="2"/><br /><br />
	开户行支行联行号:<input type="text" name="pmsbankno" class="pmsbankno" value="103110023002"/><br /><br />
	代付类型:<input type="text" name="productType" class="productType" value="3"/><br /><br />
	说明:<input type="text" name="remarks" class="remarks" value="代付测试"/><br /><br />
	<input type="button" onclick="daifu()" value="点击代付"/><br /><br />
	<div class="div"></div>
</center>
<script type="text/javascript">
	function daifu(){
		console.info($("input[name]").serialize());
		$.ajax({
			url:"${pageContext.request.contextPath}/HJController/codeSign.action",
			type:"post",
			dataType:"json",
			data:$("input[name]").serialize(),
			success:function(data){
				console.info(data);
				$.ajax({
					url:"${pageContext.request.contextPath}/HJController/pay.action",
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