/**
 * <p>Description:与nodedao都是操作表nms_topo_node,但nodedao主要用于发现</p>
 * <p>Description:而toponodedao主要用于页面操作</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-09-20
 */

package com.afunms.application.course.dao;


import java.sql.ResultSet;

import java.util.List;


import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.util.SysLogger;

import com.afunms.application.course.model.LsfHostNode;

public class LsfTopoHostNodeDao extends BaseDao implements DaoInterface
{
   public LsfTopoHostNodeDao()
   {
	   super("topo_host_node");
   }
   
   public List lsfHostOrNet()
   {
	   
		   return findByCriteria("select id,ip_address,alias ,`type`,sys_descr from topo_host_node where category='4' and id not in(select nodeid from lsf_class_node)"); 
	   
	   
   }


   public BaseVo loadFromRS(ResultSet rs) {
		// TODO Auto-generated method stub
	   LsfHostNode vo = new LsfHostNode();
	      try
	      {
	         vo.setId(rs.getInt("id"));
	         vo.setIp_address(rs.getString("ip_address"));
	         vo.setAlias(rs.getString("alias"));
	         vo.setType(rs.getString("type"));
	         vo.setSys_descr(rs.getString("sys_descr"));
	       
	      }
	      catch(Exception ex)
	      {
	          SysLogger.error("Error in UserDAO.loadFromRS()",ex);
	          vo = null;
	      }
	      return vo;
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
