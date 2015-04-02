// Andrei Parvu
// 341C3

tree grammar LCPLTreeCheckerBonus;

options {
    tokenVocab=LCPLTreeBuilderBonus;
    ASTLabelType=CommonTree;
}

@members {
  int getLineNr(List<Expression> exs) {
    if (exs.size() == 0) {
      return 0;
    }
    return exs.get(0).getLineNumber();
  }
}

@header {
    import java.util.LinkedList;
    import ro.pub.cs.lcpl.*;
}

program returns [Program result]
  @init {
    LinkedList<LCPLClass> classes = new LinkedList<LCPLClass>();
  }:
  ^(PROGRAM (classdef { classes.add($classdef.result); })*) {
    // Adaugam toate clasele intr-o lista si ne cream clasa de Program
    $result=new Program($PROGRAM.line, classes); 
  };

classdef returns [LCPLClass result]
    @init {
      List<Feature> body = new ArrayList<Feature>();
    }:
    ^(CLASS 
      ^(NAME name=id)
      ^(INHERIT i1=id?)
      ^(BODY (b=classbody { body.add($b.result); })* )
    ) {
      // Adaugam feature-urile intr-o lista si cream clasa de LCPL
      $result= new LCPLClass($CLASS.line, $name.text, $i1.text, body);
    }
    ;

classbody returns [Feature result]:
  method { result=$method.result; }
  |
  ^(DECL id1=id id2=id e=expr?) {
    // Instantiem clasa de atribut, cu sau fara o valoare initiala
    $result = new Attribute($id1.line, $id2.text, $id1.text, $e.expr);
  };

method returns [Method result]
  @init {
    List<FormalParam> params = new ArrayList<FormalParam>();
  }:
  ^(METHOD name=id
    ^(PARAMS
      (^(PARAM type=id namep=id) {
        params.add(new FormalParam($namep.text, $type.text));
      })*
    )
    ^(RET ret=id?)
    body=blockStatement
  ) {
    // Instantiem clasa de metoda, pe baza numelui, a parametrilor, a valorii
    // de return si a blocului
    $result = new Method($METHOD.line, $name.text, params,
      $ret.text != null ? $ret.text : "void", $body.block);
  };

blockStatement returns [Block block]
  @init {
    List<List<Expression>> body = new ArrayList<List<Expression>>();
    List<LocalDefinition> localEnd = new ArrayList<LocalDefinition>();
    body.add(new ArrayList<Expression>());
    int number = 0;
    int line = 0;
  }:
  (s=statement {
    // Pentru a calcula eficient scope-ul unei definitii locale vom mentine
    // toate expresiile intr-o lista de liste, trecerea fiind facuta cand
    // intalnim un nod LOCAL
    if (body.get(0).size() == 0) {
      if ($s.result instanceof LocalDefinition) {
        line = $s.localLine;
      } else {
        line = $s.result.getLineNumber();
      }
    }

    body.get(number).add($s.result);
    if ($s.result instanceof LocalDefinition) {
      // Am intalnit un 'local', retinem ultima variabila locala si
      // trecem la un nou nivel
      localEnd.add($s.resEnd);
      body.add(new ArrayList<Expression>());
      number++;
    }
  })* {
    // Parcurgem nivelurile, cream din fiecare un bloc, si il punem ca scope
    // pentru nivelul superior
    for (int i = number; i > 0; i--) {
      int lineNr = 0;
      if (body.get(i).size() > 0){
        lineNr = body.get(i).get(0).getLineNumber();
      }
      Block bl = new Block(lineNr, body.get(i));
      localEnd.get(i - 1).setScope(bl);
    }

    $block = new Block(line, body.get(0));
  };

statement returns [Expression result, LocalDefinition resEnd, int localLine]:
  expr { $result = $expr.expr; }
  |
  local {
    // Ne intereseaza prima si ultima definire si linia pe care a aparut 'local'
    $result = $local.def;
    $resEnd = $local.defEnd;
    $localLine = $local.line;
  };

local returns [LocalDefinition def, LocalDefinition defEnd, int line]
  @init {
    ArrayList<LocalDefinition> decls = new ArrayList<LocalDefinition>();
  }:
  ^(LOCAL
    (
      (^(DECL id1=id id2=id) {
        decls.add(new LocalDefinition($id1.line, $id2.text, $id1.text, null, null));
      })
      |
      (^(DECL id1=id id2=id e=expr) {
        decls.add(new LocalDefinition($id1.line, $id2.text, $id1.text, $e.expr, null));
      })
    )*
  ) {
    // Trebuie sa parcurgem fiecare definire si sa marcam scopul acesteia ca
    // fiind urmatoarea definire
    for (int i = decls.size() - 1; i > 0; i--) {
      decls.get(i - 1).setScope(decls.get(i));
    }
    $def = decls.get(0);
    $defEnd = decls.get(decls.size() - 1);
    $line = $LOCAL.line;
  };

