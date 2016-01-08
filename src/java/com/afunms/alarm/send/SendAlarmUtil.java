package com.afunms.alarm.send;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.afunms.alarm.dao.AlarmWayDao;
import com.afunms.alarm.dao.AlarmWayDetailDao;
import com.afunms.alarm.model.AlarmIndicatorsNode;
import com.afunms.alarm.model.AlarmPort;
import com.afunms.alarm.model.AlarmWay;
import com.afunms.alarm.model.AlarmWayDetail;
import com.afunms.common.util.SysLogger;
import com.afunms.event.dao.AlarmInfoDao;
import com.afunms.event.model.AlarmInfo;
import com.afunms.event.model.CheckEvent;
import com.afunms.event.model.EventList;
import com.afunms.system.dao.AlertInfoServerDao;
import com.afunms.system.model.AlertInfoServer;
import com.afunms.util.AgentalarmControlutil;

public class SendAlarmUtil {
	
	public void sendAlarm(CheckEvent checkEvent, EventList eventList , AlarmIndicatorsNode alarmIndicatorsNode){
		//SysLogger.info(" #### �ȼ�: " + eventList.getLevel1() + " �澯ָ��: " + alarmIndicatorsNode.getName()+" ###");
		String alarmWayId = getAlarmWayId(eventList, alarmIndicatorsNode);
		if(alarmWayId!=null && !alarmWayId.equals("null")){
			//System.out.println("==================alarmWayId==========================="+alarmWayId);
			//System.out.println("==================alarmWayId==========================="+eventList.getContent());
			String alarmWayIdArray[] = alarmWayId.split(",");
			
			if(alarmWayIdArray!=null&&alarmWayIdArray.length>0){
				for(int j = 0;j<alarmWayIdArray.length;j++){
					AlarmWay alarmWay = null;
					if(alarmWayIdArray[j] != null && alarmWayIdArray[j].trim().length()>0){
						try{
							alarmWay = getAlarmWay(alarmWayIdArray[j]);
						}catch(Exception e){
							
						}
						if(alarmWay == null ){
							//SysLogger.info("### �澯ָ��: " + alarmIndicatorsNode.getName() +  " �޸澯��ʽ ֻ����ϵͳ�澯 ###");
							//Ĭ�������,��Ҫ����ϵͳ�¼�
							AlarmWayDetail alarmWayDetail = null;
							SendPageAlarm sendPageAlarm = new SendPageAlarm();
							sendPageAlarm.sendAlarm(eventList,alarmWayDetail);
						}else{
							if("1".equals(alarmWay.getIsPageAlarm())){
								AlarmWayDetail alarmWayDetail = null;
								SendPageAlarm sendPageAlarm = new SendPageAlarm();
								sendPageAlarm.sendAlarm(eventList,alarmWayDetail);
							}
							List<AlarmWayDetail> list = getAlarmWayDetail(alarmWayIdArray[j]);
							if(list == null || list.size() == 0){
								//SysLogger.info("### �澯ָ��: " + alarmIndicatorsNode.getName() +  " �޸澯��ϸ���� ���澯 ###");
							}else{
								SendAlarmFilter sendAlarmFilter = new SendAlarmFilter();
								for(int i = 0 ; i < list.size(); i++){
									AlarmWayDetail alarmWayDetail = list.get(i);
									
									
									boolean result = sendAlarmFilter.isSendAlarm(checkEvent , alarmIndicatorsNode , alarmWay , alarmWayDetail);
									//SysLogger.info("result---------���Ͷ���------------"+result+"=="+alarmWay.getIsMailAlarm()+"===");
									if(result){
										try{
										//	SysLogger.info("#####=========="+alarmWayDetail.getAlarmCategory());
											SendAlarmDispatcher.sendAlarm(eventList , alarmWayDetail);
										}catch(Exception e){
											e.printStackTrace();
										}
									}
								}
							}
						}
					}
				}
			}
		}
		//Ĭ�������,�������������澯����д����
		// �������澯����д����
		AlarmInfo alarminfo = new AlarmInfo();
		alarminfo.setContent(eventList.getContent());
		alarminfo.setIpaddress(eventList.getEventlocation());
		alarminfo.setLevel1(new Integer(2));
		alarminfo.setRecordtime(Calendar.getInstance());
		alarminfo.setType("");
		AgentalarmControlutil.Getnms_alarminfoIntosql(alarminfo);
//		AlarmInfoDao alarmdao = new AlarmInfoDao();
//		try {
//			alarmdao.save(alarminfo);
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			alarmdao.close();
//		}
	}
	
