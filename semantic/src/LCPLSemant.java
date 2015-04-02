// Andrei Parvu
// 341C3

import java.io.*;
import java.util.*;

import ro.pub.cs.lcpl.*;

import org.yaml.snakeyaml.*;
import org.yaml.snakeyaml.constructor.Constructor;

import src.Properties;

public class LCPLSemant {

  // Toate clasele definite in fisierul .lcpl
  public static Map<String, LCPLClass> allClasses = new HashMap<String, LCPLClass>();
  // Arborele de mosteniri de clase
  private static Map<String, List<String>> inherits = new HashMap<String, List<String>>();

  private static Program p;

  public static LCPLClass getClass(String name, TreeNode t) throws LCPLException {
    if (allClasses.get(name) == null) {
      throw new LCPLException("Class " + name + " not found.", t);
    }

    return allClasses.get(name);
  }

  // Generarea instantei clasei Object
  private static LCPLClass genObject() {
    List<Feature> f = new LinkedList<Feature>();

    f.add(new Method(0, "abort", new LinkedList<FormalParam>(), "void", null));
    f.add(new Method(0, "typeName", new LinkedList<FormalParam>(), "String", null));
    f.add(new Method(0, "copy", new LinkedList<FormalParam>(), "Object", null));

    return new LCPLClass(0, "Object", null, f);
  }

  // Generarea instantei clasei String
  private static LCPLClass genString() {
    List<Feature> f = new LinkedList<Feature>();

    f.add(new Method(0, "length", new LinkedList<FormalParam>(), "Int", null));
    f.add(new Method(0, "toInt", new LinkedList<FormalParam>(), "Int", null));

    return new LCPLClass(0, "String", null, f);
  }

  // Generarea instantei clasei IO
  private static LCPLClass genIO() {
    List<Feature> f = new LinkedList<Feature>();

    List<FormalParam> fp = new LinkedList<FormalParam>();
    fp.add(new FormalParam("msg", "String"));
    f.add(new Method(0, "out", fp, "IO", null));
    f.add(new Method(0, "in", new LinkedList<FormalParam>(), "String", null));

    return new LCPLClass(0, "IO", null, f);
  }

