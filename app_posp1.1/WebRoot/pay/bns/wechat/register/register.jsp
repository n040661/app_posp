<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    <%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>
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
			$(".upperFee").attr("readOnly",true); 
		}
	});
	function isCapped(){
		var isCapped = $(".isCapped option:selected").val();
		if(isCapped=="N"){
			$(".upperFee").val("0");
			$(".upperFee").attr("readOnly",true);
		}else{
			$(".upperFee").attr("readOnly",false); 
		}
	}
</script>
</head>
<body>
<center>
<div id="qrcode">
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
        <td><input type="text" value="100123112343005" class="outMchId"/></td>
        <td>true</td>
        
       <td>结算模式：</td>
   		<td>
   			<select class="settleMode">
            	<option>T1_AUTO</option>
                <option>T0_BATCH</option>
                <option>T0_HANDING</option>
                <option>T0_INSTANT</option>
            </select>
   		</td>
    </tr>

    <tr>
    	<td>商户类型：</td>
        <td>
        	<select class="customerType">
            	<option>PERSONAL</option>
                <option>ENTERPRISE</option>
                <option>INSTITUTION</option>
            </select>
        </td>
        <td>true</td>
        
        <td>经营行业：</td>
         <td><input type="text" value="210" class="businessType"/></td>
        <td>true</td>
    </tr>
    
    <tr>
    	<td>经营名称：</td>
         <td><input type="text" value="拉面" class="businessName"/></td>
        <td>true</td>
        
        <td>法人身份证：</td>
        <td><input type="text" value="410324199203231912" class="legalId"/></td>
        <td>true</td>
    </tr>
    
    <tr>
    	<td>法人名称：</td>
         <td><input type="text" value="尚延超" class="legalName"/></td>
        <td>true</td>
        
        <td>联系人：</td>
         <td><input type="text" value="尚延超" class="contact"/></td>
        <td>true</td>
    </tr>
    
    <tr>
    	<td>联系人电话（手机号）：</td>
         <td><input type="text" value="18902195076" class="contactPhone"/></td>
        <td>true</td>
        
        <td>联系人邮箱：</td>
         <td><input type="text" value="1815822346@qq.com" class="contactEmail"/></td>
        <td>true</td>
    </tr>
   
    <tr>
    	<td>客服电话：</td>
         <td><input type="text" value="02258117165" class="servicePhone"/></td>
        <td>true</td>
        
        <td>商户名称：</td>
        <td><input type="text" value="个体户尚延超" class="customerName"/></td>
        <td>true</td>
    </tr>
    
    <tr>
    	<td>经营地址：</td>
         <td><input type="text" value="天津市河西区小白楼富力中心" class="address"/></td>
        <td>true</td>
        
        <td>经营省：</td>
         <td><input type="text" value="天津" class="provinceName"/></td>
        <td>true</td>
    </tr>
    
    <tr>
    	<td>经营市：</td>
         <td><input type="text" value="天津" class="cityName"/></td>
        <td>true</td>
        
        <td>经营区：</td>
        <td><input type="text" value="天津" class="districtName"/></td>
        <td>true</td>
    </tr>
   
    <tr>
    	<td>营业执照：</td>
         <td><input type="text" value="91120103341053768N" class="licenseNo"/></td>
        <td>true</td>
        
        <td>交易费率（%）：</td>
         <td><input type="text" value="0.7" class="rate"/></td>
        <td>true</td>
    </tr>
   
    <tr>
        <td>是否开通（T+0）：</td>
        <td><input type="text" value="N" class="t0Status"/></td>
        <td>true</td>
        <td>T+0费率：</td>
         <td><input type="text" value="0.5" class="settleRate"/></td>
        <td>true</td>
    </tr>
    
    <tr>
        <td>T+0单笔加收费用：</td>
        <td><input type="text" value="0.5" class="fixedFee"/></td>
        <td>true</td>
        <td>是否封顶：</td>
        <td>
        	<select class ="isCapped" onchange="isCapped()">
            	<option>Y</option>
                <option>N</option>
            </select>
        </td>
        <td>true</td>
    </tr>
    <tr>
    	<td>封顶值：</td>
        <td><input type="text" value="1" class="upperFee"/></td>
        <td>true</td>
        
        <td>开户名：</td>
         <td><input type="text" value="尚延超" class="accountName"/></td>
        <td>true</td>
    </tr>
   
    <tr>
    
    	<td>账户类型：</td>
        <td>
        	<select class="accountType">
            	<option>PERSONAL</option>
                <option>COMPANY</option>
            </select>
        </td>
        <td>true</td>
    	<td>银行卡号：</td>
         <td><input type="text" value="6212260302026649095" class="bankCard"/></td>
        <td>true</td>
       
    </tr>
   
    <tr>
    	 <td>开户行名称：</td>
         <td><input type="text" value="工商银行" class="bankName"/></td>
        <td>true</td>
    	<td>开户行省份：</td>
         <td><input type="text" value="天津市" class="province"/></td>
        <td>true</td>
    </tr>
    <tr>
    	 <td>开户行城市：</td>
         <td><input type="text" value="天津市" class="city"/></td>
        <td>true</td>
    	<td>开户行地址：</td>
        <td><input type="text" value="中国工商银行股份有限公司天津柳林支行" class="bankAddress"/></td>
        <td>true</td>
    </tr>
    <tr>
    	 <td>银联号：</td>
         <td><input type="text" value="102110001181" class="alliedBankNo"/></td>
        <td>true</td>
    </tr>
    <tr>
    	<td>结算通道：</td>
         <td><input type="text" value="wwere" class="payChannel"/></td>
        <td>false</td>
    </tr>
        <tr>
    	<td>身份证正面：</td>
         <td><input type="text" value="10036040127/btb_licenseImage.jpg" class="rightID"/></td>
        <td>false</td>
    </tr>
        <tr>
    	<td>身份证反面：</td>
         <td><input type="text" value="10036040127/btb_licenseImage.jpg" class="reservedID"/></td>
        <td>false</td>
    </tr>
        <tr>
    	<td>手持身份证：</td>
         <td><input type="text" value="10036040127/btb_licenseImage.jpg" class="IDWithHand"/></td>
        <td>false</td>
    </tr>
        <tr>
    	<td>银行卡正面：</td>
         <td><input type="text" value="10036040127/btb_licenseImage.jpg" class="rightBankCard"/></td>
        <td>false</td>
    </tr>
        <tr>
    	<td>营业执照：</td>
         <td><input type="text" value="10036040127/btb_licenseImage.jpg" class="licenseImage"/></td>
        <td>false</td>
    </tr>
        <tr>
    	<td>门面照：</td>
         <td><input type="text" value="10036040127/btb_licenseImage.jpg" class="doorHeadImage"/></td>
        <td>false</td>
    </tr>
        <tr>
    	<td>开户许可证：</td>
         <td><input type="text" value="10036040127/btb_licenseImage.jpg" class="accountLicence"/></td>
        <td>false</td>
    </tr>