	public void sendPortAlarm(CheckEvent checkEvent, EventList eventList ,int alarmLevel,AlarmPort portNode){
		//SysLogger.info(" #### �ȼ�: " + eventList.getLevel1() + " �澯ָ��: " + alarmIndicatorsNode.getName()+" ###");
		//String alarmWayId = getAlarmWayId(eventList, alarmIndicatorsNode);
		String alarmWayId = "";
		if(alarmLevel == 1){
			alarmWayId = portNode.getWayin1();
		} else if (alarmLevel == 2){
			alarmWayId = portNode.getWayin2();
		}else if (alarmLevel == 3){
			alarmWayId = portNode.getWayin3();
		}else if (alarmLevel == 4){
			alarmWayId = portNode.getWayout1();
		}else if (alarmLevel == 5){
			alarmWayId = portNode.getWayout2();
		}else if (alarmLevel == 6){
			alarmWayId = portNode.getWayout3();
		}
		AlarmWay alarmWay = null;
		if(alarmWayId != null && alarmWayId.trim().length()>0){
			try{
				alarmWay = getAlarmWay(alarmWayId);
			}catch(Exception e){
				
			}
		}
		//Ĭ�������,�������������澯����д����
		// �������澯����д����
		AlarmInfo alarminfo = new AlarmInfo();
		alarminfo.setContent(eventList.getContent());
		alarminfo.setIpaddress(eventList.getEventlocation());
		alarminfo.setLevel1(new Integer(2));
		alarminfo.setRecordtime(Calendar.getInstance());
		alarminfo.setType("");
		AlarmInfoDao alarmdao = new AlarmInfoDao();
		try {
			alarmdao.save(alarminfo);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			alarmdao.close();
		}
		AlarmIndicatorsNode alarmIndicatorsNode=new AlarmIndicatorsNode();
		alarmIndicatorsNode.setId(portNode.getId());
		alarmIndicatorsNode.setNodeid(portNode.getId()+"");
		alarmIndicatorsNode.setType(portNode.getType());
		alarmIndicatorsNode.setSubtype(portNode.getSubtype());
		alarmIndicatorsNode.setName(portNode.getName());
//		alarmIndicatorsNode.setWay0(portNode.getWay1());
//		alarmIndicatorsNode.setWay1(portNode.getWay2());
//		alarmIndicatorsNode.setWay2(portNode.getWay3());
		
		
		if(alarmWay == null ){
			//SysLogger.info("### �澯ָ��: " + alarmIndicatorsNode.getName() +  " �޸澯��ʽ ֻ����ϵͳ�澯 ###");
			//Ĭ�������,��Ҫ����ϵͳ�¼�
			AlarmWayDetail alarmWayDetail = null;
			SendPageAlarm sendPageAlarm = new SendPageAlarm();
			sendPageAlarm.sendAlarm(eventList,alarmWayDetail);
		}else{
			if("1".equals(alarmWay.getIsPageAlarm())){
				AlarmWayDetail alarmWayDetail = null;
				SendPageAlarm sendPageAlarm = new SendPageAlarm();
				sendPageAlarm.sendAlarm(eventList,alarmWayDetail);
			}
			List<AlarmWayDetail> list = getAlarmWayDetail(alarmWayId);
			if(list == null || list.size() == 0){
				//SysLogger.info("### �澯ָ��: " + alarmIndicatorsNode.getName() +  " �޸澯��ϸ���� ���澯 ###");
			}else{
				SendAlarmFilter sendAlarmFilter = new SendAlarmFilter();
				for(int i = 0 ; i < list.size(); i++){
					AlarmWayDetail alarmWayDetail = list.get(i);
					boolean result = sendAlarmFilter.isSendAlarm(checkEvent , alarmIndicatorsNode , alarmWay , alarmWayDetail);
					if(result){
						SendAlarmDispatcher.sendAlarm(eventList , alarmWayDetail);
					}
				}
			}
		}	
	}
	/**
	 * ���͸澯,ֻ��Ҫ���η��͸澯��Ϣ,������Ҫ�����ж��м��θ澯����,ֻҪ�����澯�ͷ�������ô˷���
	 * @param alarmWayId
	 * @param eventList
	 * @return
	 */
	public void sendAlarmNoIndicator(String alarmWayId, EventList eventList){
		//SysLogger.info(" #### �ȼ�: " + eventList.getLevel1() + " �澯ָ��: " +" ###");
		//String alarmWayId = getAlarmWayId(eventList, alarmIndicatorsNode);
		AlarmWay alarmWay = null;
		if(alarmWayId != null && alarmWayId.trim().length()>0){
			try{
				alarmWay = getAlarmWay(alarmWayId);
			}catch(Exception e){
				
			}
		}
		//Ĭ�������,�������������澯����д����
		// �������澯����д����
		AlarmInfo alarminfo = new AlarmInfo();
		alarminfo.setContent(eventList.getContent());
		alarminfo.setIpaddress(eventList.getEventlocation());
		alarminfo.setLevel1(new Integer(2));
		alarminfo.setRecordtime(Calendar.getInstance());
		alarminfo.setType("");
		AlarmInfoDao alarmdao = new AlarmInfoDao();
		try {
			alarmdao.save(alarminfo);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			alarmdao.close();
		}
		
		
		
		if(alarmWay == null ){
			//SysLogger.info("### �澯ָ��: " + alarmIndicatorsNode.getName() +  " �޸澯��ʽ ֻ����ϵͳ�澯 ###");
			//Ĭ�������,��Ҫ����ϵͳ�¼�
			AlarmWayDetail alarmWayDetail = null;
			SendPageAlarm sendPageAlarm = new SendPageAlarm();
			sendPageAlarm.sendAlarm(eventList,alarmWayDetail);
		}else{
			if("1".equals(alarmWay.getIsPageAlarm())){
				AlarmWayDetail alarmWayDetail = null;
				SendPageAlarm sendPageAlarm = new SendPageAlarm();
				sendPageAlarm.sendAlarm(eventList,alarmWayDetail);
			}
			List<AlarmWayDetail> list = getAlarmWayDetail(alarmWayId);
			if(list == null || list.size() == 0){
				//SysLogger.info("### �澯ָ��: " +"1122" +  " �޸澯��ϸ���� ���澯 ###");
			}else{
				SendAlarmFilter sendAlarmFilter = new SendAlarmFilter();
				for(int i = 0 ; i < list.size(); i++){
					AlarmWayDetail alarmWayDetail = list.get(i);
					//boolean result = sendAlarmFilter.isSendAlarm(checkEvent , alarmIndicatorsNode , alarmWay , alarmWayDetail);
					//if(result){
						SendAlarmDispatcher.sendAlarm(eventList , alarmWayDetail);
					//}
				}
			}
		}	
	}
	
	
	
	
	
	
	/**
	 * 
	 * ���͸澯���Ը澯���й���
	 * @param alarmWayId ���͸澯�����÷�ʽ��id
	 * @param eventList
	 * konglq
	 * 
	 */
	public void sendAlarmNoIndicatorOther(CheckEvent checkEvent, EventList eventList , AlarmIndicatorsNode alarmIndicatorsNode){
		SysLogger.info(" #### �ȼ�: " + eventList.getLevel1() + " �澯ָ��: " + alarmIndicatorsNode.getName()+" ###");
		String alarmWayId = getAlarmWayId(eventList, alarmIndicatorsNode);
		if(alarmWayId!=null && !alarmWayId.equals("null")){
			//System.out.println("==================alarmWayId==========================="+alarmWayId);
			//System.out.println("==================alarmWayId==========================="+eventList.getContent());
			String alarmWayIdArray[] = alarmWayId.split(",");
			//System.out.println("==================alarmWayIdArray[]===��С========================"+alarmWayIdArray.length);
			if(alarmWayIdArray!=null&&alarmWayIdArray.length>0){
				for(int j = 0;j<alarmWayIdArray.length;j++){
					AlarmWay alarmWay = null;
					if(alarmWayIdArray[j] != null && alarmWayIdArray[j].trim().length()>0){
						try{
							
			
							
							alarmWay = getAlarmWay(alarmWayIdArray[j]);
						}catch(Exception e){
							
						}
						if(alarmWay == null ){
							//SysLogger.info("### �澯ָ��: " + alarmIndicatorsNode.getName() +  " �޸澯��ʽ ֻ����ϵͳ�澯 ###");
							//Ĭ�������,��Ҫ����ϵͳ�¼�
							AlarmWayDetail alarmWayDetail = null;
							SendPageAlarm sendPageAlarm = new SendPageAlarm();
							sendPageAlarm.sendAlarm(eventList,alarmWayDetail);
						}else{
							if("1".equals(alarmWay.getIsPageAlarm())){
								AlarmWayDetail alarmWayDetail = null;
								SendPageAlarm sendPageAlarm = new SendPageAlarm();
								sendPageAlarm.sendAlarm(eventList,alarmWayDetail);
							}
							//System.out.println("===alarmWayIdArray[j]=="+alarmWayIdArray[j]);
							
							List<AlarmWayDetail> list = getAlarmWayDetail(alarmWayIdArray[j]);
							if(list == null || list.size() == 0){
								//SysLogger.info("### �澯ָ��: " + alarmIndicatorsNode.getName() +  " �޸澯��ϸ���� ���澯 ###");
							}else{
								AlarmTimesFilter sendAlarmFilter = new AlarmTimesFilter();
								for(int i = 0 ; i < list.size(); i++){
									AlarmWayDetail alarmWayDetail = list.get(i);
									
									///System.out.println("======tt===="+alarmWayDetail.getId());
									boolean result = sendAlarmFilter.isSendAlarmOther(checkEvent , alarmIndicatorsNode , alarmWay , alarmWayDetail);
									//SysLogger.info("result---------���Ͷ���------------"+result+"=="+alarmWay.getIsMailAlarm()+"===");
									if(result){
										try{
										//	SysLogger.info("#####=========="+alarmWayDetail.getAlarmCategory());
											SendAlarmDispatcher.sendAlarm(eventList , alarmWayDetail);
										}catch(Exception e){
											e.printStackTrace();
										}
									}
								}
							}
						}
					}
				}
			}
		}
		//Ĭ�������,�������������澯����д����
		// �������澯����д����
		AlarmInfo alarminfo = new AlarmInfo();
		alarminfo.setContent(eventList.getContent());
		alarminfo.setIpaddress(eventList.getEventlocation());
		alarminfo.setLevel1(new Integer(2));
		alarminfo.setRecordtime(Calendar.getInstance());
		alarminfo.setType("");
		AlarmInfoDao alarmdao = new AlarmInfoDao();
		try {
			alarmdao.save(alarminfo);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			alarmdao.close();
		}
	}
	
	
	/**
	 * ��ȡ��澯��ʽ�� id
	 * @param eventList
	 * @param alarmIndicators
	 * @return
	 */
	private String getAlarmWayId(EventList eventList , AlarmIndicatorsNode alarmIndicatorsNode){
		String alarmWayId = "";
		if(eventList.getLevel1() == 1){
			alarmWayId = alarmIndicatorsNode.getWay0();
		}else if( eventList.getLevel1() == 2 ){
			alarmWayId = alarmIndicatorsNode.getWay1();
		}else if( eventList.getLevel1() == 3 ){
			alarmWayId = alarmIndicatorsNode.getWay2();
		}
		return alarmWayId;
	}
	
