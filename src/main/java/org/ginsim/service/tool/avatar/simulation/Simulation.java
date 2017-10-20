package org.ginsim.service.tool.avatar.simulation;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JTextPane;

import org.colomoto.biolqm.NodeInfo;
import org.colomoto.biolqm.StatefulLogicalModel;
import org.ginsim.service.tool.avatar.domain.CompactStateSet;
import org.ginsim.service.tool.avatar.domain.Dictionary;
import org.ginsim.service.tool.avatar.domain.Result;
import org.ginsim.service.tool.avatar.domain.StateSet;
import org.ginsim.service.tool.avatar.utils.ChartGNUPlot;


/**
 * Class defining an abstract simulation and providing facilities for their management.<br>
 * Specialized simulations (e.g. Avatar, Firefront, MonteCarlo) can be added.
 * 
 * @author Rui Henriques, Pedro Monteiro
 * @version 1.0
 */
public abstract class Simulation {

	/** output directory to save the outputs */
    public String outputDir;
	/** whether a graphical or programmatic interface is being used (to store outputs) */
    public boolean isGUI = false;
	/** whether detailed logs of the behavior of the simulation are to be printed (true is suggested to not hamper efficiency) */
    public boolean quiet = true;
	/** whether charts should be created and plotted */
	public boolean plots = true;
    
	protected StatefulLogicalModel model;
	protected List<CompactStateSet> oracle;
    protected String resultLog="";
	protected ChartGNUPlot chart = new ChartGNUPlot();  
    protected int memory;
		
	public void addModel(StatefulLogicalModel _model) {
		model = _model;
		List<NodeInfo> components = model.getComponents(); 
		oracle = new ArrayList<CompactStateSet>();
		int nstates = -1; 
		for(NodeInfo comp : components) nstates=Math.max(nstates, comp.getMax()+1);
		if(Math.pow(nstates,components.size())>=Long.MAX_VALUE){
			BigInteger[] hugeFactors = new BigInteger[components.size()];
			for(int i=0, l=components.size(); i<l; i++) hugeFactors[i]=new BigInteger(nstates+"").pow(i);
			Dictionary.codingLongStates(hugeFactors);
		} else {
			long[] factors = new long[components.size()];
			for(int i=0, l=components.size(); i<l; i++) factors[i]=(long)Math.pow(nstates,i);
			Dictionary.codingShortStates(factors);
		}
		int i=0;
		List<List<byte[]>> os = model.getOracles();
		if(os!=null) {
			for(List<byte[]> o : os)
				oracle.add(new CompactStateSet("oracle_"+(i++),model.getComponents(),o));
		}
	}

	/**
	 * Performs the simulation
	 * @return the discovered attractors, their reachability, and remaining contextual information
	 * @throws Exception
	 */
	public Result runSimulation() throws Exception {
  		long time = System.currentTimeMillis();
		Result res = runSim();
		for(String key : res.complexAttractors.keySet()){
			if(res.complexAttractors.get(key) instanceof StateSet)
				res.complexAttractorPatterns.put(key,MDDUtils.getStatePatterns(model.getComponents(),(StateSet)res.complexAttractors.get(key)));
			else if(res.complexAttractors.get(key) instanceof CompactStateSet)
				res.complexAttractorPatterns.put(key,((CompactStateSet)res.complexAttractors.get(key)).getStates());
		}

		res.time = (System.currentTimeMillis()-time);
		res.name = getName();
		res.nodes = getNodes();
		res.parameters = parametersToString();
		res.iconditions = model.getInitialStates();
		return res;
	}

	/**
	 * Performs the simulation
	 * @return the discovered attractors, their reachability, and remaining contextual information
	 * @throws Exception
	 */
	public abstract Result runSim() throws Exception;

	/**
	 * Prints the current parameters
	 * @return a String describing the parameters
	 */
	public abstract String parametersToString();
	public abstract String getName();

	private String getNodes() {
		String result = "";
		for(NodeInfo node : model.getComponents()) result+=node.getNodeID()+",";
		return result.substring(0,result.length()-1);
	}

	/**
	 * Updates a simulation with parameterizations dynamically fixed based on the properties of the input model
	 * @return simulation with updated parameters (values dynamically selected based on the input model)
	 */
	public abstract void dynamicUpdateValues();

	protected void output(String s){
		if(isGUI) resultLog+=s+"\n";
		else System.out.println(s);
	}
	
	protected String saveOutput() {
		if(isGUI) return resultLog;
		return "";
	}

	/**********************************/
	/** For dynamically updating GUI **/
	/**********************************/
	
    protected boolean exit = false;
	protected Thread t1; //used for heavy tasks from external libraries
	private JTextPane progress;

    public void exit(){
    	if(t1!=null && t1.isAlive()) t1.stop();
    }
    public Result run() throws Exception{
    	final Result[] res = new Result[1];
    	final Exception[] es = new Exception[1];
    	final boolean[] ok = new boolean[]{true};
		t1 = new Thread(new Runnable(){
	        @Override
	        public void run() {
	        	try {
					res[0]=runSimulation();
				} catch (Exception e) {
					//e.printStackTrace();
					es[0]=e;
					ok[0]=false;
				}
	        }
		});
		t1.start();
		t1.join();
		t1 = null;
		if(!ok[0]) throw es[0];
		return res[0];
    }
    
    public void setComponents(JTextPane _progress){
    	progress = _progress;
    }   
    
    protected void publish(String note){
      //System.out.println(note);
  	  if(note.startsWith("It")) progress.setText(note);
  	  else if(note.startsWith(" It")) progress.setText(note);
  	  else {
  		  //System.out.println(progress.getText().replace("</","<br>"+note+"</")+"\n============================");
  		  //if(!progress.getText().startsWith("<html>")) progress.setText("<html>"+progress.getText()+"</html>");
  		  progress.setText(progress.getText().replace("</body>","<br>"+note+"</body>"));
  	  }
  	  //progress.updateUI();
    }
}
