package org.ginsim.gui.graph.regulatorygraph.initialstate;

import org.ginsim.core.graph.objectassociation.ObjectAssociationManager;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.graph.regulatorygraph.RegulatoryNode;
import org.ginsim.core.graph.regulatorygraph.namedstates.*;

import javax.swing.*;

public class PatternSelectionPanel extends JComboBox {

    /**
     * @param lrg a model
     * @return a panel to select an input configuration, or null if the model has no inputs
     */
    public static PatternSelectionPanel getInputSelectorOrNull(RegulatoryGraph lrg, PatternHolder holder) {
        for (RegulatoryNode node: lrg.getNodeOrder()) {
            if (node.isInput()) {
                return new PatternSelectionPanel(lrg, true, holder);
            }
        }
        return null;
    }

    private final RegulatoryGraph lrg;
    private final PatternComboModel model;

    public PatternSelectionPanel(RegulatoryGraph lrg, boolean input, PatternHolder holder) {
        setBorder(BorderFactory.createTitledBorder("Input restriction"));
        this.lrg = lrg;
        this.model = new PatternComboModel(lrg, input, holder);
        setModel(model);
    }


    class PatternComboModel extends AbstractListModel<NamedState> implements ComboBoxModel<NamedState> {

        public final NamedStateList list;
        public final PatternHolder holder;

        public PatternComboModel(RegulatoryGraph lrg, boolean input, PatternHolder holder) {
            NamedStatesHandler imanager = (NamedStatesHandler) ObjectAssociationManager.getInstance().getObject(lrg, NamedStatesManager.KEY, true);
            this.list = input ? imanager.getInputConfigs() : imanager.getInitialStates();
            this.holder = holder;
        }

        @Override
        public int getSize() {
            return list.size() + 1;
        }

        @Override
        public NamedState getElementAt(int i) {
            if (i == 0) {
                return null;
            }
            return list.get(i-1);
        }

        @Override
        public void setSelectedItem(Object o) {
            if (o instanceof NamedState) {
                this.holder.setPattern((NamedState) o);
            } else {
                this.holder.setPattern(null);
            }
        }

        @Override
        public NamedState getSelectedItem() {
            return holder.getPattern();
        }
    }
}
