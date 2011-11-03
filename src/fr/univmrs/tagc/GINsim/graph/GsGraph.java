package fr.univmrs.tagc.GINsim.graph;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.filechooser.FileFilter;

import fr.univmrs.tagc.GINsim.annotation.Annotation;
import fr.univmrs.tagc.GINsim.data.GsDirectedEdge;
import fr.univmrs.tagc.GINsim.global.GsEnv;
import fr.univmrs.tagc.GINsim.gui.GsFileFilter;
import fr.univmrs.tagc.GINsim.gui.GsMainFrame;
import fr.univmrs.tagc.GINsim.gui.GsOpenAction;
import fr.univmrs.tagc.GINsim.gui.GsParameterPanel;
import fr.univmrs.tagc.GINsim.jgraph.GsJgraphtGraphManager;
import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryGraphDescriptor;
import fr.univmrs.tagc.GINsim.xml.GsGinmlHelper;
import fr.univmrs.tagc.common.GsException;
import fr.univmrs.tagc.common.OptionStore;
import fr.univmrs.tagc.common.datastore.ObjectEditor;
import fr.univmrs.tagc.common.managerresources.Translator;

/**
 * Base class for specialized graphs, with some common functions.
 * each kind of graph (ie regulatory, dynamic...) must extend it
 */
public abstract class GsGraph<V,E extends GsDirectedEdge<V>> implements GsGraphListener<V,E>, GraphChangeListener {
    /** graphManager used */
    protected GsGraphManager<V,E> graphManager;
    
    // TODO Move to GUI side
    /** name of the file in which we save */
    protected String saveFileName = null;
    
    // TODO Move to GUI side if used
    /** the save mode, maybe unused... */
    protected int saveMode = 2;
    /** the frame containing the graph */
    protected GsMainFrame mainFrame;
    protected GsFileFilter defaultFileFilter = null;

    protected boolean canDelete = false;
    protected boolean opening = false;
    protected boolean saved = true;
    protected boolean isParsing = false;
    protected List nodeOrder = new ArrayList();
    protected GsGraphDescriptor descriptor = null;
    protected List<GsGraphListener<V,E>> listeners = new ArrayList<GsGraphListener<V,E>>();

    protected String graphName = "default_name";

    protected String tabLabel = "STR_tab_selection";

    protected Annotation gsAnnotation = null;
    protected String dtdFile = GsGinmlHelper.DEFAULT_URL_DTD_FILE;

    private static List v_layout = null;
    private static List v_export = null;
    private static List v_action = null;
    private static List v_OManager = null;

    private List v_notification = new ArrayList();
    private List v_blockEdit = null;
    private List v_blockClose = null;

    /**  an edged has been added */
    public static final int CHANGE_EDGEADDED = 0;
    /**  an edged has been removed */
    public static final int CHANGE_EDGEREMOVED = 1;
    /** a vertex has been added  */
    public static final int CHANGE_VERTEXADDED = 2;
    /**  a vertex has been removed */
    public static final int CHANGE_VERTEXREMOVED = 3;
    /**  an edge has been modified */
    public static final int CHANGE_EDGEUPDATED = 4;
    /**  a vertex has been modified */
    public static final int CHANGE_VERTEXUPDATED = 5;
    /**  a vertex has been modified */
    public static final int CHANGE_MERGED = 6;
    /**  other kind of change */
    public static final int CHANGE_METADATA = 7;

    protected static final String zip_prefix = "GINsim-data/";

	protected GsVertexAttributesReader vReader;
	protected GsEdgeAttributesReader eReader;

    private Map m_objects = null;

    private static int graphID = 0;
    private int id;

    // TODO specialize his variable on the specialized graph that require it
    protected GsGraph associatedGraph = null;
    protected String associatedID = null;

    protected boolean annoted = false;
    protected boolean extended = false;
    protected boolean compressed = true;

    /**
     * @param descriptor
     */
    public GsGraph(GsGraphDescriptor descriptor) {
        this(descriptor, null, false);
    }
    /**
     *
     * @param descriptor
     * @param saveFileName
     */
    public GsGraph(GsGraphDescriptor descriptor, String saveFileName, boolean parsing) {
        this.descriptor = descriptor;
        this.saveFileName = saveFileName;
        this.isParsing = parsing;
        if (saveFileName == null) {
            id = graphID++;
           GsEnv.registerGraph(this, "[UNSAVED-"+id+"]");
        } else {
            GsEnv.registerGraph(this, saveFileName);
        }
        graphManager = new GsJgraphtGraphManager(this, mainFrame);
        vReader = graphManager.getVertexAttributesReader();
        eReader = graphManager.getEdgeAttributesReader();
    }

    // TODO Usage? Where to move?
    // should stay on server side: emit a signal when parsing is finished
    // Needed as the graph data structure can be inconsistent during parsing
    public void endParsing() {
    	isParsing = false;
    	for (GsGraphListener<V,E> l: listeners) {
    		l.endParsing();
    	}
    }

