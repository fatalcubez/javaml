package fatalcubez.ml.workspace.functions;

import java.util.ArrayList;
import java.util.List;

import fatalcubez.ml.workspace.ExpressionValue;
import fatalcubez.ml.workspace.MatOp;
import fatalcubez.ml.workspace.WorkspaceInputException;

public class SumFunction implements IFunction{

	@Override
	public List<ExpressionValue> evaluate(List<ExpressionValue> params, int numOutputs) throws WorkspaceInputException {
		if(numOutputs > Function.SUM.getMaxOutputs()) throw new WorkspaceInputException("Too many outputs for function 'sum'.");
		if(params.size() > 2) throw new WorkspaceInputException("Too many arguments for function 'sum'.");
		if(params.size() <= 0) throw new WorkspaceInputException("Too little arguments for function 'sum'.");
		List<ExpressionValue> ret = new ArrayList<ExpressionValue>();
		if(params.size() == 1){
			ret.add(MatOp.sum(params.get(0)));
			return ret;
		}
		ret.add(MatOp.sum(params.get(0), params.get(1)));
		return ret;
	}

}
