package org.ginsim.common.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Basic tools for common list operations.
 *
 * @author Aurelien Naldi
 */
public class ListTools {

    /**
     * Create a list based on an array
     *
     * @param t the array we want to convert to vector
     * @return the new Vector
     */
    public static <T> List<T> getListFromArray(T[] t) {

        List<T> list = new ArrayList<T>(t.length);

        for (int i = 0; i < t.length; i++) {
            list.add(t[i]);
        }
        return list;
    }


    /**
     * Move some items in a list: swap position with other items.
     *
     * @param l
     * @param sel
     * @param diff
     *
     * @return true if the move was possible.
     */
    public static boolean moveItems(List l, int[] sel, int diff) {
        if (diff == 0 || sel == null || sel.length == 0 ||
                diff < 0 && sel[0] <= -(diff+1) ||
                diff > 0 && sel[sel.length-1] >= l.size() - diff) {
            return false;
        }
        if (diff > 0) {
            doMoveDown(l, sel, diff);
        } else {
            doMoveUp(l, sel, diff);
        }
        return true;
    }

    private static void doMoveUp(List l, int[] sel, int diff) {
        for (int i=0 ; i<sel.length ; i++) {
            int a = sel[i];
            if (a >= diff) {
                moveElement(l, a, a+diff);
                sel[i] += diff;
            }
        }
    }
    private static void doMoveDown(List l, int[] sel, int diff) {
        for (int i=sel.length-1 ; i>=0 ; i--) {
            int a = sel[i];
            if (a < l.size()+diff) {
                moveElement(l, a, a+diff);
                sel[i] += diff;
            }
        }
    }

    protected static boolean moveElement(List l, int src, int dst) {
        if (src < 0 || dst < 0 || src >= l.size() || dst >= l.size()) {
            return false;
        }
        Object o = l.remove(src);
        l.add(dst, o);
        return true;
    }

}
