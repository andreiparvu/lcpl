package ro.pub.cs.lcpl;

import src.Properties;

/** An explicit cast
 * { <i>type</i> <i>expression</i> }
 */
public class Cast extends Expression {
  private String type;
  private Expression e1;
  public String getType() {
    return type;
  }
  public void setType(String type) {
    this.type = type;
  }
  public Expression getE1() {
    return e1;
  }
  public void setE1(Expression e1) {
    this.e1 = e1;
  }
  public Cast(int lineNumber, String type, Expression e1) {
    super(lineNumber);
    this.type = type;
    this.e1 = e1;

    if (e1.isConstant > 0) {
      // Daca cream o un cast si o expresie e constanta, trebuie sa ii mentinem datele - folosit
      // pentru folding
      this.isConstant = e1.isConstant;
      this.typeConstant = e1.typeConstant;
      this.intConstant = e1.intConstant;
      this.stringConstant = e1.stringConstant;
    }
  }
  public Cast() {}

  public void eval(Properties props) throws LCPLException {
    e1.eval(props);

    String etype = e1.getTypeData().getName();

    // Verificam sa existe clasa catre care facem cast
    if (props.allClasses.get(type) == null && type.compareTo("Int") != 0) {
      Properties.classNotFound(type, this);
    }

    if (props.canConvert(etype, type) || props.canConvert(type, etype)) {
      setTypeData(props.allClasses.get(type));
    } else {
      throw new LCPLException("Invalid cast. Cannot convert " + etype + " to " + type,
          this);
    }
  }
}
