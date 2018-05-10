<%@ page language="java" contentType="text/html; charset=UTF-8"
   import="xdt.quickpay.hengfeng.util.*"  pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0, minimum-scale=0.5, maximum-scale=2.0, user-scalable=yes" />
<title>Insert title here</title>
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
		<td><input type="text" class="merchantId1" title="必填" readonly="readonly"  value="<%=request.getParameter("merchantCode")%>" style="color:#CCC;border:none;"></td>
	</tr>
	<tr>
		<td>商品描述:</td>
		<td><input type="text" class="OrderDesc" title="必填" readonly="readonly" style="color: #CCC;" value="网购消费" onfocus="test1(this)" onblur="test2(this)"></td>
	</tr>
	<tr>
		<td>金额:</td>
		<td><input type="text" class="Amount" title="必填"  style="color: #CCC;" value="1000" onfocus="test1(this)" onblur="test2(this)"></td>
	</tr>
	<tr>
		<td>订单号:</td>
		<td><input type="text" class="merchOrderId" title="必填"  readonly="readonly" value="<%=HFUtil.HFrandomOrder()%>" style="color:#CCC;border:none;"></td>
	</tr>
	<tr>
		<td><input type="hidden" class="ExtData" title="非必填"  value="测试"></td>
	</tr>
	<tr>
		<td>姓名:</td>
		<td><input type="text" class="name" title="非必填后期填也可以"   value="尚延超" style="color:#CCC;font-size:15px;" onfocus="test1(this)" onblur="test2(this)"></td>
	</tr>
	<tr>
		<td>手机号:</td>
		<td><input type="text" class="phone" title="非必填后期填也可以"  style="color: #CCC;" value="18902195076" onfocus="test1(this)" onblur="test2(this)"></td>
	</tr>
	<tr>
		<td>证件号码:</td>
		<td><input type="text" title="身份证号" title="非必填后期填也可以" class="number"  style="color: #CCC;" value="410324199203231912" onfocus="test1(this)" onblur="test2(this)"></td>
	</tr>
	<tr>
		<td>银行卡号:</td>
		<td><input type="text" class="bank" title="非必填后期填也可以"  style="color: ;" value="6212260302026649095" onfocus="test1(this)" onblur="test2(this)"></td>
	</tr>
	
	<tr>
		<td><input type="hidden" class="ip" title="针对配置了防钓鱼的商户需要提交；商户服务器通过获取访问ip得到该参数"></td>
	</tr>
	<tr>
		<td><input type="hidden" class="notifyUrl" value="http://60.28.24.164:8103/app_posp/clientH5Controller/bgPayResult.action"></td>
	</tr>
	<tr>
		<td><input type="hidden" class="tranTp" value="<%=request.getParameter("tranTp")%>"></td>
	</tr>
		<tr>
		<td><input type="hidden" class="product" value="<%=request.getParameter("product")%>"></td>
	</tr>
		<tr>
		<td><input type="hidden" class="realName" value="<%=request.getParameter("realName")%>"></td>
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
        	
           $.ajax({
        	   type:"post",
        	   data:{"merchantId1":merchantId1,"orderDesc":OrderDesc,"amount":Amount,"extData":ExtData,"miscData":MiscData,"clientIp":ip,"notifyUrl":notifyUrl,"merchOrderId":merchOrderId,"tranTp":tranTp},
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