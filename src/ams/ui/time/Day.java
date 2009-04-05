package ams.ui.time;

import java.util.Calendar;

public class Day implements Comparable<Day>
{

	private Month month;
	
	private Integer year;
	
	private Integer day;
	
	public Day(Calendar calendar)
	{
		year = calendar.get(Calendar.YEAR);
		month = Month.getMonth(calendar.get(Calendar.MONTH));
		day = calendar.get(Calendar.DAY_OF_MONTH);
	}
	
	@Override
	public int compareTo(Day o)
	{
		int value = year.compareTo(o.year);
		if (value != 0)
			return value;
		value = month.compareTo(o.month);
		if (value != 0)
			return value;
		value = day.compareTo(o.day);
		return value;
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if (obj == null || !(obj instanceof Day))
			return false;
		Day day = (Day) obj;
		return compareTo(day)==0;
	}
}
