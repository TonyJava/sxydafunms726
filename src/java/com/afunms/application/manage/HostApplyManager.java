package com.afunms.application.manage;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;

import com.afunms.application.dao.DBTypeDao;
import com.afunms.application.dao.HostApplyDao;
import com.afunms.application.model.ApacheConfig;
import com.afunms.application.model.CicsConfig;
import com.afunms.application.model.DBTypeVo;
import com.afunms.application.model.DBVo;
import com.afunms.application.model.DnsConfig;
import com.afunms.application.model.DominoConfig;
import com.afunms.application.model.EmailMonitorConfig;
import com.afunms.application.model.FTPConfig;
import com.afunms.application.model.HostApply;
import com.afunms.application.model.HostApplyModel;
import com.afunms.application.model.IISConfig;
import com.afunms.application.model.JBossConfig;
import com.afunms.application.model.MQConfig;
import com.afunms.application.model.Tomcat;
import com.afunms.application.model.TuxedoConfig;
import com.afunms.application.model.WasConfig;
import com.afunms.application.model.WeblogicConfig;
import com.afunms.common.base.BaseManager;
import com.afunms.common.base.ErrorMessage;
import com.afunms.common.base.ManagerInterface;
import com.afunms.common.util.SessionConstant;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.base.Node;
import com.afunms.polling.node.Host;
import com.afunms.system.model.User;
import com.afunms.topology.dao.HostNodeDao;
import com.afunms.topology.model.HostNode;

/**
 * @author HONGLI E-mail: ty564457881@163.com
 * @version ����ʱ�䣺Oct 9, 2011 1:56:12 PM
 * ��˵�� ������Ӧ������
 */
public class HostApplyManager extends BaseManager implements ManagerInterface{
	/**
	 * ������
	 */
	private static String rootPath;
	/**
	 * ���ݿ�����map
	 */
	private static Map<Integer, String> dbTypeMap = new HashMap<Integer, String>();
	
