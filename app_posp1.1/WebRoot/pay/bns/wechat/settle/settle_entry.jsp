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
					id="mch_id" name="mch_id" value="100123112343005" />
			</p>
			<p>
				order_num:<input style="width: 300px;" id="order_num"
					name="order_num" value="O20170705190515322106351" />
			</p>
			<p>
				order_count:<input style="width: 300px;" id="order_count" name="order_count"
					value="1" />
			</p>
			<p>
				out_trade_no:<input style="width: 300px;" id="out_trade_no"
					name="out_trade_no" value="<%=RandomUtil.getOrderNum("ORDER")%>" />
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
				mch_id:$('[name="mch_id"]').val(),
				order_num:$('[name="order_num"]').val(),
				order_count:$('[name="order_count"]').val(),
				out_trade_no:$('[name="out_trade_no"]').val()
					};
			sign('settleSign',formBean,function(formBean){
				$.post('<%=path%>/wechat/settle.action',{requestData:JSON.stringify(formBean)},function(data){
					       console.info(data);
						   $('#qrcode').attr('src',url);
							$('#result').html(JSON.stringify(data));				
				},'json');
			})
			
		}
	</script>
</body>
</html>