/*
 * @(#)DefaultResourceBundle.java 1.0 04.08.2003
 *
 * Copyright (C) 2003 sven_luzar
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
package fr.univmrs.tagc.common.managerresources;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Locale;
import java.util.ResourceBundle;


/**
 * Defaultresourcebundle for proper names. (e.g. Locales and
 * Look And Feel names are equal in any language. Therefore
 * this Class contains this proper names.)
 *
 * @author <a href="mailto:Sven.Luzar@web.de">Sven luzar</a>
 * @version 1.0
 */

public class DefaultResourceBundle extends ResourceBundle  {

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
