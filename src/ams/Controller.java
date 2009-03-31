package ams;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.Vector;

import ams.model.Purchase;
import ams.model.PurchaseDAO;
import ams.ui.AMSFrame;

public class Controller {

	private static Controller instance;
	
	private static Connection connection;
	
	private String currentInsert, currentDelete;
	private PreparedStatement insert, delete;
	
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
			e.printStackTrace();
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
			e.printStackTrace();
		}
		return set;
	}
	
	/**
	 * Deletes a tuple from a table given the values of the tuple.
	 * whereValues must contain all the values of the tuple. If missing columns, use deleteTuple(String tableName, Map<String,Object> whereValues)
	 * 
	 * @param tableName the name of the table from which you are deleting
	 * @param whereValues a vector of objects containing the values of the tuple being deleted
	 * @return true if operation is successful 
	 */
	public boolean deleteTuple(String tableName, Vector<Object> whereValues)
	{
		boolean result = true;
		try
		{
			if (currentDelete == null || !tableName.equals(currentDelete))
			{
				Vector<String> columnNames = getColumnNames(tableName);
				String whereString = "";
				for (int i = 0; i < columnNames.size(); ++i)
				{
					if (i > 0) 
						whereString += " AND ";
					whereString += columnNames.get(i) + "=?";
				}
					
				String query = "DELETE FROM " + tableName + " WHERE " + whereString;
				delete = getConnection().prepareStatement(query);
				currentDelete = tableName;
			}
			
			for (int i = 0; i < whereValues.size(); ++i)
				delete.setObject(i+1, whereValues.get(i));
			
			delete.executeUpdate();
			delete.close();
		} catch ( SQLException e )
		{
			e.printStackTrace();
			result = false;
		}
		return result;
	}
	
	/**
	 * Warning: this may delete multiple tuples if your key does not single out 1 tuple.
	 * 
	 * @param tableName the name of the table from where you are deleting
	 * @param whereValues a mapping of column names to values of the tuple you are deleting
	 * @return true if the operation is successful
	 */
	public boolean deleteTuple(String tableName, Map<String,Object> whereValues)
	{
		boolean result = true;
		try
		{
			String whereString = "";
			int i = 1;
			for (String columnName : whereValues.keySet())
			{
				if (i > 1) 
					whereString += " AND ";
				whereString += columnName + "=?";				
				++i;
			}					
				
			String query = "DELETE FROM " + tableName + " WHERE " + whereString;
			PreparedStatement statement = getConnection().prepareStatement(query);
			
			i = 1;
			for (String columnName : whereValues.keySet())
			{
				statement.setObject(i, whereValues.get(columnName));
				++i;
			}
			
			statement.executeUpdate();
			statement.close();
		} catch ( SQLException e )
		{
			e.printStackTrace();
			result = false;
		}
		return result;
	}
	
	/**
	 * Inserts a tuple into a table given the values of the tuple.
	 * 
	 * @param tableName the name of the table into which you are inserting
	 * @param values a vector of objects containing the values of the tuple being inserted
	 * @return true if operation is successful
	 */
	public boolean insertTuple(String tableName, Vector<Object> values)
	{
		boolean result = true;
		try
		{
			if (currentInsert == null || !tableName.equals(currentInsert))
			{
				Vector<String> columnNames = getColumnNames(tableName);
				String tableDef = "";
				String valString = "";
				for (int i = 0; i < columnNames.size(); ++i)
				{
					if (i > 0) {
						tableDef += ",";
						valString += ",";
					}
					tableDef += columnNames.get(i);
					valString += "?";
				}
					
				String query = "INSERT INTO " + tableName + " (" + tableDef + ") VALUES (" + valString + ")";
				insert = getConnection().prepareStatement(query);
				currentInsert = tableName;
			}
			
			for (int i = 0; i < values.size(); ++i)
				insert.setObject(i+1, values.get(i));
			insert.executeUpdate();
			insert.close();
		} catch ( SQLException e)
		{
			result = false;
			e.printStackTrace();
		}
		return result;
	}
	
	public void purchase(Purchase purchase)
	{
		Date date = new Date(System.currentTimeMillis());
		purchase.setPurchaseDate(date);
		PurchaseDAO.getInstance().purchase(purchase);
	}
	
	public void start()
	{
		AMSFrame frame = new AMSFrame();
		frame.pack();
		frame.setVisible(true);
	}
}
