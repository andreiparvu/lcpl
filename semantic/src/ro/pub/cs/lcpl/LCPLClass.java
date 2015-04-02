package ro.pub.cs.lcpl;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

/** An LCPL class
 * class <i>name</i> inherits <i>parent</i> <i>feature</i>... end;
 */
public class LCPLClass extends TreeNode implements Type {
  private String name;

  /** Name of the superclass, it can be null */
  private String parent;
  private List<Feature> features;

  /** A reference to the superclass of this class, or "null" for the class hierarchy root (Object) */
  private LCPLClass parentData;

  // Stiva de variabile ale clasei
  private HashMap<String, List<Variable>> variables = new HashMap<String, List<Variable>>();
  // Lista de metode ale clasei
  private HashMap<String, Method> methods = new HashMap<String, Method>();

  public void addVariable(Variable v) {
    if (variables.get(v.getName()) == null) {
      variables.put(v.getName(), new ArrayList<Variable>());
    }

    variables.get(v.getName()).add(v);
  }

  public void removeVariable(Variable v) {
    variables.get(v.getName()).remove(variables.get(v.getName()).size() - 1);
  }

  public Variable takeVariable(String name, boolean self) {
    List<Variable> curVars = variables.get(name);

    if (self) {
      // self este true - cautam numai variabile care fac parte din clasa curenta, nu si din clase
      // mostenite
      if (curVars == null || curVars.size() == 0) {
        return null;
      }
      if (curVars.get(0) instanceof Attribute) {
        return curVars.get(0);
      }
      return null;
    }

    // Mergem in sus pe ierarhia de clase, pana gasim variabila
    if (curVars != null && curVars.size() > 0) {
      return curVars.get(curVars.size() - 1);
    } else {
      if (parentData == null) {
        return null;
      }
      return parentData.takeVariable(name, false);
    }
  }

  public void addMethod(Method method) {
    methods.put(method.getName(), method);
  }

  public Method takeMethod(String methodName) {
    // Mergem in sus pe ierarhia de clase si cautam metoda cu numele dat
    if (methods.get(methodName) == null) {
      if (parentData == null) {
        return null;
      }
      return parentData.takeMethod(methodName);
    }
    return methods.get(methodName);
  }

  public Method takeSpecificMethod(String methodName, String className) throws LCPLException {
    // Cautam o metoda a unei clase specifice: gasim clasa si returnam metoda
    if (name.compareTo(className) == 0) {
      return methods.get(methodName);
    }
    if (parentData == null) {
      throw new LCPLException("", null);
    }
    return parentData.takeSpecificMethod(methodName, className);
  }

  public String getName() {
    return name;
  }
  public void setName(String name) {
    this.name = name;
  }
  public String getParent() {
    return parent;
  }
  public void setParent(String parent) {
    this.parent = parent;
  }
  public LCPLClass getParentData() {
    return parentData;
  }
  public void setParentData(LCPLClass parentData) {
    this.parentData = parentData;
  }
  public List<Feature> getFeatures() {
    return features;
  }
  public void setFeatures(List<Feature> features) {
    this.features = features;
  }

  public LCPLClass(int lineNumber, String name, String parent,
      List<Feature> features) {
    super(lineNumber);
    this.name = name;
    this.parent = parent;
    this.features = features;
  }
  public LCPLClass() {}


}
