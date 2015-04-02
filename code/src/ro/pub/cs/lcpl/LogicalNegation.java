package ro.pub.cs.lcpl;

import java.io.PrintStream;
import src.Properties;

/** ! <i>expression</i> */
public class LogicalNegation extends UnaryOp {

  public LogicalNegation(int lineNumber, Expression e1) {
    super(lineNumber, e1);
  }

  public LogicalNegation() {}

  public String emitCode(PrintStream os, boolean isValue) {
    // isValue = true
    String e1Index = getE1().emitCode(os, true);
    String[] index = Properties.genIndexes(3);

    // Comparam cu 0 rezultatul expresiei, facem xor si apoi extindem la 32 de biti
    os.println(
      "\t" + index[1] + " = icmp ne i32 " + e1Index + ", 0" + "\n" +
      "\t" + index[2] + " = xor i1 " + index[1] + ", 1" +
      "\t" + index[3] + " = sext i1 " + index[2] + " to i32"
    );

    return index[3];
  }
}
