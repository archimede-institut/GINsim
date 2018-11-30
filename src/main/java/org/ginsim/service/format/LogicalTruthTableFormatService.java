package org.ginsim.service.format;

import org.colomoto.biolqm.io.implicanttables.ImplicantTableFormat;
import org.ginsim.core.service.*;
import org.kohsuke.MetaInfServices;

/**
 * GINsim import/export service for the implicant table format.
 * 
 * @author Aurelien Naldi
 */
@MetaInfServices(Service.class)
@Alias("LogicalTruthTable")
@ServiceStatus(EStatus.RELEASED)
public class LogicalTruthTableFormatService extends FormatSupportService<ImplicantTableFormat> {

	public LogicalTruthTableFormatService() {
		super(new ImplicantTableFormat());
	}
}
