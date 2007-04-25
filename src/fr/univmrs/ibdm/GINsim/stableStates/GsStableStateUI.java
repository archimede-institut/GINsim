package fr.univmrs.ibdm.GINsim.stableStates;

import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import fr.univmrs.ibdm.GINsim.manageressources.Translator;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.GsRegulatoryGraph;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.OmddNode;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.ui.GsMutantCombo;
import fr.univmrs.ibdm.GINsim.util.widget.GsDialog;

public class GsStableStateUI extends GsDialog {
	private static final long serialVersionUID = -3605525202652679586L;
	
	GsRegulatoryGraph graph;
	GsMutantCombo comboMutant;
	JPanel mutantPanel;
	JPanel buttonPanel;
	JButton butRun;
	JButton butClose;
	JTextArea l_result;
	
	public GsStableStateUI(GsRegulatoryGraph graph) {
		super(graph.getGraphManager().getMainFrame(), "display.stableStates", 200, 100);
		this.graph = graph;
		Container panel = getContentPane();
		panel.setLayout(new GridBagLayout());
		
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		panel.add(getMutantPanel(), c);

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

		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 2;
		panel.add(getButtonPanel(), c);
	}
	
	private JPanel getMutantPanel() {
		if (mutantPanel == null) {
			mutantPanel = new JPanel();
			mutantPanel.add(new JLabel(Translator.getString("STR_mutants")));

			comboMutant = new GsMutantCombo(graph);
			mutantPanel.add(comboMutant);
		}
		return mutantPanel;
	}
	
	private JPanel getButtonPanel() {
		if (buttonPanel == null) {
			buttonPanel = new JPanel();

			butClose = new JButton(Translator.getString("STR_close"));
			buttonPanel.add(butClose);
			butClose.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					doClose();
				}
			});

			butRun = new JButton(Translator.getString("STR_run"));
			buttonPanel.add(butRun);
			butRun.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					doStart();
				}
			});
		}
		return buttonPanel;
	}
	
	protected void doStart() {
		butRun.setEnabled(false);
		butClose.setEnabled(false);
		new GsSearchStableStates(graph, comboMutant.getMutant(), this).start();
	}
	
	protected void setResult(OmddNode stable) {
		int[] state = new int[graph.getNodeOrder().size()];
		for (int i=0 ; i<state.length ; i++) {
			state[i] = -1;
		}
		StringBuffer s = new StringBuffer();
		findStableState(state, stable, s);
		l_result.setText(s.toString());
		butRun.setEnabled(true);
		butClose.setEnabled(true);
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
