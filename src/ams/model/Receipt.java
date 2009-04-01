package ams.model;

import java.util.Vector;

public class Receipt
{
	private int id;
	
	private Purchase pur;
	
	private Vector<Item> items;
	
	public Receipt(int rId, Purchase purchase)
	{
		id = rId;
		pur = purchase;
		
		items = new Vector<Item>();
	}
	
	public void addItem(Item item)
	{
		items.add(item);
	}
	
	public int getReceiptId()
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
