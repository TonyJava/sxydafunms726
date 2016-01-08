/**
 * <p>Description:operate table NMS_MENU and NMS_ROLE_MENU</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-07
 */

package com.afunms.event.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.base.JspPage;
import com.afunms.common.util.SysLogger;
import com.afunms.event.model.CheckEvent;
import com.afunms.event.model.EventList;
import com.afunms.system.vo.FlexVo;

public class CheckEventDao extends BaseDao implements DaoInterface {
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public CheckEventDao() {
		super("nms_checkevent");
	}

	// -------------load all --------------
	public List loadAll() {
		List list = new ArrayList(5);
		try {
			rs = conn
					.executeQuery("select * from nms_checkevent order by name");
			while (rs.next())
        	list.add(loadFromRS(rs)); 
     }
     catch(Exception e)
     {
         SysLogger.error("CheckEventDao:loadAll()",e);
         list = null;
     }
     finally
     {
         conn.close();
     }
     return list;
  }

  public List loadByWhere(String where){
	  List list = new ArrayList(); 
      try {
    	  rs = conn.executeQuery("select * from nms_checkevent " + where);
		while(rs.next()){
			  list.add(loadFromRS(rs)); 
		  }
	} catch (SQLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}	
	  return list;
  }
	
	
	
  
	public boolean save(BaseVo baseVo)
	{
		CheckEvent vo = (CheckEvent)baseVo;
		//先删除,如果有该指标告警
		delete(vo.getName());
		StringBuffer sql = new StringBuffer(100);
		sql.append("insert into nms_checkevent(name,alarmlevel)values(");
		sql.append("'");
		sql.append(vo.getName());
		sql.append("',");
		sql.append(vo.getAlarmlevel());	
		sql.append(")");
		//SysLogger.info(sql.toString());
		return saveOrUpdate(sql.toString());
	}
	
	public boolean savecheckevent(BaseVo baseVo)
	{
		CheckEvent vo = (CheckEvent)baseVo;
		boolean flag = true;
		//先删除,如果有该指标告警
		delete(vo.getName());
		StringBuffer sql = new StringBuffer(100);
		sql.append("insert into nms_checkevent(name,alarmlevel)values(");
		sql.append("'");
		sql.append(vo.getName());
		sql.append("',");
		sql.append(vo.getAlarmlevel());	
		sql.append(")");
		try{
			conn.executeUpdate(sql.toString());
		}catch(Exception e){
			flag = false;
		}
		//SysLogger.info(sql.toString());
		return flag;
	}
	
	public boolean delete(String name)
	{
		boolean flag = true;
		String sql = "delete from nms_checkevent where name='"+name+"'";
		//SysLogger.info(sql);
		try{
			conn.executeUpdate(sql);
		}catch(Exception e){
			flag = false;
		}
		return flag;
	}
	
	public boolean deleteByNodeType(String nodeid,String type)
	{
		boolean flag = true;
		String sql = "delete from nms_checkevent where nodeid='"+nodeid+"' and type ='"+type+"'";
		try{
			conn.executeUpdate(sql);
		}catch(Exception e){
			flag = false;
		}
		return flag;
	}
	
	public boolean empty()
	{
		String sql = "delete from nms_checkevent";
		//SysLogger.info(sql);
		return saveOrUpdate(sql);
	}
	
  //---------------update a business----------------
  public boolean update(BaseVo baseVo)
  {    
     return true;
  }
  
	public boolean delete(String[] id)
	{
	    return true;
	}
  
  public BaseVo findByID(String id)
  {
     BaseVo vo = null;
     try
     {
        rs = conn.executeQuery("select * from nms_checkevent where id=" + id );
        if(rs.next())
           vo = loadFromRS(rs);
     }
     catch(Exception e)
     {
         SysLogger.error("EventListDao.findByID()",e);
         vo = null;
     }
     finally
     {
        conn.close();
     }
     return vo;
  }
  
  public int findByName(String name)
  {
	  int flag = 0;
	  CheckEvent vo = null;
     try
     {
        rs = conn.executeQuery("select * from nms_checkevent where name='" + name+"'" );
        if(rs.next()){
        	vo = (CheckEvent)loadFromRS(rs);
        	flag = vo.getAlarmlevel();
        }
           
     }
     catch(Exception e)
     {
         //SysLogger.error("EventListDao.findByID()",e);
         //vo = null;
     }
     finally
     {
        //conn.close();
     }
     return flag;
  }
  
  	public int findMaxAlarmLevelByName(String name) {
		int flag = 0;
		try {
			rs = conn.executeQuery("select max(alarmlevel) from nms_checkevent where name like '%"
					+ name + "%'");
			if (rs.next()) {
				flag = rs.getInt("max(alarmlevel)");
			}

		} catch (Exception e) {
			
		} finally {
			
		}
		
		//System.out.println(flag+"==================" + name);
		return flag;
	}
  
   public BaseVo loadFromRS(ResultSet rs)
   {
	   CheckEvent vo = new CheckEvent();
      try
      {
          vo.setName(rs.getString("name"));
          vo.setAlarmlevel(rs.getInt("alarmlevel"));
      }
      catch(Exception e)
      {
          SysLogger.error("EventListDao.loadFromRS()",e);
          vo = null;
      }
      return vo;
   } 
   
   public BaseVo findCheckEventByName(String name){
	    CheckEvent vo = null;
	    try
	    {
	    	rs = conn.executeQuery("select * from nms_checkevent where name='" + name+"'" );
	        if(rs.next()){
	        	vo = (CheckEvent)loadFromRS(rs);
	        }
	    } catch(Exception e) {
	    	SysLogger.error("CheckEventDao.findByID()",e);
	    }
	    return vo;
   }
   
   public boolean deleteCheckEvent(String nodeId,String type,String subtype,String name){
	    return saveOrUpdate("delete from nms_checkevent where nodeid='"+ nodeId +"' and type='" + type+"' and subtype='" + subtype+"' and indicators_name='" +name +"'");
   }
   
   public boolean deleteCheckEvent(String nodeId,String type,String subtype,String name, String sindex){
	    return saveOrUpdate("delete from nms_checkevent where nodeid='"+ nodeId +"' and type='" + type+"' and subtype='" + subtype+"' and indicators_name='" +name +"' and sindex='" + sindex + "'");
   }
}
