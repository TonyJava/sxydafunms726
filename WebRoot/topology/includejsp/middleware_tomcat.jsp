<%@page language="java" contentType="text/html;charset=gb2312"%>
<%@ include file="/include/globe.inc"%>
<%@ include file="/include/globeChinese.inc"%>
<%@page import="com.afunms.report.jfree.ChartCreator"%>
<%@page import="com.afunms.topology.util.NodeHelper"%>
<%@page import="com.afunms.polling.*"%>
<%@page import="org.jfree.data.general.DefaultPieDataset"%>
<%@page import="com.afunms.polling.node.Tomcat"%>
<%@page import="com.afunms.common.util.*"%>
<%@page import="java.util.*"%>
<%@page import="com.afunms.application.manage.TomcatManager"%>
<%@ page import="com.afunms.event.model.EventList"%>
<%@ page import="com.afunms.detail.service.tomcatInfo.TomcatInfoService"%>
<%@page import="com.afunms.initialize.*"%>
<%
	String rootPath = request.getContextPath();
	String runmodel = PollingEngine.getCollectwebflag();
	String tmp = request.getParameter("tmp");
	String avgpingStr = request.getParameter("avgpingcon") == null ? "0"
			: request.getParameter("avgpingcon");
	double avgpingcon = Double.parseDouble(avgpingStr);
	String avgjvm = request.getParameter("avgjvmcon") == null ? "0"
			: request.getParameter("avgjvmcon");
	double avgjvmcon = Double.parseDouble(avgjvm);
	double jvm_memoryuiltillize = 0;
	double tomcatping = 0;
	String lasttime;
	String nexttime;
	String jvm = "";
	String jvm_utilization = "";
	Hashtable data_ht = new Hashtable();
	Hashtable pollingtime_ht = new Hashtable();
	TomcatManager tm = new TomcatManager();
	Tomcat tomcat = (Tomcat) PollingEngine.getInstance().getTomcatByID(
			Integer.parseInt(tmp));
	Hashtable hash_data = null;
	if ("0".equals(runmodel)) {
		//�ɼ�������Ǽ���ģʽ
		Hashtable tomcatvalues = ShareData.getTomcatdata();
		if (tomcatvalues != null && tomcatvalues.containsKey(tmp)) {
			data_ht = (Hashtable) tomcatvalues.get(tmp);
		}
	} else {
		//�ɼ�����ʷ���ģʽ
		TomcatInfoService tomcatInfoService = new TomcatInfoService();
		data_ht = tomcatInfoService.getTomcatDataHashtable(tmp);
	}

	tomcatping = (double) tm.tomcatping(tomcat.getId());
	pollingtime_ht = tm.getCollecttime(tomcat.getIpAddress());

	if (pollingtime_ht != null) {
		lasttime = (String) pollingtime_ht.get("lasttime");
		nexttime = (String) pollingtime_ht.get("nexttime");
	} else {
		lasttime = null;
		nexttime = null;
	}
	if (data_ht != null) {
		if (data_ht.get("jvm") != null)
			jvm = (String) data_ht.get("jvm");
		if (data_ht.get("jvm_utilization") != null) {
			jvm_utilization = (String) data_ht.get("jvm_utilization");
		}

	} else {
		jvm = "";
	}
	int percent1 = Double.valueOf(tomcatping).intValue();
	int percent2 = 100 - percent1;
	String cpuper = "0";
	cpuper = jvm_utilization;

	CreateMetersPic cmp = new CreateMetersPic();
	String path = ResourceCenter.getInstance().getSysPath()
			+ "resource\\image\\dashboard1.png";
	cmp.createPic(tmp, avgjvmcon, path, "JVM������", "tomcat_jvm");

	StringBuffer dataStr = new StringBuffer();
	dataStr.append("��ͨ;").append(Math.round(avgpingcon)).append(
			";false;7CFC00\\n");
	dataStr.append("δ��ͨ;").append(100 - Math.round(avgpingcon)).append(
			";false;FF0000\\n");
	String avgdata = dataStr.toString();
