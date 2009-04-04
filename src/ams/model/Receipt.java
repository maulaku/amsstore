package ams.model;

import java.util.Vector;

public class Receipt
{
	private long id;
	
	private Purchase pur;
	
	private Vector<Item> items;
	
	public Receipt(long rId, Purchase purchase)
	{
		id = rId;
		pur = purchase;
		
		items = new Vector<Item>();
	}
	
	public void addItem(Item item)
	{
		items.add(item);
	}
	
	public long getReceiptId()
	{
		return id;
	}
	
	public Purchase getPurchase()
	{
		return pur;
	}
	
	public Vector<Item> getItems()
	{
		return items;
	}
	
}
