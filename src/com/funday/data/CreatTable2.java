package com.funday.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import com.funday.util.ConfigUtil;


public class CreatTable2 {
	//��ȡ�����ļ���������Ŀλ��ʱ��Ҫ�޸�
		public static String configUrl = "../../workspace/Funday/WebContent/WEB-INF/config.properties";	
		
		public static void main(String[] args) {
			// TODO Auto-generated method stub
			try {
				Class.forName("com.mysql.jdbc.Driver");     //����MYSQL JDBC��������   
				//Class.forName("org.gjt.mm.mysql.Driver");
				System.out.println("Success loading Mysql Driver!");
				}catch (Exception e) {
					System.out.print("Error loading Mysql Driver!");
					e.printStackTrace();
				}
				try {
					ConfigUtil cu = new ConfigUtil(configUrl);
					String CONN_URL = cu.getValue("dbUrl");
					String USERNAME = cu.getValue("dbUserName");
					String PASSWORD = cu.getValue("dbPassword");	
					
					Connection connect = DriverManager.getConnection(
							CONN_URL,USERNAME,PASSWORD);
					           //����URLΪ   jdbc:mysql//��������ַ/���ݿ���  �������2�������ֱ��ǵ�½�û���������
					      String sql = "CREATE TABLE match_user (openid varchar(100) not null unique,message varchar(100),toOpenid varchar(100),ifMatch varchar(20)) ENGINE = MyISAM  DEFAULT CHARSET = utf8;";
					      Statement stmt = connect.prepareStatement(sql);
					      stmt.execute(sql);
					      ResultSet rs = stmt.executeQuery("select * from match_user");					     					      
						      
						while (rs.next()) {
						        System.out.println("openid:" + rs.getString("openid") +  " message: "+rs.getString("message") + " toOpenid:" +rs.getString("toOpenid") + " ifMatch:" + rs.getString("ifMatch"));
						}
						    }
						    catch (Exception e) {
						      System.out.print("get data error!");
						      e.printStackTrace();
						    }
						  

	}
}
