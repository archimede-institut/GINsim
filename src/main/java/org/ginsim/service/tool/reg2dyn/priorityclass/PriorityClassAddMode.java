package org.ginsim.service.tool.reg2dyn.priorityclass;

/**
 * Created by aurelien on 1/23/14.
 */
public enum PriorityClassAddMode {

    SIMPLE("One unique class"),
    SPLIT("One class for each node"),
    FINE_GRAINED("Splitting transitions – one unique class");

    private final String descr;

    private PriorityClassAddMode(String s) {
        this.descr = s;
    }
    public String toString() {
        return descr;
    }
}
