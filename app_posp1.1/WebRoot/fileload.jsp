<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>Insert title here</title>
<script>  

function openUpload_(){  
    openUpload(null,'JPG,GIF,JPEG,PNG','5',callback);  
}  
  
/**  
 * 回调函数,获取上传文件信息  
 * realName真实文件名  
 * saveName文件保存名  
 * maxSize文件实际大小  
 */  
function callback(realName,saveName,maxSize){  
    $("#photo_").val(saveName);  
    //回调后其它操作  
} 
function openUpload(functionId,fileType,maxSize,callback){  
    var url = root+"/CommonController.jhtml?method=goFileUpload&";  
    if(functionId!=null){  
        url = url + "functionId="+functionId+"&";  
    }  
    if(fileType!=null){  
        url = url + "fileType="+fileType+"&";  
    }  
    if(maxSize!=null){  
        url = url + "maxSize="+maxSize;  
    }  
    var win = window.showModalDialog(url,"","dialogWidth:300px;dialogHeight:150px;scroll:no;status:no");  
    if(win != null){  
        var arrWin = win.split(",");  
        callback(arrWin[0],arrWin[1],arrWin[2]);  
    }  
}
</script>  
</head>
<body>
	<table>
		<tr>
			<td>头像：</td>
			<td><input type="hidden" name="photo" id="photo_"></input> <input
				type="button" onclick="openUpload_()" value="上传" /></td>
		</tr>
	</table>
</body>
</html>