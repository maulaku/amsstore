package ams.model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import ams.Controller;
import ams.exceptions.OutOfStockException;

public class ItemDAO {
		
	private static ItemDAO instance;
	
	private ItemDAO() 
	{
		

	}

	
	public static Vector<Item> selectForItemSearch(String title, String category, String leadSingerName)
	{
		Vector<Item> results = new Vector<Item>();
		ResultSet rs = null;
		try
		{
			String query;
			int titleIndex = -1;
			if(leadSingerName.equals(""))
			{
				query = "SELECT * FROM ITEM WHERE title LIKE ? AND category LIKE ?";
				titleIndex = 1;
			}
			else
			{
				query = "SELECT * FROM ITEM WHERE upc IN(SELECT upc from LEADSINGER WHERE name LIKE ? GROUP BY upc) AND title LIKE ? AND category LIKE ?";
				titleIndex = 2;
			}
			
			PreparedStatement ps = Controller.getInstance().getConnection().prepareStatement(query);			
			
			if(leadSingerName.equals(""))
				ps.setString(1, "%");
			else
				ps.setString(1, leadSingerName);
			
			if(title.equals(""))
				ps.setString(titleIndex, "%");
			else
				ps.setString(titleIndex, title);
			
			if(category.equals(""))
				ps.setString(titleIndex+1, "%");
			else
				ps.setString(titleIndex+1, category);
			
//			System.out.println(query);
			rs = ps.executeQuery();
			
			while (rs.next())
			{
				Item item = new Item(rs.getLong(1));
				item.setTitle(rs.getString(2));
				item.setType(rs.getString(3));
				item.setCategory(rs.getString(4));
				item.setCompany(rs.getString(5));
				item.setYear(rs.getInt(6));
				item.setPrice(rs.getDouble(7));
				results.add(item);
			}
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		return results;
	}

	
	public static ItemDAO getInstance()
	{
		if (instance == null)
			instance = new ItemDAO();
		return instance;
	}
	
	public Item getItem(long upc) throws SQLException
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
	
	public void updatePrice(long upc, double price) throws SQLException
	{
		String update = "UPDATE ITEM SET sellPrice = ? WHERE upc = ?";
		PreparedStatement statement = Controller.getInstance().getConnection().prepareStatement(update);
		statement.setDouble(1, price);
		statement.setLong(2, upc);
		statement.executeUpdate();
		statement.close();		
	}	
	
	/**
	 * Sets the stock of the item specified by upc to be old stock + dQuantity.
	 * @param upc
	 * @param dQuantity
	 * @throws SQLException
	 */
	public void updateStock(String storeName, long upc, int dQuantity) throws SQLException
	{
		String query = "SELECT stock FROM STORED WHERE name = ? AND upc = ?";
		PreparedStatement statement = Controller.getInstance().getConnection().prepareStatement(query);
		statement.setString(1, storeName);
		statement.setLong(2, upc);
		ResultSet result = statement.executeQuery();
		int oldQuantity = 0;
		if (result.next())
			oldQuantity = result.getInt(1);
		statement.close();
		
		if (oldQuantity + dQuantity < 0)
			throw new OutOfStockException();
		
		String update = "UPDATE STORED SET stock = ? WHERE name = ? AND upc = ?";
		statement = Controller.getInstance().getConnection().prepareStatement(update);
		statement.setInt(1, oldQuantity + dQuantity);
		statement.setString(2, storeName);
		statement.setLong(3, upc);
		statement.executeUpdate();
		statement.close();
	}

}
