package org.ginsim.servicegui.export.sat;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import org.ginsim.common.application.Translator;
import org.ginsim.gui.graph.regulatorygraph.initialstate.InitialStatePanel;
import org.ginsim.gui.utils.dialog.stackdialog.AbstractStackDialogHandler;
import org.ginsim.service.export.sat.SATConfig;

public class SATExportConfigPanel extends AbstractStackDialogHandler {
	private static final long serialVersionUID = 5286016801445949364L;

	private final SATConfig config;
	private final SATExportAction action;
	private InitialStatePanel initPanel;

	private JRadioButton jrb2;

	public SATExportConfigPanel(SATConfig config, SATExportAction action) {
		this.config = config;
		this.action = action;
	}

	@Override
	protected void init() {
		setLayout(new BorderLayout());

		JPanel jpType = new JPanel();
		jpType.setLayout(new BoxLayout(jpType, BoxLayout.PAGE_AXIS));
		jpType.setBorder(BorderFactory.createTitledBorder(Translator
				.getString("STR_SAT_Type")));
		JRadioButton jrb1 = new JRadioButton(
				Translator.getString("STR_SAT_Type1"));
		jrb1.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				changeExportType();
			}
		});
		jrb2 = new JRadioButton(Translator.getString("STR_SAT_Type2"));
		jrb2.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				changeExportType();
			}
		});

		ButtonGroup group = new ButtonGroup();
		group.add(jrb1);
		group.add(jrb2);
		jrb1.setSelected(true);
		jpType.add(jrb1);
		jpType.add(jrb2);
		add(jpType, BorderLayout.NORTH);

		initPanel = new InitialStatePanel(config.getGraph(), false);
		initPanel.setParam(config);
		add(initPanel, BorderLayout.CENTER);
	}

	private void changeExportType() {
		int type = (jrb2.isSelected()) ? SATConfig.CFG_SCC
				: SATConfig.CFG_FIX_POINT;
		this.config.setExportType(type);
	}

	@Override
	public boolean run() {
		action.selectFile();
		return true;
	}
}