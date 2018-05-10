<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="xdt.quickpay.nbs.common.util.*"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>公众号支付</title>
<link rel="stylesheet" type="text/css"></link>

<script src="<%=basePath%>/js/jquery-2.0.0.js"></script>
</head>
<body>
	<fieldset>
		<legend>生成二维码</legend>
		<form action="">
			<p>
				MerchantId:<input style="width: 300px; color: lavender;"
					id="merchantId" name="merchantId" value="100120154114115" />
			</p>
			<p>
				out_trade_no:<input style="width: 300px;" id="out_trade_no"
					name="out_trade_no" value="<%=RandomUtil.getOrderNum("ORDER")%>" />
			</p>
			<p>
				body:<input style="width: 300px;" id="body" name="body"
					value="Ipad_mini_16G" />
			</p>
			<p>
				total_fee:<input style="width: 300px;" id="total_fee"
					name="total_fee" value="1000" />
			</p>
			<p>
				notify_url:<input style="width: 300px;" id="notify_url"
					name="notify_url"
					value="http://157.10.1.115:8080/app_posp/wechat/bgPayResult.action" />
			</p>
			<p>
				attach:<input style="width: 300px;" id="attach"	name="attach" value="attach" />
			</p>
			<p>
				<input type="button" value="生成" id="create" onclick="createCode()" /><input
					type="button" value="查询" id="create" onclick="query()" />
			</p>
		</form>

	</fieldset>
	<fieldset>

		<legend>生成结果</legend>
		<div>
			<img alt="二维码" src="" id="qrcode" style="width: 100; height: 100px;" />
			<br /> 提示 :
			<p>生成一次刷新一次</p>
			结果:
			<p id="result"></p>
			<br />
		</div>
	</fieldset>
	<fieldset>
		<legend>查询结果</legend>
		结果:
		<p id="query_result"></p>
	</fieldset>
	<script>
	
		//签名
		function sign(method,bean,callback){
			
			$.post('<%=path%>/wechat/'+method+'.action',{requestData:JSON.stringify(bean)},function(data){
				console.info(data);
				bean.sign=data;		
				callback(bean);
			},'text');
		}
		//创建二维码
		function createCode(){
			console.info('创建二维码')
			var formBean={
				merchantId:$('[name="merchantId"]').val(),
				out_trade_no:$('[name="out_trade_no"]').val(),
				body:$('[name="body"]').val(),
				total_fee:$('[name="total_fee"]').val(),
				notify_url:$('[name="notify_url"]').val(),
				attach:$('[name="attach"]').val()
					};
			sign('paySign',formBean,function(formBean){
				$.post('<%=path%>/wechat/wechatWebPay.action',{requestData:JSON.stringify(formBean)},function(data){
					       console.info(data);
						 /*  var body1 = encodeURI(encodeURI(document.getElementById('body').value)); */
						   var array=document.getElementById('out_trade_no').value+'||'+document.getElementById('body').value+'||'+document.getElementById('total_fee')+'||'+document.getElementById('notify_url').value+'||'+data.sign+'||'+data.service_type+'||'+data.mch_id+'||'+data.spbill_create_ip+'||'+data.nonce_str+'||'+data.brcb_gateway_url+'||'+document.getElementById('attach');
					       var url1='http://157.10.1.115:8080/app_posp/pay/bns/wechat/webpay/webpay_pay.jsp?array=' + array;
						   var url='http://s.jiathis.com/qrcode.php?url='+url1;
						   $('#qrcode').attr('src',url);
							$('#result').html(JSON.stringify(data));				
				},'json');
			})
			
		}
		
		//查询
		function query(){
			console.info('创建二维码')
			var formBean={
					merchantId:$('[name="MerchantId"]').val(),
					orderId:$('[name="orderId"]').val(),
					};
			sign('orderSign',formBean,function(formBean){
				$.post('<%=path%>/wechat/orderquery_param.action', {
					requestData : JSON.stringify(formBean)
				}, function(data) {
					console.info(data);
					$('#query_result').html(JSON.stringify(data));
				}, 'json');
			});
		}
	</script>
</body>
</html>