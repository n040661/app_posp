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
商户号:<input type="text" name="v_mid" class="v_mid" value="10032061473"><br />
时间： <input type="text" class="v_time" name="v_time" value="20180409"/><br />
<input type="button" value="提交" onclick="tijiao()"><br />
<div id ="div"></div>

<br /><br /><br />

01 受理失败 02 受理成功 03 受理中 04 交易进行中 05 交易完成
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
										url : "${pageContext.request.contextPath}/totalPayController/selectPay.action",
										type : 'post',
										data :$("input[name]").serialize()+"&v_sign:"+data,
										dataType:"json",
										success : function(data) {
											var html='订单号,时间,状态,成功付款笔数,失败付款笔数,成功付款总金额单位分,失败付款总金额单位分 \n ';
											for (var i = 0; i < data.length; i++) {
											html+=data[i].merchantOrderId+","+data[i].merchantOrderTime+","+data[i].transStatus+","+data[i].succPaidNum+","+data[i].failPaidNum+","+data[i].succPaidTotalAmt+","+data[i].failPaidTotalAmt+" \n ";
											}
											$("#div").text(html);
											console.info(html);
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