package com.afunms.system.model;

import com.afunms.common.base.BaseVo;

public class ResourceConf extends BaseVo{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1111111;
	private int id;         //ID
	private String confititle;	 //标题
	private String logname;    //维护人
	private String startdate;	 //开始时间
	private String enddate;		//结束时间
	private	String state;//状态
	private String resourcedesc;	 //描述
	
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
