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
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1" />
<meta name="description" content="" />
<meta name="renderer" content="webkit" />
<meta name="viewport"
	content="initial-scale=1.0, maximum-scale=1.0, user-scalable=no" />
<meta name="viewport" content="width=device-width, initial-scale=1" />
<title>提现结算</title>
<script src="${pageContext.request.contextPath}/js/jquery-3.1.1.min.js"></script>
<style type="text/css">
    table tr{
        display: inline-block;
        margin-bottom: 7%;
    }
</style>
</head>
<body>
<div style="width: 90%;margin: 10% auto;">
<div style="width: 100%;height: 59px;"><img alt="" src="${pageContext.request.contextPath }/images/4.png" width="100%" height="100%"></div>
<table>
	<tr>
		<td>商户号:</td>
		<td><input type="text" class="MERCHANT_ID" name="MERCHANT_ID" id="MERCHANT_ID" title="必填" readonly="readonly"  value="${merid}" style="color:#000000;border:none;font-size:15px;"></td>
	</tr>
	<tr>
		<td>订单号:</td>
		<td style="margin-left: 2%;"><input type="text" class="BATCH_NO" name="BATCH_NO" id="BATCH_NO" title="必填" style="color: #000000;border:none;font-size:15px;" value="<%=HFUtil.randomOrder()%>" onfocus="test1(this)" onblur="test2(this)" readonly="readonly" ></td>
	</tr>
	<tr>
		<td>流水号:</td>
		<td><input type="text" class="SN" name="SN" id="SN" title="必填"  style="color: #000000;border:none;font-size:15px;" value="<%=HFUtil.HFrandomOrder()%>" onfocus="test1(this)" onblur="test2(this)" readonly="readonly" ></td>
	</tr>
	<tr>
		<td>提现金额:</td>
		<td><input type="text" class="AMOUNT" name="AMOUNT" id="AMOUNT" title="必填"   value="${paymount}" style="color:#000000;border:none;font-size:15px;width: 60px" readonly="readonly" ><font color="red">（每笔提现费用:2元）</font></td>
	</tr>
	<tr>
		<td>户名:</td>
		<td><input type="text" class="ACC_NAME" name="ACC_NAME" id="ACC_NAME" value="${name}"  style="color:#000000;font-size:15px;" onfocus="test1(this)" onblur="test2(this)" readonly="readonly"></td>
	</tr>
	<tr>
		<td>结算卡号:</td>
		<td><input type="text" class="ACC_NO" name="ACC_NO" id="ACC_NO" title="非必填后期填也可以"  style="color: #000000;font-size:15px;" value="6226888888888888" onfocus="test1(this)" onblur="test2(this)"></td>
	</tr>
	<tr>
	      <input type="hidden" class="ACC_PROVINCE" name="ACC_PROVINCE" id="ACC_PROVINCE" title="非必填后期填也可以"  value=""/>
	      <input type="hidden" class="ACC_CITY" name="ACC_CITY" id="ACC_CITY" title="非必填后期填也可以"  value=""/>
	      <input type="hidden" class="BANK_NAME" name="BANK_NAME" id="BANK_NAME" title="非必填后期填也可以"  value=""/>
	      <input type="hidden" class="ACC_PROP" name="ACC_PROP" id="ACC_PROP" title="非必填后期填也可以"  value=""/>
	      <input type="hidden" class="BANK_NO" name="BANK_NO" id="BANK_NO" title="非必填后期填也可以"  value=""/>
	      <input type="hidden" class="ACC_TYPE" name="ACC_TYPE" id="ACC_TYPE" title="非必填后期填也可以"  value=""/>
              <input type="hidden" class="orderid" name="orderid" id="orderid" title="非必填后期填也可以"  value="${orderid}"/>		      
	</tr>
	<tr>
		<td colspan="2"><input type="checkbox" checked="checked"><font size="2">我已确认以上提现操作，体现至以上银行卡</font></td>
	</tr>
</table>
<div>
<input type="button" value="确认提现" onclick="tijiao()" style="background-color:#87CEEB;width:250px;height:30px;border:none;margin-left: 4%;">
</div>
</div>
<script type="text/javascript">
	function tijiao(){
		var BATCH_NO=$(".BATCH_NO").val();
		var MERCHANT_ID=$(".MERCHANT_ID").val();
		var map="";
		var SN =$(".SN").val();
		var AMOUNT =$(".AMOUNT").val();
		var ACC_NO =$(".ACC_NO").val();
		var ACC_NAME =$(".ACC_NAME").val();
		var ACC_PROVINCE =$(".ACC_PROVINCE").val();
		var ACC_CITY =$(".ACC_CITY").val();
		var BANK_NAME =$(".BANK_NAME").val();
		var BANK_NO =$(".BANK_NO").val();
		var ACC_PROP =$(".ACC_PROP").val();
		var ACC_TYPE =$(".ACC_TYPE").val();
                var orderid =$(".orderid").val(); 

		map =SN+"?"+AMOUNT+"?"+ACC_NO+"?"+ACC_NAME+"?"+ACC_PROVINCE+"?"+ACC_CITY+"?"+BANK_NAME+"?"+BANK_NO+"?"+ACC_PROP+"?"+ACC_TYPE+"1,";
			
		map=map.substr(0,map.length-1);
		$.ajax({
			url:"${pageContext.request.contextPath}/clientCollectionPayController/paySign.action",
			dataType:"json",
			 type: 'post',
			data:{"BATCH_NO":BATCH_NO,"MERCHANT_ID":MERCHANT_ID,"MAP":map.toString()},
			success:function(data){
				console.info(data.sIGN);
				$.ajax({
					url:"${pageContext.request.contextPath}/clientCollectionPayController/paySigns.action",
					type: 'post',
					data:{"BATCH_NO":BATCH_NO,"MERCHANT_ID":MERCHANT_ID ,"MAP":map.toString(),"SIGN":data.sIGN},
					dataType:"json",
					success:function(data){
						console.info(data);
						$.each(data.BODYS,function(index,datas){
							console.info(datas.REMARK);
							if(datas.PAY_STATE=='00A4'){
								$(".ss").html('订单代付中...');
							}else if(datas.PAY_STATE=='0000'){
								window.location.href="http://pay.changjiezhifu.com:8103/app_posp/pay/yilian/success.jsp?batch_no="+BATCH_NO+"&orderid="+orderid;
							}else{
								window.location.href="http://pay.changjiezhifu.com:8103/app_posp/pay/yilian/fail.jsp?batch_no="+BATCH_NO+"&orderid="+orderid;
							}
						});
						
					}
				});
			}
		});
	}
</script>
</html>