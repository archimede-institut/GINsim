package org.ginsim.service.export.cadp;

import java.util.List;

import org.ginsim.core.graph.regulatorygraph.RegulatoryNode;


/**
 * 
 * Writer class for common.lnt including general functions
 * and type definitions, based on the maximum value of
 * the multi-valued components, if any are referred to in the
 * model specification
 * 
 * @author Nuno D. Mendes
 *
 */
public class CADPCommonWriter {
	private CADPExportConfig config;
	
	public CADPCommonWriter(CADPExportConfig config){
		this.config = config;
	}
	
	public String toString(){
		List<RegulatoryNode> listNodes = config.getGraph().getNodeOrder();
		int maxValue = 0;
		for(RegulatoryNode node : listNodes)
			if (node.getMaxValue() > maxValue)
				maxValue = node.getMaxValue();
		
		boolean hasMulti = false;
		if (maxValue > 1)
			hasMulti = true;
		
		String output = "";
		
		output += "module common is\n\n";
		output += "type Binary is range 0..1 of Nat with \"==\", \"!=\", \">\", \"<\", \"<=\",\">=\"  end type\n";
		if (hasMulti)
			output += "type Multi is range 0.." + maxValue + " of Nat with \"==\", \"!=\", \">\", \"<\", \"<=\", \">=\" end type\n";

		output += "function 0B : Binary is return Binary (0) end function\n";
		output += "function 1B : Binary is return Binary (1) end function\n";
	
		if (hasMulti){
			for(int i = 0; i<= maxValue; i++){
				output += "function " + i + "M : Multi is return Multi ("+ i +") end function\n";
			}
		}
		
		output += "\n";
		
		String operations[] = {"+","-"};
		
		for (int i=0; i < operations.length; i++){
			output += "function _" + operations[i] + "_ (op1 : Binary, op2 : Nat) : Binary is\n";
			output += "\treturn Binary (Nat (op1) " + operations[i] +" op2)\n";
			output += "end function\n";
		
			output += "\n";
		}
		
		if (hasMulti){
			for (int i=0; i < operations.length; i++){
				output += "function _" + operations[i] + "_ (op1 : Multi, op2 : Nat) : Multi is\n";
				output += "\treturn Multi (Nat (op1) " + operations[i] +" op2)\n";
				output += "end function\n";
				
				output += "\n";
			}
			
		}
		
		output += "channel Binary is (Binary) end channel\n";
		if (hasMulti)
			output += "channel Multi is (Multi) end channel\n";


		output += "channel BinaryIntegration is\n";
		output += "(Binary)";
		if (hasMulti)
			output += ",\n(Multi)";
		output += ",\n(Binary,Binary)";
		if (hasMulti)
			output += ",\n(Multi,Binary)";
		output +="\nend channel\n";
		
		if (hasMulti){
			output += "channel MultiIntegration is \n";
			output += "(Multi),\n";
			output += "(Binary),\n";
			output += "(Multi, Multi),\n";
			output += "(Binary,Multi)\n";
			output += "end channel\n";
		}
		
		output += "channel None is () end channel\n";
		output += "-- other multiport combinations may be necessary\n\n";

		output += "\n";
		
		output += "process Set_regulatorB [G : Binary] (inout g : Binary, in new_g : Binary) is\n";
		output += "\tif g != new_g then\n";
		output += "\t\tG (new_g);\n";
		output += "\t\tg := new_g";
		output += "\tend if\n";
		output += "end process\n\n";
		
		if (hasMulti){
			output += "process Set_regulatorM[G: Multi] (inout g : Multi, in new_g : Multi) is\n";
			output += "\tif g != new_g then\n";
			output += "\t\tif new_g > g then\n";
			output += "\t\t\tG (g + 1);\n";
			output += "\t\t\tg := (g + 1)\n";
			output += "\t\telse\n";
			output += "\t\t\tG (g - 1);";
			output += "\t\t\tg := (g - 1)";
			output += "\t\tend if\n";
			output += "\tend if\n";
			output += "end process\n";
		}
		
		output += "\n";
		
		output += "process Input_regulatorB [U : Binary] (inout u : Binary) is\n";
		output += "\tvar Val : Binary in\n";
		output += "\t\tVal := any Binary;\n";
		output += "\t\tSet_regulatorB [U] (!?u, Val)\n";
		output += "\tend var\n";
		output += "end process\n";

		output += "\n";
		
		if (hasMulti){
			output += "process Input_regulatorM [U : Multi] (inout u : Multi) is\n";
			output += "\tvar Val : Multi in\n";
			output += "\t\tselect\n";
			output += "\t\t\tif u !="  + maxValue + "M then Val := u+1 else stop end if\n";
			output += "\t\t[] if u != 0M then Val := u-1 else stop end if\n";
			output += "\t\tend select;\n";
			output += "\t\tSet_regulatorM[U] (!?u, Val)\n";
			output += "\tend var\n";
			output += "end process\n";
			
			output += "\n";
		}

		output += "process Proper_regulatorB [G : Binary] (b : Bool, inout g : Binary, in new_g : Binary) is\n";
		output += "\tif b then\n";
		output += "\t\tSet_regulatorB [G] (!?g, new_g)\n";
		output += "\t end if\n";
		output += "end process\n";
		
		output += "\n";
		
		if (hasMulti){
			
			output += "process Proper_regulatorM [G : Multi] (b : Bool, inout g : Multi, in new_g : Multi) is\n";
			output += "\tif b then\n";
			output += "\t\tSet_regulatorM [G] (!?g, new_g)\n";
			output += "\t end if\n";
			output += "end process\n";
			
			output += "\n";
		}

		output += "process Stable_State [STABLE:None] (b:Bool) is\n";
		output += "\tif b then STABLE end if\n";
		output += "end process\n";
		
		output += "\nend module\n";
		
				
		return output;
	}

}
