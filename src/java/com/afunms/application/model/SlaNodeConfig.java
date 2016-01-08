/**
 * <p>Description:mapping app_db_node</p>
 * <p>Company: dhcc.com</p>
 * @author miiwill
 * @project afunms
 * @date 2007-1-7
 */

package com.afunms.application.model;

import com.afunms.common.base.BaseVo;

public class SlaNodeConfig extends BaseVo {

	private int id;
	private int telnetconfig_id;
	private String name;
	private String slatype;
	private int intervals;
	private String intervalunit;
	private String descr;
	private String bak;
	private int mon_flag;
	private String bid;
	private int entrynumber;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getTelnetconfig_id() {
		return telnetconfig_id;
	}
	public void setTelnetconfig_id(int telnetconfig_id) {
		this.telnetconfig_id = telnetconfig_id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSlatype() {
		return slatype;
	}
	public void setSlatype(String slatype) {
		this.slatype = slatype;
	}
	public int getIntervals() {
		return intervals;
	}
	public void setIntervals(int intervals) {
		this.intervals = intervals;
	}
	public String getIntervalunit() {
		return intervalunit;
	}
	public void setIntervalunit(String intervalunit) {
		this.intervalunit = intervalunit;
	}
	public String getDescr() {
		return descr;
	}
	public void setDescr(String descr) {
		this.descr = descr;
	}
	public String getBak() {
		return bak;
	}
	public void setBak(String bak) {
		this.bak = bak;
	}
	public int getMon_flag() {
		return mon_flag;
	}
	public void setMon_flag(int mon_flag) {
		this.mon_flag = mon_flag;
	}
	public String getBid() {
		return bid;
	}
	public void setBid(String bid) {
		this.bid = bid;
	}
	public int getEntrynumber() {
		return entrynumber;
	}
	public void setEntrynumber(int entrynumber) {
		this.entrynumber = entrynumber;
	}
	
	
	
	
	
}