package com.afunms.system.vo;

import com.afunms.common.base.BaseVo;

public class AlarmCorrelationVo extends BaseVo{
	
	private int fathernode;
	private int locanode;
	private int id;
	private String ip_address;
	private String alias;
	private String type;
	private String aip;
	private String aalias;
	private String atype;
	private String bip;
	private String balias;
	private String btype;
	private int bid;
	private String acategory;
	private String category;
	private String bcategory;
	public String getBcategory() {
		return bcategory;
	}
	public void setBcategory(String bcategory) {
		this.bcategory = bcategory;
	}
	public String getAcategory() {
		return acategory;
	}
	public void setAcategory(String acategory) {
		this.acategory = acategory;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	
	public int getBid() {
		return bid;
	}
	public void setBid(int bid) {
		this.bid = bid;
	}
	public String getAip() {
		return aip;
	}
	public void setAip(String aip) {
		this.aip = aip;
	}
	public String getAalias() {
		return aalias;
	}
	public void setAalias(String aalias) {
		this.aalias = aalias;
	}
	public String getAtype() {
		return atype;
	}
	public void setAtype(String atype) {
		this.atype = atype;
	}
	public String getBip() {
		return bip;
	}
	public void setBip(String bip) {
		this.bip = bip;
	}
	public String getBalias() {
		return balias;
	}
	public void setBalias(String balias) {
		this.balias = balias;
	}
	public String getBtype() {
		return btype;
	}
	public void setBtype(String btype) {
		this.btype = btype;
	}

	public int getFathernode() {
		return fathernode;
	}
	public void setFathernode(int fathernode) {
		this.fathernode = fathernode;
	}
	public int getLocanode() {
		return locanode;
	}
	public void setLocanode(int locanode) {
		this.locanode = locanode;
	}
	
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

	
}
