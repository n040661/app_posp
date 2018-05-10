<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>

<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-3.1.1.min.js"></script>
<script type="text/javascript">
	$(document).ready(function(){
		var isCapped = $(".isCapped option:selected").val();
		console.info(isCapped)
		if(isCapped=="Y"){
			$(".upperFee").attr("readOnly",false); 
		}else{
			$(".upperFee").attr("readOnly",false); 
		}
	});
	function isCapped(){
		var isCapped = $(".isCapped option:selected").val();
		if(isCapped=="N"){
			$(".upperFee").val("0");
			$(".upperFee").attr("readOnly",false);
		}else{
			$(".upperFee").attr("readOnly",false); 
		}
	}
	function dianji(){
		
		//alert(accountType);
		var payChannel =$(".payChannel").val();
		var settleMode =$(".settleMode").val();
		var outMchId =$(".outMchId").val();
		var customerType =$(".customerType").val();
		var businessType =$(".businessType").val();
		var businessName =$(".businessName").val();
		var legalId =$(".legalId").val();
		var legalName =$(".legalName").val();
		var contact =$(".contact").val();
		var contactPhone =$(".contactPhone").val();
		var contactEmail =$(".contactEmail").val();
		var servicePhone =$(".servicePhone").val();
		var customerName =$(".customerName").val();
		var address =$(".address").val();
		var provinceName =$(".provinceName").val();
		var cityName =$(".cityName").val();
		var districtName =$(".districtName").val();
		var licenseNo =$(".licenseNo").val();
		var rate =$(".rate").val();
		var t0Status =$(".t0Status").val();
		var isCapped = $(".isCapped option:selected").val();
		var settleRate =$(".settleRate").val();
		var fixedFee =$(".fixedFee").val();
		var upperFee =$(".upperFee").val();
		var accountName =$(".accountName").val();
		var accountType =$(".accountType").val(); 
		var bankCard =$(".bankCard").val();
		var bankName =$(".bankName").val();
		var province =$(".province").val();
		var city =$(".city").val();
		var bankAddress =$(".bankAddress").val();
		var alliedBankNo =$(".alliedBankNo").val();
		var merchantNumber =$(".merchantNumber").val();
		 $.ajax({
      	   type:"post",
      	   dataType:"json",
      	   data:{"payChannel":payChannel,"settleMode":settleMode,"merchantNumber":merchantNumber,"servicePhone":servicePhone,"outMchId":outMchId,"customerType":customerType,"businessType":businessType,"businessName":businessName,"legalId":legalId,"legalName":legalName,"contact":contact,"contactPhone":contactPhone,"contactEmail":contactEmail,
			"customerName":customerName,"address":address,"provinceName":provinceName,"cityName":cityName,"districtName":districtName,"licenseNo":licenseNo,"rate":rate,"t0Status":t0Status,"isCapped":isCapped,
			"settleRate":settleRate,"fixedFee":fixedFee,"upperFee":upperFee,"accountName":accountName,"accountType":accountType,"bankCard":bankCard,"bankName":bankName,"province":province,"city":city,"bankAddress":bankAddress,"alliedBankNo":alliedBankNo},
			url:"${pageContext.request.contextPath}/registerController/update.action",
			success:function(datas){
					console.info(datas);
					//window.location.href="http://brcb.pufubao.net/customer/update?"+datas;
				}
         })
	}
