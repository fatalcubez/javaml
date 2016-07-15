package fatalcubez.ml.workspace.functions;

import java.util.ArrayList;
import java.util.List;

import fatalcubez.ml.workspace.ExpressionValue;
import fatalcubez.ml.workspace.MatOp;
import fatalcubez.ml.workspace.WorkspaceInputException;

public class TanFunction implements IFunction {

	@Override
	public List<ExpressionValue> evaluate(List<ExpressionValue> params, int numOutputs) throws WorkspaceInputException {
		if(numOutputs > Function.TAN.getMaxOutputs()) throw new WorkspaceInputException("Too many outputs for function 'tan'.");
		if(params.size() != 1) throw new WorkspaceInputException("Invalid parameters for function 'tan'.");
		List<ExpressionValue> ret = new ArrayList<ExpressionValue>();
		ret.add(MatOp.tan(params.get(0)));
		return ret;
	}

}