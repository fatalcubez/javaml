package fatalcubez.ml.workspace;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;

public class MatOp {

	public static ExpressionValue multiply(ExpressionValue v1, ExpressionValue v2) throws WorkspaceInputException {
		if (isStringArgument(v1) || isStringArgument(v2)) throw new WorkspaceInputException("Can't operate with string values.");
		if (v1 instanceof ScalarValue && v2 instanceof ScalarValue) {
			return multiply((ScalarValue) v1, (ScalarValue) v2);
		}
		if (v1 instanceof ScalarValue && v2 instanceof MatrixValue) {
			return multiply((ScalarValue) v1, (MatrixValue) v2);
		}
		if (v1 instanceof MatrixValue && v2 instanceof ScalarValue) {
			return multiply((ScalarValue) v2, (MatrixValue) v1);
		}
		if (v1 instanceof MatrixValue && v2 instanceof MatrixValue) {
			return multiply((MatrixValue) v1, (MatrixValue) v2);
		}
		return null;
	}

	private static ExpressionValue multiply(ScalarValue v1, ScalarValue v2) {
		return new ScalarValue(v1.getScalar() * v2.getScalar());
	}

	private static ExpressionValue multiply(ScalarValue v1, MatrixValue v2) {
		return new MatrixValue(v2.getMatrix().scalarMultiply(v1.getScalar()));
	}

	private static ExpressionValue multiply(MatrixValue v1, MatrixValue v2) throws WorkspaceInputException {
		if (v1.getCols() != v2.getRows()) throw new WorkspaceInputException("Inner matrix dimensions must agree.");
		return new MatrixValue(v1.getMatrix().multiply(v2.getMatrix()));
	}

	public static ExpressionValue divide(ExpressionValue v1, ExpressionValue v2) throws WorkspaceInputException {
		if (isStringArgument(v1) || isStringArgument(v2)) throw new WorkspaceInputException("Can't operate with string values.");
		if (v1 instanceof ScalarValue && v2 instanceof ScalarValue) {
			return multiply((ScalarValue) v1, (ScalarValue) reciprocal(v2));
		}
		if (v1 instanceof ScalarValue && v2 instanceof MatrixValue) {
			return multiply((ScalarValue) v1, (MatrixValue) reciprocal(v2));
		}
		if (v1 instanceof MatrixValue && v2 instanceof ScalarValue) {
			return multiply((ScalarValue) reciprocal(v2), (MatrixValue) v1);
		}
		if (v1 instanceof MatrixValue && v2 instanceof MatrixValue) {
			throw new WorkspaceInputException("Can't divide two matrices.");
		}
		return null;
	}

	public static ExpressionValue reciprocal(ExpressionValue v1) throws WorkspaceInputException {
		if (isStringArgument(v1)) throw new WorkspaceInputException("Can't operate with string values.");
		if (v1 instanceof ScalarValue) {
			ScalarValue sV = (ScalarValue) v1;
			return new ScalarValue(1.0d / sV.getScalar());
		}
		if (v1 instanceof MatrixValue) {
			MatrixValue mV = (MatrixValue) v1;
			RealMatrix ret = new Array2DRowRealMatrix(mV.getRows(), mV.getCols());
			for (int i = 0; i < mV.getRows(); i++) {
				for (int j = 0; j < mV.getCols(); j++) {
					ret.setEntry(i, j, 1.0d / mV.getValue(i, j));
				}
			}
			return new MatrixValue(ret);
		}
		return null;
	}

	public static ExpressionValue add(ExpressionValue v1, ExpressionValue v2) throws WorkspaceInputException {
		if (isStringArgument(v1) || isStringArgument(v2)) throw new WorkspaceInputException("Can't operate with string values.");
		if (v1 instanceof ScalarValue && v2 instanceof ScalarValue) {
			return add((ScalarValue) v1, (ScalarValue) v2);
		}
		if (v1 instanceof ScalarValue && v2 instanceof MatrixValue) {
			return add((ScalarValue) v1, (MatrixValue) v2);
		}
		if (v1 instanceof MatrixValue && v2 instanceof ScalarValue) {
			return add((ScalarValue) v2, (MatrixValue) v1);
		}
		if (v1 instanceof MatrixValue && v2 instanceof MatrixValue) {
			return add((MatrixValue) v1, (MatrixValue) v2);
		}
		return null;
	}

	private static ExpressionValue add(ScalarValue v1, ScalarValue v2) {
		return new ScalarValue(v1.getScalar() + v2.getScalar());
	}

	private static ExpressionValue add(ScalarValue v1, MatrixValue v2) {
		return new MatrixValue(v2.getMatrix().scalarAdd(v1.getScalar()));
	}

