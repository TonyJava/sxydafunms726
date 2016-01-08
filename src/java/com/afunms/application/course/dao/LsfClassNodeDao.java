package com.afunms.application.course.dao;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.base.JspPage;
import com.afunms.common.util.SysLogger;
import com.afunms.application.course.model.LsfClassNode;







public class LsfClassNodeDao extends BaseDao implements DaoInterface{

	public LsfClassNodeDao() {
		super("lsf_class_node");
		// TODO Auto-generated constructor stub
	}

	
	
	
	@Override
	public BaseVo loadFromRS(ResultSet rs) {
		// TODO Auto-generated method stub
		LsfClassNode vo = new LsfClassNode();
	      try
	      {
	         vo.setClass_id(rs.getInt("classid"));
	         vo.setClassname(rs.getString("classname"));
	         vo.setAlias(rs.getString("alias"));
	         vo.setIp_address(rs.getString("ip_address"));
	         vo.setType(rs.getString("type"));
	         vo.setNodeid(Integer.parseInt(rs.getString("nodeid")));
	         vo.setEnable(Integer.parseInt(rs.getString("enable")));
	         vo.setLogflg(Integer.parseInt(rs.getString("logflg")));
	         vo.setJid(Integer.parseInt(rs.getString("jid")));
	      }
	      catch(Exception ex)
	      {
	          SysLogger.error("Error in LsfClassNodeDao.loadFromRS()",ex);
	          vo = null;
	      }
	      return vo;
	}
	
	
	public BaseVo loadFromRS2(ResultSet rs) {
		// TODO Auto-generated method stub
		LsfClassNode vo = new LsfClassNode();
	      try
	      {
	         vo.setClass_id(rs.getInt("classid"));
	        // vo.setClassname(rs.getString("classname"));
	        // vo.setAlias(rs.getString("alias"));
	         //vo.setIp_address(rs.getString("ip_address"));
	        // vo.setType(rs.getString("type"));
	         vo.setNodeid(Integer.parseInt(rs.getString("nodeid")));
	         vo.setEnable(Integer.parseInt(rs.getString("enable")));
	         vo.setLogflg(Integer.parseInt(rs.getString("logflg")));
	         vo.setJid(Integer.parseInt(rs.getString("jid")));
	      }
	      catch(Exception ex)
	      {
	          SysLogger.error("Error in LsfClassNodeDao.loadFromRS()",ex);
	          vo = null;
	      }
	      return vo;
	}
	

	
	/**
	 * 添加记录
	 */
	public boolean save(BaseVo baseVo) {
		// TODO Auto-generated method stub
		
		LsfClassNode vo = (LsfClassNode)baseVo;
		//Calendar tempCal = (Calendar)vo.getCollecttime();							
		//Date cc = tempCal.getTime();
		//String recordtime = sdf.format(cc);
		StringBuffer sql = new StringBuffer(100);
		sql.append("insert into lsf_class_node(classid,nodeid,enable,logflg,jid)values('");
		sql.append(vo.getClass_id());
		sql.append("','");
		sql.append(vo.getNodeid());
		sql.append("','");
		sql.append(vo.getEnable());
		sql.append("','");
		sql.append(vo.getLogflg());
		sql.append("','");
		sql.append(vo.getJid());
		sql.append("')");
		
		//System.out.println("sss============lsf="+sql.toString());
		return saveOrUpdate(sql.toString());
	}

