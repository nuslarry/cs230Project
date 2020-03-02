<%@ page language="java" contentType="text/html; charset=BIG5"
    pageEncoding="BIG5"%>
 <!-- need to declare JSTL before using it. Add this to the top of the JSP:  -->
 <!--https://stackoverflow.com/questions/9794101/foreach-not-working-for-list-->
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html>
<head>
<meta charset="BIG5">
<title>Insert title here</title>
</head>
<body>
	<h1>Welcome ${user}</h1>
	<h2>Uploading file</h2>
	
	<form action="/doUpload" method="post" enctype="multipart/form-data">
	    <label>Enter file</label>
	    <input type="file" name="file">
	    <input type="hidden" name="user" value=  <%= request.getParameter("user")%> >
	    <button type="submit">Upload</button>
	</form>
	<br>
	<br>
	<form action="/download">
		<label>File to Download:</label><br>
		<input type="text" id="downloadFileName" name="downloadFileName"><br>
		<input type="hidden" name="user" value=  <%= request.getParameter("user")%> >
		<input type="submit" value="Submit">
	</form> 
	<br>
	<br>

	    <c:forEach items="${list}" var="info">
	       <li>${info}</li>
		</c:forEach>

</body>
</html>
