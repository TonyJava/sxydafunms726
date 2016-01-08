package com.afunms.initialize;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class RandomValidateCode {
	public static final String RANDOMCODEKEY = "RANDOMVALIDATECODEKEY";//鏀惧埌session涓殑key
    private Random random = new Random();
    private String randString = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";//闅忔満浜х敓鐨勫瓧绗︿覆
    
    private int width = 80;//鍥剧墖瀹�
    private int height = 26;//鍥剧墖楂�
    private int lineSize = 40;//骞叉壈绾挎暟閲�
    private int stringNum = 4;//闅忔満浜х敓瀛楃鏁伴噺
    /*
     * 鑾峰緱瀛椾綋
     */
    private Font getFont(){
        return new Font("Fixedsys",Font.CENTER_BASELINE,18);
    }
    /*
     * 鑾峰緱棰滆壊
     */
    private Color getRandColor(int fc,int bc){
        if(fc > 255)
            fc = 255;
        if(bc > 255)
            bc = 255;
        int r = fc + random.nextInt(bc-fc-16);
        int g = fc + random.nextInt(bc-fc-14);
        int b = fc + random.nextInt(bc-fc-18);
        return new Color(r,g,b);
    }
    /**
     * 鐢熸垚闅忔満鍥剧墖
     */
    public void getRandcode(HttpServletRequest request,
            HttpServletResponse response) {
        HttpSession session = request.getSession();
        //BufferedImage绫绘槸鍏锋湁缂撳啿鍖虹殑Image绫�Image绫绘槸鐢ㄤ簬鎻忚堪鍥惧儚淇℃伅鐨勭被
        BufferedImage image = new BufferedImage(width,height,BufferedImage.TYPE_INT_BGR);
        Graphics g = image.getGraphics();//浜х敓Image瀵硅薄鐨凣raphics瀵硅薄,鏀瑰璞″彲浠ュ湪鍥惧儚涓婅繘琛屽悇绉嶇粯鍒舵搷浣�
        g.fillRect(0, 0, width, height);
        g.setFont(new Font("Times New Roman",Font.ROMAN_BASELINE,18));
        g.setColor(getRandColor(110, 133));
        //缁樺埗骞叉壈绾�
        for(int i=0;i<=lineSize;i++){
            drowLine(g);
        }
        //缁樺埗闅忔満瀛楃
        String randomString = "";
        for(int i=1;i<=stringNum;i++){
            randomString=drowString(g,randomString,i);
        }
        session.removeAttribute(RANDOMCODEKEY);
        session.setAttribute(RANDOMCODEKEY, randomString);
        System.out.println(randomString);
        g.dispose();
        try {
            ImageIO.write(image, "JPEG", response.getOutputStream());//灏嗗唴瀛樹腑鐨勫浘鐗囬�杩囨祦鍔ㄥ舰寮忚緭鍑哄埌瀹㈡埛绔�
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /*
     * 缁樺埗瀛楃涓�
     */
    private String drowString(Graphics g,String randomString,int i){
        g.setFont(getFont());
        g.setColor(new Color(random.nextInt(101),random.nextInt(111),random.nextInt(121)));
        String rand = String.valueOf(getRandomString(random.nextInt(randString.length())));
        randomString +=rand;
        g.translate(random.nextInt(3), random.nextInt(3));
        g.drawString(rand, 13*i, 16);
        return randomString;
    }
    /*
     * 缁樺埗骞叉壈绾�
     */
    private void drowLine(Graphics g){
        int x = random.nextInt(width);
        int y = random.nextInt(height);
        int xl = random.nextInt(13);
        int yl = random.nextInt(15);
        g.drawLine(x, y, x+xl, y+yl);
    }
    /*
     * 鑾峰彇闅忔満鐨勫瓧绗�
     */
    public String getRandomString(int num){
        return String.valueOf(randString.charAt(num));
    }

}
