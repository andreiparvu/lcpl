package ro.pub.cs.lcpl;

/** - <i>expression</i>, in unary operator context */
public class UnaryMinus extends UnaryOp {

  public UnaryMinus(int lineNumber, Expression e1) {
    super(lineNumber, e1);
  }
  public UnaryMinus() {}

  public void checkConst(boolean use) {
    // Evaluare expresie pentru constant folding
    if (use == true) {
      if (getE1().isConstant > 0) {
        this.isConstant = this.FOLD_CONSTANT;
        setIntConstant(-getE1().intConstant);
      }
    }
  }
}
