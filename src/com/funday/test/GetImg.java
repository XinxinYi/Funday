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
     * @˵�� �������ȡͼƬ������ 
     * @author ����ǿ 
     * @version 1.0 
     * @since 
     */  
    public class GetImg{  
        /** 
         * ���� 
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
            //bufImg.getGraphics().drawImage(image, 2, 2, 256, 256, null); //��ͼ
            /*
            
            BufferedImageBuilder bib = new BufferedImageBuilder();
            BufferedImage inputbig = new BufferedImage(256, 256,BufferedImage.TYPE_INT_BGR);
            
            BufferedImage bi = bib.bufferImage(image);
            inputbig.getGraphics().drawImage(bi, 2, 2, 256, 256, null); //��ͼ
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
                System.out.println("��ȡ����" + btImg.length + " �ֽ�");  
                String fileName = "�ٶ�.gif";  
                writeImageToDisk(btImg, fileName);  
            }else{  
                System.out.println("û�дӸ����ӻ������");  
            }  
            */
            targetZoomOut(url); 
            
        }  
        /** 
         * ��ͼƬд�뵽���� 
         * @param img ͼƬ������ 
         * @param fileName �ļ�����ʱ������ 
         */  
        public static void writeImageToDisk(byte[] img, String fileName){  
            try {  
                File file = new File(SaveImg.SAVE_URL + fileName);  
                FileOutputStream fops = new FileOutputStream(file);  

                
                
                fops.write(img);  
                fops.flush();  
                fops.close();  
                System.out.println("ͼƬ�Ѿ�д�뵽F��");  
            } catch (Exception e) {  
                e.printStackTrace();  
            }  
        }  
        /** 
         * ���ݵ�ַ������ݵ��ֽ��� 
         * @param strUrl �������ӵ�ַ 
         * @return 
         */  
        public static byte[] getImageFromNetByUrl(String strUrl){  
            try {  
                URL url = new URL(strUrl);  
                HttpURLConnection conn = (HttpURLConnection)url.openConnection();  
                conn.setRequestMethod("GET");  
                conn.setConnectTimeout(5 * 1000);  
                InputStream inStream = conn.getInputStream();//ͨ����������ȡͼƬ����  
                byte[] btImg = readInputStream(inStream);//�õ�ͼƬ�Ķ���������  
                return btImg;  
            } catch (Exception e) {  
                e.printStackTrace();  
            }  
            return null;  
        }  
        /** 
         * ���������л�ȡ���� 
         * @param inStream ������ 
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
        
        
        public static void targetZoomOut(String sourcePath){         //��Ŀ��ͼƬ��С��256*256������
        	//URL url = new URL(sourcePath);
        	
        	File file1=new File(sourcePath);             //��file1ȡ��ͼƬ����
            String name=file1.getName();
        	;
            try{
            Image image = 	Toolkit.getDefaultToolkit().createImage(GetImg.class.getResource(sourcePath));
            
            BufferedImageBuilder bib = new BufferedImageBuilder();
            BufferedImage inputbig = new BufferedImage(256, 256,BufferedImage.TYPE_INT_BGR);
            
            BufferedImage bi = bib.bufferImage(image);
            inputbig.getGraphics().drawImage(bi, 2, 2, 256, 256, null); //��ͼ
            
           


                File file2 =new File("F:/test/");            //��Ŀ¼������С��Ĺؼ�ͼ
                if(file2.exists()){
                     System.out.println("�༶Ŀ¼�Ѿ����ڲ���Ҫ��������");
                }else{
                  //���Ҫ�����Ķ༶Ŀ¼�����ڲ���Ҫ������
                   file2.mkdirs();
                 }
                ImageIO.write(inputbig, "gif", new File("F:/test/"+name));   //���䱣����C:/imageSort/targetPIC/��
            } catch (Exception ex) {ex.printStackTrace();}
        } 
        
    }  