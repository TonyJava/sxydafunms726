package com.afunms.linkReport.util;

import java.util.Map;

public class LinkReportStatic {

	private String id;
	private String linkName;
	private String value;
	private String max;
	private String min;
	private String unit;
	private Map<String,Double> weekValues;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getLinkName() {
		return linkName;
	}
	public void setLinkName(String linkName) {
		this.linkName = linkName;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}
	public Map<String, Double> getWeekValues() {
		return weekValues;
	}
	public void setWeekValues(Map<String, Double> weekValues) {
		this.weekValues = weekValues;
	}
	public String getMax() {
		return max;
	}
	public void setMax(String max) {
		this.max = max;
	}
	public String getMin() {
		return min;
	}
	public void setMin(String min) {
		this.min = min;
	}
	
}
