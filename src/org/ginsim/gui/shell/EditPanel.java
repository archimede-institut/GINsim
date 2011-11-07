package org.ginsim.gui.shell;

import java.awt.Component;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JTabbedPane;

import org.ginsim.gui.graph.GUIEditor;
import org.ginsim.gui.graph.GraphGUI;

import fr.univmrs.tagc.common.OptionStore;

public class EditPanel extends JTabbedPane {

    private Map<String, Integer> m_tabs = new HashMap<String, Integer>();
    private int mmapDivLocation = ((Integer)OptionStore.getOption("display.minimapSize", new Integer(100))).intValue();

	
	protected EditPanel() {
		
	}
	
	/**
	 * Add an editing tab.
	 * 
	 * @param tab
	 * @param constraint
	 */
    public void addTab(String name, GUIEditor<?> tab, int constraint) {
    	Component panel = tab.getComponent();
        if (m_tabs.containsKey(name)) {
            // TODO: error
            return;
        }
        addTab(name, null, panel, null);
        m_tabs.put(name, new Integer(constraint));
        updateTabs(TabSelection.TAB_CHECK);
    }

    /**
     * 
     * @param vertices the list of selected vertices (can be null)
     * @param edges the list of selected edges (can be null)
     * 
     * @return the current selection type
     */
    private TabSelection getCurrentSelectionType(Collection<?> vertices, Collection<?> edges) {
        int nb_edges = vertices == null ? 0 : vertices.size();
        int nb_vertices = edges == null ? 0 : edges.size();
        if (nb_edges == 0) {
            switch (nb_vertices) {
                case 0:
                    return TabSelection.TAB_NONE;
                case 1:
                    return TabSelection.TAB_SINGLE;
                default:
                	return TabSelection.TAB_MULTIPLE;
            }
        }
        
        if (nb_vertices == 0) {
            if (nb_edges == 1) {
            	return TabSelection.TAB_SINGLE;
            }
            return TabSelection.TAB_MULTIPLE;
        }
        return TabSelection.TAB_MULTIPLE;
    }
    
    /**
     * enable/disable tabs depending on the constraint
     * if the selected tab becomes inactive, select another one
     */
    private void updateTabs(TabSelection constraint) {
    	TabSelection cst = constraint;
        if (constraint == TabSelection.TAB_CHECK) {
        	// FIXME:
        	cst = TabSelection.TAB_NONE;
            // cst = getCurrentSelectionType();
        }

        int selected = getSelectedIndex();
        boolean need_change = true;
        if (selected != -1) {
            Integer i_sel = (Integer)m_tabs.get(getTitleAt(selected));
            int sel = 0;
            if (i_sel != null) {
                sel = i_sel.intValue();
            }
            need_change = (sel & cst.flag) == 0;
        }
        int nbtabs = getTabCount();
        for (int i=0 ; i<nbtabs ; i++) {
            Integer curCst = (Integer)m_tabs.get(getTitleAt(i));
            int cur_cst = 0;
            if (curCst != null) {
                cur_cst = curCst.intValue();
            }

            if ((cur_cst & cst.flag) > 0) {
                setEnabledAt(i, true);
                if (need_change) {
                    setSelectedIndex(i);
                    need_change = false;
                }
            } else {
                setEnabledAt(i, false);
            }
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
            m_tabs.remove(name);
            updateTabs(TabSelection.TAB_CHECK);
            return true;
        }
        return false;
    }

	public void buildEditionPanels(GraphGUI<?, ?, ?> gui) {
    	// TODO: add the edition panels at the right position
    	gui.getMainEditionPanel();
    	gui.getEdgeEditionPanel();
    	gui.getNodeEditionPanel();
	}

}

enum TabSelection {
	TAB_CHECK(0), TAB_NONE(1), TAB_SINGLE(2), TAB_MULTIPLE(4);

	public final int flag;
	
	private TabSelection(int flag) {
		this.flag = flag;
	}
}
