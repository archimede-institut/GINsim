package org.ginsim.gui.utils.data;

import java.util.List;

/**
 * Encapsulate a list and its helper to allow editing it as a generic property
 *
 * @author Aurelien Naldi
 */
public class HelpedList {

    public final ListPanelHelper helper;
    public List list;

    public HelpedList(ListPanelHelper helper) {
        this.helper = helper;
    }
}
