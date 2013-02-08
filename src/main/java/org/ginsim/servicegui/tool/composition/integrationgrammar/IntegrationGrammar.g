grammar IntegrationGrammar;

@header {
import org.ginsim.servicegui.tool.composition.integrationgrammar.IntegrationGrammar;
}


start	:	expression;

expression  	returns [IntegrationGrammar.IntegrationExpression value]
	:	 or=disjunction { $value = $or.value; } 
	;

disjunction	returns [IntegrationGrammar.IntegrationDisjunction value]
	: c1=conjunction (OR c2=conjunction)* {$value = IntegrationGrammar.createDisjunction($c1.value,$c2.value); }
	; 
	
conjunction    returns [IntegrationGrammar.IntegrationConjunction value ]
	: a1=atom (AND a2=atom)* {$value = IntegrationGrammar.createConjunction($a1.value,$a2.value); }
	;
		
	
atom	 returns [ IntegrationGrammar.IntegrationAtom value]
	: id=ID '(' threshold=NUMBER ',' min=NUMBER ',' max=NUMBER ')' 
	{ $value = IntegrationGrammar.createIntegrationAtom($id.text,$threshold.text,$max.text,$min.text));}
	| id=ID '(' threshold=NUMBER ',' min=NUMBER ',' max=NUMBER ',' dist=NUMBER ')'
	{ $value = IntegrationGrammar.createIntegrationAtom($id.text,$threshold.text,$min.text,$max.text,$dist.text);}
	|'(' exp=expression ')' { $value = IntegrationGrammar.createIntegrationAtom($exp.value);}
	;	
	
	

NUMBER 	:	('0'..'9')+;	
ID	:	('a'..'z'|'A'..'Z'|'0'..'9'|'_'|'-')+;
OR	:	'|';
AND	:	'&';
	
	