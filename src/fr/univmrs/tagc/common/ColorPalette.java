package fr.univmrs.tagc.common;

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


}
