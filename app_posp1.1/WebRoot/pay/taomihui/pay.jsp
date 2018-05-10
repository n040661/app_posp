<%@page import="xdt.util.UtilDate"%>
<%@page import="xdt.util.DateUtil"%>
<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>

<!DOCTYPE >
<html>
<head>
<base href="<%=basePath%>">

<title>生成二维码</title>

<script src="<%=basePath%>/js/jquery-2.0.0.js"></script>
</head>

<body>
	<fieldset>
		<legend>生成二维码</legend>
		<form action="">
		
		    <p>商户号:<input type="text" value="10012014708" name="merchantId"></p>
		    <p>交易金额:<input type="text" value="10.00" name="amount"></p>
		    <p>订单号:<input type="text" name="outTradeNo" value="<%=UtilDate.getOrderNum() %>" ></p>
		    <p>商品简介:<input type="text"  value="测试"  name="merchantName"></p>
		    <p>商品名称:<input type="text" value="测试商品" name="merchantShortName"></p>
		    <p>省份:<input type="text" value="天津" name="province"></p>
		    <p>城市:<input type="text"  name="city" value="天津市" ></p>
		    <p>区县:<input type="text"  value="河西区"  name="districtCode"></p>
		    <p>地址:<input type="text" value="天津市河西区小白楼亚太大厦1903" name="address"></p>
	        <p>结算卡号:<input type="text" value="6228480028542136370" name="bankNo"></p>
		    <p>持卡人姓名:<input type="text" value="尚延超" name="realName"></p>
		    <p>持卡人身份证:<input type="text" name="cardNo" value="410324199203231912" ></p>
		    <p>预留手机号:<input type="text" value="18902195076"  name="tel"></p>
		    <p>银行名称:<input type="text" value="中国农业银行" name="bankName"></p>
		    <p>银行支行编号:<input type="text" value="103" name="bankBranchId"></p>
		    <p>银行支行名称:<input type="text"  name="bankBranchName" value="中国农业银行天津正东支行" ></p>
		    <p>支付类型:
		    	<select name="payType">
		    		<option selected="selected" value="1">支付宝</option>
		    		<option value="2">微信</option>
		    	</select>	
		    </p>
		    <p><input type="button" value="生成" id="create" onclick="createCode()"><input type="button" value="查询" id="create" onclick="query()"></p>
		</form>
		
	</fieldset>
	<fieldset>
		<legend>生成结果</legend>
		<div>
		<img alt="二维码" src="" id="qrcode" style="width: 100;height: 100px;">
		<br>
		提示	:<p >生成一次刷新一次</p>
		结果:<p id="result"></p>
		
		<br> 
		<!-- https://qr.alipay.com/bax01348reskltxmpcyf40a2 -->
	</div>
	</fieldset>
	<fieldset>
		<legend>查询结果</legend>
		结果:<p id="query_result"></p>
		
		<br> 
		<!-- https://qr.alipay.com/bax01348reskltxmpcyf40a2 -->
	</div>
	</fieldset>
	<script>
	
		//签名
		function sign(method,bean,callback){
			$.post('<%=path%>/tmh/'+method+'.action',{requestData:JSON.stringify(bean)},function(data){
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
					amount:$('[name="amount"]').val(),
					outTradeNo:$('[name="outTradeNo"]').val(),
					merchantName:$('[name="merchantName"]').val(),
					merchantShortName:$('[name="merchantShortName"]').val(),
					province:$('[name="province"]').val(),
					city:$('[name="city"]').val(),
					districtCode:$('[name="districtCode"]').val(),
					address:$('[name="address"]').val(),
					bankNo:$('[name="bankNo"]').val(),
					realName:$('[name="realName"]').val(),
					cardNo:$('[name="cardNo"]').val(),
					tel:$('[name="tel"]').val(),
					bankName:$('[name="bankName"]').val(),
					bankBranchId:$('[name="bankBranchId"]').val(),
					bankBranchName:$('[name="bankBranchName"]').val(),
					payType:$('[name="payType"]').val()
					};
			sign('paySign',formBean,function(formBean){
				$.post('<%=path%>/tmh/pay.action',{requestData:JSON.stringify(formBean)},function(data){
					console.info(data);
					var url='http://s.jiathis.com/qrcode.php?url='+data.url;
					$('#qrcode').attr('src',url);
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
