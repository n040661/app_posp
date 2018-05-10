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
		商户号:<input type="text" name="mercNo" class="mercNo" value="10032061473"><br/><br/>
		支付订单号:<input type="text" name="orderNo" class="orderNo" value="<%=System.currentTimeMillis()%>"><br/><br/>
		订单交易金额(分):<input type="text" name="tranAmt" class="tranAmt" value="212"><br/><br/>
		商品名称:<input type="text" name="pname" class="pname" value="大饼鸡蛋"><br/><br/>
		商品描述:<input type="text" name="pdesc" class="pdesc" value="鸡蛋不加盐" ><br/><br/>
		同步回调地址:<input type="text" name="retUrl" class="retUrl" value="http://60.28.24.164:8104/app_posp/TFBController/returnUrl.action"><br/><br/>
		异步回调地址:<input type="text" name="notifyUrl" class="notifyUrl" value="http://60.28.24.164:8104/app_posp/TFBController/notifyUrl.action"><br/><br/>
		银行简称:<input type="text" name="bankWay" class="bankWay" value="" placeholder="ICBC..（个人网银连、企业网银、快捷 支付直连必填）"><br/><br/>
		订单描述:<input type="text" name="desc" class="desc" value=""><br/><br/>
		商户用户 id :<input type="text" name="userId" class="userId" value="123" placeholder="直连必填"><br/><br/>
		支付方式 :<input type="text" name="payWay" class="payWay" value="1" placeholder="1 收银台/2 直连/3 移动 "><br/><br/>
		支付渠道:<input type="text" name="payChannel" class="payChannel" value="0"><br/><br/>
		银行卡号 :<input type="text" name="bankCardNo" class="bankCardNo" value="" placeholder="直连必填"><br/><br/>
		信用卡cvv:<input type="text" name="cvv" class="cvv" value="" placeholder="直连必填"><br/><br/>
		信用卡有效 期:<input type="text" name="valid" class="valid" value="" placeholder="直连必填"><br/><br/>
		姓名:<input type="text" name="accountName" class="accountName" value="" placeholder="直连必填"><br/><br/>
		身份证号:<input type="text" name="certificateNo" class="certificateNo" value="" placeholder="直连必填"><br/><br/>
		手机号 :<input type="text" name="mobilePhone" class="mobilePhone" value="" placeholder="直连必填"><br/><br/>
		
		<input type="button" onclick="shengcheng()" value="提交生成">
	
	
	<script type="text/javascript">
		function shengcheng(){
			var mercNo =$(".mercNo").val();
			var orderNo =$(".orderNo").val();
			var tranAmt =$(".tranAmt").val();
			var pname =$(".pname").val();
			var pdesc =$(".pdesc").val();
			var retUrl =$(".retUrl").val();
			var notifyUrl =$(".notifyUrl").val();
			var bankWay =$(".bankWay").val();
			var desc =$(".desc").val();
			var userId =$(".userId").val();
			var payWay =$(".payWay").val();
			var payChannel =$(".payChannel").val();
			var bankCardNo =$(".bankCardNo").val();
			var cvv =$(".cvv").val();
			var valid =$(".valid").val();
			var accountName =$(".accountName").val();
			var certificateNo =$(".certificateNo").val();
			var mobilePhone =$(".mobilePhone").val();
			 var data={"mercNo":mercNo,"orderNo":orderNo,"tranAmt":tranAmt,"pname":pname,"pdesc":pdesc,"retUrl":retUrl,
					 "notifyUrl":notifyUrl,"bankWay":bankWay,"desc":desc,"userId":userId,"payWay":payWay,"payChannel":payChannel,"bankCardNo":bankCardNo
					 ,"cvv":cvv,"valid":valid,"accountName":accountName,"certificateNo":certificateNo,"mobilePhone":mobilePhone};
			$.ajax({
				url:"${pageContext.request.contextPath}/SXFController/cardPayParameter.action",
				type:"post",
				data:data,
				success:function(data){
					console.info(data);
					var data1={"mercNo":mercNo,"orderNo":orderNo,"tranAmt":tranAmt,"pname":pname,"pdesc":pdesc,"retUrl":retUrl,
							 "notifyUrl":notifyUrl,"bankWay":bankWay,"desc":desc,"userId":userId,"payWay":payWay,"payChannel":payChannel,"bankCardNo":bankCardNo
							 ,"cvv":cvv,"valid":valid,"accountName":accountName,"certificateNo":certificateNo,"mobilePhone":mobilePhone,"sign":data};
					$.ajax({
						url:"${pageContext.request.contextPath}/SXFController/cardPay.action",
						type:"post",
						data:data1,
						dataType:"json",
						success:function(data){
							console.info(data.sign);
							var str ="mercNo="+data.mercNo+"&tranCd="+data.tranCd+"&version="+data.version+"&reqData="+data.reqData+"&ip="+data.ip+"&type="+data.type+"&sign="+data.sign+"&encodeType="+data.encodeType;
							window.location.href="sendPost.jsp?"+str;
						}
					});
				}
				
			});
			
			}
	</script>
</center>
</body>
</html>