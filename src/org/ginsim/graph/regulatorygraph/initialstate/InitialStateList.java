package org.ginsim.graph.regulatorygraph.initialstate;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.ginsim.graph.regulatorygraph.RegulatoryVertex;

import fr.univmrs.tagc.common.datastore.SimpleGenericList;
import fr.univmrs.tagc.common.xml.XMLWriter;

public class InitialStateList extends SimpleGenericList {
	List nodeOrder;
	
    public InitialStateList(List nodeOrder, boolean input) {
    	prefix = input ? "input_" : "initState_";
    	canAdd = true;
    	canEdit = true;
    	canRemove = true;
    	canOrder = true;
    	this.nodeOrder = nodeOrder;
    }
    
    public List getNodeOrder() {
    	
		return nodeOrder;
	}

	protected Object doCreate(String name, int mode) {
		InitialState i = new InitialState();
		i.setName(name);
		return i;
	}
	
	public void vertexRemoved(Object data, List v) {
        // remove it from initial states
        for (int i=0 ; i<v_data.size() ; i++) {
        	InitialState is = (InitialState)v_data.get(i);
        	if (is.m.containsKey(data)) {
        		is.m.remove(data);
        		v.add(is);
            }
        }
	}

	public void vertexUpdated(Object data, List v) {
	    // remove unavailable values from initial states
        RegulatoryVertex vertex = (RegulatoryVertex)data;
        for (int i=0 ; i<v_data.size() ; i++) {
            InitialState is = (InitialState)v_data.get(i);
            List v_val = (List)is.m.get(data);
            if (v_val != null) {
                for (int k=v_val.size()-1 ; k>-1 ; k--) {
                    Integer val = (Integer)v_val.get(k);
                    if (val.intValue() > vertex.getMaxValue()) {
                        v_val.remove(k);
                        if (v_val.size() == 0) {
                            is.m.remove(data);
                            if (is.m.isEmpty()) {
                                remove(null, new int[] {i});
                            }
                        }
                        v.add(is);
                    }
                }
            }
        }
	}

    public Object getInitState(String s) {
        for (int i=0 ; i<getNbElements(null) ; i++) {
            InitialState istate = (InitialState)getElement(null, i);
            if (istate.getName().equals(s)) {
                return istate;
            }
        }
        return null;
    }
    public void addInitState(String s, Map m) {
        for (int i=0 ; i<getNbElements(null) ; i++) {
            InitialState istate = (InitialState)getElement(null, i);
            if (istate.getName().equals(s)) {
                m.put(istate, null);
                return;
            }
        }
    }

    public void toXML(XMLWriter out, String tag) throws IOException {
        for (int i=0 ; i<getNbElements(null) ; i++) {
            InitialState is = (InitialState)getElement(null, i);
            out.openTag(tag);
            out.addAttr("name", is.name);
            String s = "";
            Iterator it_line = is.m.keySet().iterator();
            while (it_line.hasNext()) {
                RegulatoryVertex vertex = (RegulatoryVertex)it_line.next();
                List v_val = (List)is.m.get(vertex);
                s += vertex.getId();
                for (int j=0 ; j<v_val.size() ; j++) {
                        s += ";"+((Integer)v_val.get(j)).intValue();
                }
                s += " ";
            }
            out.addAttr("value", s.trim());
            out.closeTag();
        }
    }
}
