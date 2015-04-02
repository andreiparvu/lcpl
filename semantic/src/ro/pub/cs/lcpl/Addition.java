package ro.pub.cs.lcpl;

import src.Properties;

/** <i>expression</i> + <i>expression</i> */
public class Addition extends BinaryOp {

  public Addition(int lineNumber, Expression e1, Expression e2) {
    super(lineNumber, e1, e2);
  }

  public Addition() {}

  protected void compConsValue() {
    // Calculam constanta pentru folding
    if (getTypeData().getName().compareTo("Int") == 0) {
      // Rezultatul este Int, facem adunare simpla
      this.typeConstant = this.INT;
      setIntConstant(getE1().intConstant + getE2().intConstant);
    } else {
      this.typeConstant = this.STRING;
      this.stringConstant = getE1().stringConstant + getE2().stringConstant;
    }
  }

  public void eval(Properties props) throws LCPLException {
    if (this.isFoldConstant()) {
      return ;
    }

    getE1().eval(props);
    getE2().eval(props);

    String type1 = getE1().getTypeData().getName(), type2 = getE2().getTypeData().getName();

    if (type1.compareTo("Int") == 0) {
      if (type2.compareTo("String") == 0 || type2.compareTo("Int") == 0) {
        // Avem Int + [Int | String]
        if (type2.compareTo("String") == 0) {
          // Adunam Int cu String, trebuie convertit Int-ul la String
          castE1("String", props);
        }
        setTypeData(getE2().getTypeData());
        checkConst(props);
      } else {
        throw new LCPLException("Cannot convert '+' expression to Int or String", this);
      }

      return ;
    }
    if (type2.compareTo("Int") == 0 || type2.compareTo("String") == 0) {
      if (type1.compareTo("String") == 0) {
        // Avem String + [Int | String]
        if (type2.compareTo("Int") == 0) {
          // Adunam String cu Int, trebuie convertit Int-ul la String
          castE2("String", props);
        }
        setTypeData(getE1().getTypeData());
        checkConst(props);
      } else {
        Properties.cannotConvert(type1, type2, this);
      }

      return ;
    }

    if (type1.compareTo("String") == 0) {
      Properties.cannotConvert(type2, "String", this);
    }

    throw new LCPLException("Cannot covert '+' expression to Int or String", this);
  }
}
