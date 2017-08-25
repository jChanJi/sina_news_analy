package org.liky.sina.dao;

import java.util.List;

import org.liky.sina.vo.News;

public interface INewsDAO {

	/**
	 * 添加数据
	 * 
	 * @param news
	 *            要添加的数据对象
	 * @throws Exception
	 */
	public void doCreate(News news) throws Exception;

	/**
	 * 根据主键查询数据
	 * 
	 * @param id
	 *            主键
	 * @return 查询到的对象
	 * @throws Exception
	 */
	public News findById(int id) throws Exception;

	/**
	 * 根据id来查询所有结
	 * 
	 * @param ids
	 *            要查询的id
	 * @return 查询到的数据
	 * @throws Exception
	 */
	public List<News> findByIds(Integer[] ids, int start, int pageSize)
			throws Exception;

	public int getAllCount(Integer[] ids) throws Exception;

}
