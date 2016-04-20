package com.funday.data;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;  

public class ModifyHtml {
	
		    private static final String regEx_script = "<script[^>]*?>[\\s\\S]*?<\\/script>"; // 定义script的正则表达式  
		    private static final String regEx_style = "<style[^>]*?>[\\s\\S]*?<\\/style>"; // 定义style的正则表达式  
		    private static final String regEx_html = "<[^>]+>"; // 定义HTML标签的正则表达式  
		    private static final String regEx_space = "\\s*|\t|\r|\n";//定义空格回车换行符  
		   
		      
		    /** 
		     * @param htmlStr 
		     * @return 
		     *  删除Html标签 
		     */ 
		    //去除所有标签
		    public static String delHTMLTag(String htmlStr) {  
		        Pattern p_script = Pattern.compile(regEx_script, Pattern.CASE_INSENSITIVE);  
		        Matcher m_script = p_script.matcher(htmlStr);  
		        htmlStr = m_script.replaceAll(""); // 过滤script标签  
		  
		        Pattern p_style = Pattern.compile(regEx_style, Pattern.CASE_INSENSITIVE);  
		        Matcher m_style = p_style.matcher(htmlStr);  
		        htmlStr = m_style.replaceAll(""); // 过滤style标签  
		  
		        Pattern p_html = Pattern.compile(regEx_html, Pattern.CASE_INSENSITIVE);  
		        Matcher m_html = p_html.matcher(htmlStr);  
		        htmlStr = m_html.replaceAll(""); // 过滤html标签  
		  
		        Pattern p_space = Pattern.compile(regEx_space, Pattern.CASE_INSENSITIVE);  
		        Matcher m_space = p_space.matcher(htmlStr);  
		        htmlStr = m_space.replaceAll(""); // 过滤空格回车标签  
		        return htmlStr.trim(); // 返回文本字符串  
		    } 
		    //去除空格
		    public static String getTextFromHtml(String htmlStr){  
		    	htmlStr = removeStyle(htmlStr);  
		        //htmlStr = htmlStr.replaceAll("&nbsp;", "");  
		        //htmlStr = htmlStr.substring(0, htmlStr.indexOf("。")+1);  
		        return htmlStr;  
		    }  
		    //去除style标签
		    public static String removeStyle(String htmlStr){
		    	
		    	Pattern p_style = Pattern.compile(regEx_style, Pattern.CASE_INSENSITIVE);  
		        Matcher m_style = p_style.matcher(htmlStr);  
		        htmlStr = m_style.replaceAll(""); // 过滤style标签  
		        return htmlStr.trim(); // 返回文本字符串  
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

	       //     String regEx_img = "<img.*src=(.*?)[^>]*?>"; //图片链接地址 

	             // String regEx_img = "<img.*src\\s*=\\s*(.*?)[^>]*?>"; 
	            String regEx_img = "src\\s*=\\s*\"?(.*?)(\"|>|\\s+)"; 
	            p_image = Pattern.compile(regEx_img,Pattern.CASE_INSENSITIVE);   
	           m_image = p_image.matcher(htmlStr);
	           System.out.print(m_image.find());
	           
	           int i=0;
	           int j=0;
	           
	           //这里的循环怎么实现的？为什么会重复存储》
	           while(m_image.find()){   
	        	  
	           //if(m_image.find()){
	                img = img + "," + m_image.group();   
	               // Matcher m  = Pattern.compile("src=\"?(.*?)(\"|>|\\s+)").matcher(img); //匹配src

	              // Matcher m  = Pattern.compile("src\\s*=\\s*\"?(.*?)(\"|>|\\s+)").matcher(img);
	              
	              //  while(m.find()){
	              // if(m.find()){
	              pics.add(m_image.group(1));
	               
	                }
	        //   }   
	               return pics;   
	        }  
		    
		 
		
}
