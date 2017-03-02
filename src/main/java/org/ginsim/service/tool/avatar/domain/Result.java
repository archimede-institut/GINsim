package org.ginsim.service.tool.avatar.domain;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.colomoto.biolqm.io.avatar.AvatarUtils;
import org.ginsim.service.tool.avatar.utils.AvaMath;

public class Result {

	/** Associated type of simulation (e.g. Avatar, FF, MonteCarlo) **/
	public String strategy;
	/** Complex attractors (terminal cycles) **/
	public Map<String,AbstractStateSet> complexAttractors; 
	/** Point attractors (stable states) **/
	public Map<String,State> pointAttractors; 
	/** Pattern representation of complex attractors **/
	public Map<String,List<byte[]>> complexAttractorPatterns;
	/** Number of occurrences per attractor **/
	public Map<String,Integer> attractorsCount;  
	/** Lower probability bound of attractors **/
	public Map<String,Double> attractorsLowerBound;
	/** Upper probability bound of attractors **/
	public Map<String,Double> attractorsUpperBound;
	/** Transient cycles *
	public List<AbstractStateSet> transients;*/
	public int maxTransientSize = -1;
	/** Depth of attractors (from a well-defined portion of the state space) **/
	public Map<String,List<Integer>> attractorsDepths; 
	/** Charts to be plotted **/
	public Map<String,BufferedImage> charts;
	/** Simulation log **/
	public String log;
	/** Maximum number of iterations **/
	public int transientMinSize=-1;
	/** Maximum number of iterations **/
	public int runs=-1;
	/** Number of truncated iterations **/
	public int truncated=-1; 
	/** Number of performed iterations **/
	public int performed=-1;
	/** Simulation time (miliseconds) **/
	public long time;
	/** Simulation memory (Mbytes) **/
	public int memory;
	/** FireFront residual probability **/
	public double residual;
	/** Simulation name **/
	public String name;
	/** Parameters description **/
	public String parameters;
	/** Names of components **/
	public String nodes;
	/** Initial states associated with these results **/
	public List<byte[]> iconditions;
	/** Applied perturbations **/
	public String perturbation = null;
	/** Applied reductions **/
	public String reduction = null;
	
	private int complexAttID = 0;

	public Result(){
		attractorsDepths = new HashMap<String,List<Integer>>();
		complexAttractors = new HashMap<String,AbstractStateSet>();
		complexAttractorPatterns = new HashMap<String,List<byte[]>>();
		pointAttractors = new HashMap<String,State>();
		attractorsCount = new HashMap<String,Integer>();
		attractorsLowerBound = new HashMap<String,Double>();
		attractorsUpperBound = new HashMap<String,Double>();
		charts = new HashMap<String,BufferedImage>();
	}
	
	/**
	 * Adds a point attractor (stable state) to results
	 * @param s point attractor to be added
	 */
	public void add(State s) {
		pointAttractors.put(s.key,s);
		attractorsCount.put(s.key,1);
		attractorsDepths.put(s.key, new ArrayList<Integer>());
	}
	
	/**
	 * Adds a complex attractor (terminal cycle) to results
	 * @param s complex attractor to be added
	 */
	public void add(AbstractStateSet s) {
		s.setKey("C.Att."+(complexAttID++));
		complexAttractors.put(s.getKey(),s);
		attractorsCount.put(s.getKey(),1);
		attractorsDepths.put(s.getKey(), new ArrayList<Integer>());
		//System.out.println("ADDED to:"+complexAttractors.keySet());
	}
	
	/**
	 * Adds a point attractor (stable state) to results
	 * @param s point attractor to be added
	 * @param steps depth of the point attractor from a portion of the state space
	 */
	public void add(State s, int steps) {
		if(contains(s)){
			attractorsCount.put(s.key,attractorsCount.get(s.key)+1);
		} else {
			pointAttractors.put(s.key,s);
			attractorsCount.put(s.key,1);
			attractorsDepths.put(s.key, new ArrayList<Integer>());
		}
		attractorsDepths.get(s.key).add(steps);
	}
	
