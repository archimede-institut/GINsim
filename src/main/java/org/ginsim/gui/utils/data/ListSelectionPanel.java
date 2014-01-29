package org.ginsim.gui.utils.data;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;

import org.ginsim.common.application.Txt;
import org.ginsim.gui.utils.dialog.stackdialog.StackDialog;


abstract public class ListSelectionPanel<T> extends JPanel implements ActionListener {

	protected final StackDialog dialog;
	private ListComboModel<T> comboModel;
	
	protected ListSelectionPanel(StackDialog dialog, String name) {
		this.dialog = dialog;
		
		setLayout(new GridBagLayout());
		if (name != null) {
			setBorder(BorderFactory.createTitledBorder(name));
		}
	}
	
	protected void initialize(String name, boolean hasEmptyChoice) {
		initialize(name, hasEmptyChoice, Txt.t("STR_configure"), null);
	}
	
	protected void initialize(String name, boolean hasEmptyChoice, String s_config, String s_config_tooltip) {
		
		comboModel = new ListComboModel<T>(this, getList(), hasEmptyChoice);
		JComboBox combo = new JComboBox(comboModel);
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1;
		add(combo, c);

		if (dialog != null) {
	        JButton but_configure = new JButton(s_config);
	        but_configure.setToolTipText(s_config_tooltip);
	        but_configure.addActionListener(this);
	        c = new GridBagConstraints();
			c.gridx = 1;
			c.gridy = 0;
	        add(but_configure, c);
		}
	}
	
	public void actionPerformed(ActionEvent e) {
		configure();
	}

	public void refresh() {
		comboModel.refresh(getList());
	}
	
	abstract protected List<T> getList();
	
	abstract public void configure();

	abstract public T getSelected();
	
	abstract public void setSelected(T sel);
	
}

class ListComboModel<T> extends DefaultComboBoxModel implements ComboBoxModel {
    private static final long serialVersionUID = 2348678706086666489L;
    
    private List<T> list;
    private final ListSelectionPanel<T> panel;
    private final boolean hasEmptyChoice;
    int id;
    
    public ListComboModel(ListSelectionPanel<T> panel, List<T> list, boolean hasEmptyChoice) {
    	this.hasEmptyChoice = hasEmptyChoice;
    	this.panel = panel;
    	refresh(list);
    }
    

    public void refresh(List<T> list) {
    	this.list = list;
    	fireContentsChanged(this, 0, getSize());
    }
    
    public Object getSelectedItem() {
    	T selected = panel.getSelected();
    	if (selected == null) {
    		return "--";
    	}
    	return selected;
    }

    public void setSelectedItem(Object anItem) {
    	if (list == null) {
    		return;
    	}
    	
        if (list.indexOf(anItem) != -1) {
        	panel.setSelected((T)anItem);
        } else {
        	panel.setSelected(null);
        }
        fireContentsChanged(this, 0, getSize());
    }

    public Object getElementAt(int index) {
    	if (index == 0 && hasEmptyChoice || list == null) {
            return "--";
        }
        if (hasEmptyChoice) {
        	return list.get(index-1);
        }
    	return list.get(index);
    }

    public int getSize() {
        if (list == null) {
        	if (hasEmptyChoice) {
        		return 1;
        	}
            return 0;
        }
        
        if (hasEmptyChoice) {
        	return list.size()+1;
        }
    	return list.size();
    }
}
