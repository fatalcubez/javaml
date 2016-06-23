package fatalcubez.ml.workspace;

public class StringValue extends ExpressionValue{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1819856044489728475L;
	private String value;
	
	public StringValue(String value){
		this.setValue(value);
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
