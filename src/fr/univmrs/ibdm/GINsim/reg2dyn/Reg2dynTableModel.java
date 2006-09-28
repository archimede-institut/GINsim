package fr.univmrs.ibdm.GINsim.reg2dyn;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.swing.table.AbstractTableModel;

import fr.univmrs.ibdm.GINsim.regulatoryGraph.GsRegulatoryVertex;

/**
 * model for the initState table.
 * "help" the user to select initial states for his simulation.
 */
public class Reg2dynTableModel extends AbstractTableModel {

	private static final long serialVersionUID = -1553864043658960569L;
	private Vector nodeOrder;
    private GsSimulationParameters param = null;
	private int nbCol;
    private GsReg2dynFrame frame;
	
	/**
	 * simple constructor
	 * 
	 * @param nodeOrder
     * @param frame
	 */	
	public Reg2dynTableModel(Vector nodeOrder, GsReg2dynFrame frame) {
		super();
		this.nodeOrder = nodeOrder;
        this.frame = frame;
		nbCol = nodeOrder.size();
        setValueAt("0", 0, 0);
	}

	public int getRowCount() {
        if (param == null ) {
            return 0;
        }
        if (param.initStates == null) {
            return 1;
        }
		return param.initStates.size()+1;
	}

	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return true;
	}

	public Class getColumnClass(int columnIndex) {
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

		if (param == null || param.initStates == null || rowIndex >= param.initStates.size()) return "";
        Map m_row = (Map)param.initStates.get(rowIndex);
        element = (Vector)m_row.get(nodeOrder.get(columnIndex));
        return showValue(element, ((GsRegulatoryVertex)nodeOrder.get(columnIndex)).getMaxValue());
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
		if (param == null || rowIndex > getRowCount() || columnIndex > nbCol) return;
		int maxvalue = ((GsRegulatoryVertex)nodeOrder.get(columnIndex)).getMaxValue();
		
        if (aValue == null || ((String)aValue).trim().equals("") || ((String)aValue).trim().equals("*")) {
            if (rowIndex >= 0 && rowIndex < getRowCount()-1) {
                Map m_line = (Map)param.initStates.get(rowIndex);
                m_line.remove(nodeOrder.get(columnIndex));
                if (m_line.size() == 0) {
                    param.initStates.remove(m_line);
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
            // if on the last line: create a new line
            Map m_line;
            if (param.initStates == null) {
                param.initStates = new Vector();
            }
            if (rowIndex == getRowCount()-1) {
                    m_line = new HashMap();
                    param.initStates.add(m_line);
                    fireTableRowsInserted(rowIndex, rowIndex);
            } else {
                m_line = (Map)param.initStates.get(rowIndex);
            }
            m_line.put(nodeOrder.get(columnIndex),newcell);

			fireTableCellUpdated(rowIndex,columnIndex);
		} catch (Exception e) {}
	}

	public String getColumnName(int columnIndex) {
		if (columnIndex >= nodeOrder.size()) {
			return null;
		}
		return ((GsRegulatoryVertex)nodeOrder.elementAt(columnIndex)).toString();
	}

	public int getColumnCount() {
		return nodeOrder.size();
	}
    
	/**
	 * empty the table of init states.
	 *
	 */
	public void reset() {
        if (param == null || param.initStates == null) {
            return;
        }
        for (int i=0 ; i<param.initStates.size() ; i++) {
            ((Map)param.initStates.get(i)).clear();
        }
        param.initStates.clear();
        param.initStates = null;
	    fireTableStructureChanged();
	}
	
	/**
	 * delete a row
	 * @param row the index of the row to delete
	 */
	public void deleteRow(int row) {
        if (param == null || param.initStates == null || row < 0 || row == getRowCount()-1) {
            return;
        }
        Map m = (Map)param.initStates.get(row);
        m.clear();
        param.initStates.remove(m);
        fireTableRowsDeleted(row, row);
	}

    /**
     * reverse job of the "getContent" method
     * @param param
     */
    public void setParam(GsSimulationParameters param) {
        this.param = param;
        fireTableStructureChanged();
    }
}
