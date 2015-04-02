package ro.pub.cs.lcpl;

/** <i>expression</i> / <i>expression</i> */
public class Division extends BinaryOp {
  public Division(int lineNumber, Expression e1, Expression e2) {
    super(lineNumber, e1, e2);
  }
  public Division() {}

  protected void compConsValue() {
    // Calcul valoare pentru constant folding
    this.isConstant = this.FOLD_CONSTANT;

    this.typeConstant = this.INT;
    setIntConstant(getE1().intConstant / getE2().intConstant);
  }
}
