/**
 * <p>Description:operate table NMS_MENU and NMS_ROLE_MENU</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-07
 */

package com.afunms.config.dao;

import java.util.List;
import java.util.ArrayList;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;

import com.afunms.common.util.SysLogger;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.base.GenericBaseDao;
import com.afunms.common.base.GenericDaoInterface;
import com.afunms.config.model.Business;
import com.afunms.common.base.BaseVo;
import com.afunms.system.model.Department;
import com.afunms.system.model.User;
import com.ibm.ctg.server.TServer;
import com.ibm.db2.jcc.c.r;

public class GenericBusinessDao extends GenericBaseDao<Business> //implements GenericDaoInterface<T>
{


  public boolean save(BaseVo baseVo)
	{
		Business vo = (Business)baseVo;
		StringBuffer sql = new StringBuffer(100);
		sql.append("insert into system_business(name,descr,pid,bigsystem,smallsystem)values(");
		sql.append("'");
		sql.append(vo.getName());
		sql.append("','");
		sql.append(vo.getDescr());	
		sql.append("','");
		sql.append(vo.getPid());	
		sql.append("','");
		sql.append(vo.getBigsystem());
		sql.append("','");
		sql.append(vo.getSmallsystem());
		sql.append("')");
		return saveOrUpdate(sql.toString());
	}
	
//---------------update a business----------------
public  boolean update(BaseVo baseVo)
{
	  Business vo = (Business)baseVo;
   boolean result = false;
   StringBuffer sql = new StringBuffer();
   sql.append("UPDATE system_business SET name='"); 
   sql.append(vo.getName());                           
   sql.append("',descr='");
   sql.append(vo.getDescr());                          
   sql.append("',pid='");
   sql.append(vo.getPid());                            
   sql.append("',bigsystem='");
   sql.append(vo.getBigsystem());                      
   sql.append("',smallsystem='");
   sql.append(vo.getSmallsystem());                    
   sql.append("'WHERE id="); 
   sql.append(vo.getId());  
   

   conn.executeUpdate(sql.toString());
   result = true;
   conn.close();
   
   return result;
}
  
	public boolean delete(String[] id)
	{
		boolean result = false;
	    try
	    {	    
	        for(int i=0;i<id.length;i++)
	        {
	            conn.addBatch("delete from system_business where id=" + id[i]);
	        }	         
	        conn.executeBatch();
	        result = true;
	    }
	    catch(Exception e)
	    {
	    	result = false;
	        SysLogger.error("BusinessDao.delete()",e);	        
	    }
	    finally
	    {
	         conn.close();
	    }
	    return result;
	}
	
	
	public boolean deleteVoAndChildVoById(String id)
	{
		boolean result = false;
		try {
			String sql = "delete from system_business where id='" + id +"' or pid='" + id + "'";
			System.out.println(sql);
			conn.executeUpdate(sql);
			
			result = true;
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			result = false;
		}finally{
			conn.close();
		}
	    return result;
	}
  


  public GenericBusinessDao(Class<Business> clazz){
	  super("system_business",clazz);	  	 
  }

}