expr returns [Expression expr]
  @init {
    List<String> ids = new LinkedList<String>();
    List<Integer> lines = new LinkedList<Integer>();
    List<Expression> args = new LinkedList<Expression>();
    List<Expression> left = new LinkedList<Expression>();
    List<Expression> right = new LinkedList<Expression>();
  }:
  ^('='
    ^(LEFT 
      (self='self'? dot='.'? id1=id {
        // Trebuie tratat cazul in care apare si 'self'
        String ret = "";
        int line = $id1.line;

        if ($self.text != null) {
          ret = $self.text + $dot.text;
          line = $self.line;
        }
        ret += $id1.text;

        lines.add(line);
        ids.add(ret);})+
    )
    e=expr) {
    // Dat fiind ca '=' este asociativ la dreapta, trebuie sa parurgem lista
    // de la dreapta la stanga si sa cream clasele de asignare
    List<Assignment> assigns = new ArrayList<Assignment>();
    int nr = ids.size();

    assigns.add(new Assignment(lines.get(nr - 1), ids.get(nr - 1), $e.expr));

    for (int j = nr - 2; j >= 0; j--) {
      assigns.add(new Assignment(lines.get(j), ids.get(j), assigns.get(nr - 2 - j)));
    }

    $expr = assigns.get(nr - 1);
  }
  |
  constant=(VOID | 'self') {
    if ($constant.text.compareTo("void") == 0) {
      $expr = new VoidConstant($constant.line);
    } else {
      $expr = new Symbol($constant.line, $constant.text);
    }
  }
  |
  int_cont=INT {
    $expr = new IntConstant($int_cont.line, Integer.parseInt($int_cont.text));
  }
  |
  ^(op=('+'|'-'|'*'|'/'|'=='|'<='|'<') e1=expr e2=expr) {
    if ($op.text.compareTo("+") == 0)
        $expr = new Addition($op.line, e1, e2);

    if ($op.text.compareTo("-") == 0)
        $expr = new Subtraction($op.line, e1, e2);

    if ($op.text.compareTo("*") == 0)
        $expr = new Multiplication($op.line, e1, e2);

    if ($op.text.compareTo("/") == 0)
        $expr = new Division($op.line, e1, e2);

    if ($op.text.compareTo("==") == 0)
        $expr = new EqualComparison($op.line, e1, e2);

    if ($op.text.compareTo("<=") == 0)
        $expr = new LessThanEqual($op.line, e1, e2);

    if ($op.text.compareTo("<") == 0)
        $expr = new LessThan($op.line, e1, e2);
  }
  |
  ^(MINUS e=expr) {
    $expr = new UnaryMinus($MINUS.line, $e.expr);
  }
  |
  ^(neg='!' e=expr) {
    $expr = new LogicalNegation($neg.line, $e.expr);
  }
  |
  ^(NEW id) {
    $expr = new NewObject($NEW.line, $id.text);
  }
  |
  ^(CAST ^(TYPE type=id) ^(BODY e=expr)) {
    $expr = new Cast($CAST.line, $type.text, $e.expr);
  }
  |
  str=STRING_CONST {
    // Trebuie sa substituim caracterele speciale \t, \n, \r
    String actualString = $str.text.substring(1, $str.text.length()-1).
      replace("\\n", "\n").replace("\\t", "\t").replace("\\r", "\r");

    // In afara de cele de sus, \caracter devine caracter
    String rets = "";
    for (int i = 0; i < actualString.length(); i++) {
      if (actualString.charAt(i) == '\\') {
        rets += actualString.charAt(i+1);
        i ++;
      } else {
        rets += actualString.charAt(i);
      }
    }
    $expr = new StringConstant($str.line, rets);
  }
  |
  ^(IF
    ^(COND cond=expr)
    ^(THEN body=blockStatement)
    ^(ELSE elseb=blockStatement)) {

    $expr = new IfStatement($IF.line, $cond.expr,
      $body.block, $elseb.block);
  }
  |
  ^(WHILE
    ^(COND cond=expr)
    ^(BODY body=blockStatement)) {
    $expr = new WhileStatement($WHILE.line, $cond.expr, $body.block);
  }
  |
  ^(DISPATCH
    ^(NAME name=id)
    ^(OBJECT (obj=expr)?)
    ^(ARGS (e=expr {args.add($e.expr);})* )) {
    $expr = new Dispatch($DISPATCH.line, $obj.expr, $name.text, args);
  }
  |
  ^(STATIC_DISPATCH
    ^(NAME name=id)
    ^(OBJECT (obj=expr)?)
    ^(TYPE type=id)
    ^(ARGS (e=expr {args.add($e.expr);})* )) {
    $expr = new StaticDispatch($STATIC_DISPATCH.line, $obj.expr, $type.text, $name.text, args);
  }
  |
  ^(STRING
    e=expr
    (^(SUB e1=expr e2=expr) {
      left.add($e1.expr); right.add($e2.expr);
    })+
  ) {
    // Cream o lista cu toate nodurile de tip SUB si apoi le aplicam de la stanga la dreapta
    List<SubString> substrings = new ArrayList<SubString>();
    int nr = left.size();

    substrings.add(new SubString($STRING.line, $e.expr, left.get(0), right.get(0)));

    for (int j = 1; j < left.size(); j++) {
      substrings.add(new SubString($STRING.line, substrings.get(j - 1), left.get(j), right.get(j)));
    }

    $expr = substrings.get(nr - 1);
  }
  |
  id {
    $expr = new Symbol($id.line, $id.text);
  };

id returns [int line, String text]:
  // Un nod VECTOR trebuie transformat in VECTOR + tip
  ^(VECTOR ID) {$line = $VECTOR.line; $text = $VECTOR.text + $ID.text;}
  |
  // Metodele pentru lucru cu vectori
  c=(INIT | SET | GET | ID) { $line = $c.line; $text = $c.text; };
