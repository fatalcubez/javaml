package fatalcubez.ml.workspace;

import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.MatrixDimensionMismatchException;
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
					ret.setEntry(i, j, 1.0d / mV.getEntry(i, j));
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
				ret[i][j] = v1.getEntry(i,j) * v2.getEntry(i,j);
			}
		}
		return new MatrixValue(ret);
	}

	public static ExpressionValue elementWiseDivide(MatrixValue v1, MatrixValue v2) throws WorkspaceInputException {
		if (!areEqualDimensions(v1, v2)) throw new WorkspaceInputException("Matrix dimensions must be equal.");
		double[][] ret = new double[v1.getRows()][v1.getCols()];
		for (int i = 0; i < v1.getRows(); i++) {
			for (int j = 0; j < v1.getCols(); j++) {
				ret[i][j] = v1.getEntry(i,j) * (1.0d / v2.getEntry(i,j));
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
				ret[i][j] = Math.pow(v1.getScalar(), v2.getEntry(i,j));
			}
		}
		return new MatrixValue(ret);
	}

	private static ExpressionValue elementWisePower(MatrixValue v1, ScalarValue v2) throws WorkspaceInputException {
		double[][] ret = new double[v1.getRows()][v1.getCols()];
		for (int i = 0; i < v1.getRows(); i++) {
			for (int j = 0; j < v1.getCols(); j++) {
				ret[i][j] = Math.pow(v1.getEntry(i,j), v2.getScalar());
			}
		}
		return new MatrixValue(ret);
	}

	private static ExpressionValue elementWisePower(MatrixValue v1, MatrixValue v2) throws WorkspaceInputException {
		if (!areEqualDimensions(v1, v2)) throw new WorkspaceInputException("Matrix dimensions must be equal.");
		double[][] ret = new double[v1.getRows()][v1.getCols()];
		for (int i = 0; i < v1.getRows(); i++) {
			for (int j = 0; j < v1.getCols(); j++) {
				ret[i][j] = Math.pow(v1.getEntry(i,j), v2.getEntry(i,j));
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
					sum += mV.getMatrix().getEntry(0, i);
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

	public static MatrixValue getOnesMatrix(ScalarValue v1) throws WorkspaceInputException {
		return getOnesMatrix(v1, v1);
	}

	public static MatrixValue getOnesMatrix(ScalarValue v1, ScalarValue v2) throws WorkspaceInputException {
		return (MatrixValue) add(getZerosMatrix(v1, v2), new ScalarValue(1.0d));
	}

	public static boolean isInteger(double input) {
		return input == Math.floor(input);
	}

	public static boolean areEqualDimensions(MatrixValue v1, MatrixValue v2) {
		return v1.getCols() == v2.getCols() || v1.getRows() == v2.getRows();
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
}
