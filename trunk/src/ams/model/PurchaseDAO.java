package ams.model;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import ams.Controller;
import ams.ui.AMSFrame;

public class PurchaseDAO
{
	private static PurchaseDAO instance;
	
	public static final int DELIVERIES_PER_DAY = 10;
	
	private PurchaseDAO()
	{
	}
	
	public static PurchaseDAO getInstance()
	{
		if (instance == null)
			instance = new PurchaseDAO();
		return instance;
	}
	
	public long findMaxReceiptID()
	{
		int receiptId = 0;
		try
		{
			String query = "SELECT MAX(receiptId) FROM Purchase";
			PreparedStatement statement = Controller.getInstance().getConnection().prepareStatement(query);
			ResultSet result = statement.executeQuery();
			result.next();
			receiptId = result.getInt(1);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return receiptId;
	}	
	
	public void insertPurchase(long rId, Purchase purchase) throws SQLException
	{
		Vector<Object> values = new Vector<Object>();
		values.add(rId);
		values.add(purchase.getPurchaseDate());
		values.add(null);
		values.add(null);
		Long creditNum = purchase.isPaidByCredit() ? purchase.getCreditCardNum() : null;
		Date creditDate = purchase.isPaidByCredit() ? purchase.getCardExpiryDate() : null;
		values.add(creditNum);
		values.add(creditDate);
		values.add(null);
		values.add(null);
		Controller.getInstance().insertTuple("PURCHASE", values);
	}
	
	public void insertPurchaseItem(Receipt receipt, PurchaseItem item) throws SQLException
	{		
		Vector<Object> values = new Vector<Object>();
		values.clear();						
		values.add(receipt.getReceiptId());
		values.add(item.getUPC());
		values.add(item.getQuantity());
		
		Controller.getInstance().insertTuple("PURCHASEITEM", values);
		ItemDAO.getInstance().updateStock(item.getUPC(), -item.getQuantity());
		receipt.addItem(ItemDAO.getInstance().getItem(item.getUPC()));
	}
	
	public Vector<PurchaseItem> selectPurchaseItems(long rId) throws SQLException
	{
		Vector<PurchaseItem> items = new Vector<PurchaseItem>();
		
		String query = "SELECT upc, quantity FROM PURCHASEITEM WHERE receiptId = ?";
		PreparedStatement statement = Controller.getInstance().getConnection().prepareStatement(query);
		statement.setLong(1, rId);
		ResultSet results = statement.executeQuery();
		
		while (results.next())
		{
			long upc = results.getLong(1);
			int quantity = results.getInt(2);
			PurchaseItem item = new PurchaseItem(upc, quantity);
			items.add(item);
		}
		return items;
	}
	
	public void updateDeliveredDate(long receiptId, Date deliveredDate) throws SQLException
	{
		// set delivered date
		String update = "UPDATE PURCHASE SET deliveredDate = ? WHERE receiptId = ?";
		PreparedStatement statement = Controller.getInstance().getConnection().prepareStatement(update);
		statement.setDate(1, deliveredDate);
		statement.setLong(2, receiptId);
		
		statement.executeUpdate();
	}
	
	public static Date getExpectedDeliveryDate()
	{
		Date estimatedDelivery = null;
		try
		{
			String query = "SELECT count(*) FROM PURCHASE WHERE delivereddate IS null";
			PreparedStatement statement = Controller.getInstance().getConnection().prepareStatement(query);
//			statement.setDate(1, null);
			ResultSet rs = statement.executeQuery();
			rs.next();
			int outstandingDeliveries = rs.getInt(1);
			int daysToWait = outstandingDeliveries/PurchaseDAO.DELIVERIES_PER_DAY;
			estimatedDelivery = new Date(System.currentTimeMillis());
			estimatedDelivery.setDate(estimatedDelivery.getDate()+daysToWait);
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		return estimatedDelivery;
	}
}
