package org.ginsim.common;

import java.awt.Color;

public class ColorPalette {
	public static final Color[] RAINBOW = {
		new Color(220, 0, 99),
		new Color(240, 0, 0),
		new Color(240, 130, 41),
		new Color(230, 176, 46),
		new Color(230, 220, 51),
		new Color(161, 230, 51),
		new Color(0, 220, 0),
		new Color(0, 209, 140),
		new Color(0, 200, 200),
		new Color(0, 161, 230),
		new Color(31, 61, 250),
		new Color(130, 0, 220),
		new Color(161, 0, 199),
	};
	
	public static final Color[] defaultPalette = new Color[] {
		new Color(32, 74, 135),
		new Color(164, 0, 0),
		new Color(78, 154, 6),
		new Color(114, 159, 207),
		new Color(239, 41, 41),
		new Color(138, 226, 52),
		new Color(252, 175, 62),
		new Color(233, 185, 110),
		new Color(252, 233, 79),
		new Color(173, 127, 168),
		new Color(143, 89, 2),
		new Color(196, 160, 0),
		new Color(92, 53, 102),
		new Color(206, 92, 0),

	};
	public static final Color[] defaultPalette3 = new Color[] {
		new Color(115, 173, 203),
		new Color(246, 73, 69),
		new Color(170, 209, 111),
		new Color(255, 209, 122),
		new Color(70, 95, 146),
		new Color(218, 215, 255),
		new Color(227, 134, 58),
		new Color(66, 255, 66),
		new Color(59, 107, 222),
		new Color(176, 58, 60)
	};
	public static final Color[] defaultPalette2 = new Color[] {
		Color.red,
		Color.blue,
		Color.green,
		Color.orange,
		Color.magenta,
		Color.cyan,
		Color.black,
		Color.pink,
		Color.yellow
	};
	
	/**
	 * An array of blue hues, all the blue web colors plus the greenish blues Teal, MediumAquamarine... and purplish blues Indigo, MediumPurple...
	 */
	public static final Color[] blueHues = new Color[] {
		new Color(0,0,255),
		new Color(30,144,255),
		new Color(127,255,212),
		new Color(0,139,139),
		new Color(186,85,211),
		new Color(153,50,204),
		new Color(0,0,205),
		new Color(143,188,143),
		new Color(95,158,160),
		new Color(175,238,238),
		new Color(128,0,128),
		new Color(25,25,112),
		new Color(0,191,255),
		new Color(0,206,209),
		new Color(139,0,139),
		new Color(176,224,230),
		new Color(100,149,237),
		new Color(176,196,222),
		new Color(75,0,130),
		new Color(72,209,204),
		new Color(135,206,250),
		new Color(70,130,180),
		new Color(106,90,205),
		new Color(123,104,238),
		new Color(32,178,170),
		new Color(0,255,255),
		new Color(0,0,128),
		new Color(147,112,219),
		new Color(0,128,128),
		new Color(72,61,139),
		new Color(135,206,235),
		new Color(64,224,208),
		new Color(224,255,255),
		new Color(102,205,170),
		new Color(0,0,139),
		new Color(148,0,211),
		new Color(65,105,225),
		new Color(138,43,226),
		new Color(173,216,230)
	};
	
	/**
	 * Create a palette of color by variing the hue for nbcolors steps
	 * @param nbcolors
	 * @param saturation
	 * @param brightness
	 */
	public static Color[] createColorPaletteByRange(int nbcolors, float saturation, float brightness) {
    	Color[] colorPalette = new Color[nbcolors];
    	for (int i = 0; i < nbcolors ; i++) {
			colorPalette[i] = Color.getHSBColor((float)i/(float)nbcolors , saturation, brightness);
		}
    	return colorPalette;
    }
	public static Color[] createColorPaletteByRange(int nbcolors) {
		return createColorPaletteByRange(nbcolors, 0.85f, 1.0f);
	}

	/**
	 * Return black or white to contrast with the given background
	 * @param backgroundColor the color of the background
	 * @return Color.BLACK or Color.WHITE
	 */
	public static Color getConstrastedForegroundColor(Color backgroundColor) {
		if (isColorPercievedBright(backgroundColor)) {
			return Color.BLACK;
		}
		return Color.WHITE; 
	}
		
	/**
	 * Indicates if the given color is percieved bright
	 * @param col
	 * @return true if the color is percieved bright
	 */
	public static boolean isColorPercievedBright(Color col) {
		return percievedBrightness(col.getRed(), col.getGreen(), col.getBlue()) >= 130;
	}

	/**
	 * Return the percieved brightness of the given color
	 * @param r red value from 0-255
	 * @param g green value from 0-255
	 * @param b blue value from 0-255
	 * @return the percieved brightness of the given color
	 */
	public static int percievedBrightness(int r, int g, int b) {
		   return (int)Math.sqrt(
				   r * r * .241 + 
				   g * g * .691 + 
				   b * b * .068);
	}
}
