package ro.pub.cs.lcpl;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

import src.Properties;

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

  public int size;
  public List<String> methodTable = null;
  public List<String> methodSignatures = null;

  public List<Attribute> attributes = null;

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

  public void emitStructure(PrintStream os) {
    // Reprezentarea obiectului in memorie
    os.print("%struct.T" + getName() + " = type { %struct.__lcpl_rtti*");
    int nrs = 0;

    // Generam atributele
    genAttributes();
    size = 4 + 4 * attributes.size();

    for (int i = 0; i < attributes.size(); i++) {
      // Emitem structura atributelor
      os.print(", " + Properties.genType(attributes.get(i).getType()));
    }
    os.println(" }");

    if (name.compareTo("Main") == 0) {
      Properties.main = this;
    }
  }

  private void emitConstructor(PrintStream os) {
    Properties.curClass = this;
    Properties.index = 3;

    String type = Properties.genType(name);

    os.println("; Class constructor for " + name);
    // Salvam local self si apelam constructorul clasei parinte
    os.println(
      "define void @" + name + "_init(" + type + " %self) {" + "\n" +
      Properties.alloca("%1", type) +
      Properties.store(type, "%self", "%1") +
      Properties.bitcast("%2", type, "%self", Properties.genType(parent)) +
      "\tcall void @" + parent + "_init(%struct.T" + parent + "* %2)\n"
    );

    // Toate stringurile trebuie initializate inainte de a initializa alti parametrii
    for (int i = 0; i < features.size(); i++) {
      if (features.get(i) instanceof Attribute) {
        Attribute curAttr = (Attribute)features.get(i);

        String attrIndex = curAttr.load(os, false);

        if (curAttr.getType().compareTo("String") == 0) {
          String[] index = Properties.genIndexes(2);

          os.println(
            "\t" + index[1] + " = call i8* @__lcpl_new(%struct.__lcpl_rtti* @RString)" + "\n" +
            Properties.bitcast(index[2], "i8*", index[1], "%struct.TString*") +
            Properties.store("%struct.TString*", index[2], attrIndex)
          );
        }
      }
    }

    // Initializam fiecare atribut
    for (int i = 0; i < features.size(); i++) {
      if (features.get(i) instanceof Attribute) {
        Attribute curAttr = (Attribute)features.get(i);

        String attrIndex = curAttr.load(os, false);
        if (curAttr.getInit() != null) {
          curAttr.getAttrInitSelf().index = "%1";

          // Emitem codul pentru expresia de initializare
          String initIndex = curAttr.getInit().emitCode(os, true);
          String attrType = Properties.genType(curAttr.getTypeData().getName());

          // Adaugam valoarea la atribut
          os.println(Properties.store(attrType, initIndex, attrIndex));
        } // alte initializari?
      }
    }

    os.println("\n\tret void\n}\n");
  }

  public void emitMethods(PrintStream os) {
    // Emitem constructorul
    emitConstructor(os);

    // Emitem codul pentru fiecare metoda
    for (int i = 0; i < features.size(); i++) {
      if (features.get(i) instanceof Method) {
        ((Method)features.get(i)).emitCode(os);

        os.println();
      }
    }
  }

  public List<Attribute> genAttributes() {
    if (attributes != null) {
      return attributes;
    }

    // Construim lista de atribute de la parinte
    if (name.compareTo("Object") == 0) {
      attributes = new ArrayList<Attribute>();
    } else {
      attributes = new ArrayList<Attribute>(parentData.genAttributes());
    }

    // Adaugam si atributele clasei curente
    for (int i = 0; i < features.size(); i++) {
      if (features.get(i) instanceof Attribute) {
        attributes.add((Attribute)features.get(i));
        ((Attribute)features.get(i)).attrPos = attributes.size();
      }
    }

    return attributes;
  }

  public List<String> genMethodTable() {
    if (methodTable != null) {
      return methodTable;
    }

    // Construim lista de metode de la parinte
    if (name.compareTo("Object") == 0) {
      methodTable = new ArrayList<String>();
      methodSignatures = new ArrayList<String>();
    } else {
      methodTable = new ArrayList<String>(parentData.genMethodTable());
      methodSignatures = new ArrayList<String>(parentData.methodSignatures);
    }

    // Adaugam metodele clasei curente, sau inlocuim o metoda suprascrisa
    for (int i = 0; i < features.size(); i++) {
      if (features.get(i) instanceof Method) {
        Method curMethod = (Method)features.get(i);

        int pos = methodTable.indexOf(curMethod.getName());
        if (pos == -1) {
          methodTable.add(curMethod.getName());
          methodSignatures.add(curMethod.emitSignature() + curMethod.getIRName());
        } else {
          methodSignatures.set(pos, curMethod.emitSignature() + curMethod.getIRName());
        }
      }
    }

    return methodTable;
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
