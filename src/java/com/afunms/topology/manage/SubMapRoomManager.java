package com.afunms.topology.manage;

import java.util.List;

import org.jdom.Element;

import com.afunms.cabinet.dao.EqpRoomDao;
import com.afunms.cabinet.model.EqpRoom;
import com.afunms.common.base.BaseManager;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.base.ErrorMessage;
import com.afunms.common.base.ManagerInterface;
import com.afunms.topology.dao.ManageXmlDao;
import com.afunms.topology.dao.RelationDao;
import com.afunms.topology.model.ManageXml;
import com.afunms.topology.model.Relation;
import com.afunms.topology.util.ManageXmlOperator;

public class SubMapRoomManager extends BaseManager implements ManagerInterface {
	
	
	public String execute(String action) {
		//关联机房
		if(action.equals("save_relation_node")){
			return relationMap();
		}
		//取消关联
		if(action.equals("cancel_relation_node")){
			return cancelRelation();
		}	
		
		if (action.equals("relationRoomList")) {
			String fileName = (String) session.getAttribute("fatherXML");
			String nodeId = getParaValue("nodeId");
			String category = getParaValue("category");
			RelationDao rdao = new RelationDao();
			Relation vo1 = (Relation) rdao.findByNodeId(nodeId, fileName);
			if (vo1 != null) {
				request.setAttribute("mapId", vo1.getMapId());
			} else {
				request.setAttribute("mapId", "-1");
			}
			DaoInterface dao = new EqpRoomDao();
			String targetJsp = null;
		   	int perpage = getPerPagenum();
		   	List list = dao.listByPage(getCurrentPage(),perpage);
		   	request.setAttribute("list",list);
//		   	for(int i=0;i<list.size();i++){
//		   		EqpRoom vo = (EqpRoom)list.get(i);
//		   		int id = vo.getId();
//		   		String name = vo.getName();
//		   		int opId = vo.getOperId();
//		   		String descr = vo.getDescr();
//		   		String bak = vo.getBak();
//		   		
//		   	}
			request.setAttribute("category", category);
			request.setAttribute("nodeId", nodeId);
			return "/topology/submap/relationRoom.jsp";
		}
		setErrorCode(ErrorMessage.ACTION_NO_FOUND);
		return null;
	}
	// 保存关联拓扑图的节点
	private String relationMap() {
		String fileName = (String) getParaValue("xml");
		RelationDao dao = new RelationDao();
		Relation vo = new Relation();
		vo.setXmlName(fileName);
		vo.setNodeId(getParaValue("nodeId"));// 节点id
		vo.setCategory(getParaValue("category"));//节点类型
		vo.setRoomid(getParaValue("radio"));// 机房id
		vo.setMapId(getParaValue("mapId"));//子图id
		
		Relation vo1 = (Relation) dao.findByNodeId(getParaValue("nodeId"),
				fileName);
		if (vo1 != null) {
			vo.setId(vo1.getId());
			dao.updateRoom(vo);
		} else {
			dao.saveRoom(vo);
		}
		ManageXmlDao mdao = new ManageXmlDao();
		ManageXml manageXml = (ManageXml) mdao.findByID(getParaValue("radio"));
		ManageXmlOperator mXmlOpr = new ManageXmlOperator();
		mXmlOpr.setFile(fileName);
		mXmlOpr.init4updateXml();
		if (mXmlOpr.isNodeExist(getParaValue("nodeId"))) {
			mXmlOpr.updateNode(getParaValue("nodeId"), "relationMap","#4");
			//mXmlOpr.updateNode(getParaValue("nodeId"), "relationMap", manageXml.getXmlName());
		}
		mXmlOpr.writeXml();
		mdao.close();
		return null;
	}
	
	private String cancelRelation() {
		String fileName = getParaValue("xml");
		RelationDao dao = new RelationDao();
		dao.deleteByNode(getParaValue("nodeId"), fileName);
		ManageXmlOperator mXmlOpr = new ManageXmlOperator();
		mXmlOpr.setFile(fileName);
		mXmlOpr.init4updateXml();
		if (mXmlOpr.isNodeExist(getParaValue("nodeId"))) {
			mXmlOpr.updateNode(getParaValue("nodeId"), "relationMap", "");
		}
		mXmlOpr.writeXml();
		return null;

	}
	  /**
	    * 分页显示记录
	    * targetJsp:目录jsp
	    */
	protected String list(DaoInterface dao){
		String targetJsp = null;
		int perpage = getPerPagenum();
	    List list = dao.listByPage(getCurrentPage(),perpage);
	    if(list==null) return null;   
	    request.setAttribute("page",dao.getPage());
	    request.setAttribute("list",list);
	    targetJsp = getTarget(); 
	return targetJsp;
	}

    public static void main(String args[]){
//    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
//		Calendar tempCal = Calendar.getInstance();
//		Date cc = tempCal.getTime();
//		String time = sdf.format(cc);
//		System.out.println(time);
//    	String path = "E://MyWork//Tomcat5.0//webapps//afunms//resource//xml";    
//        getFile(path);    
    }
}
