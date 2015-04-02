package ro.pub.cs.lcpl;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import src.Properties;

/** A declaration of a method in a class
 * <i>name</i> <i>parameters</i> -> <i>returnType</i> : <i>body</i> end;
 */
public class Method extends TreeNode implements Feature {
  private String name;
  private List<FormalParam> parameters = new LinkedList<FormalParam>();

  /** Type returned by the method, or "void" if the method does not return anything. */
  private String returnType;

  private Expression body;

  /** Reference to the LCPL class that contains the current method. */
  private LCPLClass parent;

  /** Create a new FormalParam object for the shadow parameter "self". 
   * This object will be referred by the self symbol in this method. 
   * Do not add it to the parameters list. */
  private FormalParam self;

  /** A reference to the type of the value returned by the method. This can be
   * <li> Program.intType - for Int parameters
   * <li> An LCPLClass - for class parameters
   * <li> Program.noType - for methods that do not return any type */
  private Type returnTypeData;	

  public String getName() {
    return name;
  }
  public void setName(String name) {
    this.name = name;
  }
  public LCPLClass getParent() {
    return parent;
  }
  public void setParent(LCPLClass parent) {
    this.parent = parent;
  }
  public List<FormalParam> getParameters() {
    return parameters;
  }
  public void setParameters(List<FormalParam> parameters) {
    this.parameters = parameters;
  }
  public FormalParam getSelf() {
    return self;
  }
  public void setSelf(FormalParam self) {
    this.self = self;
  }
  public String getReturnType() {
    return returnType;
  }	
  public void setReturnType(String returnType) {
    this.returnType = returnType;
  }
  public Type getReturnTypeData() {
    return returnTypeData;
  }
  public void setReturnTypeData(Type returnTypeData) {
    this.returnTypeData = returnTypeData;
  }
  public Expression getBody() {
    return body;
  }
  public void setBody(Expression body) {
    this.body = body;
  }
  public Method(int lineNumber, String name, List<FormalParam> parameters,
      String returnType, Expression body) {
    super(lineNumber);
    this.name = name;
    this.parameters = parameters;
    this.returnType = returnType;
    this.body = body;
  }
  public Method() {}

  public String emitFeature(boolean hasIntClass) {
    return "";
  }

  public String getIRName() {
    // Numele metodei in cadrul fisierului .ir
    return "@M" + parent.getName().length() + "_" + parent.getName() + "_" + name;
  }

  public String emitSignature() {
    // Antetul metodei
    String result = Properties.genType(returnTypeData.getName());

    // Primul parametru e pointer la structura 
    //result += " (%struct.T" + parent.getName() + "*";
    result += " (" + Properties.genType(parent.getName());

    // Adaugam tipul fiecarui parametru
    for (int i = 0; i < parameters.size(); i++) {
      result += ", " + Properties.genType(parameters.get(i).getType());
    }

    result += ")* ";

    return result;
  }

  private String storeParameter(int index, String type, String name) {
    //return "\t%" + index + " = alloca " + type + "\n" +
      //"\tstore " + type + " %" + name + ", " + type + "* %" + index + "\n";
    return
      Properties.alloca("%" + index, type) +
      Properties.store(type, "%" + name, "%" + index);
  }

  public void emitCode(PrintStream os) {
    String store = "";
    Properties.curClass = parent;
    Properties.curMethod = this;
    Properties.index = 2; // indexul urmatorulu simbol local
    Properties.label = 1; // indexul urmatorului label

    // Antetul metodei
    os.print("define " + Properties.genType(returnTypeData.getName()) + " " + getIRName() + "(");

    String selfType = Properties.genType(parent.getName());

    os.print(selfType + " %self");

    // Salvam parametrul self
    store = storeParameter(1, selfType, "self");
    self.index = "%" + 1;

    // Salvam restul parametrilor
    for (int i = 0; i < parameters.size(); i++) {
      String type = Properties.genType(parameters.get(i).getType());

      os.print(", " + type + " %" + parameters.get(i).getName());

      int index = Properties.index++;
      store += storeParameter(index, type, parameters.get(i).getName());

      parameters.get(i).index = "%" + (i + 2);
    }

    os.println(") {");

    os.println(store);

    // Emitem codul pentru corpul functiei
    String index = body.emitCode(os, true);

    if (returnType.compareTo("void") == 0) {
      os.println("\n\tret void");
    } else {
      if (body.getTypeData().getName().compareTo("void") == 0 &&
          returnTypeData.getName().compareTo("(none)") != 0) {
        // Facem conversie de la void(null) la tipul de date returnat de functie
        String newIndex = "%" + Properties.index++;

        os.println(
          Properties.bitcast(newIndex, Properties.genType("Object"), index,
          Properties.genType(returnTypeData.getName()))
        );
        index = newIndex;
      }
      os.println("\n\tret " + Properties.genType(returnType) + " " + index);
    }

    os.println("}\n");
  }
}