	private static ExpressionValue add(MatrixValue v1, MatrixValue v2) throws WorkspaceInputException {
		if (!areEqualDimensions(v1, v2)) throw new WorkspaceInputException("Matrix dimensions must be equal.");
		return new MatrixValue(v1.getMatrix().add(v2.getMatrix()));
	}

	public static ExpressionValue subtract(ExpressionValue v1, ExpressionValue v2) throws WorkspaceInputException {
		v2 = multiply(new ScalarValue(-1.0d), v2);
		return add(v1, v2);
	}

	/**
	 * Computes the value when v1 is raised to the v2 power. Currently doesn't work for raising a scalar to a matrix value or a matrix value raised to another matrix value
	 * 
	 * @param v1
	 * @param v2
	 * @return
	 * @throws WorkspaceInputException
	 */
	public static ExpressionValue power(ExpressionValue v1, ExpressionValue v2) throws WorkspaceInputException {
		if (isStringArgument(v1) || isStringArgument(v2)) throw new WorkspaceInputException("Can't operate with string values.");
		if (v1 instanceof ScalarValue && v2 instanceof ScalarValue) {
			return power((ScalarValue) v1, (ScalarValue) v2);
		}
		if (v1 instanceof MatrixValue && v2 instanceof ScalarValue) {
			return power((MatrixValue) v1, (ScalarValue) v2);
		}
		throw new WorkspaceInputException("Invalid expression arguments for '^' function.");
	}

	private static ExpressionValue power(ScalarValue v1, ScalarValue v2) {
		return new ScalarValue(Math.pow(v1.getScalar(), v2.getScalar()));
	}

	private static ExpressionValue power(MatrixValue v1, ScalarValue v2) throws WorkspaceInputException {
		if (!isInteger(v2.getScalar())) throw new WorkspaceInputException("Must use integers when raising a matrix to a power.");
		if (v1.getRows() != v1.getCols()) throw new WorkspaceInputException("Must use a SQUARE matrix as input.");
		if (v2.getScalar() < 0) throw new WorkspaceInputException("Can't raise matrix to a negative power.");
		return new MatrixValue(v1.getMatrix().power((int) Math.floor(v2.getScalar())));
	}

	public static ExpressionValue elementWiseMultiply(MatrixValue v1, MatrixValue v2) throws WorkspaceInputException {
		if (!areEqualDimensions(v1, v2)) throw new WorkspaceInputException("Matrix dimensions must be equal.");
		double[][] ret = new double[v1.getRows()][v1.getCols()];
		for (int i = 0; i < v1.getRows(); i++) {
			for (int j = 0; j < v1.getCols(); j++) {
				ret[i][j] = v1.getValue(i,j) * v2.getValue(i,j);
			}
		}
		return new MatrixValue(ret);
	}

	public static ExpressionValue elementWiseDivide(MatrixValue v1, MatrixValue v2) throws WorkspaceInputException {
		if (!areEqualDimensions(v1, v2)) throw new WorkspaceInputException("Matrix dimensions must be equal.");
		double[][] ret = new double[v1.getRows()][v1.getCols()];
		for (int i = 0; i < v1.getRows(); i++) {
			for (int j = 0; j < v1.getCols(); j++) {
				ret[i][j] = v1.getValue(i,j) * (1.0d / v2.getValue(i,j));
			}
		}
		return new MatrixValue(ret);
	}

	public static ExpressionValue elementWisePower(ExpressionValue v1, ExpressionValue v2) throws WorkspaceInputException {
		if (isStringArgument(v1) || isStringArgument(v2)) throw new WorkspaceInputException("Can't operate with string values.");
		if (v1 instanceof ScalarValue && v2 instanceof ScalarValue) {
			return power(v1, v2);
		}
		if (v1 instanceof ScalarValue && v2 instanceof MatrixValue) {
			return elementWisePower((ScalarValue) v1, (MatrixValue) v2);
		}
		if (v1 instanceof MatrixValue && v2 instanceof ScalarValue) {
			return elementWisePower((MatrixValue) v1, (ScalarValue) v2);
		}
		if (v1 instanceof MatrixValue && v2 instanceof MatrixValue) {
			return elementWisePower((MatrixValue) v1, (MatrixValue) v2);
		}
		return null;
	}

	private static ExpressionValue elementWisePower(ScalarValue v1, MatrixValue v2) throws WorkspaceInputException {
		double[][] ret = new double[v2.getRows()][v2.getCols()];
		for (int i = 0; i < v2.getRows(); i++) {
			for (int j = 0; j < v2.getCols(); j++) {
				ret[i][j] = Math.pow(v1.getScalar(), v2.getValue(i,j));
			}
		}
		return new MatrixValue(ret);
	}