	/**
	 * Adds a complex attractor (terminal cycle) to results
	 * @param s complex attractor to be added
	 * @param steps depth of the complex attractor from a portion of the state space
	 */
	public void add(AbstractStateSet s, int steps) {
		add(s);
		attractorsDepths.get(s.getKey()).add(steps);
	}

	public void incrementComplexAttractor(String key, int steps) {
		attractorsCount.put(key,attractorsCount.get(key)+1);
		attractorsDepths.get(key).add(steps);
	}

	/**
	 * Checks whether a point attractor is stored
	 * @param s point attractor to be checked
	 * @return true if the attractor is stored
	 */
	public boolean contains(State s) {
		return pointAttractors.containsKey(s.key);
	}
	
	/**
	 * Checks whether a complex attractor is stored
	 * @param s complex attractor to be checked
	 * @return true if the attractor is stored
	public boolean contains(AbstractStateSet s) {
		return complexAttractors.containsKey(s.getKey());
	}
	 */
	
	/**
	 * Increments the number of occurrences for a point attractor (assumes the results contain the attractor)
	 * @param s point attractor whose occurrence is to be accounted
	 */
	public void increment(State s) {
		attractorsCount.put(s.key,attractorsCount.get(s.key)+1);
	}
	
	/**
	 * Increments the number of occurrences for a complex attractor (assumes the results contain the attractor)
	 * @param s complex attractor whose occurrence is to be accounted
	 */
	public void increment(StateSet s) {
		attractorsCount.put(s.getKey(),attractorsCount.get(s.getKey())+1);
	}
	
	/**
	 * Bounds the probability of a given attractor
	 * @param attractor the attractor whose probability is to be bounded
	 * @param lower lower bound
	 * @param upper upper bound
	 */
	public void setBounds(String attractor, double lower, double upper){
		attractorsLowerBound.put(attractor, Math.max(0, lower));
		attractorsUpperBound.put(attractor, Math.min(1.0, upper));
	}
	
	/**
	 * Adds a plotted chart to results
	 * @param title the name of the chart
	 * @param img the chart to be stored
	 */
	public void addPlot(String title, BufferedImage img) {
		charts.put(title, img);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		return "\n"+toHTMLString().replace("<br>","\n").replace("<b>","").replace("</b>","").replace("&nbsp;","  ").replace("&plusmn;","+/-"); 
	}
	
