package ro.pub.cs.lcpl;

import java.util.Stack;

import src.Properties;

/** Expression of type <i>symbol</i> = <i>e1</i> */
public class Assignment extends Expression {
	/** Left hand side of the assignment.
	 * Take into account the syntax "self.sym = ..." to specify explicitly assignments to attributes.
	 */
	private String symbol;
	private Expression e1;
	
	/** Reference to the variable corresponding to the symbol on the left side of the assignment.
	 * The variable can be:
	 *   <li> an Attribute of the current class
	 *   <li> a FormalParam of the current method. It can be any formal parameter, except for self. 
	 *   <li> a LocalDefinition. The Assignment must be inside the scope of the LocalDefinition.
	 *  */
	private Variable symbolData;
	
	public String getSymbol() {
		return symbol;
	}
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
	public Expression getE1() {
		return e1;
	}
	public void setE1(Expression e1) {
		this.e1 = e1;
	}
	public Variable getSymbolData() {
		return symbolData;
	}
	public void setSymbolData(Variable symbolData) {
		this.symbolData = symbolData;
	}
	public Assignment(int lineNumber, String symbol, 
			Expression e1) {
		super(lineNumber);
		this.symbol = symbol;
		this.e1 = e1;
	}
	public Assignment() {}
	
  public void eval(Properties props) throws LCPLException {
    Variable v;
    String actualName = getSymbol();
    if (getSymbol().indexOf("self.") == 0) {
      actualName = getSymbol().substring(5);

      v = props.curClass.takeVariable(actualName, true);
    } else {
      v = props.curClass.takeVariable(getSymbol(), false);
    }

    if (v == null) {
      throw new LCPLException("Attribute " + actualName + " not found in class " +
        props.curClass.getName(), this);
    }

    setSymbolData(v);
    setTypeData(v.takeTypeData());

    e1.eval(props);

    if (e1.getType().compareTo(getType()) != 0) {
      if (props.canConvert(e1.getType(), getType())) {
        Cast c = new Cast(e1.getLineNumber(), getType(), e1);
        c.eval(props);
        e1 = c;
        return ;
      }
      Properties.cannotConvert(e1.getType(), getType(), this);
    }
  }
}
