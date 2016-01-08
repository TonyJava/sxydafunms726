/*
 * Created on 2005-7-4
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.afunms.initialize;

import java.util.Calendar;
import java.util.Vector;
import java.util.List;

import org.snmp4j.CommandResponder;
import org.snmp4j.CommandResponderEvent;
import org.snmp4j.PDU;
import org.snmp4j.PDUv1;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.smi.UdpAddress;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;

import com.afunms.alarm.model.AlarmWayDetail;
import com.afunms.alarm.send.SendPageAlarm;
import com.afunms.alarm.send.SendSoundAlarm;
import com.afunms.config.model.Portconfig;
import com.afunms.config.model.TrapOIDConfig;
import com.afunms.config.dao.PortconfigDao;
import com.afunms.config.dao.TrapOIDConfigDao;
import com.afunms.common.util.*;
import com.afunms.polling.*;
import com.afunms.system.dao.TimeShareConfigDao;
import com.afunms.topology.model.*;
import com.afunms.polling.node.*;
import com.afunms.event.dao.*;
import com.afunms.event.model.AlarmInfo;
import com.afunms.event.model.EventList;
import com.afunms.event.model.Smscontent;
import com.afunms.event.model.TrapConf;

import org.apache.log4j.Logger;
import java.net.*;
import java.io.*;

/**
 * @author Administrator To change the template for this generated type comment go to Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class SnmpTrapsListener {
	private static SnmpTrapsListener instance = null;
	final static int RECEIVE_PORT = 162;
	final static int SYSLOG_PORT = 3072;
	private Snmp snmp = null;
	private Snmp syslogsnmp = null;
	private CommandResponder trapPrinter = null;
	private TransportMapping transport = null;
	private static final Logger logger = Logger.getLogger(SnmpTrapsListener.class);
	public static synchronized SnmpTrapsListener getInstance() {
		if (instance == null)
			instance = new SnmpTrapsListener();
		return instance;
	}

	public void syslogListen() {
		// try {
		// //ExecuteCollectSyslog ecs=new ExecuteCollectSyslog();
		// //ecs.start();
		// } catch (Exception e) {
		//			
		// e.printStackTrace();
		// }
	}

	public void listen() {
		try {
			UdpAddress udpAddress = new UdpAddress(RECEIVE_PORT);
			transport = new DefaultUdpTransportMapping(udpAddress);
			snmp = new Snmp(transport);
			transport.listen();
			trapPrinter = new CommandResponder() {
				public synchronized void processPdu(CommandResponderEvent e) {
					String trapType = e.getPDU().getTypeString(e.getPDU().getType()); 
					logger.info("SNMP Trap type="+trapType);
					
					if (trapType.equals("V1TRAP")) {
						PDUv1 command = (PDUv1) e.getPDU();
						if (command != null) {
							try { 
								java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("MM-dd HH:mm:ss");
								Calendar date = Calendar.getInstance();
								String time = sdf.format(date.getTime());
								String address = e.getPeerAddress().toString();

								Vector v = command.getVariableBindings();
								// ;
								String ttaddress = address.substring(0, address.indexOf("/"));
								Host host = (Host) PollingEngine.getInstance().getNodeByIP(ttaddress);
								if (host == null) {
									AlarmInfoDao alermdao = new AlarmInfoDao();
									String ipchange = alermdao.ipchange(ttaddress);
									host = (Host) PollingEngine.getInstance().getNodeByIP(ipchange);
									ttaddress = ipchange;
								}
								// ttaddress = ipchange;
								if (host == null) {
									return;
								}
								//=====================
								logger.info(address);
								logger.info("VariableBinding 1======="+((VariableBinding)v.get(1)).getOid().toString());
								//=================
								//1.3.6.1.6.3.1.1.4.1.0
								// begin 处理端口是否DOWN
								if (((VariableBinding) v.get(1)).getOid().toString().startsWith("1.3.6.1.6.3.1.1.4.1.0")) {
									// .startsWith("1.3.6.1")) {
									logger.info("VariableBinding v1 list 数量="+v.size());
									if (v.size() >= 2) {
										try {
											String upOrDown = "";// 1.3.6.1.6.3.1.1.5.4 up 1.3.6.1.6.3.1.1.5.3 down
											VariableBinding vb = (VariableBinding) v.get(1);
											// VariableBinding vb2 = (VariableBinding) v.get(2);
											// VariableBinding vb3 = (VariableBinding) v.get(3);
											// ;
											// ;
											logger.info("VariableBinding 1======="+vb.getVariable().toString());
												if (vb.getVariable().toString().startsWith("1.3.6.1.6.3.1.1.5.1")) {
												upOrDown = "ColdStart";// coldStart
												// Smscontent smscontent = new Smscontent();
												// smscontent.setMessage(time+"&"+monitorip.getEquipname()+"&"+ttaddress+"&"+upOrDown+"&level=2");
												// 发送短信
												Vector tosend = new Vector();
												// tosend.add(smscontent);
												// smsmanager.sendSmscontent(tosend);
											} else if (vb.getVariable().toString().startsWith("1.3.6.1.6.3.1.1.5.2")) {
												upOrDown = "WarmStart";// coldStart
												// Smscontent smscontent = new Smscontent();
												// smscontent.setMessage(time+"&"+monitorip.getEquipname()+"&"+ttaddress+"&"+upOrDown+"&level=2");
												// 发送短信
												Vector tosend = new Vector();
												// tosend.add(smscontent);
												// smsmanager.sendSmscontent(tosend);

											} else if (vb.getVariable().toString().startsWith("1.3.6.1.4.1.52.2501.10.2.1")) {
												upOrDown = "PS-failure";// PS-failure
												// Smscontent smscontent = new Smscontent();
												// smscontent.setMessage(time+"&"+monitorip.getEquipname()+"&"+ttaddress+"&"+upOrDown+"&level=2");
												// 发送短信
												Vector tosend = new Vector();
												// tosend.add(smscontent);
												// smsmanager.sendSmscontent(tosend);

											} else if (vb.getVariable().toString().startsWith("1.3.6.1.4.1.52.2501.10.2.6")) {
												upOrDown = "reboot";// PS-failure
												// Smscontent smscontent = new Smscontent();
												// smscontent.setMessage(time+"&"+monitorip.getEquipname()+"&"+ttaddress+"&"+upOrDown+"&level=2");
												// 发送短信
												Vector tosend = new Vector();
												// tosend.add(smscontent);
												// smsmanager.sendSmscontent(tosend);

											} else if (vb.getVariable().toString().startsWith("1.3.6.1.4.1.52.2501.10.2.2")) {
												upOrDown = "PS-recover";// PS-recover
												// Smscontent smscontent = new Smscontent();
												// smscontent.setMessage(time+"&"+monitorip.getEquipname()+"&"+ttaddress+"&"+upOrDown+"&level=2");
												// 发送短信
												Vector tosend = new Vector();
												// tosend.add(smscontent);
												// smsmanager.sendSmscontent(tosend);
											} else {
												if (vb.getVariable().toString().startsWith("1.3.6.1.6.3.1.1.5.4")) {
													upOrDown = "UP";// up
												} else if (vb.getVariable().toString().startsWith("1.3.6.1.6.3.1.1.5.3")) {
													upOrDown = "DOWN";// down
												} else if (vb.getVariable().toString().startsWith("1.3.6.1.2.1.11.0.3")) {
													upOrDown = "UP";// down
												} else if (vb.getVariable().toString().startsWith("1.3.6.1.2.1.11.0.2")) {
													upOrDown = "DOWN";// down
												} else {
													return;
												}
												vb = (VariableBinding) v.get(2);
												String portIndex = vb.getVariable().toString();
												// Smscontent smscontent = new Smscontent();
												;
												PortconfigDao configdao = new PortconfigDao();
												Portconfig portconfig = configdao.getByipandindex(ttaddress, portIndex);

												if (upOrDown != null && upOrDown.trim().length() > 0) {
													if (portconfig != null) {
														if (portconfig.getSms().intValue() == 1) {
															SmscontentDao dao = new SmscontentDao();
															dao.sendSms(time, host, portconfig, upOrDown);
															// 开始发送短信
															Smscontent smscontent = new Smscontent();
															String errorcontent = time + " " + host.getAlias() + "(" + host.getIpAddress() + ")" + portconfig.getLinkuse() + "(第"
																	+ portconfig.getPortindex() + "号)端口状态改变为" + upOrDown;
															smscontent.setLevel("2");
															smscontent.setObjid(host.getId() + "");
															smscontent.setMessage(errorcontent);
															smscontent.setRecordtime(time);
															smscontent.setSubtype("equipment");
															smscontent.setSubentity("network");
															smscontent.setIp(host.getIpAddress());
															List list = null;
															TimeShareConfigDao timeconfigdao = new TimeShareConfigDao();
															try {
																list = timeconfigdao.getTimeShareConfigByObject(smscontent.getObjid(), smscontent.getSubtype());
															} catch (Exception ex) {
																ex.printStackTrace();
															} finally {
																timeconfigdao.close();
															}
															SmscontentDao senddao = new SmscontentDao();
															try {
																senddao.sentDetailSMS(list, errorcontent);
															} catch (Exception ext) {
																ext.printStackTrace();
															}
															// 结束发送短信
														}
													}
												}

											}
										} catch (Exception ex) {

										}
									}// end for v.size()>2
								}

								if (v != null && v.size() > 1) {
									// begin 处理端口是否DOWN
									// H3C设备
									if (((VariableBinding) v.get(0)).getOid().toString().startsWith("1.3.6.1.2.1.2.2.1.1")) {
										if (v.size() == 3) {
											int flag = 0;
											try {
												String upOrDown = "";// 1.3.6.1.6.3.1.1.5.4 up 1.3.6.1.6.3.1.1.5.3 down
												VariableBinding vb = (VariableBinding) v.get(0);
												// vb[0]是portindex vb[1]是端口描述 vb[3]是端口状态改变的值
												// vb=(VariableBinding) v.get(0);
												String portIndex = vb.getVariable() + "";

												vb = (VariableBinding) v.get(2);// OperateStatus
												upOrDown = (vb.getVariable() + "").trim();
												if (upOrDown.equalsIgnoreCase("1")) {
													upOrDown = "up";
												} else if (upOrDown.equalsIgnoreCase("2")) {
													upOrDown = "down";
												} else {
													flag = 1;
												}
												if (flag == 0) {
													;
													PortconfigDao configdao = new PortconfigDao();
													Portconfig portconfig = configdao.getByipandindex(ttaddress, portIndex);

													if (upOrDown != null && upOrDown.trim().length() > 0) {
														if (portconfig != null) {
															if (portconfig.getSms().intValue() == 1) {
																SmscontentDao dao = new SmscontentDao();
																dao.sendSms(time, host, portconfig, upOrDown);

																// 开始发送短信
																Smscontent smscontent = new Smscontent();
																String errorcontent = time + " " + host.getAlias() + "(" + host.getIpAddress() + ")" + portconfig.getLinkuse() + "(第"
																		+ portconfig.getPortindex() + "号)端口状态改变为" + upOrDown;
																smscontent.setLevel("2");
																smscontent.setObjid(host.getId() + "");
																smscontent.setMessage(errorcontent);
																smscontent.setRecordtime(time);
																smscontent.setSubtype("equipment");
																smscontent.setSubentity("network");
																smscontent.setIp(host.getIpAddress());
																List list = null;
																TimeShareConfigDao timeconfigdao = new TimeShareConfigDao();
																try {
																	list = timeconfigdao.getTimeShareConfigByObject(smscontent.getObjid(), smscontent.getSubtype());
																} catch (Exception ex) {
																	ex.printStackTrace();
																} finally {
																	timeconfigdao.close();
																}
																SmscontentDao senddao = new SmscontentDao();
																try {
																	senddao.sentDetailSMS(list, errorcontent);
																} catch (Exception ext) {
																	ext.printStackTrace();
																}
																// 结束发送短信
															}
														}
													}
												}

												// }

											} catch (Exception ex) {

											}
										} // end for v.size()>2

										if (v.size() == 4) {
											try {
												String upOrDown = "";// 1.3.6.1.6.3.1.1.5.4 up 1.3.6.1.6.3.1.1.5.3 down
												VariableBinding vb = (VariableBinding) v.get(0);
												// vb[0]是portindex vb[1]是端口描述 vb[2]是端口类型 vb[3]是端口状态改变的值
												String portIndex = vb.getVariable() + "";

												vb = (VariableBinding) v.get(1);// 端口描述
												String portDesc = (vb.getVariable() + "").trim();

												vb = (VariableBinding) v.get(3);// 原因
												String locIfReason = (vb.getVariable() + "").trim();

												if (locIfReason.contains("Keepalive OK")) {
													upOrDown = "up";
												} else if (locIfReason.contains("Keepalive failed")) {
													upOrDown = "down";
												} else if (locIfReason.contains("up")) {
													upOrDown = "up";
												} else if (locIfReason.contains("down")) {
													upOrDown = "down";
												}

												;
												PortconfigDao configdao = new PortconfigDao();
												Portconfig portconfig = configdao.getByipandindex(ttaddress, portIndex);

												if (upOrDown != null && upOrDown.trim().length() > 0) {
													if (portconfig != null) {
														if (portconfig.getSms().intValue() == 1) {

															// 写事件
															SmscontentDao dao = new SmscontentDao();
															dao.sendSms(time, host, portconfig, upOrDown);

															// 开始发送短信
															Smscontent smscontent = new Smscontent();
															String errorcontent = time + " " + host.getAlias() + "(" + host.getIpAddress() + ")" + portconfig.getLinkuse() + "(第"
																	+ portconfig.getPortindex() + "号)端口状态改变为" + upOrDown;
															smscontent.setLevel("2");
															smscontent.setObjid(host.getId() + "");
															smscontent.setMessage(errorcontent);
															smscontent.setRecordtime(time);
															smscontent.setSubtype("equipment");
															smscontent.setSubentity("network");
															smscontent.setIp(host.getIpAddress());
															List list = null;
															TimeShareConfigDao timeconfigdao = new TimeShareConfigDao();
															try {
																list = timeconfigdao.getTimeShareConfigByObject(smscontent.getObjid(), smscontent.getSubtype());
															} catch (Exception ex) {
																ex.printStackTrace();
															} finally {
																timeconfigdao.close();
															}
															SmscontentDao senddao = new SmscontentDao();
															try {
																senddao.sentDetailSMS(list, errorcontent);
															} catch (Exception ext) {
																ext.printStackTrace();
															}

															// 向声音告警表里写数据
															AlarmInfo alarminfo = new AlarmInfo();
															alarminfo.setContent(errorcontent);
															alarminfo.setIpaddress(host.getIpAddress());
															alarminfo.setLevel1(new Integer(3));
															alarminfo.setRecordtime(Calendar.getInstance());
															alarminfo.setType("");
															AlarmInfoDao alarmdao = new AlarmInfoDao();
															try {
																alarmdao.save(alarminfo);
															} catch (Exception ex) {
																ex.printStackTrace();
															} finally {
																alarmdao.close();
															}
														}
													}
												}

												// }

											} catch (Exception ex) {

											}
										}
									}
									// end 开始处理端口是否DOWN

								}

							} catch (Exception iep) {

							}
						}// end command null

					}

					if (trapType.equals("TRAP")) {
						PDU command = (PDU) e.getPDU();
						if (command != null) {
							try {
								String address = e.getPeerAddress().toString();
								// ;
								// ;
								Vector v = command.getVariableBindings();
								String ttaddress = address.substring(0, address.indexOf("/"));

								//
								Host host = (Host) PollingEngine.getInstance().getNodeByIP(ttaddress);
								System.out.println(host);
								if (host == null)
									return;

								java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("MM-dd HH:mm:ss");
								Calendar date = Calendar.getInstance();

								String time = sdf.format(date.getTime());
								// Calendar pdate = sdf.parse(time);
								/*
								 * I_MonitorIpList monitormanager=new MonitoriplistManager(); I_Smscontent smsmanager=new SmscontentManager(); Monitoriplist monitorip =
								 * (Monitoriplist)monitormanager.getByIpaddress(ttaddress); if (!ttaddress.equals("10.217.252.234")&&!ttaddress.equals("10.216.1.252")){ if (monitorip == null
								 * ||monitorip.getEquipname() == null ) { I_PortRelation pManager = new PortRelationManager(); PortRelation portRelate = new PortRelation(); //if (portconfig == null){
								 * //从关联IP里查找 portRelate = pManager.getByrelateip(ttaddress); if (portRelate == null)return; String ipAddress = portRelate.getIpaddress(); monitorip =
								 * (Monitoriplist)monitormanager.getByIpaddress(ipAddress); if (monitorip == null)return; ttaddress = ipAddress; //} //return ; } }
								 */
								//=====================
								
								VariableBinding vbt = null;
								
								logger.info("v2 "+address);
								logger.info("v2 "+((VariableBinding)v.get(1)).getOid().toString());
								logger.info("---------------遍历Trap list---------------");
								for(int ii = 0;ii<v.size();ii++){
									vbt = (VariableBinding) v.get(ii);
									logger.info("index = "+ii+"    OID="+vbt.getOid()+"  value="+vbt.getVariable());
								}
								//获取要过滤掉的OID
								TrapConfDao tdao = new TrapConfDao();
								List<TrapConf> tlist = tdao.loadThrowable();
								
								vbt = (VariableBinding)v.get(1);
								//接收到的trap的oid一旦在过滤配置中  方法返回，不发短信，不入库
								for(TrapConf vo:tlist){
									logger.info("tc =  "+vo.getOid()+"  vbt="+vbt.getVariable());
									if(vbt.getVariable().toString().startsWith(vo.getOid().trim())){
										return;
									}
								}
								StringBuffer info = new StringBuffer("("+address+")"+((VariableBinding)v.get(1)).getVariable());
								if(v.size()>=2){
									for(int i =2,size = v.size();i<size;i++){
										info.append("--").append(((VariableBinding)v.get(i)).getVariable());
									}
								}
								//获取要追加描述的OID
								TrapConfDao tdao2 = new TrapConfDao();
								List<TrapConf> alist = tdao2.loadAppendDes();
								
								vbt = (VariableBinding)v.get(1);
								//接收到的trap的oid一旦在追加配置中  追加描述
								for(TrapConf vo:alist){
									logger.info("ac =  "+vo.getOid()+"  vba="+vbt.getVariable());
									if(vbt.getVariable().toString().equals(vo.getOid().trim())){
										info.append("描述:"+vo.getDes());
										break;
									}
								}
								logger.info("完整的Trap信息--------"+info);
								System.out.println(((VariableBinding) v.get(1)).getOid().toString().startsWith("11111111.3.6.1.6.3.1.1.4.1.0"));
								//=================
								// begin 处理端口是否DOWN
								//comment by hp at 2013-04-07 由于trap全部接收
								if (((VariableBinding) v.get(1)).getOid().toString().startsWith("11111111.3.6.1.6.3.1.1.4.1.0")) {
									logger.info("v2  VariableBinding list 数量======="+v.size());
									
									
									if (v.size() >= 2) {
										try {
											
											String upOrDown = "";// 1.3.6.1.6.3.1.1.5.4 up 1.3.6.1.6.3.1.1.5.3 down
											VariableBinding vb = (VariableBinding) v.get(1);
											logger.info("v2 VariableBinding 1======="+vb.getVariable().toString());
											
											String cancelStr = "";
											if (v.size() == 6) {
												VariableBinding _vb = (VariableBinding) v.get(5);
												cancelStr = _vb.getVariable() + "";
											}
											logger.info("v2 cancelStr = "+cancelStr);
											
											if (cancelStr != null && cancelStr.trim().length() > 0) {
												if (cancelStr.indexOf("Keepalive") >= 0)
													return;
											}
											// ;
											if ((vb.getVariable() + "").startsWith("1.3.6.1.6.3.1.1.5.1")) {
												upOrDown = "ColdStart";// coldStart
												// Smscontent smscontent = new Smscontent();
												// smscontent.setMessage(time+"&"+monitorip.getEquipname()+"&"+ttaddress+"&"+upOrDown+"&level=2");
												// 发送短信
												Vector tosend = new Vector();
												// tosend.add(smscontent);
												//										 			
												// smsmanager.sendSmscontent(tosend);
											} else if ((vb.getVariable() + "").startsWith("1.3.6.1.6.3.1.1.5.2")) {
												upOrDown = "WarmStart";// coldStart
												// Smscontent smscontent = new Smscontent();
												// smscontent.setMessage(time+"&"+monitorip.getEquipname()+"&"+ttaddress+"&"+upOrDown+"&level=2");
												// 发送短信
												Vector tosend = new Vector();
												// tosend.add(smscontent);
												//										 			
												// smsmanager.sendSmscontent(tosend);

											} else if ((vb.getVariable() + "").startsWith("1.3.6.1.4.1.52.2501.10.2.1")) {
												upOrDown = "PS-failure";// PS-failure
												// Smscontent smscontent = new Smscontent();
												// smscontent.setMessage(time+"&"+monitorip.getEquipname()+"&"+ttaddress+"&"+upOrDown+"&level=2");
												// 发送短信
												Vector tosend = new Vector();
												// tosend.add(smscontent);
												//										 			
												// smsmanager.sendSmscontent(tosend);

											} else if ((vb.getVariable() + "").startsWith("1.3.6.1.4.1.52.2501.10.2.6")) {
												upOrDown = "reboot";// PS-failure
												// Smscontent smscontent = new Smscontent();
												// smscontent.setMessage(time+"&"+monitorip.getEquipname()+"&"+ttaddress+"&"+upOrDown+"&level=2");
												// 发送短信
												Vector tosend = new Vector();
												// tosend.add(smscontent);
												//										 			
												// smsmanager.sendSmscontent(tosend);

											} else if ((vb.getVariable() + "").startsWith("1.3.6.1.4.1.52.2501.10.2.2")) {
												upOrDown = "PS-recover";// PS-recover
												// Smscontent smscontent = new Smscontent();
												// smscontent.setMessage(time+"&"+monitorip.getEquipname()+"&"+ttaddress+"&"+upOrDown+"&level=2");
												// 发送短信
												Vector tosend = new Vector();
												// tosend.add(smscontent);
												//										 			
												// smsmanager.sendSmscontent(tosend);
											}
											//处理厂商自己扩展的trap  1.3.6.1.6.3.1.1.5.6
											else if ((vb.getVariable() + "").startsWith("1.3.6.1.2.1.14.16.2.2")){  //ospf
												VariableBinding vbtrapent = (VariableBinding) v.get(2);
												//企业OID
												/*if(vbtrapent.getOid().toString().equals("1.3.6.1.6.3.1.1.4.3.0")){
													//华为设备
													if(vbtrapent.getVariable().toString().startsWith("1.3.6.1.4.1.2011")){
														String info = "未知错误";
														String[] infos = new String[v.size()-2];
														for(int index = 3,size = v.size();index<size;index++){
															if(((VariableBinding)v.get(index)).getOid().equals("1.3.6.1.2.1.14.16.2.2")){
																info = "OSPF邻居状态发生变化";
															}
															else if(((VariableBinding)v.get(index)).getOid().equals("1.3.6.1.2.1.15.7.1")){
																info = "BGP的状态机进入Established状态";
															}else if(((VariableBinding)v.get(index)).getOid().equals("1.3.6.1.2.1.15.7.2")){
																info = "BGP状态机的状态值从高值状态变为低值状态";
															}else if(((VariableBinding)v.get(index)).getOid().equals("1.3.6.1.2.1.68.0.1")){
																info = "路由器变成Master状态";
															}else if(((VariableBinding)v.get(index)).getOid().equals("1.3.6.1.2.1.68.0.4")){
																info = "VRRP从Master状态变为其他状态";
															}else if(((VariableBinding)v.get(index)).getOid().equals("1.3.6.1.4.1.2011.5.25.19.2.8")){
																info = "主备倒换成功";
															}else if(((VariableBinding)v.get(index)).getOid().equals("1.3.6.1.4.1.2011.5.25.19.2.9")){
																info = "主备倒换失败";
															}else if(((VariableBinding)v.get(index)).getOid().toString().startsWith("1.3.6.1.4.1.2011.5.25.129.2")){
																info = "单板、电源或风扇被拔出";
															}
															infos[index-2] = info;
														}
														logger.info("--------------处理后的trap告警------------------");
														for(String infot:infos){
															logger.info(infot);
														}
													}//Cisco设备
													else if(vbtrapent.getVariable().toString().startsWith("1.3.6.1.4.1.9")){
														
													}
												}*/
												upOrDown = "OSPF邻居状态发生变化";
											}
											else if ((vb.getVariable() + "").startsWith("1.3.6.1.2.1.15.7.1")) {
												
												upOrDown = "BGP的状态机进入Established状态";// BGP的状态机进入Established状态
											}else if ((vb.getVariable() + "").startsWith("1.3.6.1.2.1.15.7.2")) {
												
												upOrDown = "BGP状态机的状态值从高值状态变为低值状态";// BGP状态机的状态值从高值状态变为低值状态
											}else if ((vb.getVariable() + "").startsWith("1.3.6.1.2.1.68.0.1")) {
												
												upOrDown = "路由器变成Master状态";// 路由器变成Master状态
											}else if ((vb.getVariable() + "").startsWith("1.3.6.1.2.1.68.0.4")) {
												
												upOrDown = "VRRP从Master状态变为其他状态";// VRRP从Master状态变为其他状态
											}else if ((vb.getVariable() + "").startsWith("1.3.6.1.4.1.2011.5.25.19.2.8")) {
												
												upOrDown = "主备倒换成功";// 主备倒换成功
											}else if ((vb.getVariable() + "").startsWith("1.3.6.1.4.1.2011.5.25.19.2.9")) {
												
												upOrDown = "主备倒换失败";// 主备倒换失败
											}else if ((vb.getVariable() + "").startsWith("1.3.6.1.4.1.2011.5.25.129.2.1.1")) {
												for(int index = 2,size = v.size();index<size;index++){
													VariableBinding vbtemp = (VariableBinding) v.get(index);
													if(vbtemp.getVariable().toString().trim().equals("65536")){
														upOrDown = "单板、电源或风扇被拔出";	// 单板、电源或风扇被拔出
														
													}
												}
											}else {
												
												logger.info("v2  upOrDown = "+upOrDown);
												if ((vb.getVariable() + "").startsWith("1.3.6.1.6.3.1.1.5.4")) {

													upOrDown = "UP";// up
												} else if ((vb.getVariable() + "").toString().startsWith("1.3.6.1.6.3.1.1.5.3")) {
													upOrDown = "DOWN";// down
												} else if ((vb.getVariable() + "").startsWith("1.3.6.1.2.1.11.0.3")) {
													upOrDown = "UP";// down
												} else if ((vb.getVariable() + "").startsWith("1.3.6.1.2.1.11.0.2")) {
													upOrDown = "DOWN";// down
												} else {
													return;
												}
												logger.info("v2 upOrDown done= "+upOrDown);
												
												vb = (VariableBinding) v.get(2);
												String portIndex = vb.getVariable() + "";
												// Smscontent smscontent = new Smscontent();
												;
												PortconfigDao configdao = new PortconfigDao();
												Portconfig portconfig = configdao.getByipandindex(ttaddress, portIndex);
												logger.info("1111111111111111Trap设备+端口===== "+portconfig.getIpaddress()+"  "+portconfig.getPortindex()+"  "+portconfig.getSms().intValue());
												if (upOrDown != null && upOrDown.trim().length() > 0) {
													if (portconfig != null) {
														if (portconfig.getSms().intValue() == 1) {
															SmscontentDao dao = new SmscontentDao();
															// 写事件
															dao.sendSms(time, host, portconfig, upOrDown);
															logger.info("1111111111111111 trap事件入库 ");
															
															// 开始发送短信
															Smscontent smscontent = new Smscontent();
															String errorcontent = time + "(" + host.getIpAddress() + ")" + portconfig.getLinkuse() + "("
																	+ portconfig.getName() + ")端口状态改变为" + upOrDown;
															smscontent.setLevel("2");
															smscontent.setObjid(host.getId() + "");
															smscontent.setMessage(errorcontent);
															smscontent.setRecordtime(time);
															smscontent.setSubtype("equipment");
															smscontent.setSubentity("network");
															smscontent.setIp(host.getIpAddress());
															List list = null;
															TimeShareConfigDao timeconfigdao = new TimeShareConfigDao();
															try {
																list = timeconfigdao.getTimeShareConfigByObject(smscontent.getObjid(), smscontent.getSubtype());
															} catch (Exception ex) {
																ex.printStackTrace();
															} finally {
																timeconfigdao.close();
															}
															SmscontentDao senddao = new SmscontentDao();
															try {
																senddao.sentDetailSMS(list, errorcontent);
															} catch (Exception ext) {
																ext.printStackTrace();
															}
															return;
															// 结束发送短信
														}
													}
												}
													
											}
											//不是端口的up 或者down时，trap以短信发送
											if(upOrDown != null&&upOrDown.trim().length()>0){
												SmscontentDao dao = new SmscontentDao();
												// 写事件
												//dao.sendSms(time, host, upOrDown);
												logger.info("1111111111111111 trap事件入库 ");
												
												// 开始发送短信
												Smscontent smscontent = new Smscontent();
												String errorcontent = time + "(" + host.getIpAddress() + ")" + upOrDown;
												smscontent.setLevel("2");
												smscontent.setObjid(host.getId() + "");
												smscontent.setMessage(errorcontent);
												smscontent.setRecordtime(time);
												smscontent.setSubtype("equipment");
												smscontent.setSubentity("network");
												smscontent.setIp(host.getIpAddress());
												List list = null;
												TimeShareConfigDao timeconfigdao = new TimeShareConfigDao();
												try {
													list = timeconfigdao.getTimeShareConfigByObject(smscontent.getObjid(), smscontent.getSubtype());
												} catch (Exception ex) {
													ex.printStackTrace();
												} finally {
													timeconfigdao.close();
												}
												SmscontentDao senddao = new SmscontentDao();
												try {
													senddao.sentDetailSMS(list, errorcontent);
												} catch (Exception ext) {
													ext.printStackTrace();
												}
											}

										} catch (Exception ex) {

										}
									}// end for v.size()>2
								}
								// end 开始处理端口是否DOWN
								if (v != null && v.size() > 1) {
									//at 2013-04-15 by hp   Trap接收规则改变，需要全部接收，个别过滤
									// begin 处理端口是否DOWN
									if (((VariableBinding) v.get(0)).getOid().toString().startsWith("11111111111.3.6.1.2.1.2.2.1.1")) {
										if (v.size() == 4) {
											try {
												String upOrDown = "";// 1.3.6.1.6.3.1.1.5.4 up 1.3.6.1.6.3.1.1.5.3 down
												VariableBinding vb = (VariableBinding) v.get(0);
												// vb[0]是portindex vb[1]是端口描述 vb[3]是端口状态改变的值
												// vb=(VariableBinding) v.get(0);
												String portIndex = vb.getVariable().toString();

												vb = (VariableBinding) v.get(3);
												upOrDown = vb.getVariable().toString().trim();

												PortconfigDao configdao = new PortconfigDao();
												Portconfig portconfig = configdao.getByipandindex(ttaddress, portIndex);
												logger.info("22222222222Trap设备+端口===== "+portconfig.getIpaddress()+"  "+portconfig.getPortindex()+"  "+portconfig.getSms().intValue());
												
												if (upOrDown != null && upOrDown.trim().length() > 0) {
													if (portconfig != null) {
														if (portconfig.getSms() == 1) {
															// 开始发送短信
															Smscontent smscontent = new Smscontent();
															String errorcontent = time + " " + host.getAlias() + "(" + host.getIpAddress() + ")" + portconfig.getLinkuse() + "(第"
																	+ portconfig.getPortindex() + "号)端口状态改变为" + upOrDown;
															smscontent.setLevel("2");
															smscontent.setObjid(host.getId() + "");
															smscontent.setMessage(errorcontent);
															smscontent.setRecordtime(time);
															smscontent.setSubtype("equipment");
															smscontent.setSubentity("network");
															smscontent.setIp(host.getIpAddress());
															List list = null;
															TimeShareConfigDao timeconfigdao = new TimeShareConfigDao();
															try {
																list = timeconfigdao.getTimeShareConfigByObject(smscontent.getObjid(), smscontent.getSubtype());
															} catch (Exception ex) {
																ex.printStackTrace();
															} finally {
																timeconfigdao.close();
															}
															SmscontentDao senddao = new SmscontentDao();
															try {
																senddao.sentDetailSMS(list, errorcontent);
															} catch (Exception ext) {
																ext.printStackTrace();
															}
															// 结束发送短信
														}
													}
												}

												// }

											} catch (Exception ex) {

											}
										}// end for v.size()>2
									}
									// end 开始处理端口是否DOWN

								}
								
								// add by hp at 2013-04-07
								//全部trap都接受
								SmscontentDao dao = new SmscontentDao();
								// 写事件
								dao.sendSms(time, host, new Portconfig(), info.toString()); //兼容老的接口，port对象站位
								logger.info("1111111111111111 trap事件入库 ");
								
								// 开始发送短信
								/*Smscontent smscontent = new Smscontent();
								String errorcontent = time + info.toString();
								smscontent.setLevel("2");
								smscontent.setObjid(host.getId() + "");
								smscontent.setMessage(errorcontent);
								smscontent.setRecordtime(time);
								smscontent.setSubtype("equipment");
								smscontent.setSubentity("network");
								smscontent.setIp(host.getIpAddress());
								List list = null;
								TimeShareConfigDao timeconfigdao = new TimeShareConfigDao();
								try {
									list = timeconfigdao.getTimeShareConfigByObject(smscontent.getObjid(), smscontent.getSubtype());
								} catch (Exception ex) {
									ex.printStackTrace();
								} finally {
									timeconfigdao.close();
								}
								SmscontentDao senddao = new SmscontentDao();
								

								try {
									senddao.sentDetailSMS(list, errorcontent);
									
								} catch (Exception ext) {
									ext.printStackTrace();
								}*/
									//end
								String errorcontent = time + info.toString();
								EventList eventList = new EventList();
								eventList.setEventlocation(host.getId()+"");
								eventList.setContent(errorcontent);
								eventList.setLevel1(2);
								eventList.setRecordtime(date);
								eventList.setSubtype("equipment");
								eventList.setSubentity("network");
								

							} catch (Exception ex) {
							}
							
							
						}// end command null
						
					} else {
					}
				}
			};
			snmp.addCommandResponder(trapPrinter);

		} catch (Exception ex) {

			ex.printStackTrace();
		}
	}
	
	public void close() {
		try {
			if (snmp != null) {
				snmp.close();
				transport.close();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {

		SnmpTrapsListener.getInstance().listen();

	}

	/**
	 * 创建任务
	 */
	private static Runnable createTask(final Host host, final Portconfig portconfig, final String trapvalue) {

		return new Runnable() {
			public void run() {
				try {
					// SysConfigFileUtil snmputil = new SysConfigFileUtil();
					try {
						// snmputil.deleteArp(ipaddress,writecommunity,version,oid);
						// Thread.sleep(30000);
						// ;
					} catch (Exception e) {
						e.printStackTrace();
					}
				} catch (Exception exc) {

				}

				//
			}
		};
	}
	
}
