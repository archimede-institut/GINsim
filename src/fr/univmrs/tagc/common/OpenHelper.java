package fr.univmrs.tagc.common;


public interface OpenHelper {
	public boolean open(String proto, String value);
	public void add(String proto, String value);
	public String getLink(String proto, String value);
}
