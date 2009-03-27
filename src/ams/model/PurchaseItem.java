package ams.model;

public class PurchaseItem
{
	private int upc, quantity;
	
	public PurchaseItem(int itemUPC, int itemQuantity)
	{
		upc = itemUPC;
		quantity = itemQuantity;
	}
	
	public int getUPC()
	{
		return upc;
	}
	
	public int getQuantity()
	{
		return quantity;
	}
}
