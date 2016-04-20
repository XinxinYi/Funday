package com.funday.test;
import java.io.IOException;

import com.funday.data.GetItems;
import com.funday.spam.ArticleTextArr;
import com.funday.spam.Items;
import com.funday.util.MessageUtil;
import com.funday.util.WeixinUtil;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;


public class AutoSpamTest {
	
	private static final String UPLOAD_NEWS_URL = "https://api.weixin.qq.com/cgi-bin/material/add_news?access_token=ACCESS_TOKEN";
	private static final String GET_NEWS_URL = "https://api.weixin.qq.com/cgi-bin/material/add_material?access_token=ACCESS_TOKEN";
	
	public static String postNews(){
		String message = null;
		String filePath = "../../1.jpg";
		
		String imgId;
		try {
			String token = WeixinUtil.getExitAccessToken().getToken();
			imgId = WeixinUtil.uploadForever(filePath, token, "image");
			Items[] items = GetItems.getItems();
			
			ArticleTextArr atArr = MessageUtil.makeNews(items, imgId);
			String newsText = JSONObject.fromObject(atArr).toString();
			
			
			System.out.println("上传的图文内容： " + newsText);
			String result = null;
			String url = UPLOAD_NEWS_URL.replace("ACCESS_TOKEN", token);
			JSONObject jsonObject = WeixinUtil.doPostStr(url,newsText);
			System.out.println("上传图文消息中的文本：");
			System.out.println(jsonObject.size());
			if(jsonObject.size() < 3){
				result = jsonObject.getString("errcode");
				System.out.println("错误代码为：" + result);
			}else{				
				result = jsonObject.getString("media_id");
				System.out.println("media_id为：" + result);
			}
			
			return  result;
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
		
	}
	 public static String getNews(String mediaId){
		 String result = null;
		 PostNews pn = new PostNews();
		 pn.setMediaId(mediaId);
		 
		 String token = WeixinUtil.getExitAccessToken().getToken();
		 String getNews= JSONArray.fromObject(pn).toString();
		 System.out.println(getNews);
		 
		 String url = GET_NEWS_URL.replace("ACCESS_TOKEN", token);
		 JSONObject jsonObject = WeixinUtil.doPostStr(url,getNews);
		 
		 System.out.println(jsonObject);
		 return result;
	 }
	
	 public static void main(String[] args) {  
		 String newId = postNews();
		 String result = getNews(newId);
	
	 }
}
