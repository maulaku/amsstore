package ams.model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import ams.Controller;

public class PurchaseDAO
{
	private static PurchaseDAO instance;
	
	private int receiptId;	
	
	private PurchaseDAO()
	{
		findNextReceiptID();
	}
	
	public static PurchaseDAO getInstance()
	{
		if (instance == null)
			instance = new PurchaseDAO();
		return instance;
	}
	
	private void findNextReceiptID()
	{
		try
		{
			String query = "SELECT MAX(receiptId) FROM Purchase";
			PreparedStatement statement = Controller.getInstance().getConnection().prepareStatement(query);
			ResultSet result = statement.executeQuery();
			receiptId = result.getInt(1);
		} catch (SQLException e) {}
	}
		
	public void purchase(Purchase purchase)
	{
		int rId = ++receiptId;		
		String insert = "INSERT INTO PURCHASE (receiptId, purchaseDate, cid, name, cardNum, expire, expectedDate, deliveredDate) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

		try
		{
			PreparedStatement statement = Controller.getInstance().getConnection().prepareStatement(insert);
			statement.setInt(1, rId);
			statement.setDate(2, purchase.getPurchaseDate());
			statement.setNull(3, Types.INTEGER);
			statement.setNull(4, Types.CHAR);
			if (purchase.isPaidByCredit())
			{
				statement.setInt(5, purchase.getCreditCardNum());
				statement.setString(6, purchase.getCardExpiryDate());
			} else
			{
				statement.setNull(5, Types.INTEGER);
				statement.setNull(6, Types.CHAR);
			}
			statement.setNull(7, Types.DATE);
			statement.setNull(8, Types.DATE);
			
			statement.executeUpdate();
			statement.close();
			
			insertPurchaseItems(rId, purchase.getPurchaseItems());
		} catch (SQLException e)
		{
			e.printStackTrace();
		}
		
	}
	
	public void insertPurchaseItems(int rId, PurchaseItem[] items) throws SQLException
	{
		String insert = "INSERT INTO PurchaseItem (receiptId, upc, quantity) VALUES (?, ?, ?)";
		
		PreparedStatement statement = Controller.getInstance().getConnection().prepareStatement(insert);
		
		for (PurchaseItem item: items)
		{
			statement.setInt(1, rId);
			statement.setInt(2, item.getUPC());
			statement.setInt(3, item.getQuantity());
			statement.executeUpdate();
		}
		statement.close();
		
	}
}
