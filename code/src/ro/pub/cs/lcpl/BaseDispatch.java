package ro.pub.cs.lcpl;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import src.Properties;

/** Common code for Dispatch and StaticDispatch */
public class BaseDispatch extends Expression {
  private Expression object;
  private String name;
  private List<Expression> arguments;

  /** A reference to the method invoked by the dispatch expression.
   * 
   * In case of dynamic dispatch, use the type of the <i>object</i> expression to 
   * identify which method the dispatch refers to.
   * 
   * The actual method invoked at runtime could be different due to polymorphism, 
   * because a derived class can override methods of the base class. 
   *  */
  private Method method;

  public Expression getObject() {
    return object;
  }
  public void setObject(Expression object) {
    this.object = object;
  }
  public String getName() {
    return name;
  }
  public void setName(String name) {
    this.name = name;
  }
  public Method getMethod() {
    return method;
  }
  public void setMethod(Method method) {
    this.method = method;
  }
  public List<Expression> getArguments() {
    return arguments;
  }
  public void setArguments(List<Expression> arguments) {
    this.arguments = arguments;
  }
  public BaseDispatch() {}

  protected String getRtti(PrintStream os, String objectIndex, String objectType) {
    // Suprascrisa in StaticDispatch
    String[] index = Properties.genIndexes(2);

    os.println(
      Properties.getElementPtr(index[1], objectType, objectIndex, 0) +
      Properties.load(index[2], "%struct.__lcpl_rtti*", index[1])
    );

    return index[2];
  }

  protected String getCallType() {
    // Suprascrisa in StaticDispatch
    return Properties.genType(method.getParent().getName());
  }

  public String emitCode(PrintStream os, boolean isValue) {
    String objectIndex = object.emitCode(os, true);
    String objectType = Properties.genType(object.getTypeData().getName());

    // Tipul obiectului cu care se apeleaza
    String callType = getCallType();

    String[] index = Properties.genIndexes(1);
    // Verificam daca obiectul este nul
    os.println(
      Properties.bitcast(index[1], objectType, objectIndex, "i8*") +
      "\tcall void @__lcpl_checkNull(i8* " + index[1] + ")" + "\n"
    );

    // Calculam informatia de runtime de unde trebuie luata metoda
    String rttiIndex = getRtti(os, objectIndex, objectType);

    // Gasim metoda in tabela de metode, facem cast la semnatura metodei si
    // facem cast la tipul obiectului cu care se apeleaza metoda
    index = Properties.genIndexes(6);
    os.println(
      Properties.getElementPtr(index[1], "%struct.__lcpl_rtti*", rttiIndex, 3) +
      Properties.bitcast(index[2], "[0 x i8*]*", index[1], "i8**") +
      "\t" + index[3] + " = getelementptr i8** " + index[2] + ", i32 " +
      (((LCPLClass)object.getTypeData()).methodTable.indexOf(name) + 1) + "\n" +
      Properties.load(index[4], "i8*", index[3]) +
      Properties.bitcast(index[5], "i8*", index[4], method.emitSignature()) +
      Properties.bitcast(index[6], objectType, objectIndex, callType)
    );

    String[] argIndex = new String[arguments.size()];
    for (int i = 0; i < arguments.size(); i++) {
      // emitem codul pentru parametrii
      argIndex[i] = arguments.get(i).emitCode(os, true);

      if (arguments.get(i).getTypeData().getName().compareTo("void") == 0) {
        // Daca avem un parametru void(null) facem cast la tipul de date dorit
        String newIndex = "%" + Properties.index++;

        os.println(
          Properties.bitcast(newIndex, Properties.genType("Object"), argIndex[i],
            Properties.genType(method.getParameters().get(i).getType()))
        );
        argIndex[i] = newIndex;
      }
    }

    String retData = Properties.genType(method.getReturnTypeData().getName());
    String retIndex = "", ret = "";

    if (method.getReturnTypeData().getName().compareTo("(none)") != 0) {
      retIndex = "%" + Properties.index++;
      ret = retIndex + " = ";
    }

    // Facem apelul propriu-zis
    os.print("\t" + ret + "call " + retData + " " + index[5] + "(" + callType + " " + index[6]);

    for (int i = 0; i < arguments.size(); i++) {
      String type = Properties.genType(method.getParameters().get(i).getType());

      os.print(", " + type + " " + argIndex[i]);
    }
    os.println(")");

    return retIndex;
  }
}
