package com.afunms.indicators.util;

import java.util.ArrayList;
import java.util.List;

import com.afunms.application.dao.ApacheConfigDao;
import com.afunms.application.dao.CicsConfigDao;
import com.afunms.application.dao.DBDao;
import com.afunms.application.dao.DBTypeDao;
import com.afunms.application.dao.DnsConfigDao;
import com.afunms.application.dao.DominoConfigDao;
import com.afunms.application.dao.EmailConfigDao;
import com.afunms.application.dao.FTPConfigDao;
import com.afunms.application.dao.IISConfigDao;
import com.afunms.application.dao.JBossConfigDao;
import com.afunms.application.dao.MQConfigDao;
import com.afunms.application.dao.OraclePartsDao;
import com.afunms.application.dao.PSTypeDao;
import com.afunms.application.dao.TomcatDao;
import com.afunms.application.dao.TuxedoConfigDao;
import com.afunms.application.dao.WasConfigDao;
import com.afunms.application.dao.WebConfigDao;
import com.afunms.application.dao.WeblogicConfigDao;
import com.afunms.application.model.ApacheConfig;
import com.afunms.application.model.CicsConfig;
import com.afunms.application.model.DBTypeVo;
import com.afunms.application.model.DBVo;
import com.afunms.application.model.DnsConfig;
import com.afunms.application.model.DominoConfig;
import com.afunms.application.model.EmailMonitorConfig;
import com.afunms.application.model.FTPConfig;
import com.afunms.application.model.IISConfig;
import com.afunms.application.model.JBossConfig;
import com.afunms.application.model.MQConfig;
import com.afunms.application.model.OracleEntity;
import com.afunms.application.model.PSTypeVo;
import com.afunms.application.model.Tomcat;
import com.afunms.application.model.TuxedoConfig;
import com.afunms.application.model.WasConfig;
import com.afunms.application.model.WebConfig;
import com.afunms.application.model.WeblogicConfig;
import com.afunms.common.base.BaseVo;
import com.afunms.common.util.SysLogger;
import com.afunms.config.dao.BusinessDao;
import com.afunms.config.model.Business;
import com.afunms.config.model.Procs;
import com.afunms.indicators.model.NodeDTO;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.base.Node;
import com.afunms.polling.node.Ftp;
import com.afunms.polling.node.Host;
import com.afunms.polling.node.Mail;
import com.afunms.polling.node.SocketService;
import com.afunms.polling.node.Web;
import com.afunms.topology.dao.HostNodeDao;
import com.afunms.topology.dao.ManageXmlDao;
import com.afunms.topology.model.HostNode;
import com.afunms.topology.model.ManageXml;
import com.afunms.polling.node.UPSNode;
import com.afunms.security.dao.MgeUpsDao;
import com.afunms.security.model.MgeUps;



/**
 * ����Ϊ ͨ��  ���� �� �������� ��ȡ �豸
 * @author Administrator
 *
 */

public class NodeUtil {
	   
//	private List<NodeDTO> nodeDTOList;
	 
	private String bid;
	
	private String monitorFlag;
	
	private boolean isSetedBid = false;
	
	private boolean isSetedMonitorFlag = false;
	
