package fr.univmrs.ibdm.GINsim.reg2dyn;


/**
 * a priority class for the simulation.
 * each class has a name, a simulation mode and a list of elements
 */
public class GsReg2dynPriorityClass {
    
    /** this class is synchronous */
    public static final int SYNCHRONOUS = 0;
    /** this class is asynchronous */
    public static final int ASYNCHRONOUS = 1;
    /** rank of this class */
    public int rank;
    
    private String name;
    private int mode;
    
    /**
     * 
     */
    public GsReg2dynPriorityClass() {
        this(0);
    }
    /**
     * @param priority
     */
    public GsReg2dynPriorityClass(int priority) {
        this.mode = ASYNCHRONOUS;
        this.rank = priority;
        name = "new class";
    }
    
    public String toString() {
        return rank+": "+name + " ; "+((mode==ASYNCHRONOUS) ? "async" : "sync");
    }
    
    /**
     * change the simulation mode for this class
     * 
     * @param mode
     */
    public void setMode(int mode) {
        if (mode == SYNCHRONOUS || mode == ASYNCHRONOUS) {
            this.mode = mode;
        }
    }

    /**
     * @return the simulation mode for this class
     */
    public int getMode() {
        return mode;
    }
    
    /**
     * change this class' name.
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * @return the name of this priority class
     */
    public String getName() {
        return name;
    }
    
    public Object clone() {
    	GsReg2dynPriorityClass clone = new GsReg2dynPriorityClass();
    	clone.mode = mode;
    	clone.name = name;
    	clone.rank = rank;
    	return clone;
    }
    
}
