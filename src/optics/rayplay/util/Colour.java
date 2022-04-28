package optics.rayplay.util;

import java.awt.Color;

public enum Colour {
		RED("Red", "red", Color.RED),
		GREEN("Green", "green", Color.GREEN),
		BLUE("Blue", "blue", Color.BLUE),
		ORANGE("Orange", "orange", Color.ORANGE),
		CYAN("Cyan", "cyan", Color.CYAN);
	
		public static Colour RAY_COLOURS[] = {RED, GREEN, BLUE, ORANGE};
		
		private final String name;
		private final String svgName;
		private final Color color;
		
		Colour(String name, String svgName, Color color)
		{
			this.name = name;
			this.svgName = svgName;
			this.color = color;
		}
		
		public String getName()
		{
			return name;
		}
		
		public String getSVGName()
		{
			return svgName;
		}
		
		public Color getColor()
		{
			return color;
		}
		
		public String getSVGNameOfDarkerColour()
		{
			return "dark"+svgName;
		}
		
		public Color getDarkerColor()
		{
			return color.darker().darker();
		}
}
