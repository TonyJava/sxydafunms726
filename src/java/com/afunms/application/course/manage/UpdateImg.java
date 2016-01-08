package com.afunms.application.course.manage;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;




import javax.imageio.ImageIO;

public class UpdateImg {
    private Font font=null;// ����������������
    private Graphics2D g=null;
    private int fontsize=0;
    private int x=0;
    private

    int y=0;
    
    public static void main(String[] args){
	UpdateImg img = new UpdateImg();
	BufferedImage bufferImg = img.loadImageLocal("D:\\1.jpg");
	bufferImg = img.modifyImage(bufferImg,"˧��",100,100);
	bufferImg = img.modifyImage(bufferImg,"qoeifj",100,200);
	img.writeImageLocal("2.jpg",bufferImg);
    }

    /**
     * ���뱾��ͼƬ��������
     */
    public BufferedImage loadImageLocal(String imgName) {
	try{
	    return ImageIO.read(new File(imgName));
	}catch(IOException e){
	    System.out.println(e.getMessage());
	}
	return null;
    }

    /**
     * ��������ͼƬ��������
     */
    public BufferedImage loadImageUrl(String imgName) {
	try{
	    URL url=new URL(imgName);
	    return ImageIO.read(url);
	}catch(IOException e){
	    System.out.println(e.getMessage());
	}
	return null;
    }

    /**
     * ������ͼƬ������
     */
    public boolean writeImageLocal(String newImage, BufferedImage img) {
    	if(newImage != null && img != null){
    		try{
    			String path = "";
    			File outputfile=new File(newImage);
    			ImageIO.write(img,"jpg",outputfile);
    			return true;
    		}catch(IOException e){
    			e.printStackTrace();
    			System.out.println(e.getMessage()+"--������������----&&&&&&---######--ͼƬ���ɳ���--������������----&&&&&&---######----");
    		}
		}
    	return false;
    }

    /**
     * �趨���ֵ������
     */
    public void setFont(String fontStyle, int fontSize) {
	this.fontsize=fontSize;
	this.font=new Font(fontStyle,Font.PLAIN,fontSize);
	
    }

    /**
     * �޸�ͼƬ,�����޸ĺ��ͼƬ��������ֻ���һ���ı���
     */
    public BufferedImage modifyImage(BufferedImage img, Object content, int x, int y) {
	try{
	    int w=img.getWidth();
	    int h=img.getHeight();
	    g=img.createGraphics();
	    g.setBackground(Color.WHITE);
	    g.setColor(Color.RED);
	    if(this.font != null)
		g.setFont(this.font);
	    // ��֤���λ�õ�������ͺ�����
	    if(x >= h || y >= w){
		this.x=h - this.fontsize + 2;
		this.y=w;
	    }else{
		this.x=x;
		this.y=y;
	    }
	    java.util.Random ran=new java.util.Random();   
	    for(int i=0; i<20;i++){
		g.setColor(Color.BLACK);
		int x1=ran.nextInt(w);
		int y1=ran.nextInt(h);
		g.drawLine(x1,y1,x1,y1);		
	    }
	    if(content != null){
		g.drawString(content.toString(),this.x,this.y);
	    }
	    g.dispose();
	}catch(Exception e){
	    System.out.println(e.getMessage());
	}
	return img;
    }

    /**
     * �޸�ͼƬ,�����޸ĺ��ͼƬ���������������ı��Σ� xory��true��ʾ��������һ���������false��ʾ�����ݶ������
     */
    public BufferedImage modifyImage(BufferedImage img, Object[] contentArr, int x, int y, boolean xory) {
	try{
	    int w=img.getWidth();
	    int h=img.getHeight();
	    g=img.createGraphics();
	    g.setBackground(Color.WHITE);
	    g.setColor(Color.RED);
	    if(this.font != null)
		g.setFont(this.font);
	    // ��֤���λ�õ�������ͺ�����
	    if(x >= h || y >= w){
		this.x=h - this.fontsize + 2;
		this.y=w;
	    }else{
		this.x=x;
		this.y=y;
	    }		
	    
	    if(contentArr != null){
		int arrlen=contentArr.length;
		if(xory){
		    for(int i=0; i < arrlen; i++){
			g.drawString(contentArr[i].toString(),this.x,this.y);
			this.x+=contentArr[i].toString().length() * this.fontsize / 2 + 5;// ���¼����ı����λ��
		    }
		}else{
		    for(int i=0; i < arrlen; i++){
			g.drawString(contentArr[i].toString(),this.x,this.y);
			this.y+=this.fontsize + 2;// ���¼����ı����λ��
		    }
		}
	    }
	    
	    g.dispose();
	}catch(Exception e){
	    System.out.println(e.getMessage());
	}
	return img;
    }
}