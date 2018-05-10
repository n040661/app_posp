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
		商户号:<input type="text" name="spid" class="spid" value="10012015423"><br/><br/>
		支付订单号:<input type="text" name="spbillno" class="spbillno" value="<%=System.currentTimeMillis()%>"><br/><br/>
		订单交易金额(分):<input type="text" name="money" class="money" value="13"><br/><br/>
		订单备注:<input type="text" name="memo" class="memo" value="大饼鸡蛋"><br/><br/>
		用户类型:<input type="text" name="user_type" class="user_type" value="1" placeholder="1:个人,2:企业"><br/><br/>
		银行卡类型:<input type="text" name="card_type" class="card_type" value="1" placeholder="1:借记卡,2:贷记卡"><br/><br/>
		银行代号:<input type="text" name="bank_segment"class="bank_segment" value="6666" placeholder="详见银行代号部分"><br/><br/>
		同步回调地址:<input type="text" name="return_url" class="return_url" value="http://60.28.24.164:8104/app_posp/TFBController/returnUrl.action"><br/><br/>
		异步回调地址:<input type="text" name="notify_url" class="notify_url" value="http://60.28.24.164:8104/app_posp/TFBController/notifyUrl.action"><br/><br/>
		错误页面回调地址:<input type="text" name="errpage_url" class="errpage_url" value=""><br/><br/>
		渠道类型:<input type="text" name="channel" class="channel" value="1" placeholder="1.PC端,2.手机端"><br/><br/>
		<input type="button" onclick="shengcheng()" value="提交生成">
	
	
	<script type="text/javascript">
		function shengcheng(){
			var spid =$(".spid").val();
			var spbillno =$(".spbillno").val();
			var money =$(".money").val();
			var memo =$(".memo").val();
			var user_type =$(".user_type").val();
			var card_type =$(".card_type").val();
			var bank_segment =$(".bank_segment").val();
			var return_url =$(".return_url").val();
			var notify_url =$(".notify_url").val();
			var errpage_url =$(".errpage_url").val();
			var channel=$(".channel").val();
			var data={"spid":spid,"spbillno":spbillno,"money":money,"memo":memo,"user_type":user_type,
					"card_type":card_type,"bank_segment":bank_segment,"return_url":return_url,"notify_url":notify_url,"errpage_url":errpage_url,"channel":channel};
			$.ajax({
				url:"${pageContext.request.contextPath}/TFBController/cardpayParameter.action",
				type:"post",
				data:data,
				success:function(data){
					console.info(data);
					var errpage_url =$(".errpage_url").val();
					var data1={"spid":spid,"spbillno":spbillno,"money":money,"memo":memo,"user_type":user_type,
							"card_type":card_type,"bank_segment":bank_segment,"return_url":return_url,"notify_url":notify_url,"errpage_url":errpage_url,"channel":channel,"sign":data};
					$.ajax({
						url:"${pageContext.request.contextPath}/TFBController/cardpayApply.action",
						type:"post",
						data:data1,
						dataType:"json",
						success:function(data){
							console.info(data.str);
							//alert(data.str);
							window.location.href=data.str;
						}
					});
				}
				
			});
			
			}
	</script>
</center>
</body>
</html>