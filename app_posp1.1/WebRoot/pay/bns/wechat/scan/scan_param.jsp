<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml"/>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>扫码</title>
<link rel="stylesheet" type="text/css"
	href="${pageContext.request.contextPath}/css/404/cmstop-error.css"
	media="all"/>
	<script src="<%=basePath%>/js/jquery-2.0.0.js"></script>
</head>
<body>
	<fieldset>
		<legend>生成二维码</legend>
		<form action="">
			<p>
				MerchantId:<input style="width: 300px; color: lavender;"
					id="merchantId" name="merchantId" value="100123112343005" />
			</p>
			<p>
				key:<input style="width: 300px; color: lavender;" id="key"
					name="key" value="${key }" />
			</p>
			<p>
				service_type:<input style="width: 300px; color: lavender;"
					id="service_type" name="service_type" value="${scan.service_type }" />
			</p>
			<p>
				appid:<input readonly="readonly"
					style="width: 300px; color: lavender;" id="appid" name="appid"
					value="${scan.appid }" />
			</p>
			<p>
				mch_id:<input readonly="readonly"
					style="width: 300px; color: lavender;" id="mch_id" name="mch_id"
					value="${scan.mch_id }" />
			</p>
			<p>
				out_trade_no:<input style="width: 300px;" id="out_trade_no"
					name="out_trade_no" value="${scan.out_trade_no }" />
			</p>
			<p>
				device_info:<input style="width: 300px;" id="device_info"
					name="device_info" value="${scan.device_info }" />
			</p>
			<p>
				body:<input style="width: 300px;" id="body" name="body"
					value="${scan.body }" />
			</p>
			<p>
				detail:<input style="width: 300px;" id="detail" name="detail"
					value="${scan.detail }" />
			</p>
			<p>
				attach:<input style="width: 300px;" id="attach" name="attach"
					value="${scan.attach }" />
			</p>
			<p>
				fee_type:<input readonly="readonly"
					style="width: 300px; color: lavender" id="fee_type" name="fee_type"
					value="${scan.fee_type }" />
			</p>
			<p>
				total_fee:<input style="width: 300px;" id="total_fee"
					name="total_fee" value="${scan.total_fee }" />
			</p>
			<p>
				spbill_create_ip:<input readonly="readonly"
					style="width: 300px; color: lavender" id="spbill_create_ip"
					name="spbill_create_ip" value="${scan.spbill_create_ip }" />
			</p>
			<p>
				notify_url:<input style="width: 300px;" id="notify_url"
					name="notify_url" value="${scan.notify_url }" />
			</p>
			<p>
				time_start:<input style="width: 300px;" id="time_start"
					name="time_start" value="${scan.time_start }" />
			</p>
			<p>
				time_expire:<input style="width: 300px;" id="time_expire"
					name="time_expire" value="${scan.time_expire }" />
			</p>
			<p>
				op_user_id:<input style="width: 300px;" id="op_user_id"
					name="op_user_id" value="${scan.op_user_id }" />
			</p>
			<p>
				goods_tag:<input style="width: 300px;" id="goods_tag"
					name="goods_tag" value="${scan.goods_tag }" />
			</p>
			<p>
				product_id:<input style="width: 300px;" id="product_id"
					name="product_id" value="${scan.product_id }" />
			</p>
			<p>
				limit_pay:<input style="width: 300px;" id="limit_pay"
					name="limit_pay" value="${scan.limit_pay }" />
			</p>
			<p>
				nonce_str:<input style="width: 300px;" id="nonce_str"
					name="nonce_str" value="${scan.nonce_str }" />
			</p>
			<p>
				<input type="button" value="生成" id="create" onclick="createCode()"/><input
					type="button" value="查询" id="create" onclick="query()"/>
			</p>
		</form>

	</fieldset>
	<fieldset>
		<legend>生成结果</legend>
		<div>
			<img alt="二维码" src="" id="qrcode" style="width: 100; height: 100px;"/>
				<br/> 提示 :
					<p>生成一次刷新一次</p> 结果:
					<p id="result"></p> <br/> <!-- https://qr.alipay.com/bax01348reskltxmpcyf40a2 -->
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
				key:$('[name="key"]').val(),
				service_type:$('[name="service_type"]').val(),
				appid:$('[name="appid"]').val(),
				mch_id:$('[name="mch_id"]').val(),
				out_trade_no:$('[name="out_trade_no"]').val(),
				device_info:$('[name="device_info"]').val(),
				body:$('[name="body"]').val(),
				detail:$('[name="detail"]').val(),
				attach:$('[name="attach"]').val(),
				fee_type:$('[name="fee_type"]').val(),
				total_fee:$('[name="total_fee"]').val(),
				spbill_create_ip:$('[name="spbill_create_ip"]').val(),
				notify_url:$('[name="notify_url"]').val(),
				time_expire:$('[name="time_expire"]').val(),
				op_user_id:$('[name="op_user_id"]').val(),
				goods_tag:$('[name="goods_tag"]').val(),
				product_id:$('[name="product_id"]').val(),
				time_start:$('[name="time_start"]').val(),
				limit_pay:$('[name="limit_pay"]').val(),
				nonce_str:$('[name="nonce_str"]').val()
					};
			sign('codeSign',formBean,function(formBean){
				$.post('<%=path%>/wechat/scan.action',{requestData:JSON.stringify(formBean)},function(data){
					console.info(data);
					var url='http://s.jiathis.com/qrcode.php?url='+data.code_url;
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