	/**
	 * 修改mac记录
	 */
	public boolean update(BaseVo baseVo) {
		// TODO Auto-generated method stub
		
		LsfClassNode vo = (LsfClassNode)baseVo;

		   StringBuffer sql = new StringBuffer(200);
	       sql.append("update lsf_class_node set ");
	       sql.append("nodeid='");
	       sql.append(vo.getNodeid());
	       sql.append("',enable='");
	       sql.append(vo.getEnable());
	       sql.append("',logflg='");
	       sql.append(vo.getLogflg());
	       sql.append("',jid='");
	       sql.append(vo.getJid()).append("'");
	       sql.append(" where classid='").append(vo.getClass_id()).append("'");
	       sql.append(" and ");
	       sql.append(" nodeid='").append(vo.getNodeid()).append("'");
	       
	      // System.out.println("dddddd============lsf="+sql.toString());
	       return saveOrUpdate(sql.toString());
	}
	
	
	/**
	 * 修改mac记录
	 */
	public boolean update(BaseVo baseVo,String classidold ,String nodeidold) {
		// TODO Auto-generated method stub
		
		LsfClassNode vo = (LsfClassNode)baseVo;

		   StringBuffer sql = new StringBuffer(200);
	       sql.append("update lsf_class_node set ");
	       sql.append("classid='");
	       sql.append(vo.getClass_id());
           sql.append("',");
	       sql.append("nodeid='");
	       sql.append(vo.getNodeid());
	       sql.append("',enable='");
	       sql.append(vo.getEnable());
	       sql.append("',logflg='");
	       sql.append(vo.getLogflg());
	       sql.append("',jid='");
	       sql.append(vo.getJid()).append("'");
	       sql.append(" where classid='").append(classidold).append("'");
	       sql.append(" and ");
	       sql.append(" nodeid='").append(nodeidold).append("'");
	       
	       //System.out.println("=update===========lsf="+sql.toString());
	       return saveOrUpdate(sql.toString());
	}
	
	
	 /**
	    * 删除一批记录
	    */
	   public boolean lsfdelete(String[] nodeid)
	   {
		   
		   LsfNmsDao nmsdao=new LsfNmsDao();
		   boolean result = false;
		   try
		   {
		       for(int i=0;i<nodeid.length;i++)
		       {
		    	   
		    	   System.out.println("==="+nodeid[i]);
		           conn.addBatch("delete from lsf_class_node where nodeid='"  + nodeid[i]+"'");
		           conn.addBatch(nmsdao.delnmsgathertosql(nodeid[i]));
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
		  * 根据id删除这条记录
		  * @param id
		  * @return
		  */
		  public boolean lsfDeleteByDistrictId(String nodeid)
		   {
			   boolean result = false;
			   try
			   {
				   conn.addBatch("delete  from  lsf_class_node where nodeid='" +nodeid+"'");
				   //System.out.println("delete from nms_contract where id=" + id);
				   conn.executeBatch();
				   result = true;
			   }
			   catch(Exception e)
			   {
				   SysLogger.error("Lsfclassdao.delete()",e); 
			   }
			   finally
			   {
				   conn.close();
			   }
			   return result;
		   }
	  
	  
		  
		  
		  
//		  /**
//		  * 
//		  * @param id
//		  * @return
//		  * @modify nielin
//		  */
//		public List findByMac(String mac){
//			String sql = "select * from nms_macconfig where mac='" + mac + "'";
//			return findByCriteria(sql);
//		}
		
		  /**
		    * 按ID找一条记录
		    */
		   public LsfClassNode lsffindid(String nodeid)
		   {
			   LsfClassNode vo = null;
		       try
			   {
				   rs = conn.executeQuery("select * from  lsf_class_node where nodeid='" + nodeid+"'"); 
				   if(rs.next())
				       vo = (LsfClassNode) loadFromRS2(rs);
			   }    
			   catch(Exception ex)
			   {
				   //ex.printStackTrace();
				   SysLogger.error("Lsfclassdao.findByID()",ex);
			   }finally{
				   if(rs != null){
					   try{
						   rs.close();
					   }catch(Exception e){
					   }
				   }
			   }
		       return vo;
		   }
		/**
		  * 
		  * @param id
		  * @return
		  * @modify nielin
		  */
		public boolean deleteAll(){
			String sql = "delete from lsf_class_node";
			return saveOrUpdate(sql);
		}
		
		/**
		 * 
		 * @param mac
		 * @return
		 * @add nielin
		 */
		public boolean saveBatch(List list){
			boolean result = false;
			if(list!=null && list.size()>0){
				try {
					for(int i = 0 ; i < list.size(); i++){
						LsfClassNode vo = (LsfClassNode)list.get(i);
						StringBuffer sql = new StringBuffer(100);
						sql.append("insert into lsf_class(classid,nodeid,afunms,logflg,jid)values('");
						sql.append(vo.getNodeid());
						sql.append("','");
						sql.append(vo.getClass_id());
						sql.append("','");
						sql.append(vo.getEnable());
						sql.append("','");
						sql.append(vo.getLogflg());	
						sql.append("','");
						sql.append(vo.getJid());	
						sql.append("')");
						conn.addBatch(sql.toString());
					}
					conn.executeBatch();
					result = true;
				} catch (RuntimeException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					result = false;
				}
			}
			
			return result;
		}
	
	
		
		
		 /**
		    * 有条件分页显示
		    */
		   public List listByPage2(int curpage,int perpage)
		   {
			   List list = new ArrayList();	   
			   try 
			   {	
				   
				   
				   String sql="select b.classid,a.classname,b.nodeid,b.enable,b.logflg,b.jid,c.ip_address,c.alias,c.type from " +
			   		"lsf_class a,lsf_class_node b,topo_host_node c " + "where a.classid=b.classid and b.nodeid=c.id";
				   String sqlcount="select count(*) from " +
			   		"lsf_class a,lsf_class_node b,topo_host_node c " + "where a.classid=b.classid and b.nodeid=c.id";
				   
				  // System.out.println("===sqlll======="+sql);
				   
				   rs = conn.executeQuery(sqlcount);
				   if(rs.next())
					   jspPage = new JspPage(perpage,curpage,rs.getInt(1));

				  rs = conn.executeQuery(sql );
				   //SysLogger.info("select * from " + table + " " + where );
				   //System.out.println("================================================================22=============================");
				   
				   int loop = 0;
				   while(rs.next())
				   {
					 //  System.out.println("===============11================"+rs.getString("ip_address"));
					   
					  loop++;
					  if(loop<jspPage.getMinNum()) continue;
					  list.add(loadFromRS(rs));
					  if(loop==jspPage.getMaxNum()) break;
				   }
			   } 
			   catch (Exception e) 
			   {
				   SysLogger.error("BaseDao.listByPage()",e);
				   list = null;
			   }
			   finally
			   {
				   if(rs != null){
					   try{
						   rs.close();
					   }catch(Exception e){
					   }
				   }
				   conn.close();
			   }
			   return list;
		   }

		
		

	
	
	

}
