package fr.univmrs.tagc.GINsim.graph;

import java.io.File;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.filechooser.FileFilter;

import org.ginsim.graph.common.Graph;

/**
 * describes a kind of graph.
 * Implementors are highly encouraged to offer a static <code>getInstance()</code> method
 */
public interface GsGraphDescriptor {

    /**
     * @return the kind of graph.
     */
    public String getGraphType();
    /**
     * @return the byte name of this kind of graph (translatable).
     */
    public String getGraphName();
    /**
     * @return a description of this kind of graph (translatable).
     */
    public String getGraphDescription();
    
    /**
     * @return true if this kind of graph can be created interactivly by the user.
     */
    public boolean canCreate();
    
	/**
	 * @param file
	 * @return a new graph readed from the file
	 */
	public Graph open(File file);
	/**
	 * @param map
	 * @param file
	 * @return a new graph readed from the file
	 */
	public Graph open(Map map, File file);
	/**
	 * @return the fileFilter for the open dialog
	 */
	public FileFilter getFileFilter();
	/**
	 * @param mode
	 * @return an icon for the graph.
	 */
	public ImageIcon getGraphIcon(int mode);
}