package com.gathertask.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;


import org.apache.log4j.Logger;

import com.afunms.common.base.BaseVo;
import com.afunms.common.util.SysLogger;
import com.afunms.indicators.model.NodeGatherIndicators;
import com.afunms.system.model.ResourceConfNode;
import com.database.DBManager;
import com.database.config.SystemConfig;

public class TaskResDao {

	Logger logger = Logger.getLogger(TaskResDao.class);

	/**
	 * 收索维护配置状态为活动并且结束时间已超过当前时间 0：活动 1：结束
	 */
//	public Hashtable queryEndState() {
//		Hashtable _map = new Hashtable();
//		
//		ResultSet rs = null;
//		DBManager conn = new DBManager();
//		String sql = "select n.nodeid from resourceconf f ,resourcenode n where f.enddate <= '" +
//						getEndTime() +
//				"' and f.id=n.resourceconfid and confstatre ='0'";
//		try {
//			rs = conn.executeQuery(sql);
//			if (rs == null)
//				return null;
//			while (rs.next())
//				_map.put(rs.getInt("nodeid"), rs.getInt("nodeid"));
//		} catch (Exception e) {
//			e.printStackTrace();
//			SysLogger.error("TaskResDao.findByid()", e);
//		} finally {
//			if (rs != null) {
//				try {
//					rs.close();
//				} catch (SQLException e) {
//					e.printStackTrace();
//				}
//			}
//			conn.close();
//		}
//		return _map;
//	}
	
	
	/**
	 * 查询是否有维护日期到时维护任务
	 */
	public boolean queryRes(){
		boolean flag = false;
		String sql ="select r.id from resourceconf r where r.enddate <='" +
						getEndTime()
						+"' and r.confstatre=0";
		DBManager conn = new DBManager();
		ResultSet rs = null;
		try {
			conn.executeQuery(sql);
			flag = true;
		} catch (Exception e) {
			e.printStackTrace();
			SysLogger.error("TaskResDao.queryRes()", e);
			flag = false;
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			conn.close();
		}
		return flag;
	}
	/**
	 * 更新维护配置状态 0：活动 1：结束
	 */
	public boolean updateState() {
		boolean result = false;
		DBManager conn = new DBManager();
		String sql = "update resourceconf f inner join (select id from resourceconf where confstatre=0)c on f.enddate < '"+
				getEndTime()
				+"' set f.confstatre = 1;";
		try {
			conn.executeUpdate(sql,false);
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
			SysLogger.error("TaskResDao.findByid()", e);
			result = false;
		} finally {
				try {
					conn.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			
		}
		return result;
	}

	/**
	 * 得到开始时间与结束时间之间并且状态是0的nodeid
	 */
	public Hashtable queryResConf() {
		Hashtable map = new Hashtable();
//		List list = new ArrayList();
		ResultSet rs = null;
		DBManager conn = new DBManager();
		String sql = "select node.id from resourceconf f ,resourcenode n ,nms_gather_indicators_node node where '" 
						+getNowTime()+
						"' between f.startdate and f.enddate and f.id=n.resourceconfid and confstatre ='0' and node.nodeid=n.nodeid";
		 try {
			 
			 
			map= conn.executeQuerykeyoneListHashMap(sql,"id");
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
			SysLogger.error("TaskResDao.queryResConf()", e);
		}
		return map;
	}


	public BaseVo loadFromRS(ResultSet rs) {
		ResourceConfNode vo = new ResourceConfNode();
		try {
			vo.setNodeid(rs.getInt("nodeid"));

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return vo;
	}
	
	
	public String getNowTime() {
		String nowDate = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		nowDate = sdf.format(new Date());
		return nowDate;
	}

	public static void main(String[] args) {
		TaskResDao taskResDao = new TaskResDao();
		ResourceConfNode vo = new ResourceConfNode();
		Hashtable _map = taskResDao.queryResConf();
	}

	public String getEndTime() {
		String endDate = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		endDate = sdf.format(new Date());
		endDate = endDate + " 23:59:59";
		// request.setAttribute("startdate", startDate);
		return endDate;
	}
}
