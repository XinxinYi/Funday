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
				System.out.println("ʱ��=" + new Date() + " ִ����" + count + "��"); // 1��
			}
		};

		//����ִ��ʱ��
		Calendar calendar = Calendar.getInstance();
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH);
		int day = calendar.get(Calendar.DAY_OF_MONTH);//ÿ��
		//����ÿ���21:09:00ִ�У�
		calendar.set(year, month, day, 23, 59, 59);
		Date date = calendar.getTime();
		Timer timer = new Timer();
		System.out.println(date);
		               
		//int period = 2 * 1000;
		//ÿ���dateʱ��ִ��task��ÿ��2���ظ�ִ��
		//timer.schedule(task, date, period);
		//ÿ���dateʱ��ִ��task, ��ִ��һ��
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
				//�����б�
						
				//����ȡ��items����ϴ���ͼ�ĸ�ʽ
				
				try {
					ArticleTextArr atArr = new ArticleTextArr();	
					
					String userDir = System.getProperty("user.dir");
					System.out.println("�˴����ļ���ַ��" + userDir);
					
					//String filePath = items[0].getThumbnail();
					String filePath = "../../1.jpg";
					String imgId = WeixinUtil.upload(filePath,token,"image");
					System.out.println("�ϴ���ͼƬ�ز�ID�� " + imgId);
					
					atArr = MessageUtil.makeNews(GetItems.getItems(),imgId);
					
					//���齨��article����ת����String����
					String newStr = JSONObject.fromObject(atArr).toString();									
					System.out.println("��Ҫ�ϴ���ͼ���ı���Ϣ����Ϊ�� " + newStr);
					
					//�ϴ�ͼ����Ϣ���ı����ݣ�������ͼ����Ϣ�ı���ID
					String newId = SpamUtil.uploadNews(newStr, token);
					System.out.println("ͼ����Ϣ���ı��زĵ�ID:" + newId);
					
					//��������POST�����ݶ���
					NewsSpam ns = MessageUtil.makeSpamNews(newId);
					String newSpam = JSONObject.fromObject(ns).toString();
					System.out.println("���POST���͵����ݣ� " + newSpam);
					
					String result = SpamUtil.spamNews(newSpam, token);
					System.out.println("���Ľ����" + result);
					
					++count;
					//System.out.println("�Զ�Ⱥ����Ϣ��");
					//System.out.println("ʱ��=" + date + " ִ����" + count + "��"); // 1��
					
					//String textStr = date + "��" + count + "�η���ͼ����Ϣ��\n\n" + "�ϴ���ͼƬ�ز�ID�� " + imgId + "\n\n" + "��Ҫ�ϴ���ͼ���ı���Ϣ����Ϊ�� " + newStr + "\n\n" + "ͼ����Ϣ���ı��زĵ�ID:" + newId + "\n\n" + "���POST���͵����ݣ� " + newSpam + "\n\n" + "���Ľ����" + result;
					//TextSpam ts  = MessageUtil.makeSpamText(textStr);
					//String textPost = JSONObject.fromObject(ts).toString();
					//String resultText = SpamUtil.spamText(textPost, token);
					//System.out.println();
					//System.out.println("�Զ������ı���Ϣ����󷵻�ֵ��" + resultText);
					
					if(count>2)
						timer.cancel();
						
			
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};

		//����ִ��ʱ��
		Calendar calendar = Calendar.getInstance();
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH);
		int day = calendar.get(Calendar.DAY_OF_MONTH);//ÿ��
		//����ÿ���21:09:00ִ�У�
		calendar.set(year, month, day, 12, 38, 00);
		Date date = calendar.getTime();
		
		System.out.println(date);
		               
		int period = 2  * 60 * 60 * 1000;
		//ÿ���dateʱ��ִ��task��ÿ��2���ظ�ִ��
		timer.schedule(task, date, period);
		//ÿ���dateʱ��ִ��task, ��ִ��һ��
		//timer.schedule(task, date);
	}
	
	public static void main(String[] args) {
	     //showTimer();
		autoSpam();
	}
	       
}
