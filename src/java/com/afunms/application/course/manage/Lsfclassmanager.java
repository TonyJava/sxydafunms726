package com.afunms.application.course.manage;

import java.util.List;

import com.afunms.common.base.BaseManager;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.ErrorMessage;
import com.afunms.common.base.ManagerInterface;
import com.afunms.application.course.dao.Lsfclassdao;
import com.afunms.application.course.model.Lsfclass;
//import com.afunms.config.model.Macconfig;

/**
 * 对于agent进行配置
 * @author LiNan
 *
 */




public class Lsfclassmanager extends BaseManager implements ManagerInterface {
	public String execute(String action) {
		if (action.equals("list")) {
			return list();
		}
		if (action.equals("ready_add")) {
			return "/application/course/add.jsp";
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
		setErrorCode(ErrorMessage.ACTION_NO_FOUND);
		return null;
	}
/**
 * 在页面中进行列表展示
 * @return
 */
	public String list() {
		Lsfclassdao dao = new Lsfclassdao();
		setTarget("/application/course/list.jsp");
		return list(dao);
	}
/**
 * 添加新的agent
 * @return
 */
	public String add() {
		Lsfclass vo = new Lsfclass();
		Lsfclassdao dao = new Lsfclassdao();

		
		
		vo.setClass_name(getParaValue("class_name"));
		vo.setClass_pesc(getParaValue("class_pesc"));

		try {
			dao.save(vo);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}
		return "/lsfprocess.do?action=list";
	}
/**
 * 对agent进行修改
 * 跳转到修改页面
 * @return
 */
	public String ready_edit() {
		Lsfclassdao dao = new Lsfclassdao();
		
		//System.out.println("1111111111111================+dao==" + getParaValue("class_id"));
		Lsfclass vo = null;
		try {
			vo = dao.lsffindid(getParaValue("class_id"));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}
		request.setAttribute("vo", vo);
		return "/application/course/edit.jsp";
	}
/**
 * 对agent修改
 * @return
 */
	public String update() {
		Lsfclass vo = new Lsfclass();
		vo.setClass_id(getParaIntValue("class_id"));
		vo.setClass_name(getParaValue("class_name"));
		vo.setClass_pesc(getParaValue("class_pesc"));
		Lsfclassdao dao = new Lsfclassdao();
		try {
			dao.update(vo);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}
		return "/lsfprocess.do?action=list";
	}
	/**
	 * 批量删除agent
	 * @return
	 */
	public String delete(){
		
		
	    System.out.println("=================deltet===");
		String[] ids = getParaArrayValue("checkbox");
		Lsfclassdao dao = new Lsfclassdao();
		try
		{
			if(ids!=null && ids.length>0){
				dao.lsfdelete(ids);
			}
			
		}catch(Exception e)
		{
			e.printStackTrace();
		}finally
		{
			dao.close();
		}
			
        return "/lsfprocess.do?action=list"; 
	}
}
