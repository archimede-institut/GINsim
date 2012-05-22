/*
 * @(#)Translator.java	1.0 23/01/02
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
package org.ginsim.common.application;

import java.text.MessageFormat;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Stack;
import java.util.Vector;


/** 
 * Contains a list of ResourceBundle objects.
 *  
 * Requests for translated text will be forwarded to the highest bundle
 * and go down until a match is found. If the deepest bundle hasn't a
 * value for the key the key will be returned untranslated.
 * 
 * NOTE: GINsim will push at least one bundle, an extension can push
 * its own bundle(s) with the <tt>pushBundle</tt> method.
 */
public class Translator {

	/**
	 * The translator creates outputs on the
	 * System.err if a resource wasn't found and
	 * this boolean is <tt>true</tt>
	 */
	protected static boolean logNotFoundResources = false;

	/** Contains ResourceBundle objects.
	 *  The first bundle is the Graphpad bundle.
	 *  If a user wants to use Graphpad as a framework
	 *  he can push his own bundle.
	 */
	protected static Stack bundles = new Stack();

	/** 
	 * Contains ResourceBundle names
	 * The first bundlename is the Graphpad bundle.
	 * If a user wants to use Graphpad as a framework
	 * he can push his own bundle.
	 */
	protected static Stack bundleNames = new Stack();
	
    /**
	 * Resource Bundle with proper names.
	 */
    protected static DefaultResourceBundle defaultBundle = new DefaultResourceBundle();

	/** 
	 * get the localized String for the key. If
	 * the String wasn't found the method will return
	 * the Key but not null.
	 * 
	 * @param sKey Key for the localized String.
	 * @return the localized String for the key.
	 *
	 */
	public static String getString(String sKey) {
		return getString(bundles.size()-1, sKey);
	}

	/** 
	 * get the localized String for the key. If
	 * the String wasn't found the method will return
	 * null.
	 *
	 * @param bundleIndex The bundle index for the request.
	 *                    The method requests the bundle
	 *                    at the specified position and
	 *                    at all deeper positions.
	 *
	 * @param sKey Key for the localized String.
	 * @return the localized String for the key.
	 *
	 */
	private static String getString(int bundleIndex, String sKey) {
		if (bundleIndex < bundles.size() && bundleIndex >= 0){
			ResourceBundle bundle = (ResourceBundle)bundles.get(bundleIndex);
			try {
				return bundle.getString(sKey);
			} catch (MissingResourceException mrex) {
				return getString(bundleIndex-1, sKey);
			}
		}
		try {
			return defaultBundle.getString(sKey);
		} catch (MissingResourceException mrex) {
			if (logNotFoundResources) {
				LogManager.error( "Resource for the following key not found:" + sKey);
			}
			return sKey;
		}
	}

	/**
	 * Return the String array of key translations of the given String array
	 * 
	 * @param keys the array of string to translate
	 * @return the String array of key translations of the given String array
	 */
	public static String[] getStrings( String[] keys){
		
		String[] translated = new String[ keys.length];
		for( int i = 0 ; i < keys.length; i++){
			translated[i] = getString( keys[i]);
		}
		
		return translated;
	}
	
	/**
	 * Return the string composed by replacing the "%s" string in the given key by the given params
	 * 
	 * @param key
	 * @param params
	 * @return
	 */
	public static String getString( String key, String... params){
		
		String result = getString( key);
		
		for( String param : params){
			result = result.replaceFirst( "%s", param);
		}
		
		return result;
	}
	
	/** 
	 * Pushes the specified bundle on the stack.
	 * An example for a bundle file name is 'org.jgraph.pad.resources.Graphpad'.
	 * Don't add the suffix of the filename, the language or country code.
	 * @param filename
	 */
	public static void pushBundle(String filename){
		ResourceBundle resourcebundle = ResourceBundle.getBundle(filename);
		bundles.push(resourcebundle);
		bundleNames.push( filename);
	}
}


/**
 * Defaultresourcebundle for proper names. (e.g. Locales and
 * Look And Feel names are equal in any language. Therefore
 * this Class contains this proper names.)
 */
class DefaultResourceBundle extends ResourceBundle  {

  /** 
   * Hashtable with languageskeys as key and
   * propername as value
   */
  Hashtable defaultNames = new Hashtable();

  /** 
   * Creates a new Instance an requerys all default names.
   */
  public DefaultResourceBundle() {
    super();
    requeryDefaultNames();
  }


  /** 
   * Quires the default names. Therefore any registered
   * Propernameprovider is queried.
   */
  public void requeryDefaultNames(){
    defaultNames.clear();

    Locale[] locales = Locale.getAvailableLocales();
    for (int i = 0; i < locales.length; i++){
      defaultNames.put("Component." + locales[i].toString() + ".Text",        locales[i].getDisplayName());
      defaultNames.put("Component." + locales[i].toString() + ".ToolTipText", locales[i].getDisplayName());
      defaultNames.put("Component." + locales[i].toString() + ".Mnemonic",    locales[i].getDisplayName());
    }
  }

  /** 
   * @return the merged keys of any registered ProperNameProvider
   */
  public Enumeration getKeys() {
    return defaultNames.elements();
  }

  /** 
   * @param key
   * @return the object for the key or null
   */
  public Object handleGetObject(String key) {
    return defaultNames.get(key);
  }
}
