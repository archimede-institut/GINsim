package org.ginsim.service.tool.reg2dyn.priorityclass;

/**
 * Default modes to add a new priority set
 *
 * @author Aurelien Naldi
 */
public enum PrioritySetAddMode {

    SIMPLE("One unique class"),
    SPLIT("One class for each node"),
    FINE_GRAINED("Splitting transitions – one unique class");

    private final String descr;

    private PrioritySetAddMode(String s) {
        this.descr = s;
    }
    public String toString() {
        return descr;
    }
}
