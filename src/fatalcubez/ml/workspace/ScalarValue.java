package fatalcubez.ml.workspace;

public final class ScalarValue implements ExpressionValue {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8653433352329730574L;
	private final double scalar;
	
	public ScalarValue(double scalar){
		this.scalar = scalar;
	}
	
	public double getScalar() {
		return scalar;
	}

	@Override
	public int getMaxIndex() {
		return 1;
	}

	@Override
	public double getValue(int index) {
		if(index > getMaxIndex() - 1) throw new IllegalArgumentException("Index out of range.");
		return scalar;
	}
}
