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
	<table>
		<tr>
			<td>商户号:</td>
			<td><input type="text" class="Spid" name="Spid" value=""></td><!-- 10012015423 -->
		</tr>
		<tr>
			<td>银行卡:</td>
			<td><input type="text" class="CardNo" name="CardNo" value=""></td>
		</tr>
		<tr>
			<td>证件类型:</td>
			<td><select class="IDCardType">
				<option value="01" selected="selected">身份证</option>
				<option value="02">军官证</option>
				<option value="03">护照</option>
				<option value="04">回乡证</option>
				<option value="05">台胞证</option>
				<option value="06">警官证</option>
				<option value="07">士兵证</option>
				<option value="99">其他证件</option>
			</select></td>
		</tr>
		<tr>
			<td>证件号码:</td>
			<td><input type="text" class="IDCardNo" name="IDCardNo" value=""></td>
		</tr>
		<tr>
			<td>用户姓名:</td>
			<td><input type="text" class="UserName" name="UserName" value=""></td>
		</tr>
		<tr>
			<td>银行预留电话:</td>
			<td><input type="text" class="TelephoneNo" name="TelephoneNo" value=""></td>
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
	 var CardNo=$(".CardNo").val();
	 var IDCardType=$(".IDCardType option:selected").val();
	 var IDCardNo=$(".IDCardNo").val();
	 var UserName=$(".UserName").val();
	 var TelephoneNo=$(".TelephoneNo").val();
	 var data={"Spid":Spid,"CardNo":CardNo,"IDCardType":IDCardType,"IDCardNo":IDCardNo,"UserName":UserName,"TelephoneNo":TelephoneNo};
	 console.info(IDCardType);
	 $.ajax({
		url:"${pageContext.request.contextPath}/jqController/jqParameter.action",
		type:"post",
		data:data,
		success:function(data){
			console.info(data);
			var datas={"Spid":Spid,"CardNo":CardNo,"IDCardType":IDCardType,"IDCardNo":IDCardNo,"UserName":UserName,"TelephoneNo":TelephoneNo,"Sign":data};
			$.ajax({
				url:"${pageContext.request.contextPath}/jqController/jq.action",
				type:"post",
				dataType:"json",
				data:datas,
				success:function(data){
					console.info(data);
					$(".div").html("Code:"+data.Code+","+"Data:"+data.Data+","+"Exception:"+data.Exception+","+"ExtInfo:"+data.ExtInfo+","+"IsCharging:"+data.IsCharging+","+"Result:"+data.Result+","+"TransactionNo:"+data.TransactionNo+","+"BatchNo:"+data.BatchNo);
				}
			 });
		}
	 });
	}
</script>
</body>
</html>