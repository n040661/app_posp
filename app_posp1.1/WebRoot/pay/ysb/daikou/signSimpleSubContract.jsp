<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
<!-- 引入JQuery -->
<script type="text/javascript" src="${pageContext.request.contextPath}/jquery-easyui-1.5.2/jquery-1.8.3.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/My97DatePicker/WdatePicker.js"></script>
<link href="${pageContext.request.contextPath}/css/404/index.css"  type="text/css" rel="stylesheet"/>
<style type="text/css">
table th{
	float: right;
}
</style>
</head>
<body>
<div id="total">
	<div id="one">
		<form action="" name="demoOneForm" id="demoOneForm" method="post">
			<table>
				<tr>
					<td colspan="2" style="text-align: center;color: red;font-size: 24px;">1.1子协议录入接口</td>
				</tr>
				<tr>
					<th>商户编号：</th>
					<td><input type="text" style="width:350px;" name="merchantId" value="10012012745"></td>
				</tr>
				<tr>
					<th>银行卡号：</th>
					<td><input type="text" style="width:350px;" name="cardNo" value="6228480028051728872"/></td>
				</tr>
				<tr>
					<th>用户姓名：</th>
					<td><input type="text" style="width:350px;" name="name" value="罗星"></td>
				</tr>
				<tr>
					<th>身份证号：</th>
					<td><input type="text" style="width:350px;" name="idCardNo" value="210922198702016916"></td>
				</tr>
				<tr>
					<th>手机号：</th>
					<td><input type="text" style="width:350px;" name="phoneNo" value="18622273275"></td>
				</tr>
				<tr>
					<th>子协议开始时间：</th>
					<td><input type="text" class="Wdate" style="width:350px;" name="startDate"  onfocus="WdatePicker({isShowClear:false,readOnly:true})" value="20180108"/></td>
				</tr>
				<tr>
					<th>子协议结束时间：</th>
					<td><input type="text" class="Wdate" style="width:350px;" name="endDate" onfocus="WdatePicker({isShowClear:false,readOnly:true})" value="20180108"></td>
				</tr>
				<tr>
					<th>扣款频率：</th>
					<td>
						<select name="cycle" style="width:350px;">
							<option value="">请选择</option>
							<option value="1">每年</option>
							<option value="2">每月</option>
							<option value="3">每日</option>
						</select>
					</td>
				</tr>
				<tr>
					<th>扣款次数限制：</th>
					<td><input type="text" style="width:350px;" name="triesLimit" value="1"></td>
				</tr>
				<tr>
					<th></th>
					<td><input type="button" value="提交录入" id="tj" style="background: #999"></td>
				</tr>
				<tr>
			</table>
		</form>
	</div>
</div>
</body>
<script type="text/javascript">
$("#tj").click(function(){
	demoOneForm.action='${pageContext.request.contextPath}/ysb/hfsignForWap.action';
	demoOneForm.submit();
})
</script>
</html>