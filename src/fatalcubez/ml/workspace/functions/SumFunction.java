package fatalcubez.ml.workspace.functions;

import java.util.List;

import fatalcubez.ml.workspace.ExpressionValue;
import fatalcubez.ml.workspace.MatOp;
import fatalcubez.ml.workspace.WorkspaceInputException;

public class SumFunction implements IFunction{

	@Override
	public ExpressionValue evaluate(List<ExpressionValue> params) throws WorkspaceInputException {
		if(params.size() > 2) throw new WorkspaceInputException("Too many arguments for function 'sum'.");
		if(params.size() == 1){
			return MatOp.sum(params.get(0));
		}
		return MatOp.sum(params.get(0), params.get(1));
	}

}
