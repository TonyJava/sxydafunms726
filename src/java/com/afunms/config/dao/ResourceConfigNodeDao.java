package com.afunms.config.dao;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.util.DBManager;
import com.afunms.common.util.SysLogger;
import com.afunms.config.model.AgentNode;
import com.afunms.system.model.ResourceConfNode;

/**
 * agent配置中添加设备 dao
 * @author jhl
 * 
 */

public class ResourceConfigNodeDao  extends BaseDao implements DaoInterface {
	
	public ResourceConfigNodeDao(){
		super("ResourceConfNode");
	}
	
	public BaseVo loadFromRS(ResultSet rs) {
		ResourceConfNode vo = new ResourceConfNode();
		try {
			vo.setNodeid(rs.getInt("id"));
			vo.setIp_address(rs.getString("ip_address"));
			vo.setAlias(rs.getString("alias"));
		} catch (Exception e) {
			SysLogger.error("ResourceConfigNodeDao.loadFromRS()", e);
		}
		return vo;
	}

	/**
	 * 根据nms_agent_config中的agentID 在nms_node_agent表中查询到相应的nodeid
	 * 在表topo_host_node中根据ID查询设备相应ip_address和alias显示
	 */
	public List findbyid(int resourceId) {
		List list = new ArrayList();
		ResultSet rs = null;
		DBManager conn = new DBManager();
		try {
			rs = conn
					.executeQuery("select hnode.ip_address, hnode.alias,nod.id from topo_host_node hnode ,resourcenode nod where hnode.id=nod.nodeid and nod.resourceconfid='"
							+resourceId+"'");
			if (rs == null)
				return null;
			while (rs.next())
				list.add(loadFromRS(rs));
		} catch (Exception e) {
			e.printStackTrace();
			SysLogger.error("ResourceConfigNodeDao.findByid()", e);
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

	public List findfornode(int agentid) {
		String resourceid = agentid+"";
		List list = new ArrayList();
		ResultSet rs = null;
		DBManager conn = new DBManager();
		try {
			rs = conn
					.executeQuery("select nd.ip_address, nd.alias, nd.id from topo_host_node nd where " +
							"nd.id not in (select rn.nodeid from resourcenode rn where rn.resourceconfid='"+resourceid+"')");
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
	 * linan 对于nms_node_agent
	 * 进行数据批量添加方法
	 */
	public boolean save(String[] id,int agentid){
		boolean result = false;
		DBManager conn=new DBManager();
		try
		   {
		       for(int i=0;i<id.length;i++){
		           conn.addBatch("insert into resourcenode(resourceconfid,nodeid)values('" +agentid+"','" +id[i]+"');");
		       		System.out.println("insert into resourcenode(agentid,nodeid)values('" +agentid+"','" +id[i]+"');");
			       conn.executeBatch();
			       result = true;
		       }
		   }
		   catch(Exception ex)
		   {
		       SysLogger.error("ResourceConfigDao.save()",ex);
		       result = false;
		   }
		
		return result;
	}

	/** 
	 * 对于nms_node_agent
	 * 进行数据批量删除方法
	 */	
	public boolean delete(String[] nodeid){
		DBManager conn=new DBManager();
		boolean result = false;
		try{
			for(int i=0;i<nodeid.length;i++){
			conn.addBatch("delete from resourcenode where id=" + nodeid[i]);
			conn.executeBatch();
			result = true;
	   }
		}
	   catch(Exception ex){
		    SysLogger.error("ResourceConfigNodeDao.delete()",ex);
	        result = false;
	   }finally{
		    conn.close();
	   }
	   return result;
	}

	public boolean save(BaseVo vo) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean update(BaseVo vo) {
		// TODO Auto-generated method stub
		return false;
	}

}
