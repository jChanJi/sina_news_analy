package org.liky.sina.dbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataBaseConnection {

	private static final String DBDRIVER = "com.mysql.jdbc.Driver";
	private static final String DBURL = "jdbc:mysql://localhost:3306/sina_news?useUnicode=true&characterEncoding=utf-8&useSSL=false";
	private static final String DBUSER = "root";
	private static final String DBPASSWORD = "123";

	private Connection conn;

	public Connection getConnection() {
		try {
			if (conn == null || conn.isClosed()) {
				// 建立一个新的连接
				Class.forName(DBDRIVER);
				conn = DriverManager.getConnection(DBURL, DBUSER, DBPASSWORD);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		return conn;
	}

	public void close() {
		if (conn != null) {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
}
