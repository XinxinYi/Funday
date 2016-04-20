package com.funday.util;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class GetHtml {
	
	
	
	public static String[] getHtml(String url) throws IOException{
		String[] title_img = new String[2];
				
		
		// �� URL ֱ�Ӽ��� HTML �ĵ�
        Document doc = Jsoup.connect(url).get();       
        
        Elements ListDiv = doc.getElementsByAttributeValue("id","activity-name");
        
        title_img[0] = ListDiv.html();
        //System.out.println(ListDiv.html());
               
        
       // System.out.println("************");
        
        Elements ListDiv2 = doc.getElementsByAttributeValue("id","js_content");
       // System.out.println(ListDiv2.html());
     
       
        for (Element element :ListDiv2) {
            Elements links = element.getElementsByTag("img");
            //System.out.println(links);
            
            for (Element link : links) {        	               	
            	String linkHref = link.attr("data-src");	          
	            if(linkHref.endsWith("jpeg")){
	            	title_img[1] = linkHref;
	            		break;
	            	}           	            
            }
        }                  
        return title_img;
	}
	
	
}