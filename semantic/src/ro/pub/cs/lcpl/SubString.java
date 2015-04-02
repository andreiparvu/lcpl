package ro.pub.cs.lcpl;

import src.Properties;

/** <i>stringExpression</i> [ <i>start</i> , <i>stop</i> ] */
public class SubString extends Expression {
  private Expression stringExpr;
  private Expression startPosition;
  private Expression endPosition;
  public Expression getStringExpr() {
    return stringExpr;
  }
  public void setStringExpr(Expression stringExpr) {
    this.stringExpr = stringExpr;
  }
  public Expression getStartPosition() {
    return startPosition;
  }
  public void setStartPosition(Expression startPosition) {
    this.startPosition = startPosition;
  }
  public Expression getEndPosition() {
    return endPosition;
  }
  public void setEndPosition(Expression endPosition) {
    this.endPosition = endPosition;
  }
  public SubString(int lineNumber, Expression stringExpr,
      Expression startPosition, Expression endPosition) {
    super(lineNumber);
    this.stringExpr = stringExpr;
    this.startPosition = startPosition;
    this.endPosition = endPosition;
  }
  public SubString() {}

  public void eval(Properties props) throws LCPLException {
    // Evaluam expresiile si eventual convertim din Int in String, daca este nevoie
    stringExpr.eval(props);
    startPosition.eval(props);
    endPosition.eval(props);

    setTypeData(props.allClasses.get("String"));

    if (stringExpr.getTypeData().getName().compareTo("String") != 0) {
      if (stringExpr.getTypeData().getName().compareTo("Int") == 0) {
        Cast c = new Cast(stringExpr.getLineNumber(), "String", stringExpr);
        c.eval(props);
        stringExpr = c;

        return ;
      }

      Properties.cannotConvert(stringExpr.getType(), "String", this);
    }

    if (startPosition.getType().compareTo("Int") != 0) {
      Properties.cannotConvert(startPosition.getType(), "Int", this);
    }
    if (endPosition.getType().compareTo("Int") != 0) {
      Properties.cannotConvert(endPosition.getType(), "Int", this);
    }
  }
}
