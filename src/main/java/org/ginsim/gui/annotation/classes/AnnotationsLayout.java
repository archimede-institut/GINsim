package org.ginsim.gui.annotation.classes;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.colomoto.biolqm.LogicalModel;
import org.colomoto.biolqm.NodeInfo;
import org.colomoto.biolqm.io.LogicalModelFormat;
import org.colomoto.biolqm.metadata.annotations.Metadata;
import org.colomoto.biolqm.service.LQMServiceManager;

/**
 * Class used to test the GUI to manage annotations
 *
 * @author Martin Boutroux
 */
public class AnnotationsLayout {

    public AnnotationsLayout()
    {
        JFrame frame = new JFrame();
        frame.setTitle("Annotations module");
        //frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        
        int width = (int) screenSize.getWidth();
        
        int height = (int) ((0.7) * screenSize.getHeight());
        
        frame.setSize(new Dimension(width, height));
        
        JPanel mainPane = new JPanel();
        frame.setContentPane(mainPane);
        
        mainPane.setLayout(new GridLayout(2, 1));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridx = 0;
        gbc.gridy = 0;
        
        JPanel modelPane = new JPanel();
        modelPane.setLayout(new GridBagLayout());
        
        GridBagConstraints gbcModel = new GridBagConstraints();
        gbcModel.weightx = 1.0;
        gbcModel.weighty = 1.0;
        gbcModel.fill = GridBagConstraints.BOTH;
        gbcModel.gridx = 0;
        gbcModel.gridy = 0;
        
        mainPane.add(modelPane);
        
        gbc.gridy = 1;
        final JPanel annotationsPane = new JPanel();
        annotationsPane.setLayout(new BorderLayout());
        mainPane.add(annotationsPane);
        
        
        // inner panels
        final JPanel components = new JPanel();
        RelativeLayout rl = new RelativeLayout(RelativeLayout.Y_AXIS);
        components.setLayout(rl);
        modelPane.add(components, gbcModel);
        
        gbcModel.gridx = 1;
        JPanel buttons = new JPanel();
        buttons.setLayout(rl);
        modelPane.add(buttons, gbcModel);
        
        JButton loadModel = new JButton("Load SBML model");
        buttons.add(Box.createRigidArea(new Dimension(0,15)));
        buttons.add(loadModel);
        
        JButton loadAnnotations = new JButton("Load JSON annotations");
        buttons.add(Box.createRigidArea(new Dimension(0,15)));
        buttons.add(loadAnnotations);
        
        JButton saveModel = new JButton("Save SBML model");
        buttons.add(Box.createRigidArea(new Dimension(0,15)));
        buttons.add(saveModel);
        
        JButton saveAnnotations = new JButton("Save JSON annotations");
        buttons.add(Box.createRigidArea(new Dimension(0,15)));
        buttons.add(saveAnnotations);
        
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		
		// actions
		final LogicalModel[] model = new LogicalModel[1];
		final String[] nodeClicked = new String[1];
		
		loadModel.addActionListener(new ActionListener(){  
	    	public void actionPerformed(ActionEvent e) {
	    		
	    		components.removeAll();
	    		components.revalidate();
	    		components.repaint();
	    		annotationsPane.removeAll();
				annotationsPane.revalidate();
				annotationsPane.repaint();
	    		
	    		LogicalModelFormat format = LQMServiceManager.getFormat("sbml");
	    		
				try {
				    JFileChooser chooser = new JFileChooser(System.getProperty("user.dir"));
				    FileNameExtensionFilter filter = new FileNameExtensionFilter(
				        "SBML files", "sbml");
				    chooser.setFileFilter(filter);
				    int returnVal = chooser.showOpenDialog(null);
				    if (returnVal == JFileChooser.APPROVE_OPTION) {
				    	System.out.println("You chose to open this file: " +
				            chooser.getSelectedFile().getName());
				    	
				       	model[0] = format.load(new File(chooser.getSelectedFile().getName()));
				       	
						JButton modelButton = new JButton("Model");
						components.add(Box.createRigidArea(new Dimension(0,15)));
						components.add(modelButton);
						
						modelButton.addActionListener(new ActionListener(){  
							public void actionPerformed(ActionEvent e) {
								Metadata modelMetadata = model[0].getMetadataOfModel();
								JPanel modelAnnotations = new AnnotationsComponent(modelMetadata, false);
								
								annotationsPane.removeAll();
								annotationsPane.add(modelAnnotations, BorderLayout.CENTER);
								annotationsPane.revalidate();
								annotationsPane.repaint();
								
								nodeClicked[0] = "Model";
							}
						});
						
						JButton nodeButton;
						for (final NodeInfo elementNode: model[0].getComponents()) {
							final String nodeId = elementNode.getNodeID();
							nodeButton = new JButton(nodeId);
							
							components.add(Box.createRigidArea(new Dimension(0,10)));
							components.add(nodeButton);
							
							nodeButton.addActionListener(new ActionListener(){  
						    	public void actionPerformed(ActionEvent e) {
						    		Metadata nodeMetadata;
									try {
										nodeMetadata = model[0].getMetadataOfNode(elementNode);
										
							    		JPanel nodeAnnotations = new AnnotationsComponent(nodeMetadata, false);
							    		
							    		annotationsPane.removeAll();
							    		annotationsPane.add(nodeAnnotations, BorderLayout.CENTER);
							    		annotationsPane.revalidate();
							    		annotationsPane.repaint();
							    		
							    		nodeClicked[0] = nodeId;
									} catch (Exception e1) {
										// TODO Auto-generated catch block
										e1.printStackTrace();
									}
						    	}
						    });
						}
						
						components.revalidate();
				    }
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
	    	}
	    });
		
		loadAnnotations.addActionListener(new ActionListener(){  
	    	public void actionPerformed(ActionEvent e) {
	    		
				try {
				    JFileChooser chooser = new JFileChooser(System.getProperty("user.dir"));
				    FileNameExtensionFilter filter = new FileNameExtensionFilter(
				        "JSON files", "json");
				    chooser.setFileFilter(filter);
				    int returnVal = chooser.showOpenDialog(null);
				    if (returnVal == JFileChooser.APPROVE_OPTION) {
				    	System.out.println("You chose to open this file: " +
				            chooser.getSelectedFile().getName());
				    	
				       	model[0].importMetadata(chooser.getSelectedFile().getName());
				       
				        Component[] modelandnodes = components.getComponents();

				        // Reset user interface
				        for(int i=0; i<modelandnodes.length; i++) {
				        	
				            if (modelandnodes[i] instanceof JButton) {
				                JButton button = (JButton) modelandnodes[i];
				                
				                if (button.getText().equals(nodeClicked[0])) {
				                	button.doClick();
				                }
				            }
				        }
				    }
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
	    	}
	    });
		
		saveModel.addActionListener(new ActionListener(){  
	    	public void actionPerformed(ActionEvent e) {
	    		
				try {
				    JFileChooser chooser = new JFileChooser(System.getProperty("user.dir"));
				    FileNameExtensionFilter filter = new FileNameExtensionFilter(
				        "SBML files", "sbml");
				    chooser.setFileFilter(filter);
				    int returnVal = chooser.showSaveDialog(null);
				    if (returnVal == JFileChooser.APPROVE_OPTION) {
				    	System.out.println("You chose to save this file under the name: " +
				            chooser.getSelectedFile().getName());
				    	
				    	LQMServiceManager.save(model[0], chooser.getSelectedFile().getName(), "sbml");
				    }
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
	    	}
	    });
		
		saveAnnotations.addActionListener(new ActionListener(){  
	    	public void actionPerformed(ActionEvent e) {
	    		
				try {
				    JFileChooser chooser = new JFileChooser(System.getProperty("user.dir"));
				    FileNameExtensionFilter filter = new FileNameExtensionFilter(
				        "JSON files", "json");
				    chooser.setFileFilter(filter);
				    int returnVal = chooser.showOpenDialog(null);
				    if (returnVal == JFileChooser.APPROVE_OPTION) {
				    	System.out.println("You chose to save the annotations under the name: " +
				            chooser.getSelectedFile().getName());
				    	
				       	model[0].exportMetadata(chooser.getSelectedFile().getName());
				    }
				} catch (Exception e1) {
					e1.printStackTrace();
				}
	    	}
	    });
    }
    
    public static void main(String[] args) {
        new AnnotationsLayout();
    }
}

