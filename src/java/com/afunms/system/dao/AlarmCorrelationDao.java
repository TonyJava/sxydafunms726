package com.afunms.system.dao;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.base.JspPage;
import com.afunms.common.util.SysLogger;
import com.afunms.system.vo.AlarmCorrelationVo;

public class AlarmCorrelationDao extends BaseDao implements DaoInterface{

	public AlarmCorrelationDao() {
		super("nms_alarm_correlations");
		// TODO Auto-generated constructor stub
	}

	
	
	
	@Override
	public BaseVo loadFromRS(ResultSet rs) {
		// TODO Auto-generated method stub
		AlarmCorrelationVo vo = new AlarmCorrelationVo();
	      try
	      {
	         vo.setBid(rs.getInt("bid"));
	         vo.setAip(rs.getString("aip"));
	         vo.setAalias(rs.getString("aalias"));
	         vo.setAtype(rs.getString("atype"));
	         vo.setAcategory(rs.getString("acategory"));
	         vo.setBip(rs.getString("bip"));
	         vo.setBalias(rs.getString("Balias"));
	         vo.setBtype(rs.getString("btype"));
	         vo.setAtype(rs.getString("atype"));
	         vo.setBcategory(rs.getString("bcategory"));
	        
	      }
	      catch(Exception ex)
	      {
	          SysLogger.error("Error in AlarmCorrelationDao.loadFromRS()",ex);
	          vo = null;
	      }
	      return vo;
	}
	
	
	public BaseVo loadNodeFromRS(ResultSet rs) {
		// TODO Auto-generated method stub
		AlarmCorrelationVo vo = new AlarmCorrelationVo();
	      try
	      {
	    	  vo.setId(rs.getInt("id"));
	    	  vo.setIp_address(rs.getString("ip_address"));
		      vo.setAlias(rs.getString("alias"));
		      vo.setType(rs.getString("type")); 
		      vo.setCategory(rs.getString("category"));
	      }
	      catch(Exception ex)
	      {
	          SysLogger.error("Error in AlarmCorrelationDao.loadNodeFromRS()",ex);
	          vo = null;
	      }
	      return vo;
	}
	
	public BaseVo loadNodeSaveFromRS(ResultSet rs) {
		// TODO Auto-generated method stub
		AlarmCorrelationVo vo = new AlarmCorrelationVo();
	      try
	      {
	    	  vo.setId(rs.getInt("id"));
	    	  vo.setIp_address(rs.getString("ip_address"));
		      vo.setAlias(rs.getString("alias"));
		      vo.setType(rs.getString("type")); 
		      vo.setCategory(rs.getString("category"));
	      }
	      catch(Exception ex)
	      {
	          SysLogger.error("Error in AlarmCorrelationDao.loadNodeSaveFromRS()",ex);
	          vo = null;
	      }
	      return vo;
	}
	
	
	public BaseVo loadFromRS2(ResultSet rs) {
		// TODO Auto-generated method stub
		AlarmCorrelationVo vo = new AlarmCorrelationVo();
	      try
	      {
	    	  //vo.setLocanode(rs.getInt("locanode"));
		      vo.setAip(rs.getString("aip"));
		      vo.setAalias(rs.getString("aalias"));
		      vo.setAtype(rs.getString("atype"));
		      vo.setAcategory(rs.getString("acategory"));
		      vo.setBip(rs.getString("bip"));
		      vo.setBalias(rs.getString("Balias"));
		      vo.setBtype(rs.getString("btype"));
		      vo.setBcategory(rs.getString("bcategory"));
		      //vo.setLocanode(Integer.parseInt(rs.getString("locanode")));
	      }
	      catch(Exception ex)
	      {
	          SysLogger.error("Error in AlarmCorrelationDao.loadFromRS()",ex);
	          vo = null;
	      }
	      return vo;
	}
	
	/**
	 * 添加记录
	 */
	public boolean save(BaseVo baseVo) {
		// TODO Auto-generated method stub
		
		AlarmCorrelationVo vo = (AlarmCorrelationVo)baseVo;
		StringBuffer sql = new StringBuffer(100);
		sql.append("insert into nms_alarm_correlations(fathernode,locanode)values('");
		sql.append(vo.getFathernode());
		sql.append("','");
		sql.append(vo.getLocanode());
		sql.append("')");
		return saveOrUpdate(sql.toString());
	}

