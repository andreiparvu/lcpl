package ro.pub.cs.lcpl;

import src.Properties;

/** A generic class for any expression in the program */
public class Expression extends TreeNode {

  /** The name of the type of the expression (e.g. "Int", "String"...) */
  private String type;

  /** A reference to the type of the expression. This can be :
   * <li> An LCPLClass - for expressions returning an object.
   * <li> Program.intType - for expressions returning an Int.
   * <li> Program.nullType - for expressions returning the void constant.
   * <li> Program.noType - for expressions that do not return a value (e.g. 'while').
   * */
  private Type typeData;

  // Variabile folosite pentru constant folding
  protected int isConstant = 0;
  protected int typeConstant = 0;
  protected int intConstant = 0;
  protected String stringConstant = null;

  protected static final int ORIGINAL_CONSTANT = 1;
  protected static final int FOLD_CONSTANT = 2;
  protected static final int INT = 0;
  protected static final int STRING = 1;
  protected static final int VOID = 2;

  protected void setIntConstant(int c) {
    intConstant = c;

    stringConstant = "" + c;
  }

  public boolean isFoldConstant() {
    return isConstant == Expression.FOLD_CONSTANT;
  }

  public Expression takeConstant(Properties props) throws LCPLException {
    Expression e;

    // Trebuie sa cream o constanta, Int sau String
    if (typeConstant == Expression.INT) {
      e = new IntConstant(getLineNumber(), intConstant);
    } else {
      e = new StringConstant(getLineNumber(), stringConstant);
    }

    e.eval(props);

    return e;
  }

  public Type getTypeData() {
    return typeData;
  }

  public void setTypeData(Type typeData) {
    this.typeData = typeData;
    this.type = typeData.getName();
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public Expression(int lineNumber) {
    super(lineNumber);
    this.isConstant = 0;
  }

  public Expression() {}

  public void eval(Properties props) throws LCPLException {}

}
