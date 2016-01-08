/**
 * <p>Description:operate topo xml</p>
 * <p>Company: dhcc.com</p>
 * @author miiwill
 * @project afunms
 * @date 2006-09-25
 */

package com.afunms.topology.util;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jdom.Element;
import org.jdom.Document;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import com.afunms.initialize.ResourceCenter;
import com.afunms.polling.*;
import com.afunms.polling.node.Host;
import com.afunms.topology.dao.HintNodeDao;
import com.afunms.topology.dao.ManageXmlDao;
import com.afunms.topology.dao.NodeEquipDao;
import com.afunms.topology.dao.RelationDao;
import com.afunms.topology.model.HintNode;
import com.afunms.topology.model.ManageXml;
import com.afunms.topology.model.NodeEquip;
import com.afunms.topology.model.Relation;
import com.afunms.common.util.*;
  
public class XmlOperator {  

	private final String headBytes = "<%@page contentType=\"text/html; charset=GB2312\"%>\r\n";

	private SAXBuilder builder;

	private FileInputStream fis;

	private FileOutputStream fos;

	private XMLOutputter serializer;   

	private String fullPath;

	private String xmlName;// yangjun add

	protected Document doc;

	protected Element root;

	protected Element nodes;

	protected Element lines;

	protected Element assistantLines;

	protected Element demoLines;// yangjun add ʾ����·

	private Hashtable alarmMapList; // ������к��б����豸������ͼid
    
	public XmlOperator() {
		alarmMapList = new Hashtable();
		xmlName = "";
	}
	
	public synchronized void setFile(String fileName) {
		xmlName = fileName;
		fullPath = ResourceCenter.getInstance().getSysPath()
				+ "resource/xml/" + fileName;
		//SysLogger.info(fullPath);
	}
	public synchronized void setfile(String fileName) {
		xmlName = fileName;
		fullPath = ResourceCenter.getInstance().getSysPath()
				+ "flex/data/" + fileName;
		SysLogger.info(fullPath);
	}

