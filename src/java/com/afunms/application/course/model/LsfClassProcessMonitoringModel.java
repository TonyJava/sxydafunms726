package com.afunms.application.course.model;

import com.afunms.common.base.BaseVo;

public class LsfClassProcessMonitoringModel extends BaseVo{
	
	private String class_id;
	private String node_id;
	private String log_count;
	private String master;
	private String jid;
	public String getClass_id() {
		return class_id;
	}
	public void setClass_id(String class_id) {
		this.class_id = class_id;
	}
	public String getNode_id() {
		return node_id;
	}
	public void setNode_id(String node_id) {
		this.node_id = node_id;
	}
	public String getLog_count() {
		return log_count;
	}
	public void setLog_count(String log_count) {
		this.log_count = log_count;
	}
	public String getMaster() {
		return master;
	}
	public void setMaster(String master) {
		this.master = master;
	}
	public String getJid() {
		return jid;
	}
	public void setJid(String jid) {
		this.jid = jid;
	}
}
