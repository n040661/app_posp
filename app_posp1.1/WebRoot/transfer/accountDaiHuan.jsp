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
总行数:<input type="text" name="v_count" class="v_count" value="1"><br />
总金额： <input type="text" class="v_sum_amount" name="v_sum_amount" value="0.02"/><br />
批次号： <input type="text" class="v_batch_no" name="v_batch_no" value="<%=System.currentTimeMillis()%>"/><br />
账号： <input type="text" class="v_cardNo" name="v_cardNo" value="6253360097935869"/><br />
帐户名： <input type="text" class="v_realName" name="v_realName" value="高立明"/><br />
开户行： <input type="text" class="v_bankname" name="v_bankname" value="中国农业银行"/><br />
银行代号： <input type="text" class="v_bankCode" name="v_bankCode" value="ABC"/><br />
省份： <input type="text" class="v_province" name="v_province" value="天津市"/><br />
城市： <input type="text" class="v_city" name="v_city" value="天津市"/><br />
金额： <input type="text" class="v_amount" name="v_amount" value="0.02"/><br />
客户标示(同名必传)： <input type="text" class="v_identity" name="v_identity" value="QP20180308135230358859"/><br />
联行号： <input type="text" class="v_pmsBankNo" name="v_pmsBankNo" value="103110009024"/><br />
代付类型： <input type="text" class="v_type" name="v_type" value="0"/><br />
代付时间： <input type="text" class="v_time" name="v_time" value="<%=UtilDate.getOrderNum()%>"/><br />
代付币种： <input type="text" class="v_currency" name="v_currency" value="1"/><br />
账号类型： <input type="text" class="v_accountType" name="v_accountType" value="1"/><br />
手机号： <input type="text" class="v_phone" name="v_phone" value="18322276803"/><br />
证件号： <input type="text" class="v_cert_no" name="v_cert_no" value="120224199303303413"/><br />
是否同名(1同名2非)： <input type="text" class="v_ifName" name="v_ifName" value="1"/><br />
1对私2对公： <input type="text" class="v_cardType" name="v_cardType" value="1"/><br />
<input type="button" value="提交" onclick="tijiao()"><br />
<div id ="div"></div>
<br />
</center>
<script type="text/javascript">
	function tijiao(){
		    var v_version = $(".v_version").val();
			var v_mid = $(".v_mid").val();
			var v_count = $(".v_count").val();
			var v_sum_amount =$(".v_sum_amount").val();
			var v_batch_no = $(".v_batch_no").val();
			var v_cardNo = $(".v_cardNo").val();
			var v_realName =$(".v_realName").val();
			var v_bankname = $(".v_bankname").val();
			var v_province = $(".v_province").val();
			var v_city = $(".v_city").val();
			var v_amount =$(".v_amount").val();
			var v_identity = $(".v_identity").val();
			var v_pmsBankNo = $(".v_pmsBankNo").val();
			var v_type =$(".v_type").val();
			var v_time = $(".v_time").val();
			var v_currency = $(".v_currency").val();
			var v_accountType = $(".v_accountType").val();
			var v_phone =$(".v_phone").val();
			var v_cert_no = $(".v_cert_no").val();
			var v_bankCode = $(".v_bankCode").val();
			var v_ifName = $(".v_ifName").val();
			var v_cardType = $(".v_cardType").val();
			
			var data={"v_version":v_version,
					  "v_mid":v_mid,
					  "v_count":v_count,
					  "v_sum_amount":v_sum_amount,
					  "v_batch_no":v_batch_no,
					  "v_cardNo":v_cardNo,
					  "v_realName":v_realName,
					  "v_bankname":v_bankname,
					  "v_province":v_province,
					  "v_city":v_city,
					  "v_amount":v_amount,
					  "v_identity":v_identity,
					  "v_pmsBankNo":v_pmsBankNo,
					  "v_type":v_type,
					  "v_time":v_time,
					  "v_currency":v_currency,
					  "v_accountType":v_accountType,
					  "v_phone":v_phone,
					  "v_cert_no":v_cert_no,
					  "v_bankCode":v_bankCode,
					  "v_ifName":v_ifName,
					  "v_cardType":v_cardType};
			$.ajax({
						url : "${pageContext.request.contextPath}/totalPayController/paySign.action",
						type : 'post',
						data : data,
						success : function(data) {
							console.info(data);
							var datas={"v_version":v_version,
									  "v_mid":v_mid,
									  "v_count":v_count,
									  "v_sum_amount":v_sum_amount,
									  "v_batch_no":v_batch_no,
									  "v_cardNo":v_cardNo,
									  "v_realName":v_realName,
									  "v_bankname":v_bankname,
									  "v_province":v_province,
									  "v_city":v_city,
									  "v_amount":v_amount,
									  "v_identity":v_identity,
									  "v_pmsBankNo":v_pmsBankNo,
									  "v_type":v_type,
									  "v_time":v_time,
									  "v_currency":v_currency,
									  "v_accountType":v_accountType,
									  "v_phone":v_phone,
									  "v_cert_no":v_cert_no,
									  "v_bankCode":v_bankCode,
									  "v_ifName":v_ifName,
									  "v_cardType":v_cardType,
									  "v_sign":data};
							$.ajax({
										url : "${pageContext.request.contextPath}/totalPayController/merchant/virement/mer_payment.action",
										type : 'post',
										data :datas,
										success : function(data) {
											console.info(data);
											$("#div").text(data);
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