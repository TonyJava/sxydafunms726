
package com.afunms.polling.task;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Vector;
import java.util.Hashtable;

import org.apache.commons.beanutils.BeanUtils;

import com.afunms.application.dao.TomcatDao;
import com.afunms.application.dao.WeblogicConfigDao;
import com.afunms.application.manage.WeblogicManager;
import com.afunms.application.model.WebConfig;
import com.afunms.application.model.WeblogicConfig;
import com.afunms.application.weblogicmonitor.WeblogicNormal;
import com.afunms.application.weblogicmonitor.WeblogicServer;
import com.afunms.application.weblogicmonitor.WeblogicSnmp;

import com.afunms.common.util.ShareData;
import com.afunms.event.dao.SmscontentDao;
import com.afunms.event.model.Smscontent;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.node.Host;
import com.afunms.polling.om.Pingcollectdata;
import com.afunms.polling.om.Task;



/**
 * @author Administrator
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class WeblogicDataCollector{
	/**
	 * 
	 */
	private Hashtable sendeddata = ShareData.getSendeddata();
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	public WeblogicDataCollector() {
	}

	public void collect_data(String id,Hashtable gatherHash) {
		
		WeblogicManager weblogicManager = new WeblogicManager();
		WeblogicConfig weblogicconf = null;
        try {  
        	WeblogicConfigDao configdao = new WeblogicConfigDao();
        	try{
        		weblogicconf = (WeblogicConfig)configdao.findByID(id);
        	}catch(Exception e){
        		
        	}finally{
        		configdao.close();
        	}
        	if (weblogicconf == null)return;
            int serverflag = 0;
         	WeblogicSnmp weblogicsnmp=null;
         	Hashtable sendeddata = ShareData.getSendeddata();
         	Hashtable hash = null;
         	weblogicsnmp = new WeblogicSnmp(weblogicconf.getIpAddress(),weblogicconf.getCommunity(),weblogicconf.getPortnum());
         	String ipaddress = "";
         	hash=weblogicsnmp.collectData(gatherHash);
     		if(hash == null) {
     			hash = new Hashtable();
     		} 
     		com.afunms.polling.node.Weblogic _tnode=(com.afunms.polling.node.Weblogic)PollingEngine.getInstance().getWeblogicByIP(weblogicconf.getIpAddress());
				Calendar _date=Calendar.getInstance();
				Date _cc = _date.getTime();
	 			String _tempsenddate = sdf.format(_cc);
	 			//初始化Weblogic对象的状态
				_tnode.setLastTime(_tempsenddate);
				_tnode.setAlarm(false);
				_tnode.getAlarmMessage().clear();
				_tnode.setStatus(0);
     		
			int flag = 0;
     		if(hash.get("normalValue") != null){
         		List normalValue = (List)hash.get("normalValue");
         		if(normalValue != null && normalValue.size()>0){
         			for(int i=0;i<normalValue.size();i++){
         				WeblogicNormal normal = (WeblogicNormal)normalValue.get(i);
         				if(!normal.getDomainActive().equals("2")){
         					flag = 1;
         				}
         			}
         			if(flag == 1){
         				//有不活动的域,则连通率为0
         				//依据配置判断是否需要告警
//							if (wlservers.containsKey(weblogicconf.getIpaddress()+":"+server.getServerRuntimeName()))
//								createSMS(weblogicconf,server);       
     					
     					try {
								//com.afunms.polling.node.Weblogic tc = new com.afunms.polling.node.Weblogic();
								//BeanUtils.copyProperties(tc, weblogicconf);
								//if (data_ht==null){
     							//需要增加邮件服务所在的服务器是否能连通
     							Host host = (Host)PollingEngine.getInstance().getNodeByIP(weblogicconf.getIpAddress());
     							Vector ipPingData = (Vector)ShareData.getPingdata().get(weblogicconf.getIpAddress());
     							ipaddress = weblogicconf.getIpAddress();
     							if(ipPingData != null){
     								Pingcollectdata pingdata = (Pingcollectdata)ipPingData.get(0);
     								Calendar tempCal = (Calendar)pingdata.getCollecttime();							
     								Date cc = tempCal.getTime();
     								String _time = sdf.format(cc);		
     								String lastTime = _time;
     								String pingvalue = pingdata.getThevalue();
     								if(pingvalue == null || pingvalue.trim().length()==0)pingvalue="0";
     								double pvalue = new Double(pingvalue);
     								if(pvalue == 0){
     									//主机服务器连接不上***********************************************
     									//com.afunms.polling.node.Weblogic tnode=(com.afunms.polling.node.Weblogic)PollingEngine.getInstance().getWeblogicByIP(ipaddress);
     									_tnode.setAlarm(true);
     									_tnode.setStatus(1);
     									List alarmList = _tnode.getAlarmMessage();
     									if(alarmList == null)alarmList = new ArrayList();
     									_tnode.getAlarmMessage().add("WEBLOGIC服务停止");
     						            String sysLocation = "";
     						              try{
     						            	  SmscontentDao eventdao = new SmscontentDao();
     						            	  String eventdesc = "WEBLOGIC服务("+_tnode.getAlias()+" IP:"+_tnode.getAdminIp()+")"+"的WEBLOGIC服务停止";
     						            	  eventdao.createEventWithReasion("poll",_tnode.getId()+"",_tnode.getAdminIp()+"("+_tnode.getAdminIp()+")",eventdesc,3,"weblogic","ping","所在的服务器连接不上");
     						              }catch(Exception e){
     						            	  e.printStackTrace();
     						              }
     								}else{
     									//com.afunms.polling.node.Weblogic tnode=(com.afunms.polling.node.Weblogic)PollingEngine.getInstance().getWeblogicByIP(ipaddress);
     									_tnode.setAlarm(true);
     									_tnode.setStatus(3);
     									List alarmList = _tnode.getAlarmMessage();
     									if(alarmList == null)alarmList = new ArrayList();
     									_tnode.getAlarmMessage().add("WEBLOGIC服务停止");
 	        							Pingcollectdata hostdata=null;
 	        							hostdata=new Pingcollectdata();
 	        							hostdata.setIpaddress(ipaddress);
 	        							Calendar date=Calendar.getInstance();
 	        							hostdata.setCollecttime(date);
 	        							hostdata.setCategory("WeblogicPing");
 	        							hostdata.setEntity("Utilization");
 	        							hostdata.setSubentity("ConnectUtilization");
 	        							hostdata.setRestype("dynamic");
 	        							hostdata.setUnit("%");
 	        							hostdata.setThevalue("0");	
 	        							WeblogicConfigDao weblogicconfigdao=new WeblogicConfigDao();
		        							try{
		        								weblogicconfigdao.createHostData(hostdata);	        								
		        							}catch(Exception e){
		        								e.printStackTrace();
		        							}finally{
		        								weblogicconfigdao.close();
		        							}
     								}
     								
     							}else{
     								//com.afunms.polling.node.Weblogic tnode=(com.afunms.polling.node.Weblogic)PollingEngine.getInstance().getWeblogicByIP(ipaddress);
 									_tnode.setAlarm(true);
 									_tnode.setStatus(3);
 									List alarmList = _tnode.getAlarmMessage();
 									if(alarmList == null)alarmList = new ArrayList();
 									_tnode.getAlarmMessage().add("WEBLOGIC服务停止");
	        							Pingcollectdata hostdata=null;
	        							hostdata=new Pingcollectdata();
	        							hostdata.setIpaddress(ipaddress);
	        							Calendar date=Calendar.getInstance();
	        							hostdata.setCollecttime(date);
	        							hostdata.setCategory("WeblogicPing");
	        							hostdata.setEntity("Utilization");
	        							hostdata.setSubentity("ConnectUtilization");
	        							hostdata.setRestype("dynamic");
	        							hostdata.setUnit("%");
	        							hostdata.setThevalue("0");	
	        							WeblogicConfigDao weblogicconfigdao=new WeblogicConfigDao();
	        							try{
	        								weblogicconfigdao.createHostData(hostdata);	        								
	        							}catch(Exception e){
	        								e.printStackTrace();
	        							}finally{
	        								weblogicconfigdao.close();
	        							}
     							}	        							
         						try{					
         							createSMS("weblogicDomain",weblogicconf);
         							serverflag = 1;
         						}catch(Exception e){
         							e.printStackTrace();
         						}
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
         				
         			}else{
         				//所有域都是连通的,连通率为100
         				//运行状态
     					Pingcollectdata hostdata=null;
 						hostdata=new Pingcollectdata();
 						hostdata.setIpaddress(weblogicconf.getIpAddress());
 						Calendar date=Calendar.getInstance();
 						hostdata.setCollecttime(date);
 						hostdata.setCategory("WeblogicPing");
 						hostdata.setEntity("Utilization");
 						hostdata.setSubentity("ConnectUtilization");
 						hostdata.setRestype("dynamic");
 						hostdata.setUnit("%");
 						hostdata.setThevalue("100");
 						WeblogicConfigDao weblogicconfigdao=new WeblogicConfigDao();
 						try{
 							weblogicconfigdao.createHostData(hostdata);
 							if(sendeddata.containsKey("weblogic"+":"+weblogicconf.getIpAddress()))
 								sendeddata.remove("weblogic"+":"+weblogicconf.getIpAddress());
 						}catch(Exception e){
 							e.printStackTrace();
 						}finally{
 							weblogicconfigdao.close();
 						}
         			}
         		}else {
         			
						Host host = (Host)PollingEngine.getInstance().getNodeByIP(weblogicconf.getIpAddress());
						Vector ipPingData = (Vector)ShareData.getPingdata().get(weblogicconf.getIpAddress());
						ipaddress = weblogicconf.getIpAddress();
						if(ipPingData != null){
							Pingcollectdata pingdata = (Pingcollectdata)ipPingData.get(0);
							Calendar tempCal = (Calendar)pingdata.getCollecttime();							
							Date cc = tempCal.getTime();
							String _time = sdf.format(cc);		
							String lastTime = _time;
							String pingvalue = pingdata.getThevalue();
							if(pingvalue == null || pingvalue.trim().length()==0)pingvalue="0";
							double pvalue = new Double(pingvalue);
							if(pvalue == 0){
								//主机服务器连接不上***********************************************
								com.afunms.polling.node.Weblogic tnode=(com.afunms.polling.node.Weblogic)PollingEngine.getInstance().getWeblogicByIP(ipaddress);
								tnode.setAlarm(true);
								_tnode.setStatus(1);
								List alarmList = tnode.getAlarmMessage();
								if(alarmList == null)alarmList = new ArrayList();
								tnode.getAlarmMessage().add("WEBLOGIC服务停止");
					            String sysLocation = "";
					              try{
					            	  SmscontentDao eventdao = new SmscontentDao();
					            	  String eventdesc = "WEBLOGIC服务("+tnode.getAlias()+" IP:"+tnode.getAdminIp()+")"+"的WEBLOGIC服务停止";
					            	  eventdao.createEventWithReasion("poll",tnode.getId()+"",tnode.getAdminIp()+"("+tnode.getAdminIp()+")",eventdesc,3,"weblogic","ping","所在的服务器连接不上");
					              }catch(Exception e){
					            	  e.printStackTrace();
					              }
							}else{
								com.afunms.polling.node.Weblogic tnode=(com.afunms.polling.node.Weblogic)PollingEngine.getInstance().getWeblogicByIP(ipaddress);
								tnode.setAlarm(true);
								_tnode.setStatus(3);
								List alarmList = tnode.getAlarmMessage();
								if(alarmList == null)alarmList = new ArrayList();
								tnode.getAlarmMessage().add("WEBLOGIC服务停止");
 							Pingcollectdata hostdata=null;
 							hostdata=new Pingcollectdata();
 							hostdata.setIpaddress(ipaddress);
 							Calendar date=Calendar.getInstance();
 							hostdata.setCollecttime(date);
 							hostdata.setCategory("WeblogicPing");
 							hostdata.setEntity("Utilization");
 							hostdata.setSubentity("ConnectUtilization");
 							hostdata.setRestype("dynamic");
 							hostdata.setUnit("%");
 							hostdata.setThevalue("0");	
 							WeblogicConfigDao weblogicconfigdao=new WeblogicConfigDao();
 							try{
 								weblogicconfigdao.createHostData(hostdata);	        								
 							}catch(Exception e){
 								e.printStackTrace();
 							}finally{
 								weblogicconfigdao.close();
 							}
							}
							
						}else{
							com.afunms.polling.node.Weblogic tnode=(com.afunms.polling.node.Weblogic)PollingEngine.getInstance().getWeblogicByIP(ipaddress);
							tnode.setAlarm(true);
							_tnode.setStatus(3);
							List alarmList = tnode.getAlarmMessage();
							if(alarmList == null)alarmList = new ArrayList();
							tnode.getAlarmMessage().add("WEBLOGIC服务停止");
							Pingcollectdata hostdata=null;
							hostdata=new Pingcollectdata();
							hostdata.setIpaddress(ipaddress);
							Calendar date=Calendar.getInstance();
							hostdata.setCollecttime(date);
							hostdata.setCategory("WeblogicPing");
							hostdata.setEntity("Utilization");
							hostdata.setSubentity("ConnectUtilization");
							hostdata.setRestype("dynamic");
							hostdata.setUnit("%");
							hostdata.setThevalue("0");	
							WeblogicConfigDao weblogicconfigdao=new WeblogicConfigDao();
							try{
								weblogicconfigdao.createHostData(hostdata);	        								
							}catch(Exception e){
								e.printStackTrace();
							}finally{
								weblogicconfigdao.close();
							}
						}	        							
						try{					
							createSMS("weblogicDomain",weblogicconf);
							serverflag = 1;
						}catch(Exception e){
							e.printStackTrace();
						}
         		}
     		}
     		//根据配置判断是否告警
     		if(hash.get("serverValue") != null){
         		List serverValue = (List)hash.get("serverValue");
         		if(serverValue != null && serverValue.size()>0){
         			for(int i=0;i<serverValue.size();i++){
         				WeblogicServer server = (WeblogicServer)serverValue.get(i);
       				
         				if(server.getServerRuntimeState().equals("RUNNING")){
         					//运行状态:正常
         				}else{
         					//有告警产生
         					if(serverflag ==0)
         					createSMS("weblogicServer",weblogicconf);
         				}
         			}
         		}else {
         			if(serverflag == 0)
         			createSMS("weblogicServer",weblogicconf);
         		}
     		}
				try{
					ShareData.setWeblogicdata(weblogicconf.getIpAddress(), hash);
					
				}catch(Exception ex){
					ex.printStackTrace();
				}            		
				weblogicsnmp=null;
     		hash=null;            	 
         }catch(Exception exc){
         	exc.printStackTrace();
         }
	}

	public void createSMS(String weblogic,WeblogicConfig weblogicconf){
 	//建立短信		 	
 	//从内存里获得当前这个IP的PING的值
		Calendar date=Calendar.getInstance();
		try{
			if (!sendeddata.containsKey(weblogic+":"+weblogicconf.getId())){
				//若不在，则建立短信，并且添加到发送列表里
 			Smscontent smscontent = new Smscontent();
 			String time = sdf.format(date.getTime());
 			smscontent.setLevel("2");
 			smscontent.setObjid(weblogicconf.getId()+"");
 			if("weblogicDomain".equals(weblogic)){
 				smscontent.setMessage(weblogicconf.getAlias()+" ("+weblogicconf.getIpAddress()+")"+"的Weblogic Server服务停止");
 			}
 			if("weblogicServer".equals(weblogic)){
 				smscontent.setMessage(weblogicconf.getAlias()+" ("+weblogicconf.getIpAddress()+")"+"的活动域状态为 不活动");	
 			}
 			smscontent.setRecordtime(time);
 			smscontent.setSubtype("weblogic");
 			smscontent.setSubentity("ping");
 			smscontent.setIp(weblogicconf.getIpAddress());
 			
 			//smscontent.setMessage("db&"+time+"&"+dbmonitorlist.getId()+"&"+db+"("+dbmonitorlist.getDbName()+" IP:"+dbmonitorlist.getIpAddress()+")"+"的数据库服务停止");
 			//发送短信
 			SmscontentDao smsmanager=new SmscontentDao();
 			smsmanager.sendURLSmscontent(smscontent);	
			sendeddata.put(weblogic+":"+weblogicconf.getId(),date);		 					 				
			}else{
				//若在，则从已发送短信列表里判断是否已经发送当天的短信
				Calendar formerdate =(Calendar)sendeddata.get(weblogic+":"+weblogicconf.getId());		 				
 			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
 			Date last = null;
 			Date current = null;
 			Calendar sendcalen = formerdate;
 			Date cc = sendcalen.getTime();
 			String tempsenddate = formatter.format(cc);
 			
 			Calendar currentcalen = date;
 			cc = currentcalen.getTime();
 			last = formatter.parse(tempsenddate);
 			String currentsenddate = formatter.format(cc);
 			current = formatter.parse(currentsenddate);
 			
 			long subvalue = current.getTime()-last.getTime();			 			
 			if (subvalue/(1000*60*60*24)>=1){
 				//超过一天，则再发信息
	 			Smscontent smscontent = new Smscontent();
	 			String time = sdf.format(date.getTime());
	 			smscontent.setLevel("2");
	 			smscontent.setObjid(weblogicconf.getId()+"");
	 			if("weblogicDomain".equals(weblogic)){
	 				smscontent.setMessage(weblogicconf.getAlias()+" ("+weblogicconf.getIpAddress()+")"+"的活动域状态为 不活动");
	 			}
	 			if("weblogicServer".equals(weblogic)){
	 				smscontent.setMessage(weblogicconf.getAlias()+" ("+weblogicconf.getIpAddress()+")"+"的Weblogic Server服务停止");	
	 			}
	 			smscontent.setRecordtime(time);
	 			smscontent.setSubtype(weblogic);
	 			smscontent.setSubentity("ping");
	 			smscontent.setIp(weblogicconf.getIpAddress());
	 			//smscontent.setMessage("db&"+time+"&"+dbmonitorlist.getId()+"&"+db+"("+dbmonitorlist.getDbName()+" IP:"+dbmonitorlist.getIpAddress()+")"+"的数据库服务停止");
	 			//发送短信
	 			SmscontentDao smsmanager=new SmscontentDao();
	 			smsmanager.sendURLSmscontent(smscontent);
				//修改已经发送的短信记录	
	 			sendeddata.put(weblogic+":"+weblogicconf.getId(),date);	
	 		}	
			}	 			 			 			 			 	
 	}catch(Exception e){
 		e.printStackTrace();
 	}
 }

}

