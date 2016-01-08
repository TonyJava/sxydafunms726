/*
 * Created on 2010-06-24
 *
 */
package com.afunms.polling.task;

import com.afunms.cabinet.util.CabinetXML;
import com.afunms.common.util.SysLogger;





/**
 *3D机房数据更新task
 */
public class CabinetTask extends MonitorTask {
	
	public void run() {
		try{
			//重新生成3D机房数据xml文件
			CabinetXML cxml = new CabinetXML();
		   	cxml.CreateCabinetXML();
		}catch(Exception e){
			SysLogger.error(e.getMessage());
			e.printStackTrace();
			//若出现例外，则需要重新调用启动定时任务的方法
		}finally{
			SysLogger.info("********CabinetTask Thread Count : "+Thread.activeCount());
		}
	}
}