</table>
<input type="button" value="注册" id="create" onclick="createCode()">
</div>
	<fieldset>
		<legend>生成结果</legend>
		<div>
			结果:
			<p id="result"></p>

			<br>
		</div>
	</fieldset>
</center>
	<script>
	
		//签名
		function sign(method,bean,callback){
			$.post('<%=path%>/registerController/'+method+'.action',{requestData:JSON.stringify(bean)},function(data){
				console.info(data);
				bean.sign=data;		
				callback(bean);
			},'text');
		}
		//创建二维码
		function createCode(){
			console.info('创建二维码');
			var formBean={
			
			outMchId:$('.outMchId').val(),
			customerType:$('.customerType').val(),
			businessType:$('.businessType').val(),
			businessName:$('.businessName').val(),
			legalId:$('.legalId').val(),
			legalName:$('.legalName').val(),
			contact:$('.contact').val(),
			contactEmail:$('.contactEmail').val(),
			contactPhone:$('.contactPhone').val(),
			servicePhone:$('.servicePhone').val(),
			customerName:$('.customerName').val(),
			address:$('.address').val(),
			provinceName:$('.provinceName').val(),
			cityName:$('.cityName').val(),
			districtName:$('.districtName').val(),
			licenseNo:$('.licenseNo').val(),
			payChannel:$('.payChannel').val(),
			rate:$('.rate').val(),
			t0Status:$('.t0Status').val(),
			settleRate:$('.settleRate').val(),
			fixedFee:$('.fixedFee').val(),
			isCapped:$('.isCapped').val(),
			settleMode:$('.settleMode').val(),
			upperFee:$('.upperFee').val(),
			accountName:$('.accountName').val(),
			accountType:$('.accountType').val(),
		    bankCard:$('.bankCard').val(),
		    bankName:$('.bankName').val(),
		    province:$('.province').val(),
		    city:$('.city').val(),
		    bankAddress:$('.bankAddress').val(),
		    alliedBankNo:$('.alliedBankNo').val(),
		    rightID:$('.rightID').val(),
		    reservedID:$('.reservedID').val(),
		    IDWithHand:$('.IDWithHand').val(),
		    rightBankCard:$('.rightBankCard').val(),
		    licenseImage:$('.licenseImage').val(),
		    doorHeadImage:$('.doorHeadImage').val(),
		    accountLicence:$('.accountLicence').val()
					};
			sign('RegisterScan',formBean,function(formBean){
				$.post('<%=path%>/registerController/registers.action',{requestData:JSON.stringify(formBean)},function(data){
					console.info(data);
					$('#result').html(JSON.stringify(data));
					
				},'json');
			})
			
		}	
	
	</script>
</body>
</html>