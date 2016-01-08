/**
 * <p>Description:logger,writes error and debug information within system running</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-06
 */

package com.afunms.common.util;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import com.afunms.alarm.dao.SendAlarmTimeDao;
import com.afunms.alarm.model.AlarmIndicatorsNode;
import com.afunms.alarm.model.AlarmPort;
import com.afunms.alarm.model.SendAlarmTime;
import com.afunms.alarm.send.SendAlarmUtil;
import com.afunms.alarm.util.AlarmConstant;
import com.afunms.alarm.util.AlarmIndicatorsUtil;
import com.afunms.alarm.util.AlarmResourceCenter;
import com.afunms.application.manage.PerformancePanelManager;
import com.afunms.application.model.HostServiceGroup;
import com.afunms.application.model.HostServiceGroupConfiguration;
import com.afunms.application.model.JobForAS400Group;
import com.afunms.application.model.JobForAS400GroupDetail;
import com.afunms.application.model.JobForAS400SubSystem;
import com.afunms.application.model.ProcessGroup;
import com.afunms.application.model.ProcessGroupConfiguration;
import com.afunms.application.model.SystemFlag;
import com.afunms.application.util.HostServiceGroupConfigurationUtil;
import com.afunms.application.util.JobForAS400GroupDetailUtil;
import com.afunms.application.util.ProcessGroupConfigurationUtil;
import com.afunms.common.base.BaseVo;
import com.afunms.config.dao.AclBaseDao;
import com.afunms.config.model.AclDetail;
import com.afunms.config.model.Diskconfig;
import com.afunms.config.model.PolicyInterface;
import com.afunms.config.model.QueueInfo;
import com.afunms.event.dao.CheckEventDao;
import com.afunms.event.dao.EventListDao;
import com.afunms.event.model.CheckEvent;
import com.afunms.event.model.EventList;
import com.afunms.indicators.model.NodeDTO;
import com.afunms.indicators.model.NodeGatherIndicators;
import com.afunms.indicators.util.NodeUtil;
import com.afunms.initialize.ResourceCenter;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.base.Node;
import com.afunms.polling.node.Ftp;
import com.afunms.polling.node.Host;
import com.afunms.polling.node.Mail;
import com.afunms.polling.node.Web;
import com.afunms.polling.om.AllUtilHdx;
import com.afunms.polling.om.CPUcollectdata;
import com.afunms.polling.om.Diskcollectdata;
import com.afunms.polling.om.Interfacecollectdata;
import com.afunms.polling.om.Memorycollectdata;
import com.afunms.polling.om.Pingcollectdata;
import com.afunms.polling.om.Processcollectdata;
import com.afunms.polling.om.Servicecollectdata;
import com.afunms.polling.om.UtilHdx;
import com.afunms.topology.model.JobForAS400;
import com.afunms.topology.model.SubsystemForAS400;
import com.afunms.util.AgentalarmControlutil;

