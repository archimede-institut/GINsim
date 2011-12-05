/*
 * @(#)ImageLoader.java	1.0 23/01/02
 *
 * Copyright (C) 2003 Sven Luzar
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 */
package org.ginsim.gui.resource;

import java.awt.Image;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;

/**Loader for the resource images. The class
 * uses the getResource Method to get the
 * Resource from the relative path.
 * 
 * NOTE: hou HAVE to push at least a default serch path
 */
public class ImageLoader {

	/** contains string objects which respresents the search paths
	 */	
	protected static List<String> searchPath = new ArrayList<String>();

	/** Returns an Image from the same path.
	 *  @param imageName An image name with the file extension
	 *                    buttonEdge.g. Can.gif
	 * @return Image
	 */
	public static Image getImage(String imageName){
		return getImageIcon(imageName).getImage() ;
	}

    /** Returns an ImageIcon from the same path.
     *  @param imageName An image name with the file extension
     *                    buttonEdge.g. Can.gif
     * @return an image icon for this image.
     */
    public static ImageIcon getImageIcon(String imageName){
        return getImageIcon(searchPath.size()-1, imageName);
    }

    /**
     * Return the path of an image.
     * @param imageName
     * @return the path
     */
    public static URL getImagePath(String imageName){
        return getImagePath(searchPath.size()-1, imageName);
    }

    /** Returns an ImageIcon from the same path.
     * @param searchPathIndex
     *  @param imageName An image name with the file extension
     *                    buttonEdge.g. Can.gif
     * @return an ImageIcon for this image.
     */
    public static ImageIcon getImageIcon(int searchPathIndex , String imageName){
        URL url = getImagePath(searchPathIndex, imageName);
        if (url == null) {
            return null;
        }
        return new ImageIcon(url);
    }
    
    /**
     * Return the path of an image.
     * @param searchPathIndex
     *  @param imageName An image name with the file extension
     *                    buttonEdge.g. Can.gif
     * @return the path
     */
    public static URL getImagePath(int searchPathIndex , String imageName){
        // precondition test
        if (imageName == null) {
			return null;
		}
            
        // image loading
        if (searchPathIndex < searchPath.size() && searchPathIndex >= 0){
        	String s_url = (String)searchPath.get(searchPathIndex) + imageName;
            URL url = ImageLoader.class.getResource(s_url) ;
            if (url == null) {
            	File f = new File(s_url);
            	if (f.exists()) {
            		try {
						return new URL("file", "", f.getAbsolutePath());
					} catch (MalformedURLException e) {}
            	}
            }
            if (url != null){
                return url;
            }
            return getImagePath(searchPathIndex - 1, imageName);
        } 
        return null;
    }
    
	/** 
	 * pushes the specified path to the search path.
	 *  
	 * An example for a search path file name is 'com/jgraph/pad/resources'.
	 * @param path
	 *  
	 */
	public static void pushSearchPath(String path){
		if (path != null) {
			searchPath.add(path.endsWith("/") ? path : path+"/");
		}
	}
}
