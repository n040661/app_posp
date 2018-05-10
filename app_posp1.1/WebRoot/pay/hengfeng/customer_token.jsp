<%@ page language="java" contentType="text/html; charset=UTF-8"
	import="xdt.quickpay.hengfeng.util.*" pageEncoding="UTF-8"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>开通token</title>
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
<script src="<%=basePath%>/js/jquery-3.1.1.min.js"></script>
</head>
<body>
	<form id="form" action="${pageContext.request.contextPath}/hfquick/hfpay1.action" method="post">
		<center>
				<div ><img alt="" src="${pageContext.request.contextPath }/images/4.png" width="300" height="50"></div>
			<table>
				<tr>
					<td>商户号：</td>
					<td><input type="text" id="merchantId" name="merchantId" value="<%=request.getParameter("merchantCode") %>"></td>
				</tr>
				<tr>
					<td>订单交易时间：</td>
					<td><input type="text" id="txnTime" readonly="readonly"  name="txnTime"
						value="<%=HFUtil.dateTime()%>"></td>
				</tr>
				<tr>
					<td>订单号：</td>
					<td><input type="text"  id="orderId" readonly="readonly" name="orderId"
						value="<%=HFUtil.HFrandomOrder()%>"></td>
				</tr>
				<tr>
					<td>卡号：</td>
					<td><input type="text" id="accNo" name="accNo" style="color: #CCC;" value="6212260302026649095" onfocus="test1(this)" onblur="test2(this)"></td>
				</tr>
				<tr>
					<td><input type="hidden" id="frontUrl" name="frontUrl"
						value="http://60.28.24.164:8103/app_posp/pay/hengfeng/result.jsp"></td>
				</tr>
				<tr>
					<td><input type="hidden" id="backUrl" name="backUrl"
						value="http://60.28.24.164:8103/app_posp/hfquick/hfbgPayResult.action"></td>
				</tr>
				<tr>
					<td>交易类型:</td>
					<td><input type="text"  id="tranTp" name="tranTp"
						value="<%=request.getParameter("tranTp") %>"></td>
				</tr>
				<tr>
					<td colspan="2" align="center"><input type="submit"
						name="confirm" onclick="time(this)" value="确定"></td>
				</tr>
			</table>
		</center>
	</form>
<!-- 	<script type="text/javascript">
    var signmsg="";
    var merchantId =$("#merchantId").val();
    var txnTime =$("#txnTime").val();
	var orderId =$("#orderId").val();
	var accNo =$("#accNo").val();
	var frontUrl =$("#frontUrl").val();	
	var backUrl =$("#backUrl").val();
	var tranTp =$("#tranTp").val();
	var data1={"merchantId":merchantId,"txnTime":txnTime,"orderId":orderId,"accNo":accNo,"txnTime":txnTime,"frontUrl":frontUrl,"backUrl":backUrl,"tranTp":tranTp};
	var data2;
	var wait=60;
	var ss =0;
    function time(o) { 
		    if(ss ==0){
			     console.info('创建二维码');
				 console.info(data1);
				 $.ajax({
						type : "post",
						url : "${pageContext.request.contextPath}/hfquick/hfsignForWap.action",
						data:data1,
						success : function(data) {
							console.info("111:"+data);
							signmsg=signmsg+data;
							console.info("11:"+signmsg);
							data2={"merchantId":merchantId,"txnTime":txnTime,"orderId":orderId,"accNo":accNo,"txnTime":txnTime,"frontUrl":frontUrl,"backUrl":backUrl,"tranTp":tranTp,"signmsg":data};
							console.info(data2);
							$.ajax({
								type : "post",
								url : "${pageContext.request.contextPath}/hfquick/hfpay.action",
								data:data2,
								success : function(data) {
									console.info("222");
								}
							})
						}
					});
		    }

	 } 
</script> -->
</body>
</html>