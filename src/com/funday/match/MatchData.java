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
			
		    Class.forName("com.mysql.jdbc.Driver");     //����MYSQL JDBC��������   
		    conn = DriverManager.getConnection(CONN_URL,USERNAME,PASSWORD);
		    //����URLΪ   jdbc:mysql//��������ַ/���ݿ���  �������2�������ֱ��ǵ�½�û���������
		      
		    //System.out.println("Success connect Mysql server!");
		    stmt = conn.createStatement();
		    ResultSet rs = stmt.executeQuery("select * from "+ TABLE_NAME);
		     
		    //user Ϊ��������
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
            System.out.println("�ر����ݿ����� ��");  
            e.printStackTrace();  
        }  
     }
     	  
     //����һ���û����û�����������һ����Ч��Ϣ
    public void insertMatchUser(MatchUser matchUser){  	
    	//Ĭ�������δ���
    	matchUser.setIfMatch(false);
    	String insert = "insert into "+ TABLE_NAME + " values('"+matchUser.getOpenid()+"','"+matchUser.getMessage()+"','"+matchUser.getToOpenid()+"','"+matchUser.isIfMatch() + "')";
    	
    	System.out.println(insert);
    	try {	
			this.connSQL();
			stmt = conn.createStatement();
			stmt.executeUpdate(insert);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}       	                 
    	this.deconnSQL(); 
    }  
    //�����û���Ϣ���û�ƥ��ɹ�ʱ����,����Ӧ�ø��������û��������Ϣ
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
    //���������Ϣ
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
    //��������û���Ϣ
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
				
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
    	this.deconnSQL();
    	return matchUser;
    }
    
   //ɾ�������Ϣ
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
    
    //���ѡ��δ����û���������openId
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

