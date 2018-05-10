<%@ page language="java" contentType="text/html; charset=UTF-8" import="xdt.util.UtilDate" 
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/ajaxfileupload.js"></script>
</head>
<body>
<center>
<%-- <form action="${pageContext.request.contextPath}/totalPayController/yfPay.action" id="from"> --%>
版本号:<input type="text" name="v_version" class="v_version" value="1.0.0.0"><br />
商户号:<input type="text" name="v_mid" class="v_mid" value="10032061473"><br />
批次号： <input type="text" class="v_batch_no" name="v_batch_no" value="<%=System.currentTimeMillis()%>"/><br />
代付时间： <input type="text" class="v_time" name="v_time" value="<%=UtilDate.getOrderNum()%>"/><br />
业务类型:<input type="text" name="v_type" class="v_type" value="1"><br />
异步地址： <input type="text" class="v_notify_url" name="v_notify_url" value="http://60.28.24.164:8102/app_posp/test/qrcode/interface.action"/><br />
文件： <input type="file" class="v_fileName" name="v_fileName" id="fileName"/><br />
<input type="button" value="提交" onclick="tijiao()"><br />
<!-- </form> -->
<div id ="div"></div>
<br />
</center>
<script type="text/javascript">
	function tijiao(){
		var v_version = $(".v_version").val();
		var v_mid = $(".v_mid").val();
		var v_batch_no = $(".v_batch_no").val();
		var v_time = $(".v_time").val();
		var v_type =$(".v_type").val();
		var v_notify_url=$(".v_notify_url").val();
			$.ajax({url : "${pageContext.request.contextPath}/totalPayController/paySign.action",
						type : 'post',
						data : $('input[name]').serialize(),
						success : function(data) {
							console.info(data);
							/* $("#from").append('<input type="text" name="v_sign" class="v_sign" style="display: none" value="'+data+'"><br/>');
							console.info($(".sign").val());
							$("#from").submit(); */
							 $.ajaxFileUpload({//ajaxfileupload.js是用来上传文件的，自定义form已经被写定  method=“post”，所以没有“method”或者“type”
						            url:"${pageContext.request.contextPath}/totalPayController/merchant/virement/mer_payment.action",//需要链接到服务器地址  
						            secureuri:false,
						            isMore:false, //是否为多文件上传
						            fileElementId:$("input[id^=file]")[0].id,//文件选择框的id属性，多文件为id[]/单文件为'id' 
						            dataType:"json",//接收JSON类型
						           /*  contentType:"serial",//发送类型“serial”,为空默认为JSON */
						            data:{"v_version":v_version,"v_mid":v_mid,"v_batch_no":v_batch_no,"v_time":v_time,"v_type":v_type,"v_notify_url":v_notify_url,"v_sign":data},
						            success: function(data,status){ 
						            //上传成功之后的操作
						            console.info('laiel ');
						            console.info(data);
						            },error: function (data,status){ 
						            //上传失败之后的操作
						            console.info(data);
						            }  
						        }); 
						}
					});
	}
	
</script>
</body>

</html>