package com.afunms.system.model;
import com.afunms.common.base.BaseVo;
public class Message extends BaseVo{
	private int id;
	private String ip;
	private String devtype;
	private String bigsys;
	private String smallsys;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public String getDevtype() {
		return devtype;
	}
	public void setDevtype(String devtype) {
		this.devtype = devtype;
	}
	public String getBigsys() {
		return bigsys;
	}
	public void setBigsys(String bigsys) {
		this.bigsys = bigsys;
	}
	public String getSmallsys() {
		return smallsys;
	}
	public void setSmallsys(String smallsys) {
		this.smallsys = smallsys;
	}
}
