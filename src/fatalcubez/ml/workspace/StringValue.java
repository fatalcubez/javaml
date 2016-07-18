package fatalcubez.ml.workspace;

public class StringValue implements ExpressionValue{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1819856044489728475L;
	private final String value;
	
	public StringValue(String value){
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	@Override
	public int getMaxIndex() {
		return value.length();
	}

	@Override
	public double getValue(int index) {
		if(index > getMaxIndex() - 1) throw new IllegalArgumentException("Index out of range.");
		return (double)value.charAt(index);
	}

	@Override
	public Dimension getDimension() {
		return new Dimension(1, getMaxIndex());
	}

	@Override
	public double getValue(int row, int col) {
		if(row > 0 || col > getMaxIndex() - 1) throw new IllegalArgumentException("Index out of range.");
		return getValue(col);
	}
	
	@Override
	public String toString(){
		return value;
	}
	
}
