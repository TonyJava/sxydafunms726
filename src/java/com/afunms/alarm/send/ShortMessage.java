package com.afunms.alarm.send;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;
import org.apache.commons.net.ftp.FTPClient;

public class ShortMessage {
	
	//大系统
	private String bigsystem;
	//小系统
	private String smallsystem;
	//报警级别
	private String level;
	//报警内容
	private String content;
	//报警时间
	private String faulttime;
	//报警ID
	private String faultID;
	//报警IP
	private String ip;
	
	/**
	 * 根据报警IP获得生成文件的路径
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
	 * 生成TXT文件
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
	        // System.out.println("写入xml的数据："+data);
	         
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
	 * 根据文件名和属性名获取属性对应值
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
	 * 上传文件
	 * @param ipaddress
	 * @param username
	 * @param password
	 * @param path
	 * @param portnumber
	 * @return
	 */
	public boolean uploadFile(String ipaddress,String username ,String password,String path,int portnumber)
	{
		
		  // 初始表示上传失败
		  boolean success = false;
		  // 创建FTPClient对象
		  FTPClient ftp = new FTPClient();
		  try {
		   int reply;
		   // 连接FTP服务器
		   ftp.connect(ipaddress, portnumber);
		   // 登录ftp
		   ftp.login(username, password);
		   // 看返回的值是不是230，如果是，表示登陆成功
		   reply = ftp.getReplyCode();
		   if(reply == 230){
			   File f = new File(path);
			   FileInputStream input = new FileInputStream(f);
			   // 将上传文件存储到指定目录
			   ftp.storeFile(f.getName(), input);
			   // 关闭输入流
			   input.close();
			   // 退出ftp
			   ftp.logout();
			   
			   //f.delete();//删除文件
			 
			// 表示上传成功
			   success = true;
		   }
		   
		  } catch (IOException e) {
			  
			 System.out.println("======================== ftp 连接异常---====");
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
	 * 发送txt文件
	 * @param ip  报警IP
	 * @param bigsystem 报警源所属大系统
	 * @param smallsystem  报警源所属小系统
	 * @param content  报警内容
	 * @param faultID  报警ID
	 */
	
	public void sendTxt(String ip,String bigsystem,String smallsystem,
			String content,String faultID){
		ShortMessage sm = new ShortMessage();
		String path = sm.getPathByIP(ip);
		sm.setIp(ip);
		sm.setBigsystem(bigsystem);
		sm.setSmallsystem(smallsystem);
		sm.setLevel(sm.getConfigInfomation("Ftpagentconfig", "Level"));//从配置文件中读取告警级别
		sm.setContent(content);
		sm.setFaulttime(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
		sm.setFaultID(faultID);
		sm.createTxt(path, sm);
		sm.uploadFile(sm.getConfigInfomation("Ftpagentconfig", "IpAddress"), sm.getConfigInfomation("Ftpagentconfig", "UserName"), 
				sm.getConfigInfomation("Ftpagentconfig", "PassWord"), path, Integer.valueOf(sm.getConfigInfomation("Ftpagentconfig", "PortNumber")));
	}
	
	/**
	 * 测试main方法
	 * @param args
	 */
	public static void main(String[] args) {
		ShortMessage shortMessage = new ShortMessage();
		shortMessage.sendTxt("10.43.196.112", "Jigui", "FY3B", "测试报警短信", "1");
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
