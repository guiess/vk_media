<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<html>
<head>
    <title>VK Albums</title>
</head>
<body >

<h3>Find album by id</h3>
<form:form action="${pageContext.request.contextPath}/albums/find">
    <table>
        <tr>
            <td><label>Album id</label></td>
            <td><input type="number" name="id" /></td>
        </tr>
        <tr>
            <td><input type="submit" value="Find"/></td>
        </tr>
    </table>
</form:form>

<c:if test="${result != null}">
    Result: ${result}<br>
</c:if>

<c:if test="${album != null}">
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
</c:if>
<c:if test="${Error != null}">
    Error: ${Error}<br>
</c:if>

</body>
</html>
