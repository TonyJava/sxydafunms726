package org.apache.jsp.performance;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;
import java.util.List;
import com.afunms.topology.dao.TreeNodeDao;
import com.afunms.topology.model.TreeNode;
import com.afunms.topology.util.NodeHelper;
import com.afunms.common.util.SessionConstant;
import com.afunms.system.model.User;
import com.afunms.config.dao.BusinessDao;
import com.afunms.config.model.Business;
import java.util.ArrayList;
import com.afunms.indicators.util.NodeUtil;
import com.afunms.indicators.model.NodeDTO;
import com.afunms.event.dao.CheckEventDao;
import com.afunms.indicators.util.Constant;
import com.afunms.system.dao.SystemConfigDao;

public final class tree_jsp extends org.apache.jasper.runtime.HttpJspBase
    implements org.apache.jasper.runtime.JspSourceDependent {

  private static final JspFactory _jspxFactory = JspFactory.getDefaultFactory();

  private static java.util.List _jspx_dependants;

  private javax.el.ExpressionFactory _el_expressionfactory;
  private org.apache.AnnotationProcessor _jsp_annotationprocessor;

  public Object getDependants() {
    return _jspx_dependants;
  }

  public void _jspInit() {
    _el_expressionfactory = _jspxFactory.getJspApplicationContext(getServletConfig().getServletContext()).getExpressionFactory();
    _jsp_annotationprocessor = (org.apache.AnnotationProcessor) getServletConfig().getServletContext().getAttribute(org.apache.AnnotationProcessor.class.getName());
  }

  public void _jspDestroy() {
  }

  public void _jspService(HttpServletRequest request, HttpServletResponse response)
        throws java.io.IOException, ServletException {

    PageContext pageContext = null;
    HttpSession session = null;
    ServletContext application = null;
    ServletConfig config = null;
    JspWriter out = null;
    Object page = this;
    JspWriter _jspx_out = null;
    PageContext _jspx_page_context = null;


    try {
      response.setContentType("text/html;charset=GB2312");
      pageContext = _jspxFactory.getPageContext(this, request, response,
      			null, true, 8192, true);
      _jspx_page_context = pageContext;
      application = pageContext.getServletContext();
      config = pageContext.getServletConfig();
      session = pageContext.getSession();
      out = pageContext.getOut();
      _jspx_out = out;

      out.write("\r\n");
      out.write("\r\n");
      out.write("\r\n");
      out.write("\r\n");
      out.write("\r\n");
      out.write("\r\n");
      out.write("   \r\n");
      out.write("\r\n");
      out.write("\r\n");
      out.write("\r\n");
      out.write("\r\n");
      out.write("\r\n");
      out.write("\r\n");
      out.write("\r\n");
      out.write("\r\n");

	String rootPath = request.getContextPath();
	User current_user = (User) session
			.getAttribute(SessionConstant.CURRENT_USER);
	List<Business> list = null;
	BusinessDao dao = new BusinessDao();
	try {
		list = dao.loadAll();
	} catch (Exception e) {
		e.printStackTrace();
	} finally {
		dao.close();
	}
	if (list == null) {
		list = new ArrayList();
	}
	List<Business> bussinessList_tmp = new ArrayList();
	String bids_str = ",";
	for (int i = 0; i < list.size(); i++) {
		Business business = (Business) list.get(i);
		if (current_user.getBusinessids() != null 
			&& current_user.getBusinessids().contains("," + business.getId() + ",")) {
			bussinessList_tmp.add(business);
		}
		bids_str = bids_str + business.getPid() + ",";
	}
	if(current_user.getRole() != 0) {
		list = bussinessList_tmp;
	}



      out.write("\r\n");
      out.write("<html>\r\n");
      out.write("<head>\r\n");
      out.write("<title></title>\r\n");
      out.write("<link rel=\"StyleSheet\" href=\"../performance/dtree/dtree.css\" type=\"text/css\" />\r\n");
      out.write("<script type=\"text/javascript\" src=\"../performance/dtree/dtree.js\"></script>\r\n");
      out.write("<style>\r\n");
      out.write("body {\r\n");
      out.write("margin-left: 6px;\r\n");
      out.write("margin-top: 0px;\r\n");
      out.write("margin-right: 6px;\r\n");
      out.write("margin-bottom: 6px;\r\n");
      out.write("scrollbar-face-color: #E0E3EB;\r\n");
      out.write("scrollbar-highlight-color: #E0E3EB;\r\n");
      out.write("scrollbar-shadow-color: #E0E3EB;\r\n");
      out.write("scrollbar-3dlight-color: #E0E3EB;\r\n");
      out.write("scrollbar-arrow-color: #7ED053;\r\n");
      out.write("scrollbar-track-color: #ffffff;\r\n");
      out.write("scrollbar-darkshadow-color: #9D9DA1;\r\n");
      out.write("}\r\n");
      out.write("body,td,th {color: #666666;line-height:20px}\r\n");
      out.write(".div_RightMenu\r\n");
      out.write("{\r\n");
      out.write("    z-index:30000;\r\n");
      out.write("    text-align:left;    \r\n");
      out.write("    cursor: default;\r\n");
      out.write("    position: absolute;\r\n");
      out.write("    background-color:#FAFFF8;\r\n");
      out.write("    width:100px;\r\n");
      out.write("    height:auto;\r\n");
      out.write("    border: 1px solid #333333;\r\n");
      out.write("    display:none;\r\n");
      out.write("    filter:progid:DXImageTransform.Microsoft.Shadow(Color=#333333,Direction=120,strength=5);    \r\n");
      out.write("}\r\n");
      out.write(".divMenuItem\r\n");
      out.write("{\r\n");
      out.write("    height:17px;\r\n");
      out.write("    color:Black;\r\n");
      out.write("    font-family:宋体;\r\n");
      out.write("    vertical-align:middle;\r\n");
      out.write("    font-size:10pt;\r\n");
      out.write("    margin-bottom:3px;\r\n");
      out.write("    cursor:hand;\r\n");
      out.write("    padding-left:10px;\r\n");
      out.write("    padding-top:2px;\r\n");
      out.write("}\r\n");
      out.write("</style>\r\n");
      out.write("</head>\r\n");
      out.write("<body>\r\n");
      out.write("<div class=\"dtree\" style=\"\">\r\n");
      out.write("<p><a href=\"javascript: d.openAll();\">展开</a> | <a href=\"javascript: d.closeAll();\">合闭</a></p>\r\n");
      out.write("<script type=\"text/javascript\">\r\n");
      out.write("        var currTreeNodeId = '';\t\t// 当前树的节点 Id\r\n");
      out.write("        var treeNodeFatherId = '';\t\t// 当前树的节点的父 Id\t\r\n");
      out.write("        var key = 0 ;\r\n");
      out.write("\t\td = new dTree('d');\r\n");
      out.write("\t\td.add(0,-1,' 设备资源树');\r\n");
      out.write("\t\t\r\n");
      out.write("\t\t");

		
			 
			String treeshowflag_str = "0";
			SystemConfigDao systemConfigDao = new SystemConfigDao();
			try{
				treeshowflag_str = systemConfigDao.getSystemCollectByVariablename("treeshowflag");
			} catch(Exception e){
				
			} finally {
				systemConfigDao.close();
			}
			boolean treeshowflag = false;
			if("1".equals(treeshowflag_str)){
				treeshowflag = true;			// 树的显示模式 以后 可以改成  0，1， 2，。。。。。 
			}
					
			String allBusinessPid = bids_str;	// 所有具有子节点业务的id集合
			Business currBusiness = null;		// 当前业务
			String currbid = "";				// 当前业务id
			String currBusinessNodeId = "";		// 当前业务节点的Id
			List treeNodeList = null;			// 所有节点列表
			String currTreeNodeId = "";			// 当前节点Id
			boolean isShowTreeNodeFlag = true;	// 是否显示该节点 	
			boolean rightFrameFlag = true;		// 右边框架显示页面的模式 暂时只能为 true 以后添加其他模式 
					
			String currTreeNodeFatherId = "";			// 当前节点父 Id
			TreeNodeDao treeNodeDao = new TreeNodeDao();
			try{
				treeNodeList = treeNodeDao.loadAll(); // 获取所有的节点
			}catch(Exception e){
				
			} finally {
				treeNodeDao.close();
			}
			
			NodeUtil nodeUtil = new NodeUtil();
			nodeUtil.setSetedMonitorFlag(true);
			nodeUtil.setMonitorFlag("1");
			int treeNodeNum = 0;				// 树节点中 第几个
			for(Business object : list){
				// 循环每一个业务
				currBusiness = (Business)object;
				currbid = currBusiness.getId();
				if(currBusiness == null || allBusinessPid.contains(currbid)){
					continue;
				}
				
				currTreeNodeFatherId = "0";
				currTreeNodeId = "business_" + currbid;
				currBusinessNodeId = currTreeNodeId;
				
      out.write("\r\n");
      out.write("\t\t\t\t \tcurrTreeNodeId = '");
      out.print(currTreeNodeId);
      out.write("';\r\n");
      out.write("      \t\t\t\tcurrTreeNodeFatherId = '");
      out.print(currTreeNodeFatherId);
      out.write("';\r\n");
      out.write("\t\t\t\t    var imagestr = \"\";\r\n");
      out.write("\t\t\t\t    d.add(currTreeNodeId,currTreeNodeFatherId,'");
      out.print(" " + currBusiness.getName());
      out.write("',\"\",\"\",\"\",\"rightFrame\",imagestr,imagestr);\r\n");
      out.write("\t\t\t\t");

				
				if(treeNodeList == null || treeNodeList.size() == 0){
					return;
				}
				
				// 将当前节点id 赋值给 父节点
				currTreeNodeFatherId = currTreeNodeId;
				for(Object treeNodeObject : treeNodeList){
					// 循环每一个设备树节点
					TreeNode currTreeNode = (TreeNode)treeNodeObject;
			   		List nodeList = nodeUtil.getByNodeTag(currTreeNode.getNodeTag(), currTreeNode.getCategory());
			   		List nodeDTOList = nodeUtil.conversionToNodeDTO(nodeList);
			   		
			   		if(nodeDTOList == null){
			   			nodeDTOList = new ArrayList();
			   		}
			   		
			   		List tempNodeDTOList = new ArrayList();		// 临时存储node
			   		for(Object nodeDTOObject : nodeDTOList){
			   			NodeDTO nodeDTO = (NodeDTO)nodeDTOObject;
			   			if(nodeDTO.getBusinessId().contains("," + currbid + ",")){
			   				tempNodeDTOList.add(nodeDTO);
			   			}
			   		}
			   		nodeDTOList = tempNodeDTOList;
			   		currTreeNode.setDeceiveNum(nodeDTOList.size()+"");
				 	
				 	isShowTreeNodeFlag = true;
				 	if("0".equals(currTreeNode.getDeceiveNum())){
				 		// 如果设备数为 0 则 将 显示模式的赋值给
				 		isShowTreeNodeFlag = treeshowflag;
					}
					
					// 给当前节点赋值 为 该父节点 + "_" + 该节点id;
					currTreeNodeId = currBusinessNodeId + "_" + currTreeNode.getId();
					currTreeNodeFatherId = currBusinessNodeId + "_" + currTreeNode.getFatherId();
					if(0 == currTreeNode.getFatherId()){
						currTreeNodeFatherId = currBusinessNodeId;
					}
					if(isShowTreeNodeFlag){
						
      out.write("\r\n");
      out.write("\t\t\t\t\t \tcurrTreeNodeId = '");
      out.print(currTreeNodeId);
      out.write("';\r\n");
      out.write("\t      \t\t\t\tcurrTreeNodeFatherId = '");
      out.print(currTreeNodeFatherId);
      out.write("';\r\n");
      out.write("\t\t\t\t\t    var imagestr = \"");
      out.print(rootPath);
      out.write("/performance/");
      out.print(NodeHelper.getTypeImage(currTreeNode
										.getName()));
      out.write("\";\r\n");
      out.write("\t\t\t\t\t    d.add(currTreeNodeId,currTreeNodeFatherId,'");
      out.print(" " + currTreeNode.getText() + "("
								+ currTreeNode.getDeceiveNum() + ")");
      out.write('\'');
      out.write(',');
      out.write('"');
      out.print(rootPath + currTreeNode.getUrl()
								+ "&treeBid=" + currbid);
      out.write("\",\"\",\"\",\"rightFrame\",imagestr,imagestr);\r\n");
      out.write("\t\t\t\t\t    \r\n");
      out.write("\t\t\t\t\t\tif(0 == \"");
      out.print(treeNodeNum);
      out.write("\" && \"");
      out.print(rightFrameFlag);
      out.write("\"){\r\n");
      out.write("\t\t\t\t\t\t\t");

								//首页和拓扑图点击设备时跳转的页面链接
								String rightFramePath = rootPath + request.getParameter("rightFramePath");
								rightFramePath = rightFramePath.replaceAll("-equals-","=");
								rightFramePath = rightFramePath.replaceAll("-and-","&");
								if(request.getParameter("rightFramePath") == null || request.getParameter("rightFramePath").equals("null")){
									rightFramePath = rootPath + currTreeNode.getUrl() + "&treeBid=" + currbid;
								}
							
      out.write("\r\n");
      out.write("\t\t\t\t\t\t\t//默认情况下，让右侧窗口显示路由器性能列表，暂时作为一种性能优化的方案\r\n");
      out.write("\t\t\t\t\t\t\t//parent.document.getElementById(\"rightFrame\").src=\"");
      out.print(rightFramePath);
      out.write("\";\r\n");
      out.write("\t\t\t\t\t\t}\r\n");
      out.write("\t\t\t\t\t\t");

						treeNodeNum++;
						currTreeNodeFatherId = currTreeNodeId;
						if("1".equals(currTreeNode.getIsHaveChild())){       
				    	// 不干任何事
				    	} else {
							for(Object nodeDTOObject : nodeDTOList){
					   			NodeDTO nodeDTO = (NodeDTO)nodeDTOObject;
					   			
					   			int alermlevel = 0;
					   			CheckEventDao checkEventDao = new CheckEventDao();
					   			try{
					   				alermlevel = checkEventDao.findMaxAlarmLevelByName(nodeDTO.getId() + ":" + nodeDTO.getType());
					   			} catch (Exception e) {
					   				
					   			} finally {
					   				checkEventDao.close();
					   			}
					   			
					   			checkEventDao.close();
					   			
					   			currTreeNodeId = currTreeNode.getNodeTag() + "_" + nodeDTO.getId();
					   			
					   			String imagestr = rootPath + "/resource/" + NodeHelper.getCurrentStatusImage(alermlevel);
					   			if(Constant.TYPE_GATEWAY.equals(nodeDTO.getType()) || Constant.TYPE_F5.equals(nodeDTO.getType()) || Constant.TYPE_VPN.equals(nodeDTO.getType()) || Constant.TYPE_HOST.equals(nodeDTO.getType()) || Constant.TYPE_NET.equals(nodeDTO.getType()) || Constant.TYPE_DB.equals(nodeDTO.getType())){
					   				imagestr = rootPath + "/performance/" + NodeHelper.getSubTypeImage(nodeDTO.getSubtype());  
					   			}   
					   			
					   			
      out.write("\r\n");
      out.write("\t\t\t\t\t\t\t \tcurrTreeNodeId = '");
      out.print(currTreeNodeId);
      out.write("';\r\n");
      out.write("\t\t\t      \t\t\t\tcurrTreeNodeFatherId = '");
      out.print(currTreeNodeFatherId);
      out.write("';\r\n");
      out.write("\t\t\t\t\t\t\t    var imagestr = \"");
      out.print(imagestr);
      out.write("\";\r\n");
      out.write("\t\t\t\t\t\t\t    d.add(currTreeNodeId,currTreeNodeFatherId,'");
      out.print(" " + nodeDTO.getName());
      out.write('\'');
      out.write(',');
      out.write('"');
      out.print(rootPath
										+ "/detail/dispatcher.jsp?flag=1&id="
										+ currTreeNode.getNodeTag()
										+ nodeDTO.getId());
      out.write("\",\"\",\"\",\"rightFrame\",imagestr,imagestr);\r\n");
      out.write("\t\t\t\t\t\t\t\t");

					   		} // 完成 每一个设备 循环 (L3)
					   	}
					}
				}	// 完成每一个设备树节点循环 (L2)
				
			}	// 完成每一个业务循环 (L1)
			
		
      out.write("\r\n");
      out.write("\t\tdocument.write(d);\r\n");
      out.write("\r\n");
      out.write("//------------search one device-------------根据选中的树节点在地图上搜索对应的设备\r\n");
      out.write("\r\n");
      out.write("function SearchNode(ip)\r\n");
      out.write("{\r\n");
      out.write("\tvar coor = window.parent.mainFrame.mainFrame.getNodeCoor(ip);\r\n");
      out.write("\tif (coor == null)\r\n");
      out.write("\t{\r\n");
      out.write("\t\tvar msg = \"没有在图中搜索到IP地址为 \"+ ip +\" 的设备。\";\r\n");
      out.write("\t\twindow.alert(msg);\r\n");
      out.write("\t\treturn;\r\n");
      out.write("\t}\r\n");
      out.write("\telse if (typeof coor == \"string\")\r\n");
      out.write("\t{\r\n");
      out.write("\t\twindow.alert(coor);\r\n");
      out.write("\t\treturn;\r\n");
      out.write("\t}\r\n");
      out.write("\twindow.parent.mainFrame.mainFrame.moveMainLayer(coor);\r\n");
      out.write("}\r\n");
      out.write("//--------------------end--------------------\r\n");
      out.write("//--------------------begin选中设备显示右键菜单--------------------\r\n");
      out.write("var nodeid=\"\";\r\n");
      out.write("var nodeip=\"\";\r\n");
      out.write("var nodecategory=\"\";\r\n");
      out.write("function showMenu(id,ip){\r\n");
      out.write("    nodeid = id.split(\";\")[0];\r\n");
      out.write("    nodecategory = id.split(\";\")[1];\r\n");
      out.write("    nodeip = ip;\r\n");
      out.write("    /**/\r\n");
      out.write("    if(document.getElementById(\"div_RightMenu\") == null)\r\n");
      out.write("    {    \r\n");
      out.write("        CreateMenu();\r\n");
      out.write("        document.oncontextmenu = ShowMenu\r\n");
      out.write("        document.body.onclick  = HideMenu    \r\n");
      out.write("    }\r\n");
      out.write("    else\r\n");
      out.write("    {\r\n");
      out.write("        document.oncontextmenu = ShowMenu\r\n");
      out.write("        document.body.onclick  = HideMenu    \r\n");
      out.write("    } \r\n");
      out.write("\r\n");
      out.write("}\r\n");
      out.write("function add(){\r\n");
      out.write("    var nodeId = nodeid;//要保证nodeid的长度大于3\r\n");
      out.write("    var coor = window.parent.mainFrame.mainFrame.getNodeId(nodeId);\r\n");
      out.write("    if (coor == null)\r\n");
      out.write("\t{\r\n");
      out.write("         window.parent.mainFrame.mainFrame.addEquip(nodeId,nodecategory);\r\n");
      out.write("\t}\r\n");
      out.write("\telse if (typeof coor == \"string\")\r\n");
      out.write("\t{\r\n");
      out.write("\t\twindow.alert(coor);\r\n");
      out.write("\t\treturn;\r\n");
      out.write("\t}\r\n");
      out.write("    window.parent.mainFrame.mainFrame.moveMainLayer(coor);\r\n");
      out.write("    window.alert(\"该设备已经在拓扑图中存在！\");\r\n");
      out.write("}\r\n");
      out.write("function detail(){\r\n");
      out.write("    showalert(nodeid);\r\n");
      out.write("\twindow.parent.parent.opener.focus();\r\n");
      out.write("}\r\n");
      out.write("function showalert(id) {\r\n");
      out.write("\twindow.parent.parent.opener.location=\"/afunms/detail/dispatcher.jsp?id=\"+id;\r\n");
      out.write("}\r\n");
      out.write("function evtMenuOnmouseMove()\r\n");
      out.write("{\r\n");
      out.write("    this.style.backgroundColor='#8AAD77';\r\n");
      out.write("    this.style.paddingLeft='10px';    \r\n");
      out.write("}\r\n");
      out.write("function evtOnMouseOut()\r\n");
      out.write("{\r\n");
      out.write("    this.style.backgroundColor='#FAFFF8';\r\n");
      out.write("}\r\n");
      out.write("function CreateMenu()\r\n");
      out.write("{    \r\n");
      out.write("        var div_Menu          = document.createElement(\"Div\");\r\n");
      out.write("        div_Menu.id           = \"div_RightMenu\";\r\n");
      out.write("        div_Menu.className    = \"div_RightMenu\";\r\n");
      out.write("        \r\n");
      out.write("        var div_Menu1         = document.createElement(\"Div\");\r\n");
      out.write("        div_Menu1.id          = \"div_Menu1\";\r\n");
      out.write("        div_Menu1.className   = \"divMenuItem\";\r\n");
      out.write("        div_Menu1.onclick     = add;\r\n");
      out.write("        div_Menu1.onmousemove = evtMenuOnmouseMove;\r\n");
      out.write("        div_Menu1.onmouseout  = evtOnMouseOut;\r\n");
      out.write("        div_Menu1.innerHTML   = \"添加到拓扑图\";\r\n");
      out.write("        var div_Menu2         = document.createElement(\"Div\");\r\n");
      out.write("        div_Menu2.id          = \"div_Menu2\";\r\n");
      out.write("        div_Menu2.className   = \"divMenuItem\";\r\n");
      out.write("        div_Menu2.onclick     = detail;\r\n");
      out.write("        div_Menu2.onmousemove = evtMenuOnmouseMove;\r\n");
      out.write("        div_Menu2.onmouseout  = evtOnMouseOut;\r\n");
      out.write("        div_Menu2.innerHTML   = \"详细信息\";\r\n");
      out.write("        \r\n");
      out.write("        div_Menu.appendChild(div_Menu1);\r\n");
      out.write("        div_Menu.appendChild(div_Menu2);\r\n");
      out.write("        document.body.appendChild(div_Menu);\r\n");
      out.write("}\r\n");
      out.write("// 判断客户端浏览器\r\n");
      out.write("function IsIE() \r\n");
      out.write("{\r\n");
      out.write("    if (navigator.appName==\"Microsoft Internet Explorer\") \r\n");
      out.write("    {\r\n");
      out.write("        return true;\r\n");
      out.write("    } \r\n");
      out.write("    else \r\n");
      out.write("    {\r\n");
      out.write("        return false;\r\n");
      out.write("    }\r\n");
      out.write("}\r\n");
      out.write("\r\n");
      out.write("function ShowMenu()\r\n");
      out.write("{\r\n");
      out.write("    \r\n");
      out.write("    if (IsIE())\r\n");
      out.write("    {\r\n");
      out.write("        document.body.onclick  = HideMenu;\r\n");
      out.write("        var redge=document.body.clientWidth-event.clientX;\r\n");
      out.write("        var bedge=document.body.clientHeight-event.clientY;\r\n");
      out.write("        var menu = document.getElementById(\"div_RightMenu\");\r\n");
      out.write("        if (redge<menu.offsetWidth)\r\n");
      out.write("        {\r\n");
      out.write("            menu.style.left=document.body.scrollLeft + event.clientX-menu.offsetWidth\r\n");
      out.write("        }\r\n");
      out.write("        else\r\n");
      out.write("        {\r\n");
      out.write("            menu.style.left=document.body.scrollLeft + event.clientX\r\n");
      out.write("            //这里有改动\r\n");
      out.write("            menu.style.display = \"block\";\r\n");
      out.write("        }\r\n");
      out.write("        if (bedge<menu.offsetHeight)\r\n");
      out.write("        {\r\n");
      out.write("            menu.style.top=document.body.scrollTop + event.clientY - menu.offsetHeight\r\n");
      out.write("        }\r\n");
      out.write("        else\r\n");
      out.write("        {\r\n");
      out.write("            menu.style.top = document.body.scrollTop + event.clientY\r\n");
      out.write("            menu.style.display = \"block\";\r\n");
      out.write("        }\r\n");
      out.write("    }\r\n");
      out.write("    return false;\r\n");
      out.write("}\r\n");
      out.write("function HideMenu()\r\n");
      out.write("{\r\n");
      out.write("    if (IsIE())  document.getElementById(\"div_RightMenu\").style.display=\"none\";    \r\n");
      out.write("}\r\n");
      out.write("</script>\r\n");
      out.write("</div>\r\n");
      out.write("</body>\r\n");
      out.write("</html>");
    } catch (Throwable t) {
      if (!(t instanceof SkipPageException)){
        out = _jspx_out;
        if (out != null && out.getBufferSize() != 0)
          try { out.clearBuffer(); } catch (java.io.IOException e) {}
        if (_jspx_page_context != null) _jspx_page_context.handlePageException(t);
        else log(t.getMessage(), t);
      }
    } finally {
      _jspxFactory.releasePageContext(_jspx_page_context);
    }
  }
}
