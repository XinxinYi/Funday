package com.funday.data;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;  

public class ModifyHtml {
	
		    private static final String regEx_script = "<script[^>]*?>[\\s\\S]*?<\\/script>"; // ����script��������ʽ  
		    private static final String regEx_style = "<style[^>]*?>[\\s\\S]*?<\\/style>"; // ����style��������ʽ  
		    private static final String regEx_html = "<[^>]+>"; // ����HTML��ǩ��������ʽ  
		    private static final String regEx_space = "\\s*|\t|\r|\n";//����ո�س����з�  
		   
		      
		    /** 
		     * @param htmlStr 
		     * @return 
		     *  ɾ��Html��ǩ 
		     */ 
		    //ȥ�����б�ǩ
		    public static String delHTMLTag(String htmlStr) {  
		        Pattern p_script = Pattern.compile(regEx_script, Pattern.CASE_INSENSITIVE);  
		        Matcher m_script = p_script.matcher(htmlStr);  
		        htmlStr = m_script.replaceAll(""); // ����script��ǩ  
		  
		        Pattern p_style = Pattern.compile(regEx_style, Pattern.CASE_INSENSITIVE);  
		        Matcher m_style = p_style.matcher(htmlStr);  
		        htmlStr = m_style.replaceAll(""); // ����style��ǩ  
		  
		        Pattern p_html = Pattern.compile(regEx_html, Pattern.CASE_INSENSITIVE);  
		        Matcher m_html = p_html.matcher(htmlStr);  
		        htmlStr = m_html.replaceAll(""); // ����html��ǩ  
		  
		        Pattern p_space = Pattern.compile(regEx_space, Pattern.CASE_INSENSITIVE);  
		        Matcher m_space = p_space.matcher(htmlStr);  
		        htmlStr = m_space.replaceAll(""); // ���˿ո�س���ǩ  
		        return htmlStr.trim(); // �����ı��ַ���  
		    } 
		    //ȥ���ո�
		    public static String getTextFromHtml(String htmlStr){  
		    	htmlStr = removeStyle(htmlStr);  
		        //htmlStr = htmlStr.replaceAll("&nbsp;", "");  
		        //htmlStr = htmlStr.substring(0, htmlStr.indexOf("��")+1);  
		        return htmlStr;  
		    }  
		    //ȥ��style��ǩ
		    public static String removeStyle(String htmlStr){
		    	
		    	Pattern p_style = Pattern.compile(regEx_style, Pattern.CASE_INSENSITIVE);  
		        Matcher m_style = p_style.matcher(htmlStr);  
		        htmlStr = m_style.replaceAll(""); // ����style��ǩ  
		        return htmlStr.trim(); // �����ı��ַ���  
		    }
		    
		    public static String getString(String str, String subStr){
		        String sb = "";
		        String[] s1 = str.split(subStr);
		        for (int i = 0; i < s1.length; i++) {
		            if (i==0) {
		                sb = sb + s1[0];
		                continue;
		            }
		            int ii = s1[i].indexOf(">", 0);
		            sb = sb + s1[i].substring(ii);
		        }
		        return sb.toString();
		    }

		    public static void main(String[] args) {  
		        String str = "<p><img alt=\"\" src=\"http://tongyuan.tunnel.qydev.com/Funday/files/22.gif\"></p><p><img alt=\"\" src=\"http://tongyuan.tunnel.qydev.com/Funday/files/22.gif\"></p>";  
		       //System.out.println(getTextFromHtml(str));  
		        
		        List pics = ModifyHtml.getImgStr(str);
		        System.out.println(pics.toString());
		        
		    } 
		    
		    public static List<String> getImgStr(String htmlStr){   
	            String img="";   
	            Pattern p_image;   
	            Matcher m_image;   
	            List<String> pics = new ArrayList<String>();

	       //     String regEx_img = "<img.*src=(.*?)[^>]*?>"; //ͼƬ���ӵ�ַ 

	             // String regEx_img = "<img.*src\\s*=\\s*(.*?)[^>]*?>"; 
	            String regEx_img = "src\\s*=\\s*\"?(.*?)(\"|>|\\s+)"; 
	            p_image = Pattern.compile(regEx_img,Pattern.CASE_INSENSITIVE);   
	           m_image = p_image.matcher(htmlStr);
	           System.out.print(m_image.find());
	           
	           int i=0;
	           int j=0;
	           
	           //�����ѭ����ôʵ�ֵģ�Ϊʲô���ظ��洢��
	           while(m_image.find()){   
	        	  
	           //if(m_image.find()){
	                img = img + "," + m_image.group();   
	               // Matcher m  = Pattern.compile("src=\"?(.*?)(\"|>|\\s+)").matcher(img); //ƥ��src

	              // Matcher m  = Pattern.compile("src\\s*=\\s*\"?(.*?)(\"|>|\\s+)").matcher(img);
	              
	              //  while(m.find()){
	              // if(m.find()){
	              pics.add(m_image.group(1));
	               
	                }
	        //   }   
	               return pics;   
	        }  
		    
		 
		
}
