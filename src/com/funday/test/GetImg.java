package com.funday.test;

  
    import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.imageio.ImageIO;

import com.funday.data.SaveImg;  
    /** 
     * @说明 从网络获取图片到本地 
     * @author 崔素强 
     * @version 1.0 
     * @since 
     */  
    public class GetImg{  
        /** 
         * 测试 
         * @param args 
         */  
        public static void main(String[] args) {  
            String url = "http://i1.hoopchina.com.cn/blogfile/201604/09/BbsImg146020809265243_354x207.gif"; 
            
            /*
            Image image = Toolkit.getDefaultToolkit().createImage(GetImg.class.getResource(url));
            System.out.println(image.getSource());
            
            BufferedImage bufImg = new BufferedImage(image.getWidth(null), image.getHeight(null),BufferedImage.TYPE_INT_RGB);  
            Graphics g = bufImg.createGraphics();  
            g.drawImage(image, 0, 0, null);  
            g.dispose();
            
            bufImg.createGraphics().drawImage(image, 0, 0, null);
            */
           // BufferedImage inputbig = new BufferedImage(256, 256,BufferedImage.TYPE_INT_BGR);
            //bufImg.getGraphics().drawImage(image, 2, 2, 256, 256, null); //画图
            /*
            
            BufferedImageBuilder bib = new BufferedImageBuilder();
            BufferedImage inputbig = new BufferedImage(256, 256,BufferedImage.TYPE_INT_BGR);
            
            BufferedImage bi = bib.bufferImage(image);
            inputbig.getGraphics().drawImage(bi, 2, 2, 256, 256, null); //画图
            */
            /*
           try {
			ImageIO.write(bufImg, "gif", new File("F:/test/66.gif"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
            
            byte[] btImg = getImageFromNetByUrl(url);  
            if(null != btImg && btImg.length > 0){  
                System.out.println("读取到：" + btImg.length + " 字节");  
                String fileName = "百度.gif";  
                writeImageToDisk(btImg, fileName);  
            }else{  
                System.out.println("没有从该连接获得内容");  
            }  
            */
            targetZoomOut(url); 
            
        }  
        /** 
         * 将图片写入到磁盘 
         * @param img 图片数据流 
         * @param fileName 文件保存时的名称 
         */  
        public static void writeImageToDisk(byte[] img, String fileName){  
            try {  
                File file = new File(SaveImg.SAVE_URL + fileName);  
                FileOutputStream fops = new FileOutputStream(file);  

                
                
                fops.write(img);  
                fops.flush();  
                fops.close();  
                System.out.println("图片已经写入到F盘");  
            } catch (Exception e) {  
                e.printStackTrace();  
            }  
        }  
        /** 
         * 根据地址获得数据的字节流 
         * @param strUrl 网络连接地址 
         * @return 
         */  
        public static byte[] getImageFromNetByUrl(String strUrl){  
            try {  
                URL url = new URL(strUrl);  
                HttpURLConnection conn = (HttpURLConnection)url.openConnection();  
                conn.setRequestMethod("GET");  
                conn.setConnectTimeout(5 * 1000);  
                InputStream inStream = conn.getInputStream();//通过输入流获取图片数据  
                byte[] btImg = readInputStream(inStream);//得到图片的二进制数据  
                return btImg;  
            } catch (Exception e) {  
                e.printStackTrace();  
            }  
            return null;  
        }  
        /** 
         * 从输入流中获取数据 
         * @param inStream 输入流 
         * @return 
         * @throws Exception 
         */  
        public static byte[] readInputStream(InputStream inStream) throws Exception{  
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();  
            byte[] buffer = new byte[1024];  
            int len = 0;  
            while( (len=inStream.read(buffer)) != -1 ){  
                outStream.write(buffer, 0, len);  
            }  
            inStream.close();  
            return outStream.toByteArray();  
        } 
        
        
        public static void targetZoomOut(String sourcePath){         //将目标图片缩小成256*256并保存
        	//URL url = new URL(sourcePath);
        	
        	File file1=new File(sourcePath);             //用file1取得图片名字
            String name=file1.getName();
        	;
            try{
            Image image = 	Toolkit.getDefaultToolkit().createImage(GetImg.class.getResource(sourcePath));
            
            BufferedImageBuilder bib = new BufferedImageBuilder();
            BufferedImage inputbig = new BufferedImage(256, 256,BufferedImage.TYPE_INT_BGR);
            
            BufferedImage bi = bib.bufferImage(image);
            inputbig.getGraphics().drawImage(bi, 2, 2, 256, 256, null); //画图
            
           


                File file2 =new File("F:/test/");            //此目录保存缩小后的关键图
                if(file2.exists()){
                     System.out.println("多级目录已经存在不需要创建！！");
                }else{
                  //如果要创建的多级目录不存在才需要创建。
                   file2.mkdirs();
                 }
                ImageIO.write(inputbig, "gif", new File("F:/test/"+name));   //将其保存在C:/imageSort/targetPIC/下
            } catch (Exception ex) {ex.printStackTrace();}
        } 
        
    }  