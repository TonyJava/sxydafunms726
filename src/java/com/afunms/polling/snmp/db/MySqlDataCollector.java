package com.afunms.polling.snmp.db;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import montnets.SmsDao;

import org.apache.commons.beanutils.BeanUtils;

import com.afunms.alarm.model.AlarmIndicatorsNode;
import com.afunms.alarm.util.AlarmConstant;
import com.afunms.alarm.util.AlarmIndicatorsUtil;
import com.afunms.alarm.util.AlarmResourceCenter;
import com.afunms.application.dao.DBDao;
import com.afunms.application.dao.DBTypeDao;
import com.afunms.application.model.DBTypeVo;
import com.afunms.application.model.DBVo;
import com.afunms.application.util.IpTranslation;
import com.afunms.common.util.EncryptUtil;
import com.afunms.common.util.ShareData;
import com.afunms.common.util.SysLogger;
import com.afunms.common.util.SystemConstant;
import com.afunms.event.dao.EventListDao;
import com.afunms.event.dao.SmscontentDao;
import com.afunms.event.model.EventList;
import com.afunms.event.model.Smscontent;
import com.afunms.initialize.ResourceCenter;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.node.DBNode;
import com.afunms.polling.node.Host;
import com.afunms.polling.om.Pingcollectdata;
import com.afunms.polling.snmp.LoadMySqlFile;
import com.afunms.system.util.TimeGratherConfigUtil;

public class MySqlDataCollector{

	private Hashtable sendeddata = ShareData.getSendeddata();

	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");

