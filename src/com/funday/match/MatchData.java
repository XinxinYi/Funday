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
     	  
     //新增一个用户，用户启动并发送一条有效消息
    public void insertMatchUser(MatchUser matchUser){  	
    	//默认最初均未配对
    	matchUser.setIfMatch(false);
    	String insert = "insert into "+ TABLE_NAME + " values('"+matchUser.getOpenid()+"','"+matchUser.getMessage()+"','"+matchUser.getToOpenid()+"','"+matchUser.isIfMatch()  +"','"+matchUser.isInMatch() +"')";
    	String select = "select * from " + TABLE_NAME + " where openid = '" + matchUser.getOpenid() + "'";
    	String update = "UPDATE "+ TABLE_NAME + " set toOpenid='" +null+"',ifMatch ='false',inMatch ='true' where openid='" + matchUser.getOpenid() + "'";
    	
    	try {	
			this.connSQL();
			stmt = conn.createStatement();
			
			ResultSet rs = stmt.executeQuery(select);
			System.out.println(rs.next());
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
    	
    	String update1 = "UPDATE "+ TABLE_NAME + " set toOpenid='" +toOpenid+"',ifMatch ='true' where openid='" + openid + "'";
    	String update2 = "UPDATE "+ TABLE_NAME + " set toOpenid='" +openid+"',ifMatch ='true' where openid='" + toOpenid + "'";
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
    //开启某一个用户match状态
    public void openMatch(String openId){
    	String update = "UPDATE "+ TABLE_NAME + " set inMatch='true' where openid='" + openId + "'";
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
    //关闭某一个用户match状态
    public void closeMatch(String openId){
    	String update = "UPDATE "+ TABLE_NAME + " set inMatch='false' where openid='" + openId + "'";
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
    
    //随机选择未配对用户，并返回openId
    public String matchUser(String openid){
    	String noMatch = "select * from " + TABLE_NAME + " where ifMatch = 'false' order by rand() limit 1";  	
    	String toOpenid = null;
    	try {	
			this.connSQL();
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(noMatch);
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
    
   //
    public void setMatchFalse(){
    	String setMatch = "UPDATE "+ TABLE_NAME + " set ifMatch='false'";
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
     
}

