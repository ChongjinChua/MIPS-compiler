grammar MicroGrammar;
/*
@member {
	protected void mismatch(IntStream input, int ttype) throws RecognitionException {
	System.out.println("In member.\n");
		  throw new MismatchedTokenException(ttype, input);
	}
}*/
@rulecatch {
	catch (RecognitionException e) {	
	System.out.println(e);
		throw e;
	}
}

/*------------------------------------------------------------------
 * LEXER RULES
 *------------------------------------------------------------------*/

WHITESPACE
        : (['\t'|'\n'|' '|'\r'])+ -> skip;

INTLITERAL
        : ([0-9])+;

STRINGLITERAL
        :  ('"')(~(['--']|['"']))*('"') | ('"')OPERATOR('"') ;

KEYWORD
        :('PROGRAM')|('BEGIN')|('END')|('FUNCTION')|('READ')|('WRITE')|('IF')|('ELSIF')|('ENDIF')|('DO')|('WHILE')|('CONTINUE')|('BREAK')|('RETURN')|('INT')|('VOID')|('STRING')|('FLOAT')|('TRUE')|('FALSE');

IDENTIFIER
        : ([A-Za-z])([A-Za-z]|[0-9])*;

//BracketStart : '(' | '{';
//BracketEnd : ')' | '}';
//BRACKETS
//      : ([BracketStart|BracketEnd])*;

OPERATOR
        //: ('(' | '{' | ')' | '}')|(':=')|[\+]|[\-]|[\*]|[/]|['!=']|['=']|[<]|[>]|[\(]|[\)]|[;]|[,]|[<=]|[>=];
        : ('=') | (('(' | '{' | ')' | '}')|(':=')|[\+]|[\-]|[\*]|[/]|('!=')|[<]|[>]|[\(]|[\)]|[;]|[,]|('<=')|('>='));

FLOATLITERAL
        : [0-9]*[\.][0-9]+;

COMMENT
        :('--'.*?'\n') -> skip; //? makes it non-greedy

/* Program */
program 	: 'PROGRAM' id 'BEGIN' pgm_body 'END' 
	{/*FunctionList.FuncList.get(1).FuncIR.PrintList()*/}
	{Liveness.DoIt();}
	{FunctionList.PrintFunctions();}
	{System.out.println(";tiny code");}
	{TinyGeneration.buildTiny();}
	;
id 			: IDENTIFIER;
pgm_body	: 
	{SymbolTable.createScope("GLOBAL");}
	{FunctionObject func = new FunctionObject("GLOBAL");}
	{FunctionList.FuncList.add(func);} 
		decl 
	{/*SymbolTable.printSymbolTable();*/} 
		func_declarations;
decl  		: (string_decl_list decl | var_decl_list decl) decl | ;

/* Global String Declaration */
string_decl_list  : string_decl +;
string_decl       : 'STRING' id ':=' str ';' 
	{SymbolTable.insertSymbol($id.text, "STRING", $str.text);}
	{FunctionObject func = FunctionList.ListPeek();}
	{/*System.out.println("In Funcob");*/}
	{func.isLocal = 1;}
	{func.insertSymbol($id.text, "STRING", $str.text);}
	{func.isLocal = 1;}
	;
str               : STRINGLITERAL;
//string_decl_tail  : string_decl;

/* Variable Declaration */
var_decl_list     : var_decl+; //
var_decl          : var_type id_list ';'
	{SymbolTable.insertSymbol($id_list.text, $var_type.text, "");}
	{FunctionObject func = FunctionList.ListPeek();}
	{func.isLocal = 1;}
	{func.insertSymbol($id_list.text, $var_type.text, "");}
	{func.isLocal = 0;} 
	;
var_type          : 'FLOAT' | 'INT';
any_type          : var_type | 'VOID' ; 
id_list           : id id_tail;
id_tail           : (',' id)*;
//var_decl_tail   : var_decl;

/* Function Paramater List */
param_decl_list   : param_decl param_decl_tail;
param_decl        : var_type id 
	{SymbolTable.insertSymbol($id.text, $var_type.text, "");}
	{FunctionObject func = FunctionList.ListPeek();}
	{FunctionObject.curr_par_count++;}
	{func.insertSymbol($id.text, $var_type.text, "");} 
	;
param_decl_tail   : (',' param_decl )*;

/* Function Declarations */
func_declarations : func_decl *;
func_decl         : 
		'FUNCTION' any_type id 
			{SymbolTable.createScope($id.text);}
			{FunctionObject.curr_par_count=0;}
			{FunctionObject func = new FunctionObject($id.text);}
			{ValueRegister.registers.clear();}
			{FunctionList.FuncList.add(func);}
		'(' param_decl_list? ')'
			{func.par_count=FunctionObject.curr_par_count;}
			{/*System.out.println("Func name: "+func.name+" Par_Count = "+func.par_count);*/}
		'BEGIN' func_body 'END'
			{SymbolTable.popScope();}
			{FunctionList.top = FunctionList.ListPeek();}
			{ExprStack.CheckReturn();} 
		;
func_decl_tail    : func_decl * ;
func_body         : decl //goes back to decl on top
			{FunctionList.top = FunctionList.ListPeek();}
			{ExprStack.addLinkLabel();}
			{/*SymbolTable.printSymbolTable();*/}
		stmt_list ;

