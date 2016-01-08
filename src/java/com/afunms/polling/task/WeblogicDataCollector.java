
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
	 			//��ʼ��Weblogic�����״̬
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
         				//�в������,����ͨ��Ϊ0
         				//���������ж��Ƿ���Ҫ�澯
//							if (wlservers.containsKey(weblogicconf.getIpaddress()+":"+server.getServerRuntimeName()))
//								createSMS(weblogicconf,server);       
     					
     					try {
								//com.afunms.polling.node.Weblogic tc = new com.afunms.polling.node.Weblogic();
								//BeanUtils.copyProperties(tc, weblogicconf);
								//if (data_ht==null){
     							//��Ҫ�����ʼ��������ڵķ������Ƿ�����ͨ
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
     									//�������������Ӳ���***********************************************
     									//com.afunms.polling.node.Weblogic tnode=(com.afunms.polling.node.Weblogic)PollingEngine.getInstance().getWeblogicByIP(ipaddress);
     									_tnode.setAlarm(true);
     									_tnode.setStatus(1);
     									List alarmList = _tnode.getAlarmMessage();
     									if(alarmList == null)alarmList = new ArrayList();
     									_tnode.getAlarmMessage().add("WEBLOGIC����ֹͣ");
     						            String sysLocation = "";
     						              try{
     						            	  SmscontentDao eventdao = new SmscontentDao();
     						            	  String eventdesc = "WEBLOGIC����("+_tnode.getAlias()+" IP:"+_tnode.getAdminIp()+")"+"��WEBLOGIC����ֹͣ";
     						            	  eventdao.createEventWithReasion("poll",_tnode.getId()+"",_tnode.getAdminIp()+"("+_tnode.getAdminIp()+")",eventdesc,3,"weblogic","ping","���ڵķ��������Ӳ���");
     						              }catch(Exception e){
     						            	  e.printStackTrace();
     						              }
     								}else{
     									//com.afunms.polling.node.Weblogic tnode=(com.afunms.polling.node.Weblogic)PollingEngine.getInstance().getWeblogicByIP(ipaddress);
     									_tnode.setAlarm(true);
     									_tnode.setStatus(3);
     									List alarmList = _tnode.getAlarmMessage();
     									if(alarmList == null)alarmList = new ArrayList();
     									_tnode.getAlarmMessage().add("WEBLOGIC����ֹͣ");
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
 									_tnode.getAlarmMessage().add("WEBLOGIC����ֹͣ");
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
         				//����������ͨ��,��ͨ��Ϊ100
         				//����״̬
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
								//�������������Ӳ���***********************************************
								com.afunms.polling.node.Weblogic tnode=(com.afunms.polling.node.Weblogic)PollingEngine.getInstance().getWeblogicByIP(ipaddress);
								tnode.setAlarm(true);
								_tnode.setStatus(1);
								List alarmList = tnode.getAlarmMessage();
								if(alarmList == null)alarmList = new ArrayList();
								tnode.getAlarmMessage().add("WEBLOGIC����ֹͣ");
					            String sysLocation = "";
					              try{
					            	  SmscontentDao eventdao = new SmscontentDao();
					            	  String eventdesc = "WEBLOGIC����("+tnode.getAlias()+" IP:"+tnode.getAdminIp()+")"+"��WEBLOGIC����ֹͣ";
					            	  eventdao.createEventWithReasion("poll",tnode.getId()+"",tnode.getAdminIp()+"("+tnode.getAdminIp()+")",eventdesc,3,"weblogic","ping","���ڵķ��������Ӳ���");
					              }catch(Exception e){
					            	  e.printStackTrace();
					              }
							}else{
								com.afunms.polling.node.Weblogic tnode=(com.afunms.polling.node.Weblogic)PollingEngine.getInstance().getWeblogicByIP(ipaddress);
								tnode.setAlarm(true);
								_tnode.setStatus(3);
								List alarmList = tnode.getAlarmMessage();
								if(alarmList == null)alarmList = new ArrayList();
								tnode.getAlarmMessage().add("WEBLOGIC����ֹͣ");
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
							tnode.getAlarmMessage().add("WEBLOGIC����ֹͣ");
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
     		//���������ж��Ƿ�澯
     		if(hash.get("serverValue") != null){
         		List serverValue = (List)hash.get("serverValue");
         		if(serverValue != null && serverValue.size()>0){
         			for(int i=0;i<serverValue.size();i++){
         				WeblogicServer server = (WeblogicServer)serverValue.get(i);
       				
         				if(server.getServerRuntimeState().equals("RUNNING")){
         					//����״̬:����
         				}else{
         					//�и澯����
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
 	//��������		 	
 	//���ڴ����õ�ǰ���IP��PING��ֵ
		Calendar date=Calendar.getInstance();
		try{
			if (!sendeddata.containsKey(weblogic+":"+weblogicconf.getId())){
				//�����ڣ��������ţ�������ӵ������б���
 			Smscontent smscontent = new Smscontent();
 			String time = sdf.format(date.getTime());
 			smscontent.setLevel("2");
 			smscontent.setObjid(weblogicconf.getId()+"");
 			if("weblogicDomain".equals(weblogic)){
 				smscontent.setMessage(weblogicconf.getAlias()+" ("+weblogicconf.getIpAddress()+")"+"��Weblogic Server����ֹͣ");
 			}
 			if("weblogicServer".equals(weblogic)){
 				smscontent.setMessage(weblogicconf.getAlias()+" ("+weblogicconf.getIpAddress()+")"+"�Ļ��״̬Ϊ ���");	
 			}
 			smscontent.setRecordtime(time);
 			smscontent.setSubtype("weblogic");
 			smscontent.setSubentity("ping");
 			smscontent.setIp(weblogicconf.getIpAddress());
 			
 			//smscontent.setMessage("db&"+time+"&"+dbmonitorlist.getId()+"&"+db+"("+dbmonitorlist.getDbName()+" IP:"+dbmonitorlist.getIpAddress()+")"+"�����ݿ����ֹͣ");
 			//���Ͷ���
 			SmscontentDao smsmanager=new SmscontentDao();
 			smsmanager.sendURLSmscontent(smscontent);	
			sendeddata.put(weblogic+":"+weblogicconf.getId(),date);		 					 				
			}else{
				//���ڣ�����ѷ��Ͷ����б����ж��Ƿ��Ѿ����͵���Ķ���
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
 				//����һ�죬���ٷ���Ϣ
	 			Smscontent smscontent = new Smscontent();
	 			String time = sdf.format(date.getTime());
	 			smscontent.setLevel("2");
	 			smscontent.setObjid(weblogicconf.getId()+"");
	 			if("weblogicDomain".equals(weblogic)){
	 				smscontent.setMessage(weblogicconf.getAlias()+" ("+weblogicconf.getIpAddress()+")"+"�Ļ��״̬Ϊ ���");
	 			}
	 			if("weblogicServer".equals(weblogic)){
	 				smscontent.setMessage(weblogicconf.getAlias()+" ("+weblogicconf.getIpAddress()+")"+"��Weblogic Server����ֹͣ");	
	 			}
	 			smscontent.setRecordtime(time);
	 			smscontent.setSubtype(weblogic);
	 			smscontent.setSubentity("ping");
	 			smscontent.setIp(weblogicconf.getIpAddress());
	 			//smscontent.setMessage("db&"+time+"&"+dbmonitorlist.getId()+"&"+db+"("+dbmonitorlist.getDbName()+" IP:"+dbmonitorlist.getIpAddress()+")"+"�����ݿ����ֹͣ");
	 			//���Ͷ���
	 			SmscontentDao smsmanager=new SmscontentDao();
	 			smsmanager.sendURLSmscontent(smscontent);
				//�޸��Ѿ����͵Ķ��ż�¼	
	 			sendeddata.put(weblogic+":"+weblogicconf.getId(),date);	
	 		}	
			}	 			 			 			 			 	
 	}catch(Exception e){
 		e.printStackTrace();
 	}
 }

}

