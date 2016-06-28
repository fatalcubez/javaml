package fatalcubez.ml.workspace;

import java.io.Serializable;

public interface ExpressionValue extends Serializable{

	public int getMaxIndex();
	public double getValue(int index);
	public double getValue(int row, int col);
	public Dimension getDimension();

}
