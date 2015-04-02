package ro.pub.cs.lcpl;

import java.io.PrintStream;
import src.Properties;

/** <i>expression</i> < <i>expression</i> */
public class LessThan extends BinaryOp {

  public LessThan(int lineNumber, Expression e1, Expression e2) {
    super(lineNumber, e1, e2);
  }

  public LessThan() {}

  public String emitCode(PrintStream os, boolean isValue) {
    String e1Index = getE1().emitCode(os, true), e2Index = getE2().emitCode(os, true);

    String[] index = Properties.genIndexes(2);

    // Comparam cele doua valori si extindem rezultatul pentru i32
    os.println(
      "\t" + index[1] + " = icmp slt i32 " + e1Index + ", " + e2Index + "\n" +
      "\t" + index[2] + " = zext i1 " + index[1] + "to i32" + "\n"
    );

    return index[2];
  }
}
