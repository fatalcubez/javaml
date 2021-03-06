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

	@Override
	public Dimension getDimension() {
		return new Dimension(1,1);
	}

	@Override
	public double getValue(int row, int col) {
		if(row > 0 || col > 0) throw new IllegalArgumentException("Index out of range.");
		return scalar;
	}
	
	@Override
	public String toString(){
		return String.format((scalar == Math.floor(scalar) ? "%.0f" : "%.4f"), scalar);
	}
	
}

