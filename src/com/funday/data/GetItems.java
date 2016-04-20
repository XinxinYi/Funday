package com.funday.data;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;

import com.funday.spam.Items;
import com.funday.spam.SourceArticle;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class GetItems {
	private static final String ITEM_URL = "http://101.200.141.146:9000/arsenal/item/";
	private static final String ARTICAL_URL = "http://101.200.141.146:9000/arsenal/artical/artical_id/";
	
    public static String loadJson (String urlPath) throws IOException {  
    	 URL url = new URL(urlPath);  
         HttpURLConnection connection = (HttpURLConnection) url.openConnection();  
         connection.connect();  
         InputStream inputStream = connection.getInputStream();  
         //对应的字符编码转换  
         Reader reader = new InputStreamReader(inputStream, "UTF-8");  
         BufferedReader bufferedReader = new BufferedReader(reader);  
         String str = null;  
         StringBuffer sb = new StringBuffer();  
         while ((str = bufferedReader.readLine()) != null) {  
             sb.append(str);  
         }  
         reader.close();  
         connection.disconnect();  
         return sb.toString();   
    }  
	
	/*
	 * 获取所有的文章摘要等
	 */
	public static Items[] getItems() throws IOException{
		String jsonStr;
		
			jsonStr = loadJson(ITEM_URL);
			
			//JSONObject json = new JSONObject();
			JSONArray jsonArr = JSONArray.fromObject(jsonStr);
			Items[] items = new Items[jsonArr.size()];
			/*
			System.out.println(jsonArr.getJSONObject(0).getString("content"));
			String source = jsonArr.getJSONObject(0).getString("source");
			String header = jsonArr.getJSONObject(0).getString("header");
			String content = jsonArr.getJSONObject(0).getString("content");
			String articalId = jsonArr.getJSONObject(0).getString("articalId");
			String fullTextUrl = jsonArr.getJSONObject(0).getString("fullTextUrl");
			String thumbnail = jsonArr.getJSONObject(0).getString("thumbnail");
			*/
			//System.out.println(source);
			//System.out.println(header);
			
			//items[0] = new Items();
			//items[0].setSource(source);
			//System.out.println(items[0].getSource());
			
			
			for(int i=0;i<jsonArr.size();i++){
				items[i] = new Items();
				items[i].setSource(jsonArr.getJSONObject(i).getString("source"));
				items[i].setHeader(jsonArr.getJSONObject(i).getString("header"));
				items[i].setContent(jsonArr.getJSONObject(i).getString("content"));
				items[i].setArticalId(jsonArr.getJSONObject(i).getString("articalId"));
				items[i].setFullTextUrl(jsonArr.getJSONObject(i).getString("fullTextUrl"));
				items[i].setThumbnail(jsonArr.getJSONObject(i).getString("thumbnail"));
			}
			return items;	
		
	}
	/*
	 * 获取具体文章的内容
	 */
	public static SourceArticle getSourceArticle(String articleId) throws IOException{
		System.out.println(articleId);
		
		
		SourceArticle sourceArt = new SourceArticle();
		String jsonStr;
		
		jsonStr = loadJson(ARTICAL_URL.replace("artical_id", articleId));
		System.out.print(jsonStr);
		//JSONObject json = new JSONObject();
		JSONObject jsonArr = JSONObject.fromObject(jsonStr);
		
		sourceArt.setDate(jsonArr.getString("date"));
		sourceArt.setContent(jsonArr.getString("content"));
		sourceArt.setEditor(jsonArr.getString("editor"));
		sourceArt.setHeader(jsonArr.getString("header"));
		sourceArt.setPicture_src(jsonArr.getString("picture_src"));
		sourceArt.setSourc(jsonArr.getString("source"));
		sourceArt.setType(jsonArr.getString("type"));
		sourceArt.setVideo(jsonArr.getString("video"));
		

		return sourceArt;
	}

	public static void main(String[] args) {
		//getItems();
		try {
			System.out.println(getItems());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
