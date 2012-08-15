package org.ginsim.commongui.utils;

import java.awt.Image;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;

/**
 * Load images from a list of image sources.
 * This will load images inside jars or from the file system.
 * 
 * NOTE: GINsim will push some paths, extensions can add more if needed
 */
public class ImageLoader {

	private static List<String> paths = new ArrayList<String>();
	
	public static void pushSearchPath(String path) {
		if (!path.endsWith("/")) {
			path = path+"/";
		}
		paths.add(path);
	}

	public static URL getImagePath(String name) {
		for (String base: paths) {
			URL url = ImageLoader.class.getResource(base+name);
			if (url != null) {
				return url;
			}
		}
		
		// TODO log missing images?
		return null;
	}

	public static ImageIcon getImageIcon(String name) {
        URL url = getImagePath(name);
        if (url != null) {
            return new ImageIcon(url);
        }
        return null;
	}

	public static Image getImage(String name) {
		ImageIcon icon = getImageIcon(name);
		if (icon != null) {
			return icon.getImage();
		}
		return null;
	}

}
