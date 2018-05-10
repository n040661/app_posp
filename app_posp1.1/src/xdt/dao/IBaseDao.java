package xdt.dao;

import java.util.List;

/**
 * @author xiaomei
 * @param <T>
 */
public interface IBaseDao<T> {
	
	/**
	 * 根据传入的对象新增记录
	 * @param t
	 * @return
	 */
	public int insert(T t) throws Exception;
	
	/**
	 * 根据id删除记录
	 * @param id
	 * @return
	 */
	public int delete(String id) throws Exception;
	
	/**
	 * 根据传入的对象更新记录
	 * @param t
	 * @return
	 */
	public int update(T t) throws Exception;
	
	/**
	 * 根据id检索记录
	 * @param id
	 * @return
	 */
	public T searchById(String id) throws Exception;
	
	/**
	 * 根据实体检索记录
	 * @param t
	 * @return
	 */
	public List<T> searchList(T t) throws Exception;
}
