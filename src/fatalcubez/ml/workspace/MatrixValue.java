package fatalcubez.ml.workspace;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;

public final class MatrixValue implements ExpressionValue{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4231050452116360135L;
	private final RealMatrix matrix;
	
	public MatrixValue(RealMatrix matrix){
		this.matrix = new Array2DRowRealMatrix(matrix.getData());
	}
	
	public MatrixValue(double[][] values){
		matrix = new Array2DRowRealMatrix(values);
	}

	public RealMatrix getMatrix() {
		return matrix.copy();
	}
	
	public int getRows(){
		return matrix.getRowDimension();
	}
	
	public int getCols(){
		return matrix.getColumnDimension();
	}

	@Override
	public String toString(){
		return matrix.getRowDimension() + "," + matrix.getColumnDimension();
	}

	@Override
	public int getMaxIndex() {
		return getRows() * getCols();
	}

	@Override
	public double getValue(int index) {
		if(index > getMaxIndex() - 1) throw new IllegalArgumentException("Index out of range.");
		return getValue(index % getRows(), index / getRows());
	}

	@Override
	public Dimension getDimension() {
		return new Dimension(getRows(), getCols());
	}

	@Override
	public double getValue(int row, int col) {
		if(row > getRows() - 1 || col > getCols() - 1) throw new IllegalArgumentException("Index out of range.");
		return matrix.getEntry(row, col);
	}
}
