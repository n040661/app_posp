<%@page import="xdt.util.UtilDate"%>
<%@ page contentType="text/html;charset=UTF-8"
	import="xdt.quickpay.hengfeng.util.*" language="java"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>
<!DOCTYPE html>
<html>

<head>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">

<title>测试页面</title>
<style type="text/css">
.page {
	height: 50px;
	width: 200px;
}
</style>

<script src="<%=basePath%>/js/jquery-2.0.0.js"></script>
 <script type="text/javascript">
            function test1 (obj) {
                // var username = document.getElementById('username');
                if (obj.value == obj.defaultValue) {
                    obj.value = "";
                }
            }
            function test2 (obj) {
                // var username = document.getElementById('username');
                if (obj.value == "") {
                    obj.value = obj.defaultValue;
                }
            }
        </script>
</head>

<body>




	<fieldset>
		<legend>商户注册页面</legend>
		<div ><img alt="" src="${pageContext.request.contextPath }/images/4.png" width="300" height="50"></div>
		<form action="">
			<p>
				商户号:<input type="text" name="merchantCode" style="color: #CCC;" value="请输入商户号" onfocus="test1(this)" onblur="test2(this)">
			</p>
			<p>
				支付方式: <select name="payType">
					<option selected="selected" value="0">混合快捷</option>
					<option value="1">标准快捷</option>
				</select>
			</p>
			<p>
				产品类型:<select name="product">
					<option selected="selected" value="0">商户版</option>
					<option value="1">个人版</option>
				</select>
			</p>
			<p>
				结算方式: <select name="tranTp">
					<option selected="selected" value="0">D0</option>
					<option value="1">T1</option>
				</select>
			</p>
			<p>
				商户手续费率: <input type="text" name="fee" style="color: #CCC;" value="请输入费率" onfocus="test1(this)" onblur="test2(this)"><font color="#0093dd">%</font>
			</p>
			<p>
				清算手续费:<input type="text" style="color: #CCC;" value="请输入手续费" name="paymentfee" onfocus="test1(this)" onblur="test2(this)"><font color="#0093dd">元/笔</font>
			</p>
			<hr width="300" height="50" align="left">
			<font color="#0093dd">******只有标准快捷商户版需要填写以下信息，其他情况无需填写*****</font>
			<p>
				户名:<input type="text" name="realName" style="color: #CCC;" value="请输入姓名" onfocus="test1(this)" onblur="test2(this)">
			</p>
			<p>
				结算卡号:<input type="text" name="cardNo" style="color: #CCC;" value="请输入卡号" onfocus="test1(this)" onblur="test2(this)">
			</p>
			<p>
				<input type="hidden" name="identidy" value="1123">
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
			$.post('<%=path%>/live/qrcode/'+method+'.action',{requestData:JSON.stringify(bean)},function(data){
				console.info(data);
				bean.sign=data;		
				callback(bean);
			},'text');
		}
		//创建二维码
		function createCode(){
			console.info('创建二维码')
			var formBean={
				merchantCode:$('[name="merchantCode"]').val(),
				payType:$('[name="payType"]').val(),
				product:$('[name="product"]').val(),
				tranTp:$('[name="tranTp"]').val(),
				fee:$('[name="fee"]').val(),
				paymentfee:$('[name="paymentfee"]').val(),
				realName:$('[name="realName"]').val(),
				certNo:$('[name="certNo"]').val(),
				cardNo:$('[name="cardNo"]').val(),
				mobile:$('[name="mobile"]').val(),
				bankCardName:$('[name="bankCardName"]').val(),
				pmsBankNo:$('[name="pmsBankNo"]').val(),
				identidy:$('[name="identidy"]').val(),
					};
			sign('paySign1',formBean,function(formBean){
				$.post('<%=path%>/live/qrcode/customerRegister.action',{requestData:JSON.stringify(formBean)},function(data){
					console.info(data);
					//var url='http://s.jiathis.com/qrcode.php?url='+data.url;
					//$('#qrcode').attr('src',url);
					$('#result').html(JSON.stringify(data));
					
				},'json');
			})
			
		}
		
		//查询
		function query(){
			console.info('创建二维码')
			var formBean={
				merchantCode:$('[name="merchantCode"]').val(),
				identidy:$('[name="identidy"]').val(),
					};
			sign('querySign',formBean,function(formBean){
				$.post('<%=path%>/live/qrcode/query.action', {
									requestData : JSON.stringify(formBean)
								}, function(data) {
									console.info(data);
									$('#query_result').html(
											JSON.stringify(data));
								}, 'json');
							});
						}
					</script>
</body>

</html>
