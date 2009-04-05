package assets.time;

public class TimeUtil
{
	public static String getTimeText(double time)
	{
		int hours = (int) time;
		int minutes = (int) Math.round((time - hours) * 60);
		String text = Integer.toString(hours);
		text += ":";
		if (minutes < 10)
			text += "0";
		text += Integer.toString(minutes);
		return text;
	}
}
