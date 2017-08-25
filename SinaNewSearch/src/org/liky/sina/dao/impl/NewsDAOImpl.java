package org.liky.sina.dao.impl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.liky.sina.dao.INewsDAO;
import org.liky.sina.dbc.DataBaseConnection;
import org.liky.sina.vo.News;

public class NewsDAOImpl implements INewsDAO {

	private DataBaseConnection dbc;

	public NewsDAOImpl(DataBaseConnection dbc) {
		this.dbc = dbc;
	}

	@Override
	public void doCreate(News news) throws Exception {
		String sql = "INSERT INTO news (id,title,description,url) VALUES (?,?,?,?)";
		PreparedStatement pst = dbc.getConnection().prepareStatement(sql);
		// 设置参数
		pst.setInt(1, news.getId());
		pst.setString(2, news.getTitle());
		pst.setString(3, news.getDescription());
		pst.setString(4, news.getUrl());

		pst.executeUpdate();

	}

	@Override
	public News findById(int id) throws Exception {
		String sql = "SELECT id,title,description,url FROM news WHERE id = ?";
		PreparedStatement pst = dbc.getConnection().prepareStatement(sql);
		pst.setInt(1, id);
		ResultSet rs = pst.executeQuery();
		News news = null;
		if (rs.next()) {
			news = new News();
			news.setId(rs.getInt(1));
			news.setTitle(rs.getString(2));
			news.setDescription(rs.getString(3));
			news.setUrl(rs.getString(4));
		}
		return news;
	}

	@Override
	public List<News> findByIds(Integer[] ids, int start, int pageSize)
			throws Exception {
		StringBuilder sql = new StringBuilder(
				"SELECT id,title,description,url FROM news WHERE id IN (");
		if (ids != null && ids.length > 0) {
			for (int id : ids) {
				sql.append(id);
				sql.append(",");
			}
			// 第一个 ? 表示开始的记录数，第二个 ？ 表示每页显示的记录数。
			String resultSQL = sql.substring(0, sql.length() - 1)
					+ ") LIMIT ?,?";

			PreparedStatement pst = dbc.getConnection().prepareStatement(
					resultSQL);
			pst.setInt(1, start);
			pst.setInt(2, pageSize);
			ResultSet rs = pst.executeQuery();
			List<News> list = new ArrayList<News>();
			while (rs.next()) {
				News news = new News();
				news.setId(rs.getInt(1));
				news.setTitle(rs.getString(2));
				news.setDescription(rs.getString(3));
				news.setUrl(rs.getString(4));
				list.add(news);
			}

			return list;

		} else {
			return null;
		}
	}

	@Override
	public int getAllCount(Integer[] ids) throws Exception {
		StringBuilder sql = new StringBuilder(
				"SELECT count(id) FROM news WHERE id IN (");
		if (ids != null && ids.length > 0) {
			for (int id : ids) {
				sql.append(id);
				sql.append(",");
			}
			// 第一个 ? 表示开始的记录数，第二个 ？ 表示每页显示的记录数。
			String resultSQL = sql.substring(0, sql.length() - 1) + ")";

			PreparedStatement pst = dbc.getConnection().prepareStatement(
					resultSQL);
			ResultSet rs = pst.executeQuery();
			if (rs.next()) {
				return rs.getInt(1);
			}

		}
		return 0;
	}

}
