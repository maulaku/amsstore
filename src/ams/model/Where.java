package ams.model;

public class Where {
	
	enum Operator
	{
		EQUALS("="),
		NOT_EQUALS("!=");
		

		private String rep;
		Operator(String r)
		{
			rep = r;
		}
	}
	
	Where(Operator o, String attr, String value)
	{
	}
	
	public String getRep()
	{
		return "";
	}
}