/* Statement List */
stmt_list         : stmt * ;
stmt_tail         : stmt * ;
stmt              : base_stmt | if_stmt | do_while_stmt;
base_stmt         : assign_stmt | read_stmt | write_stmt | return_stmt;

/* Basic Statements */
assign_stmt       : assign_expr ';' ;
assign_expr       : id ':=' expr 
		{FunctionList.top = FunctionList.ListPeek();}
		{ExprStack.addLIdentifier($id.text);
}		{ExprStack.evaluateExprIR();}
		;
read_stmt         : 'READ' '(' id_list ')' ';'{FunctionList.top = FunctionList.ListPeek();ExprStack.addReadIR($id_list.text);};
write_stmt        : 'WRITE' '(' id_list ')' ';'{FunctionList.top = FunctionList.ListPeek();ExprStack.addWriteIR($id_list.text);};
return_stmt       : 'RETURN' expr ';'{FunctionList.top = FunctionList.ListPeek();ExprStack.addReturn();};

/* Expressions */
expr              : factor expr_tail;
expr_tail         : (addop factor {FunctionList.top = FunctionList.ListPeek();ExprStack.addOperatorIR($addop.text);})* ;
factor            : postfix_expr factor_tail;
factor_tail       : (mulop postfix_expr {FunctionList.top = FunctionList.ListPeek();ExprStack.addOperatorIR($mulop.text);})*;
postfix_expr      : primary | call_expr {FunctionList.top.push_cnt = 0;};
call_expr         : id {FunctionList.top = FunctionList.ListPeek();FunctionList.top.push_cnt = 0;}'(' expr_list? ')'{FunctionList.top = FunctionList.ListPeek();ExprStack.addFunctionCall($id.text);};
expr_list         : expr {FunctionList.top = FunctionList.ListPeek();ExprStack.addPushCall($expr.text);}expr_list_tail;
expr_list_tail    : (',' expr {FunctionList.top = FunctionList.ListPeek();ExprStack.addPushCall($expr.text);})*;
primary           : ('('expr')') | id {FunctionList.top = FunctionList.ListPeek();ExprStack.addRIdentifier($id.text);} | INTLITERAL {FunctionList.top = FunctionList.ListPeek();ExprStack.addLiteralIR($INTLITERAL.text);} | FLOATLITERAL {FunctionList.top = FunctionList.ListPeek();ExprStack.addLiteralIR($FLOATLITERAL.text);};
addop             : '+' | '-' ;
mulop             : '*' | '/' ;

/* Complex Statements and Condition */
if_stmt     : {/* Start of new IF block, so need to add store new end label*/} 
			'IF'
			{FunctionList.top = FunctionList.ListPeek();ExprStack.pushLabel();}
			{FunctionList.top = FunctionList.ListPeek();ExprStack.pushLabel();}
			'(' cond ')' 
			  {SymbolTable.createScope("BLOCK");}
			decl
			  {SymbolTable.popScope();}
			stmt_list 
			  {FunctionList.top = FunctionList.ListPeek();ExprStack.addJumpIR(ExprStack.peekSecondTopLabel());}
			  {FunctionList.top = FunctionList.ListPeek();ExprStack.addLabelIR(ExprStack.popLabel());}
			else_part
			'ENDIF' 
			  {FunctionList.top = FunctionList.ListPeek();ExprStack.addLabelIR(ExprStack.popLabel());}
			;
else_part   : ( 'ELSIF'
			{/*ExprStack.addLabelIR(ExprStack.popLabel());*/}
			{FunctionList.top = FunctionList.ListPeek();ExprStack.pushLabel();}
			 '(' cond ')' 
			{SymbolTable.createScope("BLOCK");}
			 decl 
			{SymbolTable.popScope();}
			 stmt_list 
			{FunctionList.top = FunctionList.ListPeek();ExprStack.addJumpIR(ExprStack.peekSecondTopLabel());}
			{FunctionList.top = FunctionList.ListPeek();ExprStack.addLabelIR(ExprStack.popLabel());}
			 
			 )* 
			;
cond        : expr compop expr {ExprStack.addInvConditionalIR($compop.text);}  
			| 'TRUE' {ExprStack.addTrueIR();}
			| 'FALSE' {ExprStack.addFalseIR();}
			;
compop      : '<' | '>' | '=' | '!=' | '<=' | '>=' ;

/* ECE 468 students use this version of do_while_stmt */
do_while_stmt       : 'DO' 
			{SymbolTable.createScope("BLOCK");}
			{FunctionList.top = FunctionList.ListPeek();ExprStack.pushLabel();}
			{FunctionList.top = FunctionList.ListPeek();ExprStack.addLabelIR(ExprStack.peekTopLabel());}
			 decl 
			stmt_list 'WHILE' 
			/*{ExprStack.addLabelIR(ExprStack.popLabel());}*/
			{SymbolTable.popScope();} 
			 '(' 
			{ExprStack.pushLabel();}
			 cond
			{ExprStack.addJumpIR(ExprStack.peekSecondTopLabel());}
			{ExprStack.addLabelIR(ExprStack.popLabel());}
			 ')' ';' ;