%>
<table id="service-detail-content" class="service-detail-content">
	<tr>
		<td>
			 <jsp:include page="/topology/includejsp/detail_content_top.jsp">
			 	<jsp:param name="contentTitle" value="Tomcat��Ϣ"/> 
			 </jsp:include>
		</td>
	</tr>
	<tr>
		<td>
			<table id="service-detail-content-body"
				class="service-detail-content-body">
				<tr>
					<td>
						<table style="BORDER-COLLAPSE: collapse" bordercolor=#cedefa
							rules=none align=center border=1 cellpadding=0 cellspacing="0"
							width=100%>
							<tr>
								<td width="80%" align="left" valign="top">
									<table>
										<tr>
											<td width="30%" height="29" align="left" nowrap
												class=txtGlobal>
												&nbsp;����:
											</td>
											<td width="70%"><%=tomcat.getAlias()%></td>
										</tr>
										<tr bgcolor="#F1F1F1">
											<td height="29" class=txtGlobal align="left" nowrap>
												&nbsp;״̬:
											</td>
											<td>
												<img
													src="<%=rootPath%>/resource/<%=NodeHelper.getCurrentStatusImage(tomcat
									.getStatus())%>">
												&nbsp;<%=NodeHelper.getStatusDescr(tomcat.getStatus())%>
											</td>
										</tr>
										<tr>
											<td height="29" align=left nowrap class=txtGlobal>
												&nbsp;IP��ַ:
											</td>
											<td><%=tomcat.getIpAddress()%></td>
										</tr>
										<tr bgcolor="#F1F1F1">
											<td height="29" class=txtGlobal align="left" nowrap>
												&nbsp;�˿�:
											</td>
											<td><%=tomcat.getPort()%></td>
										</tr>
										<tr>
											<td height="29" align=left nowrap class=txtGlobal>
												&nbsp;Tomcat�汾:
											</td>
											<td><%=tomcat.getVersion()%></td>
										</tr>
										<tr bgcolor="#F1F1F1">
											<td height="29" align=left nowrap class=txtGlobal>
												&nbsp;JVM�汾:
											</td>
											<td><%=tomcat.getJvmversion()%></td>
										</tr>
										<tr>
											<td height="29" align=left nowrap class=txtGlobal>
												&nbsp;JVM��Ӧ��:
											</td>
											<td><%=tomcat.getJvmvender()%></td>
										</tr>
										<tr bgcolor="#F1F1F1">
											<td height="29" align=left nowrap class=txtGlobal>
												&nbsp;����������ϵͳ:
											</td>
											<td><%=tomcat.getOs()%></td>
										</tr>
										<tr>
											<td height="29" nowrap class=txtGlobal>
												&nbsp;����ϵͳ�汾:
											</td>
											<td><%=tomcat.getOsversion()%></td>
										</tr>
										<tr bgcolor="#F1F1F1">
											<td height="29" class=txtGlobal nowrap>
												&nbsp;���¸澯��Ϣ:
											</td>
											<td><%=tomcat.getLastAlarm()%></td>
										</tr>
										<tr>
											<td height="29" align=left nowrap class=txtGlobal>
												&nbsp;��һ����ѯ:
											</td>
											<td><%=lasttime%></td>
										</tr>
										<tr bgcolor="#F1F1F1">
											<td height="29" class=txtGlobal nowrap>
												&nbsp;��һ����ѯ:
											</td>
											<td><%=nexttime%></td>
										</tr>
										<tr>
											<td height="29" class=txtGlobal align=right nowrap colspan=3>
												&nbsp;
												<a
													href="<%=rootPath%>/tomcat.do?action=syncconfig&id=<%=tomcat.getId()%>&flag=<%=flag%>">ͬ������</a>&nbsp;&nbsp;
											</td>
										</tr>
									</table>
								</td>
								<td width=20% align="center">
									<table class="container-main-service-detail-tool">
										<tr>
											<td>
												<table style="BORDER-COLLAPSE: collapse" bordercolor=#cedefa
													rules=none align=center border=1 cellpadding=0
													cellspacing="0" width=100%>
													<tr bgcolor=#F1F1F1 height="26">
														<td align="center">
															������ͨ��
														</td>
													</tr>
													<tr>
														<td align=center>
															<!-- 
															<div id="flashcontent00">
																<strong>You need to upgrade your Flash Player</strong>
															</div>
															<script type="text/javascript">
																var so = new SWFObject("<%=rootPath%>/flex/Pie_Component.swf?percent1=<%=percent1%>&percentStr1=����&percent2=<%=percent2%>&percentStr2=������", "Pie_Component", "160", "160", "8", "#ffffff");
																so.write("flashcontent00");
															</script>
														-->
															<div id="avgping">
																<strong>You need to upgrade your Flash Player</strong>
															</div>
															<script type="text/javascript"
																src="<%=rootPath%>/include/swfobject.js"></script>
															<script type="text/javascript">
						                                       var so = new SWFObject("<%=rootPath%>/amchart/ampie.swf", "ampie","160", "155", "8", "#FFFFFF");
						                                           so.addVariable("path", "<%=rootPath%>/amchart/");
						                                           so.addVariable("settings_file", escape("<%=rootPath%>/amcharts_settings/pingStatepie.xml"));
						                                           so.addVariable("chart_data","<%=avgdata%>");
						                                           so.write("avgping");
					                                  </script>
														</td>
													</tr>
													<tr>
														<td height="7" align=center>
															<img src="<%=rootPath%>/resource/image/Loading_2.gif">
														</td>
													</tr>
												</table>
											</td>
										</tr>
										<tr>

											<td>
												<table style="BORDER-COLLAPSE: collapse" bordercolor=#cedefa
													rules=none align=center border=1 cellpadding=0
													cellspacing="0" width=100%>
													<!-- 
													<tr>
														<td align="center" valign="middle" height='30'>
															<div id="flashcontent01">
																<strong>You need to upgrade your Flash Player</strong>
															</div>
															<script type="text/javascript">
																var so = new SWFObject("<%=rootPath%>/flex/DHCCGauge.swf?percent=<%=cpuper%>&title=JVM������", "Pie_Component1", "160", "160", "8", "#ffffff");
																so.write("flashcontent01");
															</script>
														</td>
													</tr>
													<tr>
														<td align=center>
															<img src="<%=rootPath%>/resource/image/Loading.gif">
														</td>
													</tr>
											-->
													<tr bgcolor=#F1F1F1 height="26">
														<td align="center">
															JVMƽ��������
														</td>
													</tr>
													<tr height=160>
														<td align="center">
															<img src="<%=rootPath%>/resource/image/jfreechart/reportimg/<%=tmp%>tomcat_jvm.png">
														</td>
													</tr>
													<tr height="7">
														<td align=center>
															&nbsp;
															<img src="<%=rootPath%>/resource/image/Loading.gif">
														</td>
													</tr>
												</table>
											</td>
										</tr>
									</table>
								</td>
							</tr>
						</table>
					</td>
				</tr>
			</table>
		</td>
	</tr>
	<tr>
		<td>
			 <jsp:include page="/topology/includejsp/detail_content_footer.jsp"/>
		</td>
	</tr>
</table>