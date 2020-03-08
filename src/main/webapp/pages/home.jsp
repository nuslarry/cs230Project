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
	<form action="/doUpload" method="post" enctype="multipart/form-data" onsubmit="return validateMyForm3();">
	    <label>File to Upload:</label><br>
	    <input type="file" name="file" id="file"><br>
	    <label for="replications">Replication factor (between 1 and 1000):</label>
	    <input type="number" id="replications" name="replications" min="1" max="1000" value="1">
	    <input type="hidden" name="user" value=  <%= request.getParameter("user")%> >
	    <br>
	    <button type="submit">Upload</button>
	</form>
	<br>
	<h2>Download file</h2>
	<form action="/download" onsubmit="return validateMyForm();">
		<input type="hidden" id="downloadFileName" name="downloadFileName">
		<input type="hidden" id="downloadSharedUser" name="downloadSharedUser">
		<input type="hidden" name="user" value=  <%= request.getParameter("user")%> >
		<input type="submit" value="Download" id= "Download">
	</form>
	<br>
    <h2>Share file</h2>
	<form action="/share" onsubmit="return validateMyForm2();">
    		<input type="hidden" id="fileToShare" name="fileToShare" value = ><br>
    		<label>Share With:</label><br>
    		<input type="text" id="shareWith" name="shareWith"><br>
    		<input type="hidden" name="user" value=  <%= request.getParameter("user")%> >
    		<input type="submit" value="Share" id ="Share">
    	</form>
	<br>
	<h2>Delete file</h2>
    	<form action="/delete" onsubmit="return validateMyForm4();">
        		<input type="hidden" id="fileToDelete" name="fileToDelete" value = ><br>
        		<input type="hidden" name="user" value=  <%= request.getParameter("user")%> >
        		<input type="submit" value="Delete" id ="Delete">
        	</form>
	</div>
	<div class="item2">
        <h2>Files: </h2>
        <br>
	       <table id="table1">
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
		<table id = "table2">
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
    <script>
    let table1 = document.getElementById("table1");
    let table2 = document.getElementById("table2");
    var selectedRow = null;
    var selectedShared = null;
    var recordedBg = null;
function genHandle3(row)
        {
            return function(event) {
            if(selectedRow!=row)
            {
            row.style.background="yellow";
            }
     };
        };
    function genHandle(row,sharedFile)
    {
    var oldbg = row.style.background;
        return function() {
        if(selectedRow != null){
            selectedRow.style.background = recordedBg;
        }
        row.style.background="lightblue";
        selectedRow = row;
        selectedShared = sharedFile;
        recordedBg = oldbg;
    };
 };

    function genHandle2(row)
        {
        var oldbg = row.style.background;
            return function(event) {
            if(selectedRow != row){
            row.style.background=oldbg;
            }
     };
        };

     function validateMyForm(){
     if(selectedRow == null){
     alert("You must select one file to download!")
     return false;
     }
     return true;
     }

     function validateMyForm2(){
          if(selectedRow == null){
          alert("You must select one file to share!")
          return false;
          }
          if(selectedShared){
          alert("You must select your own file to share!")
          return false;
          }
          if('${user}' == document.getElementById("shareWith").value){
          alert("You cannot share file to yourself!")
          return false;
          }
          return true;
          }

     function validateMyForm3(){
          if(document.getElementById("replications").value==""){
          alert("You cannot set replication factor as null!")
          return false;
          }
          if(document.getElementById("file").value==""){
                    alert("You must specify the file name!")
                    return false;
                    }
          return true;
          }


     function validateMyForm4(){
          if(selectedRow == null){
                    alert("You must select one file to delete!")
                    return false;
                    }
                    if(selectedShared){
                    alert("You must select your own file to delete!")
                    return false;
                    }
          return true;
          }



    for(var i=1;i<table1.rows.length;i++){
    table1.rows[i].addEventListener("mouseenter",genHandle3(table1.rows[i]));
    table1.rows[i].onclick= genHandle(table1.rows[i],false);
    table1.rows[i].addEventListener("mouseleave",genHandle2(table1.rows[i]));
    }
    for(var i=1;i<table2.rows.length;i++){
        table2.rows[i].addEventListener("mouseenter",genHandle3(table2.rows[i]));
        table2.rows[i].onclick= genHandle(table2.rows[i],true);
        table2.rows[i].addEventListener("mouseleave",genHandle2(table2.rows[i]));
        }


    var download = document.getElementById("Download");
    var share = document.getElementById("Share");
    var delete1 = document.getElementById("Delete");
    download.onclick = function(){
       if(!selectedShared){
        document.getElementById("downloadFileName").value = selectedRow.children[0].innerText;
        document.getElementById("downloadSharedUser").disabled = true
       }else{
       document.getElementById("downloadSharedUser").disabled = false
       document.getElementById("downloadFileName").value = selectedRow.children[0].innerText;
       document.getElementById("downloadSharedUser").value = selectedRow.children[1].innerText;
       }
    }
    share.onclick = function(){
    document.getElementById("fileToShare").value = selectedRow.children[0].innerText;
    }

    delete1.onclick = function(){
        document.getElementById("fileToDelete").value = selectedRow.children[0].innerText;
        }
    </script>
</body>

</html>
