package org.ginsim.gui.shell.editpanel;

import java.awt.Component;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.JTabbedPane;

import org.ginsim.gui.graph.GraphGUI;
import org.ginsim.gui.graph.GraphGUIListener;

import fr.univmrs.tagc.GINsim.gui.GsGraphicAttributePanel;

/**
 * Tabbed panel which follows the current selection.
 * 
 * TODO: should we put back some of the old activation and selection logics?
 * 
 * @author Aurelien Naldi
 */
public class EditPanel extends JTabbedPane implements GraphGUIListener {

    private final GraphGUI<?, ?, ?> gui;
    private final List<EditTab> tabs = new ArrayList<EditTab>();
    
    private SelectionType selectionType = SelectionType.SEL_NONE;
	
	public EditPanel( GraphGUI<?, ?, ?> gui) {
		this.gui = gui;
		gui.addGraphGUIListener(this);
		addTab(new MainEditTab(gui));
		addTab(new GsGraphicAttributePanel(gui));
	}
	
	/**
	 * Add an editing tab.
	 * 
	 * @param tab
	 * @param constraint
	 */
    public void addTab(EditTab tab) {
    	String name = tab.getTitle();
    	Component panel = tab.getComponent();
        addTab(name, null, panel, null);
        tabs.add(tab);
        updateTabs(selectionType);
    }

    /**
     * 
     * @param vertices the list of selected vertices (can be null)
     * @param edges the list of selected edges (can be null)
     * 
     * @return the current selection type
     */
    private SelectionType getCurrentSelectionType(Collection<?> vertices, Collection<?> edges) {
        int nb_edges = vertices == null ? 0 : vertices.size();
        int nb_vertices = edges == null ? 0 : edges.size();
        if (nb_edges == 0) {
            switch (nb_vertices) {
                case 0:
                    return SelectionType.SEL_NONE;
                case 1:
                    return SelectionType.SEL_NODE;
                default:
                	return SelectionType.SEL_MULTIPLE;
            }
        }
        
        if (nb_vertices == 0) {
            if (nb_edges == 1) {
            	return SelectionType.SEL_EDGE;
            }
            return SelectionType.SEL_MULTIPLE;
        }
        return SelectionType.SEL_MULTIPLE;
    }
    
    /**
     * enable/disable tabs depending on the constraint
     * if the selected tab becomes inactive, select another one
     */
    private void updateTabs(SelectionType selection) {
        for (int i=0 ; i<tabs.size() ; i++) {
        	boolean enabled = tabs.get(i).isActive(selection);
        	setEnabledAt(i, enabled);
        }
    }
    
    /**
     * Remove an editing tab.
     * 
     * @param name
     * @return
     */
    private boolean removeTab(String name) {
        int i = indexOfTab(name);
        if (i != -1) {
            Component c = getComponentAt(i);
            removeTabAt(i);
            tabs.remove(i);
            return true;
        }
        return false;
    }

	@Override
	public void graphSelectionChanged(GraphGUI gui) {
		updateTabs( getCurrentSelectionType(null, null));
	}

	@Override
	public void graphGUIClosed(GraphGUI gui) {
		tabs.clear();
		removeAll();
	}
}
