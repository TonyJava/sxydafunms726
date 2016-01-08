package com.oss.services.client;

public interface WebServicesServer {
	public String example(String message);
	/**
	 * 
	 * @param bsid 业务系统标识ID
	 * @param nodeid 设备节点ID
	 * @param desc 告警描述
	 * @param type 告警类型
	 * @param level 告警级别
	 */
	public void ServicesReceiveAlarmMessage(String bsid,String nodeid,String desc,String type,String level);
}

