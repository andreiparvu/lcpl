package ro.pub.cs.lcpl;
import java.io.PrintStream;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Iterator;

import src.Properties;

/** A complete LCPL program
 * <i>class</i> ... 
 */
public class Program extends TreeNode {
  /** List of all classes in the program.
   * Add the built-in classes below to this list: 
   * Program.objectType, Program.stringType, Program.ioType. */
  private List<LCPLClass> classes;

  /** Create a new IntType object representing the type of all Int expressions in the program. */
  private IntType intType;

  /** Create a new NoType object representing the type of all expressions in the program that do not return a value, such as 'while'. */
  private NoType noType;

  /** Create a new NullType object representing the type of all expressions in the program that are evaluated to the constant 'void'. */
  private NullType nullType;

  /** Create a new LCPLClass object representing the Object class */
  private LCPLClass objectType;

  /** Create a new LCPLClass object representing the String class */
  private LCPLClass stringType;

  /** Create a new LCPLClass object representing the IO class */
  private LCPLClass ioType;

  public List<LCPLClass> getClasses() {
    return classes;
  }

  public void setClasses(List<LCPLClass> classes) {
    this.classes = classes;
  }

  public IntType getIntType() {
    return intType;
  }

  public void setIntType(IntType intType) {
    this.intType = intType;
  }

  public NoType getNoType() {
    return noType;
  }

  public void setNoType(NoType noType) {
    this.noType = noType;
  }

  public NullType getNullType() {
    return nullType;
  }

  public void setNullType(NullType nullType) {
    this.nullType = nullType;
  }

  public LCPLClass getObjectType() {
    return objectType;
  }

  public void setObjectType(LCPLClass objectType) {
    this.objectType = objectType;
  }

  public LCPLClass getStringType() {
    return stringType;
  }

  public void setStringType(LCPLClass stringType) {
    this.stringType = stringType;
  }

  public LCPLClass getIoType() {
    return ioType;
  }

  public void setIoType(LCPLClass ioType) {
    this.ioType = ioType;
  }

  public Program(int lineNumber, List<LCPLClass> classes) {
    super(lineNumber);
    this.classes = classes;
  }
  public Program () {}

  public void emitIR(PrintStream os) {
    List<LCPLClass> workClasses = new ArrayList<LCPLClass>();

    for (int i = 0; i < classes.size(); i++) {
      String curName = classes.get(i).getName();
      classes.get(i).genMethodTable();

      if (curName.compareTo("Object") != 0 && curName.compareTo("String") != 0 &&
        curName.compareTo("IO") != 0) {
        // Vom crea doar clasele care nu sunt de baza
        workClasses.add(classes.get(i));

        if (curName.compareTo("Int") == 0) {
          // Putem avea clasa Int
          Properties.hasIntClass = true;
        }
      }
    }

    // Emitem structura pentru fiecare clasa
    os.println("; Structure of classes\n");
    for (int i = 0; i < workClasses.size(); i++) {
      workClasses.get(i).emitStructure(os);
    }

    os.println();

    // Emitem numele pentru fiecare clasa
    os.println("; Names of classes\n");
    for (int i = 0; i < workClasses.size(); i++) {
      String curName = workClasses.get(i).getName();
      os.println(Properties.genString(curName, "N" + curName));
    }
    os.println();

    os.println("; Return type information and virtual tables\n");
    for (int i = 0; i < workClasses.size(); i++) {
      LCPLClass curClass = workClasses.get(i);
      List<String> methodTable = curClass.genMethodTable();

      // Pentru clasele definite de utilizator, trebuie facut cast la struct __lcpl_rtti
      String rtti;
      if (curClass.getParent().compareTo("IO") == 0 || curClass.getParent().compareTo("Object")  == 0 ||
        curClass.getParent().compareTo("String") == 0) {
        rtti = "@R" + curClass.getParent();
      } else {
        rtti = "bitcast (%R" + curClass.getParent() + "* @R" + curClass.getParent() + " to %struct.__lcpl_rtti *) ";
      }

      // Structura de runtime
      os.println(
        "%R" + curClass.getName() + " = type { %struct.TString*, i32, %struct.__lcpl_rtti*, [" +
        (methodTable.size() + 1) + " x i8*] }"
      );
      os.println(
        "@R" + curClass.getName() + " = global %R" + curClass.getName() + " { %struct.TString* @N" +
        curClass.getName() + ", i32 " +
        curClass.size + ", %struct.__lcpl_rtti* " + rtti + ",\n" +
        "[" + (methodTable.size() + 1) + " x i8*] ["
      );

      // Constructorul
      os.print(
        "\ti8* bitcast (void (%struct.T" + curClass.getName() + "*)* @" + curClass.getName() +
        "_init to i8*)"
      );

      // Celelalte metode
      for (int j = 0; j < methodTable.size(); j++) {
        os.println(",");
        os.print("\ti8* bitcast (" + curClass.methodSignatures.get(j) + " to i8*)");
      }
      os.println("\n]}\n");
    }

    // Generam metodele pentru fiecare clasa definita de utilizator
    for (int i = 0; i < workClasses.size(); i++) {
      os.println();
      os.println("; Methods for class " + workClasses.get(i).getName() + "\n");

      workClasses.get(i).emitMethods(os);
    }

    // Afisam constantele de stringuri, adunate pe parcursul generarii coodului
    for (int i = 0; i < Properties.constStrings.size(); i++) {
      os.println(Properties.constStrings.get(i));
    }
  }
}
