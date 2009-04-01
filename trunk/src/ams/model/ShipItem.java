package ams.model;

public class ShipItem
{
	private int upc, quantity;
	
	private double price;
	
	public ShipItem(int itemUPC)
	{
		upc = itemUPC;
	}	
	
	public int getUPC()
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
