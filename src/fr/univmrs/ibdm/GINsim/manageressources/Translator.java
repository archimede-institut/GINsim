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
package fr.univmrs.ibdm.GINsim.manageressources;

import java.text.MessageFormat;
import java.util.Enumeration;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Stack;
import java.util.Vector;

/** 
 * Contains ResourceBundle objects.
 * The first (deepest) bundle is the Graphpad bundle.
 * If a user wants to use Graphpad as a framework
 * he can push his own bundle with the <tt>pushBundle</tt>
 * method. Requests will procedure with the following
 * logic: The translator asks the highest bundle
 * for a localized text string. If the bundle has
 * no entry for the specified key. The next bundle will
 * ask for the key. If the deepest bundle hasn't a
 * value for the key the key will return.
 * 
 * NOTE: you HAVE to push a default message file
 */

public class Translator {

	/** 
	 * Container for the registered LocaleChangeListener.
	 * @see LocaleChangeListener
	 */
	protected static Vector listeners = new Vector();

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
	 * Resouce Bundle with proper names.
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
	public static String getString(int bundleIndex, String sKey) {
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
			if (logNotFoundResources)
					System.err.println("Resource for the following key not found:" + sKey);
			return sKey;
		}
	}

	/** 
	 * get the localized String for the key. If
	 * the String wasn't found the method will return
	 * the key but not null.
	 *
	 * @param sKey Key for the localized String.
	 * @param values Object array for placeholders.
	 * @return the localized String for the key.
	 *
	 * @see MessageFormat#format(String, Object[])
	 */
	public static String getString(String sKey, Object[] values) {
		return getString(bundles.size()-1, sKey, values);
	}

	/** 
	 * Returns the localized String for the key. If
	 * the String wasn't found the method will return
	 * an empty String but not null and will log
	 * on the System.err the key String,
	 * if logNotFoundResources is true.
	 *
	 * @param bundleIndex
	 * @param sKey Key for the localized String.
	 * @param oValues Object array for placeholders.
	 * @return the localized String for the key.
	 *
	 * @see MessageFormat#format(String, Object[])
	 */
	public static String getString(
		int bundleIndex,
		String sKey,
		Object[] oValues
		) {

		if (bundleIndex < bundles.size() && bundleIndex >= 0){
			ResourceBundle bundle = (ResourceBundle)bundles.get(bundleIndex);
			try {
				return MessageFormat.format(bundle.getString(sKey), oValues);
			} catch (MissingResourceException mrex) {
				return getString(bundleIndex-1, sKey, oValues);
			}
		}
		try {
			return defaultBundle.getString(sKey);
		} catch (MissingResourceException mrex) {
			if (logNotFoundResources)
					System.err.println("Resource for the following key not found:" + sKey);
			return null;
		}
	}

	/**
	 * Adds a locale change listener to this Translator. If
	 * the local changes, this
	 * translator will fire locale change events.
	 * @param listener the new listener to register.
	 *
	 */
	public static void addLocaleChangeListener(LocaleChangeListener listener) {
		listeners.add(listener);
	}

	/** 
	 * Removes the registered Listener
	 * @param listener
	 */
	public static void removeLocaleChangeListener(LocaleChangeListener listener) {
		listeners.remove(listener);
	}

	/** 
	 * Returns the current locale
	 * @return the currently used locale
	 */
	public static Locale getLocale() {
		return Locale.getDefault();
	}

	/** 
	 * Sets the new locale and fires events
	 * to the locale change listener.
	 * @param locale
	 */
	public static void setLocale(Locale locale) {
		// change the locale
		Locale oldLocale = Locale.getDefault();
		Locale.setDefault(locale);

		// reload the bundles
		reloadBundles();

		// update listeners
		Vector cpyListeners;
		synchronized (listeners) {
			cpyListeners = (Vector) listeners.clone();
		}

		Enumeration oEnum = cpyListeners.elements();
		while (oEnum.hasMoreElements()) {
			LocaleChangeListener listener =
				(LocaleChangeListener) oEnum.nextElement();
			listener.localeChanged(new LocaleChangeEvent(oldLocale, locale));
		}
	}

	/** 
	 * Reloads the bundles at the stack by using the default locale.
	 */
	public static void reloadBundles(){
		// update the proper bundles		
		defaultBundle.requeryDefaultNames();
		
		// update the bundles at the stack		
		for (int i = 0; i < bundleNames.size() ; i++){
			ResourceBundle resourcebundle =
				ResourceBundle.getBundle((String)bundleNames.get(i));
			bundles.set( i, resourcebundle);
		}
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

	/** 
	 * Pops the highest bundle on the stack
	 */
	public static void popBundle(){
		bundles.pop() ;
		bundleNames.pop() ;
	}

	/** 
	 * removes the specified bundle
	 * @param index
	 */
	public static void removeBundle(int index){
		bundles.remove(index) ;
		bundleNames.remove(index) ;
	}
	/**
	 * Returns the logNotFoundResources.
	 * @return boolean
	 */
	public static boolean isLogNotFoundResources() {
		return logNotFoundResources;
	}

	/**
	 * Sets the logNotFoundResources.
	 * @param logNotFoundResources The logNotFoundResources to set
	 */
	public static void setLogNotFoundResources(boolean logNotFoundResources) {
		Translator.logNotFoundResources = logNotFoundResources;
	}
}