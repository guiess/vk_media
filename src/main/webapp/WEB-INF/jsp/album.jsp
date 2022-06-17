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
    <c:forEach items="${photos}" var="photo" step="5" varStatus="i">
        <tr>
            <td>
                <a href='#' onclick='javascript:window.open("${photos[i.index].photoURI}", "_blank", "scrollbars=1,resizable=1,height=1280,width=1024");' title='Pop Up'>
                    <img src="${photos[i.index].previewPhotoURI}" height="100" alt="">
                </a>
            </td>


            <c:if test="${photos[i.index+1] != null}">
                <td>
                    <a href='#' onclick='javascript:window.open("${photos[i.index+1].photoURI}", "_blank", "scrollbars=1,resizable=1,height=1280,width=1024");' title='Pop Up'>
                        <img src="${photos[i.index+1].previewPhotoURI}" height="100" alt="">
                    </a>
                </td>
            </c:if>
            <c:if test="${photos[i.index+2] != null}">
                <td>
                    <a href='#' onclick='javascript:window.open("${photos[i.index+2].photoURI}", "_blank", "scrollbars=1,resizable=1,height=1280,width=1024");' title='Pop Up'>
                        <img src="${photos[i.index+2].previewPhotoURI}" height="100" alt="">
                    </a>
                </td>
            </c:if>
            <c:if test="${photos[i.index+3] != null}">
                <td>
                    <a href='#' onclick='javascript:window.open("${photos[i.index+3].photoURI}", "_blank", "scrollbars=1,resizable=1,height=1280,width=1024");' title='Pop Up'>
                        <img src="${photos[i.index+3].previewPhotoURI}" height="100" alt="">
                    </a>
                </td>
            </c:if>
            <c:if test="${photos[i.index+4] != null}">
                <td>
                    <a href='#' onclick='javascript:window.open("${photos[i.index+4].photoURI}", "_blank", "scrollbars=1,resizable=1,height=1280,width=1024");' title='Pop Up'>
                        <img src="${photos[i.index+4].previewPhotoURI}" height="100" alt="">
                    </a>
                </td>
            </c:if>
        </tr>
    </c:forEach>
    </tbody>
</table> <br>

<c:if test="${Error != null}">
    Error: ${Error}<br>
</c:if>
</body>
</html>
