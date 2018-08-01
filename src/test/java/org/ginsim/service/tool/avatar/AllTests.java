package org.ginsim.service.tool.avatar;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import junit.framework.TestCase;
import org.colomoto.biolqm.StatefulLogicalModel;
import org.colomoto.biolqm.StatefulLogicalModelImpl;
import org.colomoto.biolqm.io.avatar.AvatarUtils;
import org.ginsim.core.graph.GSGraphManager;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.graph.regulatorygraph.RegulatoryNode;
import org.ginsim.service.tool.avatar.domain.Result;
import org.ginsim.service.tool.avatar.simulation.AvatarSimulation;
import org.ginsim.service.tool.avatar.simulation.FirefrontSimulation;
import org.ginsim.service.tool.avatar.simulation.MonteCarloSimulation;
import org.ginsim.service.tool.avatar.simulation.AvatarSimulation.AvatarStrategy;
import org.ginsim.service.tool.avatar.utils.AvaMath;


public class AllTests extends TestCase {

	static String dir = "C:\\Users\\Rui\\Documents\\00 Avatar\\Avatar Material\\table-models\\";
	static String outputDir = new File("").getAbsolutePath()+"/output/";
	
	public void testAll() throws Exception {
		//myTestMajorData();
		myTestBladder(null);
	}

	/*public static void  main(String[] args) throws Exception {
		List<Integer> init = new ArrayList<Integer>();
		for(int i=0, l=args.length; i<l; i++) init.add(Integer.valueOf(args[i]));
		myTestBladder(init);
	}*/

	/*private static void myTestMajorData() throws Exception {
		List<String> filenames = Arrays.asList(
				/*"random_001_v010_k2.avatar",
				"random_002_v010_k2.avatar",
				"random_003_v015_k2.avatar",
				"random_004_v015_k2.avatar",
				"synthetic_1.avatar",
				"synthetic_2.avatar",
				"mmc-cycD1.avatar",
				"mmc.avatar");
				//"sp1.avatar",
				"sp2.avatar",
				"sp4.avatar");
				//"th-reduced.avatar");//
		for(String filename : filenames){
			System.out.println(">>> "+filename);
			AvatarImport avaImport = new AvatarImport(new File(dir+filename));
			StatefulLogicalModel model = avaImport.getModel(); //model.fromNuSMV(filename);
			
			int[] expansions = new int[]{1000};//,1000,10000};
			double[] alphas = new double[]{0.0000001}; //0.0001,0.00001,0.000001,0.0000001,0.00000001,0.00000001,0.00000001};
			for(Integer expansion : expansions)
				for(Double alpha : alphas);
					//testFirefront(model,expansion,alpha);
			
			int[] minTransientSize = new int[]{0,50,200,1000}; //synth2,random1,sp2
			int[] minStatesToRewire = new int[]{100,200,500,1000,10000}; //synth2,random1,sp2
			int[] maxDepth = new int[]{100,1000,10000}; //sp2,sp4
			boolean keepAttractors = true;
			/*System.gc();
			testMonteCarlo(model);
			System.gc();
			while(true){
				try{ 
					testAvatar(model,AvatarStrategy.RandomExit,true); 
					break; }
				catch(Exception e){}
			}
			//while(true){
				//try{ 
					//testAvatar(model,AvatarStrategy.MatrixInversion,true); 
					//break; }
				//catch(Exception e){}
			//}
		}
	}*/
	