	/**
	 * ��������info���image��
	 */
	public synchronized void updateInfo(boolean isCustom) {
		List list = nodes.getChildren();
		Calendar date=Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date c1 = date.getTime();
		String recordtime = sdf.format(c1);
		for (int i = 0; i < list.size(); i++) {
			Element eleNode = (Element) list.get(i);
			String Id = eleNode.getChildText("id");
			System.out.println("==xml=nodeid=="+Id);
			
			//---------------���������澯-----����
			if(ShareData.getSubmapnodeHashtable().containsKey(Id))
			{
				if(((Hashtable)ShareData.getSubmapnodeHashtable().get(Id)).get("room_id")!="-1" && ShareData.getRoomAlarmHashtable().containsKey(((Hashtable)ShareData.getSubmapnodeHashtable().get(Id)).get("room_id")))
				{
					
					System.out.println("===ROOM--ROOM="+xmlName);
					alarmMapList.put(xmlName,xmlName);
				}
				
			}
			
			
			
			int id = Integer.valueOf(Id.substring(3)).intValue();
			String category = eleNode.getChild("id").getAttributeValue("category");
			if (Id.indexOf("hin") != -1) {// yangjun add if..else..
				SysLogger.info("����ʾ���豸����ʼ��Ϣ����...");
				HintNodeDao hintNodeDao = new HintNodeDao();
				HintNode vo = null;
				try{
					vo = (HintNode) hintNodeDao.findById(Id, xmlName);
				}catch(Exception e){
					
				}finally{
					hintNodeDao.close();
				}
				if(vo == null)continue;
				String strSign = vo.getAlias();
				int status = 0;
				//SysLogger.info("ʾ���豸�澯�ָ�-------------");
				
				//System.out.println("============== vo.getImage()============"+ vo.getImage());
				
				
				if(null!=vo.getImage() && vo.getImage().length()>16)
				{
				updateNode(Id, "img", vo.getImage().substring(17));
				}
				updateNode(Id, "info", "ʾ���豸<br>����ʱ�䣺"+recordtime);
		
			} else {
				//�ж����ݿ�
				com.afunms.polling.base.Node node = null;
				if(Id.indexOf("dbs") != -1){
					node = PollingEngine.getInstance().getNodeByCategory("dbs", id);
				} else {
					node = PollingEngine.getInstance().getNodeByCategory(category, id);
				}
				//

				if (node == null) {
					SysLogger.info("����һ����ɾ���Ľڵ㣬ID=" + Id);
					if (isNodeExist(Id)) {
						deleteNodeByID(Id);
					}
					continue;
				}
				// System.out.println(node.getCategory()+"....................."+node.isAlarm());
				eleNode.getChild("alias").setText(node.getAlias());
				eleNode.getChild("ip").setText(node.getIpAddress());
				// SysLogger.info("IP : " + node.getIpAddress() + " info : "
				// + node.getShowMessage());
				List alarmList = node.getAlarmMessage();
				String alarmmessage = "";
				if (alarmList != null && alarmList.size() > 0) {
					for (int k = 0; k < alarmList.size(); k++) {
						alarmmessage = alarmmessage + alarmList.get(k) + "<br>";
					}
				}
				eleNode.getChild("info").setText(node.getShowMessage() + "<br>" + alarmmessage);
				// eleNode.getChild("info").setText(node.getShowMessage());
				NodeEquip vo = null;
				NodeEquipDao nodeEquipDao = new NodeEquipDao();// yangjun add
				try {
					vo = (NodeEquip) nodeEquipDao.findByNodeAndXml(Id, xmlName);
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					nodeEquipDao.close();
				}
				if (node.getCategory() == 4) {
					
					if (vo != null) {
						if (node.isAlarm()) {// ����
							EquipService equipservice = new EquipService();
							eleNode.getChild("img").setText(
									"image/topo/"
											+ equipservice.getAlarmImage(vo
													.getEquipId()));
							//if (!alarmMapList.contains(xmlName)) {// ���澯ͼ����alarmMapList
								alarmMapList.put(xmlName,xmlName);
							//}
						} else {
							
							
							EquipService equipservice = new EquipService();
							eleNode.getChild("img").setText(
									"image/topo/"
											+ equipservice.getTopoImage(vo
													.getEquipId()));
						}
					} else {
						if (node.isAlarm()) { // ����
							eleNode
									.getChild("img")
									.setText(
											NodeHelper
													.getServerAlarmImage(((com.afunms.polling.node.Host) node)
															.getSysOid()));
							//if (!alarmMapList.contains(xmlName)) {// ���澯ͼ����alarmMapList
								alarmMapList.put(xmlName,xmlName);
							//}
						} else {
							
							
							eleNode
									.getChild("img")
									.setText(
											NodeHelper
													.getServerTopoImage(((com.afunms.polling.node.Host) node)
															.getSysOid()));
							
						}
					}

				} else {
					if (node.getDiscoverstatus() > 0) {
						// û�б����ֵ��豸
						eleNode.getChild("img").setText(
								NodeHelper.getLostImage(node.getCategory()));
					} else {
						if (vo != null) {
							if (node.isAlarm()) { // ����
								EquipService equipservice = new EquipService();
								eleNode.getChild("img").setText(
										"image/topo/"
												+ equipservice.getAlarmImage(vo
														.getEquipId()));
								//if (!alarmMapList.contains(xmlName)) {// ���澯ͼ����alarmMapList
									alarmMapList.put(xmlName,xmlName);
								//}
							} else {
								
						
								EquipService equipservice = new EquipService();
								eleNode.getChild("img").setText(
										"image/topo/"
												+ equipservice.getTopoImage(vo
														.getEquipId()));
								
							}
						} else {
							if (node.isAlarm()) { // ����
								eleNode.getChild("img").setText(
										NodeHelper.getAlarmImage(node
												.getCategory()));
								//if (!alarmMapList.contains(xmlName)) {// ���澯ͼ����alarmMapList
									alarmMapList.put(xmlName,xmlName);
								//}
							} else {
							
								
								eleNode.getChild("img").setText(
										NodeHelper.getTopoImage(node
												.getCategory()));
								
							}
						}

					}
				}
				
			}

		}

		if (isCustom) {
			writeXml();
			return;
		}
		// ----------------������·(2007.2.27)-------------------
		ManageXmlDao manageXmlDao = new ManageXmlDao();
		ManageXml mvo = null;
		try{
			mvo = (ManageXml) manageXmlDao.findByXml(xmlName);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			manageXmlDao.close();
		}
		List linkList = lines.getChildren();
		for (int i = 0; i < linkList.size(); i++) {
			Element eleLine = (Element) linkList.get(i);
			// SysLogger.info("link id === "+id);
			int id = Integer.valueOf(eleLine.getAttributeValue("id")).intValue();
			com.afunms.polling.base.LinkRoad lr = (com.afunms.polling.base.LinkRoad) PollingEngine.getInstance().getLinkByID(id);
			eleLine.getChild("lineInfo").setText(lr.getShowMessage());// yangjun  //HONGLI�����˸���xml�ļ��ڵ���Ⱥ�˳��(������ִ�У��ȸ�����·�Ƿ��и澯���ٻ���·)
			int type = lr.getType();
			String linetext = "";
			linetext = lr.getMessage(type);
			eleLine.getChild("alias").setText(linetext);//��·��ʾ��Ϣ
			eleLine.getChild("lineWidth").setText(lr.getLinkWidth(Integer.parseInt(mvo.getUtilhdx()),Integer.parseInt(mvo.getUtilhdxperc())));//��·���
			String startdescr = "##";
			String enddescr = "##";
			if(lr.getShowinterf()==1){
				startdescr = getString(lr.getStartDescr());
				enddescr = getString(lr.getEndDescr());
			}
			eleLine.getChild("start").setText(startdescr);//���ӿ�����
			eleLine.getChild("end").setText(enddescr);//�յ�ӿ�����
			// ������Ҫ��TRAP���и澯ȷ��
			if (lr == null)
				continue;
			if (lr.isAlarm()) {
				eleLine.getChild("color").setText("red");
				//if (!alarmMapList.contains(xmlName)) {// ��·�澯ʱҲ���澯ͼ����alarmMapList
					alarmMapList.put(xmlName,xmlName);
				//}
			} else {
				if (lr.getAssistant() == 0)
					eleLine.getChild("color").setText("green");
				else
					eleLine.getChild("color").setText("blue");
			}
			// add��ȡ��·��Ϣ
			eleLine.getChild("lineMenu").setText(NodeHelper.getMenuItem(lr.getId() + "", lr.getStartId() + "", lr.getEndId() + ""));
		}

		List alinkList = assistantLines.getChildren();
		for (int i = 0; i < alinkList.size(); i++) {
			Element eleLine = (Element) alinkList.get(i);
			int id = Integer.valueOf(eleLine.getAttributeValue("id"))
					.intValue();
			com.afunms.polling.base.LinkRoad lr = (com.afunms.polling.base.LinkRoad) PollingEngine.getInstance().getLinkByID(id);
			eleLine.getChild("lineInfo").setText(lr.getShowMessage());// yangjun
			int type = lr.getType();
			String linetext = "";
			linetext = lr.getMessage(type);
			eleLine.getChild("alias").setText(linetext);//��·��ʾ��Ϣ
			eleLine.getChild("lineWidth").setText(lr.getLinkWidth(Integer.parseInt(mvo.getUtilhdx()),Integer.parseInt(mvo.getUtilhdxperc())));//��·���
			String startdescr = "##";
			String enddescr = "##";
			if(lr.getShowinterf()==1){
				startdescr = getString(lr.getStartDescr());
				enddescr = getString(lr.getEndDescr());
			}
			eleLine.getChild("start").setText(startdescr);//���ӿ�����
			eleLine.getChild("end").setText(enddescr);//�յ�ӿ�����
			if (lr.isAlarm()) {
				eleLine.getChild("color").setText("red");
				//if (!alarmMapList.containsKey((xmlName)) {// ��·�澯ʱҲ���澯ͼ����alarmMapList
					alarmMapList.put(xmlName,xmlName);
				//}
			} else {
				if (lr.getAssistant() == 0)
					eleLine.getChild("color").setText("green");
				else
					eleLine.getChild("color").setText("blue");
			}
			// add��ȡ��·��Ϣ
			eleLine.getChild("lineMenu").setText(NodeHelper.getMenuItem(lr.getId() + "", lr.getStartId() + "", lr.getEndId() + ""));
		}
		writeXml();
	}

