package ams.model;

public class Item
{
	private int year;
	
	private long id;
	
	private String title, type, category, company;
	
	private double price;
	
	public Item(long upc)
	{
		id = upc;
	}
	
	public long getUPC()
	{
		return id;
	}
	
	public void setTitle(String t)
	{
		title = t;
	}
	
	public String getTitle()
	{
		return title;
	}
	
	public void setType(String t)
	{
		type = t;
	}
	
	
	public String getType()
	{
		return type;
	}
	
	public void setCategory(String c)
	{
		category = c;
	}
	
	
	public String getCategory()
	{
		return category;
	}
	
	public void setCompany(String c)
	{
		company = c;
	}
	
	public String getCompany()
	{
		return company;
	}
	
	public void setYear(int y)
	{
		year = y;
	}
	
	public int getYear()
	{
		return year;
	}
	
	public void setPrice(double p)
	{
		price = p;
	}
	
	public double getPrice()
	{
		return price;
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if (obj == null || !(obj instanceof Item)) return false;
		Item item = (Item) obj;
		return id == item.id;
	}
	
	@Override
	public int hashCode()
	{
		return (int) id;
	}
}
