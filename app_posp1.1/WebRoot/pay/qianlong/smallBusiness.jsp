<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="utf-8" import="xdt.quickpay.qianlong.util.*"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<base href="<%=basePath%>">

<title>生成二维码</title>

<meta http-equiv="pragma" content="no-cache">
<meta http-equiv="cache-control" content="no-cache">
<meta http-equiv="expires" content="0">
<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
<meta http-equiv="description" content="This is my page">
<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->
<script src="<%=basePath%>/js/jquery-2.0.0.js"></script>
</head>

<body>
	<fieldset>
		<legend>生成二维码</legend>
		<form action="">
			<p>
				商户号:<input type="text" name="merchartId" value="10041036244">
			</p>
			<p>
				回调地址:<input type="text" name="notifyUrl"
					value="http://60.28.24.164:8102/app_posp/ql/bgPayResult.action">
			</p>
			<p>
				账户:<input type="text" name="account" value="18322276802">(11为手机号码)
			</p>
			<p>
				商品名称:<input type="text" name="subject" value="测试商品">
			</p>
			<p>
				机构订单号:<input type="text" value="<%=SdkUtil.randomOrder()%>"
					name="orgOrderNo">
			</p>
			<p>
				订单金额:<input type="text" name="amount" value="1300">(单位分)
			</p>
			<p>
				付款方式: <select name="source">
					<option selected="selected" value="0">微信</option>
					<option  value="1">支付宝</option>
				   </select>
			</p>
			<p>
				交易类型: <select name="tranTp">
					<option selected="selected" value="0">T0</option>
					<option  value="1">T1</option>
				    </select>
			</p>
			<p>
				<input type="button" value="生成" id="create" onclick="createCode()"><input
					type="button" value="查询" id="create" onclick="query()">
			</p>
		</form>

	</fieldset>
	<fieldset>
		<legend>生成结果</legend>
		<div>
			<img alt="二维码" src="" id="qrcode" style="width: 100; height: 100px;">
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
		</div>
	</fieldset>
	<script>
	
		//签名
		function sign(method,bean,callback){
			$.post('<%=path%>/ql/'+method+'.action',{requestData:JSON.stringify(bean)},function(data){
				console.info(data);
				bean.sign=data;		
				callback(bean);
			},'text');
		}
		//创建二维码
		function createCode(){
			console.info('创建二维码')
			var formBean={
				    merchartId:$('[name="merchartId"]').val(),
					notifyUrl:$('[name="notifyUrl"]').val(),
					account:$('[name="account"]').val(),
					subject:$('[name="subject"]').val(),
					orgOrderNo:$('[name="orgOrderNo"]').val(),
					amount:$('[name="amount"]').val(),
					source:$('[name="source"]').val(),
					tranTp:$('[name="tranTp"]').val()
					};
			sign('paySign',formBean,function(formBean){
				$.post('<%=path%>/ql/dimension.action', {
					requestData : JSON.stringify(formBean)
				}, function(data) {
					console.info(data);
					var url = 'http://s.jiathis.com/qrcode.php?url='
							+ data.qrcode;
					$('#qrcode').attr('src', url);
					$('#result').html(JSON.stringify(data));

				}, 'json');
			})

		}
		//查询
		function query(){
			console.info('创建二维码')
			var formBean={
				    merchartId:$('[name="merchartId"]').val(),
					orgOrderNo:$('[name="orgOrderNo"]').val(),
					};
			sign('paySign',formBean,function(formBean){
				$.post('<%=path%>/ql/payQuery.action', {
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
