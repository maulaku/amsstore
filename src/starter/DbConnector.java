package starter;
import java.io.*;
import java.sql.*;
import java.util.regex.Pattern;


public class DbConnector {
	
	private static Connection con;
	
	public static void main(String args[])
	{
			initialize();
			runScript("src" + File.separator+ "assets" + File.separator + "dropall.txt");
			/*
			 * Current Files:
			 * "src\\assets\\createall.txt"
			 * "src\\assets\\dropall.txt"
			 */
			runScript("src" + File.separator+ "assets" + File.separator+ "createall.txt");
//			update();
//			query();
			cleanUp();
	}
	
	private static void initialize()
	{
		try
		{
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
			
			con = DriverManager.getConnection(dbUrl, username, password);	

//			con.setAutoCommit(false);
		}
		catch(SQLException e)
		{
			System.out.println("exception caught: " + e.getMessage());
		}
	}
	
	private static void runScript(String filename)
	{
		try 
		{
			BufferedReader in = new BufferedReader(new FileReader(filename));
			String line;
			String fileText = "";
			while ((line = in.readLine()) != null) {
				fileText += line;
			}
			in.close();
			
			String[] statements = fileText.split(";");
			Statement stmt = con.createStatement();
			for(String sqlText : statements){
				try
				{
					stmt.executeUpdate(sqlText);
					
				}
				/*
				 * this inner try catch is meant to disregard drop table command
				 * exceptions if the table does not exist
				 */
				catch(SQLException e)
				{
					System.out.println(e.getMessage());
				}
			}
			stmt.close();
		} 
		catch (Exception e) //IOException or SQLException
		{
			System.out.println(e.getMessage());
		}

	}
	
	private static void update()
	{
		try
		{
			Statement stmt = con.createStatement();
			String sqlText = "INSERT INTO Item VALUES(123, 'itemName1', 'CD', 'rock', 'companyName1', 1980, 20.00)";
			stmt.executeUpdate(sqlText);
		}
		catch(SQLException e)
		{
			System.out.println(e.getMessage());
		}
	}
	
	private static void query()
	{
		try
		{
			Statement stmt = con.createStatement();
			String sqlText = "SELECT * FROM Item";

			ResultSet rs = stmt.executeQuery(sqlText);
			//rs.getMetaData().getColumnCount();
			
		    /*
		     * ResultSet object is like an iterator.  Calling rs.next() will iterate through 
		     * the tuples.  Then you can call 'rs.getString(x)' where x can be the column number
		     * or the column title.  There is much more depth to ResultSet but thats as far as 
		     * I explored.
		     */		    
		    while(rs.next())
		    {
		    	for(int i = 1; i <= 2; i++)
		    	{
		    		System.out.print(rs.getString(i) + ", ");
		    	}
		    	System.out.print("\n");
		    }
		     
		    
//		    while(rs.next())
//		    {
//		    	for(int i = 1; i <= 5; i++)
//		    	{
//		    		System.out.print(rs.getString(i) + ", ");
//		    	}
//		    	System.out.print("\n");
//		    }
		    
		    /**
		     * ssh command to connect:
		     * ssh -N -p 22 k9e6@remote.ugrad.cs.ubc.ca -L 9998:dbhost.ugrad.cs.ubc.ca:1521
		     */
		    /** 
		     * Below are tests from Martin, executed properly
		     */
//		    String create = "CREATE TABLE Test( id INTEGER, text CHAR(8), PRIMARY KEY(id))";
//		    stmt.executeUpdate(create);
//		    
//		    String insert = "INSERT INTO Test(id,text) VALUES(0,'haha')";
//		    stmt.executeUpdate(insert);
//		    insert = "INSERT INTO Test(id,text) VALUES(5,'yay')";
//		    stmt.executeUpdate(insert);
//		    insert = "INSERT INTO Test(id,text) VALUES(9,'lol')";
//		    stmt.executeUpdate(insert);
//		    
//		    String select = "SELECT * FROM Test";
//		    ResultSet rs = stmt.executeQuery(select);
//		    
//		    while (rs.next())
//		    	System.out.println(rs.getInt("id") + ": " + rs.getString("text"));
//		    
//		    select = "SELECT text FROM Test WHERE id > 3";
//		    rs = stmt.executeQuery(select);
//		    
//		    while (rs.next())
//		    	System.out.println(rs.getString("text"));
		    
		    //System.out.println("\n-end");
		}
		catch(SQLException e)
		{
			System.out.println(e.getMessage());
		}
	}
	
	private static void cleanUp()
	{
		try
		{
//			con.rollback();
			con.close();
//			String username = "ora_v6c6";
//			String password = "a83455063";
//			String dbUrl = "jdbc:oracle:thin:@localhost:9998:ug";
//			Connection con2 = DriverManager.getConnection(dbUrl, username, password);
//			con2.rollback();
			
		}
		catch(SQLException e)
		{
			System.out.println(e.getMessage());
		}
	}
}
