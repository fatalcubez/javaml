package fatalcubez.ml.workspace;

public enum Function {

	EYE("eye", new int[]{1}),
	ZEROS("zeros", new int[]{1});
	
	private String consoleName;
	private int[] numParamsAllowed;
	
	private Function(String consoleName, int[] numParamsAllowed){
		this.consoleName = consoleName;
		this.numParamsAllowed = numParamsAllowed;
	}
	
	public String getConsoleName(){
		return consoleName;
	}
	
	public boolean isParamsValid(int numParams){
		for(int i = 0; i < numParamsAllowed.length; i++){
			if(numParamsAllowed[i] == numParams) return true;
		}
		return false;
	}
}
