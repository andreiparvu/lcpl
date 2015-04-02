package ro.pub.cs.lcpl;

import java.io.PrintStream;
import java.util.List;

/** A block of zero, one or more expressions, executed sequentially */
public class Block extends Expression {
  List<Expression> expressions;

  public List<Expression> getExpressions() {
    return expressions;
  }

  public void setExpressions(List<Expression> expressions) {
    this.expressions = expressions;
  }

  public Block(int lineNumber, List<Expression> expressions) {
    super(lineNumber);
    this.expressions = expressions;
  }
  public Block() {}

  public String emitCode(PrintStream os, boolean isValue) {
    // isValue = true
    String index = "";

    // Generam cod pentru fiecare expresie si returnam indexul ultimului simbol
    for (int i = 0; i < expressions.size(); i++) {
      index = expressions.get(i).emitCode(os, true);
    }

    return index;
  }
}