	// ���ݱ�����������ͼlist���澯״̬���ظ�ͼ�Ľڵ�
	public void alarmNode(Hashtable alarmMapList) {
		//���ݸ澯��ͼ���¸�ͼ�ڵ���Ϣ
		//SysLogger.info("���ݸ澯��ͼ���¸�ͼ�ڵ���Ϣ......" + alarmMapList.size());
		
		System.out.println("============&&&443322==============");
		
		if(null==alarmMapList)
			return ;
		
		Hashtable alarmFMapList = new Hashtable();
		Calendar date=Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date c1 = date.getTime();
		String recordtime = sdf.format(c1);
		if (alarmMapList.size() > 0) {
			
			 Set set = alarmMapList.keySet();
			Iterator itr = set.iterator();
			   while (itr.hasNext()) {
				String xmlname = (String) alarmMapList.get((String) itr.next());
				SysLogger.info("====����ͼ==" + xmlname + "==�и澯������");
				ManageXmlDao manageXmlDao = new ManageXmlDao();
				ManageXml mvo = null;
				try{
					mvo = (ManageXml) manageXmlDao.findByXml(xmlname);
				}catch(Exception e){
					e.printStackTrace();
				}finally{
					manageXmlDao.close();
				}
				if (mvo != null) {
					RelationDao relationDao = new RelationDao();
					List rvoList = null;
					try{
						rvoList = relationDao.findByMapId(mvo.getId() + "",xmlname);
					}catch(Exception e){
						e.printStackTrace();
					}finally{
						relationDao.close();
					}
					//SysLogger.info("rvoList.size()=======" + rvoList.size());
					if (rvoList != null && rvoList.size() > 0) {
						
						//SysLogger.info("rvoList.size()=======" + rvoList.size());
						for (int j = 0; j < rvoList.size(); j++) {
							Relation rvo = (Relation) rvoList.get(j);
							String xmlName = rvo.getXmlName();
							String nodeId = rvo.getNodeId();
							String category = rvo.getCategory();
//							SysLogger.info("���ֹ�����ͼ�Ľڵ�..." + nodeId + "+"
//									+ category + "+" + xmlName);
							com.afunms.polling.base.Node node = null;
							if(category!=null&&!"null".equalsIgnoreCase(category)&&!"".equalsIgnoreCase(category)){
								node = PollingEngine
								.getInstance().getNodeByCategory(
										category,
										Integer.parseInt(nodeId
												.substring(3)));
							}
							setFile(xmlName);
							init4updateXml();
							if (isNodeExist(nodeId)) {
								if (node == null) {
									SysLogger.error("��ʼ����ʾ���豸...");
									HintNodeDao hintNodeDao = new HintNodeDao();
									HintNode vo = (HintNode) hintNodeDao.findById(nodeId, xmlName);
									if(vo!=null){
										SysLogger.info(vo.getAlias()+"----"+nodeId+"---image/topo/"+vo.getImage().substring(27, vo.getImage().lastIndexOf("/"))+"/alarm.gif");
										updateNode(nodeId, "info", "<font color='red'>--������Ϣ:--</font><br>��ͼ�и澯<br>����ʱ�䣺"+recordtime);
										updateNode(nodeId, "img", "image/topo/"+vo.getImage().substring(27, vo.getImage().lastIndexOf("/"))+"/alarm.gif");
									}
								} else {
									SysLogger.error("��ʼ����ʾʵ���豸...");
									NodeEquipDao nodeEquipDao = new NodeEquipDao();// yangjun add
									NodeEquip vo = null;
									try {
										vo = (NodeEquip) nodeEquipDao
												.findByNodeAndXml(nodeId, xmlName);
									} catch (Exception e) {
										e.printStackTrace();
									} finally {
										nodeEquipDao.close();
									}			
									if (vo == null) {
//										SysLogger.info("node.getCategory()===="
//												 + node.getCategory());
										updateNode(nodeId, "img", NodeHelper
												.getAlarmImage(node.getCategory()));
									} else {
										EquipService equipservice = new EquipService();
										updateNode(nodeId, "img", "image/topo"+equipservice
												.getAlarmImage(vo.getEquipId()));
									}
									List alarmList = node.getAlarmMessage();
									String alarmmessage = "";
									if (alarmList != null && alarmList.size() > 0) {
										for (int k = 0; k < alarmList.size(); k++) {
											alarmmessage = alarmmessage
													+ alarmList.get(k) + "<br>";
										}
									}
									if (node.isAlarm()) {// �ĳɴ�alarmListȡ����
										updateNode(
												nodeId,
												"info",
												node.getShowMessage()
														+ "<br>"
														+ alarmmessage
														+ "<br>"
														+ "<font color='red'>��ͼ�и澯</font><br>");
									} else {
										//node.setAlarm(true);
										updateNode(
												nodeId,
												"info",
												node.getShowMessage()
														+ "<br>"
														+ "<font color='red'>--������Ϣ:--</font>"
														+ "<br>"
														+ "<font color='red'>��ͼ�и澯</font><br>");
									}
								}
							}
							writeXml();
							alarmFMapList.put(xmlName,xmlName);
						}
						//alarmNode(alarmFMapList); ����Ҫ�ٵ���
					}
				}
			}
		}
		SysLogger.info("���¸��ڵ���Ϣ����...");
	}

