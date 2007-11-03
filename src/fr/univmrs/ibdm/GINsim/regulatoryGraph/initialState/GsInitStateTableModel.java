package fr.univmrs.ibdm.GINsim.regulatoryGraph.initialState;

import java.util.Map;
import java.util.Vector;

import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import fr.univmrs.ibdm.GINsim.regulatoryGraph.GsRegulatoryVertex;
import fr.univmrs.tagc.widgets.EnhancedJTable;
import fr.univmrs.tagc.widgets.StackDialog;

/**
 * model for the initState table.
 * "help" the user to select initial states for his simulation.
 */
public class GsInitStateTableModel extends AbstractTableModel {

	private static final long serialVersionUID = -1553864043658960569L;
	private Vector nodeOrder;
    private Map m_initState = null;
    private GsInitialStateList imanager;
	private int nbCol;
    private StackDialog frame;
    private boolean several;
    
    private JTable theTable;
	
	/**
	 * simple constructor
	 * 
	 * @param nodeOrder
     * @param frame
	 */	
	public GsInitStateTableModel(Vector nodeOrder, StackDialog frame, GsInitialStateList imanager, boolean several) {
		super();
		this.nodeOrder = nodeOrder;
        this.frame = frame;
        this.imanager = imanager;
        this.several = several;
		nbCol = nodeOrder.size();
	}

	public int getRowCount() {
        if (imanager == null ) {
            return 0;
        }
		return imanager.getNbElements(null)+1;
	}

	public boolean isCellEditable(int rowIndex, int columnIndex) {
		if (columnIndex < 2 && rowIndex >= imanager.getNbElements(null)) {
			return false;
		}
		return true;
	}

	public Class getColumnClass(int columnIndex) {
		if (columnIndex == 1) {
			return Boolean.class;
		}
		return String.class;
	}

	/**
	  * @see javax.swing.table.TableModel#getValueAt(int, int)
	  * 
	  * here, each cell contains a vector of integer to represent a list of ranges/values
	  * 
	  * we want to show the vector [1 ; 2 ; 3 ; 4 ;  6 ; 7] as : "1-4 ; 6-7"
	 */
	public Object getValueAt(int rowIndex, int columnIndex) {
		Vector element;

		if (columnIndex == 0) {
			if (rowIndex >= imanager.getNbElements(null)) {
				return "";
			}
			return ((GsInitialState)imanager.getElement(null, rowIndex)).getName();
		}
		if (columnIndex == 1) {
			if (m_initState == null || rowIndex >= imanager.getNbElements(null)) {
				return Boolean.FALSE;
			}
			if (m_initState.containsKey(imanager.getElement(null, rowIndex))) {
				return Boolean.TRUE;
			}
			return Boolean.FALSE;
		}
		int ci = columnIndex - 2;
		if (imanager == null || rowIndex >= imanager.getNbElements(null)) {
			return "";
		}
        Map m_row = ((GsInitialState)imanager.getElement(null, rowIndex)).m;
        element = (Vector)m_row.get(nodeOrder.get(ci));
        return showValue(element, ((GsRegulatoryVertex)nodeOrder.get(ci)).getMaxValue());
    }
    
    /**
     * 
     * @param element
     * @param maxvalue
     * @return a formated String showing values for this gene
     */
    public static Object showValue(Vector element, int maxvalue) {
        if (element == null) {
            return "*";
        }
		
        String ret = "";
		int i=1;
		
		int len = element.size()-1;
		int precval =((Integer)element.get(0)).intValue();
		int nextval = precval+1;
		int val;

		if (precval == maxvalue) {
			ret += "M"+maxvalue+"";
		} else {
			ret += precval;
		}
		nextval = precval+1;
		for ( ; i<len ; i++) {
			val = ((Integer)element.get(i)).intValue();
			if ( val == nextval ) {  // we are still in the same serial
				nextval++;
			}
			else {
				// close previous serial
				if ( nextval != precval+1) {
					if (precval-1 == maxvalue) {
					} else {
						if (nextval-1 == maxvalue) {
							ret += "-M"+maxvalue+"";
						} else {
							ret += "-" + (nextval-1);
						}
					}
				}
				// open a new serial
				if (val == maxvalue) {
					ret += " ; M"+maxvalue+"";
				}else {
					ret += " ; " + val;
				}
				precval = val;
				nextval = val+1;
			}
		}
		i = element.size()-1;
		if ( i > 0) {  // we have to threat the last val differently !
			val = ((Integer)element.get(i)).intValue();
			if ( val == nextval ) {
				if (val == maxvalue) {
					ret += "-M"+maxvalue+"";
				} else {
					ret += "-" + val;
				}
			} else {
				// close previous serial
				if ( nextval != precval+1) {
					if (nextval-1 == maxvalue) {
						ret += "-M"+maxvalue+"";
					} else {
						ret += "-" + (nextval-1);
					}
				}
				//write the last element
				if (val == maxvalue) {
					ret += " ; M"+maxvalue+"";
				} else {
					ret += " ; " + val;
				}
			}
		}
		return ret;
	}

