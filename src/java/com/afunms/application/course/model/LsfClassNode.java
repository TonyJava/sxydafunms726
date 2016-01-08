package com.afunms.application.course.model;

import com.afunms.common.base.BaseVo;
/**
 * lsf ¼à¿Ø ×é Model
 * @author Administrator
 *
 */
public class LsfClassNode extends BaseVo{
	
	private int class_id;
	private String classname;
	private String ip_address;
	
	private String alias;
	private String type;
	private int nodeid;
	private int enable;
	private int logflg;
	private int jid;
	public String getClassname() {
		return classname;
	}
	public void setClassname(String calssname) {
		this.classname = calssname;
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
	
	
	public int getClass_id() {
		return class_id;
	}
	public void setClass_id(int classId) {
		class_id = classId;
	}
	public int getNodeid() {
		return nodeid;
	}
	public void setNodeid(int nodeid) {
		this.nodeid = nodeid;
	}
	public int getEnable() {
		return enable;
	}
	public void setEnable(int enable) {
		this.enable = enable;
	}
	public int getLogflg() {
		return logflg;
	}
	public void setLogflg(int logflg) {
		this.logflg = logflg;
	}
	public int getJid(){
		return jid;
	}
	public void setJid(int jid){
		this.jid = jid;
	
	}
	
}
