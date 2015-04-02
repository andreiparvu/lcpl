package ro.pub.cs.lcpl;

import java.io.PrintStream;
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

  private String index;

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

  public String load(PrintStream os, boolean isValue) {
    if (!isValue) {
      return index;
    }

    String index2 = "%" + Properties.index++;
    os.println(Properties.load(index2, Properties.genType(type), index));

    return index2;
  }

  public String emitCode(PrintStream os, boolean isValue) {
    // isValue = true
    String type = Properties.genType(this.type);
    index = "%" + Properties.index++;

    // Alocam memorie pentru noua variabila
    os.println(Properties.alloca(index, type));

    if (init != null) {
      // Emitem codul pentru initializare si stocam valoarea obtinuta la atribut
      String initIndex = init.emitCode(os, true);

      os.println(
        Properties.store(type, initIndex, index)
      );
    } else if (this.type.compareTo("String") == 0) {
      String[] index = Properties.genIndexes(2);

      // Construim un nou string
      os.println(
        "\t" + index[1] + " = call i8* @__lcpl_new(%struct.__lcpl_rtti* @RString)" + "\n" +
        Properties.bitcast(index[2], "i8*", index[1], "%struct.TString*") +
        Properties.store("%struct.TString*", index[2], this.index)
      );
    } else if (type.compareTo("i32") != 0) {
      // Obiectele neinitializate primesc null
      os.println(Properties.store(type, "null", index));
    } else {
      // Intregii primesc 0
      os.println(Properties.store(type, "0", index));
    }

    return scope.emitCode(os, isValue);
  }
}
