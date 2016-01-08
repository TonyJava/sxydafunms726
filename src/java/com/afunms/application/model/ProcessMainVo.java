package com.afunms.application.model;

import com.afunms.common.base.BaseVo;

public class ProcessMainVo  extends BaseVo{
	
	private String id;
	private String ipaddress;
	private String ismain="0";
	private String thevalue;//进程名称
	private String nodeid;//设备id
	private String ip;
	private String subtype;//设备类型
	private String alias;//设备别名
	
	public String getThevalue() {
		return thevalue;
	}
	public void setThevalue(String thevalue) {
		this.thevalue = thevalue;
	}
	public String getNodeid() {
		return nodeid;
	}
	public void setNodeid(String nodeid) {
		this.nodeid = nodeid;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public String getSubtype() {
		return subtype;
	}
	public void setSubtype(String subtype) {
		this.subtype = subtype;
	}
	public String getAlias() {
		return alias;
	}
	public void setAlias(String alias) {
		this.alias = alias;
	}
	
	public String getIsmain() {
		return ismain;
	}
	public void setIsmain(String ismain) {
		this.ismain = ismain;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getIpaddress() {
		return ipaddress;
	}
	public void setIpaddress(String ipaddress) {
		this.ipaddress = ipaddress;
	}
	
}