    // TODO Usage? Where to move?
    // stays on server side
    // it is only used by the BiblioList to avoid errors during parsing that will be solved once all the data is here
    // better solution is welcome
    public boolean isParsing() {
    	return isParsing;
    }
    /**
     * set the save fileName.
     *
     * @param saveFileName
     */
    // TODO Move to GUI side
    public void setSaveFileName(String saveFileName) {
        this.saveFileName = saveFileName;
        // TODO: cleaner way to remember if extended or not
        if (saveFileName.endsWith(".zginml")) {
            extended = true;
        } else if (saveFileName.endsWith(".ginml")) {
            extended = false;
        }
        endParsing();
    }
    
    
    /**
     * set the save mode.
     *
     * @param saveMode
     */
    // TODO Move to GUI side
    public void setSaveMode(int saveMode) {
        this.saveMode = saveMode;
    }
    
    
    /**
     * @param param kind of vertex to create
     * @return the newly created vertex (or null if failed/inapropriate)
     */
    // TODO Change to generic call of Vertex creation  from Graph and change position on the new node
    abstract protected V doInteractiveAddVertex (int param);
    
    
    /**
     *
     * @param source
     * @param target
     * @param param the kind of edge to create
     * @return the newly created edge (or null if failed/inapropriate)
     */
 // TODO Change to generic call of Edge creation from Graph
    abstract protected Object doInteractiveAddEdge (V source, V target, int param);
    
    
    
    /**
     * actually save the graph.
     *
     * @param os
     * @param mode
     * @param selectedOnly
     * @throws GsException
     */
    // TODO Move to GUI side
    // I think it should stay on the server side as it is the actual save implementation
    // it could be moved to a dedicated IO class, but should be server side
    abstract protected void doSave(OutputStreamWriter os, int mode, boolean selectedOnly) throws GsException;

    /**
     *
     * @return a FileFilter for the save dialog (or null)
     */
    // TODO Move to GUI side
    // --> GraphGUIHelper
    abstract protected FileFilter doGetFileFilter();
    
    /**
     * @return an accessory panel for the save dialog (or null)
     */
    // TODO Move to GUI side
    // --> GraphGUIHelper
    abstract protected JPanel doGetFileChooserPanel();

    /**
     * save the current graph.
     * if the graph has never been saved, calls saveAs.
     *
     * this will call the specialised method doSave() to do the work
     * @throws GsException
     */
    // TODO Move to GUI side
    // merge it with the save action if the options are moved there as well
    public void save() throws GsException {
        if (saveFileName == null) {
            saveAs(false);
        } else {
            save(false, saveFileName, saveMode, extended, compressed);
        }
    }

	/**
	 * similar to save() but will save only the selected subgraph
	 * @throws GsException
	 */
    // TODO Move to GUI side
	public void saveSubGraph() throws GsException {
	    GsGraph subGraph = getSubGraph( mainFrame.getSelectedVertices(), mainFrame.getSelectedEdges() );
	    if (subGraph != null) {
	        subGraph.saveAs();
	        subGraph.saveMode = saveMode;
	    }
	}

    /**
     * select a new save location.
     * when done, calls save()
     * @throws GsException
     */
    // TODO Move to GUI side
    public void saveAs() throws GsException {
    		saveAs(false);
    }
    
    
    /**
     *
     * @param selectedOnly
     * @throws GsException
     */
    // TODO Move to GUI side
    private void saveAs(boolean selectedOnly) throws GsException {
    	JComponent poption = doGetFileChooserPanel();
    	String filename = GsOpenAction.selectSaveFile(mainFrame,
    						doGetFileFilter(),
							poption, getAutoFileExtension());

    	if (filename != null) {
    		int saveMode = 0;
            boolean extended = false;
            boolean compressed = true;
    		if (poption != null && poption instanceof GsGraphOptionPanel ) {
    			saveMode = ((GsGraphOptionPanel)poption).getSaveMode();
                extended = ((GsGraphOptionPanel)poption).isExtended();
                compressed = ((GsGraphOptionPanel)poption).isCompressed();
    		}
    		if (selectedOnly) {
    			save(true, filename, saveMode, extended, compressed);
    		} else {
    			this.saveMode = saveMode;
                String s_oldfn = saveFileName;
    			saveFileName = filename;
    			save(false, null, saveMode, extended, compressed);
                if (s_oldfn == null) {
                    GsEnv.renameGraph("[UNSAVED-"+id+"]", saveFileName);
                } else {
                    GsEnv.renameGraph(s_oldfn, saveFileName);
                }
    		}
    	}
    }

