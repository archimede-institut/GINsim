package org.ginsim.service.format;

import org.colomoto.biolqm.io.truthtable.LogicalTruthTableFormat;
import org.ginsim.core.service.*;
import org.kohsuke.MetaInfServices;

/**
 * GINsim import service for the extended TruthTable Format.
 * 
 * @author Aurelien Naldi
 */
@MetaInfServices(Service.class)
@Alias("LogicalTruthTable")
@ServiceStatus(EStatus.RELEASED)
public class LogicalTruthTableFormatService extends FormatSupportService<LogicalTruthTableFormat> {

	public LogicalTruthTableFormatService() {
		super(new LogicalTruthTableFormat());
	}
}
