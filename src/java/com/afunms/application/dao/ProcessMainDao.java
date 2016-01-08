package com.afunms.application.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Hashtable;

import com.afunms.application.course.model.LsfClassNode;
import com.afunms.application.model.ProcessMainModel;
import com.afunms.application.model.ProcessMainVo;
import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.base.JspPage;
import com.afunms.common.util.DBManager;
import com.afunms.common.util.SysLogger;


public class ProcessMainDao extends BaseDao implements DaoInterface{
	
	public ProcessMainDao() {
		super("nms_mainprocess");
	}
	/**
	 * @param nodeid 设备id
	 * @param thevalue 进程名称
	 * @return
	 */
	public int findByNodeidMainProcess(String nodeid,String thevalue){
		int flag =0;
		StringBuffer sql = new StringBuffer();
		sql.append("select * from nms_mainprocess where nodeid='");
		sql.append(nodeid);
		sql.append("' and proname='");
		sql.append(thevalue);
		sql.append("';");
		DBManager dbManager = new DBManager();
		ResultSet rs = dbManager.executeQuery(sql.toString());
		try{
			while(rs.next()){
				flag = 1;
			}
		}catch(SQLException e){
			e.printStackTrace();
		}finally{
			dbManager.close();
		}
		return flag;
	}
	
	

	/**
	 * 
	 * 返回所有的关键进程列表
	 * @return
	 */
	
	public Hashtable getMainProcesslist()
	{
		
		
		Hashtable list=new Hashtable();
		DBManager dbManager = new DBManager();
		try {
			list=dbManager.executeQuerykeytwoListHashMap("select * from nms_mainprocess", "nodeid", "proname");
			dbManager.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return list;
	}
	
	public List getProcessList(String ip){
		StringBuffer sql = new StringBuffer();
		sql.append("select distinct(pro.ip),pro.nodeid,pro.subtype from nms_process_data_temp pro");

		//System.out.println("==================123123111111111111111111=========================sql==================="+ sql.toString());
		
		DBManager dbManager = new DBManager();
		ResultSet rs = dbManager.executeQuery(sql.toString());
		List ls = new ArrayList();
		try {
			while(rs.next()){
				ProcessMainVo vo = new ProcessMainVo();
				//vo.setAlias(rs.getString("alias"));
				vo.setIp(rs.getString("ip"));
				vo.setNodeid(rs.getString("nodeid"));
				vo.setSubtype(rs.getString("subtype"));
				//vo.setThevalue(rs.getString("thevalue"));
				ls.add(vo);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			//dbManager.close();
		}
		return ls;
	}

	@Override
	public BaseVo loadFromRS(ResultSet rs) {
		// TODO Auto-generated method stub
		Hashtable list=this.getMainProcesslist();
		ProcessMainVo vo = new ProcessMainVo();
	      try
	      {
	         vo.setThevalue(rs.getString("thevalue"));
	         vo.setNodeid(rs.getString("nodeid"));
	         vo.setAlias(rs.getString("alias"));
	         vo.setIp(rs.getString("ip"));
	         vo.setSubtype(rs.getString("subtype"));
	         if(list.containsKey(vo.getNodeid()+"-"+vo.getThevalue()))
	         {
	         vo.setId((String)((Hashtable)(list.get(vo.getNodeid()+"-"+vo.getThevalue()))).get("id"));
	         vo.setIsmain("1");
	         }
	      }
	      catch(Exception ex)
	      {
	          SysLogger.error("Error in ProcessMainDao.loadFromRS()",ex);
	          vo = null;
	      }
	      return vo;
	}

	public boolean save(BaseVo vo) {
		ProcessMainModel model =(ProcessMainModel)vo;
		StringBuffer sql_in = new StringBuffer();
		sql_in.append("insert into nms_mainprocess (nodeid,proname,ipaddress) value ('");
		sql_in.append(model.getNodeid()+"','");
		sql_in.append(model.getProcessName()+"','");
		sql_in.append(model.getIpaddress()+"');");
		//System.out.println("=====sql_in======" + sql_in.toString());
		StringBuffer sql_de = new StringBuffer();
		sql_de.append("delete from nms_mainprocess where proname='");
		sql_de.append(model.getProcessName());
		sql_de.append("' and nodeid=");
		sql_de.append(model.getNodeid());
		//System.out.println("=====sql_de======" + sql_de.toString());
		
		DBManager dbManager = new DBManager();
		if(model.getMon_flag().endsWith("0")){
			dbManager.executeUpdate(sql_de.toString());
		}else{
			dbManager.executeUpdate(sql_de.toString());
			dbManager.executeUpdate(sql_in.toString());
		}
		dbManager.close();
		return true;
	}
	public boolean update(BaseVo vo) {
		// TODO Auto-generated method stub
		return false;
	}
	
	  /**
	    * 删除一批记录
	    */
	   public boolean delete(String[] id)
	   {
		   boolean result = false;
		   try
		   {
			   System.out.println("2222222====id[i]==" + id[0].toString());
		       for(int i=0;i<id.length;i++)
		    	   
		    	   if(!id[i].equals("null"))
		    	   {
		    		   System.out.println("2222221112====id[i]==" + id[i].toString());
		           conn.addBatch("delete from nms_mainprocess where id=" + id[i]);
		    	   }
		      
		       conn.executeBatch();
		       result = true;
		       
		   }
		   catch(Exception ex)
		   {
		       SysLogger.error("BaseDao.delete()",ex);
		       result = false;
		   }
		   return result;
	   }
	   
	   
	   /**
	    * 有条件分页显示
	    */
	   public List listByPage2(int curpage,int perpage,String ip)
	   {
		   List list = new ArrayList();	   
		   try 
		   {	
			   
			   
			   String sql="select distinct(pro.thevalue),pro.nodeid,pro.ip,pro.subtype,nod.ip_address,nod.alias from " +
		   		"nms_process_data_temp pro ,topo_host_node nod " + "where subentity = 'Name' and pro.nodeid=nod.id";
			   if(ip!=null){
					sql=sql+" and pro.ip='"+ip+"'";
				}
			   sql=sql+" group by thevalue order by nodeid asc;";
			   System.out.println("+++++++++++++++++++++++++++++++++++_____________====="+sql);
			   if(null !=ip){
			   System.out.println("+ip+"+ip.toString());
			   }
			   String sqlcount="select count(*) from " +
		   		"nms_process_data_temp pro ,topo_host_node nod " + "where subentity = 'Name' and pro.nodeid=nod.id";
			   if(ip!=null){
				   sqlcount=sqlcount+" and pro.ip='"+ip+"'";
				}
			  
			   System.out.println("----------sqlcount===================" + sqlcount.toString());
			   rs = conn.executeQuery(sqlcount);
			   if(rs.next())
				   jspPage = new JspPage(perpage,curpage,rs.getInt(1));
			  rs = conn.executeQuery(sql );
			 
			   
			   int loop = 0;
			   while(rs.next())
			   {
				  //System.out.println("===============11================"+rs.getString("ip"));
				   
				  loop++;
				  if(loop<jspPage.getMinNum()) continue;
				  list.add(loadFromRS(rs));
				  if(loop==jspPage.getMaxNum()) break;
			   }
		   } 
		   catch (Exception e) 
		   {
			   SysLogger.error("Dao.listByPage2()",e);
			   list = null;
		   }
		   finally
		   {
			   if(rs != null){
				   try{
					 //  rs.close();
				   }catch(Exception e){
				   }
			   }
			 //  conn.close();
		   }
		   return list;
	   }

}
