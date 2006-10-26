package fr.univmrs.ibdm.GINsim.global;

public interface GsNamedObject {
    /**
     * @return the name of this object
     */
    public String getName();
    /**
     * change the name of this object
     * @param name: the new name
     */
    public void setName(String name);
}
