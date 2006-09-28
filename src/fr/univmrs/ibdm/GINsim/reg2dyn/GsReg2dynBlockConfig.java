package fr.univmrs.ibdm.GINsim.reg2dyn;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import fr.univmrs.ibdm.GINsim.gui.GsJTable;
import fr.univmrs.ibdm.GINsim.manageressources.Translator;

/**
 * Configure gene state blocking
 */
public class GsReg2dynBlockConfig extends JDialog {

	private static final long serialVersionUID = -7398674287463858306L;
	private JTable blockTable;

    private JScrollPane jsp;
    private JPanel contentPane;
    private GsReg2dynBlockModel model;
    private JButton but_close = null;
    
	/**
	 * @param frame
	 * @param nodeOrder
	 * @param minBlock
	 * @param maxBlock
	 */
	public GsReg2dynBlockConfig(JFrame frame, Vector nodeOrder, int[] minBlock, int[] maxBlock) {
	    super(frame);
        model = new GsReg2dynBlockModel(nodeOrder, minBlock, maxBlock);
		initialize();
	}

	private void initialize() {
		this.setSize(150, 250);
		this.setTitle(Translator.getString("STR_configureStateBlocker"));
		this.setContentPane(getContentPanel());
        setVisible(true);
	}
	
	private JTable getBlockTable() {
		if (blockTable == null) {
			blockTable = new GsJTable();
			blockTable.setModel(model);
		}
		return blockTable;
	}
	
    private JPanel getContentPanel() {
        if (contentPane == null) {
            contentPane = new JPanel();
            contentPane.setLayout(new GridBagLayout());
            
            GridBagConstraints c_sp = new GridBagConstraints();
            GridBagConstraints c_bc = new GridBagConstraints();
            
            c_sp.gridx = 0;
            c_sp.gridy = 0;
            c_sp.weightx = 1;
            c_sp.weighty = 1;
            c_sp.fill = GridBagConstraints.BOTH;
            c_bc.gridx = 0;
            c_bc.gridy = 1;
            c_bc.anchor = GridBagConstraints.EAST;
            
            contentPane.add(getJsp(), c_sp);
            contentPane.add(getButClose(), c_bc);
        }
        return contentPane;
    }

    private JButton getButClose() {
        if (but_close == null) {
            but_close = new JButton(Translator.getString("STR_close"));
            but_close.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    setVisible(false);
                }
            });
        }
        return but_close;
    }

    private JScrollPane getJsp() {
        if (jsp == null) {
            jsp = new JScrollPane();
            jsp.setViewportView(getBlockTable());
        }
        return jsp;
    }

    /**
     * refresh the state blocking.
     * @param nodeOrder
     * @param minBlock
     * @param maxBlock
     */
    public void refresh(Vector nodeOrder, int[] minBlock, int[] maxBlock) {
        model.refresh(nodeOrder, minBlock, maxBlock);
    }
}
