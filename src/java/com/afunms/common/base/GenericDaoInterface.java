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
	 * �������м�¼
	 */
	public List loadAll();
	
	/**
	 * ��ҳ�б� 
	 */
	public List listByPage(int curpage,int perpage);
	
	/**
	 * �������ķ�ҳ�б� 
	 */
	public List listByPage(int curpage,String where,int perpage);
	
	/**
	 * ɾ��һ����¼ 
	 */
	public boolean delete(String[] id);
		
	/**
	 * ��ID��һ����¼ 
	 */
	public BaseVo findByID(String id);
	
	/**
	 * ����һ����¼ 
	 */
	public boolean save(BaseVo vo);
	   
    /**
     * ����һ����¼ 
     */
    public boolean update(BaseVo vo);
    
    /**
     * �õ���ҳ����
     */
    public JspPage getPage();

}
