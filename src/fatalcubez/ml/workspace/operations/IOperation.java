package fatalcubez.ml.workspace.operations;

import fatalcubez.ml.workspace.ExpressionValue;
import fatalcubez.ml.workspace.WorkspaceInputException;

public interface IOperation {

	public ExpressionValue evaluate(ExpressionValue v1, ExpressionValue v2) throws WorkspaceInputException;
	
}
