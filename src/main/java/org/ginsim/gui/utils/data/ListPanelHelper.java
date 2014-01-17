package org.ginsim.gui.utils.data;

import org.ginsim.core.utils.data.NamedList;

import java.awt.Component;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.JLabel;

/**
 * Provide configuration and helper methods for a list panel.
 * The helper defines capability switches (reordering, adding, removing),
 * GUI hints such as available actions, and methods to create new items.
 * 
 * @author Aurelien Naldi
 * @param <T> the type of objects in the list
 */
public class ListPanelHelper<T, L extends List<T>> {
	
	public static final String SEL_EMPTY="SEL_EMPTY", SEL_SINGLE="SEL_SINGLE", SEL_MULTIPLE="SEL_MULTIPLE";

    private static final String NAME_CREATE = "doCreate";
    private static final String NAME_ADDINLINE = "addInline";
    private static final String NAME_REMOVE = "doRemove";
    private static final String NAME_COLUMN = "getColumnName";

    private static final ColumnDefinition[] DEFAULT_COLUMN = new ColumnDefinition[] {ColumnDefinition.SINGLE};

	public Map<Class<?>, Component> m_right = null;

	public boolean canFilter = true;
	public boolean canOrder = true;

    // Auto-detected capabilities
    private final boolean hasNamedColumn;
	private final boolean canAdd;
    private final boolean canAddInline;
    private final boolean canRemove;

	List<String> addOptions = null;

    public ListPanelHelper() {

        // Detect capabilities based on overridden methods
        boolean canAdd = false;
        boolean canAddInline = false;
        boolean canRemove = false;
        boolean hasNamedColumn = false;
        Class cl = getClass();
        for (Method m: cl.getMethods()) {
            if (m.getDeclaringClass() != cl) {
                continue;
            }
            if (m.getName() == NAME_CREATE) {
                canAdd = true;
            } else if (m.getName() == NAME_ADDINLINE) {
                canAddInline = true;
            } else if (m.getName() == NAME_REMOVE) {
                canRemove = true;
            } else if (m.getName() == NAME_COLUMN) {
                hasNamedColumn = true;
            }
        }

        // save capabilities
        this.canAdd = canAdd;
        this.canAddInline = canAddInline;
        this.canRemove = canRemove;
        this.hasNamedColumn = hasNamedColumn;
    }

    public int create(L list, Object arg) {
        return doCreate(list, arg);
    }

	public boolean remove(L list, int[] sel) {
		if (sel == null || sel.length < 1 || !canRemove) {
			return false;
		}
		
		if (doRemove(list, sel)) {
			return true;
		}
		return false;
	}


    /* *************************************************************************
     *                       DECLARE CAPABILITIES
     *****************************************************************************/
    public final boolean canCreate() {
        return canAdd;
    }
    public final boolean canAddInline() {
        return canAddInline;
    }
    public final boolean canRemove() {
        return canRemove;
    }
    public final boolean hasNamedColumn() {
        return hasNamedColumn;
    }


    /* ***************************************************************************
     *     The following methods should be overridden to extend capabilities
     *****************************************************************************/
    public ColumnDefinition[] getColumns() {
        return DEFAULT_COLUMN;
    }

    public String[] getActionLabels() {
        return new String[0];
    }

    public Object[] getCreateTypes() {
        return null;
    }

    public Object getValue(L list, T o, int column) {
        return o;
    }
    public boolean setValue(L list, int row, int column, Object value) {
        if (column == 0 && list instanceof NamedList) {
            return ((NamedList) list).rename(row, value.toString());
        }
        return false;
    }
    public int doCreate(L list, Object arg) {
        return -1;
    }

    public int addInline(L list, String s) {
        return -1;
    }

    public ListPanelCompanion getCompanion(ListEditionPanel<T,L> editPanel) {
        return null;
    }

    public void runAction(L list, int row, int col) {}

    public boolean doRemove(L list, int[] sel) {
        return false;
    }

    protected final boolean removeItems(L list, int[] sel) {
        if (sel == null || sel.length < 1) {
            return false;
        }

        if (sel.length == 1) {
            list.remove(sel[0]);
            return true;
        }

        List<T> toRemove = new ArrayList<T>();
        for (int idx: sel) {
            toRemove.add(list.get(idx));
        }
        list.removeAll(toRemove);

        return true;
    }

    public boolean moveData(L list, int[] sel, int diff) {

        // check that the move is possible
        int max = list.size();
        for (int a: sel) {
            int dst = a+diff;
            if (dst < 0 || dst >= max) {
                // can not do this move
                return false;
            }
        }

        // actually move elements
        for (int i=0 ; i<sel.length ; i++) {
            int src = sel[i];
            int dst = src + diff;

            if (src < 0 || dst < 0 || src >= max || dst >= max) {
                continue;
            }
            T o = list.remove(src);
            list.add(dst, o);

            sel[i] = dst;
        }
        return true;
    }

}
