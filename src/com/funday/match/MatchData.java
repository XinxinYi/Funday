package com.funday.match;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.funday.data.CreatTable;
import com.funday.user.MatchUser;
import com.funday.user.User;
import com.funday.util.ConfigUtil;

public class MatchData {
	private Connection conn = null;  
    private Statement stmt = null; 
    private static final String TABLE_NAME = "match_user";
    
	public void connSQL(){
		
		
		try {						
			//String userDir = System.getProperty("user.dir");
			//System.out.println(userDir);
			
			ConfigUtil cu = new ConfigUtil(CreatTable.configUrl);
			String CONN_URL = cu.getValue("dbUrl");
			String USERNAME = cu.getValue("dbUserName");
			String PASSWORD = cu.getValue("dbPassword");			
			
		    Class.forName("com.mysql.jdbc.Driver");     //加载MYSQL JDBC驱动程序   
		    conn = DriverManager.getConnection(CONN_URL,USERNAME,PASSWORD);
		    //连接URL为   jdbc:mysql//服务器地址/数据库名  ，后面的2个参数分别是登陆用户名和密码
		      
		    //System.out.println("Success connect Mysql server!");
		    stmt = conn.createStatement();
		    ResultSet rs = stmt.executeQuery("select * from "+ TABLE_NAME);
		     
		    //user 为你表的名称
		    }catch (Exception e) {
		      System.out.print("get data error!");
		      e.printStackTrace();
		    } 
		  }
	
	// disconnect to MySQL  
     void deconnSQL() {  
        try {  
            if (conn != null)  
                conn.close();  
        } catch (Exception e) {  
            System.out.println("关闭数据库问题 ：");  
            e.printStackTrace();  
        }  
     }
     	  
