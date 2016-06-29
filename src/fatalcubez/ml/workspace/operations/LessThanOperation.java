package fatalcubez.ml.workspace.operations;

import fatalcubez.ml.workspace.ExpressionValue;
import fatalcubez.ml.workspace.MatOp;
import fatalcubez.ml.workspace.WorkspaceInputException;

public class LessThanOperation implements IOperation{

	@Override
	public ExpressionValue evaluate(ExpressionValue v1, ExpressionValue v2, boolean elementWise) throws WorkspaceInputException {
		return MatOp.lessThan(v1, v2);
	}

}