/**
 * @author Administrator
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class CheckEventUtil {
         
        public CheckEventUtil(){
        }
         
        /**
         * CPU,MEMORY,��������Ϣ���
         */
        public void updateData(Object vo , Object collectingData,String type,String subtype,AlarmIndicatorsNode alarmIndicatorsNode){
        	//SysLogger.info(alarmIndicatorsNode.getName() + "===========CPU,MEMORY,��������Ϣ���=================");
            try {	
            	
            	
            	Node node = (Node)vo;
				//���ڴ�����صĸ澯���,����ʵ�ִ����ݿ����ɾ���������
				deleteEvent(node.getId()+":"+alarmIndicatorsNode.getType()+":"+alarmIndicatorsNode.getName());
				
				AgentalarmControlutil.DeleteAlarmlevel(node.getId()+"", alarmIndicatorsNode.getName(), alarmIndicatorsNode.getType(), "");
				
				
				Hashtable datahashtable = (Hashtable)collectingData;
      			//��ȡJOB,�ж�JOB����
      			List joblist = new ArrayList();
      			//��ȡϵͳ��Ϣ,�õ�CPU������
      			Hashtable systemStatushashtable = new Hashtable();
				if("1".equals(alarmIndicatorsNode.getEnabled())){
					String indicators = alarmIndicatorsNode.getName();
					String value = "0";
					String limvalue = "";
					if("jobnumber".equals(indicators)){
						if(datahashtable.get("Jobs") != null)joblist = (List)datahashtable.get("Jobs");
						if(joblist == null)joblist = new ArrayList();
						value = joblist.size()+"";
					}else if("cpu".equals(indicators)){
						//CUP������
						Vector cpuVector = new Vector();
						if(datahashtable.get("cpu") != null)cpuVector = (Vector)datahashtable.get("cpu");
						if(cpuVector != null && cpuVector.size()>0){
							
							CPUcollectdata cpudata=(CPUcollectdata)cpuVector.get(0);
							value = cpudata.getThevalue();
						}
						if(systemStatushashtable.get("cpu")!= null)value = (String)systemStatushashtable.get("cpu");
					}else if("pagingusage".equals(indicators)){
						//��ҳ��������
						Hashtable paginghash = new Hashtable();
						if(datahashtable.get("pagingusage") != null)paginghash = (Hashtable)datahashtable.get("pagingusage");
						if(paginghash != null && paginghash.size()>0){
							if(paginghash.get("Percent_Used") != null){
								value=((String)paginghash.get("Percent_Used")).replaceAll("%", "");
							}
							
						}
						if(systemStatushashtable.get("cpu")!= null)value = (String)systemStatushashtable.get("cpu");
					}else if("physicalmemory".equals(indicators)){
						//�����ڴ�������
						Vector memoryVector = new Vector();
						if(datahashtable.get("physicalmem") != null)memoryVector = (Vector)datahashtable.get("physicalmem");
						if(memoryVector != null && memoryVector.size()>0){
							for(int i=0;i<memoryVector.size();i++){
								Memorycollectdata memorydata = (Memorycollectdata)memoryVector.get(i);
								//SysLogger.info("windows=========="+memorydata.getSubentity()+"==="+memorydata.getEntity()+"==="+memorydata.getThevalue());
								if("PhysicalMemory".equalsIgnoreCase(memorydata.getSubentity()) && memorydata.getEntity().equalsIgnoreCase("Utilization")){
									value = memorydata.getThevalue();
									break;
								}
							}
						}else{
							return;
						}
					}else if("virtualmemory".equals(indicators)){
						//�����ڴ�������
						Vector memoryVector = new Vector();
						if(datahashtable.get("virtalmem") != null)memoryVector = (Vector)datahashtable.get("virtalmem");
						if(memoryVector != null && memoryVector.size()>0){
							for(int i=0;i<memoryVector.size();i++){
								Memorycollectdata memorydata = (Memorycollectdata)memoryVector.get(i);
								if("VirtualMemory".equalsIgnoreCase(memorydata.getSubentity()) && memorydata.getEntity().equalsIgnoreCase("Utilization")){
									value = memorydata.getThevalue();
									break;
								}
							}
						}else{
							return;
						}
					}else if("swapmemory".equals(indicators)){
						//�����ڴ�������
						Vector memoryVector = new Vector();
						if(datahashtable.get("swapmem") != null)memoryVector = (Vector)datahashtable.get("swapmem");
						if(memoryVector != null && memoryVector.size()>0){
							for(int i=0;i<memoryVector.size();i++){
								Memorycollectdata memorydata = (Memorycollectdata)memoryVector.get(i);
								if(memorydata.getEntity().equalsIgnoreCase("Utilization")){
									value = memorydata.getThevalue();
									break;
								}
							}
						}else{
							return;
						}
					}else if("iowait".equals(indicators)){
						//ioƽ���ȴ�ʱ��
						//Vector avwaitVector = new Vector();
						Hashtable avwaitVector = new Hashtable();
						if(datahashtable.get("vmstat") != null){
							        Hashtable avwaiths = (Hashtable)datahashtable.get("vmstat");
							        if(avwaiths!=null && avwaiths.size()>0){
									value = (String)avwaiths.get("iw");
							        }else{
							        	return;
							        }
						}else{
							return;
						}
					}else if("AllInBandwidthUtilHdx".equals(indicators) || "utilhdx".equals(indicators) 
							|| "AllOutBandwidthUtilHdx".equals(indicators)){
						
						//���������
						
						Vector allutilVector = new Vector();
						if(datahashtable.get("allutilhdx") != null)allutilVector = (Vector)datahashtable.get("allutilhdx");
						
						if(allutilVector != null && allutilVector.size()>0){
							
							for(int i=0;i<allutilVector.size();i++){
								AllUtilHdx allutilhdx = (AllUtilHdx)allutilVector.get(i);
								
								 
								if(allutilhdx.getEntity().equalsIgnoreCase(indicators)){
									
									value = allutilhdx.getThevalue();
									//System.out.println("=========indicators============="+indicators);
									//System.out.println("==="+value);
									break;
								}
											
							}
							
							
						}else{
							return;
						}
					}else if("diskperc".equals(indicators)){
						return;
					}else if("send".equals(indicators)){
						//�ʼ����ͷ���
						Vector mailVector = new Vector();
						if(datahashtable.get("mail") != null)mailVector = (Vector)datahashtable.get("mail");
						if(mailVector != null && mailVector.size()>0){
							for(int i=0;i<mailVector.size();i++){
								Interfacecollectdata maildata = (Interfacecollectdata)mailVector.get(i);
								if(maildata.getEntity().equalsIgnoreCase("Send")){
									value = maildata.getThevalue();
									break;
								}
							}
						}else{
							return;
						}
					}else if("receieve".equalsIgnoreCase(indicators)){
						//�ʼ����ͷ���
						Vector mailVector = new Vector();
						if(datahashtable.get("mail") != null)mailVector = (Vector)datahashtable.get("mail");
						if(mailVector != null && mailVector.size()>0){
							for(int i=0;i<mailVector.size();i++){
								Interfacecollectdata maildata = (Interfacecollectdata)mailVector.get(i);
								if(maildata.getEntity().equalsIgnoreCase("receieve")){
									value = maildata.getThevalue();
									break;
								}
							}
						}else{
							return;
						}
					}else if("upload".equals(indicators)){
						//�ʼ����ͷ���
						Vector ftpVector = new Vector();
						if(datahashtable.get("ftp") != null)ftpVector = (Vector)datahashtable.get("ftp");
						if(ftpVector != null && ftpVector.size()>0){
							for(int i=0;i<ftpVector.size();i++){
								Interfacecollectdata ftpdata = (Interfacecollectdata)ftpVector.get(i);
								if(ftpdata.getEntity().equalsIgnoreCase("upload")){
									value = ftpdata.getThevalue();
									break;
								}
							}
						}else{
							return;
						}
					}else if("download".equalsIgnoreCase(indicators)){
						//�ʼ����ͷ���
						Vector ftpVector = new Vector();
						if(datahashtable.get("ftp") != null)ftpVector = (Vector)datahashtable.get("ftp");
						if(ftpVector != null && ftpVector.size()>0){
							for(int i=0;i<ftpVector.size();i++){
								Interfacecollectdata ftpdata = (Interfacecollectdata)ftpVector.get(i);
								if(ftpdata.getEntity().equalsIgnoreCase("download")){
									value = ftpdata.getThevalue();
									break;
								}
							}
						}else{
							return;
						}
					}else if("socketping".equalsIgnoreCase(indicators)){
						//SOCKET����
						Vector socketVector = new Vector();
						if(datahashtable.get("socket") != null)socketVector = (Vector)datahashtable.get("socket");
						if(socketVector != null && socketVector.size()>0){
							Pingcollectdata socketdata = (Pingcollectdata)socketVector.get(0);
								if(socketdata.getEntity().equalsIgnoreCase("Utilization")){
									value = socketdata.getThevalue();
								}
						}else{
							return;
						}
					}else if("webping".equalsIgnoreCase(indicators)){
						//web����
						Vector webVector = new Vector();
						if(datahashtable.get("url") != null)webVector = (Vector)datahashtable.get("url");
						if(webVector != null && webVector.size()>0){
							for(int i=0;i<webVector.size();i++){
								Pingcollectdata webdata = (Pingcollectdata)webVector.get(i);
								if(webdata.getEntity().equalsIgnoreCase("Utilization")){
									value = webdata.getThevalue();
								}
							}
						}else{
							return;
						}
					}else if("webresponsetime".equalsIgnoreCase(indicators)){
						//web����
						Vector webVector = new Vector();
						if(datahashtable.get("url") != null)webVector = (Vector)datahashtable.get("url");
						if(webVector != null && webVector.size()>0){
							for(int i=0;i<webVector.size();i++){
								Pingcollectdata webdata = (Pingcollectdata)webVector.get(i);
								if(webdata.getEntity().equalsIgnoreCase("webresponsetime")){
									value = webdata.getThevalue();
								}
							}
						}else{
							return;
						}
					}else if("webpagesize".equalsIgnoreCase(indicators)){
						//WEB����
						Vector webVector = new Vector();
						if(datahashtable.get("url") != null)webVector = (Vector)datahashtable.get("url");
						if(webVector != null && webVector.size()>0){
							for(int i=0;i<webVector.size();i++){
								Pingcollectdata webdata = (Pingcollectdata)webVector.get(i);
								if(webdata.getEntity().equalsIgnoreCase("webpagesize")){
									value = webdata.getThevalue();
								}
							}
						}else{
							return;
						}
					}else if("webkeyword".equalsIgnoreCase(indicators)){
						//WEB�Ĺؼ��ּ�����
						Vector webVector = new Vector();
						if(datahashtable.get("url") != null)webVector = (Vector)datahashtable.get("url");
						if(webVector != null && webVector.size()>0){
							for(int i=0;i<webVector.size();i++){
								Pingcollectdata webdata = (Pingcollectdata)webVector.get(i);
								if(webdata.getEntity().equalsIgnoreCase("webkeyword")){
									value = webdata.getThevalue();
								}
							}
						}else{
							return;
						}
					}else if("droprate".equalsIgnoreCase(indicators)){
						//������
						List<PolicyInterface> interfaceList = new ArrayList<PolicyInterface>();
						if(datahashtable.get("policy") != null)interfaceList = (List<PolicyInterface>)datahashtable.get("policy");
						if(interfaceList != null && interfaceList.size()>0){
							for(int i=0;i<interfaceList.size();i++){
								PolicyInterface interData = (PolicyInterface)interfaceList.get(i);
									value =interData.getDropRate()+"";
							}
						}else{
							return;
						}
					}else {
						return;
					}
					if(value == null){
						return;
					}
					if( AlarmConstant.DATATYPE_NUMBER.equals(alarmIndicatorsNode.getDatatype())){
						try {
							double value_int = Double.valueOf(value);
							double Limenvalue2 = Double.valueOf(alarmIndicatorsNode.getLimenvalue2());
							double Limenvalue1 = Double.valueOf(alarmIndicatorsNode.getLimenvalue1());
							double Limenvalue0 = Double.valueOf(alarmIndicatorsNode.getLimenvalue0());
							
							//SysLogger.info(alarmIndicatorsNode.getNodeid()+"=="+alarmIndicatorsNode.getName()+"=="+value_int+"=="+Limenvalue0+"=="+Limenvalue1+"=="+Limenvalue2);
							
							String level = "";
							String alarmTimes = "";
							
							// �Ƿ񳬹���ֵ
							boolean result = true;     
							
							if(alarmIndicatorsNode.getCompare()==0){
								//����Ƚ�
								if(value_int <= Limenvalue2){
									level = "3";
									alarmTimes = alarmIndicatorsNode.getTime2();//���ʹ���
									limvalue = alarmIndicatorsNode.getLimenvalue2();
								}else if(value_int <= Limenvalue1){
									level = "2";
									alarmTimes = alarmIndicatorsNode.getTime1();
									limvalue = alarmIndicatorsNode.getLimenvalue1();
								}else if(value_int <= Limenvalue0){
									level = "1";
									alarmTimes = alarmIndicatorsNode.getTime0();
									limvalue = alarmIndicatorsNode.getLimenvalue0();
								}else{
									result = false;
								}
							}else{
								//����Ƚ�
								if(value_int >= Limenvalue2){
									level = "3";
									alarmTimes = alarmIndicatorsNode.getTime2();
									limvalue = alarmIndicatorsNode.getLimenvalue2();
								}else if(value_int >= Limenvalue1){
									level = "2";
									alarmTimes = alarmIndicatorsNode.getTime1();
									limvalue = alarmIndicatorsNode.getLimenvalue1();
								}else if(value_int >= Limenvalue0){
									level = "1";
									alarmTimes = alarmIndicatorsNode.getTime0();
									limvalue = alarmIndicatorsNode.getLimenvalue0();
								}else{
									result = false;
								}
							}
							
							
//							System.out.println("===================level="+level );
//							System.out.println("===================alarmIndicatorsNode-name="+alarmIndicatorsNode.getName() );
//							System.out.println("===================result="+result );
//							
//							System.out.println("==&&&***+===============alarm====================");
//							System.out.println(ShareData.getAgentalarmlevellist().toString());
//							System.out.println("==&&&***+================alarm===================");
//							
							
							// �澯��Դ�е� �¼�����
							String num = (String)AlarmResourceCenter.getInstance().getAttribute(alarmIndicatorsNode.getNodeid()+":"+alarmIndicatorsNode.getType()+":"+alarmIndicatorsNode.getName());
							
							if(num == null || "".equals(num)){
								num = "0";
							}
							
							if(!result){
								
								
								//System.out.println("===================û�и澯==1122============================");
								
								//ShareData.getAgentalarmlevellist().remove(node.getId()+":"+alarmIndicatorsNode.getType()+":"+alarmIndicatorsNode.getName());
								AgentalarmControlutil.DeleteAlarmlevel(node.getId()+"", alarmIndicatorsNode.getName(), alarmIndicatorsNode.getType(), "");
								
								// δ�����澯��ֵ ��ɾ���澯���͵�ʱ��ļ�¼
//								SendAlarmTimeDao sendAlarmTimeDao = new SendAlarmTimeDao();
//								try {
//									sendAlarmTimeDao.delete(node.getId()+":"+alarmIndicatorsNode.getType()+":"+alarmIndicatorsNode.getName());
//								} catch (Exception e) {
//									// TODO Auto-generated catch block
//									e.printStackTrace();
//								} finally {
//									sendAlarmTimeDao.close();
//								}
								// �����ʱδ�����澯 �� ��������Ϊ 0 ��
								num = "0";
								AlarmResourceCenter.getInstance().setAttribute(alarmIndicatorsNode.getNodeid()+":"+alarmIndicatorsNode.getType()+":"+alarmIndicatorsNode.getName(), num);
								return;
							}
							
							int num_int = 0;
							int alarmTimes_int = 0;
							try {
								num_int = Integer.valueOf(num);					//��ǰ�澯����
								alarmTimes_int = Integer.valueOf(alarmTimes);	//����ĸ澯����
							} catch (RuntimeException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
							
							//SysLogger.info(alarmIndicatorsNode.getNodeid()+"   "+alarmIndicatorsNode.getName()+" value: "+value+"  num_int: "+num_int+"    alarmTimes_int:" +alarmTimes_int);
							if(num_int+1 >= alarmTimes_int){
								
								//ShareData.getAgentalarmlevellist().put(node.getId()+":"+alarmIndicatorsNode.getType()+":"+alarmIndicatorsNode.getName(), level);
								AgentalarmControlutil.PutAlarmlevel(node.getId()+"", alarmIndicatorsNode.getName(), alarmIndicatorsNode.getType(), "",level+"",true);
								
								if(vo instanceof Web){
									//�����ڴ���WEB��״̬
									Web _web = (Web)PollingEngine.getInstance().getWebByID(node.getId());	
									_web.setAlarm(true);
									_web.getAlarmMessage().add(alarmIndicatorsNode.getAlarm_info() + " ��ǰֵΪ��" + value +  alarmIndicatorsNode.getThreshlod_unit()+" ��ֵΪ:"+limvalue+alarmIndicatorsNode.getThreshlod_unit());
									//������֮ǰ�ĸ澯����,������󼶱�
									if(Integer.valueOf(level)> _web.getStatus())_web.setStatus(Integer.valueOf(level));
									if(Integer.valueOf(level)> _web.getAlarmlevel())_web.setAlarmlevel(Integer.valueOf(level));
									
									NodeUtil nodeUtil = new NodeUtil();
									NodeDTO nodeDTO = nodeUtil.conversionToNodeDTO(_web);
									sendAlarm(nodeDTO, alarmIndicatorsNode, value, Integer.valueOf(level),0);
								}else if(vo instanceof Host){
									// �¼����������澯���������� ���ɸ澯
									//SysLogger.info(alarmIndicatorsNode.getNodeid()+"=="+alarmIndicatorsNode.getName()+"=="+value_int+"=="+Limenvalue0+"=="+Limenvalue1+"=="+Limenvalue2);
									Host host = (Host) PollingEngine.getInstance().getNodeByID(node.getId());
									host.setAlarm(true);
									host.getAlarmMessage().add(alarmIndicatorsNode.getAlarm_info() + " ��ǰֵΪ��" + value +  alarmIndicatorsNode.getThreshlod_unit()+" ��ֵΪ:"+limvalue+alarmIndicatorsNode.getThreshlod_unit());
									//������֮ǰ�ĸ澯����,������󼶱�
									if(Integer.valueOf(level)> host.getStatus())host.setStatus(Integer.valueOf(level));
									if(Integer.valueOf(level)> host.getAlarmlevel())host.setAlarmlevel(Integer.valueOf(level));
									NodeUtil nodeUtil = new NodeUtil();
									NodeDTO nodeDTO = nodeUtil.conversionToNodeDTO(host);
									sendAlarm(nodeDTO, alarmIndicatorsNode, value, Integer.valueOf(level),0);
								}
								
							}else{
								num_int = num_int + 1;
								AlarmResourceCenter.getInstance().setAttribute(String.valueOf(alarmIndicatorsNode.getId()), String.valueOf(num_int));
							}
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}else if( AlarmConstant.DATATYPE_STRING.equals(alarmIndicatorsNode.getDatatype())){
						try {
							String value_str = value;
							String Limenvalue2 = alarmIndicatorsNode.getLimenvalue2();
							String Limenvalue1 = alarmIndicatorsNode.getLimenvalue1();
							String Limenvalue0 = alarmIndicatorsNode.getLimenvalue0();
							
							//SysLogger.info(alarmIndicatorsNode.getNodeid()+"=="+alarmIndicatorsNode.getName()+"=="+value_str+"=="+Limenvalue0+"=="+Limenvalue1+"=="+Limenvalue2);
							
							String level = "";
							String alarmTimes = "";
							
							// �Ƿ񳬹���ֵ
							boolean result = true;     
							
							if(alarmIndicatorsNode.getCompare()==0){
								//����Ƚ�
								//���ø÷�ֵ�ж�ָ��
								if("1".equals(alarmIndicatorsNode.getEnabled())){
									if(value_str.equalsIgnoreCase(Limenvalue2)){
										level = "3";
										alarmTimes = alarmIndicatorsNode.getTime2();
										limvalue = alarmIndicatorsNode.getLimenvalue2();
									}else if(value_str.equalsIgnoreCase(Limenvalue1)){
										level = "2";
										alarmTimes = alarmIndicatorsNode.getTime1();
										limvalue = alarmIndicatorsNode.getLimenvalue1();
									}else if(value_str.equalsIgnoreCase(Limenvalue0)){
										level = "1";
										alarmTimes = alarmIndicatorsNode.getTime0();
										limvalue = alarmIndicatorsNode.getLimenvalue0();
									}else{
										result = false; 
									}
								}
							}else{
								//����Ƚ�
								//���ø÷�ֵ�ж�ָ��
								if("1".equals(alarmIndicatorsNode.getEnabled())){
									if(value_str.equalsIgnoreCase(Limenvalue2)){
										level = "3";
										alarmTimes = alarmIndicatorsNode.getTime2();
										limvalue = alarmIndicatorsNode.getLimenvalue2();
									}else if(value_str.equalsIgnoreCase(Limenvalue1)){
										level = "2";
										alarmTimes = alarmIndicatorsNode.getTime1();
										limvalue = alarmIndicatorsNode.getLimenvalue1();
									}else if(value_str.equalsIgnoreCase(Limenvalue0)){
										level = "1";
										alarmTimes = alarmIndicatorsNode.getTime0();
										limvalue = alarmIndicatorsNode.getLimenvalue0();
									}else{
										result = false; 
									}
								}
							}
							
							// �澯��Դ�е� �¼�����
							String num = (String)AlarmResourceCenter.getInstance().getAttribute(alarmIndicatorsNode.getNodeid()+":"+alarmIndicatorsNode.getType()+":"+alarmIndicatorsNode.getName());
							
							if(num == null || "".equals(num)){
								num = "0";
							}
							
							//System.out.println("==&&&***+===============alarm====================");
							//System.out.println(ShareData.getAgentalarmlevellist().toString());
							//System.out.println("==&&&***+================alarm===================");
							
							if(!result){
								// δ�����澯��ֵ ��ɾ���澯���͵�ʱ��ļ�¼
								//ShareData.getAgentalarmlevellist().remove(node.getId()+":"+alarmIndicatorsNode.getType()+":"+alarmIndicatorsNode.getName());
								AgentalarmControlutil.DeleteAlarmlevel(node.getId()+"", alarmIndicatorsNode.getName(), alarmIndicatorsNode.getType(), "");
								
								AgentalarmControlutil.delete_nms_send_alarm_time(alarmIndicatorsNode,node.getId()+":"+alarmIndicatorsNode.getType()+":"+alarmIndicatorsNode.getName());
								
								
								
								//System.out.println("==11=�������澯===="+alarmIndicatorsNode.getType());
//								SendAlarmTimeDao sendAlarmTimeDao = new SendAlarmTimeDao();
//								try {
//									sendAlarmTimeDao.delete(node.getId()+":"+alarmIndicatorsNode.getType()+":"+alarmIndicatorsNode.getName());
//								} catch (Exception e) {
//									// TODO Auto-generated catch block
//									e.printStackTrace();
//								} finally {
//									sendAlarmTimeDao.close();
//								}
								// �����ʱδ�����澯 �� ��������Ϊ 0 ��
								num = "0";
								AlarmResourceCenter.getInstance().setAttribute(alarmIndicatorsNode.getNodeid()+":"+alarmIndicatorsNode.getType()+":"+alarmIndicatorsNode.getName(), num);
								return;
							}
							
							int num_int = 0;
							int alarmTimes_int = 0;
							try {
								num_int = Integer.valueOf(num);					//��ǰ�澯����
								alarmTimes_int = Integer.valueOf(alarmTimes);	//����ĸ澯����
							} catch (RuntimeException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
							
							//SysLogger.info(alarmIndicatorsNode.getNodeid()+"   "+alarmIndicatorsNode.getName()+" value: "+value+"  num_int: "+num_int+"    alarmTimes_int:" +alarmTimes_int);
							if(num_int+1 >= alarmTimes_int){
								// �¼����������澯���������� ���ɸ澯
								
								//�Ѹ澯����Ϣ�θ澯���뵽�ڴ���
								//ShareData.getAgentalarmlevellist().put(node.getId()+":"+alarmIndicatorsNode.getType()+":"+alarmIndicatorsNode.getName(), level);
								AgentalarmControlutil.PutAlarmlevel(node.getId()+"", alarmIndicatorsNode.getName(), alarmIndicatorsNode.getType(), "",limvalue+"",true);
								
								
				            	if(vo instanceof Mail){
									//�����ڴ���MAIL��״̬
				            		Mail mail = (Mail)PollingEngine.getInstance().getMailByID(node.getId());
				            		mail.setAlarm(true);
									mail.getAlarmMessage().add(alarmIndicatorsNode.getAlarm_info() + " ��ǰֵΪ��" + value +  alarmIndicatorsNode.getThreshlod_unit()+" ��ֵΪ:"+limvalue+alarmIndicatorsNode.getThreshlod_unit());
									//������֮ǰ�ĸ澯����,������󼶱�
									if(Integer.valueOf(level)> mail.getStatus())mail.setStatus(Integer.valueOf(level));
									if(Integer.valueOf(level)> mail.getAlarmlevel())mail.setAlarmlevel(Integer.valueOf(level));
									
									NodeUtil nodeUtil = new NodeUtil();
									NodeDTO nodeDTO = nodeUtil.conversionToNodeDTO(mail);
									sendAlarm(nodeDTO, alarmIndicatorsNode, value, Integer.valueOf(level),1);
				            	}else if(vo instanceof Ftp){
									//�����ڴ���MAIL��״̬
				            		Ftp ftp = (Ftp)PollingEngine.getInstance().getFtpByID(node.getId());
				            		ftp.setAlarm(true);
				            		ftp.getAlarmMessage().add(alarmIndicatorsNode.getAlarm_info() + " ��ǰֵΪ��" + value +  alarmIndicatorsNode.getThreshlod_unit()+" ��ֵΪ:"+limvalue+alarmIndicatorsNode.getThreshlod_unit());
									//������֮ǰ�ĸ澯����,������󼶱�
									if(Integer.valueOf(level)> ftp.getStatus())ftp.setStatus(Integer.valueOf(level));
									if(Integer.valueOf(level)> ftp.getAlarmlevel())ftp.setAlarmlevel(Integer.valueOf(level));
									
									NodeUtil nodeUtil = new NodeUtil();
									NodeDTO nodeDTO = nodeUtil.conversionToNodeDTO(ftp);
									sendAlarm(nodeDTO, alarmIndicatorsNode, value, Integer.valueOf(level),1);
				            	}else if(vo instanceof com.afunms.polling.node.SocketService){									
									//�����ڴ���SOCKET��״̬
									com.afunms.polling.node.SocketService _tnode=(com.afunms.polling.node.SocketService)PollingEngine.getInstance().getSocketByID(node.getId());
									//SocketService socket = (SocketService)PollingEngine.getInstance().getSocketByID(node.getId());
									_tnode.setAlarm(true);
									_tnode.getAlarmMessage().add(alarmIndicatorsNode.getAlarm_info() + " ��ǰֵΪ��" + value +  alarmIndicatorsNode.getThreshlod_unit()+" ��ֵΪ:"+limvalue+alarmIndicatorsNode.getThreshlod_unit());
									//������֮ǰ�ĸ澯����,������󼶱�
									if(Integer.valueOf(level)> _tnode.getStatus())_tnode.setStatus(Integer.valueOf(level));
									if(Integer.valueOf(level)> _tnode.getAlarmlevel())_tnode.setAlarmlevel(Integer.valueOf(level));
									
									NodeUtil nodeUtil = new NodeUtil();
									NodeDTO nodeDTO = nodeUtil.conversionToNodeDTO(_tnode);
									sendAlarm(nodeDTO, alarmIndicatorsNode, value, Integer.valueOf(level),1);
				            	}else if(vo instanceof Web){
									//�����ڴ���WEB��״̬
									Web _web = (Web)PollingEngine.getInstance().getWebByID(node.getId());	
									_web.setAlarm(true);
									_web.getAlarmMessage().add(alarmIndicatorsNode.getAlarm_info() + " ��ǰֵΪ��" + value +  alarmIndicatorsNode.getThreshlod_unit()+" ��ֵΪ:"+limvalue+alarmIndicatorsNode.getThreshlod_unit());
									//������֮ǰ�ĸ澯����,������󼶱�
									if(Integer.valueOf(level)> _web.getStatus())_web.setStatus(Integer.valueOf(level));
									if(Integer.valueOf(level)> _web.getAlarmlevel())_web.setAlarmlevel(Integer.valueOf(level));
									
									NodeUtil nodeUtil = new NodeUtil();
									NodeDTO nodeDTO = nodeUtil.conversionToNodeDTO(_web);
									sendAlarm(nodeDTO, alarmIndicatorsNode, value, Integer.valueOf(level),1);
				            	}
//								Host host = (Host) PollingEngine.getInstance().getNodeByID(node.getId());
//								host.setAlarm(true);
//								host.getAlarmMessage().add(alarmIndicatorsNode.getAlarm_info() + " ��ǰֵΪ��" + value +  alarmIndicatorsNode.getThreshlod_unit()+" ��ֵΪ:"+limvalue+alarmIndicatorsNode.getThreshlod_unit());
//								//������֮ǰ�ĸ澯����,������󼶱�
//								if(Integer.valueOf(level)> host.getStatus())host.setStatus(Integer.valueOf(level));
//								if(Integer.valueOf(level)> host.getAlarmlevel())host.setAlarmlevel(Integer.valueOf(level));
//								NodeUtil nodeUtil = new NodeUtil();
//								NodeDTO nodeDTO = nodeUtil.conversionToNodeDTO(host);
//								sendAlarm(nodeDTO, alarmIndicatorsNode, value, Integer.valueOf(level));
							}else{
								AgentalarmControlutil.DeleteAlarmlevel(node.getId()+"", alarmIndicatorsNode.getName(), alarmIndicatorsNode.getType(), "");
								num_int = num_int + 1;
								AlarmResourceCenter.getInstance().setAttribute(alarmIndicatorsNode.getNodeid()+":"+alarmIndicatorsNode.getType()+":"+alarmIndicatorsNode.getName(), String.valueOf(num_int));
							}
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					
				}
      		} catch (Exception e) {
      			// TODO Auto-generated catch block
      			e.printStackTrace();
      		}
      	}
        public void checkData(Object vo , Object collectingData,String type,String subtype,AlarmIndicatorsNode alarmIndicatorsNode){
        	//SysLogger.info(alarmIndicatorsNode.getName() + "===========CPU,MEMORY,��������Ϣ���=================");
            try {				
            	Node node = (Node)vo;
				//���ڴ�����صĸ澯���,����ʵ�ִ����ݿ����ɾ���������
				deleteEvent(node.getId()+":"+alarmIndicatorsNode.getType()+":"+alarmIndicatorsNode.getName());
				Hashtable datahashtable = (Hashtable)collectingData;
      			//��ȡJOB,�ж�JOB����
      			List joblist = new ArrayList();
				if("1".equals(alarmIndicatorsNode.getEnabled())){
					String indicators = alarmIndicatorsNode.getName();
					String value = "0";
					String limvalue = "";
					 if("droprate".equalsIgnoreCase(indicators)){
						//������
						List<PolicyInterface> interfaceList = new ArrayList<PolicyInterface>();
						if(datahashtable.get("policy") != null)interfaceList = (List<PolicyInterface>)datahashtable.get("policy");
						if(interfaceList != null && interfaceList.size()>0){
							for(int i=0;i<interfaceList.size();i++){
								PolicyInterface interData = (PolicyInterface)interfaceList.get(i);
									value =interData.getDropRate()+"";
									if(value == null){
										continue;
									}
									setAlarmEvent(vo, alarmIndicatorsNode, value, interData,indicators);
							}
						}else{
							return;
						}
					}else if("dropbytes".equalsIgnoreCase(indicators)){
						//������
						List<QueueInfo> queueList = new ArrayList<QueueInfo>();
						if(datahashtable.get("queue") != null)queueList = (List<QueueInfo>)datahashtable.get("queue");
						if(queueList != null && queueList.size()>0){
							for(int i=0;i<queueList.size();i++){
								QueueInfo queueInfo = (QueueInfo)queueList.get(i);
									value =queueInfo.getInputDrops()+"";
									if(value == null){
										continue;
									}
									setAlarmEvent(vo, alarmIndicatorsNode, "input queue", queueInfo,indicators);
									value =queueInfo.getOutputDrops()+"";
									if(value == null){
										continue;
									}
									setAlarmEvent(vo, alarmIndicatorsNode, "output queue", queueInfo,indicators);
							}
						}
						}else if ("matches".equalsIgnoreCase(indicators)) {
							List<AclDetail> detailList = new ArrayList<AclDetail>();
							if(datahashtable.get("detail") != null)detailList = (List<AclDetail>)datahashtable.get("detail");
							if(detailList != null && detailList.size()>0){
								AclBaseDao dao=null;
								HashMap<Integer, String> map=new HashMap<Integer, String>();
								try {
									 dao=new AclBaseDao();
									 map=dao.getDataByIp(node.getIpAddress());
								} catch (Exception e) {
									e.printStackTrace();
								}finally{
									dao.close();
								}
								
								for(int i=0;i<detailList.size();i++){
									AclDetail detail = (AclDetail)detailList.get(i);
										value=map.get(detail.getBaseId());
										setAlarmEvent(vo, alarmIndicatorsNode, value, detail,indicators);
								}
							}
						}else{
							return;
						}
					
					
			
				}
      		} catch (Exception e) {
      			// TODO Auto-generated catch block
      			e.printStackTrace();
      		}
      	}
        private void  setAlarmEvent(Object vo,AlarmIndicatorsNode alarmIndicatorsNode,String value,Object object,String objType) {
        	String limvalue = "";
        	Node node = (Node)vo;
        	PolicyInterface pInterface=null;
        	QueueInfo queueInfo=null;
        	AclDetail detail=null;
        	String temp=alarmIndicatorsNode.getAlarm_info();
        	String key="";
        	String realVal="";
        	String alamInfo="";
        	if (objType.equals("droprate")) {
        		pInterface=(PolicyInterface)object;
        		key=String.valueOf(alarmIndicatorsNode.getId())+":"+pInterface.getInterfaceName()+":"+pInterface.getClassName();
        		alamInfo=pInterface.getInterfaceName()+":"+pInterface.getClassName();
        		realVal=value;
			}else if (objType.equals("dropbytes")) {
				queueInfo=(QueueInfo)object;
				if(value.equals("input queue")){
				realVal=queueInfo.getInputDrops()+"";
				key=String.valueOf(node.getId()+":"+alarmIndicatorsNode.getId())+":"+queueInfo.getEntity()+":input";
				alamInfo=queueInfo.getEntity()+":�������";
				}else if(value.equals("output queue")) {
				realVal=queueInfo.getOutputDrops()+"";
				key=String.valueOf(node.getId()+":"+alarmIndicatorsNode.getId())+":"+queueInfo.getEntity()+":output";
				alamInfo=queueInfo.getEntity()+":�������";
				}
			}else if (objType.equals("matches")){
				detail=(AclDetail)object;
				key=String.valueOf(node.getId()+":"+alarmIndicatorsNode.getId())+":"+detail.getBaseId()+":"+detail.getName();
				alamInfo=value+":"+detail.getName();
				realVal=detail.getMatches()+"";
			}
    		if( AlarmConstant.DATATYPE_NUMBER.equals(alarmIndicatorsNode.getDatatype())){
				try {
					double value_int = Double.valueOf(realVal);
					double Limenvalue2 = Double.valueOf(alarmIndicatorsNode.getLimenvalue2());
					double Limenvalue1 = Double.valueOf(alarmIndicatorsNode.getLimenvalue1());
					double Limenvalue0 = Double.valueOf(alarmIndicatorsNode.getLimenvalue0());
					
					
					String level = "";
					String alarmTimes = "";
					
					// �Ƿ񳬹���ֵ
					boolean result = true;     
					
					if(alarmIndicatorsNode.getCompare()==0){
						//����Ƚ�
						if(value_int <= Limenvalue2){
							level = "3";
							alarmTimes = alarmIndicatorsNode.getTime2();
							limvalue = alarmIndicatorsNode.getLimenvalue2();
						}else if(value_int <= Limenvalue1){
							level = "2";
							alarmTimes = alarmIndicatorsNode.getTime1();
							limvalue = alarmIndicatorsNode.getLimenvalue1();
						}else if(value_int <= Limenvalue0){
							level = "1";
							alarmTimes = alarmIndicatorsNode.getTime0();
							limvalue = alarmIndicatorsNode.getLimenvalue0();
						}else{
							result = false;
						}
					}else{
						//����Ƚ�
						if(value_int >= Limenvalue2){
							level = "3";
							alarmTimes = alarmIndicatorsNode.getTime2();
							limvalue = alarmIndicatorsNode.getLimenvalue2();
						}else if(value_int >= Limenvalue1){
							level = "2";
							alarmTimes = alarmIndicatorsNode.getTime1();
							limvalue = alarmIndicatorsNode.getLimenvalue1();
						}else if(value_int >= Limenvalue0){
							level = "1";
							alarmTimes = alarmIndicatorsNode.getTime0();
							limvalue = alarmIndicatorsNode.getLimenvalue0();
						}else{
							result = false;
						}
					}
					
					// �澯��Դ�е� �¼�����
					
					String num = (String)AlarmResourceCenter.getInstance().getAttribute(key);
					
					if(num == null || "".equals(num)){
						num = "0";
					}
					
					if(!result){
						// δ�����澯��ֵ ��ɾ���澯���͵�ʱ��ļ�¼
						
						AgentalarmControlutil.delete_nms_send_alarm_time(alarmIndicatorsNode,node.getId()+":"+alarmIndicatorsNode.getType()+":"+alarmIndicatorsNode.getName());
//						SendAlarmTimeDao sendAlarmTimeDao = new SendAlarmTimeDao();
//						try {
//							sendAlarmTimeDao.delete(node.getId()+":"+alarmIndicatorsNode.getType()+":"+alarmIndicatorsNode.getName());
//						} catch (Exception e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						} finally {
//							sendAlarmTimeDao.close();
//						}
						// �����ʱδ�����澯 �� ��������Ϊ 0 ��
						num = "0";
						AlarmResourceCenter.getInstance().setAttribute(key, num);
						return;
					}
					
					int num_int = 0;
					int alarmTimes_int = 0;
					try {
						num_int = Integer.valueOf(num);					//��ǰ�澯����
						alarmTimes_int = Integer.valueOf(alarmTimes);	//����ĸ澯����
					} catch (RuntimeException e1) {
						e1.printStackTrace();
					}
					
					//SysLogger.info(alarmIndicatorsNode.getNodeid()+"   "+alarmIndicatorsNode.getName()+" value: "+value+"  num_int: "+num_int+"    alarmTimes_int:" +alarmTimes_int);
					if(num_int+1 >= alarmTimes_int){
						
						
						 if(vo instanceof Host){
							 
							// �¼����������澯���������� ���ɸ澯
							//SysLogger.info(alarmIndicatorsNode.getNodeid()+"=="+alarmIndicatorsNode.getName()+"=="+value_int+"=="+Limenvalue0+"=="+Limenvalue1+"=="+Limenvalue2);
							Host host = (Host) PollingEngine.getInstance().getNodeByID(node.getId());
							host.setAlarm(true);
							host.getAlarmMessage().add(alarmIndicatorsNode.getAlarm_info()+ " ��ǰֵΪ��" + realVal +  alarmIndicatorsNode.getThreshlod_unit()+" ��ֵΪ:"+limvalue+alarmIndicatorsNode.getThreshlod_unit());
							//������֮ǰ�ĸ澯����,������󼶱�
							if(Integer.valueOf(level)> host.getStatus())host.setStatus(Integer.valueOf(level));
							if(Integer.valueOf(level)> host.getAlarmlevel())host.setAlarmlevel(Integer.valueOf(level));
							NodeUtil nodeUtil = new NodeUtil();
							NodeDTO nodeDTO = nodeUtil.conversionToNodeDTO(host);
							sendAlarm(nodeDTO, alarmIndicatorsNode, realVal,alamInfo, Integer.valueOf(level),1);
						}
						
					}else{
						num_int = num_int + 1;
						AlarmResourceCenter.getInstance().setAttribute(key, String.valueOf(num_int));
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		}
        public void updatePortData(Object vo,Object collectingData,List list){
           				
            	Node node = (Node)vo;
            	
            	String unit="kb";
            	NodeUtil nodeUtil = new NodeUtil();
				NodeDTO nodeDTO = null;
				String value = "0";
				int levelValue =0;
					
				//���������
				//Vector allutilVector = new Vector();
						
				//if(((Hashtable)collectingData).get("utilhdx") != null)allutilVector = (Vector)((Hashtable)collectingData).get("utilhdx");
						
						if((Vector)((Hashtable)collectingData).get("utilhdx") != null && ((Vector)((Hashtable)collectingData).get("utilhdx")).size()>0 ){
							
								double levelValue1=0;
								double levelValue2=0;
								double levelValue3=0;
								String inOrOut="";
								
								if (list!=null&&list.size()>0) {
									AlarmPort port=null;
									UtilHdx utilhdx= null;
								for (int j = 0; j < list.size(); j++) {
									 port=(AlarmPort) list.get(j);
									 boolean flag=false;
									 for(int i=0;i<((Vector)((Hashtable)collectingData).get("utilhdx")).size();i++){
											utilhdx = (UtilHdx)((Vector)((Hashtable)collectingData).get("utilhdx")).get(i);
											if (utilhdx.getRestype().equals("dynamic")) {
												value = utilhdx.getThevalue();
											
											int sindex=Integer.parseInt(utilhdx.getSubentity());
										
									if (port.getPortindex()==sindex) {
										if (utilhdx.getEntity().equalsIgnoreCase("InBandwidthUtilHdx")) {//�������
											 levelValue1 = Double.valueOf(port.getLevelinvalue1());
											 levelValue2 = Double.valueOf(port.getLevelinvalue2());
											 levelValue3 = Double.valueOf(port.getLevelinvalue3());	
											 inOrOut="���";
											 flag=true;
										}else {//��������
											levelValue1 = Double.valueOf(port.getLeveloutvalue1());
											 levelValue2 = Double.valueOf(port.getLeveloutvalue2());
											 levelValue3 = Double.valueOf(port.getLeveloutvalue3());
											 inOrOut="����";
											 flag=true;
										}
										break;
									}
								}
									 }
									//���ڴ�����صĸ澯���,����ʵ�ִ����ݿ����ɾ���������
										deleteEvent(port.getId()+":"+port.getType()+":"+port.getName());
									 if (flag) {
								
								double value_int = Double.valueOf(value);

								String level = "";
								int alarmTimes = 0;
								
								// �Ƿ񳬹���ֵ
								boolean result = true; 
								if(port.getCompare()==0){
									//����Ƚ�
									if(value_int <= levelValue3){
										level = "3";
										if (utilhdx.getEntity().equalsIgnoreCase("InBandwidthUtilHdx")){
										alarmTimes = port.getLevelintimes3();
										levelValue =  port.getLevelinvalue3();
										}else {
											alarmTimes = port.getLevelouttimes3();
											levelValue =  port.getLeveloutvalue3();
											level = "6";
										}
									}else if(value_int <= levelValue2){
										level = "2";
										if (utilhdx.getEntity().equalsIgnoreCase("InBandwidthUtilHdx")){
										alarmTimes = port.getLevelintimes2();
										levelValue = port.getLevelinvalue2();
										}else {
											alarmTimes = port.getLevelouttimes2();
											levelValue =  port.getLeveloutvalue2();
											level = "5";
										}
									}else if(value_int <= levelValue1){
										level = "1";
										if (utilhdx.getEntity().equalsIgnoreCase("InBandwidthUtilHdx")){
										alarmTimes = port.getLevelintimes1();
										levelValue =port.getLevelinvalue1();
										}else {
											alarmTimes = port.getLevelouttimes1();
											levelValue =  port.getLeveloutvalue1();
											level = "4";
										}
									}else{
										result = false;
									}
								}else{
									//����Ƚ�
									if(value_int >= levelValue3){
										level = "3";
										if (utilhdx.getEntity().equalsIgnoreCase("InBandwidthUtilHdx")){
										alarmTimes = port.getLevelintimes3();
										levelValue =  port.getLevelinvalue3();
										}else {
											alarmTimes = port.getLevelouttimes3();
											levelValue =  port.getLeveloutvalue3();	
											level = "6";
										}
									}else if(value_int >= levelValue2){
										level = "2";
										if (utilhdx.getEntity().equalsIgnoreCase("InBandwidthUtilHdx")){
										alarmTimes = port.getLevelintimes2();
										levelValue = port.getLevelinvalue2();
										}else {
											alarmTimes = port.getLevelouttimes2();
											levelValue =  port.getLeveloutvalue2();
											level = "5";
										}
									}else if(value_int >= levelValue1){
										level = "1";
										if (utilhdx.getEntity().equalsIgnoreCase("InBandwidthUtilHdx")){
										alarmTimes = port.getLevelintimes1();
										levelValue =port.getLevelinvalue1();
										}else {
											alarmTimes = port.getLevelouttimes1();
											levelValue =  port.getLeveloutvalue1();
											level = "4";
										}
									}else{
										result = false;
									}
							}
							
								String id=node.getId()+":"+port.getType()+":"+port.getName();
								// �澯��Դ�е� �¼�����
								String num = (String)AlarmResourceCenter.getInstance().getAttribute(id);
								
								if(num == null || "".equals(num)){
									num = "0";
								}
								
								if(!result){
									// δ�����澯��ֵ ��ɾ���澯���͵�ʱ��ļ�¼
									AgentalarmControlutil.delete_nms_send_alarm_time(port.getId()+":"+port.getType()+":"+port.getName());
									
//									SendAlarmTimeDao sendAlarmTimeDao = new SendAlarmTimeDao();
//									try {
//										sendAlarmTimeDao.delete(port.getId()+":"+port.getType()+":"+port.getName());
//									} catch (Exception e) {
//										// TODO Auto-generated catch block
//										e.printStackTrace();
//									} finally {
//										sendAlarmTimeDao.close();
//									}
									// �����ʱδ�����澯 �� ��������Ϊ 0 ��
									num = "0";
									AlarmResourceCenter.getInstance().setAttribute(id, num);
									continue;
								}
								
								int num_int = 0;
								int alarmTimes_int = 0;
								try {
									num_int = Integer.valueOf(num);					//��ǰ�澯����
									alarmTimes_int = Integer.valueOf(alarmTimes);	//����ĸ澯����
								} catch (RuntimeException e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								}
								
								if(num_int+1 >= alarmTimes_int){
									 if(vo instanceof Host){
										// �¼����������澯���������� ���ɸ澯
										//SysLogger.info(alarmIndicatorsNode.getNodeid()+"=="+alarmIndicatorsNode.getName()+"=="+value_int+"=="+Limenvalue0+"=="+Limenvalue1+"=="+Limenvalue2);
										Host host = (Host) PollingEngine.getInstance().getNodeByID(node.getId());
										host.setAlarm(true);
										host.getAlarmMessage().add(port.getName()+":"+inOrOut+port.getAlarm_info() + " ��ǰֵΪ��" + value+unit +" ��ֵΪ:"+levelValue+unit);
										//������֮ǰ�ĸ澯����,������󼶱�
										int realLevel=Integer.valueOf(level);
										if (realLevel>3) {
											realLevel=realLevel-3;
										}
										if(realLevel> host.getStatus())host.setStatus(realLevel);
										if(realLevel> host.getAlarmlevel())host.setAlarmlevel(realLevel);
										//NodeUtil nodeUtil = new NodeUtil();
										nodeDTO = nodeUtil.conversionToNodeDTO(host);
										sendAlarmPort(nodeDTO, port, value, Integer.valueOf(level),inOrOut);
										nodeDTO = null;
									}
									
								}else{
									num_int = num_int + 1;
									AlarmResourceCenter.getInstance().setAttribute(id, String.valueOf(num_int));
								}
									 }
							  }
								}
						}
						nodeUtil = null;
						nodeDTO = null;
        }
        /**
         * ����Ƿ�Ϊ�澯
         * <p>�÷����Ժ������Ϊһ��ͨ�õķ��� ������Ƿ�Ϊһ���澯������ ����Ϊ�ɼ���ͳһ���
         *    �÷���������</p>
         * @see checkEvent(Host node,AlarmIndicatorsNode nm,String value, String sIndex)</a> 
         * <p>
         * @param node		�豸
         * @param nm		ָ��
         * @param pingvalue ֵ
         */
        public void checkEvent(BaseVo baseVo,AlarmIndicatorsNode nm,String value){
        	NodeDTO node = null;
        	if( !(baseVo instanceof NodeDTO) ){
        		NodeUtil nodeUtil = new NodeUtil();
        		node = nodeUtil.conversionToNodeDTO(baseVo);
        	}else {
        		node = (NodeDTO)baseVo;
        	}
//        	SysLogger.info("=====���Ping�ĸ澯===" + "===node===" + node.getId() + "===nm===" + nm.getLimenvalue0() + "==value====" + value);
        	checkEvent(node, nm, value, "");
        	return;
        }
        
        /**
         * ����Ƿ�Ϊ�澯
         * <p>�÷����Ժ������Ϊһ��ͨ�õķ��� ������Ƿ�Ϊһ���澯������ ����Ϊ�ɼ���ͳһ���
         *    �÷���������</p>
         * @see checkEvent(Host node,AlarmIndicatorsNode nm,String value, String sIndex)</a> 
         * <p>
         * @param node		�豸
         * @param nm		ָ��
         * @param pingvalue ֵ
         */
        public void checkEvent(Node node,AlarmIndicatorsNode nm,String value){
        	NodeDTO nodeDTO = null;
    		NodeUtil nodeUtil = new NodeUtil();
    		nodeDTO = nodeUtil.conversionToNodeDTO(node);
        	//SysLogger.info("=====���Ping�ĸ澯===" + "===node===" + node.getId() + "===nm===" + nm.getName() + "==value====" + value);
        	//SysLogger.info("=====���Ping�ĸ澯===" + "===node===" + node.getId() + "===nm===" + nm.getLimenvalue0() + "==value====" + value);
        	checkEvent(nodeDTO, nm, value, "");
        	return;
        }
        
        /**
         * ����Ƿ�Ϊ�澯
         * <p>�÷����Ժ������Ϊһ��ͨ�õķ��� ������Ƿ�Ϊһ���澯������ ����Ϊ�ɼ���ͳһ���
         *    �÷���������</p>
         * @see checkEvent(Host node,AlarmIndicatorsNode nm,String value, String sIndex)</a> 
         * <p>
         * @param node		�豸
         * @param nm		ָ��
         * @param pingvalue ֵ
         */
        public void checkMiddlewareEvent(BaseVo node,AlarmIndicatorsNode nm,String value){
        	NodeDTO nodeDTO = null;
    		NodeUtil nodeUtil = new NodeUtil();
    		nodeDTO = nodeUtil.conversionToNodeDTO(node);
        	//SysLogger.info("=====���Ping�ĸ澯===" + "===node===" + node.getId() + "===nm===" + nm.getName() + "==value====" + value);
        	//SysLogger.info("=====���Ping�ĸ澯===" + "===node===" + node.getId() + "===nm===" + nm.getLimenvalue0() + "==value====" + value);
    		checkMiddlewareEvent(nodeDTO, nm, value, "");
        	return;
        }
        
        /**
         * ����Ƿ�Ϊ�澯
         * <p>�÷����Ժ������Ϊһ��ͨ�õķ��� ������Ƿ�Ϊһ���澯������ ����Ϊ�ɼ���ͳһ���<p>
         * @param node		�豸
         * @param nm		ָ��
         * @param value 	ֵ
         * @param sIndex 	���ֵʱ����Ϊ��־����
         */
        public void checkMiddlewareEvent(BaseVo baseVo,AlarmIndicatorsNode nm,String value, String sIndex){
        	NodeDTO node = null;
        	if( !(baseVo instanceof NodeDTO) ){
        		NodeUtil nodeUtil = new NodeUtil();
        		node = nodeUtil.conversionToNodeDTO(baseVo);
        	}else {
        		node = (NodeDTO)baseVo;
        	}
            int alarmLevel = 0;		// �澯�ȼ�
            // �� name ��Ϊ �ø澯��Ψһ��ʶ��
            String name = node.getId()+":"+nm.getType()+":"+nm.getSubtype()+":"+nm.getName();
            if(sIndex!=null && sIndex.trim().length() > 0){
            	name = name + ":" +sIndex;
            }
            //���ڴ�����صĸ澯���,����ʵ�ִ����ݿ����ɾ���������
			CheckEvent lastCheckEvent = deleteEvent(name);
			if(nm.getEnabled().equalsIgnoreCase("0")){
				//�澯ָ��δ��� �����κ����� ����
				return;
			}
			if(!AlarmConstant.DATATYPE_NUMBER.equals(nm.getDatatype())){
				// ���������͵ķ���
				return;
			}
			if(value == null || value.trim().length()==0){
 				//δ�ɼ�ֵ �����κ��� ֱ�ӷ���
 				return;
			}
			// �ж��Ƿ��͸澯 ������� >0 ����
			try {
				alarmLevel = checkAlarm(node, nm, Double.valueOf(value), name);
//				SysLogger.info("========���͸澯����=====================" + alarmLevel);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				SysLogger.error("�ж��Ƿ��͸澯����" , e);
				e.printStackTrace();
			}
 			if(alarmLevel >0){
 				//��Ҫ���͸澯����
 				try{
					sendMiddlewareAlarm(node, nm, value, alarmLevel , sIndex);
 				}catch(Exception e){
 					e.printStackTrace();
 				}
 			}else{
 				// δ�����澯��ֵ ��ɾ���澯���͵�ʱ��ļ�¼
 				AgentalarmControlutil.delete_nms_send_alarm_time(nm,name);
 				
//				SendAlarmTimeDao sendAlarmTimeDao = new SendAlarmTimeDao();
//				try {
//					sendAlarmTimeDao.delete(name);
//				} catch (Exception e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				} finally {
//					sendAlarmTimeDao.close();
//				}
				
 				//�ж�֮ǰ�Ƿ��и澯,�������͸澯�ָ���Ϣ
 				if(lastCheckEvent!=null){
 					// ֮ǰ�и澯 �� ���͸澯�ָ���Ϣ
 					// TODO ������ø澯�ָ���Ϣ�ķ���
 					
 				}
		    }
        }
        
        /**
         * �������Ƿ�Ϊ�澯
         * <p>�÷����Ժ������Ϊһ��ͨ�õķ��� ������Ƿ�Ϊһ���澯������ ����Ϊ�ɼ���ͳһ���<p>
         * @param node		�豸
         * @param nm		ָ��
         * @param value 	ֵ
         * @param sIndex 	���ֵʱ����Ϊ��־����
         * @diskAlarmIndicatorType  �澯���
         */
        public void checkDiskEvent(BaseVo baseVo,AlarmIndicatorsNode nm,String value, String sIndex,String diskAlarmIndicatorType){
        	//sIndex = diskAlarmIndicatorType + ":"+ sIndex;
        	checkEvent(baseVo,nm,value,sIndex);
        }
        
        
        /**
         * ����Ƿ�Ϊ�澯
         * <p>�÷����Ժ������Ϊһ��ͨ�õķ��� ������Ƿ�Ϊһ���澯������ ����Ϊ�ɼ���ͳһ���<p>
         * @param node		�豸
         * @param nm		ָ��
         * @param value 	ֵ
         * @param sIndex 	���ֵʱ����Ϊ��־����
         */
        public void checkEvent(BaseVo baseVo,AlarmIndicatorsNode nm,String value, String sIndex){
        	NodeDTO node = null;
        	if( !(baseVo instanceof NodeDTO) ){
        		NodeUtil nodeUtil = new NodeUtil();
        		node = nodeUtil.conversionToNodeDTO(baseVo);
        	}else {
        		node = (NodeDTO)baseVo;
        	}
            int alarmLevel = 0;		// �澯�ȼ�
            // �� name ��Ϊ �ø澯��Ψһ��ʶ��
            String name = node.getId()+":"+nm.getType()+":"+nm.getName();
           // System.out.println("===*******===999888=="+name);
            
            if(sIndex!=null && sIndex.trim().length() > 0){
            	name = name + ":" +sIndex;
            }
           // System.out.println("===*******===999888=="+name);
            
            
            //���ڴ�����صĸ澯���,����ʵ�ִ����ݿ����ɾ���������
			CheckEvent lastCheckEvent = deleteEvent(name);
			if(nm.getEnabled().equalsIgnoreCase("0")){
				//�澯ָ��δ��� �����κ����� ����
				return;
			}
			if(!AlarmConstant.DATATYPE_NUMBER.equals(nm.getDatatype())){
				// ���������͵ķ���
				return;
			}
			if(value == null || value.trim().length()==0){
 				//δ�ɼ�ֵ �����κ��� ֱ�ӷ���
 				return;
			}
			// �ж��Ƿ��͸澯 ������� >0 ����
			try {
				alarmLevel = checkAlarm(node, nm, Double.valueOf(value), name);
				//SysLogger.info("========���͸澯����===2233=======sIndex="+sIndex+"===========" + alarmLevel);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				SysLogger.error("�ж��Ƿ��͸澯����" , e);
				e.printStackTrace();
			}
			
			
			
			
 			if(alarmLevel >0){
 				//��Ҫ���͸澯����
 				//if(baseVo instanceof HostNode){
					// �¼����������澯���������� ���ɸ澯
					Host host = (Host) PollingEngine.getInstance().getNodeByID(node.getId());
					host.setAlarm(true);
					String limvalue = "";
					String alarmTimes="";
					if(alarmLevel == 1)
					{
						limvalue = nm.getLimenvalue0();
						alarmTimes=nm.getTime0();
					}
					else if (alarmLevel == 2)
					{
						limvalue = nm.getLimenvalue1();
						alarmTimes=nm.getTime1();
					}
					else if (alarmLevel == 3)
					{
						limvalue = nm.getLimenvalue2();
						alarmTimes=nm.getTime2();
					}
					
 				//}
					
					
					// �澯��Դ�е� �¼�����
					//String num = (String)AlarmResourceCenter.getInstance().getAttribute(String.valueOf(node.getId()));
					
					//if(num == null || "".equals(num)){
					//	num = "0";
					//}
					
					
//					int num_int = 0;
//					int alarmTimes_int = 0;
//					try {
//						num_int = Integer.valueOf(num);					//��ǰ�澯����
//						alarmTimes_int = Integer.valueOf(alarmTimes);	//����ĸ澯����
//					} catch (RuntimeException e1) {
//						// TODO Auto-generated catch block
//						e1.printStackTrace();
//					}
					
					//AlarmResourceCenter.getInstance().setAttribute(name, String.valueOf(num_int+1));
					
				//�����澯����
				//if(num_int+1 >= alarmTimes_int){
 				try{
 					//System.out.println("=���͸澯--index==***&&&&&&*******=====888888===");
 					
 				    nm.setMoid(name);
 					
					sendAlarm(node, nm, value, alarmLevel , sIndex);
					
					host.getAlarmMessage().add(nm.getAlarm_info() + " ��ǰֵΪ��" + value +  nm.getThreshlod_unit()+" ��ֵΪ:"+limvalue+nm.getThreshlod_unit());
					//������֮ǰ�ĸ澯����,������󼶱�
					if(Integer.valueOf(alarmLevel)> host.getStatus())host.setStatus(Integer.valueOf(alarmLevel));
					if(Integer.valueOf(alarmLevel)> host.getAlarmlevel())host.setAlarmlevel(Integer.valueOf(alarmLevel));
 				}catch(Exception e){
 					e.printStackTrace();
 				}
//				}
//				else
//				{
//					num_int = num_int + 1;
//					AlarmResourceCenter.getInstance().setAttribute(name, String.valueOf(num_int));
//					
//				}
				
 			}else{
 				// δ�����澯��ֵ ��ɾ���澯���͵�ʱ��ļ�¼
 				
 				AgentalarmControlutil.delete_nms_send_alarm_time(nm,name);
//				SendAlarmTimeDao sendAlarmTimeDao = new SendAlarmTimeDao();
//				try {
//					sendAlarmTimeDao.delete(name);
//				} catch (Exception e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				} finally {
//					sendAlarmTimeDao.close();
//				}
//				
 				//�ж�֮ǰ�Ƿ��и澯,�������͸澯�ָ���Ϣ
// 				if(lastCheckEvent!=null){
// 					// ֮ǰ�и澯 �� ���͸澯�ָ���Ϣ
// 					// TODO ������ø澯�ָ���Ϣ�ķ���
// 					
// 				}
		    }
        }
        /**
         * ����Ƿ�Ϊ�澯
         * <p>�÷����Ժ������Ϊһ��ͨ�õķ��� ������Ƿ�Ϊһ���澯������ ����Ϊ�ɼ���ͳһ���
         *    �÷���������</p>
         * @see checkEvent(Host node,AlarmIndicatorsNode nm,String value, String sIndex)</a> 
         * <p>
         * @param node		�豸
         * @param nm		ָ��
         * @param pingvalue ֵ
         */
        public void checkEvent(Node node,AlarmIndicatorsNode nm,String value, String sIndex){
        	NodeDTO nodeDTO = null;
    		NodeUtil nodeUtil = new NodeUtil();
    		nodeDTO = nodeUtil.conversionToNodeDTO(node);
        	//SysLogger.info("=====���Ping�ĸ澯===" + "===node===" + node.getId() + "===nm===" + nm.getName() + "==value====" + value);
        	//SysLogger.info("=====���Ping�ĸ澯===" + "===node===" + node.getId() + "===nm===" + nm.getLimenvalue0() + "==value====" + value);
        	checkEvent(nodeDTO, nm, value, sIndex);
        	return;
        }
	    /** 	
	     * �����̸澯��Ϣ
	     * @param node
	     * @param diskVector
	     * @param nm
	     */
        public void checkDisk(Host node,Vector diskVector,AlarmIndicatorsNode nm){
			// �� diskVector ������Ϣ
			//SysLogger.info("### ��ʼ���м������Ƿ�澯... ###");
			SysLogger.info(node.getIpAddress() + "============��ʼ���м������Ƿ�澯================="+diskVector.size() + "===="+nm.getName());
			if("0".equals(nm.getEnabled())){
				//�澯ָ��δ��� �����κ����� ����
				return;
			}
			if (diskVector == null || diskVector.size() == 0){
				//δ�ɼ������� �����κ����� ����
				return;
			}
			for(int i = 0 ; i < diskVector.size(); i++){
				Diskcollectdata diskcollectdata = null;
				diskcollectdata = (Diskcollectdata)diskVector.get(i);
				if(diskcollectdata.getEntity().equalsIgnoreCase("Utilization") && nm.getName().equalsIgnoreCase("diskperc")){
					//������
					String diskname = diskcollectdata.getSubentity();
					
					
					
					if(node.getSysOid().startsWith("1.3.6.1.4.1.311.1.1.3")){
						diskname = diskcollectdata.getSubentity().substring(0, 3);
					}
					
					//System.out.println("===========������====="+diskname);
					Hashtable alldiskalarmdata = null;
					try{
						alldiskalarmdata = ShareData.getAlldiskalarmdata();
					}catch(Exception e){
						e.printStackTrace();
					}
					if (alldiskalarmdata == null )alldiskalarmdata = new Hashtable();
					Diskconfig diskconfig = null;
    				if(node.getOstype() ==4 || node.getSysOid().startsWith("1.3.6.1.4.1.311.1.1.3")){
    					diskconfig = (Diskconfig)alldiskalarmdata.get(node.getIpAddress()+":"+diskcollectdata.getSubentity().substring(0, 3)+":"+"��������ֵ");
    				}else
    				{
    					diskconfig = (Diskconfig)alldiskalarmdata.get(node.getIpAddress()+":"+diskcollectdata.getSubentity()+":"+"��������ֵ");
    				}
    				if(diskconfig != null){
    					int limevalue0 = diskconfig.getLimenvalue();
    					int limevalue1 = diskconfig.getLimenvalue1();
    					int limevalue2 = diskconfig.getLimenvalue2();
        				nm.setLimenvalue0(limevalue0+"");
        				nm.setLimenvalue1(limevalue1+"");
        				nm.setLimenvalue2(limevalue2+"");
        				
        				
        				NodeUtil nodeUtil = new NodeUtil();
        				NodeDTO nodeDTO = nodeUtil.conversionToNodeDTO(node);
//    					checkEvent(nodeDTO, nm, diskcollectdata.getThevalue(), diskname);
        				checkDiskEvent(nodeDTO, nm, diskcollectdata.getThevalue(), diskname ,"diskperc");
    				}
					
				}else if(diskcollectdata.getEntity().equalsIgnoreCase("UtilizationInc") && nm.getName().equals("diskinc") ){
					//����������
					
					
					String diskname = diskcollectdata.getSubentity();
					if(node.getSysOid().startsWith("1.3.6.1.4.1.311.1.1.3")){
						diskname = diskcollectdata.getSubentity().substring(0, 3);
					}
					//System.out.println("=========================����������========="+diskname);
					
					Hashtable alldiskalarmdata = null;
					try{
						alldiskalarmdata = ShareData.getAlldiskalarmdata();
					}catch(Exception e){
						e.printStackTrace();
					}
					if (alldiskalarmdata == null )alldiskalarmdata = new Hashtable();
					Diskconfig diskconfig = null;
    				if(node.getOstype() ==4 || node.getSysOid().startsWith("1.3.6.1.4.1.311.1.1.3")){
    					diskconfig = (Diskconfig)alldiskalarmdata.get(node.getIpAddress()+":"+diskcollectdata.getSubentity().substring(0, 3)+":"+"��������ֵ");
    				}else
    					diskconfig = (Diskconfig)alldiskalarmdata.get(node.getIpAddress()+":"+diskcollectdata.getSubentity()+":"+"��������ֵ");
    				if(diskconfig != null){
    					int limevalue0 = diskconfig.getLimenvalue();
    					int limevalue1 = diskconfig.getLimenvalue1();
    					int limevalue2 = diskconfig.getLimenvalue2();
        				nm.setLimenvalue0(limevalue0+"");
        				nm.setLimenvalue1(limevalue1+"");
        				nm.setLimenvalue2(limevalue2+"");
        				NodeUtil nodeUtil = new NodeUtil();
        				NodeDTO nodeDTO = nodeUtil.conversionToNodeDTO(node);
//    					checkEvent(nodeDTO, nm, diskcollectdata.getThevalue(), diskname);
        				checkDiskEvent(nodeDTO, nm, diskcollectdata.getThevalue(), diskname ,"diskinc");
    				}
    				
    				//System.out.println("=========================����������=====================================");
					
				}
			}
		}
	     	
		/**
		 * 
		 * �������������澯
		 * nielin add
		 * @date 2010-08-18
		 * @param ip
		 * @param proVector
		 */
		public List createProcessGroupEventList(String ip , Vector proVector, AlarmIndicatorsNode alarmIndicatorsNode){
//			SysLogger.info("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
//			SysLogger.info("$$$$$ ��ʼ�жϽ��̸澯 $$$$$$$$$$$$$$$$$$$$$$$$$$$$");
//			SysLogger.info("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
			if(alarmIndicatorsNode==null){
				SysLogger.info("=========�޽��̸澯ָ�겻�澯====================");
				return null;
			}
			List retList = new ArrayList();
			if(proVector == null || proVector.size()==0)return retList;
			try {
				Node hostNode = PollingEngine.getInstance().getNodeByIP(ip);
				
				ProcessGroupConfigurationUtil processGroupConfigurationUtil = new ProcessGroupConfigurationUtil();
				List list = processGroupConfigurationUtil.getProcessGroupByIpAndMonFlag(ip, "1");
				
				if(list == null || list.size() ==0){
					return null;
				}
				for(int i = 0; i < list.size() ; i ++){
					ProcessGroup processGroup = (ProcessGroup)list.get(i);
					List processGroupConfigurationList = processGroupConfigurationUtil.getProcessGroupConfigurationByGroupId(String .valueOf(processGroup.getId()));
					
					if(processGroupConfigurationList == null || processGroupConfigurationList.size() ==0){
						continue;
					}
					List wrongList = new ArrayList();
					for(int j = 0 ; j < processGroupConfigurationList.size() ; j++){
						int num = 0;
						ProcessGroupConfiguration processGroupConfiguration = (ProcessGroupConfiguration)processGroupConfigurationList.get(j);
						for(int k = 0  ; k < proVector.size() ; k ++){
							Processcollectdata processdata = (Processcollectdata) proVector.elementAt(k);
							if("Name".equals(processdata.getEntity())){
								if(processGroupConfiguration.getName().trim().equals(processdata.getThevalue().trim())){
									num++;
								}
							}
						}
						int times = Integer.parseInt(processGroupConfiguration.getTimes());
						String status = processGroupConfiguration.getStatus();
						if("1".equals(status)){
							if(num > times){
								// ����ĸ���
								num = num - times;
								List wrongProlist = new ArrayList();
								wrongProlist.add(processGroupConfiguration.getName());
								wrongProlist.add(num);
								wrongProlist.add(status);
								wrongList.add(wrongProlist);
							}
						}else{
							if(num < times){
								// ��ʧ�ĸ���
								num = times - num;
								List wrongProlist = new ArrayList();
								wrongProlist.add(processGroupConfiguration.getName());
								wrongProlist.add(num);
								wrongProlist.add(status);
								
								wrongList.add(wrongProlist);
								
							}
						}
					}
					
					if(wrongList.size() > 0){
						String message = ip + " ������Ϊ��" + processGroup.getName() + " ���ֽ����쳣!";
						for(int j = 0 ; j < wrongList.size() ; j ++){
							List wrongProList = (List)wrongList.get(j);
							String status = (String)wrongProList.get(2);
							if("1".equals(status)){
								message = message + "���̣�" + wrongProList.get(0) + "��������Ϊ��" + wrongProList.get(1) + ";";
							}else {
								message = message + "���̣�" + wrongProList.get(0) + "��ʧ����Ϊ��" + wrongProList.get(1) + ";";
							}
						}
						EventList eventList = new EventList();
						eventList.setEventtype("poll");
						eventList.setEventlocation(hostNode.getAlias() + "(" + ip + ")");
						eventList.setContent(message);
						eventList.setLevel1(Integer.valueOf(processGroup.getAlarm_level()));
						eventList.setManagesign(0);
						eventList.setRecordtime(Calendar.getInstance());
						eventList.setReportman("ϵͳ��ѯ");
						eventList.setNodeid(hostNode.getId());
						eventList.setBusinessid(hostNode.getBid());
						eventList.setSubtype("host");
						eventList.setSubentity("proc");
						
						retList.add(eventList);
						
						try{
							/*
							 * nielin modify 2010-10-25 start  �� createSMS ע�� ��Ϊ ʹ�ø澯���˺��͸澯
							 */
							//createSMS(eventList.getSubtype(), eventList.getSubentity(),ip , hostNode.getId() + "", message , eventList.getLevel1() , 1 , processGroup.getName() , eventList.getBusinessid(),hostNode.getAlias() + "(" + ip + ")");
							sendAlarm(eventList, alarmIndicatorsNode);
							/*nielin modify 2010-10-25 end */
						}catch(Exception e){
							
						}
						
					}
					
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//SysLogger.info("=========���̸澯ָ��====================size:"+retList.size());
			return retList;
		}
	    
	    /**
	     * �������������澯
	     * @param ip
	     * @param hostServiceVector
	     * @return
	     */
		public List createHostServiceGroupEventList(String ip , Vector hostServiceVector, AlarmIndicatorsNode alarmIndicatorsNode){
			if(alarmIndicatorsNode==null){
				SysLogger.info("==========������������澯==========");
				return null;
			}
			List returnList = new ArrayList();
			if(hostServiceVector == null || hostServiceVector.size()==0)return returnList;
			
			try {
				
				Node hostNode = PollingEngine.getInstance().getNodeByIP(ip);
				
				HostServiceGroupConfigurationUtil hostServiceGroupConfigurationUtil = new HostServiceGroupConfigurationUtil();
				List list = hostServiceGroupConfigurationUtil.gethostservicegroupByIpAndMonFlag(ip, "1");
				
				if(list == null || list.size() == 0){
					return returnList;
				}
				
				for(int i = 0 ; i < list.size() ; i++){
					HostServiceGroup hostServiceGroup = (HostServiceGroup)list.get(i);
					List hostServiceList = hostServiceGroupConfigurationUtil.gethostservicegroupConfigurationByGroupId(String.valueOf(hostServiceGroup.getId()));
					
					if(hostServiceList == null || hostServiceList.size() ==0){
						continue;
					}
					
					List wrongList = new ArrayList();
					
					//�и澯�ĺ����������б�
					List blackWrongList = new ArrayList();
					
					
					for(int j = 0 ; j < hostServiceList.size() ; j++){
						HostServiceGroupConfiguration hostServiceGroupConfiguration = (HostServiceGroupConfiguration) hostServiceList.get(j);
						String status = hostServiceGroupConfiguration.getStatus();//1:�����������  0:�������������
						boolean isLived = false;
						
						if(hostServiceVector != null){
							for(int k = 0 ; k < hostServiceVector.size() ; k++){
								Servicecollectdata servicedata = (Servicecollectdata)hostServiceVector.get(k);
								if(hostServiceGroupConfiguration.getName().trim().equals(servicedata.getName())){
									isLived = true;
									break;
								}
							}
						}
						
						if(!isLived && "0".equals(status)){//���Ӱ������ж����� (�����ڸý��� ��澯)
							wrongList.add(hostServiceGroupConfiguration);
						}
						if(isLived && "1".equals(status)){//���Ӻ������ж����� (���ڸý�����澯)
							blackWrongList.add(hostServiceGroupConfiguration);
						}
					}
					StringBuffer message = new StringBuffer();
					message.append(ip);
					message.append(" ����������Ϊ��");
					message.append(hostServiceGroup.getName());
					message.append("������������澯! ");
					if(wrongList.size() > 0){
						for(int j = 0 ; j < wrongList.size() ; j ++){
							HostServiceGroupConfiguration hostServiceGroupConfiguration = (HostServiceGroupConfiguration)wrongList.get(j);
							message.append("��������");
							message.append(hostServiceGroupConfiguration.getName());
							message.append("��ʧ��; "); 
						}
					}
					if(blackWrongList.size() > 0){
						for(int j = 0 ; j < blackWrongList.size() ; j ++){
							HostServiceGroupConfiguration hostServiceGroupConfiguration = (HostServiceGroupConfiguration)blackWrongList.get(j);
							message.append("��������");
							message.append(hostServiceGroupConfiguration.getName());
							message.append("��������; ");
						}
					}
					EventList eventList = new EventList();
					eventList.setEventtype("poll");
					eventList.setEventlocation(hostNode.getAlias() + "(" + ip + ")");
					eventList.setContent(message.toString());
					eventList.setLevel1(Integer.parseInt(hostServiceGroup.getAlarm_level()));
					eventList.setManagesign(0);
					eventList.setRecordtime(Calendar.getInstance());
					eventList.setReportman("ϵͳ��ѯ");
					eventList.setNodeid(hostNode.getId());
					eventList.setBusinessid(hostNode.getBid());
					eventList.setSubtype("host");
					eventList.setSubentity("hostservice");
					returnList.add(eventList);
					try{
						sendAlarm(eventList, alarmIndicatorsNode);
						//createSMS(eventList.getSubtype(), eventList.getSubentity(),ip , hostNode.getId() + "", message , eventList.getLevel1() , 1 , hostServiceGroup.getName() , eventList.getBusinessid(),hostNode.getAlias() + "(" + ip + ")");
					}catch(Exception e){
						
					}
				}
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return returnList;
		}
		/**
		 * ����as400��ϵͳ�澯
		 * @param ip
		 * @param hostServiceVector
		 * @return
		 */
		public List createJobForAS400SubSystemEventList(String ip , List jobForAS400list, List subSystemForAS400list, AlarmIndicatorsNode alarmIndicatorsNode){
			if(alarmIndicatorsNode==null){
//				SysLogger.info("==========��as400SubSystem�澯ָ��==========");
				return null;
			}
			List returnList = new ArrayList();
//			System.out.println("jobForAS400list.size()="+jobForAS400list.size());
//			System.out.println("subSystemForAS400list.size()="+subSystemForAS400list.size());
			if(jobForAS400list == null || jobForAS400list.size()==0 || subSystemForAS400list == null || subSystemForAS400list.size()==0)return returnList;
			String path = "";
			try {
//				System.out.println("as400��ϵͳ��ʼ�澯");
				Node hostNode = PollingEngine.getInstance().getNodeByIP(ip);
				JobForAS400GroupDetailUtil jobForAS400GroupDetailUtil = new JobForAS400GroupDetailUtil();
				List list = jobForAS400GroupDetailUtil.getJobForAS400SubSystemByIpAndMonFlag(ip, "1");
//				System.out.println("as400��ϵͳ��ʼ�澯��list.size()="+list.size());
				if(list == null || list.size() == 0){
					return returnList;
				}
				NodeDTO node = null;
				NodeUtil nodeUtil = new NodeUtil();
        		node = nodeUtil.conversionToNodeDTO(hostNode);

				for(int i = 0 ; i < list.size() ; i++){
					try {
						JobForAS400SubSystem jobForAS400SubSystem = (JobForAS400SubSystem)list.get(i);
						
						for(int j = 0 ; j < subSystemForAS400list.size(); j++){
							SubsystemForAS400 subsystemForAS400 = (SubsystemForAS400)subSystemForAS400list.get(j);
							if(subsystemForAS400.getName().equalsIgnoreCase(jobForAS400SubSystem.getName())){
								path = subsystemForAS400.getPath();
								break;
							}
						}
//						System.out.println("as400��ϵͳ��ʼ�澯��path="+path);
						List wrongList = new ArrayList();
						
                        //���ڴ�����صĸ澯���,����ʵ�ִ����ݿ����ɾ���������
//						CheckEvent lastCheckEvent = deleteEvent(node.getId()+"", node.getType(), node.getSubtype(), alarmIndicatorsNode.getName(), jobForAS400SubSystem.getName());
						//���ڴ�����صĸ澯���,����ʵ�ִ����ݿ����ɾ���������
//						deleteValue(node, alarmIndicatorsNode, "", jobForAS400SubSystem.getName());
							
						boolean isLived = false;
						List jobForAS400List2 = new ArrayList();
						if(jobForAS400list != null){
							for(int k = 0 ; k < jobForAS400list.size() ; k++){
								JobForAS400 jobForAS400 = (JobForAS400)jobForAS400list.get(k);
								if(path.equals(jobForAS400.getSubsystem())){
									jobForAS400List2.add(jobForAS400);
									isLived = true;
								}
							}
						}
						
						String eventMessage = "";
						
						Vector perVector = new Vector();

						if(!"-1".equals(jobForAS400SubSystem.getActive_status_type())){
							// ��� ��ϵͳ��ҵ�Ļ�ļ��״̬���ǲ��� ������ж�
							try {
								int num = Integer.valueOf(jobForAS400SubSystem.getNum());
								if(num > jobForAS400List2.size()){
									eventMessage = "��ϵͳ��" + jobForAS400SubSystem.getName() + " �����쳣,��ҵ�������ڼ����Ŀ,��ʧ��" + (num - jobForAS400List2.size()) + "��;";
								} else if(num < jobForAS400List2.size()){
									eventMessage = "��ϵͳ��" + jobForAS400SubSystem.getName() + " �����쳣,��ҵ�������ڼ����Ŀ,���ӣ�" + (jobForAS400List2.size() - num) + "��;";
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
							
							String activeStatus = jobForAS400SubSystem.getActive_status();
							
							if(activeStatus!=null){
								for(int m = 0 ; m < jobForAS400List2.size() ; m++){
									JobForAS400 jobForAS400 = (JobForAS400)jobForAS400List2.get(m);
									// �ж�ÿһ�����ֵ���ҵ
									if("1".equals(jobForAS400SubSystem.getActive_status_type())!= (activeStatus.indexOf(jobForAS400.getActiveStatus()) != -1) ){
										// ��� ��ҵ�״̬����Ϊ������� �� �״̬�����ڵ�ǰ���״̬�� ��������Գ����쳣
										// ��� ��ҵ�״̬����Ϊ��������� �� �״̬���ܳ����ڵ�ǰ���״̬�� �� ������� �����쳣
										eventMessage = eventMessage + "��ϵͳ��" + jobForAS400SubSystem.getName() + " ��ҵ��������쳣״̬Ϊ; ��״̬Ϊ��" + jobForAS400.getActiveStatus() + ";";
									}
								}
							}
							
							if(eventMessage.trim().length() > 1){
								perVector.add(eventMessage);
							}
						}
					
						if(perVector.size()>0){
							wrongList.add(perVector);
						}
//						System.out.println("as400��ϵͳ��ʼ�澯��wrongList.size()="+wrongList.size());
						if(wrongList.size() > 0){
							String message = ip + " ����ϵͳ��" + jobForAS400SubSystem.getName() + " �����쳣!";
							for(int j = 0 ; j < wrongList.size() ; j ++){
								Vector perVector1 = (Vector)wrongList.get(j);
								message = message + perVector1.get(0);
							
							}
							EventList eventList = new EventList();
							eventList.setEventtype("poll");
							eventList.setEventlocation(hostNode.getAlias() + "(" + ip + ")" + " ��ϵͳΪ��" + jobForAS400SubSystem.getName());
							eventList.setContent(message);
							eventList.setLevel1(Integer.parseInt(jobForAS400SubSystem.getAlarm_level()));
							eventList.setManagesign(0);
							eventList.setRecordtime(Calendar.getInstance());
							eventList.setReportman("ϵͳ��ѯ");
							eventList.setNodeid(hostNode.getId());
							eventList.setBusinessid(hostNode.getBid());
							eventList.setSubtype("host");
							eventList.setSubentity("subsystem");
							sendAlarm(eventList, alarmIndicatorsNode);
						} else {
//							�ж�֮ǰ�Ƿ��и澯,�������͸澯�ָ���Ϣ
//							System.out.println("as400��ϵͳ��ʼ�澯��lastCheckEvent="+lastCheckEvent);
//			 				if(lastCheckEvent!=null){
//			 					// ֮ǰ�и澯 �� ���͸澯�ָ���Ϣ
//			 					// TODO ������ø澯�ָ���Ϣ�ķ���
//			 					try{
////			 						System.out.println("as400��ϵͳ��ʼ�澯��jobForAS400SubSystem.getName()="+jobForAS400SubSystem.getName());
//			 						sendAlert(node, alarmIndicatorsNode, "", 0 , jobForAS400SubSystem.getName());
//			 	 				}catch(Exception e){
//			 	 					e.printStackTrace();
//			 	 				}
//			 				}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
	    	return returnList;
	    }		
		
		/**
		 * ����as400������澯
		 * @param ip
		 * @param hostServiceVector
		 * @return
		 */
		public List createJobForAS400GroupEventList(String ip , List jobForAS400list, AlarmIndicatorsNode alarmIndicatorsNode){
			if(alarmIndicatorsNode==null){
				//SysLogger.info("==========��as400JOB�澯ָ��==========");
				return null;
			}
			List returnList = new ArrayList();
			if(jobForAS400list == null || jobForAS400list.size()==0)return returnList;
			
			try {
				
				Node hostNode = PollingEngine.getInstance().getNodeByIP(ip);
				JobForAS400GroupDetailUtil jobForAS400GroupDetailUtil = new JobForAS400GroupDetailUtil();
				List list = jobForAS400GroupDetailUtil.getJobForAS400GroupByIpAndMonFlag(ip, "1");
				
				if(list == null || list.size() == 0){
					return returnList;
				}
				
				for(int i = 0 ; i < list.size() ; i++){
					try {
						JobForAS400Group jobForAS400Group = (JobForAS400Group)list.get(i);
						List jobForAS400DetailList = jobForAS400GroupDetailUtil.getJobForAS400GroupDetailByGroupId(String.valueOf(jobForAS400Group.getId()));
						
						if(jobForAS400DetailList == null || jobForAS400DetailList.size() ==0){
							continue;
						}
						
						List wrongList = new ArrayList();
						
						
						for(int j = 0 ; j < jobForAS400DetailList.size() ; j++){
							JobForAS400GroupDetail jobForAS400GroupDetail = (JobForAS400GroupDetail) jobForAS400DetailList.get(j);
							
							boolean isLived = false;
							List jobForAS400List2 = new ArrayList();
							if(jobForAS400list != null){
								for(int k = 0 ; k < jobForAS400list.size() ; k++){
									JobForAS400 jobForAS400 = (JobForAS400)jobForAS400list.get(k);
									if(jobForAS400GroupDetail.getName().trim().equals(jobForAS400.getName())){
										jobForAS400List2.add(jobForAS400);
										isLived = true;
									}
								}
							}
							
							String eventMessage = "";
							
							Vector perVector = new Vector();
							if(jobForAS400GroupDetail.getStatus().equals("0") && isLived ){
								// ��� ��ҵ���� ���� ��ҵ�ļ��״̬Ϊ��������� ��澯
								perVector.add(jobForAS400GroupDetail);
								perVector.add("��ҵ��" + jobForAS400GroupDetail.getName() + " ���ֻ,�Ҹ���Ϊ��" + jobForAS400List2.size() + ";");
							} else if(jobForAS400GroupDetail.getStatus().equals("1") && !isLived ){
								// ��� ��ҵδ���� ���� ��ҵ�ļ��״̬Ϊ������� ��澯
								perVector.add(jobForAS400GroupDetail);
								perVector.add("��ҵ��" + jobForAS400GroupDetail.getName() + " δ�;");
							} else if(!jobForAS400GroupDetail.getStatus().equals("0") && isLived){
								// ��� ��ҵ���� ���� ��ҵ�ļ��״̬Ϊ������� ���һ���ж�
								if(!"-1".equals(jobForAS400GroupDetail.getActiveStatusType())){
									// ��� ��ҵ�Ļ�ļ��״̬���ǲ��� ������ж�
									
									try {
										int num = Integer.valueOf(jobForAS400GroupDetail.getNum());
										if(num > jobForAS400List2.size()){
											eventMessage = "��ҵ��" + jobForAS400GroupDetail.getName() + " �����쳣,�������ڼ����Ŀ,��ʧ��" + (num - jobForAS400List2.size()) + "��";
										}
									} catch (Exception e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									
									String activeStatus = jobForAS400GroupDetail.getActiveStatus();
									if(activeStatus!=null){
										for(int m = 0 ; m < jobForAS400List2.size() ; m++){
											JobForAS400 jobForAS400 = (JobForAS400)jobForAS400List2.get(m);
											// �ж�ÿһ�����ֵ���ҵ
											if("1".equals(jobForAS400GroupDetail.getActiveStatusType()) 
													!= (activeStatus.indexOf(jobForAS400.getActiveStatus()) != -1) ){
												// ��� ��ҵ�״̬����Ϊ������� �� �״̬�����ڵ�ǰ���״̬�� ��������Գ����쳣
												// ��� ��ҵ�״̬����Ϊ��������� �� �״̬���ܳ����ڵ�ǰ���״̬�� �� ������� �����쳣
												eventMessage = eventMessage + "��ҵ��" + jobForAS400GroupDetail.getName() + " �����쳣״̬Ϊ; ��״̬Ϊ��" + jobForAS400.getActiveStatus() + ";";
											}
										}
									}
									
									if(eventMessage.trim().length() > 1){
										perVector.add(jobForAS400GroupDetail);
										perVector.add(eventMessage);
									}
								}
							}
							if(perVector.size()>1){
								wrongList.add(perVector);
							}
						}
						if(wrongList.size() > 0){
							String message = ip + " ����ҵ�飺" + jobForAS400Group.getName() + " �����쳣!";
							for(int j = 0 ; j < wrongList.size() ; j ++){
								Vector perVector = (Vector)wrongList.get(j);
								JobForAS400GroupDetail jobForAS400GroupDetail = (JobForAS400GroupDetail)perVector.get(0);
								message = message + perVector.get(1);
							
							}
							EventList eventList = new EventList();
							eventList.setEventtype("poll");
							eventList.setEventlocation(hostNode.getAlias() + "(" + ip + ")" + " ��ҵ��Ϊ��" + jobForAS400Group.getName());
							eventList.setContent(message);
							eventList.setLevel1(Integer.parseInt(jobForAS400Group.getAlarm_level()));
							eventList.setManagesign(0);
							eventList.setRecordtime(Calendar.getInstance());
							eventList.setReportman("ϵͳ��ѯ");
							eventList.setNodeid(hostNode.getId());
							eventList.setBusinessid(hostNode.getBid());
							eventList.setSubtype("host");
							eventList.setSubentity("jobForAS400Gourp");
							sendAlarm(eventList, alarmIndicatorsNode);
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    	return returnList;
	    }
		
		/**
		 * ȷ���Ƿ�Ϊ�澯 
		 * <p>�������¼��Ĵ������ڹ涨�Ĵ��� ����Ϊ�澯�������ظ澯�ĵȼ�
		 * 	  ������澯�򷵻�<p>
		 * @param node		�豸
		 * @param nm		ָ��
		 * @param value		ֵ
		 * @param name		�澯Ψһ��־	
		 * @return
		 */
		private int checkAlarm(NodeDTO node, AlarmIndicatorsNode nm, double value, String name){
			//SysLogger.info("===========��ʼȷ���Ƿ�Ϊ�澯=============");
			int alarmLevel = 0;
       	 	int eventLevel = 0;			// �¼��ȼ�
       	 	int eventTimes = 0;			// �¼�����
    		double limenvalue0 = Double.parseDouble(nm.getLimenvalue0());//һ����ֵ
			double limenvalue1 = Double.parseDouble(nm.getLimenvalue1());//������ֵ
			double limenvalue2 = Double.parseDouble(nm.getLimenvalue2());//������ֵ
			
			// ����¼��ȼ�
			eventLevel = checkEventLevel(value, limenvalue0, limenvalue1, limenvalue2, nm.getCompare());
			//SysLogger.info("========����¼��ȼ�===========name="+name+"====" + eventLevel);
			// ����¼����� 
			// ���������¼��ȼ�Ϊ 0 ��˵���¼��ָ� �������� �¼�����
			// ������ش������� 0 ��˵�����ڹ涨���¼����� ���¼�����Ϊ�澯 ������ش��������� 0 ��ֻ�ǽ��¼����� + 1;;
			eventTimes = checkEventTimes(nm, eventLevel, name);
			//SysLogger.info("======== ����¼����� ===========" + eventTimes);
			if(eventLevel  >0)
			{
				//System.out.println("===========***777888***====�Ѹ澯��Ϣ���뵽�ڴ�======================"+nm.getName());
				if(nm.getName().equals("diskperc") ||nm.getName().equals("proce") || nm.getName().equals("diskinc") || nm.getName().equals("diskbusy"))
				{
					
		         // System.out.println("========***666==diskperc======proce=====diskinc============"+name);
				//ShareData.getAgentalarmlevellist().put(name, eventLevel+"");
					AgentalarmControlutil.PutAlarmlevel(node.getId()+"", name, nm.getType(), "",eventLevel+"",false);
				}else
				{
					
					//System.out.println("=====not=====diskperc======proce=====diskinc======================");
					//System.out.println("=="+node.getId()+":"+nm.getType()+":"+nm.getName()+"=="+node.getIpaddress());
					//ShareData.getAgentalarmlevellist().put(node.getId()+":"+nm.getType()+":"+nm.getName(), eventLevel+"");
					
					AgentalarmControlutil.PutAlarmlevel(node.getId()+"", nm.getName(), nm.getType(), "",eventLevel+"",true);
					
				}
				
				//AlarmResourceCenter.getInstance().setAttribute(node.getNodeid()+":"+nm.getType()+":"+nm.getName(), "0");
				
			}else
			 {
				
				if(nm.getName().equals("diskperc") ||nm.getName().equals("proce") || nm.getName().equals("diskinc") || nm.getName().equals("diskbusy"))
				{
				//ShareData.getAgentalarmlevellist().put(name, eventLevel+"");
					
				//ShareData.getAgentalarmlevellist().remove(name);
				AgentalarmControlutil.DeleteAlarmkeyname(node.getId()+"", name);
				}else
				{
					//ShareData.getAgentalarmlevellist().put(node.getId()+":"+nm.getType()+":"+name, eventLevel+"");
					//ShareData.getAgentalarmlevellist().remove(node.getId()+":"+nm.getType()+":"+nm.getName());
					AgentalarmControlutil.DeleteAlarmlevel(node.getId()+"", nm.getName(), nm.getType(), "");
				}
				
				//AlarmResourceCenter.getInstance().setAttribute(node.getNodeid()+":"+nm.getType()+":"+nm.getName(), "0");
				
			 }
			
			
			
			if(eventTimes > 0){
				// ������� 0 ���¼��ȼ����� Ϊ �澯�ȼ�
				if(nm.getName().equals("diskperc") ||nm.getName().equals("proce") || nm.getName().equals("diskinc") || nm.getName().equals("diskbusy"))
				{
				//ShareData.getAgentalarmlevellist().put(name, eventLevel+"");
				
				AgentalarmControlutil.PutAlarmlevel(node.getId()+"", name, nm.getType(), "",eventLevel+"",false);
				}else
				{
					//ShareData.getAgentalarmlevellist().put(node.getId()+":"+nm.getType()+":"+nm.getName(), eventLevel+"");
					AgentalarmControlutil.PutAlarmlevel(node.getId()+"", nm.getName(), nm.getType(), "",eventLevel+"",true);
				}
				
				alarmLevel = eventLevel;
			}
			//System.out.println("===========checkAlarm========================"+nm.getName()+":"+eventLevel);
			//System.out.println(ShareData.getAgentalarmlevellist());
			//System.out.println("============checkAlarm=======================");
			return alarmLevel;
    		
    	}
		
		// ����澯  ly add
		public void updateData(Host node, NodeGatherIndicators nodeGatherIndicators, String value) {
			updateData(node, nodeGatherIndicators, value, null);
		}

		// ����澯 ly add
		public void updateData(Host node, NodeGatherIndicators nodeGatherIndicators, String value, String sIndex) {
			AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
			List list = alarmIndicatorsUtil.getAlarmInicatorsThresholdForNode(node.getId() + "", nodeGatherIndicators.getType(), nodeGatherIndicators.getSubtype(), nodeGatherIndicators.getName());
			if (list != null && list.size() > 0) {
				for (int i = 0; i < list.size(); i++) {
					AlarmIndicatorsNode nm = (AlarmIndicatorsNode) list.get(i);
					checkEvent(node, nm, value, sIndex);
				}
			}
		}
	    
		/**
		 * ����澯����
		 * 
		 * @param node			�豸
		 * @param alarmIndicatorsNode	ָ��
		 * @param value			�ɼ�����ֵ
		 * @param alarmLevel	�澯�ȼ�
		 */
        public void sendAlarm(NodeDTO node, AlarmIndicatorsNode alarmIndicatorsNode, String value, int alarmLevel){
			CheckEvent checkEvent = new CheckEvent();
			checkEvent.setName(node.getId()+":"+alarmIndicatorsNode.getType()+":"+alarmIndicatorsNode.getName());
			checkEvent.setAlarmlevel(alarmLevel);
			//���ø澯�Ա���Ϣ�ŵ��ڴ�ϵͳ��,������Ҫ�ŵ����ݿ����
			saveEvent(checkEvent);
			// --------------------
			String unit = alarmIndicatorsNode.getThreshlod_unit();
			String threshold = getThresholdByLevel(alarmIndicatorsNode, alarmLevel);
			String eventtype = "poll";
			String eventlocation = node.getName() + "(" + node.getName() + ")";
			String bid = node.getBusinessId();
			String content = node.getName()+"(IP: "+node.getIpaddress()+") "+alarmIndicatorsNode.getAlarm_info()
							 + " ��ǰֵ:"+ value + " " + unit 
							 + " ��ֵ:" + threshold + " " + unit;
			Integer level1 = alarmLevel;
			String subtype = "";
			if("service".equalsIgnoreCase(alarmIndicatorsNode.getType())){
				subtype = alarmIndicatorsNode.getSubtype();
			}else{
				subtype = alarmIndicatorsNode.getType();
			}
			//String subtype = alarmIndicatorsNode.getType();
			String subentity = alarmIndicatorsNode.getName();
			String objid = node.getId() + "";
			String ipaddress = node.getIpaddress();
			// ���� eventList 
			EventList eventList = createEvent(eventtype, eventlocation, bid, content, level1, subtype, subentity, ipaddress, objid);
        	SendAlarmUtil alarmUtil = new SendAlarmUtil();
            alarmUtil.sendAlarm(checkEvent ,eventList, alarmIndicatorsNode);
            
            
        }
        /**
		 * ����˿ڸ澯����
		 * 
		 * @param node			�豸
		 * @param portNode	ָ��
		 * @param value			�ɼ�����ֵ
		 * @param alarmLevel	�澯�ȼ�
		 * 
		 */
        public void sendAlarmPort(NodeDTO node, AlarmPort portNode, String value, int alarmLevel,String inOrOut){
			CheckEvent checkEvent = new CheckEvent();
			
			checkEvent.setName(portNode.getId()+":"+portNode.getType()+":"+portNode.getName());
			int realLevel=Integer.valueOf(alarmLevel);
			if (realLevel>3) {
				realLevel=realLevel-3;
			}
			checkEvent.setAlarmlevel(realLevel);
			//���ø澯�Ա���Ϣ�ŵ��ڴ�ϵͳ��,������Ҫ�ŵ����ݿ����
			saveEvent(checkEvent);
			// --------------------
			String unit = "kb/s";
			int threshold=0;
			if(alarmLevel == 1){
				threshold = portNode.getLevelinvalue1();
			} else if (alarmLevel == 2){
				threshold = portNode.getLevelinvalue2();
			}else if (alarmLevel == 3){
				threshold = portNode.getLevelinvalue3();
			}else if (alarmLevel == 4){
				threshold = portNode.getLeveloutvalue1();
			}else if (alarmLevel == 5){
				threshold = portNode.getLeveloutvalue2();
			}else if (alarmLevel == 6){
				threshold = portNode.getLeveloutvalue3();
			}
			
			String eventtype = "poll";
			String eventlocation = portNode.getName() + "(IP: "+node.getIpaddress()+") ";
			String bid = node.getBusinessId();
			String content = "";
		//	if(checkflag == 0){
				content = portNode.getName()+"(IP: "+node.getIpaddress()+") "+inOrOut+portNode.getAlarm_info()
							+ " ��ǰֵ:"+ value + " " + unit 
							 + " ��ֵ:" + threshold + " " + unit;
//			}else{
//				content = node.getName()+"(IP: "+node.getIpaddress()+") "+portNode.getAlarm_info();
//			}
							 
			Integer level1 = alarmLevel;
			String subtype = "";
			if("service".equalsIgnoreCase(portNode.getType())){
				subtype = portNode.getSubtype();
			}else{
				subtype = portNode.getType();
			}
			
			String subentity = "utilUdx";
			String objid = node.getId() + "";
			String ipaddress = node.getIpaddress();
			// ���� eventList 
			EventList eventList = createEvent(eventtype, eventlocation, bid, content, level1, subtype, subentity, ipaddress, objid);
       	SendAlarmUtil alarmUtil = new SendAlarmUtil();
       	alarmUtil.sendPortAlarm(checkEvent ,eventList, alarmLevel,portNode);
            
            
        }
        /**
		 * ����澯����
		 * 
		 * @param node			�豸
		 * @param alarmIndicatorsNode	ָ��
		 * @param value			�ɼ�����ֵ
		 * @param alarmLevel	�澯�ȼ�
		 * @param checkflag	    �ж����ַ��������ָ�ʽ,��Ϊ�ַ�,����Ҫ�Ѿ���ķ�ֵд���¼���ȥ,0:���� 1:�ַ�
		 */
        public void sendAlarm(NodeDTO node, AlarmIndicatorsNode alarmIndicatorsNode, String value, int alarmLevel,int checkflag){
			CheckEvent checkEvent = new CheckEvent();
			checkEvent.setName(node.getId()+":"+alarmIndicatorsNode.getType()+":"+alarmIndicatorsNode.getName());
			checkEvent.setAlarmlevel(alarmLevel);
			//���ø澯�Ա���Ϣ�ŵ��ڴ�ϵͳ��,������Ҫ�ŵ����ݿ����
			saveEvent(checkEvent);
			// --------------------
			String unit = alarmIndicatorsNode.getThreshlod_unit();
			String threshold = getThresholdByLevel(alarmIndicatorsNode, alarmLevel);
			String eventtype = "poll";
			String eventlocation = node.getName() + "(" + node.getName() + ")";
			String bid = node.getBusinessId();
			String content = "";
			if(checkflag == 0){
				content = node.getName()+"(IP: "+node.getIpaddress()+") "+alarmIndicatorsNode.getAlarm_info()
							+ " ��ǰֵ:"+ value + " " + unit 
							 + " ��ֵ:" + threshold + " " + unit;
			}else{
				content = node.getName()+"(IP: "+node.getIpaddress()+") "+alarmIndicatorsNode.getAlarm_info();
			}
							 
			Integer level1 = alarmLevel;
			String subtype = "";
			if("service".equalsIgnoreCase(alarmIndicatorsNode.getType())){
				subtype = alarmIndicatorsNode.getSubtype();
			}else{
				subtype = alarmIndicatorsNode.getType();
			}
			//String subtype = alarmIndicatorsNode.getType();
			String subentity = alarmIndicatorsNode.getName();
			String objid = node.getId() + "";
			String ipaddress = node.getIpaddress();
			// ���� eventList 
			EventList eventList = createEvent(eventtype, eventlocation, bid, content, level1, subtype, subentity, ipaddress, objid);
        	SendAlarmUtil alarmUtil = new SendAlarmUtil();
            alarmUtil.sendAlarm(checkEvent ,eventList, alarmIndicatorsNode);
            
            //���澯��Ϣ����澯��Ϣ������
            NodeAlarmUtil.saveNodeAlarmInfo(eventList, subentity);
        }
        /**
         * ����澯����
         * 
         * @param node			�豸
         * @param alarmIndicatorsNode	ָ��
         * @param value			�ɼ�����ֵ
         * @param value			��ظ澯��Ϣ
         * @param alarmLevel	�澯�ȼ�
         * @param checkflag	    �ж����ַ��������ָ�ʽ,��Ϊ�ַ�,����Ҫ�Ѿ���ķ�ֵд���¼���ȥ,0:���� 1:�ַ�
         */
        public void sendAlarm(NodeDTO node, AlarmIndicatorsNode alarmIndicatorsNode, String value,String alarminfo, int alarmLevel,int checkflag){
        	CheckEvent checkEvent = new CheckEvent();
        	checkEvent.setName(node.getId()+":"+alarmIndicatorsNode.getType()+":"+alarmIndicatorsNode.getName());
        	checkEvent.setAlarmlevel(alarmLevel);
        	//���ø澯�Ա���Ϣ�ŵ��ڴ�ϵͳ��,������Ҫ�ŵ����ݿ����
        	saveEvent(checkEvent);
        	// --------------------
        	String unit = alarmIndicatorsNode.getThreshlod_unit();
        	String threshold = getThresholdByLevel(alarmIndicatorsNode, alarmLevel);
        	String eventtype = "poll";
        	String eventlocation = node.getName() + "(" + node.getName() + ")";
        	String bid = node.getBusinessId();
        	String content = "";
        	if(checkflag == 0){
        		content = node.getName()+"(IP: "+node.getIpaddress()+") "+alarmIndicatorsNode.getAlarm_info()
        		+ " ��ǰֵ:"+ value + " " + unit 
        		+ " ��ֵ:" + threshold + " " + unit;
        	}else if (checkflag == 1) {
        		content = node.getName()+"(IP: "+node.getIpaddress()+":"+alarminfo+") "+alarmIndicatorsNode.getAlarm_info()
        		+ " ��ǰֵ:"+ value + " " + unit 
        		+ " ��ֵ:" + threshold + " " + unit;
			}else{
        		content = node.getName()+"(IP: "+node.getIpaddress()+") "+alarmIndicatorsNode.getAlarm_info();
        	}
        	
        	Integer level1 = alarmLevel;
        	String subtype = "";
        	if("service".equalsIgnoreCase(alarmIndicatorsNode.getType())){
        		subtype = alarmIndicatorsNode.getSubtype();
        	}else{
        		subtype = alarmIndicatorsNode.getType();
        	}
        	//String subtype = alarmIndicatorsNode.getType();
        	String subentity = alarmIndicatorsNode.getName();
        	String objid = node.getId() + "";
        	String ipaddress = node.getIpaddress();
        	// ���� eventList 
        	EventList eventList = createEvent(eventtype, eventlocation, bid, content, level1, subtype, subentity, ipaddress, objid);
        	SendAlarmUtil alarmUtil = new SendAlarmUtil();
        	alarmUtil.sendAlarm(checkEvent ,eventList, alarmIndicatorsNode);
        	
        	
        }
        
        /**
		 * ����澯����
		 * 
		 * @param node			�豸
		 * @param alarmIndicatorsNode	ָ��
		 * @param value			�ɼ�����ֵ
		 * @param alarmLevel	�澯�ȼ�
		 */
        public void sendAlarm(NodeDTO node, AlarmIndicatorsNode alarmIndicatorsNode, String value, int alarmLevel, String sIndex){
        	// �� name ��Ϊ �ø澯��Ψһ��ʶ��
            String name = node.getId()+":"+alarmIndicatorsNode.getType()+":"+alarmIndicatorsNode.getName();
            if(sIndex!=null && sIndex.trim().length() > 0){
            	name = name + ":" +sIndex;
            }
			CheckEvent checkEvent = new CheckEvent();
			checkEvent.setName(name);
			checkEvent.setAlarmlevel(alarmLevel);
			//���ø澯�Ա���Ϣ�ŵ��ڴ�ϵͳ��,������Ҫ�ŵ����ݿ����
			saveEvent(checkEvent);
			// --------------------
			String unit = alarmIndicatorsNode.getThreshlod_unit();
			String threshold = getThresholdByLevel(alarmIndicatorsNode, alarmLevel);
			String eventtype = "poll";
			String eventlocation = node.getName() + "(" + node.getName() + ")";
			String bid = node.getBusinessId();
			String content = node.getName()+"(IP: " +node.getIpaddress()+") " + sIndex + " " +alarmIndicatorsNode.getAlarm_info()
							 + " ��ǰֵ:"+ value + " " + unit 
							 + " ��ֵ:" + threshold + " " + unit;
			Integer level1 = alarmLevel;
			String subtype = alarmIndicatorsNode.getType();
			String subentity = alarmIndicatorsNode.getName();
			String objid = node.getId() + "";
			String ipaddress = node.getIpaddress();
			//���̸澯����Ϣ����benji(IP: 127.0.0.1) diskperc:/ ���������ʳ�����ֵ ��ǰֵ:70.438705 % ��ֵ:3 %  ��
			//����diskperc/diskincΪ��������̸澯��ϸ���.
			
			System.out.println("=============���͸澯=================="+name);
			System.out.println("=============���͸澯=================="+name);
			System.out.println("=============���͸澯=================="+name);
			System.out.println("=============���͸澯=================="+name);
			System.out.println("=============���͸澯=================="+name);
			System.out.println("=============���͸澯=================="+name);
			System.out.println("=============���͸澯=================="+name);
			if(subentity.equalsIgnoreCase("diskperc") || subentity.equalsIgnoreCase("diskinc")){
				content = content.replace(subentity+":", "");
			}
			// ���� eventList 
			EventList eventList = createEvent(eventtype, eventlocation, bid, content, level1, subtype, subentity, ipaddress, objid);
        	SendAlarmUtil alarmUtil = new SendAlarmUtil();
            alarmUtil.sendAlarm(checkEvent ,eventList, alarmIndicatorsNode);
        }
        
        /**
		 * ����澯����
		 * 
		 * @param node			�豸
		 * @param alarmIndicatorsNode	ָ��
		 * @param value			�ɼ�����ֵ
		 * @param alarmLevel	�澯�ȼ�
		 */
        public void sendMiddlewareAlarm(NodeDTO node, AlarmIndicatorsNode alarmIndicatorsNode, String value, int alarmLevel, String sIndex){
        	// �� name ��Ϊ �ø澯��Ψһ��ʶ��
            String name = node.getId()+":"+alarmIndicatorsNode.getType()+":"+alarmIndicatorsNode.getSubtype()+":"+alarmIndicatorsNode.getName();
            if(sIndex!=null && sIndex.trim().length() > 0){
            	name = name + ":" +sIndex;
            }
			CheckEvent checkEvent = new CheckEvent();
			checkEvent.setName(name);
			checkEvent.setAlarmlevel(alarmLevel);
			//���ø澯�Ա���Ϣ�ŵ��ڴ�ϵͳ��,������Ҫ�ŵ����ݿ����
			saveEvent(checkEvent);
			// --------------------
			String unit = alarmIndicatorsNode.getThreshlod_unit();
			String threshold = getThresholdByLevel(alarmIndicatorsNode, alarmLevel);
			String eventtype = "poll";
			String eventlocation = node.getName() + "(" + node.getName() + ")";
			String bid = node.getBusinessId();
			String content = node.getIpaddress()+" " + sIndex + " " +alarmIndicatorsNode.getAlarm_info()
							 + " ��ǰֵ:"+ value + " " + unit 
							 + " ��ֵ:" + threshold + " " + unit;
			Integer level1 = alarmLevel;
			String subtype = alarmIndicatorsNode.getType();
			String subentity = alarmIndicatorsNode.getName();
			String objid = node.getId() + "";
			String ipaddress = node.getIpaddress();
			// ���� eventList 
			EventList eventList = createEvent(eventtype, eventlocation, bid, content, level1, subtype, subentity, ipaddress, objid);
        	SendAlarmUtil alarmUtil = new SendAlarmUtil();
            alarmUtil.sendAlarm(checkEvent ,eventList, alarmIndicatorsNode);
        }
        
        /**
		 * ����澯����
		 * 
		 * @param node			�豸
		 * @param alarmIndicatorsNode	ָ��
		 * @param value			�ɼ�����ֵ
		 * @param alarmLevel	�澯�ȼ�
		 */
        public void sendAlarm(EventList eventList, AlarmIndicatorsNode alarmIndicatorsNode){
			CheckEvent checkEvent = new CheckEvent();
			checkEvent.setName(eventList.getNodeid()+":"+alarmIndicatorsNode.getType()+":"+alarmIndicatorsNode.getName());
			checkEvent.setAlarmlevel(eventList.getLevel1());
			//���ø澯�Ա���Ϣ�ŵ��ڴ�ϵͳ��,������Ҫ�ŵ����ݿ����
			saveEvent(checkEvent);
			// --------------------
        	SendAlarmUtil alarmUtil = new SendAlarmUtil();
            alarmUtil.sendAlarm(checkEvent ,eventList, alarmIndicatorsNode);
        }
		
		/**
		 * �����澯��
		 * @param eventtype
		 * @param eventlocation
		 * @param bid
		 * @param content
		 * @param level1
		 * @param subtype
		 * @param subentity
		 * @param ipaddress
		 * @param objid
		 * @return
		 */
		private EventList createEvent(String eventtype,String eventlocation,String bid,String content,int level1,String subtype,String subentity,String ipaddress,String objid){
    		//�����¼�
    		SysLogger.info("##############��ʼ�����¼�############");
    		EventList eventlist = new EventList();
    		eventlist.setEventtype(eventtype);
    		eventlist.setEventlocation(eventlocation);
    		eventlist.setContent(content);
    		eventlist.setLevel1(level1);
    		eventlist.setManagesign(0);
    		eventlist.setBak("");
    		eventlist.setRecordtime(Calendar.getInstance());
    		eventlist.setReportman("ϵͳ��ѯ");
    		eventlist.setBusinessid(bid);
    		eventlist.setNodeid(Integer.parseInt(objid));
    		eventlist.setOid(0);
    		eventlist.setSubtype(subtype);
    		eventlist.setSubentity(subentity);
    		eventlist.setIpaddress(ipaddress);
    		return eventlist;
    	}	
	    	
		/**
		 * 
		 * ����¼��ȼ�
		 * 
		 * <p>compare_type �ȽϷ�ʽ��1 Ϊ���� �����ڱȽϣ�0 Ϊ���� ��С�ڱȽ�</p>
		 * <p>���ظ澯�ȼ� ������澯�򷵻� 0</p>
		 * 
		 * @author nielin
		 * @param value 	   ֵ
		 * @param limenvalue0  һ����ֵ
		 * @param limenvalue1  ������ֵ
		 * @param limenvalue2  ������ֵ
		 * @param compare_type �ȽϷ�ʽ
		 * 
		 * @return level
		 */
		private int checkEventLevel(double value , 
								   double limenvalue0 , 
								   double limenvalue1 ,
								   double limenvalue2 ,
								   int compare_type)
		{
			int level = 0; // ��Ҫ���صĵȼ�
			if(compare_type==0){
				//����Ƚ�
				if(value <= limenvalue2){
					level = 3;
				}else if(value <= limenvalue1){
					level = 2;
				}else if(value <= limenvalue0){
					level = 1;
				}else{
					level = 0;
				}
			}else{
				//����Ƚ�
				
				if(value >= limenvalue2){
					level = 3;
				}else if(value >= limenvalue1){
					level = 2;
				}else if(value >= limenvalue0){
					level = 1;
				}else{
					level = 0;
				}
			}
			return level;
		}
		
		/**
		 * ���澯���� 
		 * <p>������ڹ涨�ĸ澯����������һ������ 0���� ���򷵻� 0 ����ͬʱ���澯���� + 1<p>;
		 * @param alarmIndicatorsNode �澯ָ��
		 * @param alarmLevel		  ��ǰ�澯�ȼ�
		 * @return
		 */
		private int checkEventTimes(AlarmIndicatorsNode alarmIndicatorsNode , int eventLevel, String name){
			int eventTimes = 0;  		// �����¼��Ĵ���
			int defineTimes = 0;		// ��������������¼�����
			int lastEventTimes = 0;		// ֮ǰ�������¼�����
			if(eventLevel == 0){
				// ����¼��ȼ� Ϊ 0 ˵���¼��ָ� ������¼�������Ϊ 0
				setEventTimes(name, 0);
				return eventTimes;
			}
			
			defineTimes = getTimesByLevel(alarmIndicatorsNode, eventLevel); 
			
			//System.out.println("==defineTimes="+defineTimes +"===nodeid="+alarmIndicatorsNode.getNodeid());
			lastEventTimes = getEventTimes(name, eventLevel);
			
			//System.out.println("==lastEventTimes="+lastEventTimes);
			eventTimes = lastEventTimes + 1;   // �ϴμ���� Ȼ�󱣴� 
			
			setEventTimes(name, eventTimes);
			
			if(eventTimes < defineTimes){
				// ���С�ڶ���Ĵ����򲻲����澯 ���� 0 
				eventTimes = 0;
			} 
			return eventTimes;
		}
		
		/**
		 * �õ�֮ǰ�ĸ澯����
		 * @param name
		 * @param alarmLevel
		 * @return
		 */
		private int getEventTimes(String name, int alarmLevel)
		{
			int times = 0;
			try {
				String num = (String)AlarmResourceCenter.getInstance().getAttribute(name);
				if(num!=null&&num.length()>0){
					times = Integer.parseInt(num);
				}
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return times;
		}
		
		/**
		 * ���ø澯����
		 * @param name
		 * @param times
		 * @return
		 */
		private int setEventTimes(String name , int times)
		{
			try {
				
				//System.out.println("**&&&&&&&*****8888999988===999==NAME="+name+"===="+ times);
				AlarmResourceCenter.getInstance().setAttribute(name, String.valueOf(times));
				
				//System.out.println("===="+AlarmResourceCenter.getInstance());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return times;
		}
		
		
		/**
		 * ����ָ��͸澯�ȼ����ط�ֵ
		 * @param nm
		 * @param alarmLevel
		 * @return
		 */
		private String getThresholdByLevel(AlarmIndicatorsNode nm , int alarmLevel){
			String threshold = "";
			if(alarmLevel == 1){
				threshold = nm.getLimenvalue0();
			} else if (alarmLevel == 2){
				threshold = nm.getLimenvalue1();
			}else if (alarmLevel == 3){
				threshold = nm.getLimenvalue2();
			}
			return threshold;
		}
		
		/**
		 * ����ָ��͸澯�ȼ����ض���ĸ澯����
		 * @param nm
		 * @param alarmLevel
		 * @return
		 */
		private int getTimesByLevel(AlarmIndicatorsNode nm , int eventLevel){
			int times_int = 0;
			String times_str = "0";
			if(eventLevel == 1){
				times_str = nm.getTime0();
			} else if (eventLevel == 2){
				times_str = nm.getTime1();
			}else if (eventLevel == 3){
				times_str = nm.getTime2();
			}
			try {
				times_int = Integer.parseInt(times_str);
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return times_int;
		}
		
		/**
		 * �� ���ݿ��� ɾ���ϴεĸ澯
		 * ����������ҳ��� ��ɾ�������� �� �����������null
		 * <p>���ڴ�����صĸ澯���,����ʵ�ִ����ݿ����ɾ���������<p>
		 * @param name
		 * @return CheckEvent
		 */
		private CheckEvent deleteEvent(String name){
			//���ڴ�����صĸ澯���,����ʵ�ִ����ݿ����ɾ���������
			CheckEvent checkEvent = null;
			Hashtable checkEventHash = ShareData.getCheckEventHash();
			
			
			//System.out.println("=tttbb====="+checkEventHash);
			
			
			if(checkEventHash != null && checkEventHash.size()>0){
				if(checkEventHash.containsKey(name)){
					//�����ڸ澯
					checkEvent = new CheckEvent();
					checkEvent.setName(name);
					checkEvent.setAlarmlevel((Integer)checkEventHash.get(name));
					AgentalarmControlutil.GetCheckEventsql(checkEvent);
					//CheckEventDao checkeventdao = new CheckEventDao();
//					try{
//						checkeventdao.delete(name);
//					}catch(Exception e){
//						e.printStackTrace();
//					}finally{
//						checkeventdao.close();
//					}
				}
			}

			return checkEvent;
		}
		
		/**
		 * �� ���ݿ��� ɾ���ϴεĸ澯 ����������ҳ��� ��ɾ�������� �� �����������null
		 * <p>
		 * ���ڴ�����صĸ澯���,����ʵ�ִ����ݿ����ɾ���������
		 * <p>
		 * 
		 * @param name
		 * @return CheckEvent
		 */
		public CheckEvent deleteEvent(String nodeId, String type, String subtype, String indicatorsName, String sIndex) {
			// ���ڴ�����صĸ澯���,����ʵ�ִ����ݿ����ɾ���������
			CheckEvent checkEvent = null;
			// List<CheckEvent> list = null;
			String name = nodeId + ":" + type + ":" + subtype + ":" + indicatorsName;
			// System.out.println("-----sIndex----------:"+sIndex);
			if (sIndex != null && sIndex.trim().length() > 0) {
				name = name + ":" + sIndex;
			}  
			Hashtable checkEventHash = ShareData.getCheckEventHash();
			// System.out.println(name+"-----checkEventHash----------:"+checkEventHash);
			if (checkEventHash != null && checkEventHash.size() > 0) {
				// System.out.println("-----checkEventHash.containsKey(name)----------:"+checkEventHash.containsKey(name));
				if (checkEventHash.containsKey(name)) {
					checkEvent = new CheckEvent();
					checkEvent.setNodeid(nodeId);
					checkEvent.setIndicatorsName(indicatorsName);
					checkEvent.setType(type);
					checkEvent.setSubtype(subtype);
					checkEvent.setSindex(sIndex);
					checkEvent.setAlarmlevel((Integer) checkEventHash.get(name));
					CheckEventDao checkeventdao = new CheckEventDao();
					try {
						if (sIndex != null && sIndex.length() > 0) {
							checkeventdao.deleteCheckEvent(nodeId, type, subtype, indicatorsName, sIndex);
						} else {
							checkeventdao.deleteCheckEvent(nodeId, type, subtype, indicatorsName);
						}
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						checkeventdao.close();
					}
					checkEventHash.remove(name);
				}
			}
			// CheckEventDao checkeventdao = new CheckEventDao();
			// try {
			// if (sIndex != null && sIndex.length() > 0) {
			// //list�������Ӧid��CheckEvent����
			// list = (List<CheckEvent>) checkeventdao.findCheckEvent(nodeId, type,
			// subtype, indicatorsName, sindex);
			// } else {
			// list = (List<CheckEvent>) checkeventdao.findCheckEvent(nodeId, type,
			// subtype, indicatorsName);
			// }
			// if (list != null && list.size() > 0) {
			// checkEvent = list.get(0);//
			// }
			// } catch (Exception e) {
			// e.printStackTrace();
			// } finally {
			// checkeventdao.close();
			// }

			return checkEvent;// ���� CheckEvent����
		}
		
		
		
		/**
		 * �����θ澯��Ϣ���浽���ݿ���
		 * <p>���ø澯�Ա���Ϣ�ŵ��ڴ�ϵͳ��,������Ҫ�ŵ����ݿ����<p>
		 * @param name
		 */
		private void saveEvent(CheckEvent checkEvent){
			//���ø澯�Ա���Ϣ�ŵ��ڴ�ϵͳ��,������Ҫ�ŵ����ݿ����
			
			
			AgentalarmControlutil.GetDeleteCheckEventsql(checkEvent);
			AgentalarmControlutil.GetCheckEventsql(checkEvent);
			
//			CheckEventDao checkeventdao = new CheckEventDao();
//			try{
//				checkeventdao.save(checkEvent);
//			}catch(Exception e){
//				e.printStackTrace();
//			}finally{
//				checkeventdao.close();
//			}
		}
		
		/**
		 * ���ݸ澯ָ������ ��ȡ�澯���� 
		 * @param target   �澯ָ��
		 * @param oldvalue ��ֵ
		 * @param value    ��ֵ
		 * @return
		 */
		public synchronized static String getAlarmInfo(String target, Object oldvalue, Object valueObj){
			String content = null;
			String value = null;
			if("cpu".equalsIgnoreCase(target.trim())){
				value = (String)valueObj;
				content = "CPU�����иı䣬֮ǰ��"+oldvalue+"����������"+value+"��";
			}else if("diskSize".equalsIgnoreCase(target.trim())){
				value = (String)valueObj;
				content = "���������иı䣬֮ǰ��"+oldvalue+"��������"+value;
			}else  if("diskArray".equalsIgnoreCase(target.trim())){
				List<String> oldDiskArray = (ArrayList<String>)oldvalue;
				StringBuffer tempBuffer = new StringBuffer();
				tempBuffer.append("�����̷��иı䣬֮ǰ��");
				for(int i=0; i<oldDiskArray.size(); i++){
					tempBuffer.append(oldDiskArray.get(i));
					if(i != oldDiskArray.size()-1){
						tempBuffer.append(",");
					}
				}
				tempBuffer.append("�����ڵ��̷�Ϊ");
				List<String> diskArray = (ArrayList<String>)valueObj;
				for(int i=0; i<diskArray.size(); i++){
					tempBuffer.append(diskArray.get(i));
					if(i != diskArray.size()-1){
						tempBuffer.append(",");
					}
				}
				content = tempBuffer.toString();
			}else if("PhysicalMemory".equalsIgnoreCase(target.trim())){
				value = (String)valueObj;
				content = "�����ڴ��иı䣬֮ǰ��"+oldvalue+"��������"+value;
			}
			return content;
		}
		
		/**
		 * �澯ָ��
		 * @param node    �澯�ڵ�
		 * @param content �澯����
		 * @param target  �澯ָ������
		 */
		public synchronized static void saveEventList(Host node, String content, String target){
			Calendar Cal=Calendar.getInstance();
    		EventListDao dao = null;
			try {
				dao = new EventListDao();
	    		EventList vo = new EventList();
	    		vo.setEventtype("poll");
	    		vo.setEventlocation(node.getLocation());
	    		vo.setContent(content);
	    		vo.setLevel1(1);
	    		vo.setManagesign(1);
	    		vo.setBusinessid(node.getBid());
	    		vo.setManagesign(0);
	    		vo.setReportman("ϵͳ��ѯ");
	    		vo.setNodeid(node.getId());
	    		vo.setOid(0);
	    		vo.setRecordtime(Cal);
	    		vo.setSubtype("host");
	    		vo.setSubentity(target);
				dao.save(vo);
			} catch (Exception e) {
				e.printStackTrace();
			} finally{
				if(dao != null){
					dao.close();
				}
			}
		}
		
	/**
	 * ָ�����жϹ�����
	 * @param node    ����ص��豸
	 * @param target  �澯ָ��
	 * @param valueObj   ָ��ֵ���ϻ����ַ���
	 */
	public synchronized void hardwareInfo(Host node,String target,Object valueObj){
		if(target == null){
			return;
		}
		//�̷��������̷�ֵ
		Hashtable<String,Object> diskInfo = null; 
		//ָ��仯��ʷ��Ϣ����
		Hashtable indicatorsInfoHash = ResourceCenter.getInstance().getIndicatorsChangeInfoHash();
		boolean isSystemFirstStart = SystemFlag.getInstance().isFirstStart();//ϵͳĬ�ϲ��ǵ�һ����ѵ
		if(!isSystemFirstStart){
			//�澯����
			String content = null;
			if("cpu".equalsIgnoreCase(target) || "PhysicalMemory".equalsIgnoreCase(target)){
				//����ֵ���� cpu����\�����ڴ��С\�����ܴ�С
				String value = (String)valueObj;
				//####################���Դ���##########################
//					value = "98";
				//####################���Դ���##########################
				String oldValue = null;
				if(indicatorsInfoHash.containsKey(node.getIpAddress()+target)){
					oldValue = (String)indicatorsInfoHash.get(node.getIpAddress()+target);
				}
				if(oldValue != null && Math.floor(Double.parseDouble(oldValue.replace("M", "")))!=Math.floor(Double.parseDouble(value.replace("M", "")))){
					content = getAlarmInfo(target, oldValue, value);
					saveEventList(node, content, target);
				}
			}
			if("disk".equalsIgnoreCase(target)){
				diskInfo = (Hashtable<String,Object>)valueObj;
				//��һ�α����disk����
				Hashtable<String,Object> oldDiskInfo = null;
				if(indicatorsInfoHash.containsKey(node.getIpAddress()+target)){
					oldDiskInfo = (Hashtable<String,Object>)indicatorsInfoHash.get(node.getIpAddress()+target);
				}
				if(oldDiskInfo != null){
					//��ǰ���̵��ܴ�С
					String diskSize = null;
					String oldDiskSize = null;
					if(diskInfo.containsKey("diskSize")){
						diskSize = (String)diskInfo.get("diskSize");
						//####################���Դ���##########################
//						diskSize = "98";
						//####################���Դ���##########################
						if(oldDiskInfo.containsKey("diskSize")){
							oldDiskSize = (String)oldDiskInfo.get("diskSize");
						}
						if(diskSize != null && Math.floor(Double.parseDouble(diskSize.replace("G", "")))!=Math.floor(Double.parseDouble(oldDiskSize.replace("G", "")))){
							content = getAlarmInfo("diskSize", oldDiskSize, diskSize);
							saveEventList(node, content, target);
						}
					}
					//��ǰ�̷�����
					List<String> diskNameList = null;
					List<String>  oldDiskNameList = null;//�̷�����
					if(diskInfo.containsKey("diskNameList")){
						diskNameList = (ArrayList<String>)diskInfo.get("diskNameList");
						//####################���Դ���##########################
//						diskNameList = new ArrayList<String>();
//						diskNameList.add("d:");
//						diskNameList.add("e:");
						//####################���Դ���##########################
						if(oldDiskInfo.containsKey("diskNameList")){
							oldDiskNameList = (ArrayList<String>)oldDiskInfo.get("diskNameList");
						}
						boolean containFlag = true;//Ĭ�ϰ������̷�
						boolean diskNumFlag = true;//Ĭ�ϴ��̸������ޱ仯
						if(oldDiskNameList.containsAll(diskNameList)){
							containFlag = true;
						}else{//�������Ʋ�һ�µ�����
							containFlag = false;
						}
						//���̸�����һ��������
						if(diskNameList.size() != oldDiskNameList.size()){
							diskNumFlag = false;
						}
						if(!containFlag || !diskNumFlag){//���������̷������ ���� ������һ��
							content = getAlarmInfo("diskArray", oldDiskNameList, diskNameList);
							saveEventList(node, content, target);
						}
					}
				}
			}
		}
		//��ǰ�ɼ����ݴ�����ʷ���� key��ipaddress+ָ������   value���仯��ֵ
		indicatorsInfoHash.put(node.getIpAddress()+target, valueObj);
	}
	
	
	
	
	/**
	 * 
	 * 
	 * ���������ڴ����ҳ�ʵ�ʹ��������ж��Ƿ��и澯
	 * @param vo  �ڵ�Ķ���
	 * @param memoryData  �ڴ��ֵ
	 * @param type
	 * @param pageData
	 */
	public void checkMemoryAndPage(Object vo , Hashtable datahashtable,String physicalmemory,String vmemory,String hosttype)
	{
		
		Node node = (Node)vo;
		
		AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
		List listmem = alarmIndicatorsUtil.getAlarmInicatorsThresholdForNode(String.valueOf(node.getId()), AlarmConstant.TYPE_HOST, hosttype,physicalmemory);
		List listpage = alarmIndicatorsUtil.getAlarmInicatorsThresholdForNode(String.valueOf(node.getId()), AlarmConstant.TYPE_HOST, hosttype,vmemory);//"pagingusage"
		
		
		//System.out.println("=====�ڴ�澯��ֵ1==listmem="+listmem.size());
		//System.out.println("=====�ڴ�澯��ֵ2==listpage="+listpage.size());
		
		
		int levntmem=0;
		int levntpage=0;
		CheckEventUtil checkutil = new CheckEventUtil();
		
		Hashtable paginghash = new Hashtable();
		String pagevalue="";//pagingusage
		String memvalue="";
		
		if(vmemory=="pagingusage")
			
		{
		if(datahashtable.get("pagingusage") != null)paginghash = (Hashtable)datahashtable.get("pagingusage");
		if(paginghash != null && paginghash.size()>0){
			if(paginghash.get("Percent_Used") != null){
				pagevalue=((String)paginghash.get("Percent_Used")).replaceAll("%", "");
			}
			
		}
		}
		
		
		
			//�����ڴ�������
			Vector memoryVector = new Vector();
			if(datahashtable.get("physicalmem") != null)memoryVector = (Vector)datahashtable.get("physicalmem");
			if(memoryVector != null && memoryVector.size()>0){
				for(int i=0;i<memoryVector.size();i++){
					Memorycollectdata memorydata = (Memorycollectdata)memoryVector.get(i);
					 //SysLogger.info("windows=========="+memorydata.getSubentity()+"==="+memorydata.getEntity()+"==="+memorydata.getThevalue());
					if("PhysicalMemory".equalsIgnoreCase(memorydata.getSubentity()) && memorydata.getEntity().equalsIgnoreCase("Utilization")){
						memvalue = memorydata.getThevalue();
						if(vmemory=="pagingusage")
						{
						break;
						}
					}else if("SwapMemory".equalsIgnoreCase(memorydata.getSubentity()) && memorydata.getEntity().equalsIgnoreCase("Utilization"))
					{
						//��������
						pagevalue=(String)memorydata.getThevalue();
						
					}
				}
			}
		
		
		NodeDTO nodeDTO = null;
		NodeUtil nodeUtil = new NodeUtil();
		nodeDTO = nodeUtil.conversionToNodeDTO(node);
		
	
		AlarmIndicatorsNode alarmIndicatorsnode=new AlarmIndicatorsNode ();
		try {
			
		//System.out.println("===================��齻�������ĸ澯====");
		for(int i = 0 ; i < listpage.size() ; i ++){
			 alarmIndicatorsnode = (AlarmIndicatorsNode)listpage.get(i);
			 //System.out.println("====***==="+alarmIndicatorsnode.getName());
			//levntpage=checkutil.checkAlarm(nodeDTO, alarmIndicatorsnode, Double.parseDouble(pagevalue), "pagingusage");
			
			double limenvalue0 = Double.parseDouble(alarmIndicatorsnode.getLimenvalue0());//һ����ֵ
			double limenvalue1 = Double.parseDouble(alarmIndicatorsnode.getLimenvalue1());//������ֵ
			double limenvalue2 = Double.parseDouble(alarmIndicatorsnode.getLimenvalue2());//������ֵ
			levntpage = checkEventLevel(Double.parseDouble(pagevalue), limenvalue0, limenvalue1, limenvalue2, alarmIndicatorsnode.getCompare());
			
		}
		}catch(Exception e)
		{}
		try {
			
			///System.out.println("===================��������ڴ����ĸ澯====");
		for(int i = 0 ; i < listmem.size() ; i ++){
			 alarmIndicatorsnode = (AlarmIndicatorsNode)listmem.get(i);
			// System.out.println("====***2==="+alarmIndicatorsnode.getName());
	    		double limenvalue0 = Double.parseDouble(alarmIndicatorsnode.getLimenvalue0());//һ����ֵ
				double limenvalue1 = Double.parseDouble(alarmIndicatorsnode.getLimenvalue1());//������ֵ
				double limenvalue2 = Double.parseDouble(alarmIndicatorsnode.getLimenvalue2());//������ֵ
				levntmem = checkEventLevel(Double.parseDouble(memvalue), limenvalue0, limenvalue1, limenvalue2, alarmIndicatorsnode.getCompare());
			
		}
		}catch(Exception e)
		{}
		
		
		String alarmTimes="";
		System.out.println("================================="+node.getIpAddress());
		System.out.println("levntmem=="+levntmem);
		System.out.println("memvalue=="+memvalue);
		
		System.out.println("levntpage=="+levntpage);
		System.out.println("pagevalue=="+pagevalue);
		System.out.println("================================");
		
		int resultleve=0;
		int eventTimes=0;
		
		if(levntmem==1 && levntpage==1)
		{//��ͨ�澯
			
			//System.out.println("==������ͨ�澯=="+levntmem);
			
			//System.out.println("levntmem>0 && levntpage==0");
			Host host = (Host) PollingEngine.getInstance().getNodeByID(node.getId());
			host.setAlarm(true);
			host.getAlarmMessage().add(alarmIndicatorsnode.getAlarm_info()+ " ��ǰֵΪ��" + levntmem +  alarmIndicatorsnode.getThreshlod_unit()+" ��ֵΪ:"+alarmIndicatorsnode.getLimenvalue0()+alarmIndicatorsnode.getThreshlod_unit());
			//������֮ǰ�ĸ澯����,������󼶱�
			
			resultleve=1;
			if(Integer.valueOf(levntmem)> host.getStatus())host.setStatus(Integer.valueOf(resultleve));
			if(Integer.valueOf(levntmem)> host.getAlarmlevel())host.setAlarmlevel(Integer.valueOf(resultleve));
			
			alarmTimes=alarmIndicatorsnode.getTime0();
			//sendAlarm(nodeDTO, alarmIndicatorsnode, memvalue, levntmem);
			//sendAlarm(nodeDTO, alarmIndicatorsnode, realVal,alamInfo, Integer.valueOf(levntmem),1);
			
		}
		
		
		if(levntmem==2 && levntpage==1)
		{//���ظ澯
			//System.out.println("levntmem>0 && levntpage==0");
			
			Host host = (Host) PollingEngine.getInstance().getNodeByID(node.getId());
			host.setAlarm(true);
			host.getAlarmMessage().add(alarmIndicatorsnode.getAlarm_info()+ " ��ǰֵΪ��" + levntmem +  alarmIndicatorsnode.getThreshlod_unit()+" ��ֵΪ:"+alarmIndicatorsnode.getLimenvalue0()+alarmIndicatorsnode.getThreshlod_unit());
			
			resultleve=1;
			alarmTimes=alarmIndicatorsnode.getTime1();
			//������֮ǰ�ĸ澯����,������󼶱�
			if(Integer.valueOf(levntmem)> host.getStatus())host.setStatus(Integer.valueOf(resultleve));
			if(Integer.valueOf(levntmem)> host.getAlarmlevel())host.setAlarmlevel(Integer.valueOf(resultleve));
			
			//sendAlarm(nodeDTO, alarmIndicatorsnode, memvalue, levntmem);
			//sendAlarm(nodeDTO, alarmIndicatorsnode, realVal,alamInfo, Integer.valueOf(levntmem),1);
			
		}
		
		
		if(levntmem==3 && levntpage==1)
		{//���ظ澯
			//System.out.println("levntmem>0 && levntpage==0");
			
			Host host = (Host) PollingEngine.getInstance().getNodeByID(node.getId());
			host.setAlarm(true);
			host.getAlarmMessage().add(alarmIndicatorsnode.getAlarm_info()+ " ��ǰֵΪ��" + levntmem +  alarmIndicatorsnode.getThreshlod_unit()+" ��ֵΪ:"+alarmIndicatorsnode.getLimenvalue0()+alarmIndicatorsnode.getThreshlod_unit());
			
			resultleve=1;
			alarmTimes=alarmIndicatorsnode.getTime1();
			//������֮ǰ�ĸ澯����,������󼶱�
			if(Integer.valueOf(levntmem)> host.getStatus())host.setStatus(Integer.valueOf(resultleve));
			if(Integer.valueOf(levntmem)> host.getAlarmlevel())host.setAlarmlevel(Integer.valueOf(resultleve));
			
			//sendAlarm(nodeDTO, alarmIndicatorsnode, memvalue, levntmem);
			//sendAlarm(nodeDTO, alarmIndicatorsnode, realVal,alamInfo, Integer.valueOf(levntmem),1);
			
		}
		
		if(levntmem==2 && levntpage==2)
		{//���ظ澯
			//System.out.println("levntmem>0 && levntpage==0");
			
			Host host = (Host) PollingEngine.getInstance().getNodeByID(node.getId());
			host.setAlarm(true);
			host.getAlarmMessage().add(alarmIndicatorsnode.getAlarm_info()+ " ��ǰֵΪ��" + levntmem +  alarmIndicatorsnode.getThreshlod_unit()+" ��ֵΪ:"+alarmIndicatorsnode.getLimenvalue0()+alarmIndicatorsnode.getThreshlod_unit());
			
			resultleve=2;
			alarmTimes=alarmIndicatorsnode.getTime1();
			//������֮ǰ�ĸ澯����,������󼶱�
			if(Integer.valueOf(levntmem)> host.getStatus())host.setStatus(Integer.valueOf(resultleve));
			if(Integer.valueOf(levntmem)> host.getAlarmlevel())host.setAlarmlevel(Integer.valueOf(resultleve));
			
			//sendAlarm(nodeDTO, alarmIndicatorsnode, memvalue, levntmem);
			//sendAlarm(nodeDTO, alarmIndicatorsnode, realVal,alamInfo, Integer.valueOf(levntmem),1);
			
		}
		
		if(levntmem==2 && levntpage==3)
		{//���ظ澯
			//System.out.println("levntmem>0 && levntpage==0");
			
			Host host = (Host) PollingEngine.getInstance().getNodeByID(node.getId());
			host.setAlarm(true);
			host.getAlarmMessage().add(alarmIndicatorsnode.getAlarm_info()+ " ��ǰֵΪ��" + levntmem +  alarmIndicatorsnode.getThreshlod_unit()+" ��ֵΪ:"+alarmIndicatorsnode.getLimenvalue0()+alarmIndicatorsnode.getThreshlod_unit());
			
			resultleve=3;
			alarmTimes=alarmIndicatorsnode.getTime1();
			//������֮ǰ�ĸ澯����,������󼶱�
			if(Integer.valueOf(levntmem)> host.getStatus())host.setStatus(Integer.valueOf(resultleve));
			if(Integer.valueOf(levntmem)> host.getAlarmlevel())host.setAlarmlevel(Integer.valueOf(resultleve));
			
			//sendAlarm(nodeDTO, alarmIndicatorsnode, memvalue, levntmem);
			//sendAlarm(nodeDTO, alarmIndicatorsnode, realVal,alamInfo, Integer.valueOf(levntmem),1);
			
		}
		
		if(levntmem==3 && levntpage==2)
		{//���ظ澯
			//System.out.println("levntmem>0 && levntpage==0");
			
			Host host = (Host) PollingEngine.getInstance().getNodeByID(node.getId());
			host.setAlarm(true);
			host.getAlarmMessage().add(alarmIndicatorsnode.getAlarm_info()+ " ��ǰֵΪ��" + levntmem +  alarmIndicatorsnode.getThreshlod_unit()+" ��ֵΪ:"+alarmIndicatorsnode.getLimenvalue0()+alarmIndicatorsnode.getThreshlod_unit());
			
			resultleve=2;
			alarmTimes=alarmIndicatorsnode.getTime1();
			//������֮ǰ�ĸ澯����,������󼶱�
			if(Integer.valueOf(levntmem)> host.getStatus())host.setStatus(Integer.valueOf(resultleve));
			if(Integer.valueOf(levntmem)> host.getAlarmlevel())host.setAlarmlevel(Integer.valueOf(resultleve));
			
			//sendAlarm(nodeDTO, alarmIndicatorsnode, memvalue, levntmem);
			//sendAlarm(nodeDTO, alarmIndicatorsnode, realVal,alamInfo, Integer.valueOf(levntmem),1);
			
		}
		
		if(levntmem==3 && levntpage==3)
		{//���ظ澯
			//System.out.println("levntmem>0 && levntpage==0");
			
			Host host = (Host) PollingEngine.getInstance().getNodeByID(node.getId());
			host.setAlarm(true);
			host.getAlarmMessage().add(alarmIndicatorsnode.getAlarm_info()+ " ��ǰֵΪ��" + levntmem +  alarmIndicatorsnode.getThreshlod_unit()+" ��ֵΪ:"+alarmIndicatorsnode.getLimenvalue0()+alarmIndicatorsnode.getThreshlod_unit());
			
			resultleve=3;
			alarmTimes=alarmIndicatorsnode.getTime1();
			//������֮ǰ�ĸ澯����,������󼶱�
			if(Integer.valueOf(levntmem)> host.getStatus())host.setStatus(Integer.valueOf(resultleve));
			if(Integer.valueOf(levntmem)> host.getAlarmlevel())host.setAlarmlevel(Integer.valueOf(resultleve));
			
			//sendAlarm(nodeDTO, alarmIndicatorsnode, memvalue, levntmem);
			//sendAlarm(nodeDTO, alarmIndicatorsnode, realVal,alamInfo, Integer.valueOf(levntmem),1);
			
		}
		
		
		
		//��ǰ�澯����
		String num = (String)AlarmResourceCenter.getInstance().getAttribute(alarmIndicatorsnode.getNodeid()+":"+alarmIndicatorsnode.getType()+":"+alarmIndicatorsnode.getName());
		
		
		eventTimes = checkEventTimes(alarmIndicatorsnode, resultleve, alarmIndicatorsnode.getNodeid()+":"+alarmIndicatorsnode.getType()+":"+alarmIndicatorsnode.getName());
		
		if(eventTimes>0)
		{
			
			// �澯��Դ�е� �¼�����
			
			
			if(num == null || "".equals(num)){
				num = "0";
			}
			
			int num_int = 0;
			int alarmTimes_int = 0;
			try {
				num_int = Integer.valueOf(num);					//��ǰ�澯����
				alarmTimes_int = Integer.valueOf(alarmTimes);	//����ĸ澯����
			} catch (RuntimeException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			//AgentalarmControlutil.DeleteAlarmlevel(node.getId()+"", alarmIndicatorsnode.getName(), alarmIndicatorsnode.getType(), "");
			//AgentalarmControlutil.PutAlarmlevel(node.getId()+"", alarmIndicatorsnode.getName(), alarmIndicatorsnode.getType(), "",eventLevel+"",true);
			
			if(num_int+1 >= alarmTimes_int){
				
				
				AgentalarmControlutil.PutAlarmlevel(node.getId()+"", alarmIndicatorsnode.getName(), alarmIndicatorsnode.getType(), "",resultleve+"",true);
				//System.out.println("=============================resultleve=="+resultleve);
				
				sendAlarm(nodeDTO, alarmIndicatorsnode, memvalue, resultleve);
				AlarmResourceCenter.getInstance().setAttribute(alarmIndicatorsnode.getNodeid()+":"+alarmIndicatorsnode.getType()+":"+alarmIndicatorsnode.getName(), num_int+1+"");
			}
			
			
			
		}else
		{
			AgentalarmControlutil.DeleteAlarmkeyname(node.getId()+"", alarmIndicatorsnode.getNodeid()+":"+alarmIndicatorsnode.getType()+":"+alarmIndicatorsnode.getName());
		}
				
	}
	
	
	
	
		
    public static void main(String [] args){
	}                           
}
