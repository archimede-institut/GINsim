package org.ginsim.common;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.ginsim.common.utils.EnvUtils;
import org.ginsim.common.utils.GUIMessageUtils;
import org.ginsim.common.xml.XMLWriter;
import org.ginsim.exception.GsException;
import org.ginsim.graph.view.EdgeAttributesReader;
import org.ginsim.graph.view.NodeAttributesReader;
import org.ginsim.gui.shell.callbacks.FileCallBack;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;


/**
 * handle options: remember them during the session, restore them when first called
 * and save them when exiting.
 */
public class OptionStore extends DefaultHandler {

    // some static stuff
    private static Map m_option = new HashMap();
    private static String optionFile = null;
    
    static {
    	switch (EnvUtils.os) {
		case EnvUtils.SYS_MACOSX:
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
            	GUIMessageUtils.openErrorDialog(new GsException(GsException.GRAVITY_ERROR, "Error in the configuration file: "+optionFile+"\n"+
                        e.getLocalizedMessage()), null);
            } catch (IOException e) {
            	GUIMessageUtils.openErrorDialog(new GsException(GsException.GRAVITY_ERROR, "Error in the configuration file: "+optionFile+"\n"+
                        e.getLocalizedMessage()), null);
            } catch (ParserConfigurationException e) {
            	GUIMessageUtils.openErrorDialog(new GsException(GsException.GRAVITY_ERROR, "Error in the configuration file: "+optionFile+"\n"+
                        e.getLocalizedMessage()), null);
            } catch (SAXParseException e) {
            	GUIMessageUtils.openErrorDialog(new GsException(GsException.GRAVITY_ERROR, "Error in the configuration file: "+optionFile), null);
            } catch (SAXException e) {
            	GUIMessageUtils.openErrorDialog(new GsException(GsException.GRAVITY_ERROR, "Error in the configuration file: "+optionFile), null);
            }
        }
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
        EdgeAttributesReader.saveOptions();
        NodeAttributesReader.saveOptions();
        
        List<String> recents = FileCallBack.getRecentFiles();
        if (recents.size() == 0 && m_option.size() == 0) {
            return;
        }
        try {
            OutputStreamWriter fos = new OutputStreamWriter(new FileOutputStream(optionFile), "UTF-8");
            XMLWriter out = new XMLWriter(fos, null); 
            out.write("<gsconfig>\n");
            for (String recent: recents) {
                out.openTag("recent");
                out.addAttr("filename", recent);
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
            FileCallBack.addRecentFile(attributes.getValue("filename"));
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
