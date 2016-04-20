package com.funday.data;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class SaveImg {
	public static final String SAVE_URL = "../../workspace/Funday/WebContent/files/article/";
	
	public static String saveImg(String getUrl,String fileName){		

		
		String saveUrl = "";
		byte[] btImg = getImageFromNetByUrl(getUrl);  
		
      
		if(null != btImg && btImg.length > 0){  
            System.out.println("读取到：" + btImg.length + " 字节");  
              
            saveUrl = writeImageToDisk(btImg, fileName);  
        }else{  
            System.out.println("没有从该连接获得内容");  
        } 
        return saveUrl;
	}
	
	public static boolean isFileExist(String fileName){
		String userDir = System.getProperty("user.dir");
		System.out.println(userDir);
		
		boolean isExist = false;
		String saveUrl = SAVE_URL + fileName;
		File file = new File(SAVE_URL + fileName); 
		if(file.exists()){
         	System.out.println("文件已存在");
         	isExist = true;         	
         }else{
        	 System.out.println("文件不存在");
         }
		return isExist;
	}
	
	
	/** 
     * 将图片写入到磁盘 
     * @param img 图片数据流 
     * @param fileName 文件保存时的名称 
     */  
    public static String writeImageToDisk(byte[] img, String fileName){  
        String saveUrl = SAVE_URL + fileName;
    	try {  
            File file = new File(SAVE_URL + fileName);  
            if(file.exists()){
            	System.out.println("文件已存在");
            	
            }
            FileOutputStream fops = new FileOutputStream(file);             
            
            fops.write(img);  
            fops.flush();  
            fops.close();  
            //System.out.println("图片已经写入到F盘");  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
        return saveUrl;
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
}
