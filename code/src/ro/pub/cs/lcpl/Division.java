package ro.pub.cs.lcpl;

import java.io.PrintStream;
import src.Properties;

/** <i>expression</i> / <i>expression</i> */
public class Division extends BinaryOp {
  public Division(int lineNumber, Expression e1, Expression e2) {
    super(lineNumber, e1, e2);
  }
  public Division() {}

  public String emitCode(PrintStream os, boolean isValue) {
    // isValue = true
    String e1Index = getE1().emitCode(os, true), e2Index = getE2().emitCode(os, true);

    // Facem impartire cu semn
    String index = "%" + Properties.index++;
    os.println("\t" + index + " = sdiv i32 " + e1Index + ", " + e2Index);

    return index;
  }
}
