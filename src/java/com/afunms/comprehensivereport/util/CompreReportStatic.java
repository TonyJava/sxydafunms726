package com.afunms.comprehensivereport.util;

import java.util.Map;

public class CompreReportStatic {

	private String ip;
	private double value;
	private double max;
	private double min;
	private String type;
	private String unit;
	private Map<String,Double> weekValues;
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public double getValue() {
		return value;
	}
	public void setValue(double value) {
		this.value = value;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
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
	public double getMax() {
		return max;
	}
	public void setMax(double max) {
		this.max = max;
	}
	public double getMin() {
		return min;
	}
	public void setMin(double min) {
		this.min = min;
	}
	
	
}
