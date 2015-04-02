package ro.pub.cs.lcpl;

import java.io.PrintStream;
import src.Properties;

/** <i>expression</i> + <i>expression</i> */
public class Addition extends BinaryOp {

  public Addition(int lineNumber, Expression e1, Expression e2) {
    super(lineNumber, e1, e2);
  }

  public Addition() {}

  public String emitCode(PrintStream os, boolean isValue) {
    // Folosita ca valoare tot timpul, deci isValue este true
    String e1Index = getE1().emitCode(os, true), e2Index = getE2().emitCode(os, true);

    String type = getE1().getTypeData().getName();
    String index = "%" + Properties.index++;;

    if (type.compareTo("Int") == 0) {
      // Adunam intregi
      os.println(
        "\t" + index + " = add i32 " + e1Index+ ", " + e2Index + "\n"
      );

      return index;
    }

    // Concatenam stringuri
    os.println(
      "\t" + index + " = call %struct.TString* @M6_String_concat(%struct.TString* " + e1Index +
        ", %struct.TString* " + e2Index + ")\n"
    );

    return index;
  }
}
