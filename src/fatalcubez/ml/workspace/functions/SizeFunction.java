package fatalcubez.ml.workspace.functions;

import java.util.List;

import fatalcubez.ml.workspace.ExpressionValue;
import fatalcubez.ml.workspace.MatOp;
import fatalcubez.ml.workspace.WorkspaceInputException;

public class SizeFunction implements IFunction{

	@Override
	public List<ExpressionValue> evaluate(List<ExpressionValue> params, int numOutputs) throws WorkspaceInputException {
		if(numOutputs > Function.SIZE.getMaxOutputs()) throw new WorkspaceInputException("Too many outputs for function 'size'.");
		if(params.size() != 1) throw new WorkspaceInputException("Invalid parameters for function 'size'.");
		return MatOp.getSize(params.get(0), numOutputs);
	}

}
