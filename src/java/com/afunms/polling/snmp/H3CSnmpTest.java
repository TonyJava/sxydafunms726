package com.afunms.polling.snmp;

/*
 * @author hukelei@dhcc.com.cn
 *
 */

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import montnets.SmsDao;

import com.afunms.common.util.Arith;
import com.afunms.common.util.ShareData;
import com.afunms.common.util.SnmpUtil;
import com.afunms.common.util.SysLogger;
import com.afunms.config.dao.PortconfigDao;
import com.afunms.config.model.Portconfig;
import com.afunms.event.dao.EventListDao;
import com.afunms.event.dao.SmscontentDao;
import com.afunms.event.model.EventList;
import com.afunms.event.model.Smscontent;
import com.afunms.monitor.executor.base.MonitorInterface;
import com.afunms.monitor.executor.base.SnmpMonitor;
import com.afunms.monitor.item.base.MonitoredItem;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.api.I_HostLastCollectData;
import com.afunms.polling.base.Node;
import com.afunms.polling.impl.HostLastCollectDataManager;
import com.afunms.polling.om.AllUtilHdx;
import com.afunms.polling.om.Buffercollectdata;
import com.afunms.polling.om.CPUcollectdata;
import com.afunms.polling.om.DiscardsPerc;
import com.afunms.polling.om.ErrorsPerc;
import com.afunms.polling.om.Flashcollectdata;
import com.afunms.polling.om.InPkts;
import com.afunms.polling.om.Interfacecollectdata;
import com.afunms.polling.om.IpMac;
import com.afunms.polling.om.IpRouter;
import com.afunms.polling.om.Memorycollectdata;
import com.afunms.polling.om.OutPkts;
import com.afunms.polling.om.Packs;
import com.afunms.polling.om.Systemcollectdata;
import com.afunms.polling.om.Task;
import com.afunms.polling.om.UtilHdx;
import com.afunms.polling.om.UtilHdxPerc;
import com.afunms.polling.task.TaskXml;
import com.afunms.topology.model.HostNode;




