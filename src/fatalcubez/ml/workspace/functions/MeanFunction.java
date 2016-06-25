package fatalcubez.ml.workspace.functions;

import java.util.List;

import fatalcubez.ml.workspace.ExpressionValue;
import fatalcubez.ml.workspace.MatOp;
import fatalcubez.ml.workspace.WorkspaceInputException;

public class MeanFunction implements IFunction{

	@Override
	public ExpressionValue evaluate(List<ExpressionValue> params) throws WorkspaceInputException {
		if(params.size() > 2) throw new WorkspaceInputException("Too many arguments for function 'mean'.");
		if(params.size() <= 0) throw new WorkspaceInputException("Too little arguments for function 'mean'.");
		if(params.size() == 1){
			return MatOp.mean(params.get(0));
		}
		return MatOp.mean(params.get(0), params.get(1));
	}

}
