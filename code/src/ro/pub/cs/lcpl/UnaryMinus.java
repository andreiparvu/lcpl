package ro.pub.cs.lcpl;

import java.io.PrintStream;
import src.Properties;

/** - <i>expression</i>, in unary operator context */
public class UnaryMinus extends UnaryOp {

  public UnaryMinus(int lineNumber, Expression e1) {
    super(lineNumber, e1);
  }
  public UnaryMinus() {}

  public String emitCode(PrintStream os, boolean isValue) {
    // isValue = true
    String e1Index = getE1().emitCode(os, true);

    String index = "%" + Properties.index++;
    os.println(
      "\t" + index + " = sub i32 " + 0 + ", " + e1Index + "\n"
    );

    return index;
  }
}
