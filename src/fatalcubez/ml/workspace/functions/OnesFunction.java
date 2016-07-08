package fatalcubez.ml.workspace.functions;

import java.util.ArrayList;
import java.util.List;

import fatalcubez.ml.workspace.ExpressionValue;
import fatalcubez.ml.workspace.MatOp;
import fatalcubez.ml.workspace.ScalarValue;
import fatalcubez.ml.workspace.WorkspaceInputException;

public class OnesFunction implements IFunction{

	@Override
	public List<ExpressionValue> evaluate(List<ExpressionValue> params, int numOutputs) throws WorkspaceInputException {
		if(numOutputs > Function.ONES.getMaxOutputs()) throw new WorkspaceInputException("Too many outputs for function 'ones'.");
		if(params.size() > 2) throw new WorkspaceInputException("Invalid number of inputs for function.");
		for(ExpressionValue v : params){
			if(!(v instanceof ScalarValue))throw new WorkspaceInputException("Input values must be scalars.");
		}
		List<ExpressionValue> ret = new ArrayList<ExpressionValue>();
		if(params.size() == 1){
			ret.add(MatOp.getOnesMatrix((ScalarValue)params.get(0)));
			return ret;
		}
		else{
			ret.add(MatOp.getOnesMatrix((ScalarValue)params.get(0), (ScalarValue)params.get(1)));
			return ret;
		}
	}

}
