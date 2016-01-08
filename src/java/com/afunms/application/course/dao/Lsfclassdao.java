package com.afunms.application.course.dao;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.util.SysLogger;
import com.afunms.application.course.model.Lsfclass;




public class Lsfclassdao extends BaseDao implements DaoInterface{

	public Lsfclassdao() {
		super("lsf_class");
		// TODO Auto-generated constructor stub
	}

	
	
	
	@Override
	public BaseVo loadFromRS(ResultSet rs) {
		// TODO Auto-generated method stub
		Lsfclass vo = new Lsfclass();
	      try
	      {
	         vo.setClass_id(rs.getInt("classid"));
	         vo.setClass_name(rs.getString("classname"));
	         vo.setClass_pesc(rs.getString("classpesc"));
	       
	      }
	      catch(Exception ex)
	      {
	          SysLogger.error("Error in UserDAO.loadFromRS()",ex);
	          vo = null;
	      }
	      return vo;
	}
	
	
	

	
	/**
	 * 添加记录
	 */
	public boolean save(BaseVo baseVo) {
		// TODO Auto-generated method stub
		
		Lsfclass vo = (Lsfclass)baseVo;
		//Calendar tempCal = (Calendar)vo.getCollecttime();							
		//Date cc = tempCal.getTime();
		//String recordtime = sdf.format(cc);
		StringBuffer sql = new StringBuffer(100);
		sql.append("insert into lsf_class(classname,classpesc)values('");
		sql.append(vo.getClass_name());
		sql.append("','");
		sql.append(vo.getClass_pesc());
		sql.append("')");
		
		
		//System.out.println("sss============lsf="+sql.toString());
		return saveOrUpdate(sql.toString());
	}

	/**
	 * 修改mac记录
	 */
	public boolean update(BaseVo baseVo) {
		// TODO Auto-generated method stub
		
		Lsfclass vo = (Lsfclass)baseVo;

		   StringBuffer sql = new StringBuffer(200);
	       sql.append("update lsf_class set ");
	       sql.append("classname='");
	       sql.append(vo.getClass_name());
	       sql.append("',classpesc='");
	       sql.append(vo.getClass_pesc()).append("'");
	       sql.append(" where classid='").append(vo.getClass_id()).append("'");
	       //System.out.println("dddddd============lsf="+sql.toString());
	       return saveOrUpdate(sql.toString());
	}
	 /**
	    * 删除一批记录
	    */
	   public boolean lsfdelete(String[] classid)
	   {
		   boolean result = false;
		   try
		   {
		       for(int i=0;i<classid.length;i++)
		           conn.addBatch("delete from lsf_class where classid=" + classid[i]);
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
		  public boolean lsfDeleteByDistrictId(String classid)
		   {
			   boolean result = false;
			   try
			   {
				   conn.addBatch("delete  from  lsf_class where classid='" + classid+"'");
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
		   public Lsfclass lsffindid(String classid)
		   {
			   Lsfclass vo = null;
		       try
			   {
				   rs = conn.executeQuery("select * from  lsf_class where classid='" + classid+"'"); 
				   if(rs.next())
				       vo = (Lsfclass) loadFromRS(rs);
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
			String sql = "delete from lsf_class";
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
						Lsfclass vo = (Lsfclass)list.get(i);
						StringBuffer sql = new StringBuffer(100);
						sql.append("insert into lsf_class(classid,classname,classpesc)values('");
						sql.append(vo.getClass_name());
						sql.append("','");
						sql.append(vo.getClass_id());
						sql.append("','");
						sql.append(vo.getClass_pesc());	
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
	
		public List lsfloadall()
		   {
			   
			   List list=new ArrayList();
		       try
			   {
		    	  
				   rs = conn.executeQuery("select * from  lsf_class "); 
				   while(rs.next())
				   {
					   Lsfclass vo = new Lsfclass();
				       vo = (Lsfclass) loadFromRS(rs);
				       list.add(vo);
				   }
			   }    
			   catch(Exception ex)
			   {
				   //ex.printStackTrace();
				   SysLogger.error("lsfloadall()",ex);
			   }finally{
				   if(rs != null){
					   try{
						   rs.close();
					   }catch(Exception e){
					   }
				   }
			   }
		       return list;
		   }
	
	
	
		   /**
		    * 得到lsf_class
		    */
		   public String getRoleBox(int index)
		   {
		      StringBuffer sb = new StringBuffer(1000);
		      sb.append("<select size=1 name='classid' style='width:108px;'>");

		      List list =this.lsfloadall();
		      
		      
		      Lsfclass vo = null;
		      for(int i=0;i<list.size();i++)
		      {
		         vo = (Lsfclass)list.get(i);
		         
		         System.out.println("============vo============" +vo.getClass_name());
		         System.out.println("============vo============" +vo.getClass_id());
		         if(index==vo.getClass_id())
		             sb.append("<option value='" + vo.getClass_id() + "' selected>");
		         else
		             sb.append("<option value='" + vo.getClass_id() + "'>");
		         sb.append(vo.getClass_name());
		         sb.append("</option>");
		      }
		      sb.append("</select>");
		      
		      System.out.println("===================vo======================" + sb.toString());
		      return sb.toString();
		   }
		
	

}
