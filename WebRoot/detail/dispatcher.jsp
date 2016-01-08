<%@page language="java" contentType="text/html;charset=GB2312"%>
<%@page import="com.afunms.polling.base.Node"%>
<%@page import="com.afunms.polling.*"%>
<%@page import="com.afunms.polling.node.*"%>
<%@page import="com.afunms.topology.dao.*"%>
<%@page import="com.afunms.topology.model.*"%>

<%
	String nodeId = "";
	String temp = null;
	String belong = request.getParameter("belong");
	String fromtopo = request.getParameter("fromtopo");
    if(fromtopo == null) fromtopo = "false";
	String flag = request.getParameter("flag");
	//String flag = "1";
	if (flag == null) {
		flag = "0";
	}
	temp = request.getParameter("id");
	if (temp != null)
		nodeId = temp;
	Node node = null;
	String nodeTag = nodeId.substring(0, 3);
	String node_id = nodeId.substring(3);
	
	String rootPath = request.getContextPath();

	TreeNodeDao treeNodeDao = new TreeNodeDao();
	TreeNode vo = (TreeNode) treeNodeDao.findByNodeTag(nodeTag);
	String rightFramePath = "";
	if("bus".equals(nodeTag)){
		String treeBid = request.getParameter("treeBid");
		rightFramePath = "/businessview.do?action=showViewNode&bid=" + treeBid + "&viewId="
							+ nodeId.substring(3) + "&flag=" + flag +"&="+belong ;
		
	} else if ("soc".equals(nodeTag)){
		rightFramePath = "/pstype.do?action=detail&id=" + nodeId.substring(3)
						+ "&flag=" + flag;
	}else if ("ups".equals(nodeTag)){
		rightFramePath = "/ups.do?action=tosysinfo&id=" + nodeId.substring(3)
						+ "&flag=" + flag;
	} else if ("ups".equals(nodeTag)){
		rightFramePath = "/ups.do?action=tosysinfo&id=" + nodeId.substring(3)
						+ "&flag=" + flag;
	} else if (nodeTag.equals("pro")) {		
			//rightFramePath = "/processgroup.do?action=showlist&jp=1&flag=1";
			rightFramePath = "/processgroup.do?action=listdetail&id="+nodeId.substring(3);
	}
	  System.out.println("=============vo======================"+vo);
	if (vo != null && vo.getName() != null && !"".equals(vo.getName())) {
		node = PollingEngine.getInstance().getNodeByCategory(vo.getName(), Integer.parseInt(node_id));
	}
      System.out.println("=============vo===================="+node);
	if (node == null) {
		if (nodeTag.equals("app")) {
			rightFramePath = "/appsystem.do?action=list_app&id=" + node_id;

		} else if (nodeTag.equals("sit")) {
			rightFramePath = "/website.do?action=list_web&id=" + node_id;
		} else if (nodeTag.equals("oas")) {
			rightFramePath = "/oasys.do?action=list&jp=1";
		} else if (nodeTag.equals("was")) {		
			rightFramePath = "was.do?action=detail&id="+ nodeId.substring(3) + "&flag=" + flag;
		}else if (nodeTag.equals("mbs")) {
			rightFramePath = "/mb.do?action=mbinfo&id="+ nodeId.substring(3) + "&flag=" + flag;
		} else if (nodeTag.equals("dhc")) {		
			rightFramePath = "dhcp.do?action=detail&id="+ nodeId.substring(3) + "&flag=" + flag;
		} else if (nodeTag.equals("tft")) {		
			rightFramePath = "tftp.do?action=detail&id="+ nodeId.substring(3) + "&flag=" + flag;
		}else if (nodeTag.equals("sto")) {	
			rightFramePath = "/storage.do?action=toDetail&id="+ nodeId.substring(3) + "&flag=" + flag;
			%>
			<script type="text/javascript">
			
			alert("û�иýڵ����Ϣ��");
			history.go(-1);
			</script>
			<%
		}
		String path = rootPath +rightFramePath;
		
		 response.sendRedirect(path);
		 
		 return;
		//  return;
	} else {
		String paraString = ".jsp?id=" + nodeId.substring(3) + "&flag="
				+ flag +"&belong="+belong;
		String paraStringTemp = "&id=" + nodeId.substring(3) + "&flag="
				+ flag;
	
		
		if (node.getCategory() == 4) {
		    
			if (node.getCollecttype() == 3
					|| node.getCollecttype() == 4
					|| node.getCollecttype() == 8
					|| node.getCollecttype() == 9) {
				//PING��REMOTEPING��ֻTELNET��SSH��ʽ�����ͨ��
				//�����Ƿ���AS400���ж�
				String runmodel = PollingEngine.getCollectwebflag();
		//		System.out.println("����ǽ====#########################"+runmodel);
				if("0".equals(runmodel)){
       				//�ɼ�������Ǽ���ģʽ
       				Host host = (Host)PollingEngine.getInstance().getNodeByID(node.getId()); 
       	//			System.out.println(host.getId()+"==�ɼ�������Ǽ���ģʽ====>"+host.getSysOid());
       				if("as400".equals(host.getSysOid())){
       					//as400������ 
       					rightFramePath = "/monitor.do?action=hostping&id="
							+ nodeId.substring(3) + "&flag=" + flag +"&belong="+belong;
       				}else{
       					//rightFramePath = "/monitor.do?action=hostcpu&id="
						//	+ nodeId.substring(3) + "&flag=" + flag +"&belong="+belong;
						rightFramePath = "/detail/host_linux.jsp?flag=" + flag;
       				}
       			}else{
       				//�ɼ�������Ƿ���ģʽ
       				Host host = (Host)PollingEngine.getInstance().getNodeByID(node.getId()); 
       //				System.out.println(host.getId()+"==�ɼ�������Ƿ���ģʽ====>"+host.getSysOid());
       				if("as400".equals(host.getSysOid())){
       					//as400������
       					rightFramePath = "/monitor.do?action=hostping&id="
							+ nodeId.substring(3) + "&flag=" + flag +"&belong="+belong;
       				}else{
       				
       				//rightFramePath = "/monitor.do?action=hostcpu&id="
					//		+ nodeId.substring(3) + "&flag=" + flag +"&belong="+belong;
					rightFramePath = "/detail/host_linux.jsp?flag=" + flag;
       				}        				
       			}
			} else {
				//�����Ƿ���AS400���ж�
				String runmodel = PollingEngine.getCollectwebflag();
				
				
				if("0".equals(runmodel)){
       				//�ɼ�������Ǽ���ģʽ
       				Host host = (Host)PollingEngine.getInstance().getNodeByID(node.getId()); 
       				if("as400".equals(host.getSysOid())){
       					//as400������ 
       					rightFramePath = "/monitor.do?action=hostping&id="
							+ nodeId.substring(3) + "&flag=" + flag +"&belong="+belong;
       				}else{
       	//			System.out.println("hostwindows---###########################################");
       				//rightFramePath = "/monitor.do?action=hostwindows&id="
						//	+ nodeId.substring(3) + "&flag=" + flag +"&belong="+belong;
							rightFramePath = "/detail/host_linux.jsp?flag=" + flag;
       				}
       			}else{
       				//�ɼ�������Ƿ���ģʽ
       				Host host = (Host)PollingEngine.getInstance().getNodeByID(node.getId()); 
       				if("as400".equals(host.getSysOid())){
       					//as400������
       					rightFramePath = "/monitor.do?action=hostping&id="
							+ nodeId.substring(3) + "&flag=" + flag +"&belong="+belong;
       				}else{
       				if(host.getSysOid().startsWith("1.3.6.1.4.1.311.") || host.getSysOid().startsWith("1.3.6.1.4.1.8072.3.2.10")){
       					rightFramePath = "/monitor.do?action=hostwindows&id="
							+ nodeId.substring(3) + "&flag=" + flag +"&belong="+belong;
       				}else{
       					rightFramePath = "/detail/host_linux.jsp?flag=" + flag + "&id=" + nodeId.substring(3);
       				}
       				//rightFramePath = "/monitor.do?action=hostcpu&id="
						//	+ nodeId.substring(3) + "&flag=" + flag +"&belong="+belong;
							
       				}        				
       			}			
			}
		} else if ((node.getCategory() < 4||node.getCategory()==7) && node.getCategory() > 0 ) {
			if (node.getCollecttype() == 3
					|| node.getCollecttype() == 4
					|| node.getCollecttype() == 8
					|| node.getCollecttype() == 9) {
				//PING��REMOTEPING��ֻTELNET��SSH��ʽ�����ͨ��
				rightFramePath = "/monitor.do?action=netcpu" + paraStringTemp;
			} else if((belong!=null)&&(belong.equals("floor"))){
				rightFramePath = "/topology/host_server/networkview" + paraString;
			}else{
				rightFramePath = "/topology/network/networkview" + paraString;
			}

		}else if (node.getCategory() == 50){
			rightFramePath = "ip_detail" + paraString;
		}else if (node.getCategory() == 51){
			rightFramePath = "/tomcat.do?action=sys&id="
					+ nodeId.substring(3) + "&flag=" + flag;
		}else if (node.getCategory() == 72||nodeTag.equals("res")){
			rightFramePath = "/resin.do?action=system&id="
				+ nodeId.substring(3) + "&flag=" + flag;
	     }else if (node.getCategory() == 57){
			rightFramePath = "/web.do?action=detail&id=" + nodeId.substring(3)
					+ "&flag=" + flag;
		}else if (node.getCategory() == 88){
			rightFramePath = "/weblogin.do?action=detail&id=" + nodeId.substring(3)
					+ "&flag=" + flag;
		}else if (node.getCategory() == 70){
			rightFramePath = "/jboss.do?action=detail&id=" + nodeId.substring(3)
					+ "&flag=" + flag;
		}else if (node.getCategory() == 71){
			rightFramePath = "/tuxedo.do?action=toDetail&id=" + nodeId.substring(3)
					+ "&flag=" + flag;
		}else if (node.getCategory() == 58){
			rightFramePath = "/FTP.do?action=detail&id=" + nodeId.substring(3)
					+ "&flag=" + flag;
		}else if (node.getCategory() == 64){
			rightFramePath = "/weblogic.do?action=detail&id="
					+ nodeId.substring(3) + "&flag=" + flag;
		}else if (node.getCategory() == 63 || nodeTag.equals("was")){
			rightFramePath = "/was.do?action=detail&id="
					+ nodeId.substring(3) + "&flag=" + flag;
		}else if (node.getCategory() == 67){
			rightFramePath = "/iis.do?action=detail&id=" + nodeId.substring(3)
					+ "&flag=" + flag;
			//response.sendRedirect(rootPath
			//		+ "/iis.do?action=detail&id=" + nodeId.substring(3)
			//		+ "&flag=" + flag);
		}else if (node.getCategory() == 61 || nodeTag.equals("mqs")){
			rightFramePath = "/mq.do?action=detail&id="
					+ nodeId.substring(3) + "&flag=" + flag;
			//response.sendRedirect(rootPath
			//		+ "/was.do?action=detail&id="
			//		+ nodeId.substring(3) + "&flag=" + flag);
		}else if (node.getCategory() == 56){
			rightFramePath = "/mail.do?action=detail&id="
					+ nodeId.substring(3) + "&flag=" + flag;
			//response.sendRedirect(rootPath
			//		+ "/mail.do?action=detail&id="
			//		+ nodeId.substring(3) + "&flag=" + flag);
		}else if (node.getCategory() == 62){
			rightFramePath = "/domino.do?action=domcpu&id="
					+ nodeId.substring(3) + "&flag=" + flag;
		}else if (node.getCategory() == 93){
			rightFramePath = "/dhcp.do?action=detail&id="
					+ nodeId.substring(3) + "&flag=" + flag;
			
		}else if (node.getCategory() == 95){
			rightFramePath = "/bi.do?action=detail&id="
					+ nodeId.substring(3) + "&flag=" + flag;
			
		} else if (node.getCategory() == 122) {	
			rightFramePath = "/mb.do?action=mbinfo&id="+ nodeId.substring(3) + "&flag=" + flag;
			
		}else if (node.getCategory() == 52 || node.getCategory() == 54 || node.getCategory() == 55
				|| node.getCategory() == 59 || node.getCategory() == 60 || node.getCategory() == 53 || node.getCategory() == 99){
			rightFramePath = "/db.do?action=check&id="
					+ nodeId.substring(3) + "&flag=" + flag;
			//response.sendRedirect(rootPath + "/db.do?action=check&id="
			//		+ nodeId.substring(3) + "&flag=" + flag);
		}else if (node.getCategory() == 11){
		    HostNodeDao dao = new HostNodeDao();
			HostNode host = null;
			try {
				host = (HostNode) dao.findByID(temp.substring(3));
			} catch (Exception ex) {
				ex.printStackTrace();
			} finally {
				dao.close();
			}
		    if(host.getSysOid().startsWith("1.3.6.1.4.1.3375.2.1.3.4.")){
		   		//��ʾF5���ؾ���    1.3.6.1.4.1.3375.2.1.3.4.
		   		//rightFramePath = "/topology/network/f5serverview"+paraString;
		    	rightFramePath = "/topology/network/networkview" + paraString;
		   	}
		   	else if(host.getSysOid().startsWith("1.3.6.1.4.1.89.1.1.62.20")){
		      	//��ʾF5���ؾ���    1.3.6.1.4.1.89.1.1.62.20
		      	rightFramePath = "/topology/network/networkview" + paraString;
		   	   //rightFramePath = "/topology/network/f5serverview"+paraString;
		   	   }
//		   	else if(host.getSysOid().startWith("null")){
//		   		rightFramePath ="/topology/network/networkview" + paraString;    
//		   	   }
		   	   
		}else if (node.getCategory() == 10){
		    rightFramePath = "/topology/network/gatewayview"+paraString;
		}else if (node.getCategory() == 8) {
			HostNodeDao dao = new HostNodeDao();
			HostNode host = null;
			try {
				host = (HostNode) dao.findByID(temp.substring(3));
			} catch (Exception ex) {
				ex.printStackTrace();
			} finally {
				dao.close();
			}
			//if(host.getSysOid().equalsIgnoreCase("1.3.6.1.4.1.14331.1.4")){
		   		//��ʾ�����ŷ���ǽ
		   		//rightFramePath = "/topology/network/firewallview_tos"+paraString;
		   //	} else
		   	 if (host.getSysOid().startsWith("1.3.6.1.4.1.9.1.")){
		   		//��ʾPIX����ǽ
		   		rightFramePath = "/topology/network/firewallview"+paraString;
		   		//response.sendRedirect(rootPath+"/topology/network/firewallview"+paraString);
		   	} else if (host.getSysOid().startsWith("1.3.6.1.4.1.94.")){
		   		//��ʾNOKIA����ǽ
		   		rightFramePath = "/topology/network/nokiafirewallview"+paraString;
		   	} else {
		   		//��ʾNETSCREEN����ǽ
		   		rightFramePath = "/topology/network/firewall"+paraString;
		   		//response.sendRedirect(rootPath+"/topology/network/firewallview"+paraString);
		   	}
		}else if (node.getCategory() == 9) {
			//ATM����
			HostNodeDao dao = new HostNodeDao();
			HostNode host = null;
			try {
				host = (HostNode) dao.findByID(temp.substring(3));
			} catch (Exception ex) {
				ex.printStackTrace();
			} finally {
				dao.close();
			}
			rightFramePath = "/monitor.do?action=netcpu" + paraStringTemp;
		}else if (node.getCategory() == 12){
			HostNodeDao dao = new HostNodeDao();
			HostNode host = null;
			try {
				host = (HostNode) dao.findByID(temp.substring(3));
			} catch (Exception ex) {
				ex.printStackTrace();
			} finally {
				dao.close();
			}
			
			rightFramePath = "/topology/network/vpnview"+paraString;
			//rightFramePath = "/monitor.do?action=netcpu" + paraStringTemp;
		}else if (node.getCategory() == 14){
			HostNodeDao dao = new HostNodeDao();
			HostNode host = null;
			try {
				host = (HostNode) dao.findByID(temp.substring(3));
			} catch (Exception ex) {
				ex.printStackTrace();
			} finally {
				dao.close();
			}
			
			//rightFramePath = "/topology/network/vpnview"+paraString;
			//�����洢����������1.3.6.1.4.1.311.1.1.3.1.1
					if("1.3.6.1.4.1.116.3.11.4.1.1".contains(host.getSysOid()) || "1.3.6.1.4.1.311.1.1.3.1.1".contains(host.getSysOid())){
						rightFramePath = "/storagehdc.do?action=running" + paraStringTemp;
				    }else{ 
					    rightFramePath = "/storagehdc.do?action=syslist" + paraStringTemp;
					}
		  //emc�洢
					if(host.getSysOid().contains("1.3.6.1.4.1.1981.")){
					    rightFramePath = "/emc.do?action=system"+paraStringTemp;
					}
		  //netapp�洢
					if(host.getSysOid().contains("1.3.6.1.4.1.789.")){
					    rightFramePath = "/netapp.do?action=raid"+paraStringTemp;
					}
		 //�˳�����洢
		            if(host.getSysOid().contains("1.3.6.1.4.1.3764.1.1")){
					    rightFramePath = "/application/storage/inspurSystem" + paraString;
					}
			//���մ洢							 1.3.6.1.4.1.11.2.3.7.11
					if(host.getSysOid().contains("1.3.6.1.4.1.11.2.3.7.11")){
					    rightFramePath = "/hpstorage.do?action=system"+paraStringTemp;
					}
			//IBM�洢							 1.3.6.1.4.1.11.2.3.7.11
					if(host.getSysOid().contains("1.3.6.1.4.1.2.3.1.2.1.43")){
					    rightFramePath = "/ibmstorage.do?action=enclosure"+paraStringTemp;
					}	
					
			}else if (node.getCategory() == 15){
System.out.println("vmware========");
				//VMWare
				HostNodeDao dao = new HostNodeDao();
				HostNode host = null;
				try {
					host = (HostNode) dao.findByID(temp.substring(3));
				} catch (Exception ex) {
					ex.printStackTrace();
				} finally {
					dao.close();
				}
				rightFramePath = "/vmware.do?action=showsys" + paraStringTemp; 
			}else if(node.getCategory() == 66){
				rightFramePath = "/apache.do?action=system" + paraStringTemp; 
			}else if(nodeTag.equals("dns")){
				rightFramePath = "/dns.do?action=system" + paraStringTemp; 
			}else if (node.getCategory() == 16){
				//�˳��洢
				rightFramePath = "/storage.do?action=toDetail&id="+ nodeId.substring(3) + "&flag=" + flag;
			}
	
		//�����ں�andת��һ��
		String path = "";
		
		if(fromtopo.equals("true")){ 
			//�����ں�andת��һ��  
			//rightFramePath = rightFramePath.replaceAll("&","-and-");
			//rightFramePath = rightFramePath.replaceAll("=","-equals-");
			//ʹ��ѭ���������ں�andת��һ��  
			while(rightFramePath.indexOf("&") != -1){
				rightFramePath = rightFramePath.replace("&","-and-");
			}
			while(rightFramePath.indexOf("=") != -1){
				rightFramePath = rightFramePath.replace("=","-equals-");
			}
			path = rootPath + "/performance/index.jsp?flag=1&rightFramePath="+rightFramePath;
		} else {
			path = rootPath +rightFramePath;
		}
		
		
		response.sendRedirect(path);
	}
%>