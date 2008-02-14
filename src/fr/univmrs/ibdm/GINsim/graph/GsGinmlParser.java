package fr.univmrs.ibdm.GINsim.graph;

import java.io.InputStream;
import java.util.Map;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import fr.univmrs.ibdm.GINsim.connectivity.GsReducedGraphParser;
import fr.univmrs.ibdm.GINsim.dynamicGraph.GsDynamicParser;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.GsRegulatoryParser;
import fr.univmrs.ibdm.GINsim.xml.GsGinmlHelper;
import fr.univmrs.ibdm.GINsim.xml.GsXMLHelper;
import fr.univmrs.tagc.xml.XMLHelper;

/**
 * parses a ginml regulatory graph.
 */
public final class GsGinmlParser extends XMLHelper {
    
    private Map map;
    private GsXMLHelper realParser = null;
    
    /**
     * @param is from where to read
     * @param map
     * @return the new graph
     */
    public GsGraph  parse(InputStream is, Map map) {
    	this.map = map;
		startParsing(is);
		if (realParser == null) {
		    return null;
		}
		return realParser.getGraph();
    }

    public void startElement(String uri, String localName, String qName,
            Attributes attributes) throws SAXException {
        super.startElement(uri, localName, qName, attributes);

        if (qName.equals("graph")) {
            String s_class = attributes.getValue("class");
            if ("regulatory".equals(s_class)) {
                realParser = new GsRegulatoryParser(map, attributes, s_dtd, s_filename);
            } else if ("dynamical".equals(s_class)) {
                realParser = new GsDynamicParser(map, attributes, s_dtd, s_filename);
            } else if ("reduced".equals(s_class)) {
                realParser = new GsReducedGraphParser(map, attributes, s_dtd, s_filename);
            } else {
                throw new SAXException("bad type of graph");
            }
            xr.setContentHandler(realParser);
        }
    }

    public GsGraph getGraph() {
		if (realParser == null) {
		    return null;
		}
		return realParser.getGraph();
    }

    public String getFallBackDTD() {
        return GsGinmlHelper.LOCAL_URL_DTD_FILE;
    }
}
