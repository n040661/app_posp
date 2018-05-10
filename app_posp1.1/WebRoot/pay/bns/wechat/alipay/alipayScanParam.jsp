<%@ page language="java" import="xdt.util.UtilDate"
	contentType="text/html; charset=UTF-8"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>Insert title here</title>
<script type="text/javascript"
	src="<%=basePath%>/js/jquery-3.1.1.min.js"></script>
</head>
<body>
	<fieldset>
		<legend>生成二维码</legend>
		<p>
			商户号:<input type="text" class="merchantId" name="merchantId"
				value="100123112343005" />
		</p>
		<p>
			商户订单号:<input type="text" class="out_trade_no" name="out_trade_no"
				value="<%=UtilDate.bnsrandomOrder()%>" />
		</p>
		<p>
			总金额:<input type="text" class="total_fee" name="total_fee"
				value="1000" />
		</p>
		<p>
			订单标题:<input type="text" class="subject" name="subject" value="测试商户" />
		</p>
		<p>
			商品描述:<input type="text" class="body" name="body" value="测试产品" />
		</p>
		<p>
			通知地址:<input type="text" class="notify_url" name="notify_url"
				value="http://60.28.24.164:8107/app_posp/wechat/wechatWebPay.action" />
		</p>
		<p>
			自定义跳转页面:<input type="text" class="callback_url" name="callback_url"
				value="http://60.28.24.164:8107/app_posp/wechat/wechatWebPay.action" />
		</p>
		<p>
			随机字符串:<input type="text" class="nonce_str" name="nonce_str"
				value="bns" />
		</p>
		<p>
			设备号:<input type="text" class="device_info" name="device_info" value="<%=UtilDate.getDate()%>" />
		</p>
		<p>
			交易类型:<input type="text" class="service_type" name="service_type" value="3" />
		</p>
		<p>
			<input type="button" value="生成" id="create" onclick="createCode()"><input
				type="button" value="查询" id="create" onclick="query()">
		</p>
	</fieldset>
	<fieldset>
		<legend>生成结果</legend>
		<div>
			<br> 提示 :
			<p>生成一次刷新一次</p>
			结果:
			<p id="result"></p>

			<br>
			<!-- https://qr.alipay.com/bax01348reskltxmpcyf40a2 -->
		</div>
	</fieldset>
	<fieldset>
		<legend>查询结果</legend>
		结果:
		<p id="query_result"></p>

		<br>
		<!-- https://qr.alipay.com/bax01348reskltxmpcyf40a2 -->
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
				total_fee:$('[name="total_fee"]').val(),
				subject:$('[name="subject"]').val(),
				body:$('[name="body"]').val(),
				notify_url:$('[name="notify_url"]').val(),
				callback_url:$('[name="callback_url"]').val(),
				nonce_str:$('[name="nonce_str"]').val(),
				service_type:$('[name="service_type"]').val()
					};
			sign('alipayScan',formBean,function(formBean){
				$.post('<%=path%>/wechat/alipayScanParam.action',{requestData:JSON.stringify(formBean)},function(data){
					console.info(data.code_url);
					window.location.href=data.code_url;
					$('#result').html(JSON.stringify(data));
					
				},'json');
			})
			
		}
		
		//查询
		function query(){
			console.info('创建二维码')
			var formBean={
					merchantId:$('[name="merchantId"]').val(),
					orderId:$('[name="orderId"]').val(),
					};
			sign('querySign',formBean,function(formBean){
				$.post('<%=path%>/pufa/query.action',{requestData:JSON.stringify(formBean)},function(data){
					console.info(data);
					$('#query_result').html(JSON.stringify(data));
				},'json');
			});
		}
	
	</script>
</body>
</html>