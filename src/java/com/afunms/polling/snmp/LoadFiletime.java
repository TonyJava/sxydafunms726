package com.afunms.polling.snmp;

/*
 * @author hukelei@dhcc.com.cn
 *
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.afunms.alarm.model.AlarmIndicatorsNode;
import com.afunms.alarm.send.SendAlarmUtil;
import com.afunms.alarm.util.AlarmConstant;
import com.afunms.alarm.util.AlarmIndicatorsUtil;
import com.afunms.common.util.ShareData;
import com.afunms.common.util.ShareDataLsf;
import com.afunms.config.model.Nodeconfig;
import com.afunms.event.dao.SmscontentDao;
import com.afunms.event.model.CheckEvent;
import com.afunms.event.model.EventList;
import com.afunms.event.model.Smscontent;
import com.afunms.indicators.model.NodeGatherIndicators;
import com.afunms.indicators.util.NodeUtil;
import com.afunms.initialize.ResourceCenter;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.node.Host;
  
/**
 * @author Administrator
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */  

public class LoadFiletime {
	/**
	 * @param hostname
	 */
	private String ipaddress;
	private Hashtable sendeddata = ShareData.getProcsendeddata();
	private java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	//public LoadAixFile(String ipaddress) {
	//	this.ipaddress = ipaddress;
	//}
	/**
	 * 2012-07-26
	 * @param host
	 *            节点对象
	 * @param Content
	 *            告警的内容 #设备ip：192.168.0.1:（内容）LSF采集文件超时
	 * @param eventname
	 *            （nodeid：host：proce：procename） 58：host：proce：Lsflog
	 */
	public void check(Host host, String Content, String eventname,String alarmtype) {
		NodeUtil nodeutil = new NodeUtil();
		String subtype = nodeutil.creatNodeDTOByHost(host).getSubtype();
		try {
			
			EventList eventlist = new EventList();
			eventlist.setEventtype("poll");
			eventlist.setEventlocation(host.getAlias()+"("+host.getIpAddress()+")");
			eventlist.setContent(Content);
			eventlist.setLevel1(1);
			eventlist.setManagesign(0);
			eventlist.setBak("");
			eventlist.setRecordtime(Calendar.getInstance());
			eventlist.setReportman("系统轮询");
			eventlist.setBusinessid(host.getBid());
			eventlist.setNodeid(host.getId());
			eventlist.setOid(0);
			eventlist.setSubtype(subtype);
			eventlist.setSubentity(alarmtype);
			eventlist.setIpaddress(ipaddress);
			ShareDataLsf.getAlarmlist().put(host.getId(), "1");
			AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
			List list = alarmIndicatorsUtil.getAlarmInicatorsThresholdForNode(
					String.valueOf(host.getId()), AlarmConstant.TYPE_HOST,
					subtype, alarmtype);
			for (int z = 0; z < list.size(); z++) {
				 //System.out.println("========阀值个数==="+list.size());
				AlarmIndicatorsNode alarmIndicatorsnode = (AlarmIndicatorsNode) list
						.get(z);
				// 对物理内存值进行告警检测
				CheckEvent checkevent = new CheckEvent();
				checkevent.setAlarmlevel(1);
				checkevent.setName(eventname);
				SendAlarmUtil sendAlarmUtil = new SendAlarmUtil();
				// 发送告警
				sendAlarmUtil.sendAlarmNoIndicatorOther(checkevent, eventlist,
						alarmIndicatorsnode);// 
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public Hashtable collect_Data(NodeGatherIndicators alarmIndicatorsNode)
    {
		
		
		
		Host host = (Host)PollingEngine.getInstance().getNodeByID(Integer.parseInt(alarmIndicatorsNode.getNodeid()));
		
		//如果系统中遇到ping 不通着不再对文件进行解析
		if(ShareData.getAgentalarmlevellist().containsKey(host.getId()+":host:ping") )
		{
			
			if(Integer.parseInt((String)ShareData.getAgentalarmlevellist().get(host.getId()+":host:ping"))>2)
			{
				
				return null;
			}
			
		}
		ipaddress=host.getIpAddress();
		
		StringBuffer fileContent = new StringBuffer();
		Nodeconfig nodeconfig = new Nodeconfig();
		String collecttime = "";
		//Host host = (Host)PollingEngine.getInstance().getNodeByIP(ipaddress);
		if(host == null)return null;
		nodeconfig.setNodeid(host.getId());
		nodeconfig.setHostname(host.getAlias());
		
		
		String dateString="";
		
		
    	try 
		{
    	
			String filename = ResourceCenter.getInstance().getSysPath() + "/linuxserver/"+host.getIpAddress()+".log";	
			
			File file=new File(filename);
			if(!file.exists()){
				//文件不存在,则产生告警
				try{
					createFileNotExistSMS(ipaddress);
				}catch(Exception e){
					e.printStackTrace();
				}
				//return null;
			}
			
			
			
			
			long time = file.lastModified();
			
			Date d = new Date(time);
			Format simpleFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");	 
			dateString = simpleFormat.format(d);//系统当前的时间

			
			file = null;
			FileInputStream fis = new FileInputStream(filename);
			InputStreamReader isr=new InputStreamReader(fis);
			BufferedReader br=new BufferedReader(isr);
			String strLine = null;
    		//读入文件内容
    		while((strLine=br.readLine())!=null)
    		{
    			fileContent.append(strLine + "\n");
    			//SysLogger.info(strLine);
    		}
    		isr.close();
    		fis.close();
    		br.close();
    	
		} 
    	catch (Exception e)
		{
			e.printStackTrace();
			
			//System.out.println("===读取配置文件有异常===############################===");
			
		}

    	Pattern tmpPt = null;
    	Matcher mr = null;
    	Calendar date = Calendar.getInstance();
    	
    	System.out.println("=========================1==============================");
	     //----------------解析数据采集时间内容--创建监控项---------------------        	
		tmpPt = Pattern.compile("(cmdbegin:collecttime)(.*)(cmdbegin:version)",Pattern.DOTALL);
		mr = tmpPt.matcher(fileContent.toString());

		if (mr.find()) {
			
			
			
			
			
			collecttime = mr.group(2).trim();
			
			System.out.println("===dateString===="+dateString+"==collecttime=="+collecttime);
				int time_flag = this.computeDateTime(dateString,
						collecttime);
				
				System.out.println("time_flag=="+time_flag);
				
				
					if ( time_flag > 1 || time_flag < -1) {
						
						System.out.println("============开始告警===========");
						String content = host.getAlias() + "(" + ipaddress + ")"
								+ "采集服务器与当前服务器的时间相差时间超过1分钟";
						String name = host.getId() + ":host:servertime";
						this.check(host, content, name,"servertime");
					    return null;
					//}
				}
			}
    		
		return null;
    }	
	

    /**
     * 连个时间比较
     * @param str_1
     * @param str_2
     * @return
     */
	public int computeDateTime(String str_1,String str_2){
		int resultTime = 0;
		try {
			if(str_1!=null && str_2!=null){
				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
				SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
				long result = (
							(format.parse(str_2.replaceAll("\n", "")).getTime())
							- 
							(format2.parse(str_1.replaceAll("\n", "")).getTime())
							)/60000;
					resultTime = new Long(result).intValue();
		        }
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return resultTime;
	}
	
	 

	 
	 public void createFileNotExistSMS(String ipaddress){
		 	//建立短信		 	
		 	//从内存里获得当前这个IP的PING的值
				Calendar date=Calendar.getInstance();
				try{
					Host host = (Host)PollingEngine.getInstance().getNodeByIP(ipaddress);
					if(host == null)return;
					
					if (!sendeddata.containsKey(ipaddress+":file:"+host.getId())){
						//若不在，则建立短信，并且添加到发送列表里
						Smscontent smscontent = new Smscontent();
						String time = sdf.format(date.getTime());
						smscontent.setLevel("3");
						smscontent.setObjid(host.getId()+"");
						smscontent.setMessage(host.getAlias()+" ("+host.getIpAddress()+")"+"的日志文件无法正确上传到网管服务器");
						smscontent.setRecordtime(time);
						smscontent.setSubtype("host");
						smscontent.setSubentity("ftp");
						smscontent.setIp(host.getIpAddress());//发送短信
						SmscontentDao smsmanager=new SmscontentDao();
						smsmanager.sendURLSmscontent(smscontent);	
						sendeddata.put(ipaddress+":file"+host.getId(),date);		 					 				
					}else{
						//若在，则从已发送短信列表里判断是否已经发送当天的短信
						Calendar formerdate =(Calendar)sendeddata.get(ipaddress+":file:"+host.getId());		 				
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
							smscontent.setLevel("3");
							smscontent.setObjid(host.getId()+"");
							smscontent.setMessage(host.getAlias()+" ("+host.getIpAddress()+")"+"的日志文件无法正确上传到网管服务器");
							smscontent.setRecordtime(time);
							smscontent.setSubtype("host");
							smscontent.setSubentity("ftp");
							smscontent.setIp(host.getIpAddress());//发送短信
							SmscontentDao smsmanager=new SmscontentDao();
							smsmanager.sendURLSmscontent(smscontent);
							//修改已经发送的短信记录	
							sendeddata.put(ipaddress+":file:"+host.getId(),date);	
						}	
					}	 			 			 			 			 	
				}catch(Exception e){
					e.printStackTrace();
				}
		 	}	 
}






