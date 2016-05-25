package fatalcubez.ml.workspace.operations;

import fatalcubez.ml.workspace.ExpressionValue;
import fatalcubez.ml.workspace.MatOp;
import fatalcubez.ml.workspace.WorkspaceInputException;

public class PowerOperation implements IOperation{

	@Override
	public ExpressionValue evaluate(ExpressionValue v1, ExpressionValue v2, boolean elementWise) throws WorkspaceInputException {
		if(elementWise){
			return MatOp.elementWisePower(v1, v2);
		} else{
			return MatOp.power(v1, v2);
		}
	}

}
