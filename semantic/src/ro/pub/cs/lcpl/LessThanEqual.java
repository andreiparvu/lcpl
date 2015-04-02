package ro.pub.cs.lcpl;

/** <i>expression</i> <= <i>expression</i> */
public class LessThanEqual extends BinaryOp {

  public LessThanEqual(int lineNumber, Expression e1, Expression e2) {
    super(lineNumber, e1, e2);
  }

  protected void compConsValue() {
    // Evaluam expresia pentru constant folding

    this.typeConstant = this.INT;

    if (getE1().intConstant <= getE2().intConstant) {
      setIntConstant(1);
    } else {
      setIntConstant(0);
    }
  }

	public LessThanEqual() {}
	
}
