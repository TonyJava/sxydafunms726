package com.afunms.application.model;

import com.afunms.common.base.BaseVo;

public class ProcessMainModel extends BaseVo{
	
	private String nodeid;
	private String ipaddress;
	private String processName;
	private String mon_flag;
	public String getMon_flag() {
		return mon_flag;
	}
	public void setMon_flag(String mon_flag) {
		this.mon_flag = mon_flag;
	}
	public String getNodeid() {
		return nodeid;
	}
	public void setNodeid(String nodeid) {
		this.nodeid = nodeid;
	}
	public String getProcessName() {
		return processName;
	}
	public void setProcessName(String processName) {
		this.processName = processName;
	}
	public String getIpaddress() {
		return ipaddress;
	}
	public void setIpaddress(String ipaddress) {
		this.ipaddress = ipaddress;
	}
	
}
