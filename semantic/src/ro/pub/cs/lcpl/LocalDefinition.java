package ro.pub.cs.lcpl;

import src.Properties;

/** A local variable definition in a "local <i>name</i> <i>type</i>; ... end;" area
 */
public class LocalDefinition extends Expression implements Variable {
  private String name;

  /** Type of the variable, as a String */
  private String type;

  /** Initialization expression, can be null */
  private Expression init;

  /** Contains the expression where this definition is valid
   * 
   * For example
   * <pre>
   *   if x == 0; then local Int a = 0; Int b = a; end; a = x; b = x; end;
   * </pre>
   * The scope for the local definition "Int a = 0" is the local definition "Int b = a", which includes its own scope.
   * The scope for the local definition "Int b = a" is the block "a = x; b = x;"
   */
  private Expression scope;

  /** A reference to the type of the variable. This can be
   * <li> Program.intType - for Int variables.
   * <li> An LCPLClass - for class variables. */
  private Type variableType;

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
  public void setVariableType(Type variableType) {
    this.variableType = variableType;
  }

  public Type takeTypeData() {
    return getVariableType();
  }

  public Expression getInit() {
    return init;
  }
  public void setInit(Expression init) {
    this.init = init;
  }
  public Expression getScope() {
    return scope;
  }
  public void setScope(Expression scope) {
    this.scope = scope;
  }
  public LocalDefinition(int lineNumber, String name, String type,
      Expression init, Expression scope) {
    super(lineNumber);
    this.name = name;
    this.type = type;
    this.init = init;
    this.scope = scope;
  }
  public LocalDefinition() {}

  public void eval(Properties props) throws LCPLException {
    if (type.compareTo("Int") == 0) {
      setVariableType(props.p.getIntType());
    } else if (props.allClasses.get(type) == null) {
      Properties.classNotFound(type, this);
    } else {
      setVariableType(props.allClasses.get(type));
    }

    if (init != null) {
      // Evaluam expresia de initializare si eventual o convertim
      init.eval(props);

      if (props.canConvert(init.getTypeData().getName(), getVariableType().getName()) == false) {
        Properties.cannotConvert(init.getTypeData().getName(), getVariableType().getName(), this);
      }

      if (props.useFolding == true) {
        // Facem folding daca este nevoie
        if (init.isFoldConstant()) {
          init = init.takeConstant(props);
        }
      }
    }

    // Adaugam variabila pe stiva
    props.curClass.addVariable(this);
    // Evaluam
    scope.eval(props);
    setTypeData(scope.getTypeData());
    // Scoatem variabila de pe stiva
    props.curClass.removeVariable(this);
  }
}
