package ams.model;

public class PurchaseItem
{
	private int quantity;
	private long upc;
	
	public PurchaseItem(long itemUPC, int itemQuantity)
	{
		upc = itemUPC;
		quantity = itemQuantity;
	}
	
	public long getUPC()
	{
		return upc;
	}
	
	public int getQuantity()
	{
		return quantity;
	}
}
