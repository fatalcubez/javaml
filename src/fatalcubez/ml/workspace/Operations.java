package fatalcubez.ml.workspace;

public class Operations {

	public static ExpressionValue multiply(ExpressionValue v1, ExpressionValue v2) {
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

	private static ExpressionValue multiply(MatrixValue v1, MatrixValue v2) {
		return new MatrixValue(v1.getMatrix().multiply(v2.getMatrix()));
	}

}
