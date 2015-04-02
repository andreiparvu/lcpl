package ro.pub.cs.lcpl;

import src.Properties;

/** Arithmetic operation with one operand */
public class UnaryOp extends Expression {
  private Expression e1;

  public Expression getE1() {
    return e1;
  }

  public void setE1(Expression e1) {
    this.e1 = e1;
  }

  public UnaryOp(int lineNumber, Expression e1) {
    super(lineNumber);
    this.e1 = e1;
  }
  public UnaryOp() {}

  public void checkConst(boolean useFolding) {}

  public void eval(Properties props) throws LCPLException {
    if (this.isFoldConstant()) {
      return ;
    }

    setTypeData(props.p.getIntType());
    e1.eval(props);

    checkConst(props.useFolding);
  }
}
