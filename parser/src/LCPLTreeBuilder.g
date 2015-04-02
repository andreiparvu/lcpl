// Andrei Parvu
// 341C3


grammar LCPLTreeBuilder;

options {
    language = Java;
    output = AST;
    ASTLabelType = CommonTree;
}

tokens {
    PROGRAM;
    CLASS = 'class';
    INHERIT = 'inherits';
    METHOD;
    STATEMENT;
    THEN='then';
    IF='if';
    END = 'end';
    ID;
    STRING_CONST;
    DECL;
    PARAMS;
    PARAM;
    RET;
    DISPATCH;
    NAME;
    ARGS;
    OBJECT;
    ELSE = 'else';
    COND;
    LOCAL = 'local';
    WHILE = 'while';
    LOOP = 'loop';
    BODY;
    NEW = 'new';
    TYPE;
    CAST = '{';
    STRING;
    MINUS;
    STATIC_DISPATCH;
    LEFT;
    SUB;
    VOID = 'void';
}

program:
  classdef* -> ^(PROGRAM classdef*)
;

// Declaratia unei clase, formeaza nod AST de tip CLASS.
// contine nume, posibila mostenire si corp.
classdef:
  CLASS name=id class_inherit? classbody* END ';'
    -> ^(CLASS ^(NAME $name) ^(INHERIT class_inherit?) ^(BODY classbody*));

// formeaza nod in AST de tip INHERIT
class_inherit:
  INHERIT! id;

// Corpul clasei poate fi fie declaratie de variabile, fie o metoda
classbody:
  ('var') => declarations_class
  |
  method;

// Declaratie de variabile din clasa incepe cu 'var'
declarations_class:
  'var'! declaration+ END! ';'!;

// O declaratie poate fi simpla, sau urmata si de o initializare - se
// genereaza nod AST de tip DECL
declaration:
  (id id '=') => (id id '=' expr ';' -> ^(DECL id id expr))
  |
  id id ';' -> ^(DECL id id);

// O metoda contine un nume, posibili parametrii, posibila valoare de
// return si corpul metodei; genereaza nod AST de tip METHOD.
method:
  name=id parameters? retrn? ':' statement* END ';'
    -> ^(METHOD $name ^(PARAMS parameters?) ^(RET retrn?)  statement*);

// Lista de parametrii este formata din mai multi parametrii separati prin
// virgula
parameters:
  parameter (','! parameter)* ;
// Un parametru este format din tip si nume
parameter:
  type=id name=id -> ^(PARAM $type $name);

retrn:
  '->'! id ;

// O metoda poate contine fie o expresie urmata de ';', fie o declaratie de
// variabile locale
statement:
  fullexpr_semicolon
  |
  declarations_local;

// Declaratia de variabile locale este asemanatoare cu cea din clasa, numai ca
// formeaza un nod AST de tip local, pentru a sti care variabile fac parte
// din aceeasi declaratie
declarations_local:
  LOCAL declaration+ END ';' -> ^(LOCAL declaration+);

// Expresiile pot fi sau nu terminate cu ';', depinzand daca fac parte din
// corpul metodei sau o conditie pentru if, while, argument dipatch, etc.
fullexpr_semicolon:
  exprassign ';'!;

fullexpr:
  exprassign;

// Asignarile sunt asociative la dreapta, deci sunt puse toate intr-un nod de
// tip LEFT, urmand ca aceasta sa se proceseze in tree checker
exprassign:
  (self_id '=') =>
    ((self_id assign_middle* '=' expr) ->
      ^('=' ^(LEFT self_id assign_middle*) expr))
  |
  exprneg;

assign_middle:
  '='! self_id;

// asignarile pot contine si referiri la metode din clasa curenta
self_id:
  'self'? '.'? id;

// Operatorul de negatie
exprneg:
  '!'^ exprcomp
  |
  exprcomp;

// Operatorii de comparatie, au expresii pe ambele parti
exprcomp:
  (expr ('<' | '<=' | '==')) => expr ('<' | '<=' | '==')^ expr
  |
  expr;

// Expresie aritmetica: + si - sunt mai putin prioritari decat * si /
expr:
  term (('+'^|'-'^) term)* ;

term:
  factminus (('*'^|'/'^) factminus) *;

// - unar are cea mai mare prioritate dintre operatorii aritmetici
factminus:
  '-' fact_string -> ^(MINUS fact_string)
  |
  fact_string;

// Operator de substring, putem avea mai multi inlantuiti [p1, p2][p3, p4]...
// Se formeaza noduri in AST de tip STRING, urmand ca fiecare substring sa
// aiba un nod SUB asociat
fact_string:
  (fact '[' expr ',' ) => (fact string+ -> ^(STRING fact string+))
  |
  fact;

string:
  ('[' e1=expr ',' e2=expr ']' -> ^(SUB $e1 $e2));


// factorii de baza
fact:
  (CAST) => (CAST id expr '}' -> ^(CAST ^(TYPE id) ^(BODY expr)))
  |
  (NEW) => NEW^ id
  |
  INT | STRING_CONST | VOID | id | 'self'
  |
  '('! fullexpr ')'!
  |
  dispatch
  |
  ifstatement
  |
  whilestatement;

// Avem 3 cazuri de dispatch, doua generand noduri de tip DISPATCH, si celalat
// nod de tip STATIC_DISPATCH
dispatch:
  ('[' expr '.' id '.') =>
    ('[' obj=expr '.' type=id '.' name=id dispatch_args? ']' ->
      ^(STATIC_DISPATCH
        ^(NAME $name)
        ^(OBJECT $obj)
        ^(TYPE $type)
        ^(ARGS dispatch_args?)
        )
    )
  |
  ('[' expr '.') =>
    ('[' obj=expr '.' id dispatch_args? ']' ->
      ^(DISPATCH ^(NAME id) ^(OBJECT $obj) ^(ARGS dispatch_args?)))
  |
  '[' id dispatch_args? ']' ->
    ^(DISPATCH ^(NAME id) ^(OBJECT) ^(ARGS dispatch_args?));

// Argumentele sunt expresii (fara ';'), separate prin ','
dispatch_args:
  fullexpr (','! fullexpr) * ;

// Expresie de if, care in interior poate contine statement-uri, la fel ca o metoda
ifstatement:
  IF cond=fullexpr THEN statement* elseexpr? END ->
    ^(IF ^(COND $cond) ^(THEN statement*) ^(ELSE elseexpr?));

elseexpr:
  ELSE! statement*;

// Expresie de while, care in interior poate contine statement-uri, la fel ca o metoda
whilestatement:
  WHILE cond=fullexpr LOOP statement* END ->
    ^(WHILE ^(COND $cond) ^(BODY statement*));

id:
  ID;

// Constanta de string
STRING_CONST:
  '"' (
    '\\' '\r'? '\n' // escape de newline
    |
    '\\' '"' // escape de "
    |
    ~('\n'|'\r'|'"') // orice altceva decat newline si "
  )* '"';

ID:
  ('a'..'z'|'A'..'Z') ('a'..'z'|'A'..'Z'|'0'..'9'|'_')*;

INT:
  '0' | ('1'..'9')('0'..'9'*) ;

WS:
  (' ' | '\t' | '\n' | '\r') { $channel = HIDDEN; };

COMMENT:
  '#' (~('\n' | '\r'))* ('\n' | '\r') { skip(); };
