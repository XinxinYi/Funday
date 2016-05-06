package com.funday.util;

import java.io.IOException;
import java.text.ParseException;

import com.funday.semantic.Category;
import com.funday.semantic.Semantic;

import net.sf.json.JSONObject;

public class SemanticUtil extends WeixinUtil{
	private static final String SEMANTIC_URL = "https://api.weixin.qq.com/semantic/semproxy/search?access_token=YOUR_ACCESS_TOKEN";
	private static final String APPID = "wx89bbf4a5b2fb537a"; 
	/*
	 * 拼接语义理解post的字符串
	 */
	public static Semantic makeSemantic(String query,String openId) throws IOException{	
		Semantic smt = new Semantic();
		smt.setQuery(query);				
		smt.setAppid(APPID);
		smt.setCategory(getCategory(query));
		smt.setCity("北京");
		smt.setUid(openId);
		
		return smt;

	}
	
	/*
	 * 分析用户发送内容的大致类别
	 */
	public static Category getCategory(String query){	
		//娱乐类
		if(query.indexOf("电影") >= 0){
			return Category.movie;
		}else if(query.indexOf("音乐") >= 0 || query.indexOf("歌曲") >= 0){
			return Category.music;
		}else if(query.indexOf("视频") >= 0 || query.indexOf("电视剧") >= 0){
			return Category.video;
		}else if(query.indexOf("小说") >= 0 || query.indexOf("书") >= 0 || query.indexOf("文章") >= 0){
			return Category.novel;
		}
		//生活类
		else if(query.indexOf("附近") >= 0 || query.indexOf("周边") >= 0 ){
			return Category.nearby;
		}else if(query.indexOf("饭馆") >= 0 || query.indexOf("饭店") >= 0 || query.indexOf("餐厅") >= 0){
			return Category.restaurant;
		}else if(query.indexOf("怎么走") >= 0 || query.indexOf("怎么去") >= 0 || query.indexOf("路线") >= 0){
			return Category.map;
		}else if(query.indexOf("火车") >= 0 || query.indexOf("车票") >= 0 || query.indexOf("飞机") >= 0){
			return Category.flight;
		}
		//工具类
		else if(query.indexOf("天气") >= 0 ){
			return Category.weather;
		}else if(query.indexOf("电话") >= 0 ){
			return Category.telephone;
		}
		
		return Category.baike;
		
		
	}
	
	/*
	 * post语义理解的请求信息
	 */
	public static String postSemtic(String token,String semantic, Category ctg) throws ParseException,IOException{
		
		String answer = null;
		String url = SEMANTIC_URL.replace("YOUR_ACCESS_TOKEN", token);
		JSONObject jsonObject = doPostStr(url,semantic);
		System.out.println(jsonObject);
		if(jsonObject != null){
			if(jsonObject.getString("errcode").equals("20703")){
				answer = "哦呦！没有查到呢，要不您再仔细描述下";
			}else{
			
				switch(ctg)
				{
				case telephone:	
					answer = jsonObject.getJSONObject("semantic").getJSONObject("details").getString("telephone");
					break;
				default:
					answer = jsonObject.getJSONObject("semantic").getJSONObject("details").getString("answer");
					break;
				}
				System.out.println("answer"  + answer);
			}
		}	
		
		return answer;
	}	

}
