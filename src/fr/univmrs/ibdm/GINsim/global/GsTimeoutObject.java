package fr.univmrs.ibdm.GINsim.global;

/**
 * Objects waiting for a timeout
 */
public interface GsTimeoutObject {

    /**
     * the timeout is elapsed
     */
    public void timeout();
}
