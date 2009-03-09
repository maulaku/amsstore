package starter;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class DbConnector {
	
		public static void main(String args[])
		{
			try
			{
				System.out.println("-start");
				DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
			    Connection con = DriverManager.getConnection("jdbc:oracle:thin:@localhost:9998:ug", "ora_v6c6", "a83455063");
			    //"jdbc:oracle:thin:@localhost:9998:ug" 
			    //"jdbc:oracle:thin:@dbhost.ugrad.cs.ubc.ca:1521:ug"
			    Statement stmt = con.createStatement();
			    
			    ResultSet rs = stmt.executeQuery("SELECT * FROM branch");
			    rs.next();
			    System.out.print(rs.getString(2));
			    
			    System.out.println("\n-end");
			}
			catch(SQLException e)
			{
				System.out.println("exception caught:" + e.getMessage());
			}
		}
	}


