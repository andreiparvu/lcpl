package ro.pub.cs.lcpl;

import java.io.PrintStream;
import src.Properties;

/** An explicit cast
 * { <i>type</i> <i>expression</i> }
 */
public class Cast extends Expression {
  private String type;
  private Expression e1;
  public String getType() {
    return type;
  }
  public void setType(String type) {
    this.type = type;
  }
  public Expression getE1() {
    return e1;
  }
  public void setE1(Expression e1) {
    this.e1 = e1;
  }
  public Cast(int lineNumber, String type, Expression e1) {
    super(lineNumber);
    this.type = type;
    this.e1 = e1;
  }
  public Cast() {}

  public String emitCode(PrintStream os, boolean isValue) {
    // isValue = true
    String e1Index = e1.emitCode(os, true);
    String e1Type = e1.getTypeData().getName();

    String[] index;

    if (type.compareTo("String") == 0) {
      if (e1Type.compareTo("Int") == 0 && !Properties.hasIntClass) {
        // Facem conversie din Int in String
        String index1 = "%" + Properties.index++;

        os.println("\t" + index1 + " = call %struct.TString* @__lcpl_intToString(i32 " + e1Index + ")");

        return index1;
      }
    }

    String rE1Type = Properties.genType(e1Type);
    String rtype = Properties.genType(type);

    index = Properties.genIndexes(4);

    String ptrType = Properties.getRttiType(type);

    // Convertim expresia la void*, facem conversia folosind functia din runtime si apoi convertim
    // pointerul la ce ne trebuie
    os.println(
      Properties.bitcast(index[1], rE1Type, e1Index, "i8*") +
      Properties.bitcast(index[2], ptrType, "@R" + type, "%struct.__lcpl_rtti*") +
      "\t" + index[3] + " = call i8* @__lcpl_cast(i8* " + index[1] + ", %struct.__lcpl_rtti* "
        + index[2] + ")\n" +
      Properties.bitcast(index[4], "i8*", index[3], rtype)
    );

    return index[4];
  }
}
