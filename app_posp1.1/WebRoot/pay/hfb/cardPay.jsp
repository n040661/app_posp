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
		<form action="${pageContext.request.contextPath}/HFBController/cardPay.action" method="post" id ="from">
		商户号:<input type="text" name="merchantId" class="merchantId" value="10032061473"><br/><br/>
		支付订单号:<input type="text" name="merchantOrderNo" class="merchantOrderNo" value="<%=System.currentTimeMillis()%>"><br/><br/>
		订单交易金额(分):<input type="text" name="payAmount" class="payAmount" value="1000"><br/><br/>
		异步回调地址:<input type="text" name="notifyUrl" class="notifyUrl" value="http://60.28.24.164:8104/app_posp/TFBController/notifyUrl.action"><br/><br/>
		同步回调地址:<input type="text" name="callBackUrl" class="callBackUrl" value="https://www.baidu.com"><br/><br/>
		商品信息:<input type="text" name="description" class="description" value="大饼鸡蛋"><br/><br/>
		
		<!-- 选择银行方式:<input type="text" name="onlineType" class="onlineType" placeholder="simple:商户侧选择银行hard:汇付宝收银台上选择银行" value="simple"><br/><br/>
		银行id :<input type="text" name="bankId" class="bankId" value="102" placeholder="商户侧选择银行时必填"><br/><br/>
		银行名称 :<input type="text" name="bankName" class="bankName" value="中国工商银行" placeholder="商户侧选择银行时必填"><br/><br/>
		银行卡类型:<input type="text" name="bankCardType" class="bankCardType" value="SAVING" placeholder="商户侧选择银行时必填"><br/><br/> -->
		
		<input type="button" onclick="shengcheng()" value="提交生成">
		</form>
</center>
</body>
<script type="text/javascript">
	$(function(){
		var data={"merchantId":"10012016346"};
		$.ajax({
			url:"${pageContext.request.contextPath}/HFBController/paySign.action",
			type:"post",
			data:data,
			success:function(data){
				var data1={"merchantId":"10012016346","sign":data};
				$.ajax({
					url:"${pageContext.request.contextPath}/HFBController/payApplyParameter.action",
					type:"post",
					dataType:"json",
					data:data1,
					success:function(data){
						console.info(data.data);
					}
				});
			}
		});
		
	});
	
	function shengcheng(){
		
		$.ajax({
			url:"${pageContext.request.contextPath}/HFBController/paySign.action",
			type:"post",
			data:$("input[name]").serialize(),
			success:function(data){
				$("#from").append('<input type="text" name="sign" class="sign" style="display: none" value="'+data+'"><br/>');
				console.info($(".sign").val());
				$("#from").submit();
				/* $.ajax({
					url:"${pageContext.request.contextPath}/HFBController/cardPay.action",
					type:"post",
					dataType:"json",
					data:$("input[name]").serialize()+"&sign="+data,
					success:function(data){
						console.info(data);
						if(data.respCode=='00'){
							var str ="callBackUrl="+data.callBackUrl+"&merchantId="+data.merchantId+"&merchantOrderNo="+data.merchantOrderNo+"&merchantUserId="+data.merchantUserId+"&notifyUrl="+data.notifyUrl+"&onlineType="+data.onlineType+"&payAmount="+data.payAmount+"&productCode="+data.productCode+"&requestTime="+data.requestTime+"&version="+data.version+"&signString="+data.signString;
							if(data.onlineType=='simple'){
								str+="&bankId="+data.bankId+"&bankName="+data.bankName+"&bankCardType="+data.bankCardType;
							}
							window.location.href="cardPost.jsp?"+str;
						}
						
						
					}
				}); */
			}
		});
	}
</script>
</html>