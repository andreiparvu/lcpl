package ro.pub.cs.lcpl;

import java.io.PrintStream;
import src.Properties;

/** the constant "void" */
public class VoidConstant extends Expression {

  public VoidConstant(int lineNumber) {
    super(lineNumber);
  }

  public VoidConstant() {}

  public String emitCode(PrintStream os, boolean isValue) {
    String index = "%" + Properties.index++;

    // Facem cast pointerului null la un pointer la Object
    os.println(
      Properties.bitcast(index, "i8*", "null", "%struct.TObject*")
    );

    return index;
  }
}