	// �޸Ľڵ���Ϣ yangjun add
	public void updateNode(String nodeId, String tag, String txt) {
		List eleNodes = nodes.getChildren();
		for (int i = 0; i < eleNodes.size(); i++) {
			Element ele = (Element) eleNodes.get(i);
			if (ele.getChildText("id").equals(nodeId)) {
				ele.getChild(tag).setText(txt);
				break;
			}
		}
	}

	/**
	 * ����xml�ļ�(��������ͼ�ϵ�"����"��ť)
	 */
	public void saveImage(String content) {
		FileOutputStream fos = null;
		OutputStreamWriter osw = null;
		try {
			fos = new FileOutputStream(fullPath);
			osw = new OutputStreamWriter(fos, "GB2312");
			osw.write("<%@page contentType=\"text/html; charset=GB2312\"%>\r\n"
					+ content);
		} catch (Exception e) {
			SysLogger.error("XmlOperator.imageToXml()", e);
		} finally {
			try {
				osw.close();
			} catch (Exception ee) {
			}
		}
	}

	/**
	 * �����ļ�
	 */
	public void writeXml() {
		try {
			Format format = Format.getCompactFormat();
			format.setEncoding("GB2312");
			format.setIndent("	");
			serializer = new XMLOutputter(format);
			fos = new FileOutputStream(fullPath);
			fos.write(headBytes.getBytes());
			serializer.output(doc, fos);
			fos.close();
		} catch (Exception e) {
			SysLogger.error("Error in XmlOperator.close()", e);
		}
	}

	/**
	 * ׼������һ���µ�xml
	 */
	public void init4updateXml() {
		try {
			fis = new FileInputStream(fullPath);
			fis.skip(headBytes.getBytes().length);
			builder = new SAXBuilder();
			doc = builder.build(fis);

			root = doc.getRootElement();
			nodes = root.getChild("nodes");
			lines = root.getChild("lines");
			assistantLines = root.getChild("assistant_lines");
			demoLines = root.getChild("demoLines");
		} catch (Exception e) {
			e.printStackTrace();
			SysLogger.error("Error in XmlOperator.init4updateXml(),file="
					+ fullPath);
		}
	}

	/**
	 * ׼������һ���µ�xml
	 */
	public void init4createXml() {
		root = new Element("root");
		nodes = new Element("nodes");
		lines = new Element("lines");
		assistantLines = new Element("assistant_lines");
		demoLines = new Element("demoLines");
	}

