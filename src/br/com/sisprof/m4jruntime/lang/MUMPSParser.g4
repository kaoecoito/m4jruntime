parser grammar MUMPSParser;
options { tokenVocab=MUMPS2Lexer; }

file: entryPoint line+ EOF
    | line+ EOF
;

line
	: NL entryPoint cmd+  // single-line entry point w/ commands
	| NL entryPoint       // entry point-only line
	| NLI DOT* cmd*      // typical line (from routine file)
	| NL? entryPoint DOT* cmd*
	| NL? DOT* cmd+		     // typical line (from console)
	| NL
;

lines: line+; // for console/tests to parse multiple lines

//TODO: Call this a lineTag instead?
entryPoint
	: {getCurrentToken().getCharPositionInLine()==0}? name=ID LP entryPointArgs? RP
	| {getCurrentToken().getCharPositionInLine()==0}? name=ID
;

entryPointArgs: entryPointArg (COMMA entryPointArg)*;
entryPointArg: ID ( OPER literal)?;

// command structure
cmd
	: ID cmdPostCond // weird issue with "Q:I=10 RET", having this at the top makes the full expression go to the pce
	| ID cmdPostCond? expr (COMMA expr)* // regular command with expression list
	| ID cmdPostCond? nameIndex              // for commands with arguments like OPEN, FOR, USE, CLOSE, READ, etc.
	| ID cmdPostCond?                  // command with no expressions/arguments
;
cmdPostCond: ':' expr;

// expression structure
expr
	: literal      #ExprLiteral
	| format       #ExprFormat
	| func         #ExprFunc
	| var          #ExprVar
	| ref          #ExprRef
	| AT LP expr RP (AT LP nameIndex? RP)? #ExprIndrExpr
	| AT LP expr RP (LP nameIndex? RP)? #ExprIndrExpr
	| AT ref (AT LP nameIndex? RP)? #ExprIndrRef
	| AT var (AT LP nameIndex? RP)? #ExprIndrVar
	| AT func (AT LP nameIndex? RP)? #ExprIndrFunc
	| OPER expr     #ExprUnary
	| expr OPER expr #ExprBinary
	| expr (MATCH | NOT_MATCH) exprPatternItem+ #ExprMatch
	| LP expr? RP #ExprGroup
	| LP expr (COMMA expr)* RP #ExprList // for S (A,B,C)=1 style commands
	| lineRef cmdPostCond?    #ExprLineRef
;

literal : STR_LITERAL | NUM_LITERAL;
format: OPER* '?' PAT_INT | ID | OPER+; // format control characters for READ, WRITE

lineRef
	: tag=ID (OPER n=NUM_LITERAL)? ('^' routine=ID)?  // line label reference for $T(TAG+N^ROUTINE), GO F1: command, etc.
	| tag=ID OPER LP expr RP ('^' routine=ID)? 
;

func: flags='$' name=ID LP nameIndex? RP;

// variable reference (global or local) or special system variables
var
	: flags=(DOT | '^')? namespace? ID LP nameIndex RP // variable reference (local or global) w/ subscripts
	| flags=(DOT | '^')? namespace? ID            // variable reference (local or global) wo/ subscripts
	| flags='^' LP nameIndex RP             // naked global reference
	| flags='$' ID  // special variable ($H, etc.)
;

// ref represents a reference to routine, function, etc.
ref
	: flags='$$' ep=ID               // call entry point within current routine
	| flags='$$' ep=ID LP nameIndex? RP // call entry point within current routine (w/nameIndex)
	| flags='$$'? ep=ID? '^' routine=ID LP nameIndex? RP // call routine w/ nameIndex
	| flags='$$'? ep=ID? '^' routine=ID 			     // call routine wo/ nameIndex
;
nameIndex
	: expr (COMMA expr?)* // normal comma separated list of arguments
	| expr (':' expr?)*   // for command nameIndex like READ, FOR, etc. separated by :'s
	| expr ':' expr (COMMA expr ':' expr)* // GO style nameIndex
;

namespace
    : '|' STR_LITERAL '|'
    | '|' ID '|'
;

exprPatternItem
	: PAT_INT (PAT_CODES | PAT_LITERAL)      // X?1"FOO"
	| PAT_INT DOT (PAT_CODES | PAT_LITERAL)  // X?1."F"
	| PAT_DOT (PAT_CODES | PAT_LITERAL)      // X?.N
	| PAT_DOT PAT_INT (PAT_CODES | PAT_LITERAL)   // X?.1"-" 
	| PAT_INT // W ?10,"INDENTED WRITE"
	| AT expr // pattern indirection (S zipPat="5N1""-""4N"	I zip'?@zipPat W "invalid zip")
;

