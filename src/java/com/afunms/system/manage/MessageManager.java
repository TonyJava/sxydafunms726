package com.afunms.system.manage;

import java.util.List;

import com.afunms.common.base.BaseManager;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.ManagerInterface;
import com.afunms.system.dao.MessageDao;
import com.afunms.system.model.Message;
import com.afunms.topology.dao.HostNodeDao;

public class MessageManager extends BaseManager implements ManagerInterface {
	public String execute(String action) {
		if (action.equals("list")) {
			return list();
		}
		if (action.equals("ready_add")) {
			return "/system/message/add.jsp";
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
		if (action.equals("allip")) {
			return allip();
		}
		return null;
	}

	/**
	 * 在页面中进行列表展示
	 * @return
	 */
	public String list() {
		MessageDao dao = new MessageDao();
		setTarget("/system/message/list.jsp");
		return list(dao);
	}

	/**
	 * 添加新的message
	 * @return
	 */
	public String add() {
		Message vo = new Message();
		MessageDao dao = new MessageDao();

		
		vo.setIp(getParaValue("ip"));
		vo.setDevtype(getParaValue("devtype"));
		vo.setBigsys(getParaValue("bigsys"));
		vo.setSmallsys(getParaValue("smallsys"));
		
		try {
			dao.save(vo);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}
		return "/message.do?action=list";
	}

	/**
	 * 对message进行修改
	 * 跳转到修改页面
	 * @return
	 */
	public String ready_edit() {
		MessageDao dao = new MessageDao();
		BaseVo vo = null;
		try {
			vo = dao.findByID(getParaValue("id"));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}
		request.setAttribute("vo", vo);
		return "/system/message/edit.jsp";
	}

	/**
	 * 对message修改
	 * @return
	 */
	public String update() {
		Message vo = new Message();
		vo.setId(getParaIntValue("id"));
		vo.setIp(getParaValue("ip"));
		vo.setDevtype(getParaValue("devtype"));
		vo.setBigsys(getParaValue("bigsys"));
		vo.setSmallsys(getParaValue("smallsys"));
		MessageDao dao = new MessageDao();
		try {
			dao.update(vo);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}
		return "/message.do?action=list";
	}

	/**
	 * 批量删除message
	 * @return
	 */
	public String delete() {
		String[] messageid = getParaArrayValue("checkbox");
		MessageDao dao = new MessageDao();
		try {
			if (messageid != null && messageid.length > 0) {
				dao.deleteall(messageid);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}

		return "/message.do?action=list";
	}
	
	
	private String allip() {
		HostNodeDao dao = new HostNodeDao();
		List list = dao.messageall();
		dao.close();
		request.setAttribute("list", list);
		return "/system/message/allip.jsp";
        }
	
}
