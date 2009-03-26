package ams;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Vector;

import javax.swing.DefaultListModel;

import ams.ui.AMSFrame;

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
	
	public Vector<String> getTableNames()
	{
		Vector<String> set = new Vector<String>();
		try
		{
			
			PreparedStatement statement = getConnection().prepareStatement("SELECT table_name FROM user_tables");
			ResultSet results = statement.executeQuery();
			
			while (results.next())
				set.add(results.getString("TABLE_NAME"));
		} 
		catch (Exception e)
		{
			set = null;
		}
		return set;
	}
	
	public Vector<String> getColumnNames(String tableName) 
	{
		Vector<String> set = new Vector<String>();
	
		try
		{
			DatabaseMetaData data = getConnection().getMetaData();
			ResultSet columns = data.getColumns(null, null, tableName, null);
			while (columns.next())
				set.add(columns.getString("COLUMN_NAME"));
		} 
		catch (Exception e)
		{
			set = null;
		}
		return set;
	}
	
	public void start()
	{
		AMSFrame frame = new AMSFrame();
		frame.pack();
		frame.setVisible(true);
	}
}
