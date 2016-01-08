/**
 * <p>Description:mapping table NMS_POSITION</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-07
 */

package com.afunms.event.model;

import com.afunms.common.base.BaseVo;

public class CheckEvent extends BaseVo implements java.io.Serializable
{
    private String name;
    private int alarmlevel;
   

    public java.lang.String getName() {
        return this.name;
    }

	public void setName(java.lang.String name) {
		this.name = name;
	}

    public Integer getAlarmlevel() {
        return this.alarmlevel;
    }

	public void setAlarmlevel(Integer alarmlevel) {
		this.alarmlevel = alarmlevel;
	}
	
	
	private String nodeid; //ly add  设备id
	/**
	 * @param nodeid the nodeid to set
	 */
	public void setNodeid(String nodeid) {
		this.nodeid = nodeid;
	}
	
	
	private String indicatorsName;//ly add  指标名称
	/**
	 * @param indicatorsName the indicatorsName to set
	 */
	public void setIndicatorsName(String indicatorsName) {
		this.indicatorsName = indicatorsName;
	}
	
	 private String type;//ly add  类型
	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}
	
	
	 private String subtype;//ly add  子类型
	/**
	 * @param subtype the subtype to set
	 */
	public void setSubtype(String subtype) {
		this.subtype = subtype;
	}
	
	 private String sindex;//ly add sIndex
		/**
	/**
	 * @param sindex the sindex to set
	 */
	public void setSindex(String sindex) {
		this.sindex = sindex;
	}


}
