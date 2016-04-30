package fatalcubez.ml.workspace.operations;

import fatalcubez.ml.workspace.ExpressionValue;
import fatalcubez.ml.workspace.MatOp;
import fatalcubez.ml.workspace.ScalarValue;
import fatalcubez.ml.workspace.WorkspaceInputException;

public class SubtractOperation implements IOperation{

	@Override
	public ExpressionValue evaluate(ExpressionValue v1, ExpressionValue v2) throws WorkspaceInputException {
		return MatOp.add(v1, MatOp.multiply(v2,	new ScalarValue(-1)));
	}

}
