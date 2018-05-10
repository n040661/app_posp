package xdt.controller;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("verifycode")
public class VerifyCodeAction extends BaseAction {
	 public Color getRandColor(int fc, int bc)
	   {
	     Random random = new Random();
	     if (fc > 255)
	       fc = 255;
	     if (bc > 255)
	       bc = 255;
	     int r = fc + random.nextInt(bc - fc);
	     int g = fc + random.nextInt(bc - fc);
	     int b = fc + random.nextInt(bc - fc);
	     return new Color(r, g, b);
	   }
	  @RequestMapping(value = "verifyCode")
	   public String verifyCode( HttpServletRequest request, HttpServletResponse response) throws Exception
	   {
	     int width = 90; int height = 10;
	     BufferedImage image = new BufferedImage(width, height, 
	       1);
	 
	     Graphics g = image.getGraphics();
	 
	     Random random = new Random();
	 
	     g.setColor(getRandColor(200, 250));
	     g.fillRect(0, 0, width, height);
	 
	     g.setFont(new Font("Times New Roman", 0, 25));
	 
	     g.setColor(getRandColor(160, 200));
	     for (int i = 0; i < 155; i++) {
	       int x = random.nextInt(width);
	       int y = random.nextInt(height);
	       int xl = random.nextInt(12);
	       int yl = random.nextInt(12);
	       g.drawLine(x, y, x + xl, y + yl);
	     }
	 
	     String codeList = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890";
	 
	     String sRand = "";
	 
	     for (int i = 0; i < 4; i++) {
	       int a = random.nextInt(codeList.length() - 1);
	       String rand = codeList.substring(a, a + 1);
	       sRand = sRand + rand;
	 
	       g.setColor(
	         new Color(20 + random.nextInt(110), 20 + random
	         .nextInt(110), 20 + random.nextInt(110)));
//	       g.drawString(rand, 13 * i + 6, 16);
	       g.drawString(rand, 13 * i + 6, 30);
	     }
	 
	     request.setAttribute("verifycode", sRand);
	 
	     response.setHeader("Pragma", "no-cache");
	     response.setHeader("Cache-Control", "no-cache");
	     response.setDateHeader("Expires", 0L);
	     response.setContentType("image/jpeg");
	 
	     g.dispose();
	     try
	     {
	       ImageIO.write(image, "JPEG", response.getOutputStream());
	     } catch (Exception e) {
	       e.printStackTrace();
	     }
	     
	     return "pay/hengfeng/result";
	   }
	  @RequestMapping(value="vifyty")
	  public void vifyty(String checkcode,HttpServletRequest request,HttpServletResponse response) throws Exception {
		  	HttpSession session =request.getSession();
	        if(checkcode.equals("")||checkcode==null){  
	           outString(response, "请输入验证码");
	        }else{  
	            if(!checkcode.equalsIgnoreCase((String)session.getAttribute("randCheckCode"))){  
	            	 outString(response,"验证码错误");  
	            }else{  
	            	outString(response,"验证成功");  
	            }  
	        }  
	}
		
}
