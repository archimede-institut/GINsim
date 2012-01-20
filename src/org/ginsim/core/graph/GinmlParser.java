package org.ginsim.core.graph;

import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import org.ginsim.common.exception.GsException;
import org.ginsim.common.xml.ParsingWarningReport;
import org.ginsim.common.xml.XMLHelper;
import org.ginsim.core.graph.common.Graph;
import org.ginsim.core.io.parser.GsXMLHelper;
import org.ginsim.core.notification.NotificationManager;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;


/**
 * parses a ginml regulatory graph.
 */
public final class GinmlParser extends XMLHelper {
    
    private Map map;
    private GsXMLHelper realParser = null;
    
    static {
        //String DTD = "/fr/univmrs/tagc/GINsim/resources/GINML_2_1.dtd";
        String DTD = "/fr/univmrs/tagc/GINsim/resources/GINML_2_3.dtd";
        XMLHelper.addEntity("http://gin.univ-mrs.fr/GINsim/GINML_2_0.dtd", DTD);
        XMLHelper.addEntity("http://gin.univ-mrs.fr/GINsim/GINML_2_1.dtd", DTD);
        XMLHelper.addEntity("file://fr/univmrs/ibdm/GINsim/resources/GINML_2_1.dtd", DTD);
        XMLHelper.addEntity("file://fr/univmrs/ibdm/GINsim/resources/GINML_2_0.dtd", DTD);
        XMLHelper.addEntity("file://fr/univmrs/tagc/GINsim/resources/GINML_2_1.dtd", DTD);
        XMLHelper.addEntity("file://fr/univmrs/tagc/GINsim/resources/GINML_2_0.dtd", DTD);
    }
    
    /**
     * @param is from where to read
     * @param map
     * @return the new graph
     */
    public Graph  parse(InputStream is, Map map) {
    	this.map = map;
		startParsing(is);
		if (realParser == null) {
		    return null;
		}
		Graph g = realParser.getGraph();
		
		// Notification for parsing errors.
		// Note: warnings are collected by the generic parser, not the real one
		ParsingWarningReport warnings = getWarnings();
		if (warnings != null) {
			NotificationManager.publishDetailedWarning(g, warnings.getMessage(), warnings.getDetail());
		}
		return g;
    }

    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        super.startElement(uri, localName, qName, attributes);

        if (qName.equals("graph")) {
        	try{
	            String s_class = attributes.getValue("class");
	            Class parser_class = GraphManager.getInstance().getParserClass( s_class);
	            if( parser_class != null){
		            Class<?> parameter_types[] = new Class[4];
		            parameter_types[0] = Map.class;
		            parameter_types[1] = Attributes.class;
		            parameter_types[2] = String.class;
		            parameter_types[3] = String.class;
		            
		            Object[] arg_list = new Object[4];
		            arg_list[0] = map;
		            arg_list[1] = attributes;
		            arg_list[2] = s_dtd;
		            arg_list[3] = s_filename;

		            realParser = (GsXMLHelper) parser_class.getConstructor( parameter_types).newInstance( arg_list);
	            	
	            }
	            else {
	                throw new SAXException( new GsException( GsException.GRAVITY_ERROR, "STR_noSuchGraphType"));
	            }
        	}
        	catch( NoSuchMethodException nsme){
        		throw new SAXException( new GsException( "STR_NoAvailableParser", nsme));
        	}
        	catch( InvocationTargetException ite){
        		throw new SAXException( new GsException( "STR_NoAvailableParser", ite));
        	}
        	catch( IllegalAccessException iae){
        		throw new SAXException( new GsException( "STR_NoAvailableParser", iae));
        	}
        	catch( InstantiationException ie){
        		throw new SAXException( new GsException( "STR_NoAvailableParser", ie));
        	}

            xr.setContentHandler(realParser);
        }
    }
    
    public Graph getGraph() {
    	
		if (realParser == null) {
		    return null;
		}
		return realParser.getGraph();
    }
}
