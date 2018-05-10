<%@ page language="java" contentType="text/html; charset=UTF-8"
   import="xdt.quickpay.hengfeng.util.*"  pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0, minimum-scale=0.5, maximum-scale=2.0, user-scalable=yes" />
<title>快捷支付</title>
<style type="text/css">
    table tr{
        display: inline-block;
        margin-bottom: 7%;
    }
</style>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-3.1.1.min.js"></script>
 <script type="text/javascript">
            function test1 (obj) {
                // var username = document.getElementById('username');
                if (obj.value == obj.defaultValue) {
                    obj.value = "";
                }
            }
            function test2 (obj) {
                // var username = document.getElementById('username');
                if (obj.value == "") {
                    obj.value = obj.defaultValue;
                }
            }
        </script>
</head>
<body>
<center>
<div style="width: 90%;margin: 10% auto;">
<div style="width: 100%;height: 59px;"><img alt="" src="${pageContext.request.contextPath }/images/4.png" width="100%" height="100%"></div>
<table>
	<tr>
		<td>商户号:</td>
		<td><input type="text" class="merchantId1" title="必填" readonly="readonly"  value="<%=request.getParameter("merchantCode")%>" style="color:#000000;border:none;font-size:15px;"></td>
	</tr>
	<tr>
		<td>商品描述:</td>
		<td><input type="text" class="OrderDesc" title="必填" readonly="readonly" style="color: #000000;font-size:15px;border:none;" value="网购消费" onfocus="test1(this)" onblur="test2(this)" readonly="readonly" ></td>
	</tr>
	<tr>
		<td>金额:</td>
		<td><input type="text" class="Amount" title="必填"  style="color: #000000;font-size:15px;" value="1000" onfocus="test1(this)" onblur="test2(this)"></td>
	</tr>
	<tr>
		<td>订单号:</td>
		<td><input type="text" class="merchOrderId" title="必填"  readonly="readonly" value="<%=HFUtil.HFrandomOrder()%>" style="color:#000000;border:none;font-size:15px;"></td>
	</tr>
	<tr>
		<td><input type="hidden" class="ExtData" title="非必填"  value="测试"></td>
	</tr>
	<tr>
		<td>姓名:</td>
		<td><input type="text" class="name" title="非必填后期填也可以"   value="张三" style="color:#000000;font-size:15px" onfocus="test1(this)" onblur="test2(this)"></td>
	</tr>
	<tr>
		<td>手机号:</td>
		<td><input type="text" class="phone" title="非必填后期填也可以"  style="color: #000000;font-size:15px;" value="13188888888" onfocus="test1(this)" onblur="test2(this)"></td>
	</tr>
	<tr>
		<td>证件号码:</td>
		<td><input type="text" title="身份证号" title="非必填后期填也可以" class="number"  style="color: #000000;font-size:15px;" value="410111111111111111" onfocus="test1(this)" onblur="test2(this)"></td>
	</tr>
	<tr>
		<td>银行卡号:</td>
		<td><input type="text" class="bank" title="非必填后期填也可以"  style="color: #000000;font-size:15px;" value="6226888888888888" onfocus="test1(this)" onblur="test2(this)"></td>
	</tr>
	
	<tr>
		<td><input type="hidden" class="ip" title="针对配置了防钓鱼的商户需要提交；商户服务器通过获取访问ip得到该参数"></td>
	</tr>
	<tr>
		<td><input type="hidden" class="notifyUrl" value="http://pay.changjiezhifu.com:8103/app_posp/clientH5Controller/bgPayResult.action"></td>
	</tr>
<tr>
		<td><input type="hidden" class="tranTp" value="<%=request.getParameter("tranTp")%>"></td>
	</tr>
		<tr>
		<td><input type="hidden" class="product" value="<%=request.getParameter("product")%>"></td>
	</tr>
		<tr>
		<td><input type="hidden" class="realName" value="<%=new String(request.getParameter("realName").getBytes("ISO-8859-1"),"UTF-8")%>"></td>
	</tr>
	<tr>
		<td><input type="hidden" class="cardNo" value="<%=request.getParameter("cardNo")%>"></td>
	</tr>
</table>
<input type="button" value="提交订单" onclick="dianji()">
</center>
 <script type="text/javascript">
    
        function dianji() {
        	var merchantId1 =$(".merchantId1").val();
        	var OrderDesc =$(".OrderDesc").val();
        	var Amount =$(".Amount").val();
        	var ExtData =$(".ExtData").val();
        	var MiscData =$(".phone").val()+"|0||"+$(".name").val()+"|"+$(".number").val()+"|"+$(".bank").val()+"|";
        	var ip =$(".ip").val();
        	var notifyUrl =$(".notifyUrl").val();
        	var merchOrderId =$(".merchOrderId").val();
                var tranTp =$(".tranTp").val();
        	var product =$(".product").val();
        	var realName =$(".realName").val();
        	var cardNo =$(".cardNo").val();
        	var name=$(".name").val();
        	
           $.ajax({
        	   type:"post",
        	   data:{"merchantId1":merchantId1,"orderDesc":OrderDesc,"amount":Amount,"extData":ExtData,"miscData":MiscData,"clientIp":ip,"notifyUrl":notifyUrl,"merchOrderId":merchOrderId,"tranTp":tranTp,"product":product,"realName":realName,"cardNo":cardNo,"name":name},
				url:"${pageContext.request.contextPath}/clientH5Controller/merchantOrderTestH5.action",
				success:function(datas){
					 window.location.href=datas;
					console.info(datas);
				}
           })
        }
    </script>
</body>
</html>