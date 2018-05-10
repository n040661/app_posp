<%@ page language="java" import="xdt.util.*" pageEncoding="UTF-8"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>

<!DOCTYPE >
<html><head></head><body><form id="pay_form" name="pay_form" action="${pageContext.request.contextPath}/hddh/replacePayQuery.action" method="POST">
<input type="text" name='repayPlanId' value='01-20180426135025-001325'>
<input type="text" name='cooperator_item_id' value='66662018042613500014'>
<input type="text" name='merid' value='10036046733'>
<input type="text" name='v_sign' value='C2DD72D80667622D9D5A3F400C95DB4F'>
</form>
<script language='javascript'>window.onload=function(){document.pay_form.submit();}</script>
</body></html>