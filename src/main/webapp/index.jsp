<%--
  Created by IntelliJ IDEA.
  User: 15M-4528S
  Date: 2018/3/17
  Time: 9:48
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<body>
<h2>Hello World!</h2>

ememmmeeee
<form name="upload" action="/manage/product/upload.do" method="post" enctype="multipart/form-data">
    <input name="multipartFile" type="file" />
    <input value="上传文件" type="submit"/>
</form>
<br>
富文本图片上传文件
<form name="form2" action="/manage/product/richtext_img_upload.do" method="post" enctype="multipart/form-data">
    <input type="file" name="upload_file" />
    <input type="submit" value="富文本图片上传文件" />
</form>
</body>