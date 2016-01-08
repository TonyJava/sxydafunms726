package com.afunms.comprehensivereport.util;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.afunms.common.util.DBManager;
import com.afunms.common.util.SysLogger;

public class CompreReportDataCreate {
	private DBManager conn;
	private ResultSet rs;
	public List getDayAlarmDataForList(String dateFrom, String dateTo,String business){
		conn = new DBManager();
		List list = new ArrayList();
		business = business==null?"":" and nodeid in ("+business+")";
		Map<Integer, Integer> map=new TreeMap<Integer, Integer>();
		for(int i=0;i<24;i++){
			map.put(i, 0);
		}
		try
	     {
	    	 rs = conn.executeQuery("select HOUR(recordtime)  as h,count(1) as cnt from system_eventlist " +
	    	 		"where recordtime between '"+dateFrom+"' and '"+dateTo+"' "+business+" group by h;");
	         while(rs.next())
	        	 map.put(rs.getInt("h"), rs.getInt("cnt"));
	     }
	     catch(Exception e)
	     {
	         SysLogger.error("CompreReportDataCreate:",e);
	     }finally{
	    	 conn.close();
	     }
	     
	     for(int i=0;i<24;i++){
	    	 CompreReportStatic crs = new CompreReportStatic();
	    	 crs.setIp(i+"");
	    	 crs.setValue(map.get(i));
	    	 crs.setType("dayhour");
	    	 list.add(crs);
		}
	     return list;
	}
	public List getWeekAlarmData(String[][] weekDay){
		DBManager conn = new DBManager();;
		List list = new ArrayList();
		ResultSet rs = null;
		Map<String, Integer> map=new TreeMap<String, Integer>();
		for(int i=0;i<weekDay.length;i++){
			map.put(weekDay[i][0], 0);
		}
		try
	     {
	    	 rs = conn.executeQuery("select Date(recordtime) as w,count(1) as cnt from system_eventlist " +
	    			 "where recordtime between '" + weekDay[0][1] +"' and '"+ weekDay[weekDay.length-1][2] +
	    	 				"' group by w;");
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
	     for(int i=0;i<7;i++){
	    	 CompreReportStatic crs = new CompreReportStatic();
	    	 crs.setIp(weekDay[i][0]);
	    	 crs.setValue(map.get(weekDay[i][0]));
	    	 crs.setType("week");
	    	 list.add(crs);
		}
		return list;
	}
	
	public String[][] getDevTableData(String type, String dateFrom, String dateTo,String business){
		conn = new DBManager();
		String typeStr = type.equals("host")?"服务器":"网络设备";
		business = business==null?"":" and t.id in ("+business+")";
		String[] dataStr = new String[] {typeStr,"IP地址","设备名称","操作系统","事件总数","普通","严重","紧急","连通率事件","内存事件","磁盘事件","CPU事件"};
		List<String[]> list = new ArrayList<String[]>();
		list.add(dataStr);
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
				"from system_eventlist s , topo_host_node t " +
				"where recordtime between '"+dateFrom+"' and '"+dateTo+
				"' and s.nodeid = t.id and s.subtype = '" + type + "' "+business+" group by s.nodeid;");
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
			SysLogger.error("getDevTableData:",e);
		}finally{
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
	public String[][] gettableData(String dateFrom, String dateTo,String business){
		conn = new DBManager();
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
			sb.append(" where recordtime between '"+dateFrom+"' and '"+dateTo);
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
	         SysLogger.error("CompreReportDataCreate:",e);
	     } finally{
				conn.close();
			}
		return dataStr;
	}
	public  Map<String, String> getLevelPieDataForMap(String dateFrom, String dateTo,String business){
		conn = new DBManager();
		business = business==null?"":" and nodeid in ("+business+")";
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
				sb.append("where recordtime between '"+dateFrom+"' and '"+dateTo);
				sb.append("' "+business+" group by (level1) ; ");
				rs = conn.executeQuery(sb.toString());
	         while(rs.next())
	        	 map.put(rs.getString("sign"), rs.getString("cnt"));
	     }
	     catch(Exception e)
	     {
	         SysLogger.error("CompreReportDataCreate:",e);
	     }finally{
	    	 conn.close();
	     }
	     return map;
	}
}
