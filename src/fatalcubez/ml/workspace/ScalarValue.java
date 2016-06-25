package fatalcubez.ml.workspace;

public final class ScalarValue extends ExpressionValue {
	
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
}
