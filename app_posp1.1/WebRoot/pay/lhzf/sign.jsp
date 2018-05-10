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
		<form action="">
		商户号:<input type="text" name="merNo" class="merNo" value="10012014187"><br/>
		支付订单号:<input type="text" name="orderNo" class="orderNo" value="<%=System.currentTimeMillis()%>"><br/>
		订单交易金额(分):<input type="text" name="transAmt" class="transAmt" value="1000"><br/>
		商品描述:<input type="text" name="orderDesc" class="orderDesc" value="大饼鸡蛋"><br/>
		异步通知地址:<input type="text" name="notifyUrl" class="notifyUrl" value="http://60.28.24.164:8104/app_posp/TFBController/notifyUrl.action"><br/>
		同步通知地址:<input type="text" name="returnUrl" class="returnUrl" value="http://60.28.24.164:8104/app_posp/TFBController/returnUrl.action"><br/>
		备注:<input type="text" name="remark" class="remark" value="标准快捷"><br/>
		出款银行卡号:<input type="text" name="cardNo" class="cardNo" value="4637580007110368"><br/>
		银行卡类型:<input type="text" name="cardType" class="cardType" value="02"><br/>
		证件名称:<input type="text" name="idName" class="idName" value="李娟"><br/>
		开户证件类型:<input type="text" name="idType" class="idType" value="01"><br/>
		开户证件号:<input type="text" name="idNo" class="idNo" value="120105197510055420"><br/>
		开户手机号码:<input type="text" name="mobileNo" class="mobileNo" value="13323358548"><br/>
		收款证件类型:<input type="text" name="payeeCardType" class="payeeCardType" value="01"><br/>
		收款银行卡号:<input type="text" name="payeeCardNo" class="payeeCardNo" value="6228450028016697770"><br/>
		交易扩展信息:<input type="text" name="extraInfo" class="extraInfo" value="大饼加一切"><br/>
		交易附带信息:<input type="text" name="transInfo" class="transInfo" value="大饼鸡蛋不加盐"><br/>
		
		<input type="button" onclick="shengcheng()" value="提交生成">
		<font class="font" color="red"></font>
		</form>
</center>
</body>
<script type="text/javascript">
	
	function shengcheng(){
		
		$.ajax({
			url:"${pageContext.request.contextPath}/LqzfController/paySign.action",
			type:"post",
			data:$("input[name]").serialize(),
			success:function(data){
				$.ajax({
					url:"${pageContext.request.contextPath}/LqzfController/wxpayParameter.action",
					type:"post",
					dataType:"json",
					data:$("input[name]").serialize()+"&sign="+data,
					success:function(data){
						console.info(data);
						
						/* if(data.respCode=='00'){
							var str="idNo="+data.idNo+"&idType="+data.idType+"&payeeCardType="+data.payeeCardType+"&orderDesc="+data.orderDesc+"&remark="+data.remark+"&transInfo="+data.transInfo;
							str+="&idName="+data.idName+"&cardType="+data.cardType+"&transAmt="+data.transAmt+"&currency="+data.currency+"&sign="+data.sign+"&cardNo="+data.cardNo+"&serialNo="+data.serialNo;
							str+="&transDate="+data.transDate+"&orderNo="+data.orderNo+"&transId="+data.transId+"&transTime="+data.transTime+"&extraInfo="+data.extraInfo+"&payeeCardNo="+data.payeeCardNo+"&merKey="+data.merKey;
							str+="&notifyUrl="+data.notifyUrl+"&mobileNo="+data.mobileNo+"&requestUrl="+data.requestUrl+"&returnUrl="+data.returnUrl;
							window.location.href="zhfu.jsp?"+str;
						}else{
							$(".font").text("错误码:"+data.respCode+",错误信息:"+data.respMsg);
						} */
						
					}
				});
			}
		});
	}
</script>
</html>