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
	 * ƴ���������post���ַ���
	 */
	public static Semantic makeSemantic(String query,String openId) throws IOException{	
		Semantic smt = new Semantic();
		smt.setQuery(query);				
		smt.setAppid(APPID);
		smt.setCategory(getCategory(query));
		smt.setCity("����");
		smt.setUid(openId);
		
		return smt;

	}
	
	/*
	 * �����û��������ݵĴ������
	 */
	public static Category getCategory(String query){	
		//������
		if(query.indexOf("��Ӱ") >= 0){
			return Category.movie;
		}else if(query.indexOf("����") >= 0 || query.indexOf("����") >= 0){
			return Category.music;
		}else if(query.indexOf("��Ƶ") >= 0 || query.indexOf("���Ӿ�") >= 0){
			return Category.video;
		}else if(query.indexOf("С˵") >= 0 || query.indexOf("��") >= 0 || query.indexOf("����") >= 0){
			return Category.novel;
		}
		//������
		else if(query.indexOf("����") >= 0 || query.indexOf("�ܱ�") >= 0 ){
			return Category.nearby;
		}else if(query.indexOf("����") >= 0 || query.indexOf("����") >= 0 || query.indexOf("����") >= 0){
			return Category.restaurant;
		}else if(query.indexOf("��ô��") >= 0 || query.indexOf("��ôȥ") >= 0 || query.indexOf("·��") >= 0){
			return Category.map;
		}else if(query.indexOf("��") >= 0 || query.indexOf("��Ʊ") >= 0 || query.indexOf("�ɻ�") >= 0){
			return Category.flight;
		}
		//������
		else if(query.indexOf("����") >= 0 ){
			return Category.weather;
		}else if(query.indexOf("�绰") >= 0 ){
			return Category.telephone;
		}
		
		return Category.baike;
		
		
	}
	
	/*
	 * post��������������Ϣ
	 */
	public static String postSemtic(String token,String semantic, Category ctg) throws ParseException,IOException{
		
		String answer = null;
		String url = SEMANTIC_URL.replace("YOUR_ACCESS_TOKEN", token);
		JSONObject jsonObject = doPostStr(url,semantic);
		System.out.println(jsonObject);
		if(jsonObject != null){
			if(jsonObject.getString("errcode").equals("20703")){
				answer = "Ŷ�ϣ�û�в鵽�أ�Ҫ��������ϸ������";
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
