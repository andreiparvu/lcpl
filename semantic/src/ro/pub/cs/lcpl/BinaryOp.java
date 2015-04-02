package ro.pub.cs.lcpl;

import src.Properties;

/** A generic two-operand operation
 * <i>expression</i> <i>operand</i> <i>expression</i>
 */
public class BinaryOp extends Expression {
  private Expression e1;
  private Expression e2;
  public Expression getE1() {
    return e1;
  }
  public void setE1(Expression e1) {
    this.e1 = e1;
  }
  public Expression getE2() {
    return e2;
  }
  public void setE2(Expression e2) {
    this.e2 = e2;
  }
  public BinaryOp(int lineNumber, Expression e1, Expression e2) {
    super(lineNumber);
    this.e1 = e1;
    this.e2 = e2;
  }
  public BinaryOp() {}

  // Metode pentru introducerea de casturi in operatiile + sau =
  protected void castE1(String type, Properties props) throws LCPLException {
    Cast c = new Cast(getE1().getLineNumber(), type, getE1());
    c.eval(props);

    setE1(c);
  }

  protected void castE2(String type, Properties props) throws LCPLException {
    Cast c = new Cast(getE2().getLineNumber(), type, getE2());
    c.eval(props);

    setE2(c);
  }

  protected void compConsValue() {}

  public void checkConst(Properties props) throws LCPLException {
    if (props.useFolding == true) {
      // Daca avem optiunea e folding si ambii operanzi sunt constante, calculam valoarea operatiei
      // folosind metoda compConsValue, suprascrisa in fiecare clasa
      Expression e1 = getE1(), e2 = getE2();

      if (e1.isConstant > 0 && e2.isConstant > 0) {
        this.isConstant = Expression.FOLD_CONSTANT;

        compConsValue();

        setLineNumber(e1.getLineNumber());
        setE1(null);
        setE2(null);

        return ;
      }

      // Nu e toate operatia constanta, cream constante unde putem
      if (e1.isFoldConstant()) {
        setE1(e1.takeConstant(props));
      }
      if (e2.isFoldConstant()) {
        setE2(e2.takeConstant(props));
      }
    }
  }

  public void eval(Properties props) throws LCPLException {
    if (this.isConstant > 0) {
      return ;
    }

    // Functie de evaluare generica, pentru operatii unde nu avem nevoie de conversii
    setTypeData(props.p.getIntType());
    e1.eval(props);
    e2.eval(props);

    if (e1.getType().compareTo("Int") != 0) {
      Properties.cannotConvert(e1.getType(), "Int", this);
    }
    if (e2.getType().compareTo("Int") != 0) {
      Properties.cannotConvert(e2.getType(), "Int", this);
    }

    checkConst(props);

    return ;
  }
}
