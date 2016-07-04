<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
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
			
		</form>
	</div>
	<div style="height: 100px;"></div>
</body>
</html>