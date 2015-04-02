package ro.pub.cs.lcpl;

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

  // Cautam metoda 'name' din ierarhia de clase, incepand cu clasa curenta
  // Metoda suprascrisa in StaticDispatch
  protected Method takeMethod(Properties props, String name, LCPLClass curClass)
    throws LCPLException {
    return curClass.takeMethod(name);
  }

  public void eval(Properties props) throws LCPLException {
    String type;

    if (object == null) {
      // Metoda refera obiectul curent
      type = props.curClass.getName();
      Symbol s = new Symbol(props.self.getLineNumber(), "self");
      s.setTypeData(props.curClass);
      s.setVariable(props.self);

      object = s;
    } else {
      // Evaluam obiectul pentru a determina unde sa cautam metoda
      object.eval(props);
      type = object.getTypeData().getName();
    }

    LCPLClass methodClass = props.allClasses.get(type);
    method = takeMethod(props, name, methodClass);

    if (methodClass == null || method == null) {
      throw new LCPLException("Method " + name + " not found in class " + type,
          this);
    }

    if (arguments.size() < method.getParameters().size()) {
      throw new LCPLException("Not enough arguments in method call " + name,
          this);
    }
    if (arguments.size() > method.getParameters().size()) {
      throw new LCPLException("Too many arguments in method call " + name,
          this);
    }

    for (int i = 0; i < arguments.size(); i++) {
      // Evaluam argumentele apelului si determinan tipul acestora si tipul parametrilor metodei
      arguments.get(i).eval(props);
      String typeArg = arguments.get(i).getTypeData().getName(), typeParam =
        method.getParameters().get(i).getType();

      if (props.useFolding == true) {
        // Facem folding daca este necesar
        if (arguments.get(i).isFoldConstant()) {
          Expression e = arguments.get(i).takeConstant(props);
          arguments.set(i, e);
        }
      }

      if (typeArg.compareTo(typeParam) != 0) {
        // Daca tipurile nu se potrivesc, incercam sa facem o converie
        if (props.canConvert(typeArg, typeParam)) {
          Cast c = new Cast(arguments.get(i).getLineNumber(),
            typeParam, arguments.get(i));
          c.eval(props);

          arguments.set(i, c);
        } else {
          Properties.cannotConvert(typeArg, typeParam, this);
        }
      }
    }

    setTypeData(method.getReturnTypeData());
  }
}
