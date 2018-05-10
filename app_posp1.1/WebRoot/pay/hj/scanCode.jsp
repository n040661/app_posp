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
		
		商户号:<input type="text" name="merchantNo" class="merchantId" value="10032061473"><br/><br/>
		支付订单号:<input type="text" name="orderNo" class="merchantOrderNo" value="<%=System.currentTimeMillis()%>"><br/><br/>
		订单交易金额(分):<input type="text" name="amount" class="payAmount" value="1000"><br/><br/>
		异步回调地址:<input type="text" name="notifyUrl" class="notifyUrl" value="http://60.28.24.164:8104/app_posp/TFBController/notifyUrl.action"><br/><br/>
		同步回调地址:<input type="text" name="returnUrl" class="callBackUrl" value="https://www.baidu.com"><br/><br/>
		商品名称 :<input type="text" name="productName" class="productName" value="大饼鸡蛋" ><br/><br/>
		交易类型:<input type="text" name="frpCode" class="frpCode" value="WEIXIN_NATIVE"><br/><br/>
		银行商户编码 :<input type="text" name="merchantBankCode" class="merchantBankCode" value="" ><br/><br/>
		子商户号:<input type="text" name="subMerchantNo" class="subMerchantNo" value="" ><br/><br/>
		是否展示图片:<input type="text" name="isShowPic" class="isShowPic" value="1" ><br/><br/>
		微信 Openid:<input type="text" name="openId" class="openId" value="" ><br/><br/>
		付款码数字:<input type="text" name="authCode" class="authCode" value="" ><br/><br/>
		APPID:<input type="text" name="appId" class="appId" value="" ><br/><br/>
		微信 H5 模式:<input type="text" name="transactionModel" class="transactionModel" value="" ><br/><br/>
		商品描述:<input type="text" name="productDesc" class="productDesc" value="大饼夹一切" ><br/><br/>
		回传参数:<input type="text" name="mp" class="mp" value="大饼夹一切" ><br/><br/>
		<input type="button" onclick="shengcheng()" value="提交生成">
		<div id="div"></div>
		<img alt="" src="" id="img">
		<table>
			<tr>
				<td colspan="2">交易类型</td>
			</tr>
			<!-- <tr>
				<td>ALIPAY_NATIVE</td>
				<td>支付宝扫码(主扫) 【注：此为用户主扫，商户被扫】 </td>
			</tr>
			<tr>
				<td>ALIPAY_CARD</td>
				<td>支付宝刷卡（被扫） 【注：此为用户被扫，商户主扫】</td>
			</tr>
			<tr>
				<td>ALIPAY_APP</td>
				<td>支付宝 APP 支付 </td>
			</tr>
			<tr>
				<td>ALIPAY_H5</td>
				<td>支付宝 H5</td>
			</tr>
			<tr>
				<td>ALIPAY_FWC</td>
				<td>支付宝服务窗</td>
			</tr>
			<tr>
				<td>ALIPAY_SYT</td>
				<td>支付宝收银台 </td>
			</tr> -->
			<tr>
				<td>WEIXIN_NATIVE</td>
				<td>微信扫码（主扫【注：此为用户主扫，商户被扫】 </td>
			</tr>
			<!-- <tr>
				<td>WEIXIN_CARD</td>
				<td>微信刷卡（被扫）【注：此为用户被扫，商户主扫】 </td>
			</tr>
			<tr>
				<td>WEIXIN_APP</td>
				<td>微信 APP 支付 </td>
			</tr>
			<tr>
				<td>WEIXIN_H5</td>
				<td>微信 H5</td>
			</tr>
			<tr>
				<td>WEIXIN_GZH</td>
				<td>微信公众号 </td>
			</tr>
			<tr>
				<td>WEIXIN_XCX</td>
				<td>微信小程序 </td>
			</tr> -->
			<tr>
				<td>JD_NATIVE</td>
				<td>京东扫码（主扫） 【注：此为用户主扫，商户被扫】</td>
			</tr>
			<!-- <tr>
				<td>JD_CARD</td>
				<td>京东刷卡（被扫）【注：此为用户被扫，商户主扫】</td>
			</tr> -->
			<!-- <tr>
				<td>JD_APP</td>
				<td> 京东 APP 支付 </td>
			</tr> -->
			<!-- <tr>
				<td>JD_H5</td>
				<td>京东 H5</td>
			</tr> -->
			<tr>
				<td>QQ_NATIVE</td>
				<td>QQ 扫码（主扫）【注：此为用户主扫，商户被扫】 </td>
			</tr>
			<!-- <tr>
				<td>QQ_CARD</td>
				<td>QQ 刷卡（被扫） 【注：此为用户被扫，商户主扫】</td>
			</tr> -->
			<!-- <tr>
				<td>QQ_APP</td>
				<td>QQ APP 支付 </td>
			</tr> -->
			<!-- <tr>
				<td>QQ_H5</td>
				<td>QQH5</td>
			</tr> -->
			<!-- <tr>
				<td>UNIONPAY_NATIVE</td>
				<td>银联扫码（主扫）【注：此为用户主扫，商户被扫】 </td>
			</tr> -->
			 <!-- <tr>
				<td>UNIONPAY_CARD</td>
				<td>银联刷卡（被扫）【注：此为用户被扫，商户主扫】</td>
			</tr> -->
			<!-- <tr>
				<td>UNIONPAY_APP</td>
				<td>银联 APP 支付</td>
			</tr> -->
			<!--<tr>
				<td>UNIONPAY_H5</td>
				<td>银联 H5</td>
			</tr> -->
			<tr>
				<td>BAIDU_NATIVE</td>
				<td>百度扫码（主扫）【注：此为用户主扫，商户被扫】 </td>
			</tr>
		</table>

 
 
 
 
 
 

		
</body>

<script type="text/javascript">
	
	function shengcheng(){
		
		$.ajax({
			url:"${pageContext.request.contextPath}/HJController/paySign.action",
			type:"post",
			dataType:"json",
			data:$("input[name]").serialize(),
			success:function(data){
				  $.ajax({
					url:"${pageContext.request.contextPath}/HJController/wxpayParameter.action",
					type:"post",
					dataType:"json",
					data:$("input[name]").serialize()+"&sign="+data.sign,
					success:function(data){
						console.info(data.result);
						var src="http://pan.baidu.com/share/qrcode?w=150&h=150&url="+data.result;
						 $("#img").attr({"src":src});
					}
				}); 
			}
		});
	}
</script>
</html>