	/**
	 * Serializes the results into HTML 
	 * @return HTML text describing the gathered results
	 */
	public String toHTMLString(){
		
		String result = "<b>"+name+"</b><br><br><b>Parameters</b><br>"+parameters.replace("\n","<br>").replace("\t","&nbsp;&nbsp;")+"<br>";
		if(perturbation==null) result += "<br>No perturbations applied<br>";
		else result += "<br>Applied perturbation: "+perturbation+"<br>";
		if(perturbation==null) result += "No reductions applied<br>";
		else result += "Name of the selected reduction: "+reduction+"<br>";
		
		result += "<br><b>Nodes</b>=["+nodes+"]<br>";
		if(iconditions!=null){
			result += "<br><b>Initial conditions</b><br>";
			for(byte[] s : iconditions) result+="&nbsp;&nbsp;&nbsp;"+AvatarUtils.toString(s).replace("-1","*")+"<br>";
		}

		int sum=AvaMath.sumCollection(attractorsCount.values());
		result += "<br><b>Time</b>="+(((double)time)/1000.0)+"s";
		if(strategy.contains("vatar")) result+="<br><b>Successful runs</b>="+sum+"<br>";		
		else {
			if(strategy.contains("ront")){
				if(performed==0) result+="<br><b>WARNING</b>: firefront could not converge before reaching the maximum specified depth. Please increase the maximum depth for a more precise analysis of point attractors.<br>";
				else result+="<br>Success: the simulation converged before reaching the maximum depth.<br>";
			} else result+="<br><b>Support</b>: "+performed+" successful runs (below max depth) out of "+runs+"</br>";
		}

		/** A: print the discovered attractors */
		if(pointAttractors.size()>0){
			result+="<br><b>Point attractors</b>:<br>";
			for(String key : pointAttractors.keySet()){
				result+="&nbsp;&nbsp;&nbsp;"+ AvatarUtils.toString(pointAttractors.get(key).state);
				if(!strategy.contains("ront")){
					result+="&nbsp;prob="+format("%.5f",((double)attractorsCount.get(key))/(double)sum);
					//result+="&nbsp;counts="+attractorsCount.get(key);
					if(!strategy.contains("vatar")) result+="&nbsp;depth="+format("%.1f",AvaMath.mean(attractorsDepths.get(key)))+"&plusmn;"+format("%.1f",AvaMath.std(attractorsDepths.get(key)))+"<br>";
					else result+="<br>";
				} else {
					result += "&nbsp;&nbsp;prob=["+format("%.5f",attractorsLowerBound.get(key))+","+format("%.5f",attractorsUpperBound.get(key))+"]";
					result+="&nbsp;depth="+((int)AvaMath.mean(attractorsDepths.get(key)))+"<br>";
				}
			}
		}
		if(complexAttractors.size()>0){
			result+="<br><b>Complex attractors</b>:<br>";
			//int i=0;
			for(String key : complexAttractors.keySet()){
				if(complexAttractorPatterns.size()==0) result+="&nbsp;&nbsp;&nbsp;"+key+"&nbsp;=>&nbsp;"+complexAttractors.get(key).toString();
				else result+="&nbsp;&nbsp;&nbsp;"+key+"&nbsp;=>&nbsp;"+AvatarUtils.toString(complexAttractorPatterns.get(key)).replace("-1","*");
				if(strategy.contains("ront")){
					result += "<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;prob=["+format("%.5f",attractorsLowerBound.get(key))+","+format("%.5f",attractorsUpperBound.get(key))+"]";
					result+="&nbsp;depth="+(int)AvaMath.mean(attractorsDepths.get(key))+"<br>";
				} else {
					result+="<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;prob="+format("%.5f",((double)attractorsCount.get(key))/(double)sum);
					result+="&nbsp;size="+complexAttractors.get(key).size();
					if(!strategy.contains("vatar")&&attractorsDepths.containsKey(key)) result+="&nbsp;depth="+format("%.1f",AvaMath.mean(attractorsDepths.get(key)))+"&plusmn;"+format("%.1f",AvaMath.std(attractorsDepths.get(key)))+"<br>";
					else result+="<br>";
				}
			}
		}
		
		/** B: prints statistics */
		if(strategy.contains("vatar")){
			/*result+="<br><b>Countings</b>:&nbsp;{";
			for(String k : attractorsCount.keySet()) 
				result+=k+"="+attractorsCount.get(k)+",";
			if(attractorsCount.size()>0) result=result.substring(0,result.length()-1);*/
			if(maxTransientSize>0) result+="<br>Largest SCC transient found: #"+maxTransientSize+" states";
			/*if(transients.size()>0){ 
				result+=" with sizes {";
				for(AbstractStateSet s : transients) result+=s.size()+",";
				result=result.substring(0,result.length()-1)+"}";
			}*/
		}
		//result+="<br>Runs:"+runs+" truncated:"+truncated+" performed:"+performed+"</br>";
		//result+="<br><b>Probability bounds</b> per attractor:<br>";
		return result;
	}
		
	/**
	 * Serializes the log into HTML 
	 * @return HTML text describing the stored log
	 */
	public String logToHTMLString() {
		return log.replace("\n","<br>").replace("\t","&nbsp;&nbsp;&nbsp;&nbsp;");
	}

