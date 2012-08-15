package org.ginsim.common.application;

import java.util.ArrayList;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;


/**
 * Translate text based on a list of resource bundles. 
 * 
 * NOTE: GINsim will push at least one bundle, an extension can push
 * its own bundle(s) with the <tt>pushBundle</tt> method.
 */
public class Translator {

	private static List<ResourceBundle> bundles = new ArrayList<ResourceBundle>();
	
	public static void pushBundle(String name) {
		ResourceBundle bundle = ResourceBundle.getBundle(name);
		if (bundle == null) {
			LogManager.error("Bundle not found: "+name);
			return;
		}
		bundles.add(bundle);
	}

	public static String getString(String key) {
		for (ResourceBundle bundle: bundles) {
            try {
                return bundle.getString(key);
            } catch (MissingResourceException e) { }
		}
		// TODO: log missing keys?
		return key;
	}

	public static String getString(String key, String... params) {
        String result = getString( key);

        for( String param : params){
                result = result.replaceFirst( "%s", param);
        }

        return result;
	}

	public static String[] getStrings(String[] keys) {
        String[] ret = new String[ keys.length];
        for (int i=0 ; i<keys.length; i++) {
                ret[i] = getString( keys[i]);
        }

        return ret;
	}

}
