package org.ginsim.service.tool.avatar.params;

import java.io.IOException;
import org.colomoto.biolqm.io.avatar.AvatarUtils;
import org.ginsim.common.xml.XMLWriter;
import org.ginsim.common.xml.XMLize;
import org.ginsim.core.graph.objectassociation.ObjectAssociationManager;
import org.ginsim.core.utils.data.NamedObject;
import org.ginsim.service.tool.avatar.simulation.AvatarSimulation;
import org.ginsim.service.tool.avatar.simulation.FirefrontSimulation;
import org.ginsim.service.tool.avatar.simulation.MonteCarloSimulation;
import org.ginsim.service.tool.avatar.simulation.Simulation;
import org.ginsim.service.tool.avatar.simulation.AvatarSimulation.AvatarStrategy;


/**
 * Serializable context of a simulation (parameters, oracles and initial states)
 * 
 * @author Rui Henriques
 * @version 1.0
 */
/**
 * @author Rui
 *
 */
public class AvatarParameters implements XMLize, NamedObject {

	public String name = "new_parameter";
	public AvatarStateStore statestore;
	public boolean[] statesSelected, istatesSelected; //oraclesSelected, ioraclesSelected, enabled, ienabled;
	public boolean plots, quiet, avaKeepTrans;
	public int algorithm, avaStrategy;
	public String avaRuns, avaTau, avaDepth, avaAproxDepth, avaMinTran, avaMinCycle, avaMaxPSize, avaMaxRewiringSize;
	public String ffMaxExpand, ffDepth, ffAlpha, ffBeta, mcDepth, mcRuns;
    
    /**
     * Creates an empty context
     */
    public AvatarParameters() {}
    
    /**
     * Updates the context with the parameters of a given simulation
     * @param sim simulation whose parameters are to be stored
     */
    public void complete(Simulation sim){
		if(sim instanceof AvatarSimulation){
			AvatarSimulation avasim = (AvatarSimulation) sim;
			algorithm = 0;
	    	plots = avasim.plots;
	    	quiet = avasim.quiet;
	    	avaRuns=""+avasim.runs;
	    	avaTau=""+avasim.tauInit;
	    	avaDepth=""+avasim.maxSteps;
	    	avaAproxDepth=""+avasim.approxDepth;
	    	avaMinTran=""+avasim.minTransientSize;
	    	avaMinCycle=""+avasim.minCSize;
	    	avaMaxPSize=""+avasim.maxPSize;
	    	avaMaxRewiringSize=""+avasim.maxRewiringSize;
	    	avaKeepTrans=avasim.keepTransients;//avasim.keepOracle;
	    	AvatarStrategy[] stgs = AvatarStrategy.values();
	    	for(int i=0, l=stgs.length; i<l; i++)
	    		if(stgs[i].equals(avasim.strategy)) avaStrategy=i;
		} else if(sim instanceof FirefrontSimulation){
			FirefrontSimulation ffsim = (FirefrontSimulation) sim;
			algorithm = 1;
	    	ffMaxExpand=""+ffsim.maxExpand;
	    	ffDepth=""+ffsim.maxDepth;
	    	ffAlpha=""+ffsim.alpha;
	    	ffBeta=""+ffsim.beta;
	    	plots=ffsim.plots;
	    	quiet=ffsim.quiet;
		} else { //MonteCarlo
			MonteCarloSimulation mcsim = (MonteCarloSimulation) sim;
			algorithm = 2;
	    	mcDepth=""+mcsim.maxSteps;
	    	mcRuns=""+mcsim.runs;
		}
    }
	
	
	/**
	 * Serializes the AvatarParameters into a string
	 * @return the string representing the context of a simulation
	 */
	public String toFullString() {
		return "name="+name+"\n"+printStates()
		+"\nplots="+plots+"\nquiet="+quiet+"\navaKeepTrans="+avaKeepTrans
		+"\nalgorithm="+algorithm+"\navaStrategy="+avaStrategy
		+"\navaRuns="+avaRuns+"\navaTau="+avaTau+"\navaDepth="+avaDepth+"\navaAproxDepth="+avaAproxDepth+"\navaMinTran="+avaMinTran+"\navaMinCycle="+avaMinCycle+"\navaMaxPSize="+avaMaxPSize+"\navaMaxRewiringSize="+avaMaxRewiringSize
		+"\nffMaxExpand="+ffMaxExpand+"\nffDepth="+ffDepth+"\nffAlpha="+ffAlpha+"\nffBeta="+ffBeta+"\nmcDepth="+mcDepth+"\nmcRuns="+mcRuns;
    }
	
	private String printStates(){
		int l1=statestore.nstates.size(), l2=statestore.instates.size(), l3=statestore.oracles.size();
		String result="states="; 
    	for(int i=0; i<l1; i++) result+=statestore.nstates.get(i).getMap().toString()+",";
    	if(l1>0) result = result.substring(0,result.length()-1); 
    	result+="\nnamestates=[";
    	for(int i=0; i<l1; i++) result+=statestore.nstates.get(i).getName()+",";
    	if(l1>0) result = result.substring(0,result.length()-1); 
    	result+="]\nistates=";
    	for(int i=0; i<l2; i++) result+=statestore.instates.get(i).getMap().toString()+",";
    	if(l2>0) result = result.substring(0,result.length()-1); 
    	result+="\ninamestates=[";
    	for(int i=0; i<l2; i++) result+=statestore.instates.get(i).getName()+",";
    	if(l2>0) result = result.substring(0,result.length()-1); 
    	result+="]\nostates=";
    	for(int i=0; i<l3; i++) result+=statestore.oracles.get(i).getMap().toString()+",";
    	if(l3>0) result = result.substring(0,result.length()-1); 
    	result+="\nonamestates=[";
    	for(int i=0; i<l3; i++) result+=statestore.oracles.get(i).getName()+",";
    	if(l3>0) result = result.substring(0,result.length()-1); 
    	return result+"]\nstatesselection="+AvatarUtils.toString(statesSelected)+"\nistatesselection="+AvatarUtils.toString(istatesSelected);
    	//result +="\noracleselection="+AvatarUtils.toString(oraclesSelected)+"\nioracleselection="+AvatarUtils.toString(ioraclesSelected);
    	//result+"\nenabled="+AvatarUtils.toString(enabled)+"\nienabled="+AvatarUtils.toString(ienabled);
	}

	@Override
	public void toXML(XMLWriter out) throws IOException {
		out.openTag("parameter");
		out.addAttr("name",name);
        out.addAttr("avatarparameters",toFullString());
		System.out.println(toFullString());
		out.closeTag();
	}

    @Override
	public String getName() {
		return name;
	}

    @Override
	public void setName(String _name) {
		//ObjectAssociationManager.getInstance().fireUserUpdate(null, this.name, name);
		this.name = _name;
	}

    @Override
	public String toString() {
		return name;
	}
}