    protected String getGraphZipName() {
    	return "ginml";
    }
    /**
     *
     * @param selectedOnly
     * @param savePath
     * @param saveMode
     * @param extended
     * @param compressed
     * @throws GsException
     */
    // TODO Move to GUI
    // this is the implementation, it stays on the server side
    // all interactive-notifications should be replaced by exceptions...
    private void save(boolean selectedOnly, String savePath, int saveMode, boolean extended, boolean compressed) {
        try {
        	File f = new File(savePath != null ? savePath : this.saveFileName);
        	File ftmp = null;
        	String fileName = f.getAbsolutePath();;
        	if (f.exists()) {
            	// create a temporary file to avoid destroying a good file in case save does not work
        		try {
        			ftmp = File.createTempFile(f.getName(), null, f.getParentFile());
        			fileName = ftmp.getAbsolutePath();
        		} catch (Exception e) {
        			// TODO: introduce a clean permission checking
        			System.out.println("Could not use a tmp file in the same directory");
//        			ftmp = File.createTempFile(f.getName(), null);
//        			fileName = ftmp.getAbsolutePath();
        		}
        	}

            if (!extended) {
                OutputStreamWriter os = new OutputStreamWriter(new FileOutputStream(fileName), "UTF-8");
                doSave(os, saveMode, selectedOnly);
                os.close();
            } else {
                ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(fileName));
                if (!compressed) {
                	// FIXME: uncompressed Zip require to set the size and CRC by hand!!
                	// this must be done for each ZipEntry: save it to a tmpfile,
                	// mesure the CRC and the size, then put it in the uncompressed zip...
                	zos.setMethod(ZipOutputStream.STORED);
                }
                zos.putNextEntry(new ZipEntry(zip_prefix+getGraphZipName()));
                OutputStreamWriter osw = new OutputStreamWriter(zos, "UTF-8");
                doSave(osw, saveMode, selectedOnly);
                osw.flush();
                zos.closeEntry();
                // now save associated objects
                if (v_OManager != null) {
                    for (int i=0 ; i<v_OManager.size() ; i++) {
                        GsGraphAssociatedObjectManager manager = (GsGraphAssociatedObjectManager)v_OManager.get(i);
                        if (manager.needSaving(this)) {
                            zos.putNextEntry(new ZipEntry(zip_prefix+manager.getObjectName()));
                            try {
                                manager.doSave(osw, this);
                            } catch (Exception e) {
                                if (mainFrame != null) {
                                    addNotificationMessage(new GsGraphNotificationMessage(this, new GsException(GsException.GRAVITY_ERROR, e)));
                                } else {
                                    e.printStackTrace();
                                }
                            } finally {
                                osw.flush();
                                zos.closeEntry();
                            }
                        }
                    }
                }
                List v_specManager = getSpecificObjectManager();
                if (v_specManager != null) {
                    for (int i=0 ; i<v_specManager.size() ; i++) {
                        GsGraphAssociatedObjectManager manager = (GsGraphAssociatedObjectManager)v_specManager.get(i);
                        if (manager.needSaving(this)) {
                            zos.putNextEntry(new ZipEntry(zip_prefix+manager.getObjectName()));
                            manager.doSave(osw, this);
                            osw.flush();
                        }
                    }
                }
                zos.close();
            }
            if (selectedOnly) {
                if (mainFrame != null) {
                    addNotificationMessage(new GsGraphNotificationMessage(this, "selection saved", GsGraphNotificationMessage.NOTIFICATION_INFO));
                }
            } else {
                saved = true;
                this.extended = extended;
                this.compressed = compressed;
                if (mainFrame != null) {
                    addNotificationMessage(new GsGraphNotificationMessage(this, "graph saved", GsGraphNotificationMessage.NOTIFICATION_INFO));
                    mainFrame.updateTitle();
                }
            }

            if (ftmp != null) {
            	// Everything went fine, rename the temporary file
            	boolean r = ftmp.renameTo(f);
            	if (!r) {
            		if (f.exists()) {
            			f.delete();
            			r = ftmp.renameTo(f);
            		}
                	if (!r) {
                        addNotificationMessage(new GsGraphNotificationMessage(this, new GsException(GsException.GRAVITY_ERROR, "renaming of the temporary file failed: "+ftmp.getAbsolutePath())));
                	}
            	}
            }
            OptionStore.addRecent(fileName);
        } catch (Exception e) {
            if (mainFrame != null) {
                addNotificationMessage(new GsGraphNotificationMessage(this, new GsException(GsException.GRAVITY_ERROR, e)));
            } else {
                e.printStackTrace();
            }
        }
    }

    /**
     *
     * @return the graphManager
     */
    public GsGraphManager<V,E> getGraphManager() {
        return graphManager;
    }

    /**
     * add a new vertex at the given position.
     *
     * @param param the kind of vertex to create.
     * @param x
     * @param y
     * @return the added vertex
     */
    public Object interactiveAddVertex(int param, int x, int y) {
        if (v_blockEdit != null) {
            return null;
        }
        V obj = doInteractiveAddVertex(param);
        if (obj != null) {
            graphManager.placeVertex(obj, x, y);
            fireGraphChange(CHANGE_VERTEXADDED, obj);
        }
        return obj;
    }

    /**
     *
     * @param source
     * @param target
     * @param param
     */
    // TODO Usage? Where to move?
    // triggered by the GUI, it changes a bit with the new architecture, should be killed and
    // replaced by a cleaner solution. The signal firing should go to the server side
    public void interactiveAddEdge(V source, V target, int param) {
        if (v_blockEdit != null) {
            return;
        }
    	Object obj = doInteractiveAddEdge(source, target, param);
    	if (obj != null) {
    		fireGraphChange(CHANGE_EDGEADDED, obj);
    	}
    }

    /**
     * remove a vertex from the graph.
     *
     * @param obj
     */
    // TODO Move 'fireGraphChange' call to AbstractGraphFrontend ?
    // yes, something like that
    public void removeVertex(V obj) {
        graphManager.removeVertex(obj);
        fireGraphChange(CHANGE_VERTEXREMOVED, obj);
    }

    /**
     * @return a GsParameterPanel able to edit edges of this graph or null if not applicable.
     */
    // TODO Move to GUI
    public GsParameterPanel getEdgeAttributePanel() {
    	return null;
    }
    
    /**
     * @return a GsParameterPanel able to edit vertices of this graph or null if not applicable.
     */
    // TODO Move to GUI
    public GsParameterPanel getVertexAttributePanel() {
    	return null;
    }

    // TODO Move to GUI
    // TODO: deprecate get----AttributePanel to promote get---Editor
    public ObjectEditor getEdgeEditor() {
    	return null;
    }
    
    // TODO Move to GUI
    public ObjectEditor getVertexEditor() {
    	return null;
    }

    /**
     * @return true if the graph is already saved (ie hasn't been altered since the last save);
     */
    // TODO Move to GUI
    public boolean isSaved() {
        return saved;
    }
    /**
     * @return the name under which this graph is saved
     */
    // TODO Move to GUI
    public String getSaveFileName() {
        return saveFileName;
    }

    /**
     * @return the name associated with this graph.
     */
    // TODO Move to AbstractGraphFrontend
    public String getGraphName() {
        return graphName;
    }
    /**
     * changes (if success) the name associated with this graph.
     * By default only valid xmlid are accepted.
     *
     * @param graphName the new name.
     * @throws GsException if the name is invalid.
     */
    // TODO Move to AbstractGraphFrontend
    public void setGraphName(String graphName) throws GsException {

		if (!graphName.matches("[a-zA-Z_]+[a-zA-Z0-9_-]*")) {
		    throw new GsException(GsException.GRAVITY_ERROR, "Invalid name");
		}
        this.graphName = graphName;
        annoted = true;
        fireMetaChange();
    }

    /**
     * This function returns null by default, if not replaced, the frame will create generics buttons to
     * switch between editing modes. if the graph has it's own modes it should return a Vector here, eventually empty
     * to disable editing.
     *
     * @return a vector describing edit modes.
     * each element of the vector must be an GsEditModeDescriptor object
     */
    // TODO Move to AbstractGraphFrontend
    public Vector getEditingModes() {
        return null;
    }
    /**
     * set the id of the given vertex to the given new id.
     * we have to avoid duplicates id in some kind of graphs.
     *
     * @param vertex
     * @param newId
     * @throws GsException
     */
    abstract public void changeVertexId(Object vertex, String newId) throws GsException;

    /**
     * @param obj
     */
    abstract public void removeEdge(E obj);

	/**
	 * @param layout
	 */
 // TODO Move to AbstractGraphFrontend (GraphView)
	public static void registerLayoutProvider(GsActionProvider layout) {
		if (v_layout == null) {
			v_layout = new Vector();
		}
		v_layout.add(layout);
	}
	
	/**
	 * @return a list of avaible layouts.
	 */
	// TODO Move to AbstractGraphFrontend (GraphView)
	public List getLayout() {
		return v_layout;
	}

	/**
	 * @param export
	 */
	// TODO Move to AbstractGraphFrontend
	public static void registerExportProvider(GsActionProvider export) {
		if (v_export == null) {
			v_export = new Vector();
		}
		v_export.add(export);
	}

	/**
	 * @return a list of avaible export filters.
	 */
	// TODO Move to AbstractGraphFrontend
	public List getExport() {
		return v_export;
	}

	/**
	 *
	 * @param action
	 */
	// TODO Move to AbstractGraphFrontend
	public static void registerActionProvider(GsActionProvider action) {
		if (v_action == null) {
			v_action = new Vector();
		}
		v_action.add(action);
	}
	/**
	 * @return a list of avaible actions.
	 */
	// TODO Move to AbstractGraphFrontend
	public List getAction() {
		return v_action;
	}

    /**
     * register a manager to open/save associated objects
     *
     * @param manager
     */
	// TODO Move to AbstractGraphFrontend
    public static void registerObjectManager(GsGraphAssociatedObjectManager manager) {
        if (v_OManager == null) {
            v_OManager = new Vector();
        }
            v_OManager.add(manager);
    }
    /**
     * @return object managers
     */
    // TODO Move to AbstractGraphFrontend
    public List getObjectManager() {
        return v_OManager;
    }

    // TODO Move to AbstractGraphFrontend
    public GsGraphAssociatedObjectManager getObjectManager(Object key) {
    	if (v_OManager == null) {
    		return null;
    	}
        for (int i=0 ; i<v_OManager.size() ; i++) {
        	GsGraphAssociatedObjectManager manager = (GsGraphAssociatedObjectManager)v_OManager.get(i);
        	if (manager.getObjectName().equals(key)) {
        		return manager;
        	}
        }
        return null;
    }

	/**
	 * @return a vector of layout actions for this kind of graph
	 */
    // TODO Move to AbstractGraphFrontend (GraphView)
	abstract public List getSpecificLayout();
	
	/**
	 * @return a vector of export actions for this kind of graph.
	 */
	// TODO Move to AbstractGraphFrontend
	abstract public List getSpecificExport();
	
    /**
     * @return a vector of action related to this kind of graph.
     */
	// TODO Move to AbstractGraphFrontend
    abstract public List getSpecificAction();
    
    /**
     * @return a vector of action related to this kind of graph.
     */
    // TODO Move to AbstractGraphFrontend
    abstract public List getSpecificObjectManager();
    /**
     * @param key
     * @return the object manager associated with THIS kind of graph and to a given key
     */
    // TODO Move to AbstractGraphFrontend
    public GsGraphAssociatedObjectManager getSpecificObjectManager(Object key) {
    	Vector v_OManager = GsRegulatoryGraphDescriptor.getObjectManager();
    	if (v_OManager == null) {
    		return null;
    	}
        for (int i=0 ; i<v_OManager.size() ; i++) {
        	GsGraphAssociatedObjectManager manager = (GsGraphAssociatedObjectManager)v_OManager.get(i);
        	if (manager.getObjectName().equals(key)) {
        		return manager;
        	}
        }
        return null;
    }


	/**
	 * @return the node order
	 */
    // TODO Move to AbstractGraphFrontend
	public List getNodeOrder() {
		return nodeOrder;
	}
	
	
	/**
	 * add a graph listener
	 * @param gl
	 */
	// TODO Move to AbstractGraphFrontend
	public void addGraphListener(GsGraphListener gl) {
		listeners.add(gl);
	}
	
	
	/**
	 * remove a graph listener.
	 * @param gl
	 */
	// TODO Move to AbstractGraphFrontend
	public void removeGraphListener(GsGraphListener gl) {
		listeners.remove(gl);
	}
	
	/**
	 * the graph has changed, all listeners will be notified.
	 * it will also be marked as unsaved.
	 * @param change
     * @param data
	 */
	// TODO Move to AbstractGraphFrontend
	public void fireGraphChange(int change, Object data) {
		if (saved && !opening) {
		    saved = false;
		    if (mainFrame != null) {
		        mainFrame.updateTitle();
		    }
		}

        // TODO: extend this to support undo/redo and more events!
        List l_cascade = new ArrayList();
		switch (change) {
		case CHANGE_EDGEADDED:
			for (GsGraphListener<V, E> l: listeners) {
				GsGraphEventCascade gec = l.edgeAdded((E)data);
                if (gec != null) {
                    l_cascade.add(gec);
                }
			}
			break;
		case CHANGE_EDGEREMOVED:
			for (GsGraphListener<V, E> l: listeners) {
				GsGraphEventCascade gec = l.edgeRemoved((E)data);
                if (gec != null) {
                    l_cascade.add(gec);
                }
			}
			break;
		case CHANGE_VERTEXADDED:
			for (GsGraphListener<V, E> l: listeners) {
				GsGraphEventCascade gec = l.vertexAdded((V)data);
                if (gec != null) {
                    l_cascade.add(gec);
                }
			}
			break;
        case CHANGE_VERTEXREMOVED:
			for (GsGraphListener<V, E> l: listeners) {
				GsGraphEventCascade gec = l.vertexRemoved((V)data);
                if (gec != null) {
                    l_cascade.add(gec);
                }
            }
            break;
        case CHANGE_MERGED:
			for (GsGraphListener<V, E> l: listeners) {
				GsGraphEventCascade gec = l.graphMerged((List<V>)data);
                if (gec != null) {
                    l_cascade.add(gec);
                }
            }
            break;
        case CHANGE_VERTEXUPDATED:
			for (GsGraphListener<V, E> l: listeners) {
				GsGraphEventCascade gec = l.vertexUpdated((V)data);
                if (gec != null) {
                    l_cascade.add(gec);
                }
            }
            break;
        case CHANGE_EDGEUPDATED:
			for (GsGraphListener<V, E> l: listeners) {
				GsGraphEventCascade gec = l.edgeUpdated((E)data);
                if (gec != null) {
                    l_cascade.add(gec);
                }
            }
            break;
		}
        if (l_cascade.size() > 0) {
            addNotificationMessage(new GsGraphNotificationMessage(this, "cascade update", new GsGraphEventCascadeNotificationAction(), l_cascade, GsGraphNotificationMessage.NOTIFICATION_INFO_LONG));
        }
	}

	/**
	 * panel to edit graph parameters, null by default => hidden.
	 * if you want one, just override this method and return your JPanel.
	 *
	 * @return the graphParameterPanel (null by default, please override)
	 */
	// TODO Move to GUI (GUIGraphHelper)
	public JPanel getGraphParameterPanel() {
		return null;
	}
	
	// TODO Move to GUI (GUIGraphHelper)
	public ObjectEditor getGraphEditor() {
		return null;
	}
	
	/**
	 * @param nodeOrder The nodeOrder to set.
	 */
	// TODO Move to AbstractGraphFrontend
	public void setNodeOrder(Vector nodeOrder) {
		this.nodeOrder = nodeOrder;
	}

	/**
     * is interactive deletion of items allowed in this graph ?
     * if true, it will get a "DEL" toolbar button.
	 * @return true if this graph allow interactivly deleting items
	 */
	// TODO Move to GUI (GraphGUIHelper)
	public boolean canDelete() {
		return canDelete;
	}

	/**
	 * get the extension to be added to this kind of graph.
     * override me to return something else than <code>null</code> (ie no automatic extension)
	 * @return the extension to always add to filenames.
	 */
	// TODO Move to GUI (GraphGUIHelper)
    public String getAutoFileExtension() {
        return null;
    }

	/**
	 * set the mainFrame containing this graph.
	 *
	 * @param mainFrame
	 */
    // TODO Move to GUI
	public void setMainFrame(GsMainFrame mainFrame) {
		this.mainFrame = mainFrame;
        graphManager.setMainFrame(mainFrame);
        graphManager.ready();
        if (mainFrame != null) {
            if (mainFrame.getGraph() != this) {
                mainFrame.getEventDispatcher().fireGraphChange(null, mainFrame.getGraph(), this, false);
            }
            updateGraphNotificationMessage(this);
        }
        vReader = graphManager.getVertexAttributesReader();
    	eReader = graphManager.getEdgeAttributesReader();
        saved = true;
	}

	/**
	 * get the copied graph or null if none.
	 * NOTE: storing the copied graph in a static field is strongly encouraged
	 * to allow copying between different graphs of the same type.
	 *
	 * graph which aren't interactively editable should just return null here.
	 *
	 * @return the previously copied graph.
	 */
	// TODO deprecated
	protected abstract GsGraph getCopiedGraph();

	/**
	 * set the copied graph
	 * NOTE: storing the copied graph in a static field is strongly encouraged
	 * to allow copying between different graphs of the same type.
	 *
	 * graph which aren't interactively editable should do nothing here.
	 *
	 * @param graph the new copied graph
	 * @see #getSubGraph(Vector, Vector)
	 */
	// TODO deprecated
	protected abstract void setCopiedGraph(GsGraph graph);

	/**
	 * Merge the current graph with the given graph.
	 * the graph to merge can come from a file or be a copied graph.
	 *
	 * graph which aren't interactively editable should do nothing here.
	 *
	 * @param otherGraph
     * @return the vector of newly added items
	 */
	// TODO Already moved
	protected abstract List doMerge (GsGraph otherGraph);

	/**
	 * create a copied graph with selected vertices and edges.
	 * NOTE: storing the copied graph in a static field is strongly encouraged
	 * to allow copying between different graphs of the same type.
	 *
	 * graph which aren't interactively editable should do nothing here.
	 *
	 * @param vertex the selected vertices
	 * @param edges the selected edges
	 *
	 * @return the copied graph
	 */
	protected abstract GsGraph getSubGraph (Collection<V> vertex, Collection<E> edges) ;

	/**
	 * copy the selected part of the current graph.
	 * this is just an abstract layer, the real copy has to be done by the graph implementor.
	 * @see #getSubGraph(Vector, Vector)
	 * @see #getCopiedGraph()
	 */
	// TODO Move to GUI side
	public void copy() {
		if (mainFrame == null) {
			return;
		}
		List v_edges = new Vector();
		List v_vertex = mainFrame.getSelectedVertices();
        if (v_vertex == null) {
            // stop here if nothing to copy: avoid loosing copied graph for nothing...
            return;
        }
		for (int i=0 ; i<v_vertex.size() ; i++) {
			for (int j=0 ; j<v_vertex.size() ; j++) {
			    Object edge = mainFrame.getGraph().getGraphManager().getEdge(v_vertex.get(i), v_vertex.get(j));
			    if (edge != null) {
			        v_edges.add(edge);
			    }
			}
        }
		getSubGraph(v_vertex, v_edges);
	}

	/**
	 * paste a previously copied graph.
	 * @see #doMerge(GsGraph)
	 */
	// TODO Remove this method:
	// On GUI side:
	//     - Copy method that get the selected vertex and edges and call the specialized vertex DoCopySelection() method
	//        to obtain a graph
	//     - call the server side on the merge( graph) method
	public void paste( ) {
	    GsGraph graph = getCopiedGraph();
	    if (graph != null) {
	        List v = doMerge(graph);
	        if (v != null) {
	        	fireGraphChange(CHANGE_MERGED, v);
	        }
            graphManager.select(v);
	    }
	}

	/**
	 * open a graph from a file and merge it with the current one.
	 */
	// TODO REMOVE
	public void merge() {
	    GsGraph graph = GsOpenAction.open(GsGinsimGraphDescriptor.getInstance(), null);
	    if (graph != null) {
	    	List v = doMerge(graph);
	        if (v != null) {
	        	fireGraphChange(CHANGE_MERGED, v);
	        	graphManager.select(v);
	        }
	    }
	}

	/**
	 * block editing mode for this graph.
	 *
	 * @param key
	 * @see #removeBlockEdit(Object)
	 */
	// TODO Move on GUI Side (FrameActions)
	public void addBlockEdit(Object key) {
	    if (key == null) {
	        return;
	    }
	    if (v_blockEdit == null) {
	        v_blockEdit = new Vector();
	    }
	    v_blockEdit.add(key);
	}

	/**
	 * @return true if edit isn't blocked
	 * @see #addBlockEdit(Object)
	 */
	// TODO Move on GUI Side (FrameActions)
	public boolean isEditAllowed() {
	    return v_blockEdit == null;
	}

	/**
	 * @param key
	 * @see #addBlockEdit(Object)
	 */
	// TODO Move on GUI Side (FrameActions)
	public void removeBlockEdit (Object key) {
	    if (v_blockEdit == null) {
	        return;
	    }

	    v_blockEdit.remove(key);
	    if (v_blockEdit.size() == 0) {
	        v_blockEdit = null;
	    }
	}
	/**
	 * prevent closing this graph.
	 * use with HIGH caution please!
	 * @see #removeBlockClose(Object)
	 *
	 * @param key
	 */
	// TODO Move on GUI Side (FrameActions)
	public void addBlockClose(Object key) {
	    if (key == null) {
	        return;
	    }
	    if (v_blockClose == null) {
	        v_blockClose = new Vector();
	    }
	    v_blockClose.add(key);
	}

	/**
	 * @param key
	 * @see #addBlockClose(Object)
	 */
	// TODO Move on GUI Side (FrameActions)
	public void removeBlockClose (Object key) {
	    if (v_blockClose == null) {
	        return;
	    }

	    v_blockClose.remove(key);
	    if (v_blockClose.size() == 0) {
	        v_blockClose = null;
	    }
	}

    /**
     * is this graph closable or is it blocked ?
     * NOTE: this is _NOT_ a matter of being saved or not
     * @return true if closing this graph is allowed
     * @see #addBlockClose(Object)
     * @see #isSaved()
     */
	// TODO Move on GUI Side (FrameActions)
    public boolean canClose() {
        return v_blockClose == null;
    }

    /**
     * @return an info panel for "whattodo" frame
     */
    // TODO Already moved 
    public JPanel getInfoPanel() {
        return null;
    }

    /**
     * @return the descriptor
     */
    public GsGraphDescriptor getDescriptor() {
        return descriptor;
    }

    /**
     * plugins/algo/anything may want to associate objects with a graph to retrieve them later.
     * this (and <code>addObject(key, obj)</code>) makes it easy.
     *
     * @see #addObject(Object, Object)
     * @param key
     * @param create if true, a non-defined object will be created
     * @return the associated object
     */
    public Object getObject (Object key, boolean create) {
        if (m_objects == null) {
        	if (create) {
        		m_objects = new HashMap();
        	} else {
        		return null;
        	}
        }
        Object ret = m_objects.get(key);
        if (create && ret == null) {
        	GsGraphAssociatedObjectManager manager = getObjectManager(key);
        	if (manager == null) {
        		manager = getSpecificObjectManager(key);
        	}
        	if (manager != null) {
        		ret = manager.doCreate(this);
        		addObject(key, ret);
        	}
        }
        return ret;
    }

    /**
     * plugins/algo/anything may want to associate objects with a graph to retrieve them later.
     * this (and <code>getObject(key)</code>) makes it easy.
     *
     * @see #getObject(Object)
     * @see #removeObject(Object)
     * @param key
     * @param obj
     */
    public void addObject(Object key, Object obj) {
        if (m_objects == null) {
            m_objects = new HashMap();
        }
        m_objects.put(key, obj);
    }

    /**
     * remove an object previously associated to a graph with <code>addObject(Object, Object)</code>.
     *
     * @see #getObject(Object)
     * @see #addObject(Object, Object)
     * @param key
     */
    public void removeObject(Object key) {
        if (m_objects == null) {
            return;
        }
        m_objects.remove(key);
    }

	/**
     * get the annotation associated with this graph.
	 * @return the association associated with this graph
	 */
	public Annotation getAnnotation() {
		if (gsAnnotation == null) {
			gsAnnotation = new Annotation();
		}
		return gsAnnotation;
	}

    /**
     * @return true is the graph is empty
     */
    public boolean isEmpty() {
        return !annoted && getAnnotation().isEmpty() && graphManager.getVertexCount() == 0;
    }

	/**
     * get the DTD used by this graph (if relevant).
	 * @return the (ginml) DTD for this graph
	 */
	public String getDTD() {
		return dtdFile;
	}

    /**
     * get the label to display in the "selected item" tab
     * @return the label (translatable string)
     */
    public String getTabLabel() {
        return tabLabel;
    }

    /**
     * is this graph visible ?
     * some actions may be only avaible on visibles graphs...
     * @return true if the graph is visible
     */
    public boolean isVisible() {
        return mainFrame != null;
    }
    /**
     * close the graph.
     */
    public void close() {
        if (saveFileName != null) {
            GsEnv.unregisterGraph(saveFileName);
        } else {
            GsEnv.unregisterGraph("[UNSAVED-"+id+"]");
        }
        graphManager = null;
    }

    /**
     * @return the graph associated with this one.
     */
    public GsGraph getAssociatedGraph() {

        if (associatedGraph == null && getAssociatedGraphID() != null) {
            GsGraph ag = GsEnv.getRegistredGraph(associatedID);
            if (ag != null) {
                setAssociatedGraph(ag);
            } else {
                File f = new File(associatedID);
                if (f.exists()) {
                    ag = GsGinsimGraphDescriptor.getInstance().open(f);
                    GsEnv.newMainFrame(ag);
                    setAssociatedGraph(ag);
                } else {
                    GsEnv.error(new GsException(GsException.GRAVITY_INFO, "STR_openAssociatedGraphFailed"+"\n"+associatedID), mainFrame);
                }
            }
        }

        // check association
        if (associatedGraph != null && !isAssociationValid(associatedGraph)) {
            associatedGraph = null;
            associatedID = null;
        }

        return associatedGraph;
    }

    /**
     * test if a graph can be associated with this one.
     * this is a default implementation and will always return false, override to do something usefull.
     *
     * @param graph
     * @return true if this is a valid associated graph.
     */
    protected boolean isAssociationValid(GsGraph graph) {
        if (graph == null) {
            return false;
        }
        return false;
    }

    /**
     * @param regGraph
     */
    public void setAssociatedGraph(GsGraph regGraph) {

        if (regGraph == null || !isAssociationValid(regGraph)) {
            return;
        }

        if (associatedGraph != null) {
            associatedGraph.removeGraphListener(this);
            associatedGraph.getGraphManager().getEventDispatcher().removeGraphChangeListener(this);
            associatedGraph = null;
            return;
        }
        associatedGraph = regGraph;
        associatedGraph.addGraphListener(this);
        regGraph.getGraphManager().getEventDispatcher().addGraphChangedListener(this);
    }

    public GsGraphEventCascade edgeAdded(E data) {
        setAssociatedGraph(null);
        return null;
    }
    public GsGraphEventCascade edgeRemoved(E data) {
        setAssociatedGraph(null);
        return null;
    }
    public GsGraphEventCascade edgeUpdated(E data) {
        return null;
    }


    public GsGraphEventCascade vertexAdded(V data) {
        setAssociatedGraph(null);
        return null;
    }

	public GsGraphEventCascade graphMerged(Collection<V> data) {
        setAssociatedGraph(null);
		return null;
	}
    public GsGraphEventCascade vertexUpdated(V data) {
        return null;
    }

    public GsGraphEventCascade vertexRemoved(Object data) {
        setAssociatedGraph(null);
        return null;
    }

    public void graphChanged(GsNewGraphEvent event) {
        if (event.getOldGraph() == associatedGraph) {
            setAssociatedGraph(null);
        }
    }

    public void graphSelectionChanged(GsGraphSelectionChangeEvent event) {
    }

    public void graphClosed(GsGraph graph) {
        // it must be the associated regulatory graph
        if (graph == associatedGraph) {
            associatedID = associatedGraph.getSaveFileName();
            setAssociatedGraph(null);
        }
    }

    /**
     * set the path to the associated graph.
     * @param value
     */
    public void setAssociatedGraphID(String value) {
        associatedID = value;
    }
    /**
     * @return the ID (path) of the associated graph.
     */
    public String getAssociatedGraphID() {
        if (associatedGraph != null) {
            associatedID = associatedGraph.getSaveFileName();
            if (associatedID == null) {
                GsEnv.error(new GsException(GsException.GRAVITY_INFO, Translator.getString("STR_associate_save")), mainFrame);
                return null;
            }
        }

        if (associatedID != null) {
            File f = new File(associatedID);
            if (!f.exists() || !f.canRead()) {
                GsEnv.error(new GsException(GsException.GRAVITY_INFO, Translator.getString("STR_associate_notfound")+associatedID), mainFrame);
                associatedID = null;
            }
        } else {
            GsEnv.error(new GsException(GsException.GRAVITY_INFO, Translator.getString("STR_associate_manual")), mainFrame);
        }

        if (associatedID == null) {
            associatedID = GsOpenAction.selectFileWithOpenDialog(mainFrame);
        }

        return associatedID;
    }
    /**
     * fire a change in the configuration of the graph (structural changes are handled by graphManager)
     */
    public void fireMetaChange() {
        fireGraphChange(CHANGE_METADATA, null);
    }

    // TODO: ideally move to the GUI
    // may be troublesome, also affects all other notification methods below
	public void updateGraphNotificationMessage(GsGraph graph) {
        if (graphManager == null) {
            // can happen when the graph has been closed before the end of the timeout ?
            return;
        }
		graphManager.getEventDispatcher().updateGraphNotificationMessage(this);
	}

	/**
	 * @return the topmost message to display
	 */
	public GsGraphNotificationMessage getTopMessage() {
		if (v_notification == null | v_notification.size() == 0) {
			return null;
		}
		return (GsGraphNotificationMessage)v_notification.get(v_notification.size()-1);
	}

	/**
	 * @param message
	 */
	public void deleteNotificationMessage(GsGraphNotificationMessage message) {
		if (v_notification == null | v_notification.size() == 0) {
			return;
		}
		v_notification.remove(message);
		updateGraphNotificationMessage(this);
	}
	/**
	 * @param message
	 */
	public void deleteAllNotificationMessage(GsGraphNotificationMessage message) {
		if (v_notification == null | v_notification.size() == 0) {
			return;
		}
		for (int i=v_notification.size()-1 ; i>=0 ; i--) {
			if (message.equals(v_notification.get(i))) {
				v_notification.remove(i);
			}
		}
		updateGraphNotificationMessage(this);
	}
	/**
	 *
	 */
	public void deleteAllNotificationMessage() {
		if (v_notification == null | v_notification.size() == 0) {
			return;
		}
		v_notification.clear();
		updateGraphNotificationMessage(this);
	}

	/**
	 * @param message
	 */
	public void addNotificationMessage(GsGraphNotificationMessage message) {
		v_notification.add(message);
		if (mainFrame != null) {
			updateGraphNotificationMessage(this);
		}
	}
	
	/**
	 * Search the nodes ID matching the regexp. 
	 * Other kind of graph could overwrite this method. 
	 * @param regexp
	 * @return
	 */
	public Vector searchNodes(String regexp) {
		Vector v = new Vector();
		
		Pattern pattern = Pattern.compile(regexp, Pattern.COMMENTS | Pattern.CASE_INSENSITIVE);
		
		for (Iterator it = getGraphManager().getVertexIterator(); it.hasNext();) {
			Object vertex = (Object) it.next();
			Matcher matcher = pattern.matcher(vertex.toString());
			if (matcher.find()) {
				v.add(vertex);
			}
		}
		return v;
	}
}