	/**
	 * 修改mac记录
	 */
	public boolean update(BaseVo baseVo) {
		// TODO Auto-generated method stub
		
		AlarmCorrelationVo vo = (AlarmCorrelationVo)baseVo;

		   StringBuffer sql = new StringBuffer(200);
	       sql.append("update nms_alarm_correlations set ");
	       sql.append("fathernode='");
	       sql.append(vo.getFathernode());
	       sql.append("',locanode='");
	       sql.append(vo.getLocanode()).append("'");   
	      // System.out.println("dddddd============lsf="+sql.toString());
	       return saveOrUpdate(sql.toString());
	}
	
	
	/**
	 * 修改mac记录
	 */
	public boolean update(BaseVo baseVo,String fathernode,String locanode) {
		// TODO Auto-generated method stub
		
		AlarmCorrelationVo vo = (AlarmCorrelationVo)baseVo;

		   StringBuffer sql = new StringBuffer(200);
	       sql.append("update nms_alarm_correlations set ");
	       sql.append("fathernode='");
	       sql.append(vo.getFathernode());
           sql.append("',");
	       sql.append("',locanode='");
	       sql.append(vo.getLocanode()).append("'");
 
	       //System.out.println("=update===========lsf="+sql.toString());
	       return saveOrUpdate(sql.toString());
	}
	
	
	 /**
	    * 删除一批记录
	    */
	   public boolean alarmcorrelationdelete(String[] locanode)
	   {
		   boolean result = false;
		   try
		   {
		       for(int i=0;i<locanode.length;i++)
		       {
		    	   
		    	   System.out.println("==="+locanode[i]);
		           conn.addBatch("delete from nms_alarm_correlations where locanode='"  + locanode[i]+"'");
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
		    * 按ID找一条记录
		    */
		   public AlarmCorrelationVo alarmcorrelationfindid(String locanode)
		   {
			   AlarmCorrelationVo vo = null;
		       try
			   {
				   rs = conn.executeQuery("select * from  nms_alarm_correlations where locanode='" + locanode+"'"); 
				   if(rs.next())
				       vo = (AlarmCorrelationVo) loadFromRS2(rs);
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
		    * 有条件分页显示
		    */
		   public List listByPage2(int curpage,int perpage)
		   {
			   List list = new ArrayList();	   
			   try 
			   {	
				   
				   
				   String sql="select  a.id as aid, a.ip_address as aip ,a.alias as aalias,a.type as atype ,a.category as acategory ,b.id as bid,b.ip_address as bip ,b.alias as balias,b.type as btype,b.category as bcategory " +
				   		"      from topo_host_node a, topo_host_node b, nms_alarm_correlations c where a.id=c.fathernode and b.id=c.locanode";
				   String sqlcount="select count(*) from topo_host_node a, topo_host_node b, nms_alarm_correlations c where a.id=c.fathernode and b.id=c.locanode";
				   
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

		
		

		   
		   
		   /**
		    * 有条件分页显示
		    */
		   public List listnodeipByPage(int curpage,int perpage)
		   {
			   List list = new ArrayList();	   
			   try 
			   {	
				   
				   
				   String sql="select a.id,a.ip_address,a.alias ,a.`type`,a.`category` from topo_host_node a where  a.id not in(select locanode from nms_alarm_correlations)";
				   String sqlcount="select count(*) from topo_host_node a where  a.id not in(select locanode from nms_alarm_correlations)";
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
					  list.add(loadNodeFromRS(rs));
					  //System.out.println("---------list----------"+list);
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

	
		   /**
		    * 有条件分页显示
		    */
		   public List listnodesaveByPage(int curpage,int perpage, String id_s, String category)
		   {
			   System.out.println("===========id_s   dao================="+id_s);
			   System.out.println("===========category   dao================="+category);
			   List list = new ArrayList();	
			   String sql=null;
			   String sqlcount = null;
			   try 
			   {	
				   if(category=="4"){
					   sql="select a.id,a.ip_address,a.alias ,a.`type`,a.`category` from topo_host_node a where a.id!="+id_s+" and category not in(+"+category+" )";
					   sqlcount="select count(*) from topo_host_node a where a.id!="+id_s+" and category not in("+category+" )"; 
				   }else{
					   sql="select a.id,a.ip_address,a.alias ,a.`type`,a.`category` from topo_host_node a where a.id!="+id_s+" and category <>4";
					   sqlcount="select count(*) from topo_host_node a where a.id!="+id_s+" and category <>4"; 
				   }
				   //String sql="select a.id,a.ip_address,a.alias ,a.`type`,a.`category` from topo_host_node a where a.id!="+id_s+" and category not in(+"+category+" )";
				   //String sqlcount="select count(*) from topo_host_node a where a.id!="+id_s+" and category not in("+category+" )";
				   System.out.println("===========sql================="+sql);
				   System.out.println("===========sqlcount================="+sqlcount);
				   rs = conn.executeQuery(sqlcount);
				   if(rs.next())
					   jspPage = new JspPage(perpage,curpage,rs.getInt(1));

				  rs = conn.executeQuery(sql);
				   //SysLogger.info("select * from " + table + " " + where );
				   System.out.println("===========rs11111================="+rs);
				   
				   int loop = 0;
				   while(rs.next())
				   {
					   System.out.println("===============11================"+rs.getString("ip_address"));
					   
					  loop++;
					  if(loop<jspPage.getMinNum()) continue;
					  list.add(loadNodeSaveFromRS(rs));
					  System.out.println("---------list----2222222**********************************222------"+list.size());
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