	static{
		DBTypeDao typedao = new DBTypeDao();
		try {
			List list = typedao.loadAll();
			if(list != null){
				for(int i=0; i<list.size(); i++){
					DBTypeVo dbTypeVo = (DBTypeVo)list.get(i);
					dbTypeMap.put(dbTypeVo.getId(), dbTypeVo.getDbtype());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			typedao.close();
		}
	}

	public String execute(String action) {
		rootPath = request.getContextPath();
		if("list".equals(action)){
			return list();
		}else if("serverview".equals(action)){
			return serverview();
		}else if("show".equals(action)){
			return show();
		}else if("allList".equals(action)){
			return allList();
		}else if("allShow".equals(action)){
			return allShow();
		}
		setErrorCode(ErrorMessage.ACTION_NO_FOUND);
		return null;
	}
	
	/**
	 * ������ͼ��������Ӧ�õĶ�ά�б���ʾͼ��
	 * @return
	 */
	private String serverview(){
		String nodeid = getParaValue("nodeid");
		String type = getParaValue("type");
		String ipaddress=getParaValue("ipaddress");
		String subtype = getParaValue("subtype");
		
		String freshTimeMinute = getParaValue("freshTimeMinute");
		if(freshTimeMinute == null){
			freshTimeMinute = "300";//ҳ��Ĭ��Ϊ60��ˢ��һ��
		}
		
		//����IP��ַ�õ����е�Ӧ��
//		List<HostApplyModel> hostApplyList = getHostApplyList(ipaddress);
		//�õ����еķ�����
		HostNodeDao hostNodeDao = null;
		List<HostNode> hostNodeList = null;
		try{
			hostNodeDao = new HostNodeDao();
			hostNodeList = hostNodeDao.findByCondition(" where managed=1 and category=4 " + getBidSql());
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(hostNodeDao != null){
				hostNodeDao.close();
			}
		}
		
		List<HostApplyModel> hostApplyList = null;
		HostApplyDao hostApplyDao = null;
		try{
			hostApplyDao = new HostApplyDao();
			hostApplyList = hostApplyDao.findByCondition(" ");
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(hostApplyDao != null){
				hostApplyDao.close();
			}
		}
		Hashtable<String,HostApply> hostApplyHash = getHostApplyHash(hostApplyList, hostNodeList);

		request.setAttribute("freshTimeMinute", freshTimeMinute);
		request.setAttribute("hostApplyHash", hostApplyHash);
		return "/application/hostApply/serverview.jsp";
	}
	
	/**
	 * ���еķ�����Ӧ�õ��б�
	 * @return
	 */
	public String allList(){
		String type = getParaValue("type");
		String subtype = getParaValue("subtype");
		
		//�õ����еķ�����
		HostNodeDao hostNodeDao = null;
		List<HostNode> hostNodeList = null;
		try{
			hostNodeDao = new HostNodeDao();
			hostNodeList = hostNodeDao.findByCondition(" where managed=1 and category=4 " + getBidSql());
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(hostNodeDao != null){
				hostNodeDao.close();
			}
		}
		
		//����IP��ַ�õ����е�Ӧ��
//		List<HostApplyModel> hostApplyList = getHostApplyList(ipaddress);
		List<HostApplyModel> hostApplyList = null;
		HostApplyDao hostApplyDao = null;
		try{
			hostApplyDao = new HostApplyDao();
			hostApplyList = hostApplyDao.findByCondition("");
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(hostApplyDao != null){
				hostApplyDao.close();
			}
		}
		
		Hashtable<String,HostApply> hostApplyHash = getHostApplyHash(hostApplyList, hostNodeList);
		
		request.setAttribute("type", type);
		request.setAttribute("subtype", subtype);
		request.setAttribute("hostApplyHash", hostApplyHash);
		return "/application/hostApply/allList.jsp";
	}
	
	/**
	 * ��ȡ�÷����������Ӧ�õ��б�
	 * @return
	 */
	public String list(){
		String nodeid = getParaValue("nodeid");
		String type = getParaValue("type");
		String ipaddress=getParaValue("ipaddress");
		String subtype = getParaValue("subtype");
		
		//�õ���ǰ�ķ�����
		List<HostNode> hostNodeList = new ArrayList<HostNode>();
		HostNode hostNode = new HostNode();
		Host node = (Host)PollingEngine.getInstance().getNodeByID(Integer.parseInt(nodeid));
		//����copy����  
		try {
			BeanUtils.copyProperties(hostNode,node);
		} catch (IllegalAccessException e1) {
			e1.printStackTrace();
		} catch (InvocationTargetException e1) {
			e1.printStackTrace();
		} 
		node.setIpAddress(ipaddress);
		hostNodeList.add(hostNode);
		
		//����IP��ַ�õ����е�Ӧ��
//		List<HostApplyModel> hostApplyList = getHostApplyList(ipaddress);
		List<HostApplyModel> hostApplyList = null;
		HostApplyDao hostApplyDao = null;
		try{
			hostApplyDao = new HostApplyDao();
			hostApplyList = hostApplyDao.findByCondition(" where ipaddress = '"+ipaddress+"'");
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(hostApplyDao != null){
				hostApplyDao.close();
			}
		}
		
		Hashtable<String,HostApply> hostApplyHash = getHostApplyHash(hostApplyList, hostNodeList);
		
		request.setAttribute("ipaddress", ipaddress);
		request.setAttribute("type", type);
		request.setAttribute("subtype", subtype);
		request.setAttribute("hostApplyHash", hostApplyHash);
		request.setAttribute("nodeid", nodeid);
		return "/application/hostApply/list.jsp";
	}
	
	/**
	 * ����ҳ��չʾ��Ӧ��ģ���б�
	 * @param list          ����˷�����Ӧ��ģ���б�
	 * @param hostNodeList  ϵͳ�ķ���������
	 * @return  
	 */
	public Hashtable<String,HostApply> getHostApplyHash(List<HostApplyModel> list, List<HostNode> hostNodeList){
		Hashtable<String,HostApply> retHash = new Hashtable<String,HostApply>();
		if(list != null){
			for(int i=0; i<list.size(); i++){
				HostApplyModel hostApplyModel = list.get(i);//ϵͳ��ĳһӦ��
				String ipaddress = hostApplyModel.getIpaddres();
				if(!retHash.containsKey(ipaddress)){//�����map�����в��������豸��Ӧ����Ϣ,�½�Ӧ��
					HostApply hostApply = new HostApply();
					retHash.put(ipaddress, hostApply);
				}
				HostApply hostApply = retHash.get(ipaddress);
				int applyId = hostApplyModel.getNodeid();//Ӧ�õ�id
				String status = null;
				if(hostApplyModel.getSubtype().equalsIgnoreCase("oracle")){
					Node dbNode = PollingEngine.getInstance().getDbByID(applyId);
					if (dbNode == null) {
						status = "0";
					} else {
						status = dbNode.getStatus() + "";
						hostApply.setOracleName(dbNode.getAlias());
					}
					//oracleΪ��汾������������ǰoracleӦ�õ�״̬Ϊ�澯������ߵ�״̬
					if(hostApply.getOracleStatus() == null){
						hostApply.setOracleStatus(status);
					} else if(Integer.parseInt(hostApply.getOracleStatus()) < Integer.parseInt(status)){
						hostApply.setOracleStatus(status);
					} 
					//���õ�ǰ�������ϵ�oracle�Ƿ���ʾ
					hostApply.setOracleIsShow(hostApplyModel.isShow());
				}else if(hostApplyModel.getSubtype().equalsIgnoreCase("sqlserver")){
					Node dbNode = PollingEngine.getInstance().getDbByID(applyId);
					if (dbNode == null) {
						status = "0";
					} else {
						status = dbNode.getStatus() + "";
						hostApply.setSqlserverName(dbNode.getAlias());
					}
					//sqlserverΪ��汾������������ǰsqlserverӦ�õ�״̬Ϊ�澯������ߵ�״̬
					if(hostApply.getSqlserverStatus() == null){
						hostApply.setSqlserverStatus(status);
					} else if(Integer.parseInt(hostApply.getSqlserverStatus()) < Integer.parseInt(status)){
						hostApply.setSqlserverStatus(status);
					} 
					//���õ�ǰ�������ϵ�sqlserver�Ƿ���ʾ
					hostApply.setSqlserverIsShow(hostApplyModel.isShow());
				}else if(hostApplyModel.getSubtype().equalsIgnoreCase("db2")){
					Node dbNode = PollingEngine.getInstance().getDbByID(applyId);
					if (dbNode == null) {
						status = "0";
					} else {
						status = dbNode.getStatus() + "";
						hostApply.setDb2Name(dbNode.getAlias());
					}
					//db2Ϊ��汾������������ǰdb2Ӧ�õ�״̬Ϊ�澯������ߵ�״̬
					if(hostApply.getDb2Status() == null){
						hostApply.setDb2Status(status);
					} else if(Integer.parseInt(hostApply.getDb2Status()) < Integer.parseInt(status)){
						hostApply.setDb2Status(status);
					} 
					//���õ�ǰ�������ϵ�db2�Ƿ���ʾ
					hostApply.setDb2IsShow(hostApplyModel.isShow());
				}else if(hostApplyModel.getSubtype().equalsIgnoreCase("sybase")){
					Node dbNode = PollingEngine.getInstance().getDbByID(applyId);
					if (dbNode == null) {
						status = "0";
					} else {
						status = dbNode.getStatus() + "";
						hostApply.setSybaseName(dbNode.getAlias());
					}
					//sybaseΪ��汾������������ǰsybaseӦ�õ�״̬Ϊ�澯������ߵ�״̬
					if(hostApply.getSybaseStatus() == null){
						hostApply.setSybaseStatus(status);
					} else if(Integer.parseInt(hostApply.getSybaseStatus()) < Integer.parseInt(status)){
						hostApply.setSybaseStatus(status);
					} 
					//���õ�ǰ�������ϵ�sybase�Ƿ���ʾ
					hostApply.setSybaseIsShow(hostApplyModel.isShow());
				}else if(hostApplyModel.getSubtype().equalsIgnoreCase("mysql")){
					Node dbNode = PollingEngine.getInstance().getDbByID(applyId);
					if (dbNode == null) {
						status = "0";
					} else {
						status = dbNode.getStatus() + "";
						hostApply.setMysqlName(dbNode.getAlias());
					}
					//mysqlΪ��汾������������ǰmysqlӦ�õ�״̬Ϊ�澯������ߵ�״̬
					if(hostApply.getMysqlStatus() == null){
						hostApply.setMysqlStatus(status);
					} else if(Integer.parseInt(hostApply.getMysqlStatus()) < Integer.parseInt(status)){
						hostApply.setMysqlStatus(status);
					} 
					//���õ�ǰ�������ϵ�mysql�Ƿ���ʾ
					hostApply.setMysqlIsShow(hostApplyModel.isShow());
				}else if(hostApplyModel.getSubtype().equalsIgnoreCase("informix")){
					Node dbNode = PollingEngine.getInstance().getDbByID(applyId);
					if (dbNode == null) {
						status = "0";
					} else {
						status = dbNode.getStatus() + "";
						hostApply.setInformixName(dbNode.getAlias());
					}
					//informixΪ��汾������������ǰinformixӦ�õ�״̬Ϊ�澯������ߵ�״̬
					if(hostApply.getInformixStatus() == null){
						hostApply.setInformixStatus(status);
					} else if(Integer.parseInt(hostApply.getInformixStatus()) < Integer.parseInt(status)){
						hostApply.setInformixStatus(status);
					} 
					//���õ�ǰ�������ϵ�informix�Ƿ���ʾ
					hostApply.setInformixIsShow(hostApplyModel.isShow());
				}else if(hostApplyModel.getSubtype().equalsIgnoreCase("ftp")){
					Node node = PollingEngine.getInstance().getFtpByID(applyId);
					if (node == null) {
						status = "0";
					} else {
						status = node.getStatus() + "";
						hostApply.setFtpName(node.getAlias());
					}
					//ftpΪ��汾������������ǰftpӦ�õ�״̬Ϊ�澯������ߵ�״̬
					if(hostApply.getFtpStatus() == null){
						hostApply.setFtpStatus(status);
					} else if(Integer.parseInt(hostApply.getFtpStatus()) < Integer.parseInt(status)){
						hostApply.setFtpStatus(status);
					} 
					//���õ�ǰ�������ϵ�ftp�Ƿ���ʾ
					hostApply.setFtpIsShow(hostApplyModel.isShow());
				}else if(hostApplyModel.getSubtype().equalsIgnoreCase("email")){
					Node node = PollingEngine.getInstance().getMailByID(applyId);
					if (node == null) {
						status = "0";
					} else {
						status = node.getStatus() + "";
						hostApply.setEmailName(node.getAlias());
					}
					//emailΪ��汾������������ǰemailӦ�õ�״̬Ϊ�澯������ߵ�״̬
					if(hostApply.getEmailStatus() == null){
						hostApply.setEmailStatus(status);
					} else if(Integer.parseInt(hostApply.getEmailStatus()) < Integer.parseInt(status)){
						hostApply.setEmailStatus(status);
					} 
					//���õ�ǰ�������ϵ�email�Ƿ���ʾ
					hostApply.setEmailShow(hostApplyModel.isShow());
				}else if(hostApplyModel.getSubtype().equalsIgnoreCase("telnet")){
	//				hostApply.setTelnetName(alias);
				}else if(hostApplyModel.getSubtype().equalsIgnoreCase("mq")){
					Node node = PollingEngine.getInstance().getMqByID(applyId);
					if (node == null) {
						status = "0";
					} else {
						status = node.getStatus() + "";
						hostApply.setMqName(node.getAlias());
					}
					//mqΪ��汾������������ǰmqӦ�õ�״̬Ϊ�澯������ߵ�״̬
					if(hostApply.getMqStatus() == null){
						hostApply.setMqStatus(status);
					} else if(Integer.parseInt(hostApply.getMqStatus()) < Integer.parseInt(status)){
						hostApply.setMqStatus(status);
					} 
					//���õ�ǰ�������ϵ�mq�Ƿ���ʾ
					hostApply.setMqIsShow(hostApplyModel.isShow());
				}else if(hostApplyModel.getSubtype().equalsIgnoreCase("domino")){
					Node node = PollingEngine.getInstance().getDominoByID(applyId);
					if (node == null) {
						status = "0";
					} else {
						status = node.getStatus() + "";
						hostApply.setDominoName(node.getAlias());
					}
					//dominoΪ��汾������������ǰdominoӦ�õ�״̬Ϊ�澯������ߵ�״̬
					if(hostApply.getDominoStatus() == null){
						hostApply.setDominoStatus(status);
					} else if(Integer.parseInt(hostApply.getDominoStatus()) < Integer.parseInt(status)){
						hostApply.setDominoStatus(status);
					} 
					//���õ�ǰ�������ϵ�domino�Ƿ���ʾ
					hostApply.setDominoIsShow(hostApplyModel.isShow());
				}else if(hostApplyModel.getSubtype().equalsIgnoreCase("was")){
					Node node = PollingEngine.getInstance().getWasByID(applyId);
					if (node == null) {
						status = "0";
					} else {
						status = node.getStatus() + "";
						hostApply.setWasName(node.getAlias());
					}
					//wasΪ��汾������������ǰwasӦ�õ�״̬Ϊ�澯������ߵ�״̬
					if(hostApply.getWasStatus() == null){
						hostApply.setWasStatus(status);
					} else if(Integer.parseInt(hostApply.getWasStatus()) < Integer.parseInt(status)){
						hostApply.setWasStatus(status);
					} 
					//���õ�ǰ�������ϵ�was�Ƿ���ʾ
					hostApply.setWasIsShow(hostApplyModel.isShow());
				}else if(hostApplyModel.getSubtype().equalsIgnoreCase("weblogic")){
					Node node = PollingEngine.getInstance().getWeblogicByID(applyId);
					if (node == null) {
						status = "0";
					} else {
						status = node.getStatus() + "";
						hostApply.setWeblogicName(node.getAlias());
					}
					//weblogicΪ��汾������������ǰweblogicӦ�õ�״̬Ϊ�澯������ߵ�״̬
					if(hostApply.getWeblogicStatus() == null){
						hostApply.setWeblogicStatus(status);
					} else if(Integer.parseInt(hostApply.getWeblogicStatus()) < Integer.parseInt(status)){
						hostApply.setWeblogicStatus(status);
					} 
					//���õ�ǰ�������ϵ�weblogic�Ƿ���ʾ
					hostApply.setWeblogicIsShow(hostApplyModel.isShow());
				}else if(hostApplyModel.getSubtype().equalsIgnoreCase("tomcat")){
					Node node = PollingEngine.getInstance().getTomcatByID(applyId);
					if (node == null) {
						status = "0";
					} else {
						status = node.getStatus() + "";
						hostApply.setTomcatName(node.getAlias());
					}
					//tomcatΪ��汾������������ǰtomcatӦ�õ�״̬Ϊ�澯������ߵ�״̬
					if(hostApply.getTomcatStatus() == null){
						hostApply.setTomcatStatus(status);
					} else if(Integer.parseInt(hostApply.getTomcatStatus()) < Integer.parseInt(status)){
						hostApply.setTomcatStatus(status);
					} 
					//���õ�ǰ�������ϵ�tomcat�Ƿ���ʾ
					hostApply.setTomcatIsShow(hostApplyModel.isShow());
				}else if(hostApplyModel.getSubtype().equalsIgnoreCase("iis")){
					Node node = PollingEngine.getInstance().getIisByID(applyId);
					if (node == null) {
						status = "0";
					} else {
						status = node.getStatus() + "";
						hostApply.setIisName(node.getAlias());
					}
					//iisΪ��汾������������ǰiisӦ�õ�״̬Ϊ�澯������ߵ�״̬
					if(hostApply.getIisStatus() == null){
						hostApply.setIisStatus(status);
					} else if(Integer.parseInt(hostApply.getIisStatus()) < Integer.parseInt(status)){
						hostApply.setIisStatus(status);
					} 
					//���õ�ǰ�������ϵ�iis�Ƿ���ʾ
					hostApply.setIisIsShow(hostApplyModel.isShow());
				}else if(hostApplyModel.getSubtype().equalsIgnoreCase("cics")){
					Node node = PollingEngine.getInstance().getCicsByID(applyId);
					if (node == null) {
						status = "0";
					} else {
						status = node.getStatus() + "";
						hostApply.setCicsName(node.getAlias());
					}
					//cicsΪ��汾������������ǰiisӦ�õ�״̬Ϊ�澯������ߵ�״̬
					if(hostApply.getCicsStatus() == null){
						hostApply.setCicsStatus(status);
					} else if(Integer.parseInt(hostApply.getCicsStatus()) < Integer.parseInt(status)){
						hostApply.setCicsStatus(status);
					} 
					//���õ�ǰ�������ϵ�cics�Ƿ���ʾ
					hostApply.setCicsIsShow(hostApplyModel.isShow());
				}else if(hostApplyModel.getSubtype().equalsIgnoreCase("dns")){
					Node node = PollingEngine.getInstance().getDnsByID(applyId);
					if (node == null) {
						status = "0";
					} else {
						status = node.getStatus() + "";
						hostApply.setDnsName(node.getAlias());
					}
					//dnsΪ��汾������������ǰiisӦ�õ�״̬Ϊ�澯������ߵ�״̬
					if(hostApply.getDnsStatus() == null){
						hostApply.setDnsStatus(status);
					} else if(Integer.parseInt(hostApply.getDnsStatus()) < Integer.parseInt(status)){
						hostApply.setDnsStatus(status);
					} 
					//���õ�ǰ�������ϵ�dns�Ƿ���ʾ
					hostApply.setDnsIsShow(hostApplyModel.isShow());
				}else if(hostApplyModel.getSubtype().equalsIgnoreCase("jboss")){
					Node node = PollingEngine.getInstance().getJBossByID(applyId);
					if (node == null) {
						status = "0";
					} else {
						status = node.getStatus() + "";
						hostApply.setJbossName(node.getAlias());
					}
					//jbossΪ��汾������������ǰiisӦ�õ�״̬Ϊ�澯������ߵ�״̬
					if(hostApply.getJbossStatus() == null){
						hostApply.setJbossStatus(status);
					} else if(Integer.parseInt(hostApply.getJbossStatus()) < Integer.parseInt(status)){
						hostApply.setJbossStatus(status);
					} 
					//���õ�ǰ�������ϵ�jboss�Ƿ���ʾ
					hostApply.setJbossIsShow(hostApplyModel.isShow());
				}else if(hostApplyModel.getSubtype().equalsIgnoreCase("apache")){
					Node node = PollingEngine.getInstance().getApacheByID(applyId);
					if (node == null) {
						status = "0";
					} else {
						status = node.getStatus() + "";
						hostApply.setApacheName(node.getAlias());
					}
					//apacheΪ��汾������������ǰiisӦ�õ�״̬Ϊ�澯������ߵ�״̬
					if(hostApply.getApacheStatus() == null){
						hostApply.setApacheStatus(status);
					} else if(Integer.parseInt(hostApply.getApacheStatus()) < Integer.parseInt(status)){
						hostApply.setApacheStatus(status);
					} 
					//���õ�ǰ�������ϵ�apache�Ƿ���ʾ
					hostApply.setApacheIsShow(hostApplyModel.isShow());
				}else if(hostApplyModel.getSubtype().equalsIgnoreCase("tuxedo")){
					Node node = PollingEngine.getInstance().getTuxedoById(applyId);
					if (node == null) { 
						status = "0";
					} else {
						status = node.getStatus() + "";
						hostApply.setTuxedoName(node.getAlias()); 
					}
					//tuxdeoΪ��汾������������ǰiisӦ�õ�״̬Ϊ�澯������ߵ�״̬
					if(hostApply.getTuxedoStatus() == null){
						hostApply.setTuxedoStatus(status);
					} else if(Integer.parseInt(hostApply.getTuxedoStatus()) < Integer.parseInt(status)){
						hostApply.setTuxedoStatus(status);
					} 
					//���õ�ǰ�������ϵ�tuxdeo�Ƿ���ʾ 
					hostApply.setTuxedoIsShow(hostApplyModel.isShow());
				}
			}
		}
		
		//
		if(hostNodeList != null){
			for(int i=0; i<hostNodeList.size(); i++){
				HostNode hostNode = hostNodeList.get(i);
				if(!retHash.containsKey(hostNode.getIpAddress())){//�����map�����в��������豸��Ӧ����Ϣ,�½�Ӧ��
					HostApply hostApply = new HostApply();
					retHash.put(hostNode.getIpAddress(), hostApply);
				}
			}
		}
		return retHash;
	}
	
	/**
	 * �ڵ�״̬��־
	 */
	public static String getCurrentStatusImage(int status) {
		String image = null;
		if (status == 0){
			image = "a_level_0.gif";
		}else if (status == 1){
			image = "a_level_1.gif";
		}else if (status == 2){
			image = "a_level_2.gif";
		}else if (status == 3){
			 image = "a_level_3.gif";
		}else{
			image = "small7.png";
			return null;
		}
		return rootPath+"/resource/image/topo/" + image;
	}
	
	/**
	 * ������ת��Ϊ��������Ӧ��Ӧ��������
	 * @param obj  ��Ҫ����Ķ���
	 */
	public static synchronized void save(Object obj){
		if(obj == null){
			return;
		}
		HostApplyModel hostApplyModel = new HostApplyModel();
//		Node node = null;
		//���ݿ�����
		if(obj instanceof DBVo){
			DBVo vo = (DBVo) obj;
			hostApplyModel.setNodeid(vo.getId());
			hostApplyModel.setIpaddres(vo.getIpAddress());
			hostApplyModel.setShow(true);
			String subtype = dbTypeMap.get(vo.getDbtype());
			hostApplyModel.setSubtype(subtype);
			hostApplyModel.setType("db");
		}
		//Tomcat�м�������
		if(obj instanceof Tomcat){
			Tomcat vo = (Tomcat) obj;
			hostApplyModel.setNodeid(vo.getId());
			hostApplyModel.setIpaddres(vo.getIpAddress());
			hostApplyModel.setShow(true);
			hostApplyModel.setSubtype("tomcat");
			hostApplyModel.setType("middleware");
//			node = PollingEngine.getInstance().getTomcatByID(vo.getId());
		}
		//mq�м�������
		if(obj instanceof MQConfig){
			MQConfig vo = (MQConfig) obj;
			hostApplyModel.setNodeid(vo.getId());
			hostApplyModel.setIpaddres(vo.getIpaddress());
			hostApplyModel.setShow(true);
			hostApplyModel.setSubtype("mq");
			hostApplyModel.setType("middleware");
//			node = PollingEngine.getInstance().getMqByID(vo.getId());
		}
		//domino�м�������
		if(obj instanceof DominoConfig){
			DominoConfig vo = (DominoConfig) obj;
			hostApplyModel.setNodeid(vo.getId());
			hostApplyModel.setIpaddres(vo.getIpaddress());
			hostApplyModel.setShow(true);
			hostApplyModel.setSubtype("domino");
			hostApplyModel.setType("middleware");
//			node = PollingEngine.getInstance().getDominoByID(vo.getId());
		}
		//was�м�������
		if(obj instanceof WasConfig){
			WasConfig vo = (WasConfig) obj;
			hostApplyModel.setNodeid(vo.getId());
			hostApplyModel.setIpaddres(vo.getIpaddress());
			hostApplyModel.setShow(true);
			hostApplyModel.setSubtype("was");
			hostApplyModel.setType("middleware");
//			node = PollingEngine.getInstance().getWasByID(vo.getId());
		}
		//weblogic�м�������
		if(obj instanceof WeblogicConfig){
			WeblogicConfig vo = (WeblogicConfig) obj;
			hostApplyModel.setNodeid(vo.getId());
			hostApplyModel.setIpaddres(vo.getIpAddress());
			hostApplyModel.setShow(true);
			hostApplyModel.setSubtype("weblogic");
			hostApplyModel.setType("middleware");
//			node = PollingEngine.getInstance().getWeblogicByID(vo.getId());
		}
		//JBoss�м�������
		if(obj instanceof JBossConfig){
			JBossConfig vo = (JBossConfig) obj;
			hostApplyModel.setNodeid(vo.getId());
			hostApplyModel.setIpaddres(vo.getIpaddress());
			hostApplyModel.setShow(true);
			hostApplyModel.setSubtype("jboss");
			hostApplyModel.setType("middleware");
//			node = PollingEngine.getInstance().getJBossByID(vo.getId());
		}
		//apache�м�������
		if(obj instanceof ApacheConfig){
			ApacheConfig vo = (ApacheConfig) obj;
			hostApplyModel.setNodeid(vo.getId());
			hostApplyModel.setIpaddres(vo.getIpaddress());
			hostApplyModel.setShow(true);
			hostApplyModel.setSubtype("apache");
			hostApplyModel.setType("middleware");
//			node = PollingEngine.getInstance().getApacheByID(vo.getId());
		}
		//tuxedo�м�������
		if(obj instanceof TuxedoConfig){
			TuxedoConfig vo = (TuxedoConfig) obj;
			hostApplyModel.setNodeid(vo.getId());
			hostApplyModel.setIpaddres(vo.getIpAddress());
			hostApplyModel.setShow(true);
			hostApplyModel.setSubtype("tuxedo");
			hostApplyModel.setType("middleware");
//			node = PollingEngine.getInstance().getTuxedoById(vo.getId());
		}
		//IIS�м�������
		if(obj instanceof IISConfig){
			IISConfig vo = (IISConfig) obj;
			hostApplyModel.setNodeid(vo.getId());
			hostApplyModel.setIpaddres(vo.getIpaddress());
			hostApplyModel.setShow(true);
			hostApplyModel.setSubtype("iis");
			hostApplyModel.setType("middleware");
//			node = PollingEngine.getInstance().getIisByID(vo.getId());
		}
		//cics�м�������
		if(obj instanceof CicsConfig){
			CicsConfig vo = (CicsConfig) obj;
			hostApplyModel.setNodeid(vo.getId());
			hostApplyModel.setIpaddres(vo.getIpaddress());
			hostApplyModel.setShow(true);
			hostApplyModel.setSubtype("cics");
			hostApplyModel.setType("middleware");
//			node = PollingEngine.getInstance().getCicsByID(vo.getId());
		}
		//dns�м�������
		if(obj instanceof DnsConfig){
			DnsConfig vo = (DnsConfig) obj;
			hostApplyModel.setNodeid(vo.getId());
			hostApplyModel.setIpaddres(vo.getHostip());
			hostApplyModel.setShow(true);
			hostApplyModel.setSubtype("dns");
			hostApplyModel.setType("middleware");
//			node = PollingEngine.getInstance().getDnsByID(vo.getId());
		}
		//ftp����
		if(obj instanceof FTPConfig){
			FTPConfig vo = (FTPConfig) obj;
			hostApplyModel.setNodeid(vo.getId());
			hostApplyModel.setIpaddres(vo.getIpaddress());
			hostApplyModel.setShow(true);
			hostApplyModel.setSubtype("ftp");
			hostApplyModel.setType("service");
//			node = PollingEngine.getInstance().getFtpByID(vo.getId());
		}
		//email����
		if(obj instanceof EmailMonitorConfig){
			EmailMonitorConfig vo = (EmailMonitorConfig) obj;
			hostApplyModel.setNodeid(vo.getId());
			hostApplyModel.setIpaddres(vo.getIpaddress());
			hostApplyModel.setShow(true);
			hostApplyModel.setSubtype("email");
			hostApplyModel.setType("service");
//			node = PollingEngine.getInstance().getMailByID(vo.getId());
		}
		//telnet����
		//���������Ӧ����Ϣ
		HostApplyDao hostApplyDao = null;
		try{
			hostApplyDao = new HostApplyDao();
			hostApplyDao.save(hostApplyModel);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(hostApplyDao != null){
				hostApplyDao.close();
			}
		}
	}
	
	/**
	 * ���ҵ��Ȩ�޵� SQL ���
	 * @return
	 */
	public String getBidSql(){
		User current_user = (User)session.getAttribute(SessionConstant.CURRENT_USER);
		StringBuffer s = new StringBuffer();
		int _flag = 0;
		if (current_user.getBusinessids() != null){
			if(current_user.getBusinessids() !="-1"){
				String[] bids = current_user.getBusinessids().split(",");
				if(bids.length>0){
					for(int i=0;i<bids.length;i++){
						if(bids[i].trim().length()>0){
							if(_flag==0){
								s.append(" and ( bid like '%,"+bids[i].trim()+",%' ");
								_flag = 1;
							}else{
								//flag = 1;
								s.append(" or bid like '%,"+bids[i].trim()+",%' ");
							}
						}
					}
					s.append(") ") ;
				}
				
			}	
		}
//		SysLogger.info("select * from topo_host_node where managed=1 "+s);
		String sql = "";
		if(current_user.getRole() == 0){
			sql = "";
		}else{
			sql = s.toString();
		}
		String treeBid = request.getParameter("treeBid");
		if(treeBid != null && treeBid.trim().length() > 0){
			treeBid = treeBid.trim();
			treeBid = "," + treeBid + ",";
			String[] treeBids = treeBid.split(",");
			if(treeBids != null){
				for(int i = 0; i < treeBids.length; i++){
					if(treeBids[i].trim().length() > 0){
						sql = sql + " and " + "bid" + " like '%," + treeBids[i].trim() + ",%'";
					}
				}
			}
		}
		request.setAttribute("treeBid", treeBid);
		return sql;
	}
	
	/**
	 * ��ʾ/����Ӧ��
	 * @return
	 */
	public String show(){
		String ipaddress = getParaValue("ipaddress");
		String type = getParaValue("type");
		String subtype = getParaValue("subtype");
		String nodeid = getParaValue("nodeid");
		
		//ip��ַ:���� ���� 
		String[] ipAndSubTypes = getParaArrayValue("checkbox");
		String modifyFlag = getParaValue("modifyFlag");
		boolean isShow = Boolean.parseBoolean(modifyFlag);
		HostApplyDao hostApplyDao = null;
		try{
			hostApplyDao = new HostApplyDao();
			hostApplyDao.batchUpdateSingleIsShow(ipAndSubTypes, isShow, ipaddress);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(hostApplyDao != null){
				hostApplyDao.close();
			}
		}
		request.setAttribute("type", type);
		request.setAttribute("subtype", subtype);
		request.setAttribute("ipaddress", ipaddress);
		request.setAttribute("nodeid", nodeid);
		return list();
	}
	
	/**
	 * ��ʾ/����Ӧ��
	 * @return
	 */
	public String allShow(){
		String ipaddress = getParaValue("ipaddress");
		String type = getParaValue("type");
		String subtype = getParaValue("subtype");
		
		//ip��ַ:���� ���� 
		String[] ipAndSubTypes = getParaArrayValue("checkbox");
		String modifyFlag = getParaValue("modifyFlag");
		boolean isShow = Boolean.parseBoolean(modifyFlag);
		HostApplyDao hostApplyDao = null;
		try{
			hostApplyDao = new HostApplyDao();
			hostApplyDao.batchUpdateMultilIsShow(ipAndSubTypes, isShow);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(hostApplyDao != null){
				hostApplyDao.close();
			}
		}
		request.setAttribute("type", type);
		request.setAttribute("subtype", subtype);
		request.setAttribute("ipaddress", ipaddress);
		return allList();
	}
}
