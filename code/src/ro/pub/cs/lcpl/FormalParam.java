package ro.pub.cs.lcpl;

import java.io.PrintStream;
import src.Properties;

/** A formal parameter in the declaration of a method */
public class FormalParam extends TreeNode implements Variable {
  private String name;
  private String type;

  /** A reference to the type of the formal parameter. This can be
   * <li> Program.intType - for Int parameters
   * <li> An LCPLClass - for class parameters */
  private Type variableType;

  public String index;

  public String getName() {
    return name;
  }
  public void setName(String name) {
    this.name = name;
  }
  public String getType() {
    return type;
  }
  public void setType(String type) {
    this.type = type;
  }
  public Type getVariableType() {
    return variableType;
  }
  public void setVariableType(Type typeData) {
    this.variableType = typeData;
  }
  public FormalParam(String name, String type) {
    this.name = name;
    this.type = type;
  }
  public FormalParam() {}

  public String load(PrintStream os, boolean isValue) {
    if (!isValue) {
      // Dorim doar referinta
      return index;
    }

    // Dorim valoarea, deci o incarcam de la adresa pe care o avem
    String index2 = "%" + Properties.index++;
    os.println(Properties.load(index2, Properties.genType(variableType.getName()), index));

    return index2;
  }

}