	/**
	 * @see javax.swing.table.TableModel#setValueAt(java.lang.Object, int, int)
	 * 
	  * here, each cell contains a vector of integer to represent a list of ranges/values
	  * 
	  * here we have to parse strings like "1-4 ; 6-7" to construct vector like [1 ; 2 ; 3 ; 4 ;  6 ; 7]
	 */
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        frame.setMessage("");
		if (m_initState == null || rowIndex > getRowCount() || columnIndex > nbCol+1) {
			return;
		}
		if (columnIndex == 0) {
			if (rowIndex >= imanager.getNbElements(null)) {
				return;
			}
			((GsInitialState)imanager.getElement(null, rowIndex)).setName((String)aValue);
			// FIXME: the name should be unique
			return;
		}
		if (columnIndex == 1) {
			if (rowIndex >= imanager.getNbElements(null)) {
				return;
			}
			if (aValue == Boolean.TRUE) {
				if (!several) {
					m_initState.clear();
					//fireTableDataChanged();
				}
				m_initState.put(imanager.getElement(null, rowIndex), null);
			} else {
				m_initState.remove(imanager.getElement(null, rowIndex));
				// set it to null if empty ? probably _not_ a good idea
			}
			return;
		}
		
		int[] r_sel = theTable.getSelectedRows();
		int[] c_sel = theTable.getSelectedColumns();
		for (int i=0 ; i<r_sel.length ; i++) {
			for (int j=0 ; j<c_sel.length ; j++) {
				doSetValueAt(aValue, r_sel[i], c_sel[j]);
			}
		}
	}
	
	public void doSetValueAt(Object aValue, int rowIndex, int columnIndex) {
		int ci = columnIndex - 2;
		int maxvalue = ((GsRegulatoryVertex)nodeOrder.get(ci)).getMaxValue();
        if (aValue == null || ((String)aValue).trim().equals("") || ((String)aValue).trim().equals("*")) {
            if (rowIndex >= 0 && rowIndex < getRowCount()-1) {
                Map m_line = ((GsInitialState)imanager.getElement(null, rowIndex)).m;
                m_line.remove(nodeOrder.get(ci));
                if (m_line.size() == 0) {
                    imanager.remove(null, new int[] {rowIndex});
                    fireTableStructureChanged();
                }
            }
            return;
        }
        
		// change the value if appropriate
		Vector newcell = new Vector(1);
		try {
			String[] values = ((String)aValue).split(";");
			for (int i=0 ; i<values.length ; i++) {
				String[] minmax = values[i].split("-");
				if (minmax.length > 2) {
                    frame.setMessage("syntax error");
					return;
				}
				// remove spaces !!
				int min;
				int max;
				String s_tmp = minmax[0].trim();
				if (s_tmp.equalsIgnoreCase("m") || s_tmp.equalsIgnoreCase("max")) {
					if (minmax.length>1) {
                        frame.setMessage("interval starting at max !!");
						return;
					}
					min = maxvalue;
				} else {
					min = Integer.parseInt(s_tmp);
				}
				if (minmax.length == 2) {
					s_tmp = minmax[1].trim(); 
					if (s_tmp.equalsIgnoreCase("m") || s_tmp.equalsIgnoreCase("max")) {
						max = maxvalue;
					} else {
						max = Integer.parseInt(s_tmp);
					}
					if (max < min) {
                        frame.setMessage("bad interval");
						return; 
					}
				} else {
					max = min;
				}
				if (max > maxvalue || min > maxvalue || max<0 ||min<0 ) {
                    frame.setMessage("bad value: out of range");
					return;
				}
				while (min <= max) {
					newcell.add(new Integer(min));
					min++;
				}
			}
            // if on the last line: create a new line an check it
            if (rowIndex == imanager.getNbElements(null)) {
            	imanager.add();
            	fireTableRowsInserted(rowIndex, rowIndex);
            	setValueAt(Boolean.TRUE, rowIndex, 1);
            }
            Map m_line = ((GsInitialState)imanager.getElement(null, rowIndex)).m;
            m_line.put(nodeOrder.get(ci),newcell);
			fireTableCellUpdated(rowIndex,ci+2);
		} catch (Exception e) {}
	}

	public String getColumnName(int columnIndex) {
		if (columnIndex > nodeOrder.size()+1) {
			return null;
		}
		if (columnIndex == 0) {
			return "name";
		}
		if (columnIndex == 1) {
			return "use";
		}
		return ((GsRegulatoryVertex)nodeOrder.elementAt(columnIndex-2)).toString();
	}

	public int getColumnCount() {
		return nodeOrder.size()+2;
	}
    
	/**
	 * empty the table of init states.
	 *
	 */
	public void reset() {
        if (m_initState == null) {
            return;
        }
        m_initState.clear();
	    fireTableStructureChanged();
	}
	
	/**
	 * delete a row
	 * @param row the index of the row to delete
	 */
	public void deleteRow(int row) {
        if (imanager == null || row < 0 || row == getRowCount()-1) {
            return;
        }
        imanager.remove(null, new int[] {row});
        fireTableRowsDeleted(row, row);
	}

    /**
     * reverse job of the "getContent" method
     * @param param
     */
    public void setParam(GsInitialStateStore param) {
        this.m_initState = param.getInitialState();
        fireTableStructureChanged();
    }

	public void setTable(EnhancedJTable tableInitStates) {
		theTable = tableInitStates;
	}
}
