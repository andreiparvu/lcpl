package ro.pub.cs.lcpl;

import java.util.List;

import src.Properties;

/** A method call with a know static type
 * [<i>object</i>.<i>type</i>.<i>name</i> <i>expression</i> , ... ]
 */
public class StaticDispatch extends BaseDispatch {
  private String type;
  private Type selfType;
  public String getType() {
    return type;
  }
  public void setType(String type) {
    this.type = type;
  }
  public Type getSelfType() {
    return selfType;
  }
  public void setSelfType(Type selfType) {
    this.selfType = selfType;
  }
  public StaticDispatch(int lineNumber, Expression object, String type,
      String name, List<Expression> arguments) {
    setLineNumber(lineNumber);
    setObject(object);
    setType(type);
    setName(name);
    setArguments(arguments);
  }
  public StaticDispatch() {}

  // Metoda suprascrisa din BaseDispatch
  protected Method takeMethod(Properties props, String name, LCPLClass curClass)
    throws LCPLException {
    if (props.allClasses.get(type) == null) {
      Properties.classNotFound(type, this);
    }

    Method m = null;
    try {
      // Cautam metoda unei clase specifice
      m = curClass.takeSpecificMethod(name, type);
    } catch (LCPLException ex) {
      throw new LCPLException("Cannot convert from " + curClass.getName() + " to " + type +
          " in StaticDispatch", this);
    }

    return m;
  }
}
