package ro.pub.cs.lcpl;

import java.io.PrintStream;
import src.Properties;

/** <i>expression</i> == <i>expression</i> */
public class EqualComparison extends BinaryOp {

  public EqualComparison(int lineNumber, Expression e1, Expression e2) {
    super(lineNumber, e1, e2);
  }
  public EqualComparison() {}

  public String emitCode(PrintStream os, boolean isValue) {
    // isValue = true
    String e1Index = getE1().emitCode(os, true), e2Index = getE2().emitCode(os, true);

    String type = getE1().getTypeData().getName();

    if (type.compareTo("String") == 0) {
      // In cazul string-urilor, folosim functia din runtime pentru comparatie
      String index = "%" + Properties.index++;

      os.println(
        "\t" + index + " = call i32 @M6_String_equal(%struct.TString* " + e1Index +
          ", %struct.TString* " + e2Index + ")\n"
      );

      return index;
    }

    String rtype = Properties.genType(type);
    String[] index = Properties.genIndexes(2);

    // Comparam cele doua expresii si extinem rezultatul pe 32 de biti
    os.println(
      "\t" + index[1] + " = icmp eq " + rtype + " " + e1Index + ", " + e2Index + "\n" +
      "\t" + index[2] + " = zext i1 " + index[1] + "to i32" + "\n"
    );

    return index[2];
  }
}
