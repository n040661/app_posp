<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" import="xdt.quickpay.hengfeng.util.*" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-3.1.1.min.js"></script>
</head>
<body>
<center>
	<table>
		<tr>
			<td>商户号:</td>
			<td><input type="text" class="Spid" name="Spid" value="10012015423"></td><!-- 10012015423 -->
		</tr>
		<tr>
		<td>流水号:</td>
			<td><input type="text" class="orderId" name="orderId" value="<%=HFUtil.HFrandomOrder()%>">
		</tr>
		<tr>
			<td>银行卡:</td>
			<td><input type="text" class="CardNo" name="CardNo" value="6228450028016697770"></td>
		</tr>
		<tr>
			<td>证件号码:</td>
			<td><input type="text" class="IDCardNo" name="IDCardNo" value="120105197510055420"></td>
		</tr>
		<tr>
			<td>用户姓名:</td>
			<td><input type="text" class="UserName" name="UserName" value="李娟"></td>
		</tr>
		<tr>
			<td>银行预留电话:</td>
			<td><input type="text" class="TelephoneNo" name="TelephoneNo" value="13323358548"></td>
		</tr>
		<tr>
			<td>鉴权类型:</td>
			<td><select class="type" name="type" id="type">
			   <option selected="selected" value="1">实名认证</option>
			   <option value="2">精准错误返回实名认证(只支持借记卡)</option>
			</select></td>
		</tr>
		<tr>
			<td colspan="1"><input type="button" class="" name="" onclick="chaxun()" value="查询"></td>
		</tr>
	</table>
	
	<div class="div"></div>
</center>
<script type="text/javascript">
	function chaxun(){
	 var Spid=$(".Spid").val();
	 var orderId=$(".orderId").val();
	 var CardNo=$(".CardNo").val();
	 var IDCardType=$(".IDCardType option:selected").val();
	 var IDCardNo=$(".IDCardNo").val();
	 var UserName=$(".UserName").val();
	 var TelephoneNo=$(".TelephoneNo").val();
	 var type=$(".type").val();
	 var data={"Spid":Spid,"orderId":orderId,"CardNo":CardNo,"IDCardType":IDCardType,"IDCardNo":IDCardNo,"UserName":UserName,"TelephoneNo":TelephoneNo,"type":type};
	 console.info(IDCardType);
	 $.ajax({
		url:"${pageContext.request.contextPath}/jqController/jqParameter.action",
		type:"post",
		data:data,
		success:function(data){
			console.info(data);
			var datas={"Spid":Spid,"orderId":orderId,"CardNo":CardNo,"IDCardType":IDCardType,"IDCardNo":IDCardNo,"UserName":UserName,"TelephoneNo":TelephoneNo,"type":type,"Sign":data};
			$.ajax({
				url:"${pageContext.request.contextPath}/jqController/csjq.action",
				type:"post",
				dataType:"json",
				data:datas,
				success:function(data){
					console.info(data);
					$(".div").html("key:"+data.key+","+"msg:"+data.msg+","+"Spid:"+data.Spid+","+"BatchNo:"+data.BatchNo);
				}
			 });
		}
	 });
	}
</script>
</body>
</html>