/**
 * @author Administrator
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class H3CSnmpTest extends SnmpMonitor implements MonitorInterface {
	private static Hashtable ifEntity_ifStatus = null;
	static {
		ifEntity_ifStatus = new Hashtable();
		ifEntity_ifStatus.put("1", "up");
		ifEntity_ifStatus.put("2", "down");
		ifEntity_ifStatus.put("3", "testing");
		ifEntity_ifStatus.put("5", "unknow");
		ifEntity_ifStatus.put("7", "unknow");
	};
	
	private static Hashtable power_status = null;
	static {
		power_status = new Hashtable();
		power_status.put("1", "active");
		power_status.put("2", "deactive");
		power_status.put("3", "not-install");
		power_status.put("4", "unsupport");
	};
	
	private static Hashtable fan_status = null;
	static {
		fan_status = new Hashtable();
		fan_status.put("1", "active");
		fan_status.put("2", "deactive");
		fan_status.put("3", "not-install");
		fan_status.put("4", "unsupport");
	};
	
	   public H3CSnmpTest()
	   {
	   }
	   public void collectData(Node node,MonitoredItem item){
		   
	   }
	   public void collectData(HostNode node){
		   
	   }
	   
	   public Hashtable collect_Data(HostNode node)
	   {
		  //SnmpItem item = (SnmpItem)monitoredItem;  
		   Calendar date=Calendar.getInstance();
		   Vector cpuVector=new Vector();
		   Vector memoryVector=new Vector();
		   Vector flashVector=new Vector();
		   Vector bufferVector=new Vector();
		   Vector fanVector=new Vector();
		   Vector powerVector=new Vector();
		   Vector voltageVector=new Vector();
		   
		   Vector systemVector=new Vector();
		   Vector ipmacVector = new Vector();
		   Vector iprouterVector = new Vector();
		   Vector interfaceVector=new Vector();
		   Vector utilhdxpercVector = new Vector();
		   Vector utilhdxVector=new Vector();
		   Vector packsVector = new Vector();
		   Vector inpacksVector = new Vector();
		   Vector outpacksVector = new Vector();
		   Vector inpksVector = new Vector();
		   Vector outpksVector = new Vector();
			Vector discardspercVector = new Vector();
			Vector errorspercVector = new Vector();
			Vector allerrorspercVector = new Vector();
			Vector alldiscardspercVector = new Vector();
			Vector allutilhdxpercVector=new Vector();
			Vector allutilhdxVector=new Vector();
			List ifEntityList = new ArrayList();
			Vector fdbVector=new Vector();
			Vector temperatureVector=new Vector();
			
		   HostNode host = (HostNode)node;
	   	   CPUcollectdata cpudata=null;	
	   	   Systemcollectdata systemdata=null;
	   	   Interfacecollectdata interfacedata=null;
			//AllUtilHdxPerc allutilhdxperc=null;
			AllUtilHdx allutilhdx=null;
			UtilHdxPerc utilhdxperc=null;
			InPkts inpacks = null;
			OutPkts outpacks = null;
			UtilHdx utilhdx=null;
			SnmpUtil snmputil = null;
			Hashtable MACVSIP = new Hashtable();
			
		  try{
			  snmputil = SnmpUtil.getInstance();
			  ifEntityList = snmputil.getIfEntityList(host.getIpAddress(), host.getCommunity(), host.getCategory());
		  }catch(Exception e){
			  
		  }
		   
	   	  int result = 0;
	   	  List cpuList = new ArrayList();
	   	List memoryList = new ArrayList();
	   	List flashList = new ArrayList();
	   	List bufferList = new ArrayList();
	   	List temperatureList = new ArrayList();
	   	List fanList = new ArrayList();
	   	List powerList = new ArrayList();
	   	List voltageList = new ArrayList();
		  try{
			  SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			  com.afunms.polling.base.Node snmpnode = (com.afunms.polling.base.Node)PollingEngine.getInstance().getNodeByIP(node.getIpAddress());
			  Date cc = date.getTime();
			  String time = sdf.format(cc);
			  snmpnode.setLastTime(time);
		  }catch(Exception e){
			  
		  }
	   	  try {
//				-------------------------------------------------------------------------------------------cpu start
	   		  String temp = "0";
	   		  if(host.getSysOid().startsWith("1.3.6.1.4.1.2011.")){
	   			//temp = snmp.getMibValue(host.getIpAddress(),host.getCommunity(),"1.3.6.1.4.1.2011.6.1.1.1.4.0");
	   			String[][] valueArray = null;
	   			String[] oids =                
					  new String[] {               
						"1.3.6.1.4.1.2011.6.1.1.1.4"};
	   			String[] oids2 = new String[] {"1.3.6.1.4.1.2011.10.2.6.1.1.1.1.6"};
	   			valueArray = snmp.getCpuTableData(host.getIpAddress(),host.getCommunity(),oids);
	   			if(valueArray==null||valueArray.length==0){//yangjun add  
	   				valueArray = snmp.getCpuTableData(host.getIpAddress(), host.getCommunity(), oids2);
	   			}
	   			int allvalue=0;
	   			int flag = 0;
				if(valueArray != null){
				   	  for(int i=0;i<valueArray.length;i++)
				   	  {
				   		String _value = valueArray[i][0];
				   		String index = valueArray[i][1];
				   		int value=0;
				   		value=Integer.parseInt(_value);
						allvalue = allvalue+Integer.parseInt(_value);
						if(value >0){
							flag = flag +1;
					   		List alist = new ArrayList();
					   		alist.add(index);
					   		alist.add(_value);
					   		//SysLogger.info(host.getIpAddress()+" "+index+" CPU1 Value:"+_value);
					   		cpuList.add(alist);
						}
				   	  }
				}
				
				if(flag >0){
					int intvalue = (allvalue/flag);
					temp = intvalue+"";
					//SysLogger.info(host.getIpAddress()+" cpu "+allvalue/flag);
				}
	   		  }else if(host.getSysOid().startsWith("1.3.6.1.4.1.25506.")){
	   			String[][] valueArray = null;
	   			String[] oids =                
					  new String[] {               
						"1.3.6.1.4.1.2011.10.2.6.1.1.1.1.6"//CPU利用率
	   			};
	   			String[] oids2 = new String[] {"1.3.6.1.4.1.25506.2.6.1.1.1.1.6"};
	   			valueArray = snmp.getCpuTableData(host.getIpAddress(),host.getCommunity(),oids);
	   			if(valueArray==null||valueArray.length==0){//hukelei add  
	   				valueArray = snmp.getCpuTableData(host.getIpAddress(), host.getCommunity(), oids2);
	   			}
	   			int allvalue=0;
	   			int flag = 0;
				if(valueArray != null){
				   	  for(int i=0;i<valueArray.length;i++)
				   	  {

				   		String _value = valueArray[i][0];		   		
				   		String index = valueArray[i][1];
				   		
				   		
				   		int value=0;
				   		value=Integer.parseInt(_value);
						allvalue = allvalue+Integer.parseInt(_value);
						if(value >0){
							flag = flag +1;
					   		List alist = new ArrayList();
					   		alist.add(index);
					   		alist.add(_value);
					   		cpuList.add(alist);					   		
					   		//SysLogger.info(host.getIpAddress()+" "+index+" CPU2 Value:"+_value);
						}
				   	  }
				}
				
				if(flag >0){
					int intvalue = (allvalue/flag);
					temp = intvalue+"";
					//SysLogger.info(host.getIpAddress()+" cpu "+allvalue/flag);
				}
	   		  } 
	   		  
	   		  if(temp == null){
	   			  result = 0;
	   		  }else{
	   			  try{
	   				  if(temp.equalsIgnoreCase("noSuchObject")){
	   					result = 0;
	   				  }else
	   					  result = Integer.parseInt(temp); 
	   			  }catch(Exception ex){
	   				  ex.printStackTrace();
	   				  result = 0;
	   			  }
	   		  }
	   		  
	   		  //SysLogger.info(host.getIpAddress() + "_H3CSnmp value="+result );
			  cpudata=new CPUcollectdata();
			  cpudata.setIpaddress(node.getIpAddress());
			  cpudata.setCollecttime(date);
			  cpudata.setCategory("CPU");
			  cpudata.setEntity("Utilization");
			  cpudata.setSubentity("Utilization");
			  cpudata.setRestype("dynamic");
			  cpudata.setUnit("%");		
			  cpudata.setThevalue(result+"");
			  //SysLogger.info(host.getIpAddress()+" CPU "+result+"%");
				
			  //if (cpudata != null && !cpuusage.equalsIgnoreCase("noSuchObject"))
			  cpuVector.add(0, cpudata);
			  //if(cpuList != null && cpuList.size()>0){
				  cpuVector.add(1, cpuList);
			  //}
			  //cpuVector.addElement(cpudata);
	   	  }
	   	  catch(Exception e)
	   	  {
	   		  e.printStackTrace();
	   		  result = -1;    		  
	   		  SysLogger.error(host.getIpAddress() + "_H3CSnmp",e);
	   	  }	   	  
//			-------------------------------------------------------------------------------------------cpu end	

	   	//-------------------------------------------------------------------------------------------内存 start
	   	  	  
//			-------------------------------------------------------------------------------------------内存 end	

//			-------------------------------------------------------------------------------------------闪存 start
	   	   
//			-------------------------------------------------------------------------------------------闪存 end	
	   	  
//			-------------------------------------------------------------------------------------------缓存 start
	   	    	  
//			-------------------------------------------------------------------------------------------缓存 end	

	   	  //-------------------------------------------------------------------------------------------温度 start
	   	  	  
	   	  //-------------------------------------------------------------------------------------------温度 end
	   	  
	   	  //-------------------------------------------------------------------------------------------电压 start
	   	   	  
	   	  //-------------------------------------------------------------------------------------------电压 end

	   	   
//			-------------------------------------------------------------------------------------------风扇 end	
	   	  
	   	//-------------------------------------------------------------------------------------------电源 start
	   	  try {}
	   	  catch(Exception e)
	   	  {
	   		  e.printStackTrace();
	   		  result = -1;    		  
	   		  SysLogger.error(host.getIpAddress() + "_H3CSnmp",e);
	   	  }	   	  
//			-------------------------------------------------------------------------------------------电源 end
	   	  
	   	//-------------------------------------------------------------------------------------------软件版本 start
	   	  try {}
	   	  catch(Exception e)
	   	  {
	   		  e.printStackTrace();
	   		  result = -1;    		  
	   		  SysLogger.error(host.getIpAddress() + "_H3CSnmp",e);
	   	  }	   	  
//			-------------------------------------------------------------------------------------------软件版本 end
	   	  
//			-------------------------------------------------------------------------------------------system start			
		  try{}
		  catch(Exception e){e.printStackTrace();}
//		  -------------------------------------------------------------------------------------------system end		
		  
//        ---------------------------------------------------得到所有IpNetToMedia,即直接与该设备连接的ip start
		     try
		     {}
		    catch (Exception e)
		    {
		    	//SysLogger.error("getIpNetToMediaTable(),ip=" + address + ",community=" + community);
		        //tableValues = null;
		        e.printStackTrace();
		    }
		  
//       ---------------------------------------------------得到所有IpNetToMedia,即直接与该设备连接的ip end	
		    
		    //---------------------------------------------------得到所有FDB,即直接与该设备连接的ip start
		     try
		     {}
		    catch (Exception e)
		    {
		        e.printStackTrace();
		    }
		  
		    //---------------------------------------------------得到所有FDB,即直接与该设备连接的ip end
	   	  
//	     ---------------------------------------------------从ip router table中得到与该设备相连的路由器 start
		     try
		     {}
		    catch (Exception e)
		    {
		    	//SysLogger.error("getIpNetToMediaTable(),ip=" + address + ",community=" + community);
		        e.printStackTrace();
		    }			    		  
//         ---------------------------------------------------从ip router table中得到与该设备相连的路由器 end		
		    
//			-------------------------------------------------------------------------------------------interface start			
			  try{}catch(Exception e){e.printStackTrace();}
//			  -------------------------------------------------------------------------------------------interface end
			
//			-------------------------------------------------------------------------------------------interface pkts start			
			  try{}catch(Exception e){e.printStackTrace();}
//			  -------------------------------------------------------------------------------------------interface end			
			  //Hashtable returnHas = new Hashtable();
			Hashtable returnHas = (Hashtable)ShareData.getSharedata().get(host.getIpAddress());
			if(returnHas == null)returnHas = new Hashtable();
				//if (pingVector != null && pingVector.size()>0)returnHas.put("ping",pingVector);
//				if (systemVector != null && systemVector.size()>0)returnHas.put("system",systemVector);
//				//if (memoryVector != null && memoryVector.size()>0)returnHas.put("memory",memoryVector);
//				if (cpuVector != null && cpuVector.size()>0)returnHas.put("cpu",cpuVector);
//				if (interfaceVector != null && interfaceVector.size()>0)returnHas.put("interface",interfaceVector);		
//				if (allutilhdxpercVector != null && allutilhdxpercVector.size()>0)returnHas.put("allutilhdxperc",allutilhdxpercVector);
//				if (allutilhdxVector != null && allutilhdxVector.size()>0)returnHas.put("allutilhdx",allutilhdxVector);
//				if (utilhdxpercVector != null && utilhdxpercVector.size()>0)returnHas.put("utilhdxperc",utilhdxpercVector);
//				if (utilhdxVector != null && utilhdxVector.size()>0)returnHas.put("utilhdx",utilhdxVector);		
//				//if (inpacksVector != null && inpacksVector.size()>0)returnHas.put("inpacks",inpacksVector);	
//				//if (outpacksVector != null && outpacksVector.size()>0)returnHas.put("outpacks",outpacksVector);	
//				if (inpksVector != null && inpksVector.size()>0)returnHas.put("inpacks",inpksVector);	
//				if (outpksVector != null && outpksVector.size()>0)returnHas.put("outpacks",outpksVector);
//				if (discardspercVector != null && discardspercVector.size()>0)returnHas.put("discardsperc",discardspercVector);
//				if (errorspercVector != null && errorspercVector.size()>0)returnHas.put("errorsperc",errorspercVector);
//				if (allerrorspercVector != null && allerrorspercVector.size()>0)returnHas.put("allerrorsperc",allerrorspercVector);
//				if (alldiscardspercVector != null && alldiscardspercVector.size()>0)returnHas.put("alldiscardsperc",alldiscardspercVector);
//				if (packsVector != null && packsVector.size()>0)returnHas.put("packs",packsVector);
//				if (ipmacVector != null && ipmacVector.size()>0)returnHas.put("ipmac",ipmacVector);
//				if (iprouterVector != null && iprouterVector.size()>0)returnHas.put("iprouter",iprouterVector);
//				if (ifEntityList != null && ifEntityList.size()>0)returnHas.put("ifentitylist",ifEntityList);
//				if (fdbVector != null && fdbVector.size()>0)returnHas.put("fdb",fdbVector);
//				if (temperatureVector != null && temperatureVector.size()>0)returnHas.put("temperature",temperatureVector);
//				if (memoryVector != null && memoryVector.size()>0)returnHas.put("memory",memoryVector);
//				if (flashVector != null && flashVector.size()>0)returnHas.put("flash",flashVector);
//				if (bufferVector != null && bufferVector.size()>0)returnHas.put("buffer",bufferVector);
//				if (fanVector != null && fanVector.size()>0)returnHas.put("fan",fanVector);
//				if (powerVector != null && powerVector.size()>0)returnHas.put("power",powerVector);
//				if (voltageVector != null && voltageVector.size()>0)returnHas.put("voltage",voltageVector);
				
				//returnHas.put("flag",flag);
				return returnHas;
	   }
	public void run(){
		//collectData();
	}



	public int getInterval(float d,String t){
				int interval=0;
				  if(t.equals("d"))
					 interval =(int) d*24*60*60; //天数
				  else if(t.equals("h"))
					 interval =(int) d*60*60;    //小时
				  else if(t.equals("m"))
					 interval = (int)d*60;       //分钟
				else if(t.equals("s"))
							 interval =(int) d;       //秒
				return interval;
	}
	
	public void createSMS(String subtype,String subentity,String ipaddress,String objid,String content,int flag,int checkday,String sIndex,String bids){
	 	//建立短信		 	
	 	//从内存里获得当前这个IP的PING的值
	 	Calendar date=Calendar.getInstance();
	 	Hashtable sendeddata = ShareData.getSendeddata();
	 	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	 	System.out.println("端口事件--------------------");
	 	try{
 			if (!sendeddata.containsKey(subtype+":"+subentity+":"+ipaddress+":"+sIndex)){
 				//若不在，则建立短信，并且添加到发送列表里
	 			Smscontent smscontent = new Smscontent();
	 			String time = sdf.format(date.getTime());
	 			smscontent.setLevel(flag+"");
	 			smscontent.setObjid(objid);
	 			smscontent.setMessage(content);
	 			smscontent.setRecordtime(time);
	 			smscontent.setSubtype(subtype);
	 			smscontent.setSubentity(subentity);
	 			smscontent.setIp(ipaddress);
	 			//发送短信
	 			SmscontentDao smsmanager=new SmscontentDao();
	 			smsmanager.sendURLSmscontent(smscontent);	
				sendeddata.put(subtype+":"+subentity+":"+ipaddress+":"+sIndex,date);	
				
 			}else{
 				//若在，则从已发送短信列表里判断是否已经发送当天的短信
 				SmsDao smsDao = new SmsDao();
 				List list = new ArrayList();
 				String startTime = new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + " 00:00:00";
 				String endTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
 				try {
 					list = smsDao.findByEvent(content,startTime,endTime);
				} catch (RuntimeException e) {
					e.printStackTrace();
				} finally {
					smsDao.close();
				}
 				if(list!=null&&list.size()>0){//短信列表里已经发送当天的短信
 					Calendar formerdate =(Calendar)sendeddata.get(subtype+":"+subentity+":"+ipaddress+":"+sIndex);		 				
 		 			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
 		 			Date last = null;
 		 			Date current = null;
 		 			Calendar sendcalen = formerdate;
 		 			Date cc = sendcalen.getTime();
 		 			String tempsenddate = formatter.format(cc);
 		 			
 		 			Calendar currentcalen = date;
 		 			Date ccc = currentcalen.getTime();
 		 			last = formatter.parse(tempsenddate);
 		 			String currentsenddate = formatter.format(ccc);
 		 			current = formatter.parse(currentsenddate);
 		 			
 		 			long subvalue = current.getTime()-last.getTime();	
 		 			if(checkday == 1){
 		 				//检查是否设置了当天发送限制,1为检查,0为不检查
 		 				if (subvalue/(1000*60*60*24)>=1){
 			 				//超过一天，则再发信息
 				 			Smscontent smscontent = new Smscontent();
 				 			String time = sdf.format(date.getTime());
 				 			smscontent.setLevel(flag+"");
 				 			smscontent.setObjid(objid);
 				 			smscontent.setMessage(content);
 				 			smscontent.setRecordtime(time);
 				 			smscontent.setSubtype(subtype);
 				 			smscontent.setSubentity(subentity);
 				 			smscontent.setIp(ipaddress);//发送短信
 				 			SmscontentDao smsmanager=new SmscontentDao();
 				 			smsmanager.sendURLSmscontent(smscontent);
 							//修改已经发送的短信记录	
 							sendeddata.put(subtype+":"+subentity+":"+ipaddress+":"+sIndex,date);
 				 		} else {
 	 	                    //开始写事件
 	 		 	            String sysLocation = "";
 	 		 				createEvent("poll",sysLocation,bids,content,flag,subtype,subentity,ipaddress,objid);
 	 		 			}
 		 			} 
 				} else {
 					Smscontent smscontent = new Smscontent();
 		 			String time = sdf.format(date.getTime());
 		 			smscontent.setLevel(flag+"");
 		 			smscontent.setObjid(objid);
 		 			smscontent.setMessage(content);
 		 			smscontent.setRecordtime(time);
 		 			smscontent.setSubtype(subtype);
 		 			smscontent.setSubentity(subentity);
 		 			smscontent.setIp(ipaddress);
 		 			//发送短信
 		 			SmscontentDao smsmanager=new SmscontentDao();
 		 			smsmanager.sendURLSmscontent(smscontent);	
 					sendeddata.put(subtype+":"+subentity+":"+ipaddress+":"+sIndex,date);
 				}
 				
 			}	 			 			 			 			 	
	 	}catch(Exception e){
	 		e.printStackTrace();
	 	}
	 }
	private void createEvent(String eventtype,String eventlocation,String bid,String content,int level1,String subtype,String subentity,String ipaddress,String objid){
		//生成事件
		SysLogger.info("##############开始生成事件############");
		EventList eventlist = new EventList();
		eventlist.setEventtype(eventtype);
		eventlist.setEventlocation(eventlocation);
		eventlist.setContent(content);
		eventlist.setLevel1(level1);
		eventlist.setManagesign(0);
		eventlist.setBak("");
		eventlist.setRecordtime(Calendar.getInstance());
		eventlist.setReportman("系统轮询");
		eventlist.setBusinessid(bid);
		eventlist.setNodeid(Integer.parseInt(objid));
		eventlist.setOid(0);
		eventlist.setSubtype(subtype);
		eventlist.setSubentity(subentity);
		EventListDao eventlistdao = new EventListDao();
		try{
			eventlistdao.save(eventlist);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			eventlistdao.close();
		}
	}
}





