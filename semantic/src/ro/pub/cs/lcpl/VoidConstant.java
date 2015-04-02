package ro.pub.cs.lcpl;

import src.Properties;

/** the constant "void" */
public class VoidConstant extends Expression {

  public VoidConstant(int lineNumber) {
    super(lineNumber);
  }

  public VoidConstant() {}

  public void eval(Properties props) {
    // Avem de-a face cu o constanta
    this.isConstant = this.ORIGINAL_CONSTANT;
    this.typeConstant = this.VOID;

    setTypeData(props.p.getNullType());
  }
}