	/**
	 * ����һ���µ�xml
	 */
	public void createXml() {
		root.addContent(nodes);
		root.addContent(lines);
		root.addContent(assistantLines);
		root.addContent(demoLines);
		doc = new Document(root);
		writeXml();
	}

	/**
	 * ɾ��һ��xml
	 */
	public void deleteXml() {
		try {
			File delFile = new File(fullPath);
			delFile.delete();
		} catch (Exception e) {
			SysLogger.error("ɾ���ļ���������" + fullPath, e);
		}
	}

	/**
	 * ����һ���µĽڵ�(���ڷ���֮�󣬻����ֶ�����һ���ڵ�)
	 */
	public void addNode(String nodeId, int categroy, String image, String ip,
			String alias, String x, String y) {
		Element eleNode = new Element("node");
		Element eleId = new Element("id");
		Element eleImg = new Element("img");
		Element eleX = new Element("x");
		Element eleY = new Element("y");
		Element eleIp = new Element("ip");
		Element eleAlias = new Element("alias");
		Element eleInfo = new Element("info");
		Element eleMenu = new Element("menu");
		Element relationMap = new Element("relationMap");// ��������ͼ

		eleId.setText(nodeId);
		eleId.setAttribute("category", NodeHelper.getNodeEnCategory(categroy));
		if (image == null)
			eleImg.setText(NodeHelper.getTopoImage(categroy));
		else
			eleImg.setText(image);
		eleX.setText((Integer.parseInt(x)+50)+"");
		eleY.setText(y);
		SysLogger.info("id: " + nodeId + "  ip---" + ip + " ����:" + categroy
				+ " ͼƬ:" + image);
		eleIp.setText(ip);
		eleAlias.setText(alias);
		eleInfo.setText("�豸��ǩ:" + alias + "<br>IP��ַ:" + ip);
		eleMenu.setText(NodeHelper.getMenu(nodeId, ip, NodeHelper
				.getNodeEnCategory(categroy)));//yangjun xiugai
		relationMap.setText("");
		eleNode.addContent(eleId);
		eleNode.addContent(eleImg);
		eleNode.addContent(eleX);
		eleNode.addContent(eleY);
		eleNode.addContent(eleIp);
		eleNode.addContent(eleAlias);
		eleNode.addContent(eleInfo);
		eleNode.addContent(eleMenu);
		eleNode.addContent(relationMap);
		nodes.addContent(eleNode);
	}
	
	/**
	 * ����һ���µ������ڵ�(���ڷ���֮�󣬻����ֶ�����һ�������ڵ�) yangjun add
	 */
	public void addHostNode(String nodeId, int categroy, String image, String ip,
			String alias, String x, String y) {
		Element eleNode = new Element("node");
		Element eleId = new Element("id");
		Element eleImg = new Element("img");
		Element eleX = new Element("x");
		Element eleY = new Element("y");
		Element eleIp = new Element("ip");
		Element eleAlias = new Element("alias");
		Element eleInfo = new Element("info");
		Element eleMenu = new Element("menu");
		Element relationMap = new Element("relationMap");// ��������ͼ

		eleId.setText(nodeId);
		eleId.setAttribute("category", NodeHelper.getNodeEnCategory(categroy));
		if (image == null)
			eleImg.setText(NodeHelper.getTopoImage(categroy));
		else
			eleImg.setText(image);
		eleX.setText(x);
		eleY.setText(y);
		SysLogger.info("id: " + nodeId + "  ip---" + ip + " ����:" + categroy
				+ " ͼƬ:" + image);
		eleIp.setText(ip);
		eleAlias.setText(alias);
		eleInfo.setText("�豸��ǩ:" + alias + "<br>IP��ַ:" + ip);
		eleMenu.setText(NodeHelper.getHostMenu(nodeId, ip, NodeHelper
				.getNodeEnCategory(categroy)));//yangjun xiugai
		relationMap.setText("");
		eleNode.addContent(eleId);
		eleNode.addContent(eleImg);
		eleNode.addContent(eleX);
		eleNode.addContent(eleY);
		eleNode.addContent(eleIp);
		eleNode.addContent(eleAlias);
		eleNode.addContent(eleInfo);
		eleNode.addContent(eleMenu);
		eleNode.addContent(relationMap);
		nodes.addContent(eleNode);
	}

	public void addNode(com.afunms.discovery.Host host) {
		String img = null;
		if (host.getCategory() == 4){
			img = NodeHelper.getServerTopoImage(host.getSysOid());
			addHostNode("net" + String.valueOf(host.getId()), host.getCategory(), img,
					host.getIpAddress(), host.getAlias(), "30", "30");// yangjun
		} else {
			img = NodeHelper.getTopoImage(host.getCategory());
			addNode("net" + String.valueOf(host.getId()), host.getCategory(), img,
					host.getIpAddress(), host.getAlias(), "30", "30");// yangjun
		}
	}

