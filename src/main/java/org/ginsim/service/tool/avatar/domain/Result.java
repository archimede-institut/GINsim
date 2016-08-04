package org.ginsim.service.tool.avatar.domain;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.colomoto.logicalmodel.io.avatar.AvatarUtils;
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
	/** Transient cycles **/
	public List<AbstractStateSet> transients; 
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
		s.setKey("Att_"+(complexAttID++));
		complexAttractors.put(s.getKey(),s);
		attractorsCount.put(s.getKey(),1);
		attractorsDepths.put(s.getKey(), new ArrayList<Integer>());
		System.out.println("ADDED to:"+complexAttractors.keySet());
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
	 */
	public boolean contains(AbstractStateSet s) {
		return complexAttractors.containsKey(s.getKey());
	}
	
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
		String result = 
				"\nPoint attractors:\n\t"+pointAttractors+
				//"\nComplex attractors:\n\t"+complexAttractors+
				"\nAttractors numerosity:\n\t"+attractorsCount+ 
				"\nProbs and depths per attractor::\n";
		int sum=AvaMath.sumCollection(attractorsCount.values());
		for(String key : attractorsDepths.keySet()) 
			result += key+"\t"+String.format("%.5f",((double)attractorsCount.get(key))/(double)sum)+"\t"+String.format("%.2f",AvaMath.mean(attractorsDepths.get(key)))+"\t"+String.format("%.2f",AvaMath.std(attractorsDepths.get(key)))+"\n";
		return result;
	}
	
	/**
	 * Serializes the results into HTML 
	 * @return HTML text describing the gathered results
	 */
	public String toHTMLString(){
		
		String result = "<b>Time</b>="+(((double)time)/1000.0)+"s<br>";
		
		/** A: print the discovered attractors */
		if(pointAttractors.size()>0){
			result+="<br><b>Point attractors</b>:<br>";
			for(String key : pointAttractors.keySet()){
				String mstate = strategy.contains("ront") ? 
						pointAttractors.get(key).toShortString() : 
						AvatarUtils.toString(pointAttractors.get(key).state);
				result+="&nbsp;&nbsp;&nbsp;"+key+"&nbsp;=>&nbsp;"+mstate;
				if(!strategy.contains("ront")){
					int sum=AvaMath.sumCollection(attractorsCount.values());
					result+="&nbsp;prob="+String.format("%.5f",((double)attractorsCount.get(key))/(double)sum);
					result+="&nbsp;depth="+String.format("%.1f",AvaMath.mean(attractorsDepths.get(key)))+"&plusmn;"+String.format("%.1f",AvaMath.std(attractorsDepths.get(key)))+"<br>";
				} 
				else result+="&nbsp;depth="+((int)AvaMath.mean(attractorsDepths.get(key)))+"<br>";
			}
		}
		if(complexAttractors.size()>0){
			result+="<br><b>Complex attractors</b>:<br>";
			int i=0;
			for(String key : complexAttractors.keySet()){
				if(complexAttractorPatterns.size()==0) result+="&nbsp;&nbsp;&nbsp;"+key+"&nbsp;=>&nbsp;"+complexAttractors.get(key).toString();
				else result+="&nbsp;&nbsp;&nbsp;att_"+(i++)+"&nbsp;=>&nbsp;"+AvatarUtils.toString(complexAttractorPatterns.get(key)).replace("-1","*");
				int sum=AvaMath.sumCollection(attractorsCount.values());
				if(strategy.contains("ront")){
					result+="&nbsp;depth="+(int)AvaMath.mean(attractorsDepths.get(key))+"<br>";
				} else {
					result+="<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;prob="+String.format("%.5f",((double)attractorsCount.get(key))/(double)sum);
					if(attractorsDepths.containsKey(key))
						result+="&nbsp;depth="+String.format("%.1f",AvaMath.mean(attractorsDepths.get(key)))+"&plusmn;"+String.format("%.1f",AvaMath.std(attractorsDepths.get(key)))+"<br>";
				}
			}
		}
		
		/** B: prints statistics */
		if(strategy.contains("vatar")){
			result+="<br><b>Countings</b>:&nbsp;{";
			for(String k : attractorsCount.keySet()) 
				result+=k+"="+attractorsCount.get(k)+",";
			if(attractorsCount.size()>0) result=result.substring(0,result.length()-1);
			result+="}<br><b>Transients</b> (more than #"+transientMinSize+" states): #"+transients.size();
			if(transients.size()>0){ 
				result+=" with sizes {";
				for(AbstractStateSet s : transients) result+=s.size()+",";
				result=result.substring(0,result.length()-1)+"}";
			}
			result+="<br>";
		} else {
			if(strategy.contains("ront")){
				if(performed==0) result+="<br><b>WARNING</b>: firefront could not converge before reaching the maximum specified depth. Please increase the maximum depth for a more precise analysis of point attractors.<br>";
				else result+="<br>Success: the simulation converged before reaching the inputted maximum depth.";
			} else result+="<br><b>Support</b>: "+(performed-truncated)+" successful runs (below max depth) out of "+performed+" (max="+runs+")</br>";
			//result+="<br>Runs:"+runs+" truncated:"+truncated+" performed:"+performed+"</br>";
			result+="<br><b>Probability bounds</b> per attractor:<br>";
			for(String key : pointAttractors.keySet()) 
				result += "&nbsp;&nbsp;"+key+" => Prob=["+attractorsLowerBound.get(key)+","+attractorsUpperBound.get(key)+"]<br>";
			for(String key : complexAttractors.keySet()) 
				result += "&nbsp;&nbsp;"+key+" => Prob=["+attractorsLowerBound.get(key)+","+attractorsUpperBound.get(key)+"]<br>";
		}
		return result;
	}
		
	/**
	 * Serializes the log into HTML 
	 * @return HTML text describing the stored log
	 */
	public String logToHTMLString() {
		return log.replace("\n","<br>").replace("\t","&nbsp;&nbsp;&nbsp;&nbsp;");
	}
}
