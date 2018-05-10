<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <base href="<%=basePath%>">
    
    <title>My JSP 'validate.jsp' starting page</title>
    
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
        <form action="">
           <p align="left">商户号:<input type="text" name="merchartId"  value="10041036244"></p>
           <p align="left">账户号:<input type="text" name="account"  value="18902195076">(11位手机号)</p>
           <p align="left"><input type="button" value="查询" id="create" onclick="createCode()"></p>
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
  		console.info('验证')
  		var formBean={
  			    merchartId:$('[name="merchartId"]').val(),
  				account:$('[name="account"]').val()
  				};
  		sign('paySign1',formBean,function(formBean){
  			$.post('<%=path%>/ql/validate.action', {
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
