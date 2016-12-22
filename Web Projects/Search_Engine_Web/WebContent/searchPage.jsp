<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
<%
	String[] urlArray = (String[]) request.getAttribute("urlArray");
	for(int i = 0; i < urlArray.length; ++i){
		if(urlArray[i] != null && !urlArray[i].equals(""))
			urlArray[i] = "http://" + urlArray[i];
	}
%>
</head>
<body>
	<div class="container">
		<h1 class="page-header">
			<span class="glyphicon glyphicon-user"></span>Search Engine
		</h1>
		<form action="/Search_Engine_Web/Controller" class="form-horizontal" method="post">
			<div class="form-group">
				<div class="col-sm-5">

					<input type="text" class="form-control" placeholder="please input keywords" name="keywords" value="">
				</div>
			</div>
			<div class="form-group">
				<div class="col-sm-5">
					<button class="submit" name="action" value="searching">start</button>
				</div>
			</div>
			
			<h3>
			<a href=<%=urlArray[0]%> class="navbar-link" name="url1" value=""><%=urlArray[0]%>
			</a>
			<br />
			<a href=<%=urlArray[1]%> class="navbar-link" name="url1" value=""><%=urlArray[1]%>
			</a>
			<br />
			<a href=<%=urlArray[2]%> class="navbar-link" name="url2" value=""><%=urlArray[2]%>
			</a>
			<br />
			<a href=<%=urlArray[3]%> class="navbar-link" name="url3" value=""><%=urlArray[3]%>
			</a>
			<br />
			<a href=<%=urlArray[4]%> class="navbar-link" name="url4" value=""><%=urlArray[4]%>
			</a>
			<br />
			<a href=<%=urlArray[5]%> class="navbar-link" name="url5" value=""><%=urlArray[5]%>
			</a>
			<br />
			<a href=<%=urlArray[6]%> class="navbar-link" name="url6" value=""><%=urlArray[6]%>
			</a>
			<br />
			<a href=<%=urlArray[7]%> class="navbar-link" name="url7" value=""><%=urlArray[7]%>
			</a>
			<br />
			<a href=<%=urlArray[8]%> class="navbar-link" name="url8" value=""><%=urlArray[8]%>
			</a>
			<br />
			<a href=<%=urlArray[9]%> class="navbar-link" name="url9" value=""><%=urlArray[9]%>
			</a>
			<br />
			
		</form>
	</div>
	<div style="height: 100px;"></div>
</body>
</html>