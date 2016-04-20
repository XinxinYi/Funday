package com.funday.test;

import java.io.IOException;
import java.text.ParseException;

import com.funday.data.GetItems;
import com.funday.data.ModifyHtml;
import com.funday.menu.Menu;
import com.funday.spam.SourceArticle;
import com.funday.util.WeixinUtil;

import net.sf.json.JSONObject;

public class WeixinTest {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		
		Menu menu = WeixinUtil.initMenu();
		String menuStr = JSONObject.fromObject(menu).toString();
		System.out.println(menuStr);
		
		try {
			int resutl = WeixinUtil.createMenu(WeixinUtil.getExitAccessToken().getToken(), menuStr);
			System.out.println(resutl);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		/*
		
		SourceArticle sa = GetItems.getSourceArticle("2020721");
		String source = JSONObject.fromObject(sa).toString();
		System.out.println(source);
		
		String htmlStr = sa.getContent();
		System.out.println(htmlStr);
		System.out.println("***********");
		String removeHtml = ModifyHtml.getTextFromHtml(htmlStr);
		System.out.println(removeHtml);
		
		System.out.println("***********");
		String removeHtml2 = ModifyHtml.getString(htmlStr, "style");
		System.out.println(removeHtml2);
		*/
		/*
		String str = "{\"type\":news,\"offset\":0,\"count\":1}";
		String url = "https://api.weixin.qq.com/cgi-bin/material/batchget_material?access_token=ACCESS_TOKEN";
		String token = WeixinUtil.getExitAccessToken().getToken();
		
		System.out.println(token);
		url.replace("ACCESS_TOKEN", token);
		System.out.println(str);
		JSONObject jsonObject = WeixinUtil.doPostStr(url,str);
		System.out.println(jsonObject);
		
		String url2 = "https://api.weixin.qq.com/cgi-bin/material/get_materialcount?access_token=ACCESS_TOKEN";
		url2.replace("ACCESS_TOKEN", token);
		JSONObject jsonObject2 = WeixinUtil.doGetStr(url2);
		System.out.println(jsonObject2);
		*/
		
	}

}
