package fr.univmrs.ibdm.GINsim.regulatoryGraph;

import javax.swing.JComboBox;
import javax.swing.JPanel;

import fr.univmrs.ibdm.GINsim.graph.GsGraphOptionPanel;
import fr.univmrs.ibdm.GINsim.manageressources.Translator;

/**
 * offer save option for regulatory graphs 
 */
public class GsRegulatoryGraphOptionPanel extends JPanel implements
		GsGraphOptionPanel {

	private static final long serialVersionUID = 4585614812066176148L;
	private JComboBox comboBox;
	
	/**
	 * create the save option panel for ginml based graphs
	 * @param b
	 */
	public GsRegulatoryGraphOptionPanel (boolean b) {
		this.add(getComboBox());
	    if (b) {
	        comboBox.setSelectedIndex(2);
	    } else {
	        comboBox.setSelectedIndex(0);
	    }
	}
	
	/**
	 * @see fr.univmrs.ibdm.GINsim.graph.GsGraphOptionPanel#getSaveMode()
	 */
	public int getSaveMode() {
		return getComboBox().getSelectedIndex();
	}

	
	private JComboBox getComboBox() {
		if (comboBox == null) {
			comboBox = new JComboBox();
			comboBox.addItem(Translator.getString("STR_saveNone"));
			comboBox.addItem(Translator.getString("STR_savePosition"));
			comboBox.addItem(Translator.getString("STR_saveComplet"));
			
			comboBox.setSelectedIndex(1);
		}
		return comboBox;
	}
}
