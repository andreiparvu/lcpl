package ro.pub.cs.lcpl;

import java.io.PrintStream;
import src.Properties;

/** new <i>type</i> */
public class NewObject extends Expression {
  private String type;

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public NewObject(int lineNumber, String type) {
    super(lineNumber);
    this.type = type;
  }
  public NewObject() {}

  public String emitCode(PrintStream os, boolean isValue) {
    String[] index = Properties.genIndexes(2);
    String rtype1 = Properties.getRttiType(this.type), rtype2 = "@R" + this.type;
    String type = Properties.genType(this.type);

    // Apelam metoda din runtime si apoi facem cast de la void* la noul tip
    os.println(
      "\t" + index[1] + " = call i8* @__lcpl_new(%struct.__lcpl_rtti* bitcast (" + rtype1 +
        " " + rtype2 + " to %struct.__lcpl_rtti*))" + "\n" +
      Properties.bitcast(index[2], "i8*", index[1], type)
    );
    return index[2];
  }
}
