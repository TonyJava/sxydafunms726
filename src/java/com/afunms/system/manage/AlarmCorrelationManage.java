package com.afunms.system.manage;

import java.util.List;

import com.afunms.application.course.dao.LsfTopoHostNodeDao;
import com.afunms.common.base.BaseManager;
import com.afunms.common.base.ErrorMessage;
import com.afunms.common.base.JspPage;
import com.afunms.common.base.ManagerInterface;
import com.afunms.system.dao.AlarmCorrelationDao;
import com.afunms.system.vo.AlarmCorrelationVo;
//import com.afunms.config.model.Macconfig;

/**
 * 对于agent进行配置
 * @author LiNan
 *
 */
public class AlarmCorrelationManage extends BaseManager implements ManagerInterface {
	public String execute(String action) {
		if (action.equals("list")) {
			return list();
		}
		if (action.equals("add")) {
			return add();
		}
		if (action.equals("delete")) {
			return delete();
		}
		if (action.equals("fadd")) {
			return fadd();
		}
		if (action.equals("save")) {
			return save();
		}
		setErrorCode(ErrorMessage.ACTION_NO_FOUND);
		return null;
	}
/**
 * 在页面中进行列表展示
 * @return
 */
	public String list() {
		AlarmCorrelationDao dao = new AlarmCorrelationDao();
		setTarget("/system/alarmcorrelation/list.jsp");
		return list2(dao);
	}
/**
 * 添加新的agent
 * @return
 */
	public String save() {
		String id2= (String)request.getSession().getAttribute("son_id");
		AlarmCorrelationVo vo = new AlarmCorrelationVo();
		AlarmCorrelationDao dao = new AlarmCorrelationDao();
		vo.setFathernode(Integer.parseInt(getParaValue("id")));
		vo.setLocanode(Integer.parseInt(id2));
		try {
			dao.save(vo);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
			//request.getSession().removeAttribute("son_id");
		}
		return "/alarmcorrelation.do?action=list";
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
				AlarmCorrelationDao dao = new AlarmCorrelationDao();
				dao.alarmcorrelationdelete(ids);
				dao.close();			
			}
			
		}catch(Exception e)
		{
			e.printStackTrace();
		}
			
        return "/alarmcorrelation.do?action=list"; 
	}
	
	public String add() {
		
		//System.out.println("=======================netip=============1======================");
		//AlarmCorrelationDao dao = new AlarmCorrelationDao();
		//List list = dao.alarmHostOrNet();
		//System.out.println("==list=="+list);
		//request.setAttribute("iplist", list);
		//AlarmCorrelationDao listdao = new AlarmCorrelationDao();
		//setTarget("system/alarmcorrelation/netip.jsp");
		//String page = listnodeip(listdao);
		//JspPage jp = (JspPage) request.getAttribute("page");
		//request.setAttribute("page", jp);
		//System.out.println("=======================netip=============2======================");
		//return listnodeip(listdao);
		AlarmCorrelationDao dao = new AlarmCorrelationDao();
		setTarget("/system/alarmcorrelation/add.jsp");
		return listnodeip(dao);
	}
	public String fadd() {
		 String id = (String)request.getParameter("id");
		 request.getSession().setAttribute("son_id", id);
		 //String id_s= (String)request.getSession().getAttribute("id");
		 
		 //System.out.println("===========+++++++"+id_s);
		 
		 ///String id_s=(String) request.getAttribute("id_s");
		 
		 String category =  request.getParameter("category");
		AlarmCorrelationDao dao = new AlarmCorrelationDao();
		setTarget("/system/alarmcorrelation/fadd.jsp");
		return listnodesaveip(dao,id,category);
	}
	
	   /**
	    * 分页显示记录
	    * targetJsp:目录jsp
	    */
	   protected String list2(AlarmCorrelationDao dao)
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
	   
	   
	   /**
	    * 分页显示记录
	    * targetJsp:目录jsp
	    */
	   protected String listnodeip(AlarmCorrelationDao dao)
	   {
		 //System.out.println("=================list2======================123123");
		   String targetJsp = null;
		   int perpage = getPerPagenum();
	       List list = dao.listnodeipByPage(getCurrentPage(),perpage);
	      // System.out.println("========list+++11111++++"+list.toString());
	       if(list==null) return null; 
	      // System.out.println("=====================================ttt+list="+list.size());
	       request.setAttribute("page",dao.getPage());
	       request.setAttribute("list",list);
	       targetJsp = getTarget(); 
	       
		   return targetJsp;
	   }
	   protected String listnodesaveip(AlarmCorrelationDao dao,String id_s,String category)
	   {
		   String targetJsp = null;
		   int perpage = getPerPagenum();
		   
		  
		   //System.out.println("=========manage=listnodesaveip===id_s==="+id_s);
		  // System.out.println("=========manage=listnodesaveip==category===="+category);
		   //id_s=(String)request.getSession().getAttribute("id_s");
	       List list = dao.listnodesaveByPage(getCurrentPage(),perpage,id_s,category);
	      // System.out.println("list======"+list.toString());
	       if(list==null) return null; 
	      
	       request.setAttribute("page",dao.getPage());
	       request.setAttribute("list",list);
	       request.setAttribute("id_s", id_s);
	       request.setAttribute("category", category);
	       request.setAttribute("id", id_s);
	       //request.getSession().setAttribute("id_s", id_s);
	       //request.setAttribute("id_s2", id_s);
	       //request.setAttribute("category2", category);
	       
	       targetJsp = getTarget(); 
	       
		   return targetJsp;
	   }
}
