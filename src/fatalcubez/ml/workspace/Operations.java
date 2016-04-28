package fatalcubez.ml.workspace;

import org.apache.commons.math3.linear.MatrixDimensionMismatchException;

public class Operations {

	public static ExpressionValue multiply(ExpressionValue v1, ExpressionValue v2) throws WorkspaceInputException{
		if (v1 instanceof ScalarValue && v2 instanceof ScalarValue) {
			return multiply((ScalarValue)v1, (ScalarValue)v2);
		}
		if(v1 instanceof ScalarValue && v2 instanceof MatrixValue){
			return multiply((ScalarValue)v1, (MatrixValue)v2);
		}
		if(v1 instanceof MatrixValue && v2 instanceof ScalarValue){
			return multiply((ScalarValue)v2, (MatrixValue)v1);
		}
		if(v1 instanceof MatrixValue && v2 instanceof MatrixValue){
			return multiply((MatrixValue)v1, (MatrixValue)v2);
		}
		return null;
	}

	private static ExpressionValue multiply(ScalarValue v1, ScalarValue v2) {
		return new ScalarValue(v1.getScalar() * v2.getScalar());
	}

	private static ExpressionValue multiply(ScalarValue v1, MatrixValue v2) {
		return new MatrixValue(v2.getMatrix().scalarMultiply(v1.getScalar()));
	}

	private static ExpressionValue multiply(MatrixValue v1, MatrixValue v2) throws WorkspaceInputException{
		MatrixValue value = null;
		try{
			value = new MatrixValue(v1.getMatrix().multiply(v2.getMatrix()));
		}
		catch(MatrixDimensionMismatchException e){
			throw new WorkspaceInputException("Inner matrix dimensions must agree (" + v1.getMatrix().getColumnDimension() + " != " + v2.getMatrix().getRowDimension() + ").");
		}
		return value;
	}
	
	public static ExpressionValue add(ExpressionValue v1, ExpressionValue v2) throws WorkspaceInputException {
		if (v1 instanceof ScalarValue && v2 instanceof ScalarValue) {
			return add((ScalarValue)v1, (ScalarValue)v2);
		}
		if(v1 instanceof ScalarValue && v2 instanceof MatrixValue){
			return add((ScalarValue)v1, (MatrixValue)v2);
		}
		if(v1 instanceof MatrixValue && v2 instanceof ScalarValue){
			return add((ScalarValue)v2, (MatrixValue)v1);
		}
		if(v1 instanceof MatrixValue && v2 instanceof MatrixValue){
			return add((MatrixValue)v1, (MatrixValue)v2);
		}
		return null;
	}

	private static ExpressionValue add(ScalarValue v1, ScalarValue v2) {
		return new ScalarValue(v1.getScalar() + v2.getScalar());
	}

	private static ExpressionValue add(ScalarValue v1, MatrixValue v2) {
		return new MatrixValue(v2.getMatrix().scalarAdd(v1.getScalar()));
	}

	private static ExpressionValue add(MatrixValue v1, MatrixValue v2) throws WorkspaceInputException{
		MatrixValue value = null;
		try{
			value = new MatrixValue(v1.getMatrix().add(v2.getMatrix()));
		}
		catch(MatrixDimensionMismatchException e){
			throw new WorkspaceInputException("Inner matrix dimensions must agree (" + v1.getMatrix().getColumnDimension() + " != " + v2.getMatrix().getRowDimension() + ").");
		}
		return value;
	}
	
	public static ExpressionValue power(ExpressionValue v1, ExpressionValue v2) throws WorkspaceInputException{
		if(v1 instanceof ScalarValue && v2 instanceof ScalarValue){
			return power((ScalarValue)v1, (ScalarValue)v2);
		}
		if(v1 instanceof MatrixValue && v2 instanceof ScalarValue){
			return power((MatrixValue)v1, (ScalarValue)v2);
		}
		throw new WorkspaceInputException("Invalid expression arguments for '^' function.");
	}
	
	private static ExpressionValue power(ScalarValue v1, ScalarValue v2){
		return new ScalarValue(Math.pow(v1.getScalar(), v2.getScalar()));
	}
	
	private static ExpressionValue power(MatrixValue v1, ScalarValue v2) throws WorkspaceInputException{
		if(v2.getScalar() != Math.floor(v2.getScalar())) throw new WorkspaceInputException("Must use integers when raising a matrix to a power.");
		return new MatrixValue(v1.getMatrix().power((int)Math.floor(v2.getScalar())));
	}
	
	public static ExpressionValue elementWiseMultiply(MatrixValue v1, MatrixValue v2) throws WorkspaceInputException{
		if(!isValidDimensions(v1, v2)){
			throw new WorkspaceInputException("Inner matrix dimensions must agree (" + v1.getMatrix().getColumnDimension() + " != " + v2.getMatrix().getRowDimension() + ").");
		}
		double[][] ret = new double[v1.getMatrix().getRowDimension()][v1.getMatrix().getColumnDimension()];
		for(int i = 0; i < v1.getMatrix().getRowDimension(); i++){
			for(int j = 0; j < v1.getMatrix().getColumnDimension(); j++){
				ret[i][j] = v1.getMatrix().getEntry(i, j) * v2.getMatrix().getEntry(i, j);
			}
		}
		return new MatrixValue(ret);
	}

	public static ExpressionValue elementWisePower(MatrixValue v1, MatrixValue v2) throws WorkspaceInputException{
		if(!isValidDimensions(v1, v2)){
			throw new WorkspaceInputException("Inner matrix dimensions must agree (" + v1.getMatrix().getColumnDimension() + " != " + v2.getMatrix().getRowDimension() + ").");
		}
		double[][] ret = new double[v1.getMatrix().getRowDimension()][v1.getMatrix().getColumnDimension()];
		for(int i = 0; i < v1.getMatrix().getRowDimension(); i++){
			for(int j = 0; j < v1.getMatrix().getColumnDimension(); j++){
				ret[i][j] = Math.pow(v1.getMatrix().getEntry(i, j), v2.getMatrix().getEntry(i, j));
			}
		}
		return new MatrixValue(ret);
	}
	
	private static boolean isValidDimensions(MatrixValue v1, MatrixValue v2){
		return !(v1.getMatrix().getColumnDimension() != v2.getMatrix().getColumnDimension() || v1.getMatrix().getRowDimension() != v2.getMatrix().getRowDimension());
	}
}
