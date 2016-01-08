package com.afunms.comprehensivereport.util;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.afunms.common.util.DBManager;
import com.afunms.common.util.SysLogger;
import com.afunms.polling.impl.HomeCollectDataManager;
import com.afunms.polling.om.Diskcollectdata;
import com.afunms.topology.dao.HostNodeDao;
import com.afunms.topology.model.HostNode;

public class CompreReportHelper {

	public HashMap getAllValue(String ids,String startTime,String toTime,String business){
		HashMap allValueMap=new HashMap();
		HashMap hostValueMap=new HashMap();
		HashMap netValueMap=new HashMap();
		String[] idValue=this.getIdValue(ids);
		String[] netIds = this.getIdsValues(idValue, "net|");
		String[] hostIds = this.getIdsValues(idValue, "host|");
		
		//设置时间
	  	if(startTime == null){
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			startTime = sdf.format(new Date())+" 00:00:00";
		}else if(startTime != null && !startTime.contains("00:00:00")){
			startTime=startTime+" 00:00:00";
		}
	  	if(toTime == null){
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			toTime = sdf.format(new Date())+ " 23:59:59";
		}else if(toTime != null && !toTime.contains("23:59:59")){
			toTime=toTime+" 23:59:59";
		}
	  	hostValueMap = getHostTopValue(hostIds,startTime,toTime,business);
	  	netValueMap = getNetTopValue(netIds,startTime,toTime,business);
	  	
		allValueMap.put("host", hostValueMap);
		allValueMap.put("net", netValueMap);
		
		return allValueMap;
	}
	public HashMap getHostTopValue(String[] idValue,String startTime,String toTime,String business){
		HashMap hostValueMap=new HashMap();
		HomeCollectDataManager hcdm = new HomeCollectDataManager();
		
		List cpuList = new ArrayList();				//cpu
		List memList = new ArrayList();				//内存
		List pingList = new ArrayList();			//连通性
		List responseList = new ArrayList();		//响应度
		List diskUtilizationList = new ArrayList();	//磁盘利用率
		
		HostNodeDao hostDao = new HostNodeDao();
		List hostList = new ArrayList();
		if(business==null){
			hostList = hostDao.loadHostOrNet(2);
		}else{
			hostList = hostDao.loadHostOrNet(2," and id in ("+business+")");
		}
		hostDao.close();
		if(hostList == null && hostList.size() == 0){
			return hostValueMap;
		}
		String[] ips = new String[hostList.size()];
		for(int i=0;i<hostList.size();i++){
			ips[i] = ((HostNode)hostList.get(i)).getIpAddress();
		}
		if (idValue!=null&&idValue.length>0) {
			for (int i = 0; i < idValue.length; i++) {
				//cpu
				List<CompreReportStatic> cpuTemp = new ArrayList<CompreReportStatic>();
				List<CompreReportStatic> memTemp = new ArrayList<CompreReportStatic>();
				List<CompreReportStatic> pingTemp = new ArrayList<CompreReportStatic>();
				List<CompreReportStatic> responseTemp = new ArrayList<CompreReportStatic>();
				List<CompreReportStatic> diskTemp = new ArrayList<CompreReportStatic>();
				
				if (idValue[i].indexOf("cpu")>=0) {
					//查出所有IP的cpu信息
					for(int j=0;j<ips.length;j++){
						//cpu
						String cpuvalue = hcdm.getCpuAvg("0", ips[j], startTime, toTime);
						cpuvalue = (cpuvalue == null?"0":(cpuvalue.equals("null")? "0":cpuvalue));
						CompreReportStatic crscpu = new CompreReportStatic();
						crscpu.setIp(ips[j]);
						crscpu.setType("cpu");
						crscpu.setUnit("%");
						crscpu.setValue(Double.valueOf(cpuvalue));
						cpuTemp.add(crscpu);
					}
					cpuList = compareTop(cpuTemp,10);//CPU top10
				}else if (idValue[i].indexOf("mem")>=0) {
					//查出所有IP的cpu信息
					for(int j=0;j<ips.length;j++){
						//内存
						String memvalue = hcdm.getMemoryAvg("0", ips[j], startTime, toTime);
						memvalue = (memvalue == null? "0":memvalue);
						CompreReportStatic crsmem = new CompreReportStatic();
						crsmem.setIp(ips[j]);
						crsmem.setType("mem");
						crsmem.setUnit("%");
						crsmem.setValue(Double.valueOf(memvalue));
						memTemp.add(crsmem);
					}
					memList = compareTop(memTemp,10);//内存 top10
				}
				else if(idValue[i].indexOf("ping")>=0) {
					//查出所有IP的ping信息
					for(int j=0;j<ips.length;j++){
						//ping
						String pingvalue = hcdm.getPingAvg("0", ips[j], startTime, toTime);
						pingvalue = (pingvalue == null? "0":pingvalue);
						CompreReportStatic crsping = new CompreReportStatic();
						crsping.setIp(ips[j]);
						crsping.setType("ping");
						crsping.setUnit("%");
						crsping.setValue(Double.valueOf(pingvalue));
						pingTemp.add(crsping);
					}
					pingList = compareTop(pingTemp,10);//ping top10
				}else if(idValue[i].indexOf("resp")>=0) {
					//查出所有IP的response信息
					for(int j=0;j<ips.length;j++){
						//响应度
						String responsevalue = hcdm.getResponseAvg("0", ips[j], startTime, toTime);
						responsevalue = responsevalue == null? "0":responsevalue;
						CompreReportStatic crsresponse = new CompreReportStatic();
						crsresponse.setIp(ips[j]);
						crsresponse.setType("response");
						crsresponse.setUnit("ms");
						crsresponse.setValue(Double.valueOf(responsevalue));
						responseTemp.add(crsresponse);
					}
					responseList = compareTop(responseTemp,10);//响应度 top10
				}else if(idValue[i].indexOf("disk")>=0){
					for(int k = 0;k<ips.length;k++){
						List temp = hcdm.getDisk("0", ips[k], startTime, toTime);
						if(temp!=null&&temp.size()>0){
							for(int j=0;j<temp.size();j++){
								Diskcollectdata disk = (Diskcollectdata)temp.get(j);
								CompreReportStatic crsdisk = new CompreReportStatic();
								crsdisk.setIp(ips[k]+"/"+disk.getSubentity());
								crsdisk.setType("disk");
								crsdisk.setUnit("%");
								crsdisk.setValue(Double.valueOf(disk.getThevalue()==null?"0":disk.getThevalue()));
								diskTemp.add(crsdisk);
							}
						}else{
							CompreReportStatic crsdisk = new CompreReportStatic();
							crsdisk.setIp(ips[k]);
							crsdisk.setType("disk");
							crsdisk.setUnit("%");
							crsdisk.setValue(Double.valueOf(0));
							diskTemp.add(crsdisk);
						}
					}
					diskUtilizationList = compareTop(diskTemp,10);//磁盘利用率 top10
				}
			}
		}
		hostValueMap.put("cpu", cpuList);
		hostValueMap.put("mem", memList);
		hostValueMap.put("ping", pingList);
		hostValueMap.put("response", responseList);
		hostValueMap.put("disk", diskUtilizationList);
		return hostValueMap;
	}
	public HashMap getNetTopValue(String[] idValue,String startTime,String toTime,String business){
		HashMap netValueMap=new HashMap();
		HomeCollectDataManager hcdm = new HomeCollectDataManager();
		
		List cpuList = new ArrayList();				//cpu
		List memList = new ArrayList();				//内存
		List pingList = new ArrayList();			//连通性
		List responseList = new ArrayList();		//响应度
		List inList = new ArrayList();				//入口流速
		List outList = new ArrayList();				//出口流速
		
		HostNodeDao netDao = new HostNodeDao();
		List netList = new ArrayList();
		if(business==null){
			netList = netDao.loadHostOrNet(1);
		}else{
			netList = netDao.loadHostOrNet(1," and id in ("+business+")");
		}
		netDao.close();
		if(netList == null && netList.size() == 0){
			return netValueMap;
		}
		String[] ips = new String[netList.size()];
		for(int i=0;i<netList.size();i++){
			ips[i] = ((HostNode)netList.get(i)).getIpAddress();
		}
		if (idValue!=null&&idValue.length>0) {
			for (int i = 0; i < idValue.length; i++) {
				//cpu
				List<CompreReportStatic> cpuTemp = new ArrayList<CompreReportStatic>();
				List<CompreReportStatic> memTemp = new ArrayList<CompreReportStatic>();
				List<CompreReportStatic> pingTemp = new ArrayList<CompreReportStatic>();
				List<CompreReportStatic> responseTemp = new ArrayList<CompreReportStatic>();
				List<CompreReportStatic> inTemp = new ArrayList<CompreReportStatic>();
				List<CompreReportStatic> outTemp = new ArrayList<CompreReportStatic>();
				
				if (idValue[i].indexOf("cpu")>=0) {
					//查出所有IP的cpu信息
					for(int j=0;j<ips.length;j++){
						//cpu
						String cpuvalue = hcdm.getCpuAvg("0", ips[j], startTime, toTime);
						cpuvalue = cpuvalue == null? "0":cpuvalue;
						CompreReportStatic crscpu = new CompreReportStatic();
						crscpu.setIp(ips[j]);
						crscpu.setType("cpu");
						crscpu.setUnit("%");
						crscpu.setValue(Double.valueOf(cpuvalue));
						cpuTemp.add(crscpu);
					}
					cpuList = compareTop(cpuTemp,10);//CPU top10
				}else if (idValue[i].indexOf("mem")>=0) {
					//查出所有IP的cpu信息
					for(int j=0;j<ips.length;j++){
						//内存
						String memvalue = hcdm.getMemoryAvg("0", ips[j], startTime, toTime);
						memvalue = memvalue == null? "0":memvalue;
						CompreReportStatic crsmem = new CompreReportStatic();
						crsmem.setIp(ips[j]);
						crsmem.setType("mem");
						crsmem.setUnit("%");
						crsmem.setValue(Double.valueOf(memvalue));
						memTemp.add(crsmem);
					}
					memList = compareTop(memTemp,10);//内存 top10
				}else if(idValue[i].indexOf("ping")>=0) {
					//查出所有IP的ping信息
					for(int j=0;j<ips.length;j++){
						//ping
						String pingvalue = hcdm.getPingAvg("0", ips[j], startTime, toTime);
						pingvalue = pingvalue == null? "0":pingvalue;
						CompreReportStatic crsping = new CompreReportStatic();
						crsping.setIp(ips[j]);
						crsping.setType("ping");
						crsping.setUnit("%");
						crsping.setValue(Double.valueOf(pingvalue));
						pingTemp.add(crsping);
					}
					pingList = compareTop(pingTemp,10);//ping top10
				}else if(idValue[i].indexOf("resp")>=0) {
					//查出所有IP的ping信息
					for(int j=0;j<ips.length;j++){
						//response
						String responsevalue = hcdm.getResponseAvg("0", ips[j], startTime, toTime);
						responsevalue = responsevalue == null? "0":responsevalue;
						CompreReportStatic crsresponse = new CompreReportStatic();
						crsresponse.setIp(ips[j]);
						crsresponse.setType("response");
						crsresponse.setUnit("ms");
						crsresponse.setValue(Double.valueOf(responsevalue));
						responseTemp.add(crsresponse);
					}
					responseList = compareTop(responseTemp,10);//响应度 top10
				}else if(idValue[i].indexOf("utilIn")>=0){
					//入口
					for(int j=0;j<ips.length;j++){
						String invalue = hcdm.getAllInutilhdxAvg("0",ips[j], startTime, toTime);
						invalue = invalue == null? "0":invalue;
						CompreReportStatic crsin = new CompreReportStatic();
						crsin.setIp(ips[j]);
						crsin.setType("inutilhdx");
						crsin.setUnit("KB/s");
						crsin.setValue(Double.valueOf(invalue));
						inTemp.add(crsin);
					}
					inList = compareTop(inTemp,10);//入口 top10
				}else if(idValue[i].indexOf("utilOut")>=0){
					//入口
					for(int j=0;j<ips.length;j++){
						//出口
						String outvalue = hcdm.getAllOututilhdxAvg("0",ips[j], startTime, toTime);
						outvalue = outvalue == null? "0":outvalue;
						CompreReportStatic crsout = new CompreReportStatic();
						crsout.setIp(ips[j]);
						crsout.setType("oututilhdx");
						crsout.setUnit("KB/s");
						crsout.setValue(Double.valueOf(outvalue));
						outTemp.add(crsout);
					}
					outList = compareTop(outTemp,10);//出口 top10
				}
			}
		}
		netValueMap.put("cpu", cpuList);
		netValueMap.put("mem", memList);
		netValueMap.put("ping", pingList);
		netValueMap.put("response", responseList);
		netValueMap.put("utilIn", inList);
		netValueMap.put("utilOut", outList);
		return netValueMap;
	}
	//选择出Top
	private List compareTop(List<CompreReportStatic> temp, int top){
		List list = new ArrayList();
		if(top >= temp.size()){
			top = temp.size();
			for(int i=0;i<top;i++){
				if(i==top){
					list.add(temp.get(i));
				}else{
					for(int j = i+1;j<temp.size(); j++){
						if(temp.get(i).getValue()<temp.get(j).getValue()){
							CompreReportStatic crs = null;
							crs = (CompreReportStatic)temp.get(i);
							temp.set(i, (CompreReportStatic)temp.get(j));
							temp.set(j, crs);
						}
					}
					list.add(temp.get(i));
				}
			}
		}
		else{
			for(int i=0;i<top;i++){
				for(int j = i+1;j<temp.size(); j++){
					if(temp.get(i).getValue()<temp.get(j).getValue()){
						CompreReportStatic crs = null;
						crs = (CompreReportStatic)temp.get(i);
						temp.set(i, (CompreReportStatic)temp.get(j));
						temp.set(j, crs);
					}
				}
				list.add(temp.get(i));
			}
		}
		return list;
	}
	private String[] getIdValue(String ids){
		String[] idValue=null;
		if (ids!=null&&!ids.equals("null")&&!ids.equals("")) {
			 idValue=new String[ids.split(",").length];
	    	idValue=ids.split(",");
		}
		return idValue;
	}
	private String[] getIdsValues(String[] idValue,String type){
		String[] idsValue = null;
		List<String> idsList = new ArrayList<String>();
		for(int i=0;i<idValue.length;i++){
			if(idValue[i].contains(type)){
				idsList.add(idValue[i].replace(type, ""));
			}
		}
		if(idsList != null && idsList.size() > 0){
			idsValue = new String[idsList.size()];
			for(int i=0;i<idsList.size();i++){
				idsValue[i] = idsList.get(i);
			}
		}
		return idsValue;
	}
	
