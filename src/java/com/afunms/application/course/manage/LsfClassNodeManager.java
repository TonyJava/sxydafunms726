package com.afunms.application.course.manage;

import java.util.List;

import com.afunms.common.base.BaseManager;
import com.afunms.common.base.ErrorMessage;
import com.afunms.common.base.JspPage;
import com.afunms.common.base.ManagerInterface;
import com.afunms.application.course.dao.LsfNmsAlarmIndicatorsNode;
import com.afunms.application.course.dao.LsfTopoHostNodeDao;
import com.afunms.application.course.dao.LsfClassNodeDao;
import com.afunms.application.course.dao.LsfNmsDao;
import com.afunms.application.course.model.LsfClassNode;
//import com.afunms.config.model.Macconfig;

/**
 * 对于agent进行配置
 * @author LiNan
 *
 */




public class LsfClassNodeManager extends BaseManager implements ManagerInterface {
	public String execute(String action) {
		if (action.equals("list")) {
			return list();
		}
		if (action.equals("ready_add")) {
			return "/application/course/nodeadd.jsp";
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
		if (action.equals("netip")) {
			return netip();
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
		LsfClassNodeDao dao = new LsfClassNodeDao();
		setTarget("/application/course/nodelist.jsp");
		return list2(dao);
	}
/**
 * 添加新的agent
 * @return
 */
	public String add() {
		LsfClassNode vo = new LsfClassNode();
		LsfClassNodeDao dao = new LsfClassNodeDao();

		LsfNmsDao nmsdao = new LsfNmsDao();
		
		LsfNmsAlarmIndicatorsNode lsfnmsalarmdao = new LsfNmsAlarmIndicatorsNode();
		vo.setClass_id(Integer.parseInt(getParaValue("classid")));
		vo.setNodeid(Integer.parseInt(getParaValue("nodeid")));
		vo.setEnable(Integer.parseInt(getParaValue("enable")));
		vo.setLogflg(Integer.parseInt(getParaValue("logflg")));
		vo.setJid(Integer.parseInt(getParaValue("jid")));
		//System.out.println("--------------getParaValue('classid)---------------------" + getParaValue("classid"));
		try {
			dao.save(vo);
			lsfnmsalarmdao.save(getParaValue("nodeid"));
			//System.out.println("======enable==="+vo.getEnable());
			
			if(vo.getEnable()==1)
			nmsdao.save(getParaValue("nodeid"));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}
		return "/lsfprocessnode.do?action=list";
	}
/**
 * 对agent进行修改
 * 跳转到修改页面
 * @return
 */
	public String ready_edit() {
		LsfClassNodeDao dao = new LsfClassNodeDao();
		
		//System.out.println("1111111111111================+dao==" + getParaValue("nodeid"));
		LsfClassNode vo = null;
		try {
			vo = dao.lsffindid(getParaValue("nodeid"));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}
		request.setAttribute("vo", vo);
		return "/application/course/nodeedit.jsp";
	}
/**
 * 对agent修改
 * @return
 */
	public String update() {
		LsfClassNode vo = new LsfClassNode();
		
		String classidold=getParaValue("class_id_old");
		String nodeidold=getParaValue("nodeid_old");
		
		
		vo.setClass_id(Integer.parseInt(getParaValue("classid")));
		vo.setNodeid(Integer.parseInt(getParaValue("nodeid")));
		vo.setEnable(Integer.parseInt(getParaValue("enable")));
		vo.setLogflg(Integer.parseInt(getParaValue("logflg")));
		vo.setJid(Integer.parseInt(getParaValue("jid")));
		LsfClassNodeDao dao = new LsfClassNodeDao();
		LsfNmsDao nmsdao=new LsfNmsDao();
		
		try {
			dao.update(vo,classidold,nodeidold);
			
			if(vo.getEnable()==1)
			{
				nmsdao.delete(nodeidold, "lsfprocess", "lsfprocess");
				nmsdao.save(""+vo.getNodeid());
				
			}else
			{
				nmsdao.delete(nodeidold, "lsfprocess", "lsfprocess");
				nmsdao.delete(vo.getNodeid()+"", "lsfprocess", "lsfprocess");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}
		return "/lsfprocessnode.do?action=list";
	}
	
	
	public String netip() {
		
		//System.out.println("=======================netip=============1======================");
		LsfTopoHostNodeDao dao = new LsfTopoHostNodeDao();
		List list = dao.lsfHostOrNet();
		int listsize = list.size();
		request.setAttribute("iplist", list);
		LsfTopoHostNodeDao listdao = new LsfTopoHostNodeDao();
		setTarget("/application/course/lsfnetip.jsp");
		String page = list(listdao);
		JspPage jp = (JspPage) request.getAttribute("page");
		jp.setTotalRecord(listsize);
		request.setAttribute("page", jp);
		//System.out.println("=======================netip=============2======================");
		return page;
	}
	
	
	
	/**
	 * 批量删除agent
	 * @return
	 */
	public String delete(){
		
		
	   // System.out.println("=================deltet===");
		String[] ids = getParaArrayValue("checkbox");
		
		
		try
		{
			if(ids!=null && ids.length>0){
				LsfClassNodeDao dao = new LsfClassNodeDao();
				dao.lsfdelete(ids);
				dao.close();
				LsfNmsDao nmsdao = new LsfNmsDao();
				//nmsdao.nmsdelete(getParaArrayValue("checkbox"));
				
			}
			
		}catch(Exception e)
		{
			e.printStackTrace();
		}
			
        return "/lsfprocessnode.do?action=list"; 
	}
	
	
	
	
	
	   /**
	    * 分页显示记录
	    * targetJsp:目录jsp
	    */
	   protected String list2(LsfClassNodeDao dao)
	   {
		 //System.out.println("=================list2======================123123");
		   String targetJsp = null;
		   int perpage = getPerPagenum();
	       List list = dao.listByPage2(getCurrentPage(),perpage);
	       if(list==null) return null; 
	      // System.out.println("=====================================ttt+list="+list.size());
	       request.setAttribute("page",dao.getPage());
	       request.setAttribute("list",list);
	       targetJsp = getTarget(); 
	       
		   return targetJsp;
	   }
	
}
