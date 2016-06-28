package fatalcubez.ml.workspace;

public final class Dimension {

	private final int rows;
	private final int cols;
	
	public Dimension(int rows, int cols){
		this.rows = rows;
		this.cols = cols;
	}
	
	public int getRows(){
		return rows;
	}
	
	public int getCols(){
		return cols;
	}
	
	@Override
	public String toString(){
		return "Rows: " + rows + ", Cols: " + cols;
	}
	
}
