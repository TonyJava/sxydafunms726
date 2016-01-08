/**
 * <p>Description:Tomcat Manager</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-12-07
 */

package com.afunms.application.manage;

import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.beanutils.BeanUtils;
import org.jfree.data.time.Minute;
import org.jfree.data.time.TimeSeries;

import com.afunms.alarm.dao.AlarmIndicatorsNodeDao;
import com.afunms.alarm.util.AlarmIndicatorsUtil;
import com.afunms.application.dao.HostApplyDao;
import com.afunms.application.dao.TomcatDao;
import com.afunms.application.model.Tomcat;
import com.afunms.application.tomcatmonitor.ServerStream;
import com.afunms.application.util.TomcatJvmReport;
import com.afunms.common.base.BaseManager;
import com.afunms.common.base.ErrorMessage;
import com.afunms.common.base.ManagerInterface;
import com.afunms.common.util.CEIString;
import com.afunms.common.util.ChartGraph;
import com.afunms.common.util.ChartXml;
import com.afunms.common.util.CreateTableManager;
import com.afunms.common.util.DBManager;
import com.afunms.common.util.DateE;
import com.afunms.common.util.SessionConstant;
import com.afunms.common.util.ShareData;
import com.afunms.common.util.SysLogger;
import com.afunms.common.util.SysUtil;
import com.afunms.config.dao.SupperDao;
import com.afunms.config.model.Supper;
import com.afunms.detail.service.tomcatInfo.TomcatInfoService;
import com.afunms.event.dao.EventListDao;
import com.afunms.indicators.dao.NodeGatherIndicatorsDao;
import com.afunms.indicators.model.NodeGatherIndicators;
import com.afunms.indicators.util.NodeGatherIndicatorsUtil;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.api.I_HostCollectData;
import com.afunms.polling.base.Node;
import com.afunms.polling.impl.HostCollectDataManager;
import com.afunms.polling.loader.TomcatLoader;
import com.afunms.polling.om.Pingcollectdata;
import com.afunms.polling.task.TomcatDataCollector;
import com.afunms.report.abstraction.JspReport;
import com.afunms.system.model.TimeGratherConfig;
import com.afunms.system.model.User;
import com.afunms.system.util.TimeGratherConfigUtil;
import com.afunms.system.util.TimeShareConfigUtil;
import com.afunms.topology.dao.DiscoverCompleteDao;
import com.afunms.topology.dao.LineDao;
import com.afunms.topology.dao.ManageXmlDao;
import com.afunms.topology.dao.NodeDependDao;
import com.afunms.topology.model.ManageXml;
import com.afunms.topology.model.NodeDepend;
import com.afunms.topology.util.KeyGenerator;



public class TomcatManager extends BaseManager implements ManagerInterface
{
	
	DateE datemanager = new DateE();
	private String list()
	{
		
		User operator = (User)session.getAttribute(SessionConstant.CURRENT_USER);
		String bids = operator.getBusinessids();
		String bid[] = bids.split(",");
		Vector rbids = new Vector();
		if(bid != null && bid.length>0){
			for(int i=0;i<bid.length;i++){
				if(bid[i] != null && bid[i].trim().length()>0)
					rbids.add(bid[i].trim());
			}
		}
		
		List list = null;
		TomcatDao dao = new TomcatDao();
		try{
			if(operator.getRole()==0){
				list = dao.loadAll();
			}else
				list = dao.getTomcatByBID(rbids);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			dao.close();
		}
		if(list == null)list = new ArrayList();
		for(int i=0;i<list.size();i++)
		{
			Tomcat vo = (Tomcat)list.get(i);
			Node tomcatNode = PollingEngine.getInstance().getTomcatByID(vo.getId());
			if(tomcatNode==null)
			   vo.setStatus(0);
			else
			   vo.setStatus(tomcatNode.getStatus());	
		}
		request.setAttribute("list",list);	
		return "/application/tomcat/list.jsp";
	}
	
	/**
	 * snow 增加前将供应商查找到
	 * @return
	 */
	private String ready_add(){
		SupperDao supperdao = new SupperDao();
    	List<Supper> allSupper = supperdao.loadAll();
    	request.setAttribute("allSupper", allSupper);
		return "/application/tomcat/add.jsp";
	}

