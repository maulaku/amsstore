package starter;
import java.sql.*;


public class DbConnector {
	public static void main(String args[])
	{
		try
		{
			System.out.println("-start");
			//remember to add classes12.zip to your build path
			DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
		    
			String username = "ora_v6c6";
			String password = "a83455063";
			
			/*
			 *In order for this dbUrl to work you must have an XShell connection running.
			 *Making the XShell connection:
			 *	Connection category:
			 *		Protocol: SSH
			 *		Host: remote.ugrad.cs.ubc.ca
			 *		Port: 22
			 *	Tunneling category:
			 *		-click 'Add...'
			 *			Type: Local (outgoing)
			 *			Listen Port: 9998
			 *			Check 'Accept local connections only'
			 *			Destination Host: dbhost.ugrad.cs.ubc.ca
			 *			Destination Port: 1521
			 */			
			String dbUrl = "jdbc:oracle:thin:@localhost:9998:ug";
			
			Connection con = DriverManager.getConnection(dbUrl, username, password);		    
		    Statement stmt = con.createStatement();
		    		    
		    String sqlText = "SELECT * FROM branch WHERE branch_city='Richmond'";
		    /*
		     * if your sqlText is an Insert you must call 'stmt.executeUpdate(sqlText)'
		     * which returns the number of rows processed
		    */
		    ResultSet rs = stmt.executeQuery(sqlText);
		    
		    /*
		     * ResultSet object is like an iterator.  Calling rs.next() will iterate through 
		     * the tuples.  Then you can call 'rs.getString(x)' where x can be the column number
		     * or the column title.  There is much more depth to ResultSet but thats as far as 
		     * I explored.
		     */
		    
		    while(rs.next())
		    {
		    	for(int i = 1; i <= 5; i++)
		    	{
		    		System.out.print(rs.getString(i) + ", ");
		    	}
		    	System.out.print("\n");
		    }
		  
		    System.out.println("\n-end");
		}
		catch(SQLException e)
		{
			System.out.println("exception caught: " + e.getMessage());
		}
	}
}
