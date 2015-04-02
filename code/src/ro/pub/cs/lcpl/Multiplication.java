package ro.pub.cs.lcpl;

import java.io.PrintStream;
import src.Properties;

/** <i>expression</i> * <i>expression</i> */
public class Multiplication extends BinaryOp {

  public Multiplication(int lineNumber, Expression e1, Expression e2) {
    super(lineNumber, e1, e2);
  }

  public Multiplication(){}

  public String emitCode(PrintStream os, boolean isValue) {
    // isValue = true
    String e1Index = getE1().emitCode(os, true), e2Index = getE2().emitCode(os, true);

    String index = "%" + Properties.index++;
    os.println("\t" + index + " = mul i32 " + e1Index + ", " + e2Index);

    return index;
  }
}