</script>
</head>
<body>
<center>
<br>
<table style="border-collapse:collapse;">
	<tr align="center">
    	<td>参数：</td>
        <td>说明</td>
        <td>必输项</td>
        
        
        <td>参数：</td>
        <td>说明</td>
        <td>必输项</td>
    </tr>
	
    <tr>
    	<td>下游商户号：</td>
        <td><input type="text" value="" class="outMchId"/></td>
        <td>true</td>
        
        <td>结算模式：</td>
   		<td>
   		<input type="text" value="" class="settleMode"/>
   		</td>
    </tr>

    <tr>
    	<td>商户类型：</td>
        <td>
        <input type="text" value="" class="customerType"/>
        </td>
        <td>false</td>
        
        <td>经营行业：</td>
         <td><input type="text" value="" class="businessType"/></td>
        <td>false</td>
    </tr>
    
    <tr>
    	<td>经营名称：</td>
         <td><input type="text" value="大饼鸡蛋" class="businessName"/></td>
        <td>false</td>
        
        <td>法人身份证：</td>
        <td><input type="text" value="" class="legalId"/></td>
        <td>false</td>
    </tr>
    
    <tr>
    	<td>法人名称：</td>
         <td><input type="text" value="" class="legalName"/></td>
        <td>false</td>
        
        <td>联系人：</td>
         <td><input type="text" value="" class="contact"/></td>
        <td>false</td>
    </tr>
    
    <tr>
    	<td>联系人电话（手机号）：</td>
         <td><input type="text" value="" class="contactPhone"/></td>
        <td>false</td>
        
        <td>联系人邮箱：</td>
         <td><input type="text" value="" class="contactEmail"/></td>
        <td>false</td>
    </tr>
   
    <tr>
    	<td>客服电话：</td>
         <td><input type="text" value="" class="servicePhone"/></td>
        <td>false</td>
        
        <td>商户名称：</td>
        <td><input type="text" value="" class="customerName"/></td>
        <td>false</td>
    </tr>
    
    <tr>
    	<td>经营地址：</td>
         <td><input type="text" value="" class="address"/></td>
        <td>false</td>
        
        <td>经营省：</td>
         <td><input type="text" value="" class="provinceName"/></td>
        <td>false</td>
    </tr>
    
    <tr>
    	<td>经营市：</td>
         <td><input type="text" value="" class="cityName"/></td>
        <td>false</td>
        
        <td>经营区：</td>
        <td><input type="text" value="" class="districtName"/></td>
        <td>false</td>
    </tr>
   
    <tr>
    	<td>营业执照：</td>
         <td><input type="text" value="" class="licenseNo"/></td>
        <td>false</td>
        
        <td>交易费率（%）：</td>
         <td><input type="text" value="" class="rate"/></td>
        <td>false</td>
    </tr>
   
    <tr>
        <td>是否开通（T+0）：</td>
        <td><input type="text" value="" class="t0Status"/></td>
        <td>false</td>
        <td>T+0费率：</td>
         <td><input type="text" value="" class="settleRate"/></td>
        <td>false</td>
    </tr>
    
    <tr>
        <td>T+0单笔加收费用：</td>
        <td><input type="text" value="" class="fixedFee"/></td>
        <td>false</td>
        <td>是否封顶：</td>
        <td>
        	<select class ="isCapped" onchange="isCapped()">
            	<option>Y</option>
                <option>N</option>
            </select>
        </td>
        <td>false</td>
    </tr>
    <tr>
    	<td>封顶值：</td>
        <td><input type="text" value="" class="upperFee"/></td>
        <td>false</td>
        
        <td>开户名：</td>
         <td><input type="text" value="" class="accountName"/></td>
        <td>false</td>
    </tr>
   
    <tr>
    
    	<td>账户类型：</td>
        <td>
        <input type="text" value="" class="accountType"/>
        </td>
        <td>false</td>
    	<td>银行卡号：</td>
         <td><input type="text" value="" class="bankCard"/></td>
        <td>false</td>
        
       
    </tr>
   
    <tr>
    	 <td>开户行名称：</td>
         <td><input type="text" value="" class="bankName"/></td>
        <td>false</td>
    	<td>开户行省份：</td>
         <td><input type="text" value="" class="province"/></td>
        <td>false</td>
    </tr>
    <tr>
    	 <td>开户行城市：</td>
         <td><input type="text" value="" class="city"/></td>
        <td>false</td>
    	<td>开户行地址：</td>
        <td><input type="text" value="" class="bankAddress"/></td>
        <td>false</td>
    </tr>
    <tr>
    	 <td>银联号：</td>
         <td><input type="text" value="" class="alliedBankNo"/></td>
        <td>false</td>
        <td>结算通道：</td>
         <td><input type="text" value="WECHAT_OFFLINE" class="payChannel"/></td>
        <td>false</td>
        
    </tr>
</table>
<input type="button" value="点击" onclick="dianji()">
</center>
</body>
</html>