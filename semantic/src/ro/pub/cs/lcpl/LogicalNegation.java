package ro.pub.cs.lcpl;

/** ! <i>expression</i> */
public class LogicalNegation extends UnaryOp {

  public LogicalNegation(int lineNumber, Expression e1) {
    super(lineNumber, e1);
  }

  public LogicalNegation() {}

  public void checkConst(boolean use) {
    // Evaluare pentru constant folding
    if (use == true) {
      if (getE1().isConstant > 0) {
        this.isConstant = this.FOLD_CONSTANT;
        if (getE1().intConstant != 0) {
          setIntConstant(0);
        } else {
          setIntConstant(1);
        }
      }
    }
  }
}
