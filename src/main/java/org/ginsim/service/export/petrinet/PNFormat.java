package org.ginsim.service.export.petrinet;

import org.colomoto.logicalmodel.LogicalModel;
import org.ginsim.common.utils.FileFormatDescription;

abstract public class PNFormat {

	private final String name, filterDescr, extension;
	
	public PNFormat(String name, String extension) {
		this(name, name, extension);
	}
	
	public PNFormat(String name, String filterDescr, String extension) {
		this.name = name;
		this.filterDescr = filterDescr;
		this.extension = extension;
	}
	
	abstract public BasePetriNetExport getWriter( LogicalModel model);
	
	public FileFormatDescription getFormatDescription() {
		return new FileFormatDescription(filterDescr, extension);
	}

	public String getName() {
		return name;
	}
	
}