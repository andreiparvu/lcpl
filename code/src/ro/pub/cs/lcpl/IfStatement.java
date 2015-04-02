package ro.pub.cs.lcpl;

import java.io.PrintStream;
import src.Properties;

/** if <i>expression</i> then <i>expression</i> else <i>expression</i> end; */
public class IfStatement extends Expression {
  /** Conditional expression : if <i>expression</i> then */
  private Expression condition;

  /** Expression evaluated if condition is true */
  private Expression ifExpr;

  /** Expression evaluated if condition is false; or null if the else branch is missing. */
  private Expression thenExpr;

  public Expression getCondition() {
    return condition;
  }
  public void setCondition(Expression condition) {
    this.condition = condition;
  }
  public Expression getIfExpr() {
    return ifExpr;
  }
  public void setIfExpr(Expression ifExpr) {
    this.ifExpr = ifExpr;
  }
  public Expression getThenExpr() {
    return thenExpr;
  }
  public void setThenExpr(Expression thenExpr) {
    this.thenExpr = thenExpr;
  }
  public IfStatement(int lineNumber, Expression condition, Expression ifExpr,
      Expression thenExpr) {
    super(lineNumber);
    this.condition = condition;
    this.ifExpr = ifExpr;
    this.thenExpr = thenExpr;
  }
  public IfStatement() {}

  public String emitCode(PrintStream os, boolean isValue) {
    // isValue = true

    // Emitem codul pentru conditie
    String condIndex = condition.emitCode(os, true);
    String condIndex2 = "%" + Properties.index++, thenIndex = "", thenLabel = "";

    String retType = getTypeData().getName();
    String[] label = Properties.genLabels(3);

    // Comparam codul returnat de conditie cu 0
    os.println(
      "\t" + condIndex2 + " = icmp ne i32 " + condIndex + ", 0" + "\n" +
      "\tbr i1 " + condIndex2 + ", label %" + label[1] + ", label %" + label[2] + "\n" +
      label[1] + ":"
    );
    Properties.lastLabel = label[1];

    // Emitem codul pentru cazul true al conditiei si retinem ultimul label pus in acesta
    String ifIndex = ifExpr.emitCode(os, true);
    String ifLabel = Properties.lastLabel;

    if (ifExpr.getTypeData().getName().compareTo("void") == 0 &&
        retType.compareTo("(none)") != 0) {
      // Daca tipul returnat de ifExpr este void (null) atunci il convertim la tipul returnat de
      // intregul if
      String newIndex = "%" + Properties.index++;

      os.println(
        Properties.bitcast(newIndex, Properties.genType("Object"), ifIndex,
        Properties.genType(retType))
      );
      ifIndex = newIndex;
    }

    // Jump dupa expresia de else
    os.println(
      "\tbr label %" + label[3] + "\n"
    );

    os.println(label[2] + ":");
    Properties.lastLabel = label[2];

    if (thenExpr != null) {
      // Emitem cod pentru ramura de else
      thenIndex = thenExpr.emitCode(os, true);
      // Retinem ultim label
      thenLabel = Properties.lastLabel;

      if (thenExpr.getTypeData().getName().compareTo("void") == 0 &&
          retType.compareTo("(none)") != 0) {
        String newIndex = "%" + Properties.index++;

        os.println(
          Properties.bitcast(newIndex, Properties.genType("Object"), thenIndex,
            Properties.genType(retType))
        );
        thenIndex = newIndex;
      }
    }

    // Codul de dupa if
    os.println("\tbr label %" + label[3] + "\n");
    os.println(label[3] + ":");
    Properties.lastLabel = label[3];

    if (thenExpr != null /*&& getTypeData() != null*/ && retType.compareTo("void") != 0 &&
        retType.compareTo("(none)") != 0) {
      String index = "%" + Properties.index++;

      // Calculam valoarea intoarsa de if
      os.println(
        "\t" + index + " = phi " + Properties.genType(getTypeData().getName()) + "[ " + ifIndex +
          ", %" + ifLabel + "], [" + thenIndex + ", %" + thenLabel + "]"
      );

      return index;
    }

    return "";
  }
}
