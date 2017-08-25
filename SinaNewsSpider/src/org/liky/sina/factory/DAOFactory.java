package org.liky.sina.factory;

import org.liky.sina.dao.INewsDAO;
import org.liky.sina.dao.impl.NewsDAOImpl;
import org.liky.sina.dbc.DataBaseConnection;

public class DAOFactory {

	public static INewsDAO getINewsDAOInstance(DataBaseConnection dbc) {
		return new NewsDAOImpl(dbc);
	}

}
