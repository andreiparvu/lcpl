package ro.pub.cs.lcpl;

import src.Properties;

/** A symbol - local variable or class attribute */
public class Symbol extends Expression {
  /** The name of the symbol being evaluated.
   * Take into account the syntax "self.sym" to specify explicitly class attributes.
   */
  private String name;

  /** Reference to the variable corresponding to the symbol.
   * The variable can be:
   *   <li> an Attribute of the current class
   *   <li> a FormalParam of the current method. It can be any formal parameter, except for self. 
   *   <li> a LocalDefinition. The Assignment must be inside the scope of the LocalDefinition.
   *  */
  private Variable variable;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Variable getVariable() {
    return variable;
  }

  public void setVariable(Variable variable) {
    this.variable = variable;
  }

  public Symbol(int lineNumber, String name) {
    super(lineNumber);
    this.name = name;
  }
  public Symbol() {}

  public void eval(Properties props) throws LCPLException {
    if (getName().compareTo("self") == 0) {
      // Avem o referinta la obiectul curent
      setVariable(props.self);
      setTypeData(props.curClass);

      return ;
    }

    Variable v;
    String actualName = getName();
    if (getName().indexOf("self.") == 0) {
      // Este vorba de un atribut al clasei, se uitam la primul element din stiva
      actualName = getName().substring(5);

      v = props.curClass.takeVariable(actualName, true);
    } else {
      v = props.curClass.takeVariable(getName(), false);

    }
    if (v == null) {
      throw new LCPLException("Attribute " + actualName + " not found in class " +
          props.curClass.getName(), this);
    }

    setVariable(v);

    setTypeData(v.takeTypeData());
  }
}
