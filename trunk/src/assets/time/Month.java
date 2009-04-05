package assets.time;

public enum Month {
	JANUARY,
	FEBRUARY,
	MARCH,
	APRIL,
	MAY,
	JUNE,
	JULY,
	AUGUST,
	SEPTEMBER,
	OCTOBER,
	NOVEMBER,
	DECEMBER;
	
	public static Month getMonth(int month)
	{
		return values()[month];
	}
	
	public Month getPrevious()
	{
		int ordinal = ordinal();
		if (ordinal == 0)
			ordinal = values().length;
			
		return values()[ordinal-1];
	}
	
	public Month getNext()
	{
		int ordinal = ordinal();
		if (ordinal == values().length-1)
			ordinal = -1;
		return values()[ordinal+1];
	}
	
	@Override
	public String toString() {
		String name = name().toLowerCase();
		name = name.substring(0,1).toUpperCase() + name.substring(1,name.length());
		return name;
	}
}
