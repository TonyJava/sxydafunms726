/**
 * <p>Description:topo hepler</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-09-20
 */

package com.afunms.topology.util;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import com.afunms.polling.loader.HostLoader;
import com.afunms.topology.model.*;
import com.afunms.topology.dao.*;
import com.afunms.common.util.*;
import com.afunms.initialize.ResourceCenter;

public class TopoHelper  
{
	private com.afunms.discovery.Host host;
	
	/**
	 * ����һ̨����
	 */
    public int addHost(String ipAddress,String alias,String community,String writecommunity,int category)
    {
    	HostNodeDao tmpDao = new HostNodeDao();
    	List tmpList = null;
    	try{
    		tmpList = tmpDao.findByCondition("ip_address",ipAddress);
    	}catch(Exception e){
	    	   e.printStackTrace();
	       }finally{
	    	   tmpDao.close();
	       }
    	if(tmpList.size()>0)
    	   return -1;     //IP�Ѿ�����	
    	if(NetworkUtil.ping(ipAddress)==0) 
	       return -2;     //ping��ͨ 
	    if(SnmpUtil.getInstance().getSysOid(ipAddress,community)==null)
	       return -3;     //��֧��snmp 
	    	    
	    host = new com.afunms.discovery.Host();	    	    
	    int result = 0;
	    int id = 0;
	    try //���°������ӵ����������ݼ������ݿ�
	    {	       
	       id = KeyGenerator.getInstance().getNextKey();
	       host.setId(id);
	       
	       host.setCategory(category);
	       host.setCommunity(community);
	       host.setWritecommunity(writecommunity);
	       host.setAlias(alias);
	       host.setIpAddress(ipAddress);
	       host.setSysOid(SnmpUtil.getInstance().getSysOid(ipAddress,community));
	       //host.setSysDescr(SnmpUtil.getInstance().getSysDescr(ipAddress,community));	 
	       host.setIfEntityList(SnmpUtil.getInstance().getIfEntityList(ipAddress,community,category));	    
	       //host.setSysName(SnmpUtil.getInstance().getSysName(ipAddress,community));	 
	       host.setLocalNet(0);
	       host.setNetMask("255.255.255.0");
	       host.setDiscoverstatus(-1);
	       SnmpUtil snmp = SnmpUtil.getInstance();
	    	SysLogger.info("��ʼ��ȡ�豸:"+host.getIpAddress()+"��ϵͳ����");
	    	Hashtable sysGroupProperty = snmp.getSysGroup(host.getIpAddress(),host.getCommunity());
	    	if(sysGroupProperty != null){
	    		host.setSysDescr((String)sysGroupProperty.get("sysDescr"));
	        	//newNode.setsys((String)sysGroupProperty.get("sysUpTime"));
	    		host.setSysContact((String)sysGroupProperty.get("sysContact"));
	    		host.setSysName((String)sysGroupProperty.get("sysName"));
	    		host.setSysLocation((String)sysGroupProperty.get("sysLocation"));
	    	}
	       
	       SubnetDao netDao = new SubnetDao();
	       List netList = null;
	       try{
	    	   netList = netDao.loadAll(); //�ҳ��������ĸ�����
	       }catch(Exception e){
	    	   e.printStackTrace();
	       }finally{
	    	   netDao.close();
	       }
	       for(int i=0;i<netList.size();i++)
	       {
	       	  Subnet net = (Subnet)netList.get(i);
	    	  if(NetworkUtil.isValidIP(net.getNetAddress(),net.getNetMask(),ipAddress))
	    	  {
	    		  host.setLocalNet(net.getId());
	    		  host.setNetMask(net.getNetMask());
	    		  break;
	    	  }	
	       }
	       List hostList = new ArrayList(1);
	       hostList.add(host);	       
	       DiscoverCompleteDao dcDao = new DiscoverCompleteDao();	
	       try{
	    	   dcDao.addHostDataByHand(hostList);
	    	   dcDao.addInterfaceData(hostList);		   
	    	   dcDao.addMonitor(hostList);
	       }catch(Exception e){
	    	   e.printStackTrace();
	       }finally{
	    	   dcDao.close();
	       }
		   		   
		   result = id; //���ݳɹ�����,����ID		   
	    }
	    catch(Exception e)
	    {
	    	SysLogger.error("TopoHelper.addHost(),insert db",e);	    	
	    }	    
	    if(result==0) return 0; //�����������ʧ���򲻼���
	    HostNodeDao dao = new HostNodeDao();
	    try //���°������������ڴ�(��PollingInitializtion.loadHost()���)
	    {	    		    		   
	    		
	    	HostNode vo = (HostNode)dao.findByID(String.valueOf(id));
	    	HostLoader loader = new HostLoader();
	    	loader.loadOne(vo);
	    	loader.close();
	    	SysLogger.info("�ɹ�����һ̨����,id=" + id);
	    }
	    catch(Exception e)
	    {
	    	SysLogger.error("TopoUtil.addHost(),insert memory",e);	    
	    	result = 0;
	    }finally{
	    	dao.close();
	    }
	    return result;
    }
    
