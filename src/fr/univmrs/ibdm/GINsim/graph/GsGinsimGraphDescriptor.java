package fr.univmrs.ibdm.GINsim.graph;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Map;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.swing.ImageIcon;
import javax.swing.filechooser.FileFilter;

import fr.univmrs.ibdm.GINsim.connectivity.GsReducedGraph;
import fr.univmrs.ibdm.GINsim.dynamicGraph.GsDynamicGraph;
import fr.univmrs.ibdm.GINsim.global.GsEnv;
import fr.univmrs.ibdm.GINsim.global.GsException;
import fr.univmrs.ibdm.GINsim.gui.GsFileFilter;
import fr.univmrs.ibdm.GINsim.gui.GsMainFrame;
import fr.univmrs.ibdm.GINsim.gui.GsOpenAction;
import fr.univmrs.ibdm.GINsim.manageressources.Translator;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.GsRegulatoryGraph;

/**
 * descriptor for regulatoryGraph.
 */
public class GsGinsimGraphDescriptor implements GsGraphDescriptor {

    private static Vector v_layout = null;
    private static Vector v_export = null;
    private static Vector v_action = null;
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

    public GsGraph getNew(GsMainFrame m) {
        GsGraph graph = new GsRegulatoryGraph();
        return graph;
    }

	public GsGraph open(File file) {
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
			v_layout = new Vector();
		}
		v_layout.add(layout);
	}
	/**
	 * @return a list of avaible layouts.
	 */
	public Vector getLayout() {
		return v_layout;
	}

	/**
	 * @param export
	 */
	public static void registerExportProvider(GsActionProvider export) {
		if (v_export == null) {
			v_export = new Vector();
		}
		v_export.add(export);
	}
	/**
	 * @return a list of avaible export filters.
	 */
	public Vector getExport() {
		return v_export;
	}

	/**
	 * 
	 * @param action
	 */
	public static void registerActionProvider(GsActionProvider action) {
		if (v_action == null) {
			v_action = new Vector();
		}
		v_action.add(action);
	}
	/**
	 * @return a list of avaible actions.
	 */
	public Vector getAction() {
		return v_action;
	}

	public ImageIcon getGraphIcon(int mode) {
	    switch (mode) {
	    	case GsOpenAction.MODE_OPEN:
	    	    return GsEnv.getIcon("open.gif");
	    	case GsOpenAction.MODE_NEW:
	    	    return GsEnv.getIcon("new.gif");
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
    public GsGraph open(Map map, File file) {
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
                        		// TODO: nicer error here
                        		System.out.println("unable to find a known main zip entry");
                        	}
                    	}
                	}
                }
                GsGraph graph = parser.parse(f.getInputStream(ze), map);
                if (map == null) {
                	// try to restore associated data ONLY if no subgraph is selected
                	// TODO: need to load associated entry with subgraphs
	                Vector v_omanager = graph.getObjectManager();
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
            GsGraph graph = parser.parse(new FileInputStream(file), map);
            graph.setSaveFileName(file.getAbsolutePath());
            return graph;
        } catch (FileNotFoundException e) {
            GsEnv.error(new GsException(GsException.GRAVITY_ERROR, e), null);
            return null;
        }
    }
}
