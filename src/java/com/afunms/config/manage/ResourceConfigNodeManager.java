package com.afunms.config.manage;

import java.util.List;

import com.afunms.common.base.BaseManager;
import com.afunms.common.base.ErrorMessage;
import com.afunms.common.base.ManagerInterface;
import com.afunms.config.dao.AgentNodeDao;
import com.afunms.config.dao.ResourceConfigNodeDao;
import com.afunms.topology.dao.HostNodeDao;

/**
 * 对于某个维护配置的添加
 * @author jhl
 *
 */

public class ResourceConfigNodeManager extends BaseManager implements ManagerInterface {

	public String execute(String action) {

		if (action.equals("list")) {
			return list();
		}
		if (action.equals("add")) {
			return add();
		}
		if(action.equals("save")){
			return save();
		}
		if(action.equals("delete")){
			return delete();
		}
		setErrorCode(ErrorMessage.ACTION_NO_FOUND);
		return null;
	}
/**
 *将已经添加好的设备节点进行页面展示
 */
	public String list() {
		int resourceid = getParaIntValue("id");
		String jsp = "/system/resourceAllocationNode/list.jsp";
		List list = null;
		ResourceConfigNodeDao dao = new ResourceConfigNodeDao();
		try {
			list = dao.findbyid(resourceid);
		} catch (RuntimeException e) {
			e.printStackTrace();
		}

		request.setAttribute("list", list);
		request.setAttribute("agentid", resourceid);

		return jsp;
	}

/**
 * 跳转到尚未分配设备节点页面
 * @return
 */	
	public String add() {
		String jsp = "/system/resourceAllocationNode/add.jsp";
		int agentid=getParaIntValue("agentid");
		List list = null;
 		ResourceConfigNodeDao dao = new ResourceConfigNodeDao();
		try {
			list = dao.findfornode(agentid);
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		request.setAttribute("list", list);
		request.setAttribute("agentid", agentid);
		return jsp;
	}
	
/**
 * 对于选择的设备节点进行批量添加
 * @return
 */
	public String save(){
		int agentid=getParaIntValue("agentid");
		String[] ids=getParaArrayValue("checkbox");
		ResourceConfigNodeDao dao=new ResourceConfigNodeDao();
		try {
			dao.save(ids, agentid);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "/resourceConfigNode.do?action=list";
	}

/**
 * 对于选择的设备节点进行批量删除
 * @return
 */
	public String delete(){
		String[] ids=getParaArrayValue("checkbox");
		ResourceConfigNodeDao dao=new ResourceConfigNodeDao();
		try {
			dao.delete(ids);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "/resourceConfigNode.do?action=list";
	}

}
