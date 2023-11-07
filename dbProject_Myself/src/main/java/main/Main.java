package main;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class Main{
	 static String server = "jdbc:mysql://127.0.0.1:3307/";
     static String database = "dbproject";
     static String url = server + database + "?useSSL=false";
     static String userN = "root";
     static String password = "syy88824";
	
	 /*public static void main(String[] args) throws SQLException {
		 
		 loginFunc.LoginFunc login = new loginFunc.LoginFunc(conn);
	  }*/
	 
	 public static Connection getConn() throws SQLException {
		 Connection conn = DriverManager.getConnection(url, userN, password);
		 return conn;
	 }
	 
}
