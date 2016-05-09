package com.funday.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import com.funday.util.ConfigUtil;


public class CreatTable2 {
	//获取配置文件，更改项目位置时需要修改
		public static String configUrl = "../../workspace/Funday/WebContent/WEB-INF/config.properties";	
		
		public static void main(String[] args) {
			// TODO Auto-generated method stub
			try {
				Class.forName("com.mysql.jdbc.Driver");     //加载MYSQL JDBC驱动程序   
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
					           //连接URL为   jdbc:mysql//服务器地址/数据库名  ，后面的2个参数分别是登陆用户名和密码
					      String sql = "CREATE TABLE match_user (openid varchar(100) not null unique,message varchar(1000),toOpenid varchar(100),ifMatch varchar(20),inMatch varchar(20)) ENGINE = MyISAM  DEFAULT CHARSET = utf8;";
					      Statement stmt = connect.prepareStatement(sql);
					      stmt.execute(sql);
					      ResultSet rs = stmt.executeQuery("select * from match_user");					     					      
						      
						while (rs.next()) {
						        System.out.println("openid:" + rs.getString("openid") +  " message: "+rs.getString("message") + " toOpenid:" +rs.getString("toOpenid") + " ifMatch:" + rs.getString("ifMatch") + " inMatch:" +rs.getString("inMatch"));
						}
						    }
						    catch (Exception e) {
						      System.out.print("get data error!");
						      e.printStackTrace();
						    }
						  

	}
}
