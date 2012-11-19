package org.ginsim.service.format;

import org.colomoto.logicalmodel.io.truthtable.TruthTableFormat;
import org.ginsim.core.service.Alias;
import org.ginsim.core.service.FormatSupportService;
import org.ginsim.core.service.Service;
import org.mangosdk.spi.ProviderFor;

/**
 * GINsim import service for the TruthTable Format.
 * 
 * @author Pedro T. Monteiro
 */
@ProviderFor(Service.class)
@Alias("TruthTable")
public class TruthTableFormatService extends FormatSupportService<TruthTableFormat> {

	public TruthTableFormatService() {
		super(new TruthTableFormat());
	}
}
