package ams.model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import ams.Controller;

public class ItemDAO {
	
	private static ItemDAO instance;
	
	private ItemDAO() 
	{
		
	}
	
	public static ItemDAO getInstance()
	{
		if (instance == null)
			instance = new ItemDAO();
		return instance;
	}
	
	public Item getItem(int upc) throws SQLException
	{
		String query = "SELECT * FROM ITEM WHERE upc=" + upc;
		
		PreparedStatement statement = Controller.getInstance().getConnection().prepareStatement(query);
		ResultSet results = statement.executeQuery();
		results.next();
		Item item = new Item(upc);
		item.setTitle(results.getString(2));
		item.setType(results.getString(3));
		item.setCategory(results.getString(4));
		item.setCompany(results.getString(5));
		item.setYear(results.getInt(6));
		item.setPrice(results.getDouble(7)); 
		
		return item;
	}
	
	public void updatePrice(int upc, double price) throws SQLException
	{
		String update = "UPDATE ITEM SET sellPrice = ? WHERE upc = ?";
		PreparedStatement statement = Controller.getInstance().getConnection().prepareStatement(update);
		statement.setDouble(1, price);
		statement.setInt(2, upc);
		statement.executeUpdate();
		statement.close();		
	}
	
	/**
	 * Sets the stock of the item specified by upc to be old stock + dQuantity.
	 * @param upc
	 * @param dQuantity
	 * @throws SQLException
	 */
	public void updateStock(int upc, int dQuantity) throws SQLException
	{
		String query = "SELECT stock FROM STORED WHERE upc = ?";
		PreparedStatement statement = Controller.getInstance().getConnection().prepareStatement(query);
		statement.setInt(1, upc);
		ResultSet result = statement.executeQuery();
		result.next();
		int oldQuantity = result.getInt(1);
		statement.close();
		
		String update = "UPDATE STORED SET stock = ? WHERE upc = ?";
		statement = Controller.getInstance().getConnection().prepareStatement(update);
		statement.setInt(1, oldQuantity + dQuantity);
		statement.setInt(2, upc);
		statement.executeUpdate();
		statement.close();
	}
}
