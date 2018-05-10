<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>
<center>
<body>
<br><br>
<h1>页面支付信息填写</h1>
<h6>鼠标放在输入框会有提示信息！</h6>
<div class ="ss"></div>
<br><br>
<table>
	<tr>
		<td>商户号:</td>
		<td><input type="text" class="merchantId1" title="必填" value="100120242118015"></td>
	</tr>
	<tr>
		<td>商品描述:</td>
		<td><input type="text" class="OrderDesc" title="必填" value="测试商品"></td>
	</tr>
	<tr>
		<td>金额（分）:</td>
		<td><input type="text" class="Amount" title="必填"  value="1000"></td>
	</tr>
	<tr>
		<td>订单号（不能重复）:</td>
		<td><input type="text" class="merchOrderId" title="必填"  value=""></td>
	</tr>
	<tr>
		<td>商户保留信息:</td>
		<td><input type="text" class="ExtData" title="非必填"  value="测试"></td>
	</tr>
	<tr>
		<td>姓名:</td>
		<td><input type="text" class="name" title="非必填后期填也可以"  value="张三"></td>
	</tr>
	<tr>
		<td>手机号:</td>
		<td><input type="text" class="phone" title="非必填后期填也可以" value="13922897656"></td>
	</tr>
	<tr>
		<td>证件号码:</td>
		<td><input type="text" title="身份证号" title="非必填后期填也可以" class="number" value="440121197511140912"></td>
	</tr>
	<tr>
		<td>银行卡号:</td>
		<td><input type="text" class="bank" title="非必填后期填也可以" value="62220040001154868428"></td>
	</tr>
	
	<tr>
		<td>用户请求IP:</td>
		<td><input type="text" class="ip" title="针对配置了防钓鱼的商户需要提交；商户服务器通过获取访问ip得到该参数"></td>
	</tr>
	<tr>
		<td>异步通知地址:</td>
		<td><input type="text" class="notifyUrl" title=""></td>
	</tr>
</table>
<input type="button" value="提交订单" onclick="dianji()">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-3.1.1.min.js"></script>
    <script type="text/javascript">
    
        function dianji() {
        	var merchantId1 =$(".merchantId1").val();
        	var OrderDesc =$(".OrderDesc").val();
        	var Amount =$(".Amount").val();
        	var ExtData =$(".ExtData").val();
        	var MiscData =$(".phone").val()+"|0||"+$(".name").val()+"|"+$(".number").val()+"|"+$(".bank").val();
        	var ip =$(".ip").val();
        	var notifyUrl =$(".notifyUrl").val();
        	var merchOrderId =$(".merchOrderId").val();
        	
           $.ajax({
        	   type:"post",
        	   data:{"merchantId1":merchantId1,"orderDesc":OrderDesc,"amount":Amount,"extData":ExtData,"miscData":MiscData,"clientIp":ip,"notifyUrl":notifyUrl,"merchOrderId":merchOrderId},
				url:"${pageContext.request.contextPath}/clientH5Controller/merchantOrderTestH5.action",
				success:function(datas){
					 window.location.href=datas; 
					//$(".ss").html(datas);
					console.info(datas);
				}
           })
        }
    </script>
</body>
</center>
</html>