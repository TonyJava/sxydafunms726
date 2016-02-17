/**
 * <p>Description:operate table NMS_USER</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-07
 */

package com.afunms.system.dao;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.base.GenericBaseDao;
import com.afunms.common.util.SysLogger;
import com.afunms.system.model.User;

public class UserDao extends GenericBaseDao<User> implements DaoInterface<User>
{
   public UserDao()
   {
	   super("system_user",User.class);
   }

   @Override
   public List listByPage(int curpage,int perpage)
   {
	   //不显示超级管理员
	   return listByPage(curpage,"",perpage);
   }
   
   /**
    * 
 * @param userid	用户的唯一标识
 * @return	一个完整的用户对象
 */
public User loadAllByUser(String userid)
   {

	   User vo = null;
   
      try
      {
          rs = conn.executeQuery("select * from system_user where userid ='"+ userid+"'");
              vo = loadFromRS(rs);
      }
      catch(Exception e)
      {     
          SysLogger.error("UserDao:loadAll()",e);
      }
      finally
      {
          conn.close();
      }
      return vo;
   }
   
   /**
    * 按用户ID和密码找一条记录,用于登录
    */
   public User findByLogin(String id,String pwd)
   {
      User vo = null;
      try
      {
          rs = conn.executeQuery("select * from system_user where userid='" + id + "' and password='" + pwd + "'");
             vo = loadFromRS(rs);
      }
      catch(Exception e)
      {
          SysLogger.error("UserDao.findByLogin",e);
      }
      finally
      {
          conn.close();
      }
      return vo;
   }

   /**
    * 按用户ID找一条记录,用于登录
    * @param id 用户id，一般对应登录框中的用户名
    */
   public User findByUserId(String id)
   {
      User vo = null;
      try
      {
          rs = conn.executeQuery("select * from system_user where userid='" + id + "'");
     
           vo = loadFromRS(rs);
      }
      catch(Exception e)
      {
          SysLogger.error("UserDao.findByLogin",e);
      }
      finally
      {
          conn.close();
      }
      return vo;
   }
   @Override
   public boolean save(BaseVo baseVo)
   {
	   return false;	   
   }

   public int save(User vo)
   {	   
       int result = -1;
       String sql = null;
	   try
	   {
	       sql = "select * from system_user where userid='" + vo.getUserid() + "'";
	       rs = conn.executeQuery(sql);
	       if(rs.next())  //用户已经存在
	          return 0;

	       StringBuffer sqlBf = new StringBuffer(100);
	       sqlBf.append("insert into system_user(id,name,userid,password,sex,dept_id,position_id,role_id,phone,email,mobile,businessids)");
	       sqlBf.append("values(");
	       sqlBf.append(getNextID());
	       sqlBf.append(",'");
	       sqlBf.append(vo.getName());
	       sqlBf.append("','");
	       sqlBf.append(vo.getUserid());
	       sqlBf.append("','");
	       sqlBf.append(vo.getPassword());
	       sqlBf.append("',");
	       sqlBf.append(vo.getSex());
	       sqlBf.append(",");
	       sqlBf.append(vo.getDept());
	       sqlBf.append(",");
	       sqlBf.append(vo.getPosition());
	       sqlBf.append(",");	       
	       sqlBf.append(vo.getRole());
	       sqlBf.append(",'");
	       sqlBf.append(vo.getPhone());
	       sqlBf.append("','");
	       sqlBf.append(vo.getEmail());
	       sqlBf.append("','");
	       sqlBf.append(vo.getMobile());
	       sqlBf.append("','");
	       sqlBf.append(vo.getBusinessids());
	       sqlBf.append("')");
	       conn.executeUpdate(sqlBf.toString());
	       result = 1;
	   }
	   catch (Exception e)
	   {
	    	result = -1;
	        SysLogger.error("Error in UserDao.save()",e);
	   }
	   finally
	   {
	       conn.close();
	   }
	   return result;
   }
   
   @Override
   public boolean update(BaseVo baseVo)
   {
	   User vo = (User)baseVo;

	   StringBuffer sql = new StringBuffer(200);
       sql.append("update system_user set name='");
       sql.append(vo.getName());
       sql.append("',sex=");
       sql.append(vo.getSex());
       sql.append(",dept_id=");
       sql.append(vo.getDept());
       sql.append(",position_id=");
       sql.append(vo.getPosition());
       sql.append(",role_id=");
       sql.append(vo.getRole());
       sql.append(",phone='");
       sql.append(vo.getPhone());
       sql.append("',mobile='");
       sql.append(vo.getMobile());
       sql.append("',email='");
       sql.append(vo.getEmail());
       sql.append("',businessids='");
       sql.append(vo.getBusinessids());  
       sql.append("',skins='");
       sql.append(vo.getSkins());
       if(vo.getPassword()!=null) //密码要修改
       {
           sql.append("',password='");
           sql.append(vo.getPassword());
       }
	   sql.append("' where id=");
       sql.append(vo.getId());
       return saveOrUpdate(sql.toString());
   }

}