	/**
	 * ����һ����·
	 */
	public void addLine(String lineId, String startId, String endId) {
		if(PollingEngine.getInstance().getNodeByID(Integer.parseInt(startId)) == null 
				|| PollingEngine.getInstance().getNodeByID(Integer.parseInt(endId)) == null) 
			return;
		Element line = new Element("line");
		Element a = new Element("a");
		Element b = new Element("b");
		Element color = new Element("color");
		Element dash = new Element("dash");
		Element lineWidth = new Element("lineWidth");// yangjun add
		Element alias = new Element("alias");// yangjun add
		Element startDescr = new Element("start");// yangjun add
		Element endDescr = new Element("end");// yangjun add
		Element lineInfo = new Element("lineInfo");// yangjun add
		Element lineMenu = new Element("lineMenu");// yangjun add

		line.setAttribute("id", lineId);
		a.setText(startId);
		b.setText(endId);
		color.setText("green");
		dash.setText("Solid");
		lineWidth.setText("1");
		alias.setText("#.#");
		startDescr.setText("##");
		endDescr.setText("##");
		lineInfo.setText("��·����: " + lineId + "<br>��Դ����:" + " ��·"
				+ "<br>��·��������:" + " ����ȡֵ" + "<br>��·��������:" + " ����ȡֵ"
				+ "<br>��·����������:" + " ����ȡֵ" + "<br>��·����������:" + " ����ȡֵ");
		lineMenu.setText(NodeHelper.getMenuItem(lineId, startId, endId));

		line.addContent(a);
		line.addContent(b);
		line.addContent(color);
		line.addContent(dash);
		line.addContent(lineWidth);
		line.addContent(alias);
		line.addContent(startDescr);
		line.addContent(endDescr);
		line.addContent(lineInfo);
		line.addContent(lineMenu);
		lines.addContent(line);
	}

	/**
	 * ����һ����· yangjun add
	 */
	public void addLine(String lineName, String lineId, String startId,
			String endId) {
		//SysLogger.info("###### �ؽ�����ͼ11 ###### startId:"+startId+"===endId:"+endId);
		String mstartid = startId;
		if(startId.contains("net")){
			mstartid = mstartid.substring(3);
		}
		String mendid = endId;
		if(endId.contains("net")){
			mendid = mendid.substring(3);
		}
		if(PollingEngine.getInstance().getNodeByID(Integer.parseInt(mstartid)) == null 
				|| PollingEngine.getInstance().getNodeByID(Integer.parseInt(mendid)) == null) 
			return;
		Element line = new Element("line");
		Element a = new Element("a");
		Element b = new Element("b");
		Element color = new Element("color");
		Element dash = new Element("dash");
		Element alias = new Element("alias");// yangjun add
		Element startDescr = new Element("start");// yangjun add
		Element endDescr = new Element("end");// yangjun add
		Element lineWidth = new Element("lineWidth");// yangjun add
		Element lineInfo = new Element("lineInfo");// yangjun add
		Element lineMenu = new Element("lineMenu");// yangjun add

		line.setAttribute("id", lineId);
		a.setText(startId);
		b.setText(endId);
		color.setText("green");
		dash.setText("Solid");
		alias.setText("#.#");
		startDescr.setText("##");
		endDescr.setText("##");
		lineWidth.setText("1");
		lineInfo.setText("��·����: " + lineName + "<br>��Դ����:" + " ��·"
				+ "<br>��·��������:" + " ����ȡֵ" + "<br>��·��������:" + " ����ȡֵ"
				+ "<br>��·����������:" + " ����ȡֵ" + "<br>��·����������:" + " ����ȡֵ");
		lineMenu.setText(NodeHelper.getMenuItem(lineId, startId, endId));

		line.addContent(a);
		line.addContent(b);
		line.addContent(color);
		line.addContent(dash);
		line.addContent(alias);
		line.addContent(startDescr);
		line.addContent(endDescr);
		line.addContent(lineWidth);
		line.addContent(lineInfo);
		line.addContent(lineMenu);
		lines.addContent(line);
	}

	/**
	 * ����һ��������·
	 */
	public void addAssistantLine(String lineId, String startId, String endId) {
		String mstartid = startId;
		if(startId.contains("net")){
			mstartid = mstartid.substring(3);
		}
		String mendid = endId;
		if(endId.contains("net")){
			mendid = mendid.substring(3);
		}
		if(PollingEngine.getInstance().getNodeByID(Integer.parseInt(mstartid)) == null 
				|| PollingEngine.getInstance().getNodeByID(Integer.parseInt(mendid)) == null) 
			return;
		Element line = new Element("assistant_line");
		Element a = new Element("a");
		Element b = new Element("b");
		Element color = new Element("color");
		Element dash = new Element("dash");
		Element alias = new Element("alias");// yangjun add
		Element startDescr = new Element("start");// yangjun add
		Element endDescr = new Element("end");// yangjun add
		Element lineWidth = new Element("lineWidth");// yangjun add
		Element lineInfo = new Element("lineInfo");// yangjun add
		Element lineMenu = new Element("lineMenu");// yangjun add

		line.setAttribute("id", lineId);
		a.setText(startId);
		b.setText(endId);
		color.setText("blue"); // ������·����ɫ��ʾ
		dash.setText("Solid");
		alias.setText("#.#");
		startDescr.setText("##");
		endDescr.setText("##");
		lineWidth.setText("1");
		lineInfo.setText("��·����: " + lineId + "<br>��Դ����:" + " ��·"
				+ "<br><br>��·��������:" + " ����ȡֵ" + "<br>��·��������:" + " ����ȡֵ"
				+ "<br>��·����������:" + " ����ȡֵ" + "<br>��·����������:" + " ����ȡֵ");
		lineMenu.setText(NodeHelper.getMenuItem(lineId, startId, endId));

		line.addContent(a);
		line.addContent(b);
		line.addContent(color);
		line.addContent(dash);
		line.addContent(alias);
		line.addContent(startDescr);
		line.addContent(endDescr);
		line.addContent(lineWidth);
		line.addContent(lineInfo);
		line.addContent(lineMenu);
		assistantLines.addContent(line);
	}

