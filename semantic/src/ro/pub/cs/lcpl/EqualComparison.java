package ro.pub.cs.lcpl;

import src.Properties;

/** <i>expression</i> == <i>expression</i> */
public class EqualComparison extends BinaryOp {

  public EqualComparison(int lineNumber, Expression e1, Expression e2) {
    super(lineNumber, e1, e2);
  }
  public EqualComparison() {}

  protected void compConsValue() {
    // Calcul valoare pentru constant folding
    this.typeConstant = this.INT;
    if (getE1().typeConstant == this.STRING || getE2().typeConstant == this.STRING) {
      if (getE1().stringConstant == null || getE2().stringConstant == null) {
        // Avem de comparat un string cu un void
        setIntConstant(0);
      } else if (getE1().stringConstant.compareTo(getE2().stringConstant) == 0) {
        setIntConstant(1);
      } else {
        setIntConstant(0);
      }

      return ;
    }

    if (getE1().intConstant == getE2().intConstant) {
      setIntConstant(1);
    } else {
      setIntConstant(0);
    }
  }

  public void eval(Properties props) throws LCPLException {
    getE1().eval(props);
    getE2().eval(props);

    setTypeData(props.p.getIntType());

    String type1 = getE1().getTypeData().getName(), type2 = getE2().getTypeData().getName();

    if (type1.compareTo(type2) != 0) {
      // Trebuie sa introducem noduri de cast pentru a aduce ambii operanzi la acelasi tip
      if ((type1.compareTo("void") == 0 || type1.compareTo("Object") == 0)
          && props.allClasses.get(type2) != null) {
        castE2("Object", props);
      } else if ((type2.compareTo("void") == 0 || type2.compareTo("Object") == 0)
          && props.allClasses.get(type1) != null) {
        castE1("Object", props);
      } else if (type1.compareTo("Int") == 0 && type2.compareTo("String") == 0) {
        castE1("String", props);
      } else if (type2.compareTo("Int") == 0 && type1.compareTo("String") == 0) {
        castE2("String", props);
      } else if (props.allClasses.get(type1) != null && props.allClasses.get(type2) != null) {
        castE1("Object", props);
        castE2("Object", props);
      } else {
        throw new LCPLException("Invalid type of parameters for == expression", this);
      }
    }

    checkConst(props);
  }
}
