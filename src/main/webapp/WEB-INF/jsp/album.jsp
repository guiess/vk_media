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
<body >

${album.title}<br>
<img  title="title" alt="alt" src="${album.coverURI}" height="100">
<br>

<table>
    <tbody>
    <tr>
        <c:forEach items="${photos}" var="photo" varStatus="i">
        <td>
            <a href='#' onclick='javascript:window.open("${photos[i.index].photoURI}", "_blank", "scrollbars=1,resizable=1,height=1280,width=1024");' title='Pop Up'>
                <img src="${photos[i.index].previewPhotoURI != null? photos[i.index].previewPhotoURI: photos[i.index].photoURI}" height="100" alt="">
            </a> <br>
                ${photos[i.index].tags}
        </td>
        <c:if test="${i.count % 5 == 0}">
    </tr><tr>
        </c:if>
        </c:forEach>
    </tr>
    </tbody>
</table> <br>

<c:if test="${Error != null}">
    Error: ${Error}<br>
</c:if>
</body>
</html>
