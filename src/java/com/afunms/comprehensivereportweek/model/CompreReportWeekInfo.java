package com.afunms.comprehensivereportweek.model;

import com.afunms.common.base.BaseVo;

/**
 *
 * �ۺ��ܱ�model
 * @author jhl
 *
 */
public class CompreReportWeekInfo extends BaseVo {

	 //nms_compreReportWeek_resources
	private int id; //id
	private String reportName;  //������
	private String reportUserName; //���������
	private String reportUserEmail;//������email
	private String emailTitle;//�ʼ�����
	private String emailContent;//�ʼ�����
	private String attachmentBusiness;//����ҵ��
	private String reportType;	//��������
	private String ExpReportType;  //��������
	private String ids;//�豸id ������
	private String eventCount;//�豸�¼�ͳ��
	private	String hostAlarmCount; //�����豸�澯ͳ����ϸ
	private String netAlarmCount;  //�����豸�澯ͳ����ϸ
	private String eventAnalyze;  //�¼��澯���Ʒ���
	private String hostTopAnalyze;//�����豸TOP���Ʒ���
	private String netTopAnalyze; //�����豸TOP���Ʒ���
//	private String reportContentOption;//��������ѡ�� ������
	private int sendWeek; //����ʱ�� �ܱ�
	private int sendOtherDay;//��һ������
	private String sendTime;//ʱ��
	private String startTime;
	private String toTime;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getReportName() {
		return reportName;
	}
	public void setReportName(String reportName) {
		this.reportName = reportName;
	}
	public String getReportUserName() {
		return reportUserName;
	}
	public void setReportUserName(String reportUserName) {
		this.reportUserName = reportUserName;
	}
	public String getEmailTitle() {
		return emailTitle;
	}
	public void setEmailTitle(String emailTitle) {
		this.emailTitle = emailTitle;
	}
	public String getEmailContent() {
		return emailContent;
	}
	public void setEmailContent(String emailContent) {
		this.emailContent = emailContent;
	}
	public String getAttachmentBusiness() {
		return attachmentBusiness;
	}
	public void setAttachmentBusiness(String attachmentBusiness) {
		this.attachmentBusiness = attachmentBusiness;
	}
	public String getReportType() {
		return reportType;
	}
	public void setReportType(String reportType) {
		this.reportType = reportType;
	}
	public String getExpReportType() {
		return ExpReportType;
	}
	public void setExpReportType(String expReportType) {
		ExpReportType = expReportType;
	}
	public String getIds() {
		return ids;
	}
	public void setIds(String ids) {
		this.ids = ids;
	}
	public String getEventCount() {
		return eventCount;
	}
	public void setEventCount(String eventCount) {
		this.eventCount = eventCount;
	}
	public String getHostAlarmCount() {
		return hostAlarmCount;
	}
	public void setHostAlarmCount(String hostAlarmCount) {
		this.hostAlarmCount = hostAlarmCount;
	}
	public String getNetAlarmCount() {
		return netAlarmCount;
	}
	public void setNetAlarmCount(String netAlarmCount) {
		this.netAlarmCount = netAlarmCount;
	}
	public String getEventAnalyze() {
		return eventAnalyze;
	}
	public void setEventAnalyze(String eventAnalyze) {
		this.eventAnalyze = eventAnalyze;
	}
	public String getHostTopAnalyze() {
		return hostTopAnalyze;
	}
	public void setHostTopAnalyze(String hostTopAnalyze) {
		this.hostTopAnalyze = hostTopAnalyze;
	}
	public String getNetTopAnalyze() {
		return netTopAnalyze;
	}
	public void setNetTopAnalyze(String netTopAnalyze) {
		this.netTopAnalyze = netTopAnalyze;
	}
//	public String getReportContentOption() {
//		return reportContentOption;
//	}
//	public void setReportContentOption(String reportContentOption) {
//		this.reportContentOption = reportContentOption;
//	}
	public int getSendWeek() {
		return sendWeek;
	}
	public void setSendWeek(int sendWeek) {
		this.sendWeek = sendWeek;
	}
	public int getSendOtherDay() {
		return sendOtherDay;
	}
	public void setSendOtherDay(int sendOtherDay) {
		this.sendOtherDay = sendOtherDay;
	}
	public String getSendTime() {
		return sendTime;
	}
	public void setSendTime(String sendTime) {
		this.sendTime = sendTime;
	}
	public String getReportUserEmail() {
		return reportUserEmail;
	}
	public void setReportUserEmail(String reportUserEmail) {
		this.reportUserEmail = reportUserEmail;
	}
	public String getStartTime() {
		return startTime;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	public String getToTime() {
		return toTime;
	}
	public void setToTime(String toTime) {
		this.toTime = toTime;
	}
	
	
}










