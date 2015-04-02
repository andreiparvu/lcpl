package ro.pub.cs.lcpl;

import java.io.PrintStream;
import src.Properties;

/** <i>stringExpression</i> [ <i>start</i> , <i>stop</i> ] */
public class SubString extends Expression {
  private Expression stringExpr;
  private Expression startPosition;
  private Expression endPosition;
  public Expression getStringExpr() {
    return stringExpr;
  }
  public void setStringExpr(Expression stringExpr) {
    this.stringExpr = stringExpr;
  }
  public Expression getStartPosition() {
    return startPosition;
  }
  public void setStartPosition(Expression startPosition) {
    this.startPosition = startPosition;
  }
  public Expression getEndPosition() {
    return endPosition;
  }
  public void setEndPosition(Expression endPosition) {
    this.endPosition = endPosition;
  }
  public SubString(int lineNumber, Expression stringExpr,
      Expression startPosition, Expression endPosition) {
    super(lineNumber);
    this.stringExpr = stringExpr;
    this.startPosition = startPosition;
    this.endPosition = endPosition;
  }
  public SubString() {}

  public String emitCode(PrintStream os, boolean isValue) {
    // isValue = true

    String indexString = stringExpr.emitCode(os, true),
           indexStart = startPosition.emitCode(os, true),
           indexEnd = endPosition.emitCode(os, true);

    String index = "%" + Properties.index++;

    // Apelam metoda din runtime
    os.println(
      "\t" + index + " = call %struct.TString* @M6_String_substring(%struct.TString* " +
        indexString + ", i32 " + indexStart + ", i32 " + indexEnd + ")\n"
    );

    return index;
  }
}
