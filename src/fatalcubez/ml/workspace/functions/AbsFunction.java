package fatalcubez.ml.workspace.functions;

import java.util.ArrayList;
import java.util.List;

import fatalcubez.ml.workspace.ExpressionValue;
import fatalcubez.ml.workspace.MatOp;
import fatalcubez.ml.workspace.WorkspaceInputException;

public class AbsFunction implements IFunction {

	@Override
	public List<ExpressionValue> evaluate(List<ExpressionValue> params, int numOutputs) throws WorkspaceInputException {
		if(numOutputs > Function.ABS.getMaxOutputs()) throw new WorkspaceInputException("Too many outputs for function 'abs'.");
		if(params.size() != 1) throw new WorkspaceInputException("Invalid parameters for function 'abs'.");
		List<ExpressionValue> ret = new ArrayList<ExpressionValue>();
		ret.add(MatOp.abs(params.get(0)));
		return ret;
	}

}