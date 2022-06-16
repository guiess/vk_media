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
    <title>VK Albums</title>
</head>
<body>
<table>
    <thead>
    <tr>
        <th>name</th>
        <th>cover url</th>
    </tr>
    </thead>
    <tbody>
    <c:forEach items="${albums}" var="album">
        <tr>
            <td>${album.albumFull.title}</td>
            <td><a href="/albums/${album.albumFull.id}"><img  title="title" alt="alt" src="${album.coverURI}" height="100"></a></td>
        </tr>
    </c:forEach>
    </tbody>
</table> <br>
Error: ${Error}<br>
</body>
</html>
