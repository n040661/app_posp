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
	<form action="${pageContext.request.contextPath}/TFBController/wxpayParameter.action" method="post">
		<table>
			<tr>
				<td>商户号:</td>
				<td><input type="text" name="spid" class="spid" value="10012015423"/></td>
			</tr>
			<tr>
				<td>商户订单号:</td>
				<td><input type="text" name="sp_billno" class="sp_billno" value="<%=System.currentTimeMillis()%>"/></td>
			</tr>
			<tr>
				<td>交易金额:</td>
				<td><input type="text" name="tran_amt" class="tran_amt" value="1"/></td>
			</tr>
			<tr>
				<td>通知回调URL:</td>
				<td><input type="text" name="notify_url" class="notify_url" value="http://60.28.24.164:8104/app_posp/TFBController/notifyUrl.action"/></td>
			</tr>
			<tr>
				<td>成功跳转URL:</td>
				<td><input type="text" name="pay_show_url" class="pay_show_url" value="http://60.28.24.164:8104/app_posp/TFBController/returnUrl.action"/></td>
			</tr>
			<tr>
				<td>商户ip:</td>
				<td><input type="text" name="spbill_create_ip" class="spbill_create_ip" value="192.168.17.1"/></td>
			</tr>
			<tr>
				<td>外接支付方式:</td>
				<td><input type="text" name="out_channel" class="out_channel" value="qqpay" placeholder="wxpay:微信支付,qqpay:QQ钱包支付"/></td>
			</tr>
			<tr>
				<td>支付类型:</td>
				<td><input type="text" name="pay_type" class="pay_type" value="800201"/></td>
			</tr>
			<tr>
				<td>支付限制:</td>
				<td><input type="text" name="pay_limit" class="pay_limit" value=""/></td>
			</tr>
			<tr>
				<td>二维码:</td>
				<td><input type="text" name="auth_code" class="auth_code" value="" placeholder="支付方式为刷卡必填"/></td>
			</tr>
			<tr>
				<td>商品描述:</td>
				<td><input type="text" name="item_name" class="item_name" value="大饼鸡蛋"/></td>
			</tr>
			<tr>
				<td>三级商户名称:</td>
				<td><input type="text" name="bank_mch_name" class="bank_mch_name" value="" placeholder="微信支付必填"/></td>
			</tr>
			<tr>
				<td>三级商户ID:</td>
				<td><input type="text" name="bank_mch_id" class="bank_mch_id" value="" placeholder="微信支付必填"/></td>
			</tr>
			<tr>
				<td>终端设备id:</td>
				<td><input type="text" name="sp_udid" class="sp_udid" value="" placeholder="qq钱包条码支付必填"/></td>
			</tr>
			<tr>
				<td colspan="2"><input type="button" onclick="tijiao()" value="提交"/></td>
			</tr>
		</table>
		<span class="url"></span>
		<div>
			<img alt="二维码" class="img" src="">
		</div>
	</form>
</center>
	<script type="text/javascript">
		function tijiao(){
			
			var spid =$(".spid").val();
			var sp_billno =$(".sp_billno").val();
			var tran_amt =$(".tran_amt").val();
			var notify_url =$(".notify_url").val();
			var pay_show_url =$(".pay_show_url").val();
			var spbill_create_ip =$(".spbill_create_ip").val();
			var out_channel =$(".out_channel").val();
			var pay_type =$(".pay_type").val();
			var pay_limit =$(".pay_limit").val();
			var auth_code =$(".auth_code").val();
			var item_name =$(".item_name").val();
			var bank_mch_name =$(".bank_mch_name").val();
			var bank_mch_id =$(".bank_mch_id").val();
			var sp_udid =$(".sp_udid").val();
			var data={"spid":spid,"sp_billno":sp_billno,"tran_amt":tran_amt,"notify_url":notify_url,"pay_show_url":pay_show_url,"spbill_create_ip":spbill_create_ip,"out_channel":out_channel
					,"pay_type":pay_type,"pay_limit":pay_limit,"auth_code":auth_code,"item_name":item_name,"bank_mch_name":bank_mch_name,"bank_mch_id":bank_mch_id,"sp_udid":sp_udid};
			$.ajax({
				url:"${pageContext.request.contextPath}/TFBController/wxpayParameter.action",
				type:"post",
				data:data,
				success:function(data){
					console.info(data);
					var data1={"spid":spid,"sp_billno":sp_billno,"tran_amt":tran_amt,"notify_url":notify_url,"pay_show_url":pay_show_url,"spbill_create_ip":spbill_create_ip,"out_channel":out_channel
							,"pay_type":pay_type,"pay_limit":pay_limit,"auth_code":auth_code,"item_name":item_name,"bank_mch_name":bank_mch_name,"bank_mch_id":bank_mch_id,"sp_udid":sp_udid,"sign":data};
					
					$.ajax({
						url:"${pageContext.request.contextPath}/TFBController/wxPayApply.action",
						type:"post",
						dataType:"json",
						data:data1,
						success:function(data){
							console.info(data);
							var src ="http://api.k780.com:88/?app=qr.get&data="+data.qrcode;/* http://pan.baidu.com/share/qrcode?w=150&h=150&url= */
							console.info(src);
							$(".url").html(data.qrcode);
							$(".img").attr({"src":src});
						}
					})
				}
			})
		}
		
	</script>
</body>
</html>