package fr.univmrs.tagc.GINsim.regulatoryGraph.logicalfunction.neweditor.qmc;

import java.util.Vector;
import java.util.List;
import javax.swing.JProgressBar;
import fr.univmrs.tagc.common.widgets.GsButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class QMCAlgo implements ActionListener {
	private ParameterGroupS groups;
	private String function;
	private boolean cnf;
	private JProgressBar progressBar;
	private GsButton cancelButton;
	private boolean toKill = false;

	public QMCAlgo(boolean cnf, JProgressBar jpb, GsButton b) {
		super();
		this.cnf = cnf;
		progressBar = jpb;
		cancelButton = b;
	}
	public void init(List interactions, List parameters) {
		groups = new ParameterGroupS(interactions, parameters, cnf, this);
		function = "";
	}
	public void exec() {
		cancelButton.addActionListener(this);
		cancelButton.setEnabled(true);
		if (progressBar != null) progressBar.setMaximum(groups.size() - 1);
		while (groups.size() > 1) {
			groups.nextGroupS();
			if (progressBar != null) progressBar.setValue(progressBar.getMaximum() - groups.size() + 1);
			if (toKill) break;
		}
		if (!toKill) {
			groups.setPremier();
			MCArray mcArray = new MCArray(groups.getPremierTerms(), groups.getNbParameters());
			mcArray.run();
			Vector v = mcArray.getBaseParameters();
			if (v.size() > 0) {
				function = ((LogicalParameter)v.firstElement()).getStringParameter(cnf);
				for (int i = 1; i < v.size(); i++)
					function += (cnf ? " & " : " | ") + ((LogicalParameter)v.elementAt(i)).getStringParameter(cnf);
			}
		}
		if (progressBar != null) progressBar.setValue(0);
		cancelButton.removeActionListener(this);
	}
	public String getFunction() {
		return function;
	}
	public String getString() {
		return groups.getString();
	}
	public int getNbPremier() {
		return groups.getPremierTerms().size();
	}
	public void actionPerformed(ActionEvent e) {
		toKill = true;
	}
	public boolean shouldKill() {
		return toKill;
	}
}
