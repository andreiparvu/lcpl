package ro.pub.cs.lcpl;

import java.io.PrintStream;
import src.Properties;

/** <i>expression</i> - <i>expression</i> */
public class Subtraction extends BinaryOp {

  public Subtraction(int lineNumber, Expression e1, Expression e2) {
    super(lineNumber, e1, e2);
  }

  public Subtraction() {}


  public String emitCode(PrintStream os, boolean isValue) {
    // isValue = true
    String e1Index = getE1().emitCode(os, true), e2Index = getE2().emitCode(os, true);

    String index = "%" + Properties.index++;
    os.println(
      "\t" + index + " = sub i32 " + e1Index + ", " + e2Index + "\n"
    );

    return index;
  }
}
