package fatalcubez.ml.workspace.functions;

import fatalcubez.ml.workspace.ExpressionValue;

public interface IFunction {

	public ExpressionValue evaluate(ExpressionValue[] params);
	
}
