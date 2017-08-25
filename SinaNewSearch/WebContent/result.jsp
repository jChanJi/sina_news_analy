<%@page import="org.liky.sina.vo.News"%>
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
		<%
			List<News> allNews = (List<News>)request.getAttribute("allNews");
		%>
		<table width="80%">
			<%
				for (News n : allNews) {
			%>
				<tr>
					<td>
						<a href="<%=n.getUrl() %>" target="_blank"><%=n.getTitle()%></a> <br>
						<%=n.getDescription() %>
						<hr/>
					</td>
				</tr>
			<%
				}
				
			%>
		</table>
		
		<%
			int cp = (Integer)request.getAttribute("currentPage");
			int allPages = (Integer)request.getAttribute("allPages");
		%>
		<form id="split_page_form" action="SearchServlet" method="post">
			<input type="hidden"  name="currentPage" id="cp" value="<%=cp %>" />
			<input type="button" <%=cp == 1?"disabled":"" %> value="首页" onclick="changeCp(1);">
			<input type="button" <%=cp == 1?"disabled":"" %> value="上一页" onclick="changeCp(<%=cp - 1 %>);">
			<input type="button" <%=cp == allPages?"disabled":"" %> value="下一页" onclick="changeCp(<%=cp + 1 %>);">
			<input type="button" <%=cp == allPages?"disabled":"" %> value="尾页" onclick="changeCp(<%=allPages %>);">
			第 <%=cp %>  页 / 共 <%=allPages %> 页
			<br>
			请输入查询关键字：<input type="text" name="keyword" value="<%=request.getParameter("keyword")%>">
			<input type="submit" value="查询">
		</form>
		<script type="text/javascript">
			function changeCp(newcp) {
				// 改变当前页数
				document.getElementById("cp").value = newcp;
				// 提交表单
				document.getElementById("split_page_form").submit();
			}
		</script>
		
	</center>
</body>
</html>
