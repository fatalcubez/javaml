package fatalcubez.ml.workspace.operations;

import fatalcubez.ml.workspace.ExpressionValue;
import fatalcubez.ml.workspace.MatOp;
import fatalcubez.ml.workspace.MatrixValue;
import fatalcubez.ml.workspace.ScalarValue;
import fatalcubez.ml.workspace.WorkspaceInputException;

public class DivideOperation implements IOperation{

	@Override
	public ExpressionValue evaluate(ExpressionValue v1, ExpressionValue v2) throws WorkspaceInputException {
		if(v1 instanceof MatrixValue && v2 instanceof MatrixValue) throw new WorkspaceInputException("Can't divide two matrices.");
		if(v1 instanceof ScalarValue){
			((ScalarValue) v1).setScalar(1.0d / ((ScalarValue) v1).getScalar());
		}
		if(v2 instanceof ScalarValue){
			((ScalarValue) v2).setScalar(1.0d / ((ScalarValue) v2).getScalar());
		}
		return MatOp.multiply(v1, v2);
	}

}
