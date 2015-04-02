package ro.pub.cs.lcpl;

import java.io.PrintStream;
import src.Properties;

/** An integer constant */
public class IntConstant extends Expression {
  private int value;

  public int getValue() {
    return value;
  }

  public void setValue(int value) {
    this.value = value;
  }

  public IntConstant(int lineNumber, int value) {
    super(lineNumber);
    this.value = value;
  }
  public IntConstant() {}


  public String emitCode(PrintStream os, boolean isValue) {
    // isValue = true
    String[] index = Properties.genIndexes(2);

    // Alocam o zona de memorie pentru constanta, si apoi intoarcem valoarea
    os.println(
      Properties.alloca(index[1], "i32") +
      Properties.store("i32", value + "", index[1]) +
      Properties.load(index[2], "i32", index[1])
    );

    return index[2];
  }
}
