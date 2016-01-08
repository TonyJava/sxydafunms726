package com.afunms.linkReport.manager;

import com.afunms.common.base.BaseVo;

public class LinkReportInfo extends BaseVo {

	private int id;
	private String name;    	//报表名称
	private String type;		//报表类型day.week
	private String userName;	//收件人
	private String email;		//收件人邮箱
	private String emailTitle;	//标题
	private String emailContent; //内容
	private String attachmentFormat; //附件类型doc.pdf.exl
	private String ids;              //链路id
	private String terms;       //链路内容类型 up,down...
	private String sendTime;		 //发送时间日
	private String sendTime2;		 //发送时间周
	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
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
	public String getAttachmentFormat() {
		return attachmentFormat;
	}
	public void setAttachmentFormat(String attachmentFormat) {
		this.attachmentFormat = attachmentFormat;
	}
	public String getIds() {
		return ids;
	}
	public void setIds(String ids) {
		this.ids = ids;
	}
	public String getTerms() {
		return terms;
	}
	public void setTerms(String terms) {
		this.terms = terms;
	}
	public String getSendTime() {
		return sendTime;
	}
	public void setSendTime(String sendTime) {
		this.sendTime = sendTime;
	}
	public String getSendTime2() {
		return sendTime2;
	}
	public void setSendTime2(String sendTime2) {
		this.sendTime2 = sendTime2;
	}

	
}