	/**
	 * 生成报警表格数据(设备)
	 * @return
	 */
	public String[][] getDevTableData(String type,String dateFrom,String dateTo,String business){
		DBManager conn = new DBManager();
		ResultSet rs = null;
		String typeStr = type.equals("host")?"服务器":"网络设备";
		String[] dataStr = new String[] {typeStr,"IP地址","设备名称","操作系统","事件总数","普通","严重","紧急","连通率事件","内存事件","磁盘事件","CPU事件"};
		List<String[]> list = new ArrayList<String[]>();
		list.add(dataStr);
		business = business==null?"":" and t.id in ("+business+")";
		try
		{
			StringBuilder sb=new StringBuilder();
			sb.append("select t.ip_address as ip, t.alias as alias,t.type as type,count(1) as cnt ," +
				" sum(case when s.level1=1 then 1 else 0 end) as le1," +
				"sum(case when s.level1=2 then 1 else 0 end) as le2," +
				"sum(case when s.level1=3 then 1 else 0 end) as le3," +
				"sum(case when s.subentity like '%ping%' then 1 else 0 end) as ping," +
				"sum(case when s.subentity like '%memory%' then 1 else 0 end) as memory," +
				"sum(case when s.subentity like '%disk%' then 1 else 0 end) as disk," +
				"sum(case when s.subentity like '%cpu%' then 1 else 0 end) as cpu " +
				"from system_eventlist s , topo_host_node  t " +
				"where s.recordtime between '" + dateFrom +"' and '"+ dateTo +
						"' and s.nodeid = t.id "+business+" and s.subtype = '" + type + "' group by s.nodeid;");
			System.out.println("###MT##SQL######"+sb.toString());
			rs = conn.executeQuery(sb.toString());
			while(rs.next()){
				String[] data = new String[12];
				data[1] = rs.getString("ip");
				data[2] = rs.getString("alias");
				data[3] = rs.getString("type");
				data[4] = rs.getString("cnt");
				data[5] = rs.getString("le1");
				data[6] = rs.getString("le2");
				data[7] = rs.getString("le3");
				data[8] = rs.getString("ping");
				data[9] = rs.getString("memory");
				data[10] = rs.getString("disk");
				data[11] = rs.getString("cpu");
				list.add(data);
			}	
		}catch(Exception e){
			SysLogger.error("CompreReportHelper:",e);
		}finally{
	    	 try {
					rs.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				conn.close();
		     } 
		String[][] backTable = null;
		if(list!=null&&list.size()>1){
			backTable = new String[list.size()][12];
			for(int i = 0; i<list.size();i++){
				for(int j = 0;j < dataStr.length;j++){
					backTable[i][j] = (list.get(i))[j];
					if( i > 0){
						backTable[i][0] = i + "";
					}
				}
			}
		}
		return backTable;
	}
	public String[][] gettableData(String dateFrom , String dateTo,String business){
		DBManager conn = new DBManager();;
		ResultSet rs = null;
		business = business==null?"":" and nodeid in ("+business+")";
		String [][] dataStr=new String[][] {{"类别","提示","普通","严重","紧急"},
											{"网络告警","0","0","0","0"},
											{"设备告警","0","0","0","0"},
											{"服务器告警","0","0","0","0"},
											{"数据库告警","0","0","0","0"},
											{"中间件告警","0","0","0","0"},
											{"应用告警","0","0","0","0"},
											{"存储告警","0","0","0","0"},
											{"业务告警","0","0","0","0"},
											{"安全告警","0","0","0","0"}};
		try
	     {
			String subtype="";
			int level=0;
			//rs = conn.executeQuery("select subtype,level1,count(1) as cnt from system_eventlist group by subtype,level1;");
			//--修改为只查询当天数据
			StringBuilder sb=new StringBuilder();
			sb.append(" select subtype,level1,count(1) as cnt from system_eventlist ");
			sb.append(" where recordtime between '" + dateFrom +"' and '"+ dateTo);
			sb.append("' "+business+" group by subtype,level1; ");
			rs = conn.executeQuery(sb.toString()); 
	         while(rs.next()){
	        	 subtype=rs.getString("subtype"); 
				//端口："plot"
				//网络:"net""dns" 
				//数据库 "db"
				//服务器："host" 
				//中间件 "domino""tomcat""cics""mq""wasserver""weblogic""iis""jboss""apache" 
				//服务："mail" "ftp" "web""socket"
				//业务告警 "bus"   
				//"grapes" "radar"
		        // "network"
				//   	 	
	        	 if(subtype.equalsIgnoreCase("net")||subtype.equalsIgnoreCase("dns")){//网络告警
	        		 level=rs.getInt("level1");
	        		 dataStr[1][level+1]= String.valueOf((Integer.parseInt(dataStr[1][level+1])+Integer.parseInt(rs.getString("cnt"))));
//	 	         }else if(subtype.equalsIgnoreCase("network")){ //设备告警 
//	        		 level=rs.getInt("level1"); 
//	        		 dataStr[2][level+1]= String.valueOf((Integer.parseInt(dataStr[2][level+1])+Integer.parseInt(rs.getString("cnt"))));
	        	 }else if(subtype.equalsIgnoreCase("host")){//服务器告警 
	        		 level=rs.getInt("level1");
	        		 dataStr[3][level+1]= String.valueOf((Integer.parseInt(dataStr[3][level+1])+Integer.parseInt(rs.getString("cnt"))));
	 	         }else if(subtype.equalsIgnoreCase("db")){//数据库告警 
	        		 level=rs.getInt("level1");
	        		 dataStr[4][level+1]=rs.getString("cnt");
	        	 }else if(subtype.equalsIgnoreCase("domino")
	        			 ||subtype.equalsIgnoreCase("tomcat")
	        			 ||subtype.equalsIgnoreCase("cics")
	        			 ||subtype.equalsIgnoreCase("mq")
	        			 ||subtype.equalsIgnoreCase("wasserver")
	        			 ||subtype.equalsIgnoreCase("weblogic")
	        			 ||subtype.equalsIgnoreCase("iis")
	        			 ||subtype.equalsIgnoreCase("jboss")
	        			 ||subtype.equalsIgnoreCase("apache")){ //中间件告警 
	        		 level=rs.getInt("level1");
	        		 dataStr[5][level+1]= String.valueOf((Integer.parseInt(dataStr[5][level+1])+Integer.parseInt(rs.getString("cnt"))));
	        	 } 
	        	 //有待继续添加
	        	 //6应用告警 
	        	 //7存储告警 
	        	 //8业务告警 
	        	 else if(subtype.equalsIgnoreCase("bus")){//业务告警  
        		 level=rs.getInt("level1");
        		 dataStr[8][level+1]=rs.getString("cnt");
	        	 }
	        	 //9安全告警 
	        	 
	         }
	     }
	     catch(Exception e)
	     {
	         SysLogger.error("CompreReportHelper:",e);
	     }finally{
	    	 try {
					rs.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				conn.close();
		     }
		return dataStr;
	}
	public List getLevelPieData(String dateFrom , String dateTo,String business){
		DBManager conn = new DBManager();;
		ResultSet rs = null;
		business = business==null?"":" and nodeid in ("+business+")";
		StringBuffer dataStr=new StringBuffer();
		List list = new ArrayList();
		 Map<String, String> map=new TreeMap<String, String>();
		 //初始值为0
		 map.put("1", "0");
		 map.put("2", "0");
		 map.put("3", "0");
	     try
	     {
	    	 //rs = conn.executeQuery("select managesign as sign,count(1) as cnt from system_eventlist group by (managesign)");
	    	//--修改为只查询当天数据
	    	 StringBuilder sb=new StringBuilder();
				sb.append(" select level1 as sign,count(1) as cnt from system_eventlist ");
				sb.append(" where recordtime between '" + dateFrom +"' and '"+ dateTo);
				sb.append("' "+business+" group by (level1) ; ");
				rs = conn.executeQuery(sb.toString());
	         while(rs.next()){
	        	map.put(rs.getString("sign"), rs.getString("cnt"));
	         }
	     }
	     catch(Exception e)
	     {
	         SysLogger.error("CompreReportHelper:",e);
	     } finally{
	    	 try {
				rs.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			conn.close();
	     }
	     for(int i=1;i<=map.size();i++){
	    	 CompreReportStatic crspie = new CompreReportStatic();
	    	 crspie.setIp(i==1?"普通":(i==2?"严重":"紧急"));
	    	 crspie.setValue(Integer.valueOf(map.get(i+"")));
	    	 list.add(crspie);
	    }
	    return list;
	}
	public List getDayAlarmData(String dateFrom , String dateTo,String business){
		List list = new ArrayList();
		DBManager conn = new DBManager();;
		ResultSet rs = null;
		business = business==null?"":" and nodeid in ("+business+")";
		Map<Integer, Integer> map=new TreeMap<Integer, Integer>();
		for(int i=0;i<24;i++){
			map.put(i, 0);
		}
		try
	     {
	    	 rs = conn.executeQuery("select HOUR(recordtime)  as h,count(1) as cnt from system_eventlist " +
	    			 "where recordtime between '" + dateFrom +"' and '"+ dateTo +
	    	 				"' "+business+" group by h;");
	         while(rs.next())
	        	 map.put(rs.getInt("h"), rs.getInt("cnt"));
	     }
	     catch(Exception e)
	     {
	         SysLogger.error("CompreReportHelper:",e);
	     }
	     finally{
	    	 try {
				rs.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			conn.close();
	     }
		for(int i=0;i<24;i++){
			list.add(map.get(i));
		}
		return list;
	}
	
	public HashMap getAllValueWeek(String ids,String[][] weekDay,String business){
		HashMap allValueMap=new HashMap();
		HashMap hostValueMap=new HashMap();
		HashMap netValueMap=new HashMap();
		String[] idValue=this.getIdValue(ids);
		String[] netIds = this.getIdsValues(idValue, "net|");
		String[] hostIds = this.getIdsValues(idValue, "host|");
		
		hostValueMap = getHostTopValueWeek(hostIds,weekDay,business);
	  	netValueMap = getNetTopValueWeek(netIds,weekDay,business);
		
		//设置时间
		
	  	
	  	
		allValueMap.put("host", hostValueMap);
		allValueMap.put("net", netValueMap);
		
		return allValueMap;
	}
	public HashMap getHostTopValueWeek(String[] idValue,String[][] weekDay,String business){
		HashMap hostValueMap=new HashMap();
		HomeCollectDataManager hcdm = new HomeCollectDataManager();
		
		List cpuList = new ArrayList();				//cpu
		List memList = new ArrayList();				//内存
		List pingList = new ArrayList();			//连通性
		List responseList = new ArrayList();		//响应度
		List diskUtilizationList = new ArrayList();	//磁盘利用率
		
		HostNodeDao hostDao = new HostNodeDao();
		List hostList = new ArrayList();
		if(business==null){
			hostList = hostDao.loadHostOrNet(2);
		}else{
			hostList = hostDao.loadHostOrNet(2," and id in ("+business+")");
		}
		hostDao.close();
		if(hostList == null && hostList.size() == 0){
			return hostValueMap;
		}
		String[] ips = new String[hostList.size()];
		for(int i=0;i<hostList.size();i++){
			ips[i] = ((HostNode)hostList.get(i)).getIpAddress();
		}
		if (idValue!=null&&idValue.length>0) {
			for (int i = 0; i < idValue.length; i++) {
				//cpu
				List<CompreReportStatic> cpuTemp = new ArrayList<CompreReportStatic>();
				List<CompreReportStatic> memTemp = new ArrayList<CompreReportStatic>();
				List<CompreReportStatic> pingTemp = new ArrayList<CompreReportStatic>();
				List<CompreReportStatic> responseTemp = new ArrayList<CompreReportStatic>();
				List<CompreReportStatic> diskTemp = new ArrayList<CompreReportStatic>();
				
				if (idValue[i].indexOf("cpu")>=0) {
					//查出所有IP的cpu信息
					for(int j=0;j<ips.length;j++){
						//cpu
						Map<String,Double> weekValues = new TreeMap<String,Double>();
						for(int n=0;n<weekDay.length;n++){
							for(int f=0;f<24;f++){
								if(f/10<1){
									weekValues.put(weekDay[n][0]+"-0"+f, 0d);
								}else{
									weekValues.put(weekDay[n][0]+"-"+f, 0d);
								}
							}
						}
						weekValues = hcdm.getCpuAvgWeek("0", ips[j], weekDay[0][1], weekDay[weekDay.length-1][2],weekValues);
						
						String avgcpuvalue = hcdm.getCpuAvg("0", ips[j], weekDay[0][1], weekDay[weekDay.length-1][2]);
						avgcpuvalue = (avgcpuvalue == null?"0":(avgcpuvalue.equals("null")? "0":avgcpuvalue));
						String max = hcdm.getCpuExtreme("0", ips[j], weekDay[0][1],weekDay[weekDay.length-1][2],"max");
						String min = hcdm.getCpuExtreme("0", ips[j], weekDay[0][1],weekDay[weekDay.length-1][2],"min");
						max = max == null? "0":max;
						min = min == null? "0":min;
						CompreReportStatic crscpu = new CompreReportStatic();
						crscpu.setIp(ips[j]);
						crscpu.setType("cpu");
						crscpu.setUnit("%");
						crscpu.setValue(Double.valueOf(avgcpuvalue));
						crscpu.setMax(Double.valueOf(max));
						crscpu.setMin(Double.valueOf(min));
						crscpu.setWeekValues(weekValues);
						cpuTemp.add(crscpu);
					}
					cpuList = compareTop(cpuTemp,10);//CPU top10
				}else if (idValue[i].indexOf("mem")>=0) {
					//查出所有IP的cpu信息
					for(int j=0;j<ips.length;j++){
						Map<String,Double> weekValues = new TreeMap<String,Double>();
						for(int n=0;n<weekDay.length;n++){
							for(int f=0;f<24;f++){
								if(f/10<1){
									weekValues.put(weekDay[n][0]+"-0"+f, 0d);
								}else{
									weekValues.put(weekDay[n][0]+"-"+f, 0d);
								}
							}
						}
						weekValues = hcdm.getMemoryAvgWeek("0", ips[j], weekDay[0][1], weekDay[weekDay.length-1][2],weekValues);
						
						//内存
						String memvalue = hcdm.getMemoryAvg("0",ips[j],weekDay[0][1],weekDay[weekDay.length-1][2]);
						memvalue = (memvalue == null? "0":memvalue);
						String max = hcdm.getMemoryExtreme("0", ips[j], weekDay[0][1],weekDay[weekDay.length-1][2],"max");
						String min = hcdm.getMemoryExtreme("0", ips[j], weekDay[0][1],weekDay[weekDay.length-1][2],"min");
						max = max == null? "0":max;
						min = min == null? "0":min;
						CompreReportStatic crsmem = new CompreReportStatic();
						crsmem.setIp(ips[j]);
						crsmem.setType("mem");
						crsmem.setUnit("%");
						crsmem.setValue(Double.valueOf(memvalue));
						crsmem.setMax(Double.valueOf(max));
						crsmem.setMin(Double.valueOf(min));
						crsmem.setWeekValues(weekValues);
						memTemp.add(crsmem);
					}
					memList = compareTop(memTemp,10);//内存 top10
				}
				else if(idValue[i].indexOf("ping")>=0) {
					//查出所有IP的ping信息
					for(int j=0;j<ips.length;j++){
						Map<String,Double> weekValues = new TreeMap<String,Double>();
						for(int n=0;n<weekDay.length;n++){
							for(int f=0;f<24;f++){
								if(f/10<1){
									weekValues.put(weekDay[n][0]+"-0"+f, 0d);
								}else{
									weekValues.put(weekDay[n][0]+"-"+f, 0d);
								}
							}
						}
						weekValues = hcdm.getPingAvgWeek("0", ips[j], weekDay[0][1], weekDay[weekDay.length-1][2],weekValues);
						
						//ping
						String pingvalue = hcdm.getPingAvg("0", ips[j],weekDay[0][1],weekDay[weekDay.length-1][2]);
						pingvalue = (pingvalue == null? "0":pingvalue);
						String max = hcdm.getPingExtreme("0", ips[j], weekDay[0][1],weekDay[weekDay.length-1][2],"max");
						String min = hcdm.getPingExtreme("0", ips[j], weekDay[0][1],weekDay[weekDay.length-1][2],"min");
						max = max == null? "0":max;
						min = min == null? "0":min;
						CompreReportStatic crsping = new CompreReportStatic();
						crsping.setIp(ips[j]);
						crsping.setType("ping");
						crsping.setUnit("%");
						crsping.setValue(Double.valueOf(pingvalue));
						crsping.setMax(Double.valueOf(max));
						crsping.setMin(Double.valueOf(min));
						crsping.setWeekValues(weekValues);
						pingTemp.add(crsping);
					}
					pingList = compareTop(pingTemp,10);//ping top10
				}else if(idValue[i].indexOf("resp")>=0) {
					//查出所有IP的response信息
					for(int j=0;j<ips.length;j++){
						Map<String,Double> weekValues = new TreeMap<String,Double>();
						for(int n=0;n<weekDay.length;n++){
							for(int f=0;f<24;f++){
								if(f/10<1){
									weekValues.put(weekDay[n][0]+"-0"+f, 0d);
								}else{
									weekValues.put(weekDay[n][0]+"-"+f, 0d);
								}
							}
						}
						weekValues = hcdm.getResponseAvgWeek("0", ips[j], weekDay[0][1], weekDay[weekDay.length-1][2],weekValues);
						
						//响应度
						String responsevalue = hcdm.getResponseAvg("0", ips[j],weekDay[0][1],weekDay[weekDay.length-1][2]);
						responsevalue = responsevalue == null? "0":responsevalue;
						String max = hcdm.getResponseExtreme("0", ips[j], weekDay[0][1],weekDay[weekDay.length-1][2],"max");
						String min = hcdm.getResponseExtreme("0", ips[j], weekDay[0][1],weekDay[weekDay.length-1][2],"min");
						max = max == null? "0":max;
						min = min == null? "0":min;
						CompreReportStatic crsresponse = new CompreReportStatic();
						crsresponse.setIp(ips[j]);
						crsresponse.setType("response");
						crsresponse.setUnit("ms");
						crsresponse.setValue(Double.valueOf(responsevalue));
						crsresponse.setMax(Double.valueOf(max));
						crsresponse.setMin(Double.valueOf(min));
						crsresponse.setWeekValues(weekValues);
						responseTemp.add(crsresponse);
					}
					responseList = compareTop(responseTemp,10);//响应度 top10
				}else if(idValue[i].indexOf("disk")>=0){
					for(int k = 0;k<ips.length;k++){
						List temp = hcdm.getDisk("0", ips[k],weekDay[0][1],weekDay[weekDay.length-1][2]);
						if(temp!=null&&temp.size()>0){
							for(int j=0;j<temp.size();j++){
								Diskcollectdata disk = (Diskcollectdata)temp.get(j);
								CompreReportStatic crsdisk = new CompreReportStatic();
								crsdisk.setIp(ips[k]+"/"+disk.getSubentity());
								crsdisk.setType("disk");
								crsdisk.setUnit("%");
								crsdisk.setValue(Double.valueOf(disk.getThevalue()==null?"0":disk.getThevalue()));
								Map<String,Double> weekValues = new TreeMap<String,Double>();
								for(int n=0;n<weekDay.length;n++){
									for(int f=0;f<24;f++){
										if(f/10<1){
											weekValues.put(weekDay[n][0]+"-0"+f, 0d);
										}else{
											weekValues.put(weekDay[n][0]+"-"+f, 0d);
										}
									}
								}
								weekValues = hcdm.getDiskWeek("0", ips[k],disk.getSubentity(),weekDay[0][1], weekDay[weekDay.length-1][2],weekValues);
								crsdisk.setWeekValues(weekValues);
								String max = hcdm.getDiskExtreme("0", ips[k],disk.getSubentity(), weekDay[0][1],weekDay[weekDay.length-1][2],"max");
								String min = hcdm.getDiskExtreme("0", ips[k],disk.getSubentity(), weekDay[0][1],weekDay[weekDay.length-1][2],"min");
								max = max == null? "0":max;
								min = min == null? "0":min;
								crsdisk.setMax(Double.valueOf(max));
								crsdisk.setMin(Double.valueOf(min));
								diskTemp.add(crsdisk);
							}
						}else{
							CompreReportStatic crsdisk = new CompreReportStatic();
							crsdisk.setIp(ips[k]);
							crsdisk.setType("disk");
							crsdisk.setUnit("%");
							crsdisk.setValue(Double.valueOf(0));
							crsdisk.setMax(Double.valueOf(0));
							crsdisk.setMin(Double.valueOf(0));
							Map<String,Double> weekValues = new TreeMap<String,Double>();
							for(int n=0 ;n<weekDay.length;n++){
								String time = weekDay[n][0];
								weekValues.put(time, Double.valueOf(0));
							}
							diskTemp.add(crsdisk);
						}
					}
					diskUtilizationList = compareTop(diskTemp,10);//磁盘利用率 top10
				}
			}
		}
		hostValueMap.put("cpu", cpuList);
		hostValueMap.put("mem", memList);
		hostValueMap.put("ping", pingList);
		hostValueMap.put("response", responseList);
		hostValueMap.put("disk", diskUtilizationList);		
		return hostValueMap;
	}
	public HashMap getNetTopValueWeek(String[] idValue,String[][] weekDay,String business){
		HashMap netValueMap=new HashMap();
		HomeCollectDataManager hcdm = new HomeCollectDataManager();
		
		List cpuList = new ArrayList();				//cpu
		List memList = new ArrayList();				//内存
		List pingList = new ArrayList();			//连通性
		List responseList = new ArrayList();		//响应度
		List inList = new ArrayList();				//入口流速
		List outList = new ArrayList();				//出口流速
		
		HostNodeDao netDao = new HostNodeDao();
		List netList = new ArrayList();
		if(business==null){
			netList = netDao.loadHostOrNet(1);
		}else{
			netList = netDao.loadHostOrNet(1," and id in ("+business+")");
		}
		netDao.close();
		if(netList == null && netList.size() == 0){
			return netValueMap;
		}
		String[] ips = new String[netList.size()];
		for(int i=0;i<netList.size();i++){
			ips[i] = ((HostNode)netList.get(i)).getIpAddress();
		}
		if (idValue!=null&&idValue.length>0) {
			for (int i = 0; i < idValue.length; i++) {
				//cpu
				List<CompreReportStatic> cpuTemp = new ArrayList<CompreReportStatic>();
				List<CompreReportStatic> memTemp = new ArrayList<CompreReportStatic>();
				List<CompreReportStatic> pingTemp = new ArrayList<CompreReportStatic>();
				List<CompreReportStatic> responseTemp = new ArrayList<CompreReportStatic>();
				List<CompreReportStatic> inTemp = new ArrayList<CompreReportStatic>();
				List<CompreReportStatic> outTemp = new ArrayList<CompreReportStatic>();
				
				if (idValue[i].indexOf("cpu")>=0) {
					//查出所有IP的cpu信息
					for(int j=0;j<ips.length;j++){
						Map<String,Double> weekValues = new TreeMap<String,Double>();
						for(int n=0;n<weekDay.length;n++){
							for(int f=0;f<24;f++){
								if(f/10<1){
									weekValues.put(weekDay[n][0]+"-0"+f, 0d);
								}else{
									weekValues.put(weekDay[n][0]+"-"+f, 0d);
								}
							}
						}
						weekValues = hcdm.getCpuAvgWeek("0", ips[j], weekDay[0][1], weekDay[weekDay.length-1][2],weekValues);
						
						//cpu
						String cpuvalue = hcdm.getCpuAvg("0", ips[j], weekDay[0][1],weekDay[weekDay.length-1][2]);
						cpuvalue = cpuvalue == null? "0":cpuvalue;
						String max = hcdm.getCpuExtreme("0", ips[j], weekDay[0][1],weekDay[weekDay.length-1][2],"max");
						String min = hcdm.getCpuExtreme("0", ips[j], weekDay[0][1],weekDay[weekDay.length-1][2],"min");
						max = max == null? "0":max;
						min = min == null? "0":min;
						CompreReportStatic crscpu = new CompreReportStatic();
						crscpu.setIp(ips[j]);
						crscpu.setType("cpu");
						crscpu.setUnit("%");
						crscpu.setValue(Double.valueOf(cpuvalue));
						crscpu.setMax(Double.valueOf(max));
						crscpu.setMin(Double.valueOf(min));
						crscpu.setWeekValues(weekValues);
						cpuTemp.add(crscpu);
					}
					cpuList = compareTop(cpuTemp,10);//CPU top10
				}else if (idValue[i].indexOf("mem")>=0) {
					//查出所有IP的内存信息
					for(int j=0;j<ips.length;j++){
						Map<String,Double> weekValues = new TreeMap<String,Double>();
						for(int n=0;n<weekDay.length;n++){
							for(int f=0;f<24;f++){
								if(f/10<1){
									weekValues.put(weekDay[n][0]+"-0"+f, 0d);
								}else{
									weekValues.put(weekDay[n][0]+"-"+f, 0d);
								}
							}
						}
						weekValues = hcdm.getMemoryAvgWeek("0", ips[j], weekDay[0][1], weekDay[weekDay.length-1][2],weekValues);
						
						//内存
						String memvalue = hcdm.getMemoryAvg("0", ips[j], weekDay[0][1],weekDay[weekDay.length-1][2]);
						memvalue = memvalue == null? "0":memvalue;
						String max = hcdm.getMemoryExtreme("0", ips[j], weekDay[0][1],weekDay[weekDay.length-1][2],"max");
						String min = hcdm.getMemoryExtreme("0", ips[j], weekDay[0][1],weekDay[weekDay.length-1][2],"min");
						max = max == null? "0":max;
						min = min == null? "0":min;
						CompreReportStatic crsmem = new CompreReportStatic();
						crsmem.setIp(ips[j]);
						crsmem.setType("mem");
						crsmem.setUnit("%");
						crsmem.setValue(Double.valueOf(memvalue));
						crsmem.setMax(Double.valueOf(max));
						crsmem.setMin(Double.valueOf(min));
						crsmem.setWeekValues(weekValues);
						memTemp.add(crsmem);
					}
					memList = compareTop(memTemp,10);//内存 top10
				}else if(idValue[i].indexOf("ping")>=0) {
					//查出所有IP的ping信息
					for(int j=0;j<ips.length;j++){
						Map<String,Double> weekValues = new TreeMap<String,Double>();
						for(int n=0;n<weekDay.length;n++){
							for(int f=0;f<24;f++){
								if(f/10<1){
									weekValues.put(weekDay[n][0]+"-0"+f, 0d);
								}else{
									weekValues.put(weekDay[n][0]+"-"+f, 0d);
								}
							}
						}
						weekValues = hcdm.getPingAvgWeek("0", ips[j], weekDay[0][1], weekDay[weekDay.length-1][2],weekValues);
						
						//ping
						String pingvalue = hcdm.getPingAvg("0", ips[j], weekDay[0][1],weekDay[weekDay.length-1][2]);
						pingvalue = pingvalue == null? "0":pingvalue;
						String max = hcdm.getPingExtreme("0", ips[j], weekDay[0][1],weekDay[weekDay.length-1][2],"max");
						String min = hcdm.getPingExtreme("0", ips[j], weekDay[0][1],weekDay[weekDay.length-1][2],"min");
						max = max == null? "0":max;
						min = min == null? "0":min;
						CompreReportStatic crsping = new CompreReportStatic();
						crsping.setIp(ips[j]);
						crsping.setType("ping");
						crsping.setUnit("%");
						crsping.setValue(Double.valueOf(pingvalue));
						crsping.setMax(Double.valueOf(max));
						crsping.setMin(Double.valueOf(min));
						crsping.setWeekValues(weekValues);
						pingTemp.add(crsping);
					}
					pingList = compareTop(pingTemp,10);//ping top10
				}else if(idValue[i].indexOf("resp")>=0) {
					//查出所有IP的response信息
					for(int j=0;j<ips.length;j++){
						Map<String,Double> weekValues = new TreeMap<String,Double>();
						for(int n=0;n<weekDay.length;n++){
							for(int f=0;f<24;f++){
								if(f/10<1){
									weekValues.put(weekDay[n][0]+"-0"+f, 0d);
								}else{
									weekValues.put(weekDay[n][0]+"-"+f, 0d);
								}
							}
						}
						weekValues = hcdm.getResponseAvgWeek("0", ips[j], weekDay[0][1], weekDay[weekDay.length-1][2],weekValues);
						
						//response
						String responsevalue = hcdm.getResponseAvg("0", ips[j], weekDay[0][1],weekDay[weekDay.length-1][2]);
						responsevalue = responsevalue == null? "0":responsevalue;
						String max = hcdm.getResponseExtreme("0", ips[j], weekDay[0][1],weekDay[weekDay.length-1][2],"max");
						String min = hcdm.getResponseExtreme("0", ips[j], weekDay[0][1],weekDay[weekDay.length-1][2],"min");
						max = max == null? "0":max;
						min = min == null? "0":min;
						CompreReportStatic crsresponse = new CompreReportStatic();
						crsresponse.setIp(ips[j]);
						crsresponse.setType("response");
						crsresponse.setUnit("ms");
						crsresponse.setValue(Double.valueOf(responsevalue));
						crsresponse.setMax(Double.valueOf(max));
						crsresponse.setMin(Double.valueOf(min));
						crsresponse.setWeekValues(weekValues);
						responseTemp.add(crsresponse);
					}
					responseList = compareTop(responseTemp,10);//响应度 top10
				}else if(idValue[i].indexOf("utilIn")>=0){
					//入口
					for(int j=0;j<ips.length;j++){
						Map<String,Double> weekValues = new TreeMap<String,Double>();
						for(int n=0;n<weekDay.length;n++){
							for(int f=0;f<24;f++){
								if(f/10<1){
									weekValues.put(weekDay[n][0]+"-0"+f, 0d);
								}else{
									weekValues.put(weekDay[n][0]+"-"+f, 0d);
								}
							}
						}
						weekValues = hcdm.getAllInutilhdxAvgWeek("0", ips[j], weekDay[0][1], weekDay[weekDay.length-1][2],weekValues);
						
						String invalue = hcdm.getAllInutilhdxAvg("0",ips[j], weekDay[0][1],weekDay[weekDay.length-1][2]);
						invalue = invalue == null? "0":invalue;
						String max = hcdm.getAllInutilhdxExtreme("0", ips[j], weekDay[0][1],weekDay[weekDay.length-1][2],"max");
						String min = hcdm.getAllInutilhdxExtreme("0", ips[j], weekDay[0][1],weekDay[weekDay.length-1][2],"min");
						max = max == null? "0":max;
						min = min == null? "0":min;
						CompreReportStatic crsin = new CompreReportStatic();
						crsin.setIp(ips[j]);
						crsin.setType("inutilhdx");
						crsin.setUnit("KB/s");
						crsin.setValue(Double.valueOf(invalue));
						crsin.setMax(Double.valueOf(max));
						crsin.setMin(Double.valueOf(min));
						crsin.setWeekValues(weekValues);
						inTemp.add(crsin);
					}
					inList = compareTop(inTemp,10);//入口 top10
				}else if(idValue[i].indexOf("utilOut")>=0){
					//出口
					for(int j=0;j<ips.length;j++){
						Map<String,Double> weekValues = new TreeMap<String,Double>();
						for(int n=0;n<weekDay.length;n++){
							for(int f=0;f<24;f++){
								if(f/10<1){
									weekValues.put(weekDay[n][0]+"-0"+f, 0d);
								}else{
									weekValues.put(weekDay[n][0]+"-"+f, 0d);
								}
							}
						}
						weekValues = hcdm.getAllOututilhdxAvgWeek("0", ips[j], weekDay[0][1], weekDay[weekDay.length-1][2],weekValues);
						
						//出口
						String outvalue = hcdm.getAllOututilhdxAvg("0",ips[j], weekDay[0][1],weekDay[weekDay.length-1][2]);
						outvalue = outvalue == null? "0":outvalue;
						String max = hcdm.getAllOututilhdxExtreme("0", ips[j], weekDay[0][1],weekDay[weekDay.length-1][2],"max");
						String min = hcdm.getAllOututilhdxExtreme("0", ips[j], weekDay[0][1],weekDay[weekDay.length-1][2],"min");
						max = max == null? "0":max;
						min = min == null? "0":min;
						CompreReportStatic crsout = new CompreReportStatic();
						crsout.setIp(ips[j]);
						crsout.setType("oututilhdx");
						crsout.setUnit("KB/s");
						crsout.setValue(Double.valueOf(outvalue));
						crsout.setMax(Double.valueOf(max));
						crsout.setMin(Double.valueOf(min));
						crsout.setWeekValues(weekValues);
						outTemp.add(crsout);
					}
					outList = compareTop(outTemp,10);//出口 top10
				}
			}
		}
		netValueMap.put("cpu", cpuList);
		netValueMap.put("mem", memList);
		netValueMap.put("ping", pingList);
		netValueMap.put("response", responseList);
		netValueMap.put("utilIn", inList);
		netValueMap.put("utilOut", outList);
		
		return netValueMap;
	}
	public Map<String, Integer> getWeekAlarmData(String[][] weekDay,String business){
		DBManager conn = new DBManager();;
		ResultSet rs = null;
		business = business==null?"":" and nodeid in ("+business+")";
		Map<String, Integer> map=new TreeMap<String, Integer>();
		for(int i=0;i<weekDay.length;i++){
			map.put(weekDay[i][0], 0);
		}
		try
	     {
	    	 rs = conn.executeQuery("select Date(recordtime) as w,count(1) as cnt from system_eventlist " +
	    			 "where recordtime between '" + weekDay[0][1] +"' and '"+ weekDay[weekDay.length-1][2] +
	    	 				"'"+business+" group by w;");
	         while(rs.next())
	        	 map.put(rs.getString("w"), rs.getInt("cnt"));
	     }
	     catch(Exception e)
	     {
	         SysLogger.error("CompreReportHelper:",e);
	     }
	     finally{
	    	 try {
				rs.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			conn.close();
	     }
		return map;
	}
	public HashMap getProValue(String startTime,String toTime,String business){
		HashMap hostValueMap=new HashMap();
		HomeCollectDataManager hcdm = new HomeCollectDataManager();
		List cpuList = new ArrayList();				//cpu
		List memList = new ArrayList();				//内存
		List cpuTimeList = new ArrayList();			//cpu用时
		HostNodeDao hostDao = new HostNodeDao();
		List hostList = new ArrayList();
		if(business==null){
			hostList = hostDao.loadAllHostAndNet("");
		}else{
			hostList = hostDao.loadAllHostAndNet(" and id in ("+business+")");
		}
		hostDao.close();
		
		if(hostList == null && hostList.size() == 0){
			return null;
		}
		String[] ips = new String[hostList.size()];
		for(int i=0;i<hostList.size();i++){
			ips[i] = ((HostNode)hostList.get(i)).getIpAddress();
		}
		
		List<CompreReportStatic> cpuTemp = new ArrayList<CompreReportStatic>();
		List<CompreReportStatic> memTemp = new ArrayList<CompreReportStatic>();
		List<CompreReportStatic> cpuTimeTemp = new ArrayList<CompreReportStatic>();
		//查出所有IP的cpu信息
		for(int j=0;j<ips.length;j++){
			//cpu
			List<String[]> cpulist = hcdm.getProCpuAvg("0", ips[j], startTime, toTime);
			for(int g = 0; g< cpulist.size(); g++){
				String[] cpus = cpulist.get(g);
				String cpuvalue = cpus[cpus.length-1];
				cpuvalue = cpuvalue == null? "0":cpuvalue;
				CompreReportStatic crscpu = new CompreReportStatic();
				crscpu.setIp(ips[j]);
				crscpu.setType(cpus[0]);
				crscpu.setUnit("%");
				crscpu.setValue(Double.valueOf(cpuvalue));
				cpuTemp.add(crscpu);
			}
		}
		cpuList = compareTop(cpuTemp,10);//CPU top10
		//查出所有IP的mem信息
		for(int j=0;j<ips.length;j++){
			//内存
			List<String[]> memlist = hcdm.getProMemoryAvg("0", ips[j], startTime, toTime);
			for(int g = 0; g< memlist.size(); g++){
				String[] mems = memlist.get(g);
				String memvalue = mems[mems.length-1];
				memvalue = memvalue == null? "0":memvalue;
				CompreReportStatic crsmem = new CompreReportStatic();
				crsmem.setIp(ips[j]);
				crsmem.setType(mems[0]);
				crsmem.setUnit("%");
				crsmem.setValue(Double.valueOf(memvalue));
				memTemp.add(crsmem);
			}
		}
		memList = compareTop(memTemp,10);//内存 top10
		//查出所有IP的用时
		for(int j=0;j<ips.length;j++){
			//内存
			List<String[]> timelist = hcdm.getProTimeAvg("0", ips[j], startTime, toTime);
			for(int g = 0; g< timelist.size(); g++){
				String[] times = timelist.get(g);
				String timevalue = times[times.length-1];
				timevalue = timevalue == null? "0":timevalue;
				CompreReportStatic crstime = new CompreReportStatic();
				crstime.setIp(ips[j]);
				crstime.setType(times[0]);
				crstime.setUnit("秒");
				crstime.setValue(Double.valueOf(timevalue));
				cpuTimeTemp.add(crstime);
			}
		}
		cpuTimeList = compareTop(cpuTimeTemp,10);//内存 top10
		
		hostValueMap.put("cpu", cpuList);
		hostValueMap.put("mem", memList);
		hostValueMap.put("time", cpuTimeList);
		return hostValueMap;
	}
	
	public HashMap getProWeekValue(String[][] weekDay,String business){
		HashMap hostValueMap=new HashMap();
		HomeCollectDataManager hcdm = new HomeCollectDataManager();
		List cpuList = new ArrayList();				//cpu
		List memList = new ArrayList();				//内存
		List cpuTimeList = new ArrayList();			//cpu用时
		HostNodeDao hostDao = new HostNodeDao();
		List hostList = new ArrayList();
		if(business==null){
			hostList = hostDao.loadAllHostAndNet("");
		}else{
			hostList = hostDao.loadAllHostAndNet(" and id in ("+business+")");
		}
		hostDao.close();
		
		if(hostList == null && hostList.size() == 0){
			return null;
		}
		String[] ips = new String[hostList.size()];
		for(int i=0;i<hostList.size();i++){
			ips[i] = ((HostNode)hostList.get(i)).getIpAddress();
		}
		
		List<CompreReportStatic> cpuTemp = new ArrayList<CompreReportStatic>();
		List<CompreReportStatic> memTemp = new ArrayList<CompreReportStatic>();
		List<CompreReportStatic> cpuTimeTemp = new ArrayList<CompreReportStatic>();
		//查出所有IP的cpu信息
		for(int j=0;j<ips.length;j++){
			//cpu
			List<String[]> cpulist = hcdm.getProCpuAvg("0", ips[j], weekDay[0][1], weekDay[weekDay.length-1][2]);
			for(int g = 0; g< cpulist.size(); g++){
				String[] cpus = cpulist.get(g);
				String cpuvalue = cpus[cpus.length-1];
				cpuvalue = cpuvalue == null? "0":cpuvalue;
				CompreReportStatic crscpu = new CompreReportStatic();
				crscpu.setIp(ips[j]);
				crscpu.setType(cpus[0]);
				Map<String,Double> weekValues = new TreeMap<String,Double>();
				for(int n=0;n<weekDay.length;n++){
					for(int f=0;f<24;f++){
						if(f/10<1){
							weekValues.put(weekDay[n][0]+"-0"+f, 0d);
						}else{
							weekValues.put(weekDay[n][0]+"-"+f, 0d);
						}
					}
				}
				weekValues = hcdm.getProCpuValueHour("0",ips[j],weekDay[0][1],weekDay[weekDay.length-1][2],cpus[0],weekValues);
				crscpu.setUnit("%");
				crscpu.setValue(Double.valueOf(cpuvalue));
				crscpu.setWeekValues(weekValues);
				cpuTemp.add(crscpu);
			}
		}
		cpuList = compareTop(cpuTemp,10);//CPU top10
		//查出所有IP的mem信息
		for(int j=0;j<ips.length;j++){
			//内存
			List<String[]> memlist = hcdm.getProMemoryAvg("0", ips[j], weekDay[0][1], weekDay[weekDay.length-1][2]);
			for(int g = 0; g< memlist.size(); g++){
				String[] mems = memlist.get(g);
				String memvalue = mems[mems.length-1];
				memvalue = memvalue == null? "0":memvalue;
				CompreReportStatic crsmem = new CompreReportStatic();
				crsmem.setIp(ips[j]);
				crsmem.setType(mems[0]);
				Map<String,Double> weekValues = new TreeMap<String,Double>();
				for(int n=0;n<weekDay.length;n++){
					for(int f=0;f<24;f++){
						if(f/10<1){
							weekValues.put(weekDay[n][0]+"-0"+f, 0d);
						}else{
							weekValues.put(weekDay[n][0]+"-"+f, 0d);
						}
					}
				}
				weekValues = hcdm.getProMemoryValueHour("0",ips[j],weekDay[0][1],weekDay[weekDay.length-1][2],mems[0],weekValues);
				crsmem.setUnit("%");
				crsmem.setValue(Double.valueOf(memvalue));
				crsmem.setWeekValues(weekValues);
				memTemp.add(crsmem);
			}
		}
		memList = compareTop(memTemp,10);//内存 top10
		//查出所有IP的用时
		for(int j=0;j<ips.length;j++){
			//内存
			List<String[]> timelist = hcdm.getProTimeAvg("0", ips[j], weekDay[0][1], weekDay[weekDay.length-1][2]);
			for(int g = 0; g< timelist.size(); g++){
				String[] times = timelist.get(g);
				String timevalue = times[times.length-1];
				timevalue = timevalue == null? "0":timevalue;
				CompreReportStatic crstime = new CompreReportStatic();
				crstime.setIp(ips[j]);
				crstime.setType(times[0]);
				Map<String,Double> weekValues = new TreeMap<String,Double>();
				for(int n=0;n<weekDay.length;n++){
					for(int f=0;f<24;f++){
						if(f/10<1){
							weekValues.put(weekDay[n][0]+"-0"+f, 0d);
						}else{
							weekValues.put(weekDay[n][0]+"-"+f, 0d);
						}
					}
				}
				weekValues = hcdm.getProTimeValueHour("0",ips[j],weekDay[0][1],weekDay[weekDay.length-1][2],times[0],weekValues);
				crstime.setUnit("秒");
				crstime.setValue(Double.valueOf(timevalue));
				crstime.setWeekValues(weekValues);
				cpuTimeTemp.add(crstime);
			}
		}
		cpuTimeList = compareTop(cpuTimeTemp,10);//内存 top10
		
		hostValueMap.put("cpu", cpuList);
		hostValueMap.put("mem", memList);
		hostValueMap.put("time", cpuTimeList);
		return hostValueMap;
	}
	public HashMap getHostOrNetTopValue(int x, String startTime,String toTime,String business){
		HashMap topValueMap = new HashMap();
		HomeCollectDataManager hcdm = new HomeCollectDataManager();
		List cpuList = new ArrayList();				//cpu
		List memList = new ArrayList();				//内存
		List cpuTimeList = new ArrayList();			//cpu用时
		HostNodeDao hostDao = new HostNodeDao();
		List hostList = new ArrayList();
		if(business==null){
			hostList = hostDao.loadHostOrNet(x);
		}else{
			hostList = hostDao.loadHostOrNet(x," and id in ("+business+")");
		}
		hostDao.close();
		
		if(hostList == null && hostList.size() == 0){
			return null;
		}
		String[] ips = new String[hostList.size()];
		for(int i=0;i<hostList.size();i++){
			ips[i] = ((HostNode)hostList.get(i)).getIpAddress();
		}
		
		List<CompreReportStatic> cpuTemp = new ArrayList<CompreReportStatic>();
		List<CompreReportStatic> memTemp = new ArrayList<CompreReportStatic>();
		List<CompreReportStatic> cpuTimeTemp = new ArrayList<CompreReportStatic>();
		//查出所有IP的cpu峰值
		for(int j=0;j<ips.length;j++){
			//cpu
			String[] cpuTopValue = hcdm.getCpuMax("0", ips[j], startTime, toTime);
			String cpuvalue = cpuTopValue[0];
			cpuvalue = cpuvalue == null? "0":cpuvalue;
			CompreReportStatic crscpu = new CompreReportStatic();
			crscpu.setIp(ips[j]);
			crscpu.setType(cpuTopValue[1]);
			crscpu.setUnit(cpuTopValue[2]);
			crscpu.setValue(Double.valueOf(cpuvalue));
			cpuTemp.add(crscpu);
		}
		cpuList = compareTop(cpuTemp,10);//CPU top10
		//查出所有IP的响应时间峰值
		for(int j=0;j<ips.length;j++){
			//响应时间
			String[] resTopValue = hcdm.getResponseMax("0", ips[j], startTime, toTime);
			String resvalue = resTopValue[0];
			resvalue = resvalue == null? "0":resvalue;
			CompreReportStatic crsres = new CompreReportStatic();
			crsres.setIp(ips[j]);
			crsres.setType(resTopValue[1]);
			crsres.setUnit(resTopValue[2]);
			crsres.setValue(Double.valueOf(resvalue));
			memTemp.add(crsres);
		}
		memList = compareTop(memTemp,10);//内存 top10		
		
		topValueMap.put("cpu", cpuList);
		topValueMap.put("res", memList);
		return topValueMap;
	}
	public HashMap getTopValue(String ids,String startTime,String toTime,String business){
		HashMap topValueMap = new HashMap();
		HashMap hostValueMap = new HashMap();
		HashMap netValueMap = new HashMap();
		HomeCollectDataManager hcdm = new HomeCollectDataManager();
		List cpuList = new ArrayList();				//cpu
		List memList = new ArrayList();				//内存
		List cpuTimeList = new ArrayList();			//cpu用时
		HostNodeDao hostDao = new HostNodeDao();
		List hostList = new ArrayList();
		
		String[] idValue=this.getIdValue(ids);
		String[] netIds = this.getIdsValues(idValue, "net|");
		String[] hostIds = this.getIdsValues(idValue, "host|");
		
		if (netIds!=null&&netIds.length>0) {
			netValueMap = getHostOrNetTopValue(1,startTime,toTime,business);
			topValueMap.put("net", netValueMap);
		}
		if(hostIds!=null&&hostIds.length>0){
			hostValueMap = getHostOrNetTopValue(2,startTime,toTime,business);
			topValueMap.put("host", hostValueMap);
		}
		return topValueMap;
	}
}
