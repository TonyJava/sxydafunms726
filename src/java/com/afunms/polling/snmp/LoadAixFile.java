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
import com.afunms.alarm.send.SendMailAlarm;
import com.afunms.alarm.util.AlarmConstant;
import com.afunms.alarm.util.AlarmIndicatorsUtil;
import com.afunms.application.course.util.LsfClassUtil;
import com.afunms.common.util.Arith;
import com.afunms.common.util.CheckEventUtil;
import com.afunms.common.util.ErrptlogUtil;
import com.afunms.common.util.ReadErrptlog;
import com.afunms.common.util.ShareData;
import com.afunms.common.util.ShareDataLsf;
import com.afunms.common.util.SysLogger;
import com.afunms.common.util.SysUtil;
import com.afunms.config.dao.ErrptconfigDao;
import com.afunms.config.dao.ProcsDao;
import com.afunms.config.model.Errptconfig;
import com.afunms.config.model.Errptlog;
import com.afunms.config.model.Nodeconfig;
import com.afunms.config.model.Nodecpuconfig;
import com.afunms.config.model.Procs;
import com.afunms.event.dao.SmscontentDao;
import com.afunms.event.model.CheckEvent;
import com.afunms.event.model.EventList;
import com.afunms.event.model.Smscontent;
import com.afunms.indicators.model.NodeGatherIndicators;
import com.afunms.indicators.util.NodeUtil;
import com.afunms.initialize.ResourceCenter;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.node.Host;
import com.afunms.polling.om.CPUcollectdata;
import com.afunms.polling.om.Diskcollectdata;
import com.afunms.polling.om.Interfacecollectdata;
import com.afunms.polling.om.Memorycollectdata;
import com.afunms.polling.om.Processcollectdata;
import com.afunms.polling.om.Systemcollectdata;
import com.afunms.polling.om.Usercollectdata;
import com.afunms.polling.om.UtilHdx;
import com.gatherResulttosql.HostDatatempCollecttimeRtosql;
import com.gatherResulttosql.HostDatatempCpuconfiRtosql;
import com.gatherResulttosql.HostDatatempCpuperRtosql;
import com.gatherResulttosql.HostDatatempDiskPeriofRtosql;
import com.gatherResulttosql.HostDatatempDiskRttosql;
import com.gatherResulttosql.HostDatatempErrptRtosql;
import com.gatherResulttosql.HostDatatempNodeconfRtosql;
import com.gatherResulttosql.HostDatatempPageRtosql;
import com.gatherResulttosql.HostDatatempPagingRtosql;
import com.gatherResulttosql.HostDatatempProcessRtTosql;
import com.gatherResulttosql.HostDatatempRuteRtosql;
import com.gatherResulttosql.HostDatatempUserRtosql;
import com.gatherResulttosql.HostDatatempVolumeRtosql;
import com.gatherResulttosql.HostDatatempiflistRtosql;
import com.gatherResulttosql.HostDatatempinterfaceRtosql;
import com.gatherResulttosql.HostDatatempnDiskperfRtosql;
import com.gatherResulttosql.HostDatatempserciceRttosql;
import com.gatherResulttosql.HostDatatemputilhdxRtosql;
import com.gatherResulttosql.HostPagingResultTosql;
import com.gatherResulttosql.HostPhysicalMemoryResulttosql;
import com.gatherResulttosql.HostProcessRtosql;
import com.gatherResulttosql.HostcpuResultTosql;
import com.gatherResulttosql.HostdiskResultosql;
import com.gatherResulttosql.NetHostDatatempCpuRTosql;
import com.gatherResulttosql.NetHostDatatempSystemRttosql;
import com.gatherResulttosql.NetHostMemoryRtsql;
  
/**
 * @author Administrator
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */  

