<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>订单查询</title>
<script src="<%=basePath%>/js/jquery-2.0.0.js"></script>
</head>
<body class="body-bg">
	<fieldset>
		<legend>生成二维码</legend>
		<form action="">
			<p>
				商户号:<input style="width: 300px; color: lavender;" id="merchantId"
					name="merchantId" value="10035045336" />
			</p>
			<p>
				订单号:<input style="width: 300px;" id="out_trade_no"
					name="out_trade_no" value="" />
			</p>


			<p>
				<input type="button" value="查询" id="create" onclick="query()" />
			</p>
		</form>
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
		//查询
		function query(){
			console.info('创建二维码')
			var formBean={
					merchantId:$('[name="merchantId"]').val(),
					out_trade_no:$('[name="out_trade_no"]').val(),
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