package fatalcubez.ml.workspace;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;

public class MatrixValue extends ExpressionValue{

	private RealMatrix matrix;
	
	public MatrixValue(){
		matrix = new Array2DRowRealMatrix();
	}
	
	public MatrixValue(RealMatrix matrix){
		this.matrix = matrix;
	}
	
	public MatrixValue(double[][] values){
		matrix = new Array2DRowRealMatrix(values);
	}

	public RealMatrix getMatrix() {
		return matrix;
	}

	public void setMatrix(RealMatrix matrix) {
		this.matrix = matrix;
	}
	
	@Override
	public String toString(){
		return matrix.getRowDimension() + "," + matrix.getColumnDimension();
	}
}
