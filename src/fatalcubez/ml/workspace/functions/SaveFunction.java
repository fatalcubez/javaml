package fatalcubez.ml.workspace.functions;

import java.util.List;

import fatalcubez.ml.workspace.ExpressionValue;
import fatalcubez.ml.workspace.StringValue;
import fatalcubez.ml.workspace.WorkspaceInputException;

public class SaveFunction implements IFunction{

	@Override
	public ExpressionValue evaluate(List<ExpressionValue> params) throws WorkspaceInputException {
		// TODO: Allow for save function to take in more parameters
		if(params.size() == 0 || !(params.get(0) instanceof StringValue) || params.size() > 1) throw new WorkspaceInputException("Invalid inputs for function 'Save'.");
		
		return null;
	}

}