public class LoadAixFile {
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
	 *            �ڵ����
	 * @param Content
	 *            �澯������ #�豸ip��192.168.0.1:�����ݣ�LSF�ɼ��ļ���ʱ
	 * @param eventname
	 *            ��nodeid��host��proce��procename�� 58��host��proce��Lsflog
	 */
	public void check(Host host, String Content, String eventname,String alarmtype) {
		NodeUtil nodeutil = new NodeUtil();
		String subtype = nodeutil.creatNodeDTOByHost(host).getSubtype();
		try {
			
			EventList eventlist = new EventList();
			eventlist.setEventtype("poll");
			eventlist.setEventlocation(host.getSysLocation());
			eventlist.setContent(Content);
			eventlist.setLevel1(1);
			eventlist.setManagesign(0);
			eventlist.setBak("");
			eventlist.setRecordtime(Calendar.getInstance());
			eventlist.setReportman("ϵͳ��ѯ");
			eventlist.setBusinessid(host.getBid());
			eventlist.setNodeid(host.getId());
			eventlist.setOid(0);
			eventlist.setSubtype(subtype);
			eventlist.setSubentity("proc");
			eventlist.setIpaddress(ipaddress);
			ShareDataLsf.getAlarmlist().put(host.getId(), "1");
			AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
			List list = alarmIndicatorsUtil.getAlarmInicatorsThresholdForNode(
					String.valueOf(host.getId()), AlarmConstant.TYPE_HOST,
					subtype, alarmtype);
			for (int z = 0; z < list.size(); z++) {
				// System.out.println("========��ֵ����==="+list.size());
				AlarmIndicatorsNode alarmIndicatorsnode = (AlarmIndicatorsNode) list
						.get(z);
				// �������ڴ�ֵ���и澯���
				CheckEvent checkevent = new CheckEvent();
				checkevent.setAlarmlevel(1);
				checkevent.setName(eventname);
				SendAlarmUtil sendAlarmUtil = new SendAlarmUtil();
				// ���͸澯
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
		
		//���ϵͳ������ping ��ͨ�Ų��ٶ��ļ����н���
		if(ShareData.getAgentalarmlevellist().containsKey(host.getId()+":host:ping") )
		{
			
			if(Integer.parseInt((String)ShareData.getAgentalarmlevellist().get(host.getId()+":host:ping"))>2)
			{
				
				return null;
			}
			
		}
		
		//System.out.println("alarmIndicatorsNode.getNodeid()=="+alarmIndicatorsNode.getNodeid());
		//System.out.println("host.getIpAddress()=="+host.getIpAddress());
		ipaddress=host.getIpAddress();
		
		
		//yangjun
		Hashtable ipAllData = (Hashtable)ShareData.getSharedata().get(host.getIpAddress());
		if(ipAllData == null)ipAllData = new Hashtable();
		Hashtable returnHash = new Hashtable();
		StringBuffer fileContent = new StringBuffer();
		Vector cpuVector=new Vector();
		Vector systemVector=new Vector();
		Vector userVector=new Vector();
		Vector diskVector=new Vector();
		Vector processVector=new Vector();
		Nodeconfig nodeconfig = new Nodeconfig();
		Vector interfaceVector = new Vector();
		Vector utilhdxVector = new Vector();
		Vector errptlogVector = new Vector();
		String collecttime = "";
		Vector volumeVector=new Vector();
		List routeList = new ArrayList();
		
		
		CPUcollectdata cpudata=null;
		Systemcollectdata systemdata=null;
		Usercollectdata userdata=null;
		Processcollectdata processdata=null;
		//Host host = (Host)PollingEngine.getInstance().getNodeByIP(ipaddress);
		if(host == null)return null;
		nodeconfig.setNodeid(host.getId());
		nodeconfig.setHostname(host.getAlias());
		float PhysicalMemCap = 0;
		float freePhysicalMemory =0; 
		float allPhyPagesSize = 0;
		float usedPhyPagesSize = 0;
		float SwapMemCap = 0;
		float freeSwapMemory =0;
		float usedSwapMemory =0;
		Hashtable pagehash = new Hashtable();
		Hashtable paginghash = new Hashtable();
		
		Hashtable networkconfig = new Hashtable();
		
    	try 
		{
    	
			String filename = ResourceCenter.getInstance().getSysPath() + "/linuxserver/"+host.getIpAddress()+".log";	
			
			File file=new File(filename);
			if(!file.exists()){
				//�ļ�������,������澯
				try{
					createFileNotExistSMS(ipaddress);
				}catch(Exception e){
					e.printStackTrace();
				}
				//return null;
			}
			
			
		
			file = null;
			FileInputStream fis = new FileInputStream(filename);
			InputStreamReader isr=new InputStreamReader(fis);
			BufferedReader br=new BufferedReader(isr);
			String strLine = null;
    		//�����ļ�����
    		while((strLine=br.readLine())!=null)
    		{
    			fileContent.append(strLine + "\n");
    			//SysLogger.info(strLine);
    		}
    		isr.close();
    		fis.close();
    		br.close();
    		try{
    			copyFile(host.getIpAddress(),getMaxNum(host.getIpAddress()));
    		}catch(Exception e){
    			e.printStackTrace();
    		}
		} 
    	catch (Exception e)
		{
			e.printStackTrace();
			
			//System.out.println("===��ȡ�����ļ����쳣===############################===");
			
		}

    	Pattern tmpPt = null;
    	Matcher mr = null;
    	Calendar date = Calendar.getInstance();
    	
    	//System.out.println("=========================1==============================");
	     //----------------�������ݲɼ�ʱ������--���������---------------------        	
		tmpPt = Pattern.compile("(cmdbegin:collecttime)(.*)(cmdbegin:version)",Pattern.DOTALL);
		mr = tmpPt.matcher(fileContent.toString());
//		if(mr.find())
//		{
//		    //System.out.println("===(cmdbegin:collecttime)(.*)(cmdbegin:version)");
//			collecttime = mr.group(2);
//			//System.out.println("=====================");
//			//System.out.println("================"+collecttime);
//			//System.out.println("=====================");
//			
//		}
//		if (collecttime != null && collecttime.length()>0 ){
//			collecttime = collecttime.trim();
//		}
		//2012-07-26 �ϱ�ע�� ��־ʱ���ж�  �޸�Ϊ����
		if (mr.find()) {
			collecttime = mr.group(2).trim();
			//System.out.println("=============collecttime============"+collecttime);
			
			//boolean flg=false;
			if (null !=ShareDataLsf.getCollecttime_table() && !ShareDataLsf.getCollecttime_table().containsKey(host.getIpAddress()+"-log") ) {
				//System.out.println("=======1======collecttime============"+collecttime);
				ShareDataLsf.getCollecttime_table().put(host.getIpAddress()+"-log", collecttime);
				//flg=true;
			} else {
				String rm_time = (String) ShareDataLsf.getCollecttime_table().get(host.getIpAddress()+"-log");
				ShareDataLsf.getCollecttime_table().put(host.getIpAddress()+"-log", collecttime);
				
				//System.out.println("===rm_time="+rm_time);
				//System.out.println("===collecttime==="+collecttime);
				int time_flag = new LsfClassUtil().computeDateTime(rm_time,
						collecttime);
				
				//System.out.println("===time_flag=="+time_flag);
				//if(!flg){
					if (time_flag == 0 || time_flag > 10  || time_flag<=-1 ) {
						
						//System.out.println("============��ʼ�澯===========");
						String content = host.getAlias() + "(" + ipaddress + ").log "
								+ " aix��־�ļ��ɼ�ʱ�䳬ʱ,Ԥ��ʱ��Ϊ��10����";
						String name = host.getId() + ":host:log";
						this.check(host, content, name,"logfile");
					    return null;
					//}
				}
			}
		}
	     //----------------����version����--���������---------------------        	
		String versionContent = "";
		tmpPt = Pattern.compile("(cmdbegin:version)(.*)(cmdbegin:vmstat)",Pattern.DOTALL);
		mr = tmpPt.matcher(fileContent.toString());
		if(mr.find())
		{
			// System.out.println("===(cmdbegin:version)(.*)(cmdbegin:vmstat)");
			 versionContent = mr.group(2);
			
		}
		if (versionContent != null && versionContent.length()>0 && versionContent.length()<50){
			nodeconfig.setCSDVersion(versionContent.trim());
		}else
		{
			nodeconfig.setCSDVersion("");
		}
		
		
		//��----------------����vmstat����--���������---------------------        	
		String vmstat_Content = "";
		tmpPt = Pattern.compile("(cmdbegin:vmstat)(.*)(cmdbegin:lsps)",Pattern.DOTALL);
		mr = tmpPt.matcher(fileContent.toString());
		if(mr.find())
		{
			try{
			vmstat_Content = mr.group(2);
			//System.out.println("===========cmdbegin:vmstat)(.*)(cmdbegin:lsps)==========");
			//System.out.println("================"+vmstat_Content);
			//System.out.println("==============cmdbegin:vmstat)(.*)(cmdbegin:lsps)=======");
			}catch(Exception e){
				e.printStackTrace();
			}
			
		} 
		String[] vmstat_LineArr = null;
		String[] vmstat_tmpData = null;
		//PhysicalMemCap=0;//
		//Vector memoryVector=new Vector();
		//Memorycollectdata memorydata=null;
		try{
			vmstat_LineArr = vmstat_Content.split("\n");
			for(int i=1; i<vmstat_LineArr.length;i++)
			{  
				
				//System.out.println("=="+ vmstat_LineArr[i].trim());
				
				if(vmstat_LineArr[i].trim().indexOf("System configuration:")>=0)
				{
					
					
					String mem="0";
					if(vmstat_LineArr[i].trim().indexOf("mem=")>0)
					mem=(vmstat_LineArr[i].trim().substring(vmstat_LineArr[i].trim().indexOf("mem="),vmstat_LineArr[i].trim().length()-2));
					
					//System.out.println("===mem==="+mem);
					if(mem.indexOf("MB")>0)
					{
						mem=mem.substring(0,mem.indexOf("MB"));
					}
					//System.out.println("===mem2==="+mem);
					PhysicalMemCap = Float.parseFloat(mem.replaceAll("mem=", "").trim().replaceAll("MB", "").trim());
					
				}
				
				
				
				vmstat_tmpData = vmstat_LineArr[i].trim().split("\\s++");          			
				if((vmstat_tmpData != null && vmstat_tmpData.length==17||vmstat_tmpData.length==19))
				{							
					if (vmstat_tmpData[0]!=null && !vmstat_tmpData[0].equalsIgnoreCase("r")){
						//freeMemory
						freePhysicalMemory = Integer.parseInt(vmstat_tmpData[3])*4/1024;	
						
						String re = vmstat_tmpData[4];
						String pi = vmstat_tmpData[5];
						String po = vmstat_tmpData[6];
						String fr = vmstat_tmpData[7];
						String sr = vmstat_tmpData[8];
						String cy = vmstat_tmpData[9];
						String iw = vmstat_tmpData[16];
						pagehash.put("re", re);
						pagehash.put("pi", pi);
						pagehash.put("po", po);
						pagehash.put("fr", fr);
						pagehash.put("sr", sr);
						pagehash.put("cy", cy);
						pagehash.put("cy", cy);
						pagehash.put("iw", iw);
					}
					//��iowaitֵ���и澯���
			   		Hashtable collectHash = new Hashtable();
					collectHash.put("vmstat", pagehash);
				    try{
						AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
						//System.out.println("==================tttqq==================");
						//System.out.println(ShareData.getAlarmHashtable());
						//System.out.println("==================tttqq==================");
						List list = alarmIndicatorsUtil.getAlarmInicatorsThresholdForNode(String.valueOf(host.getId()), AlarmConstant.TYPE_HOST, "aix","iowait");
						for(int k = 0 ; k < list.size() ; k ++){
							AlarmIndicatorsNode alarmIndicatorsnode = (AlarmIndicatorsNode)list.get(k);
							//��CPUֵ���и澯���
							CheckEventUtil checkutil = new CheckEventUtil();
							checkutil.updateData(host,collectHash,"host","aix",alarmIndicatorsnode);
							//}
						}
				    }catch(Exception e){
				    	e.printStackTrace();
					}
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		//System.out.println("=========================2==============================");
		//----------------����Paging Space����--���������---------------------        	
		String Paging_Content = "";
		tmpPt = Pattern.compile("(cmdbegin:lsps)(.*)(cmdbegin:swap)",Pattern.DOTALL);
		mr = tmpPt.matcher(fileContent.toString());
		if(mr.find())
		{
			//System.out.println("===(cmdbegin:lsps)(.*)(cmdbegin:process)");
			try{
				Paging_Content = mr.group(2);
				//System.out.println("=====================");
				//System.out.println("================"+Paging_Content);
				//System.out.println("=====================");
			}catch(Exception e){
				e.printStackTrace();
			}
			
		} 
		String[] Paging_LineArr = null;
		String[] Paging_tmpData = null;
		
		try{
			Paging_LineArr = Paging_Content.split("\n");
			if(Paging_LineArr!=null&&Paging_LineArr.length>1)
			{    			
				Paging_tmpData = Paging_LineArr[2].trim().split("\\s++");          			
				if(Paging_tmpData != null)
				{							
					String Total_Paging_Space = Paging_tmpData[0];
					String Percent_Used = Paging_tmpData[1];
					paginghash.put("Total_Paging_Space", Total_Paging_Space);
					paginghash.put("Percent_Used", Percent_Used);
					
					Hashtable collectHash = new Hashtable();
					collectHash.put("pagingusage", paginghash);
					//�Ի�ҳֵ���и澯���
				   
					
					
					if (((nodeconfig.getCSDVersion().length()==15) ) && !((nodeconfig.getCSDVersion().substring(0,1).indexOf("5")<0) || (nodeconfig.getCSDVersion().substring(0,1).indexOf("6")<0))) {
						System.out.println("=======================����aix5����aix6 ��黻ҳ��=================================");
				    try{
						AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
						List list = alarmIndicatorsUtil.getAlarmInicatorsThresholdForNode(String.valueOf(host.getId()), AlarmConstant.TYPE_HOST, "aix","pagingusage");
						for(int i = 0 ; i < list.size() ; i ++){
							AlarmIndicatorsNode alarmIndicatorsnode = (AlarmIndicatorsNode)list.get(i);
							//�Ի�ҳ�ʽ��и澯���
							CheckEventUtil checkutil = new CheckEventUtil();
							checkutil.updateData(host,collectHash,"host","aix",alarmIndicatorsnode);
							//}
						}
				    }catch(Exception e){
				    	e.printStackTrace();
				    }
					}
					//�ܻ�ҳ
					try
					{
						Total_Paging_Space = Total_Paging_Space.replaceAll("MB", "");
						allPhyPagesSize = Float.parseFloat(Total_Paging_Space);
					} catch (Exception e)
					{
						e.printStackTrace();
					}
					//��ʹ����
					try
					{
						Percent_Used = Percent_Used.replaceAll("%", "");
						usedPhyPagesSize = Float.parseFloat(Percent_Used);
					} catch (Exception e)
					{
						e.printStackTrace();
					}
					
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		//��----------------����swap����--���������---------------------        	
		String swap_Content = "";
		tmpPt = Pattern.compile("(cmdbegin:swap)(.*)(cmdbegin:process\n)",Pattern.DOTALL);
		mr = tmpPt.matcher(fileContent.toString());
		if(mr.find())
		{
			//System.out.println("============swap=====XXB===============");
			try{
				swap_Content = mr.group(2);
				
				//System.out.println("================================swap=====");
				//System.out.println(swap_Content);
				//System.out.println("================================swap===end==");
				
			}catch(Exception e){
				e.printStackTrace();
			}
			
		} 
		String[] swap_LineArr = null;
		String[] swap_tmpData = null;
		try{
			swap_LineArr = swap_Content.trim().split("\n");
			if(swap_LineArr != null && swap_LineArr.length>0){
				swap_tmpData = swap_LineArr[0].trim().split("\\s++");	
				if(swap_tmpData != null && swap_tmpData.length==12){
					
					try{
						
						SwapMemCap = Float.parseFloat(swap_tmpData[2].trim());
						freeSwapMemory = Float.parseFloat(swap_tmpData[10].trim());
						usedSwapMemory = Float.parseFloat(swap_tmpData[6].trim());
						//SysLogger.info("===****==8="+SwapMemCap+"===="+freeSwapMemory+"===="+usedSwapMemory);
					}catch(Exception e){
						e.printStackTrace();
					}
				}
			}
		//
		//for(int i=1; i<swap_LineArr.length;i++)
		//{    			
			//vmstat_tmpData = swap_LineArr[i].trim().split("\\s++");			
			//if((vmstat_tmpData != null && vmstat_tmpData.length==17))
			//{							
				//if (vmstat_tmpData[0]!=null && !vmstat_tmpData[0].equalsIgnoreCase("r")){
					//freeMemory
					//freePhysicalMemory = Integer.parseInt(vmstat_tmpData[3])*4/1024;					
				//}

			//}
		//}
			
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
		//----------------����processorconfig����--���������------û�����---------------        	
		String processorconfigContent = "";

		String cpuconfigContent = "";
		tmpPt = Pattern.compile("(cmdbegin:cpuconfig\n)(.*)(cmdbegin:allconfig\n)",Pattern.DOTALL);
		mr = tmpPt.matcher(fileContent.toString());
		if(mr.find())
		{
			
			
			processorconfigContent = mr.group(2);	
			//System.out.println("===============allconfig================");
			//System.out.println(processorconfigContent);
			//System.out.println("=================allconfig=================");
		} 
		String[] cpuconfigLineArr = processorconfigContent.split("\n");
		String[] cpuconfig_tmpData = null;
		List<Nodecpuconfig> cpuconfiglist = new ArrayList<Nodecpuconfig>();
		Nodecpuconfig nodecpuconfig = new Nodecpuconfig();
		String procesors = "";
		String processorType = "";
		String processorSpeed = "";
		String cputype = "";

		nodecpuconfig = null;
		//----------------����cpuconfig����--���������---------------------        	
		 cpuconfigContent = "";
		tmpPt = Pattern.compile("(cmdbegin:allconfig)(.*)(cmdbegin:disk\n)",Pattern.DOTALL);
		mr = tmpPt.matcher(fileContent.toString());
		if(mr.find())
		{
			cpuconfigContent = mr.group(2);
			//System.out.println("================cpuconfig======================");
			///System.out.println(cpuconfigContent);
			//System.out.println("=================cpuconfig=====================");
			
		} 
		 cpuconfigLineArr = cpuconfigContent.split("\n");
		 cpuconfig_tmpData = null;
		cpuconfiglist = new ArrayList<Nodecpuconfig>();
		nodecpuconfig = new Nodecpuconfig();
		procesors = "";
		processorType = "";
		processorSpeed = "";
		for(int i=0; i<cpuconfigLineArr.length;i++){
			String[] result = cpuconfigLineArr[i].trim().split(":");
			if (result.length>0){
				if(result[0].trim().equalsIgnoreCase("Number Of Processors")){
					//����������
					//���ýڵ��CPU���ø���
					
					nodeconfig.setNumberOfProcessors(result[1].trim()+"");
					//System.out.println("======CPU����===="+result[1].trim()+"");
				}else if(result[0].trim().equalsIgnoreCase("CPU Type")){
					//CPU����λ
					if(nodeconfig.getNumberOfProcessors()!= null && nodeconfig.getNumberOfProcessors().trim().length()>0){
						int pnum = Integer.parseInt(nodeconfig.getNumberOfProcessors());
						for(int k=0;k<pnum;k++){
							nodecpuconfig.setDataWidth(result[1].trim()+"");
							nodecpuconfig.setProcessorId(k+"");
							nodecpuconfig.setName("");
							nodecpuconfig.setNodeid(host.getId());
							nodecpuconfig.setL2CacheSize("");
							nodecpuconfig.setL2CacheSpeed("");
							nodecpuconfig.setProcessorType(processorType);
							nodecpuconfig.setProcessorSpeed(processorSpeed);
							cpuconfiglist.add(nodecpuconfig);
							nodecpuconfig = new Nodecpuconfig();
						}
					}
				}else if(result[0].trim().equalsIgnoreCase("Processor Type")){
					//CPU����
					processorType = result[1].trim()+"";
//					if(nodeconfig.getNumberOfProcessors()!= null && nodeconfig.getNumberOfProcessors().trim().length()>0){
//						int pnum = Integer.parseInt(nodeconfig.getNumberOfProcessors());
//						for(int k=0;k<pnum;k++){
//							Nodecpuconfig _nodecpuconfig = (Nodecpuconfig)cpuconfiglist.get(k);
//							_nodecpuconfig.setProcessorType(result[1].trim()+"");
//							cpuconfiglist.add(k, _nodecpuconfig);
//						}
//					}
				}else if(result[0].trim().equalsIgnoreCase("Processor Clock Speed")){
					//CPU�ں���Ƶ
					processorSpeed = result[1].trim()+"";
//					if(nodeconfig.getNumberOfProcessors()!= null && nodeconfig.getNumberOfProcessors().trim().length()>0){
//						int pnum = Integer.parseInt(nodeconfig.getNumberOfProcessors());
//						SysLogger.info("cpuconfiglist========size:"+cpuconfiglist.size());
//						for(int k=0;k<pnum;k++){
//							Nodecpuconfig _nodecpuconfig = (Nodecpuconfig)cpuconfiglist.get(k);
//							_nodecpuconfig.setProcessorSpeed(result[1].trim()+"");
//							cpuconfiglist.add(k, _nodecpuconfig);
//						}
//					}
				}else if(result[0].trim().equalsIgnoreCase("Good Memory Size")){
					String allphy = result[1].trim().trim();
					try{
						allphy = allphy.replaceAll("MB", "");
						PhysicalMemCap = Float.parseFloat(allphy);
					}catch(Exception e){
						e.printStackTrace();
					}
					//nodecpuconfig.setDataWidth(result[1].trim()+"");
				}else if(result[0].trim().equalsIgnoreCase("IP Address") && result.length==2){
					//IP��ַ
					networkconfig.put("IP",result[1].trim()+"");
				}else if(result[0].trim().equalsIgnoreCase("Sub Netmask") && result.length==2 ){
					//��������
					networkconfig.put("NETMASK",result[1].trim()+"");
				}else if(result[0].trim().equalsIgnoreCase("Gateway") && result.length==2){
					//����
					networkconfig.put("GATEWAY",result[1].trim()+"");
				}else if(result[0].trim().equalsIgnoreCase("Total Paging Space")){
					//��ҳ�ռ���Ϣ
					String allphy = result[1].trim().trim();
					try{
						allphy = allphy.replaceAll("MB", "");
						allPhyPagesSize = Float.parseFloat(allphy);
					}catch(Exception e){
						e.printStackTrace();
					}
				}else if(result[0].trim().equalsIgnoreCase("Percent Used")){
					//��ʹ�õĵ�ҳ�ռ���Ϣ
					String allphy = result[1].trim().trim();
					try{
						allphy = allphy.replaceAll("%", "");
						usedPhyPagesSize = Float.parseFloat(allphy);
					}catch(Exception e){
						e.printStackTrace();
					}
				}
				
											
			}
			
		}
		nodecpuconfig = null;
		
		//System.out.println("=========================6==============================");
		//��----------------����disk����--���������---------------------
		//disk���ݼ��ϣ��仯ʱ���и澯���
		Hashtable<String,Object> diskInfoHash = new Hashtable<String,Object>();
		//���̴�С
		float diskSize = 0;
		//�������Ƽ���
		List<String> diskNameList = new ArrayList<String>();
		String diskContent = "";
		String diskLabel;
		List disklist = new ArrayList();
		tmpPt = Pattern.compile("(cmdbegin:disk\n)(.*)(cmdbegin:diskperf)",Pattern.DOTALL);
		mr = tmpPt.matcher(fileContent.toString());
		if(mr.find())
		{
			diskContent = mr.group(2);
			//System.out.println("==================(cmdbegin:disk\n)(.*)(cmdbegin:diskperf)======================");
			//System.out.println(diskContent);
			//System.out.println("==================(cmdbegin:disk\n)(.*)(cmdbegin:diskperf)======================");
			
		}
		String[] diskLineArr = diskContent.split("\n");
		String[] tmpData = null;
		Diskcollectdata diskdata=null;
		int diskflag = 0;
		for(int i=1; i<diskLineArr.length;i++)
		{
			
			tmpData = diskLineArr[i].split("\\s++");
			if((tmpData != null) && (tmpData.length == 7))
			{
				diskLabel = tmpData[6];
				
				diskdata=new Diskcollectdata();
				diskdata.setIpaddress(host.getIpAddress());
				diskdata.setCollecttime(date);
				diskdata.setCategory("Disk");
				diskdata.setEntity("Utilization");//���ðٷֱ�
				diskdata.setSubentity(tmpData[6]);//TODO subentity 
				diskdata.setRestype("static");
				diskdata.setUnit("%");
				try{
				diskdata.setThevalue(
						Float.toString(
								Float.parseFloat(
								tmpData[3].substring(
								0,
								tmpData[3].indexOf("%")))));
				}catch(Exception ex){
					continue;
				}
				diskVector.addElement(diskdata);

				//yangjun 
				try {
					String diskinc = "0.0";
					float pastutil = 0.0f;
					Vector disk_v = (Vector)ipAllData.get("disk");
					if (disk_v != null && disk_v.size() > 0) {
						for (int si = 0; si < disk_v.size(); si++) {
							Diskcollectdata disk_data = (Diskcollectdata) disk_v.elementAt(si);
							if((tmpData[6]).equals(disk_data.getSubentity())&&"Utilization".equals(disk_data.getEntity())){
								pastutil = Float.parseFloat(disk_data.getThevalue());
							}
						}
					} else {
						pastutil = Float.parseFloat(tmpData[3].substring(0,tmpData[3].indexOf("%")));
					}
					if (pastutil == 0) {
						pastutil = Float.parseFloat(
								tmpData[3].substring(
										0,
										tmpData[3].indexOf("%")));
					}
					if(Float.parseFloat(
									tmpData[3].substring(
									0,
									tmpData[3].indexOf("%")))-pastutil>0){
						diskinc = (Float.parseFloat(
										tmpData[3].substring(
										0,
										tmpData[3].indexOf("%")))-pastutil)+"";
					}
					diskdata = new Diskcollectdata();
					diskdata.setIpaddress(host.getIpAddress());
					diskdata.setCollecttime(date);
					diskdata.setCategory("Disk");
					diskdata.setEntity("UtilizationInc");// ���������ʰٷֱ�
					diskdata.setSubentity(tmpData[6]);
					diskdata.setRestype("dynamic");
					diskdata.setUnit("%");
					diskdata.setThevalue(diskinc);
					diskVector.addElement(diskdata);
				} catch (Exception e) {
					e.printStackTrace();
				}
				//
				
				diskdata=new Diskcollectdata();
				diskdata.setIpaddress(host.getIpAddress());
				diskdata.setCollecttime(date);
				diskdata.setCategory("Disk");
				diskdata.setEntity("AllSize");//�ܿռ�
				diskdata.setSubentity(tmpData[6]);
				diskdata.setRestype("static");

				float allblocksize=0;
				allblocksize=Float.parseFloat(tmpData[1]);
				float allsize=0.0f;
				allsize=allblocksize;
				//�����ܴ�С  ��λΪM
				diskSize = diskSize + allsize;
				//�������Ʒ��뼯��
				if (!diskdata.getSubentity().equals("")) {
					diskNameList.add(diskdata.getSubentity());
				}
				if(allsize>=1024.0f){
					allsize=allsize/1024;
					diskdata.setUnit("G");
				}
				else{
					diskdata.setUnit("M");
				}

				diskdata.setThevalue(Float.toString(allsize));
				diskVector.addElement(diskdata);

				diskdata=new Diskcollectdata();
				diskdata.setIpaddress(host.getIpAddress());
				diskdata.setCollecttime(date);
				diskdata.setCategory("Disk");
				diskdata.setEntity("UsedSize");//ʹ�ô�С
				diskdata.setSubentity(tmpData[6]);
				diskdata.setRestype("static");

				float FreeintSize=0;
				FreeintSize=Float.parseFloat(tmpData[2]);
				
				
				float usedfloatsize=0.0f;
				usedfloatsize = allblocksize - FreeintSize;
				if(usedfloatsize>=1024.0f){
					usedfloatsize=usedfloatsize/1024;
					diskdata.setUnit("G");
				}
				else{
					diskdata.setUnit("M");
				}
				diskdata.setThevalue(Float.toString(usedfloatsize));
				diskVector.addElement(diskdata);
				disklist.add(diskflag,diskLabel);
				diskflag = diskflag +1;
			}
		}
		
	    //���д��̸澯���
	    //SysLogger.info("### ��ʼ���м������Ƿ�澯### ... ###");
	    try{
			AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
			List list = alarmIndicatorsUtil.getAlarmInicatorsThresholdForNode(String.valueOf(host.getId()), AlarmConstant.TYPE_HOST, "aix");
			for(int i = 0 ; i < list.size() ; i ++){
				AlarmIndicatorsNode alarmIndicatorsnode = (AlarmIndicatorsNode)list.get(i);
				//SysLogger.info("alarmIndicatorsnode name ======"+alarmIndicatorsnode.getName());
				if(alarmIndicatorsnode.getName().equalsIgnoreCase("diskperc") || alarmIndicatorsnode.getName().equalsIgnoreCase("diskinc") ){
					
					//System.out.println("=======***disk****89898989=========="+alarmIndicatorsnode.getName());
					CheckEventUtil checkutil = new CheckEventUtil();
				    checkutil.checkDisk(host,diskVector,alarmIndicatorsnode);
				   // break;
				}
				
			}
			//##########�ܴ�С�Լ��̷���Ϣ�仯�����и澯�ж�
			//diskSize = diskSize/1024;
			//diskInfoHash.put("diskSize", diskSize+"G");
			//diskInfoHash.put("diskNameList", diskNameList);
			//CheckEventUtil checkutil = new CheckEventUtil();
	    	//checkutil.hardwareInfo(host, "disk", diskInfoHash);
	    }catch(Exception e){
	    	e.printStackTrace();
	    }
		
	    //System.out.println("=========================7==============================");
		//----------------����diskperf����--���������---------------------        	
		String diskperfContent = "";
		String average = "";
		tmpPt = Pattern.compile("(cmdbegin:diskperf)(.*)(cmdbegin:diskiostat)",Pattern.DOTALL);
		mr = tmpPt.matcher(fileContent.toString());
		if(mr.find())
		{
			diskperfContent = mr.group(2);
			//System.out.println("==============cmdbegin:diskperf====================");
			//System.out.println(diskperfContent);
			//System.out.println("===============cmdbegin:diskperf===================");
			
		} 
		String[] diskperfLineArr = diskperfContent.split("\n");
		String[] diskperf_tmpData = null;
		List alldiskperf = new ArrayList();
		Hashtable<String,String> diskperfhash = new Hashtable<String,String>();
		int flag = 0;
		for(int i=0; i<diskperfLineArr.length;i++){
			diskperf_tmpData = diskperfLineArr[i].trim().split("\\s++");
			if(diskperf_tmpData != null && (diskperf_tmpData.length==7 || diskperf_tmpData.length==8)){
				if(diskperf_tmpData[0].trim().equalsIgnoreCase("Average")){
					flag = 1;
					diskperfhash.put("%busy", diskperf_tmpData[2].trim());
					diskperfhash.put("avque", diskperf_tmpData[3].trim());
					diskperfhash.put("r+w/s", diskperf_tmpData[4].trim());
					diskperfhash.put("Kbs/s", diskperf_tmpData[5].trim());
					diskperfhash.put("avwait", diskperf_tmpData[6].trim());
					diskperfhash.put("avserv", diskperf_tmpData[7].trim());
					diskperfhash.put("disklebel", diskperf_tmpData[1].trim());
					alldiskperf.add(diskperfhash);
				}else if(flag == 1){
					diskperfhash.put("%busy", diskperf_tmpData[1].trim());
					diskperfhash.put("avque", diskperf_tmpData[2].trim());
					diskperfhash.put("r+w/s", diskperf_tmpData[3].trim());
					diskperfhash.put("Kbs/s", diskperf_tmpData[4].trim());
					diskperfhash.put("avwait", diskperf_tmpData[5].trim());
					diskperfhash.put("avserv", diskperf_tmpData[6].trim());
					diskperfhash.put("disklebel", diskperf_tmpData[0].trim());
					alldiskperf.add(diskperfhash);
				}
				
				diskperfhash = new Hashtable();
			}				
		}
//		----------------����diskiostat����--���������----cmdbegin:diskperf-----------------        	
		String diskioContent = "";
		tmpPt = Pattern.compile("(cmdbegin:diskiostat)(.*)(cmdbegin:netperf)",Pattern.DOTALL);
		mr = tmpPt.matcher(fileContent.toString());
		if(mr.find())
		{
			diskioContent = mr.group(2);
			//System.out.println("==============cmdbegin:diskperf====================");
			//System.out.println(diskioContent);
			//System.out.println("===============cmdbegin:diskperf===================");
			
		} 
		
		//System.out.println("==================(cmdbegin:diskiostat)(.*)(cmdbegin:netperf)==============");
		//System.out.println(diskioContent);
		//System.out.println("==================(cmdbegin:diskiostat)(.*)(cmdbegin:netperf)==============");
		String[] diskioLineArr = diskioContent.split("\n");
		String[] diskio_tmpData = null;
		List alldiskio = new ArrayList();
		Hashtable<String,String> diskpiohash = new Hashtable<String,String>();
		int flags = 0;
		for(int i=0; i<diskioLineArr.length;i++){
			diskio_tmpData = diskioLineArr[i].trim().split("\\s++");
			if(diskio_tmpData != null){
				//if("cmdbegin:netperf".equalsIgnoreCase(diskio_tmpData[0].trim()))continue;
//				System.out.println("diskio_tmpData[0].trim()==="+diskio_tmpData[0].trim());
				if(diskio_tmpData[0].trim().equalsIgnoreCase("Disks:") || "���̣�".equals(diskio_tmpData[0].trim())){
					flags = 1;
					continue;
				} 
				
				if(flags==1){
					SysLogger.info(diskio_tmpData[0].trim());
					diskpiohash.put("Disks", diskio_tmpData[0].trim());
					diskpiohash.put("%tm_act", diskio_tmpData[1].trim());
					diskpiohash.put("Kbps", diskio_tmpData[2].trim());
					diskpiohash.put("tps", diskio_tmpData[3].trim());
					diskpiohash.put("kb_read", diskio_tmpData[4].trim());
					diskpiohash.put("kb_wrtn", diskio_tmpData[5].trim());
					alldiskio.add(diskpiohash);
				}
				diskpiohash = new Hashtable();
			}				
		}
		//----------------����netperf����--���������---------------------        	
		String netperfContent = "";
		tmpPt = Pattern.compile("(cmdbegin:netperf)(.*)(cmdbegin:netallperf)",Pattern.DOTALL);
		mr = tmpPt.matcher(fileContent.toString());
		if(mr.find())
		{
			netperfContent = mr.group(2);
			

		} 
		String[] netperfLineArr = netperfContent.split("\n");
		String[] netperf_tmpData = null;
		List netperf = new ArrayList();
		//Hashtable<String,String> netnamehash = new Hashtable<String,String>();
		//int flag = 0;
		for(int i=0; i<netperfLineArr.length;i++){
			netperf_tmpData = netperfLineArr[i].trim().split("\\s++");
			//System.out.println("=============================����==="+netperf_tmpData.length);
			if(netperf_tmpData != null && netperf_tmpData.length==9){
				if(netperf_tmpData[0].trim().indexOf("en")>=0 && netperf_tmpData[2].trim().indexOf("link")>=0){
					
					//System.out.println("="+netperf_tmpData[0].trim());
					netperf.add(netperf_tmpData[0].trim());
				}
			}else if(netperf_tmpData != null && netperf_tmpData.length==10){
				if(netperf_tmpData[0].trim().indexOf("en")>=0 && netperf_tmpData[2].trim().indexOf("link")>=0){
					netperf.add(netperf_tmpData[0].trim());
				}
			}
				
		}
		
		
		//----------------����netallperf����--���������---------------------   
		List iflist = new ArrayList();
		List oldiflist = new ArrayList();
		List netmedialist = new ArrayList();
		Hashtable netmediahash = new Hashtable();
		String netallperfContent = "";
		//String average = "";
		tmpPt = Pattern.compile("(cmdbegin:netallperf)(.*)(cmdbegin:uname)",Pattern.DOTALL);
		mr = tmpPt.matcher(fileContent.toString());
		if(mr.find())
		{
			netallperfContent = mr.group(2);
			
			//System.out.println("========================netallperf======================");
			//System.out.println(netallperfContent);
			//System.out.println("==========================netallperf====================");
			
		} 
		String[] netallperfLineArr = netallperfContent.trim().split("\n");
		String[] netallperf_tmpData = null;
		List netalldiskperf = new ArrayList();
		Hashtable<String,String> netallperfhash = new Hashtable<String,String>();
		int macflag = 0;
		String MAC = "";
		//Hashtable ipAllData = (Hashtable)ShareData.getSharedata().get(host.getIpAddress());
		
		if(ipAllData != null){
			oldiflist = (List)ipAllData.get("iflist");
		}
		
		if(netperf != null && netperf.size()>0){
			//System.out.println("-------------------�������-----------------------------");
			Interfacecollectdata interfacedata = null;
			
			//��ʼѭ������ӿ�
			for(int k=0;k<netperf.size();k++){
				
				
				Hashtable ifhash = new Hashtable();
				Hashtable oldifhash = new Hashtable();//���������ϴβɼ����
				if(oldiflist != null && oldiflist.size()>0){
					oldifhash = (Hashtable)oldiflist.get(k);
				}
				
				
				String portDesc = (String)netperf.get(k);//en1
				//int index=0;//������λ
				
				//ͨ��
				  
				 /**
				  * ����aix ͬһ������ɼ������ݳ��ֲ�ͬ�����ݸ�ʽ��Ҫ����������ж�
				  * ��һ�������������ģʽ
				  * ģʽһ 
				  * ETHERNET STATISTICS (en1
				  * netflg="neten"
				  * ģʽ����
				  * Hardware Address:
				  * netflg="netmac"
				  * ʹ��һ�� netflg �ж���ʲôģʽ
				  * 
				  */
				
				 String netflg="";
				 
				 if(netallperfContent.indexOf("ETHERNET STATISTICS ("+portDesc)>0){
					 netflg="neten";
					 
				 }
				 
				 for(int i=0;i<netallperfLineArr.length;i++)
				 {
					// if(netallperfContent.indexOf("ETHERNET STATISTICS ("+portDesc)>0){
					   // index=i;
					   // netflg="neten";
					   // break;	 
					// }
					 
					 if(i==1 && netallperfLineArr[i].indexOf("Hardware Address:")>=0)
					 {
						 netflg="netmac";
						 //index=k*13;
						 break;	 
					 }
					 
				 }
				
				//IDC�汾��ȥmac��ַ
				 
				 String mideaspeed = "";//��������
				 String status = "";//����״̬
				 String Bytes = "";//������������ֽ���
				 String Packets = "";//����������ݰ�
				 String LinkStatus = "";//״̬
				// System.out.println("8888888888888888888888888888888888888888888888888888888"+netflg);
				 //ETHERNET STATISTICS (en1 ģʽ
				if(netflg.equals("neten"))	
				{
					
					//System.out.println("=+++++++============neten=====================neten=====neten==");
					
					tmpPt = Pattern.compile("(start-"+portDesc+")(.*)(end-"+portDesc+")",Pattern.DOTALL);
					mr = tmpPt.matcher(fileContent.toString());
					String netenContent="";
					if(mr.find())
					{
						netenContent = mr.group(2);
						//System.out.println("====================================");
						//System.out.println(netenContent);
						//System.out.println("====================================");

					} 
					
					String [] netLineArr=null;
					netLineArr=netenContent.trim().split("\n");
					//System.out.println("&&&&&&&"+netLineArr[3]);
			        MAC = netLineArr[3].trim().substring(netLineArr[3].trim().indexOf("Hardware Address:"));  
			        MAC = MAC.replaceAll("Hardware Address:", "").trim();
				try{
					
					mideaspeed = netLineArr[45].trim().substring(netLineArr[45].trim().indexOf("Media Speed Running:")); 
					status = netLineArr[43].trim().substring(netLineArr[43].trim().indexOf("Link Status :")); 						
					Packets = netLineArr[8].trim(); 
					LinkStatus=status;
					Bytes = netLineArr[9].trim(); 

				 
				}catch(Exception e){
					e.printStackTrace();
				}
			}	
					
		//	Hardware Address ��ʼ�ĸ�ʽ
		if(netflg.equals("netmac"))		
			{
			
			tmpPt = Pattern.compile("(start-"+portDesc+")(.*)(end-"+portDesc+")",Pattern.DOTALL);
			mr = tmpPt.matcher(fileContent.toString());
			String netenContent="";
			if(mr.find())
			{
				netenContent = mr.group(2);
				
				//System.out.println("========netenContent======================"+"(start-"+portDesc+")(.*)(end-"+portDesc+")");
				//System.out.println(netenContent);
				//System.out.println("=====================netenContent===================="+"(start-"+portDesc+")(.*)(end-"+portDesc+")");
				
			} 
			String [] netLineArr=null;
			netLineArr=netenContent.trim().split("\n");
			
			if(netLineArr.length==13)
			{
			
			       // System.out.println("Mac ��ַ��=="+netallperfLineArr[index+2]);	  
			    	MAC = netLineArr[0].trim().substring(netLineArr[0].trim().indexOf("Hardware Address:"));  
			    	MAC = MAC.replaceAll("Hardware Address:", "").trim();
				  //System.out.println("============MAC="+MAC);
				
				try{
					
					
					 //System.out.println("mideaspeed ��ַ��=="+netallperfLineArr[index+ 44]);
					 mideaspeed = netLineArr[11].trim().substring(netLineArr[11].trim().indexOf("Media Speed Running:")); 
				     //System.out.println("==��·״̬======"+netLineArr[10]);
					 if(netLineArr[10].indexOf("Link Status :")>=0)
					 {
					
						 status = netLineArr[10].trim().substring(netLineArr[10].trim().indexOf("Link Status :")); 
						 status=netLineArr[10].replaceAll("Link Status :", "").trim();
					 }
					 
					 
					 LinkStatus=status;
					Packets = netLineArr[1].trim(); 
					Bytes = netLineArr[2].trim(); 
	
				}catch(Exception e){
					e.printStackTrace();
				}
			}
			}
				
					
					
				
				String mac_ = nodeconfig.getMac();
				if(mac_ != null && mac_.trim().length()>0){
					
					//�����������࣬�����ֵ���ѽ���Ū����
					if(k<3)
					{
						mac_=mac_+","+MAC;
					}
					
					nodeconfig.setMac(mac_);
				}else{
					nodeconfig.setMac(MAC);
				}
			
				
				
				status=	status.replaceAll("Link Status :", "").trim();
				netmediahash.put("desc", portDesc);//����
				netmediahash.put("speed", mideaspeed);//����
				netmediahash.put("mac", MAC);
				netmediahash.put("status", status);//����״��
				netmedialist.add(netmediahash);
				netmediahash = new Hashtable();	
				
				
				//=================�������ݰ�==================
				String outPackets ="0";
				String inPackets ="0";
				if(Packets.indexOf("Packets:")>=0)
				{
				String[] packsperf_tmpData = Packets.split("\\s++");
				
				outPackets = packsperf_tmpData[1];//���͵����ݰ�
				inPackets = packsperf_tmpData[3];//���ܵ����ݰ�
				}
				String oldOutPackets = "0";
				String oldInPackets = "0";
				String endOutPackets = "0";
				String endInPackets = "0";
				
				if(oldifhash != null && oldifhash.size()>0){
					if(oldifhash.containsKey("outPackets")){
						oldOutPackets = (String)oldifhash.get("outPackets");
					}
					try{
						endOutPackets = (Long.parseLong(outPackets)-Long.parseLong(oldOutPackets))+"";
					}catch(Exception e){
						e.printStackTrace();
					}
					if(oldifhash.containsKey("inPackets")){
						oldInPackets = (String)oldifhash.get("inPackets");
					}
					try{
						endInPackets = (Long.parseLong(inPackets)-Long.parseLong(oldInPackets))+"";
					}catch(Exception e){
						e.printStackTrace();
					}
				}
				
		
				String outBytes ="0";
				String inBytes ="0";
				if(Bytes.indexOf("Bytes:")>=0)
				{
				String[] bytes_tmpData = Bytes.split("\\s++");
				outBytes = bytes_tmpData[1];//���͵��ֽ�
				inBytes = bytes_tmpData[3];//���ܵ��ֽ�
				
				//System.out.println("====���͵����ݰ�="+outPackets);
				//System.out.println("====���ܵ����ݰ�="+inPackets);
				}
				
				

				//===�����ֽ���=====================
				
				String oldOutBytes = "0";
				String oldInBytes = "0";
				String endOutBytes = "0";
				String endInBytes = "0";
				
				if(oldifhash != null && oldifhash.size()>0){
					if(oldifhash.containsKey("outBytes")){
						oldOutBytes = (String)oldifhash.get("outBytes");
					}
					try{
						endOutBytes = (Long.parseLong(outBytes)-Long.parseLong(oldOutBytes))*8/1024/300+"";
					}catch(Exception e){
						e.printStackTrace();
					}
					if(oldifhash.containsKey("inBytes")){
						oldInBytes = (String)oldifhash.get("inBytes");
					}
					try{
						endInBytes = (Long.parseLong(inBytes)-Long.parseLong(oldInBytes))*8/1024/300+"";
					}catch(Exception e){
						e.printStackTrace();
					}
				}				
				
				
				
				//ע�͵ļ�����ֵĿǰ��û������
				//String Interrupts = netallperfLineArr[k*13+3].trim();
				//String PacketsDropped = netallperfLineArr[k*13+4].trim();
				//String BroadcastPackets = netallperfLineArr[k*13+7].trim();
				//String MulticastPackets = netallperfLineArr[k*13+8].trim();
				
				
				//System.out.println("&&&&&&&&&&&&&&&&&&&77==="+LinkStatus);
				
//				String[] link_tmpData = LinkStatus.split(":");
//				String linkstatus ="";
//				if(link_tmpData.length>1)
//				{
//					linkstatus = link_tmpData[1].toLowerCase();
//				}
				String linkstatus ="";
				//System.out.println("&&&&&&&&&&**************************"+LinkStatus);
				linkstatus=LinkStatus.replaceAll("Link Status :", "").trim();
			    if (linkstatus.equals("Up"))
			    {
			    	//System.out.println("---------- Up");
			    	linkstatus="1";
			    }else if(linkstatus.equals("Down"))
			     {
			    	//System.out.println("---------- Down");
			    	linkstatus="2";
			     }
				//System.out.println("7788877=="+linkstatus);
				//============����===============
				String MediaSpeedRunning =mideaspeed;
				String speedunit = "";
				String speedstr = "";
				String mspeed ="0";
				if(MediaSpeedRunning.indexOf(":")>=0)
				{
				String[] speed_tmpData = MediaSpeedRunning.split(":");
				
				
			    mspeed = speed_tmpData[1].trim();
				String[] speed = mspeed.split("\\s++");
				
				if(speed.length>0){
				speedstr = speed[0];
				}else{
				speedstr = "0";
				}
				
				if(speed.length>1){
				speedunit = speed[1];
				}else{
				speedunit = "Mbps";
				}
				}
				
				
				ifhash.put("outPackets", outPackets);
				ifhash.put("inPackets", inPackets);
				ifhash.put("outBytes", outBytes);
				ifhash.put("inBytes", inBytes);
				
				
			   	//�˿�����
				interfacedata=new Interfacecollectdata();
				interfacedata.setIpaddress(ipaddress);
				interfacedata.setCollecttime(date);
				interfacedata.setCategory("Interface");
				interfacedata.setEntity("index");
				interfacedata.setSubentity(k+1+"");
				//�˿�״̬�����棬ֻ��Ϊ��̬���ݷŵ���ʱ����
				interfacedata.setRestype("static");
				interfacedata.setUnit("");
				interfacedata.setThevalue(k+1+"");
				interfacedata.setChname("�˿�����");
				interfaceVector.addElement(interfacedata);
				//�˿�����
				interfacedata=new Interfacecollectdata();
				interfacedata.setIpaddress(ipaddress);
				interfacedata.setCollecttime(date);
				interfacedata.setCategory("Interface");
				interfacedata.setEntity("ifDescr");
				interfacedata.setSubentity(k+1+"");
				//�˿�״̬�����棬ֻ��Ϊ��̬���ݷŵ���ʱ����
				interfacedata.setRestype("static");
				interfacedata.setUnit("");
				interfacedata.setThevalue(portDesc);
				interfacedata.setChname("�˿�����2");
				interfaceVector.addElement(interfacedata);
				//�˿ڴ���
				interfacedata=new Interfacecollectdata();
				interfacedata.setIpaddress(ipaddress);
				interfacedata.setCollecttime(date);
				interfacedata.setCategory("Interface");
				interfacedata.setEntity("ifSpeed");
				interfacedata.setSubentity(k+1+"");
				interfacedata.setRestype("static");
				interfacedata.setUnit(speedunit);
				interfacedata.setThevalue(speedstr);
				interfacedata.setChname("");
				interfaceVector.addElement(interfacedata);
				//��ǰ״̬
				interfacedata=new Interfacecollectdata();
				interfacedata.setIpaddress(ipaddress);
				interfacedata.setCollecttime(date);
				interfacedata.setCategory("Interface");
				interfacedata.setEntity("ifOperStatus");
				interfacedata.setSubentity(k+1+"");
				interfacedata.setRestype("static");
				interfacedata.setUnit("");
				interfacedata.setThevalue(linkstatus);
				interfacedata.setChname("��ǰ״̬");
				interfaceVector.addElement(interfacedata);
				//��ǰ״̬
				interfacedata=new Interfacecollectdata();
				interfacedata.setIpaddress(ipaddress);
				interfacedata.setCollecttime(date);
				interfacedata.setCategory("Interface");
				interfacedata.setEntity("ifOperStatus");
				interfacedata.setSubentity(k+1+"");
				interfacedata.setRestype("static");
				interfacedata.setUnit("");
				interfacedata.setThevalue(1+"");
				interfacedata.setChname("��ǰ״̬");
				interfaceVector.addElement(interfacedata);
				//�˿��������
				UtilHdx utilhdx=new UtilHdx();
				utilhdx.setIpaddress(ipaddress);
				utilhdx.setCollecttime(date);
				utilhdx.setCategory("Interface");
				String chnameBand="";
				utilhdx.setEntity("InBandwidthUtilHdx");
				utilhdx.setThevalue(endInBytes);
				utilhdx.setSubentity(k+1+"");
				utilhdx.setRestype("dynamic");
				utilhdx.setUnit("Kb/��");	
				utilhdx.setChname(k+1+"�˿����"+"����");
				utilhdxVector.addElement(utilhdx);
				//�˿ڳ�������
				utilhdx=new UtilHdx();
				utilhdx.setIpaddress(ipaddress);
				utilhdx.setCollecttime(date);
				utilhdx.setCategory("Interface");
				utilhdx.setEntity("OutBandwidthUtilHdx");
				utilhdx.setThevalue(endOutBytes);
				utilhdx.setSubentity(k+1+"");
				utilhdx.setRestype("dynamic");
				utilhdx.setUnit("Kb/��");	
				utilhdx.setChname(k+1+"�˿ڳ���"+"����");
				utilhdxVector.addElement(utilhdx);
				/*
				//���������ݰ�
				utilhdx=new UtilHdx();
				utilhdx.setIpaddress(ipaddress);
				utilhdx.setCollecttime(date);
				utilhdx.setCategory("Interface");
				utilhdx.setChname("��վ�����������ݰ�");
				utilhdx.setEntity("ifInDiscards");
				utilhdx.setThevalue((String)rValue.get("PacketsReceivedDiscarded"));
				utilhdx.setSubentity(i+"");
				utilhdx.setRestype("dynamic");
				utilhdx.setUnit("��");
				utilhdxVector.addElement(utilhdx);
				//��վ�������ݰ�
				utilhdx=new UtilHdx();
				utilhdx.setIpaddress(ipaddress);
				utilhdx.setCollecttime(date);
				utilhdx.setCategory("Interface");
				utilhdx.setChname("��վ�������ݰ�");
				utilhdx.setEntity("ifInErrors");
				utilhdx.setThevalue((String)rValue.get("PacketsReceivedErrors"));
				utilhdx.setSubentity(k+"");
				utilhdx.setRestype("dynamic");
				utilhdx.setUnit("��");
				utilhdxVector.addElement(utilhdx);
				//��ڷǵ��������ݰ�
				utilhdx=new UtilHdx();
				utilhdx.setIpaddress(ipaddress);
				utilhdx.setCollecttime(date);
				utilhdx.setCategory("Interface");
				utilhdx.setChname("�ǵ��������ݰ�");
				utilhdx.setEntity("ifInNUcastPkts");
				utilhdx.setThevalue((String)rValue.get("PacketsReceivedNonUnicastPersec"));
				utilhdx.setSubentity(k+"");
				utilhdx.setRestype("dynamic");
				utilhdx.setUnit("��");
				utilhdxVector.addElement(utilhdx);
				//��ڵ��������ݰ�
				utilhdx=new UtilHdx();
				utilhdx.setIpaddress(ipaddress);
				utilhdx.setCollecttime(date);
				utilhdx.setCategory("Interface");
				utilhdx.setChname("���������ݰ�");
				utilhdx.setEntity("ifInUcastPkts");
				utilhdx.setThevalue((String)rValue.get("PacketsReceivedUnicastPersec"));
				utilhdx.setSubentity(k+"");
				utilhdx.setRestype("dynamic");
				utilhdx.setUnit("��");
				utilhdxVector.addElement(utilhdx);
				//���ڷǵ��������ݰ�
				utilhdx=new UtilHdx();
				utilhdx.setIpaddress(ipaddress);
				utilhdx.setCollecttime(date);
				utilhdx.setCategory("Interface");
				utilhdx.setChname("�ǵ��������ݰ�");
				utilhdx.setEntity("ifOutNUcastPkts");
				utilhdx.setThevalue((String)rValue.get("PacketsSentNonUnicastPersec"));
				utilhdx.setSubentity(k+"");
				utilhdx.setRestype("dynamic");
				utilhdx.setUnit("��");
				utilhdxVector.addElement(utilhdx);
				//���ڵ��������ݰ�
				utilhdx=new UtilHdx();
				utilhdx.setIpaddress(ipaddress);
				utilhdx.setCollecttime(date);
				utilhdx.setCategory("Interface");
				utilhdx.setChname("���������ݰ�");
				utilhdx.setEntity("ifOutUcastPkts");
				utilhdx.setThevalue((String)rValue.get("PacketsSentUnicastPersec"));
				utilhdx.setSubentity(k+"");
				utilhdx.setRestype("dynamic");
				utilhdx.setUnit("��");
				utilhdxVector.addElement(utilhdx);
				*/
			   iflist.add(ifhash);
			   ifhash = new Hashtable();
				
				
			}
		}
		systemdata=new Systemcollectdata();
		systemdata.setIpaddress(ipaddress);
		systemdata.setCollecttime(date);
		systemdata.setCategory("System");
		systemdata.setEntity("MacAddr");
		systemdata.setSubentity("MacAddr");
		systemdata.setRestype("static");
		systemdata.setUnit(" ");			  
		systemdata.setThevalue(MAC);
		systemVector.addElement(systemdata);
		
		//----------------����cpu����--���������---------------------        	
		String cpuperfContent = "";
		//String average = "";
		tmpPt = Pattern.compile("(cmdbegin:cpu)(.*)(cmdbegin:allconfig)",Pattern.DOTALL);
		mr = tmpPt.matcher(fileContent.toString());
		if(mr.find())
		{
			
			cpuperfContent = mr.group(2);
			//System.out.println("===============cpu=================");
			//System.out.println(cpuperfContent);
			//System.out.println("===============cpu=================");

		} 
		String[] cpuperfLineArr = cpuperfContent.split("\n");
		List cpuperflist = new ArrayList();
		Hashtable<String,String> cpuperfhash = new Hashtable<String,String>();
		for(int i=0; i<cpuperfLineArr.length;i++){
			diskperf_tmpData = cpuperfLineArr[i].trim().split("\\s++");
			if(diskperf_tmpData != null && diskperf_tmpData.length ==5 || diskperf_tmpData.length==6 || diskperf_tmpData.length==7){
				
				
				if(diskperf_tmpData[0].trim().equalsIgnoreCase("Average") || "ƽ��ֵ".equals(diskperf_tmpData[0].trim())){
						cpuperfhash.put("%usr", diskperf_tmpData[1].trim());
						cpuperfhash.put("%sys", diskperf_tmpData[2].trim());
						cpuperfhash.put("%wio", diskperf_tmpData[3].trim());
						cpuperfhash.put("%idle", diskperf_tmpData[4].trim());
						if(diskperf_tmpData.length==6||diskperf_tmpData.length==7)
						{
						cpuperfhash.put("physc", diskperf_tmpData[5].trim());
						}
						cpuperflist.add(cpuperfhash);
						
						cpudata=new CPUcollectdata();
				   		cpudata.setIpaddress(ipaddress);
				   		cpudata.setCollecttime(date);
				   		cpudata.setCategory("CPU");
				   		cpudata.setEntity("Utilization");
				   		cpudata.setSubentity("Utilization");
				   		cpudata.setRestype("dynamic");
				   		cpudata.setUnit("%");
				   		cpudata.setThevalue(Arith.round((100.0-Double.parseDouble(diskperf_tmpData[4].trim())),0)+"");
				   		cpuVector.addElement(cpudata);
				   		
						//��CPUֵ���и澯���
				   		Hashtable collectHash = new Hashtable();
						collectHash.put("cpu", cpuVector);
					    try{
							AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
							List list = alarmIndicatorsUtil.getAlarmInicatorsThresholdForNode(String.valueOf(host.getId()), AlarmConstant.TYPE_HOST, "aix","cpu");
							for(int k = 0 ; k < list.size() ; k ++){
								AlarmIndicatorsNode alarmIndicatorsnode = (AlarmIndicatorsNode)list.get(k);
								//��CPUֵ���и澯���
								
								CheckEventUtil checkutil = new CheckEventUtil();
								checkutil.updateData(host,collectHash,"host","aix",alarmIndicatorsnode);
								//}
							}
					    }catch(Exception e){
					    	e.printStackTrace();
					    }
				}
			}				
		}
		
		//��memory����д��ȥ
		//�����ڴ�������һ������
		Vector memoryVector=new Vector();
		Memorycollectdata memorydata=null;
		
		//System.out.println("========112233====PhysicalMemCap="+PhysicalMemCap);
		if(PhysicalMemCap > 0){
			//usedPhyPagesSize������ڴ�ʹ����
			//freePhysicalMemory ���������ڴ����У������ڴ���û��
			//�����ڴ�ʹ����
			//System.out.println("============���������ڴ�========================"+freePhysicalMemory);
			float PhysicalMemUtilization =(PhysicalMemCap-freePhysicalMemory)* 100/ PhysicalMemCap;
		    //System.out.println("============ʹ����=2======================="+PhysicalMemUtilization);
			
			//�������ڴ��С
				memorydata=new Memorycollectdata();
				memorydata.setIpaddress(ipaddress);
				memorydata.setCollecttime(date);
				memorydata.setCategory("Memory");
				memorydata.setEntity("Capability");
				memorydata.setSubentity("PhysicalMemory");
				memorydata.setRestype("static");
				memorydata.setUnit("M");
				memorydata.setThevalue(Float.toString(PhysicalMemCap));
				memoryVector.addElement(memorydata);
				//�Ѿ��õ������ڴ�
				memorydata=new Memorycollectdata();
				memorydata.setIpaddress(ipaddress);
				memorydata.setCollecttime(date);
				memorydata.setCategory("Memory");
				memorydata.setEntity("UsedSize");
				memorydata.setSubentity("PhysicalMemory");
				memorydata.setRestype("static");
				memorydata.setUnit("M");
				memorydata.setThevalue(
					//Float.toString(PhysicalMemCap*(1-usedPhyPagesSize/100)));
					Float.toString(PhysicalMemCap-freePhysicalMemory));
				memoryVector.addElement(memorydata);
				//�ڴ�ʹ����
				memorydata=new Memorycollectdata();
				memorydata.setIpaddress(ipaddress);
				memorydata.setCollecttime(date);
				memorydata.setCategory("Memory");
				memorydata.setEntity("Utilization");
				memorydata.setSubentity("PhysicalMemory");
				memorydata.setRestype("dynamic");
				memorydata.setUnit("%");
				memorydata.setThevalue(Math.round(PhysicalMemUtilization)+"");
				//memorydata.setThevalue(Math.round(usedPhyPagesSize)+"");
				memoryVector.addElement(memorydata);
				
				Vector phymemV = new Vector();
				phymemV.add(memorydata);				
			    Hashtable collectHash = new Hashtable();
				collectHash.put("physicalmem", phymemV);
				collectHash.put("pagingusage", paginghash);
				//�������ڴ�ֵ���и澯���
				//-----------------���������ڴ��뻻ҳ�������ϸ澯------------------------
				
				
//			    try{
//					AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
//					List list = alarmIndicatorsUtil.getAlarmInicatorsThresholdForNode(String.valueOf(host.getId()), AlarmConstant.TYPE_HOST, "aix","physicalmemory");
//					for(int i = 0 ; i < list.size() ; i ++){
//						AlarmIndicatorsNode alarmIndicatorsnode = (AlarmIndicatorsNode)list.get(i);
//						//�������ڴ�ֵ���и澯���
//						CheckEventUtil checkutil = new CheckEventUtil();
//						checkutil.updateData(host,collectHash,"host","aix",alarmIndicatorsnode);
//						//}
//					}
//			    }catch(Exception e){
//			    	e.printStackTrace();
//			    }
//			    
				
				//System.out.println("=========333=====��ʼ�ڴ淧ֵ�Ƚ�====");
				
				//System.out.println("===version==="+nodeconfig.getCSDVersion().substring(0,1));
				
				
				
				if (((nodeconfig.getCSDVersion().length()==15) ) && ((nodeconfig.getCSDVersion().substring(0,1).indexOf("5")<0) || (nodeconfig.getCSDVersion().substring(0,1).indexOf("6")<0))) 
				{// ����ǰ汾5����6 ��������ϸ澯

					System.out.println("===================���ϼ��============================");
					//System.out.println(collectHash.toString());
					
					CheckEventUtil checkutil = new CheckEventUtil();
					checkutil.checkMemoryAndPage(host, collectHash,"physicalmemory","pagingusage","aix");
				
			} else {// �����ڴ����ҳ�����ϱȽ�

				//System.out.println("=====================11=============================");
				//System.out.println("===================eeessss==z===========");
				try {
					AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
					List list = alarmIndicatorsUtil
							.getAlarmInicatorsThresholdForNode(String
									.valueOf(host.getId()),
									AlarmConstant.TYPE_HOST, "aix",
									"physicalmemory");
					for (int i = 0; i < list.size(); i++) {
						AlarmIndicatorsNode alarmIndicatorsnode = (AlarmIndicatorsNode) list
								.get(i);
						// �������ڴ�ֵ���и澯���
						CheckEventUtil checkutil = new CheckEventUtil();
						checkutil.updateData(host, collectHash, "host", "aix",
								alarmIndicatorsnode);
						// }
					}
				} catch (Exception e) {
					e.printStackTrace();
				}


			}    
			    
			    
			    
			  //------------------------���������ڴ��뻻ҳ��------------------------------------
			    //�����ڴ��ܴ�С�仯�澯���
				//CheckEventUtil checkutil = new CheckEventUtil();
				//checkutil.hardwareInfo(host, "PhysicalMemory", Float.toString(PhysicalMemCap)+"M");
		}
		if(SwapMemCap > 0){
			//Swap
  			memorydata=new Memorycollectdata();
  			memorydata.setIpaddress(ipaddress);
  			memorydata.setCollecttime(date);
  			memorydata.setCategory("Memory");
  			memorydata.setEntity("Capability");
  			memorydata.setSubentity("SwapMemory");
  			memorydata.setRestype("static");
  			memorydata.setUnit("M");
  			//һ��BLOCK��512byte
  			//��������ʹ�ô�С
  			memorydata.setThevalue(Math.round(SwapMemCap/ 1024)+"");
  			memoryVector.addElement(memorydata);
  			memorydata=new Memorycollectdata();
  			memorydata.setIpaddress(ipaddress);
  			memorydata.setCollecttime(date);
  			memorydata.setCategory("Memory");
  			memorydata.setEntity("UsedSize");
  			memorydata.setSubentity("SwapMemory");
  			memorydata.setRestype("static");
  			memorydata.setUnit("M");
  			memorydata.setThevalue(Math.round(usedSwapMemory/1024)+"");
  			memoryVector.addElement(memorydata);
			//��������ʹ����
  			memorydata=new Memorycollectdata();
  			memorydata.setIpaddress(ipaddress);
  			memorydata.setCollecttime(date);
  			memorydata.setCategory("Memory");
  			memorydata.setEntity("Utilization");
  			memorydata.setSubentity("SwapMemory");
  			memorydata.setRestype("dynamic");
  			memorydata.setUnit("%");
  			memorydata.setThevalue(Math.round(usedSwapMemory*100/SwapMemCap)+"");
  			//System.out.println("ʹ�ô�С="+usedSwapMemory);
  			//System.out.println("�ܴ�С="+SwapMemCap);
  			//System.out.println("��������ʹ����  "+Math.round(usedSwapMemory*100/SwapMemCap)+"");
  			memoryVector.addElement(memorydata);
  			
  			Vector swapmemV = new Vector();
  			swapmemV.add(memorydata);				
		    Hashtable collectHash = new Hashtable();
			collectHash.put("swapmem", swapmemV);				
			//�Խ����ڴ�ֵ���и澯���
		    try{
				AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
				List list = alarmIndicatorsUtil.getAlarmInicatorsThresholdForNode(String.valueOf(host.getId()), AlarmConstant.TYPE_HOST, "aix","swapmemory");
				for(int i = 0 ; i < list.size() ; i ++){
					AlarmIndicatorsNode alarmIndicatorsnode = (AlarmIndicatorsNode)list.get(i);
					//�Խ����ڴ�ֵ���и澯���
					CheckEventUtil checkutil = new CheckEventUtil();
					checkutil.updateData(host,collectHash,"host","aix",alarmIndicatorsnode);
					//}
				}
		    }catch(Exception e){
		    	e.printStackTrace();
		    }
		}

		
		
//		
//		//----------------����memory����--���������---------------------        	
//		String memperfContent = "";
//		tmpPt = Pattern.compile("(cmdbegin:memory)(.*)(cmdbegin:process)",Pattern.DOTALL);
//		mr = tmpPt.matcher(fileContent.toString());
//		if(mr.find())
//		{
//			memperfContent = mr.group(2);
//		} 
//		String[] memperfLineArr = memperfContent.split("\n");
//		List memperflist = new ArrayList();
//		//Vector memoryVector=new Vector();
//		//Memorycollectdata memorydata=null;
//		Hashtable<String,String> memperfhash = new Hashtable<String,String>();
//		for(int i=0; i<memperfLineArr.length;i++){
//			diskperf_tmpData = memperfLineArr[i].trim().split("\\s++");
//			if(diskperf_tmpData != null && diskperf_tmpData.length>=4){
//				if(diskperf_tmpData[0].trim().equalsIgnoreCase("Mem:")){
//					memperfhash.put("total", diskperf_tmpData[1].trim());
//					memperfhash.put("used", diskperf_tmpData[2].trim());
//					memperfhash.put("free", diskperf_tmpData[3].trim());
//					memperfhash.put("shared", diskperf_tmpData[4].trim());
//					memperfhash.put("buffers", diskperf_tmpData[5].trim());
//					memperfhash.put("cached", diskperf_tmpData[6].trim());
//					memperflist.add(memperfhash);
//					memperfhash = new Hashtable();
//					//Memory
//					float PhysicalMemUtilization =100- Float.parseFloat(diskperf_tmpData[3])* 100/ Float.parseFloat(diskperf_tmpData[1]);
//					memorydata=new Memorycollectdata();
//		  			memorydata.setIpaddress(ipaddress);
//		  			memorydata.setCollecttime(date);
//		  			memorydata.setCategory("Memory");
//		  			memorydata.setEntity("Capability");
//		  			memorydata.setSubentity("PhysicalMemory");
//		  			memorydata.setRestype("static");
//		  			memorydata.setUnit("M");
//		  			memorydata.setThevalue(
//							Integer.toString(Integer.parseInt(diskperf_tmpData[1]) / 1024));
//		  			memoryVector.addElement(memorydata);
//		  			
//		  			memorydata=new Memorycollectdata();
//		  			memorydata.setIpaddress(ipaddress);
//		  			memorydata.setCollecttime(date);
//		  			memorydata.setCategory("Memory");
//		  			memorydata.setEntity("UsedSize");
//		  			memorydata.setSubentity("PhysicalMemory");
//		  			memorydata.setRestype("static");
//		  			memorydata.setUnit("M");
//		  			memorydata.setThevalue(
//							Integer.toString(Integer.parseInt(diskperf_tmpData[2]) / 1024));
//		  			memoryVector.addElement(memorydata);
//		  			
//		  			memorydata=new Memorycollectdata();
//		  			memorydata.setIpaddress(ipaddress);
//		  			memorydata.setCollecttime(date);
//		  			memorydata.setCategory("Memory");
//		  			memorydata.setEntity("Utilization");
//		  			memorydata.setSubentity("PhysicalMemory");
//		  			memorydata.setRestype("dynamic");
//		  			memorydata.setUnit("%");
//		  			memorydata.setThevalue(Math.round(PhysicalMemUtilization)+"");
//		  			memoryVector.addElement(memorydata);
//				}else if(diskperf_tmpData[0].trim().equalsIgnoreCase("Swap:")){
//					memperfhash.put("total", diskperf_tmpData[1].trim());
//					memperfhash.put("used", diskperf_tmpData[2].trim());
//					memperfhash.put("free", diskperf_tmpData[3].trim());
//					memperflist.add(memperfhash);
//					memperfhash = new Hashtable();
//					//Swap
//		  			memorydata=new Memorycollectdata();
//		  			memorydata.setIpaddress(ipaddress);
//		  			memorydata.setCollecttime(date);
//		  			memorydata.setCategory("Memory");
//		  			memorydata.setEntity("Capability");
//		  			memorydata.setSubentity("SwapMemory");
//		  			memorydata.setRestype("static");
//		  			memorydata.setUnit("M");
//		  			memorydata.setThevalue(Integer.toString(Integer.parseInt(diskperf_tmpData[1]) / 1024));
//		  			memoryVector.addElement(memorydata);
//		  			memorydata=new Memorycollectdata();
//		  			memorydata.setIpaddress(ipaddress);
//		  			memorydata.setCollecttime(date);
//		  			memorydata.setCategory("Memory");
//		  			memorydata.setEntity("UsedSize");
//		  			memorydata.setSubentity("SwapMemory");
//		  			memorydata.setRestype("static");
//		  			memorydata.setUnit("M");
//		  			memorydata.setThevalue(
//							Integer.toString(Integer.parseInt(diskperf_tmpData[2]) / 1024));
//		  			memoryVector.addElement(memorydata);
//					float SwapMemUtilization =(Integer.parseInt(diskperf_tmpData[2]))* 100/Integer.parseInt(diskperf_tmpData[1]);
//					
//		  			memorydata=new Memorycollectdata();
//		  			memorydata.setIpaddress(ipaddress);
//		  			memorydata.setCollecttime(date);
//		  			memorydata.setCategory("Memory");
//		  			memorydata.setEntity("Utilization");
//		  			memorydata.setSubentity("SwapMemory");
//		  			memorydata.setRestype("dynamic");
//		  			memorydata.setUnit("%");
//		  			memorydata.setThevalue(Math.round(SwapMemUtilization)+"");
//		  			memoryVector.addElement(memorydata);
//				}
//			}				
//		}
		
		//��----------------����process����--���������---------------------        	
		String processContent = "";
		tmpPt = Pattern.compile("(cmdbegin:process)(.*)(cmdbegin:cpu)",Pattern.DOTALL);
		mr = tmpPt.matcher(fileContent.toString());
		if(mr.find())
		{
			processContent = mr.group(2);
		} 

		
		Hashtable procshash = new Hashtable();//���̵������б�
		Vector procsV = new Vector();
		String[] process_LineArr = processContent.split("\n");
		String[] processtmpData = null;
		float cpuusage = 0.0f;
		for(int i=1; i<process_LineArr.length;i++)
		{    			
			processtmpData = process_LineArr[i].trim().split("\\s++");
			//System.out.println("processtmpData.length==="+processtmpData.length);
			if((processtmpData != null) && (processtmpData.length >= 11)){
				
				//SysLogger.info(processtmpData[0]+"-----------------");
				String USER=processtmpData[0];//USER
				String pid=processtmpData[1];//pid
				if("USER".equalsIgnoreCase(USER))continue;
				String cmd = processtmpData[10];
				String vbstring8 = processtmpData[8];
				String vbstring5=processtmpData[9];//cputime
				if(processtmpData.length > 11){
					cmd = processtmpData[11];
					vbstring8 = processtmpData[8]+processtmpData[9];//STIME
					vbstring5=processtmpData[10];//cputime
				}
				String vbstring2="Ӧ�ó���";
				String vbstring3="";
				String vbstring4=processtmpData[4];//memsize
				if (vbstring4 == null)vbstring4="0";
				String vbstring6=processtmpData[3];//%mem
				String vbstring7=processtmpData[2];//%CPU
				String vbstring9=processtmpData[7];//STAT
				if("Z".equals(vbstring9)){
					vbstring3="��ʬ����";
				} else {
					vbstring3="��������";
				}
				processdata=new Processcollectdata();
				processdata.setIpaddress(ipaddress);
				processdata.setCollecttime(date);
				processdata.setCategory("Process");
				processdata.setEntity("process_id");
				processdata.setSubentity(pid);
				processdata.setRestype("static");
				processdata.setUnit(" ");
				processdata.setThevalue(pid);
				processdata.setProcessname(cmd);
				
				processVector.addElement(processdata);	
				
				processdata=new Processcollectdata();
				processdata.setIpaddress(ipaddress);
				processdata.setCollecttime(date);
				processdata.setCategory("Process");
				processdata.setEntity("USER");
				processdata.setSubentity(pid);
				processdata.setRestype("static");
				processdata.setUnit(" ");
				processdata.setThevalue(USER);
				processdata.setProcessname(cmd);
				processVector.addElement(processdata);	
				
				processdata=new Processcollectdata();
				processdata.setIpaddress(ipaddress);
				processdata.setCollecttime(date);
				processdata.setCategory("Process");
				processdata.setEntity("MemoryUtilization");
				processdata.setSubentity(pid);
				processdata.setRestype("dynamic");
				processdata.setUnit("%");
				processdata.setThevalue(vbstring6);
				processdata.setProcessname(cmd);
				processVector.addElement(processdata);	
		
				processdata=new Processcollectdata();
				processdata.setIpaddress(ipaddress);
				processdata.setCollecttime(date);
				processdata.setCategory("Process");
				processdata.setEntity("Memory");
				processdata.setSubentity(pid);
				processdata.setRestype("static");
				processdata.setUnit("K");
				processdata.setThevalue(vbstring4);
				processdata.setProcessname(cmd);
				processVector.addElement(processdata);
				
				processdata=new Processcollectdata();
				processdata.setIpaddress(ipaddress);
				processdata.setCollecttime(date);
				processdata.setCategory("Process");
				processdata.setEntity("Type");
				processdata.setSubentity(pid);
				processdata.setRestype("static");
				processdata.setUnit(" ");
				processdata.setThevalue(vbstring2);
				processdata.setProcessname(cmd);
				processVector.addElement(processdata);
				
				processdata=new Processcollectdata();
				processdata.setIpaddress(ipaddress);
				processdata.setCollecttime(date);
				processdata.setCategory("Process");
				processdata.setEntity("Status");
				processdata.setSubentity(pid);
				processdata.setRestype("static");
				processdata.setUnit(" ");
				processdata.setThevalue(vbstring3);
				processdata.setProcessname(cmd);
				processVector.addElement(processdata);
				procshash.put(cmd.trim(), cmd.trim());//�����еĽ��̷��뵽�б���
			
				
				processdata=new Processcollectdata();
				processdata.setIpaddress(ipaddress);
				processdata.setCollecttime(date);
				processdata.setCategory("Process");
				processdata.setEntity("Name");
				processdata.setSubentity(pid);
				processdata.setRestype("static");
				processdata.setUnit(" ");
				processdata.setThevalue(cmd);
				processdata.setProcessname(cmd);
				processVector.addElement(processdata);
				
				processdata=new Processcollectdata();
				processdata.setIpaddress(ipaddress);
				processdata.setCollecttime(date);
				processdata.setCategory("Process");
				processdata.setEntity("CpuTime");
				processdata.setSubentity(pid);
				processdata.setRestype("dynamic");
				processdata.setUnit("��");
				processdata.setThevalue(vbstring5);
				processdata.setProcessname(cmd);
				processVector.addElement(processdata);
				
				processdata=new Processcollectdata();
				processdata.setIpaddress(ipaddress);
				processdata.setCollecttime(date);
				processdata.setCategory("Process");
				processdata.setEntity("StartTime");
				processdata.setSubentity(pid);
				processdata.setRestype("static");
				processdata.setUnit(" ");
				processdata.setThevalue(vbstring8);
				processdata.setProcessname(cmd);
				processVector.addElement(processdata);
				
				processdata=new Processcollectdata();
				processdata.setIpaddress(ipaddress);
				processdata.setCollecttime(date);
				processdata.setCategory("Process");
				processdata.setEntity("CpuUtilization");
				processdata.setSubentity(pid);
				processdata.setRestype("dynamic");
				processdata.setUnit("%");
				processdata.setThevalue(vbstring7);
				processdata.setProcessname(cmd);
				processVector.addElement(processdata);
				
			}	
		}
		//�ж�ProcsV�ﻹ��û����Ҫ���ӵĽ��̣����У���˵����ǰû�������ý��̣������������������ý��̣�ͬʱд���¼�
		procsV=this.Getipprocesslist(ipaddress);//���ڴ��еõ���Ҫ���Ľ��̶���
		//System.out.println("========================��ʼ���̱Ƚ�==========================");
		
	     if (procsV !=null && procsV.size()>0){
	     	for(int i=0;i<procsV.size();i++){	
	         
	     		Hashtable proc=(Hashtable)procsV.get(i);
	     		
	     		 //System.out.println("==procname="+proc.get("procname"));
	     		 //System.out.println(procshash.toString());
	     		 
	     		String eventContent="";
	     		boolean alarmflg=false;
	     		if(proc.get("wbstatus").equals("0"))
	     		{//������
	     			//System.out.println("==��==");
	     			
	     			
	     			if(!procshash.containsKey(proc.get("procname")))
	     			{//û���ҵ���Ӧ�Ľ��̳����澯
	     				eventContent=proc.get("procname")+"("+ipaddress+")���̶�ʧ";
	     				alarmflg=true;
	     				//System.out.println("===alarmflg===="+alarmflg);
	     				
	     			}else 
	     			{//�н��̵����ǽ�ʬ����Ҳ�澯
	     				
	     			}
	     			
	     		}else
	     		{//������
	     			//System.out.println("==��==");
	     			if(procshash.containsKey(proc.get("procname")))
	     			{//û���ҵ���Ӧ�Ľ��̳����澯
	     				eventContent=proc.get("procname")+"("+ipaddress+")�����������쳣";
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
		    		eventlist.setReportman("ϵͳ��ѯ");
		    		eventlist.setBusinessid(host.getBid());
		    		eventlist.setNodeid(host.getId());
		    		eventlist.setOid(0);
		    		eventlist.setSubtype("host");
		    		eventlist.setSubentity("proc");
		    		eventlist.setIpaddress(ipaddress);
					
					  try{
							AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
							List list = alarmIndicatorsUtil.getAlarmInicatorsThresholdForNode(String.valueOf(host.getId()), AlarmConstant.TYPE_HOST, "aix","process");
							for(int z = 0 ; z < list.size() ; z ++){
								
								//System.out.println("========��ֵ����==="+list.size());
								AlarmIndicatorsNode alarmIndicatorsnode = (AlarmIndicatorsNode)list.get(z);
								
															
								CheckEvent checkevent=new CheckEvent();
								
								checkevent.setAlarmlevel(Integer.parseInt((String )proc.get("alarmLevel")));
								checkevent.setName(proc.get("nodeid")+":host:proce:"+proc.get("procname"));
								SendAlarmUtil sendAlarmUtil = new SendAlarmUtil();
								
					    		//���͸澯
					    		sendAlarmUtil.sendAlarmNoIndicatorOther(checkevent, eventlist,alarmIndicatorsnode);//����ط�дʲô
								
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
    	
		systemdata=new Systemcollectdata();
		systemdata.setIpaddress(ipaddress);
		systemdata.setCollecttime(date);
		systemdata.setCategory("System");
		systemdata.setEntity("ProcessCount");
		systemdata.setSubentity("ProcessCount");
		systemdata.setRestype("static");
		systemdata.setUnit(" ");
		systemdata.setThevalue(process_LineArr.length+"");
		systemVector.addElement(systemdata);	
		
		//��----------------����uname����--���������---------------------        	
		String unameContent = "";
		tmpPt = Pattern.compile("(cmdbegin:uname)(.*)(cmdbegin:service)",Pattern.DOTALL);
		mr = tmpPt.matcher(fileContent.toString());
		if(mr.find())
		{
			unameContent = mr.group(2);
		} 
		String[] unameLineArr = unameContent.split("\n");
		String[] uname_tmpData = null;
		for(int i=0; i<unameLineArr.length;i++){
			uname_tmpData = unameLineArr[i].split("\\s++");				
			if (uname_tmpData.length==2){	
				  systemdata=new Systemcollectdata();
				  systemdata.setIpaddress(ipaddress);
				  systemdata.setCollecttime(date);
				  systemdata.setCategory("System");
				  systemdata.setEntity("operatSystem");
				  systemdata.setSubentity("operatSystem");
				  systemdata.setRestype("static");
				  systemdata.setUnit(" ");
				  systemdata.setThevalue(uname_tmpData[0]);
				  systemVector.addElement(systemdata);
				  
					systemdata=new Systemcollectdata();
					systemdata.setIpaddress(ipaddress);
					systemdata.setCollecttime(date);
					systemdata.setCategory("System");
					systemdata.setEntity("SysName");
					systemdata.setSubentity("SysName");
					systemdata.setRestype("static");
					systemdata.setUnit(" ");
					systemdata.setThevalue(uname_tmpData[1]);
				  systemVector.addElement(systemdata);								
				
			}				
		}
		
		//��----------------����service����--���������---------------------  
		List servicelist = new ArrayList();
		Hashtable service = new Hashtable();
		String serviceContent = "";
		tmpPt = Pattern.compile("(cmdbegin:service)(.*)(cmdbegin:usergroup)",Pattern.DOTALL);
		mr = tmpPt.matcher(fileContent.toString());
		if(mr.find())
		{
			serviceContent = mr.group(2);
			//System.out.println("================================");
			//System.out.println("======"+serviceContent);
			//System.out.println("================================");
			
		} 
		String[] serviceLineArr = serviceContent.split("\n");
		String[] service_tmpData = null;
		for(int i=0; i<serviceLineArr.length;i++){
			service_tmpData = serviceLineArr[i].trim().split("\\s++");	
			if(service_tmpData != null && service_tmpData.length>=3){
				if("Subsystem".equalsIgnoreCase(service_tmpData[0]))continue;
				if(service_tmpData.length==4){
					//�����������,��PID
					try{
						service.put("DisplayName", service_tmpData[0]);
						service.put("groupstr", service_tmpData[1]);
						service.put("pid", service_tmpData[2]);
						service.put("State", service_tmpData[3]);
						servicelist.add(service);
					}catch(Exception e){
						e.printStackTrace();
					}
					service = new Hashtable();
				}else{
					//δ���������û��PID
					try{
						service.put("DisplayName", service_tmpData[0]);
						service.put("groupstr", service_tmpData[1]);
						service.put("State", service_tmpData[2]);
						service.put("pid", "");
						servicelist.add(service);
					}catch(Exception e){
						e.printStackTrace();
					}
					service = new Hashtable();
				}
				
			}else if(service_tmpData != null && service_tmpData.length==2){
				//�����������,��PID
				try{
					service.put("DisplayName", service_tmpData[0]);
					service.put("groupstr", "");
					service.put("pid", "");
					service.put("State", service_tmpData[1]);
					servicelist.add(service);
				}catch(Exception e){
					e.printStackTrace();
				}
				service = new Hashtable();
			}
//			if (service_tmpData.length==2){	
//				  systemdata=new Systemcollectdata();
//				  systemdata.setIpaddress(ipaddress);
//				  systemdata.setCollecttime(date);
//				  systemdata.setCategory("System");
//				  systemdata.setEntity("operatSystem");
//				  systemdata.setSubentity("operatSystem");
//				  systemdata.setRestype("static");
//				  systemdata.setUnit(" ");
//				  systemdata.setThevalue(service_tmpData[0]);
//				  systemVector.addElement(systemdata);
//				  
//					systemdata=new Systemcollectdata();
//					systemdata.setIpaddress(ipaddress);
//					systemdata.setCollecttime(date);
//					systemdata.setCategory("System");
//					systemdata.setEntity("SysName");
//					systemdata.setSubentity("SysName");
//					systemdata.setRestype("static");
//					systemdata.setUnit(" ");
//					systemdata.setThevalue(service_tmpData[1]);
//				  systemVector.addElement(systemdata);								
//				
//			}				
		}
		
		//��----------------����usergroup����--���������--------------------- 
		Hashtable usergrouphash = new Hashtable();
		String usergroupContent = "";
		tmpPt = Pattern.compile("(cmdbegin:usergroup)(.*)(cmdbegin:user\n)",Pattern.DOTALL);
		mr = tmpPt.matcher(fileContent.toString());
		if(mr.find())
		{
			usergroupContent = mr.group(2);
		} 
		String[] usergroupLineArr = usergroupContent.split("\n");
		String[] usergroup_tmpData = null;
		for(int i=0; i<usergroupLineArr.length;i++){
			usergroup_tmpData = usergroupLineArr[i].split(":");				
			if (usergroup_tmpData.length>=3){	
				usergrouphash.put((String)usergroup_tmpData[2], usergroup_tmpData[0]);
			}				
		}
		
		//��----------------����user����--���������---------------------        	
		String userContent = "";
		tmpPt = Pattern.compile("(cmdbegin:user\n)(.*)(cmdbegin:date)",Pattern.DOTALL);
		mr = tmpPt.matcher(fileContent.toString());
		if(mr.find())
		{
			userContent = mr.group(2);
			//System.out.println("======userContent======");
			//System.out.println("============"+userContent);
			//System.out.println("=======userContent=====");
			
		} 
		String[] userLineArr = userContent.split("\n");
		String[] user_tmpData = null;
		for(int i=0; i<userLineArr.length;i++){
			String[] result = userLineArr[i].trim().split("\\s++");
			if (result.length>=4){
				String userName = result[0];
				String groupStr = result[3];
				String[] groups = groupStr.split("=");
				String group ="";
				if(groups != null && groups.length==2){
					group = groups[1];
				}
				//String userid = result[1];
				//int usergroupid = Integer.parseInt(result[3]);
				//С��500��Ϊϵͳ���û�,����
				//if(userid < 500)continue;
				
				userdata=new Usercollectdata();
				userdata.setIpaddress(ipaddress);
				userdata.setCollecttime(date);
				userdata.setCategory("User");
				userdata.setEntity("Sysuser");
				userdata.setSubentity(group);
				userdata.setRestype("static");
				userdata.setUnit(" ");
				userdata.setThevalue(userName);
				userVector.addElement(userdata);								
			}
			
		}
		
		//��----------------����date����--���������---------------------        	
		String dateContent = "";
		tmpPt = Pattern.compile("(cmdbegin:date)(.*)(cmdbegin:uptime)",Pattern.DOTALL);
		mr = tmpPt.matcher(fileContent.toString());
		if(mr.find())
		{
			dateContent = mr.group(2);
		}
		if (dateContent != null && dateContent.length()>0){
			systemdata=new Systemcollectdata();
			systemdata.setIpaddress(ipaddress);
			systemdata.setCollecttime(date);
			systemdata.setCategory("System");
			systemdata.setEntity("Systime");
			systemdata.setSubentity("Systime");
			systemdata.setRestype("static");
			systemdata.setUnit(" ");
			systemdata.setThevalue(dateContent.trim());
			systemVector.addElement(systemdata);

		}  

		//��----------------����uptime����--���������---------------------        	
		String uptimeContent = "";
		tmpPt = Pattern.compile("(cmdbegin:uptime)(.*)(cmdbegin:errpt)",Pattern.DOTALL);
		mr = tmpPt.matcher(fileContent.toString());
		if(mr.find())
		{
			uptimeContent = mr.group(2);
		}
		if (uptimeContent != null && uptimeContent.length()>0){
			systemdata=new Systemcollectdata();
			systemdata.setIpaddress(ipaddress);
			systemdata.setCollecttime(date);
			systemdata.setCategory("System");
			systemdata.setEntity("SysUptime");
			systemdata.setSubentity("SysUptime");
			systemdata.setRestype("static");
			systemdata.setUnit(" ");
			systemdata.setThevalue(uptimeContent.trim());
			systemVector.addElement(systemdata);
		}
		
		
		
		
		String errptlogContent = "";
		tmpPt = Pattern.compile("(cmdbegin:errpt)(.*)(cmdbegin:volume)",Pattern.DOTALL);
		mr = tmpPt.matcher(fileContent.toString());
		if(mr.find()){
			errptlogContent = mr.group(2);
			ReadErrptlog readErrptlog = new ReadErrptlog();
			List list = null;
			try {
				list = readErrptlog.praseErrptlog(errptlogContent);
				if(list == null){
					list = new ArrayList();
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			ErrptconfigDao dao = new ErrptconfigDao();
			Errptconfig errptconfig = new Errptconfig();
			try{
				errptconfig = dao.loadErrptconfigByNodeid(host.getId());
			}catch(Exception e){
				
			}finally{
				dao.close();
			}
			SendMailAlarm sendMailAlarm = new SendMailAlarm();
			int index = 0;
			try{
				for(int i = 0 ; i < list.size() ; i++){
					Errptlog errptlog = (Errptlog)list.get(i);
					errptlog.setHostid(host.getId()+"");
					errptlogVector.add(list.get(i));
					//���и澯�ж�
					//errptlog ����澯����
					if(errptconfig != null){
						if(errptconfig.getErrpttype().contains(errptlog.getErrpttype().toLowerCase()) && errptconfig.getErrptclass().contains(errptlog.getErrptclass().toLowerCase())){
							index++;
							if(index == 1){
								//System.out.println("errptlog.getErrpttype().toLowerCase()"+errptlog.getErrpttype().toLowerCase());
								EventList eventlist = new EventList();
					    		eventlist.setEventtype("poll");
					    		eventlist.setEventlocation(host.getSysLocation());
					    		eventlist.setContent("�豸IP��"+host.getIpAddress()+" �豸���ƣ�"+host.getAlias()+"�� errpt�澯��Ϣ    ����"+ErrptlogUtil.getTypename(errptlog.getErrpttype())+" ����:"+ErrptlogUtil.getClassname(errptlog.getErrptclass()));
					    		//eventlist.setContent("errpt�澯��Ϣ    ����"+ErrptlogUtil.getTypename(errptlog.getErrpttype())+" ����:"+ErrptlogUtil.getClassname(errptlog.getErrptclass()));
					    		eventlist.setLevel1(1);
					    		eventlist.setManagesign(0);
					    		eventlist.setBak("");
					    		eventlist.setRecordtime(Calendar.getInstance());
					    		eventlist.setReportman("ϵͳ��ѯ");
					    		eventlist.setBusinessid(host.getBid());
					    		eventlist.setNodeid(host.getId());
					    		eventlist.setOid(0);
					    		eventlist.setSubtype("host");
					    		eventlist.setSubentity("errptlog");
	//				    		EventListDao eventlistdao = null;
	//				    		try {
	//				    			eventlistdao = new EventListDao();
	//								eventlistdao.save(eventlist);
	//							} catch (Exception e) {
	//								e.printStackTrace();
	//							} finally{
	//								if(eventlistdao != null){
	//									eventlistdao.close();
	//								}
	//							}
					    		//���ɸ�ʽΪxml��errptlog�澯�ļ�
					    		sendMailAlarm.BuildEventXMLDoc(eventlist);  
					    		SendAlarmUtil sendAlarmUtil = new SendAlarmUtil();
					    		//���͸澯
					    		sendAlarmUtil.sendAlarmNoIndicator(errptconfig.getAlarmwayid(), eventlist);
							}
						}
					}
				}
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				dao.close();
			}
		}
		
		//��----------------����volume����--���������---------------------
		String volumeContent = "";
		String volumeLabel;
		List volumelist = new ArrayList();
		tmpPt = Pattern.compile("(cmdbegin:volume)(.*)(cmdbegin:route)",Pattern.DOTALL);
		mr = tmpPt.matcher(fileContent.toString());
		if(mr.find())
		{
			volumeContent = mr.group(2);		
		}
		String[] volumeLineArr = volumeContent.split("\n");
		String[] volumetmpData = null;
		for(int i=1; i<volumeLineArr.length;i++)
		{
			
			volumetmpData = volumeLineArr[i].split("\\s++");
			if((volumetmpData != null) && (volumetmpData.length == 4 || volumetmpData.length == 3))
			{
				Hashtable volumeHash = new Hashtable();
				volumeHash.put("disk", volumetmpData[0]);
				volumeHash.put("pvid", volumetmpData[1]);
				volumeHash.put("vg", volumetmpData[2]);
				if(volumetmpData.length == 4){
					volumeHash.put("status", volumetmpData[3]);
				}else{
					volumeHash.put("status", "-");
				}
				
				volumeVector.addElement(volumeHash);
			}
		}
		
		//��----------------����·������--���������---------------------
		String routeContent = "";
		String routeLabel;
		List routelist = new ArrayList();
		tmpPt = Pattern.compile("(cmdbegin:route)(.*)(cmdbegin:end)",Pattern.DOTALL);
		mr = tmpPt.matcher(fileContent.toString());
		if(mr.find())
		{
			routeContent = mr.group(2);		
		}
		String[] routeLineArr = routeContent.split("\n");
		String[] routetmpData = null;
		for(int i=1; i<routeLineArr.length;i++)
		{
			//SysLogger.info(routeLineArr[i]);
			routeList.add(routeLineArr[i]);
			routetmpData = routeLineArr[i].split("\\s++");
			if((volumetmpData != null) && (volumetmpData.length == 4 || volumetmpData.length == 3))
			{
				Hashtable volumeHash = new Hashtable();
				volumeHash.put("disk", volumetmpData[0]);
				volumeHash.put("pvid", volumetmpData[1]);
				volumeHash.put("vg", volumetmpData[2]);
				if(volumetmpData.length == 4){
					volumeHash.put("status", volumetmpData[3]);
				}else{
					volumeHash.put("status", "-");
				}
				
				volumeVector.addElement(volumeHash);
			}
		}
		

		try{
//			deleteFile(ipaddress);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		
		
		
		if (diskVector != null && diskVector.size()>0)
		{//����ʹ����
			returnHash.put("disk",diskVector);
			
			 //�Ѳɼ��������sql
		    HostdiskResultosql tosql=new HostdiskResultosql();
		    tosql.CreateResultTosql(returnHash, host.getIpAddress());
	
		    HostDatatempDiskRttosql temptosql=new HostDatatempDiskRttosql();
		    temptosql.CreateResultTosql(returnHash, host);
		    tosql=null;
		    temptosql=null;
			
		}
		if (cpuVector != null && cpuVector.size()>0)
			{//cpu
			returnHash.put("cpu",cpuVector);
			
			 //HostcpuResultTosql restosql=new HostcpuResultTosql();
			 //restosql.CreateResultTosql(returnHash, host.getIpAddress());
			  //�ѽ��ת����sql
			   
			 NetHostDatatempCpuRTosql totempsql=new NetHostDatatempCpuRTosql();
			 totempsql.CreateResultTosql(returnHash, host);
			 totempsql=null;
			    
			
			}
		if (memoryVector != null && memoryVector.size()>0)
			{
			returnHash.put("memory",memoryVector);
			//�Ѳɼ��������sql
		    HostPhysicalMemoryResulttosql  tosql=new HostPhysicalMemoryResulttosql();
		    tosql.CreateResultTosql(returnHash, host.getIpAddress());
		    NetHostMemoryRtsql  totempsql=new NetHostMemoryRtsql();
		    totempsql.CreateResultTosql(returnHash, host);
		    
			
			}
		if (userVector != null && userVector.size()>0)
			{
			returnHash.put("user",userVector);
			
			HostDatatempUserRtosql tosql=new HostDatatempUserRtosql();
			tosql.CreateResultTosql(returnHash, host);
			}
		if (processVector != null && processVector.size()>0)
			{
			returnHash.put("process",processVector);
			
			//�ѽ������sql
			HostDatatempProcessRtTosql temptosql=new HostDatatempProcessRtTosql();
			temptosql.CreateResultTosql(returnHash, host);
			
			HostProcessRtosql Rtosql=new HostProcessRtosql();
			Rtosql.CreateResultTosql(returnHash, host.getIpAddress());
			}
		if (systemVector != null && systemVector.size()>0)
			{//ϵͳ��Ϣ
			returnHash.put("system",systemVector);
			NetHostDatatempSystemRttosql tosql=new NetHostDatatempSystemRttosql();
			tosql.CreateResultTosql(returnHash, host);
			
			}
		if (nodeconfig != null)
			{
			returnHash.put("nodeconfig",nodeconfig);
			
			HostDatatempNodeconfRtosql tosql=new HostDatatempNodeconfRtosql();
			tosql.CreateResultTosql(returnHash, host);
			
			}
		if (iflist != null && iflist.size()>0)
			{
			returnHash.put("iflist",iflist);
			HostDatatempiflistRtosql tosql=new HostDatatempiflistRtosql();
			tosql.CreateResultTosql(returnHash, host);
			
			}
		if (utilhdxVector != null && utilhdxVector.size()>0)
			{
			returnHash.put("utilhdx",utilhdxVector);
			HostDatatemputilhdxRtosql tosql=new HostDatatemputilhdxRtosql();
			tosql.CreateResultTosql(returnHash, host);
			}
		
		
		if (interfaceVector != null && interfaceVector.size()>0)
			{
			returnHash.put("interface",interfaceVector);
			HostDatatempinterfaceRtosql tosql=new HostDatatempinterfaceRtosql();
			tosql.CreateResultTosql(returnHash, host);
			
			}
		if (alldiskperf != null && alldiskperf.size()>0)
			{
			returnHash.put("alldiskperf",alldiskperf);
			HostDatatempnDiskperfRtosql tosql=new HostDatatempnDiskperfRtosql();
			tosql.CreateResultTosql(returnHash, host);
			
			}
		if (alldiskio != null && alldiskio.size()>0)
			{
			returnHash.put("alldiskio",alldiskio);
			HostDatatempDiskPeriofRtosql tosql=new HostDatatempDiskPeriofRtosql();
			tosql.CreateResultTosql(returnHash, host);
			
			
			}
		if (cpuconfiglist != null && cpuconfiglist.size()>0)
			{
			returnHash.put("cpuconfiglist",cpuconfiglist);
			HostDatatempCpuconfiRtosql tosql=new HostDatatempCpuconfiRtosql();
			tosql.CreateResultTosql(returnHash, host);
			}
		if (netmedialist != null && netmedialist.size()>0)
			{
			returnHash.put("netmedialist",netmedialist);
			}
		if (servicelist != null && servicelist.size()>0)
			{
			returnHash.put("servicelist",servicelist);
			   //��sql����sql
			HostDatatempserciceRttosql totempsql=new HostDatatempserciceRttosql();
			totempsql.CreateResultLinuxTosql(returnHash, host);
			
			}
		if (cpuperflist != null && cpuperflist.size()>0)
			{
			returnHash.put("cpuperflist",cpuperflist);
			
			HostcpuResultTosql rtosql=new HostcpuResultTosql();
			rtosql.CreateLinuxResultTosql(returnHash, host.getIpAddress());
			
			HostDatatempCpuperRtosql tmptosql=new HostDatatempCpuperRtosql();
			tmptosql.CreateResultTosql(returnHash, host);
			
			}
		if (pagehash != null && pagehash.size()>0)
			{
			returnHash.put("pagehash",pagehash);
			
			HostDatatempPageRtosql tosql=new HostDatatempPageRtosql();
			tosql.CreateResultTosql(returnHash, host);
			}
		if (paginghash != null && paginghash.size()>0)
			{
			
			returnHash.put("paginghash",paginghash);
			
			HostDatatempPagingRtosql tosql=new HostDatatempPagingRtosql();
			tosql.CreateResultTosql(returnHash, host);
			
			HostPagingResultTosql Rtosql=new HostPagingResultTosql();
			Rtosql.CreateResultTosql(returnHash, host.getIpAddress());
			
			
			}
		if (errptlogVector != null && errptlogVector.size()>0)
			{
			
			returnHash.put("errptlog",errptlogVector);
			HostDatatempErrptRtosql tosql=new HostDatatempErrptRtosql();
			tosql.CreateResultTosql(returnHash, host);
			
			}
		if (volumeVector != null && volumeVector.size()>0)
			{
			returnHash.put("volume",volumeVector);
			
			HostDatatempVolumeRtosql tosql=new HostDatatempVolumeRtosql();
			tosql.CreateResultTosql(returnHash, host);
			
			}
		if (routeList != null && routeList.size()>0)
			{
			returnHash.put("routelist",routeList);
			
			HostDatatempRuteRtosql tosql =new HostDatatempRuteRtosql();
			tosql.CreateResultTosql(returnHash, host);
			
			}
		returnHash.put("collecttime",collecttime);
		
		HostDatatempCollecttimeRtosql tosql=new HostDatatempCollecttimeRtosql();
		tosql.CreateResultTosql(returnHash, host);
		
		//if (! "1".equals(PollingEngine.getCollectwebflag())) {
		 ShareData.getSharedata().put(host.getIpAddress(), returnHash);
	    //System.out.println(returnHash.toString());
		//}
		return returnHash;
    }	
	
    public String getMaxNum(String ipAddress){
    	String maxStr = null;
		File logFolder = new File(ResourceCenter.getInstance().getSysPath() + "linuxserver/");
   		String[] fileList = logFolder.list();
   		
   		for(int i=0;i<fileList.length;i++) //��һ�����µ��ļ�
   		{
   			if(!fileList[i].startsWith(ipAddress)) continue;
   			
   			return ipAddress;
   		}
   		return maxStr;
    }
	
    public void deleteFile(String ipAddress){

			try
			{
				File delFile = new File(ResourceCenter.getInstance().getSysPath() + "linuxserver/" + ipAddress + ".log");
			System.out.println("###��ʼɾ���ļ���"+delFile);
			//delFile.delete();
			System.out.println("###�ɹ�ɾ���ļ���"+delFile);
			}
			catch(Exception e)		
			{}
    }
    public void copyFile(String ipAddress,String max){
	try   { 
		String currenttime = SysUtil.getCurrentTime();
		currenttime = currenttime.replaceAll("-", "");
		currenttime = currenttime.replaceAll(" ", "");
		currenttime = currenttime.replaceAll(":", "");
		String ipdir = ipAddress.replaceAll("\\.", "-");
		String filename = ResourceCenter.getInstance().getSysPath() + "/linuxserver_bak/"+ipdir;
		File file=new File(filename);
		if(!file.exists())file.mkdir();
        //String cmd   =   "cmd   /c   copy   "+ResourceCenter.getInstance().getSysPath() + "linuxserver\\" + ipAddress + ".log"+" "+ResourceCenter.getInstance().getSysPath() + "linuxserver_bak\\" +ipdir+"\\"+ ipAddress+"-" +currenttime+ ".log";
		String cmd   =   "cp   "+ResourceCenter.getInstance().getSysPath() + "linuxserver/" + ipAddress + ".log"+" "+ResourceCenter.getInstance().getSysPath() + "linuxserver_bak/" +ipdir+"/"+ ipAddress+"-" +currenttime+ ".log";
        //SysLogger.info(cmd);
        Process   child   =   Runtime.getRuntime().exec(cmd);   
      }catch (IOException e){    
        e.printStackTrace();
    }   
	
    }
	 public void createSMS(Procs procs){
		 	Procs lastprocs = null;
		 	//��������	
		 	procs.setCollecttime(Calendar.getInstance());
		 	//���Ѿ����͵Ķ����б����õ�ǰ��PROC�Ѿ����͵Ķ���
		 	lastprocs = (Procs)sendeddata.get(procs.getIpaddress()+":"+procs.getProcname());	
		 	
		 	
		 	
		 	/*
		 	try{		 				 		
		 		if (lastprocs==null){
		 			//�ڴ��в�����	,����û��������,�򷢶���
		 			Equipment equipment = equipmentManager.getByip(procs.getIpaddress());
		 			Smscontent smscontent = new Smscontent();
		 			String time = sdf.format(procs.getCollecttime().getTime());
		 			smscontent.setMessage(time+"&"+equipment.getEquipname()+"&"+equipment.getIpaddress()+"&"+procs.getProcname()+"&���̶�ʧ&level=2");
		 			//���Ͷ���
		 			Vector tosend = new Vector();
		 			tosend.add(smscontent);		 			
		 			smsmanager.sendSmscontent(tosend);
		 			//�Ѹý�����Ϣ��ӵ��Ѿ����͵Ľ��̶����б���,��IP:��������Ϊkey
		 			sendeddata.put(procs.getIpaddress()+":"+procs.getProcname(),procs);		 						 				
		 		}else{
		 			//���Ѿ����͵Ķ����б�������IP��PROC����
		 			//���ڣ�����ѷ��Ͷ����б����ж��Ƿ��Ѿ����͵���Ķ���		 				
		 			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		 			Date last = null;
		 			Date current = null;
		 			Calendar sendcalen = (Calendar)lastprocs.getCollecttime();
		 			Date cc = sendcalen.getTime();
		 			String tempsenddate = formatter.format(cc);
		 			
		 			Calendar currentcalen = (Calendar)procs.getCollecttime();
		 			cc = currentcalen.getTime();
		 			last = formatter.parse(tempsenddate);
		 			String currentsenddate = formatter.format(cc);
		 			current = formatter.parse(currentsenddate);
		 			
		 			long subvalue = current.getTime()-last.getTime();			 			
		 			
		 			if (subvalue/(1000*60*60*24)>=1){
		 				//����һ�죬���ٷ���Ϣ
			 			Smscontent smscontent = new Smscontent();
			 			String time = sdf.format(procs.getCollecttime().getTime());
			 			Equipment equipment = equipmentManager.getByip(procs.getIpaddress());
			 			if (equipment == null){
			 				return;
			 			}else
			 				smscontent.setMessage(time+"&"+equipment.getEquipname()+"&"+equipment.getIpaddress()+"&"+procs.getProcname()+"&���̶�ʧ&level=2");
			 			
			 			//���Ͷ���
			 			Vector tosend = new Vector();
			 			tosend.add(smscontent);		 			
			 			smsmanager.sendSmscontent(tosend);
			 			//�Ѹý�����Ϣ��ӵ��Ѿ����͵Ľ��̶����б���,��IP:��������Ϊkey
			 			sendeddata.put(procs.getIpaddress()+":"+procs.getProcname(),procs);		 						 				
			 		}else{
			 			//û����һ��,��ֻд�¼�
			 			Vector eventtmpV = new Vector();
						EventList event = new EventList();
						  Monitoriplist monitoriplist = (Monitoriplist)monitormanager.getByIpaddress(procs.getIpaddress());
						  event.setEventtype("host");
						  event.setEventlocation(procs.getIpaddress());
						  event.setManagesign(new Integer(0));
						  event.setReportman("monitorpc");
						  event.setRecordtime(Calendar.getInstance());
						  event.setLevel1(new Integer(1));
						  event.setEquipment(equipmentManager.getByip(monitoriplist.getIpaddress()));
						  event.setNetlocation(equipmentManager.getByip(monitoriplist.getIpaddress()).getNetlocation());
						  String time = sdf.format(Calendar.getInstance().getTime());
						  event.setContent(monitoriplist.getEquipname()+"&"+monitoriplist.getIpaddress()+"&"+time+"����"+procs.getProcname()+"��ʧ&level=1");
						  eventtmpV.add(event);
						  try{
							  eventmanager.createEventlist(eventtmpV);
						  }catch(Exception e){
							  e.printStackTrace();
						  }						  
			 		}
		 		}
		 	}catch(Exception e){
		 		e.printStackTrace();
		 	}
		 	*/
		 }
	 
	/**
	 * 
	 * ���ڴ�����л�ȡ��ǰ��Ҫƥ��Ľ�����
	 * @param ipaddressip��ַ
	 * @return
	 */
	 public Vector Getipprocesslist(String ipaddress)
	 {
		 Vector list=new Vector();
		 Hashtable Vector  = new Hashtable();//���̵������б�

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
	 
	 public void createFileNotExistSMS(String ipaddress){
		 	//��������		 	
		 	//���ڴ����õ�ǰ���IP��PING��ֵ
				Calendar date=Calendar.getInstance();
				try{
					Host host = (Host)PollingEngine.getInstance().getNodeByIP(ipaddress);
					if(host == null)return;
					
					if (!sendeddata.containsKey(ipaddress+":file:"+host.getId())){
						//�����ڣ��������ţ�������ӵ������б���
						Smscontent smscontent = new Smscontent();
						String time = sdf.format(date.getTime());
						smscontent.setLevel("3");
						smscontent.setObjid(host.getId()+"");
						smscontent.setMessage(host.getAlias()+" ("+host.getIpAddress()+")"+"����־�ļ��޷���ȷ�ϴ������ܷ�����");
						smscontent.setRecordtime(time);
						smscontent.setSubtype("host");
						smscontent.setSubentity("ftp");
						smscontent.setIp(host.getIpAddress());//���Ͷ���
						SmscontentDao smsmanager=new SmscontentDao();
						smsmanager.sendURLSmscontent(smscontent);	
						sendeddata.put(ipaddress+":file"+host.getId(),date);		 					 				
					}else{
						//���ڣ�����ѷ��Ͷ����б����ж��Ƿ��Ѿ����͵���Ķ���
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
							//����һ�죬���ٷ���Ϣ
							Smscontent smscontent = new Smscontent();
							String time = sdf.format(date.getTime());
							smscontent.setLevel("3");
							smscontent.setObjid(host.getId()+"");
							smscontent.setMessage(host.getAlias()+" ("+host.getIpAddress()+")"+"����־�ļ��޷���ȷ�ϴ������ܷ�����");
							smscontent.setRecordtime(time);
							smscontent.setSubtype("host");
							smscontent.setSubentity("ftp");
							smscontent.setIp(host.getIpAddress());//���Ͷ���
							SmscontentDao smsmanager=new SmscontentDao();
							smsmanager.sendURLSmscontent(smscontent);
							//�޸��Ѿ����͵Ķ��ż�¼	
							sendeddata.put(ipaddress+":file:"+host.getId(),date);	
						}	
					}	 			 			 			 			 	
				}catch(Exception e){
					e.printStackTrace();
				}
		 	}	 
}






