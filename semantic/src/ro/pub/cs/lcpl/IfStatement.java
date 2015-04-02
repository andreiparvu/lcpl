package ro.pub.cs.lcpl;

import src.Properties;

/** if <i>expression</i> then <i>expression</i> else <i>expression</i> end; */
public class IfStatement extends Expression {
  /** Conditional expression : if <i>expression</i> then */
  private Expression condition;

  /** Expression evaluated if condition is true */
  private Expression ifExpr;

  /** Expression evaluated if condition is false; or null if the else branch is missing. */
  private Expression thenExpr;

  public Expression getCondition() {
    return condition;
  }
  public void setCondition(Expression condition) {
    this.condition = condition;
  }
  public Expression getIfExpr() {
    return ifExpr;
  }
  public void setIfExpr(Expression ifExpr) {
    this.ifExpr = ifExpr;
  }
  public Expression getThenExpr() {
    return thenExpr;
  }
  public void setThenExpr(Expression thenExpr) {
    this.thenExpr = thenExpr;
  }
  public IfStatement(int lineNumber, Expression condition, Expression ifExpr,
      Expression thenExpr) {
    super(lineNumber);
    this.condition = condition;
    this.ifExpr = ifExpr;
    this.thenExpr = thenExpr;
  }
  public IfStatement() {}

  public void eval(Properties props) throws LCPLException {
    // Evaluam conditia si incercam sa facem folding
    condition.eval(props);
    if (condition.getTypeData().getName().compareTo("Int") != 0) {
      throw new LCPLException("If condition must be Int", this);
    }

    if (props.useFolding) {
      if (condition.isFoldConstant()) {
        setCondition(condition.takeConstant(props));
      }
    }

    // Evaluam expresia si incercam sa facem folding
    ifExpr.eval(props);

    if (props.useFolding) {
      if (ifExpr.isFoldConstant()) {
        setIfExpr(ifExpr.takeConstant(props));
      }
    }

    if (thenExpr != null) {
      // Evaluam ramura 'else' si incercam sa facem folding
      thenExpr.eval(props);

      if (props.useFolding) {
        if (thenExpr.isFoldConstant()) {
          setThenExpr(thenExpr.takeConstant(props));
        }
      }

      // Calculam tipul returnat
      String type1 = ifExpr.getTypeData().getName(), type2 = thenExpr.getTypeData().getName();

      if (type1.compareTo(type2) == 0) {
        setTypeData(ifExpr.getTypeData());

        return ;
      }
      if (type1.compareTo("void") == 0 && props.allClasses.get(type2) != null) {
        setTypeData(thenExpr.getTypeData());
        return ;
      }
      if (type2.compareTo("void") == 0 && props.allClasses.get(type1) != null) {
        setTypeData(ifExpr.getTypeData());
        return ;
      }
    }

    setTypeData(props.p.getNoType());

  }
}