	public void collect_data(String dbid,Hashtable gatherHash) {
		DBDao dbdao = null;
		try{
			DBVo dbmonitorlist = new DBVo(); 
			try{
				dbdao = new DBDao();
				dbmonitorlist = (DBVo)dbdao.findByID(dbid);
			}catch(Exception e){
				
			}finally{
				dbdao.close();
			}
			//δ����
			if(dbmonitorlist.getManaged() == 0)return;
			
			//ȡ��mysql�ɼ�
			Hashtable monitorValue = new Hashtable();
			DBNode dbnode = (DBNode) PollingEngine.getInstance().getDbByID(dbmonitorlist.getId());
			//�ж��豸�Ƿ��ڲɼ�ʱ����� 0:���ڲɼ�ʱ�����,���˳�;1:��ʱ�����,���вɼ�;2:�����ڲɼ�ʱ�������,��ȫ��ɼ�
			TimeGratherConfigUtil timeconfig = new TimeGratherConfigUtil();
			int result = 0;
			result = timeconfig.isBetween(dbnode.getId()+"", "db");
			if(result == 0){
				SysLogger.info("###### "+dbnode.getIpAddress()+" ���ڲɼ�ʱ�����,����######");
				return;
			}
			
			
			dbnode.setAlarm(false);
			dbnode.setStatus(0);
			Calendar _tempCal = Calendar.getInstance();
			Date _cc = _tempCal.getTime();
			String _time = sdf.format(_cc);
			dbnode.setLastTime(_time);
			dbnode.getAlarmMessage().clear();

			String serverip = dbmonitorlist.getIpAddress();
			String username = dbmonitorlist.getUser();
			String passwords = EncryptUtil.decode(dbmonitorlist.getPassword());
			int port = Integer.parseInt(dbmonitorlist.getPort());
			String dbnames = dbmonitorlist.getDbName();
			Date d1 = new Date();
			//�жϸ����ݿ��Ƿ���������
			String[] dbs = dbnames.split(",");
			//�жϸ����ݿ��Ƿ���������
			int allFlag = 0;
			boolean mysqlIsOK = false;
			
					if (dbnode.getCollecttype() == SystemConstant.DBCOLLECTTYPE_SHELL) {
						// �ű��ɼ���ʽ
						//System.out.println("-------mysql���ýű���ʽ�ɼ�-----");
						String filename = ResourceCenter.getInstance().getSysPath() + "/linuxserver/" + serverip + ".mysql.log";
						File file = new File(filename);
						if (!file.exists()) {
							// �ļ�������,������澯
							try {
								createFileNotExistSMS(serverip);
							} catch (Exception e) {
								e.printStackTrace();
							}
							return;
						}
						SysLogger.info("#################��ʼ����Mysql:" + serverip + "�����ļ�###########");
						LoadMySqlFile loadmysql = new LoadMySqlFile(filename);
						Hashtable mysqlData = new Hashtable();
						try {
							// sqlserverdata = loadsqlserver.getSQLInital();
							mysqlData = loadmysql.getMySqlCongfig();
						} catch (Exception e) {
							e.printStackTrace();
						}
						if (mysqlData != null && mysqlData.size() > 0) {
							//System.out.println(mysqlData.containsKey("status"));
							if (mysqlData.containsKey("status")) {
								int status = Integer.parseInt((String) mysqlData.get("status"));
								if (status == 1)
									mysqlIsOK = true;
								if (!mysqlIsOK) {
									dbnode = (DBNode) PollingEngine.getInstance().getDbByID(dbmonitorlist.getId());
									dbnode.setAlarm(true);
									dbnode.setStatus(3);
									createSMS("mysql", dbmonitorlist);
									allFlag = 1;
								} else {
									// �������ϣ���������ݲɼ�
									for (int k = 0; k < dbs.length; k++) {
										String dbStr = dbs[k];
										Hashtable returnValue = new Hashtable();
										returnValue = (Hashtable) mysqlData.get(dbStr);
										if(returnValue!=null)
										   monitorValue.put(dbStr, returnValue);
									}
									if (allFlag == 1) {
										// ��һ�����ݿ��ǲ�ͨ��
										// ��Ҫ�������ݿ����ڵķ������Ƿ�����ͨ
										Host host = (Host) PollingEngine.getInstance().getNodeByIP(serverip);
										Vector ipPingData = (Vector) ShareData.getPingdata().get(serverip);
										if (ipPingData != null) {
											Pingcollectdata pingdata = (Pingcollectdata) ipPingData.get(0);
											Calendar tempCal = (Calendar) pingdata.getCollecttime();
											Date cc = tempCal.getTime();
											String time = sdf.format(cc);
											String lastTime = time;
											String pingvalue = pingdata.getThevalue();
											if (pingvalue == null || pingvalue.trim().length() == 0)
												pingvalue = "0";
											double pvalue = new Double(pingvalue);
											if (pvalue == 0) {
												// �������������Ӳ���***********************************************
												dbnode = (DBNode) PollingEngine.getInstance().getDbByID(dbmonitorlist.getId());
												dbnode.setAlarm(true);
												dbnode.setStatus(3);
												List alarmList = dbnode.getAlarmMessage();
												if (alarmList == null)
													alarmList = new ArrayList();
												dbnode.getAlarmMessage().add("���ݿ����ֹͣ");
												String sysLocation = "";
												try {
													SmscontentDao eventdao = new SmscontentDao();
													String eventdesc = "MYSQL(" + dbmonitorlist.getDbName() + " IP:"
															+ dbmonitorlist.getIpAddress() + ")" + "�����ݿ����ֹͣ";
													eventdao.createEventWithReasion("poll", dbmonitorlist.getId() + "",
															dbmonitorlist.getAlias() + "(" + dbmonitorlist.getIpAddress() + ")",
															eventdesc, 3, "db", "ping", "���ڵķ��������Ӳ���");
												} catch (Exception e) {
													e.printStackTrace();
												}
											} else {
												Pingcollectdata hostdata = null;
												hostdata = new Pingcollectdata();
												hostdata.setIpaddress(serverip);
												Calendar date = Calendar.getInstance();
												hostdata.setCollecttime(date);
												hostdata.setCategory("MYPing");
												hostdata.setEntity("Utilization");
												hostdata.setSubentity("ConnectUtilization");
												hostdata.setRestype("dynamic");
												hostdata.setUnit("%");
												hostdata.setThevalue("0");
												try {
													dbdao.createHostData(hostdata);
													// ���Ͷ���
													dbnode = (DBNode) PollingEngine.getInstance()
															.getDbByID(dbmonitorlist.getId());
													dbnode.setAlarm(true);
													List alarmList = dbnode.getAlarmMessage();
													if (alarmList == null)
														alarmList = new ArrayList();
													dbnode.getAlarmMessage().add("���ݿ����ֹͣ");
													dbnode.setStatus(3);
													createSMS("mysql", dbmonitorlist);
												} catch (Exception e) {
													e.printStackTrace();
												}
											}

										} else {
											Pingcollectdata hostdata = null;
											hostdata = new Pingcollectdata();
											hostdata.setIpaddress(serverip);
											Calendar date = Calendar.getInstance();
											hostdata.setCollecttime(date);
											hostdata.setCategory("MYPing");
											hostdata.setEntity("Utilization");
											hostdata.setSubentity("ConnectUtilization");
											hostdata.setRestype("dynamic");
											hostdata.setUnit("%");
											hostdata.setThevalue("0");
											try {
												dbdao.createHostData(hostdata);
												// ���Ͷ���
												dbnode = (DBNode) PollingEngine.getInstance().getDbByID(dbmonitorlist.getId());
												dbnode.setAlarm(true);
												List alarmList = dbnode.getAlarmMessage();
												if (alarmList == null)
													alarmList = new ArrayList();
												dbnode.getAlarmMessage().add("���ݿ����ֹͣ");
												dbnode.setStatus(3);
												createSMS("mysql", dbmonitorlist);
											} catch (Exception e) {
												e.printStackTrace();
											}
										}
									} else {
										Pingcollectdata hostdata = null;
										hostdata = new Pingcollectdata();
										hostdata.setIpaddress(serverip);
										Calendar date = Calendar.getInstance();
										hostdata.setCollecttime(date);
										hostdata.setCategory("MYPing");
										hostdata.setEntity("Utilization");
										hostdata.setSubentity("ConnectUtilization");
										hostdata.setRestype("dynamic");
										hostdata.setUnit("%");
										hostdata.setThevalue("100");
										try {
											dbdao.createHostData(hostdata);
										} catch (Exception e) {
											e.printStackTrace();
										}

									}
									if (allFlag == 0) {
										// �����ݿ��������ϣ���������ݿ����ݵĲɼ�
										/*
										 * SybaseVO sysbaseVO = new SybaseVO();
										 * try{ sysbaseVO =
										 * dbdao.getSysbaseInfo(serverip,
										 * port,username, passwords);
										 * }catch(Exception e){
										 * e.printStackTrace(); } if (sysbaseVO ==
										 * null)sysbaseVO = new SybaseVO();
										 * Hashtable retValue = new Hashtable();
										 * retValue.put("sysbaseVO", sysbaseVO);
										 * ShareData.setSysbasedata(serverip,
										 * retValue); List allspace =
										 * sysbaseVO.getDbInfo(); if(allspace !=
										 * null && allspace.size()>0){ for(int
										 * k=0;k<allspace.size();k++){ TablesVO
										 * tvo = (TablesVO)allspace.get(k);
										 * if(sybspaceconfig
										 * .containsKey(serverip
										 * +":"+tvo.getDb_name())){ //�澯�ж�
										 * Sybspaceconfig sybconfig =
										 * (Sybspaceconfig
										 * )sybspaceconfig.get(serverip
										 * +":"+tvo.getDb_name()); Integer
										 * usedperc =
										 * Integer.parseInt(tvo.getDb_usedperc
										 * ());
										 * if(usedperc>sybconfig.getAlarmvalue
										 * ()){ //������ֵ�澯 dbnode =
										 * (DBNode)PollingEngine
										 * .getInstance().getDbByID
										 * (dbmonitorlist.getId());
										 * dbnode.setAlarm(true); List alarmList =
										 * dbnode.getAlarmMessage();
										 * if(alarmList == null)alarmList = new
										 * ArrayList();
										 * dbnode.getAlarmMessage().
										 * add(sybconfig
										 * .getSpacename()+"��ռ䳬����ֵ"
										 * +sybconfig.getAlarmvalue());
										 * //dbnode.setStatus(3);
										 * createSybSpaceSMS
										 * (dbmonitorlist,sybconfig); } } } }
										 */
									}
									if (allFlag == 0) {
										monitorValue.put("runningflag", "��������");
									} else {
										monitorValue.put("runningflag", "<font color=red>����ֹͣ</font>");
									}

									if (monitorValue != null && monitorValue.size() > 0) {
										ShareData.setMySqlmonitordata(serverip, monitorValue);
									}
								}
							}
						}
						// //////////////////////////////////////////
					} else {
						//JDBC�ɼ���ʽ
						for (int k = 0; k < dbs.length; k++) {
							SysLogger.info("#### ��ʼ�ɼ�MYSQL�����ݿ�: " + dbs[k] + " ####" + serverip);
							String dbStr = dbs[k];

							try {
								mysqlIsOK = dbdao.getMySqlIsOk(serverip, username, passwords, dbStr);
							} catch (Exception e) {
								e.printStackTrace();
								mysqlIsOK = false;
							}
							if (!mysqlIsOK) {
								dbnode = (DBNode) PollingEngine.getInstance().getDbByID(dbmonitorlist.getId());
								dbnode.setAlarm(true);
								dbnode.setStatus(3);
								createSMS("mysql", dbmonitorlist);
								allFlag = 1;
							} else {
								//�������ϣ���������ݲɼ�
								Hashtable returnValue = new Hashtable();
								try {
									SysLogger.info("#### ��ʼ�ɼ�MYSQL���ݿ�start... ####");
									Date startDate = new Date();
									returnValue = dbdao.getMYSQLData(serverip, username, passwords, dbStr,gatherHash);
									SysLogger.info("#### MYSQL���ݿ�: " + dbs[k] + " ####" + serverip +"�ɼ�ʱ��Ϊ��" + (new Date().getTime()-startDate.getTime()));
//									returnValue = dbdao.getMySqlDBConfig(serverip, username, passwords, dbStr);
//									Vector vector = dbdao.getStatus(serverip, username, passwords, dbStr);
//									Vector vector1 = dbdao.getVariables(serverip, username, passwords, dbStr);
//									returnValue.put("variables", vector);
//									returnValue.put("global_status", vector1);
//									Vector dispose = dbdao.getDispose(serverip, username, passwords, dbStr);
//									Vector dispose1 = dbdao.getDispose1(serverip, username, passwords, dbStr);
//									Vector dispose2 = dbdao.getDispose2(serverip, username, passwords, dbStr);
//									Vector dispose3 = dbdao.getDispose3(serverip, username, passwords, dbStr);
//									returnValue.put("dispose", dispose);
//									returnValue.put("dispose1", dispose1);
//									returnValue.put("dispose2", dispose2);
//									returnValue.put("dispose3", dispose3);//ɨ�����
								} catch (Exception e) {
									e.printStackTrace();
								}
								monitorValue.put(dbStr, returnValue);

							}
							SysLogger.info("#### �����ɼ�MYSQL--" + dbs[k] + " -- " + serverip+" ####");
						}
						if (allFlag == 1) {
							//��һ�����ݿ��ǲ�ͨ��
							//��Ҫ�������ݿ����ڵķ������Ƿ�����ͨ
							Host host = (Host) PollingEngine.getInstance().getNodeByIP(serverip);
							Vector ipPingData = (Vector) ShareData.getPingdata().get(serverip);
							if (ipPingData != null) {
								Pingcollectdata pingdata = (Pingcollectdata) ipPingData.get(0);
								Calendar tempCal = (Calendar) pingdata.getCollecttime();
								Date cc = tempCal.getTime();
								String time = sdf.format(cc);
								String lastTime = time;
								String pingvalue = pingdata.getThevalue();
								if (pingvalue == null || pingvalue.trim().length() == 0)
									pingvalue = "0";
								double pvalue = new Double(pingvalue);
								if (pvalue == 0) {
									//�������������Ӳ���***********************************************
									dbnode = (DBNode) PollingEngine.getInstance().getDbByID(dbmonitorlist.getId());
									dbnode.setAlarm(true);
									dbnode.setStatus(3);
									List alarmList = dbnode.getAlarmMessage();
									if (alarmList == null)
										alarmList = new ArrayList();
									dbnode.getAlarmMessage().add("���ݿ����ֹͣ");
									String sysLocation = "";
									try {
										SmscontentDao eventdao = new SmscontentDao();
										String eventdesc = "MYSQL(" + dbmonitorlist.getDbName() + " IP:"
												+ dbmonitorlist.getIpAddress() + ")" + "�����ݿ����ֹͣ";
										eventdao.createEventWithReasion("poll", dbmonitorlist.getId() + "", dbmonitorlist
												.getAlias()
												+ "(" + dbmonitorlist.getIpAddress() + ")", eventdesc, 3, "db", "ping",
												"���ڵķ��������Ӳ���");
									} catch (Exception e) {
										e.printStackTrace();
									}
								} else {
									Pingcollectdata hostdata = null;
									hostdata = new Pingcollectdata();
									hostdata.setIpaddress(serverip);
									Calendar date = Calendar.getInstance();
									hostdata.setCollecttime(date);
									hostdata.setCategory("MYPing");
									hostdata.setEntity("Utilization");
									hostdata.setSubentity("ConnectUtilization");
									hostdata.setRestype("dynamic");
									hostdata.setUnit("%");
									hostdata.setThevalue("0");
									try {
										dbdao.createHostData(hostdata);
										//���Ͷ���	
										dbnode = (DBNode) PollingEngine.getInstance().getDbByID(dbmonitorlist.getId());
										dbnode.setAlarm(true);
										List alarmList = dbnode.getAlarmMessage();
										if (alarmList == null)
											alarmList = new ArrayList();
										dbnode.getAlarmMessage().add("���ݿ����ֹͣ");
										dbnode.setStatus(3);
										createSMS("mysql", dbmonitorlist);
									} catch (Exception e) {
										e.printStackTrace();
									}
								}

							} else {
								Pingcollectdata hostdata = null;
								hostdata = new Pingcollectdata();
								hostdata.setIpaddress(serverip);
								Calendar date = Calendar.getInstance();
								hostdata.setCollecttime(date);
								hostdata.setCategory("MYPing");
								hostdata.setEntity("Utilization");
								hostdata.setSubentity("ConnectUtilization");
								hostdata.setRestype("dynamic");
								hostdata.setUnit("%");
								hostdata.setThevalue("0");
								try {
									dbdao.createHostData(hostdata);
									//���Ͷ���	
									dbnode = (DBNode) PollingEngine.getInstance().getDbByID(dbmonitorlist.getId());
									dbnode.setAlarm(true);
									List alarmList = dbnode.getAlarmMessage();
									if (alarmList == null)
										alarmList = new ArrayList();
									dbnode.getAlarmMessage().add("���ݿ����ֹͣ");
									dbnode.setStatus(3);
									createSMS("mysql", dbmonitorlist);
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						} else {
							Pingcollectdata hostdata = null;
							hostdata = new Pingcollectdata();
							hostdata.setIpaddress(serverip);
							Calendar date = Calendar.getInstance();
							hostdata.setCollecttime(date);
							hostdata.setCategory("MYPing");
							hostdata.setEntity("Utilization");
							hostdata.setSubentity("ConnectUtilization");
							hostdata.setRestype("dynamic");
							hostdata.setUnit("%");
							hostdata.setThevalue("100");
							try {
								dbdao.createHostData(hostdata);
							} catch (Exception e) {
								e.printStackTrace();
							}

						}
						if (allFlag == 0) {
							monitorValue.put("runningflag", "��������");
						} else {
							monitorValue.put("runningflag", "<font color=red>����ֹͣ</font>");
						}

						if (monitorValue != null && monitorValue.size() > 0) {
							ShareData.setMySqlmonitordata(serverip, monitorValue);
						}
					}
					// �������������ݿ⣬��Ӹ澯��Ϣ   HONGLI 	
					IpTranslation tranfer = new IpTranslation();
					String hex = tranfer.formIpToHex(dbmonitorlist.getIpAddress());
					String sip = hex+":"+dbmonitorlist.getId();
					if(allFlag == 0){
//						SysLogger.info("#### �����ӵ�mysql���ݿ�"+serverip+"����Ӹ澯��Ϣ   HONGLI####");
						updateData(dbmonitorlist,ShareData.getMySqlmonitordata()); 
						//����IP��ַ���ԭ�е���Ϣ
						dbdao.clearTableData("nms_mysqlinfo", sip);
						//����ɼ���Mysql������Ϣ
						dbdao.addMysql_nmsinfo(sip, monitorValue, dbs);
					}else{
						//status 0�� ����ֹͣ  1����������
						dbdao.addOrUpdateMysql_nmsstatus(sip, "0");
					}
					SysLogger.info("#### �����ɼ�MYSQL���ݿ�"+serverip+" ####" );
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(dbdao!=null)
				dbdao.close();
			SysLogger.info("#### MYSQL����������� ####");
		}
	}

	/**
	 * ���¸澯��Ϣ    HONGLI
	 * @param vo ���ݿ�ʵ��
	 * @param collectingData ���ݿ�ʵ���еĸ���������Ϣ
	 */
	public void updateData(Object vo , Object collectingData){
//		SysLogger.info("##############updateDate  ��ʼ###########");
		DBVo mysql = (DBVo)vo;		
		Hashtable monitorValueHashtable = (Hashtable)((Hashtable)collectingData).get(mysql.getIpAddress());
//		SysLogger.info("##############HONG mysql--datahashtable--"+monitorValueHashtable);
		String[] dbs = mysql.getDbName().split(",");
		for (int k = 0; k < dbs.length; k++) {
			Hashtable mysqldHashtable = (Hashtable)monitorValueHashtable.get(dbs[k]);
//			SysLogger.info("##############HONG mysql--mysqlHashtable--"+mysqldHashtable);
			Vector val =  (Vector)mysqldHashtable.get("Val");//���ݿ���ϸ��Ϣ
			java.util.Iterator iterator = val.iterator();//����
			
			String maxUsedConnections = "";  //��������Ӧ�����������
			String threadsConnected = "";//��ǰ�򿪵����ӵ�����
			String threadsCreated = "";//���������������ӵ��߳���
			String openTables = "";//��ǰ�򿪵ı������
			while (iterator.hasNext()) {
				Hashtable tempHashtable = (Hashtable)iterator.next();
				String variableName = (String)tempHashtable.get("variable_name");
				if(("Max_used_connections").equals(variableName)){
					maxUsedConnections = (String)tempHashtable.get("value");
				}
				if(("Threads_connected").equals(variableName)){
					threadsConnected = (String)tempHashtable.get("value");				
				}
				if(("Threads_created").equals(variableName)){
					threadsCreated = (String)tempHashtable.get("value");		
				}
				if(("Open_tables").equals(variableName)){
					openTables = (String)tempHashtable.get("value");		
				}
			}
			
//		SysLogger.info("##############HONG mysql--maxUsedConnections��Threads_connected��Threads_created��Open_tables"
//				+maxUsedConnections+" ��"+threadsConnected+" ��"+threadsCreated+" ��"+openTables);
			
		/*
			Hashtable sqlserverHashtable = (Hashtable)datahashtable.get("retValue");//�õ��ɼ�sqlserver���ݿ����Ϣ
			
			Hashtable memeryHashtable = (Hashtable)sqlserverHashtable.get("pages");//�õ��������ͳ����Ϣ
			
			Hashtable locksHashtable = (Hashtable)sqlserverHashtable.get("locks");//�õ�����ϸ��Ϣ
			
			Hashtable connsHashtable = (Hashtable)sqlserverHashtable.get("conns");//�õ����ݿ�ҳ����ͳ��
			*/
			AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
			List list = alarmIndicatorsUtil.getAlarmInicatorsThresholdForNode(String.valueOf(mysql.getId()), AlarmConstant.TYPE_DB, "mysql");//��ȡ�ɼ�ָ���б�
//			SysLogger.info("##############HONG mysql-list.size--"+list.size()+"###########");
			for(int i = 0 ; i < list.size() ; i ++){
				AlarmIndicatorsNode alarmIndicatorsNode = (AlarmIndicatorsNode)list.get(i);
//				SysLogger.info("##############HONG mysql-alarmIndicatorsNode.getEnabled--"+alarmIndicatorsNode.getEnabled()+"###########");
				if("1".equals(alarmIndicatorsNode.getEnabled())){
					String indicators = alarmIndicatorsNode.getName();
					String value = "";//value ��ָʵ�����ݿ��е�ֵ���� ������������    HONGLI
//					SysLogger.info("##############HONG mysql-indicators--"+indicators+"##########");
					if("max_used_connections".equals(indicators)){
						value = maxUsedConnections;//key ��DBDao collectSQLServerMonitItemsDetail �����е�pages��keyһ��
						SysLogger.info("#######HONG mysql-maxUsedConnections-->  "+maxUsedConnections+"");
					}else if("threads_connected".equals(indicators)){
						value = threadsConnected;
					}else if("threads_created".equals(indicators)){
						value = threadsCreated;
					}else if("open_tables".equals(indicators)){
						value = openTables;
					}else {					
						continue;
					}
//					SysLogger.info("#######HONG mysql--indicator��value--"+indicators+"��"+value+"####");
					if(value == null)continue;
					if( AlarmConstant.DATATYPE_NUMBER.equals(alarmIndicatorsNode.getDatatype())){
						
						try {
							double value_int = Double.valueOf(value);//ʵ��ֵ  HONGLI
							double Limenvalue2 = Double.valueOf(alarmIndicatorsNode.getLimenvalue2());//��ֵ2 
							double Limenvalue1 = Double.valueOf(alarmIndicatorsNode.getLimenvalue1());//��ֵ1
							double Limenvalue0 = Double.valueOf(alarmIndicatorsNode.getLimenvalue0());//��ֵ0
							
//							SysLogger.info("#######HONG mysql--indicator��value_int--"+indicators+"��"+value_int+"####");
							
							String level = "";
							String alarmTimes = "";
							if(value_int > Limenvalue2){
								level = "3";
								alarmTimes = alarmIndicatorsNode.getTime2();
							}else if(value_int > Limenvalue1){
								level = "2";
								alarmTimes = alarmIndicatorsNode.getTime1();
							}else if(value_int > Limenvalue0){
								level = "1";
								alarmTimes = alarmIndicatorsNode.getTime0();
							}else{
								continue;
							}
							//?
							String num = (String)AlarmResourceCenter.getInstance().getAttribute(String.valueOf(alarmIndicatorsNode.getId()));
							
							if(num == null || "".equals(num)){
								num = "0";
							}
							
							int num_int = Integer.valueOf(num);
							
							
							int alarmTimes_int = Integer.valueOf(alarmTimes);
							
//							SysLogger.info("#######HONG mysql-indicators��num_int��level��alarmTimes--"+indicators+":"+num_int+"��"+level+"��"+alarmTimes+"##");
							
							if(num_int >= alarmTimes_int){//ʵ�ʸ澯���� >= �澯��ֵ   ������ʾ������Ϣ �� ���Ͷ���   HONGLI
								// �澯
								DBNode dbnode = (DBNode) PollingEngine.getInstance().getDbByID(mysql.getId());
								dbnode.setAlarm(true);
								List alarmList = dbnode.getAlarmMessage();
								if (alarmList == null)
									alarmList = new ArrayList();
//								SysLogger.info("########HONGLI mysql--dbnode.getAlarmMessage--start0000");
								dbnode.getAlarmMessage().add(alarmIndicatorsNode.getAlarm_info() + " ��ǰֵΪaaaaaa��" + value +  alarmIndicatorsNode.getThreshlod_unit());
								//������֮ǰ�ĸ澯����,������󼶱�
								if(Integer.valueOf(level)> dbnode.getStatus())dbnode.setStatus(Integer.valueOf(level));
//								SysLogger.info("##############updatedate0--mysql���Ͷ���############");   
								//HONGLI
								createSMS(alarmIndicatorsNode.getType(), alarmIndicatorsNode.getSubtype(), mysql.getAlias() , mysql.getId() + "", alarmIndicatorsNode.getAlarm_info() + " ��ǰֵΪ��" + value +  alarmIndicatorsNode.getThreshlod_unit() , Integer.valueOf(level) , 1 , mysql.getAlias() , mysql.getBid(),mysql.getAlias() + "(" + mysql.getAlias() + ")");
							}else{
								num_int = num_int + 1;
								AlarmResourceCenter.getInstance().setAttribute(String.valueOf(alarmIndicatorsNode.getId()), String.valueOf(num_int));
							}
							
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					
				}
				
				
			}
		}
	}
	
	/**
	 * @author HONG ���澯ָ�귢�͸澯��ʾ�͸澯����
	 * @param subtype  
	 * @param subentity 
	 * @param ipaddress
	 * @param objid
	 * @param content
	 * @param flag
	 * @param checkday
	 * @param sIndex
	 * @param bids
	 * @param sysLocation
	 */
	public void createSMS(String subtype,String subentity,String ipaddress,
			String objid,String content,int flag,int checkday,String sIndex,String bids,String sysLocation){
	 	//��������		 	
	 	//���ڴ����õ�ǰ���IP��PING��ֵ
	 	Calendar date=Calendar.getInstance();
	 	Hashtable sendeddata = ShareData.getSendeddata();
	 	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	 	//System.out.println("�˿��¼�--------------------");
	 	try{
 			if (!sendeddata.containsKey(subtype+":"+subentity+":"+ipaddress+":"+sIndex)) {
 				//�����ڣ��������ţ�������ӵ������б���
// 				SysLogger.info("######HONGLI mysql--�����ڣ��������ţ�������ӵ������б���");
	 			Smscontent smscontent = new Smscontent();
	 			String time = sdf.format(date.getTime());
	 			smscontent.setLevel(flag+"");
	 			smscontent.setObjid(objid);
	 			smscontent.setMessage(content);
	 			smscontent.setRecordtime(time);
	 			smscontent.setSubtype(subtype);
	 			smscontent.setSubentity(subentity);
	 			smscontent.setIp(ipaddress);
	 			//���Ͷ���
	 			SmscontentDao smsmanager=new SmscontentDao();
	 			smsmanager.sendURLSmscontent(smscontent);	
				sendeddata.put(subtype+":"+subentity+":"+ipaddress+":"+sIndex,date);	
				
 			} else {
 				//���ڣ�����ѷ��Ͷ����б����ж��Ƿ��Ѿ����͵���Ķ���
// 				SysLogger.info("######HONGLI mysql--���ڣ�����ѷ��Ͷ����б����ж��Ƿ��Ѿ����͵���Ķ���");
// 				SysLogger.info("#######HONGLI mysql-subtype+\":\"+subentity+\":\"+ipaddress+\":\"+sIndex---"+subtype+":"+subentity+":"+ipaddress+":"+sIndex);
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
				if(list!=null&&list.size()>0){//�����б����Ѿ����͵���Ķ���
//					SysLogger.info("######HONGLI mysql--�����б����Ѿ����͵���Ķ���");
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
		 				//����Ƿ������˵��췢������,1Ϊ���,0Ϊ�����
		 				if (subvalue/(1000*60*60*24)>=1){
			 				//����һ�죬���ٷ���Ϣ
				 			Smscontent smscontent = new Smscontent();
				 			String time = sdf.format(date.getTime());
				 			smscontent.setLevel(flag+"");
				 			smscontent.setObjid(objid);
				 			smscontent.setMessage(content);
				 			smscontent.setRecordtime(time);
				 			smscontent.setSubtype(subtype);
				 			smscontent.setSubentity(subentity);
				 			smscontent.setIp(ipaddress);//���Ͷ���
				 			SmscontentDao smsmanager=new SmscontentDao();
				 			smsmanager.sendURLSmscontent(smscontent);
							//�޸��Ѿ����͵Ķ��ż�¼	
							sendeddata.put(subtype+":"+subentity+":"+ipaddress+":"+sIndex,date);
				 		} else {
	                        //��ʼд�¼�
			 	            //String sysLocation = "";
//				 			SysLogger.info("#####HONGLI mysql--��ʼд�¼�");
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
 		 			//���Ͷ���
 		 			SmscontentDao smsmanager=new SmscontentDao();
 		 			smsmanager.sendURLSmscontent(smscontent);	
 					sendeddata.put(subtype+":"+subentity+":"+ipaddress+":"+sIndex,date);
 				}
 				
 			}	 			 			 			 			 	
	 	}catch(Exception e){
	 		e.printStackTrace();
	 	}
	 }
	
	
	/**
	 * @author HONGLI ���ɸ澯�¼�
	 * @param eventtype
	 * @param eventlocation
	 * @param bid
	 * @param content
	 * @param level1
	 * @param subtype
	 * @param subentity
	 * @param ipaddress
	 * @param objid
	 */
	private void createEvent(String eventtype,String eventlocation,String bid,String content,int level1,String subtype,String subentity,String ipaddress,String objid){
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
		EventListDao eventlistdao = new EventListDao();
		try{
			eventlistdao.save(eventlist);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			eventlistdao.close();
		}
	}
	
	public void createFileNotExistSMS(String ipaddress) {
		// ��������
		// ���ڴ����õ�ǰ���IP��PING��ֵ
		Calendar date = Calendar.getInstance();
		try {
			Host host = (Host) PollingEngine.getInstance().getNodeByIP(ipaddress);
			if (host == null)
				return;

			if (!sendeddata.containsKey(ipaddress + ":file:" + host.getId())) {
				// �����ڣ��������ţ�������ӵ������б���
				Smscontent smscontent = new Smscontent();
				String time = sdf.format(date.getTime());
				smscontent.setLevel("3");
				smscontent.setObjid(host.getId() + "");
				smscontent.setMessage(host.getAlias() + " (" + host.getIpAddress() + ")" + "����־�ļ��޷���ȷ�ϴ������ܷ�����");
				smscontent.setRecordtime(time);
				smscontent.setSubtype("host");
				smscontent.setSubentity("ftp");
				smscontent.setIp(host.getIpAddress());// ���Ͷ���
				SmscontentDao smsmanager = new SmscontentDao();
				smsmanager.sendURLSmscontent(smscontent);
				sendeddata.put(ipaddress + ":file" + host.getId(), date);
			} else {
				// ���ڣ�����ѷ��Ͷ����б����ж��Ƿ��Ѿ����͵���Ķ���
				Calendar formerdate = (Calendar) sendeddata.get(ipaddress + ":file:" + host.getId());
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

				long subvalue = current.getTime() - last.getTime();
				if (subvalue / (1000 * 60 * 60 * 24) >= 1) {
					// ����һ�죬���ٷ���Ϣ
					Smscontent smscontent = new Smscontent();
					String time = sdf.format(date.getTime());
					smscontent.setLevel("3");
					smscontent.setObjid(host.getId() + "");
					smscontent.setMessage(host.getAlias() + " (" + host.getIpAddress() + ")" + "����־�ļ��޷���ȷ�ϴ������ܷ�����");
					smscontent.setRecordtime(time);
					smscontent.setSubtype("host");
					smscontent.setSubentity("ftp");
					smscontent.setIp(host.getIpAddress());// ���Ͷ���
					SmscontentDao smsmanager = new SmscontentDao();
					smsmanager.sendURLSmscontent(smscontent);
					// �޸��Ѿ����͵Ķ��ż�¼
					sendeddata.put(ipaddress + ":file:" + host.getId(), date);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void createSMS(String db, DBVo dbmonitorlist) {
		// ��������
		// ���ڴ����õ�ǰ���IP��PING��ֵ
		Calendar date = Calendar.getInstance();
		try {
			if (!sendeddata.containsKey(db + ":" + dbmonitorlist.getIpAddress())) {
				// �����ڣ��������ţ�������ӵ������б���
				Smscontent smscontent = new Smscontent();
				String time = sdf.format(date.getTime());
				smscontent.setLevel("2");
				smscontent.setObjid(dbmonitorlist.getId() + "");
				smscontent.setMessage(db + "(" + dbmonitorlist.getDbName() + " IP:" + dbmonitorlist.getIpAddress() + ")"
						+ "�����ݿ����ֹͣ");
				smscontent.setRecordtime(time);
				smscontent.setSubtype("db");
				smscontent.setSubentity("ping");
				smscontent.setIp(dbmonitorlist.getIpAddress());
				// ���Ͷ���
				SmscontentDao smsmanager = new SmscontentDao();
				try {
					smsmanager.sendDatabaseSmscontent(smscontent);
				} catch (Exception e) {

				}
				sendeddata.put(db + ":" + dbmonitorlist.getIpAddress(), date);
			} else {
				// ���ڣ�����ѷ��Ͷ����б����ж��Ƿ��Ѿ����͵���Ķ���
				Calendar formerdate = (Calendar) sendeddata.get(db + ":" + dbmonitorlist.getIpAddress());
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

				long subvalue = current.getTime() - last.getTime();
				if (subvalue / (1000 * 60 * 60 * 24) >= 1) {
					// ����һ�죬���ٷ���Ϣ
					Smscontent smscontent = new Smscontent();
					String time = sdf.format(date.getTime());
					smscontent.setLevel("2");
					smscontent.setObjid(dbmonitorlist.getId() + "");
					smscontent.setMessage(db + "(" + dbmonitorlist.getDbName() + " IP:" + dbmonitorlist.getIpAddress() + ")"
							+ "�����ݿ����ֹͣ");
					smscontent.setRecordtime(time);
					smscontent.setSubtype("db");
					smscontent.setSubentity("ping");
					smscontent.setIp(dbmonitorlist.getIpAddress());
					// smscontent.setMessage("db&"+time+"&"+dbmonitorlist.getId()+"&"+db+"("+dbmonitorlist.getDbName()+"
					// IP:"+dbmonitorlist.getIpAddress()+")"+"�����ݿ����ֹͣ");
					// ���Ͷ���
					SmscontentDao smsmanager = new SmscontentDao();
					try {
						smsmanager.sendDatabaseSmscontent(smscontent);
					} catch (Exception e) {

					}
					// �޸��Ѿ����͵Ķ��ż�¼
					sendeddata.put(db + ":" + dbmonitorlist.getIpAddress(), date);
				} else {
					/*-------modify  zhao--------------------------*/
					String eventdesc = db + "(" + dbmonitorlist.getDbName() + " IP:" + dbmonitorlist.getIpAddress() + ")"
							+ "�����ݿ����ֹͣ";
					SmscontentDao eventdao = new SmscontentDao();
					eventdao.createEventWithReasion("poll", dbmonitorlist.getId() + "", dbmonitorlist.getAlias() + "("
							+ dbmonitorlist.getIpAddress() + ")", eventdesc, 2, "db", "ping", "���ڵķ��������Ӳ���");
					/*----------------------------------------------------*/
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
