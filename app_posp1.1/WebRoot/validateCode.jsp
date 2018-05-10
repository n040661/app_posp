 <%--
  Created by IntelliJ IDEA.
  User: Jeff
  Date: 15-5-5
  Time: 下午7:33
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>验证码列表</title>
</head>
<body>
    <div style="text-align: center;">
   <span style="font-size: 40px;padding: 0;margin: 0;">验证码列表</span>
    </div>
    <table border="1" align="center" style="width:1000px;">
       <thead>
       <tr>
           <td>手机号码</td>
           <td>发送时间</td>
           <td>是否有效</td>
           <td>来源模块</td>
           <td>验证码</td>
       </tr>
       </thead>
       <tbody>
       <c:if test="${not empty msgList}">
       <c:forEach var="msg" items="${msgList}">
         <tr style="background: <c:if test="${msg.failure eq 1}">green;</c:if> <c:if test="${msg.failure eq 0}">#dcdcdc;</c:if>">
             <td>${msg.phoneNumber}</td>
             <td>${msg.reqtime}</td>
             <td> <c:if test="${msg.failure eq 1}">有效</c:if> <c:if test="${msg.failure eq 0}">无效</c:if></td>
             <td><c:choose>
                     <c:when test="${msg.msgType eq 0}">注册</c:when>
                     <c:when test="${msg.msgType eq 1}">找回没密码</c:when>
                     <c:when test="${msg.msgType eq 2}">修改密码</c:when>
                     <c:when test="${msg.msgType eq 3}">添加收银员</c:when>
                     <c:when test="${msg.msgType eq 4}">修改收银员</c:when>
                     <c:when test="${msg.msgType eq 5}">其他</c:when>
                     <c:when test="">未知来源</c:when>
             </c:choose>
             </td>
             <td>${msg.context}</td>
         </tr>
       </c:forEach>
       </c:if>
       </tbody>
   </table>
</body>
</html>