	private static void myTestBladder(List<Integer> init) throws Exception {
		RegulatoryGraph graph = (RegulatoryGraph)GSGraphManager.getInstance().open(dir+"Bladder_Model_Stateful.zginml");
		List<String> nodes = new ArrayList<String>();
		for(RegulatoryNode node : graph.getNodeOrder()) nodes.add(node.getName());
		String[] names = new String[]{"EGFR_stimulus","FGFR3_stimulus","DNAdamage","GrowthInhibitors","Proliferation","Apoptosis","Growth_Arrest"};
		List<byte[]> states = new ArrayList<byte[]>();
		if(init==null){
			states = Arrays.asList(
				//new byte[]{0,0,0,0,0,0,1}); //1:OK
				//new byte[]{0,0,0,1,0,0,1}); //3: 2 PA {P1678822875,P1592729433}
				/*new byte[]{0,0,1,0,0,1,1}, //4:OK {P957057748974}
				new byte[]{0,0,1,1,0,1,1},//5:OK {P956971655559,P957057749001}
				new byte[]{0,1,0,0,1,0,0},//7:OK
				new byte[]{0,1,0,1,0,0,1}, //8:3 PA
				new byte[]{0,1,1,0,0,1,1}); //11: OK
				new byte[]{0,1,1,1,0,1,1}, //12: 2 PA
				new byte[]{1,0,0,0,-1,0,-1}, //14:
				new byte[]{1,0,0,1,0,0,1}, //15: OK CA-512*/
				/*new byte[]{1,0,1,0,0,1,1}, //16: OK CA 16
				new byte[]{1,0,1,1,0,1,1}); //17: OK CA 16,32
				new byte[]{1,1,0,0,1,0,0}, //18:*/
				new byte[]{1,1,0,1,0,0,1}); //19: PA 3 {P68631970297711,P68912870936836,P68632056214006}
				/*new byte[]{1,1,0,1,1,0,0}, //21: PA 3 {P68631970297711,P68912870936836,P68632056214006}
				new byte[]{1,1,1,0,0,1,1}, //22: OK*/
				//new byte[]{1,1,1,1,0,1,1}); //23: PA2 {P69587349223837,P69587435140132}*/
		} else {
			List<byte[]> all = Arrays.asList(
					new byte[]{0,0,0,0,0,0,1},new byte[]{0,0,0,1,0,0,1},new byte[]{0,0,1,0,0,1,1},
					new byte[]{0,0,1,1,0,1,1},new byte[]{0,1,0,0,1,0,0},new byte[]{0,1,0,1,0,0,1},
					new byte[]{0,1,1,0,0,1,1},new byte[]{0,1,1,1,0,1,1},new byte[]{1,0,0,0,-1,0,-1}, //14:
					new byte[]{1,0,0,1,0,0,1},new byte[]{1,0,1,0,0,1,1},new byte[]{1,0,1,1,0,1,1}, //17: OK CA 16,32
					new byte[]{1,1,0,0,1,0,0},new byte[]{1,1,0,1,0,0,1},new byte[]{1,1,0,1,1,0,0}, //21: PA 3 {P68631970297711,P68912870936836,P68632056214006}
					new byte[]{1,1,1,0,0,1,1},new byte[]{1,1,1,1,0,1,1});
			for(Integer i : init)
				states.add(all.get(i));
		}
		int k=0;
		for(byte[] state : states){
			System.out.println("State:"+AvatarUtils.toString(state)+ "I:"+(k++));
			byte[] istate = AvatarUtils.getFreeState(nodes.size());
			for(int i=0, l=state.length; i<l; i++) istate[i]=state[i];
				//if(state[i]>=0) istate[nodes.indexOf(names[i])]=state[i];
			StatefulLogicalModel model = new StatefulLogicalModelImpl(((RegulatoryGraph)graph).getModel(),Arrays.asList(istate),graph.getGraphName());
			testAvatar(model,AvatarStrategy.RandomExit,true);
			/*testMonteCarlo(model);
			testAvatar(model,AvatarStrategy.MatrixInversion,true);
			*/
		}
	}
	private static void testFirefront(StatefulLogicalModel model, int maxExpand, double alpha) throws Exception{
		FirefrontSimulation sim = new FirefrontSimulation();
		sim.addModel(model);
		sim.maxDepth=maxExpand; 
		sim.alpha=alpha; 
		sim.beta=0.00001; 
		sim.maxExpand=-1;
		sim.quiet = true;
		sim.outputDir = outputDir;
		Result res = sim.run();
		System.out.print("FireFront\t"+res.time+"\t"+res.memory+"\t"+getAttractors(res)+"\t"+getSizes(res));
		System.out.println("\t"+getLowerBounds(res)+"\t"+getUpperBounds(res)+"\t"+getDepths(res)+"\t"+res.residual+"\t"+res.performed);
	}
	private static void testAvatar(StatefulLogicalModel model, AvatarStrategy strategy, boolean keepTransients) throws Exception {
		AvatarSimulation sim = new AvatarSimulation();
		sim.addModel(model);
		sim.runs = 100;
		sim.tauInit = 4;
		sim.minCSize = 3;
		sim.maxSteps = 100000000;		
		sim.maxPSize = 200000;
		sim.maxRewiringSize = (strategy.equals(AvatarStrategy.MatrixInversion)) ? 2000 : 10000000;
		sim.minTransientSize = 200;
		sim.keepTransients = keepTransients;
		sim.keepOracle = true;
		sim.quiet = true;
		sim.strategy = strategy;
		sim.outputDir = outputDir;
		Result res = sim.run();
		System.out.print("Avatar\t"+res.time+"\t"+res.memory+"\t"+getAttractors(res)+"\t"+getProbs(res));
		System.out.println("\t"+getSizes(res)+"\t"+getDepths(res));
	}
	private static void testMonteCarlo(StatefulLogicalModel model) throws Exception {
		MonteCarloSimulation sim = new MonteCarloSimulation();
		sim.addModel(model);
		sim.runs = 10000;
		sim.maxSteps = 10000; 
		sim.quiet = true;
		Result res = sim.run();
		System.out.print("MonteCarlo\t"+res.time+"\t"+res.memory+"\t"+getAttractors(res));
		System.out.println("\t"+getLowerBounds(res)+"\t"+getUpperBounds(res)+"\t"+getDepths(res)+"\t"+((double)res.performed)/(double)res.runs);
	}

