package fatalcubez.ml.workspace.operations;

import fatalcubez.ml.workspace.ExpressionValue;
import fatalcubez.ml.workspace.MatOp;
import fatalcubez.ml.workspace.WorkspaceInputException;

public class LessThanEqualOperation implements IOperation{

	@Override
	public ExpressionValue evaluate(ExpressionValue v1, ExpressionValue v2, boolean elementWise) throws WorkspaceInputException {
		return MatOp.lessThanEqual(v1, v2);
	}

}
