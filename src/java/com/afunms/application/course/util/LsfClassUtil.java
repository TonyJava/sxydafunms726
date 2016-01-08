package com.afunms.application.course.util;

import java.awt.image.BufferedImage;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.afunms.application.course.manage.UpdateImg;
import com.afunms.application.course.model.LsfClassComprehensiveModel;

/**
 * lsf 监控 组
 * @author Administrator
 *
 */
public class LsfClassUtil {
	
	/**
	 * 根据数据信息生成图片
	 * @param ls
	 * @return
	 */
	public HashMap imgEdi(List ls){
		
		UpdateImg img = new UpdateImg();
		HashMap img_path_map = new HashMap();
		String img_path = "D:/zgqx/afunms/tomcat-6.0.18/webapps/afunms/application/course/dtree/img/sor.jpg";
		if(ls!=null){
			for(int i=0;i<ls.size();i++){
				String fileName = "D:/zgqx/afunms/tomcat-6.0.18/webapps/afunms/application/course/dtree/img/"+i+".jpg";
				LsfClassComprehensiveModel model = new LsfClassComprehensiveModel();
				model = (LsfClassComprehensiveModel)ls.get(i);
				String classname = model.getClass_name();
				String enable = model.getEnable();
				String logCount = model.getLogcoud();
				String master = model.getMaster();
				BufferedImage bufferImg = img.loadImageLocal(img_path);
				bufferImg = img.modifyImage(bufferImg,classname,10,10);
				bufferImg = img.modifyImage(bufferImg,enable,10,20);
				bufferImg = img.modifyImage(bufferImg,logCount,10,30);
				bufferImg = img.modifyImage(bufferImg,master,10,40);
				boolean flag = img.writeImageLocal(fileName,bufferImg);
				if(flag){
					System.out.println("----##########----------生成文件成功----##########----------"+fileName);
					img_path_map.put(classname, fileName);
				}
			}
		}
		return img_path_map;
	}
//	public static int Hour(Date time){
//		SimpleDateFormat st=new SimpleDateFormat("yyyyMMddHHmmss");
//		return Integer.parseInt(st.format(time));
//	}
//
//	public static Date StringToDate(String s){
//		Date time=new Date();
//		SimpleDateFormat sd=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//		try{
//			 time=sd.parse(s);
//		}
//		catch (ParseException e) { 
//			System.out.println("输入的日期格式有误！"); 
//		}
//		return time;
//	}
	
	
/**
 * 
 */
	public static int computeDateTime(String str_1,String str_2){
		int resultTime = 0;
		try {
			if(str_1!=null && str_2!=null){
				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
				SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
				long result = (
							(format.parse(str_2.replaceAll("\n", "")).getTime())
							- 
							(format2.parse(str_1.replaceAll("\n", "")).getTime())
							)/60000;
					resultTime = new Long(result).intValue();
		        }
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return resultTime;
	}
	
	public static void main(String[] args) {
//		String a="2012-06-08 11:01:30";
//		String b="2012-06-08 18:01:20";
//		String c="2012-06-09 01:01:01";
//		if(Hour(StringToDate(a))
//				      <=Hour(StringToDate(b))&&
//				      Hour(StringToDate(a))<
//				      Hour(StringToDate(c)))
//			System.out.println("成功");
//		else
//			System.out.println("失败");
		String s2="2012-06-08 06:10:10";
        String s1="2012-06-08 07:30:10";
        
//        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
//        try {
//			System.out.println((format.parse(s2).getTime() - format.parse(s1).getTime()) / 60000);
////        	System.out.println((format.parse(s2).getTime() - format.parse(s1).getTime()) /1000/60/60/24);
//		} catch (ParseException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
       System.out.println( computeDateTime(s1,s2));
	}

}
