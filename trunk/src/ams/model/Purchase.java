package ams.model;

import java.sql.Date;

public class Purchase
{
	private PurchaseItem[] items;
	
	private boolean payByCredit;
	
	private int creditCardNum;
	
	private String expiryDate;
	
	private Date purchaseDate;
	
	public void setPurchaseItems(PurchaseItem[] purchaseItems)
	{
		items = purchaseItems;
	}
	
	public PurchaseItem[] getPurchaseItems()
	{
		return items;
	}
	
	public void setPayByCredit(int ccNum, String expDate)
	{
		payByCredit = true;
		creditCardNum = ccNum;
		expiryDate = expDate;
	}
	
	public void setPayByCash()
	{
		payByCredit = false;
	}
	
	public boolean isPaidByCredit()
	{
		return payByCredit;
	}
	
	public int getCreditCardNum()
	{
		return creditCardNum;
	}
	
	public String getCardExpiryDate()
	{
		return expiryDate;
	}
	
	public void setPurchaseDate(Date date)
	{
		purchaseDate = date;
	}
	
	public Date getPurchaseDate()
	{
		return purchaseDate;
	}
}
