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
	商户号:<input type="text" name="spid" class="spid" value="10012015423"/><br /><br />
	商户代付单号:<input type="text" name="sp_serialno" class="sp_serialno" value="<%=System.currentTimeMillis()%>"/><br /><br />
	交易金额:<input type="text" name="tran_amt" class="tran_amt" value="1000"/><br /><br />
	付款方式:<input type="text" name="pay_type" class="pay_type" value="1" placeholder="1-余额支付,2-企业网银,3-垫资支付"/><br /><br />
	收款人姓名:<input type="text" name="acct_name" class="acct_name" value="孙莹莹"/><br /><br />
	收款人账号:<input type="text" name="acct_id" class="acct_id" value="5239590008962971"/><br /><br />
	账号类型:<input type="text" name="acct_type" class="acct_type" value="1" placeholder="0-借记卡,1-贷记卡,2-对公账号"/><br /><br />
	收款人手机号码:<input type="text" name="mobile" class="mobile" value="18322296951"/><br /><br />
	开户行名称:<input type="text" name="bank_name" class="bank_name" value="华夏银行"/><br /><br />
	开户行支行联行号:<input type="text" name="bank_settle_no" class="bank_settle_no" value=""/><br /><br />
	业务类型:<input type="text" name="business_type" class="business_type" value="20101"/><br /><br />
	业务号码:<input type="text" name="business_no" class="business_no" value=""/><br /><br />
	摘要:<input type="text" name="memo" class="memo" value="代付"/><br /><br />
	<input type="button" onclick="daifu()" value="点击代付"/><br /><br />
	<div class="div"></div>
</center>
<script type="text/javascript">
	function daifu(){
		var spid = $(".spid").val();
		var sp_serialno = $(".sp_serialno").val();
		var tran_amt = $(".tran_amt").val();
		var pay_type = $(".pay_type").val();
		var acct_name = $(".acct_name").val();
		var acct_id = $(".acct_id").val();
		var acct_type = $(".acct_type").val();
		var mobile = $(".mobile").val();
		var bank_name = $(".bank_name").val();
		var bank_settle_no = $(".bank_settle_no").val();
		var business_type = $(".business_type").val();
		var business_no = $(".business_no").val();
		var memo = $(".memo").val();
		var type = $(".type").val();
		var data={"spid":spid,"sp_serialno":sp_serialno,"tran_amt":tran_amt,"pay_type":pay_type,"acct_name":acct_name,
				"acct_id":acct_id,"acct_type":acct_type,"mobile":mobile,"bank_name":bank_name,"bank_settle_no":bank_settle_no,
				"business_type":business_type,"business_no":business_no,"memo":memo,"type":type};
		$.ajax({
			url:"${pageContext.request.contextPath}/TFBController/payApplyParameter.action",
			type:"post",
			data:data,
			success:function(data){
				console.info(data);
				var data1={"spid":spid,"sp_serialno":sp_serialno,"tran_amt":tran_amt,"pay_type":pay_type,"acct_name":acct_name,
						"acct_id":acct_id,"acct_type":acct_type,"mobile":mobile,"bank_name":bank_name,"bank_settle_no":bank_settle_no,
						"business_type":business_type,"business_no":business_no,"memo":memo,"type":type,"sign":data};
				$.ajax({
					url:"${pageContext.request.contextPath}/TFBController/payApply.action",
					type:"post",
					data:data1,
					success:function(data){
						console.info(data);
						$(".div").html(data);
					}
				});
			}
		});
	}
</script>
</body>
</html>