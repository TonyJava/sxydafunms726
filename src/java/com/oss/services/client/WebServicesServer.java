package com.oss.services.client;

public interface WebServicesServer {
	public String example(String message);
	/**
	 * 
	 * @param bsid ҵ��ϵͳ��ʶID
	 * @param nodeid �豸�ڵ�ID
	 * @param desc �澯����
	 * @param type �澯����
	 * @param level �澯����
	 */
	public void ServicesReceiveAlarmMessage(String bsid,String nodeid,String desc,String type,String level);
}

