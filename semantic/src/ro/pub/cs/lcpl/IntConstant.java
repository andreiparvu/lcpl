package ro.pub.cs.lcpl;

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
	
  public void eval(Properties props) {
    this.isConstant = this.ORIGINAL_CONSTANT;
    this.typeConstant = this.INT;
    setIntConstant(value);

    setTypeData(props.p.getIntType());
  }
}
