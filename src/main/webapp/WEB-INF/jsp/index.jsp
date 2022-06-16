<%--
  Created by IntelliJ IDEA.
  User: guiess
  Date: 14.06.2022
  Time: 17:21
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html>
  <head>
    <title>VK Media</title>
  </head>
  <body>
  isAuthorized: ${isAuthorized} <br>
  authUrl: <a href="${authUrl}">Authorize in VK</a><br>
  <c:if test="${isAuthorized}">
    <a href="/albums">Photos</a> <br>
  </c:if>
  Error: ${Error}<br>
  </body>
</html>
