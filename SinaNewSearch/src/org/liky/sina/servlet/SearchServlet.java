package org.liky.sina.servlet;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.liky.sina.dbc.DataBaseConnection;
import org.liky.sina.factory.DAOFactory;
import org.liky.sina.utils.HDFSUtils;
import org.liky.sina.vo.News;

public class SearchServlet extends HttpServlet {

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		this.doPost(request, response);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// 接收提交的查询关键字参数
		// 先处理乱码
		request.setCharacterEncoding("UTF-8");
		// 接收参数
		String keyword = request.getParameter("keyword");

		int currentPage = 1;
		int pageSize = 10;

		// 接收页面传入的页数
		if (request.getParameter("currentPage") != null) {
			currentPage = Integer.parseInt(request.getParameter("currentPage"));
		}

		// 根据关键字进行查询。
		try {
			Integer[] ids = HDFSUtils.getIdsByKeyword(keyword);
			// 根据这些id来查询出相应的结果
			List<News> allNews = DAOFactory.getINewsDAOInstance(
					new DataBaseConnection()).findByIds(ids,
					(currentPage - 1) * pageSize, pageSize);

			int count = DAOFactory
					.getINewsDAOInstance(new DataBaseConnection()).getAllCount(
							ids);

			int allPages = count / pageSize;
			if (count % pageSize != 0) {
				allPages++;
			}

			// 将结果传递回页面显示
			request.setAttribute("allNews", allNews);
			request.setAttribute("allPages", allPages);
			request.setAttribute("currentPage", currentPage);

			// 切换到页面上
			request.getRequestDispatcher("/result.jsp").forward(request,
					response);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
