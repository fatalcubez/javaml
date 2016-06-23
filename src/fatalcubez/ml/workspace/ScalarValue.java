package fatalcubez.ml.workspace;

public class ScalarValue extends ExpressionValue {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8653433352329730574L;
	private double scalar;

	public ScalarValue(){
		scalar = 0d;
	}
	
	public ScalarValue(double scalar){
		this.scalar = scalar;
	}
	
	public double getScalar() {
		return scalar;
	}

	public void setScalar(double scalar) {
		this.scalar = scalar;
	}
	

}
