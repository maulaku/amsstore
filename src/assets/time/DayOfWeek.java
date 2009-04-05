package assets.time;

public enum DayOfWeek
{
	SUNDAY,
	MONDAY,
	TUESDAY,
	WEDNESDAY,
	THURSDAY,
	FRIDAY,
	SATURDAY;
	
	public final static String[] SHORT_FORM = new String[] { "S", "M", "T", "W", "T", "F", "S" };
	
	public static DayOfWeek getDayOfWeek(int day)
	{
		return values()[day-1];
	}
	
	public String getShortForm()
	{
		return SHORT_FORM[ordinal()];
	}
	
	public static DayOfWeek getFirstDayOfMonth(int day, DayOfWeek dayOfWeek)
	{
		int leftover = day % values().length;
		
		int ordinal = dayOfWeek.ordinal();
		if (dayOfWeek.ordinal() - leftover < 0)
			ordinal += values().length;
		
		return DayOfWeek.values()[ordinal - leftover];
	}
	
	@Override
	public String toString()
	{
		String name = name().toLowerCase();
		name = name.substring(0,1).toUpperCase() + name.substring(1,name.length());
		return name;
	}
	
}