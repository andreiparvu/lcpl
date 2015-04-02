package ro.pub.cs.lcpl;

import java.io.PrintStream;
import src.Properties;

/** while <i>condition</i> loop <i>loopBody</i> end; */
public class WhileStatement extends Expression {
  private Expression condition;
  private Expression loopBody;
  public Expression getCondition() {
    return condition;
  }
  public void setCondition(Expression condition) {
    this.condition = condition;
  }
  public Expression getLoopBody() {
    return loopBody;
  }
  public void setLoopBody(Expression loopBody) {
    this.loopBody = loopBody;
  }
  public WhileStatement(int lineNumber, Expression condition,
      Expression loopBody) {
    super(lineNumber);
    this.condition = condition;
    this.loopBody = loopBody;
  }
  public WhileStatement() {}

  public String emitCode(PrintStream os, boolean isValue) {
    // isValue = true
    String[] label = Properties.genLabels(3);

    // Labelul dinaintea conditiei
    os.println(
      "\tbr label %" + label[1] + "\n" +
      label[1] + ":"
    );

    // Emitem codul pentru conditie
    String condIndex = condition.emitCode(os, true), condIndex2 = "%" + Properties.index++;

    // Testam conditia
    os.println(
      "\t" + condIndex2 + " = icmp ne i32 " + condIndex + ", 0" +
      "\tbr i1 " + condIndex2 + ", label %" + label[2] + ", label %" + label[3] + "\n" +
      label[2] + ":"
    );

    // Emitem codul pentru corpul while-ului
    loopBody.emitCode(os, true);

    // Sarim la evaluarea conditiei
    os.println(
      "\tbr label %" + label[1] + "\n" +
      label[3] + ":"
    );
    Properties.lastLabel = label[3];

    return "";
  }
}
