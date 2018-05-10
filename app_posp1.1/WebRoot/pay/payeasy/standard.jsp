<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>Insert title here</title>
</head>
<body>
  <form action="${pageContext.request.contextPath }/payeasy/pay.action" method="post">
     <p>机构号:<input type="text" name="merchantId" value="${temp.merchantId}" id="merchantId"></p>
     <p>订单编号:<input type="text" name="v_oid" value="${temp.v_oid}" id="v_oid"></p>
     <p>姓名:<input type="text" name="v_rcvname" value="${temp.v_rcvname}" id="v_rcvname"></p>
     <p>地址:<input type="text" name="v_rcvaddr" value="${temp.v_rcvaddr}" id="v_rcvaddr"></p>
     <p>电话:<input type="text" name="v_rcvtel" value="${temp.v_rcvtel}" id="v_rcvtel"></p>
     <p>邮政编码:<input type="text" name="v_rcvpost" value="${temp.v_rcvpost}" id="v_rcvpost"></p>
     <p>订单总金额:<input type="text" name="v_amount" value="${temp.v_amount}" id="v_amount"></p>
     <p>订单日期:<input type="text" name="v_ymd" value="${temp.v_ymd}" id="v_ymd"></p>
     <p>配货状态:<input type="text" name="v_orderstatus" value="${temp.v_orderstatus}" id="v_orderstatus"></p>
     <p>订货人姓名:<input type="text" name="v_ordername" value="${temp.v_ordername}" id="v_ordername"></p>
     <p>支付币种:<input type="text" name="v_moneytype" value="${temp.v_moneytype}" id="v_moneytype"></p>
     <p>支付类型:<input type="text" name="v_type" value="${temp.v_type}" id="v_type"></p>
     <p>银行类型:<input type="text" name="v_pmode" value="${temp.v_pmode}" id="v_pmode"></p>
     <p>返回商户页面:<input type="text" name="v_url" value="${temp.v_url}" id="v_url"></p>
     <p>后台通知地址:<input type="text" name="v_bgurl" value="${temp.v_bgurl}" id="v_bgurl"></p>
     <p>签名:<input type="text" name="v_md5info" value="${temp.v_md5info}" id="v_md5info"></p>
     <p><input type="submit" value="支付"></p>
  </form>

</body>
</html>