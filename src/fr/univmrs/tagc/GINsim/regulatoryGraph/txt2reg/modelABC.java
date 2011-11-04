package fr.univmrs.tagc.GINsim.regulatoryGraph.txt2reg;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import fr.univmrs.tagc.GINsim.data.GsDirectedEdge;
import fr.univmrs.tagc.GINsim.global.GsWhatToDoFrame;
import fr.univmrs.tagc.GINsim.graph.GsGraphManager;
import fr.univmrs.tagc.GINsim.regulatoryGraph.GsLogicalParameter;
import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryGraph;
import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryMultiEdge;
import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryVertex;


/**
 * FIXME: this whole file was commented out
 * Now it compiles and will be refactored with the rest but is it worth it?
 */
public final class modelABC {

	private GsRegulatoryGraph graph;
	private String fileName;
	protected File _FilePath;

	private byte table_gene[][];           // the whole table including all states and their images
	private List table_interactors[];      // store for each component, its regulators id
	private 	int L,n;                   // L total number of lines of the table ie number of states, n number of components
	private GsRegulatoryGraph model;


	public modelABC() {
	}

	public modelABC(String f) 
	{
		this.graph = new GsRegulatoryGraph();
		this.fileName = f;
		initialize();
	}

	/**  Parsing text file to define the truth table */


	public void initialize() {
		// ----------------------------------------------------------------------------------------------------------------------------------

		// declarations
		int i, j;

		// read the file and define the table
		String line;
		String states [] ;                         // a state and its image


		try {
			BufferedReader entree = new BufferedReader (new FileReader(fileName)); 

			L=18;// number of lines
			n=3;// number of components
			table_gene=new byte[L][2*n];
			i=0;


			// read the remaining lines to fill the table
			while (((line = entree.readLine()) != null)&& (line.length()>1))
			{
				states=line.split(" ");                    // state and its image state
				for ( j = 0; j < n; j++) 
				{ 
					table_gene[i][j]=(byte)Character.getNumericValue(states[0].charAt(j)); 
					table_gene[i][j+n]= (byte)Character.getNumericValue(states[1].charAt(j));
				}   
				i++;		

			}
			entree.close();			

			// to control... print the table
			for ( i = 0; i < L; i++) 
			{
				for ( j = 0; j < 2*n; j++)
				{
					System.out.print(table_gene[i][j]+ " ");
				} 
				System.out.println();
			}  

		} // close the try

		catch (Exception e){};    
		defineModel();
		// propose to display the new graph 
		new GsWhatToDoFrame(null, model, true);
	}				