	/**
	 * ����һ̨����
	 */
    public int addHost(String ipAddress,String alias,String community,String writecommunity,int category,int ostype,int collecttype)
    {
    	HostNodeDao tmpDao = new HostNodeDao();
    	List tmpList = tmpDao.findByCondition("ip_address",ipAddress);
    	if(tmpList.size()>0)
    	   return -1;     //IP�Ѿ�����
    	/*
    	 * ����Ϊ�˲�����,ĿǰPING��ͨ���豸Ҳ���Լӽ���
    	 */
    	//if(NetworkUtil.ping(ipAddress)==0) 
	       //return -2;     //ping��ͨ
    	if(collecttype == SystemConstant.COLLECTTYPE_SNMP){
    		//SNMP�ɼ���ʽ
    	   	   String snmpversion = "";
    	   	   snmpversion = ResourceCenter.getInstance().getSnmpversion();
    	   	   int default_version = 0;
    	   	   if(snmpversion.equals("v1")){
    	  			default_version = org.snmp4j.mp.SnmpConstants.version1;
    			  }else if(snmpversion.equals("v2")){
    				default_version = org.snmp4j.mp.SnmpConstants.version2c;
    			  }else if(snmpversion.equals("v1+v2")){
    				default_version = org.snmp4j.mp.SnmpConstants.version1;
    			  }else if(snmpversion.equals("v2+v1")){
    	  			default_version = org.snmp4j.mp.SnmpConstants.version2c;
    		   }
    		
    		
    	   	//SnmpUtil.getInstance().getSysOid(coreIp,community);
    	    if(SnmpUtil.getInstance().getSysOid(ipAddress,community)==null)
    		       return -3;     //��֧��snmp 
    	}

	    	    
	    host = new com.afunms.discovery.Host();	    	    
	    int result = 0;
	    int id = 0;
	    try //���°������ӵ����������ݼ������ݿ�
	    {	       
	       id = KeyGenerator.getInstance().getNextKey();
	       host.setId(id);
	       host.setCategory(category);
	       host.setOstype(ostype);
	       host.setCollecttype(collecttype);
	       if(collecttype == SystemConstant.COLLECTTYPE_SNMP){
		       host.setCommunity(community);
		       host.setWritecommunity(writecommunity);
		       host.setAlias(alias);
		       host.setIpAddress(ipAddress);
		       host.setSysOid(SnmpUtil.getInstance().getSysOid(ipAddress,community));	 
		       host.setMac(SnmpUtil.getInstance().getBridgeAddress(ipAddress, community));
		       host.setBridgeAddress(SnmpUtil.getInstance().getBridgeAddress(ipAddress, community));
		       host.setIfEntityList(SnmpUtil.getInstance().getIfEntityList(ipAddress,community,category));	    	 
		       host.setLocalNet(0);
		       host.setNetMask("255.255.255.0");
		       host.setDiscoverstatus(-1);
		       SnmpUtil snmp = SnmpUtil.getInstance();
		    	SysLogger.info("��ʼ��ȡ�豸:"+host.getIpAddress()+"��ϵͳ����");
		    	Hashtable sysGroupProperty = snmp.getSysGroup(host.getIpAddress(),host.getCommunity());
		    	if(sysGroupProperty != null){
		    		host.setSysDescr((String)sysGroupProperty.get("sysDescr"));
		    		host.setSysContact((String)sysGroupProperty.get("sysContact"));
		    		host.setSysName((String)sysGroupProperty.get("sysName"));
		    		host.setSysLocation((String)sysGroupProperty.get("sysLocation"));
		    	}
		       
		       SubnetDao netDao = new SubnetDao();
		       List netList = null;
		       try{
		    	   netList = netDao.loadAll(); //�ҳ��������ĸ�����
		       }catch(Exception e){
		    	   e.printStackTrace();
		       }finally{
		    	   netDao.close();
		       }
		       for(int i=0;i<netList.size();i++)
		       {
		       	  Subnet net = (Subnet)netList.get(i);
		    	  if(NetworkUtil.isValidIP(net.getNetAddress(),net.getNetMask(),ipAddress))
		    	  {
		    		  host.setLocalNet(net.getId());
		    		  host.setNetMask(net.getNetMask());
		    		  break;
		    	  }	
		       }	    	   
	       }else{
	    	   //����������
		       host.setAlias(alias);
		       //SysLogger.info(alias+"================");
		       if(ostype== 6){
		    	   //AIX
		    	   host.setSysOid("1.3.6.1.4.1.2.3.1.2.1.1");
		    	   host.setType("IBM AIX ������");
		       }else if(ostype==7){
		    	   //HP UNIX
		    	   host.setSysOid("1.3.6.1.4.1.11.2.3.10.1");
		    	   host.setType("HP UNIX ������");
		       }else if(ostype==8){
		    	   //SUN SOLARIS
		    	   host.setSysOid("1.3.6.1.4.1.42.2.1.1");
		    	   host.setType("SUN SOLARIS ������");
		       }else if(ostype==9){
		    	   //LINUX
		    	   host.setSysOid("1.3.6.1.4.1.2021.250.10");
		    	   host.setType("LINUX ������");
		       }else if(ostype==5){
		    	   //WINDOWS
		    	   host.setSysOid("1.3.6.1.4.1.311.1.1.3");
		    	   host.setType("Windows ������");
		       }
		       host.setIpAddress(ipAddress);
		       host.setLocalNet(0);
		       host.setNetMask("255.255.255.0");
		       host.setDiscoverstatus(-1);
		       SubnetDao netDao = new SubnetDao();
		       List netList = netDao.loadAll(); //�ҳ��������ĸ�����
		       for(int i=0;i<netList.size();i++)
		       {
		       	  Subnet net = (Subnet)netList.get(i);
		    	  if(NetworkUtil.isValidIP(net.getNetAddress(),net.getNetMask(),ipAddress))
		    	  {
		    		  host.setLocalNet(net.getId());
		    		  host.setNetMask(net.getNetMask());
		    		  break;
		    	  }	
		       }
	       }
	       host.setSendemail("");
	       host.setSendmobiles("");
	       host.setSendphone("");
	       host.setBid("");
	       List hostList = new ArrayList(1);
	       hostList.add(host);
	       
	       DiscoverCompleteDao dcDao = new DiscoverCompleteDao();	
	       try{
	    	   dcDao.addHostDataByHand(hostList);
	    	   dcDao.addInterfaceData(hostList);		   
	    	   dcDao.addMonitor(hostList);
	       }catch(Exception e){
	    	   e.printStackTrace();
	       }finally{
	    	   dcDao.close();
	       }
		   		   
		   result = id; //���ݳɹ�����,����ID		   
	    }
	    catch(Exception e)
	    {
	    	SysLogger.error("TopoHelper.addHost(),insert db",e);	    	
	    }	    
	    if(result==0) return 0; //�����������ʧ���򲻼���
	    
	    HostNodeDao dao = new HostNodeDao();
	    try //���°������������ڴ�(��PollingInitializtion.loadHost()���)
	    {	    		    		       		
	    	HostNode vo = (HostNode)dao.findByID(String.valueOf(id));
	    	HostLoader loader = new HostLoader();
	    	loader.loadOne(vo);
	    	loader.close();
	    	SysLogger.info("�ɹ�����һ̨����,id=" + id);
	    }
	    catch(Exception e)
	    {
	    	SysLogger.error("TopoUtil.addHost(),insert memory",e);	    
	    	result = 0;
	    }finally{
	    	dao.close();
	    }
	    return result;
    }
    
