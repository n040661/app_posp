<%@ page language="java" contentType="text/html; charset=UTF-8" import="xdt.util.UtilDate"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-3.1.1.min.js"></script>
<script src="http://pv.sohu.com/cityjson?ie=utf-8"></script>  
</head>
<body>
		<form action="${pageContext.request.contextPath}/ScanCodeController/scanCode.action" id="from" method="post">
		版本号:<input type="text" name="v_version" class="v_version" value="1.0.0.0"><br/><br/>
		商户号:<input type="text" name="v_mid" class="v_mid" value="10032061473"><br/><br/>
		支付订单号:<input type="text" name="v_oid" class="v_oid" value="<%=System.currentTimeMillis()%>"><br/><br/>
		订单交易金额(元):<input type="text" name="v_txnAmt" class="v_txnAmt" value="10"><br/><br/>
		异步回调地址:<input type="text" name="v_notify_url" class="v_notify_url" value="http://60.28.24.164:8104/app_posp/TFBController/notifyUrl.action"><br/><br/>
		异步回调地址:<input type="text" name="v_return_url" class="v_return_url" value="http://60.28.24.164:8104/app_posp/TFBController/returnUrl.action"><br/><br/>
		商品名称 :<input type="text" name="v_productName" class="v_productName" value="大饼鸡蛋" ><br/><br/>
		商品描述:<input type="text" name="v_productDesc" class="v_productDesc" value="大饼夹一切" ><br/><br/>
		交易时间:<input type="text" name="v_time" class="v_time" value="<%=UtilDate.getOrderNum()%>"><br/><br/>
		渠道类型:<input type="text" name="v_channel" class="v_channel" placeholder="D0:0,T1:1" value="1"><br/><br/>
		客户端ip:<input type="text" name="v_clientIP" class="v_clientIP" value=""><br/><br/>
		交易类型:<input type="text" name="v_cardType" class="v_cardType" value="QQ_NATIVE"><br/><br/>
		银行商户编码:<input type="text" name="v_merchantBankCode" class="v_merchantBankCode" value="" ><br/><br/>
		子商户号:<input type="text" name="v_subMerchantNo" class="v_subMerchantNo" value="" ><br/><br/>
		微信 Openid:<input type="text" name="v_openId" class="v_openId" value="" ><br/><br/>
		付款码数字:<input type="text" name="v_authCode" class="v_authCode" value="" ><br/><br/>
		APPID:<input type="text" name="v_appId" class="v_appId" value="" ><br/><br/>
		回传参数:<input type="text" name="v_attach" class="v_attach" value="大饼夹一切" ><br/><br/>
		<input type="button" onclick="shengcheng()" value="提交生成">
		</form>
		<div id="div"></div>
		<img alt="" src="" id="img">
		<table>
			<tr>
				<td colspan="2">交易类型</td>
			</tr>
			 <tr>
				<td>ALIPAY_NATIVE</td>
				<td>支付宝扫码(主扫) 【注：此为用户主扫，商户被扫】 </td>
			</tr>
			<tr>
				<td>ALIPAY_H5</td>
				<td>支付宝 H5</td>
			</tr>
			<!--<tr>
				<td>ALIPAY_CARD</td>
				<td>支付宝刷卡（被扫） 【注：此为用户被扫，商户主扫】</td>
			</tr>
			<tr>
				<td>ALIPAY_APP</td>
				<td>支付宝 APP 支付 </td>
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
			 <tr>
				<td>QQ_CARD</td>
				<td>QQ 刷卡（被扫） 【注：此为用户被扫，商户主扫】</td>
			</tr> 
			 <tr>
				<td>QQ_APP</td>
				<td>QQ APP 支付 </td>
			</tr> 
			<!-- <tr>
				<td>QQ_H5</td>
				<td>QQH5</td>
			</tr> -->
			 <tr>
				<td>UNIONPAY_NATIVE</td>
				<td>银联扫码（主扫）【注：此为用户主扫，商户被扫】 </td>
			</tr> 
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
	$(".v_clientIP").val(returnCitySN["cip"]);
	function shengcheng(){
		$.ajax({
			url:"${pageContext.request.contextPath}/ScanCodeController/paySign.action",
			type:"post",
			data:$("input[name]").serialize(),
			success:function(data){
				$("#from").append('<input type="text" name="v_sign" class="v_sign" style="display: none" value="'+data+'"><br/>');
				console.info($(".sign").val());
				$("#from").submit();
				  /* $.ajax({
					url:"${pageContext.request.contextPath}/ScanCodeController/scanCode.action",
					type:"post",
					dataType:"json",
					data:$("input[name]").serialize()+"&v_sign="+data,
					success:function(data){
						console.info(data);
						if(data.v_code=='0000'){
							location.href=data.v_result;
						}
						var src="http://pan.baidu.com/share/qrcode?w=150&h=150&url="+data.v_result;
						 $("#img").attr({"src":src});
					}
				});  */
			}
		});
	}
</script>
</html>