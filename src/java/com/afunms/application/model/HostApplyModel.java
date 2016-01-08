package com.afunms.application.model;

import java.io.Serializable;
import com.afunms.common.base.BaseVo;

/**
 * @author HONGLI E-mail: ty564457881@163.com
 * @version ����ʱ�䣺Oct 9, 2011 2:08:14 PM
 * ��˵�� ������Ӧ��ģ����
 */
public class HostApplyModel extends BaseVo implements Serializable{

	private static final long serialVersionUID = 1L;

	/**
	 * ����
	 */
	private int id;
	
	/**
	 * ��������id
	 */
	private int nodeid;
	
	/**
	 * Ӧ������
	 */
	private String type;
	
	/**
	 * Ӧ��������
	 */
	private String subtype;

	/**
	 * ������IP��ַ
	 */
	private String ipaddres;
	
	/**
	 * �Ƿ���ʾ
	 */
	private boolean isShow;
	
	
	public int getNodeid() {
		return nodeid;
	}

	public void setNodeid(int nodeid) {
		this.nodeid = nodeid;
	}

	public String getIpaddres() {
		return ipaddres;
	}

	public void setIpaddres(String ipaddres) {
		this.ipaddres = ipaddres;
	}

	public boolean isShow() {
		return isShow;
	}

	public void setShow(boolean isShow) {
		this.isShow = isShow;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
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
}