	/**
	 * ��ȡ��澯��ʽ�� id
	 * @param eventList
	 * @param alarmIndicators
	 * @return
	 */
	private AlarmWay getAlarmWay(String alarmWayId){
		AlarmWay alarmWay = null;
		AlarmWayDao alarmWayDao = new AlarmWayDao();
		try {
			
			
			alarmWayId=alarmWayId.replaceAll(",", "").trim();
			System.out.println("========alarmWayId="+alarmWayId);
			alarmWay = (AlarmWay)alarmWayDao.findByID(alarmWayId);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		} finally {
			alarmWayDao.close();
		}
		return alarmWay;
	}
	
	/**
	 * ��ȡ��澯�ķ�ʽ����ϸ����
	 * @param eventList
	 * @param alarmIndicators
	 * @return
	 */
	private List<AlarmWayDetail> getAlarmWayDetail(String alarmWayId){
		List<AlarmWayDetail> list = null;
		AlarmWayDetailDao alarmWayDetailDao = new AlarmWayDetailDao();
		try {
			list = (List<AlarmWayDetail>)alarmWayDetailDao.findByAlarmWayId(alarmWayId);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			alarmWayDetailDao.close();
		}
		return list;
	}
	
	public static void main(String[] args) {
		String alarmWayId = "115";
		String alarmWayIdArray[] = alarmWayId.split(",");
		for(int j = 0;j<alarmWayIdArray.length;j++){
			System.out.println(alarmWayIdArray[j]);
		}
	}
}