    /**
     * @author nielin add for time-sharing
     * @since 2009-12-29
     * @param String ipAddress,String alias,String community,String writecommunity,int category,
     *        int ostype,int collecttype,String bid,String sendmobiles,String sendemail,String sendphone
     *        ip,,����ͬ��,д��ͬ��,
	 * ����һ̨����
	 */
    public int addHost(String assetid,String location,String ipAddress,String alias,int snmpversion,String community,String writecommunity,int transfer,int category,int ostype,int collecttype,
    		String bid,String sendmobiles,String sendemail,String sendphone)
    {
    	HostNodeDao tmpDao = new HostNodeDao();
    	//List tmpList = tmpDao.findByCondition1("ip_address",ipAddress);
    	List tmpList = tmpDao.findBynode("ip_address",ipAddress);
    	if(tmpList.size()>0)
    	   return -1;     //IP�Ѿ�����
    	/*
    	 * ����Ϊ�˲�����,ĿǰPING��ͨ���豸Ҳ���Լӽ���
    	 */
    	//if(NetworkUtil.ping(ipAddress)==0) 
	       //return -2;     //ping��ͨ
    	if(collecttype == SystemConstant.COLLECTTYPE_SNMP){
    		//SNMP�ɼ���ʽ
    	    if(SnmpUtil.getInstance().getSysOid(ipAddress,community)==null)
    		       return -3;     //��֧��snmp 
    	}

	    	    
	    host = new com.afunms.discovery.Host();	    	    
	    int result = 0;
	    int id = 0;
	    try //���°������ӵ����������ݼ������ݿ�
	    {	       
	       id = KeyGenerator.getInstance().getNextKey();
	       host.setId(id);
	       host.setCategory(category);
	       host.setOstype(ostype);
	       host.setCollecttype(collecttype);
	       host.setAssetid(assetid);
	       host.setLocation(location);
	       host.setTransfer(transfer);
	       if(collecttype == SystemConstant.COLLECTTYPE_SNMP){
		       host.setCommunity(community);
		       host.setWritecommunity(writecommunity);
		       host.setAlias(alias);
		       host.setSnmpversion(snmpversion);
		       
		       host.setIpAddress(ipAddress);
		       host.setSysOid(SnmpUtil.getInstance().getSysOid(ipAddress,community));	 
		       host.setMac(SnmpUtil.getInstance().getBridgeAddress(ipAddress, community));
		       host.setBridgeAddress(SnmpUtil.getInstance().getBridgeAddress(ipAddress, community));
		       host.setIfEntityList(SnmpUtil.getInstance().getIfEntityList(ipAddress,community,category));	    	 
		       host.setLocalNet(0);
		       host.setNetMask("255.255.255.0");
		       host.setDiscoverstatus(-1);
		       SnmpUtil snmp = SnmpUtil.getInstance();
		    	SysLogger.info("��ʼ��ȡ�豸:"+host.getIpAddress()+"��ϵͳ����");
		    	Hashtable sysGroupProperty = snmp.getSysGroup(host.getIpAddress(),host.getCommunity());
		    	if(sysGroupProperty != null){
		    		host.setSysDescr((String)sysGroupProperty.get("sysDescr"));
		    		host.setSysContact((String)sysGroupProperty.get("sysContact"));
		    		host.setSysName((String)sysGroupProperty.get("sysName"));
		    		host.setSysLocation((String)sysGroupProperty.get("sysLocation"));
		    	}
		       
		       SubnetDao netDao = new SubnetDao();
		       List netList = netDao.loadAll(); //�ҳ��������ĸ�����
		       for(int i=0;i<netList.size();i++)
		       {
		       	  Subnet net = (Subnet)netList.get(i);
		    	  if(NetworkUtil.isValidIP(net.getNetAddress(),net.getNetMask(),ipAddress))
		    	  {
		    		  host.setLocalNet(net.getId());
		    		  host.setNetMask(net.getNetMask());
		    		  break;
		    	  }	
		       }	    	   
	       }else if(collecttype == SystemConstant.COLLECTTYPE_SHELL || collecttype == SystemConstant.COLLECTTYPE_TELNET || collecttype == SystemConstant.COLLECTTYPE_SSH){
	    	   //����������
		       host.setAlias(alias);
		       //SysLogger.info(alias+"================");
		       if(ostype== 6){
		    	   //AIX
		    	   host.setSysOid("1.3.6.1.4.1.2.3.1.2.1.1");
		    	   host.setType("IBM AIX ������");
		       }else if(ostype==7){
		    	   //HP UNIX
		    	   host.setSysOid("1.3.6.1.4.1.11.2.3.10.1");
		    	   host.setType("HP UNIX ������");
		       }else if(ostype==8){
		    	   //SUN SOLARIS
		    	   host.setSysOid("1.3.6.1.4.1.42.2.1.1");
		    	   host.setType("SUN SOLARIS ������");
		       }else if(ostype==9){
		    	   //LINUX
		    	   host.setSysOid("1.3.6.1.4.1.2021.250.10");
		    	   host.setType("LINUX ������");
		       }else if(ostype==5){
		    	   //WINDOWS
		    	   host.setSysOid("1.3.6.1.4.1.311.1.1.3");
		    	   host.setType("Windows ������");
		       } else if (ostype == 15 ){
					host.setType("AS400 ������");
		       }
		       host.setIpAddress(ipAddress);
		       host.setLocalNet(0);
		       host.setNetMask("255.255.255.0");
		       host.setDiscoverstatus(-1);
		       SubnetDao netDao = new SubnetDao();
		       List netList = netDao.loadAll(); //�ҳ��������ĸ�����
		       for(int i=0;i<netList.size();i++)
		       {
		       	  Subnet net = (Subnet)netList.get(i);
		    	  if(NetworkUtil.isValidIP(net.getNetAddress(),net.getNetMask(),ipAddress))
		    	  {
		    		  host.setLocalNet(net.getId());
		    		  host.setNetMask(net.getNetMask());
		    		  break;
		    	  }	
		       }
	       }else if(collecttype == SystemConstant.COLLECTTYPE_PING){
	    	   //����������
		       host.setAlias(alias);
		       if(ostype== 6){
		    	   //AIX
		    	   host.setSysOid("1.3.6.1.4.1.2.3.1.2.1.1");
		    	   host.setType("IBM AIX ������");
		       }else if(ostype==7){
		    	   //HP UNIX
		    	   host.setSysOid("1.3.6.1.4.1.11.2.3.10.1");
		    	   host.setType("HP UNIX ������");
		       }else if(ostype==8){
		    	   //SUN SOLARIS
		    	   host.setSysOid("1.3.6.1.4.1.42.2.1.1");
		    	   host.setType("SUN SOLARIS ������");
		       }else if(ostype==9){
		    	   //LINUX
		    	   host.setSysOid("1.3.6.1.4.1.2021.250.10");
		    	   host.setType("LINUX ������");
		       }else if(ostype==5){
		    	   //WINDOWS
		    	   host.setSysOid("1.3.6.1.4.1.311.1.1.3");
		    	   host.setType("Windows ������");
		       }else if(ostype==1){
		    	   //CISCO
		    	   host.setSysOid("1.3.6.1.4.1.9");
		    	   host.setType("Cisco");
		       }else if(ostype==2){
		    	   //H3C
		    	   host.setSysOid("1.3.6.1.4.1.2011");
		    	   host.setType("H3C");
		       }else if(ostype==3){
		    	   //Entrasys
		    	   host.setSysOid("1.3.6.1.4.1.9.2.1.57");
		    	   host.setType("Entrasys");
		       }else if(ostype==4){
		    	   //Radware
		    	   host.setSysOid("1.3.6.1.4.1.89");
		    	   host.setType("Radware");
		       }else if(ostype==10){
		    	   //MaiPu
		    	   host.setSysOid("1.3.6.1.4.1.5651");
		    	   host.setType("MaiPu");
		       }else if(ostype==11){
		    	   //RedGiant
		    	   host.setSysOid("1.3.6.1.4.1.4881");
		    	   host.setType("RedGiant");
		       } else if (ostype == 12) {
					// NorthTel
					host.setSysOid("1.3.6.1.4.1.45");
					host.setType("NorthTel");
				} else if (ostype == 13) {
					// D-Link
					host.setSysOid("1.3.6.1.4.1.171");
					host.setType("DLink");
				} else if (ostype == 14) {
					// BDCom
					host.setSysOid("1.3.6.1.4.1.3320");
					host.setType("BDCom");
		       }
		       host.setIpAddress(ipAddress);
		       host.setLocalNet(0);
		       host.setNetMask("255.255.255.0");
		       host.setDiscoverstatus(-1);
		       SubnetDao netDao = new SubnetDao();
		       List netList = netDao.loadAll(); //�ҳ��������ĸ�����
		       for(int i=0;i<netList.size();i++)
		       {
		       	  Subnet net = (Subnet)netList.get(i);
		    	  if(NetworkUtil.isValidIP(net.getNetAddress(),net.getNetMask(),ipAddress))
		    	  {
		    		  host.setLocalNet(net.getId());
		    		  host.setNetMask(net.getNetMask());
		    		  break;
		    	  }	
		       }
	       }else if(collecttype == SystemConstant.COLLECTTYPE_REMOTEPING){
	    	   //Զ��PING���豸
	    	   //����������
		       host.setAlias(alias);
		       if(ostype== 6){
		    	   //AIX
		    	   host.setSysOid("1.3.6.1.4.1.2.3.1.2.1.1");
		    	   host.setType("IBM AIX ������");
		       }else if(ostype==7){
		    	   //HP UNIX
		    	   host.setSysOid("1.3.6.1.4.1.11.2.3.10.1");
		    	   host.setType("HP UNIX ������");
		       }else if(ostype==8){
		    	   //SUN SOLARIS
		    	   host.setSysOid("1.3.6.1.4.1.42.2.1.1");
		    	   host.setType("SUN SOLARIS ������");
		       }else if(ostype==9){
		    	   //LINUX
		    	   host.setSysOid("1.3.6.1.4.1.2021.250.10");
		    	   host.setType("LINUX ������");
		       }else if(ostype==5){
		    	   //WINDOWS
		    	   host.setSysOid("1.3.6.1.4.1.311.1.1.3");
		    	   host.setType("Windows ������");
		       }else if(ostype==1){
		    	   //CISCO
		    	   host.setSysOid("1.3.6.1.4.1.9");
		    	   host.setType("Cisco");
		       }else if(ostype==2){
		    	   //H3C
		    	   host.setSysOid("1.3.6.1.4.1.2011");
		    	   host.setType("H3C");
		       }else if(ostype==3){
		    	   //Entrasys
		    	   host.setSysOid("1.3.6.1.4.1.9.2.1.57");
		    	   host.setType("Entrasys");
		       }else if(ostype==4){
		    	   //Radware
		    	   host.setSysOid("1.3.6.1.4.1.89");
		    	   host.setType("Radware");
		       }else if(ostype==10){
		    	   //MaiPu
		    	   host.setSysOid("1.3.6.1.4.1.5651");
		    	   host.setType("MaiPu");
		       }else if(ostype==11){
		    	   //RedGiant
		    	   host.setSysOid("1.3.6.1.4.1.4881");
		    	   host.setType("RedGiant");
		       } else if (ostype == 12) {
					// NorthTel
					host.setSysOid("1.3.6.1.4.1.45");
					host.setType("NorthTel");
				} else if (ostype == 13) {
					// D-Link
					host.setSysOid("1.3.6.1.4.1.171");
					host.setType("DLink");
				} else if (ostype == 14) {
					// BDCom
					host.setSysOid("1.3.6.1.4.1.3320");
					host.setType("BDCom");
		       }
		       host.setIpAddress(ipAddress);
		       host.setLocalNet(0);
		       host.setNetMask("255.255.255.0");
		       host.setDiscoverstatus(-1);
		       SubnetDao netDao = new SubnetDao();
		       List netList = netDao.loadAll(); //�ҳ��������ĸ�����
		       for(int i=0;i<netList.size();i++)
		       {
		       	  Subnet net = (Subnet)netList.get(i);
		    	  if(NetworkUtil.isValidIP(net.getNetAddress(),net.getNetMask(),ipAddress))
		    	  {
		    		  host.setLocalNet(net.getId());
		    		  host.setNetMask(net.getNetMask());
		    		  break;
		    	  }	
		       } 
	       }
	       host.setSendemail(sendemail);
	       host.setSendmobiles(sendmobiles);
	       host.setSendphone(sendphone);
	       host.setBid(bid);
	       List hostList = new ArrayList(1);
	       hostList.add(host);
	       
	       DiscoverCompleteDao dcDao = new DiscoverCompleteDao();	
	       try{
	    	   dcDao.addHostDataByHand(hostList);
	    	   dcDao.addInterfaceData(hostList);		   
	    	   dcDao.addMonitor(hostList);
	       }catch(Exception e){
	    	   e.printStackTrace();
	       }finally{
	    	   dcDao.close();
	       }
	       
	     //nielin add for as400 start
			
			if(ostype == 15 ){
				//as400������
				DiscoverCompleteDao dcDao2 = new DiscoverCompleteDao();
				
				dcDao2.createTableForAS400(host);
			}
			
			//nielin add for as400 end
			
		   result = id; //���ݳɹ�����,����ID		   
	    }
	    catch(Exception e)
	    {
	    	SysLogger.error("TopoHelper.addHost(),insert db",e);	    	
	    }	    
	    if(result==0) return 0; //�����������ʧ���򲻼���
	    
	    try //���°������������ڴ�(��PollingInitializtion.loadHost()���)
	    {	    		    		   
	    	HostNodeDao dao = new HostNodeDao();	
	    	HostNode vo = null;
	    	try{
	    		vo = (HostNode)dao.findByID(String.valueOf(id));
	    	}catch(Exception e){
	    		e.printStackTrace();
	    	}finally{
	    		dao.close();
	    	}
	    	HostLoader loader = new HostLoader();
	    	try{
	    		loader.loadOne(vo);
	    	}catch(Exception e){
	    		e.printStackTrace();
	    	}finally{
	    		loader.close();
	    	}
	    	
	    	SysLogger.info("�ɹ�����һ̨����,id=" + id);
	    }
	    catch(Exception e)
	    {
	    	SysLogger.error("TopoUtil.addHost(),insert memory",e);	    
	    	result = 0;
	    }	    	    
	    return result;
    }
    
