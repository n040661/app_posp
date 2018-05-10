<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <base href="<%=basePath%>">
    
    <title>My JSP 'register.jsp' starting page</title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="Content-Type content="text/html;charset=UTF-8">
	<meta http-equiv="description" content="This is my page">
	<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->
  <script src="<%=basePath%>/js/jquery-2.0.0.js"></script>
  </head>
  
  <body>
     <fieldset>
        <form action="">
            <p align="left">机构号:<input type="text" name="merchartId" value="10032063067" ></p>
		    <p align="left">账户号:<input type="text" name="account"  value="18902195076">(11位手机号)</p>
		    <p align="left">商户名称:<input type="text" name="merchartName" value="测试商品"></p>
		    <p align="left">密码:<input type="password" name="password" value="123456">(商户密码)</p>
		    <p align="left">开户姓名:<input type="text" name="realName" value="徐雷">(结算卡对应的真实姓名)</p>
		    <p align="left">证件号:<input type="text" name="certNo" value="341602199112223176"></p>
		    <p align="left">证件类型:<input type="text" name="certType" value="00">(默认00身份证)</p>
		    <p align="left">手机号码:<input type="text" name="mobile" value="15222871910">(结算卡绑定的11位手机号码)</p>
		    <p align="left">联行号:<input type="text" name="pmsBankNo" value="102110001181">(12位联行号)</p>
		    <p align="left">结算卡号:<input type="text" name="cardNo" value="6222020302064298250">(银行卡号)</p>
		    <p align="left">结算卡类型:<input type="text" name="cardType" value="1" ></p>
		    <p align="left">微信T1费率:<input type="text" name="wxT1Fee" value="0.002"></p>
		    <p align="left">微信T0费率:<input type="text" name="wxT0Fee" value="0.002"></p>
		    <p align="left">支付宝T1费率:<input type="text" name="alipayT1Fee" value="0.002"></p>
		    <p align="left">支付宝T0费率:<input type="text" name="alipayT0Fee" value="0.002"></p>
		    <p align="left"><input type="button" value="注册" id="create" onclick="createCode()"></p>
       </form>
       	</fieldset>
       	<fieldset>
		<legend>生成结果</legend>
		<div>
			结果:
			<p id="result"></p>

			<br>
		</div>
	</fieldset>
	<script type="text/javascript">
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
		console.info('注册')
		var formBean={
			    merchartId:$('[name="merchartId"]').val(),
				account:$('[name="account"]').val(),
				merchartName:$('[name="merchartName"]').val(),
				password:$('[name="password"]').val(),
				realName:$('[name="realName"]').val(),
				certNo:$('[name="certNo"]').val(),
				certType:$('[name="certType"]').val(),
				mobile:$('[name="mobile"]').val(),
				pmsBankNo:$('[name="pmsBankNo"]').val(),
				cardNo:$('[name="cardNo"]').val(),
				cardType:$('[name="cardType"]').val(),
				wxT1Fee:$('[name="wxT1Fee"]').val(),
				wxT0Fee:$('[name="wxT0Fee"]').val(),
				alipayT1Fee:$('[name="alipayT1Fee"]').val(),
				alipayT0Fee:$('[name="alipayT0Fee"]').val()
				};
		sign('paySign1',formBean,function(formBean){
			$.post('<%=path%>/ql/register.action', {
				requestData : JSON.stringify(formBean)
			}, function(data) {
				console.info(data);
				$('#result').html(JSON.stringify(data));

			}, 'json');
		})

	}
	</script>
  </body>
</html>
