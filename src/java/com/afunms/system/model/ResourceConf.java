package com.afunms.system.model;

import com.afunms.common.base.BaseVo;

public class ResourceConf extends BaseVo{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1111111;
	private int id;         //ID
	private String confititle;	 //����
	private String logname;    //ά����
	private String startdate;	 //��ʼʱ��
	private String enddate;		//����ʱ��
	private	String state;//״̬
	private String resourcedesc;	 //����
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getConfititle() {
		return confititle;
	}
	public void setConfititle(String confititle) {
		this.confititle = confititle;
	}
	public String getLogname() {
		return logname;
	}
	public void setLogname(String logname) {
		this.logname = logname;
	}

	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getResourcedesc() {
		return resourcedesc;
	}
	public void setResourcedesc(String resourcedesc) {
		this.resourcedesc = resourcedesc;
	}
	public String getStartdate() {
		return startdate;
	}
	public void setStartdate(String startdate) {
		this.startdate = startdate;
	}
	public String getEnddate() {
		return enddate;
	}
	public void setEnddate(String enddate) {
		this.enddate = enddate;
	}

	
}
