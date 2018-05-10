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
		支付订单号:<input type="text" name="orderNumber" class="orderNumber" value="<%=System.currentTimeMillis()%>"><br/><br/>
		订单交易金额(分):<input type="text" name="amount" class="amount" value="100"><br/><br/>
		时间戳：<input type="text" name ="timeStamp" class="timeStamp" value=""><br/><br/>
		姓名：<input type="text" name ="userName" class="userName" value="李娟"><br/><br/>
		证件号：<input type="text" name ="userId" class="userId" value="120105197510055420"><br/><br/>
		银行卡号：<input type="text" name ="userCardNo" class="userCardNo" value="6259588888621801"><br/><br/>
		手机号：<input type="text" name ="userTel" class="userTel" value="13323358548"><br/><br/>
		<input type="text" name="notifyUrl" class="notifyUrl" style="display: none;" value="http://60.28.24.164:8104/app_posp/TFBController/notifyUrl.action">
		<input type="text" name="callBackUrl" class="callBackUrl" style="display: none;" value="https://www.baidu.com">
		类别：<input type="text" name ="type" class="type" value="0"><br/><br/>
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
					url:"${pageContext.request.contextPath}/HMController/shortcutAlipay.action",
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