	private static String getSizes(Result res) {
		String result="{";
		for(String key : res.pointAttractors.keySet()) result+="1,";
		for(String key : res.complexAttractors.keySet()) result+=res.complexAttractors.get(key).size()+",";
		return result.substring(0,result.length()-1)+"}";
	}
	private static String getAttractors(Result res) {
		String result="{";
		for(String key : res.pointAttractors.keySet()) result+="P"+key+",";
		for(String key : res.complexAttractors.keySet()) result+="C"+key+",";
		return result.substring(0,result.length()-1)+"}";
	}
	private static String getLowerBounds(Result res) {
		String result="{";
		for(String key : res.pointAttractors.keySet()) result+=String.format(Locale.US,"%.2f",res.attractorsLowerBound.get(key))+",";
		for(String key : res.complexAttractors.keySet()) result+=String.format(Locale.US,"%.2f",res.attractorsLowerBound.get(key))+",";
		return result.substring(0,result.length()-1)+"}";
	}
	private static String getProbs(Result res) {
		String result="{";
		for(String key : res.pointAttractors.keySet()) result+=String.format(Locale.US,"%.2f",((double)res.attractorsCount.get(key))/(double)res.performed)+",";
		for(String key : res.complexAttractors.keySet()) result+=String.format(Locale.US,"%.2f",((double)res.attractorsCount.get(key))/(double)res.performed)+",";
		return result.substring(0,result.length()-1)+"}";
	}
	private static String getDepths(Result res) {
		String result="{";
		for(String key : res.pointAttractors.keySet()) result+=String.format(Locale.US,"%.2f",AvaMath.mean(res.attractorsDepths.get(key)))+"+-"+String.format("%.2f",AvaMath.std(res.attractorsDepths.get(key)))+",";
		for(String key : res.complexAttractors.keySet()) result+=String.format(Locale.US,"%.2f",AvaMath.mean(res.attractorsDepths.get(key)))+"+-"+String.format("%.2f",AvaMath.std(res.attractorsDepths.get(key)))+",";
		return result.substring(0,result.length()-1)+"}";
	}
	private static String getUpperBounds(Result res) {
		String result="{";
		for(String key : res.pointAttractors.keySet()) result+=String.format(Locale.US,"%.2f",res.attractorsUpperBound.get(key))+",";
		for(String key : res.complexAttractorPatterns.keySet()) result+=String.format(Locale.US,"%.2f",res.attractorsUpperBound.get(key))+",";
		return result.substring(0,result.length()-1)+"}";
	}
	
}
