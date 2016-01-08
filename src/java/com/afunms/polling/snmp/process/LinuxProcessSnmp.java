package com.afunms.polling.snmp.process;

/*
 * @author hukelei@dhcc.com.cn
 *
 */

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import com.afunms.alarm.model.AlarmIndicatorsNode;
import com.afunms.alarm.send.SendAlarmUtil;
import com.afunms.alarm.util.AlarmConstant;
import com.afunms.alarm.util.AlarmIndicatorsUtil;
import com.afunms.common.util.CheckEventUtil;
import com.afunms.common.util.ShareData;
import com.afunms.common.util.SnmpUtils;
import com.afunms.common.util.SysLogger;
import com.afunms.config.dao.NodeToBusinessDao;
import com.afunms.config.dao.ProcsDao;
import com.afunms.config.model.NodeToBusiness;
import com.afunms.config.model.Procs;
import com.afunms.event.dao.EventListDao;
import com.afunms.event.model.CheckEvent;
import com.afunms.event.model.EventList;
import com.afunms.indicators.model.NodeGatherIndicators;
import com.afunms.monitor.executor.base.SnmpMonitor;
import com.afunms.monitor.item.base.MonitoredItem;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.base.Node;
import com.afunms.polling.node.Host;
import com.afunms.polling.om.Processcollectdata;
import com.afunms.topology.model.HostNode;
import com.gatherResulttosql.HostDatatempProcessRtTosql;


