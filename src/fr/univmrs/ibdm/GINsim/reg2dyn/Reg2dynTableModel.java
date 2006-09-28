package fr.univmrs.ibdm.GINsim.reg2dyn;

import java.util.Vector;

import javax.swing.table.AbstractTableModel;

import fr.univmrs.ibdm.GINsim.regulatoryGraph.GsRegulatoryVertex;

/**
 * model for the initState table.
 * "help" the user to select initial states for his simulation.
 */
public class Reg2dynTableModel extends AbstractTableModel {

	private static final long serialVersionUID = -1553864043658960569L;
	private Vector vStates;
	private Vector nodeOrder;
	private int nbCol;
	private Vector[] line;
    private Reg2dynFrame frame;
	
	/**
	 * simple constructor
	 * 
	 * @param nodeOrder
     * @param frame
	 */	
	public Reg2dynTableModel(Vector nodeOrder, Reg2dynFrame frame) {
		super();
		vStates = new Vector();
		this.nodeOrder = nodeOrder;
        this.frame = frame;
		nbCol = nodeOrder.size();
	}

	public int getRowCount() {
		if (vStates==null) return 0;
		return vStates.size()+1;
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
		String ret = "";
		Vector element;

		if (vStates==null) return null;
		if (rowIndex >= vStates.size()) return "";
		element = ((Vector[])vStates.get(rowIndex))[columnIndex];
		
		int i=1;
		
		int len = element.size()-1;
		int precval =((Integer)element.get(0)).intValue();
		int nextval = precval+1;
		int val;
		int maxvalue = ((GsRegulatoryVertex)nodeOrder.get(columnIndex)).getMaxValue();

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
     * @param element
     * @param maxvalue
     * @return the value to display
     */
    public static String getNiceValue(Vector element, int maxvalue) {
        String ret = "";
        int i = 1;
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
		if (vStates == null || rowIndex > vStates.size() || columnIndex > nbCol) return;
		
		int maxvalue = ((GsRegulatoryVertex)nodeOrder.get(columnIndex)).getMaxValue();
		
		// if on the last line: create a new line
		if (rowIndex == vStates.size()) {
				line = new Vector[nodeOrder.size()];
				for (int i=0 ; i<nodeOrder.size() ; i++) {
					line[i] = new Vector(1);
					line[i].add(new Integer(0));
					fireTableCellUpdated(rowIndex,i);
					fireTableCellUpdated(rowIndex+1,i);
				}
				vStates.add(line);
                fireTableRowsInserted(rowIndex, rowIndex);
		} else {
			line = (Vector[])vStates.get(rowIndex);
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
				line[columnIndex] = newcell;
			}
			fireTableCellUpdated(rowIndex,columnIndex);
		} catch (Exception e) {}
        frame.setMessage("");
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

//	/**
//	 * here is the hard job for this model: transform the internal representation with each cell containing a list of value
//	 * to a list off ALL initial states corresponding to this !
//	 * 
//	 * it uses the reg2dynStatesIterator for this purpose...
//	 * 
//	 * @return a vector representing all initial states we want to use!
//	 */
//	public Vector getContent() {
//		// initialize content
//		if (content == null) {
//			content = new Vector(0);
//		} else {
//		    content.clear();
//		}
//		
//		// fill content
//		for (int i=0 ; i<vStates.size() ; i++) {
//			// create iterator
//			Reg2DynStatesIterator iterator = new Reg2DynStatesIterator(nodeOrder, (Vector[])vStates.get(i));
//			while (iterator.hasNext()) {
//				content.add(iterator.next());
//			}
//		}
//		return content;
//	}
    
	/**
	 * reset the table of init states.
	 * empty the table if the number of gene changed
	 * otherwise just remove too high values
	 * 
	 * @param nodeOrder
	 */
	public void reset(Vector nodeOrder) {
	    this.nodeOrder = nodeOrder;
	    if (nodeOrder.size() != nbCol) {
            nbCol = nodeOrder.size();
	        reset();
	        return;
	    }
	    for (int i=0 ; i<vStates.size() ; i++) {
	        Vector[] line = (Vector[])vStates.get(i);
	        for (int j=0 ; j<nbCol ; j++) {
	            Vector v = line[j];
	            int maxValue = ((GsRegulatoryVertex)nodeOrder.get(j)).getMaxValue();
	            for (int k=0 ; k<v.size() ; k++) {
	                Integer value = (Integer)v.get(k);
	                if (value.intValue() > maxValue) {
	                    v.remove(value);
	                }
	            }
	            if (v.size() == 0) {
	                v.add(new Integer(0));
	            }
	        }
	    }
        // just for the case where node order changed or a node has been renamed
        // the only clean way to detect it would be to work on a copy of the nodeOrder
        fireTableStructureChanged();
        frame.updateTable();
	}
	/**
	 * empty the table of init states.
	 *
	 */
	public void reset() {
	    if (vStates != null) {
	        vStates.clear();
	    }
	    fireTableStructureChanged();
        frame.updateTable();
	}
	
	/**
	 * delete a row
	 * @param row the index of the row to delete
	 */
	public void deleteRow(int row) {
	    if (row >= 0 && row < getRowCount()-1) {
	        vStates.remove(row);
	        fireTableRowsDeleted(row, row);
	    }
	}

    /**
     * @return vStates.
     */
    public Vector getVStates() {
        return vStates;
    }
    /**
     * reverse job of the "getContent" method
     * @param vStates
     */
    public void setVStates(Vector vStates) {
       this.vStates = vStates;
       fireTableStructureChanged();
    }
}
