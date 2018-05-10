<%@ page language="java" contentType="text/html; charset=UTF-8" import="xdt.util.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>商户订单数据(模拟)</title>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-3.1.1.min.js"></script>
</head>
<body>
    <input type="text" name="v_version" value="1.0.0.0">版本号 <br>
    <input type="text" name="v_mid" value="10032061473">商户号<br>
	<input type="text" name="v_userId" value="1523332456481">userId<br>
	<input type="button" onclick="shengcheng()" value="提交生成">
	<div id="div"></div>
	<script type="text/javascript">
	
	function shengcheng(){
		
		$.ajax({
			url:"${pageContext.request.contextPath}/quickPayAction/sign.action",
			type:"post",
			data:$("input[name]").serialize(),
			success:function(data){
				console.info(data);
				  $.ajax({
					url:"${pageContext.request.contextPath}/quickPayAction/quickVerifyId.action",
					type:"post",
					dataType:"json",
					data:$("input[name]").serialize()+"&v_sign="+data,
					success:function(data){
						console.info(data);
					}
				}); 
			}
		});
	}
</script>
</body>
</html>
