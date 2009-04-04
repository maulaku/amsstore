package ams.model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import ams.Controller;
import ams.exceptions.OutOfStockException;

public class ItemDAO {
		
	private static ItemDAO instance;
	
	private ItemDAO() 
	{
		

	}

	
	public static ResultSet selectForItemSearch(String title, String category, String leadSingerName)
	{
		ResultSet rs = null;
		try
		{
			String query = "SELECT * FROM " +
					"(SELECT * FROM ITEM NATURAL JOIN (Select * from LeadSinger))"; 
			
			if(title.equals("") == false || category.equals("") == false || leadSingerName.equals("") == false)
			{
				query += "WHERE ";			
				if(title.equals("") == false)
				{
					query += "title='"+title+"'";
				}
				if(title.equals("") == false && category.equals("") == false)
				{
					query += " AND ";
				}
				if(category.equals("") == false)
				{
					query += "category='"+category+"'";
				}
				if((category.equals("") == false || title.equals("") == false) && leadSingerName.equals("") == false)
				{
					query += " AND ";
				}
				if(leadSingerName.equals("") == false)
				{
					query += "name='"+leadSingerName+"'";
				}
			}
			//query += " GROUP BY upc";
			System.out.println(query);
			PreparedStatement pstmt = Controller.getInstance().getConnection().prepareStatement(query);			
			rs = pstmt.executeQuery();
		}
		catch(SQLException e)
		{
			System.err.println(e.getMessage());
		}
		return rs;
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
	public void updateStock(long upc, int dQuantity) throws SQLException
	{
		String query = "SELECT stock FROM STORED WHERE upc = ?";
		PreparedStatement statement = Controller.getInstance().getConnection().prepareStatement(query);
		statement.setLong(1, upc);
		ResultSet result = statement.executeQuery();
		result.next();
		int oldQuantity = result.getInt(1);
		statement.close();
		
		if (oldQuantity + dQuantity < 0)
			throw new OutOfStockException();
		
		String update = "UPDATE STORED SET stock = ? WHERE upc = ?";
		statement = Controller.getInstance().getConnection().prepareStatement(update);
		statement.setInt(1, oldQuantity + dQuantity);
		statement.setLong(2, upc);
		statement.executeUpdate();
		statement.close();
	}

}
