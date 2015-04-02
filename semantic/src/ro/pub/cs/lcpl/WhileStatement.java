package ro.pub.cs.lcpl;

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

  public void eval(Properties props) throws LCPLException {
    // Evaluam conditia, verificam sa fie Int si evaluam corpul
    condition.eval(props);

    if (condition.getTypeData().getName().compareTo("Int") != 0) {
      throw new LCPLException("While condition must be Int", this);
    }

    if (props.useFolding) {
      if (condition.isFoldConstant()) {
        condition = condition.takeConstant(props);
      }
    }

    loopBody.eval(props);

    setTypeData(props.p.getNoType());
  }
}
