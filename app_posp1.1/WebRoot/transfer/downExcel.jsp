<%@ page language="java" contentType="text/html; charset=UTF-8" import="xdt.util.UtilDate" 
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-3.1.1.min.js"></script>
</head>
<body>
<center>
版本号:<input type="text" name="v_version" class="v_version" value="1.0.0.0"><br />
商户号:<input type="text" name="v_mid" class="v_mid" value="10021019419"><br />
时间： <input type="text" class="v_time" name="v_time" value="20180409"/><br />
<input type="button" value="提交" onclick="tijiao()"><br />
<div id ="div"></div>

<br /><br /><br />

</center>
<script type="text/javascript">
	function tijiao(){
			$.ajax({
						url : "${pageContext.request.contextPath}/totalPayController/querySign.action",
						type : 'post',
						data : $("input[name]").serialize(),
						success : function(data) {
							console.info(data);
							$.ajax({
										url : "${pageContext.request.contextPath}/totalPayController/DownExcel.action",
										type : 'post',
										data :$("input[name]").serialize()+"&v_sign:"+data,
										dataType:"json",
										success : function(data) {
											console.info(data);
										}
									});
						}
					});
	}
	/* function tijiao(){
	   
	} */
</script>
</body>

</html>