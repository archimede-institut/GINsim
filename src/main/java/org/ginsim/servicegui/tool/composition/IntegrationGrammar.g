grammar IntegrationGrammar;

@header {
package org.ginsim.servicegui.tool.composition.integrationgrammar;
import org.ginsim.servicegui.tool.composition.integrationgrammar.IntegrationFunctionSpecification;
}


start	:	expression;

expression  	returns [IntegrationFunctionSpecification.IntegrationExpression value]
	:	 or=disjunction { $value = $or.value; } 
	;

disjunction	returns [IntegrationFunctionSpecification.IntegrationExpression value]
	: c1=conjunction (OR c2=conjunction)* {$value = IntegrationFunctionSpecification.createDisjunction($c1.value,$c2.value); }
	; 
	
conjunction    returns [IntegrationFunctionSpecification.IntegrationExpression value ]
	: a1=atom (AND a2=atom)* {$value = IntegrationFunctionSpecification.createConjunction($a1.value,$a2.value); }
	;
		
	
atom	 returns [ IntegrationFunctionSpecification.IntegrationExpression value]
	: id=ID '(' threshold=ENUMBER ',' min=ENUMBER ',' max=ENUMBER ')' 
	{ $value = IntegrationFunctionSpecification.createAtom($id.text,$threshold.text,$max.text,$min.text);}
	| id=ID '(' threshold=ENUMBER ',' min=ENUMBER ',' max=ENUMBER ',' dist=ENUMBER ')'
	{ $value = IntegrationFunctionSpecification.createAtom($id.text,$threshold.text,$min.text,$max.text,$dist.text);}
	|'(' exp=expression ')' { $value = IntegrationFunctionSpecification.createAtom($exp.value);}
	;	
	
	

ENUMBER	:	NUMBER | '_';
NUMBER 	:	('0'..'9')+;
ID	:	('a'..'z'|'A'..'Z'|'0'..'9'|'_'|'-')+;
OR	:	'|';
AND	:	'&';
	
	