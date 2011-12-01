package org.ginsim.gui.service.tool.circuit;


public class FunctionalityContext {

	public CircuitDescr circuitDesc;
	public int contextIndex;

	public FunctionalityContext(CircuitDescr circuitDesc, int contextIndex) {
		this.circuitDesc = circuitDesc;
		this.contextIndex = contextIndex;
	}
	
	public OmsddNode getContext() {
		return circuitDesc.getContext()[contextIndex];
	}

	public String toString() {
		StringBuffer s = new StringBuffer();
		for (int i = 0; i < circuitDesc.t_vertex.length-1; i++) {
			s.append(circuitDesc.t_vertex[i]+", ");
		}
		s.append(circuitDesc.t_vertex[circuitDesc.t_vertex.length-1]);
		if (contextIndex != 0 || ( circuitDesc.t_context.length > 1 && circuitDesc.t_context[1] != OmsddNode.FALSE)) {
			s.append(" [");
			s.append(contextIndex);
			s.append("]");
		}
		return s.toString();
	}

}