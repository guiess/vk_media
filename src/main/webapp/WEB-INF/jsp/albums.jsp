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
Albums
<table>
    <tbody>
    <tr style="vertical-align: top">
        <c:forEach items="${albums}" var="album" varStatus="i">
            <td>
                <a href="/albums/${album.id}"><img  title="title" alt="alt" src="${album.coverURI}" height="100"></a> <br>
                ${album.title}
            </td>
            <c:if test="${i.count % 5 == 0}">
                </tr><tr style="vertical-align: top">
            </c:if>
        </c:forEach>
    </tr>
    </tbody>
</table> <br>
Error: ${error}<br>
</body>
</html>
