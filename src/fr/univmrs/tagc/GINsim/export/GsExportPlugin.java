package fr.univmrs.tagc.GINsim.export;

import javax.swing.JFrame;

import org.ginsim.exception.GsException;
import org.ginsim.graph.Graph;
import org.ginsim.service.export.GsBioLayoutEncoder;
import org.ginsim.service.export.GsGraphvizEncoder;
import org.ginsim.service.export.GsSVGExport;
import org.ginsim.service.export.ImageExport;

import fr.univmrs.tagc.GINsim.export.regulatoryGraph.GsSBMLExport;
import fr.univmrs.tagc.GINsim.gui.GsFileFilter;
import fr.univmrs.tagc.GINsim.gui.GsOpenAction;
import fr.univmrs.tagc.GINsim.gui.GsPluggableActionDescriptor;
import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryGraph;

/**
 * register export plugins.
 * currently supported formats:
 *      - graphviz
 *      - biolayout
 *      - SVG
 * 
 * TODO: port it to the new service API
 */
public class GsExportPlugin implements GsPlugin, GsActionProvider {

    private static final int GRAPHVIZ = 0;
    private static final int BIOLAYOUT = 1;
    private static final int SVG = 2;
    private static final int IMAGE = 3;
    private static final int SBML = 6;
    
	public void registerPlugin() {
		GsGraphManager.registerExportProvider(this);
	}

	public GsPluggableActionDescriptor[] getT_action(int actionType, Graph graph) {
		
        if (actionType == ACTION_EXPORT) {
            if (graph instanceof GsRegulatoryGraph) {
              return new GsPluggableActionDescriptor[] {
                      new GsPluggableActionDescriptor("STR_graphviz", "STR_graphviz_descr", null, this, ACTION_EXPORT, GRAPHVIZ),
                      new GsPluggableActionDescriptor("STR_biolayout", "STR_biolayout_descr", null, this, ACTION_EXPORT, BIOLAYOUT),
                      new GsPluggableActionDescriptor("STR_SVG", "STR_SVG_descr", null, this, ACTION_EXPORT, SVG),
                      new GsPluggableActionDescriptor("STR_Image", "STR_Image_descr", null, this, ACTION_EXPORT, IMAGE),
                     // new GsPluggableActionDescriptor("STR_SBML", "STR_SBML_descr", null, this, ACTION_EXPORT, SBML),
              };
            }
            return new GsPluggableActionDescriptor[] {
                    new GsPluggableActionDescriptor("STR_graphviz", "STR_graphviz_descr", null, this, ACTION_EXPORT, GRAPHVIZ),
                    new GsPluggableActionDescriptor("STR_biolayout", "STR_biolayout_descr", null, this, ACTION_EXPORT, BIOLAYOUT),
                    new GsPluggableActionDescriptor("STR_SVG", "STR_SVG_descr", null, this, ACTION_EXPORT, SVG),
                    new GsPluggableActionDescriptor("STR_Image", "STR_Image_descr", null, this, ACTION_EXPORT, IMAGE)
            };
        }
		return null;
	}

	public void runAction (int actionType, int ref, Graph graph, JFrame frame) throws GsException {
	    if (actionType != ACTION_EXPORT) {
	        return;
        }
		GsFileFilter ffilter = new GsFileFilter();
		String extension = null;
	    String filename;
	    switch (ref) {
            case GRAPHVIZ:
                ffilter.setExtensionList(new String[] {"graphviz"}, "dot (graphviz) files");
                extension = ".dot";
                break;
            case BIOLAYOUT:
                ffilter.setExtensionList(new String[] {"layout"}, "biolayout files");
                extension = ".layout";
                break;
            case SVG:
                ffilter.setExtensionList(new String[] {"svg"}, "SVG files");
                extension = ".svg";
                break;
            case IMAGE:
                ffilter.setExtensionList(new String[] {"png"}, "PNG files");
                extension = ".png";
                break;
            case SBML:
                ffilter.setExtensionList(new String[] {"sbml"}, "SBML files");
                extension = ".sbml";
                break;
			default: 
				return;
	    }
	    
		filename = GsOpenAction.selectSaveFile(null, ffilter, null, extension);
		if (filename == null) {
			return;
		}
		
		switch (ref) {
            case GRAPHVIZ:
                GsGraphvizEncoder.encode(graph, null, null, filename);
                break;
            case BIOLAYOUT:
                GsBioLayoutEncoder.encode(graph, null, filename);
                break;
            case SVG:
                GsSVGExport.exportSVG(graph, null, null, filename);
                break;
            case IMAGE:
                ImageExport.exportImage(graph, false, filename);
                break;
            case SBML:
                GsSBMLExport.export((GsRegulatoryGraph)graph, filename);
                break;
		}
	}
}
