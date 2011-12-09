package org.ginsim.common.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.ginsim.core.utils.log.LogManager;

/**
 * Simple description of a file format.
 * 
 * A format is identified by a "group" and identifier. Beside this, it provides a name and file extension
 * 
 * @author Aurelien Naldi
 */
public class FileFormatDescription {

	private static List<FileFormatDescription> ALL_FORMATS = new ArrayList<FileFormatDescription>();
	
	public final Object group;
	public final String id;
	public final String name;
	public final String extension;
	
	public FileFormatDescription(String id, String name, String extension) {
		this(null, id, name, extension);
	}

	public FileFormatDescription(String group, String id, String name, String extension) {
		this.group = group;
		this.id = id;
		
		this.name = name;
		this.extension = extension;

		if (ALL_FORMATS.contains(this)) {
			LogManager.error("Format redefinition or conflict: "+ this);
		} else {
			ALL_FORMATS.add(this);
		}
	}
	
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof FileFormatDescription)) {
			return false;
		}
		FileFormatDescription other = (FileFormatDescription)o;
		return other.group == this.group && other.id .equals(this.id);
	}
	
	@Override
	public String toString() {
		return name;
	}
	
	public static FileFormatDescription getFormat(String id) {
		return getFormat(null, id);
	}
	public static FileFormatDescription getFormat(Object group, String id) {
		for (FileFormatDescription d: ALL_FORMATS) {
			if (d.group == group && d.id == id) {
				return d;
			}
		}
		return null;
	}

	public static Collection<FileFormatDescription> getFormats(Object group) {
		List<FileFormatDescription> l = null;
		for (FileFormatDescription d: ALL_FORMATS) {
			if (d.group == group) {
				if (l == null) {
					l = new ArrayList<FileFormatDescription>();
				}
				l.add(d);
			}
		}
		return l;
	}
}
