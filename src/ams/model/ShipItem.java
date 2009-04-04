package ams.model;

public class ShipItem
{
	private long upc;
	
	private int quantity;
	
	private double price;
	
	public ShipItem(long itemUPC)
	{
		upc = itemUPC;
	}	
	
	public long getUPC()
	{
		return upc;
	}
	
	public void setPrice(double p)
	{
		price = p;
	}
	
	public double getPrice()
	{
		return price;
	}
	
	public void setQuantity(int q)
	{
		quantity = q;
	}
	
	public int getQuantity()
	{
		return quantity;
	} 
}