	private static ExpressionValue elementWisePower(MatrixValue v1, ScalarValue v2) throws WorkspaceInputException {
		double[][] ret = new double[v1.getRows()][v1.getCols()];
		for (int i = 0; i < v1.getRows(); i++) {
			for (int j = 0; j < v1.getCols(); j++) {
				ret[i][j] = Math.pow(v1.getValue(i,j), v2.getScalar());
			}
		}
		return new MatrixValue(ret);
	}

	private static ExpressionValue elementWisePower(MatrixValue v1, MatrixValue v2) throws WorkspaceInputException {
		if (!areEqualDimensions(v1, v2)) throw new WorkspaceInputException("Matrix dimensions must be equal.");
		double[][] ret = new double[v1.getRows()][v1.getCols()];
		for (int i = 0; i < v1.getRows(); i++) {
			for (int j = 0; j < v1.getCols(); j++) {
				ret[i][j] = Math.pow(v1.getValue(i,j), v2.getValue(i,j));
			}
		}
		return new MatrixValue(ret);
	}

	public static ExpressionValue transpose(ExpressionValue v1) throws WorkspaceInputException {
		if (isStringArgument(v1)) throw new WorkspaceInputException("Can't operate with string values.");
		if (v1 instanceof ScalarValue) {
			return v1;
		}
		MatrixValue mV = (MatrixValue) v1;
		return new MatrixValue(mV.getMatrix().transpose());
	}

	public static ExpressionValue sum(ExpressionValue v1) throws WorkspaceInputException {
		if (v1 instanceof MatrixValue) {
			MatrixValue mV = (MatrixValue) v1;
			if (mV.getRows() == 1) {
				double sum = 0;
				for (int i = 0; i < mV.getCols(); i++) {
					sum += mV.getValue(0, i);
				}
				return new ScalarValue(sum);
			}
		}
		return sum(v1, new ScalarValue(1.0d));
	}

	public static ExpressionValue sum(ExpressionValue v1, ExpressionValue v2) throws WorkspaceInputException {
		if (!(v2 instanceof ScalarValue)) throw new WorkspaceInputException("Invalid dimension argument.");
		ScalarValue sV = (ScalarValue) v2;
		if (!isInteger(sV.getScalar()) || !inRange(sV.getScalar(), 1.0d, 2.0d)) throw new WorkspaceInputException("Invalid dimension argument.");
		if (v1 instanceof MatrixValue) {
			MatrixValue mV = (MatrixValue) v1;
			int rows = mV.getRows();
			int cols = mV.getCols();
			// Summing across the 1st dimension (rows)
			if (Double.compare(1.0d, sV.getScalar()) == 0) {
				RealMatrix mat = new Array2DRowRealMatrix(1, cols);
				for (int i = 0; i < rows; i++) {
					mat = mat.add(mV.getMatrix().getRowMatrix(i));
				}
				return cols == 1 ? new ScalarValue(mat.getEntry(0, 0)) : new MatrixValue(mat);
			} else {
				RealMatrix mat = new Array2DRowRealMatrix(rows, 1);
				for (int i = 0; i < cols; i++) {
					mat = mat.add(mV.getMatrix().getColumnMatrix(i));
				}
				return rows == 1 ? new ScalarValue(mat.getEntry(0, 0)) : new MatrixValue(mat);
			}
		} else if (v1 instanceof ScalarValue) {
			return new ScalarValue(((ScalarValue) v1).getScalar());
		} else {
			throw new WorkspaceInputException("Invalid input value.");
		}
	}

	public static ExpressionValue mean(ExpressionValue v1) throws WorkspaceInputException {
		if (v1 instanceof MatrixValue) {
			MatrixValue mV = (MatrixValue) v1;
			if (mV.getRows() == 1) {
				return new ScalarValue(((ScalarValue) sum(mV)).getScalar() / mV.getCols());
			}
		}
		return mean(v1, new ScalarValue(1.0d));
	}

