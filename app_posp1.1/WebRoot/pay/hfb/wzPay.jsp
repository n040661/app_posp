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
		支付订单号:<input type="text" name="merchantOrderNo" class="merchantOrderNo" value="<%=System.currentTimeMillis()%>"><br/><br/>
		订单交易金额(分):<input type="text" name="payAmount" class="payAmount" value="212"><br/>
		交易类型:<input type="text" name="tradeType" class="tradeType" value="weixin_qr" placeholder="微信扫码:weixin_qr,微信公众号:weixin_pub,微信H5:weixin_h5，支付宝扫码:alipay_qr,支付宝wap:alipay_wap"><br/>
		微信扫码:weixin_qr,微信公众号:weixin_pub,微信H5:weixin_h5，支付宝扫码:alipay_qr,支付宝wap:alipay_wap<br/>
		异步回调地址:<input type="text" name="notifyUrl" class="notifyUrl" value="http://60.28.24.164:8104/app_posp/TFBController/notifyUrl.action"><br/><br/>
		<!-- 同步回调地址:<input type="text" name="returnUrl" class="returnUrl" value="https://www.baidu.com"><br/><br/> -->
		商品名称:<input type="text" name="goodsName" class="goodsName" value="大饼鸡蛋"><br/><br/>
		<!-- 商品详细说明:<input type="text" name="goodsDetail" class="goodsDetail" value='' placeholder="交易类型是微信H5(weixin_h5)必传"><br/><br/> -->
		商品名称:<input type="text" name="goodsNote" class="goodsNote" value=""><br/><br/>
		<!-- 返回参数:<input type="text" name="qrCodeStatus" class="qrCodeStatus" value=""><br/><br/> -->
		终端ip:<input type="text" name="userIp" class="userIp" value=""><br/><br/>
		<input type="button" onclick="shengcheng()" value="提交生成">
		
</center>
</body>
<script type="text/javascript">
	
	function shengcheng(){
		
		$.ajax({
			url:"${pageContext.request.contextPath}/HFBController/paySign.action",
			type:"post",
			data:$("input[name]").serialize(),
			success:function(data){
				$.ajax({
					url:"${pageContext.request.contextPath}/HFBController/wxpayParameter.action",
					type:"post",
					dataType:"json",
					data:$("input[name]").serialize()+"&sign="+data,
					success:function(data){
						console.info(data);
						
					}
				});
			}
		});
	}
</script>
</html>