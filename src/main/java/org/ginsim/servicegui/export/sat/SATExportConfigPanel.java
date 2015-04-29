package org.ginsim.servicegui.export.sat;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import org.colomoto.logicalmodel.LogicalModel;
import org.ginsim.common.application.Txt;
import org.ginsim.gui.graph.regulatorygraph.initialstate.InitialStatePanel;
import org.ginsim.gui.utils.dialog.stackdialog.LogicalModelActionDialog;
import org.ginsim.service.export.sat.SATConfig;

public class SATExportConfigPanel extends LogicalModelActionDialog {
	private static final long serialVersionUID = -7398674287463858306L;

	private final SATConfig config;
	private final SATExportAction action;

	private JPanel mainPanel;
	private InitialStatePanel initPanel;
	private boolean isInterv;

	public SATExportConfigPanel(SATConfig config, SATExportAction action) {
		super(config.getGraph(), null, Txt.t("STR_SAT"), 600, 400);
		this.setTitle(Txt.t("STR_SATRunningTitle"));
		setUserID(Txt.t("STR_SAT"));
		this.config = config;
		this.action = action;
		this.isInterv = false;

		mainPanel = new JPanel(new BorderLayout());

		JPanel stateInputsPanel = new JPanel(new BorderLayout());
		stateInputsPanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("State inputs"),
				stateInputsPanel.getBorder()));

		// NORTH begin
		JPanel typePanel = new JPanel(new FlowLayout());
		typePanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder(Txt.t("STR_SAT_Type")),
				typePanel.getBorder()));
		JRadioButton jrbSSs = new JRadioButton(Txt.t("STR_SAT_Type_SSs"), true);
		jrbSSs.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JRadioButton jb = (JRadioButton) e.getSource();
				isInterv = !jb.isSelected();
			}
		});
		JRadioButton jrbInterv = new JRadioButton(Txt.t("STR_SAT_Type_Interv"));
		jrbInterv.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JRadioButton jb = (JRadioButton) e.getSource();
				isInterv = jb.isSelected();
			}
		});
		ButtonGroup bg = new ButtonGroup();
		bg.add(jrbSSs);
		bg.add(jrbInterv);
		typePanel.add(jrbSSs);
		typePanel.add(jrbInterv);
		mainPanel.add(typePanel, BorderLayout.NORTH);

		// CENTER begin
		initPanel = new InitialStatePanel(config.getGraph(), false);
		initPanel.setParam(config);
		mainPanel.add(initPanel, BorderLayout.CENTER);

		setMainPanel(mainPanel);
	}

	@Override
	public void run(LogicalModel model) {
		config.updateModel(model);
		config.setIsIntervention(this.isInterv);
		action.selectFile();
		cancel();
	}
}