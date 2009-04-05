package ams.model;

import java.sql.Date;

public class Purchase
{
	private PurchaseItem[] items;
	
	private boolean payByCredit, online;
	
	private long creditCardNum;
	
	private Date purchaseDate, expiryDate, expectedDate;
	
	private long cid;
	
	private String storeName; 
	
	public void setCID(long id)
	{
		cid = id;
	}
	
	public long getCID()
	{
		return cid;
	}
	
	public void setStoreName(String name)
	{
		storeName = name;
	}
	
	public String getStoreName()
	{
		return storeName;
	}	
	
	public void setPurchaseOnline(boolean o)
	{
		online = o;
	}
	
	public boolean isPurchaseOnline()
	{
		return online;
	}
	
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
	
	public void setExpectedDate(Date date)
	{
		expectedDate = date;
	}
	
	public Date getExpectedDate()
	{
		return expectedDate;
	}
}
