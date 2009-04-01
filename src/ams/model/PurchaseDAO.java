package ams.model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import ams.Controller;
import ams.ui.AMSFrame;

public class PurchaseDAO
{
	private static PurchaseDAO instance;
	
	private PurchaseDAO()
	{
	}
	
	public static PurchaseDAO getInstance()
	{
		if (instance == null)
			instance = new PurchaseDAO();
		return instance;
	}
	
	private int findNextReceiptID()
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
		
	public Receipt purchase(Purchase purchase)
	{
		int rId = findNextReceiptID() + 1;
		Receipt receipt = new Receipt(rId, purchase);
		try
		{
			Vector<Object> values = new Vector<Object>();
			values.add(rId);
			values.add(purchase.getPurchaseDate());
			values.add(null);
			values.add(null);
			Integer creditNum = purchase.isPaidByCredit() ? purchase.getCreditCardNum() : null;
			String creditDate = purchase.isPaidByCredit() ? purchase.getCardExpiryDate() : null;
			values.add(creditNum);
			values.add(creditDate);
			values.add(null);
			values.add(null);
			Controller.getInstance().insertTuple("PURCHASE", values);
			insertPurchaseItems(receipt, purchase.getPurchaseItems());
			Controller.getInstance().setStatusString("Purchase successful: see receipt.", AMSFrame.SUCCESS);
		} catch (SQLException e)
		{			
			receipt = null;
			e.printStackTrace();
			Controller.getInstance().setStatusString("Purchase Failed: " + e.getMessage(), AMSFrame.FAILURE);
		}
		return receipt;
	}
	
	public void insertPurchaseItems(Receipt receipt, PurchaseItem[] items) throws SQLException
	{		
		Vector<Object> values = new Vector<Object>();
		for (PurchaseItem item: items)
		{		
			values.clear();						
			values.add(receipt.getReceiptId());
			values.add(item.getUPC());
			values.add(item.getQuantity());
			
			Controller.getInstance().insertTuple("PURCHASEITEM", values);
			receipt.addItem(ItemDAO.getInstance().getItem(item.getUPC()));
		}
	}
}
