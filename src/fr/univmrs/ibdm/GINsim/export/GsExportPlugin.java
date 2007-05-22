package fr.univmrs.ibdm.GINsim.export;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import fr.univmrs.ibdm.GINsim.export.generic.GsBioLayoutEncoder;
import fr.univmrs.ibdm.GINsim.export.generic.GsGraphvizEncoder;
import fr.univmrs.ibdm.GINsim.export.generic.GsSVGExport;
import fr.univmrs.ibdm.GINsim.export.regulatoryGraph.GsGNAExport;
import fr.univmrs.ibdm.GINsim.export.regulatoryGraph.GsSBMLExport;
import fr.univmrs.ibdm.GINsim.export.regulatoryGraph.GsSMVExport;
import fr.univmrs.ibdm.GINsim.export.regulatoryGraph.GsSMVExportConfigPanel;
import fr.univmrs.ibdm.GINsim.export.regulatoryGraph.GsSMVexportConfig;
import fr.univmrs.ibdm.GINsim.global.GsException;
import fr.univmrs.ibdm.GINsim.graph.GsActionProvider;
import fr.univmrs.ibdm.GINsim.graph.GsGraph;
import fr.univmrs.ibdm.GINsim.graph.GsGraphManager;
import fr.univmrs.ibdm.GINsim.gui.GsFileFilter;
import fr.univmrs.ibdm.GINsim.gui.GsOpenAction;
import fr.univmrs.ibdm.GINsim.gui.GsPluggableActionDescriptor;
import fr.univmrs.ibdm.GINsim.plugin.GsPlugin;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.GsRegulatoryGraph;

/**
 * register export plugins.
 * currently supported formats:
 *      - graphviz
 *      - biolayout
 *      - SVG
 */
public class GsExportPlugin implements GsPlugin, GsActionProvider {

    private static final int GRAPHVIZ = 0;
    private static final int BIOLAYOUT = 1;
    private static final int SVG = 2;
    private static final int SMV = 5;
    private static final int SBML = 6;
    private static final int GNA = 7;
    
	public void registerPlugin() {
		GsGraphManager.registerExportProvider(this);
	}

	public GsPluggableActionDescriptor[] getT_action(int actionType, GsGraph graph) {
        if (actionType == ACTION_EXPORT) {
            if (graph instanceof GsRegulatoryGraph) {
              return new GsPluggableActionDescriptor[] {
                      new GsPluggableActionDescriptor("STR_SMV", "STR_SMV_descr", null, this, ACTION_EXPORT, SMV),
                      new GsPluggableActionDescriptor("STR_graphviz", "STR_graphviz_descr", null, this, ACTION_EXPORT, GRAPHVIZ),
                      new GsPluggableActionDescriptor("STR_biolayout", "STR_biolayout_descr", null, this, ACTION_EXPORT, BIOLAYOUT),
                      new GsPluggableActionDescriptor("STR_SVG", "STR_SVG_descr", null, this, ACTION_EXPORT, SVG)
              };
            }
            return new GsPluggableActionDescriptor[] {
                    new GsPluggableActionDescriptor("STR_graphviz", "STR_graphviz_descr", null, this, ACTION_EXPORT, GRAPHVIZ),
                    new GsPluggableActionDescriptor("STR_biolayout", "STR_biolayout_descr", null, this, ACTION_EXPORT, BIOLAYOUT),
                    new GsPluggableActionDescriptor("STR_SVG", "STR_SVG_descr", null, this, ACTION_EXPORT, SVG)
            };
        }
		return null;
	}

	public void runAction (int actionType, int ref, GsGraph graph, JFrame frame) throws GsException {
	    if (actionType != ACTION_EXPORT) {
	        return;
        }
		GsFileFilter ffilter = new GsFileFilter();
		String extension = null;
	    String filename;
        GsSMVexportConfig config = null;
	    switch (ref) {
            case GRAPHVIZ:
                ffilter.setExtensionList(new String[] {"graphviz"}, "dot (graphviz) files");
                extension = ".dot";
                break;
            case BIOLAYOUT:
                ffilter.setExtensionList(new String[] {"biolayout"}, "biolayout files");
                extension = ".biolayout";
                break;
            case SVG:
                ffilter.setExtensionList(new String[] {"svg"}, "SVG files");
                extension = ".svg";
                break;
            case SBML:
                ffilter.setExtensionList(new String[] {"sbml"}, "SBML files");
                extension = ".sbml";
                break;
            case GNA:
                ffilter.setExtensionList(new String[] {"gna"}, "GNA files");
                extension = ".GNA";
                break;
            case SMV:
                config = new GsSMVexportConfig((GsRegulatoryGraph)graph);
                GsSMVExportConfigPanel panel = new GsSMVExportConfigPanel(true, false);
                panel.setCfg(config);
                int ret = JOptionPane.showConfirmDialog(null, panel, "Configure SMV export", JOptionPane.OK_CANCEL_OPTION);
                if (ret == JOptionPane.CANCEL_OPTION) {
                    return;
                }
                ffilter.setExtensionList(new String[] {"smv"}, "SMV files");
                extension = ".smv";

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
                GsGraphvizEncoder.encode(graph, false, filename);
                break;
            case BIOLAYOUT:
                GsBioLayoutEncoder.encode(graph, false, filename);
                break;
            case SVG:
                GsSVGExport.exportSVG(graph, false, filename);
                break;
            case SBML:
                GsSBMLExport.export(graph, filename);
                break;
            case SMV:
                GsSMVExport.encode((GsRegulatoryGraph) graph, filename, config);
                break;
            case GNA:
                GsGNAExport.encode((GsRegulatoryGraph) graph, filename);
                break;
		}
	}
}
