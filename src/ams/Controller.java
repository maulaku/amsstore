package ams;

import java.awt.Color;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.Vector;

import ams.exceptions.OutOfStockException;
import ams.model.ItemDAO;
import ams.model.Purchase;
import ams.model.PurchaseDAO;
import ams.model.PurchaseItem;
import ams.model.Receipt;
import ams.model.ShipItem;
import ams.model.Shipment;
import ams.model.ShipmentDAO;
import ams.ui.AMSFrame;

public class Controller {
	
	private static Controller instance;
	
	private static Connection connection;
	
	private AMSFrame uiFrame;
	
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
	 */
	public void deleteTuple(String tableName, Vector<Object> whereValues) throws SQLException
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
	}
	
	/**
	 * Warning: this may delete multiple tuples if your key does not single out 1 tuple.
	 * 
	 * @param tableName the name of the table from where you are deleting
	 * @param whereValues a mapping of column names to values of the tuple you are deleting
	 */
	public void deleteTuple(String tableName, Map<String,Object> whereValues) throws SQLException
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
	}
	
	/**
	 * Inserts a tuple into a table given the values of the tuple.
	 * 
	 * @param tableName the name of the table into which you are inserting
	 * @param values a vector of objects containing the values of the tuple being inserted
	 */
	public void insertTuple(String tableName, Vector<Object> values) throws SQLException
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
	}
	
	public Receipt purchase(Purchase purchase)
	{
		Date date = new Date(System.currentTimeMillis());
		purchase.setPurchaseDate(date);
		
		long rId = PurchaseDAO.getInstance().findMaxReceiptID() + 1;
		Receipt receipt = new Receipt(rId, purchase);
		Connection con = getConnection();
		PurchaseItem item = null;
		try
		{
			con.setAutoCommit(false);
			PurchaseDAO.getInstance().insertPurchase(rId, purchase);
		
			for (PurchaseItem pItem : purchase.getPurchaseItems())
			{
				item = pItem;
				PurchaseDAO.getInstance().insertPurchaseItem(receipt, pItem, purchase.getStoreName());
			}
			
			con.commit();
			con.setAutoCommit(true);
		} 
		catch (OutOfStockException oosE)
		{
			if (item == null)
				rollback("Purchase Failed.");
			else
				rollback("Purchase Failed: out of stock for item with upc: " + item.getUPC());
			receipt = null;
		}catch (SQLException e)
		{			
			rollback("Purchase Failed: " + e.getMessage());
			receipt = null;
		}
		return receipt;
	}
	
	public void processShipment(Shipment shipment)
	{
		Date date = new Date(System.currentTimeMillis());
		shipment.setDate(date);
		Connection con = getConnection();
		try
		{			
			con.setAutoCommit(false);
			long sId = ShipmentDAO.getInstance().findMaxShipmentID() + 1;
			ShipmentDAO.getInstance().insertShipment(sId, shipment);
			
			for (ShipItem item : shipment.getShipItems())
			{				
				ShipmentDAO.getInstance().insertShipItem(sId, item);
				ItemDAO.getInstance().updatePrice(item.getUPC(), item.getPrice() * 1.20);
				ItemDAO.getInstance().updateStock(shipment.getStoreName(), item.getUPC(), item.getQuantity());
			}
			setStatusString("Shipment successfully processed.", AMSFrame.SUCCESS);
			
			con.commit();
			con.setAutoCommit(true);
		} catch (OutOfStockException oosE)
		{
			rollback("Shipment Process Failed: shipping negative quantities causing item to run out of stock");
		} catch (SQLException e)
		{		
			rollback("Shipment Process Failed: " + e.getMessage());
		}
	}
	
	public void processDelivery(long rId)
	{
		Date date = new Date(System.currentTimeMillis());
		Connection con = getConnection();
		try
		{
			con.setAutoCommit(false);
			
			// set delivered date
			PurchaseDAO.getInstance().updateDeliveredDate(rId, date);
		
			setStatusString("Delivery Process Successful.", AMSFrame.SUCCESS);
			con.commit();
			con.setAutoCommit(true);
		} catch (OutOfStockException oosE)
		{
			rollback("Delivery Process Failed: item not enough stock");
		} catch (SQLException e)
		{
			rollback("Delivery Process Failed: " + e.getMessage());
		}	
	}
	
	public void start()
	{
		uiFrame = new AMSFrame();
		uiFrame.pack();
		uiFrame.setVisible(true);
	}
	
	public void stop()
	{
		try
		{
			if (insert != null)
				insert.close();
			if (delete != null)
				delete.close();
			connection.close();
		} catch (SQLException e)
		{
			e.printStackTrace();
		}
	}
	
	public void setStatusString(String status)
	{
		setStatusString(status, null);
	}
	
	public void setStatusString(String status, Color color)
	{
		uiFrame.setStatusString(status);
		uiFrame.setStatusColor(color);
	}	
	
	private void rollback(String errorMsg)
	{
		Connection con = getConnection();
		try
		{
			con.rollback();
			con.setAutoCommit(true);
		} catch (SQLException ex)
		{
			ex.printStackTrace();
		}
		setStatusString(errorMsg, AMSFrame.FAILURE);
	}
}
