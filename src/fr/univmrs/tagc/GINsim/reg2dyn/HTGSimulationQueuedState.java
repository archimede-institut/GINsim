package fr.univmrs.tagc.GINsim.reg2dyn;

public class HTGSimulationQueuedState {

	public byte[] state;
	public int index;
	public int low_index;
	public SimulationUpdater updater;
	
	public HTGSimulationQueuedState(byte[] state, int index, int low_index, SimulationUpdater updater) {
		this.state = state;
		this.index = index;
		this.low_index = low_index;
		this.updater = updater;
	}

	public String toString() {
		return "["+print_state(state)+", i:"+index+", li:"+low_index+"]";
	}
	
	public static String print_state(byte[] t) {
		StringBuffer s = new StringBuffer();
		for (int i = 0 ; i < t.length ; i++){
			s.append(""+t[i]);
		}
		return s.toString();
	}
}
