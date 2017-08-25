<%@ page language="java" import="java.util.*" pageEncoding="UTF-8" %>
<%@ page contentType="text/html; charset=utf-8" %>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<base href="<%=basePath%>">

<title>新浪新闻搜索</title>
</head>

<body>
	<center>
		<form action="SearchServlet" method="post">
			请输入查询关键字：
			<input type="text" name="keyword"> 
			<input type="submit" value="查询">
		</form>	
	</center>
</body>
</html>
