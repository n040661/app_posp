<%@page import="xdt.util.UtilDate"%>
<%@page import="xdt.util.DateUtil"%>
<%@page import="cn.beecloud.BCUtil"%>
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

<title>测试在线支付</title>

<script src="<%=basePath%>/js/jquery-2.0.0.js"></script>
</head>

<body>
	<fieldset>
		<legend>生成二维码</legend>
		<form action="${pageContext.request.contextPath }/cj/quick/pay.action" >
		    <p>商户号:<input type="text" readonly="readonly" value="100510112345708" name="pid"></p>
		    <p>订单id:<input type="text" value="<%=BCUtil.generateRandomUUIDPure() %>" name="transactionid"></p>
		    <p>交易金额:<input type="text" placeholder="单位分" name="orderamount" value="50000" ></p>
		    <p>交易时间:<input type="text" readonly="readonly" value="<%=UtilDate.getOrderNum() %>"  name="ordertime"></p>
			<p>前台通知页面:<input type="text" name="pageurl" value="http://60.28.24.164:8101/app_posp/pay/quick/result.jsp"><br>			    
			<p>后台通知页面:<input type="text" name="bgurl" value="http://60.28.24.164:8101/app_posp/pay/quick/index.jsp"><br>			    
			<p>
				支付方式:
				<select name="paytype">
					<option value="UN_WAP" selected="selected">手机wap支付</option>
					<option value="UN_WEB" selected="selected">在线支付</option>
				</select>
			</p>
			<p>银行卡号:<input type="text" name="bankno" value="6228480402564890018"></p>
			<p><input type="hidden" name="signmsg" ></p>
			<p><input type="button" onclick="sign()" value="支付"></p>
		</form>
		
	</fieldset>
<script type="text/javascript">
	function sign(){
		console.info('签名')
		$.post('${pageContext.request.contextPath }/cj/quick/signForWap.action',$('form').serialize(),function(data){
			console.info(data);
			if(data.status=='1'){
				$('[name="signmsg"]').val(data.signmsg);
				$('form')[0].submit();
			}else if(data.status=='0'){
				alert('失败')
			}else if(data.status=='2'){
				alert(data.signmsg)
			}
		},'json')
	}
</script>	
</body>
</html>
