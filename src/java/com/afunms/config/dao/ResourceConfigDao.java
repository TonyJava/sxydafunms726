package com.afunms.config.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.util.DBManager;
import com.afunms.common.util.SysLogger;
import com.afunms.config.model.AgentConfig;
import com.afunms.system.model.ResourceConf;

/**
 * 资源维护
 * @author jhl
 *
 */

public class ResourceConfigDao extends BaseDao implements DaoInterface {
	public ResourceConfigDao(){
		super("resourceconf");
	}
/**
 * 将数据转换成model
 */
	public BaseVo loadFromRS(ResultSet rs) {
		ResourceConf vo=new ResourceConf();
		try {
			vo.setId(rs.getInt("id"));
			vo.setConfititle(rs.getString("confititle"));
			vo.setEnddate(rs.getString("enddate"));
			vo.setStartdate(rs.getString("startdate"));
			vo.setLogname(rs.getString("logname"));
			vo.setResourcedesc(rs.getString("confdec"));
			vo.setState(rs.getString("confstatre"));
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return vo;
	}
	/**
	 * 根据resourceconf中的agentID 在resourcenode表中查询到相应的nodeid
	 * 在表topo_host_node中根据ID查询设备相应ip_address和alias显示
	 */
	public List findbyid(int agentid) {
		List list = new ArrayList();
		ResultSet rs = null;
		DBManager conn = new DBManager();
		try {
			rs = conn
					.executeQuery("select a.id,a.ip_address,a.alias,b.agentid from topo_host_node a,nms_node_agent b where a.id=b.nodeid and b.agentid="
							+ agentid + ";");
			if (rs == null)
				return null;
			while (rs.next())
				list.add(loadFromRS(rs));
		} catch (Exception e) {
			e.printStackTrace();
			SysLogger.error("AgentNodeDao.findByid()", e);
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (Exception e) {
				}
			}
			conn.close();
		}
		return list;
	}
/**
 * 将新的agent存入数据库
 */
	public boolean save(BaseVo vo) {
		ResourceConf resourceconfig=(ResourceConf) vo;
		StringBuffer sql = new StringBuffer(100);
		
		sql.append("insert into resourceconf(confititle,logname,startdate,enddate,confdec,confstatre)values(");
		sql.append("'");
		sql.append(resourceconfig.getConfititle());
		sql.append("','");
		sql.append(resourceconfig.getLogname());
		sql.append("','");
		sql.append(getStarttime(resourceconfig.getStartdate()));
		sql.append("','");
		sql.append(getTotime(resourceconfig.getEnddate()));
		sql.append("','");
		sql.append(resourceconfig.getResourcedesc());
		sql.append("','");
		sql.append(resourceconfig.getState());
		sql.append("')");
		
		return saveOrUpdate(sql.toString());
	}
//	private String getTimeSql(String startDate , String toDate){
//		String startTime = getStarttime(startDate);
//		String toTime = getTotime(toDate);
//		StringBuffer sbSql = new StringBuffer();
//		sbSql.append(" recordtime>'" + startTime +"'");
//		sbSql.append(" and recordtime<'" + toTime +"'");
//		return sbSql.toString();
//	}
	public String getTotime(String toDate){
		if(toDate == null){
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			toDate = sdf.format(new Date());
		}
		String totime = toDate + " 23:59:59";
//		request.setAttribute("todate", toDate);
		return totime;
	}
	public String getStarttime(String startDate){
		if(startDate == null){
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			startDate = sdf.format(new Date());
		}
		String starttime = startDate + " 00:00:00";
//		request.setAttribute("startdate", startDate);
		return starttime;
	}
/**
 * 将数据库中的agent进行修改
 */
	public boolean update(BaseVo vo) {
		ResourceConf rconfig=(ResourceConf) vo;
		StringBuffer sql = new StringBuffer(100);
		
		sql.append("update resourceconf set confititle='");
		sql.append(rconfig.getConfititle());
		sql.append("',startdate='");
		sql.append(getStarttime(rconfig.getStartdate()));
		sql.append("',enddate='");
		sql.append(getTotime(rconfig.getEnddate()));
		sql.append("',confdec='");
		sql.append(rconfig.getResourcedesc());
		sql.append("',confstatre='");
		sql.append(rconfig.getState());
		sql.append("'where id='");
		sql.append(rconfig.getId());
		sql.append("'");
		return saveOrUpdate(
				sql.toString());
	}
/**
 * 批量删除配置记录
 * @param resourceConfig
 * @return
 */
	public boolean deleteall(String[] agentid){
		boolean result = false;
		try{
			for(int i=0;i<agentid.length;i++){
			conn.addBatch("delete from resourceconf where id=" + agentid[i]);
			conn.addBatch("delete from resourcenode where resourceconfid=" + agentid[i]);
			conn.executeBatch();
			result = true;
	   }
		}
	   catch(Exception ex){
		    SysLogger.error("ResourceConfigDao.delete()",ex);
	        result = false;
	   }finally{
		    conn.close();
	   }
	   return result;
	}	
}
