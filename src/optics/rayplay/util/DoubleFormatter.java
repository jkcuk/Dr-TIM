package optics.rayplay.util;

import java.text.DecimalFormat;

public class DoubleFormatter {

	// consistent formatting of double numbers
	private static DecimalFormat df3 = new DecimalFormat( "#,###,###,##0.000" );

	public static String format(double number)
	{
		return df3.format(number);
	}
}
