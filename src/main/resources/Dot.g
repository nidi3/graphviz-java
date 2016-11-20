/**
 *  An ANTLRv3 capable DOT grammar.
 *
 *  Developed from specification on http://www.graphviz.org/doc/info/lang.html
 *	This grammar is part of CesTa project, http://cesta.sourceforge.net/
 *
 *	BSD licence
 *  Copyright (c) 2010 Tobias Smolka, BUSLAB FI MUNI
 *
 *	All rights reserved.
 *
 *	http://buslab.org
 *
 *	Redistribution and use in source and binary forms, with or without
 *	modification, are permitted provided that the following conditions
 *	are met:
 *
 *	 1. Redistributions of source code must retain the above copyright
 *		notice, this list of conditions and the following disclaimer.
 *	 2. Redistributions in binary form must reproduce the above copyright
 *		notice, this list of conditions and the following disclaimer in the
 *		documentation and/or other materials provided with the distribution.
 *	 3. The name of the author may not be used to endorse or promote products
 *		derived from this software without specific prior written permission.
 *
 *	THIS SOFTWARE IS PROVIDED BY BUSLAB FI MUNI ('BUSLAB') ``AS IS''
 *	AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 *	IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 *	ARE DISCLAIMED. IN NO EVENT SHALL 'BUSLAB' BE LIABLE FOR ANY DIRECT, INDIRECT,
 *	INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 *	LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 *	OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 *	LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 *	NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 *	EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
grammar Dot;

options {
    backtrack = true;
    memoize = true;
    output = AST;
    ASTLabelType = CommonTree;
}

tokens {
    // operators and special characters
    O_BRACKET = '{';
    C_BRACKET = '}';
    O_SQR_BRACKET = '[';
    C_SQR_BRACKET = ']';
    SEMI_COLON = ';';
    EQUAL = '=';
    COMMA = ',';
    COLON = ':';
    LPAREN = '(';
    RPAREN = ')';

    // case-insensitive keywords
    GRAPH;
    DIGRAPH;
    STRICT;
    NODE;
    EDGE;
    SUBGRAPH;

    // tokens from imaginary nodes
    GRAPH_ROOT;
    SUBGRAPH_ROOT;
    STMT_LIST;
    EDGE_STMT;
    NODE_STMT;
    ATTR_LIST;
    ATTR;
}

@header {
package org.cesta.parsers.dot;

import java.util.logging.Logger;
}

@members {
    private boolean hasErrors = false;
    private Logger logger = Logger.getLogger(this.getClass().getName());

    public void setLogger(Logger newLogger){
        logger = newLogger;
    }

    @Override
    public void emitErrorMessage(String message) {
        hasErrors = true;
	if (logger!=null) logger.warning(message);
        super.emitErrorMessage(message);
    }

    public boolean hasErrors(){
        return hasErrors;
    }
}

@lexer::header {
package org.cesta.parsers.dot;
}

@lexer::members {
}

graph
    :
    	graphModifier ID? O_BRACKET stmt_list C_BRACKET
    	-> ^(GRAPH_ROOT graphModifier ID? stmt_list)
    ;

graphModifier
    	:
    		(STRICT)? (GRAPH | DIGRAPH)
    	;

stmt_list
    :  stmt (SEMI_COLON* stmt)* SEMI_COLON* -> ^(STMT_LIST stmt+)
    ;

stmt
    :
        attr_stmt |
        edge_stmt |
    	subgraph |
    	ID EQUAL ID  -> ^(ATTR ID EQUAL ID) |
    	node_stmt
    ;

attr_stmt
    :  (GRAPH^ | NODE^ | EDGE^) (attr_list)
    ;

attr_list
    :
    	(O_SQR_BRACKET a_list? C_SQR_BRACKET)*
    	-> ^(ATTR_LIST a_list*)
    ;

a_list
    :  (attr COMMA!?)+
    ;

attr
	:
		ID (EQUAL ID)? -> ^(ATTR ID (EQUAL ID)?)
	;

edge_stmt
    :
    	 node_subgraph edgeRHS attr_list?
    	-> ^(EDGE_STMT node_subgraph edgeRHS attr_list?)
    ;

node_subgraph
	:
	(node_id | subgraph)
	;

edgeRHS
    :  EDGEOP^ (node_id | subgraph) (edgeRHS)?
    ;

node_stmt
    :  node_id (attr_list)? -> ^(NODE_STMT node_id attr_list?)
    ;

node_id
    :  ID^ (port)?
    ;

port
    :
    	COLON! ID (COLON! VALIDSTR)? |
    	COLON! VALIDSTR
    ;

subgraph
    :
    	O_BRACKET stmt_list? C_BRACKET
    		-> ^(SUBGRAPH_ROOT stmt_list?)

    	|(SUBGRAPH O_BRACKET) =>
    		SUBGRAPH ID? O_BRACKET stmt_list? C_BRACKET
    		-> ^(SUBGRAPH_ROOT ID? stmt_list?)

    	| SUBGRAPH ID? O_BRACKET stmt_list? C_BRACKET
    		-> ^(SUBGRAPH_ROOT ID? stmt_list?)
    	|
    	SUBGRAPH ID
    		-> ^(SUBGRAPH_ROOT ID)
    ;

// LEXER

// case-insensitive keywords
GRAPH: G R A P H;
DIGRAPH: D I G R A P H;
STRICT: S T R I C T;
NODE: N O D E;
EDGE: E D G E;
SUBGRAPH: S U B G R A P H;

EDGEOP : '->' | '--';

ID
    :  (  VALIDSTR
        | NUMBER
        | QUOTEDSTR
        | HTMLSTR
       );



fragment ALPHACHAR
	:  (   'a'..'z'
        |  'A'..'Z'
        |  '_'
       );


fragment VALIDSTR
    :  ALPHACHAR
        (  ALPHACHAR
         |  '0'..'9'
        )*
    ;

fragment NUMBER
    :  ('-')? ('0'..'9')+ ('.' ('0'..'9')+)?
    ;

fragment QUOTEDSTR
    :  '"'
    	STR
       '"'
    ;

fragment STR
    :
    	(ESCAPE_SEQUENCE | ~('\\'|'"') )*
    ;

fragment ESCAPE_SEQUENCE
    :   '\\' ('b'|'t'|'n'|'f'|'r'|'\"'|'\''|'\\')
    ;

fragment HTMLSTR
    :  '<' (~'>')* '>'
    ;

WS  :  (' '|'\t')+
    {
        $channel = HIDDEN;
    }
    ;

NEWLINE  :  ('\r' '\n'|'\r'|'\n'|'\u000C')
    {
        $channel = HIDDEN;
    }
    ;

COMMENT
    :   '/*' ( options {greedy=false;} : . )* '*/'
    {
        $channel = HIDDEN;
    }
    ;

LINE_COMMENT
    : '//' ~('\n'|'\r')* '\r'? '\n'
    {
        $channel = HIDDEN;
    }
    ;

fragment A:('a'|'A');
fragment B:('b'|'B');
fragment C:('c'|'C');
fragment D:('d'|'D');
fragment E:('e'|'E');
fragment F:('f'|'F');
fragment G:('g'|'G');
fragment H:('h'|'H');
fragment I:('i'|'I');
fragment J:('j'|'J');
fragment K:('k'|'K');
fragment L:('l'|'L');
fragment M:('m'|'M');
fragment N:('n'|'N');
fragment O:('o'|'O');
fragment P:('p'|'P');
fragment Q:('q'|'Q');
fragment R:('r'|'R');
fragment S:('s'|'S');
fragment T:('t'|'T');
fragment U:('u'|'U');
fragment V:('v'|'V');
fragment W:('w'|'W');
fragment X:('x'|'X');
fragment Y:('y'|'Y');
fragment Z:('z'|'Z');
