/**
 * <p>Description:mapping table NMS_POSITION</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-07
 */

package com.afunms.config.model;

import com.afunms.common.base.BaseVo;

public class Business extends BaseVo
{
    private String id;
    private String name;
    private String descr;
    private String pid;
    private String bigsystem="";
    private String smallsystem="";
    
    /* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Business [id=" + id + ", name=" + name + ", descr=" + descr
				+ ", pid=" + pid + ", bigsystem=" + bigsystem
				+ ", smallsystem=" + smallsystem + "]";
	}

	public void setId(String id)
    {
       this.id = id;
    }

    public String getId()
    {
       return id;
    }

    public void setName(String name)
    {
       this.name = name;
    }

    public String getName()
    {
       return name;
    }

    public void setDescr(String descr)
    {
       this.descr = descr;
    }

    public String getDescr()
    {
       return descr;
    }

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public String getBigsystem() {
		return bigsystem;
	}

	public void setBigsystem(String bigsystem) {
		this.bigsystem = bigsystem;
	}

	public String getSmallsystem() {
		return smallsystem;
	}

	public void setSmallsystem(String smallsystem) {
		this.smallsystem = smallsystem;
	}
	
	
}
