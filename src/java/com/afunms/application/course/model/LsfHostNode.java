package com.afunms.application.course.model;

import com.afunms.common.base.BaseVo;
/**
 * lsf ¼à¿Ø ×é Model
 * @author Administrator
 *
 */
public class LsfHostNode extends BaseVo{
	
	private int id;
	private String ip_address;
	private String alias;
	private String type;
	private String sys_descr;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getIp_address() {
		return ip_address;
	}
	public void setIp_address(String ipAddress) {
		ip_address = ipAddress;
	}
	public String getAlias() {
		return alias;
	}
	public void setAlias(String alias) {
		this.alias = alias;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getSys_descr() {
		return sys_descr;
	}
	public void setSys_descr(String sysDescr) {
		this.sys_descr = sysDescr;
	}
	
	
	
}
