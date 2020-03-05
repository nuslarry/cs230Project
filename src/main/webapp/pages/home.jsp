<%@ page trimDirectiveWhitespaces="true" %>
<%@ page language="java" contentType="text/html; charset=BIG5"
    pageEncoding="BIG5"%>
 <!-- need to declare JSTL before using it. Add this to the top of the JSP:  -->
 <!--https://stackoverflow.com/questions/9794101/foreach-not-working-for-list-->
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html>
<head>
<meta charset="BIG5">
<title>Cloud Drive</title>
<style>
.grid-container {
  display: grid;
  grid-template-columns: auto auto auto;
  justify-items: center;
  justify-content: space-evenly;
}
.item1{
grid-column: 1 / span 1;
}
.item2{
grid-column: 2 / span 1;
}
.item3{
grid-column: 3 / span 1;
}
table {
  font-family: arial, sans-serif;
  border-collapse: collapse;
  width: 100%;
}

td, th {
  border: 1px solid #dddddd;
  text-align: left;
  padding: 8px;
}

tr:nth-child(even) {
  background-color: #dddddd;
}
</style>
</head>
<body>
<center><h1>Welcome ${user}</h1></center>
    <div class = "grid-container">
    <div class = "item1">
	<h2>Upload file</h2>
	<form action="/doUpload" method="post" enctype="multipart/form-data">
	    <label>File to Upload:</label><br>
	    <input type="file" name="file">
	    <input type="hidden" name="user" value=  <%= request.getParameter("user")%> >
	    <br>
	    <button type="submit">Upload</button>
	</form>
	<br>
	<h2>Download file</h2>
	<form action="/download">
		<label>File to Download:</label><br>
		<input type="text" id="downloadFileName" name="downloadFileName"><br>
		<input type="hidden" name="user" value=  <%= request.getParameter("user")%> >
		<input type="submit" value="Submit">
	</form>
	<br>
    <h2>Share file</h2>
	<form action="/share">
    		<label>File to Share:</label><br>
    		<input type="text" id="fileToShare" name="fileToShare"><br>
    		<label>Share With:</label><br>
    		<input type="text" id="shareWith" name="shareWith"><br>
    		<input type="hidden" name="user" value=  <%= request.getParameter("user")%> >
    		<input type="submit" value="Submit">
    	</form>
	<br>
	</div>
	<div class="item2">
        <h2>Files: </h2>
        <br>
	       <table>
	       <tr>
               <th>FileName</th>
               <th>Size</th>
            </tr>
	       <c:forEach items="${list}" var="info">
	       <tr>
	       <c:forEach items="${info}" var="i">
                   	<td>${i}</td>
           </c:forEach>
            </tr>
           </c:forEach>
           </table>
		<br>
		<br>
	</div>
	<div class = "item3">
		<h2>Shared with me: </h2>
		<br>
		<table>
        	       <tr>
                       <th>FileName</th>
                       <th>SharedBy</th>
                       <th>Size</th>
                    </tr>
        	       <c:forEach items="${sharedList}" var="info">
        	       <tr>
        	       <c:forEach items="${info}" var="i">
                           	<td>${i}</td>
                   </c:forEach>
                    </tr>
                   </c:forEach>
                   </table>
     </div>
    </div>
</body>
</html>
