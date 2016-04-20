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
            System.out.println("��ȡ����" + btImg.length + " �ֽ�");  
              
            saveUrl = writeImageToDisk(btImg, fileName);  
        }else{  
            System.out.println("û�дӸ����ӻ������");  
        } 
        return saveUrl;
	}
	
	public static boolean isFileExist(String fileName){
		//String userDir = System.getProperty("user.dir");
		//System.out.println(userDir);
		
		boolean isExist = false;
		String saveUrl = SAVE_URL + fileName;
		File file = new File(SAVE_URL + fileName); 
		if(file.exists()){
         	System.out.println("�ļ��Ѵ���");
         	isExist = true;         	
         }else{
        	 System.out.println("�ļ�������");
         }
		return isExist;
	}
	
	
	/** 
     * ��ͼƬд�뵽���� 
     * @param img ͼƬ������ 
     * @param fileName �ļ�����ʱ������ 
     */  
    public static String writeImageToDisk(byte[] img, String fileName){  
        String saveUrl = SAVE_URL + fileName;
    	try {  
            File file = new File(SAVE_URL + fileName);  
            if(file.exists()){
            	System.out.println("�ļ��Ѵ���");
            	
            }
            FileOutputStream fops = new FileOutputStream(file);             
            
            fops.write(img);  
            fops.flush();  
            fops.close();  
            //System.out.println("ͼƬ�Ѿ�д�뵽F��");  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
        return saveUrl;
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
}