     //用户关注时调用
    public void insertMatchUser(String openid){  	
    	//默认最初均未配对
    	String insert = "insert into "+ TABLE_NAME + " values('"+openid+"','','','false','false')";
    	String select = "select * from " + TABLE_NAME + " where openid = '" + openid + "'";
    	String update = "UPDATE "+ TABLE_NAME + " set toOpenid='',ifMatch ='false',inMatch ='true' where openid='" + openid + "'";
    	
    	try {	
			this.connSQL();
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(select);			
			if(!rs.next()){	
				System.out.println("不存在该用户！！！");
				stmt.executeUpdate(insert);
				System.out.println(insert);
			}else{
				System.out.println("存在该用户！！！");
				stmt.executeUpdate(update);
				System.out.println(update);
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}       	                 
    	this.deconnSQL(); 
    }    
   
    
    //更新用户信息，用户匹配成功时调用,这里应该更新两个用户的配对信息
    public void updateMatchUser(String openid,String toOpenid){
    	
    	String update1 = "UPDATE "+ TABLE_NAME + " set toOpenid='" +toOpenid+"',ifMatch ='true', inMatch ='true' where openid='" + openid + "'";
    	String update2 = "UPDATE "+ TABLE_NAME + " set toOpenid='" +openid+"',ifMatch ='true', inMatch ='true' where openid='" + toOpenid + "'";
    	System.out.println(update1);
    	System.out.println(update2);
    	try {	
			this.connSQL();
			stmt = conn.createStatement();
			stmt.executeUpdate(update1);
			stmt.executeUpdate(update2);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
    	this.deconnSQL();
    }
    
    //查找配对用户信息
    public MatchUser selectMatchUser(String openId){
    	String select = "select * from " + TABLE_NAME + " where openid = '" +openId + "'";
    	MatchUser matchUser = new MatchUser();
    	try {	
			this.connSQL();
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(select);
			while (rs.next()) {	
				matchUser.setOpenid(rs.getString("openid"));
				matchUser.setMessage(rs.getString("message"));
				matchUser.setToOpenid(rs.getString("toOpenid"));
				matchUser.setIfMatch(rs.getBoolean("ifMatch"));
				matchUser.setInMatch(rs.getBoolean("inMatch"));
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
    	this.deconnSQL();
    	return matchUser;
    }
    //开启某一对用户match状态
    public void openMatch(String openid,String toOpenid){
    	String update1 = "UPDATE "+ TABLE_NAME + " set inMatch='true' where openid='" + openid + "'";
    	String update2 = "UPDATE "+ TABLE_NAME + " set inMatch='true' where openid='" + toOpenid + "'";
    	try {	
			this.connSQL();
			stmt = conn.createStatement();
			stmt.executeUpdate(update1);
			stmt.executeUpdate(update2);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
    	this.deconnSQL();
    }
    //关闭某一对用户match状态
    public void closeMatch(String openid,String toOpenid){
    	String update1 = "UPDATE "+ TABLE_NAME + " set inMatch='false' where openid='" + openid + "'"; 
    	String update2 = "UPDATE "+ TABLE_NAME + " set inMatch='false' where openid='" + toOpenid + "'"; 
    	try {	
			this.connSQL();
			stmt = conn.createStatement();
			stmt.executeUpdate(update1);
			stmt.executeUpdate(update2);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
    	this.deconnSQL();
    }  
    
    //随机选择未配对用户，并返回openId，已派出自己
    public String matchUser(String openid){
    	String noMatch = "select openid from " + TABLE_NAME + " where ifMatch = 'false' and openid <> '" + openid + "' order by rand() limit 1";  	
    	//String noMatch = "select * from " + TABLE_NAME + " as t1 join (select openid from "+ TABLE_NAME + " where ifMatch ='false' and openid <> '"+openid+"') as t2 where t1.openid = t2.openid limit 1";
    	String toOpenid = null;
    	System.out.println(noMatch);
    	try {	
			this.connSQL();
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(noMatch);
			
			System.out.println(noMatch);
			while (rs.next()) {	
				toOpenid = rs.getString("openid");
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
    	this.deconnSQL();
    	
    	return toOpenid;
    }      
    //清空双方的配对，重新配对时调用，某一用户取消关注时调用
    public void cleanMatch(String openid,String toOpenid){
    	System.out.println(toOpenid);
    	String update1 = "UPDATE "+ TABLE_NAME + " set toOpenid='',message = '',inMatch='false', ifMatch ='false' where openid='" + openid + "'";
    	String update2 = "UPDATE "+ TABLE_NAME + " set toOpenid='',message = '',inMatch='false', ifMatch ='false' where openid='" + toOpenid + "'";
    	System.out.println("重新配对，应该清除所有消息");
    	System.out.println(update1);
    	System.out.println(update2);
    	try {	
			this.connSQL();
			stmt = conn.createStatement();
			stmt.executeUpdate(update1);
			stmt.executeUpdate(update2);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
    	this.deconnSQL();
    }
  //删除配对信息
    public void deleteMatchUser(String openId){
    	String delete = "delete from " + TABLE_NAME + " where openid= '" + openId +"'";      	
    	this.connSQL();		
		try {
			stmt = conn.createStatement();
			stmt.executeUpdate(delete);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
    	this.deconnSQL();
    } 
    
    
    //下面的方法暂时没什么用
    
    //将所有用户设置为未配对
    public void setMatchFalse(){
    	String setMatch = "UPDATE "+ TABLE_NAME + " set ifMatch='false', inMatch='false'";
    	try {	
			this.connSQL();
			stmt = conn.createStatement();
			stmt.executeUpdate(setMatch);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
    	this.deconnSQL();
    }  
      
    
    //清除某一用户发送的消息
    public void cleanOneMessage(String openid){
    	String update = "UPDATE "+ TABLE_NAME + " set message = '' where openid='" + openid + "'";
    	System.out.println(update);
    	try {	
			this.connSQL();
			stmt = conn.createStatement();
			stmt.executeUpdate(update);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
    	this.deconnSQL();
    }
     
    //更新配对消息
    public void updateMessage(String openid,String message){
    	String update = "UPDATE "+ TABLE_NAME + " set message='" +message+"' where openid='" + openid + "'";
    	System.out.println(update);
    	try {	
			this.connSQL();
			stmt = conn.createStatement();
			stmt.executeUpdate(update);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
    	this.deconnSQL();
    }
}

