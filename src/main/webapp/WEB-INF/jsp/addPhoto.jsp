<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<html>
<head>
    <title>VK Albums</title>
</head>
<body >

<h3>Find album by id</h3>
<form:form action="${pageContext.request.contextPath}/photos/addPhotoWithTag">
    <table>
        <tr>
            <td><label>Url</label></td>
            <td><input type="text" name="imageUrl" /></td>
        </tr>
        <tr>
            <td><label>Tag</label></td>
            <td><input type="text" name="tag" /></td>
        </tr>
        <tr>
            <td><input type="submit" value="Add"/></td>
        </tr>
    </table>
</form:form>

<c:if test="${result != null}">
    Result: ${result}<br>
</c:if>

<c:if test="${error != null}">
    Error: ${error}<br>
</c:if>

</body>
</html>
