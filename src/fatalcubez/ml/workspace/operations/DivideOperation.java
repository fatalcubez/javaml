package fatalcubez.ml.workspace.operations;

import fatalcubez.ml.workspace.ExpressionValue;
import fatalcubez.ml.workspace.MatOp;
import fatalcubez.ml.workspace.MatrixValue;
import fatalcubez.ml.workspace.WorkspaceInputException;

public class DivideOperation implements IOperation{

	@Override
	public ExpressionValue evaluate(ExpressionValue v1, ExpressionValue v2, boolean elementWise) throws WorkspaceInputException {
		if(elementWise){
			if(v1 instanceof MatrixValue && v2 instanceof MatrixValue){
				return MatOp.elementWiseDivide((MatrixValue)v1, (MatrixValue)v2);
			}
		}
		return MatOp.divide(v1, v2);
	}

}
