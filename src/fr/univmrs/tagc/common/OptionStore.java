package fr.univmrs.tagc.common;

import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;

import fr.univmrs.tagc.GINsim.global.GsEnv;
import fr.univmrs.tagc.GINsim.graph.GsEdgeAttributesReader;
import fr.univmrs.tagc.GINsim.graph.GsVertexAttributesReader;
import fr.univmrs.tagc.common.xml.XMLWriter;

/**
 * handle options: remember them during the session, restore them when first called
 * and save them when exiting.
 */
public class OptionStore extends DefaultHandler {

    // some static stuff
    private static Map m_option = new HashMap();
    private static String optionFile = null;
    private static Vector v_recent = new Vector();
    
    static {
    	switch (Tools.os) {
		case Tools.SYS_MACOSX:
	        optionFile = System.getProperty("user.home")+"/Library/Preferences/fr.univmrs.tagc.GINsim.xml";
			break;
		default:
	        optionFile = System.getProperty("user.home") + File.separator + ".ginsimrc";
			break;
		}
    	
        File f_option = new File(optionFile);
        if (f_option.exists()) {
            SAXParserFactory spf = SAXParserFactory.newInstance();
            OptionStore options = new OptionStore();
            XMLReader xr;
            try {
                SAXParser sp = spf.newSAXParser();
                xr = sp.getXMLReader();
                xr.setContentHandler(options);
                xr.setEntityResolver(options);
                xr.setErrorHandler(options);
                FileReader r = new FileReader(f_option);
                xr.parse(new InputSource(r));
            } catch (FileNotFoundException e) { 
            	Tools.error(new GsException(GsException.GRAVITY_ERROR, "Error in the configuration file: "+optionFile+"\n"+
                        e.getLocalizedMessage()), null);
            } catch (IOException e) {
            	Tools.error(new GsException(GsException.GRAVITY_ERROR, "Error in the configuration file: "+optionFile+"\n"+
                        e.getLocalizedMessage()), null);
            } catch (ParserConfigurationException e) {
            	Tools.error(new GsException(GsException.GRAVITY_ERROR, "Error in the configuration file: "+optionFile+"\n"+
                        e.getLocalizedMessage()), null);
            } catch (SAXParseException e) {
            	Tools.error(new GsException(GsException.GRAVITY_ERROR, "Error in the configuration file: "+optionFile), null);
            } catch (SAXException e) {
            	Tools.error(new GsException(GsException.GRAVITY_ERROR, "Error in the configuration file: "+optionFile), null);
            }
        }
    }
    /**
     * add a recent file to the list
     * @param path path to the file
     */
    public static void addRecent(String path) {
        if (path != null && new File(path).exists()) {
            int i = v_recent.indexOf(path);
            if (i != -1) {
                v_recent.remove(i);
                v_recent.add(path);
            } else {
                if (v_recent.size() > 9) {
                    v_recent.remove(0);
                }
                v_recent.add(path);
            }
            
            GsEnv.updateRecentMenu();
        }
    }

    /**
     * get the list of recent files.
     * In the returned list, the most recently used file is the last one
     * 
     * @return a Vector containing recent files
     */
    public static Vector getRecent() {
        return v_recent;
    }
    
    /**
     * save the value of an option. accepted types: Boolean, Integer, String
     * 
     * @param name uniq name (key) of the option
     * @param value an object giving the value of this option
     */
    public static void setOption(String name, Object value) {
        if (value instanceof Boolean ||
                value instanceof Integer ||
                value instanceof String) {
            
            m_option.put(name, value);
        }
    }
    /**
     * get the saved value of an option
     * 
     * @param name uniq name (key) of the option
     * @return an object representing the value of this option (null if not defined)
     */
    public static Object getOption(String name) {
        return m_option.get(name);
    }
    /**
     * get the saved value of an option with a fallback value.
     * If the option was not defined or if types mismatch, it will be set to the default value.
     * @param name uniq name (key) of the option
     * @param defValue value to return if this option is not defined
     * @return an object representing the value of this option
     */
    public static Object getOption(String name, Object defValue) {
        Object o = m_option.get(name);
        
        if (o != null && o.getClass().equals(defValue.getClass())) {
            return o;
        }
        m_option.put(name, defValue);
        return defValue;
    }
    /**
     * remove a saved option.
     * @param name uniq name (key) of the option
     */
    public static void removeOption(String name) {
        m_option.remove(name);
    }
    /**
     * save all options in a file, to restore them at next run.
     */
    public static void saveOptions() {
        
        // FIXME hacky: first call some components that need to save some options
        GsEdgeAttributesReader.saveOptions();
        GsVertexAttributesReader.saveOptions();
        
        if (v_recent.size() == 0 && m_option.size() == 0) {
            return;
        }
        try {
            OutputStreamWriter fos = new OutputStreamWriter(new FileOutputStream(optionFile), "UTF-8");
            XMLWriter out = new XMLWriter(fos, null); 
            out.write("<gsconfig>\n");
            for (int i=0 ; i<v_recent.size() ; i++) {
                out.openTag("recent");
                out.addAttr("filename", v_recent.get(i).toString());
                out.closeTag();
            }
            Iterator it = m_option.keySet().iterator();
            while (it.hasNext()) {
                Object k = it.next();
                Object v = m_option.get(k);
                String t;
                if (v instanceof Boolean) {
					t = "boolean";
				} else if (v instanceof Integer) {
					t = "integer";
				} else {
					t = "string";
				}
                out.write("   <option key=\""+k+"\" type=\""+t+"\" value=\""+v+"\"/>\n");
            }
            out.write("</gsconfig>\n");
            fos.close();
        } catch (Exception e) {}
    }

    /* ********************************
     *      parse options 
     **********************************/
    /*
     * exemple of config file:
     * 
     * <gsconfig>
     *  <recent filename="/path/to/file.ginml"/>
     *  <recent filename="/anotherpath/to/file.ginml"/>
     *  <option key="cle" type="string" value="valeur"/>
     *  <option key="autrecle" type="integer" value="123"/>
     * </gsconfig>
     * 
     * 
     * all that is needed is an handler for startElement
     */
    public void startElement(String uri, String localName, String qName,
            Attributes attributes) throws SAXException {
        if (qName.equals("recent")) {
            addRecent(attributes.getValue("filename"));
        } else if (qName.equals("option")) {
            String k = attributes.getValue("key");
            String sv = attributes.getValue("value");
            String t = attributes.getValue("type");
            Object v;
            if ("boolean".equals(t)) {
				v = new Boolean(sv);
			} else if ("integer".equals(t)) {
				v = new Integer(sv);
			} else {
				v = sv;
			}

            setOption(k, v);
        }
    }
    
}
