// Andrei Parvu
// 341C3
package src;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import ro.pub.cs.lcpl.*;

public class Properties {
  private static int nrString = 0;
  public static List<String> constStrings = new ArrayList<String>();

  public static boolean hasIntClass = false;

  public static LCPLClass curClass;
  public static Method curMethod;
  public static int index;
  public static int label;
  public static String lastLabel;

  public static LCPLClass main;

  public static String genString(String string) {
    // Constanta string folosita in program, o tinem minte si o afisam la final
    constStrings.add(genString(string, "S" + nrString));

    return "@S" + nrString++;
  }

  public static String genString(String string, String name) {
    int length = string.length();

    // Trebuie inlocuite caracterele speciale
    string = string.replace("\\", "\\5C").replace("\n", "\\0A").replace("\t", "\\09").
      replace("\r", "\\0D").replace("\"", "\\22");

    // Cream constanta globala string
    return
      "@.str" + name + " = constant [" + (length + 1) + " x i8] c\"" + string + "\\00\"\n" +
      "@" + name + " = global %struct.TString { %struct.__lcpl_rtti* @RString, " +
        "i32 " + length + ", i8* getelementptr ([" + (length + 1) + " x i8]*" +
        " @.str" + name + ", i32 0, i32 0) }\n";
  }

  public static String genType(String type) {
    // Intoarce tipul de date folosit in codul intermediar
    if (type.compareTo("(none)") == 0) {
      return "void";
    }
    if (type.compareTo("void") == 0) {
      return "%struct.TObject*";
    }

    if (type.compareTo("Int") == 0 && !hasIntClass) {
      return "i32";
    }

    return "%struct.T" + type + "*";
  }

  public static String[] genIndexes(int nr) {
    // Creaza nr indecsi de folosit ca simboluri locale
    String[] rez = new String[nr + 1];

    for (int i = 1; i <= nr; i++) {
      rez[i] = "%" + index++;
    }

    return rez;
  }

  public static String[] genLabels(int nr) {
    // Creaza nr label-uri de folosit
    String[] rez = new String[nr + 1];

    for (int i = 1; i <= nr; i++) {
      rez[i] = "L" + label++;
    }

    return rez;
  }

  public static String getRttiType(String type) {
    // Intoarce tipul de runtime pentru un tip dat
    if (type.compareTo("IO") == 0 || type.compareTo("Object")  == 0 ||
        type.compareTo("String") == 0) {
      return "%struct.__lcpl_rtti*";
    }
    return "%R" + type + "*";
  }

  // Instructiuni des folosite pentru generarea de cod
  public static String load(String index1, String type, String index2) {
    return "\t" + index1 + " = load " + type + "* " + index2 + "\n";
  }

  public static String alloca(String index, String type) {
    return "\t" + index + " = alloca " + type + "\n";
  }

  public static String store(String type, String index1, String index2) {
    return "\tstore " + type + " " + index1 + ", " + type + "* " + index2 + "\n";
  }

  public static String bitcast(String index1, String type1, String index2, String type2) {
    return "\t" + index1 + " = bitcast " + type1 + " " + index2 + " to " + type2 + "\n";
  }

  public static String getElementPtr(String index1, String type, String index2, int pos) {
    return "\t" + index1 + " = getelementptr " + type + " " + index2 + ", i32 0, i32 " + pos + "\n";
  }
}
