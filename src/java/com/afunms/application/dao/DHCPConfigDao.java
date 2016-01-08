/**
 * <p>Description: app_db_node</p>
 * <p>Company:dhcc.com</p>
 * @author miiwill
 * @project afunms
 * @date 2007-1-7
 */

package com.afunms.application.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import com.afunms.application.model.DHCPConfig;
import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.util.CreateTableManager;
import com.afunms.common.util.DBManager;
import com.afunms.common.util.SysLogger;
import com.afunms.common.util.SysUtil;
import com.afunms.common.util.SystemConstant;
import com.afunms.polling.om.Pingcollectdata;

public class DHCPConfigDao extends BaseDao implements DaoInterface {

	public DHCPConfigDao() {
		super("nms_dhcpconfig");
	}
	public boolean delete(String []ids){
		if(ids != null && ids.length>0){
			for(int i=0;i<ids.length;i++){
				delete(ids[i]);
			}
		}
		return true;
		//return super.delete(ids);
	}
	
	   public boolean delete(String id)
	   {
		   boolean result = false;
		   try
		   {
			   DHCPConfig pvo = (DHCPConfig)findByID(id+"");
			   String ipstr = pvo.getIpAddress();
//				String ip1 ="",ip2="",ip3="",ip4="";
//				String[] ipdot = ipstr.split(".");	
//				String tempStr = "";
//				String allipstr = "";
//				if (ipstr.indexOf(".")>0){
//					ip1=ipstr.substring(0,ipstr.indexOf("."));
//					ip4=ipstr.substring(ipstr.lastIndexOf(".")+1,ipstr.length());			
//					tempStr = ipstr.substring(ipstr.indexOf(".")+1,ipstr.lastIndexOf("."));
//				}
//				ip2=tempStr.substring(0,tempStr.indexOf("."));
//				ip3=tempStr.substring(tempStr.indexOf(".")+1,tempStr.length());
//				allipstr=ip1+ip2+ip3+ip4;
			   String allipstr = SysUtil.doip(ipstr);

				CreateTableManager ctable = new CreateTableManager();
	         
				//conn = new DBManager();
	  			ctable.deleteTable(conn,"dhcpping",allipstr,"dhcpping");//Ping
	  			ctable.deleteTable(conn,"dhcpphour",allipstr,"dhcpphour");//Ping
	  			ctable.deleteTable(conn,"dhcppday",allipstr,"dhcppday");//Ping             	
			   conn.addBatch("delete from nms_dhcpconfig where id=" + id);
			   conn.executeBatch();
			   result = true;
		   }
		   catch(Exception e)
		   {
			   SysLogger.error("DHCPConfigDao.delete()",e); 
		   }
		   finally
		   {
			   //conn.close();
		   }
		   return result;
	   }
	public List getByFlag(Integer flag){
		StringBuffer sql = new StringBuffer();
		sql.append("select * from nms_dhcpconfig where mon_flag= ");
		sql.append(flag);
		return findByCriteria(sql.toString());
	}
	@Override
	public BaseVo loadFromRS(ResultSet rs) {
		
		DHCPConfig vo=new DHCPConfig();
		
		try {
			vo.setId(rs.getInt("id"));
			vo.setAlias(rs.getString("name"));
			vo.setIpAddress(rs.getString("ipaddress"));
			vo.setCommunity(rs.getString("community"));
			vo.setMon_flag(rs.getInt("mon_flag"));
			vo.setNetid(rs.getString("netid"));
			vo.setSupperid(rs.getInt("supperid"));
			vo.setDhcptype(rs.getString("dhcptype"));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	
	
	return vo;
	}

public boolean save(BaseVo vo) {
		
		boolean flag = true;
		
		DHCPConfig vo1=(DHCPConfig)vo;
		StringBuffer sql=new StringBuffer();
		sql.append("insert into nms_dhcpconfig(id,name,ipaddress,community,mon_flag,netid,supperid,dhcptype) values(");
		sql.append(vo1.getId());
		sql.append(",'");
		sql.append(vo1.getAlias());
		sql.append("','");
		sql.append(vo1.getIpAddress());
		sql.append("','");
		sql.append(vo1.getCommunity());
		sql.append("','");
		sql.append(vo1.getMon_flag());
		sql.append("','");
		sql.append(vo1.getNetid());
		sql.append("','");
		sql.append(vo1.getSupperid());
		sql.append("','");
		sql.append(vo1.getDhcptype());
		sql.append("')");
		 try{
			 SysLogger.info(sql.toString());
			saveOrUpdate(sql.toString());
			CreateTableManager ctable = new CreateTableManager();         
			//测试生成表
			String ip = vo1.getIpAddress();
//			String ip1 ="",ip2="",ip3="",ip4="";
//			String[] ipdot = ip.split(".");	
//			String tempStr = "";
//			String allipstr = "";
//			if (ip.indexOf(".")>0){
//				ip1=ip.substring(0,ip.indexOf("."));
//				ip4=ip.substring(ip.lastIndexOf(".")+1,ip.length());			
//				tempStr = ip.substring(ip.indexOf(".")+1,ip.lastIndexOf("."));
//			}
//			ip2=tempStr.substring(0,tempStr.indexOf("."));
//			ip3=tempStr.substring(tempStr.indexOf(".")+1,tempStr.length());
//			allipstr=ip1+ip2+ip3+ip4;
			String allipstr = SysUtil.doip(ip);
			conn = new DBManager();
				ctable.createTable(conn,"dhcpping",allipstr,"dhcpping");//Ping
				ctable.createTable(conn,"dhcpphour",allipstr,"dhcpphour");//Ping
				ctable.createTable(conn,"dhcppday",allipstr,"dhcppday");//Ping   
	     
		   }catch(Exception e){
			   e.printStackTrace();
			   flag = false;
		   }finally{
				try{
					conn.executeBatch();
				}catch(Exception e){
					
				}
			   conn.close();
		   }
		   return flag;
	}

	public boolean update(BaseVo vo) {
		
		 boolean flag = true;
	
		 DHCPConfig vo1=(DHCPConfig)vo;
		 DHCPConfig pvo = (DHCPConfig)findByID(vo1.getId()+"");
		
		StringBuffer sql=new StringBuffer();
		sql.append("update nms_dhcpconfig set name='");
		sql.append(vo1.getAlias());
		sql.append("',ipaddress='");
		sql.append(vo1.getIpAddress());
		sql.append("',community='");
		sql.append(vo1.getCommunity());
		sql.append("',mon_flag='");
		sql.append(vo1.getMon_flag());
		sql.append("',netid='");
		sql.append(vo1.getNetid());
		sql.append("',supperid='");
		sql.append(vo1.getSupperid());
		sql.append("',dhcptype='");
		sql.append(vo1.getDhcptype());
		sql.append("' where id="+vo1.getId());

		try {
				
		    		saveOrUpdate(sql.toString());
				
				if (!vo1.getIpAddress().equals(pvo.getIpAddress())){
		           	//修改了IP
					//若IP地址发生改变,先把表删除，然后在重新建立
						String ipstr = pvo.getIpAddress();
//						String ip1 ="",ip2="",ip3="",ip4="";
//						String[] ipdot = ipstr.split(".");	
//						String tempStr = "";
//						String allipstr = "";
//						if (ipstr.indexOf(".")>0){
//							ip1=ipstr.substring(0,ipstr.indexOf("."));
//							ip4=ipstr.substring(ipstr.lastIndexOf(".")+1,ipstr.length());			
//							tempStr = ipstr.substring(ipstr.indexOf(".")+1,ipstr.lastIndexOf("."));
//						}
//						ip2=tempStr.substring(0,tempStr.indexOf("."));
//						ip3=tempStr.substring(tempStr.indexOf(".")+1,tempStr.length());
//						allipstr=ip1+ip2+ip3+ip4;
						String allipstr = SysUtil.doip(ipstr);

						CreateTableManager ctable = new CreateTableManager();
		               
		            	conn = new DBManager();
		       			ctable.deleteTable(conn,"dhcpping",allipstr,"dhcpping");//Ping
		       			//conn = new DBManager();
		       			ctable.deleteTable(conn,"dhcpphour",allipstr,"dhcpphour");//Ping
		       			//conn = new DBManager();
		       			ctable.deleteTable(conn,"dhcppday",allipstr,"dhcppday");//Ping    
		                 			               
		           	
						//测试生成表
						String ip = vo1.getIpAddress();
//						ip1 ="";ip2="";ip3="";ip4="";
//						ipdot = ip.split(".");	
//						tempStr = "";
//						allipstr = "";
//						if (ip.indexOf(".")>0){
//							ip1=ip.substring(0,ip.indexOf("."));
//							ip4=ip.substring(ip.lastIndexOf(".")+1,ip.length());			
//							tempStr = ip.substring(ip.indexOf(".")+1,ip.lastIndexOf("."));
//						}
//						ip2=tempStr.substring(0,tempStr.indexOf("."));
//						ip3=tempStr.substring(tempStr.indexOf(".")+1,tempStr.length());
//						allipstr=ip1+ip2+ip3+ip4;
						allipstr = SysUtil.doip(ip);

						ctable = new CreateTableManager();
			            	//conn = new DBManager();
			    			ctable.createTable(conn,"dhcpping",allipstr,"dhcpping");//Ping
			    			//conn = new DBManager();
			    			ctable.createTable(conn,"dhcpphour",allipstr,"dhcpphour");//Ping
			    			//conn = new DBManager();
			    			ctable.createTable(conn,"dhcppday",allipstr,"dhcppday");//Ping
		           }
				
			} catch (Exception e) {
					flag=false;
				e.printStackTrace();
			}finally{
				try{
					conn.executeBatch();
				}catch(Exception e){
					
				}
				conn.close();
			}
			return flag;	   
	}
	
	//处理Ping得到的数据，放到历史表里
	public synchronized boolean createHostData(Vector pingdataV,DHCPConfig dhcpconf) {
		if (pingdataV == null )
			return false;	
		try{	
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");	
			for(int i=0;i<pingdataV.size();i++){
				Pingcollectdata pingdata = (Pingcollectdata)pingdataV.get(i);
				String ip = pingdata.getIpaddress();				
				if (pingdata.getRestype().equals("dynamic")) {						
					String allipstr = "";
					allipstr = SysUtil.doip(ip);
					Calendar tempCal = (Calendar)pingdata.getCollecttime();							
					Date cc = tempCal.getTime();
					String time = sdf.format(cc);
					String tablename = "";

					tablename = "dhcpping"+allipstr;
					String sql="";
					if("mysql".equalsIgnoreCase(SystemConstant.DBType)){
						sql = "insert into "+tablename+"(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) "
						+"values('"+ip+"','"+pingdata.getRestype()+"','"+pingdata.getCategory()+"','"+pingdata.getEntity()+"','"
						+pingdata.getSubentity()+"','"+pingdata.getUnit()+"','"+pingdata.getChname()+"','"+pingdata.getBak()+"',"
						+pingdata.getCount()+",'"+pingdata.getThevalue()+"','"+time+"')";
					}else if("oracle".equalsIgnoreCase(SystemConstant.DBType)){
						sql = "insert into "+tablename+"(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) "
						+"values('"+ip+"','"+pingdata.getRestype()+"','"+pingdata.getCategory()+"','"+pingdata.getEntity()+"','"
						+pingdata.getSubentity()+"','"+pingdata.getUnit()+"','"+pingdata.getChname()+"','"+pingdata.getBak()+"',"
						+pingdata.getCount()+",'"+pingdata.getThevalue()+"',to_date('"+time+"','YYYY-MM-DD HH24:MI:SS'))";
					}				
					conn.executeUpdate(sql);
																									
				}
				
//				// 进行PING操作检查
//				//Mail mail = (Mail)PollingEngine.getInstance().getMailByID(mailconfig.getId());
//				DHCP node = (DHCP) PollingEngine.getInstance().getDHCPByID(dhcpconf.getId());
//				if (pingdata.getSubentity().equalsIgnoreCase("ConnectUtilization")) {
//					// 连通率进行判断
//					AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
//					NodeGatherIndicators alarmIndicatorsNode = new NodeGatherIndicators();
//					List list = alarmIndicatorsUtil.getAlarmInicatorsThresholdForNode(String.valueOf(node.getId()), "service", "dhcp");
//					for (int k = 0; k < list.size(); k++) {
//						AlarmIndicatorsNode _alarmIndicatorsNode = (AlarmIndicatorsNode) list.get(k);
//						if ("1".equals(_alarmIndicatorsNode.getEnabled())) {
//							if (_alarmIndicatorsNode.getName().equalsIgnoreCase("ping")) {
//								CheckEventUtil checkeventutil = new CheckEventUtil();
//								// SysLogger.info(_alarmIndicatorsNode.getName()+"=====_alarmIndicatorsNode.getName()=========");
//								checkeventutil.checkEvent(node, _alarmIndicatorsNode, pingdata.getThevalue());
//							}
//						}
//					}
//
//				}
			}				
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			conn.close();
			
		}
		return true;
	}
	
	
//	//处理Ping得到的数据，放到历史表里
//	public synchronized boolean createHostData(Pingcollectdata pingdata) {
//		if (pingdata == null )
//			return false;	
//		try{			
//				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");			
//				String ip = pingdata.getIpaddress();				
//				if (pingdata.getRestype().equals("dynamic")) {						
//					String allipstr = "";
//					allipstr = SysUtil.doip(ip);
//					Calendar tempCal = (Calendar)pingdata.getCollecttime();							
//					Date cc = tempCal.getTime();
//					String time = sdf.format(cc);
//					String tablename = "";
//
//					tablename = "dhcpping"+allipstr;
//					String sql="";
//					if("mysql".equalsIgnoreCase(SystemConstant.DBType)){
//						sql = "insert into "+tablename+"(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) "
//						+"values('"+ip+"','"+pingdata.getRestype()+"','"+pingdata.getCategory()+"','"+pingdata.getEntity()+"','"
//						+pingdata.getSubentity()+"','"+pingdata.getUnit()+"','"+pingdata.getChname()+"','"+pingdata.getBak()+"',"
//						+pingdata.getCount()+",'"+pingdata.getThevalue()+"','"+time+"')";
//					}else if("oracle".equalsIgnoreCase(SystemConstant.DBType)){
//						sql = "insert into "+tablename+"(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) "
//						+"values('"+ip+"','"+pingdata.getRestype()+"','"+pingdata.getCategory()+"','"+pingdata.getEntity()+"','"
//						+pingdata.getSubentity()+"','"+pingdata.getUnit()+"','"+pingdata.getChname()+"','"+pingdata.getBak()+"',"
//						+pingdata.getCount()+",'"+pingdata.getThevalue()+"',to_date('"+time+"','YYYY-MM-DD HH24:MI:SS'))";
//					}				
//					conn.executeUpdate(sql);
//																									
//				}
//				
//				// 进行PING操作检查
//				Host node = (Host) PollingEngine.getInstance().getNodeByIP(pingdata.getIpaddress());
//				if (pingdata.getSubentity().equalsIgnoreCase("ConnectUtilization")) {
//					// 连通率进行判断
//					AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
//					NodeGatherIndicators alarmIndicatorsNode = new NodeGatherIndicators();
//					List list = alarmIndicatorsUtil.getAlarmInicatorsThresholdForNode(String.valueOf(node.getId()), "service", "ciscodhcp");
//					for (int k = 0; k < list.size(); k++) {
//						AlarmIndicatorsNode _alarmIndicatorsNode = (AlarmIndicatorsNode) list.get(k);
//						if ("1".equals(_alarmIndicatorsNode.getEnabled())) {
//							if (_alarmIndicatorsNode.getName().equalsIgnoreCase("ping")) {
//								CheckEventUtil checkeventutil = new CheckEventUtil();
//								// SysLogger.info(_alarmIndicatorsNode.getName()+"=====_alarmIndicatorsNode.getName()=========");
//								checkeventutil.checkEvent(node, _alarmIndicatorsNode, pingdata.getThevalue());
//							}
//						}
//					}
//
//				}
//				
//		} catch (Exception e) {
//			e.printStackTrace();
//			return false;
//		} finally {
//			conn.close();
//			
//		}
//		return true;
//	}
	   public List getDHCPByBID(Vector bids){
		   List rlist = new ArrayList();
		   StringBuffer sql = new StringBuffer();
		   String wstr = "";
		   if(bids != null && bids.size()>0){
			   for(int i=0;i<bids.size();i++){
				   if(wstr.trim().length()==0){
					   wstr = wstr+" where ( netid like '%,"+bids.get(i)+",%' "; 
				   }else{
					   wstr = wstr+" or netid like '%,"+bids.get(i)+",%' ";
				   }
				   
			   }
			   wstr=wstr+")";
		   }
		   sql.append("select * from nms_dhcpconfig "+wstr);
		   //SysLogger.info(sql.toString());
		   return findByCriteria(sql.toString());
	   }
	   
	   //zhushouzhi-----------------
	  	   public int getidByIp(String ip) 
	  		{
	  			String string = "select id from nms_dhcpconfig where ipaddress ="+"'"+ip+"'";
	  			int id = 0;
	  			ResultSet rSet = null;
	  			rSet = conn.executeQuery(string);
	  			 try {
	  				while(rSet.next())
	  				 {
	  					 id = rSet.getInt(1);
	  				 }
	  			} catch (SQLException e) {
	  				// TODO Auto-generated catch block
	  				e.printStackTrace();
	  			}finally{
	  				if(rSet != null){
	  	 				try{
	  	 					rSet.close();
	  	 				}catch(Exception e){
	  	 					e.printStackTrace();
	  	 				}
	  	 			}
	  				conn.close();
	  			}
	  			return id;
	  		}
	  	   
	  	   public List getidByIDS(String[] ids) 
	  		{
	  		   if(ids != null && ids.length>0){
	  			   String where = "";
	  			 for(int i=0;i<ids.length;i++){
	  				 if(i == 0){
	  					 where = where +" where id="+ids[i];
	  				 }else
	  				 where = where +" or id="+ids[i];
	  			 }
	  			StringBuffer sql = new StringBuffer();
		  		 sql.append("select * from nms_dhcpconfig "+where);
		  		 return findByCriteria(sql.toString());
	  		   }
	  		   return null;
	  		}

}