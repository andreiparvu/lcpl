package ro.pub.cs.lcpl;

import java.util.Iterator;
import java.util.List;

import src.Properties;

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

  public void eval(Properties props) throws LCPLException {
    if (expressions.size() == 0) {
      setTypeData(props.p.getNoType());
    } else {

      for (int i = 0; i < expressions.size(); i++) {
        Expression e = expressions.get(i);
        // Evaluam fiecare expresie a blocului
        e.eval(props);

        if (props.useFolding == true) {
          // Facem folding daca e nevoie
          if (e.isFoldConstant()) {
            e = e.takeConstant(props);

            expressions.set(i, e);
          }
        }
      }

      // Setam valoarea de return
      setTypeData(expressions.get(expressions.size() - 1).getTypeData());
    }
  }
}
