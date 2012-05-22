package org.ginsim.common.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.ginsim.common.application.LogManager;

/**
 * Simple description of a file format.
 * Each format has an identifier and an extension.
 * 
 * A list of defined format is automatically maintained and can be used to retrieve a format by ID or by extension.
 * "Groups" of related formats can also be created by subclassing (the class will be used as group identifier).
 * 
 * @author Aurelien Naldi
 */
public class FileFormatDescription {

	private static List<FileFormatDescription> ALL_FORMATS = new ArrayList<FileFormatDescription>();
	
	public final String id;
	public final String extension;
	
	public FileFormatDescription(String id, String extension) {
		this.id = id;
		
		this.extension = extension;

		if (ALL_FORMATS.contains(this)) {
			LogManager.error("Format redefinition or conflict: "+ this);
		} else {
			ALL_FORMATS.add(this);
		}
	}
	
	@Override
	public boolean equals(Object o) {
		if (o.getClass() != getClass()) {
			return false;
		}
		
		FileFormatDescription other = (FileFormatDescription)o;
		return other.id .equals(this.id);
	}
	
	@Override
	public String toString() {
		return id;
	}

	/**
	 * Retrieve a format by id.
	 * @param id a format identifier
	 * @return the Format corresponding to this identifier
	 */
	public static FileFormatDescription getFormat(String id) {
		return getFormat( FileFormatDescription.class, id);
	}
	
	/**
	 * Retrieve a subclassing format by ID.
	 * 
	 * @param cl
	 * @param id
	 * @return the corresponding format
	 */
	public static <C extends FileFormatDescription> C getFormat( Class<C> cl, String id) {
		for (FileFormatDescription d: ALL_FORMATS) {
			if (d.getClass() == cl && d.id == id) {
				return (C)d;
			}
		}
		return null;
	}

	/**
	 * Retrieve all formats of a given subclass.
	 * 
	 * @param cl
	 * @return the list of matching formats
	 */
	public static <C extends FileFormatDescription> Collection<C> getFormats( Class<C> cl) {
		List<C> l = null;
		for (FileFormatDescription d: ALL_FORMATS) {
			if (d.getClass() == cl) {
				if (l == null) {
					l = new ArrayList<C>();
				}
				l.add((C)d);
			}
		}
		return l;
	}
}
