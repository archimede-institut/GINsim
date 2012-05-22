package org.ginsim.core.annotation;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.ginsim.common.utils.OpenUtils;
import org.ginsim.common.utils.OpenHelper;


public class HttpHelper implements OpenHelper {

  static Map<String,String> m_proto = new HashMap<String,String>();

  public boolean open(String proto, String value) {
    return OpenUtils.openURI(getLink(proto, value));
  }
  public void add(String proto, String value) {
  }

  public static void setup() {
    m_proto.put("http", "http://");
    m_proto.put("wp", "http://en.wikipedia.org/wiki/");

    HttpHelper h = new HttpHelper();
    Iterator<String> it = m_proto.keySet().iterator();
    while (it.hasNext()) {
    	OpenUtils.addHelperClass( it.next(), h);
    }
  }
  public String getLink(String proto, String value) {
    return m_proto.get(proto)+value;
  }
}