	/**
	 * ����һ��������· yangjun add
	 */
	public void addAssistantLine(String lineName, String lineId,
			String startId, String endId) {
		String mstartid = startId;
		if(startId.contains("net")){
			mstartid = mstartid.substring(3);
		}
		String mendid = endId;
		if(endId.contains("net")){
			mendid = mendid.substring(3);
		}
		if(PollingEngine.getInstance().getNodeByID(Integer.parseInt(mstartid)) == null 
				|| PollingEngine.getInstance().getNodeByID(Integer.parseInt(mendid)) == null) 
			return;
		Element line = new Element("assistant_line");
		Element a = new Element("a");
		Element b = new Element("b");
		Element color = new Element("color");
		Element dash = new Element("dash");
		Element alias = new Element("alias");// yangjun add
		Element startDescr = new Element("start");// yangjun add
		Element endDescr = new Element("end");// yangjun add
		Element lineWidth = new Element("lineWidth");// yangjun add
		Element lineInfo = new Element("lineInfo");// yangjun add
		Element lineMenu = new Element("lineMenu");// yangjun add

		line.setAttribute("id", lineId);
		a.setText(startId);
		b.setText(endId);
		color.setText("blue"); // ������·����ɫ��ʾ
		dash.setText("Solid");
		alias.setText("#.#");
		startDescr.setText("##");
		endDescr.setText("##");
		lineWidth.setText("1");
		lineInfo.setText("��·����: " + lineName + "<br>��Դ����:" + " ��·"
				+ "<br>��·��������:" + " ����ȡֵ" + "<br>��·��������:" + " ����ȡֵ"
				+ "<br>��·����������:" + " ����ȡֵ" + "<br>��·����������:" + " ����ȡֵ");
		lineMenu.setText(NodeHelper.getMenuItem(lineId, startId, endId));

		line.addContent(a);
		line.addContent(b);
		line.addContent(color);
		line.addContent(dash);
		line.addContent(alias);
		line.addContent(startDescr);
		line.addContent(endDescr);
		line.addContent(lineWidth);
		line.addContent(lineInfo);
		line.addContent(lineMenu);
		assistantLines.addContent(line);
	}

	/**
	 * ��xmlidɾ��һ�����
	 */
	public void deleteNodeByID(String nodeId) {
		List eleNodes = nodes.getChildren();
		int len = eleNodes.size() - 1;
		for (int i = len; i >= 0; i--) {
			Element node = (Element) eleNodes.get(i);
			if (node.getChildText("id").equals(nodeId)) {
				node.getParentElement().removeContent(node);
				deleteLineByNodeID(nodeId); // ɾ�����,��Ȼɾ��������ص�����
				break;
			}
		}
	}

	/**
	 * delete line whose startid or endid equals "nodeId"
	 */
	public void deleteLineByNodeID(String nodeId) {
		List eleLines = lines.getChildren();
		List asseleLines = assistantLines.getChildren();
		List demoeleLines = demoLines.getChildren();
		if (eleLines.size() > 0) {// ɾ��ʵ����·
			int len = eleLines.size() - 1;
			for (int i = len; i >= 0; i--) // ����ֻ���ý���������ܳ���
			{
				Element line = (Element) eleLines.get(i);
				if (line.getChildText("a").equals(nodeId))
					line.getParentElement().removeContent(line);
				else if (line.getChildText("b").equals(nodeId))
					line.getParentElement().removeContent(line);
			}
		}
		if (asseleLines.size() > 0) {// ɾ��������·
			int len = asseleLines.size() - 1;
			for (int i = len; i >= 0; i--) // ����ֻ���ý���������ܳ���
			{
				Element line = (Element) asseleLines.get(i);
				if (line.getChildText("a").equals(nodeId))
					line.getParentElement().removeContent(line);
				else if (line.getChildText("b").equals(nodeId))
					line.getParentElement().removeContent(line);
			}
		}
		if (demoeleLines.size() > 0) {// ɾ��ʾ����·
			int len = demoeleLines.size() - 1;
			for (int i = len; i >= 0; i--) // ����ֻ���ý���������ܳ���
			{
				Element line = (Element) demoeleLines.get(i);
				if (line.getChildText("a").equals(nodeId))
					line.getParentElement().removeContent(line);
				else if (line.getChildText("b").equals(nodeId))
					line.getParentElement().removeContent(line);
			}
		}

	}

