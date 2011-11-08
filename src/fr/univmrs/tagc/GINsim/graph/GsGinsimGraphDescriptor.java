package fr.univmrs.tagc.GINsim.graph;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.swing.ImageIcon;
import javax.swing.filechooser.FileFilter;

import org.ginsim.graph.Graph;

import fr.univmrs.tagc.GINsim.connectivity.GsReducedGraph;
import fr.univmrs.tagc.GINsim.dynamicGraph.GsDynamicGraph;
import fr.univmrs.tagc.GINsim.global.GsEnv;
import fr.univmrs.tagc.GINsim.gui.GsFileFilter;
import fr.univmrs.tagc.GINsim.gui.GsMainFrame;
import fr.univmrs.tagc.GINsim.gui.GsOpenAction;
import fr.univmrs.tagc.GINsim.hierachicalTransitionGraph.GsHierarchicalTransitionGraph;
import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryGraph;
import fr.univmrs.tagc.common.GsException;
import fr.univmrs.tagc.common.managerresources.Translator;
import fr.univmrs.tagc.common.widgets.BaseAction;

/**
 * descriptor for regulatoryGraph.
 */
public class GsGinsimGraphDescriptor implements GsGraphDescriptor {

    private static List v_layout = null;
    private static List v_export = null;
    private static List v_action = null;
    private GsFileFilter ffilter;
    private static GsGinsimGraphDescriptor instance = null;

    public String getGraphType() {
        return "regulatory";
    }

    public String getGraphName() {
        return "STR_regulatory";
    }

    public String getGraphDescription() {
        return Translator.getString("STR_regulatoryGraph");
    }

    public boolean canCreate() {
        return true;
    }

    public Graph getNew(GsMainFrame m) {
        Graph graph = new GsRegulatoryGraph();
        return graph;
    }

	public Graph open(File file) {
	    return open(null, file);
	}

	public FileFilter getFileFilter() {
		if (ffilter == null) {
			ffilter = new GsFileFilter();
			ffilter.setExtensionList(new String[] {"ginml", "zginml"}, "(z)ginml files");
		}
		return ffilter;
	}

	/**
	 * @param layout
	 */
	public static void registerLayoutProvider(GsActionProvider layout) {
		if (v_layout == null) {
			v_layout = new ArrayList();
		}
		v_layout.add(layout);
	}
	/**
	 * @return a list of avaible layouts.
	 */
	public List getLayout() {
		return v_layout;
	}

	/**
	 * @param export
	 */
	public static void registerExportProvider(GsActionProvider export) {
		if (v_export == null) {
			v_export = new ArrayList();
		}
		v_export.add(export);
	}
	/**
	 * @return a list of avaible export filters.
	 */
	public List getExport() {
		return v_export;
	}

	/**
	 * 
	 * @param action
	 */
	public static void registerActionProvider(GsActionProvider action) {
		if (v_action == null) {
			v_action = new ArrayList();
		}
		v_action.add(action);
	}
	/**
	 * @return a list of avaible actions.
	 */
	public List getAction() {
		return v_action;
	}

	public ImageIcon getGraphIcon(int mode) {
	    switch (mode) {
	    	case GsOpenAction.MODE_OPEN:
	    	    return BaseAction.getIcon("document-open.png");
	    	case GsOpenAction.MODE_NEW:
	    	    return BaseAction.getIcon("document-new.png");
	    }
		return null;
	}
	
    /**
     * @return an instance of this graphDescriptor.
     */
    public static GsGraphDescriptor getInstance() {
        if (instance == null) {
            instance = new GsGinsimGraphDescriptor();
        }
        return instance;
    }
    public Graph open(Map map, File file) {
        try {
            ZipFile f = new ZipFile(file);
            try {
                GsGinmlParser parser = new GsGinmlParser();
                boolean usePrefix = false;
                ZipEntry ze = f.getEntry("ginml");
                if (ze==null) {
                	usePrefix = true;
                	ze = f.getEntry(GsGraph.zip_prefix+GsRegulatoryGraph.zip_mainEntry);
                	if (ze == null) {
                		ze = f.getEntry(GsGraph.zip_prefix+GsDynamicGraph.zip_mainEntry);
                    	if (ze == null) {
                    		ze = f.getEntry(GsGraph.zip_prefix+GsReducedGraph.zip_mainEntry);
                        	if (ze == null) {
                        		ze = f.getEntry(GsGraph.zip_prefix+GsHierarchicalTransitionGraph.zip_mainEntry);
	                        	if (ze == null) {
	                        		// TODO: nicer error here
	                        		System.out.println("unable to find a known main zip entry");
	                        	}
                        	}
                    	}
                	}
                }
                
                Graph graph = parser.parse(f.getInputStream(ze), map);
                if (map == null) {
                	// try to restore associated data ONLY if no subgraph is selected
                	// TODO: need to load associated entry with subgraphs
                	List v_omanager = graph.getObjectManagerList();
	                if (v_omanager != null) {
	                    for (int i=0 ; i<v_omanager.size() ; i++) {
	                        GsGraphAssociatedObjectManager manager = (GsGraphAssociatedObjectManager)v_omanager.get(i);
	                        ze = f.getEntry((usePrefix ? GsGraph.zip_prefix:"")+manager.getObjectName());
	                        if (ze != null) {
	                            Object o = manager.doOpen(f.getInputStream(ze), graph);
	                            graph.addObject(manager.getObjectName(), o);
	                        }
	                    }
	                }
	                v_omanager = graph.getSpecificObjectManager();
	                if (v_omanager != null) {
	                    for (int i=0 ; i<v_omanager.size() ; i++) {
	                        GsGraphAssociatedObjectManager manager = (GsGraphAssociatedObjectManager)v_omanager.get(i);
	                        ze = f.getEntry((usePrefix ? GsGraph.zip_prefix:"")+manager.getObjectName());
	                        if (ze != null) {
	                            Object o = manager.doOpen(f.getInputStream(ze), graph);
	                            graph.addObject(manager.getObjectName(), o);
	                        }
	                    }
	                }
                }
                graph.setSaveFileName(file.getAbsolutePath());
                return graph; 
            } catch (Exception e) {
                System.out.println("error opening");
                e.printStackTrace();
                return null;
            }
        } catch (Exception e) {// opening as zip failed, try the old method instead
        }

        // not a zip file
        GsGinmlParser parser = new GsGinmlParser();
        try {
            Graph graph = parser.parse(new FileInputStream(file), map);
            graph.setSaveFileName(file.getAbsolutePath());
            return graph;
        } catch (FileNotFoundException e) {
            GsEnv.error(new GsException(GsException.GRAVITY_ERROR, e), null);
            return null;
        }
    }
}
