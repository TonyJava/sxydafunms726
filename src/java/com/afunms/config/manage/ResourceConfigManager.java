package com.afunms.config.manage;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.afunms.common.base.BaseManager;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.ErrorMessage;
import com.afunms.common.base.ManagerInterface;
import com.afunms.config.dao.AgentConfigDao;
import com.afunms.config.dao.AgentNodeDao;
import com.afunms.config.dao.MacconfigDao;
import com.afunms.config.dao.ResourceConfigDao;
import com.afunms.config.model.AgentConfig;
import com.afunms.config.model.Macconfig;
import com.afunms.system.model.ResourceConf;

/**
 * 资源维护
 * @author jhl
 */

public class ResourceConfigManager extends BaseManager implements ManagerInterface {
	public String execute(String action) {
		if (action.equals("resourcelist")) {
			return resourcelist();
		}
		if (action.equals("ready_add")) {
			return "/system/resourceAllocation/resourceAdd.jsp";
		}
		if (action.equals("add")) {
			return add();
		}
		if (action.equals("ready_edit")) {
			return ready_edit();
		}
		if (action.equals("update")) {
			return update();
		}
		if (action.equals("delete")) {
			return delete();
		}
		if(action.equals("listNode")){
			return listNode();
		}
		setErrorCode(ErrorMessage.ACTION_NO_FOUND);
		return null;
	}
	/**
	 *将已经添加好的资源进行页面展示
	 */
		public String listNode() {
			int agentid = getParaIntValue("id");
			String jsp = "/config/agentnode/list.jsp";
			List list = null;
			AgentNodeDao dao = new AgentNodeDao();
			try {
				list = dao.findbyid(agentid);
			} catch (RuntimeException e) {
				e.printStackTrace();
			}

			request.setAttribute("list", list);
			request.setAttribute("agentid", agentid);

			return jsp;
		}
/**
 * 在页面中进行列表展示
 * @return
 */
	public String resourcelist() {
		ResourceConfigDao dao = new ResourceConfigDao();
		setTarget("/system/resourceAllocation/list.jsp");
		return list(dao);
	}
/**
 * 添加新的配置
 * @return
 */
	public String add() {
		ResourceConf vo = new ResourceConf();
		ResourceConfigDao dao = new ResourceConfigDao();
		vo.setConfititle(getParaValue("title"));
		vo.setEnddate(getParaValue("todate"));
		vo.setStartdate(getParaValue("startdate"));
		vo.setLogname(getParaValue("maintainer"));
		vo.setResourcedesc(getParaValue("describe"));
		vo.setState(getParaValue("state"));
		try {
			dao.save(vo);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}
		return "/resourceAllocation.do?action=resourcelist";
	}
/**
 * 对agent进行修改
 * 跳转到修改页面
 * @return
 */
	public String ready_edit() {
		ResourceConfigDao dao = new ResourceConfigDao();
		BaseVo vo = null;
		try {
			vo = dao.findByID(getParaValue("id"));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}
		request.setAttribute("vo", vo);
		return "/system/resourceAllocation/edit.jsp";
	}
/**
 * 对agent修改
 * @return
 */
	public String update() {
		
		ResourceConf vo = new ResourceConf();
		vo.setId(getParaIntValue("id"));
		vo.setConfititle(getParaValue("title"));
		vo.setResourcedesc(getParaValue("des"));
		vo.setStartdate(getParaValue("startdate").toString());
		vo.setEnddate(getParaValue("todate").toString());
		vo.setState(getParaValue("state"));
		
		ResourceConfigDao dao = new ResourceConfigDao();
		try {
			dao.update(vo);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}
		return "/resourceAllocation.do?action=resourcelist";
	}
	/**
	 * 批量删除agent
	 * @return
	 */
	public String delete(){
		String[] ids = getParaArrayValue("checkbox");
		ResourceConfigDao dao = new ResourceConfigDao();
		try
		{
			if(ids!=null && ids.length>0){
				dao.deleteall(ids);
			}
			
		}catch(Exception e)
		{
			e.printStackTrace();
		}finally
		{
			dao.close();
		}
			
        return "/resourceAllocation.do?action=resourcelist"; 
	}
}
