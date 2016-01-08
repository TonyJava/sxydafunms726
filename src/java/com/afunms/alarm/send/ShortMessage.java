package com.afunms.alarm.send;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;
import org.apache.commons.net.ftp.FTPClient;

public class ShortMessage {
	
	//��ϵͳ
	private String bigsystem;
	//Сϵͳ
	private String smallsystem;
	//��������
	private String level;
	//��������
	private String content;
	//����ʱ��
	private String faulttime;
	//����ID
	private String faultID;
	//����IP
	private String ip;
	
	/**
	 * ���ݱ���IP��������ļ���·��
	 * @param ip
	 * @return
	 */
	public String getPathByIP(String ip){
		
		String str = ip.replace(".", "");
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
		str = (str+"_"+formatter.format(new Date()));
		
	//	System.out.println("===="+(this.getClass().getResource("/").toString()).replaceAll("file:/", ""));
		
		//System.out.println("===="+System.getProperty("user.dir"));
		
		//String path2=
		
		
		
		String path = (System.getProperty("user.dir")+"/sms"+"/"+str+".txt");
		return path;
	}
	
	/**
	 * ����TXT�ļ�
	 * @param path
	 * @param sm
	 */
	public void createTxt(String path ,ShortMessage sm){
		try {
			//File file = new File(path);
			//BufferedWriter ow = new BufferedWriter(new FileWriter(file));
			//OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(path),"UTF-8");
			BufferedWriter out;

			
			//FileWriter ff = new FileWriter(file);
	         java.io.FileOutputStream writerStream = new java.io.FileOutputStream(path);    
	         out = new BufferedWriter(new java.io.OutputStreamWriter(writerStream, "UTF-8"));  

	         //utput = new BufferedWriter(new FileWriter(f));
	        // System.out.println("д��xml�����ݣ�"+data);
	         
	         //utput.write(data);
	       

			
			
			out.write("Bigsystem="+sm.getBigsystem()); 
			out.write("\r\n");
			out.write("Smallsystem="+sm.getSmallsystem());
			out.write("\r\n");
			out.write("Level="+sm.getLevel());
			out.write("\r\n");
			out.write("Content="+sm.getContent());
			out.write("\r\n");
			out.write("Time="+sm.getFaulttime()); 
			out.write("\r\n");
			out.write("FaultID="+sm.getFaultID()); 	
			
			out.close();
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	
	
	
	/**
	 * �����ļ�������������ȡ���Զ�Ӧֵ
	 * @param finename
	 * @param itemIndex
	 * @return
	 */
	public String getConfigInfomation(String finename,String itemIndex) {
        try {
            ResourceBundle resource = ResourceBundle.getBundle(finename);
            return resource.getString(itemIndex);
        } catch (Exception e) {
            return "";
        }
    }
	/**
	 * �ϴ��ļ�
	 * @param ipaddress
	 * @param username
	 * @param password
	 * @param path
	 * @param portnumber
	 * @return
	 */
	public boolean uploadFile(String ipaddress,String username ,String password,String path,int portnumber)
	{
		
		  // ��ʼ��ʾ�ϴ�ʧ��
		  boolean success = false;
		  // ����FTPClient����
		  FTPClient ftp = new FTPClient();
		  try {
		   int reply;
		   // ����FTP������
		   ftp.connect(ipaddress, portnumber);
		   // ��¼ftp
		   ftp.login(username, password);
		   // �����ص�ֵ�ǲ���230������ǣ���ʾ��½�ɹ�
		   reply = ftp.getReplyCode();
		   if(reply == 230){
			   File f = new File(path);
			   FileInputStream input = new FileInputStream(f);
			   // ���ϴ��ļ��洢��ָ��Ŀ¼
			   ftp.storeFile(f.getName(), input);
			   // �ر�������
			   input.close();
			   // �˳�ftp
			   ftp.logout();
			   
			   //f.delete();//ɾ���ļ�
			 
			// ��ʾ�ϴ��ɹ�
			   success = true;
		   }
		   
		  } catch (IOException e) {
			  
			 System.out.println("======================== ftp �����쳣---====");
		    // e.printStackTrace();
		  } finally {
		   if (ftp.isConnected()) {
		    try {
		     ftp.disconnect();
		    } catch (IOException ioe) {
		    	//ioe.printStackTrace();
		    }
		   }
		  }
		  return success;

	}
    
	

	/**
	 * ����txt�ļ�
	 * @param ip  ����IP
	 * @param bigsystem ����Դ������ϵͳ
	 * @param smallsystem  ����Դ����Сϵͳ
	 * @param content  ��������
	 * @param faultID  ����ID
	 */
	
	public void sendTxt(String ip,String bigsystem,String smallsystem,
			String content,String faultID){
		ShortMessage sm = new ShortMessage();
		String path = sm.getPathByIP(ip);
		sm.setIp(ip);
		sm.setBigsystem(bigsystem);
		sm.setSmallsystem(smallsystem);
		sm.setLevel(sm.getConfigInfomation("Ftpagentconfig", "Level"));//�������ļ��ж�ȡ�澯����
		sm.setContent(content);
		sm.setFaulttime(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
		sm.setFaultID(faultID);
		sm.createTxt(path, sm);
		sm.uploadFile(sm.getConfigInfomation("Ftpagentconfig", "IpAddress"), sm.getConfigInfomation("Ftpagentconfig", "UserName"), 
				sm.getConfigInfomation("Ftpagentconfig", "PassWord"), path, Integer.valueOf(sm.getConfigInfomation("Ftpagentconfig", "PortNumber")));
	}
	
	/**
	 * ����main����
	 * @param args
	 */
	public static void main(String[] args) {
		ShortMessage shortMessage = new ShortMessage();
		shortMessage.sendTxt("10.43.196.112", "Jigui", "FY3B", "���Ա�������", "1");
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

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getFaulttime() {
		return faulttime;
	}

	public void setFaulttime(String faulttime) {
		this.faulttime = faulttime;
	}

	public String getFaultID() {
		return faultID;
	}

	public void setFaultID(String faultID) {
		this.faultID = faultID;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}
	
	

}
