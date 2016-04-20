package com.funday.servlet;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import com.funday.data.GetItems;
import com.funday.data.SqlConn;
import com.funday.spam.ArticleTextArr;
import com.funday.spam.NewsSpam;
import com.funday.spam.TextSpam;
import com.funday.util.MessageUtil;
import com.funday.util.SpamUtil;
import com.funday.util.WeixinUtil;

import net.sf.json.JSONObject;

public class AutoDo {
	static int count = 0;
    
	public static void showTimer() {
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				// task to run goes here  
				SqlConn sc = new SqlConn();
				sc.updateTodaySign();
				++count;
				System.out.println("时间=" + new Date() + " 执行了" + count + "次"); // 1次
			}
		};

		//设置执行时间
		Calendar calendar = Calendar.getInstance();
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH);
		int day = calendar.get(Calendar.DAY_OF_MONTH);//每天
		//定制每天的21:09:00执行，
		calendar.set(year, month, day, 23, 59, 59);
		Date date = calendar.getTime();
		Timer timer = new Timer();
		System.out.println(date);
		               
		//int period = 2 * 1000;
		//每天的date时刻执行task，每隔2秒重复执行
		//timer.schedule(task, date, period);
		//每天的date时刻执行task, 仅执行一次
		timer.schedule(task, date);
	}

	public static void autoSpam(){
		Timer timer = new Timer();
		
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				// task to run goes here  
				SqlConn sc = new SqlConn();
				String[][] userPoints = sc.getPointsOrder();
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String date = sdf.format(new Date());				
				String token = WeixinUtil.getExitAccessToken().getToken();				
				//文字列表
						
				//将获取的items变成上传的图文格式
				
				try {
					ArticleTextArr atArr = new ArticleTextArr();	
					
					String userDir = System.getProperty("user.dir");
					System.out.println("此处的文件地址：" + userDir);
					
					//String filePath = items[0].getThumbnail();
					String filePath = "../../1.jpg";
					String imgId = WeixinUtil.upload(filePath,token,"image");
					System.out.println("上传的图片素材ID： " + imgId);
					
					atArr = MessageUtil.makeNews(GetItems.getItems(),imgId);
					
					//将组建的article对象转换成String类型
					String newStr = JSONObject.fromObject(atArr).toString();									
					System.out.println("需要上传的图文文本消息内容为： " + newStr);
					
					//上传图文消息的文本内容，并返回图文消息文本的ID
					String newId = SpamUtil.uploadNews(newStr, token);
					System.out.println("图文消息中文本素材的ID:" + newId);
					
					//生成最终POST的内容对象
					NewsSpam ns = MessageUtil.makeSpamNews(newId);
					String newSpam = JSONObject.fromObject(ns).toString();
					System.out.println("最后POST发送的内容： " + newSpam);
					
					String result = SpamUtil.spamNews(newSpam, token);
					System.out.println("最后的结果：" + result);
					
					++count;
					//System.out.println("自动群发消息：");
					//System.out.println("时间=" + date + " 执行了" + count + "次"); // 1次
					
					//String textStr = date + "第" + count + "次发送图文消息：\n\n" + "上传的图片素材ID： " + imgId + "\n\n" + "需要上传的图文文本消息内容为： " + newStr + "\n\n" + "图文消息中文本素材的ID:" + newId + "\n\n" + "最后POST发送的内容： " + newSpam + "\n\n" + "最后的结果：" + result;
					//TextSpam ts  = MessageUtil.makeSpamText(textStr);
					//String textPost = JSONObject.fromObject(ts).toString();
					//String resultText = SpamUtil.spamText(textPost, token);
					//System.out.println();
					//System.out.println("自动发送文本消息的最后返回值：" + resultText);
					
					if(count>2)
						timer.cancel();
						
			
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};

		//设置执行时间
		Calendar calendar = Calendar.getInstance();
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH);
		int day = calendar.get(Calendar.DAY_OF_MONTH);//每天
		//定制每天的21:09:00执行，
		calendar.set(year, month, day, 12, 38, 00);
		Date date = calendar.getTime();
		
		System.out.println(date);
		               
		int period = 2  * 60 * 60 * 1000;
		//每天的date时刻执行task，每隔2秒重复执行
		timer.schedule(task, date, period);
		//每天的date时刻执行task, 仅执行一次
		//timer.schedule(task, date);
	}
	
	public static void main(String[] args) {
	     //showTimer();
		autoSpam();
	}
	       
}
