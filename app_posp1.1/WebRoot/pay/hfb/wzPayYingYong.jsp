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
		<input type="text" name="merchantId" class="merchantId" value="10032061473" style="display: none;"><br/><br/>
		<input type="text" name="merchantOrderNo" class="merchantOrderNo" value="<%=System.currentTimeMillis()%>" style="display: none;"><br/><br/>
		订单交易金额(元):<input type="text" class="payAmount" value=""><br/>
		<input type="text" name="tradeType" class="tradeType" value="weixin_qr" style="display: none;"style="display: none;"><br/>
		<input type="text" name="notifyUrl" class="notifyUrl" value="http://60.28.24.164:8104/app_posp/TFBController/notifyUrl.action" style="display: none;"><br/><br/>
		商品名称:<input type="text" name="goodsName" class="goodsName" value=""><br/><br/>
		<input type="text" name="goodsNote" class="goodsNote" value="" style="display: none;">
		<input type="text" name="userIp" class="userIp" value="127.1.1.0" style="display: none;">
		<input type="button" onclick="shengcheng()" value="提交生成">
		
		<div class='div'></div>
		<img alt="" class='img' src="">
</center>
</body>
<script type="text/javascript">
	
	function shengcheng(){
		$(".div").html('<font color="red">请求中请等待...</font>');
		$.ajax({
			url:"${pageContext.request.contextPath}/HFBController/paySign.action",
			type:"post",
			data:$("input[name]").serialize()+"&payAmount="+parseFloat($(".payAmount").val())*100,
			success:function(data){
				$.ajax({
					url:"${pageContext.request.contextPath}/HFBController/wxpayParameter.action",
					type:"post",
					dataType:"json",
					data:$("input[name]").serialize()+"&payAmount="+parseFloat($(".payAmount").val())*100+"&sign="+data,
					success:function(data){
						console.info(data);
						if(data.respCode=='00'){
							$(".div").html('<font color="green">生成成功！</font>');
						var src ="http://api.k780.com:88/?app=qr.get&data="+data.payUrl;/* http://pan.baidu.com/share/qrcode?w=150&h=150&url= */
						console.info(src);
						$(".img").attr({"src":src});
						
						}else{
							$(".div").html('<font color="red">生成失败！</font>');
						}
					}
				});
			}
		});
	}
</script>
</html>