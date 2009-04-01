package ams.model;

import java.sql.Date;
import java.util.Vector;

public class Shipment
{
	private Date shipmentDate;
	
	private Vector<ShipItem> items;
	
	private String storeName, supplierName;
	
	public Shipment()
	{
		items = new Vector<ShipItem>();
	}
	
	public void addShipItem(ShipItem item)
	{
		items.add(item);
	}
	
	public Vector<ShipItem> getShipItems()
	{
		return items;
	}
	
	public void setStoreName(String name)
	{
		storeName = name;
	}
	
	public String getStoreName()
	{
		return storeName;
	}
	
	public void setSupplierName(String name)
	{
		supplierName = name;
	}
	
	public String getSupplierName()
	{
		return supplierName;
	}
	
	public void setDate(Date date)
	{
		shipmentDate = date;
	}
	
	public Date getShipmentDate()
	{
		return shipmentDate;
	}
}
