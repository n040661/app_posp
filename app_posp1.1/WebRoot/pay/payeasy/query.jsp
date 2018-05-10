<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>Insert title here</title>
<script src="<%=basePath%>/js/jquery-2.0.0.js"></script>
</head>
<body>

 <fieldset>
		<legend>查询页面</legend>
		<form action="">
		    <p>商户号:<input type="text" value="10012112721" name="merchantId"></p>
		    <p>订单id:<input type="text" value="" name="v_oid"></p>

		    <p><input type="button" value="查询" id="create" onclick="query()"></p>
		</form>
		
	</fieldset>
	<fieldset>
		<legend>查询结果</legend>
		结果:<p id="query_result"></p>
		
		<br> 
	</div>
	</fieldset>
	<script>
	
		//签名
		function sign(method,bean,callback){
			$.post('<%=path%>/payeasy/'+method+'.action',{requestData:JSON.stringify(bean)},function(data){
				console.info(data);
				bean.v_mac=data;		
				callback(bean);
			},'text');
		}
		//查询
		function query(){
			console.info('查询接口')
			var formBean={
				 merchantId:$('[name="merchantId"]').val(),
				      v_oid:$('[name="v_oid"]').val(),
					};
			sign('paySign',formBean,function(formBean){
				$.post('<%=path%>/payeasy/queryPayResult.action',{requestData:JSON.stringify(formBean)},function(data){
					console.info(data);
					$('#query_result').html(JSON.stringify(data));
				},'json');
			});
		}
	
	</script>

</body>
</html>