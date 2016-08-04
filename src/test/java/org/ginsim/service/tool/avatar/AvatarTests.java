package org.ginsim.service.tool.avatar;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;

import org.colomoto.logicalmodel.io.avatar.AvatarUtils;
import org.ginsim.core.graph.Graph;
import org.ginsim.core.graph.GraphManager;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.io.parser.GinmlParser;
import org.ginsim.service.tool.avatar.domain.Result;
import org.ginsim.service.tool.avatar.service.AvatarServiceFacade;

public class AvatarTests extends TestCase {

	public void testAvatar() throws Exception {
		String dir = "C:\\Users\\Rui\\Documents\\00 Avatar\\Avatar Material\\table-models\\";
		
		List<String> filenames = Arrays.asList("synthetic_1.avatar");//"random_002_v010_k2.avatar");//"sp1.avatar");//"mmc.avatar");
				//"mmc-cycD1.avatar");
				//"random_001_v010_k2.avatar");//,
				//"random_003_v015_k2.avatar");
				//"random_004_v015_k2.avatar");
				//"th-reduced.avatar");//<<problem sp1,"sp2.avatar","sp4.avatar",);
				//"synthetic_2.avatar");//"synthetic_2.avatar");
				//"simple5.avatar");
		List<String> options = Arrays.asList(
				"--runs=1000 --state=init --max-steps=10000 --tau=4 --min-cycle-size=2 "
				+ "--keep-transients --keep-oracle --random "
				+ "--max-psize="+Math.pow(2,14)+" --min-transient-size=100 "
				+ "--plots --quiet --output-dir=/output --input="+dir);

		for(String option : options){
			for(String filename : filenames){
				String[] args = (option+filename).split("( --)|=|--");
				System.out.println("\nFilename = "+filename+"\nARGS = "+args.length+AvatarUtils.toString(args));
				//Result result = AvatarServiceFacade.run(args);
				//assertNotNull(result);
			}
		}
	}
}