	public static ExpressionValue mean(ExpressionValue v1, ExpressionValue v2) throws WorkspaceInputException {
		if (!(v2 instanceof ScalarValue)) throw new WorkspaceInputException("Invalid dimension argument.");
		ScalarValue sV = (ScalarValue) v2;
		if (!isInteger(sV.getScalar()) || !inRange(sV.getScalar(), 1.0d, 2.0d)) throw new WorkspaceInputException("Invalid dimension argument.");
		if (v1 instanceof MatrixValue) {
			MatrixValue mV = (MatrixValue) v1;
			int rows = mV.getRows();
			int cols = mV.getCols();
			// Summing across the 1st dimension (rows)
			if (Double.compare(1.0d, sV.getScalar()) == 0) {
				RealMatrix mat = new Array2DRowRealMatrix(1, cols);
				for (int i = 0; i < rows; i++) {
					mat = mat.add(mV.getMatrix().getRowMatrix(i));
				}
				return cols == 1 ? new ScalarValue(mat.getEntry(0, 0) / (double) rows) : new MatrixValue(mat.scalarMultiply(1.0d / (double) rows));
			} else {
				RealMatrix mat = new Array2DRowRealMatrix(rows, 1);
				for (int i = 0; i < cols; i++) {
					mat = mat.add(mV.getMatrix().getColumnMatrix(i));
				}
				return rows == 1 ? new ScalarValue(mat.getEntry(0, 0) / (double) cols) : new MatrixValue(mat.scalarMultiply(1.0d / (double) cols));
			}
		} else if (v1 instanceof ScalarValue) {
			return new ScalarValue(((ScalarValue) v1).getScalar());
		} else {
			throw new WorkspaceInputException("Invalid input value.");
		}
	}

	public static MatrixValue getEyeMatrix(ScalarValue v1) throws WorkspaceInputException {
		return getEyeMatrix(v1, v1);
	}

	public static MatrixValue getEyeMatrix(ScalarValue v1, ScalarValue v2) throws WorkspaceInputException {
		if (!isInteger(v1.getScalar()) || !isInteger(v2.getScalar())) throw new WorkspaceInputException("Size inputs must be integers.");
		int rows = (int) v1.getScalar();
		int cols = (int) v2.getScalar();
		if (rows <= 0 || cols <= 0) throw new WorkspaceInputException("Size inputs must be greater than 0.");
		if (rows != cols) {
			return new MatrixValue(MatrixUtils.createRealIdentityMatrix(rows > cols ? rows : cols).getSubMatrix(0, rows - 1, 0, cols - 1));
		} else {
			return new MatrixValue(MatrixUtils.createRealIdentityMatrix(rows));
		}
	}

	public static MatrixValue getZerosMatrix(ScalarValue v1) throws WorkspaceInputException {
		return getZerosMatrix(v1, v1);
	}

	public static MatrixValue getZerosMatrix(ScalarValue v1, ScalarValue v2) throws WorkspaceInputException {
		if (!isInteger(v1.getScalar()) || !isInteger(v2.getScalar())) throw new WorkspaceInputException("Size inputs must be integers.");
		int rows = (int) v1.getScalar();
		int cols = (int) v2.getScalar();
		if (rows <= 0 || cols <= 0) throw new WorkspaceInputException("Size inputs must be greater than 0.");
		if (rows != cols) {
			return new MatrixValue(MatrixUtils.createRealMatrix(rows, cols).getSubMatrix(0, rows - 1, 0, cols - 1));
		} else {
			return new MatrixValue(MatrixUtils.createRealMatrix(rows, cols));
		}
	}
	
	// TODO: Handle empty matrices
	public static MatrixValue createVector(List<ExpressionValue> params) throws WorkspaceInputException{
		if(params.size() <= 1) throw new WorkspaceInputException("Invalid parameters for ':' operator.");
		for(ExpressionValue e : params){
			if(e instanceof StringValue) throw new WorkspaceInputException("Invalid string value for ':' operator.");
		}
		if(params.size() == 2){
			ExpressionValue startValue = params.get(0);
			ExpressionValue endValue = params.get(1);
			double start = (startValue instanceof ScalarValue) ? ((ScalarValue)startValue).getScalar() : ((MatrixValue)startValue).getValue(0, 0);
			double end = (endValue instanceof ScalarValue) ? ((ScalarValue)endValue).getScalar() : ((MatrixValue)endValue).getValue(0, 0);
			if(end - start < 0) throw new WorkspaceInputException("Empty matrix created.");
			int numElements = (int)(end - start + 1);
			RealMatrix mat = new Array2DRowRealMatrix(1, numElements);
			for(int i = 0; i < numElements; i++){
				mat.setEntry(0, i, i + start);
			}
			return new MatrixValue(mat);
		}
		else if(params.size() == 3){
			ExpressionValue startValue = params.get(0);
			ExpressionValue incrementValue = params.get(1);
			ExpressionValue endValue = params.get(2);
			double start = (startValue instanceof ScalarValue) ? ((ScalarValue)startValue).getScalar() : ((MatrixValue)startValue).getValue(0, 0);
			double increment = (incrementValue instanceof ScalarValue) ? ((ScalarValue)incrementValue).getScalar() : ((MatrixValue)incrementValue).getValue(0, 0);
			double end = (endValue instanceof ScalarValue) ? ((ScalarValue)endValue).getScalar() : ((MatrixValue)endValue).getValue(0, 0);
			if(Double.compare(increment, 0.0d) == 0) throw new WorkspaceInputException("Empty matrix created."); 
			if((end - start < 0 && increment > 0) || (start - end < 0 && increment < 0)) throw new WorkspaceInputException("Empty matrix created.");
			int numElements = (int)((end - start) / increment + 1);
			RealMatrix mat = new Array2DRowRealMatrix(1, numElements);
			for(int i = 0; i < numElements; i++){
				mat.setEntry(0, i, i * increment + start);
			}
			return new MatrixValue(mat);
		}
		else{
			ExpressionValue e = createVector(params.subList(0, 3));
			List<ExpressionValue> rest = new ArrayList<ExpressionValue>();
			rest.add(e);
			rest.addAll(params.subList(3, params.size()));
			return createVector(rest);
		}
	}
	
