package fr.univmrs.ibdm.GINsim.global;

/**
 * Run timeouts and call back when elapsed
 */
public class GsTimeout {

    /**
     * add a new timeout waiter object
     * @param o
     * @param wait length of the timeout (milliseconds)
     */
    public static void addTimeout(GsTimeoutObject o, long wait) {
        Thread t = new cl_runtimeout(o, wait);
        t.start();
    }
}

class cl_runtimeout extends Thread {

    GsTimeoutObject obj;
    long wait;
    
    protected cl_runtimeout(GsTimeoutObject obj, long wait) {
        super();
        this.obj = obj;
        this.wait = wait;
    }

    public void run() {
        try {
            sleep(wait);
            obj.timeout();
        } catch (InterruptedException e) {
        }
    }
}
