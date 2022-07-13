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
    <script src=
                    "https://code.jquery.com/jquery-3.6.0.min.js">
    </script>
</head>
<body >

${album.title}<br>
<img  title="title" alt="alt" src="${album.coverURI}" height="100">
<br>

<table>
    <tbody>
        <tr style="vertical-align: top">

            <td>
                <table>
                    <tbody>
                    <tr style="vertical-align: top">
                        <c:forEach items="${photos}" var="photo" varStatus="i">
                        <td>
                            <%-- onclick='javascript:window.open("${photos[i.index].photoURI}", "_blank", "scrollbars=1,resizable=1,height=1280,width=1024");'--%>
                            <img id="${photos[i.index].vkId}" albumId="${photos[i.index].albumId}" onclick="javascript:onImageClick(${photos[i.index].vkId}, '${photos[i.index].photoURI}', '${photos[i.index].tags}')" src="${photos[i.index].previewPhotoURI != null? photos[i.index].previewPhotoURI: photos[i.index].photoURI}" height="100" alt="">
                            <br>
                                <u id="tags-${photos[i.index].vkId}">${photos[i.index].tags}</u>
                        </td>
                        <c:if test="${i.count % 5 == 0}">
                    </tr><tr style="vertical-align: top">
                        </c:if>
                        </c:forEach>
                    </tr>
                    </tbody>
                </table>

            </td>


            <td>
                <input type="checkbox" id="editTagToggle" name="editToggle" onclick="javascript:toggleEdit()">
                <label for="editToggle"> Edit tags</label><br>
                <div hidden id="editTagsDiv">
                    <table>
                        <tr>
                            <td><label>Tag</label></td>
                            <td><input id="tagField" type="text" name="tag" value="${tag}" />
                                <button type="button" onclick="javascript:clearTagField()">Clear</button></td>
                        </tr>
                        <tr>
                            <td><button type="button" onclick="javascript:addPhoto()">Add</button></td>
                        </tr>
                    </table>
                    <c:if test="${tags != null}">
                        Existing tags (click to add): <br>
                        <c:forEach items="${tags}" var="singleTag" varStatus="i">
                            <u onclick="javascript:addTagToField('${singleTag}')">${singleTag}</u>
                            <c:if test="${i.count % 5 == 0}">
                                <br>
                            </c:if>
                        </c:forEach>
                    </c:if>
                    <div hidden id="savingError" style="color:red">
                    </div>
                </div>
            </td>


        </tr>
    </tbody>
</table>

 <br>

<c:if test="${error != null}">
    Error: ${error}<br>
</c:if>

<script type="text/javascript">
    function addTagToField(tag)
    {
        document.getElementById("tagField").value = document.getElementById("tagField").value + ' ' + tag
    }
    function clearTagField()
    {
        document.getElementById("tagField").value = ''
    }
    function toggleEdit()
    {
        element = document.getElementById("editTagsDiv");
        if (element.hidden) {
            element.hidden = false
        } else {
            element.hidden = true
            selectedImage = document.getElementsByName("selected-image")
            if (selectedImage.length > 0) {
                selectedImage[0].style.border = "";
                selectedImage[0].removeAttribute("name")
            }
        }
    }
    function onImageClick(id, url, tags) {
        editDiv = document.getElementById("editTagsDiv");
        if (!editDiv.hidden) {
            selectedImage = document.getElementsByName("selected-image")
            if (selectedImage.length > 0) {
                selectedImage[0].style.border = "";
                selectedImage[0].removeAttribute("name")
            }
            newImage = document.getElementById(id);
            newImage.style.border = "10px solid blue";
            newImage.setAttribute("name", "selected-image")
            document.getElementById("tagField").value = tags
        } else {
            window.open(url, "_blank", "scrollbars=1,resizable=1,height=1280,width=1024");
        }
    }
    function addPhoto() {
        selectedImage = document.getElementsByName("selected-image")
        if (selectedImage.length > 0) {
            photoVkId = selectedImage[0].id
            albumId = selectedImage[0].getAttribute("albumId")
            tag = document.getElementById("tagField").value
            $.ajax({
                url: '/photos/addPhotoWithTagRest',
                method : 'POST',
                contenttype: 'application/json',
                async : false,
                data: {
                    photoVkId: photoVkId,
                    albumId: albumId,
                    tags: tag
                },
                dataType : 'json',
                complete : function(data) {
                    savingErrorField = document.getElementById("savingError")
                    if (data.responseText !== 'Success') {
                        if (savingErrorField.hidden) {
                            savingErrorField.hidden = false
                        }
                        savingErrorField.innerText = 'Error on saving tags: ' + data.responseText
                    } else {
                        if (!savingErrorField.hidden) {
                            savingErrorField.hidden = true
                            savingErrorField.innerText = ''
                        }
                        document.getElementById("tags-" + photoVkId).innerText = tag
                    }
                }
            });
        }
    }
</script>

</body>
</html>
