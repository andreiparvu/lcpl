package ro.pub.cs.lcpl;

import src.Properties;

/** new <i>type</i> */
public class NewObject extends Expression {
  private String type;

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public NewObject(int lineNumber, String type) {
    super(lineNumber);
    this.type = type;
  }
  public NewObject() {}

  public void eval(Properties props) throws LCPLException {
    // Nu putem crea un Int sau o clasa ce nu exista

    if (type.compareTo("Int") == 0) {
      throw new LCPLException("Illegal instruction : new Int", this);
    }
    if (props.allClasses.get(type) == null) {
      Properties.classNotFound(type, this);
    }
    setTypeData(props.allClasses.get(type));
  }

}
