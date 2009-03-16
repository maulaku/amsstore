package ams;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Controller {

	private static Controller instance;
	
	private static Connection connection;
	
	private Controller() {}
	
	public void initializeConnection(String dbUrl, String username, String password) throws SQLException
	{
		connection = DriverManager.getConnection(dbUrl, username, password);
	}
	
	public static Controller getInstance()
	{
		if (instance == null)
			instance = new Controller();
		return instance;
	}
	
	public Connection getConnection()
	{
		return connection;
	}
}
