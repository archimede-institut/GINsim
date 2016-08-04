package org.ginsim.servicegui.tool.avatar;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingWorker;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import org.colomoto.logicalmodel.StatefulLogicalModel;
import org.ginsim.common.application.LogManager;
import org.ginsim.core.graph.Graph;
import org.ginsim.core.graph.GraphManager;
import org.ginsim.core.graph.dynamicgraph.DynamicGraph;
import org.ginsim.core.graph.regulatorygraph.namedstates.NamedState;
import org.ginsim.gui.GUIManager;
import org.ginsim.gui.WhatToDoWithGraph;
import org.ginsim.service.tool.avatar.domain.AbstractStateSet;
import org.ginsim.service.tool.avatar.domain.Result;
import org.ginsim.service.tool.avatar.params.AvatarParameters;
import org.ginsim.service.tool.avatar.simulation.Simulation;
import org.ginsim.service.tool.avatar.simulation.SimulationUtils;
import org.ginsim.servicegui.tool.avatar.others.FixedSizePanel;
import org.ginsim.servicegui.tool.avatar.others.IndexableActionListener;
import org.ginsim.servicegui.tool.avatar.parameters.AvatarParametersHelper;

/**
 * Facilities to run and kill simulations and display their results 
 * 
 * @author Rui Henriques
 * @version 1.0
 */
public class AvatarResults {

  private Simulation sim;
  private JLabel progress;
  private boolean quiet, flexible;
  private final AvatarConfigFrame parent;
  private final StatefulLogicalModel model;
  private File memFile, logFile;
  private JButton brun, stop;
  
  /**
   * Creates the necessary context to run a simulation and display its results
   * @param _sim the simulation
   * @param flex true for a flexible gridbaglayout (default: true)
   * @param _progress the component for posting updates during the execution of a simulation
   * @param _parent the parent panel
   * @param _quiet whether detailed logs are to be printed (default: true)
   * @param _model the stateful logical model possibly defining a set of initial states and oracles
   * @param _memFile the directory or file to save plots
   * @param _logFile the file to print logs
   */
  public AvatarResults(Simulation _sim, boolean flex, JLabel _progress, final AvatarConfigFrame _parent, boolean _quiet, final StatefulLogicalModel _model, File _memFile, File _logFile, JButton _brun, JButton _stop){
	  sim = _sim;
	  flexible = flex;
	  progress = _progress;
	  parent = _parent;
	  quiet = _quiet;
	  model = _model;
	  memFile = _memFile;
	  logFile = _logFile;
	  brun = _brun;
	  stop = _stop;
  }
  
  /**
   * Executes the instantiated simulation and displays its results 
   */
  public void runAvatarResults() {
	 try {
		 sim.setComponents(progress);
		 Thread t1 = new Thread(new Runnable() {
		        @Override
		        public void run() {
		          try {
		      		long time = System.currentTimeMillis();
		           	Result res = sim.run();
		    		if(res!=null){
		    			res.time = (System.currentTimeMillis()-time);
			           	progress.setText("Simulation successfully computed!");
			           	progress.updateUI();
			        	showOutputFrame(res);
		    		} else {
			           	progress.setText("Simulation was successfully terminated!");
			           	progress.updateUI();
				 		stop.setEnabled(false);
				 		brun.setEnabled(true);
				    	System.gc();
				    	System.runFinalization();
		    		}
				  } catch (Exception e) {
				 		String fileErrorMessage = "Unfortunately we were not able to finish your request.<br><em>Reason:</em> Exception while running the algorithm.";
				 		errorDisplay(fileErrorMessage,e);
				 		stop.setEnabled(false);
				 		brun.setEnabled(true);
				 		e.printStackTrace();
				  }
		        }
		    });
		 t1.start();
	 } catch(Exception e){
 		String fileErrorMessage = "Unfortunately we were not able to finish your request.<br><em>Reason:</em> Exception while running the algorithm.";
 		errorDisplay(fileErrorMessage,e);
 		stop.setEnabled(false);
 		brun.setEnabled(true);
 		e.printStackTrace();
	 }
  }
  
  public void kill(boolean dialog){
	  sim.exit();
  }
  