	/**
	 * @author snow
	 * @param assetid
	 * @param location
	 * @param ipAddress
	 * @param alias
	 * @param community
	 * @param writecommunity
	 * @param category
	 * @param ostype
	 * @param collecttype
	 * @param bid
	 * @param sendmobiles
	 * @param sendemail
	 * @param sendphone
	 * @param supperid
	 * @return 
	 * @date 2010-05-18
	 */
	public int addHost(String assetid,String location,String ipAddress, String alias,int snmpversion, String community,
			String writecommunity,int transfer, int category, int ostype, int collecttype,
			String bid, String sendmobiles, String sendemail, String sendphone, int supperid) {
		HostNodeDao tmpDao = new HostNodeDao();
		List tmpList = new ArrayList();
		try{
			tmpList = tmpDao.findBynode("ip_address", ipAddress);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			tmpDao.close();
		}
		//SysLogger.info("ipAddress:"+ipAddress+"#############");
		if (tmpList.size() > 0)
			return -1; // IP�Ѿ�����
		/*
		 * ����Ϊ�˲�����,ĿǰPING��ͨ���豸Ҳ���Լӽ���
		 */
		// if(NetworkUtil.ping(ipAddress)==0)
		// return -2; //ping��ͨ
		if (collecttype == SystemConstant.COLLECTTYPE_SNMP) {
			// SNMP�ɼ���ʽ
			if (SnmpUtil.getInstance().getSysOid(ipAddress, community) == null)
				return -3; // ��֧��snmp
		}

		host = new com.afunms.discovery.Host();
		int result = 0;
		int id = 0;
		try // ���°������ӵ����������ݼ������ݿ�
		{
			id = KeyGenerator.getInstance().getNextKey();
			host.setId(id);
			host.setCategory(category);
			host.setOstype(ostype);
			host.setCollecttype(collecttype);
			host.setSupperid(supperid);
			host.setAssetid(assetid);
			host.setLocation(location);
			host.setTransfer(transfer);
			if (collecttype == SystemConstant.COLLECTTYPE_SNMP) {
				host.setCommunity(community);
				host.setWritecommunity(writecommunity);
				host.setSnmpversion(snmpversion);
				host.setAlias(alias);
				host.setIpAddress(ipAddress);
				host.setSysOid(SnmpUtil.getInstance().getSysOid(ipAddress,
						community));
				host.setMac(SnmpUtil.getInstance().getBridgeAddress(ipAddress,
						community));
//				host.setBridgeAddress(SnmpUtil.getInstance().getBridgeAddress(
//						ipAddress, community));
				if(host.getCategory()==4){
					host.setBridgeAddress(SnmpUtil.getInstance().getHostBridgeAddress(
							ipAddress, community));
				}else{
					host.setBridgeAddress(SnmpUtil.getInstance().getBridgeAddress(
						ipAddress, community));
				}
				if(host.getSysOid().startsWith("1.3.6.1.4.1.1588.2")){//���Ƶ������豸
					host.setIfEntityList(SnmpUtil.getInstance().getIfEntityList_brocade(
							ipAddress, community, category));
				}else{
					host.setIfEntityList(SnmpUtil.getInstance().getIfEntityList(
							ipAddress, community, category));
				}
				host.setLocalNet(0);
				host.setNetMask("255.255.255.0");
				host.setDiscoverstatus(-1);
				SnmpUtil snmp = SnmpUtil.getInstance();
				SysLogger.info("��ʼ��ȡ�豸:" + host.getIpAddress() + "��ϵͳ����");
				Hashtable sysGroupProperty = snmp.getSysGroup(host
						.getIpAddress(), host.getCommunity());
				if (sysGroupProperty != null) {
					host.setSysDescr((String) sysGroupProperty.get("sysDescr"));
					host.setSysContact((String) sysGroupProperty
							.get("sysContact"));
					host.setSysName((String) sysGroupProperty.get("sysName"));
					host.setSysLocation((String) sysGroupProperty
							.get("sysLocation"));
				}

				SubnetDao netDao = new SubnetDao();
				List netList = netDao.loadAll(); // �ҳ��������ĸ�����
				for (int i = 0; i < netList.size(); i++) {
					Subnet net = (Subnet) netList.get(i);
					if (NetworkUtil.isValidIP(net.getNetAddress(), net
							.getNetMask(), ipAddress)) {
						host.setLocalNet(net.getId());
						host.setNetMask(net.getNetMask());
						break;
					}
				}
			} else if (collecttype == SystemConstant.COLLECTTYPE_SHELL) {
				// ����������
				host.setAlias(alias);
				// SysLogger.info(alias+"================");
				if (ostype == 6) {
					// AIX
					host.setSysOid("1.3.6.1.4.1.2.3.1.2.1.1");
					host.setType("IBM AIX ������");
				} else if (ostype == 7) {
					// HP UNIX
					host.setSysOid("1.3.6.1.4.1.11.2.3.10.1");
					host.setType("HP UNIX ������");
				} else if (ostype == 8) {
					// SUN SOLARIS
					host.setSysOid("1.3.6.1.4.1.42.2.1.1");
					host.setType("SUN SOLARIS ������");
				} else if (ostype == 9) {
					// LINUX
					host.setSysOid("1.3.6.1.4.1.2021.250.10");
					host.setType("LINUX ������");
				} else if (ostype == 5) {
					// WINDOWS
					host.setSysOid("1.3.6.1.4.1.311.1.1.3");
					host.setType("Windows ������");
				} else if (ostype == 15 ){
					host.setSysOid("as400");
					host.setType("AS400 ������");
				} else if (ostype == 20 ){
		    	    host.setSysOid("scounix");
				    host.setType("SCOUNIXWARE ������");
		        } else if (ostype == 21 ){
			    	host.setSysOid("scoopenserver");
					host.setType("SCOOPENSERVER ������");
			    }
				host.setIpAddress(ipAddress);
				host.setLocalNet(0);
				host.setNetMask("255.255.255.0");
				host.setDiscoverstatus(-1);
				SubnetDao netDao = new SubnetDao();
				List netList = netDao.loadAll(); // �ҳ��������ĸ�����
				for (int i = 0; i < netList.size(); i++) {
					Subnet net = (Subnet) netList.get(i);
					if (NetworkUtil.isValidIP(net.getNetAddress(), net
							.getNetMask(), ipAddress)) {
						host.setLocalNet(net.getId());
						host.setNetMask(net.getNetMask());
						break;
					}
				}
			} else if (collecttype == SystemConstant.COLLECTTYPE_WMI) {
				// ����������
				host.setAlias(alias);
				// SysLogger.info(alias+"================");

				if (ostype == 5) {
					// WINDOWS
					host.setSysOid("1.3.6.1.4.1.311.1.1.3");
					host.setType("Windows ������");
				} 
				host.setIpAddress(ipAddress);
				host.setLocalNet(0);
				host.setNetMask("255.255.255.0");
				host.setDiscoverstatus(-1);
				SubnetDao netDao = new SubnetDao();
				List netList = netDao.loadAll(); // �ҳ��������ĸ�����
				for (int i = 0; i < netList.size(); i++) {
					Subnet net = (Subnet) netList.get(i);
					if (NetworkUtil.isValidIP(net.getNetAddress(), net.getNetMask(), ipAddress)) {
						host.setLocalNet(net.getId());
						host.setNetMask(net.getNetMask());
						break;
					}
				}
			} else if (collecttype == SystemConstant.COLLECTTYPE_TELNET || collecttype == SystemConstant.COLLECTTYPE_SSH) {
				// ����������
				host.setAlias(alias);
				// SysLogger.info(alias+"================");
				if (ostype == 6) {
					// AIX
					host.setSysOid("1.3.6.1.4.1.2.3.1.2.1.1");
					host.setType("IBM AIX ������");
				} else if (ostype == 7) {
					// HP UNIX
					host.setSysOid("1.3.6.1.4.1.11.2.3.10.1");
					host.setType("HP UNIX ������");
				} else if (ostype == 8) {
					// SUN SOLARIS
					host.setSysOid("1.3.6.1.4.1.42.2.1.1");
					host.setType("SUN SOLARIS ������");
				} else if (ostype == 9) {
					// LINUX
					host.setSysOid("1.3.6.1.4.1.2021.250.10");
					host.setType("LINUX ������");
				} else if (ostype == 5) {
					// WINDOWS
					host.setSysOid("1.3.6.1.4.1.311.1.1.3");
					host.setType("Windows ������");
				} else if (ostype == 15 ){
					host.setSysOid("as400");
					host.setType("AS400 ������");
				} else if (ostype == 20 ){
		    	    host.setSysOid("scounix");
				    host.setType("SCOUNIXWARE ������");
		        } else if (ostype == 21 ){
			    	host.setSysOid("scoopenserver");
					host.setType("SCOOPENSERVER ������");
			    }
				host.setIpAddress(ipAddress);
				host.setLocalNet(0);
				host.setNetMask("255.255.255.0");
				host.setDiscoverstatus(-1);
				SubnetDao netDao = new SubnetDao();
				List netList = netDao.loadAll(); // �ҳ��������ĸ�����
				for (int i = 0; i < netList.size(); i++) {
					Subnet net = (Subnet) netList.get(i);
					if (NetworkUtil.isValidIP(net.getNetAddress(), net
							.getNetMask(), ipAddress)) {
						host.setLocalNet(net.getId());
						host.setNetMask(net.getNetMask());
						break;
					}
				}
			} else if (collecttype == SystemConstant.COLLECTTYPE_PING || collecttype == SystemConstant.COLLECTTYPE_TELNETCONNECT 
					||collecttype == SystemConstant.COLLECTTYPE_SSHCONNECT || collecttype == SystemConstant.COLLECTTYPE_DATAINTERFACE) {
				//PING TELNET��SSH��ͨ��� �� ���ݽӿڲɼ���ʽ
				host.setAlias(alias);
				if (ostype == 6) {
					// AIX
					host.setSysOid("1.3.6.1.4.1.2.3.1.2.1.1");
					host.setType("IBM AIX ������");
				} else if (ostype == 7) {
					// HP UNIX
					host.setSysOid("1.3.6.1.4.1.11.2.3.10.1");
					host.setType("HP UNIX ������");
				} else if (ostype == 8) {
					// SUN SOLARIS
					host.setSysOid("1.3.6.1.4.1.42.2.1.1");
					host.setType("SUN SOLARIS ������");
				} else if (ostype == 9) {
					// LINUX
					host.setSysOid("1.3.6.1.4.1.2021.250.10");
					host.setType("LINUX ������");
				} else if (ostype == 5) {
					// WINDOWS
					host.setSysOid("1.3.6.1.4.1.311.1.1.3");
					host.setType("Windows ������");
				} else if (ostype == 15 ){
					host.setSysOid("as400");
					host.setType("AS400 ������");
				} else if (ostype == 20 ){
		    	    host.setSysOid("scounix");
				    host.setType("SCOUNIXWARE ������");
		        } else if (ostype == 21 ){
			    	host.setSysOid("scoopenserver");
					host.setType("SCOOPENSERVER ������");
			    } else if (ostype == 1) {
					// CISCO
					host.setSysOid("1.3.6.1.4.1.9.");
					host.setType("Cisco");
				} else if (ostype == 2) {
					// H3C
					host.setSysOid("1.3.6.1.4.1.2011.");
					host.setType("H3C");
				} else if (ostype == 3) {
					// Entrasys
					host.setSysOid("1.3.6.1.4.1.9.2.1.57.");
					host.setType("Entrasys");
				} else if (ostype == 4) {
					// Radware
					host.setSysOid("1.3.6.1.4.1.89.");
					host.setType("Radware");
				} else if (ostype == 10) {
					// MaiPu
					host.setSysOid("1.3.6.1.4.1.5651.");
					host.setType("MaiPu");
				} else if (ostype == 11) {
					// RedGiant
					host.setSysOid("1.3.6.1.4.1.4881.");
					host.setType("RedGiant");
				} else if (ostype == 12) {
					// NorthTel
					host.setSysOid("1.3.6.1.4.1.45.");
					host.setType("NorthTel");
				} else if (ostype == 13) {
					// D-Link
					host.setSysOid("1.3.6.1.4.1.171.");
					host.setType("DLink");
				} else if (ostype == 14) {
					// BDCom
					host.setSysOid("1.3.6.1.4.1.3320.");
					host.setType("BDCom");
				} else if (ostype == 16) {
					// ZTE
					host.setSysOid("1.3.6.1.4.1.3902.");
					host.setType("ZTE");
				} else if (ostype == 17) {
					// ATM
					host.setSysOid("net_atm");
					host.setType("ATM");
				} else if (ostype == 18) {
					// Array Networks
					host.setSysOid("1.3.6.1.4.1.7564");
					host.setType("ArrayNetworks");
				}
				host.setIpAddress(ipAddress);
				host.setLocalNet(0);
				host.setNetMask("255.255.255.0");
				host.setDiscoverstatus(-1);
				SubnetDao netDao = new SubnetDao();
				List netList = netDao.loadAll(); // �ҳ��������ĸ�����
				for (int i = 0; i < netList.size(); i++) {
					Subnet net = (Subnet) netList.get(i);
					if (NetworkUtil.isValidIP(net.getNetAddress(), net
							.getNetMask(), ipAddress)) {
						host.setLocalNet(net.getId());
						host.setNetMask(net.getNetMask());
						break;
					}
				}
			} else if (collecttype == SystemConstant.COLLECTTYPE_REMOTEPING) {
				// Զ��PING���豸
				host.setAlias(alias);
				if (ostype == 6) {
					// AIX
					host.setSysOid("1.3.6.1.4.1.2.3.1.2.1.1");
					host.setType("IBM AIX ������");
				} else if (ostype == 7) {
					// HP UNIX
					host.setSysOid("1.3.6.1.4.1.11.2.3.10.1");
					host.setType("HP UNIX ������");
				} else if (ostype == 8) {
					// SUN SOLARIS
					host.setSysOid("1.3.6.1.4.1.42.2.1.1");
					host.setType("SUN SOLARIS ������");
				} else if (ostype == 9) {
					// LINUX
					host.setSysOid("1.3.6.1.4.1.2021.250.10");
					host.setType("LINUX ������");
				} else if (ostype == 5) {
					// WINDOWS
					host.setSysOid("1.3.6.1.4.1.311.1.1.3");
					host.setType("Windows ������");
				} else if (ostype == 15 ){
					host.setSysOid("as400");
					host.setType("AS400 ������");
				} else if (ostype == 20 ){
		    	    host.setSysOid("scounix");
				    host.setType("SCOUNIXWARE ������");
		        } else if (ostype == 21 ){
			    	host.setSysOid("scoopenserver");
					host.setType("SCOOPENSERVER ������");
			    } else if (ostype == 1) {
					// CISCO
					host.setSysOid("1.3.6.1.4.1.9");
					host.setType("Cisco");
				} else if (ostype == 2) {
					// H3C
					host.setSysOid("1.3.6.1.4.1.2011");
					host.setType("H3C");
				} else if (ostype == 3) {
					// Entrasys
					host.setSysOid("1.3.6.1.4.1.9.2.1.57");
					host.setType("Entrasys");
				} else if (ostype == 4) {
					// Radware
					host.setSysOid("1.3.6.1.4.1.89");
					host.setType("Radware");
				} else if (ostype == 10) {
					// MaiPu
					host.setSysOid("1.3.6.1.4.1.5651");
					host.setType("MaiPu");
				} else if (ostype == 11) {
					// RedGiant
					host.setSysOid("1.3.6.1.4.1.4881");
					host.setType("RedGiant");
				} else if (ostype == 12) {
					// NorthTel
					host.setSysOid("1.3.6.1.4.1.45");
					host.setType("NorthTel");
				} else if (ostype == 13) {
					// D-Link
					host.setSysOid("1.3.6.1.4.1.171");
					host.setType("DLink");
				} else if (ostype == 14) {
					// BDCom
					host.setSysOid("1.3.6.1.4.1.3320");
					host.setType("BDCom");
				} else if (ostype == 16) {
					// ZTE
					host.setSysOid("1.3.6.1.4.1.3902");
					host.setType("ZTE");
				} else if (ostype == 12) {
					// Array Networks
					host.setSysOid("1.3.6.1.4.1.7564");
					host.setType("ArrayNetworks");
				}
				host.setIpAddress(ipAddress);
				host.setLocalNet(0);
				host.setNetMask("255.255.255.0");
				host.setDiscoverstatus(-1);
				SubnetDao netDao = new SubnetDao();
				List netList = netDao.loadAll(); // �ҳ��������ĸ�����
				for (int i = 0; i < netList.size(); i++) {
					Subnet net = (Subnet) netList.get(i);
					if (NetworkUtil.isValidIP(net.getNetAddress(), net
							.getNetMask(), ipAddress)) {
						host.setLocalNet(net.getId());
						host.setNetMask(net.getNetMask());
						break;
					}
				}
			}
			host.setSendemail(sendemail);
			host.setSendmobiles(sendmobiles);
			host.setSendphone(sendphone);
			host.setBid(bid);
			List hostList = new ArrayList(1);
			hostList.add(host);

			DiscoverCompleteDao dcDao = new DiscoverCompleteDao();
			try {
				dcDao.addHostDataByHand(hostList);
				dcDao.addInterfaceData(hostList);
				dcDao.addMonitor(hostList);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				dcDao.close();
			}

			//nielin add for as400 start
			
			if(ostype == 15 ){
				//as400������
				DiscoverCompleteDao dcDao2 = new DiscoverCompleteDao();
				
				dcDao2.createTableForAS400(host);
			}
			
			//nielin add for as400 end
			
			result = id; // ���ݳɹ�����,����ID
		} catch (Exception e) {
			SysLogger.error("TopoHelper.addHost(),insert db", e);
		}
		if (result == 0)
			return 0; // �����������ʧ���򲻼���

		try // ���°������������ڴ�(��PollingInitializtion.loadHost()���)
		{
			HostNodeDao dao = new HostNodeDao();
			HostNode vo = null;
			try {
				vo = (HostNode) dao.findByID(String.valueOf(id));
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				dao.close();
			}
			HostLoader loader = new HostLoader();
			try {
				loader.loadOne(vo);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				loader.close();
			}

			SysLogger.info("�ɹ�����һ̨����,id=" + id);
		} catch (Exception e) {
			SysLogger.error("TopoUtil.addHost(),insert memory", e);
			result = 0;
		}
		return result;
	}
    
    /**
     * �������ӵ�����
     */
    public com.afunms.discovery.Host getHost()
    {
    	return host;
    }                 
}