	public NodeUtil() {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * @param bid
	 */
	public NodeUtil(String bid) {
		this.setBid(bid);
	}



	/**
	 * @return the bid
	 */
	public String getBid() {
		return bid;
	}

	/**
	 * @param bid the bid to set
	 */
	public void setBid(String bid) {
		this.bid = bid;
		this.isSetedBid = true;
	}



	/**
	 * �������� �� ������ ����ȡ�豸 ���� BaseVo �� �б�
	 * @param type
	 * @param subtype
	 * @return
	 */
	public List<BaseVo> getNodeByTyeAndSubtype(String type , String subtype){
		//SysLogger.info(type+"============="+subtype);
		List<BaseVo> list= new ArrayList<BaseVo>();
		
		if(Constant.ALL_TYPE.equals(type)){
			list.addAll(getNetList());
			list.addAll(getFirewallList());
			list.addAll(getAtmList());
			list.addAll(getGatewayList());
			list.addAll(getF5List());
			list.addAll(getHostList());
			list.addAll(getDBList());
			list.addAll(getMiddlewareList());
			list.addAll(getServiceList());
			list.addAll(getUpsList());
			list.addAll(getAirList());
		}else if(Constant.TYPE_NET.equals(type)){
			// �����豸
			if(Constant.TYPE_NET_SUBTYPE_CISCO.equals(subtype)){
				list.addAll(getNet_CiscoList());
			}else if(Constant.TYPE_NET_SUBTYPE_BDCOM.equals(subtype)){
				list.addAll(getNet_BdcomlList());
			}else if(Constant.TYPE_NET_SUBTYPE_DLINK.equals(subtype)){
				list.addAll(getNet_DlinklList());
			} else if(Constant.TYPE_NET_SUBTYPE_ENTRASYS.equals(subtype)){
				list.addAll(getNet_EntrasysList());
			} else if(Constant.TYPE_NET_SUBTYPE_H3C.equals(subtype)){
				list.addAll(getNet_H3cList());
			} else if(Constant.TYPE_NET_SUBTYPE_ZTE.equals(subtype)){
				list.addAll(getNet_ZtelList());
			} else if(Constant.TYPE_NET_SUBTYPE_MAIPU.equals(subtype)){
				list.addAll(getNet_MaipuList());
			} else if(Constant.TYPE_NET_SUBTYPE_NORTHTEL.equals(subtype)){
				list.addAll(getNet_NorthtelList());
			} else if(Constant.TYPE_NET_SUBTYPE_RADWARE.equals(subtype)){
				list.addAll(getNet_RadwareList());
			} else if(Constant.TYPE_NET_SUBTYPE_REDGIANT.equals(subtype)){
				list.addAll(getNet_RedgiantList());
			} else if(Constant.TYPE_NET_SUBTYPE_IBM.equals(subtype)){
				list.addAll(getNet_IbmList());
			} else if(Constant.TYPE_NET_SUBTYPE_BROCADE.equals(subtype)){
				list.addAll(getNet_BrocadeList());
			} else {
				list.addAll(getNetList());
			}
		}else if(Constant.TYPE_HOST.equals(type)){
			// ������
			if(Constant.TYPE_HOST_SUBTYPE_WINDOWS.equals(subtype)){
				list.addAll(getHost_WindowsList());
			} else if(Constant.TYPE_HOST_SUBTYPE_AIX.equals(subtype)){
				list.addAll(getHost_AixList());
			} else if(Constant.TYPE_HOST_SUBTYPE_HPUNIX.equals(subtype)){
				list.addAll(getHost_HpunixList());
			} else if(Constant.TYPE_HOST_SUBTYPE_AS400.equals(subtype)){
				list.addAll(getHost_As400List());
			} else if(Constant.TYPE_HOST_SUBTYPE_LINUX.equals(subtype)){
				list.addAll(getHost_LinuxList());
			} else if(Constant.TYPE_HOST_SUBTYPE_SOLARIS.equals(subtype)){
				list.addAll(getHost_SolarisList());
			} else if(Constant.TYPE_HOST_SUBTYPE_SCOUNIX.equals(subtype)){
				list.addAll(getHost_ScounixList());
			} else if(Constant.TYPE_HOST_SUBTYPE_SCOOPENSERVER.equals(subtype)){
				list.addAll(getHost_ScoopenserverList());
			} else {
				list.addAll(getHostList());
			}
		}else if(Constant.TYPE_DB.equals(type)){
			// ���ݿ�
			if(Constant.TYPE_DB_SUBTYPE_ORACLE.equalsIgnoreCase(subtype)){
				list.addAll(getDB_OracleList());
			} else if(Constant.TYPE_DB_SUBTYPE_DB2.equalsIgnoreCase(subtype)){
				list.addAll(getDB_DB2List());
			} else if(Constant.TYPE_DB_SUBTYPE_MYSQL.equalsIgnoreCase(subtype)){
				list.addAll(getDB_MysqlList());
			} else if(Constant.TYPE_DB_SUBTYPE_SQLSERVER.equalsIgnoreCase(subtype)){
				list.addAll(getDB_SQLServerList());
			} else if(Constant.TYPE_DB_SUBTYPE_SYBASE.equalsIgnoreCase(subtype)){
				list.addAll(getDB_SybaseList());
			} else if(Constant.TYPE_DB_SUBTYPE_INFORMIX.equalsIgnoreCase(subtype)){
				list.addAll(getDB_InformixList());
			} else {
				list.addAll(getDBList());
			}
		}else if(Constant.TYPE_MIDDLEWARE.equals(type)){
			// �м��
			if(Constant.TYPE_MIDDLEWARE_SUBTYPE_APACHE.equals(subtype)){
				list.addAll(getMiddleware_ApacheList());
			} else if(Constant.TYPE_MIDDLEWARE_SUBTYPE_CICS.equals(subtype)){
				list.addAll(getMiddleware_CICSList());
			} else if(Constant.TYPE_MIDDLEWARE_SUBTYPE_DNS.equals(subtype)){
				list.addAll(getMiddleware_DNSList());
			} else if(Constant.TYPE_MIDDLEWARE_SUBTYPE_DOMINO.equals(subtype)){
				list.addAll(getMiddleware_DominoList());
			} else if(Constant.TYPE_MIDDLEWARE_SUBTYPE_IIS.equals(subtype)){
				list.addAll(getMiddleware_IISList());
			} else if(Constant.TYPE_MIDDLEWARE_SUBTYPE_JBOSS.equals(subtype)){
				list.addAll(getMiddleware_JBossList());
			} else if(Constant.TYPE_MIDDLEWARE_SUBTYPE_MQ.equals(subtype)){
				list.addAll(getMiddleware_MQList());
			} else if(Constant.TYPE_MIDDLEWARE_SUBTYPE_TOMCAT.equals(subtype)){
				list.addAll(getMiddleware_TomcatList());
			} else if(Constant.TYPE_MIDDLEWARE_SUBTYPE_TUXEDO.equals(subtype)){
				list.addAll(getMiddleware_TuxedoList());
			} else if(Constant.TYPE_MIDDLEWARE_SUBTYPE_WAS.equals(subtype)){
				list.addAll(getMiddleware_WasList());
			} else if(Constant.TYPE_MIDDLEWARE_SUBTYPE_WEBLOGIC.equals(subtype)){
				//SysLogger.info("########################1");
				list.addAll(getMiddleware_WeblogicList());
			} else {
				list.addAll(getMiddlewareList());
			}
		}else if(Constant.TYPE_SERVICE.equals(type)){
			// ����
			if(Constant.TYPE_SERVICE_SUBTYPE_EMAIL.equals(subtype)){
				list.addAll(getService_EmailList());
			} else if(Constant.TYPE_SERVICE_SUBTYPE_FTP.equals(subtype)){
				list.addAll(getService_FtpList());
			} else if(Constant.TYPE_SERVICE_SUBTYPE_HOSTPROCESS.equals(subtype)){
//				list.addAll(getService_());
			} else if(Constant.TYPE_SERVICE_SUBTYPE_PORT.equals(subtype)){
				list.addAll(getService_PSTypeVoList());
			} else if(Constant.TYPE_SERVICE_SUBTYPE_WEB.equals(subtype)){
				list.addAll(getService_WebList());
			} else {
				list.addAll(getServiceList());
			}
		}else if(Constant.TYPE_UPS.equals(type)){
			// UPS
			if(Constant.TYPE_UPS_SUBTYPE_EMS.equals(subtype)){
				list.addAll(getUps_EmsList());
			} else if(Constant.TYPE_UPS_SUBTYPE_MGE.equals(subtype)){
				list.addAll(getUps_MgeList());
			} else {
				list.addAll(getUpsList());
			}
		}else if(Constant.TYPE_AIR.equals(type)){
			// �յ�
			if(Constant.TYPE_AIR_SUBTYPE_EMS.equals(subtype)){
				list.addAll(getAir_EmsList());
			} else {
				list.addAll(getAirList());
			}
		}else if(Constant.TYPE_ATM.equals(type)){
			// ems
			if(Constant.TYPE_AIR_SUBTYPE_EMS.equals(subtype)){
				list.addAll(getAtmList());
			} else {
				list.addAll(getAtmList());
			}
		}else if(Constant.TYPE_VPN.equals(type)){
			// VPN
			if(Constant.TYPE_VPN_SUBTYPE_ARRAYNETWORKS.equals(subtype)){
				list.addAll(getVPNList());
			} else {
				list.addAll(getVPNList());
			}
		}else if(Constant.TYPE_GATEWAY.equals(type)){
			// �յ�
			if(Constant.TYPE_AIR_SUBTYPE_EMS.equals(subtype)){
				list.addAll(getGateway_CiscoList());
			} else {
				list.addAll(getGatewayList());
			}
		}else if(Constant.TYPE_F5.equals(type)){
			// F5
			if(Constant.TYPE_F5_SUBTYPE_F5.equals(subtype)){
				list.addAll(getF5List());
			} else {
				list.addAll(getF5List());
			}
		}else if(Constant.TYPE_FIREWALL.equals(type)){
			// ����ǽ
			if(Constant.TYPE_FIREWALL_SUBTYPE_HILLSTONE.equals(subtype)){
				list.addAll(getNet_HillstoneList());
			} else if(Constant.TYPE_FIREWALL_SUBTYPE_NETSCREEN.equals(subtype)){
				list.addAll(getNet_NetscreenList());
			} else if(Constant.TYPE_FIREWALL_SUBTYPE_NOKIA.equals(subtype)){
				list.addAll(getNet_NokiaList());
			} else {
				list.addAll(getFirewallList());
			}
		}
		
		return list;
	}
	
	/**
	 * ͨ�� BaseVo �б� �� ת���� NodeDTO �б�
	 * @param type
	 * @param subtype
	 * @return
	 */
	public List<NodeDTO> conversionToNodeDTO(List<BaseVo> list){
		List<NodeDTO> nodeDTOList = null;
		if(list != null){
			nodeDTOList = new ArrayList<NodeDTO>();
			for(int i = 0 ; i < list.size(); i++){
				nodeDTOList.add(conversionToNodeDTO(list.get(i)));
			}
		}
		//System.out.println("========nodeDTOList.size()=========" + nodeDTOList.size());
		return nodeDTOList;
	}
	
	/**
	 * ͨ�� BaseVo  ת���� NodeDTO 
	 * @param type
	 * @param subtype
	 * @return
	 */
	public NodeDTO conversionToNodeDTO(BaseVo baseVo){
		
		if(baseVo == null){
			return null;
		}
		
		NodeDTO nodeDTO = null;
		
		if (baseVo instanceof HostNode) {
			HostNode hostNode = (HostNode) baseVo;
			nodeDTO = creatNodeDTOByNode(hostNode);
		} else if (baseVo instanceof DBVo) {
			DBVo dbVo = (DBVo) baseVo;
			nodeDTO = creatNodeDTOByNode(dbVo);
		} else if (baseVo instanceof OracleEntity) {
			OracleEntity oracleEntity = (OracleEntity) baseVo;
			nodeDTO = creatNodeDTOByNode(oracleEntity);
		} else if (baseVo instanceof ApacheConfig) {
			ApacheConfig apacheConfig = (ApacheConfig) baseVo;
			nodeDTO = creatNodeDTOByNode(apacheConfig);
		} else if (baseVo instanceof CicsConfig) {
			CicsConfig cicsConfig = (CicsConfig) baseVo;
			nodeDTO = creatNodeDTOByNode(cicsConfig);
		} else if (baseVo instanceof DnsConfig) {
			DnsConfig dnsConfig = (DnsConfig) baseVo;
			nodeDTO = creatNodeDTOByNode(dnsConfig);
		} else if (baseVo instanceof DominoConfig) {
			DominoConfig dominoConfig = (DominoConfig) baseVo;
			nodeDTO = creatNodeDTOByNode(dominoConfig);
		} else if (baseVo instanceof IISConfig) {
			IISConfig iISConfig = (IISConfig) baseVo;
			nodeDTO = creatNodeDTOByNode(iISConfig);
		} else if (baseVo instanceof JBossConfig) {
			JBossConfig jBossConfig = (JBossConfig) baseVo;
			nodeDTO = creatNodeDTOByNode(jBossConfig);
		} else if (baseVo instanceof MQConfig) {
			MQConfig mQConfig = (MQConfig) baseVo;
			nodeDTO = creatNodeDTOByNode(mQConfig);
		} else if (baseVo instanceof Tomcat) {
			Tomcat tomcat = (Tomcat) baseVo;
			nodeDTO = creatNodeDTOByNode(tomcat);
		} else if (baseVo instanceof TuxedoConfig) {
			TuxedoConfig tuxedoConfig = (TuxedoConfig) baseVo;
			nodeDTO = creatNodeDTOByNode(tuxedoConfig);
		} else if (baseVo instanceof WasConfig) {
			WasConfig wasConfig = (WasConfig) baseVo;
			nodeDTO = creatNodeDTOByNode(wasConfig);
		} else if (baseVo instanceof WeblogicConfig) {
			WeblogicConfig weblogicConfig = (WeblogicConfig) baseVo;
			nodeDTO = creatNodeDTOByNode(weblogicConfig);
		} else if (baseVo instanceof EmailMonitorConfig) {
			EmailMonitorConfig emailMonitorConfig = (EmailMonitorConfig) baseVo;
			nodeDTO = creatNodeDTOByNode(emailMonitorConfig);
		} else if (baseVo instanceof FTPConfig) {
			FTPConfig fTPConfig = (FTPConfig) baseVo;
			nodeDTO = creatNodeDTOByNode(fTPConfig);
		} else if (baseVo instanceof Procs) {
			Procs procs = (Procs) baseVo;
			nodeDTO = creatNodeDTOByNode(procs);
		} else if (baseVo instanceof PSTypeVo) {
			PSTypeVo pSTypeVo = (PSTypeVo) baseVo;
			nodeDTO = creatNodeDTOByNode(pSTypeVo);
		} else if (baseVo instanceof WebConfig) {
			WebConfig webConfig = (WebConfig) baseVo;
			nodeDTO = creatNodeDTOByNode(webConfig);
		} else if (baseVo instanceof ManageXml) {
			ManageXml manageXml = (ManageXml) baseVo;
			nodeDTO = creatNodeDTOByNode(manageXml);
		} else if (baseVo instanceof MgeUps) {
			MgeUps mgeUps = (MgeUps) baseVo;
			nodeDTO = creatNodeDTOByNode(mgeUps);
		}
		nodeDTO.setBusinessName(getBusinessNameForNode(nodeDTO.getBusinessId()));
		
		return nodeDTO;
	}
	
	public NodeDTO conversionToNodeDTO(Node node){
		
		if(node == null){
			return null;
		}
		
		NodeDTO nodeDTO = null;
		
		if (node instanceof Host) {
			Host host = (Host) node;
			nodeDTO = creatNodeDTOByNode(host);
		}else if (node instanceof Mail) {
			Mail mail = (Mail)node;
			nodeDTO = creatNodeDTOByNode(mail);
		}else if (node instanceof Ftp) {
			Ftp ftp = (Ftp)node;
			nodeDTO = creatNodeDTOByNode(ftp);
		}else if (node instanceof SocketService) {
			SocketService socket = (SocketService)node;
			nodeDTO = creatNodeDTOByNode(socket);
		}else if (node instanceof Web) {
			Web web = (Web)node;
			nodeDTO = creatNodeDTOByNode(web);
		}
		nodeDTO.setBusinessName(getBusinessNameForNode(nodeDTO.getBusinessId()));
		
		return nodeDTO;
	}
	
	
	/**
	 * ���� ������ȡ hostNode
	 * @param condition
	 * @return
	 */
	public List<BaseVo> getHostNode(String condition){
		HostNodeDao hostNodeDao = new HostNodeDao();
		List<BaseVo> list = null;
		try {
			list = hostNodeDao.findByCondition(condition);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
	
	public List<BaseVo> getUpsNode(String condition){
		MgeUpsDao mgeUpsDao = new MgeUpsDao();
		List<BaseVo> list = null;
		try {
			list = mgeUpsDao.loadByType(condition);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
	
	public List<BaseVo> getUpsNode(String type,String subtype){
		MgeUpsDao mgeUpsDao = new MgeUpsDao();
		List<BaseVo> list = null;
		try {
			list = mgeUpsDao.loadByTypeAndSubtype(type,subtype);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
	
	//--------------------------------------------------------------------------------------------
	//                         �����豸
	//--------------------------------------------------------------------------------------------
	
	public List<BaseVo> getUpsList(){
		List<BaseVo> list = getUpsNode("ups");
		return list;
	}
	
	public List<BaseVo> getAirList(){
		List<BaseVo> list = getUpsNode("air");
		return list;
	}
	
	public List<BaseVo> getUps_EmsList(){
		List<BaseVo> list = getUpsNode("ups","ems");
		return list;
	}
	
    public List<BaseVo> getUps_MgeList(){
    	List<BaseVo> list = getUpsNode("ups","mge");
		return list;
	}
    
    public List<BaseVo> getAir_EmsList(){
    	List<BaseVo> list = getUpsNode("air","ems");
		return list;
	}
    
	/**
	 * ��ȡ�����豸 �б� 
	 * @return
	 */
	public List<BaseVo> getNetList(){
		List<BaseVo> list = getHostNode(getNetSql());
		return list;
	}
	
	/**
	 * ��ȡ����ǽ�豸 �б� 
	 * @return
	 */
	public List<BaseVo> getFirewallList(){
		List<BaseVo> list = getHostNode(getFirewallSql());
		return list;
	}
	
	/**
	 * ��ȡ�ʼ���ȫ�����豸 �б� 
	 * @return
	 */
	public List<BaseVo> getGatewayList(){
		List<BaseVo> list = getHostNode(getGatewaySql());
		return list;
	}
	/**
	 * ��ȡ���ؾ����豸 �б� 
	 * @return
	 */
	public List<BaseVo> getF5List(){
		List<BaseVo> list = getHostNode(getF5Sql());
		return list;
	}
	/**
	 * ��ȡVPN�豸 �б� 
	 * @return
	 */
	public List<BaseVo> getVPNList(){
		List<BaseVo> list = getHostNode(getVPNSql());
		return list;
	}
	
	/**
	 * ��ȡ �����豸 sql ���
	 * @return
	 */
	public String getNetSql(){
		
		String sql = " where category<4 or category=7";
		
		sql = sql + getMonFlagSql("managed");
		sql = sql + getBidSql();
		
		return sql;
	}
	
	/**
	 * ��ȡ ����ǽ�豸 sql ���
	 * @return
	 */
	public String getFirewallSql(){
		
		String sql = " where category=8";
		
		sql = sql + getMonFlagSql("managed");
		sql = sql + getBidSql();
		
		return sql;
	}
	
	/**
	 * ��ȡ �ʼ���ȫ�����豸 sql ���
	 * @return
	 */
	public String getGatewaySql(){
		
		String sql = " where category=10";
		
		sql = sql + getMonFlagSql("managed");
		sql = sql + getBidSql();
		
		return sql;
	}
	/**
	 * ��ȡ ���ؾ����豸 sql ���
	 * @return
	 */
	public String getF5Sql(){
		
		String sql = " where category=11";
		
		sql = sql + getMonFlagSql("managed");
		sql = sql + getBidSql();
		
		return sql;
	}
	
	/**
	 * ��ȡ VPN�豸 sql ���
	 * @return
	 */
	public String getVPNSql(){
		
		String sql = " where category=12";
		
		sql = sql + getMonFlagSql("managed");
		sql = sql + getBidSql();
		
		return sql;
	}
	
	/**
	 * ��ȡ hillstone �б�
	 * @return
	 */
	public List<BaseVo> getNet_HillstoneList(){
		// cisco
		
		List<BaseVo> list = getHostNode(getNet_HillstoneSql());
		
		if(list == null){
			list = new ArrayList<BaseVo>();
		}
		
		return list;
	}
	/**
	 * ��ȡ cisco�ʼ���ȫ���� �б�
	 * @return
	 */
	public List<BaseVo> getGateway_CiscoList(){
		// cisco
		
		List<BaseVo> list = getHostNode(getGateway_CiscoSql());
		
		if(list == null){
			list = new ArrayList<BaseVo>();
		}
		
		return list;
	}
	
	/**
	 * ��ȡ hillstone Sql ���
	 * @return
	 */
	public String getNet_HillstoneSql(){
		//cisco
		
		String sql = " where sys_oid like '1.3.6.1.4.1.28557%' and category=8";
		
		sql = sql + getMonFlagSql("monflag");
		
		sql = sql + getBidSql();
		
		return sql;
	}
	
	/**
	 * ��ȡ cisco�ʼ���ȫ���� Sql ���
	 * @return
	 */
	public String getGateway_CiscoSql(){
		//cisco
		
		String sql = " where sys_oid like '1.3.6.1.4.1.15497%' and category=10";
		
		sql = sql + getMonFlagSql("monflag");
		
		sql = sql + getBidSql();
		
		return sql;
	}
	
	/**
	 * ��ȡ ibm �б�
	 * @return
	 */
	public List<BaseVo> getNet_IbmList(){
		// cisco
		
		List<BaseVo> list = getHostNode(getNet_IbmSql());
		
		if(list == null){
			list = new ArrayList<BaseVo>();
		}
		
		return list;
	}
	
	/**
	 * ��ȡ Brocade �б�
	 * @return
	 */
	public List<BaseVo> getNet_BrocadeList(){
		// cisco
		
		List<BaseVo> list = getHostNode(getNet_BrocadeSql());
		
		if(list == null){
			list = new ArrayList<BaseVo>();
		}
		
		return list;
	}
	
	/**
	 * ��ȡ nokia �б�
	 * @return
	 */
	public List<BaseVo> getNet_NokiaList(){
		// cisco
		
		List<BaseVo> list = getHostNode(getNet_NokiaSql());
		
		if(list == null){
			list = new ArrayList<BaseVo>();
		}
		
		return list;
	}
	
	/**
	 * ��ȡ ibm Sql ���
	 * @return
	 */
	public String getNet_IbmSql(){
		//cisco
		
		String sql = " where sys_oid like '1.3.6.1.4.1.1588%' and (category<4 or category=6 or category=7)";
		
		sql = sql + getMonFlagSql("monflag");
		
		sql = sql + getBidSql();
		
		return sql;
	}
	
	/**
	 * ��ȡ ibm Sql ���
	 * @return
	 */
	public String getNet_BrocadeSql(){
		//cisco
		
		String sql = " where sys_oid like '1.3.6.1.4.1.1588%' and (category<4 or category=6 or category=7)";
		
		sql = sql + getMonFlagSql("monflag");
		
		sql = sql + getBidSql();
		
		return sql;
	}
	
	/**
	 * ��ȡ nokia Sql ���
	 * @return
	 */
	public String getNet_NokiaSql(){
		//cisco
		
		String sql = " where sys_oid like '1.3.6.1.4.1.94%' and category=8";
		
		sql = sql + getMonFlagSql("monflag");
		
		sql = sql + getBidSql();
		
		return sql;
	}
	
	/**
	 * ��ȡ netscreen �б�
	 * @return
	 */
	public List<BaseVo> getNet_NetscreenList(){
		// cisco
		
		List<BaseVo> list = getHostNode(getNet_NetscreenSql());
		
		if(list == null){
			list = new ArrayList<BaseVo>();
		}
		
		return list;
	}
	
	/**
	 * ��ȡ netscreen Sql ���
	 * @return
	 */
	public String getNet_NetscreenSql(){
		//cisco
		
		String sql = " where sys_oid like '1.3.6.1.4.1.3224%' or '1.3.6.1.4.1.2636%' and category=8";
		
		sql = sql + getMonFlagSql("monflag");
		
		sql = sql + getBidSql();
		
		return sql;
	}
	
	/**
	 * ��ȡ cisco �б�
	 * @return
	 */
	public List<BaseVo> getNet_CiscoList(){
		// cisco
		
		List<BaseVo> list = getHostNode(getNet_CiscoSql());
		
		if(list == null){
			list = new ArrayList<BaseVo>();
		}
		
		return list;
	}
	
	/**
	 * ��ȡ cisco Sql ���
	 * @return
	 */
	public String getNet_CiscoSql(){
		//cisco
		
		String sql = " where sys_oid like '1.3.6.1.4.1.9.%' and (category<4 or category=7 or category=10)";
		
		sql = sql + getMonFlagSql("managed");
		
		sql = sql + getBidSql();
		
		return sql;
	}
	
	/**
	 * ��ȡ h3c �б�
	 * @return
	 */
	public List<BaseVo> getNet_H3cList(){
		// h3c
		
		List<BaseVo> list = getHostNode(getNet_H3cSql());
		
		if(list == null){
			list = new ArrayList<BaseVo>();
		}
		
		return list;
	}
	
	/**
	 * ��ȡ h3c Sql ���
	 * @return
	 */
	public String getNet_H3cSql(){
		//h3c
		
		String sql = " where (sys_oid like '1.3.6.1.4.1.2011.%' or sys_oid like '1.3.6.1.4.1.25506.%') and (category<4 or category=7)";
		
		sql = sql + getMonFlagSql("managed");
		
		sql = sql + getBidSql();
		
		return sql;
	}
	
	
	/**
	 * ��ȡ entrasys �б�
	 * @return
	 */
	public List<BaseVo> getNet_EntrasysList(){
		// entrasys
		
		List<BaseVo> list = getHostNode(getNet_EntrasysSql());
		
		if(list == null){
			list = new ArrayList<BaseVo>();
		}
		
		return list;
	}
	
	/**
	 * ��ȡ entrasys Sql ���
	 * @return
	 */
	public String getNet_EntrasysSql(){
		
		String sql = " where sys_oid like '1.3.6.1.4.1.9.2.1.57.%' and (category<4 or category=7)";
		
		sql = sql + getMonFlagSql("managed");
		
		sql = sql + getBidSql();
		
		return sql;
	}
	
	
	/**
	 * ��ȡ radware �б�
	 * @return
	 */
	public List<BaseVo> getNet_RadwareList(){
		// radware
		
		List<BaseVo> list = getHostNode(getNet_RadwareSql());
		
		if(list == null){
			list = new ArrayList<BaseVo>();
		}
		
		return list;
	}
	
	/**
	 * ��ȡ radware Sql ���
	 * @return
	 */
	public String getNet_RadwareSql(){
		
		String sql = " where sys_oid like '1.3.6.1.4.1.89.%' and (category<4 or category=7)";
		
		sql = sql + getMonFlagSql("managed");
		
		sql = sql + getBidSql();
		
		return sql;
	}
	
	
	/**
	 * ��ȡ maipu �б�
	 * @return
	 */
	public List<BaseVo> getNet_MaipuList(){
		// maipu
		
		List<BaseVo> list = getHostNode(getNet_MaipuSql());
		
		if(list == null){
			list = new ArrayList<BaseVo>();
		}
		
		return list;
	}
	
	/**
	 * ��ȡ maipu Sql ���
	 * @return
	 */
	public String getNet_MaipuSql(){
		
		String sql = " where sys_oid like '1.3.6.1.4.1.5651.%' and (category<4 or category=7)";
		
		sql = sql + getMonFlagSql("managed");
		
		sql = sql + getBidSql();
		
		return sql;
	}
	
	
	/**
	 * ��ȡ redgiant �б�
	 * @return
	 */
	public List<BaseVo> getNet_RedgiantList(){
		// redgiant
		
		List<BaseVo> list = getHostNode(getNet_RedgiantSql());
		
		if(list == null){
			list = new ArrayList<BaseVo>();
		}
		
		return list;
	}
	
	/**
	 * ��ȡ redgiant Sql ���
	 * @return
	 */
	public String getNet_RedgiantSql(){
		
		String sql = " where sys_oid like '1.3.6.1.4.1.4881.%' and (category<4 or category=7)";
		
		sql = sql + getMonFlagSql("managed");
		
		sql = sql + getBidSql();
		
		return sql;
	}
	
	/**
	 * ��ȡ northtel �б�
	 * @return
	 */
	public List<BaseVo> getNet_NorthtelList(){
		// northtel
		
		List<BaseVo> list = getHostNode(getNet_NorthtelSql());
		
		if(list == null){
			list = new ArrayList<BaseVo>();
		}
		
		return list;
	}
	
	/**
	 * ��ȡ northtel Sql ���
	 * @return
	 */
	public String getNet_NorthtelSql(){
		
		String sql = " where sys_oid like '1.3.6.1.4.1.45.%' and (category<4 or category=7)";
		
		sql = sql + getMonFlagSql("managed");
		
		sql = sql + getBidSql();
		
		return sql;
	}
	
	
	
	/**
	 * ��ȡ dlink �б�
	 * @return
	 */
	public List<BaseVo> getNet_DlinklList(){
		// dlink
		
		List<BaseVo> list = getHostNode(getNet_DlinkSql());
		
		if(list == null){
			list = new ArrayList<BaseVo>();
		}
		
		return list;
	}
	
	/**
	 * ��ȡ dlink Sql ���
	 * @return
	 */
	public String getNet_DlinkSql(){
		
		String sql = " where sys_oid like '1.3.6.1.4.1.171.%' and (category<4 or category=7)";
		
		sql = sql + getMonFlagSql("managed");
		
		sql = sql + getBidSql();
		
		return sql;
	}
	
	
	/**
	 * ��ȡ bdcom �б�
	 * @return
	 */
	public List<BaseVo> getNet_BdcomlList(){
		// bdcom
		
		List<BaseVo> list = getHostNode(getNet_BdcomSql());
		
		if(list == null){
			list = new ArrayList<BaseVo>();
		}
		
		return list;
	}
	
	/**
	 * ��ȡ bdcom Sql ���
	 * @return
	 */
	public String getNet_BdcomSql(){
		
		String sql = " where sys_oid like '1.3.6.1.4.1.3320.%' and (category<4 or category=7)";
		
		sql = sql + getMonFlagSql("managed");
		
		sql = sql + getBidSql();
		
		return sql;
	}
	
	
	/**
	 * ��ȡ zte �б�
	 * @return
	 */
	public List<BaseVo> getNet_ZtelList(){
		// zte
		
		List<BaseVo> list = getHostNode(getNet_ZteSql());
		
		if(list == null){
			list = new ArrayList<BaseVo>();
		}
		
		return list;
	}
	
	/**
	 * ��ȡ zte Sql ���
	 * @return
	 */
	public String getNet_ZteSql(){
		
		String sql = " where sys_oid like '1.3.6.1.4.1.3902.%'";
		
		sql = sql + getMonFlagSql("managed");
		
		sql = sql + getBidSql();
		
		return sql;
	}
	
	
	
	
	
	//--------------------------------------------------------------------------------------------
	//                         ������
	//--------------------------------------------------------------------------------------------
	
	
	
	/**
	 * ��ȡ ������ �б� 
	 * @return
	 */
	public List<BaseVo> getHostList(){
		List<BaseVo> list = getHostNode(getHostSql());
		return list;
	}
	
	
	/**
	 * ��ȡ ������ sql ���
	 * @return
	 */
	public String getHostSql(){
		
		String sql = " where category=4";
		
		sql = sql + getMonFlagSql("managed");
		
		sql = sql + getBidSql();
		
		//SysLogger.info(sql);
		return sql;
	}
	
	/**
	 * ��ȡ windows ������ �б� 
	 * @return
	 */
	public List<BaseVo> getHost_WindowsList(){
		// windows
		List<BaseVo> list = getHostNode(getHost_WindowsSql());
		return list;
	}
	
	
	/**
	 * ��ȡ windows ������ sql ���
	 * @return
	 */
	public String getHost_WindowsSql(){
		
		String sql = " where sys_oid like '1.3.6.1.4.1.311.%' and category=4";
		
		sql = sql + getMonFlagSql("managed");
		
		sql = sql + getBidSql();
		
		return sql;
	}
	
	
	/**
	 * ��ȡ linux ������ �б� 
	 * @return
	 */
	public List<BaseVo> getHost_LinuxList(){
		// linux
		List<BaseVo> list = getHostNode(getHost_LinuxSql());
		return list;
	}
	
	/**
	 * ��ȡ linux ������ sql ���
	 * @return
	 */
	public String getHost_LinuxSql(){
		
		String sql = " where sys_oid like '1.3.6.1.4.1.2021.%' and category=4";
		
		sql = sql + getMonFlagSql("managed");
		
		sql = sql + getBidSql();
		//SysLogger.info(sql);
		return sql;
	}
	
	
	
	/**
	 * ��ȡ aix ������ �б� 
	 * @return
	 */
	public List<BaseVo> getHost_AixList(){
		// aix
		List<BaseVo> list = getHostNode(getHost_AixSql());
		return list;
	}
	
	/**
	 * ��ȡ scounix ������ �б� 
	 * @return
	 */
	public List<BaseVo> getHost_ScounixList(){
		// aix
		List<BaseVo> list = getHostNode(getHost_ScounixSql());
		return list;
	}
	/**
	 * ��ȡ scoopenserver ������ �б� 
	 * @return
	 */
	public List<BaseVo> getHost_ScoopenserverList(){
		// aix
		List<BaseVo> list = getHostNode(getHost_ScoopenserverSql());
		return list;
	}
	
	/**
	 * ��ȡ aix ������ sql ���
	 * @return
	 */
	public String getHost_AixSql(){
		
		String sql = " where sys_oid like '1.3.6.1.4.1.2.%' and category=4";
		
		sql = sql + getMonFlagSql("managed");
		
		sql = sql + getBidSql();
		
		return sql;
	}
	
	/**
	 * ��ȡ scounix ������ sql ���
	 * @return
	 */
	public String getHost_ScounixSql(){
		
		String sql = " where sys_oid like 'scounix%' and category=4";
		
		sql = sql + getMonFlagSql("monflag");
		
		sql = sql + getBidSql();
		
		return sql;
	}
	
	/**
	 * ��ȡ scounix ������ sql ���
	 * @return
	 */
	public String getHost_ScoopenserverSql(){
		
		String sql = " where sys_oid like 'scoopen%' and category=4";
		
		sql = sql + getMonFlagSql("monflag");
		
		sql = sql + getBidSql();
		
		return sql;
	}
	
	/**
	 * ��ȡ hpunix ������ �б� 
	 * @return
	 */
	public List<BaseVo> getHost_HpunixList(){
		// hpunix
		List<BaseVo> list = getHostNode(getHost_HpunixSql());
		return list;
	}
	
	
	/**
	 * ��ȡ hpunix ������ sql ���
	 * @return
	 */
	public String getHost_HpunixSql(){
		
		String sql = " where sys_oid like '1.3.6.1.4.1.11.%' and category=4";
		
		sql = sql + getMonFlagSql("managed");
		
		sql = sql + getBidSql();
		
		return sql;
	}
	
	
	/**
	 * ��ȡ solaris ������ �б� 
	 * @return
	 */
	public List<BaseVo> getHost_SolarisList(){
		// solaris
		List<BaseVo> list = getHostNode(getHost_SolarisSql());
		return list;
	}
	
	
	/**
	 * ��ȡ solaris ������ sql ���
	 * @return
	 */
	public String getHost_SolarisSql(){
		
		String sql = " where sys_oid like '1.3.6.1.4.1.42.%' and category=4";
		
		sql = sql + getMonFlagSql("managed");
		
		sql = sql + getBidSql();
		
		return sql;
	}
	
	
	/**
	 * ��ȡ as400 ������ �б� 
	 * @return
	 */
	public List<BaseVo> getHost_As400List(){
		// as400
		List<BaseVo> list = getHostNode(getHost_As400Sql());
		return list;
	}
	
	
	/**
	 * ��ȡ as400 ������ sql ���
	 * @return
	 */
	public String getHost_As400Sql(){
		
		String sql = " where sys_oid like 'as400%' and category=4";
		
		sql = sql + getMonFlagSql("managed");
		
		sql = sql + getBidSql();
		
		return sql;
	}
	
	
	//--------------------------------------------------------------------------------------------
	//                         ���ݿ�
	//--------------------------------------------------------------------------------------------
	
	
	
	
	
	/**
	 * ��ȡ���ݿ�
	 * @return
	 */
	public List<BaseVo> getDBList(){
		List<BaseVo> list = new ArrayList<BaseVo>();
		list.addAll(getDB_MysqlList());
		list.addAll(getDB_DB2List());
		list.addAll(getDB_InformixList());
		list.addAll(getDB_SQLServerList());
		list.addAll(getDB_SybaseList());
		list.addAll(getDB_OracleList());
		return list;
	}
	
	
	public List<BaseVo> getDB(String condition){
		List<BaseVo> list = null;
		DBDao dao = new DBDao();
		try {
			list = dao.findByCondition(condition);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			dao.close();
		}
		
		return list;
	}
	
	/**
	 * ��ȡ oracle ���ݿ� �б�
	 * @return
	 */
	public List<BaseVo> getDB_OracleList(){
		// oracle
		List<BaseVo> list = null;
		OraclePartsDao oraclePartsDao = new OraclePartsDao();
		try {
			list = oraclePartsDao.findByCondition(getDB_OracleSql());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			oraclePartsDao.close();
		}
		
		if( list == null){
			list = new ArrayList<BaseVo>();
		}
		return list;
	}
	
	/**
	 * ��ȡ oracle ���ݿ� Sql ���
	 * @return
	 */
	public String getDB_OracleSql(){
		
		
		String sql = " where 1=1" ;
		
		sql = sql + getMonFlagSql("managed");
		
		sql = sql + getBidSql();
		
		return sql;
		
	}
	
	/**
	 * ��ȡ mysql ���ݿ� �б�
	 * @return
	 */
	public List<BaseVo> getDB_MysqlList(){
		List<BaseVo> list = getDB(getDB_MysqlSql());
		return list;
	}
	
	/**
	 * ��ȡ mysql ���ݿ� Sql ���
	 * @return
	 */
	public String getDB_MysqlSql(){
		
		DBTypeVo dBTypeVo = null;
		DBTypeDao dBTypeDao = new DBTypeDao();
		try {
			dBTypeVo = dBTypeDao.findByDbtype("mysql");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			dBTypeDao.close();
		}
		
		String sql = " where dbtype=" + dBTypeVo.getId();
		
		sql = sql + getMonFlagSql("managed");
		
		sql = sql + getBidSql();
		
		return sql;
		
	}
	
	/**
	 * ��ȡ DB2 ���ݿ� �б�
	 * @return
	 */
	public List<BaseVo> getDB_DB2List(){
		List<BaseVo> list = getDB(getDB_DB2Sql());
		return list;
	}
	
	/**
	 * ��ȡ DB2 ���ݿ� Sql ���
	 * @return
	 */
	public String getDB_DB2Sql(){
		
		DBTypeVo dBTypeVo = null;
		DBTypeDao dBTypeDao = new DBTypeDao();
		try {
			dBTypeVo = dBTypeDao.findByDbtype("db2");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			dBTypeDao.close();
		}
		
		String sql = " where dbtype=" + dBTypeVo.getId();
		
		sql = sql + getMonFlagSql("managed");
		
		sql = sql + getBidSql();
		
		return sql;
		
	}
	
	/**
	 * ��ȡ SQLServer ���ݿ� �б�
	 * @return
	 */
	public List<BaseVo> getDB_SQLServerList(){
		// SQLServer
		List<BaseVo> list = getDB(getDB_SQLServerSql());
		return list;
	}
	
	/**
	 * ��ȡ SQLServer ���ݿ� Sql ���
	 * @return
	 */
	public String getDB_SQLServerSql(){
		
		DBTypeVo dBTypeVo = null;
		DBTypeDao dBTypeDao = new DBTypeDao();
		try {
			dBTypeVo = dBTypeDao.findByDbtype("sqlserver");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			dBTypeDao.close();
		}
		
		String sql = " where dbtype=" + dBTypeVo.getId();
		
		sql = sql + getMonFlagSql("managed");
		
		sql = sql + getBidSql();
		
		return sql;
		
	}
	
	/**
	 * ��ȡ Sybase ���ݿ� �б�
	 * @return
	 */
	public List<BaseVo> getDB_SybaseList(){
		// Sybase
		List<BaseVo> list = getDB(getDB_SybaseSql());
		return list;
	}
	
	/**
	 * ��ȡ Sybase ���ݿ� Sql ���
	 * @return
	 */
	public String getDB_SybaseSql(){
		
		DBTypeVo dBTypeVo = null;
		DBTypeDao dBTypeDao = new DBTypeDao();
		try {
			dBTypeVo = dBTypeDao.findByDbtype("sybase");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			dBTypeDao.close();
		}
		
		String sql = " where dbtype=" + dBTypeVo.getId();
		
		sql = sql + getMonFlagSql("managed");
		
		sql = sql + getBidSql();
		
		return sql;
		
	}
	
	/**
	 * ��ȡ Informix ���ݿ� �б�
	 * @return
	 */
	public List<BaseVo> getDB_InformixList(){
		// Informix
		List<BaseVo> list = getDB(getDB_InformixSql());
		return list;
	}
	
	/**
	 * ��ȡ Informix ���ݿ� Sql ���
	 * @return
	 */
	public String getDB_InformixSql(){
		
		DBTypeVo dBTypeVo = null;
		DBTypeDao dBTypeDao = new DBTypeDao();
		try {
			dBTypeVo = dBTypeDao.findByDbtype("informix");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			dBTypeDao.close();
		}
		
		String sql = " where dbtype=" + dBTypeVo.getId();
		
		sql = sql + getMonFlagSql("managed");
		
		sql = sql + getBidSql();
		
		return sql;
		
	}
	
	
	
	
	
	//-------------------------------------------------------------------------------------------------
	//                              �м��
	//-------------------------------------------------------------------------------------------------
	
	/**
	 * ��ȡ �м�� �б�
	 */
	public List<BaseVo> getMiddlewareList(){
		
		List<BaseVo> list = new ArrayList<BaseVo>();
		
		list.addAll(getMiddleware_ApacheList());
		list.addAll(getMiddleware_CICSList());
		list.addAll(getMiddleware_DNSList());
		list.addAll(getMiddleware_DominoList());
		list.addAll(getMiddleware_IISList());
		list.addAll(getMiddleware_JBossList());
		list.addAll(getMiddleware_MQList());
		list.addAll(getMiddleware_TomcatList());
		list.addAll(getMiddleware_TuxedoList());
		list.addAll(getMiddleware_WasList());
		list.addAll(getMiddleware_WeblogicList());
		
		
		return list;
	}
	
	
	/**
	 * ��ȡ Tomcat �м�� �б�
	 * @return
	 */
	public List<BaseVo> getMiddleware_TomcatList(){
		//Tomcat
		
		List<BaseVo> list = null;
		
		TomcatDao tomcatdao = new TomcatDao();
		try{
			list = tomcatdao.findByCondition(getMiddleware_TomcatSql());
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			tomcatdao.close();
		}
		
		if(list == null){
			list = new ArrayList<BaseVo>();
		}
		
		return list;
	}
	
	/**
	 * ��ȡ Tomcat Sql ���
	 * @return
	 */
	public String getMiddleware_TomcatSql(){
		
		String sql = " where 1=1";
		
		sql = sql + getMonFlagSql("monflag");
		
		sql = sql + getBidSql();
		
		return sql;
	}
	
	/**
	 * ��ȡ MQ �б�
	 * @return
	 */
	public List<BaseVo> getMiddleware_MQList(){
		//mq
		MQConfigDao mqconfigdao = new MQConfigDao();
		List<BaseVo> list = null;
		try{
			list = mqconfigdao.findByCondition(getMiddleware_MQSql());
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			mqconfigdao.close();
		}
		
		if(list == null){
			list = new ArrayList<BaseVo>();
		}
		
		return list;
	}
	
	/**
	 * ��ȡ MQ Sql ���
	 * @return
	 */
	public String getMiddleware_MQSql(){
		String sql = " where 1=1";
		
		sql = sql + getMonFlagSql("mon_flag");
		
		sql = sql + getBidSql();
		
		return sql;
	}
	

	/**
	 * ��ȡ Domino �б�
	 * @return
	 */
	public List<BaseVo> getMiddleware_DominoList(){
		//domimo
		DominoConfigDao dominoconfigdao = new DominoConfigDao();	
		List<BaseVo> list = null;
		try{
			list = dominoconfigdao.findByCondition(getMiddleware_DominoSql());
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			dominoconfigdao.close();
		}
		
		if(list == null){
			list = new ArrayList<BaseVo>();
		}
		
		return list;
	}
	
	
	/**
	 * ��ȡ Domino Sql ���
	 * @return
	 */
	public String getMiddleware_DominoSql(){
		String sql = " where 1=1";
		
		sql = sql + getMonFlagSql("mon_flag");
		
		sql = sql + getBidSql();
		
		return sql;
	}
	
	/**
	 * ��ȡ Was �б�
	 * @return
	 */
	public List<BaseVo> getMiddleware_WasList(){
		//was
		WasConfigDao wasconfigdao = new WasConfigDao();
		List<BaseVo> list = null;
		try{
			list = wasconfigdao.findByCondition(getMiddleware_WasSql());
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			wasconfigdao.close();
		}
		
		if(list == null){
			list = new ArrayList<BaseVo>();
		}
		
		return list;
	}
	
	
	/**
	 * ��ȡ Was Sql ���
	 * @return
	 */
	public String getMiddleware_WasSql(){
		String sql = " where 1=1";
		
		sql = sql + getMonFlagSql("mon_flag");
		
		sql = sql + getBidSql();
		
		return sql;
	}
	
	
	
	/**
	 * ��ȡ Weblogic �б�
	 * @return
	 */
	public List<BaseVo> getMiddleware_WeblogicList(){
		//weblogic
		//SysLogger.info("########################2");
		WeblogicConfigDao weblogicconfigdao = new WeblogicConfigDao();
		List<BaseVo> list = null;
		try{
			list = weblogicconfigdao.findByCondition(getMiddleware_WeblogicSql());
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			weblogicconfigdao.close();
		}
		if(list == null){
			list = new ArrayList<BaseVo>();
		}
		return list;
	}
	
	
	/**
	 * ��ȡ Weblogic Sql ���
	 * @return
	 */
	public String getMiddleware_WeblogicSql(){
		String sql = " where 1=1";
		
		sql = sql + getMonFlagSql("mon_flag");
		
		sql = sql + getBidSql();
		
		return sql;
	}
	
	
	
	/**
	 * ��ȡ IIS �б�
	 * @return
	 */
	public List<BaseVo> getMiddleware_IISList(){
		//IIS
		IISConfigDao iisconfigdao = new IISConfigDao();	
		List<BaseVo> list = null;
		try{
			list = iisconfigdao.findByCondition(getMiddleware_IISSql());
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			iisconfigdao.close();
		}
		if(list == null){
			list = new ArrayList<BaseVo>();
		}
		
		return list;
	}
	
	
	/**
	 * ��ȡ IIS Sql ���
	 * @return
	 */
	public String getMiddleware_IISSql(){
		String sql = " where 1=1";
		
		sql = sql + getMonFlagSql("mon_flag");
		
		sql = sql + getBidSql();
		
		return sql;
	}
	
	
	
	
	
	/**
	 * ��ȡ cics �б�
	 * @return
	 */
	public List<BaseVo> getMiddleware_CICSList(){
		//cics
		List<BaseVo> list = null;
		CicsConfigDao cicsconfigdao = new CicsConfigDao();
		try{
			list = cicsconfigdao.findByCondition(getMiddleware_CicsSql());
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			cicsconfigdao.close();
		}	
		
		if(list == null){
			list = new ArrayList<BaseVo>();
		}
		
		return list;
	}
	
	
	/**
	 * ��ȡ cics Sql ���
	 * @return
	 */
	public String getMiddleware_CicsSql(){
		String sql = " where 1=1";
		
		sql = sql + getMonFlagSql("flag");
		
		sql = sql + getBidSql();
		
		return sql;
	}
	
	/**
	 * ��ȡ DNS �б�
	 * @return
	 */
	public List<BaseVo> getMiddleware_DNSList(){
		//DNS
		DnsConfigDao dnsconfigdao = new DnsConfigDao();	
		List<BaseVo> list = null;
		try{
			list = dnsconfigdao.findByCondition(getMiddleware_DNSSql());
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			dnsconfigdao.close();
		}
		
		if(list == null){
			list = new ArrayList<BaseVo>();
		}
		
		return list;
	}
	
	
	/**
	 * ��ȡ DNS Sql ���
	 * @return
	 */
	public String getMiddleware_DNSSql(){
		String sql = " where 1=1";
		
		sql = sql + getMonFlagSql("flag");
		
		sql = sql + getBidSql();
		
		return sql;
	}
	
	/**
	 * ��ȡ JBoss �б�
	 * @return
	 */
	public List<BaseVo> getMiddleware_JBossList(){
		//JBoss
		JBossConfigDao jbossConfigDao = new JBossConfigDao();
		List<BaseVo> list = null;
		try{
			list = jbossConfigDao.findByCondition(getMiddleware_JBossSql());
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			jbossConfigDao.close();
		}
		
		if(list == null){
			list = new ArrayList<BaseVo>();
		}
		
		return list;
	}
	
	
	/**
	 * ��ȡ JBoss Sql ���
	 * @return
	 */
	public String getMiddleware_JBossSql(){
		String sql = " where 1=1";
		
		sql = sql + getMonFlagSql("flag");
		
		sql = sql + getBidSql();
		
		return sql;
	}
	
	
	/**
	 * ��ȡ Apache �б�
	 * @return
	 */
	public List<BaseVo> getMiddleware_ApacheList(){
		//Apache
		ApacheConfigDao apacheConfigDao = new ApacheConfigDao();
		List<BaseVo> list = null;
		try{
			list = apacheConfigDao.findByCondition(getMiddleware_ApacheSql());
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			apacheConfigDao.close();
		}
		
		if(list == null){
			list = new ArrayList<BaseVo>();
		}
		
		return list;
	}
	
	
	/**
	 * ��ȡ Apache Sql ���
	 * @return
	 */
	public String getMiddleware_ApacheSql(){
		String sql = " where 1=1";
		
		sql = sql + getMonFlagSql("flag");
		
		sql = sql + getBidSql();
		
		return sql;
	}
	
	/**
	 * ��ȡ Tuxedo �б�
	 * @return
	 */
	public List<BaseVo> getMiddleware_TuxedoList(){
		//Tuxedo
		TuxedoConfigDao tuxedoConfigDao = new TuxedoConfigDao();
		List<BaseVo> list = null;
		try{
			list = tuxedoConfigDao.findByCondition(getMiddleware_TuxedoSql());
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			tuxedoConfigDao.close();
		}
		
		if(list == null){
			list = new ArrayList<BaseVo>();
		}
		
		return list;
	}
	
	/**
	 * ��ȡ Tuxedo Sql ���
	 * @return
	 */
	public String getMiddleware_TuxedoSql(){
		String sql = " where 1=1";
		
		sql = sql + getMonFlagSql("mon_flag");
		
		sql = sql + getBidSql();
		
		return sql;
	}
	
	
	//---------------------------------------------------------------------------------------
	//                     ���� 
	//---------------------------------------------------------------------------------------
	
	/**
	 * ��ȡ ���� �б�
	 * @return
	 */
	public List<BaseVo> getServiceList(){
		
		List<BaseVo> list = new ArrayList<BaseVo>();
		
		list.addAll(getService_EmailList());
		list.addAll(getService_FtpList());
		list.addAll(getService_PSTypeVoList());
		list.addAll(getService_WebList());
		
		return list;
	}
	
	/**
	 * ��ȡ Email ���� �б�
	 * @return
	 */
	public List<BaseVo> getService_EmailList(){
		// mail
		List<BaseVo> list = null;
		
		EmailConfigDao emailConfigDao = new EmailConfigDao();
		try{
			list = emailConfigDao.findByCondition(getService_EmailSql());
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			emailConfigDao.close();
		}
		
		if(list == null){
			list = new ArrayList<BaseVo>();
		}
		
		return list;
	}
	
	/**
	 * ��ȡ Email Sql ���
	 * @return
	 */
	public String getService_EmailSql(){
		
		String sql = " where 1=1";
		
		sql = sql + getMonFlagSql("monflag");
		
		sql = sql + getBidSql();
		
		return sql;
	}
	
	/**
	 * ��ȡ FTP ���� �б�
	 * @return
	 */
	public List<BaseVo> getService_FtpList(){
		// FTP
		
		List<BaseVo> list = null;
		
		FTPConfigDao ftpConfigDao = new FTPConfigDao();
		try{
			list = ftpConfigDao.findByCondition(getService_FtpSql());
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			ftpConfigDao.close();
		}
		
		if(list == null){
			list = new ArrayList<BaseVo>();
		}
		
		return list;
	}
	
	/**
	 * ��ȡ Ftp Sql ���
	 * @return
	 */
	public String getService_FtpSql(){
		
		String sql = " where 1=1";
		
		sql = sql + getMonFlagSql("monflag");
		
		sql = sql + getBidSql();
		
		return sql;
	}
	
	
	/**
	 * ��ȡ Web ���� �б�
	 * @return
	 */
	public List<BaseVo> getService_WebList(){
		// WEB
		
		List<BaseVo> list = null;
		
		WebConfigDao webConfigDao = new WebConfigDao();
		try{
			list = webConfigDao.findByCondition(getService_WebSql());
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			webConfigDao.close();
		}
		
		if(list == null){
			list = new ArrayList<BaseVo>();
		}
		
		return list;
	}
	
	/**
	 * ��ȡ Web Sql ���
	 * @return
	 */
	public String getService_WebSql(){
		
		String sql = " where 1=1";
		
		sql = sql + getMonFlagSql("flag");
		
		sql = sql + getBidSql();
		
		return sql;
	}
	
	
	/**
	 * ��ȡ PSTypeVo �б�
	 * @return
	 */
	public List<BaseVo> getService_PSTypeVoList(){
		// PSTypeVo
		
		List<BaseVo> list = null;
		
		PSTypeDao psTypeDao = new PSTypeDao();
		try{
			list = psTypeDao.findByCondition(getService_PSTypeVoSql());
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			psTypeDao.close();
		}
		
		if(list == null){
			list = new ArrayList<BaseVo>();
		}
		
		return list;
	}
	
	/**
	 * ��ȡ PSTypeVo Sql ���
	 * @return
	 */
	public String getService_PSTypeVoSql(){
		
		String sql = " where 1=1";
		
		sql = sql + getMonFlagSql("monflag");
		
		sql = sql + getBidSql();
		
		return sql;
	}
	
	
	//--------------------------------------------------------------------------------------------
	//                         ����ͼ
	//--------------------------------------------------------------------------------------------
	
	
	
	/**
	 * ��ȡ ҵ�� ����ͼ �б�
	 * @return
	 */
	public List<BaseVo> getTopo_BusinessList(){
		//Tomcat
		
		List<BaseVo> list = null;
		
		ManageXmlDao manageXmlDao = new ManageXmlDao();
		try{
			list = manageXmlDao.findByCondition(getTopo_BusinessSql());
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			manageXmlDao.close();
		}
		
		if(list == null){
			list = new ArrayList<BaseVo>();
		}
		
		return list;
	}
	
	/**
	 * ��ȡ ҵ�� ����ͼ ���
	 * @return
	 */
	public String getTopo_BusinessSql(){
		
		String sql = " where 1=1 and topo_type='1'";
		
		// ��������ͼ��ʱδ���Ƿ����ѡ�� �� ע��
		//sql = sql + getMonFlagSql("monflag");
		
		sql = sql + getBidSql();
		
		return sql;
	}
	
	
	
	/**
	 * �����ֶ��� ��ȡ �Ƿ���  ���δ���� �Ƿ��� �� ���� ""
	 * @param fieldName
	 * @return
	 */
	public String getMonFlagSql(String fieldName){
		String sql = "";
		if(isSetedMonitorFlag){
			String mon_flag = monitorFlag;
	    	if(mon_flag !=null ){
	    		sql = " and "+ fieldName + "='" + monitorFlag +"'";
	    	}
		}
    	return sql;
	}
	
	

	public String getConditionSQL(){
		String sql = "";
		if(isSetedBid){
			sql = getBidSql();
		}
		
		if(isSetedMonitorFlag){
		}
		
		return sql;
	}
	
	public String getBidSql(){
		String sql = "";
		if(isSetedBid){
			sql = getBidSql(bid);
		}
		return sql;
	}
	
	public String getBidSql(String businessId){
		StringBuffer s = new StringBuffer();
		int _flag = 0;
		if (businessId != null){
			if(businessId !="-1"){
				String[] bids = businessId.split(",");
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
		String sql = s.toString();
		SysLogger.info(sql);
		return sql;
	}
	
	
	/**
	 * ͨ�� HostNode  ת���� NodeDTO 
	 * @param hostNode
	 * @return
	 */
	public NodeDTO creatNodeDTOByNode(HostNode hostNode){
		
		NodeDTO nodeDTO = new NodeDTO();
		
		nodeDTO.setId(hostNode.getId());
		nodeDTO.setName(hostNode.getAlias());
		nodeDTO.setIpaddress(hostNode.getIpAddress());
		nodeDTO.setNodeid(String.valueOf(hostNode.getId()));
		nodeDTO.setBusinessId(hostNode.getBid());
		
		String subtype = "";
		
		//SysLogger.info(hostNode.getIpAddress()+"===="+hostNode.getSysOid()+"========="+hostNode.getCategory());
		String type  = "";
		if(hostNode.getCategory() == 4){
			type = Constant.TYPE_HOST;
			if(hostNode.getSysOid() != null && hostNode.getSysOid().trim().length()>0){
				if(hostNode.getSysOid().startsWith("1.3.6.1.4.1.311."))subtype = Constant.TYPE_HOST_SUBTYPE_WINDOWS;
				if(hostNode.getSysOid().startsWith("1.3.6.1.4.1.2021."))subtype = Constant.TYPE_HOST_SUBTYPE_LINUX;
				if(hostNode.getSysOid().startsWith("1.3.6.1.4.1.2."))subtype = Constant.TYPE_HOST_SUBTYPE_AIX;
				if(hostNode.getSysOid().startsWith("1.3.6.1.4.1.11."))subtype = Constant.TYPE_HOST_SUBTYPE_HPUNIX;
				if(hostNode.getSysOid().startsWith("1.3.6.1.4.1.42."))subtype = Constant.TYPE_HOST_SUBTYPE_SOLARIS;
				if(hostNode.getSysOid().startsWith("as400"))subtype = Constant.TYPE_HOST_SUBTYPE_AS400;
				if(hostNode.getSysOid().startsWith("scounix"))subtype = Constant.TYPE_HOST_SUBTYPE_SCOUNIX;
				if(hostNode.getSysOid().startsWith("scoopenserver"))subtype = Constant.TYPE_HOST_SUBTYPE_SCOOPENSERVER;
				if(hostNode.getOstype() == 15)subtype = Constant.TYPE_HOST_SUBTYPE_AS400;
			}
		} else if(hostNode.getCategory() == 9){
			type = Constant.TYPE_ATM;
			subtype = Constant.TYPE_ATM_SUBTYPE_ATM;
		} else if(hostNode.getCategory() == 12){
			type = Constant.TYPE_VPN;
			if(hostNode.getSysOid() != null && hostNode.getSysOid().trim().length()>0){
				if(hostNode.getSysOid().startsWith("1.3.6.1.4.1.7564"))subtype = Constant.TYPE_VPN_SUBTYPE_ARRAYNETWORKS;
			}
		} else if(hostNode.getCategory() == 8){
			type = Constant.TYPE_FIREWALL;
			if(hostNode.getSysOid() != null && hostNode.getSysOid().trim().length()>0){
				if(hostNode.getSysOid().startsWith("1.3.6.1.4.1.28557."))subtype = Constant.TYPE_FIREWALL_SUBTYPE_HILLSTONE;
				if(hostNode.getSysOid().startsWith("1.3.6.1.4.1.3224."))subtype = Constant.TYPE_FIREWALL_SUBTYPE_NETSCREEN;
				if(hostNode.getSysOid().startsWith("1.3.6.1.4.1.2636."))subtype = Constant.TYPE_FIREWALL_SUBTYPE_NETSCREEN;
				if(hostNode.getSysOid().startsWith("1.3.6.1.4.1.94."))subtype = Constant.TYPE_FIREWALL_SUBTYPE_NOKIA;
			}
		} else if(hostNode.getCategory() == 10){
			type = Constant.TYPE_GATEWAY;
			subtype = Constant.TYPE_GATEWAY_SUBTYPE_CISCO;
		} else if(hostNode.getCategory() == 11){
			type = Constant.TYPE_F5;
			subtype = Constant.TYPE_F5_SUBTYPE_F5;
		} else{
			//�����豸,��Ҫ�жϲ���ϵͳ
			type = Constant.TYPE_NET;
			if(hostNode.getSysOid() != null && hostNode.getSysOid().trim().length()>0){
				if(hostNode.getSysOid().startsWith("1.3.6.1.4.1.9."))subtype = Constant.TYPE_NET_SUBTYPE_CISCO;
				if(hostNode.getSysOid().startsWith("1.3.6.1.4.1.2011."))subtype = Constant.TYPE_NET_SUBTYPE_H3C;
				if(hostNode.getSysOid().startsWith("1.3.6.1.4.1.25506."))subtype = Constant.TYPE_NET_SUBTYPE_H3C;
				if(hostNode.getSysOid().startsWith("1.3.6.1.4.1.9.2.1.57."))subtype = Constant.TYPE_NET_SUBTYPE_ENTRASYS;
				if(hostNode.getSysOid().startsWith("1.3.6.1.4.1.89."))subtype = Constant.TYPE_NET_SUBTYPE_RADWARE;
				if(hostNode.getSysOid().startsWith("1.3.6.1.4.1.5651."))subtype = Constant.TYPE_NET_SUBTYPE_MAIPU;
				if(hostNode.getSysOid().startsWith("1.3.6.1.4.1.4881."))subtype = Constant.TYPE_NET_SUBTYPE_REDGIANT;
				if(hostNode.getSysOid().startsWith("1.3.6.1.4.1.45."))subtype = Constant.TYPE_NET_SUBTYPE_NORTHTEL;
				if(hostNode.getSysOid().startsWith("1.3.6.1.4.1.171."))subtype = Constant.TYPE_NET_SUBTYPE_DLINK;
				if(hostNode.getSysOid().startsWith("1.3.6.1.4.1.3320."))subtype = Constant.TYPE_NET_SUBTYPE_BDCOM;
				if(hostNode.getSysOid().startsWith("1.3.6.1.4.1.3902."))subtype = Constant.TYPE_NET_SUBTYPE_ZTE;
				if(hostNode.getSysOid().startsWith("1.3.6.1.4.1.388.11.1.2"))subtype = Constant.TYPE_NET_MOTOROLA;
				if(hostNode.getSysOid().startsWith("1.3.6.1.4.1.1588.2.1.1."))subtype = Constant.TYPE_NET_SUBTYPE_BROCADE;
//				if(hostNode.getSysOid().startsWith("1.3.6.1.4.1.1588"))subtype = Constant.TYPE_NET_SUBTYPE_IBM;
			}
		}

		nodeDTO.setSubtype(subtype);
		nodeDTO.setType(type);
		//SysLogger.info(nodeDTO.getIpaddress()+"==="+nodeDTO.getType()+"==="+nodeDTO.getSubtype()+"=="+nodeDTO.getIpaddress());
		return nodeDTO;
	}
	
	/**
	 * ͨ�� HostNode  ת���� NodeDTO 
	 * @param hostNode
	 * @return
	 */
	public NodeDTO creatNodeDTOByHost(Host hostNode){
		
		NodeDTO nodeDTO = new NodeDTO();
		
		nodeDTO.setId(hostNode.getId());
		nodeDTO.setName(hostNode.getAlias());
		nodeDTO.setIpaddress(hostNode.getIpAddress());
		nodeDTO.setNodeid(String.valueOf(hostNode.getId()));
		nodeDTO.setBusinessId(hostNode.getBid());
		
		String subtype = "";
		
		//SysLogger.info(hostNode.getIpAddress()+"===="+hostNode.getSysOid()+"========="+hostNode.getCategory());
		String type  = "";
		if(hostNode.getCategory() == 4){
			type = Constant.TYPE_HOST;
			if(hostNode.getSysOid() != null && hostNode.getSysOid().trim().length()>0){
				if(hostNode.getSysOid().startsWith("1.3.6.1.4.1.311."))subtype = Constant.TYPE_HOST_SUBTYPE_WINDOWS;
				if(hostNode.getSysOid().startsWith("1.3.6.1.4.1.2021."))subtype = Constant.TYPE_HOST_SUBTYPE_LINUX;
				if(hostNode.getSysOid().startsWith("1.3.6.1.4.1.2."))subtype = Constant.TYPE_HOST_SUBTYPE_AIX;
				if(hostNode.getSysOid().startsWith("1.3.6.1.4.1.11."))subtype = Constant.TYPE_HOST_SUBTYPE_HPUNIX;
				if(hostNode.getSysOid().startsWith("1.3.6.1.4.1.42."))subtype = Constant.TYPE_HOST_SUBTYPE_SOLARIS;
				if(hostNode.getSysOid().startsWith("as400"))subtype = Constant.TYPE_HOST_SUBTYPE_AS400;
			}
		} else if(hostNode.getCategory() == 9){
			type = Constant.TYPE_ATM;
			subtype = Constant.TYPE_ATM_SUBTYPE_ATM;
		} else if(hostNode.getCategory() == 12){
			//type = Constant.TYPE_VPN;
			type = Constant.TYPE_VPN;
			if(hostNode.getSysOid() != null && hostNode.getSysOid().trim().length()>0){
				if(hostNode.getSysOid().startsWith("1.3.6.1.4.1.7564"))subtype = Constant.TYPE_VPN_SUBTYPE_ARRAYNETWORKS;
			}
		} else if(hostNode.getCategory() == 8){
			type = Constant.TYPE_FIREWALL;
			if(hostNode.getSysOid() != null && hostNode.getSysOid().trim().length()>0){
				if(hostNode.getSysOid().startsWith("1.3.6.1.4.1.28557."))subtype = Constant.TYPE_FIREWALL_SUBTYPE_HILLSTONE;
				if(hostNode.getSysOid().startsWith("1.3.6.1.4.1.3224."))subtype = Constant.TYPE_FIREWALL_SUBTYPE_NETSCREEN;
				if(hostNode.getSysOid().startsWith("1.3.6.1.4.1.2636."))subtype = Constant.TYPE_FIREWALL_SUBTYPE_NETSCREEN;
				if(hostNode.getSysOid().startsWith("1.3.6.1.4.1.94."))subtype = Constant.TYPE_FIREWALL_SUBTYPE_NOKIA;
			}
		} else if(hostNode.getCategory() == 10){
			type = Constant.TYPE_GATEWAY;
			subtype = Constant.TYPE_GATEWAY_SUBTYPE_CISCO;
		} else if(hostNode.getCategory() == 11){
			type = Constant.TYPE_F5;
			subtype = Constant.TYPE_F5_SUBTYPE_F5;
		} else{
			//�����豸,��Ҫ�жϲ���ϵͳ
			type = Constant.TYPE_NET;
			if(hostNode.getSysOid() != null && hostNode.getSysOid().trim().length()>0){
				if(hostNode.getSysOid().startsWith("1.3.6.1.4.1.9."))subtype = Constant.TYPE_NET_SUBTYPE_CISCO;
				if(hostNode.getSysOid().startsWith("1.3.6.1.4.1.2011."))subtype = Constant.TYPE_NET_SUBTYPE_H3C;
				if(hostNode.getSysOid().startsWith("1.3.6.1.4.1.25506."))subtype = Constant.TYPE_NET_SUBTYPE_H3C;
				if(hostNode.getSysOid().startsWith("1.3.6.1.4.1.9.2.1.57."))subtype = Constant.TYPE_NET_SUBTYPE_ENTRASYS;
				if(hostNode.getSysOid().startsWith("1.3.6.1.4.1.89."))subtype = Constant.TYPE_NET_SUBTYPE_RADWARE;
				if(hostNode.getSysOid().startsWith("1.3.6.1.4.1.5651."))subtype = Constant.TYPE_NET_SUBTYPE_MAIPU;
				if(hostNode.getSysOid().startsWith("1.3.6.1.4.1.4881."))subtype = Constant.TYPE_NET_SUBTYPE_REDGIANT;
				if(hostNode.getSysOid().startsWith("1.3.6.1.4.1.45."))subtype = Constant.TYPE_NET_SUBTYPE_NORTHTEL;
				if(hostNode.getSysOid().startsWith("1.3.6.1.4.1.171."))subtype = Constant.TYPE_NET_SUBTYPE_DLINK;
				if(hostNode.getSysOid().startsWith("1.3.6.1.4.1.3320."))subtype = Constant.TYPE_NET_SUBTYPE_BDCOM;
				if(hostNode.getSysOid().startsWith("1.3.6.1.4.1.3902."))subtype = Constant.TYPE_NET_SUBTYPE_ZTE;
				if(hostNode.getSysOid().startsWith("1.3.6.1.4.1.1588.2.1.1."))subtype = Constant.TYPE_NET_SUBTYPE_BROCADE;
//				if(hostNode.getSysOid().startsWith("1.3.6.1.4.1.1588"))subtype = Constant.TYPE_NET_SUBTYPE_IBM;
			}
		}

		nodeDTO.setSubtype(subtype);
		nodeDTO.setType(type);
		//SysLogger.info(nodeDTO.getIpaddress()+"==="+nodeDTO.getType()+"==="+nodeDTO.getSubtype()+"=="+nodeDTO.getIpaddress());
		return nodeDTO;
	}
	
	/**
	 * ͨ�� Host  ת���� NodeDTO 
	 * @param host
	 * @return
	 */
	public NodeDTO creatNodeDTOByNode(Host hostNode){
		
		NodeDTO nodeDTO = new NodeDTO();
		
		nodeDTO.setId(hostNode.getId());
		nodeDTO.setName(hostNode.getAlias());
		nodeDTO.setIpaddress(hostNode.getIpAddress());
		nodeDTO.setNodeid(String.valueOf(hostNode.getId()));
		nodeDTO.setBusinessId(hostNode.getBid());
		
		String subtype = "";
		
		String type  = "";
		if(hostNode.getCategory() == 4){
			type = Constant.TYPE_HOST;
			if(hostNode.getSysOid() != null && hostNode.getSysOid().trim().length()>0){
				if(hostNode.getSysOid().startsWith("1.3.6.1.4.1.311."))subtype = Constant.TYPE_HOST_SUBTYPE_WINDOWS;
				if(hostNode.getSysOid().startsWith("1.3.6.1.4.1.2021."))subtype = Constant.TYPE_HOST_SUBTYPE_LINUX;
				if(hostNode.getSysOid().startsWith("1.3.6.1.4.1.2."))subtype = Constant.TYPE_HOST_SUBTYPE_AIX;
				if(hostNode.getSysOid().startsWith("1.3.6.1.4.1.11."))subtype = Constant.TYPE_HOST_SUBTYPE_HPUNIX;
				if(hostNode.getSysOid().startsWith("1.3.6.1.4.1.42."))subtype = Constant.TYPE_HOST_SUBTYPE_SOLARIS;
				if(hostNode.getSysOid().startsWith("as400"))subtype = Constant.TYPE_HOST_SUBTYPE_AS400;
				if(hostNode.getSysOid().startsWith("scounix"))subtype = Constant.TYPE_HOST_SUBTYPE_SCOUNIX;
				if(hostNode.getSysOid().startsWith("scoopenserver"))subtype = Constant.TYPE_HOST_SUBTYPE_SCOOPENSERVER;
				if(hostNode.getOstype() == 15)subtype = Constant.TYPE_HOST_SUBTYPE_AS400;
			}
		} else if(hostNode.getCategory() == 9){
			type = Constant.TYPE_ATM;
			subtype = Constant.TYPE_ATM_SUBTYPE_ATM;
		} else if(hostNode.getCategory() == 12){
			type = Constant.TYPE_VPN;
			if(hostNode.getSysOid() != null && hostNode.getSysOid().trim().length()>0){
				if(hostNode.getSysOid().startsWith("1.3.6.1.4.1.7564"))subtype = Constant.TYPE_VPN_SUBTYPE_ARRAYNETWORKS;
			}
			
		} else if(hostNode.getCategory() == 8){
			type = Constant.TYPE_FIREWALL;
			if(hostNode.getSysOid() != null && hostNode.getSysOid().trim().length()>0){
				if(hostNode.getSysOid().startsWith("1.3.6.1.4.1.28557."))subtype = Constant.TYPE_FIREWALL_SUBTYPE_HILLSTONE;
				if(hostNode.getSysOid().startsWith("1.3.6.1.4.1.3224."))subtype = Constant.TYPE_FIREWALL_SUBTYPE_NETSCREEN;
				if(hostNode.getSysOid().startsWith("1.3.6.1.4.1.2636."))subtype = Constant.TYPE_FIREWALL_SUBTYPE_NETSCREEN;
				if(hostNode.getSysOid().startsWith("1.3.6.1.4.1.94."))subtype = Constant.TYPE_FIREWALL_SUBTYPE_NOKIA;
			}
		} else if(hostNode.getCategory() == 10){
			type = Constant.TYPE_GATEWAY;
			subtype = Constant.TYPE_GATEWAY_SUBTYPE_CISCO;
		} else if(hostNode.getCategory() == 11){
			type = Constant.TYPE_F5;
			subtype = Constant.TYPE_F5_SUBTYPE_F5;
		} else{
			//�����豸,��Ҫ�жϲ���ϵͳ
			type = Constant.TYPE_NET;
			if(hostNode.getSysOid() != null && hostNode.getSysOid().trim().length()>0){
				if(hostNode.getSysOid().startsWith("1.3.6.1.4.1.9."))subtype = Constant.TYPE_NET_SUBTYPE_CISCO;
				if(hostNode.getSysOid().startsWith("1.3.6.1.4.1.2011."))subtype = Constant.TYPE_NET_SUBTYPE_H3C;
				if(hostNode.getSysOid().startsWith("1.3.6.1.4.1.25506."))subtype = Constant.TYPE_NET_SUBTYPE_H3C;
				if(hostNode.getSysOid().startsWith("1.3.6.1.4.1.9.2.1.57."))subtype = Constant.TYPE_NET_SUBTYPE_ENTRASYS;
				if(hostNode.getSysOid().startsWith("1.3.6.1.4.1.89."))subtype = Constant.TYPE_NET_SUBTYPE_RADWARE;
				if(hostNode.getSysOid().startsWith("1.3.6.1.4.1.5651."))subtype = Constant.TYPE_NET_SUBTYPE_MAIPU;
				if(hostNode.getSysOid().startsWith("1.3.6.1.4.1.4881."))subtype = Constant.TYPE_NET_SUBTYPE_REDGIANT;
				if(hostNode.getSysOid().startsWith("1.3.6.1.4.1.45."))subtype = Constant.TYPE_NET_SUBTYPE_NORTHTEL;
				if(hostNode.getSysOid().startsWith("1.3.6.1.4.1.171."))subtype = Constant.TYPE_NET_SUBTYPE_DLINK;
				if(hostNode.getSysOid().startsWith("1.3.6.1.4.1.3320."))subtype = Constant.TYPE_NET_SUBTYPE_BDCOM;
				if(hostNode.getSysOid().startsWith("1.3.6.1.4.1.3902."))subtype = Constant.TYPE_NET_SUBTYPE_ZTE;
				if(hostNode.getSysOid().startsWith("1.3.6.1.4.1.1588.2.1.1."))subtype = Constant.TYPE_NET_SUBTYPE_BROCADE;
//				if(hostNode.getSysOid().startsWith("1.3.6.1.4.1.1588"))subtype = Constant.TYPE_NET_SUBTYPE_IBM;
			}
		}

		nodeDTO.setSubtype(subtype);
		nodeDTO.setType(type);
		return nodeDTO;
	}
	
	/**
	 * ͨ�� Mail  ת���� NodeDTO 
	 * @param host
	 * @return
	 */
	public NodeDTO creatNodeDTOByNode(Mail mail){
		
		NodeDTO nodeDTO = new NodeDTO();
		
		nodeDTO.setId(mail.getId());
		nodeDTO.setName(mail.getAlias());
		nodeDTO.setIpaddress(mail.getIpAddress());
		nodeDTO.setNodeid(String.valueOf(mail.getId()));
		nodeDTO.setBusinessId(mail.getBid());
		
		String subtype = "mail";
		
		String type  = "service";

		nodeDTO.setSubtype(subtype);
		nodeDTO.setType(type);
		return nodeDTO;
	}
	
	/**
	 * ͨ�� Ftp  ת���� NodeDTO 
	 * @param host
	 * @return
	 */
	public NodeDTO creatNodeDTOByNode(Ftp ftp){
		
		NodeDTO nodeDTO = new NodeDTO();
		
		nodeDTO.setId(ftp.getId());
		nodeDTO.setName(ftp.getAlias());
		nodeDTO.setIpaddress(ftp.getIpAddress());
		nodeDTO.setNodeid(String.valueOf(ftp.getId()));
		nodeDTO.setBusinessId(ftp.getBid());
		
		String subtype = "ftp";
		
		String type  = "service";

		nodeDTO.setSubtype(subtype);
		nodeDTO.setType(type);
		return nodeDTO;
	}
	
	/**
	 * ͨ�� Ftp  ת���� NodeDTO 
	 * @param host
	 * @return
	 */
	public NodeDTO creatNodeDTOByNode(com.afunms.polling.node.SocketService socket){
		
		NodeDTO nodeDTO = new NodeDTO();
		
		nodeDTO.setId(socket.getId());
		nodeDTO.setName(socket.getAlias());
		nodeDTO.setIpaddress(socket.getIpAddress());
		nodeDTO.setNodeid(String.valueOf(socket.getId()));
		nodeDTO.setBusinessId(socket.getBid());
		
		String subtype = "socket";
		
		String type  = "service";

		nodeDTO.setSubtype(subtype);
		nodeDTO.setType(type);
		return nodeDTO;
	}
	
	/**
	 * ͨ�� Ftp  ת���� NodeDTO 
	 * @param host
	 * @return
	 */
	public NodeDTO creatNodeDTOByNode(Web web){
		
		NodeDTO nodeDTO = new NodeDTO();
		
		nodeDTO.setId(web.getId());
		nodeDTO.setName(web.getAlias());
		nodeDTO.setIpaddress(web.getIpAddress());
		nodeDTO.setNodeid(String.valueOf(web.getId()));
		nodeDTO.setBusinessId(web.getBid());
		
		String subtype = "url";
		
		String type  = "service";

		nodeDTO.setSubtype(subtype);
		nodeDTO.setType(type);
		return nodeDTO;
	}
	
	/**
	 * ͨ�� DBVo  ת���� NodeDTO 
	 * @param dbVo
	 * @return
	 */
	public NodeDTO creatNodeDTOByNode(DBVo dbVo){
		
		NodeDTO nodeDTO = new NodeDTO();
		
		nodeDTO.setId(dbVo.getId());
		nodeDTO.setName(dbVo.getAlias());
		nodeDTO.setIpaddress(dbVo.getIpAddress());
		nodeDTO.setNodeid(String.valueOf(dbVo.getId()));
		nodeDTO.setBusinessId(dbVo.getBid());
		
		String type = Constant.TYPE_DB;
		String subtype = "";
		
		DBTypeDao typeDao = new DBTypeDao();
		try {
			DBTypeVo typeVo = (DBTypeVo)typeDao.findByID(String.valueOf(dbVo.getDbtype()));
			if(typeVo == null){
				subtype = "";
			} else if("oracle".equalsIgnoreCase(typeVo.getDbtype())){
				subtype = Constant.TYPE_DB_SUBTYPE_ORACLE;
			}else if("sqlserver".equalsIgnoreCase(typeVo.getDbtype())){
				subtype = Constant.TYPE_DB_SUBTYPE_SQLSERVER;
			}else if("mysql".equalsIgnoreCase(typeVo.getDbtype())){
				subtype = Constant.TYPE_DB_SUBTYPE_MYSQL;
			}else if("db2".equalsIgnoreCase(typeVo.getDbtype())){
				subtype = Constant.TYPE_DB_SUBTYPE_DB2;
			}else if("sybase".equalsIgnoreCase(typeVo.getDbtype())){
				subtype = Constant.TYPE_DB_SUBTYPE_SYBASE;
			}else if("informix".equalsIgnoreCase(typeVo.getDbtype())){
				subtype = Constant.TYPE_DB_SUBTYPE_INFORMIX;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			typeDao.close();
		}
		
		nodeDTO.setType(type);
		nodeDTO.setSubtype(subtype);
		
		return nodeDTO;
	}
	
	/**
	 * ͨ�� OracleEntity  ת���� NodeDTO 
	 * @param oracleEntity
	 * @return
	 */
	public NodeDTO creatNodeDTOByNode(OracleEntity oracleEntity){
		
		NodeDTO nodeDTO = new NodeDTO();
		
		nodeDTO.setId(oracleEntity.getId());
		nodeDTO.setName(oracleEntity.getAlias());
		//����IP
		DBDao dbdao = new DBDao();
		DBVo dbvo = null;
		try{
			dbvo = (DBVo)dbdao.findByID(oracleEntity.getDbid()+"");
		}catch(Exception e){
			
		}finally{
			dbdao.close();
		}
		if(dbvo != null)nodeDTO.setIpaddress(dbvo.getIpAddress());
		
		nodeDTO.setNodeid(String.valueOf(oracleEntity.getId()));
		nodeDTO.setBusinessId(oracleEntity.getBid());
		
		String subtype = Constant.TYPE_DB_SUBTYPE_ORACLE;
		
		String type  = Constant.TYPE_DB;
		
		nodeDTO.setType(type);
		nodeDTO.setSubtype(subtype);
		
		return nodeDTO;
	}
	
	/**
	 * ͨ�� ApacheConfig  ת���� NodeDTO 
	 * @param apacheConfig
	 * @return
	 */
	public NodeDTO creatNodeDTOByNode(ApacheConfig apacheConfig){
		
		NodeDTO nodeDTO = new NodeDTO();
		
		nodeDTO.setId(apacheConfig.getId());
		nodeDTO.setName(apacheConfig.getAlias());
		nodeDTO.setNodeid(String.valueOf(apacheConfig.getId()));
		nodeDTO.setBusinessId(apacheConfig.getNetid());
		nodeDTO.setIpaddress(apacheConfig.getIpaddress());
		
		
		
		String type  = Constant.TYPE_MIDDLEWARE;
		String subtype = Constant.TYPE_MIDDLEWARE_SUBTYPE_APACHE;
		
		nodeDTO.setType(type);
		nodeDTO.setSubtype(subtype);
		
		return nodeDTO;
	}
	
	/**
	 * ͨ�� MgeUps  ת���� NodeDTO 
	 * @param mgeUps
	 * @return
	 */
	public NodeDTO creatNodeDTOByNode(MgeUps mgeUps){
		
		NodeDTO nodeDTO = new NodeDTO();
		
		nodeDTO.setId(mgeUps.getId());
		nodeDTO.setName(mgeUps.getAlias());
		nodeDTO.setNodeid(String.valueOf(mgeUps.getId()));
		nodeDTO.setBusinessId(mgeUps.getBid());
		nodeDTO.setIpaddress(mgeUps.getIpAddress());
		nodeDTO.setType(mgeUps.getType());
		nodeDTO.setSubtype(mgeUps.getSubtype());
		
		return nodeDTO;
	}
    public NodeDTO creatNodeDTOByNode(UPSNode node){
		
		NodeDTO nodeDTO = new NodeDTO();
		
		nodeDTO.setId(node.getId());
		nodeDTO.setName(node.getAlias());
		nodeDTO.setNodeid(String.valueOf(node.getId()));
		nodeDTO.setBusinessId(node.getBid());
		nodeDTO.setIpaddress(node.getIpAddress());
		nodeDTO.setType(node.getType());
		nodeDTO.setSubtype(node.getSubtype());
		
		return nodeDTO;
	}
	
	/**
	 * ͨ�� CicsConfig  ת���� NodeDTO 
	 * @param cicsConfig
	 * @return
	 */
	public NodeDTO creatNodeDTOByNode(CicsConfig cicsConfig){
		
		NodeDTO nodeDTO = new NodeDTO();
		
		nodeDTO.setId(cicsConfig.getId());
		nodeDTO.setName(cicsConfig.getAlias());
		nodeDTO.setNodeid(String.valueOf(cicsConfig.getId()));
		nodeDTO.setBusinessId(cicsConfig.getNetid());
		nodeDTO.setIpaddress(cicsConfig.getIpaddress());
		
		String type  = Constant.TYPE_MIDDLEWARE;
		String subtype = Constant.TYPE_MIDDLEWARE_SUBTYPE_CICS;
		
		nodeDTO.setType(type);
		nodeDTO.setSubtype(subtype);
		
		return nodeDTO;
	}
	
	/**
	 * ͨ�� DnsConfig  ת���� NodeDTO 
	 * @param dnsConfig
	 * @return
	 */
	public NodeDTO creatNodeDTOByNode(DnsConfig dnsConfig){
		
		NodeDTO nodeDTO = new NodeDTO();
		
		nodeDTO.setId(dnsConfig.getId());
		nodeDTO.setName(dnsConfig.getDns());
		nodeDTO.setNodeid(String.valueOf(dnsConfig.getId()));
		nodeDTO.setBusinessId(dnsConfig.getNetid());
		nodeDTO.setIpaddress(dnsConfig.getDnsip());
		
		String type  = Constant.TYPE_MIDDLEWARE;
		String subtype = Constant.TYPE_MIDDLEWARE_SUBTYPE_DNS;
		
		nodeDTO.setType(type);
		nodeDTO.setSubtype(subtype);
		
		return nodeDTO;
	}
	
	
	/**
	 * ͨ�� DominoConfig  ת���� NodeDTO 
	 * @param dominoConfig
	 * @return
	 */
	public NodeDTO creatNodeDTOByNode(DominoConfig dominoConfig){
		
		NodeDTO nodeDTO = new NodeDTO();
		
		nodeDTO.setId(dominoConfig.getId());
		nodeDTO.setName(dominoConfig.getName());
		nodeDTO.setNodeid(String.valueOf(dominoConfig.getId()));
		nodeDTO.setBusinessId(dominoConfig.getNetid());
		nodeDTO.setIpaddress(dominoConfig.getIpaddress());
		
		String type  = Constant.TYPE_MIDDLEWARE;
		String subtype = Constant.TYPE_MIDDLEWARE_SUBTYPE_DOMINO;
		
		nodeDTO.setType(type);
		nodeDTO.setSubtype(subtype);
		
		return nodeDTO;
	}
	
	
	/**
	 * ͨ�� IISConfig  ת���� NodeDTO 
	 * @param iISConfig
	 * @return
	 */
	public NodeDTO creatNodeDTOByNode(IISConfig iISConfig){
		
		NodeDTO nodeDTO = new NodeDTO();
		
		nodeDTO.setId(iISConfig.getId());
		nodeDTO.setName(iISConfig.getName());
		nodeDTO.setNodeid(String.valueOf(iISConfig.getId()));
		nodeDTO.setBusinessId(iISConfig.getNetid());
		nodeDTO.setIpaddress(iISConfig.getIpaddress());
		
		String type  = Constant.TYPE_MIDDLEWARE;
		String subtype = Constant.TYPE_MIDDLEWARE_SUBTYPE_IIS;
		
		nodeDTO.setType(type);
		nodeDTO.setSubtype(subtype);
		
		return nodeDTO;
	}
	
	
	/**
	 * ͨ�� JBossConfig  ת���� NodeDTO 
	 * @param jBossConfig
	 * @return
	 */
	public NodeDTO creatNodeDTOByNode(JBossConfig jBossConfig){
		
		NodeDTO nodeDTO = new NodeDTO();
		
		nodeDTO.setId(jBossConfig.getId());
		nodeDTO.setName(jBossConfig.getAlias());
		nodeDTO.setNodeid(String.valueOf(jBossConfig.getId()));
		nodeDTO.setBusinessId(jBossConfig.getNetid());
		nodeDTO.setIpaddress(jBossConfig.getIpaddress());
		
		String type  = Constant.TYPE_MIDDLEWARE;
		String subtype = Constant.TYPE_MIDDLEWARE_SUBTYPE_JBOSS;
		
		nodeDTO.setType(type);
		nodeDTO.setSubtype(subtype);
		
		return nodeDTO;
	}
	
	
	/**
	 * ͨ�� MQConfig  ת���� NodeDTO 
	 * @param mQConfig
	 * @return
	 */
	public NodeDTO creatNodeDTOByNode(MQConfig mQConfig){
		
		NodeDTO nodeDTO = new NodeDTO();
		
		nodeDTO.setId(mQConfig.getId());
		nodeDTO.setName(mQConfig.getName());
		nodeDTO.setNodeid(String.valueOf(mQConfig.getId()));
		nodeDTO.setBusinessId(mQConfig.getNetid());
		nodeDTO.setIpaddress(mQConfig.getIpaddress());
		
		String type  = Constant.TYPE_MIDDLEWARE;
		String subtype = Constant.TYPE_MIDDLEWARE_SUBTYPE_MQ;
		
		nodeDTO.setType(type);
		nodeDTO.setSubtype(subtype);
		
		return nodeDTO;
	}
	
	
	/**
	 * ͨ�� Tomcat  ת���� NodeDTO 
	 * @param tomcat
	 * @return
	 */
	public NodeDTO creatNodeDTOByNode(Tomcat tomcat){
		
		NodeDTO nodeDTO = new NodeDTO();
		
		nodeDTO.setId(tomcat.getId());
		nodeDTO.setName(tomcat.getAlias());
		nodeDTO.setNodeid(String.valueOf(tomcat.getId()));
		nodeDTO.setBusinessId(tomcat.getBid());
		nodeDTO.setIpaddress(tomcat.getIpAddress());
		
		String type  = Constant.TYPE_MIDDLEWARE;
		String subtype = Constant.TYPE_MIDDLEWARE_SUBTYPE_TOMCAT;
		
		nodeDTO.setType(type);
		nodeDTO.setSubtype(subtype);
		
		return nodeDTO;
	}
	
	
	/**
	 * ͨ�� TuxedoConfig  ת���� NodeDTO 
	 * @param tuxedoConfig
	 * @return
	 */
	public NodeDTO creatNodeDTOByNode(TuxedoConfig tuxedoConfig){
		
		NodeDTO nodeDTO = new NodeDTO();
		
		nodeDTO.setId(tuxedoConfig.getId());
		nodeDTO.setName(tuxedoConfig.getName());
		nodeDTO.setNodeid(String.valueOf(tuxedoConfig.getId()));
		nodeDTO.setBusinessId(tuxedoConfig.getBid());
		nodeDTO.setIpaddress(tuxedoConfig.getIpAddress());
		
		String type  = Constant.TYPE_MIDDLEWARE;
		String subtype = Constant.TYPE_MIDDLEWARE_SUBTYPE_TUXEDO;
		
		nodeDTO.setType(type);
		nodeDTO.setSubtype(subtype);
		
		return nodeDTO;
	}
	
	
	/**
	 * ͨ�� WasConfig  ת���� NodeDTO 
	 * @param wasConfig
	 * @return
	 */
	public NodeDTO creatNodeDTOByNode(WasConfig wasConfig){
		
		NodeDTO nodeDTO = new NodeDTO();
		
		nodeDTO.setId(wasConfig.getId());
		nodeDTO.setName(wasConfig.getName());
		nodeDTO.setNodeid(String.valueOf(wasConfig.getId()));
		nodeDTO.setBusinessId(wasConfig.getNetid());
		nodeDTO.setIpaddress(wasConfig.getIpaddress());
		
		String type  = Constant.TYPE_MIDDLEWARE;
		String subtype = Constant.TYPE_MIDDLEWARE_SUBTYPE_WAS;
		
		nodeDTO.setType(type);
		nodeDTO.setSubtype(subtype);
		
		return nodeDTO;
	}
	
	
	/**
	 * ͨ�� WeblogicConfig  ת���� NodeDTO 
	 * @param weblogicConfig
	 * @return
	 */
	public NodeDTO creatNodeDTOByNode(WeblogicConfig weblogicConfig){
		
		NodeDTO nodeDTO = new NodeDTO();
		
		nodeDTO.setId(weblogicConfig.getId());
		nodeDTO.setName(weblogicConfig.getAlias());
		nodeDTO.setNodeid(String.valueOf(weblogicConfig.getId()));
		nodeDTO.setBusinessId(weblogicConfig.getNetid());
		nodeDTO.setIpaddress(weblogicConfig.getIpAddress());
		
		String type  = Constant.TYPE_MIDDLEWARE;
		String subtype = Constant.TYPE_MIDDLEWARE_SUBTYPE_WEBLOGIC;
		
		nodeDTO.setType(type);
		nodeDTO.setSubtype(subtype);
		
		return nodeDTO;
	}
	
	
	/**
	 * ͨ�� EmailMonitorConfig  ת���� NodeDTO 
	 * @param emailMonitorConfig
	 * @return
	 */
	public NodeDTO creatNodeDTOByNode(EmailMonitorConfig emailMonitorConfig){
		
		NodeDTO nodeDTO = new NodeDTO();
		
		nodeDTO.setId(emailMonitorConfig.getId());
		nodeDTO.setName(emailMonitorConfig.getName());
		nodeDTO.setNodeid(String.valueOf(emailMonitorConfig.getId()));
		nodeDTO.setBusinessId(emailMonitorConfig.getBid());
		nodeDTO.setIpaddress(emailMonitorConfig.getIpaddress());
		
		String type  = Constant.TYPE_SERVICE;
		String subtype = Constant.TYPE_SERVICE_SUBTYPE_EMAIL;
		
		nodeDTO.setType(type);
		nodeDTO.setSubtype(subtype);
		
		return nodeDTO;
	}
	
	
	/**
	 * ͨ�� FTPConfig  ת���� NodeDTO 
	 * @param fTPConfig
	 * @return
	 */
	public NodeDTO creatNodeDTOByNode(FTPConfig fTPConfig){
		
		NodeDTO nodeDTO = new NodeDTO();
		
		nodeDTO.setId(fTPConfig.getId());
		nodeDTO.setName(fTPConfig.getName());
		nodeDTO.setNodeid(String.valueOf(fTPConfig.getId()));
		nodeDTO.setBusinessId(fTPConfig.getBid());
		nodeDTO.setIpaddress(fTPConfig.getIpaddress());
		
		String type  = Constant.TYPE_SERVICE;
		String subtype = Constant.TYPE_SERVICE_SUBTYPE_FTP;
		
		nodeDTO.setType(type);
		nodeDTO.setSubtype(subtype);
		
		return nodeDTO;
	}
	
	
	/**
	 * ͨ�� Procs  ת���� NodeDTO 
	 * @param procs
	 * @return
	 */
	public NodeDTO creatNodeDTOByNode(Procs procs){
		
		NodeDTO nodeDTO = new NodeDTO();
		
		nodeDTO.setId(procs.getId());
		nodeDTO.setName(procs.getProcname());
		nodeDTO.setNodeid(String.valueOf(procs.getId()));
//		nodeDTO.setBusinessId(procs.get);
		nodeDTO.setIpaddress(procs.getIpaddress());
		
		String type  = Constant.TYPE_SERVICE;
		String subtype = Constant.TYPE_SERVICE_SUBTYPE_HOSTPROCESS;
		
		nodeDTO.setType(type);
		nodeDTO.setSubtype(subtype);
		
		return nodeDTO;
	}
	
	
	/**
	 * ͨ�� PSTypeVo  ת���� NodeDTO 
	 * @param psTypeVo
	 * @return
	 */
	public NodeDTO creatNodeDTOByNode(PSTypeVo psTypeVo){
		
		NodeDTO nodeDTO = new NodeDTO();
		
		nodeDTO.setId(psTypeVo.getId());
		nodeDTO.setName(psTypeVo.getPort());
		nodeDTO.setNodeid(String.valueOf(psTypeVo.getId()));
		nodeDTO.setBusinessId(psTypeVo.getBid());
		nodeDTO.setIpaddress(psTypeVo.getIpaddress());
		
		String type  = Constant.TYPE_SERVICE;
		String subtype = Constant.TYPE_SERVICE_SUBTYPE_PORT;
		
		nodeDTO.setType(type);
		nodeDTO.setSubtype(subtype);
		
		return nodeDTO;
	}
	
	
	/**
	 * ͨ�� WebConfig  ת���� NodeDTO 
	 * @param webConfig
	 * @return
	 */
	public NodeDTO creatNodeDTOByNode(WebConfig webConfig){
		
		NodeDTO nodeDTO = new NodeDTO();
		
		nodeDTO.setId(webConfig.getId());
		nodeDTO.setName(webConfig.getAlias());
		nodeDTO.setNodeid(String.valueOf(webConfig.getId()));
		nodeDTO.setBusinessId(webConfig.getNetid());
		nodeDTO.setIpaddress(webConfig.getIpAddress());
		
		String type  = Constant.TYPE_SERVICE;
		String subtype = Constant.TYPE_SERVICE_SUBTYPE_WEB;
		
		nodeDTO.setType(type);
		nodeDTO.setSubtype(subtype);
		
		return nodeDTO;
	}
	
	/**
	 * ͨ�� manageXml  ת���� NodeDTO 
	 * @param manageXml
	 * @return
	 */
	public NodeDTO creatNodeDTOByNode(ManageXml manageXml){
		
		NodeDTO nodeDTO = new NodeDTO();
		
		nodeDTO.setId(manageXml.getId());
		nodeDTO.setName(manageXml.getTopoName());
		nodeDTO.setNodeid(String.valueOf(manageXml.getId()));
		nodeDTO.setBusinessId(manageXml.getBid());
		nodeDTO.setIpaddress("");
		
		String type  = Constant.TYPE_TOPO;
		String subtype = Constant.TYPE_TOPO_SUBTYPE_BUSINESSVIEW;
		
		nodeDTO.setType(type);
		nodeDTO.setSubtype(subtype);
		
		return nodeDTO;
	}
	
	
	
	/**
	 * ���� bid  ��ȡ bid ������
	 * @param bid
	 * @return
	 */
	public String getBusinessNameForNode(String bid){
		BusinessDao bussdao = new BusinessDao();
	   	List allbuss = null;
		try {
			allbuss = bussdao.loadAll();
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
	   	if(bid == null){
	   		bid="";
	   	}
	   	String id[] = bid.split(",");
	   	List bidlist = new ArrayList();
	   	if(id != null &&id.length>0){
			for(int j=0;j<id.length;j++){
		    		bidlist.add(id[j]);
			}
		}
		String bussName = "";
		if( allbuss.size()>0){
			for(int k=0;k<allbuss.size();k++){
				Business buss = (Business)allbuss.get(k);
				if(bidlist.contains(buss.getId()+"")){
					bussName = bussName + ',' + buss.getName();
				}
			}
		}
		
		return bussName;
	}
	
	public List<NodeDTO> getByNodeTag(String nodeTag, String category, String name,List nodelist){
		List<NodeDTO> list = null;
		if(nodelist!=null){
			list = new ArrayList<NodeDTO>();
			for(int i=0;i<nodelist.size();i++){
				String nodestr = (String) nodelist.get(i); 
//				System.out.println(nodestr);
				if(nodestr.indexOf(nodeTag)!=-1){
					Node node = PollingEngine.getInstance().getNodeByCategory(name, Integer.parseInt(nodestr.substring(3)));
					String cate[] = category.split(",");
					if(cate!=null&&cate.length>0){
						for(int j=0;j<cate.length;j++){
							if(cate[j]!=null&&!"null".equalsIgnoreCase(cate[j])&&!"".equalsIgnoreCase(cate[j])&&node.getCategory()==Integer.parseInt(cate[j])&&node.isManaged()){
								NodeDTO nodeDTO = conversionToNodeDTO(node);
								list.add(nodeDTO);
							}
						}
					}
				}
			}
		}
		return list;
	}
	
	public List<BaseVo> getByNodeTag(String nodeTag, String category){
		List<BaseVo> list = null;
		//SysLogger.info("category==="+category+"====nodeTag:==="+nodeTag);
		if("net".equalsIgnoreCase(nodeTag)){
			// �����豸����
			String condition = " where 1=1 " + getMonFlagSql("managed");
			if(category == null) {
				category = "";
			}
			category = "," + category + ",";
			String[] categorys = category.split(",");
			if(categorys!=null && categorys.length > 0 ){
				int i = 0 ; 
				for (String category_per : categorys) {
					if(category_per != null && category_per.trim().length() > 0){
						if(i == 0) { 
							condition = condition + " and (";
						} else {
							condition = condition + " or";
						}
						condition = condition + " category ='" + category_per + "'";
						i++;
						if(i == categorys.length -1){
							condition = condition + ") ";
						}
					}
				}
			}
			//System.out.println(condition);
			list = getHostNode(condition);
			
		} else if ("mid".equalsIgnoreCase(nodeTag)){
			// �м��
			list = getMiddlewareList();
		}  else if ("ser".equalsIgnoreCase(nodeTag)){
			// ����
			list = getServiceList();
		} else if ("dom".equalsIgnoreCase(nodeTag)){
			// domino
			list = getMiddleware_DominoList();
		} else if ("dbs".equalsIgnoreCase(nodeTag)){
			// ���ݿ�
			list = getDBList();
		} else if ("tom".equalsIgnoreCase(nodeTag)){
			// Tomcat
			list = getMiddleware_TomcatList();
		} else if ("cic".equalsIgnoreCase(nodeTag)){
			// Cics
			list = getMiddleware_CICSList();
		} else if ("mqs".equalsIgnoreCase(nodeTag)){
			// MQ
			list = getMiddleware_MQList();
		} else if ("was".equalsIgnoreCase(nodeTag)){
			// WAS
			list = getMiddleware_WasList();
		} else if ("web".equalsIgnoreCase(nodeTag)){
			// Weblogic
			list = getMiddleware_WeblogicList();
		} else if ("mai".equalsIgnoreCase(nodeTag)){
			// Mail
			list = getService_EmailList();
		} else if ("ftp".equalsIgnoreCase(nodeTag)){
			// FTP
			list = getService_FtpList();
		} else if ("wes".equalsIgnoreCase(nodeTag)){
			// WEB
			list = getService_WebList();
		} else if ("iis".equalsIgnoreCase(nodeTag)){
			// IIS
			list = getMiddleware_IISList();
		} else if ("jbo".equalsIgnoreCase(nodeTag)){
			// JBoss
			list = getMiddleware_JBossList();
		} else if ("apa".equalsIgnoreCase(nodeTag)){
			// Apache
			list = getMiddleware_ApacheList();
		} else if ("ups".equalsIgnoreCase(nodeTag)){
			list = getUpsList();
		} else if ("soc".equalsIgnoreCase(nodeTag)){
			list = getService_PSTypeVoList();
		} else if ("bus".equalsIgnoreCase(nodeTag)){
			// ҵ����ͼ
			list = getTopo_BusinessList();
		} else if ("pro".equalsIgnoreCase(nodeTag)){
			
		} else if ("int".equalsIgnoreCase(nodeTag)){
			
		} else if ("sto".equalsIgnoreCase(nodeTag)){
			
		} else if ("ggs".equalsIgnoreCase(nodeTag)){
			
		} else if ("sgs".equalsIgnoreCase(nodeTag)){
			
		} else if ("saf".equalsIgnoreCase(nodeTag)){//��ȫ�豸
			list = getSafeList();
		} else if ("atm".equalsIgnoreCase(nodeTag)){//��ȫ�豸
			list = getAtmList();
		}
		return list;
	}
	
	public List getSafeList(){
		List retList = null;
		String category = "8";
		String condition = " where 1=1 " + getMonFlagSql("managed");
		if(category == null) {
			category = "";
		}
		category = "," + category + ",";
		String[] categorys = category.split(",");
		if(categorys!=null && categorys.length > 0 ){
			int i = 0 ; 
			for (String category_per : categorys) {
				if(category_per != null && category_per.trim().length() > 0){
					if(i == 0) { 
						condition = condition + " and (";
					} else {
						condition = condition + " or";
					}
					condition = condition + " category ='" + category_per + "'";
					i++;
					if(i == categorys.length -1){
						condition = condition + ") ";
					}
				}
			}
		}
		//System.out.println(condition);
		retList = getHostNode(condition);
		return retList;
	}
	
	public List<BaseVo> getAtmList(){
		List retList = null;
		String category = "9";
		String condition = " where 1=1 " + getMonFlagSql("managed");
		if(category == null) {
			category = "";
		}
		category = "," + category + ",";
		String[] categorys = category.split(",");
		if(categorys!=null && categorys.length > 0 ){
			int i = 0 ; 
			for (String category_per : categorys) {
				if(category_per != null && category_per.trim().length() > 0){
					if(i == 0) { 
						condition = condition + " and (";
					} else {
						condition = condition + " or";
					}
					condition = condition + " category ='" + category_per + "'";
					i++;
					if(i == categorys.length -1){
						condition = condition + ") ";
					}
				}
			}
		}
		//System.out.println(condition);
		retList = getHostNode(condition);
		return retList;
	}

	/**
	 * @return the monitorFlag
	 */
	public String getMonitorFlag() {
		return monitorFlag;
	}

	/**
	 * @param monitorFlag the monitorFlag to set
	 */
	public void setMonitorFlag(String monitorFlag) {
		this.monitorFlag = monitorFlag;
	}

	/**
	 * @return the isSetedMonitorFlag
	 */
	public boolean isSetedMonitorFlag() {
		return isSetedMonitorFlag;
	}

	/**
	 * @param isSetedMonitorFlag the isSetedMonitorFlag to set
	 */
	public void setSetedMonitorFlag(boolean isSetedMonitorFlag) {
		this.isSetedMonitorFlag = isSetedMonitorFlag;
	}
	
	
	
	
	
}
