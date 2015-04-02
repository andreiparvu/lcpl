package ro.pub.cs.lcpl;

import src.Properties;

/** A string constant literal */
public class StringConstant extends Expression {
  private String value;

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public StringConstant(int lineNumber, String value) {
    super(lineNumber);
    this.value = value;
  }
  public StringConstant() {}

  public void eval(Properties props) {
    // Marcam ca fiind o constanta
    this.isConstant = this.ORIGINAL_CONSTANT;
    this.typeConstant = this.STRING;
    this.stringConstant = value;

    setTypeData(props.allClasses.get("String"));
  }
}