	/**
	 * delete line whose id equals "id"(line id)
	 */
	public void deleteLineByID(String id) {
		List eleLines = lines.getChildren();
		int len = eleLines.size() - 1;
		for (int i = len; i >= 0; i--) // ����ֻ���ý���������ܳ���
		{
			Element line = (Element) eleLines.get(i);
			if (line.getAttributeValue("id").equals(id))
				line.getParentElement().removeContent(line);
		}
	}

	// ɾ��assistant_line yangjun add
	public void deleteAssLineByID(String id) {
		List eleLines = assistantLines.getChildren();
		int len = eleLines.size() - 1;
		for (int i = len; i >= 0; i--) // ����ֻ���ý���������ܳ���
		{
			Element assistant_line = (Element) eleLines.get(i);
			if (assistant_line.getAttributeValue("id").equals(id))
				assistant_line.getParentElement().removeContent(assistant_line);
		}
	}

	// ɾ��ʾ����· yangjun add
	public void deleteDemoLinesByID(String id) {
		List eleLines = demoLines.getChildren();
		int len = eleLines.size() - 1;
		for (int i = len; i >= 0; i--) // ����ֻ���ý���������ܳ���
		{
			Element demoLines = (Element) eleLines.get(i);
			if (demoLines.getAttributeValue("id").equals(id))
				demoLines.getParentElement().removeContent(demoLines);
		}
	}

	public boolean isNodeExist(String nodeId) {
		boolean result = false;
		List nodeList = nodes.getChildren();
		for (int i = 0; i < nodeList.size(); i++) {
			Element ele = (Element) nodeList.get(i);
			if (ele.getChildText("id").equals(nodeId)) {
				result = true;
				break;
			}
		}
		return result;
	}

	// �ж���·�Ƿ���� yangjun add
	public boolean isLinkExist(String linkId) {
		boolean result = false;
		List eleLines = lines.getChildren();
		for (int i = 0; i < eleLines.size(); i++) {
			Element ele = (Element) eleLines.get(i);
			if (ele.getAttributeValue("id").equals(linkId)) {
				result = true;
				break;
			}
		}
		return result;
	}

	// �ж���·�Ƿ���� yangjun add
	public boolean isAssLinkExist(String linkId) {
		boolean result = false;
		List asseleLines = assistantLines.getChildren();
		for (int i = 0; i < asseleLines.size(); i++) {
			Element ele = (Element) asseleLines.get(i);
			if (ele.getAttributeValue("id").equals(linkId)) {
				result = true;
				break;
			}
		}
		return result;
	}

	// �ж���·�Ƿ���� yangjun add
	public boolean isDemoLinkExist(String linkId) {
		boolean result = false;
		List eleLines = demoLines.getChildren();
		for (int i = 0; i < eleLines.size(); i++) {
			Element ele = (Element) eleLines.get(i);
			if (ele.getAttributeValue("id").equals(linkId)) {
				result = true;
				break;
			}
		}
		return result;
	}

	/**
	 * ��xmlidɾ��һ��ʾ���� yangjun add
	 */
	public void deleteNodeById(String nodeId) {
		List eleNodes = nodes.getChildren();
		int len = eleNodes.size() - 1;
		for (int i = len; i >= 0; i--) {
			Element node = (Element) eleNodes.get(i);
			if (node.getChildText("id").equals(nodeId)) {
				node.getParentElement().removeContent(node);
				deleteLineByNodeId(nodeId); // ɾ�����,��Ȼɾ��������ص�����
				break;
			}
		}
	}
	
	public List getAllNode() {
		List list = new ArrayList();
		List eleNodes = nodes.getChildren();
		for(int i=0;i<eleNodes.size();i++){
			Element node = (Element) eleNodes.get(i);
			list.add(node.getChildText("id"));
		}
		return list;
	}

	/**
	 * delete line whose startid or endid equals "nodeId" yangjun add
	 */
	public void deleteLineByNodeId(String nodeId) {
		List eleLines = demoLines.getChildren();
		int len = eleLines.size() - 1;
		for (int i = len; i >= 0; i--) // ����ֻ���ý���������ܳ���
		{
			Element line = (Element) eleLines.get(i);
			if (line.getChildText("a").equals(nodeId))
				line.getParentElement().removeContent(line);
			else if (line.getChildText("b").equals(nodeId))
				line.getParentElement().removeContent(line);
		}
	}

	public Hashtable getAlarmMapList() {
		return alarmMapList;
	}
	private String getString(String s) {
		String result = "";
		if(s==null||"".equals(s)||s.trim().length()==0){return "##";}
		result = s.substring(0, 1);
		Pattern p = Pattern.compile("[\\u4e00-\\u9fa5]+|\\d+");
		Matcher m = p.matcher(s);
		while(m.find()){
			result = result + m.group() + "/";
		}
//		System.out.println(result.substring(0, result.lastIndexOf("/")));
		return result.substring(0, result.lastIndexOf("/"));
	}
}