	public static ExpressionValue index(ExpressionValue value, List<ExpressionValue> params) throws WorkspaceInputException{
		if(params.size() > 2) throw new WorkspaceInputException("Too many parameters for indexing expression.");
		if(params.isEmpty()) return value;
		// Linear indexing
		if(params.size() == 1){
			ExpressionValue v1 = params.get(0);
			if(!isInteger(v1)) throw new WorkspaceInputException("Indices must be positive integers.");
			if(!inRange(v1, 0, value.getMaxIndex())) throw new WorkspaceInputException("Index out of range.");
			if(value.getMaxIndex() == 1) return new ScalarValue(value.getValue(0));
			RealMatrix mat = new Array2DRowRealMatrix(v1.getDimension().getRows(), v1.getDimension().getCols());
			for(int i = 0; i < mat.getRowDimension(); i++){
				for(int j = 0; j < mat.getColumnDimension(); j++){
					mat.setEntry(i, j, value.getValue((int)(v1.getValue(v1.getDimension().getRows() * j + i) - 1)));
				}
			}
			return new MatrixValue(mat);
		}
		// 2D indexing
		else{
			ExpressionValue v1 = params.get(0);
			ExpressionValue v2 = params.get(1);
			if(!isInteger(v1) || !isInteger(v2)) throw new WorkspaceInputException("Indices must be positive integers.");
			if(!inRange(v1, 0, value.getDimension().getRows()) || !inRange(v2, 0, value.getDimension().getCols())) throw new WorkspaceInputException("Index out of range.");
			int rows = v1.getMaxIndex();
			int cols = v2.getMaxIndex();
			RealMatrix mat = new Array2DRowRealMatrix(rows, cols);
			for(int i = 0; i < mat.getRowDimension(); i++){
				for(int j = 0; j < mat.getColumnDimension(); j++){
					mat.setEntry(i, j, value.getValue((int)(v1.getValue(i) - 1), (int)(v2.getValue(j) - 1)));
				}
			}
			return new MatrixValue(mat);
		}
	}

	public static MatrixValue getOnesMatrix(ScalarValue v1) throws WorkspaceInputException {
		return getOnesMatrix(v1, v1);
	}

	public static MatrixValue getOnesMatrix(ScalarValue v1, ScalarValue v2) throws WorkspaceInputException {
		return (MatrixValue) add(getZerosMatrix(v1, v2), new ScalarValue(1.0d));
	}

	public static boolean isInteger(ExpressionValue v1){
		for(int i = 0; i < v1.getMaxIndex(); i++){
			double value = v1.getValue(i);
			if(!isInteger(value)) return false;
		}
		return true;
	}
	
	public static boolean isInteger(double input) {
		return input == Math.floor(input);
	}

	public static boolean areEqualDimensions(MatrixValue v1, MatrixValue v2) {
		return v1.getCols() == v2.getCols() && v1.getRows() == v2.getRows();
	}

	public static boolean isStringArgument(ExpressionValue v1) {
		return v1 instanceof StringValue;
	}

	/**
	 * Returns true if the value is greater than or equal to the lower limit and less than or equal to the upper limit.
	 * 
	 * @param value
	 * @param lower
	 * @param upper
	 * @return
	 */
	public static boolean inRange(double value, double lower, double upper) {
		return value >= lower && value <= upper;
	}
	
	public static boolean inRange(ExpressionValue v1, double lower, double upper){
		for(int i = 0; i < v1.getMaxIndex(); i++){
			double value = v1.getValue(i);
			if(value < lower || value > upper) return false;
		}
		return true;
	}

}
