package fr.univmrs.ibdm.GINsim.stableStates;

import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import fr.univmrs.ibdm.GINsim.gui.GsStackDialog;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.GsRegulatoryGraph;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.OmddNode;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.mutant.MutantSelectionPanel;

public class GsStableStateUI extends GsStackDialog {
	private static final long serialVersionUID = -3605525202652679586L;
	
	GsRegulatoryGraph graph;
	MutantSelectionPanel mutantPanel;
	JPanel buttonPanel;
	JTextArea l_result;
	
	public GsStableStateUI(GsRegulatoryGraph graph) {
		super(graph.getGraphManager().getMainFrame(), "display.stableStates", 200, 100);
		this.graph = graph;
		Container panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		mutantPanel = new MutantSelectionPanel(this, graph);
		panel.add(mutantPanel, c);

		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 1;
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1;
		c.weighty = 1;
		l_result = new JTextArea();
		l_result.setEditable(false);
		JScrollPane sp = new JScrollPane();
		sp.setViewportView(l_result);
		panel.add(sp, c);
		
		setMainPanel(panel);
	}
	
	protected void run() {
		setRunning(true);
		new GsSearchStableStates(graph, mutantPanel.getMutant(), this).start();
	}
	
	protected void setResult(OmddNode stable) {
		int[] state = new int[graph.getNodeOrder().size()];
		for (int i=0 ; i<state.length ; i++) {
			state[i] = -1;
		}
		StringBuffer s = new StringBuffer();
		findStableState(state, stable, s);
		l_result.setText(s.toString());
		setRunning(false);
	}
	
	private void findStableState(int[] state, OmddNode stable, StringBuffer s) {
		if (stable.next == null) {
			if (stable.value == 1) {
				// we have a stable state:
				s.append("stable: ");
				for (int i=0 ; i<state.length ; i++) {
					s.append((state[i] != -1 ? ""+state[i] : "*") +" ");
				}
				s.append("\n");
			}
			return;
		}
		for (int i=0 ; i<stable.next.length ; i++) {
			state[stable.level] = i;
			findStableState(state, stable.next[i], s);
		}
		state[stable.level] = -1;
	}

	
	public void doClose() {
		setVisible(false);
		dispose();
	}
}
