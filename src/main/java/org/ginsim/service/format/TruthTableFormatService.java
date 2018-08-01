package org.ginsim.service.format;

import org.colomoto.biolqm.io.truthtable.TruthTableFormat;
import org.ginsim.core.service.*;
import org.kohsuke.MetaInfServices;

/**
 * GINsim import service for the TruthTable Format.
 * 
 * @author Pedro T. Monteiro
 */
@MetaInfServices(Service.class)
@Alias("TruthTable")
@ServiceStatus(EStatus.RELEASED)
public class TruthTableFormatService extends FormatSupportService<TruthTableFormat> {

	public TruthTableFormatService() {
		super(new TruthTableFormat());
	}
}
