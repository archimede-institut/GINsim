package fr.univmrs.tagc.common.datastore;

public interface NamedObject {
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
