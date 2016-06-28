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
	
	public double getEntry(int row, int col){
		return matrix.getEntry(row, col);
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
		return getEntry(index % getRows(), index / getCols());
	}
}