  public static void main(String[] args) throws LCPLException {
    if (args.length > 3 || args.length < 2)
    {
      System.err.println("Usage: LCPLSemant [-folding] <filein.yaml> <fileout.yaml>\n");
      System.exit(1);
    }
    try {
      String input, output;
      boolean useFolding = false;

      if (args.length == 3) {
        // Verificam daca este activata optiunea de folding
        if (args[0].compareTo("-folding") != 0) {
          System.err.println("Usage: LCPLSemant [-folding] <filein.yaml> <fileout.yaml>\n");
          System.exit(1);     
        }
        useFolding = true;
        input = args[1];
        output = args[2];
      } else {
        input = args[0];
        output = args[1];
      }

      Yaml yaml = new Yaml(new Constructor(Program.class));
      FileInputStream fis = new FileInputStream(input);
      p = (Program)yaml.load(fis);
      fis.close();

      // Setam tipurile de date speciale
      p.setNoType(new NoType());
      p.setIntType(new IntType());
      p.setNullType(new NullType());

      // Adaugam clasele Object, String si IO la lista de clase
      List<LCPLClass> classes = p.getClasses();
      classes.add(genString());
      classes.add(genObject());
      classes.add(genIO());

      int s = classes.size();

      p.setStringType(classes.get(s - 3));
      p.setObjectType(classes.get(s - 2));
      p.setIoType(classes.get(s - 1));

      boolean foundMain = false;
      for (Iterator<LCPLClass> it = classes.iterator(); it.hasNext(); ) {
        LCPLClass curClass = it.next();
        String className = curClass.getName();

        if (allClasses.get(className) != null) {
          throw new LCPLException("A class with the same name already exists : " +
            className, curClass);
        }
        if (className.compareTo("Main") == 0) {
          foundMain = true;
        }

        allClasses.put(className, curClass);
        if (inherits.get(className) == null) {
          // Cream o noua intrare pentru clasa curenta, pentru a stoca clasele ce o mostenesc
          inherits.put(className, new LinkedList<String>());
        }

        if (className.compareTo("Object") != 0) {
          String parent = curClass.getParent();
          if (parent == null) {
            // Orice clasa nu mosteneste nimic si nu este Object va mosteni Object
            parent = "Object";
            curClass.setParent(parent);
          }

          if (parent.compareTo("String") == 0) {
            throw new LCPLException("A class cannot inherit a String", curClass);
          }

          List<String> inherited = inherits.get(parent);

          if (inherited == null) {
            inherited = new LinkedList<String>();
          }

          // Adaugam clasa curenta la mostenirile clasei parinte
          inherited.add(className);
          inherits.put(parent, inherited);
        }
      }
      if (!foundMain) {
        throw new LCPLException("Class Main not found.", p);
      }

      // Verificam sa nu fie bucle in arborele de mosteniri
      CheckInheritance check = new CheckInheritance(inherits);
      check.start();

      Method errRetMain = null, errParamMain = null;

      Set<String> classNames = allClasses.keySet();
      for (Iterator<String> it = classNames.iterator(); it.hasNext(); ) {
        String className = it.next();
        LCPLClass curClass = allClasses.get(className);

        List<Feature> features = curClass.getFeatures();

        for (Iterator<Feature> fIt = features.iterator(); fIt.hasNext(); ) {
          Feature curFeat = fIt.next();

          if (curFeat instanceof Method) {
            Method curMethod = (Method)curFeat;

            if (curClass.takeSpecificMethod(curMethod.getName(), className) != null) {
              // Am mai definit in clasa curenta o metoda cu acelasi nume
              throw new LCPLException("A method with the same name already exists " +
                "in class " + className + " : " + curMethod.getName(), curMethod);
            }

            // Determinat tipul returnat de metoda
            Type retType;
            if (curMethod.getReturnType().compareTo("void") == 0) {
              retType = p.getNoType();
            } else if (curMethod.getReturnType().compareTo("Int") == 0) {
              retType = p.getIntType();
            } else {
              retType = getClass(curMethod.getReturnType(), curMethod);
            }
            curMethod.setReturnTypeData(retType);

            curMethod.setParent(curClass);

            // Verificam ca metoda main a clasei Main sa nu returneze nimic si sa nu primeasca
            // niciun parametru
            if (className.compareTo("Main") == 0 && curMethod.getName().compareTo("main") == 0) {
              if (curMethod.getReturnType().compareTo("void") != 0) {
                errRetMain = curMethod;
              }
              if (curMethod.getParameters().size() > 0) {
                errParamMain = curMethod;
              }
            }

            // Setam pointer catre clasa curenta
            FormalParam self = new FormalParam("self", className);
            self.setTypeData(curClass);
            curMethod.setSelf(self);

            for (Iterator<FormalParam> itp = curMethod.getParameters().iterator(); itp.hasNext(); ) {
              FormalParam fp = itp.next();
              // Evaluam parametrii
              fp.eval(new Properties(p, curClass, allClasses, self, useFolding));
            }

            curClass.addMethod(curMethod);
          } else {
            Attribute curAttr = (Attribute)curFeat;

            // Determinam tipul atributului
            Type retType;
            if (curAttr.getType().compareTo("void") == 0) {
              retType = p.getNoType();
            } else if (curAttr.getType().compareTo("Int") == 0) {
              retType = p.getIntType();
            } else {
              retType = getClass(curAttr.getType(), curAttr);
            }
            curAttr.setTypeData(retType);

            // Clasa corespondenta metodei
            curAttr.setSelfClass(curClass);

            // Verificam daca mai avem un atribuit identic in metoda curenta
            if (curClass.takeVariable(curAttr.getName(), true) != null) {
              throw new LCPLException("An attribute with the same name already exists " +
                "in class " + className + " : " + curAttr.getName(), curAttr);
            }

            curClass.addVariable(curAttr);
          }
        }
      }

      for (Iterator<String> it = classNames.iterator(); it.hasNext(); ) {
        String className = it.next();
        LCPLClass curClass = allClasses.get(className);

        List<Feature> features = curClass.getFeatures();

        for (Iterator<Feature> fIt = features.iterator(); fIt.hasNext(); ) {
          Feature curFeat = fIt.next();

          if (curFeat instanceof Method) {
            Method curMethod = (Method)curFeat;

            if (curClass.getParentData() != null) {
              // Verificam ca o eventuala metoda suprascrisa sa aiba aceeasi semnatura
              Method overLoaded = curClass.getParentData().takeMethod(curMethod.getName());
              if (overLoaded != null) {
                if (overLoaded.getParameters().size() != curMethod.getParameters().size()) {
                  throw new LCPLException("Overloaded method has a different number of parameters",
                    curMethod);
                }
                if (overLoaded.getReturnType().compareTo(curMethod.getReturnType()) != 0) {
                  throw new LCPLException("Return type changed in overloaded method.", curMethod);
                }
                for (int i = 0; i < curMethod.getParameters().size(); i++) {
                  if (curMethod.getParameters().get(i).getType().compareTo(
                    overLoaded.getParameters().get(i).getType()) != 0) {
                    throw new LCPLException("Parameter " +
                      curMethod.getParameters().get(i).getName() +
                      " has a different type in overloaded method.", curMethod);
                  }
                }
              }
            }

            for (Iterator<FormalParam> itp = curMethod.getParameters().iterator(); itp.hasNext(); ) {
              FormalParam fp = itp.next();

              // Adaugam parametrul metodei pe stiva de variabile a clasei
              curClass.addVariable(fp);
            }

            if (curMethod.getBody() != null) {
              // Evaluam corpul unei metode si verificam corectitudinea valorii de return
              Properties props =
                new Properties(p, curClass, allClasses, curMethod.getSelf(), useFolding);
              curMethod.getBody().eval(props);
              String retType = curMethod.getReturnType();

              if (retType.compareTo("void") != 0 &&
                !props.canConvert(curMethod.getBody().getType(), retType)) {
                Properties.cannotConvert(curMethod.getBody().getType(), retType, curMethod.getBody());
              }
            }

            for (Iterator<FormalParam> itp = curMethod.getParameters().iterator(); itp.hasNext(); ) {
              // Scoatem parametrul de pe stiva de variabile a clasei
              curClass.removeVariable(itp.next());
            }
          } else {
            Attribute curAttr = (Attribute)curFeat;

            // Verificam sa nu fi definit un atribut cu acelasi nume intr-o clasa parinte
            if (curClass.getParentData() != null) {
              Variable v = curClass.getParentData().takeVariable(curAttr.getName(), false);
              if (v != null) {
                throw new LCPLException("Attribute " + curAttr.getName() + " is redefined.",
                  ((Attribute)v).takeSelfClass());
              }
            }

            if (curAttr.getInit() != null) {
              // Evaluam valoarea de initializare a atributului
              FormalParam self = new FormalParam("self", curAttr.getType());
              self.setTypeData(curClass);
              curAttr.setAttrInitSelf(self);
              curAttr.getInit().eval(new Properties(p, curClass, allClasses, self, useFolding));

              if (useFolding == true) {
                // Facem folding, daca putem
                if (curAttr.getInit().isFoldConstant()) {
                  Expression init = curAttr.getInit().takeConstant(
                    new Properties(p, curClass, allClasses, self, useFolding));
                  curAttr.setInit(init);
                }
              }
            }
          }
        }

        if (errRetMain != null) {
          throw new LCPLException("The function main should not return any value", errRetMain);
        }
        if (errParamMain != null) {
          throw new LCPLException("The function main should not have any parameters", errParamMain);
        }
      }

      Yaml yamlOut = new Yaml();
      PrintStream fos = new PrintStream(new FileOutputStream(output));
      fos.println(yamlOut.dump(p));
      fos.close();
    } catch (IOException ex) {
      System.err.println("File error: " + ex.getMessage());
      System.err.println("===================================================");
    } catch (LCPLException ex) {
      System.err.println("Error in line " + ex.node.getLineNumber() + " : " + ex.message);
    }

  }

}

