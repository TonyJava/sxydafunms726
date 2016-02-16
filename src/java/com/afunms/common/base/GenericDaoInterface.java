/**
 * 
 */
package com.afunms.common.base;

import java.util.List;

/**
 * @author HP
 *
 */
public interface GenericDaoInterface<T> {

	/**
	 * 加载所有记录
	 */
	public List loadAll();
	
	/**
	 * 分页列表 
	 */
	public List listByPage(int curpage,int perpage);
	
	/**
	 * 带条件的分页列表 
	 */
	public List listByPage(int curpage,String where,int perpage);
	
	/**
	 * 删除一批记录 
	 */
	public boolean delete(String[] id);
		
	/**
	 * 按ID找一条记录 
	 */
	public BaseVo findByID(String id);
	
	/**
	 * 增加一条记录 
	 */
	public boolean save(BaseVo vo);
	   
    /**
     * 更新一条记录 
     */
    public boolean update(BaseVo vo);
    
    /**
     * 得到分页对象
     */
    public JspPage getPage();

}
