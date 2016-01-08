package com.afunms.application.course.model;

import com.afunms.common.base.BaseVo;

public class LsfNms extends BaseVo{
	
	private int id;
	private String nodeid;
	private String name;
	private String type;
	private String subtype;
	private String alias;
	private String description;
	private String category;
	private String isDefault;
	private String isCollection;
	private String poll_interval;
	private String interval_unit;
	private String classpath;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getNodeid() {
		return nodeid;
	}
	public void setNodeid(String nodeid) {
		this.nodeid = nodeid;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
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
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public String getIsDefault() {
		return isDefault;
	}
	public void setIsDefault(String isDefault) {
		this.isDefault = isDefault;
	}
	public String getIsCollection() {
		return isCollection;
	}
	public void setIsCollection(String isCollection) {
		this.isCollection = isCollection;
	}
	public String getPoll_interval() {
		return poll_interval;
	}
	public void setPoll_interval(String pollInterval) {
		poll_interval = pollInterval;
	}
	public String getInterval_unit() {
		return interval_unit;
	}
	public void setInterval_unit(String intervalUnit) {
		interval_unit = intervalUnit;
	}
	public String getClasspath() {
		return classpath;
	}
	public void setClasspath(String classpath) {
		this.classpath = classpath;
	}
	
}
