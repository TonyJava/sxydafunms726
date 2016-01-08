/**
 * <p>Description:used in resourcenodeTable</p>
 * <p>Company: dhcc.com</p>
 * @author jhl
 * @project afunms
 * @date 2012-03-29
 */

package com.afunms.system.model;


import java.sql.Date;

import com.afunms.common.base.BaseVo;

public class ResourceConfNode extends BaseVo{
	
	private int id;         //ID
	private int resourceconfid;//维护配置id
	private int nodeid;//维护资源id
	private String ip_address;//ip地址
	private String alias;//系统名称
	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getResourceconfid() {
		return resourceconfid;
	}
	public void setResourceconfid(int resourceconfid) {
		this.resourceconfid = resourceconfid;
	}
	public int getNodeid() {
		return nodeid;
	}
	public void setNodeid(int nodeid) {
		this.nodeid = nodeid;
	}
	public String getIp_address() {
		return ip_address;
	}
	public void setIp_address(String ip_address) {
		this.ip_address = ip_address;
	}
	public String getAlias() {
		return alias;
	}
	public void setAlias(String alias) {
		this.alias = alias;
	}
}