	private String add()
    {    	   
		Tomcat vo = new Tomcat();
    	vo.setId(KeyGenerator.getInstance().getNextKey());
    	vo.setUser(getParaValue("user"));
    	vo.setPassword(getParaValue("password"));        
        vo.setAlias(getParaValue("alias"));
        vo.setIpAddress(getParaValue("ip_address"));
        vo.setPort(getParaValue("port"));
        vo.setSendmobiles(getParaValue("sendmobiles"));
		vo.setMonflag(getParaIntValue("monflag"));
		vo.setSendemail(getParaValue("sendemail"));
		vo.setSendphone(getParaValue("sendphone"));
		vo.setSupperid(getParaIntValue("supperid"));//snow add 2010-5-20
		vo.setVersion("");
		vo.setJvmversion("");
		vo.setJvmvender("");
		vo.setOs("");
		vo.setOsversion("");
        vo.setBid(getParaValue("bid"));
		
        //在数据库里增加被监控指标
        DiscoverCompleteDao dcDao = new DiscoverCompleteDao();
        try{
        	dcDao.addMonitor(vo.getId(),vo.getIpAddress(),"tomcat");
        }catch(Exception e){
        	e.printStackTrace();
        }finally{
        	dcDao.close();
        }
        
        //在轮询线程中增加被监视节点
        TomcatLoader loader = new TomcatLoader();
        try{
        	loader.loadOne(vo);
        	loader.close();
        }catch(Exception e){
        	e.printStackTrace();
        }finally{
        	loader.close();
        }
        
        
        TomcatDao dao = new TomcatDao();
        try{
        	dao.save(vo);
        	TimeShareConfigUtil timeShareConfigUtil = new TimeShareConfigUtil(); // nielin add for time-sharing at 2010-01-05
        	timeShareConfigUtil.saveTimeShareConfigList(request, String.valueOf(vo.getId()), timeShareConfigUtil.getObjectType("13"));
        	/* 增加采集时间设置 snow add at 2010-5-20 */
        	TimeGratherConfigUtil timeGratherConfigUtil = new TimeGratherConfigUtil();
        	timeGratherConfigUtil.saveTimeGratherConfigList(request, String.valueOf(vo.getId()), timeGratherConfigUtil.getObjectType("13"));
        	/* snow add end*/
        	
            //初始化采集指标
			try {
				NodeGatherIndicatorsUtil nodeGatherIndicatorsUtil = new NodeGatherIndicatorsUtil();
				nodeGatherIndicatorsUtil.addGatherIndicatorsForNode(vo.getId()+"", "middleware", "tomcat","1");
			} catch (RuntimeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			//初始化指标阀值
			try {
				AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
				alarmIndicatorsUtil.saveAlarmInicatorsThresholdForNode(String.valueOf(vo.getId()), "middleware", "tomcat");
			} catch (RuntimeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	
			//保存应用
			HostApplyManager.save(vo);
        }catch(Exception e){
        	e.printStackTrace();
        }finally{
        	dao.close();
        }
        return "/tomcat.do?action=list";
    }
	
	public String delete()
	{
		String id = getParaValue("radio"); 
		TomcatDao dao = new TomcatDao();
		try {
			Node node = PollingEngine.getInstance().getTomcatByID(Integer.parseInt(id));
			//删除应用
			HostApplyDao hostApplyDao = null;
			try{
				hostApplyDao = new HostApplyDao();
				hostApplyDao.delete(" where ipaddress = '"+node.getIpAddress()+"' and subtype = 'tomcat' and nodeid = '"+id+"'");
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if(hostApplyDao != null){
					hostApplyDao.close();
				}
			}
			
			PollingEngine.getInstance().deleteTomcatByID(Integer.parseInt(id));
			dao.delete(id);		
			TimeShareConfigUtil timeShareConfigUtil = new TimeShareConfigUtil();
			timeShareConfigUtil.deleteTimeShareConfig(id, timeShareConfigUtil.getObjectType("13"));
			/* snow add at 2010-5-20*/
			TimeGratherConfigUtil tg = new TimeGratherConfigUtil();
			tg.deleteTimeGratherConfig(id, tg.getObjectType("13"));
			/* snow add end */
			
			//删除该数据库的采集指标
			NodeGatherIndicatorsDao gatherdao = new NodeGatherIndicatorsDao();
			try {
				gatherdao.deleteByNodeIdAndTypeAndSubtype(id, "middleware", "tomcat");
			} catch (RuntimeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally{
				gatherdao.close();
			}
			//删除该数据库的告警阀值
			AlarmIndicatorsNodeDao indidao = new AlarmIndicatorsNodeDao();
			try {
				indidao.deleteByNodeId(id, "middleware", "tomcat");
			} catch (RuntimeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally{
				indidao.close();
			}
			//删除Tomcat在临时表里中存储的数据
	        String[] nmsTempDataTables = {"nms_tomcat_temp"};
	        String[] ids = new String[]{id};
	        CreateTableManager createTableManager = new CreateTableManager();
	        createTableManager.clearNmsTempDatas(nmsTempDataTables, ids);
	        
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}
		//更新业务视图
		NodeDependDao nodedependao = new NodeDependDao();
		List list = nodedependao.findByNode("tom"+id);
		if(list!=null&&list.size()>0){
			for(int j = 0; j < list.size(); j++){
				NodeDepend vo = (NodeDepend)list.get(j);
				if(vo!=null){
					LineDao lineDao = new LineDao();
	    			lineDao.deleteByidXml("tom"+id, vo.getXmlfile());
	    			NodeDependDao nodeDependDao = new NodeDependDao();
	    			if(nodeDependDao.isNodeExist("tom"+id, vo.getXmlfile())){
	            		nodeDependDao.deleteByIdXml("tom"+id, vo.getXmlfile());
	            	} else {
	            		nodeDependDao.close();
	            	}
	    			
	    			//yangjun
	    			User user = (User)session.getAttribute(SessionConstant.CURRENT_USER);
	    			ManageXmlDao mXmlDao =new ManageXmlDao();
	    			List xmlList = new ArrayList();
	    			try{
	    				xmlList = mXmlDao.loadByPerAll(user.getBusinessids());
	    			}catch(Exception e){
	    				e.printStackTrace();
	    			}finally{
	    				mXmlDao.close();
	    			}
	    			try{
	    				ChartXml chartxml;
	    			    chartxml = new ChartXml("tree");
	    			    chartxml.addViewTree(xmlList);
	    		    }catch(Exception e){
	    			    e.printStackTrace();   	
	    		    }
	                
	                ManageXmlDao subMapDao = new ManageXmlDao();
	    			ManageXml manageXml = (ManageXml) subMapDao.findByXml(vo.getXmlfile());
	    			if(manageXml!=null){
	    				NodeDependDao nodeDepenDao = new NodeDependDao();
	    				try{
	    				    List lists = nodeDepenDao.findByXml(vo.getXmlfile());
	    				    ChartXml chartxml;
	    					chartxml = new ChartXml("NetworkMonitor","/"+vo.getXmlfile().replace("jsp", "xml"));
	    					chartxml.addBussinessXML(manageXml.getTopoName(),lists);
	    					ChartXml chartxmlList;
	    					chartxmlList = new ChartXml("NetworkMonitor","/"+vo.getXmlfile().replace("jsp", "xml").replace("businessmap", "list"));
	    					chartxmlList.addListXML(manageXml.getTopoName(),lists);
	    				}catch(Exception e){
	    				    e.printStackTrace();   	
	    				}finally{
	    					nodeDepenDao.close();
	                    }
	    			}
				}
			}
		}
        return "/tomcat.do?action=list";
	}
	
	/**
	 * @author nielin add for time-sharing at 2010-01-05
	 * @return
	 */
	private String ready_edit(){
		String jsp = "/application/tomcat/edit.jsp";
		List timeShareConfigList = new ArrayList();
		TomcatDao dao = new TomcatDao();
		try{
			setTarget(jsp);
			jsp = readyEdit(dao);
			TimeShareConfigUtil timeShareConfigUtil = new TimeShareConfigUtil(); 
			timeShareConfigList = timeShareConfigUtil.getTimeShareConfigList(getParaValue("id"), timeShareConfigUtil.getObjectType("13"));
		    /* 获得设备的采集时间 snow add at 2010-05-20 */
			//提供供应商信息
			SupperDao supperdao = new SupperDao();
	    	List<Supper> allSupper = supperdao.loadAll();
	    	request.setAttribute("allSupper", allSupper);
	    	//提供已设置的采集时间信息
	    	TimeGratherConfigUtil tg  = new TimeGratherConfigUtil();
	    	List<TimeGratherConfig> timeGratherConfigList = tg.getTimeGratherConfig(getParaValue("id"), tg.getObjectType("13"));
	    	for (TimeGratherConfig timeGratherConfig : timeGratherConfigList) {
	    		timeGratherConfig.setHourAndMin();
			}
	    	request.setAttribute("timeGratherConfigList", timeGratherConfigList);  
	    	/* snow end */
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			dao.close();
		}
		request.setAttribute("timeShareConfigList", timeShareConfigList);
 	    return jsp;
	}
	
	private String update()
    {    	   
		Tomcat vo = new Tomcat();
    	vo.setId(getParaIntValue("id"));
    	TomcatDao _dao = new TomcatDao();
    	Tomcat pvo = null;
    	try{
    		pvo = (Tomcat)_dao.findByID(vo.getId()+"");
    	}catch(Exception e){
    		e.printStackTrace();
    	}finally{
    		_dao.close();
    	}
    	vo.setUser(getParaValue("user"));
    	vo.setPassword(getParaValue("password"));        
        vo.setAlias(getParaValue("alias"));
        vo.setIpAddress(getParaValue("ip_address"));
        vo.setPort(getParaValue("port"));
        vo.setSendmobiles(getParaValue("sendmobiles"));
		vo.setMonflag(getParaIntValue("monflag"));
		vo.setSendemail(getParaValue("sendemail"));
		vo.setSendphone(getParaValue("sendphone"));
		vo.setSupperid(getParaIntValue("supperid"));//snow add 2010-5-20
		vo.setVersion(pvo.getVersion());
		vo.setJvmversion(pvo.getJvmversion());
		vo.setJvmvender(pvo.getJvmvender());
		vo.setOs(pvo.getOs());
		vo.setOsversion(pvo.getOsversion());
		
        vo.setBid(getParaValue("bid"));
        
        if(PollingEngine.getInstance().getTomcatByID(vo.getId())!=null)
        {        
           com.afunms.polling.node.Tomcat tomcat = (com.afunms.polling.node.Tomcat)PollingEngine.getInstance().getTomcatByID(vo.getId());
		   tomcat.setUser(vo.getUser());
           tomcat.setPassword(vo.getPassword());
           tomcat.setPort(vo.getPort());
           tomcat.setIpAddress(vo.getIpAddress());
		   tomcat.setAlias(vo.getAlias());
		   tomcat.setSendemail(vo.getSendemail());
		   tomcat.setSendmobiles(vo.getSendmobiles());
		   tomcat.setSendphone(vo.getSendphone());
		   tomcat.setBid(vo.getBid());
		   tomcat.setMonflag(vo.getMonflag());
		   tomcat.setVersion(vo.getVersion());
		   tomcat.setJvmversion(vo.getJvmversion());
		   tomcat.setJvmvender(vo.getJvmvender());
		   tomcat.setOs(vo.getOs());
		   tomcat.setOsversion(vo.getOsversion());
		   //SysLogger.info(tomcat.getId()+"===="+tomcat.getPort()+"====tomcat");
        }
        
        TomcatDao dao = new TomcatDao();
        try{
        if(dao.update(vo)){	  
        	TimeShareConfigUtil timeShareConfigUtil = new TimeShareConfigUtil();  // nielin add for time-sharing at 2010-01-05
        	timeShareConfigUtil.saveTimeShareConfigList(request, String.valueOf(vo.getId()), timeShareConfigUtil.getObjectType("13"));
        	/* 增加采集时间设置 snow add at 2010-5-20 */
        	TimeGratherConfigUtil timeGratherConfigUtil = new TimeGratherConfigUtil();
        	timeGratherConfigUtil.saveTimeGratherConfigList(request, String.valueOf(vo.getId()), timeGratherConfigUtil.getObjectType("13"));
        	/* snow add end*/
           return "/tomcat.do?action=list";
        }else{
           return null;	
        }
        }catch(Exception e){
        	e.printStackTrace();
        	return null;
        }finally{
        	dao.close();
        }
    }
	private String syncconfig()
    {    	   
    	TomcatDao _dao = new TomcatDao();
    	Tomcat pvo = null;
    	try{
    		pvo = (Tomcat)_dao.findByID(getParaValue("id"));
    	}catch(Exception e){
    		e.printStackTrace();
    	}finally{
    		_dao.close();
    	}
    	String runmodel = PollingEngine.getCollectwebflag(); 
    	Hashtable hash_data = null;
    	 if("0".equals(runmodel)){
    	   		//采集与访问是集成模式
    		 Hashtable tomcatvalues = ShareData.getTomcatdata();
    	     if(tomcatvalues != null && tomcatvalues.size()>0){
    	    	 hash_data = (Hashtable)tomcatvalues.get(pvo.getIpAddress());
    	     }
    	 }else{
    		 //采集与访问分离模式
    		 TomcatInfoService tomcatInfoService = new TomcatInfoService();
    	     hash_data = tomcatInfoService.getTomcatDataHashtable(pvo.getId()+"");
    	 }
//    	Hashtable tomcatvalues = ShareData.getTomcatdata();
//    	if(tomcatvalues != null && tomcatvalues.size()>0){
//    		Hashtable hash_data = (Hashtable)tomcatvalues.get(pvo.getIpAddress());
    	
    		if(hash_data != null && hash_data.size()>0){
    			String server=hash_data.get("server").toString();    
    			//String jvm=hash_data.get("jvm").toString();
    			if(server != null){
					String [] temserver=server.split(",");
					String tomcat_version=temserver[0];
					String jvm_version=temserver[1];
					String jvm_vender=temserver[2];
					String os_name=temserver[3];
					String os_version=temserver[4];	
			    	pvo.setVersion(tomcat_version);
			    	pvo.setJvmversion(jvm_version);
			    	pvo.setJvmvender(jvm_vender);
			    	pvo.setOs(os_name);
			    	pvo.setOsversion(os_version);
					
			        
			        if(PollingEngine.getInstance().getTomcatByID(pvo.getId())!=null)
			        {        
			           com.afunms.polling.node.Tomcat tomcat = (com.afunms.polling.node.Tomcat)PollingEngine.getInstance().getTomcatByID(pvo.getId());
					   tomcat.setVersion(pvo.getVersion());
					   tomcat.setJvmversion(pvo.getJvmversion());
					   tomcat.setJvmvender(pvo.getJvmvender());
					   tomcat.setOs(pvo.getOs());
					   tomcat.setOsversion(pvo.getOsversion());
			        }
			        
			        TomcatDao dao = new TomcatDao();
			        try{
			        if(dao.update(pvo)){	  
			           //return "/tomcat.do?action=list";
			        	return "/tomcat.do?action=tomcat_jvm&id="+pvo.getId();
			        }else{
			           return null;	
			        }
			        }catch(Exception e){
			        	e.printStackTrace();
			        	return null;
			        }finally{
			        	dao.close();
			        }
				}
    		}
//    	}
    	setTarget("/application/tomcat/tomcat_detail2.jsp");
    	return "/tomcat.do?action=tomcat_jvm&id="+pvo.getId();

    }
	
    private String report()
	{    	
    	String queryDate = getParaValue("day");
    	if(queryDate==null)  
    		queryDate = SysUtil.getCurrentDate();
    	int nodeId = getParaIntValue("node_id");
    	
    	TomcatJvmReport tjr = new TomcatJvmReport();
    	tjr.setNodeId(nodeId);
    	tjr.setQueryDate(queryDate);
    	JspReport report = new JspReport(tjr);
	    report.createReport();
	    
		request.setAttribute("report",report);
		request.setAttribute("node_id",new Integer(nodeId));
		request.setAttribute("day",queryDate);
		return "/detail/tomcat_jvm.jsp";
	}
    public Hashtable detail(com.afunms.polling.node.Tomcat node){
    	Hashtable data_ht =new Hashtable();
    	try {  
        		ServerStream serverstream = new ServerStream();
        		Hashtable returnVal = new Hashtable();
        		String ip = "";
					try {
        					com.afunms.polling.node.Tomcat tc = new com.afunms.polling.node.Tomcat();
        						BeanUtils.copyProperties(tc, node);
        						ip = tc.getIpAddress();
        						StringBuffer tmp = new StringBuffer();
        						tmp.append(tc.getIpAddress());
        						tmp.append(",");
        						tmp.append(tc.getPort());
        						tmp.append(",");
        						tmp.append(tc.getUser());
        						tmp.append(" , ");
        						tmp.append(tc.getPassword());
        						returnVal.put(String.valueOf(0), tmp.toString());
        					} catch (Exception e) {
        					e.printStackTrace();
        				}
        				String liststr = serverstream.validServer(returnVal);
        				SysLogger.info(liststr);
        				if ("".equals(liststr)) 
        					return null;
        				String[] pos_s = liststr.split(",");
        				for (int list_i = 0; list_i < pos_s.length-1; list_i++) {
        					String tmps = returnVal.get(pos_s[list_i]).toString();
        					String[] serverinfo = tmps.split(",");
        					serverstream.foundData(serverinfo[0],serverinfo[1],serverinfo[2],serverinfo[3]);
        					
        					data_ht = serverstream.data_ht;
        					
        					Hashtable sendeddata = ShareData.getSendeddata();
        					
        					try {
        						
        						
        						if (data_ht==null){
        							Pingcollectdata hostdata=null;
        							hostdata=new Pingcollectdata();
        							hostdata.setIpaddress(ip);
        							Calendar date=Calendar.getInstance();
        							hostdata.setCollecttime(date);
        							hostdata.setCategory("TomcatPing");
        							hostdata.setEntity("Utilization");
        							hostdata.setSubentity("ConnectUtilization");
        							hostdata.setRestype("dynamic");
        							hostdata.setUnit("%");
        							hostdata.setThevalue("0");	
        							TomcatDao tomcatdao=new TomcatDao();
        							try{
        								tomcatdao.createHostData(hostdata);						
        								
        							}catch(Exception e){
        								e.printStackTrace();
        							}finally{
        								tomcatdao.close();
        							}
        						}else{
        							Pingcollectdata hostdata=null;
        							hostdata=new Pingcollectdata();
        							hostdata.setIpaddress(ip);
        							Calendar date=Calendar.getInstance();
        							hostdata.setCollecttime(date);
        							hostdata.setCategory("TomcatPing");
        							hostdata.setEntity("Utilization");
        							hostdata.setSubentity("ConnectUtilization");
        							hostdata.setRestype("dynamic");
        							hostdata.setUnit("%");
        							hostdata.setThevalue("100");
        							TomcatDao tomcatdao=new TomcatDao();
        							try{
        								tomcatdao.createHostData(hostdata);
        								if(sendeddata.containsKey("tomcat"+":"+ip))
        									sendeddata.remove("tomcat"+":"+ip);
        							}catch(Exception e){
        								e.printStackTrace();
        							}finally{
        								tomcatdao.close();
        							}
        						}	
        						if(data_ht!=null){
            						String jvm =  data_ht.get("jvm").toString();
        							int jvm_memoryuiltillize=0;
        							String jvm_utilization=null;
        							String [] temjvm=jvm.split(",");
        							double freememory=Double.parseDouble(temjvm[0].trim());
        							double totalmemory=(double)Double.parseDouble(temjvm[1].trim());
        							double maxmemory=(double)Double.parseDouble(temjvm[2].trim());
        							
        							jvm_memoryuiltillize=(int)Math.rint((totalmemory-freememory)*100/totalmemory);
        							jvm_utilization=String.valueOf(jvm_memoryuiltillize);
        							
        							Pingcollectdata hostdata=null;
        							hostdata=new Pingcollectdata();
        							hostdata.setIpaddress(ip);
        							Calendar date=Calendar.getInstance();
        							hostdata.setCollecttime(date);
        							hostdata.setCategory("tomcat_jvm");
        							hostdata.setEntity("Utilization");
        							hostdata.setSubentity("jvm_utilization");
        							hostdata.setRestype("dynamic");
        							hostdata.setUnit("%");
        							hostdata.setThevalue(jvm_utilization);
        							TomcatDao tomcatdao=new TomcatDao();
        							try{
        								tomcatdao.createHostData(hostdata);
        								if(sendeddata.containsKey("tomcat"+":"+ip))
        									sendeddata.remove("tomcat"+":"+ip);
        							}catch(Exception e){
        								e.printStackTrace();
        							}finally{
        								tomcatdao.close();
        							}
        						}
        						
        					}catch(Exception e){
        						e.printStackTrace();
        					}finally{
        						//tomcatdao.close();
        					}
        				}
        		} catch (Exception e) {
        			e.printStackTrace();
        		}finally{
            }
        	return data_ht;
   }
    
    public double tomcatping(int id)
    {    	   
    	String strid = String.valueOf(id);
		Tomcat vo = new Tomcat();
		TomcatDao dao = new TomcatDao();
		double avgpingcon=0;
		Hashtable sysValue = new Hashtable();
		Hashtable imgurlhash=new Hashtable();
		Hashtable maxhash = new Hashtable();
		String pingconavg ="0";
		try{
			vo = (Tomcat)dao.findByID(strid);

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String time1 = sdf.format(new Date());
			String newip=SysUtil.doip(vo.getIpAddress());						
		
			String starttime1 = time1 + " 00:00:00";
			String totime1 = time1 + " 23:59:59";
			
			Hashtable ConnectUtilizationhash = new Hashtable();
			I_HostCollectData hostmanager=new HostCollectDataManager();
			try{
				ConnectUtilizationhash = getCategory(vo.getIpAddress(),"TomcatPing","ConnectUtilization",starttime1,totime1,"");
			}catch(Exception ex){
				ex.printStackTrace();
			}
			if (ConnectUtilizationhash.get("avgpingcon")!=null)
				pingconavg = (String)ConnectUtilizationhash.get("avgpingcon");
			if(pingconavg != null){
				pingconavg = pingconavg.replace("%", "");
			}
			  avgpingcon = new Double(pingconavg+"").doubleValue();
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			dao.close();
		}
		return avgpingcon;
    }
 
	public Hashtable getCollecttime(String ip) throws Exception{
    	
    	String collecttime = "";
    	String nexttime = "";
    	Hashtable pollingtime_ht= new Hashtable();
    	DBManager dbmanager = new DBManager();
    	ResultSet rs = null;
    	try{
//    	String ip1 ="",ip2="",ip3="",ip4="";	
//		String tempStr = "";
//		String allipstr = "";
//		if (ip.indexOf(".")>0){
//			ip1=ip.substring(0,ip.indexOf("."));
//			ip4=ip.substring(ip.lastIndexOf(".")+1,ip.length());			
//			tempStr = ip.substring(ip.indexOf(".")+1,ip.lastIndexOf("."));
//		}
//		ip2=tempStr.substring(0,tempStr.indexOf("."));
//		ip3=tempStr.substring(tempStr.indexOf(".")+1,tempStr.length());
//		allipstr=ip1+ip2+ip3+ip4;
    	String allipstr = SysUtil.doip(ip);
		
		String sql = "";
		StringBuffer sb = new StringBuffer();
		sb.append(" select max(DATE_FORMAT(h.collecttime,'%Y-%m-%d %H:%i:%s')) as collecttime from tomcatping"+allipstr+" h  ");
		sql = sb.toString();
		SysLogger.info(sql);
		rs = dbmanager.executeQuery(sql);
		if(rs.next()){
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			collecttime =rs.getString("collecttime");
			if(collecttime != null && collecttime.trim().length()>0){
				Date date=format.parse(collecttime);
				int mins=date.getMinutes()+5;
				date.setMinutes(mins);
				nexttime=format.format(date);
			}else{
				collecttime = "";
				nexttime = "";
			}
			pollingtime_ht.put("lasttime", collecttime);
			pollingtime_ht.put("nexttime", nexttime);
		}	
    	}catch(Exception e){
    		e.printStackTrace();
    	}finally{
    		dbmanager.close();
    	}
		
		return  pollingtime_ht;
    }
    public Hashtable getCategory(
			String ip,
			String category,
			String subentity,
			String starttime,
			String endtime,
			String time)
			throws Exception {
			Hashtable hash = new Hashtable();
		 	//Connection con = null;
		 	//PreparedStatement stmt = null;
		 	DBManager dbmanager = new DBManager();
		 	ResultSet rs = null;
			try{
				//con=DataGate.getCon();
				if (!starttime.equals("") && !endtime.equals("")) {
					//con=DataGate.getCon();
//					String ip1 ="",ip2="",ip3="",ip4="";	
//					String tempStr = "";
//					String allipstr = "";
//					if (ip.indexOf(".")>0){
//						ip1=ip.substring(0,ip.indexOf("."));
//						ip4=ip.substring(ip.lastIndexOf(".")+1,ip.length());			
//						tempStr = ip.substring(ip.indexOf(".")+1,ip.lastIndexOf("."));
//					}
//					ip2=tempStr.substring(0,tempStr.indexOf("."));
//					ip3=tempStr.substring(tempStr.indexOf(".")+1,tempStr.length());
//					allipstr=ip1+ip2+ip3+ip4;
					String allipstr = SysUtil.doip(ip);
					String sql = "";
					StringBuffer sb = new StringBuffer();
					 if (category.equals("TomcatPing")){
						sb.append(" select h.thevalue,DATE_FORMAT(h.collecttime,'%Y-%m-%d %H:%i:%s') as collecttime,h.unit from tomcatping"+time+allipstr+" h where ");
					 }
					 if (category.equals("tomcat_jvm")){
							sb.append(" select h.thevalue,DATE_FORMAT(h.collecttime,'%Y-%m-%d %H:%i:%s') as collecttime,h.unit from tomcat_jvm"+allipstr+" h where ");
					}
					sb.append(" h.category='");
					sb.append(category);
					sb.append("' and h.subentity='");
					sb.append(subentity);
					sb.append("' and h.collecttime >= '");
					sb.append(starttime);
					sb.append("' and h.collecttime <= '");
					sb.append(endtime);
					sb.append("' order by h.collecttime");
					sql = sb.toString();
					SysLogger.info(sql);
					
					rs = dbmanager.executeQuery(sql);
					List list1 =new ArrayList();
					String unit = "";
					//String max = "";
					double tempfloat=0;
					double pingcon = 0;
					double tomcat_jvm_con = 0;
					int downnum = 0;
					int i=0;
			        while (rs.next()) {
			        	i=i+1;
			        	Vector v =new Vector();		        	
			            String thevalue=rs.getString("thevalue");
			            String collecttime = rs.getString("collecttime");		            
			            v.add(0,emitStr(thevalue));
			            v.add(1,collecttime);
			            v.add(2,rs.getString("unit"));
			            if (category.equals("TomcatPing")&&subentity.equalsIgnoreCase("ConnectUtilization")){
			            	pingcon=pingcon+getfloat(thevalue);
			            	if(thevalue.equals("0")){
			            		downnum = downnum + 1;
			            	}	
			            }
//			            if (category.equals("tomcat_jvm")&&subentity.equalsIgnoreCase("ConnectUtilization")){
//			            	pingcon=pingcon+getfloat(thevalue);
//			            	if(thevalue.equals("0")){
//			            		downnum = downnum + 1;
//			            	}	
//			            }
			            if (subentity.equalsIgnoreCase("ConnectUtilization")) {
			            	if (i==1)tempfloat = getfloat(thevalue);
			            	if (tempfloat > getfloat(thevalue))tempfloat = getfloat(thevalue);
			            }else if (category.equalsIgnoreCase("tomcat_jvm")){
			            	tomcat_jvm_con=tomcat_jvm_con+getfloat(thevalue);
			            	if (tempfloat < getfloat(thevalue))tempfloat = getfloat(thevalue);
			            }else{
			            	if (tempfloat < getfloat(thevalue))tempfloat = getfloat(thevalue);
			            }	
			            list1.add(v);	
			    }	
			        rs.close();
			        //stmt.close();
			        
					Integer size = new Integer(0);
					hash.put("list", list1);
					if (list1.size() != 0) {
						size = new Integer(list1.size());
						if (list1.get(0) != null) {
							Vector tempV = (Vector)list1.get(0);
							unit = (String)tempV.get(2);
						}
					}
					if (category.equals("TomcatPing")&&subentity.equalsIgnoreCase("ConnectUtilization")){
						if (list1 !=null && list1.size()>0){
							hash.put("avgpingcon", CEIString.round(pingcon/list1.size(),2)+unit);						
							hash.put("pingmax", tempfloat+"");
							hash.put("downnum", downnum+"");
						}else{ 
							hash.put("avgpingcon", "0.0%");	
							hash.put("pingmax", "0.0%");
							hash.put("downnum", "0");
						}
					}
					if (category.equals("tomcat_jvm")){
						if (list1 !=null && list1.size()>0){
							hash.put("avg_tomcat_jvm",CEIString.round(tomcat_jvm_con/list1.size(), 2)+unit);				
						}else{ 
							hash.put("avg_tomcat_jvm", "0.0%");							
						}
					}
					hash.put("size", size);			
					hash.put("max", CEIString.round(tempfloat,2) + unit);
					hash.put("unit", unit);
			        }
				} catch (Exception e) {
				e.printStackTrace();
			}finally{
				if (rs != null)
				rs.close();
				dbmanager.close();
			}
			
			return hash;
		}
	private String emitStr(String num) {
				if (num != null) {
					if (num.indexOf(".")>=0){				
						if (num.substring(num.indexOf(".")+1).length()>7){
							String tempStr = num.substring(num.indexOf(".")+1);
							num = num.substring(0,num.indexOf(".")+1)+tempStr.substring(0,7);					
						}
					}
				}
				return num;
	}
	private double getfloat(String num) {
		double snum = 0.0;
		if (num != null) {
			if (num.indexOf(".")>=0){				
				if (num.substring(num.indexOf(".")+1).length()>7){
					String tempStr = num.substring(num.indexOf(".")+1);
					num = num.substring(0,num.indexOf(".")+1)+tempStr.substring(0,7);					
				}
			}
			int inum = (int) (Float.parseFloat(num) * 100);
			snum = new Double(inum/100.0).doubleValue();
		}
		return snum;
	}
	 public String tomcat_jvm(){
		 
           String id= request.getParameter("id");
           String flag= request.getParameter("flag");
           System.out.println("##############"+id);
           System.out.println("##############"+flag);
           request.setAttribute("flag", flag);
           
		   com.afunms.polling.node.Tomcat tomcat = (com.afunms.polling.node.Tomcat)PollingEngine.getInstance().getTomcatByID(Integer.parseInt(id)); 
		   String ip=tomcat.getIpAddress();
		   	Hashtable imgurlhash=new Hashtable();
			try {
				String newip=doip(ip);
				String[] time = {"",""};
				getTime(request,time);
				String starttime = time[0];
				String endtime = time[1];	
				String time1 = request.getParameter("begindate");
				if(time1 == null){
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
					time1 = sdf.format(new Date());
				}
				String starttime1 = time1 + " 00:00:00";
				String totime1 = time1 + " 23:59:59";						

				try {
					Hashtable hash1 = getCategory(ip,"TomcatPing","ConnectUtilization",starttime1,totime1,"");						
					p_draw_line(hash1,"连通率",newip+"TomcatPing",740,150);
					Hashtable hash = getCategory(ip,"tomcat_jvm","jvm_utilization",starttime1,totime1,"");						
					p_draw_line(hash,"JVM内存利用率",newip+"tomcat_jvm",740,150);
					if(hash1!=null)
					request.setAttribute("pingcon", hash1);
					if(hash!=null)
						request.setAttribute("avgjvm", hash);	
				} catch(Exception ex) {
					ex.printStackTrace();
				}
				//imgurlhash
				imgurlhash.put("tomcat_jvm","resource/image/jfreechart/"+newip+"tomcat_jvm"+".png");
				imgurlhash.put("TomcatPing","resource/image/jfreechart/"+newip+"TomcatPing"+".png");
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			request.setAttribute("imgurlhash",imgurlhash);
			request.setAttribute("id", id);
		   return  "/application/tomcat/tomcat_detail2.jsp";
	   }
	 private String doip(String ip){
//		  String newip="";
//		  for(int i=0;i<3;i++){
//			int p=ip.indexOf(".");
//			newip+=ip.substring(0,p);
//			ip=ip.substring(p+1);
//		  }
//		 newip+=ip;
		 String allipstr = SysUtil.doip(ip);
		 //System.out.println("newip="+newip);
		 return allipstr;
	}
	 private void getTime(HttpServletRequest request,String[] time){		
		  Calendar current = new GregorianCalendar();
		  String key = getParaValue("beginhour");
		  if(getParaValue("beginhour") == null){
			  Integer hour = new Integer(current.get(Calendar.HOUR_OF_DAY));
			  request.setAttribute("beginhour", new Integer(hour.intValue()-1));
			  request.setAttribute("endhour", hour);
			  //mForm.setBeginhour(new Integer(hour.intValue()-1));
			  //mForm.setEndhour(hour);
		  }
		  if(getParaValue("begindate") == null){
			  current.set(Calendar.MINUTE,59);
			  current.set(Calendar.SECOND,59);
			  time[1] = datemanager.getDateDetail(current);
			  current.add(Calendar.HOUR_OF_DAY,-1);
			  current.set(Calendar.MINUTE,0);
			  current.set(Calendar.SECOND,0);
			  time[0] = datemanager.getDateDetail(current);

			  java.text.SimpleDateFormat timeFormatter = new java.text.SimpleDateFormat("yyyy-M-d");
			  String begindate = "";
			  begindate = timeFormatter.format(new java.util.Date());
			  request.setAttribute("begindate", begindate);
			  request.setAttribute("enddate", begindate);
			  //mForm.setBegindate(begindate);
			  //mForm.setEnddate(begindate);
		 }
		 else{
			  String temp = getParaValue("begindate");
			  time[0] = temp+" "+getParaValue("beginhour")+":00:00";
			  temp = getParaValue("enddate");
			  time[1] = temp+" "+getParaValue("endhour")+":59:59";
		 }
		  if(getParaValue("startdate") == null){
			  current.set(Calendar.MINUTE,59);
			  current.set(Calendar.SECOND,59);
			  time[1] = datemanager.getDateDetail(current);
			  current.add(Calendar.HOUR_OF_DAY,-1);
			  current.set(Calendar.MINUTE,0);
			  current.set(Calendar.SECOND,0);
			  time[0] = datemanager.getDateDetail(current);

			  java.text.SimpleDateFormat timeFormatter = new java.text.SimpleDateFormat("yyyy-M-d");
			  String startdate = "";
			  startdate = timeFormatter.format(new java.util.Date());
			  request.setAttribute("startdate", startdate);
			  request.setAttribute("todate", startdate);
			  //mForm.setStartdate(startdate);
			  //mForm.setTodate(startdate);
		 }
		 else{
			  String temp = getParaValue("startdate");
			  time[0] = temp+" "+getParaValue("beginhour")+":00:00";
			  temp = getParaValue("todate");
			  time[1] = temp+" "+getParaValue("endhour")+":59:59";
		 }
		  
	}
	 private void p_draw_line(Hashtable hash,String title1,String title2,int w,int h){
	    	List list = (List)hash.get("list");
	    	try{
	    	if(list==null || list.size()==0){
	    		draw_blank(title1,title2,w,h);
	    	}
	    	else{
	    	String unit = (String)hash.get("unit");
	    	if (unit == null)unit="%";
	    	ChartGraph cg = new ChartGraph();
	    	
	    	TimeSeries ss = new TimeSeries(title1,Minute.class);
	    	TimeSeries[] s = {ss};
	    	for(int j=0; j<list.size(); j++){
	    			Vector v = (Vector)list.get(j);
	    			Double	d=new Double((String)v.get(0));			
	    			String dt = (String)v.get(1);
	    			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    			Date time1 = sdf.parse(dt);				
	    			Calendar temp = Calendar.getInstance();
	    			temp.setTime(time1);
	    			Minute minute=new Minute(temp.get(Calendar.MINUTE),temp.get(Calendar.HOUR_OF_DAY),temp.get(Calendar.DAY_OF_MONTH),temp.get(Calendar.MONTH)+1,temp.get(Calendar.YEAR));
	    			ss.addOrUpdate(minute,d);
	    	}
	    	cg.timewave(s,"x(时间)","y("+unit+")",title1,title2,w,h);
	    	
	    	
	    	
	    	
	    	}
	    	hash = null;
	    	}
	    	catch(Exception e){e.printStackTrace();}
	    }
	 private void draw_blank(String title1,String title2,int w,int h){
	    	ChartGraph cg = new ChartGraph();
	    	TimeSeries ss = new TimeSeries(title1,Minute.class);
	    	TimeSeries[] s = {ss};
	    	try{
	    		Calendar temp = Calendar.getInstance();
	    		Minute minute=new Minute(temp.get(Calendar.MINUTE),temp.get(Calendar.HOUR_OF_DAY),temp.get(Calendar.DAY_OF_MONTH),temp.get(Calendar.MONTH)+1,temp.get(Calendar.YEAR));
	    		ss.addOrUpdate(minute,null);
	    		cg.timewave(s,"x(时间)","y",title1,title2,w,h);
	    	}
	    	catch(Exception e){e.printStackTrace();}
	    }
	 
	 public Hashtable getCategory(
				String ip,
				String category,
				String subentity,
				String starttime,
				String endtime)
				throws Exception {
				Hashtable hash = new Hashtable();
			 	//Connection con = null;
			 	//PreparedStatement stmt = null;
			 	DBManager dbmanager = new DBManager();
			 	ResultSet rs = null;
				try{
					//con=DataGate.getCon();
					if (!starttime.equals("") && !endtime.equals("")) {
						//con=DataGate.getCon();
//						String ip1 ="",ip2="",ip3="",ip4="";	
//						String tempStr = "";
//						String allipstr = "";
//						if (ip.indexOf(".")>0){
//							ip1=ip.substring(0,ip.indexOf("."));
//							ip4=ip.substring(ip.lastIndexOf(".")+1,ip.length());			
//							tempStr = ip.substring(ip.indexOf(".")+1,ip.lastIndexOf("."));
//						}
//						ip2=tempStr.substring(0,tempStr.indexOf("."));
//						ip3=tempStr.substring(tempStr.indexOf(".")+1,tempStr.length());
//						allipstr=ip1+ip2+ip3+ip4;
						String allipstr = SysUtil.doip(ip);
						
						String sql = "";
						StringBuffer sb = new StringBuffer();
						 if (category.equals("TomcatPing")){
							sb.append(" select h.thevalue,DATE_FORMAT(h.collecttime,'%Y-%m-%d %H:%i:%s') as collecttime,h.unit from tomcatping"+allipstr+" h where ");
						 }
						 if (category.equals("tomcat_jvm")){
								sb.append(" select h.thevalue,DATE_FORMAT(h.collecttime,'%Y-%m-%d %H:%i:%s') as collecttime,h.unit from tomcat_jvm"+allipstr+" h where ");
						}
						sb.append(" h.category='");
						sb.append(category);
						sb.append("' and h.subentity='");
						sb.append(subentity);
						sb.append("' and h.collecttime >= '");
						sb.append(starttime);
						sb.append("' and h.collecttime <= '");
						sb.append(endtime);
						sb.append("' order by h.collecttime");
						sql = sb.toString();
						SysLogger.info(sql);
						
						rs = dbmanager.executeQuery(sql);
						List list1 =new ArrayList();
						String unit = "";
						String max = "";
						double tempfloat=0;
						double tempfloat1=0;
						double pingcon = 0;
						
						double tomcat_jvm_con = 0;
						int downnum = 0;
						int i=0;
				        while (rs.next()) {
				        	i=i+1;
				        	Vector v =new Vector();		        	
				            String thevalue=rs.getString("thevalue");
				            String collecttime = rs.getString("collecttime");		            
				            v.add(0,emitStr(thevalue));
				            v.add(1,collecttime);
				            v.add(2,rs.getString("unit"));
				            if (category.equals("TomcatPing")&&subentity.equalsIgnoreCase("ConnectUtilization")){
				            	pingcon=pingcon+getfloat(thevalue);
				            	if(thevalue.equals("0")){
				            		downnum = downnum + 1;
				            	}	
				            }
			            if (category.equals("tomcat_jvm")&&subentity.equalsIgnoreCase("ConnectUtilization")){
				            	pingcon=pingcon+getfloat(thevalue);
			            	if(thevalue.equals("0")){
				            		downnum = downnum + 1;
			            	}	
			            }
				            if (subentity.equalsIgnoreCase("ConnectUtilization")) {
				            	if (i==1)tempfloat = getfloat(thevalue);
				            	if (tempfloat > getfloat(thevalue))tempfloat = getfloat(thevalue);
				            }else if (category.equalsIgnoreCase("tomcat_jvm")){
				            	tomcat_jvm_con=tomcat_jvm_con+getfloat(thevalue);
				            	if (i==1)tempfloat1 = getfloat(thevalue);
				            	
				            	if (tempfloat1 > getfloat(thevalue))tempfloat1 = getfloat(thevalue);
				            	
				            }else{
				            	if (tempfloat < getfloat(thevalue))tempfloat = getfloat(thevalue);
				            }	
				            list1.add(v);	
				    }	
				        rs.close();
				        //stmt.close();
						Integer size = new Integer(0);
						hash.put("list", list1);
						if (list1.size() != 0) {
							size = new Integer(list1.size());
							if (list1.get(0) != null) {
								Vector tempV = (Vector)list1.get(0);
								unit = (String)tempV.get(2);
							}
						}
						if (category.equals("TomcatPing")&&subentity.equalsIgnoreCase("ConnectUtilization")){
							if (list1 !=null && list1.size()>0){
								
								hash.put("avgpingcon", CEIString.round(pingcon/list1.size(),2)+unit);						
								hash.put("pingmax", tempfloat+"");
								hash.put("downnum", downnum+"");
							}else{ 
								hash.put("avgpingcon", "0.0%");	
								hash.put("pingmax", "0.0%");
								hash.put("downnum", "0");
							}
						}
						//zhushouzhi-------------------jvm
						if (category.equals("tomcat_jvm")){
							if (list1 !=null && list1.size()>0){
								//hash.put("jvm_now", tempfloat+"");
								//System.out.println(tempfloat1+"==============tempfloat1=======================");
								hash.put("avg_tomcat_jvm",CEIString.round(tomcat_jvm_con/list1.size(), 2)+unit);	
								hash.put("max_tomcat_jvm",CEIString.round(tempfloat1,2) + unit+"");
							}else{ 
								hash.put("avg_tomcat_jvm", "0.0%");
								hash.put("max_tomcat_jvm", "0.0%");	
							}
						}
						//zhushouzhi------------------------jvm
						hash.put("size", size);			
						hash.put("max", CEIString.round(tempfloat,2) + unit);
						hash.put("unit", unit);
				        }
					} catch (Exception e) {
					e.printStackTrace();
				}finally{
					if (rs != null)
					rs.close();
					dbmanager.close();
				}
				
				return hash;
			}
	 
	 private String sychronizeData()
	    {    
			
			int queryid = getParaIntValue("id");
			String dbpage = getParaValue("dbPage");
			NodeGatherIndicatorsDao indicatorsdao = new NodeGatherIndicatorsDao();
	    	List<NodeGatherIndicators> monitorItemList = new ArrayList<NodeGatherIndicators>(); 	
	    	Hashtable<String,Hashtable<String,NodeGatherIndicators>> urlHash = new Hashtable<String,Hashtable<String,NodeGatherIndicators>>();//存放需要监视的DB2指标  <dbid:Hashtable<name:NodeGatherIndicators>>

	    	try{
	    		//获取被启用的所有被监视指标
	    		monitorItemList = indicatorsdao.getByNodeId(queryid+"", 1,"middleware","tomcat");
	    	}catch(Exception e){
	    		e.printStackTrace();
	    	}finally{
	    		indicatorsdao.close();
	    	}
	    	if(monitorItemList == null)monitorItemList = new ArrayList<NodeGatherIndicators>();
	    	Hashtable gatherHash = new Hashtable();
	    	for(int i=0;i<monitorItemList.size();i++){
	    		NodeGatherIndicators nodeGatherIndicators = (NodeGatherIndicators)monitorItemList.get(i);
				gatherHash.put(nodeGatherIndicators.getName(), nodeGatherIndicators);
	    	}
			
	        try {                	
	        	TomcatDataCollector tomcatcollector = new TomcatDataCollector();
	        	tomcatcollector.collect_data(queryid+"", gatherHash);
	        }catch(Exception exc){
	        	
	        }
	        return "/tomcat.do?action=tomcat_jvm&id="+queryid;
	    }
	 
	 private String isOK()
	    {    
			
			int queryid = getParaIntValue("id");
			
			TomcatDao dao = new TomcatDao();
			Tomcat node = new Tomcat();
			
			boolean isOK = true;
			try {
				ServerStream serverstream = new ServerStream();
				Hashtable returnVal = new Hashtable();
				String ipaddress = "";
				
				try{
					node = (Tomcat)dao.findByID(queryid+"");
				}catch(Exception e){
				}finally{
					dao.close();
				}
				try {
					com.afunms.polling.node.Tomcat tc = new com.afunms.polling.node.Tomcat();
					BeanUtils.copyProperties(tc, node);
					ipaddress = tc.getIpAddress();
					com.afunms.polling.node.Tomcat tnode=(com.afunms.polling.node.Tomcat)PollingEngine.getInstance().getTomcatByIP(ipaddress);
					
					StringBuffer tmp = new StringBuffer();
					tmp.append(tc.getIpAddress());
					tmp.append(",");
					tmp.append(tc.getPort());
					tmp.append(",");
					tmp.append(tc.getUser());
					tmp.append(" , ");
					tmp.append(tc.getPassword());
					returnVal.put(String.valueOf(0), tmp.toString());	        					
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				String liststr = serverstream.validServer(returnVal);
				if ("".equals(liststr)) {
					isOK = false;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			request.setAttribute("isOK", isOK);
			request.setAttribute("name", node.getAlias());
			request.setAttribute("str", node.getIpAddress());
	        return "/tool/tomcatisok.jsp";
	    }
	 
	 private String alarm(){
		 	tomcat_jvm();
		 	Vector vector = new Vector();
			
			String ip="";
			String tmp ="";
			List list = new ArrayList();
			int status =99;
			int level1 = 99;
			String b_time ="";
			String t_time = "";
			
			try {
				
		    	tmp = request.getParameter("id");
		    	 com.afunms.polling.node.Tomcat tomcat = (com.afunms.polling.node.Tomcat)PollingEngine.getInstance().getTomcatByID(Integer.parseInt(tmp)); 
				    ip=tomcat.getIpAddress();
				    String newip=doip(ip);
		    	status = getParaIntValue("status");
		    	level1 = getParaIntValue("level1");
		    	if(status == -1)status=99;
		    	if(level1 == -1)level1=99;
		    	request.setAttribute("status", status);
		    	request.setAttribute("level1", level1);
		    	
		    	b_time = getParaValue("startdate");
				t_time = getParaValue("todate");
		    	
				if (b_time == null){
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
					b_time = sdf.format(new Date());
				}
				if (t_time == null){
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
					t_time = sdf.format(new Date());
				}
				String starttime1 = b_time + " 00:00:00";
				String totime1 = t_time + " 23:59:59";
		    	
			
				try{
					User vo = (User)session.getAttribute(SessionConstant.CURRENT_USER);      //用户姓名
					//SysLogger.info("user businessid===="+vo.getBusinessids());
					EventListDao dao = new EventListDao();
					
					list = dao.getQuery(starttime1,totime1,status+"",level1+"",
							vo.getBusinessids(),Integer.parseInt(tmp),"tomcat");
					
					//ConnectUtilizationhash = hostmanager.getCategory(host.getIpAddress(),"Ping","ConnectUtilization",starttime1,totime1);
				}catch(Exception ex){
					ex.printStackTrace();
				}
				try {
					Hashtable hash1 = getCategory(ip,"TomcatPing","ConnectUtilization",starttime1,totime1,"");						
					Hashtable hash = getCategory(ip,"tomcat_jvm","jvm_utilization",starttime1,totime1,"");						
					if(hash1!=null)
					request.setAttribute("pingcon", hash1);
					if(hash!=null)
						request.setAttribute("avgjvm", hash);	
				} catch(Exception ex) {
					ex.printStackTrace();
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			
			request.setAttribute("vector",vector);
			request.setAttribute("id", Integer.parseInt(tmp));
			request.setAttribute("list", list);
			request.setAttribute("startdate", b_time);
			request.setAttribute("todate", t_time);
			return "/application/tomcat/alarm.jsp";
		 
	 }

	public String event(){
		TomcatDao dao=new TomcatDao();
		
		return null;
	}
	 
	public String execute(String action) 
	{	
        if(action.equals("list"))
            return list();  
        if(action.equals("ready_add"))
        	return ready_add();
        if(action.equals("add"))
        	return add();
        if(action.equals("delete"))
            return delete();
        if(action.equals("ready_edit"))
        	return ready_edit();
        if(action.equals("update"))
            return update();
        if(action.equals("report"))
            return report();
        if(action.equals("tomcat_jvm")){
            return tomcat_jvm();
        }
        if(action.equals("syncconfig"))
            return syncconfig();
        if(action.equals("sychronizeData"))
            return sychronizeData();
        if(action.equals("isOK"))
            return isOK();
        if(action.equals("alarm")){
        	return alarm();
        }
		setErrorCode(ErrorMessage.ACTION_NO_FOUND);
		return null;
	}
}