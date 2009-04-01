package ams.model;

public class Item
{
	private int id, year;
	
	private String title, type, category, company;
	
	private double price;
	
	public Item(int upc)
	{
		id = upc;
	}
	
	public int getUPC()
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
}
