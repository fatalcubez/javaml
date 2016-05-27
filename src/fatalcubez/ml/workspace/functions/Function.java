package fatalcubez.ml.workspace.functions;


public enum Function {

	EYE("eye"){
		@Override
		public IFunction getFunctionInstance() {
			return new EyeFunction();
		}
	},
	ZEROS("zeros") {
		@Override
		public IFunction getFunctionInstance() {
			return new ZerosFunction();
		}
	};
	
	private String consoleName;
	
	private Function(String consoleName){
		this.consoleName = consoleName;
	}
	
	public String getConsoleName(){
		return consoleName;
	}
	
	public static Function getFunction(String functionName){
		for(Function f : Function.values()){
			if(functionName.equals(f.getConsoleName())) return f;
		}
		return null;
	}
	
	public abstract IFunction getFunctionInstance();

}