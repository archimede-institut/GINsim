package fr.univmrs.tagc.common;

/**
 * Run timeouts and call back when elapsed
 */
public class Timeout {

    /**
     * add a new timeout waiter object
     * @param o
     * @param wait length of the timeout (milliseconds)
     */
    public static void addTimeout(TimeoutObject o, long wait) {
        Thread t = new cl_runtimeout(o, wait);
        t.start();
    }
}

class cl_runtimeout extends Thread {

    TimeoutObject obj;
    long wait;
    
    protected cl_runtimeout(TimeoutObject obj, long wait) {
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
