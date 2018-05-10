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
订单号:<input type="text" name="BATCH_NO" class="BATCH_NO">
商户号:<input type="text" name="MERCHANT_ID" class="MERCHANT_ID" value="100120242118015">
<input type="button" value="添加" onclick="tianjia()">
<input type="button" value="提交" onclick="tijiao()"><br />
<div style="float:left" class="buju">
<br />


</div>

</body>
<script type="text/javascript">
	function tijiao(){
		//window.location.href="${pageContext.request.contextPath}/clientCollectionPayController/pay.action";
		var l =$(".mokuai").length;
		//console.info(l);
		var BATCH_NO=$(".BATCH_NO").val();
		var MERCHANT_ID=$(".MERCHANT_ID").val();
		var map="";
		var SN =document.getElementsByName("SN");
		 var AMOUNT =document.getElementsByName("AMOUNT");
		 var ACC_NO =document.getElementsByName("ACC_NO");
		var ACC_NAME =document.getElementsByName("ACC_NAME");
		var ACC_PROVINCE =document.getElementsByName("ACC_PROVINCE");
		var ACC_CITY =document.getElementsByName("ACC_CITY");
		var BANK_NAME =document.getElementsByName("BANK_NAME");
		var BANK_NO =document.getElementsByName("BANK_NO");
		var ACC_PROP =document.getElementsByName("ACC_PROP");
		var ACC_TYPE =document.getElementsByName("ACC_TYPE"); 
		for (var i = 0, j = l; i < j; i++){
			map +=SN[i].value+"?"+AMOUNT[i].value+"?"+ACC_NO[i].value+"?"+ACC_NAME[i].value+"?"+ACC_PROVINCE[i].value+"?"+ACC_CITY[i].value+"?"+BANK_NAME[i].value+"?"+BANK_NO[i].value+"?"+ACC_PROP[i].value+"?"+ACC_TYPE[i].value+"1,";
			
			}
		
		console.info(map.toString());
		
		map=map.substr(0,map.length-1);
		$.ajax({
			url:"${pageContext.request.contextPath}/clientCollectionPayController/paySign.action",
			dataType:"json",
			 type: 'post',
			data:{"MAP":map.toString(),"BATCH_NO":BATCH_NO,"MERCHANT_ID":MERCHANT_ID},
			success:function(data){
				window.location.href="daifu.jsp";
			}
		});
	}
	function tianjia(){
		var strVar = "";
	    strVar += "<div style=\"width:200px; float:left;\" class=\"mokuai\">\n";
	    strVar += "	<font color=\"#FF0000\">必填--------------------<\/font>\n";
	    strVar += "	\n";
	    strVar += "    流水号：<input type=\"text\" class=\"SN\" name=\"SN\"/><br />\n";
	    strVar += "    金额： <input type=\"text\" class=\"AMOUNT\" name=\"AMOUNT\"/><br />\n";
	    strVar += "    银行卡号： <input type=\"text\" class=\"ACC_NO\" name=\"ACC_NO\"/><br />\n";
	    strVar += "    开户名： <input type=\"text\" class=\"ACC_NAME\" name=\"ACC_NAME\"/><br />\n";
	    strVar += "   	\n";
	    strVar += "    <font color=\"#FF0000\">选填--------------------<\/font>\n";
	    strVar += "   \n";
	    strVar += "    开户省份： <input type=\"text\" class=\"ACC_PROVINCE\" name=\"ACC_PROVINCE\"/><br />\n";
	    strVar += "    开户城市： <input type=\"text\" class=\"ACC_CITY\" name=\"ACC_CITY\"/><br />\n";
	    strVar += "    银行名称： <input type=\"text\" class=\"BANK_NAME\" name=\"BANK_NAME\"/><br />\n";
	    strVar += "    联行号： <input type=\"text\" class=\"BANK_NO\" name=\"BANK_NO\"/><br />\n";
	    strVar += "    账号类别： <input type=\"text\" class=\"ACC_PROP\" name=\"ACC_PROP\"/><br />\n";
	    strVar += "    账号类型： <input type=\"text\" class=\"ACC_TYPE\" name=\"ACC_TYPE\"/><br />\n";
	    strVar += "    <br />\n";
	    strVar += "  <input type=\"button\" value=\"删除\"  onclick=\"shanchu(this)\">\n";
	    strVar += "   \n";
	    strVar += "    \n";
	    strVar += "<\/div>\n";
		$(".buju").append(strVar);
	}
	
	function shanchu(o){
		o.parentNode.parentNode.removeChild(o.parentNode);
	}
</script>
</html>