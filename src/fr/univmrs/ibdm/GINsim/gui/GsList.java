package fr.univmrs.ibdm.GINsim.gui;

import java.util.Vector;

/**
 * generic interface for list of objects, a common UI will allow to sho it,
 * and eventually offer generic services: reordering, adding/removing objects...
 */
public interface GsList {

    /**
     * can elements of this list be reordered interactively ?
     * @return true if it is reorder-friendly
     */
    public boolean canOrder();
    /**
     * can elements of this list be edited interactively ?
     * @return true if one can edit elements
     */
    public boolean canEdit();
    /**
     * can elements of this list be added interactively ?
     * @return true if one can add elements
     */
    public boolean canAdd();
    /**
     * can elements of this list be added by copy ?
     * @return true if one can copy elements
     */
    public boolean canCopy();
    /**
     * can elements of this list be removed interactively ?
     * @return true if one can remove elements
     */
    public boolean canRemove();
    
    /**
     * @return the size of the list
     */
    public int getNbElements();
    
    /**
     * @param i
     * @return the ith element of the list
     */
    public Object getElement(int i);
    
    /**
     * if this list can deal with object of several types,
     * @return the list of available types, or null if none.
     */
    public Vector getObjectType();
    
    /**
     * edit an element.
     * @param i the suggested position
     * @param o the result of the edit
     * @return true if the list has changed
     */
    public boolean edit(int i, Object o);
    
    /**
     * add an element.
     * @param i the suggested position
     * @param type (index of the object type to add)
     * @return the index of the added element, or -1 if none
     */
    public int add(int i, int type);
    
    /**
     * add an element, by copying an existing one.
     * @param i the suggested position
     * @return the index of the added element, or -1 if none
     */
    public int copy(int i);
    
    /**
     * add an element.
     * @param t_index the elements to remove
     * @return true if the list has changed
     */
    public boolean remove(int[] t_index);
    
    /**
     * move an element.
     * @param src the element to move
     * @param dst its new position
     * @return true if the list has changed
     */
    public boolean moveElement(int src, int dst);
}
