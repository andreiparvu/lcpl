package ro.pub.cs.lcpl;

import java.io.PrintStream;
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

  public String emitCode(PrintStream os, boolean isValue) {
    // Nu printam direct string-ul constant, il retinem pentru a-l afisa
    // la nivel global
    return Properties.genString(value);
  }
}
