/*
 * This file is part of the Librarian project
 * (C) 2002 The Librarian Community
 *
 * Please visit our website at http://librarian.sf.de
 */
package fr.univmrs.tagc.common.managerresources;

import java.util.Enumeration;

/**
 * Container for proper names. (e.g. Look and Feel names are
 * equal in any language, sothat this container.)
 */

public interface ProperNameProvider {

  /** 
   * @return the Keys of this proper name container
   */
  public abstract  Enumeration getKeys()  ;

  /** 
   * @param key
   * @return the proper name for the locale key.
   */
  public String getString(String key) ;

}
