<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>


<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>Insert title here</title>
<!-- 引入JQuery -->
<script type="text/javascript" src="${pageContext.request.contextPath}/jquery-easyui-1.5.2/jquery-1.8.3.min.js"></script>
<!-- 引入EasyUI -->
<script type="text/javascript" src="${pageContext.request.contextPath}/jquery-easyui-1.5.2/jquery.easyui.min.js"></script>
<!-- 引入EasyUI的中文国际化js，让EasyUI支持中文 -->
<script type="text/javascript" src="${pageContext.request.contextPath}/jquery-easyui-1.5.2/locale/easyui-lang-zh_CN.js"></script>
<!-- 引入EasyUI的样式文件-->
<link rel="stylesheet" href="${pageContext.request.contextPath}/jquery-easyui-1.5.2/themes/default/easyui.css" type="text/css"/>
<!-- 引入EasyUI的图标样式文件-->
<link rel="stylesheet" href="${pageContext.request.contextPath}/jquery-easyui-1.5.2/themes/icon.css" type="text/css"/>


</head>
   <!--给body指定class属性指定easy的easyui-layout样式，这样就可以 使用body创建easyui的layout -->
   <body class="easyui-layout">
     <!-- 上北--> 
     <div data-options="region:'north',title:'',split:true" style="height:130px;background: green;color: #fff">
     	<h1 style="padding-left: 20px;line-height: 70px;font-size: 36px;">委托代扣（无汇款认证）接口</h1>
     </div>
     <!-- 下南   
     <div data-options="region:'south',title:'South Title',split:true" style="height:100px;">南</div>  -->
      <!-- 左西-->  
     <div data-options="region:'west',title:'功能导航',split:true" style="width:300px;">
	     <div class="easyui-accordion"
			data-options="fit : false,border : false" >
			<div title="代付">
				<ul style="list-style:none;">
					<li>
						<a style="font-size: 14px;text-decoration:none;margin-left: -30px;" href="###" onclick="tabs('子协议录入接口','${pageContext.request.contextPath}/pay/ysb/daikou/signSimpleSubContract.jsp')">1.子协议录入接口</a>  
					</li>
					<li>
						<a style="font-size: 14px;text-decoration:none;margin-left: -30px;" href="###" onclick="tabs('委托代扣接口','${pageContext.request.contextPath}/pay/ysb/daikou/collect.jsp')">2.委托代扣接口(模式一)</a>  
					</li>
					<li>
						<a style="font-size: 14px;text-decoration:none;margin-left: -30px;" href="###" onclick="tabs('委托代扣接口','${pageContext.request.contextPath}/pay/ysb/daikou/collect1.jsp')">2.委托代扣接口(模式二)</a>  
					</li>
					<li>
						<a style="font-size: 14px;text-decoration:none;margin-left: -30px;" href="###" onclick="tabs('订单状态查询接口','${pageContext.request.contextPath}/pay/ysb/daikou/queryOrderStatus.jsp')">3.订单状态查询接口</a>  
					</li>
					<li>
						<a style="font-size: 14px;text-decoration:none;margin-left: -30px;" href="###" onclick="tabs('子协议号查询接口','${pageContext.request.contextPath}/pay/ysb/daikou/querySubContractId.jsp')">4.子协议号查询接口</a>  
					</li>
					<li>
						<a style="font-size: 14px;text-decoration:none;margin-left: -30px;" href="###" onclick="tabs('子协议延期接口','${pageContext.request.contextPath}/pay/ysb/daikou/subConstractExtension.jsp')">5.子协议延期接口</a>  
					</li>
				</ul>
			</div>   
	     </div>
     </div>
     <!-- 右东   
     <div data-options="region:'east',iconCls:'icon-reload',title:'Menu Tree',split:true" style="width:200px;"></div>--> 
     <!--north，south， east，west这几个面板都可以删掉，唯有这个center面板一定不能删掉，否则使用easyui-layout就会出错 --> 
     <div data-options="region:'center',title:''" style="padding:5px;">
     	<div id="tab" class="easyui-tabs"
			data-options="fit:true,border:false,iconCls:'icon-reload',closable:true">
		</div>
     </div>  
  </body> 
<script type="text/javascript">
	function tabs(title, url) {
		if (parent.$('#tab').tabs('exists', title)) {
			parent.$('#tab').tabs('close', title);
		}
		parent
				.$('#tab')
				.tabs(
						"add",
						{
							title : title,
							content : '<iframe /*scrolling="no"*/ frameborder="0"  src="'
									+ url
									+ '" style="width:100%;height:100%;"></iframe>',
							closable : true
						});
	}
</script>
</html>