	public String toCSVString() {
		String result = name+"\n\n,Parameters\n"+parameters.replace("\t",",,").replace("=","=,")+"\n\n";
		if(perturbation==null) result += ",No perturbations applied\n";
		else result += ",Applied perturbation: "+perturbation+"\n";
		if(perturbation==null) result += ",No reductions applied\n";
		else result += ",Name of the selected reduction: "+reduction+"\n";

		result += "\n,Time,"+(((double)time)/1000.0)+",secs\n\n";
		
		/** A: print the discovered attractors */
		
		int sum=AvaMath.sumCollection(attractorsCount.values());
		if(pointAttractors.size()>0){
			result+=",Point attractors\n,,"+nodes;
			if(strategy.contains("vatar")) result+=",prob\n";
			else if(strategy.contains("ront")) result+= ",lowerbound,upperbound,depth\n";
			else result+=",prob,depth\n";
			for(String key : pointAttractors.keySet()){
				result+=",,"+ AvatarUtils.toOpenString(pointAttractors.get(key).state);
				if(!strategy.contains("ront")){
					result+=","+format("%.5f",((double)attractorsCount.get(key))/(double)sum);
					//result+=","+attractorsCount.get(key);
					if(!strategy.contains("vatar")) result+=","+format("%.1f",AvaMath.mean(attractorsDepths.get(key)))+"+-"+format("%.1f",AvaMath.std(attractorsDepths.get(key)))+"\n";
					else result+="\n";
				} else {
					result+=","+format("%.5f",attractorsLowerBound.get(key))+","+format("%.5f",attractorsUpperBound.get(key));
					result+=","+((int)AvaMath.mean(attractorsDepths.get(key)))+"\n";
				}
			}
		}
		if(complexAttractors.size()>0){
			result+="\n,Complex attractors\n,,"+nodes+"\n";
			if(strategy.contains("vatar")) result+=",prob,size\n";
			else if(strategy.contains("ront")) result+= ",lowerbound,upperbound,depth\n";
			else result+=",prob,depth\n";
			int i=0;
			for(String key : complexAttractors.keySet()){
				if(complexAttractorPatterns.size()==0) result+=","+key+","+complexAttractors.get(key).toString();
				else result+=",Att_"+(i++)+",";
				boolean first=true;
				for(byte[] s : complexAttractorPatterns.get(key)){
					result+=AvatarUtils.toOpenString(s).replace("-1","*");
					if(first){
						first = false;
						if(strategy.contains("ront")){
							result+=","+format("%.5f",attractorsLowerBound.get(key))+","+format("%.5f",attractorsUpperBound.get(key))+","+(int)AvaMath.mean(attractorsDepths.get(key));
						} else {
							result+=","+format("%.5f",((double)attractorsCount.get(key))/(double)sum);//+","+attractorsCount.get(key);
							if(!strategy.contains("vatar") && attractorsDepths.containsKey(key)) 
								result+=","+format("%.1f",AvaMath.mean(attractorsDepths.get(key)))+"+-"+format("%.1f",AvaMath.std(attractorsDepths.get(key)))+"\n";
							else result+="\n";
						}
					} 
					result+="\n,,,";
				}
			}
		}
		
		/** B: prints statistics */
		if(strategy.contains("vatar")){
			if(maxTransientSize>0) result+="\n,Largest SCC transient found=,"+maxTransientSize;
			/*if(transients.size()>0){ 
				for(AbstractStateSet s : transients) result+=s.size()+";";
				result=result.substring(0,result.length()-1)+";";
			}*/
			result+="\n,Successful runs=,"+sum+"\n";
		} else {
			if(strategy.contains("ront")){
				if(performed==0) result+="\n,WARNING:;firefront could not converge before reaching the maximum specified depth. Please increase the maximum depth for a more precise analysis of point attractors.\n";
				//else result+="\n;Success:;the simulation converged before reaching the inputted maximum depth.\n";
			} else result+="\n,Support:,successful runs=,"+performed+",total runs=,"+runs+"\n";
		}
		return result;
	}
	
	private String format(String pattern, double value){
		return String.format(pattern,value).replace(",",".");
	}
}