  /**
   * Creates the graphical display of the results from an executed simulation (called after finishing 'runAvatarResults')
   * @param res the results to be displayed
   */
  public void showOutputFrame(final Result res) {
	stop.setEnabled(false);
	brun.setEnabled(true);
	  
	/** B: CREATE OUTPUT **/ 	 
	JFrame output = new JFrame();
	Color purple = new Color(204,153,255);
	if(flexible){
		output.setLayout(new GridBagLayout());
	 	output.setMinimumSize(new Dimension(590,520));
	} else {
		output.setLayout(null);
	 	output.setBounds(0,0,590,570);
	}
 	GridBagConstraints g = new GridBagConstraints();  
	int yshift = 7, mheight = (int) Math.round(((double)res.charts.size())/2.0)*19+1;

	try {
	 	/** C: plot results */
	 	JTextPane outText = new JTextPane();
	 	outText.setContentType("text/html");
	 	outText.setText("<html>"+res.toHTMLString()+"</html>");
	 	//System.out.println(res.toHTMLString());
	 	//System.out.println("DONE");
	 	//outText.setAutoscrolls(true);
	 	//outText.setEditable(false);
	 	//outText.setCaretPosition(0);
	 	JScrollPane scrollPaneTutorial = new JScrollPane(outText);
	 	scrollPaneTutorial.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
	 	scrollPaneTutorial.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
	 	//scrollPaneTutorial.setPreferredSize(new Dimension(20000,300));
	 	scrollPaneTutorial.setBorder(new TitledBorder(new LineBorder(purple,2), "Output", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));

	 	if(flexible){
	 		g.gridx=0;
	 		g.gridy=0;
	 		g.weightx=1;
	 		g.gridheight=1;
	 		g.weighty=1;
	 		g.fill = GridBagConstraints.BOTH;
	 		//scrollPaneTutorial.setPreferredSize(new Dimension(560, 300));
		 	output.add(scrollPaneTutorial,g);
	 		g.weighty=0;
	 	} else {
		 	scrollPaneTutorial.setBounds(5,5,560,300);
		 	output.add(scrollPaneTutorial);
	 	}
	 	/** D: Draw plots **/
	 	JPanel panelPlot = null;
	 	if(flexible) panelPlot = new JPanel(new GridLayout(Math.max(1,res.charts.size()),1));
	 	else panelPlot = new FixedSizePanel(10,mheight);
	 	for(String title : res.charts.keySet()){
	 		JButton button = new JButton(title);
	 		button.addActionListener(new IndexableActionListener(title,parent) {
	 			public void actionPerformed(ActionEvent arg0) {
	 				JLabel picLabel = new JLabel(new ImageIcon(res.charts.get(this.getKey())));
	 				int dialogResult = JOptionPane.showOptionDialog(this.getDialog(), picLabel, "Plotted chart", JOptionPane.YES_OPTION, 
	 						JOptionPane.INFORMATION_MESSAGE, null, new String[]{"SAVE","CLOSE"}, "default");
	 				if(dialogResult == JOptionPane.YES_OPTION){
	 					JFileChooser fcnet = new JFileChooser();
	 					fcnet.setFileSelectionMode(JFileChooser.FILES_ONLY);
	 					fcnet.setSelectedFile(memFile);
	 					int returnVal = fcnet.showSaveDialog(parent);
	 					if(returnVal == JFileChooser.APPROVE_OPTION){
	 						memFile = fcnet.getSelectedFile();
	 						if(returnVal == 0)
	 						try {
	 							ImageIO.write(res.charts.get(this.getKey()), "png", memFile);
	 							JOptionPane.showMessageDialog(this.getDialog(), new JLabel("Your chart was successfully saved"),"Saving chart...",JOptionPane.PLAIN_MESSAGE);
	 						} catch (IOException e) {
	 							JOptionPane.showMessageDialog(this.getDialog(), new JLabel("Ops! We were not able to save your chart. Please contact the GINsim team!"),"Saving chart...",JOptionPane.ERROR_MESSAGE);
	 							e.printStackTrace();
	 						}
	 						//System.out.println(">>"+memorizedFile.getAbsolutePath());
	 					}
	 				}
	 			}
	 		});
	 		if(!flexible) button.setBounds(5, yshift, 300, 20);
		 	panelPlot.add(button);
	 		yshift+=20;
	 	}
	 	if(res.charts.size()==0){
	 		JLabel label = new JLabel("    Plots were not generated (please select the option)!");
	 		if(!flexible) label.setBounds(5, 10, 300, 22);
	 		panelPlot.add(label);
	 	}
	 	if(flexible){
	 		g.gridx=0;
	 		g.gridy=1;
	 		g.weightx=1;
	 		g.gridheight=1;
	 		//plots.setPreferredSize(new Dimension(560, 60));
	 		panelPlot.setBorder(new TitledBorder(new LineBorder(purple,2), "Plotted charts", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		 	output.add(panelPlot,g);
	 	} else {
		 	JScrollPane plots = new JScrollPane(panelPlot);
		 	plots.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		 	plots.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		 	plots.setBorder(new TitledBorder(new LineBorder(purple,2), "Plotted charts", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
	 		plots.setBounds(5,310,560,80);
		 	output.add(plots);
	 	}
	 } catch(Exception e){
 		String fileErrorMessage = "Unfortunately we were not able to finish your request.<br><em>Reason:</em> Exception while displaying the plots.";
 		errorDisplay(fileErrorMessage,e);
 		stop.setEnabled(false);
 		brun.setEnabled(true);
 		e.printStackTrace();
 		return;		  
	 }
	 try {
		 
	 	/** E: Draw complex attractors **/ 
	    yshift = 7;
	 	Map<String,AbstractStateSet> complexAttractors = res.complexAttractors;
	 	mheight = (int) Math.round(((double)complexAttractors.size())/2.0)*19+1;

	 	JPanel panelAtt = null;
	 	if(flexible) panelAtt = new JPanel(new GridLayout(Math.max(1,complexAttractors.size()),1));
	 	else panelAtt = new FixedSizePanel(10,mheight);
	 	
	 	int i=0;
	 	for(String key : complexAttractors.keySet()){
	 		JButton button = new JButton("Attractor "+(i++));
	 		button.addActionListener(new IndexableActionListener(key,parent) {
	 			public void actionPerformed(ActionEvent arg0) {
	 		        //HierarchicalTransitionGraph graph = GraphManager.getInstance().getNewGraph(HierarchicalTransitionGraph.class,model.getNodeOrder());
	 		        //RegulatoryGraph graph = GraphManager.getInstance().getNewGraph(RegulatoryGraph.class,model.getNodeOrder());
	 				//ReducedGraph graph = GraphManager.getInstance().getNewGraph(ReducedGraph.class,model.getNodeOrder());
	 				DynamicGraph graph = GraphManager.getInstance().getNewGraph(DynamicGraph.class,model.getNodeOrder());
	 				graph = SimulationUtils.getGraphFromAttractor(graph,res.complexAttractors.get(this.getKey()),model);
	 			    //GUIManager.getInstance().whatToDoWithGraph(graph, true);
	 			    Frame f = new WhatToDoWithGraph(graph);
	 			    f.setVisible(true);
	 			}
	 		});
	 		if(!flexible) button.setBounds(5, yshift, 300, 20);
	 		panelAtt.add(button);
	 		yshift+=20;
	 	}
	 	if(complexAttractors.size()==0){
	 		JLabel label = new JLabel("    Complex attractors were not found!");
	 		if(!flexible) label.setBounds(5, 10, 300, 22);
	 		panelAtt.add(label);
	 	} else {
	 		//for(List<byte[]> att : res.complexAttractorPatterns.values()) System.out.println(">>"+AvatarUtils.toString(att));
	    	Map<Boolean,List<NamedState>> oracles = parent.statestore.addOracle(res.complexAttractorPatterns);
	    	parent.states.updateParam(parent.statestore,oracles);
	    	AvatarParameters p = AvatarParametersHelper.load(parent);
	    	parent.setCurrent(p);
	 		parent.refresh(p);
	 	}
	 	if(flexible){
	 		g.gridx=0;
	 		g.gridy=2;
	 		g.weightx=1;
	 		g.gridheight=1;
	 		//attractors.setPreferredSize(new Dimension(560, 80));
	 		panelAtt.setBorder(new TitledBorder(new LineBorder(purple,2), "Draw complex attractors", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		 	output.add(panelAtt,g);
	 	} else {
		 	JScrollPane attractors = new JScrollPane(panelAtt);
		 	attractors.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		 	attractors.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		 	attractors.setBorder(new TitledBorder(new LineBorder(purple,2), "Draw complex attractors", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		 	attractors.setBounds(5,395,560,100);
		 	output.add(attractors);
	 	}
	 } catch(Exception e){
	 		String fileErrorMessage = "Unfortunately we were not able to finish your request.<br><em>Reason:</em> Exception while displaying the complex attractors.";
	 		errorDisplay(fileErrorMessage,e);
	 		stop.setEnabled(false);
	 		brun.setEnabled(true);
	 		e.printStackTrace();
	 		return;
	 }   
	 try {
	 	/** F: View Log **/
	 	JButton logButton = new JButton("View Log");
	 	if(!quiet){
	 		logButton.addActionListener(new IndexableActionListener("",parent) {
	 			public void actionPerformed(ActionEvent arg0) {
	 				JTextPane logText = new JTextPane();
	 				logText.setContentType("text/html");
	 				logText.setText("<html>"+res.logToHTMLString()+"</html>");
	 				logText.setEditable(false);
	 				logText.setCaretPosition(0);
	 				JScrollPane scrollLog = new JScrollPane(logText);
	 				scrollLog.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
	 				scrollLog.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
	 				scrollLog.setPreferredSize(new Dimension(700,500));
	 				//scrollLog.setBorder(new TitledBorder(new LineBorder(purple,2), "Output", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
	 				int dialogResult = JOptionPane.showOptionDialog(this.getDialog(), scrollLog,"Log Messages", JOptionPane.YES_OPTION, 
	 						JOptionPane.INFORMATION_MESSAGE, null, new String[]{"SAVE","CLOSE"}, "default");
	 				if(dialogResult == JOptionPane.YES_OPTION){
	 					JFileChooser fcnet = new JFileChooser();
	 					fcnet.setFileSelectionMode(JFileChooser.FILES_ONLY);
	 					fcnet.setSelectedFile(logFile);
	 					int returnVal = fcnet.showSaveDialog(parent);
	 					if(returnVal == JFileChooser.APPROVE_OPTION){
	 						logFile = fcnet.getSelectedFile();
	 						if(returnVal == 0)
	 						try {
	 							BufferedWriter writer = new BufferedWriter(new FileWriter(logFile));
	 						    writer.write(res.log);
	 							writer.close();
	 							JOptionPane.showMessageDialog(this.getDialog(), new JLabel("Your log was successfully saved"),"Saving log...",JOptionPane.PLAIN_MESSAGE);
	 						} catch (IOException e) {
	 							JOptionPane.showMessageDialog(this.getDialog(), new JLabel("Ops! We were not able to save your log. Please contact the GINsim team!"),"Saving log...",JOptionPane.ERROR_MESSAGE);
	 							e.printStackTrace();
	 						}
	 						//System.out.println(">>"+memorizedFile.getAbsolutePath());
	 					}
	 				}
	 			}
	 		});
	 	}
	 	if(!quiet){
	 		if(flexible){
		 		g.gridx=0;
		 		g.gridy=5;
		 		g.weightx=0;
		 		g.gridheight=0;
		 		g.weighty=0;
			 	output.add(logButton,g);
	 		} else {
		 		logButton.setBounds(8, 500, 100, 20);
			 	output.add(logButton);
	 		}
	 	}
	 } catch(Exception e){
	 		String fileErrorMessage = "Unfortunately we were not able to finish your request.<br><em>Reason:</em> Exception while gathering or displaying the log.";
	 		errorDisplay(fileErrorMessage,e);
	 		stop.setEnabled(false);
	 		brun.setEnabled(true);
	 		e.printStackTrace();
	 		return;
	 }
	 	
	 /** D: Sizes and so forth **/
	 //JOptionPane.showMessageDialog(this, output);
	 JButton jButton1 = new JButton("Close");
	 jButton1.addActionListener(new IndexableActionListener(output) {
	 	    public void actionPerformed(ActionEvent e){
	 	       this.getFrame().dispose();
	 	    }
	 	});
	  if(flexible){
	 		g.gridy=6;
	 		g.weightx=0;
	 		g.gridheight=0;
	 		g.ipadx=10;
	 		g.anchor = GridBagConstraints.EAST;
	 		g.fill = GridBagConstraints.NONE;
  		  output.add(jButton1,g);
	  } else {
		  jButton1.setBounds(483, 504, 80, 25);
		  output.add(jButton1);
	  }
      output.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	  output.setVisible(true);	  
      //for(Frame f : Frame.getFrames()) System.out.println("#"+f.toString());
   }

	/**
	 * Displays a message of error whenever a simulation is not successfully executed
	 * @param errorMessage the message to be display
	 * @param e the exception whose stack trace is to be printed in the log of ginsim
	 */
	public static void errorDisplay(String errorMessage, Exception e) {
		JTextPane output = new JTextPane();
		output.setContentType("text/html");
		String errorFull = errorMessage+"\nException:\n"+e.getMessage()+"\n\n";
		for(StackTraceElement el : e.getStackTrace()) errorFull += el.toString()+"\n";
		output.setText("<html>"+errorMessage+"</html>");//"<html>"+text+"</html>");
		LogManager.debug(errorFull);
		LogManager.error(errorFull);
		LogManager.info(errorFull);
		LogManager.trace(errorFull);
		output.setEditable(false);
		output.setCaretPosition(0);
		JScrollPane scrollPaneoutput = new JScrollPane(output);
		scrollPaneoutput.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollPaneoutput.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPaneoutput.setPreferredSize(new Dimension(300,100));
		JOptionPane.showMessageDialog(null, scrollPaneoutput, "Error", JOptionPane.INFORMATION_MESSAGE);
	}
}
