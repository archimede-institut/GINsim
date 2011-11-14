package org.ginsim.gui.service.imports.txt;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

public class TT_parser { // define a regulatory graph from a table describing a dynamics stored in a file 
	// static type???? tableName; // the file identifier from the import function l'identificateur du fichier contenant la table

	static String tableName;  // pour l instant on lit le nom du fichier en ligne de cmd



	/** on lit le nom du fichier qui contient la table
	 * on lit la premiere ligne de ce fichier pour initialiser le nb de lignes L et le nb de composants n
	 * ex: 1ere ligne du fichier  6 2 --> on a 6 lignes et 2 composants donc forcement un composant qui prend les val 0..2 et un composant Bool
	 * ensuite on va lire toutes lignes suivantes du fichier en les stockant dans une table d'entiers de taille L x 2n */


	public TT_parser (String tableName) {

		int i, j, l, k;
		int L,n; // L total nb of lines of the table ie number of states, n nb of components
		int L1;

		// read the file and define the table

		String line;
		String states [] ;// a state and its image
		byte table_gene[][]; // the whole table including all states and their images

		try {
			BufferedReader entree = new BufferedReader (new InputStreamReader(System.in));		 
			tableName = entree.readLine();
			entree = new BufferedReader(new FileReader(tableName));
			// permiere ligne 2 entiers
			StringTokenizer st = new StringTokenizer(entree.readLine());
			L=(new Integer(st.nextToken())).intValue();// nb de lignes
			n=(new Integer(st.nextToken())).intValue();// nb de composants
			System.out.println("L="+L+" n="+n);		   
			table_gene=new byte[L][2*n];
			i=0;
			// read the remaining lines to fill the table
			while (((line = entree.readLine()) != null)&& (line.length()>1)) {
				states=line.split(" ");// state and its image state
				for ( j = 0; j < n; j++) { 
					table_gene[i][j]=(byte)Character.getNumericValue(states[0].charAt(j)); 
					table_gene[i][j+n]= (byte)Character.getNumericValue(states[1].charAt(j));
				}   
				i++;		

			}
			entree.close();			
			// to control... print the table
			for ( i = 0; i < L; i++) {
				for ( j = 0; j < 2*n; j++){
					System.out.print(table_gene[i][j]+ " ");
				} 
				System.out.println();
			}  
			System.out.print("............\n");

			// search the table to define the vector m containing the max value for each component

			int m[]=new int[n]; // the max value of each component, hence the number of values of component i is m[i]+1

			for (i=0; i<n; i++) { // for each component
				j=0; 			
				while((j<L-1) && (table_gene[j][i]<=table_gene[j+1][i])) j++;
				m[i]=table_gene[j][i];
			}	   


			// search the table to define, for each component, the size of each bloc defined as the nb of lines for which its value remains cste
			int b[]=new int[n]; //the size of the blocs

			for ( i=0;i<n;i++){ 
				b[i]=1;// calcul de la taille des blocs 
				for ( j=i+1;j<n;j++){// toutes les combinaisons tde vrariation des composants i+!...n
					b[i]=b[i]*(m[j]+1);	
				}
				//System.out.println("b["+i+"]" + b[i]);

			}


			// search the table to define, for each component, the nb of occurrences of each series of blocs
			// o[i] is the number of times all the values of i have to be considered
			int o[]=new int[n]; //the nb of occurrences of the blocs for eqch component
			for (i=0;i<n;i++){
				o[i]=1;// calcul du nombre d'occurrences de la serie des blocs
				for ( j=i-1;j>=0;j--){
					o[i]=o[i]*(m[j]+1);
				}
				//System.out.print("o["+i+"]" + o[i] + "\n" );
			}


			// List of the comparisons

			for ( i=0 ; i<n ; i++) { // for each component, we want to determine its influence
				for ( j=0 ; j<o[i] ; j++) {// pour chaque occurrence de serie de blocs
					for(k=0 ; k<m[i] ; k++) {//sur toutes les valeurs de i
						System.out.println("G" +i+ " at level "  +(k+1)+" has the following influences");

						L1=(k+j*(m[i]+1))*b[i]; // premiere ligne a parcourir de chaque bloc
						for ( l=L1 ; l<L1+b[i] ; l++){ //parcours des lignes de chaque bloc
							for( int u=n ; u<2*n ; u++){
								if(table_gene[l][u]> table_gene[l+b[i]][u]) {
									System.out.println("- G" +(u-n));	
								}
								else if (table_gene[l][u]< table_gene[l+b[i]][u]) {
									System.out.println(  "+ G" +(u-n)); 
								}
							}	
						}
					}
				}
			}
		}
		catch (Exception e){};
	}

}
