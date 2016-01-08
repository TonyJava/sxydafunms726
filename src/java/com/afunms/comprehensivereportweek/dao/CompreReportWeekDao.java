package com.afunms.comprehensivereportweek.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.afunms.capreport.model.UtilReport;
import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.util.SysLogger;
import com.afunms.comprehensivereportweek.model.CompreReportWeekInfo;

/**
 * 
 * 综合周报表dao
 * @author jhl
 *
 */
public class CompreReportWeekDao extends BaseDao implements DaoInterface{

	public CompreReportWeekDao(){
		super("nms_compreReportWeek_resources");
	}

	@Override
	public BaseVo loadFromRS(ResultSet rs) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean save(BaseVo vo) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean update(BaseVo vo) {
		// TODO Auto-generated method stub
		return false;
	}
	/**
	 * 查询选中的id ip数据
	 */
	public void queryCompreDate(String id){
		
		
		String sql ="select * from topo_host_node where id="+id;
		
	}
	public String[][] getDevTableData(String type){

		String typeStr = type.equals("host")?"服务器":"网络设备";
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
				"from system_eventlist s , topo_host_node  t " +
				"where to_days(s.recordtime) = to_days('2012-04-13') and s.nodeid = t.id and s.subtype = '" + type + "' group by s.nodeid;");
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
	 public CompreReportWeekInfo findByBid(String id){
		 CompreReportWeekInfo vo=new CompreReportWeekInfo();
	     try{
	         rs = conn.executeQuery("select * from nms_comprereportweek_resources where id="+id);
	         while(rs.next())
	        	vo=(CompreReportWeekInfo) loadFromIDRS(rs);
	     }catch(Exception e){
	         SysLogger.error("BusinessNodeDao:findByBid()",e);
	     }finally{
	    	 if (rs!=null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
	    	 if (conn!=null) {
	    		 conn.close();
			}
	     }
	     return vo;
	  } 
	 public CompreReportWeekInfo loadFromIDRS(ResultSet rs){
		 CompreReportWeekInfo vo = new CompreReportWeekInfo();
			try{


//				private String ;//主机设备TOP趋势分析
//				private String ; //网络设备TOP趋势分析
////			private String reportContentOption;//报表内容选择 （串）

				vo.setId(rs.getInt("id"));
				vo.setReportName(rs.getString("reportName"));
				vo.setReportType(rs.getString("reportType"));
				vo.setIds(rs.getString("ids"));
				vo.setSendOtherDay(rs.getInt("sendOtherDay"));
				vo.setSendTime(rs.getString("sendTime"));
				vo.setHostAlarmCount(rs.getString("hostAlarmCount"));
				vo.setNetAlarmCount(rs.getString("netAlarmCount"));
				vo.setEventAnalyze(rs.getString("eventAnalyze"));
				vo.setEventCount(rs.getString("eventCount"));
				vo.setHostTopAnalyze(rs.getString("hostTopAnalyze"));
				vo.setNetTopAnalyze(rs.getString("netTopAnalyze"));
				vo.setReportUserEmail(rs.getString("reportUserEmail"));
				vo.setSendWeek(rs.getShort("sendWeek"));
				vo.setReportUserName(rs.getString("reportUserName"));
				vo.setAttachmentBusiness(rs.getString("attachmentBusiness"));
				vo.setEmailTitle(rs.getString("emailTitle"));
				vo.setSendOtherDay(rs.getInt("sendOtherDay"));
			}catch(Exception e){
				e.printStackTrace();
			}
			return vo;
		}
	}
