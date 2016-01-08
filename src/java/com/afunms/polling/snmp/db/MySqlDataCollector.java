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
			//未管理
			if(dbmonitorlist.getManaged() == 0)return;
			
			//取得mysql采集
			Hashtable monitorValue = new Hashtable();
			DBNode dbnode = (DBNode) PollingEngine.getInstance().getDbByID(dbmonitorlist.getId());
			//判断设备是否在采集时间段内 0:不在采集时间段内,则退出;1:在时间段内,进行采集;2:不存在采集时间段设置,则全天采集
			TimeGratherConfigUtil timeconfig = new TimeGratherConfigUtil();
			int result = 0;
			result = timeconfig.isBetween(dbnode.getId()+"", "db");
			if(result == 0){
				SysLogger.info("###### "+dbnode.getIpAddress()+" 不在采集时间段内,跳过######");
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
			//判断该数据库是否能连接上
			String[] dbs = dbnames.split(",");
			//判断该数据库是否能连接上
			int allFlag = 0;
			boolean mysqlIsOK = false;
			
					if (dbnode.getCollecttype() == SystemConstant.DBCOLLECTTYPE_SHELL) {
						// 脚本采集方式
						//System.out.println("-------mysql采用脚本方式采集-----");
						String filename = ResourceCenter.getInstance().getSysPath() + "/linuxserver/" + serverip + ".mysql.log";
						File file = new File(filename);
						if (!file.exists()) {
							// 文件不存在,则产生告警
							try {
								createFileNotExistSMS(serverip);
							} catch (Exception e) {
								e.printStackTrace();
							}
							return;
						}
						SysLogger.info("#################开始解析Mysql:" + serverip + "数据文件###########");
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
									// 能连接上，则进行数据采集
									for (int k = 0; k < dbs.length; k++) {
										String dbStr = dbs[k];
										Hashtable returnValue = new Hashtable();
										returnValue = (Hashtable) mysqlData.get(dbStr);
										if(returnValue!=null)
										   monitorValue.put(dbStr, returnValue);
									}
									if (allFlag == 1) {
										// 有一个数据库是不通的
										// 需要增加数据库所在的服务器是否能连通
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
												// 主机服务器连接不上***********************************************
												dbnode = (DBNode) PollingEngine.getInstance().getDbByID(dbmonitorlist.getId());
												dbnode.setAlarm(true);
												dbnode.setStatus(3);
												List alarmList = dbnode.getAlarmMessage();
												if (alarmList == null)
													alarmList = new ArrayList();
												dbnode.getAlarmMessage().add("数据库服务停止");
												String sysLocation = "";
												try {
													SmscontentDao eventdao = new SmscontentDao();
													String eventdesc = "MYSQL(" + dbmonitorlist.getDbName() + " IP:"
															+ dbmonitorlist.getIpAddress() + ")" + "的数据库服务停止";
													eventdao.createEventWithReasion("poll", dbmonitorlist.getId() + "",
															dbmonitorlist.getAlias() + "(" + dbmonitorlist.getIpAddress() + ")",
															eventdesc, 3, "db", "ping", "所在的服务器连接不上");
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
													// 发送短信
													dbnode = (DBNode) PollingEngine.getInstance()
															.getDbByID(dbmonitorlist.getId());
													dbnode.setAlarm(true);
													List alarmList = dbnode.getAlarmMessage();
													if (alarmList == null)
														alarmList = new ArrayList();
													dbnode.getAlarmMessage().add("数据库服务停止");
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
												// 发送短信
												dbnode = (DBNode) PollingEngine.getInstance().getDbByID(dbmonitorlist.getId());
												dbnode.setAlarm(true);
												List alarmList = dbnode.getAlarmMessage();
												if (alarmList == null)
													alarmList = new ArrayList();
												dbnode.getAlarmMessage().add("数据库服务停止");
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
										// 若数据库能连接上，则进行数据库数据的采集
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
										 * +":"+tvo.getDb_name())){ //告警判断
										 * Sybspaceconfig sybconfig =
										 * (Sybspaceconfig
										 * )sybspaceconfig.get(serverip
										 * +":"+tvo.getDb_name()); Integer
										 * usedperc =
										 * Integer.parseInt(tvo.getDb_usedperc
										 * ());
										 * if(usedperc>sybconfig.getAlarmvalue
										 * ()){ //超过阀值告警 dbnode =
										 * (DBNode)PollingEngine
										 * .getInstance().getDbByID
										 * (dbmonitorlist.getId());
										 * dbnode.setAlarm(true); List alarmList =
										 * dbnode.getAlarmMessage();
										 * if(alarmList == null)alarmList = new
										 * ArrayList();
										 * dbnode.getAlarmMessage().
										 * add(sybconfig
										 * .getSpacename()+"表空间超过阀值"
										 * +sybconfig.getAlarmvalue());
										 * //dbnode.setStatus(3);
										 * createSybSpaceSMS
										 * (dbmonitorlist,sybconfig); } } } }
										 */
									}
									if (allFlag == 0) {
										monitorValue.put("runningflag", "正在运行");
									} else {
										monitorValue.put("runningflag", "<font color=red>服务停止</font>");
									}

									if (monitorValue != null && monitorValue.size() > 0) {
										ShareData.setMySqlmonitordata(serverip, monitorValue);
									}
								}
							}
						}
						// //////////////////////////////////////////
					} else {
						//JDBC采集方式
						for (int k = 0; k < dbs.length; k++) {
							SysLogger.info("#### 开始采集MYSQL的数据库: " + dbs[k] + " ####" + serverip);
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
								//能连接上，则进行数据采集
								Hashtable returnValue = new Hashtable();
								try {
									SysLogger.info("#### 开始采集MYSQL数据库start... ####");
									Date startDate = new Date();
									returnValue = dbdao.getMYSQLData(serverip, username, passwords, dbStr,gatherHash);
									SysLogger.info("#### MYSQL数据库: " + dbs[k] + " ####" + serverip +"采集时间为：" + (new Date().getTime()-startDate.getTime()));
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
//									returnValue.put("dispose3", dispose3);//扫描情况
								} catch (Exception e) {
									e.printStackTrace();
								}
								monitorValue.put(dbStr, returnValue);

							}
							SysLogger.info("#### 结束采集MYSQL--" + dbs[k] + " -- " + serverip+" ####");
						}
						if (allFlag == 1) {
							//有一个数据库是不通的
							//需要增加数据库所在的服务器是否能连通
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
									//主机服务器连接不上***********************************************
									dbnode = (DBNode) PollingEngine.getInstance().getDbByID(dbmonitorlist.getId());
									dbnode.setAlarm(true);
									dbnode.setStatus(3);
									List alarmList = dbnode.getAlarmMessage();
									if (alarmList == null)
										alarmList = new ArrayList();
									dbnode.getAlarmMessage().add("数据库服务停止");
									String sysLocation = "";
									try {
										SmscontentDao eventdao = new SmscontentDao();
										String eventdesc = "MYSQL(" + dbmonitorlist.getDbName() + " IP:"
												+ dbmonitorlist.getIpAddress() + ")" + "的数据库服务停止";
										eventdao.createEventWithReasion("poll", dbmonitorlist.getId() + "", dbmonitorlist
												.getAlias()
												+ "(" + dbmonitorlist.getIpAddress() + ")", eventdesc, 3, "db", "ping",
												"所在的服务器连接不上");
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
										//发送短信	
										dbnode = (DBNode) PollingEngine.getInstance().getDbByID(dbmonitorlist.getId());
										dbnode.setAlarm(true);
										List alarmList = dbnode.getAlarmMessage();
										if (alarmList == null)
											alarmList = new ArrayList();
										dbnode.getAlarmMessage().add("数据库服务停止");
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
									//发送短信	
									dbnode = (DBNode) PollingEngine.getInstance().getDbByID(dbmonitorlist.getId());
									dbnode.setAlarm(true);
									List alarmList = dbnode.getAlarmMessage();
									if (alarmList == null)
										alarmList = new ArrayList();
									dbnode.getAlarmMessage().add("数据库服务停止");
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
							monitorValue.put("runningflag", "正在运行");
						} else {
							monitorValue.put("runningflag", "<font color=red>服务停止</font>");
						}

						if (monitorValue != null && monitorValue.size() > 0) {
							ShareData.setMySqlmonitordata(serverip, monitorValue);
						}
					}
					// 若能连接上数据库，添加告警信息   HONGLI 	
					IpTranslation tranfer = new IpTranslation();
					String hex = tranfer.formIpToHex(dbmonitorlist.getIpAddress());
					String sip = hex+":"+dbmonitorlist.getId();
					if(allFlag == 0){
//						SysLogger.info("#### 可连接到mysql数据库"+serverip+"，添加告警信息   HONGLI####");
						updateData(dbmonitorlist,ShareData.getMySqlmonitordata()); 
						//根据IP地址清空原有的信息
						dbdao.clearTableData("nms_mysqlinfo", sip);
						//保存采集的Mysql数据信息
						dbdao.addMysql_nmsinfo(sip, monitorValue, dbs);
					}else{
						//status 0： 服务停止  1：正在运行
						dbdao.addOrUpdateMysql_nmsstatus(sip, "0");
					}
					SysLogger.info("#### 结束采集MYSQL数据库"+serverip+" ####" );
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(dbdao!=null)
				dbdao.close();
			SysLogger.info("#### MYSQL任务运行完毕 ####");
		}
	}

	/**
	 * 更新告警信息    HONGLI
	 * @param vo 数据库实体
	 * @param collectingData 数据库实体中的各种数据信息
	 */
	public void updateData(Object vo , Object collectingData){
//		SysLogger.info("##############updateDate  开始###########");
		DBVo mysql = (DBVo)vo;		
		Hashtable monitorValueHashtable = (Hashtable)((Hashtable)collectingData).get(mysql.getIpAddress());
//		SysLogger.info("##############HONG mysql--datahashtable--"+monitorValueHashtable);
		String[] dbs = mysql.getDbName().split(",");
		for (int k = 0; k < dbs.length; k++) {
			Hashtable mysqldHashtable = (Hashtable)monitorValueHashtable.get(dbs[k]);
//			SysLogger.info("##############HONG mysql--mysqlHashtable--"+mysqldHashtable);
			Vector val =  (Vector)mysqldHashtable.get("Val");//数据库详细信息
			java.util.Iterator iterator = val.iterator();//遍历
			
			String maxUsedConnections = "";  //服务器相应的最大连接数
			String threadsConnected = "";//当前打开的连接的数量
			String threadsCreated = "";//创建用来处理连接的线程数
			String openTables = "";//当前打开的表的数量
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
			
//		SysLogger.info("##############HONG mysql--maxUsedConnections、Threads_connected、Threads_created、Open_tables"
//				+maxUsedConnections+" 、"+threadsConnected+" 、"+threadsCreated+" 、"+openTables);
			
		/*
			Hashtable sqlserverHashtable = (Hashtable)datahashtable.get("retValue");//得到采集sqlserver数据库的信息
			
			Hashtable memeryHashtable = (Hashtable)sqlserverHashtable.get("pages");//得到缓存管理统计信息
			
			Hashtable locksHashtable = (Hashtable)sqlserverHashtable.get("locks");//得到锁明细信息
			
			Hashtable connsHashtable = (Hashtable)sqlserverHashtable.get("conns");//得到数据库页连接统计
			*/
			AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
			List list = alarmIndicatorsUtil.getAlarmInicatorsThresholdForNode(String.valueOf(mysql.getId()), AlarmConstant.TYPE_DB, "mysql");//获取采集指标列表
//			SysLogger.info("##############HONG mysql-list.size--"+list.size()+"###########");
			for(int i = 0 ; i < list.size() ; i ++){
				AlarmIndicatorsNode alarmIndicatorsNode = (AlarmIndicatorsNode)list.get(i);
//				SysLogger.info("##############HONG mysql-alarmIndicatorsNode.getEnabled--"+alarmIndicatorsNode.getEnabled()+"###########");
				if("1".equals(alarmIndicatorsNode.getEnabled())){
					String indicators = alarmIndicatorsNode.getName();
					String value = "";//value 是指实际数据库中的值，如 缓冲区命中率    HONGLI
//					SysLogger.info("##############HONG mysql-indicators--"+indicators+"##########");
					if("max_used_connections".equals(indicators)){
						value = maxUsedConnections;//key 和DBDao collectSQLServerMonitItemsDetail 方法中的pages的key一致
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
//					SysLogger.info("#######HONG mysql--indicator、value--"+indicators+"、"+value+"####");
					if(value == null)continue;
					if( AlarmConstant.DATATYPE_NUMBER.equals(alarmIndicatorsNode.getDatatype())){
						
						try {
							double value_int = Double.valueOf(value);//实际值  HONGLI
							double Limenvalue2 = Double.valueOf(alarmIndicatorsNode.getLimenvalue2());//阀值2 
							double Limenvalue1 = Double.valueOf(alarmIndicatorsNode.getLimenvalue1());//阀值1
							double Limenvalue0 = Double.valueOf(alarmIndicatorsNode.getLimenvalue0());//阀值0
							
//							SysLogger.info("#######HONG mysql--indicator、value_int--"+indicators+"、"+value_int+"####");
							
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
							
//							SysLogger.info("#######HONG mysql-indicators：num_int、level、alarmTimes--"+indicators+":"+num_int+"、"+level+"、"+alarmTimes+"##");
							
							if(num_int >= alarmTimes_int){//实际告警次数 >= 告警阀值   ，则提示报警信息 和 发送短信   HONGLI
								// 告警
								DBNode dbnode = (DBNode) PollingEngine.getInstance().getDbByID(mysql.getId());
								dbnode.setAlarm(true);
								List alarmList = dbnode.getAlarmMessage();
								if (alarmList == null)
									alarmList = new ArrayList();
//								SysLogger.info("########HONGLI mysql--dbnode.getAlarmMessage--start0000");
								dbnode.getAlarmMessage().add(alarmIndicatorsNode.getAlarm_info() + " 当前值为aaaaaa：" + value +  alarmIndicatorsNode.getThreshlod_unit());
								//若大于之前的告警级别,则用最大级别
								if(Integer.valueOf(level)> dbnode.getStatus())dbnode.setStatus(Integer.valueOf(level));
//								SysLogger.info("##############updatedate0--mysql发送短信############");   
								//HONGLI
								createSMS(alarmIndicatorsNode.getType(), alarmIndicatorsNode.getSubtype(), mysql.getAlias() , mysql.getId() + "", alarmIndicatorsNode.getAlarm_info() + " 当前值为：" + value +  alarmIndicatorsNode.getThreshlod_unit() , Integer.valueOf(level) , 1 , mysql.getAlias() , mysql.getBid(),mysql.getAlias() + "(" + mysql.getAlias() + ")");
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
	 * @author HONG 给告警指标发送告警提示和告警短信
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
	 	//建立短信		 	
	 	//从内存里获得当前这个IP的PING的值
	 	Calendar date=Calendar.getInstance();
	 	Hashtable sendeddata = ShareData.getSendeddata();
	 	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	 	//System.out.println("端口事件--------------------");
	 	try{
 			if (!sendeddata.containsKey(subtype+":"+subentity+":"+ipaddress+":"+sIndex)) {
 				//若不在，则建立短信，并且添加到发送列表里
// 				SysLogger.info("######HONGLI mysql--若不在，则建立短信，并且添加到发送列表里");
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
				
 			} else {
 				//若在，则从已发送短信列表里判断是否已经发送当天的短信
// 				SysLogger.info("######HONGLI mysql--若在，则从已发送短信列表里判断是否已经发送当天的短信");
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
				if(list!=null&&list.size()>0){//短信列表里已经发送当天的短信
//					SysLogger.info("######HONGLI mysql--短信列表里已经发送当天的短信");
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
			 	            //String sysLocation = "";
//				 			SysLogger.info("#####HONGLI mysql--开始写事件");
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
	
	
	/**
	 * @author HONGLI 生成告警事件
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
	
	public void createFileNotExistSMS(String ipaddress) {
		// 建立短信
		// 从内存里获得当前这个IP的PING的值
		Calendar date = Calendar.getInstance();
		try {
			Host host = (Host) PollingEngine.getInstance().getNodeByIP(ipaddress);
			if (host == null)
				return;

			if (!sendeddata.containsKey(ipaddress + ":file:" + host.getId())) {
				// 若不在，则建立短信，并且添加到发送列表里
				Smscontent smscontent = new Smscontent();
				String time = sdf.format(date.getTime());
				smscontent.setLevel("3");
				smscontent.setObjid(host.getId() + "");
				smscontent.setMessage(host.getAlias() + " (" + host.getIpAddress() + ")" + "的日志文件无法正确上传到网管服务器");
				smscontent.setRecordtime(time);
				smscontent.setSubtype("host");
				smscontent.setSubentity("ftp");
				smscontent.setIp(host.getIpAddress());// 发送短信
				SmscontentDao smsmanager = new SmscontentDao();
				smsmanager.sendURLSmscontent(smscontent);
				sendeddata.put(ipaddress + ":file" + host.getId(), date);
			} else {
				// 若在，则从已发送短信列表里判断是否已经发送当天的短信
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
					// 超过一天，则再发信息
					Smscontent smscontent = new Smscontent();
					String time = sdf.format(date.getTime());
					smscontent.setLevel("3");
					smscontent.setObjid(host.getId() + "");
					smscontent.setMessage(host.getAlias() + " (" + host.getIpAddress() + ")" + "的日志文件无法正确上传到网管服务器");
					smscontent.setRecordtime(time);
					smscontent.setSubtype("host");
					smscontent.setSubentity("ftp");
					smscontent.setIp(host.getIpAddress());// 发送短信
					SmscontentDao smsmanager = new SmscontentDao();
					smsmanager.sendURLSmscontent(smscontent);
					// 修改已经发送的短信记录
					sendeddata.put(ipaddress + ":file:" + host.getId(), date);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void createSMS(String db, DBVo dbmonitorlist) {
		// 建立短信
		// 从内存里获得当前这个IP的PING的值
		Calendar date = Calendar.getInstance();
		try {
			if (!sendeddata.containsKey(db + ":" + dbmonitorlist.getIpAddress())) {
				// 若不在，则建立短信，并且添加到发送列表里
				Smscontent smscontent = new Smscontent();
				String time = sdf.format(date.getTime());
				smscontent.setLevel("2");
				smscontent.setObjid(dbmonitorlist.getId() + "");
				smscontent.setMessage(db + "(" + dbmonitorlist.getDbName() + " IP:" + dbmonitorlist.getIpAddress() + ")"
						+ "的数据库服务停止");
				smscontent.setRecordtime(time);
				smscontent.setSubtype("db");
				smscontent.setSubentity("ping");
				smscontent.setIp(dbmonitorlist.getIpAddress());
				// 发送短信
				SmscontentDao smsmanager = new SmscontentDao();
				try {
					smsmanager.sendDatabaseSmscontent(smscontent);
				} catch (Exception e) {

				}
				sendeddata.put(db + ":" + dbmonitorlist.getIpAddress(), date);
			} else {
				// 若在，则从已发送短信列表里判断是否已经发送当天的短信
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
					// 超过一天，则再发信息
					Smscontent smscontent = new Smscontent();
					String time = sdf.format(date.getTime());
					smscontent.setLevel("2");
					smscontent.setObjid(dbmonitorlist.getId() + "");
					smscontent.setMessage(db + "(" + dbmonitorlist.getDbName() + " IP:" + dbmonitorlist.getIpAddress() + ")"
							+ "的数据库服务停止");
					smscontent.setRecordtime(time);
					smscontent.setSubtype("db");
					smscontent.setSubentity("ping");
					smscontent.setIp(dbmonitorlist.getIpAddress());
					// smscontent.setMessage("db&"+time+"&"+dbmonitorlist.getId()+"&"+db+"("+dbmonitorlist.getDbName()+"
					// IP:"+dbmonitorlist.getIpAddress()+")"+"的数据库服务停止");
					// 发送短信
					SmscontentDao smsmanager = new SmscontentDao();
					try {
						smsmanager.sendDatabaseSmscontent(smscontent);
					} catch (Exception e) {

					}
					// 修改已经发送的短信记录
					sendeddata.put(db + ":" + dbmonitorlist.getIpAddress(), date);
				} else {
					/*-------modify  zhao--------------------------*/
					String eventdesc = db + "(" + dbmonitorlist.getDbName() + " IP:" + dbmonitorlist.getIpAddress() + ")"
							+ "的数据库服务停止";
					SmscontentDao eventdao = new SmscontentDao();
					eventdao.createEventWithReasion("poll", dbmonitorlist.getId() + "", dbmonitorlist.getAlias() + "("
							+ dbmonitorlist.getIpAddress() + ")", eventdesc, 2, "db", "ping", "所在的服务器连接不上");
					/*----------------------------------------------------*/
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
