package fatalcubez.ml.workspace.functions;

import java.util.ArrayList;
import java.util.List;

import fatalcubez.ml.workspace.ExpressionValue;
import fatalcubez.ml.workspace.MatOp;
import fatalcubez.ml.workspace.WorkspaceInputException;

public class LogFunction implements IFunction {

	@Override
	public List<ExpressionValue> evaluate(List<ExpressionValue> params, int numOutputs) throws WorkspaceInputException {
		if(numOutputs > Function.LOG.getMaxOutputs()) throw new WorkspaceInputException("Too many outputs for function 'log'.");
		if(params.size() != 1) throw new WorkspaceInputException("Invalid parameters for function 'log'.");
		List<ExpressionValue> ret = new ArrayList<ExpressionValue>();
		ret.add(MatOp.log(params.get(0)));
		return ret;
	}

}