<%@ page language="java" contentType="text/html; charset=UTF-8"
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
商户号:<input type="text" name="clientId" class="clientId" value="10012015423"><br />
订单号:<input type="text" name="reqId" class="reqId" value="<%=System.currentTimeMillis()%>"><br />
代付类型:<input type="text" name="payTyp " class="payTyp " value="01"><br />
付款数据id：<input type="text" class="payItemId" name="payItemId" value="CJZFGS<%=System.currentTimeMillis()%>"/><br />
金额(元)： <input type="text" class="payAmt" name="payAmt" value="11"/><br />
银行卡号： <input type="text" class="actNo" name="actNo" value="6228450028016697770"/><br />
开户名： <input type="text" class="actNm" name="actNm" value="李娟"/><br />
账户类型： <input type="text" class="actTyp" name="actTyp" value="01"/><br />
银行编码： <input type="text" class="bnkCd" name="bnkCd" value="103"/><br />
银行名称： <input type="text" class="bnkNm" name="bnkNm" value="中国农业银行"/><br />
是否短信提醒： <input type="text" class="smsFlg" name="smsFlg" value="1"/><br /><br />
联行号： <input type="text" class="LbnkNo" name="LbnkNo" value="103110023002"/><br />
联行名称： <input type="text" class="LbnkNm" name="LbnkNm" value="中国农业银行天津广厦支行"/><br />
备注： <input type="text" class="rmk" name="rmk" value="代付"/><br />
手机号： <input type="text" class="tel" name="tel" value="13323358548"/><br />
银行付款用途： <input type="text" class="bankPayPurpose" name="bankPayPurpose" value="交易"/><br />

<input type="button" value="提交" onclick="tijiao()"><br />
<br />
</center>
<script type="text/javascript">
	function tijiao(){
	    var reqId = $(".reqId").val();
		var clientId = $(".clientId").val();
		var payTyp = $(".payTyp").val();
		var payItemId =$(".payItemId").val();
		var payAmt = $(".payAmt").val();
		var actNo = $(".actNo").val();
		var actNm =$(".actNm").val();
		var actTyp = $(".actTyp").val();
		var bnkCd = $(".bnkCd").val();
		var bnkNm = $(".bnkNm").val();
		var smsFlg = $(".smsFlg").val();
		var LbnkNo = $(".LbnkNo").val();
		var LbnkNm = $(".LbnkNm").val();
		var rmk = $(".rmk").val();
		var tel = $(".tel").val();
		var bankPayPurpose = $(".bankPayPurpose").val();
		var data={"reqId":reqId,"clientId":clientId,"payTyp":payTyp,"payItemId":payItemId,"payAmt":payAmt,"actNo":actNo,
				"actNm":actNm,"actTyp":actTyp,"bnkCd":bnkCd,"bnkNm":bnkNm,"smsFlg":smsFlg,"LbnkNo":LbnkNo,"LbnkNm":LbnkNm,"rmk":rmk,"tel":tel,"bankPayPurpose":bankPayPurpose};
		$.ajax({
					url : "${pageContext.request.contextPath}/SXFController/paySign.action",
					type : 'post',
					data : data,
					success : function(data) {
						console.info(data);
						var datas={"reqId":reqId,"clientId":clientId,"payTyp":payTyp,"payItemId":payItemId,"payAmt":payAmt,"actNo":actNo,
								"actNm":actNm,"actTyp":actTyp,"bnkCd":bnkCd,"bnkNm":bnkNm,"smsFlg":smsFlg,"LbnkNo":LbnkNo,"LbnkNm":LbnkNm,"rmk":rmk,"tel":tel,"bankPayPurpose":bankPayPurpose,"sign":data};
						$.ajax({
									url : "${pageContext.request.contextPath}/SXFController/pay.action",
									type : 'post',
									data :datas,
									success : function(data) {
										console.info(data);
									}
								});
					}
				});
	}
</script>
</body>

</html>