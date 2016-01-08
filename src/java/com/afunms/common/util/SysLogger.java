/**
 * <p>Description:logger,writes error and debug information within system running</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-06
 */

package com.afunms.common.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.afunms.initialize.ResourceCenter;

public class SysLogger
{
   private static Log logger = LogFactory.getLog(SysLogger.class);

   private SysLogger()
   {
   }

   private static void init()
   {
	   
       PropertyConfigurator.configure(ResourceCenter.getInstance().getSysPath() + "WEB-INF/classes/log4j.properties");       
   }

   public static void info(String infoMessage)
   {      
       if(ResourceCenter.getInstance().isLogInfo())
       {
    	   //init();
    	   logger.info(infoMessage);
       }         
   }

   public static void error(String errorMessage, Exception ex)
   {
	   if(ResourceCenter.getInstance().isLogError())
	   {	   
          init();
          if(ex.getMessage()==null)
             logger.error(errorMessage);
          else
        	 logger.error(errorMessage + ex.getMessage()); 
          ex.printStackTrace();
	   }   
   }
   
   public static void error(String errorMessage)
   {
	   if(ResourceCenter.getInstance().isLogError())
	   {	   
          init();
          logger.error(errorMessage);
	   }   
   }   
}
