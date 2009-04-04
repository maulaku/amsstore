package ams.model;

import java.sql.Date;

public class Purchase
{
	private PurchaseItem[] items;
	
	private boolean payByCredit;
	
	private long creditCardNum;
	
	private Date purchaseDate, expiryDate;	
	
	public void setPurchaseItems(PurchaseItem[] purchaseItems)
	{
		items = purchaseItems;
	}
	
	public PurchaseItem[] getPurchaseItems()
	{
		return items;
	}
	
	public void setPayByCredit(long ccNum, Date expDate)
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
	
	public long getCreditCardNum()
	{
		return creditCardNum;
	}
	
	public Date getCardExpiryDate()
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