class CheckInheritance {
  private Map<String, List<String>> inherits;
  private Set<String> open = new HashSet<String>(), used = new HashSet<String>();

  public CheckInheritance(Map<String, List<String>> inherits) {
    this.inherits = inherits;
  }

  public void start() throws LCPLException {
    Set<String> names = inherits.keySet();
    // Facem o parcurgere DFS din clasele pe care nu le-am vizitat
    for (Iterator<String> it = names.iterator(); it.hasNext(); ) {
      String curClass = it.next();
      if (!used.contains(curClass)) {
        checkRec(curClass);
      }
    }
  }

  private void checkRec(String curClass) throws LCPLException {
    used.add(curClass); // clase vizitate
    open.add(curClass); // clase in curs de expandare - noduri gri

    for (Iterator<String> it = inherits.get(curClass).iterator(); it.hasNext(); ) {
      String nextClass = it.next();
      LCPLClass next = LCPLSemant.getClass(nextClass, null);
      if (open.contains(nextClass)) {
        // S-a creat o bucla
        throw new LCPLException("Class " + nextClass + " recursively inherits itself.", next);
      }
      // Setam informatiile despre parinte
      next.setParentData(LCPLSemant.getClass(curClass, next));
      if (!used.contains(nextClass)) {
        checkRec(nextClass);
      }
    }

    open.remove(curClass);
  }
}
