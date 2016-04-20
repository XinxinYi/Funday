package com.funday.util;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import net.sf.json.JSONObject;

public class SpamUtil extends WeixinUtil{
	
	private static final String UPLOAD_IMG_URL = "https://api.weixin.qq.com/cgi-bin/media/uploadimg?access_token=ACCESS_TOKEN";
	private static final String UPLOAD_NEWS_URL = "https://api.weixin.qq.com/cgi-bin/media/uploadnews?access_token=ACCESS_TOKEN";
	private static final String SENDTOALL_URL = "https://api.weixin.qq.com/cgi-bin/message/mass/sendall?access_token=ACCESS_TOKEN";
	
	/*
	 * �ϴ�ͼ����Ϣ�е�ͼƬ�������Զ�Ⱥ����Ϣ
	 */
	public static String uploadImg(String filePath,String accessToken) throws IOException{
		File file = new File(filePath);
		if(!file.exists() || !file.isFile()){
			throw new IOException("�ļ������ڣ�");
		}
		String url = UPLOAD_IMG_URL.replace("ACCESS_TOKEN", accessToken);
		URL urlObj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) urlObj.openConnection();
		
		con.setRequestMethod("POST");
		con.setDoInput(true);
		con.setDoOutput(true);
		con.setUseCaches(false);
		
		//��������ͷ��Ϣ
		con.setRequestProperty("Connection", "Keep-Alive");
		con.setRequestProperty("Charset", "UTF-8");
		//���ñ߽�
		String BOUNDARY = "-----------" + System.currentTimeMillis();
		con.setRequestProperty("Content-Type", "multipart/form-data;boundary =" + BOUNDARY);
		
		StringBuilder sb = new StringBuilder();
		sb.append("--");
		sb.append(BOUNDARY);
		sb.append("\r\n");
		sb.append("Content-Disposition: form-data;name=\"file\";filename=\"" + file.getName() + "\"\r\n");
		sb.append("Content-Type:application/octet-stream\r\n\r\n");

		byte[] head = sb.toString().getBytes("utf-8");

		//��������
		OutputStream out = new DataOutputStream(con.getOutputStream());
		//�����ͷ
		out.write(head);

		//�ļ����Ĳ���
		//���ļ������ļ��ķ�ʽ ���뵽url��
		DataInputStream in = new DataInputStream(new FileInputStream(file));
		int bytes = 0;
		byte[] bufferOut = new byte[1024];
		while ((bytes = in.read(bufferOut)) != -1) {
			out.write(bufferOut, 0, bytes);
		}
		in.close();

		//��β����
		byte[] foot = ("\r\n--" + BOUNDARY + "--\r\n").getBytes("utf-8");//����������ݷָ���

		out.write(foot);
		out.flush();
		out.close();

		StringBuffer buffer = new StringBuffer();
		BufferedReader reader = null;
		String result = null;
		try {
			//����BufferedReader����������ȡURL����Ӧ
			reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String line = null;
			while ((line = reader.readLine()) != null) {
				buffer.append(line);
			}
			if (result == null) {
				result = buffer.toString();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				reader.close();
			}
		}

		JSONObject jsonObj = JSONObject.fromObject(result);
		System.out.println(jsonObj);
		String typeName = "url";
		String mediaId = jsonObj.getString(typeName);
		return mediaId;
	}

	/*
	 * �ϴ�ͼ����Ϣ�е�����
	 */
	public static String uploadNews(String newsText,String token) throws IOException{

			String result = null;
			String url = UPLOAD_NEWS_URL.replace("ACCESS_TOKEN", token);
			JSONObject jsonObject = doPostStr(url,newsText);
			System.out.println("�ϴ�ͼ����Ϣ�е��ı���");
			System.out.println(jsonObject.size());
			if(jsonObject.size() < 2){
				result = jsonObject.getString("errcode");
				System.out.println("�������Ϊ��" + result);
			}else{				
				result = jsonObject.getString("media_id");
				System.out.println("media_idΪ��" + result);
			}
			return result;

		}
	/*
	 * post�ı���Ϣ
	 */
	public static String spamText(String text, String token){
		String result = null;
		String url = SENDTOALL_URL.replace("ACCESS_TOKEN", token);
		JSONObject jsonObject = doPostStr(url,text);
		if(jsonObject != null){
			result = jsonObject.getString("errcode");
		}
		return result;
	}
	/*
	 * postͼ����Ϣ
	 */
	public static String spamNews(String news,String token){
		String result = null;
		String url = SENDTOALL_URL.replace("ACCESS_TOKEN", token);
		JSONObject jsonObject = doPostStr(url,news);
		if(jsonObject != null){
			result = jsonObject.getString("errcode");
		}
		return result;
	}
	
	
}
