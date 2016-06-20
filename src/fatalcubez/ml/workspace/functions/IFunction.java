package fatalcubez.ml.workspace.functions;

import java.util.List;

import fatalcubez.ml.workspace.ExpressionValue;
import fatalcubez.ml.workspace.WorkspaceInputException;

public interface IFunction {

	public ExpressionValue evaluate(List<ExpressionValue> params) throws WorkspaceInputException;
	
}
