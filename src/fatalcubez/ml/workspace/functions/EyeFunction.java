package fatalcubez.ml.workspace.functions;

import java.util.List;

import fatalcubez.ml.workspace.ExpressionValue;
import fatalcubez.ml.workspace.MatOp;
import fatalcubez.ml.workspace.ScalarValue;
import fatalcubez.ml.workspace.WorkspaceInputException;

public class EyeFunction implements IFunction{

	@Override
	public ExpressionValue evaluate(List<ExpressionValue> params) throws WorkspaceInputException{
		if(params.size() > 2) throw new WorkspaceInputException("Invalid number of inputs for function.");
		for(ExpressionValue v : params){
			if(!(v instanceof ScalarValue))throw new WorkspaceInputException("Input values must be scalars.");
		}
		if(params.size() == 1){
			return MatOp.getEyeMatrix((ScalarValue)params.get(0));
		}
		else{
			return MatOp.getEyeMatrix((ScalarValue)params.get(0), (ScalarValue)params.get(1));
		}
	}

}