	// ----------------------------------------------------------------------------------------------------------------------------------
	private void defineModel(){
		int i, j, l, k;
		List incoming_edges[]; 
		int L1;
		List listOfParam=null;


		// ----------------------------------------------------------------------------------------------------------------------------------

		// create a simple graph
		model = new GsRegulatoryGraph();

		// ----------------------------------------------------------------------------------------------------------------------------------			


		// add  vertices
		GsRegulatoryVertex [] G = new GsRegulatoryVertex [n];

		for (j=0; j<n; j++)
		{
			G[j] = (GsRegulatoryVertex)model.interactiveAddVertex(0, j, 2*j);
		}

		// ----------------------------------------------------------------------------------------------------------------------------------		


		// search the table to define the vector m containing the max value for each component

		int m[]=new int[n];               // the max value of each component, hence the number of values of component i is m[i]+1

		for (i=0; i<n; i++) // for each component
		{
			j=0; 			
			while((j<L-1) && (table_gene[j][i]<=table_gene[j+1][i])) j++;
			m[i]=table_gene[j][i];
			G[i].setMaxValue((byte)m[i], model);

		}


		// ----------------------------------------------------------------------------------------------------------------------------------			

		// search the table to define, for each component, the size of each bloc defined as the nb of lines for which its value remains cste
		int b[]=new int[n];                //the size of the blocs

		for ( i=0;i<n;i++)
		{ 
			b[i]=1;                        // Calculation of the size of blocks
			for ( j=i+1;j<n;j++)           // All the combinations of variation of components i+!...n
			{
				b[i]=b[i]*(m[j]+1);	
			}
		}

		// search the table to define, for each component, the nb of occurrences of each series of blocs
		// o[i] is the number of times all the values of i have to be considered
		int o[]=new int[n];                //the number of occurrences of the blocs for each component
		for (i=0;i<n;i++)
		{
			o[i]=1;                        // calculation of the number of occurrences of the series of blocks
			for ( j=i-1;j>=0;j--)
			{
				o[i]=o[i]*(m[j]+1);
			}

		}
		// List of the comparisons

		incoming_edges=new List[n];
		table_interactors=new List[n];
		byte sign;
		GsGraphManager manager = model.getGraphManager();  
		for ( i=0;i<n;i++)
		{                
			incoming_edges[i]=new ArrayList(); //instantiate one list for each component to store the incoming edges
			table_interactors[i]=new ArrayList();
		}
		for ( i=0;i<n;i++)              // for each component i, we want to determine its influence to determine all the interactions
		{
			for ( j=0;j<o[i];j++)    // for each occurrence of the series of blocks
			{             
				for(k=0;k<m[i];k++)  // On all the values of i
				{               
					L1=(k+j*(m[i]+1))*b[i];            // the first line to run of each block 
					for ( l=L1;l<L1+b[i];l++)         // running the lines of each block 	
					{
						for( int u=n; u<2*n; u++) // running columns of images (second part of the table)
						{
							sign=-1; // sign is used to define the type of the interaction (positive/negative)

							if(table_gene[l][u]> table_gene[l+b[i]][u]) sign=1;  // negative interaction
							else if (table_gene[l][u]< table_gene[l+b[i]][u]) sign=0; // positive interaction
							if (sign!=-1) 
							{
								if ((GsDirectedEdge)manager.getEdge(G[i], G[u-n])==null)
								{
									model.interactiveAddEdge(G[i], G[u-n], sign); 
									GsRegulatoryMultiEdge me =(GsRegulatoryMultiEdge)manager.getEdge(G[i], G[u-n]);
									incoming_edges[u-n].add(me.getEdge(0)); 
									table_interactors[u-n].add(i);
								}
							}

						}	
					}
				}
			}
		}

		// defining the logical parameters
		for (i=0; i<n; i++) 
		{
			int deg=incoming_edges[i].size();
			for (j=0; j<L; j++) //search for the lines where the target value of i is not null
			{
				if (table_gene[j][i+n]== 1)  
				{
					List activeInteractions=new ArrayList();
					// search for the active interactions ie for the components, sources of incoming edge, which value is one in state of line j
					for(k=0;k<deg;k++)
					{
						int reg=((Integer)table_interactors[i].get(k)).intValue(); // get the index of the k.th regulator of i
						if (table_gene[j][reg]==1 )  
						{	 
							GsRegulatoryMultiEdge me = (GsRegulatoryMultiEdge)manager.getEdge(G[reg], G[i]);
							activeInteractions.add(me.getEdge(0));
						}

					}

					listOfParam = G[i].getV_logicalParameters();  
					if (!listOfParam.contains(activeInteractions)) //check if the parameter already exists in the list if not create it 
					{ 					 
						GsLogicalParameter newParam = new GsLogicalParameter(activeInteractions,1); 
						G[i].addLogicalParameter((newParam),true); 

					}
				}
			}	

		}

		//----------test-----------

		for (i=0; i<n; i++) 
		{
			int deg=incoming_edges[i].size();
			for (j=0; j<L; j++) 
			{
				if (table_gene[j][i+n]!=0)  
				{
					List activeInteractions=new ArrayList();
					for(k=0;k<deg;k++)
					{
						int reg=((Integer)table_interactors[i].get(k)).intValue(); // get the index of the k.th regulator of i
						if (table_gene[j][reg]==2 )  
						{	 
							GsRegulatoryMultiEdge me =(GsRegulatoryMultiEdge)manager.getEdge(G[reg], G[i]);
							activeInteractions.add(me.getEdge(0));
						}
					}

					listOfParam = G[i].getV_logicalParameters();  
					if (!listOfParam.contains(activeInteractions)) //check if the parameter already exists in the list if not create it 
					{ 					 
						GsLogicalParameter newParam = new GsLogicalParameter(activeInteractions,2); 
						G[i].addLogicalParameter((newParam),true); 

					}
				}
			}	

		}
	}
}