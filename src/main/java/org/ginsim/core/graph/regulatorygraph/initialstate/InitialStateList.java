package org.ginsim.core.graph.regulatorygraph.initialstate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.colomoto.logicalmodel.NodeInfo;
import org.ginsim.common.xml.XMLWriter;
import org.ginsim.core.graph.regulatorygraph.RegulatoryNode;
import org.ginsim.core.utils.data.ListenableNamedList;


public class InitialStateList extends ListenableNamedList<InitialState> {

	private final List nodeOrder;
    private final String prefix;

    public InitialStateList(List nodeOrder, boolean input) {
        super();
    	prefix = input ? "input_" : "initState_";
    	this.nodeOrder = nodeOrder;
    }
    
    public List getNodeOrder() {
    	
		return nodeOrder;
	}

	public int add() {
		InitialState i = new InitialState();
		i.setName(findUniqueName(prefix));
        add(i);
        return size()-1;
	}
	
	public void vertexRemoved(Object data, List v) {
        // remove it from initial states
        for (InitialState is: this) {
        	if (is.m.containsKey(data)) {
        		is.m.remove(data);
        		v.add(is);
            }
        }
	}

	public void vertexUpdated(Object data, List v) {
	    // remove unavailable values from initial states
        RegulatoryNode vertex = (RegulatoryNode)data;
        List<InitialState> toremove = null;
        for (InitialState is: this) {
            List v_val = is.m.get(data);
            if (v_val != null) {
                for (int k=v_val.size()-1 ; k>-1 ; k--) {
                    Integer val = (Integer)v_val.get(k);
                    if (val.intValue() > vertex.getMaxValue()) {
                        v_val.remove(k);
                        if (v_val.size() == 0) {
                            is.m.remove(data);
                            if (is.m.isEmpty()) {
                                if (toremove == null) {
                                    toremove = new ArrayList<InitialState>();
                                }
                                toremove.add(is);
                            }
                        }
                        v.add(is);
                    }
                }
            }
        }

        if (toremove != null) {
            removeAll(toremove);
        }
	}

    public Object getInitState(String s) {
        for (InitialState istate: this) {
            if (istate.getName().equals(s)) {
                return istate;
            }
        }
        return null;
    }
    public void addInitState(String s, Map m) {
        for (InitialState istate: this) {
            if (istate.getName().equals(s)) {
                m.put(istate, null);
                return;
            }
        }
    }

    public void toXML(XMLWriter out, String tag) throws IOException {
        for (InitialState is: this) {
            out.openTag(tag);
            out.addAttr("name", is.name);
            String s = "";
            for (NodeInfo ni: is.m.keySet()) {
                List v_val = (List)is.m.get(ni);
                s += ni.getNodeID();
                for (int j=0 ; j<v_val.size() ; j++) {
                        s += ";"+((Integer)v_val.get(j)).intValue();
                }
                s += " ";
            }
            out.addAttr("value", s.trim());
            out.closeTag();
        }
    }

	public String nameStateInfo(byte[] state, Object[] no) {
        for (InitialState istate: this) {
            Map<NodeInfo, List<Integer>> m_istate = istate.getMap();
            boolean ok = true;
            for (int j=0 ; j<no.length ; j++) {
                List<Integer> values = m_istate.get(no[j]);
                if (values != null) {
                    ok = false;
                    int val = state[j];
                    for (int v: values) {
                        if (v == val) {
                            ok = true;
                            break;
                        }
                    }
                    if (!ok) {
                        break;
                    }
                }
            }
            if (ok) {
                return istate.getName();
            }
        }
        return null;
	}
	public String nameState(byte[] state, List<RegulatoryNode> no) {
        for (InitialState istate: this) {
            Map<NodeInfo, List<Integer>> m_istate = istate.getMap();
            boolean ok = true;
            for (int j=0 ; j<no.size() ; j++) {
                List<Integer> values = m_istate.get(no.get(j));
                if (values != null) {
                    ok = false;
                    int val = state[j];
                    for (int v: values) {
                        if (v == val) {
                            ok = true;
                            break;
                        }
                    }
                    if (!ok) {
                        break;
                    }
                }
            }
            if (ok) {
                return istate.getName();
            }
        }
        return null;
	}
}
