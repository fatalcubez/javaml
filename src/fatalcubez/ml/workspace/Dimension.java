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
	
	@Override
	public boolean equals(Object o){
		if(!(o instanceof Dimension)) return false;
		Dimension dim = (Dimension)o;
		return dim.getRows() == rows && dim.getCols() == cols;
	}
	
	@Override
	public int hashCode(){
		int result = 17;
		result = 31 * result + rows;
		result = 31 * result + cols;
		return result;
	}
	
}
