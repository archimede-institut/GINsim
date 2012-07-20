package org.ginsim.service.tool.circuit;

public class CircuitDescrInTree {
    private CircuitDescr circuit;
    public boolean summary;
    public int key;
    
    public CircuitDescrInTree(CircuitDescr cdescr, boolean summary, int key) {
        this.setCircuit(cdescr);
        this.key = key;
        this.summary = summary;
    }

    public String toString() {
        if (getCircuit().t_vertex == null) {
            return "no name";
        }
        int nbChild = 1;
        int index;
        if (summary) {
        	Object o = null;
            switch (key) {
            case CircuitDescr.ALL:
            	if (getCircuit().t_sub != null) {
            		nbChild = getCircuit().t_sub.length;
            	} else {
            		nbChild = 1;
            	}
                break;
            case CircuitDescr.FUNCTIONAL:
            	nbChild = getCircuit().v_functionnal.size();
            	o = getCircuit().v_functionnal.get(0);
                break;
            case CircuitDescr.POSITIVE:
            	nbChild = getCircuit().v_positive.size();
            	o = getCircuit().v_positive.get(0);
                break;
            case CircuitDescr.NEGATIVE:
            	nbChild = getCircuit().v_negative.size();
            	o = getCircuit().v_negative.get(0);
                break;
            case CircuitDescr.DUAL:
            	nbChild = getCircuit().v_dual.size();
            	o = getCircuit().v_dual.get(0);
                break;
            }
            if (nbChild == 1 && o != null) {
            		index = ((CircuitDescrInTree)o).key;
            } else {
            	index = 0;
            }
        } else {
        	index = key;
        }
        String s = "";
        // if the circuit has several children, then hide details
        if (summary && (nbChild > 1 || getCircuit().t_sub == null)) {
            for (int i=0 ; i < getCircuit().t_vertex.length ; i++) {
                s += " " + getCircuit().t_vertex[i];
            }
        } else { // if one child only, show details here and hide the child
	        int[] t_pos;
	        t_pos = getCircuit().t_sub[index];
	        for (int i=0 ; i < getCircuit().t_vertex.length ; i++) {
	            s += " " + getCircuit().t_vertex[i];
	            if (t_pos[i] != 0) {
	                s += "["+t_pos[i]+"]";
	            }
	        }
        }
        if (summary && getCircuit().t_sub != null && getCircuit().t_sub.length > 1) {
        	if (getCircuit().t_sub.length == nbChild) {
                s += "  ("+nbChild+")";
        	} else {
                s += "  ("+nbChild+"/"+getCircuit().t_sub.length+")";
        	} 
        }
        return s;
    }

    public long getScore() {
        if (summary) {
            return getCircuit().score;
        }
        return getCircuit().t_mark[key][0];
    }

	public void setCircuit(CircuitDescr circuit) {
		this.circuit = circuit;
	}

	public CircuitDescr getCircuit() {
		return circuit;
	}
}