/**
 * @author Administrator
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class LinuxProcessSnmp extends SnmpMonitor {
	private Hashtable sendeddata = ShareData.getProcsendeddata();
	java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	/**
	 * 
	 */
	public LinuxProcessSnmp() {
	}

	   public void collectData(Node node,MonitoredItem item){
		   
	   }
	   public void collectData(HostNode node){
		   
	   }
	/* (non-Javadoc)
	 * @see com.dhcc.webnms.host.snmp.AbstractSnmp#collectData()
	 */
	public Hashtable collect_Data(NodeGatherIndicators alarmIndicatorsNode) {
		Hashtable returnHash=new Hashtable();
		Vector processVector=new Vector();
		List cpuList = new ArrayList();
		Host host = (Host)PollingEngine.getInstance().getNodeByID(Integer.parseInt(alarmIndicatorsNode.getNodeid()));
		
		try {
			Processcollectdata processdata=new Processcollectdata();
			Calendar date=Calendar.getInstance();
			Hashtable ipAllData = (Hashtable)ShareData.getSharedata().get(host.getIpAddress());
			if(ipAllData == null)ipAllData = new Hashtable();
			
			  try{
				  SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				  com.afunms.polling.base.Node snmpnode = (com.afunms.polling.base.Node)PollingEngine.getInstance().getNodeByIP(host.getIpAddress());
				  Date cc = date.getTime();
				  String time = sdf.format(cc);
				  snmpnode.setLastTime(time);
			  }catch(Exception e){
				  
			  }
			//-------------------------------------------------------------------------------------------process start			
				try{
				
				String[] oids =                
					new String[] {      
						"1.3.6.1.2.1.25.4.2.1.1",         
						"1.3.6.1.2.1.25.4.2.1.2" ,
						"1.3.6.1.2.1.25.4.2.1.5",
						"1.3.6.1.2.1.25.4.2.1.6" ,
						"1.3.6.1.2.1.25.4.2.1.7",
						"1.3.6.1.2.1.25.5.1.1.2" ,
						"1.3.6.1.2.1.25.5.1.1.1" ,
						
						};
				String[] oids1 =                
						new String[] {               
							"1.3.6.1.2.1.25.2.2" };
				String[][] valueArray1 = null;
				try {
					valueArray1 = SnmpUtils.getTableData(host.getIpAddress(), host.getCommunity(), oids1, host.getSnmpversion(), 3, 1000*30);
				} catch(Exception e){
					valueArray1 = null;
					SysLogger.error(host.getIpAddress() + "_WindowsSnmp");
				}
				int allMemorySize=0;
				if(valueArray1 != null){
					for(int i=0;i<valueArray1.length;i++){
						String svb0 = valueArray1[i][0];
						if(svb0 == null)continue;
						allMemorySize=Integer.parseInt(svb0);
					}
				}

				String[][] valueArray = null;
				try {
					valueArray = snmp.getTableData(host.getIpAddress(),host.getCommunity(),oids);
				} catch(Exception e){
					valueArray = null;
					SysLogger.error(host.getIpAddress() + "_WindowsSnmp");
				}
				Vector vecIndex=new Vector();
				
				
				//List procslist = procsManager.loadByIp(host.getIpAddress());
				Hashtable procshash = new Hashtable();
				Vector procsV = new Vector();
				
				if(valueArray != null){
					for(int i=0;i<valueArray.length;i++){
						if(allMemorySize!=0){
							String vbstring0=valueArray[i][0];
							String vbstring1=valueArray[i][1];
							String vbstring2=valueArray[i][2];
							String vbstring3=valueArray[i][3];
							String vbstring4=valueArray[i][4];
							String vbstring5=valueArray[i][5];
							String vbstring6=valueArray[i][6];
							String processIndex=vbstring0.trim();
							
							float value=0.0f;
							value=Integer.parseInt(vbstring5.trim())*100.0f/allMemorySize;
							
							String processName=vbstring1.trim();
				
							processdata=new Processcollectdata();
							processdata.setIpaddress(host.getIpAddress());
							processdata.setCollecttime(date);
							processdata.setCategory("Process");
							processdata.setEntity("MemoryUtilization");
							processdata.setSubentity(processIndex);
							processdata.setRestype("dynamic");
							processdata.setUnit("%");
							processdata.setThevalue(Float.toString(value));
							processdata.setChname(processName);
							processVector.addElement(processdata);	
							
							String processMemory=vbstring5.trim();
							processdata=new Processcollectdata();
							processdata.setIpaddress(host.getIpAddress());
							processdata.setCollecttime(date);
							processdata.setCategory("Process");
							processdata.setEntity("Memory");
							processdata.setSubentity(processIndex);
							processdata.setRestype("static");
							processdata.setUnit("K");
							processdata.setThevalue(processMemory);
							processdata.setChname(processName);
							processVector.addElement(processdata);
							
							
							procshash.put(processName.trim(), processName.trim());//把所有的进程放入到列表中
							
							
							String processType=vbstring3.trim();
							processdata=new Processcollectdata();
							processdata.setIpaddress(host.getIpAddress());
							processdata.setCollecttime(date);
							processdata.setCategory("Process");
							processdata.setEntity("Type");
							processdata.setSubentity(processIndex);
							processdata.setRestype("static");
							processdata.setUnit(" ");
							processdata.setThevalue(HOST_hrSWRun_hrSWRunType.get(processType).toString());
							processdata.setChname(processName);
							processVector.addElement(processdata);
							
							String processPath=vbstring2.trim();
							processdata=new Processcollectdata();
							processdata.setIpaddress(host.getIpAddress());
							processdata.setCollecttime(date);
							processdata.setCategory("Process");
							processdata.setEntity("Path");
							processdata.setSubentity(processIndex);
							processdata.setRestype("static");
							processdata.setUnit(" ");
							processdata.setThevalue(processPath);
							processdata.setChname(processName);
							processVector.addElement(processdata);
							
							String processStatus=vbstring4.trim();
							processdata=new Processcollectdata();
							processdata.setIpaddress(host.getIpAddress());
							processdata.setCollecttime(date);
							processdata.setCategory("Process");
							processdata.setEntity("Status");
							processdata.setSubentity(processIndex);
							processdata.setRestype("static");
							processdata.setUnit(" ");
							processdata.setThevalue(HOST_hrSWRun_hrSWRunStatus.get(processStatus).toString());
							processdata.setChname(processName);
							processVector.addElement(processdata);
							
							
							processdata=new Processcollectdata();
							processdata.setIpaddress(host.getIpAddress());
							processdata.setCollecttime(date);
							processdata.setCategory("Process");
							processdata.setEntity("Name");
							processdata.setSubentity(processIndex);
							processdata.setRestype("static");
							processdata.setUnit(" ");
							processdata.setThevalue(processName);
							processVector.addElement(processdata);
							
							String processCpu=vbstring6.trim();
							processdata=new Processcollectdata();
							processdata.setIpaddress(host.getIpAddress());
							processdata.setCollecttime(date);
							processdata.setCategory("Process");
							processdata.setEntity("CpuTime");
							processdata.setSubentity(processIndex);
							processdata.setRestype("static");
							processdata.setUnit("秒");
							processdata.setThevalue(Integer.toString((Integer.parseInt(processCpu)/100)));
							processdata.setChname(processName);
							processVector.addElement(processdata);
							
							//判断是否有需要监视的进程，若取得的列表里包含监视进程，则从Vector里去掉
//							if (procsV !=null && procsV.size()>0){
//								if (procsV.contains(processName)){
//									//procshash.remove(vbstring1);						
//									procsV.remove(processName);
//									//判断已经发送的进程短信列表里是否有该进程,若有,则从已发送列表里去掉该短信信息
//									if(sendeddata.containsKey(host+":"+processName)){
//										sendeddata.remove(host+":"+processName);
//									}
//									//判断进程丢失列表里是否有该进程,若有,则从该列表里去掉该信息
//						     		Hashtable iplostprocdata = (Hashtable)ShareData.getLostprocdata(host.getIpAddress());
//						     		if(iplostprocdata == null)iplostprocdata = new Hashtable();
//						     		if (iplostprocdata.containsKey(processName)){
//						     			iplostprocdata.remove(processName);
//						     			ShareData.setLostprocdata(host.getIpAddress(), iplostprocdata);
//						     		}
//						     		
//									
//								}
//							}
						}else{
							throw new Exception("Process is 0");
						}
					}
				}
				
				//判断ProcsV里还有没有需要监视的进程，若有，则说明当前没有启动该进程，则用命令重新启动该进程，同时写入事件
				procsV=this.Getipprocesslist(host.getIpAddress());//从内存中得到需要检查的进程对象
				//System.out.println("========================开始进程比较==========================");
				
				
				
				 //判断ProcsV里还有没有需要监视的进程，若有，则说明当前没有启动该进程，则用命令重新启动该进程，同时写入事件
			     if (procsV !=null && procsV.size()>0){
				     	for(int i=0;i<procsV.size();i++){	
					         
				     		Hashtable proc=(Hashtable)procsV.get(i);
				     		
				     		 //System.out.println("==procname="+proc.get("procname"));
				     		 //System.out.println(procshash.toString());
				     		 
				     		String eventContent="";
				     		boolean alarmflg=false;
				     		if(proc.get("wbstatus").equals("0"))
				     		{//白名单
				     			//System.out.println("==白==");
				     			
				     			
				     			if(!procshash.containsKey(proc.get("procname")))
				     			{//没有找到对应的进程长生告警
				     				eventContent=proc.get("procname")+"("+host.getIpAddress()+")进程丢失";
				     				alarmflg=true;
				     				//System.out.println("===alarmflg===="+alarmflg);
				     				
				     			}else 
				     			{//有进程但是是僵尸进程也告警
				     				
				     			}
				     			
				     		}else
				     		{//黑名单
				     			//System.out.println("==黑==");
				     			if(procshash.containsKey(proc.get("procname")))
				     			{//没有找到对应的进程长生告警
				     				eventContent=proc.get("procname")+"("+host.getIpAddress()+")黑名单进程异常";
				     				alarmflg=true;
				     			}
				     			
				     		}
				     		
				     	
				     		try{
				     			  
				     			
				     			if(alarmflg)
				     			{
					    		EventList eventlist = new EventList();
					    		eventlist.setEventtype("poll");
					    		eventlist.setEventlocation(host.getSysLocation());
					    		eventlist.setContent(eventContent);
					    		eventlist.setLevel1(Integer.parseInt((String )proc.get("alarmLevel")));
					    		eventlist.setManagesign(0);
					    		eventlist.setBak("");
					    		eventlist.setRecordtime(Calendar.getInstance());
					    		eventlist.setReportman("系统轮询");
					    		eventlist.setBusinessid(host.getBid());
					    		eventlist.setNodeid(host.getId());
					    		eventlist.setOid(0);
					    		eventlist.setSubtype("host");
					    		eventlist.setSubentity("proc");
					    		eventlist.setIpaddress(host.getIpAddress());
								
								  try{
										AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
										List list = alarmIndicatorsUtil.getAlarmInicatorsThresholdForNode(String.valueOf(host.getId()), AlarmConstant.TYPE_HOST, "aix","process");
										for(int z = 0 ; z < list.size() ; z ++){
											
											//System.out.println("========阀值个数==="+list.size());
											AlarmIndicatorsNode alarmIndicatorsnode = (AlarmIndicatorsNode)list.get(z);
											
																		
											CheckEvent checkevent=new CheckEvent();
											
											checkevent.setAlarmlevel(Integer.parseInt((String )proc.get("alarmLevel")));
											checkevent.setName(proc.get("nodeid")+":host:proce:"+proc.get("procname"));
											SendAlarmUtil sendAlarmUtil = new SendAlarmUtil();
											
								    		//发送告警
								    		sendAlarmUtil.sendAlarmNoIndicatorOther(checkevent, eventlist,alarmIndicatorsnode);//这个地方写什么
											
											//}
										}
								    }catch(Exception e){
								    	e.printStackTrace();
								    }
								}
				     		}catch(Exception e){
				     			e.printStackTrace();
				     		}
				     		
				     		
				     	}
				     }
				
				}
				catch(Exception e){
		
					//e.printStackTrace();
				}
				//-------------------------------------------------------------------------------------------process end
			}catch(Exception e){
				//returnHash=null;
				//e.printStackTrace();
				//return null;
			}
		
//		Hashtable ipAllData = (Hashtable)ShareData.getSharedata().get(host.getIpAddress());
//		if(ipAllData == null)ipAllData = new Hashtable();
//		ipAllData.put("process",processVector);
//	    ShareData.getSharedata().put(host.getIpAddress(), ipAllData);
//	    returnHash.put("process", processVector);
	    
		if (!(ShareData.getSharedata().containsKey(host.getIpAddress()))) {
			Hashtable ipAllData = new Hashtable();
			if (ipAllData == null) ipAllData = new Hashtable();
			if (processVector != null && processVector.size() > 0) ipAllData.put("process", processVector);
				ShareData.getSharedata().put(host.getIpAddress(), ipAllData);
			} 
		else{
			if (processVector != null && processVector.size() > 0) ((Hashtable) ShareData.getSharedata().get(host.getIpAddress())) .put("process", processVector);
		}	
	    returnHash.put("process",processVector);
	    
//	    ipAllData=null;
	    processVector=null;
	    
	    
	    List proEventList = new ArrayList();
       // Hashtable sharedata = ShareData.getSharedata();
		//Hashtable datahash = (Hashtable)sharedata.get(host.getIpAddress());
		boolean alarm = false;
		/*
		 * nielin add 2010-08-18
		 *
		 * 创建进程组告警
		 * 
		 * start ===============================
		 */
		try{
			if(processVector != null && processVector.size()>0){
				AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
				List list = alarmIndicatorsUtil.getAlarmInicatorsThresholdForNode(host.getId()+"", "host", "windows");
				AlarmIndicatorsNode alarmIndicatorsNode2 = null;
				if(list == null){
					for(int i = 0 ; i < list.size() ; i++){
						AlarmIndicatorsNode alarmIndicatorsNode2_per = (AlarmIndicatorsNode)list.get(i);
						if(alarmIndicatorsNode2_per!=null && "process".equals(alarmIndicatorsNode2_per.getName())){
							alarmIndicatorsNode2 = alarmIndicatorsNode2_per;
							break;
						}
					}
					CheckEventUtil checkutil = new CheckEventUtil();
					proEventList = checkutil.createProcessGroupEventList(host.getIpAddress() , processVector , alarmIndicatorsNode2);
				}
			}
		}catch(Exception e){
			
		}
		/*
		 * nielin add 2010-08-18
		 *
		 * 创建进程组告警
		 * 
		 * end ===============================
		 */
		if(proEventList != null && proEventList.size()>0){
			alarm = true;
		}
		if(alarm)
		{	
			Host node = (Host) PollingEngine.getInstance().getNodeByID(host.getId());
			StringBuffer msg = new StringBuffer(200);
		    msg.append("<font color='red'>--报警信息:--</font><br>");
		    msg.append(node.getAlarmMessage().toString());
		    if(proEventList != null && proEventList.size()>0){
	        	for(int i=0;i<proEventList.size();i++){
	        		EventList eventList = (EventList)proEventList.get(i);
	        		msg.append(eventList.getContent()+"<br>");
	        		if(eventList.getLevel1() > node.getAlarmlevel()){
	        			node.setAlarmlevel(eventList.getLevel1()) ;
		    		}
	        	}
	        }
		    node.getAlarmMessage().clear();
		    node.getAlarmMessage().add(msg.toString());
		    node.setStatus(node.getAlarmlevel());
		    node.setAlarm(true);
		}
		//把结果生成sql
		HostDatatempProcessRtTosql temptosql=new HostDatatempProcessRtTosql();
		temptosql.CreateResultTosql(returnHash, host);
	    return returnHash;
	}
	
	
	/**
	 * 
	 * 从内存类表中获取当前需要匹配的进程数
	 * @param ipaddressip地址
	 * @return
	 */
	 public Vector Getipprocesslist(String ipaddress)
	 {
		 Vector list=new Vector();
		 Hashtable Vector  = new Hashtable();//进程的所有列表

		 Set set = ShareData.getProcessconfigHashtable().keySet(); // get set-view of keys
		   // get iterator
		   Iterator itr = set.iterator();
		   while (itr.hasNext()) {
		    String str = (String) itr.next();
		    if(str.indexOf(ipaddress+"-")>=0)
		    
		    	list.add( ShareData.getProcessconfigHashtable().get(str));
		   }

		  return list;
		 
	 }
	
	public void createSMS(Procs procs){
	 	Procs lastprocs = null;
	 	//建立短信	
	 	procs.setCollecttime(Calendar.getInstance());
	 	//从已经发送的短信列表里获得当前该PROC已经发送的短信
	 	lastprocs = (Procs)sendeddata.get(procs.getIpaddress()+":"+procs.getProcname());	
	 }
}





