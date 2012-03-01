package org.ginsim.gui.shell;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.ginsim.common.OptionStore;
import org.ginsim.common.exception.GsException;
import org.ginsim.core.graph.GraphManager;
import org.ginsim.core.graph.common.Graph;
import org.ginsim.gui.GUIManager;
import org.ginsim.gui.resource.ImageLoader;
import org.ginsim.gui.shell.callbacks.FileCallBack;

public class StartupDialog extends JFrame {
	private static final long serialVersionUID = 2935330158118845149L;

	public StartupDialog() {
		super("GINsim");
		setSize(500, 500);
		setMinimumSize(getSize());
		setResizable(false);
		JPanel content = new JPanel(new GridBagLayout());
		setContentPane(content);
		
		JPanel header = new JPanel();
		Image logo = ImageLoader.getImage("gs1.gif");
		JLabel logoLabel = new JLabel(new ImageIcon( logo ));
		header.add(logoLabel);
		JLabel title = new JLabel("Welcome to GINsim");
		header.setBackground(Color.DARK_GRAY);
		title.setForeground(Color.WHITE);
		title.setFont(new Font("Serif", Font.BOLD, 28));
		header.add(title);

		GridBagConstraints cst = new GridBagConstraints();
		cst.fill = GridBagConstraints.HORIZONTAL;
		cst.anchor = GridBagConstraints.NORTH;
		cst.gridwidth = 3;
		cst.weightx = 1;
		content.add(header, cst);
		
		
		JPanel panel = new JPanel( new GridBagLayout());
		panel.setBorder( BorderFactory.createTitledBorder("Start with ..."));
		cst = new GridBagConstraints();
		cst.fill = GridBagConstraints.HORIZONTAL;
		cst.gridy = 1;
		cst.weightx = 1;
		content.add(panel, cst);
		
		cst = new GridBagConstraints();
		cst.gridx = 0;
		cst.gridy = 0;
		panel.add(new JButton( new ActionNew(this)), cst);
		
		cst = new GridBagConstraints();
		cst.gridx = 1;
		cst.gridy = 0;
		panel.add(new JButton( new ActionOpen(this)), cst);
		
		panel = new JPanel( new GridBagLayout());
		panel.setBorder( BorderFactory.createTitledBorder("Recent files"));
		cst = new GridBagConstraints();
		cst.fill = GridBagConstraints.BOTH;
		cst.gridy = 2;
		cst.gridx = 0;
		cst.weightx = 1;
		cst.weighty = 1;
		content.add(panel, cst);
		
		int gridx = 0;
		int gridy = 0;
		for (Action action: FileCallBack.getActionsRecent()) {
			cst = new GridBagConstraints();
			cst.gridx = gridx++;
			cst.gridy = gridy;
			cst.fill = GridBagConstraints.HORIZONTAL;
			if (gridx > 1) {
				gridy++;
				gridx = 0;
			}
			panel.add(new JButton(action), cst);
		}
		
		panel = new JPanel( new GridBagLayout());
		panel.setBorder( BorderFactory.createTitledBorder("Help"));
		cst = new GridBagConstraints();
		cst.fill = GridBagConstraints.HORIZONTAL;
		cst.gridy = 3;
		cst.gridx = 0;
		cst.weightx = 1;
		content.add(panel, cst);
		
		panel.add( new JLabel("TODO"));
		
		cst = new GridBagConstraints();
		cst.gridy = 4;
		cst.gridx = 0;
		cst.anchor = GridBagConstraints.SOUTHEAST;
		content.add(new JButton( new ActionQuit(this)), cst);
		
		this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent evt) {
				close();
			}
		});
		setVisible(true);
	}

	public void close() {
		OptionStore.saveOptions();
		System.exit(0);
	}

}

class ActionNew extends AbstractAction {
	private final StartupDialog dialog;
	
	public ActionNew(StartupDialog dialog) {
		super("New regulatory graph");
		this.dialog = dialog;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		GUIManager.getInstance().newFrame();
		dialog.dispose();
	}
}

class ActionOpen extends AbstractAction {
	private final StartupDialog dialog;
	
	public ActionOpen(StartupDialog dialog) {
		super("Open");
		this.dialog = dialog;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String filename = FileSelectionHelper.selectOpenFilename(dialog);
		if (filename == null) {
			return;
		}
		
		try {
			Graph<?, ?> graph = GraphManager.getInstance().open(filename);
			GUIManager.getInstance().newFrame();
			dialog.dispose();
		} catch (GsException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
}

class ActionQuit extends AbstractAction {
	private final StartupDialog dialog;
	
	public ActionQuit(StartupDialog dialog) {
		super("Quit");
		this.dialog = dialog;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		dialog.dispose();
	}
}
